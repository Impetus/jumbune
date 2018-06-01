/*
 * 
 */
package org.jumbune.monitoring.hprof;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jumbune.monitoring.utils.ProfilerConstants;


/**
 * <pre>
 * {
 * 	&#064;code
 * 	BinaryHprofReader reader = new BinaryHprofReader(new BufferedInputStream(inputStream));
 * 	reader.setStrict(false); // for RI compatability
 * 	reader.read();
 * 	inputStream.close();
 * 	reader.getVersion();
 * 	reader.getHprofData();
 * }
 * </pre>
 */
public final class BinaryHprofReader {


	private final DataInputStream in;

	/**
	 * By default we try to strictly validate rules followed by our HprofWriter. For example, every end thread is preceded by a matching start thread.
	 */
	private boolean strict = true;

	/**
	 * version string from header after read has been performed, otherwise null. nullness used to detect if callers try to access data before read is
	 * called.
	 */
	private String version;

	private final Map<HprofData.StackTrace, int[]> stackTraces = new HashMap<HprofData.StackTrace, int[]>();

	private final HprofData hprofData = new HprofData(stackTraces);

	private Map<Integer, String> idToString = new HashMap<Integer, String>();
	private final Map<Integer, String> idToClassName = new HashMap<Integer, String>();
	private final Map<Integer, StackTraceElement> idToStackFrame = new HashMap<Integer, StackTraceElement>();
	private final Map<Integer, HprofData.StackTrace> idToStackTrace = new HashMap<Integer, HprofData.StackTrace>();

	private HeapAllocSitesBean heapBean = new HeapAllocSitesBean();
	private CPUSamplesBean cpuSamplesBean = new CPUSamplesBean();

	/**
	 * Creates a BinaryHprofReader around the specified {@code inputStream}
	 */
	public BinaryHprofReader(InputStream inputStream) throws IOException {
		this.in = new DataInputStream(inputStream);
	}

	/**
	 * Gets the heap bean.
	 *
	 * @return the heap bean
	 */
	public HeapAllocSitesBean getHeapBean() {
		return this.heapBean;
	}

	/**
	 * Gets the cPU samples.
	 *
	 * @return the cPU samples
	 */
	public CPUSamplesBean getCPUSamples() {
		return this.cpuSamplesBean;
	}

	public boolean getStrict() {
		return strict;
	}

	/**
	 * Sets the strict.
	 *
	 * @param strict the new strict
	 */
	public void setStrict(boolean strict) {
		if (version != null) {
			throw new IllegalStateException("cannot set strict after read()");
		}
		this.strict = strict;
	}

	/**
	 * throws IllegalStateException if read() has not been called.
	 */
	private void checkRead() {
		if (version == null) {
			throw new IllegalStateException("data access before read()");
		}
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		checkRead();
		return version;
	}

	/**
	 * Gets the hprof data.
	 *
	 * @return the hprof data
	 */
	public HprofData getHprofData() {
		checkRead();
		return hprofData;
	}

	/**
	 * Read the hprof header and records from the input
	 */
	public void read() throws IOException {
		parseHeader();
		parseRecords();
	}

	private void parseHeader() throws IOException {
		parseVersion();
		parseIdSize();
		parseTime();
	}

	private void parseVersion() throws IOException {
		String versionParse = BinaryHprof.readMagic(in);
		if (versionParse == null) {
			throw new MalformedHprofException("Could not find HPROF version");
		}
		this.version = versionParse;
	}

	private void parseIdSize() throws IOException {
		int idSize = in.readInt();
		if (idSize != BinaryHprof.ID_SIZE) {
			throw new MalformedHprofException("Unsupported identifier size: " + idSize);
		}
	}

	private void parseTime() throws IOException {
		long time = in.readLong();
		hprofData.setStartMillis(time);
	}

	private void parseRecords() throws IOException {
		
		while(true){
			if(!parseRecord()){
				break;
			}
		}
			}

	/**
	 * Read and process the next record. Returns true if a record was handled, false on EOF.
	 */
	private boolean parseRecord() throws IOException {
		int tagOrEOF = in.read();
		if (tagOrEOF == -1) {
			return false;
		}
		byte tag = (byte) tagOrEOF;
		in.readInt();
		int recordLength = in.readInt();
		BinaryHprof.Tag hprofTag = BinaryHprof.Tag.get(tag);
		if (hprofTag == null) {
			skipRecord(recordLength);
			return true;
		}
		String error = hprofTag.checkSize(recordLength);
		if (error != null) {
			throw new MalformedHprofException(error);
		}
		return processHprofTag(recordLength, hprofTag);
	}

	/**
	 * This method processes the  hprof tag.
	 *
	 * @param recordLength the record length
	 * @param hprofTag the hprof tag
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private boolean processHprofTag(int recordLength, BinaryHprof.Tag hprofTag)
			throws IOException {
		switch (hprofTag) {
		case CONTROL_SETTINGS:
			parseControlSettings();
			return true;

		case STRING_IN_UTF8:
			parseStringInUtf8(recordLength);
			return true;

		case START_THREAD:
			parseStartThread();
			return true;
		case END_THREAD:
			parseEndThread();
			return true;

		case LOAD_CLASS:
			parseLoadClass();
			return true;
		default:
			return secondaryProcessHprofTag(recordLength, hprofTag);
		}
	}
	
	/**
	 * This method processes the rest of the hprof tag.
	 *
	 * @param recordLength the record length
	 * @param hprofTag the hprof tag
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private boolean secondaryProcessHprofTag(int recordLength, BinaryHprof.Tag hprofTag) throws IOException{
		switch (hprofTag) {
		case STACK_FRAME:
			parseStackFrame();
			return true;
		case STACK_TRACE:
			parseStackTrace(recordLength);
			return true;
		case CPU_SAMPLES:
			parseCpuSamples(recordLength);
			return true;
		case ALLOC_SITES:
			parseAllocSites();
			return true;
		case HEAP_SUMMARY:
		case HEAP_DUMP:
		case UNLOAD_CLASS:
		case HEAP_DUMP_SEGMENT:
		case HEAP_DUMP_END:
		default:
			skipRecord(recordLength);
			return true;
		}		
	}

	/**
	 * Skip record.
	 *
	 * @param recordLength the record length
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void skipRecord( long recordLength) throws IOException {
		long skipped = in.skip(recordLength);
		if (skipped != recordLength) {
			throw new EOFException("Expected to skip " + recordLength + " bytes but only skipped " + skipped + " bytes");
		}
	}

	/**
	 * Parses the control settings.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseControlSettings() throws IOException {
		int flags = in.readInt();
		short depth = in.readShort();
		hprofData.setFlags(flags);
		hprofData.setDepth(depth);
	}

	/**
	 * Parses the string in utf8.
	 *
	 * @param recordLength the record length
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseStringInUtf8(int recordLength) throws IOException {
		int stringId = in.readInt();
		byte[] bytes = new byte[recordLength - BinaryHprof.ID_SIZE];
		readFully(in, bytes);
		String string = new String(bytes, "UTF-8");
		String old = idToString.put(stringId, string);
		if (old != null) {
			throw new MalformedHprofException("Duplicate string id: " + stringId);
		}
	}

	/**
	 * Read fully.
	 *
	 * @param in the in
	 * @param dst the dst
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void readFully(InputStream in, byte[] dst) throws IOException {
		int offset = 0;
		int byteCount = dst.length;
		while (byteCount > 0) {
			int bytesRead = in.read(dst, offset, byteCount);
			if (bytesRead < 0) {
				throw new EOFException();
			}
			offset += bytesRead;
			byteCount -= bytesRead;
		}
	}

	/**
	 * Parses the load class.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseLoadClass() throws IOException {
		int classId = in.readInt();
		readId();
		// serial number apparently not a stack trace id. (int vs ID)
		// we don't use this field.
		in.readInt();
		String className = readString();
		String old = idToClassName.put(classId, className);
		if (old != null) {
			throw new MalformedHprofException("Duplicate class id: " + classId);
		}
	}

	/**
	 * Read id.
	 *
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private int readId() throws IOException {
		return in.readInt();
	}

	/**
	 * Read string.
	 *
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String readString() throws IOException {
		int id = readId();
		if (id == 0) {
			return null;
		}
		String string = idToString.get(id);
		if (string == null) {
			throw new MalformedHprofException("Unknown string id " + id);
		}
		return string;
	}

	/**
	 * Read class.
	 *
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String readClass() throws IOException {
		int id = readId();
		String string = idToClassName.get(id);
		if (string == null) {
			throw new MalformedHprofException("Unknown class id " + id);
		}
		return string;
	}

	/**
	 * Parses the start thread.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseStartThread() throws IOException {
		int threadId = in.readInt();
		int objectId = readId();
		// stack trace where thread was created.
		// serial number apparently not a stack trace id. (int vs ID)
		// we don't use this field.
		in.readInt();
		String threadName = readString();
		String groupName = readString();
		String parentGroupName = readString();
		HprofData.ThreadEvent event = HprofData.ThreadEvent.start(objectId, threadId, threadName, groupName, parentGroupName);
		hprofData.addThreadEvent(event);
	}

	/**
	 * Parses the end thread.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseEndThread() throws IOException {
		int threadId = in.readInt();
		HprofData.ThreadEvent event = HprofData.ThreadEvent.end(threadId);
		hprofData.addThreadEvent(event);
	}

	/**
	 * Parses the stack frame.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseStackFrame() throws IOException {
		int stackFrameId = readId();
		String methodName = readString();
		readString();
		String file = readString();
		String className = readClass();
		int line = in.readInt();
		StackTraceElement stackFrame = new StackTraceElement(className, methodName, file, line);
		StackTraceElement old = idToStackFrame.put(stackFrameId, stackFrame);
		if (old != null) {
			throw new MalformedHprofException("Duplicate stack frame id: " + stackFrameId);
		}
	}

	/**
	 * Parses the stack trace.
	 *
	 * @param recordLength the record length
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseStackTrace(int recordLength) throws IOException {
		int stackTraceId = in.readInt();
		int threadId = in.readInt();
		int frames = in.readInt();
		int expectedLength = ProfilerConstants.FOUR+ ProfilerConstants.FOUR + ProfilerConstants.FOUR + (frames * BinaryHprof.ID_SIZE);
		if (recordLength != expectedLength) {
			throw new MalformedHprofException("Expected stack trace record of size " + expectedLength + " based on number of frames but header "
					+ "specified a length of  " + recordLength);
		}
		StackTraceElement[] stackFrames = new StackTraceElement[frames];
		for (int i = 0; i < frames; i++) {
			int stackFrameId = readId();
			StackTraceElement stackFrame = idToStackFrame.get(stackFrameId);
			if (stackFrame == null) {
				throw new MalformedHprofException("Unknown stack frame id " + stackFrameId);
			}
			stackFrames[i] = stackFrame;
		}

		HprofData.StackTrace stackTrace = new HprofData.StackTrace(stackTraceId, threadId, stackFrames);
		if (strict) {
			hprofData.addStackTrace(stackTrace, new int[1]);
		} else {
			// The RI can have duplicate stacks, presumably they
			// have a minor race if two samples with the same
			// stack are taken around the same time. if we have a
			// duplicate, just skip adding it to hprofData, but
			// register it locally in idToStackFrame. if it seen
			// in CPU_SAMPLES, we will find a StackTrace is equal
			// to the first, so they will share a countCell.
			int[] countCell = stackTraces.get(stackTrace);
			if (countCell == null) {
				hprofData.addStackTrace(stackTrace, new int[1]);
			}
		}

		HprofData.StackTrace old = idToStackTrace.put(stackTraceId, stackTrace);
		if (old != null) {
			throw new MalformedHprofException("Duplicate stack trace id: " + stackTraceId);
		}

	}

	/**
	 * Parses the alloc sites.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseAllocSites() throws IOException {

		// Just Reading Bit mask
		in.readShort();

		heapBean.setCutOffRatio(in.readFloat());
		heapBean.setTotalLiveBytes(in.readInt());
		heapBean.setTotalLiveInstances(in.readInt());
		heapBean.setTotalByteAllocated(in.readLong());
		heapBean.setTotalInstancesAllocated(in.readLong());

		int noOfSites = in.readInt();
		heapBean.setNoOfSites(noOfSites);
		HeapAllocSitesBean.SiteDescriptor siteDescriptor;

		for (int i = 0; i < noOfSites; i++) {
			siteDescriptor = new HeapAllocSitesBean.SiteDescriptor();

			// Just Reading array indicator
			in.readByte();

			siteDescriptor.setClassName(idToClassName.get(in.readInt()));

			// Just Reading stack serial number
			siteDescriptor.setStackTraceId(in.readInt());

			siteDescriptor.setLiveBytes(in.readInt());
			siteDescriptor.setLiveInstances(in.readInt());
			siteDescriptor.setBytesAllocated(in.readInt());
			siteDescriptor.setInstanceAllocated(in.readInt());
			heapBean.addToSiteDetails(siteDescriptor);
		}
	}

	/**
	 * Parses the cpu samples.
	 *
	 * @param recordLength the record length
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseCpuSamples(int recordLength) throws IOException {
		int totalSamples = in.readInt();
		int samplesCount = in.readInt();
		
		int expectedLength = ProfilerConstants.FOUR + ProfilerConstants.FOUR + (samplesCount * (ProfilerConstants.FOUR + ProfilerConstants.FOUR));
		if (recordLength != expectedLength) {
			throw new MalformedHprofException("Expected CPU samples record of size " + expectedLength + " based on number of samples but header "
					+ "specified a length of  " + recordLength);
		}
		cpuSamplesBean.setTotalSamplesCount(totalSamples);
		CPUSamplesBean.SampleDescriptor sampleDescriptor;
		int total = 0;
		String methodName;
		String className;
		StackTraceElement trace;
		for (int i = 0; i < samplesCount; i++) {
			int count = in.readInt();
			int stackTraceId = in.readInt();
			HprofData.StackTrace stackTrace = idToStackTrace.get(stackTraceId);
			if (stackTrace == null) {
				throw new MalformedHprofException("Unknown stack trace id " + stackTraceId);
			}
			if (count == 0) {
				throw new MalformedHprofException("Zero sample count for stack trace " + stackTrace);
			}

			sampleDescriptor = new CPUSamplesBean.SampleDescriptor();
			// Adding count to SampleDescriptor
			sampleDescriptor.setCount(count);
			// Adding self percentage in sampleDescriptor
			sampleDescriptor.setSelfPercentage(calculateSelfPercentage(totalSamples, count));
			StackTraceElement[] frames = stackTrace.getStackFrames();
			if (frames != null) {
				trace = frames[frames.length - 1];
				methodName = trace.getMethodName();
				className = trace.getClassName();
				// Adding method information to SampleDescriptor
				sampleDescriptor.setQualifiedMethod(className + "." + methodName);
			}
			cpuSamplesBean.addDescriptorToList(sampleDescriptor);
			int[] countCell = stackTraces.get(stackTrace);
			count += countCell[0];
			countCell[0] = count;
			total += count;
		}
		if (strict && totalSamples != total) {
			throw new MalformedHprofException("Expected a total of " + totalSamples + " samples but saw " + total);
		}
	}

	/**
	 * Calculate self percentage.
	 *
	 * @param totalCount the total count
	 * @param selfCount the self count
	 * @return the float
	 */
	private float calculateSelfPercentage(int totalCount, int selfCount) {
		return ((float) selfCount / (float) totalCount) * ProfilerConstants.HUNDRED;
	}

	/**
	 * Gets the stack frames map.
	 *
	 * @return the stack frames map
	 */
	public Map<Integer, StackTraceElement> getStackFramesMap() {
		return idToStackFrame;
	}

	/**
	 * Gets the id to stack trace.
	 *
	 * @return the id to stack trace
	 */
	public Map<Integer, HprofData.StackTrace> getIdToStackTrace() {
		return idToStackTrace;
	}

}