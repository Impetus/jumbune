package org.jumbune.datavalidation;

import static org.jumbune.datavalidation.DataValidationConstants.SLAVE_FILE_LOC;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;
import org.jumbune.common.utils.Constants;
import org.jumbune.datavalidation.ArrayListWritable;
import org.jumbune.utils.JobUtil;


	

/**
 * The Reducer takes <dataviolation type, Iterable<DataViolationWritableBean>> as input and writes a list of all data violation beans corresponding to
 * different data violation types.
 * 

 * 
 */
public class DataValidationReducer extends Reducer<Text, DataDiscrepanciesArrayWritable, Text, DataViolationWritable> {

	/** The dir path. */
	private String dirPath;
	
	/** The file handler map. */
	private Map<String, BufferedWriter> fileHandlerMap;
	
	
	/** The max violations in report. */
	private int maxViolationsInReport;
		
	/** The offset lines map. 
	 * This map is used to keep track total number of lines processed against an offset which is the end offset of split
	 * A TreeMap implementation is used further so as to keep the records sorted by end offset of split. 
	 * 
	 **/
	private Map<FileOffsetKey, Long> offsetLinesMap;
	
	private Set<String> fileNames ;
	
	private MultiValueTreeMap<String, ViolationPersistenceBean> nullMap ;
	
	private MultiValueTreeMap<String, ViolationPersistenceBean> dataTypeMap ;
	
	private MultiValueTreeMap<String, ViolationPersistenceBean> regexMap ;
	
	private MultiValueTreeMap<String, ViolationPersistenceBean> numFieldsMap ;
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#setup(org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setup(Reducer.Context context) throws IOException, InterruptedException {
		super.setup(context);
		maxViolationsInReport = context.getConfiguration().getInt(DataValidationConstants.DV_NUM_REPORT_VIOLATION, 1000);
		String dir = context.getConfiguration().get(SLAVE_FILE_LOC);
		dirPath = JobUtil.getAndReplaceHolders(dir);
		fileHandlerMap = new DVLRUCache(DataValidationConstants.TEN);
		
		offsetLinesMap = new TreeMap<>();
		
		ViolationPersistenceBean bean = new ViolationPersistenceBean();
		bean.setLineNum(Integer.MAX_VALUE);
		
		nullMap = new MultiValueTreeMap<String,ViolationPersistenceBean>(maxViolationsInReport);
		dataTypeMap = new MultiValueTreeMap<String,ViolationPersistenceBean>(maxViolationsInReport);
		regexMap = new MultiValueTreeMap<String,ViolationPersistenceBean>(maxViolationsInReport);
		numFieldsMap = new MultiValueTreeMap<String,ViolationPersistenceBean>(maxViolationsInReport); 
		fileNames = new HashSet<String>();
	}

	/**
	 * reduce method takes <dataviolation type, Iterable<DataViolationWritableBean>> as input and writes a list of all data violation beans corresponding to
	 * different data violation types.
	 */
	public void reduce(Text key, Iterable<DataDiscrepanciesArrayWritable> values, Context context) throws IOException, InterruptedException {
	
		String[] falseSplits = key.toString().split("DDAW");
		if (falseSplits.length > 1) {
			key = new Text(falseSplits[0]);
		}
		createDirectory(key);
		IntWritable fieldNumber = new IntWritable();
		IntWritable fieldViolations = new IntWritable(0);
		long totalFieldViolations = 0;
		long totalNullCheckViolations = 0;
		long totalDataTypeViolations = 0;
		long totalRegexCheckViolations = 0;
		MapWritable nullCheckMapWritable = null;
		MapWritable dataTypeCheckMapWritable = null;
		MapWritable regexCheckMapWritable = null;
		MapWritable fieldMapWritable = null;
		Map<String, Integer> nullCheckfileViolationsMap = null;
		Map<String, Integer> dataTypeFileViolationsMap = null;
		Map<String, Integer> regexCheckFileViolationsMap = null;
		Map<String, Integer> fieldFileViolationsMap = null;
		Set<String> dirtyFieldTupleSet = new HashSet<String>();
		Set<String> dirtyDataTypeTupleSet = new HashSet<String>();
		Set<String> dirtyRegexTupleSet = new HashSet<String>();
		Set<String> dirtyNullCheckSet = new HashSet<String>();
		

		for (DataDiscrepanciesArrayWritable dvarrayWritable : values) {
			for (Writable writable : dvarrayWritable.get()) {
				DataViolationWB dataViolationWB = (DataViolationWB) writable;
				if (dataViolationWB != null) {
					MapWritable fieldMap = dataViolationWB.getFieldMap();
					if (fieldMap == null || fieldMap.isEmpty())
						return;
					// maintaining offsetLinesMap & offsetFilesMap according to
					// split end offset.
					Long splitEndOffset = dataViolationWB.getSplitEndOffset().get();
					long totalRecEmiByMap = dataViolationWB.getTotalRecordsEmittByMap().get();
					String fileName = dataViolationWB.getFileName().toString();
					FileOffsetKey fileOffsetKey = new FileOffsetKey(fileName, splitEndOffset);

					offsetLinesMap.put(fileOffsetKey, totalRecEmiByMap);

					for (Entry<Writable, Writable> entries : fieldMap.entrySet()) {
						FieldLWB fieldLWB = (FieldLWB) entries.getValue();
						MapWritable typeVioMap = fieldLWB.getTypeViolationMap();
						for (Entry<Writable, Writable> entry : typeVioMap.entrySet()) {
							ViolationLWB violationLWB = (ViolationLWB) entry.getValue();
							ArrayListWritable<LineLWB> lineLWBs = violationLWB.getLineLWBList();
							for (LineLWB lineLWB : lineLWBs) {
								ViolationPersistenceBean bean = null;
								switch (entry.getKey().toString()) {
								case DataValidationConstants.NUM_OF_FIELDS_CHECK:
									dirtyFieldTupleSet.add(fileName + lineLWB.getLineNumber());
									totalFieldViolations++;
									if (fieldMapWritable == null || fieldFileViolationsMap == null) {
										fieldMapWritable = new MapWritable();
										fieldFileViolationsMap = new LinkedHashMap<String, Integer>();
									}
									bean = new ViolationPersistenceBean(((IntWritable) entries.getKey()).get(),
											lineLWB.getLineNumber().get(), violationLWB.getExpectedValue().toString(),
											lineLWB.getActualValue().toString(), entry.getKey().toString(), fileName,
											splitEndOffset);
									numFieldsMap.add(bean.getFileName(), bean);
									fileNames.add(bean.getFileName());
									processTupleViolation(fieldMapWritable, fieldFileViolationsMap, entries.getKey(),
											fileName);
									break;
								case DataValidationConstants.USER_DEFINED_NULL_CHECK:
									dirtyNullCheckSet.add(fileName + lineLWB.getLineNumber());
									totalNullCheckViolations++;
									if (nullCheckMapWritable == null || nullCheckfileViolationsMap == null) {
										nullCheckMapWritable = new MapWritable();
										nullCheckfileViolationsMap = new LinkedHashMap<String, Integer>();
									}

									bean = new ViolationPersistenceBean(((IntWritable) entries.getKey()).get(),
											lineLWB.getLineNumber().get(), violationLWB.getExpectedValue().toString(),
											lineLWB.getActualValue().toString(), entry.getKey().toString(), fileName,
											splitEndOffset);
									nullMap.add(bean.getFileName(), bean);
									fileNames.add(bean.getFileName());
									processTupleViolation(nullCheckMapWritable, nullCheckfileViolationsMap,
											entries.getKey(), fileName);
									break;
								case DataValidationConstants.USER_DEFINED_DATA_TYPE:
									dirtyDataTypeTupleSet.add(fileName + lineLWB.getLineNumber());
									totalDataTypeViolations++;
									if (dataTypeCheckMapWritable == null || dataTypeFileViolationsMap == null) {
										dataTypeCheckMapWritable = new MapWritable();
										dataTypeFileViolationsMap = new LinkedHashMap<String, Integer>();
									}

									bean = new ViolationPersistenceBean(((IntWritable) entries.getKey()).get(),
											lineLWB.getLineNumber().get(), violationLWB.getExpectedValue().toString(),
											lineLWB.getActualValue().toString(), entry.getKey().toString(), fileName,
											splitEndOffset);
									dataTypeMap.add(bean.getFileName(), bean);
									fileNames.add(bean.getFileName());
									processTupleViolation(dataTypeCheckMapWritable, dataTypeFileViolationsMap,
											entries.getKey(), fileName);
									break;
								case DataValidationConstants.USER_DEFINED_REGEX_CHECK:
									dirtyRegexTupleSet.add(fileName + lineLWB.getLineNumber());
									totalRegexCheckViolations++;
									if (regexCheckMapWritable == null || regexCheckFileViolationsMap == null) {
										regexCheckMapWritable = new MapWritable();
										regexCheckFileViolationsMap = new LinkedHashMap<String, Integer>();
									}
									bean = new ViolationPersistenceBean(((IntWritable) entries.getKey()).get(),
											lineLWB.getLineNumber().get(), violationLWB.getExpectedValue().toString(),
											lineLWB.getActualValue().toString(), entry.getKey().toString(), fileName,
											splitEndOffset);
									regexMap.add(bean.getFileName(), bean);
									fileNames.add(bean.getFileName());
									processTupleViolation(regexCheckMapWritable, regexCheckFileViolationsMap,
											entries.getKey(), fileName);
									break;
								default:
									break;
								}
							}
						}
					}
				}
			}
		}
		for (BufferedWriter bw : fileHandlerMap.values()) {
			bw.close();
		}
		fileHandlerMap.clear();
		long dirtyTuple = 0;
		
		if (nullCheckfileViolationsMap != null) {
			dirtyTuple = dirtyNullCheckSet.size();
			writeViolations(DataValidationConstants.USER_DEFINED_NULL_CHECK, context, totalNullCheckViolations,
					fieldNumber, fieldViolations, nullCheckMapWritable, nullCheckfileViolationsMap, dirtyTuple);
		}
		if (dataTypeFileViolationsMap != null) {
			dirtyTuple = dirtyDataTypeTupleSet.size();
			writeViolations(DataValidationConstants.USER_DEFINED_DATA_TYPE, context, totalDataTypeViolations,
					fieldNumber, fieldViolations, dataTypeCheckMapWritable, dataTypeFileViolationsMap, dirtyTuple);
		}
		if (regexCheckFileViolationsMap != null) {
			dirtyTuple = dirtyRegexTupleSet.size();
			writeViolations(DataValidationConstants.USER_DEFINED_REGEX_CHECK, context, totalRegexCheckViolations,
					fieldNumber, fieldViolations, regexCheckMapWritable, regexCheckFileViolationsMap, dirtyTuple);
		}
		if (fieldFileViolationsMap != null) {
			dirtyTuple = dirtyFieldTupleSet.size();
			writeViolations(DataValidationConstants.NUM_OF_FIELDS_CHECK, context, totalFieldViolations, fieldNumber,
					fieldViolations, fieldMapWritable, fieldFileViolationsMap, dirtyTuple);
		}
	}

	private void setActualLineNo(ViolationPersistenceBean bean) {	 
		 bean.setLineNum(calculateActualLineNo(bean));    	
	}

	/**
	 * Calculate actual line no based on the total number of lines in each split.
	 *
	 * @param bean the bean
	 * @return the int
	 */
	public int calculateActualLineNo(ViolationPersistenceBean bean) {
		long splitEndOff = bean.getSplitEndOffset();
		long sum = 0; 	  
		FileOffsetKey key ;
	    //this fragment of code calculates the sum of all the values in the map
	    // till the splitEndOff is encountered in the keys.  
	    for(Entry<FileOffsetKey, Long> entry: offsetLinesMap.entrySet()) {	    				 
				//verifying that the split belong to the same file
				//entry.getValue() == offsetLinesMap.get(bean.getSplitEndOffset())		
	    	key = entry.getKey();
				//if(offsetFilesMap.get(entry.getKey()).equals(bean.getFileName())){
	    	if(key.getFileName().equals(bean.getFileName())){
					if(key.getOffset() == splitEndOff) {
						break;
					} else {
						sum += entry.getValue();					
					}
				}
			}
		return (int) (bean.getLineNum() + sum);
	 
	}
	
	/**
	 * Insert element to violation list.
	 * This method inserts a new item to the appropriate position in array of ViolationPersistenceBean.
	 * A Position(to insert new item) is considered appropriate such that the array remains sorted by actual line number(line number of violation). 
	 * @param arr the arr
	 * @param element the element
	 */
	private void insertElementToViolationList(ViolationPersistenceBean arr[], ViolationPersistenceBean element) {
		int indexToInsert = -1;
		for (int i = 0; i < arr.length; i++) {
			if(calculateActualLineNo(element) < calculateActualLineNo(arr[i])){
        	   indexToInsert = i;
        	   break;
           }
		}
		insert(indexToInsert, arr, element);
	}
	
	/**
	 * Inserts the element to a specific position and advances by one the position of elements that follow.
	 *
	 * @param index the index
	 * @param arr the arr
	 * @param element the element
	 */
	private void insert(final int index, ViolationPersistenceBean arr[], ViolationPersistenceBean element) {	    
		if(index == -1) return;
		for (int i = arr.length - 1; i > index; i--) {
			arr[i] = arr[i - 1];
		}
		arr[index] = element;
	}
	
	
	
	private void writeViolations(String violatoinType, Context context, long totalViolations, IntWritable fieldNumber,
			IntWritable fieldViolations, MapWritable mapWritable, Map<String, Integer> fileViolationsMap,
			long dirtyTuple) throws IOException, InterruptedException {

		ArrayListWritable<FileViolationsWritable> fileVioWriList = new ArrayListWritable<FileViolationsWritable>();
		FileViolationsWritable fvWritable;
		for (Map.Entry<String, Integer> violationMap : fileViolationsMap.entrySet()) {
			fvWritable = new FileViolationsWritable();
			fvWritable.setFileName(violationMap.getKey());
			fvWritable.setNumOfViolations(violationMap.getValue());
			fileVioWriList.add(fvWritable);
		}

		DataViolationWritable dataViolationWritable = new DataViolationWritable();
		if (mapWritable == null) {
			mapWritable = new MapWritable();
			mapWritable.put(fieldNumber, fieldViolations);
		}
		dataViolationWritable.setFieldMap(mapWritable);
		dataViolationWritable.setTotalViolations(totalViolations);
		dataViolationWritable.setFileViolationsWritables(fileVioWriList);
		dataViolationWritable.setDirtyTuple(dirtyTuple);
		context.write(new Text(violatoinType), dataViolationWritable);
	}

	private void processTupleViolation(MapWritable fieldMapWritable,
			Map<String, Integer> fieldFileViolationsMap, Writable fieldNo,String fileName)
			throws IOException {
		IntWritable fieldNumber = new IntWritable();
		IntWritable fieldViolations = new IntWritable(0);
		int violations;
		fieldNumber = (IntWritable) fieldNo;
		fieldViolations = (IntWritable) fieldMapWritable.get((fieldNumber));
		fieldViolations = setFieldViolations(fieldViolations);
		fieldMapWritable.put(fieldNumber, fieldViolations);
		violations = extractViolationsFromMap(fieldFileViolationsMap, fileName);
		violations += 1;
		fieldFileViolationsMap.put(fileName, violations);
	}

	private void createDirectory(Text key) {
			File f = new File(dirPath + File.separator + key.toString());
			f.mkdirs();
			f.setReadable(true, false);
			f.setWritable(true, false);	
	}
	
	/**
	 * Extract violations from map.
	 *
	 * @param fileViolationsMap the file violations map
	 * @param fileName the file name
	 * @return the int
	 */
	private int extractViolationsFromMap(
			Map<String, Integer> fileViolationsMap, String fileName) {
		int violations;
		if (fileViolationsMap.containsKey(fileName)) {
			violations = fileViolationsMap.get(fileName);
		} else {
			violations = 0;
		}
		return violations;
	}

	/**
	 * Sets the field violations.
	 *
	 * @param fieldViolations the field violations
	 * @return the int writable
	 */
	private IntWritable setFieldViolations(final IntWritable fieldViolations) {
		IntWritable fldViolations = fieldViolations;
		if (fldViolations != null) {
			fldViolations.set(fldViolations.get() + 1);
		} else {
			fldViolations = new IntWritable(1);
		}
		return fldViolations;
		
	}


	/**
	 * Write violations to file. This method writes violations to respective files in corresponding directories(null, data type, regex, no. of field).
	 *
	 * @param fieldNumber the field number
	 * @param lineNumber the line number
	 * @param expectedValue the expected value
	 * @param actualValue the actual value
	 * @param violType the viol type
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void appendViolationToBuffer(StringBuffer stringBuffer, int fieldNumber, long lineNumber, String expectedValue, String actualValue) throws IOException {
		stringBuffer.append(lineNumber);
		stringBuffer.append(Constants.PIPE_SEPARATOR);

		if (fieldNumber == -1) {
			stringBuffer.append("-");
		} else {
			stringBuffer.append(fieldNumber);
		}

		stringBuffer.append(Constants.PIPE_SEPARATOR).append(expectedValue).append(Constants.PIPE_SEPARATOR)
				.append(actualValue).append(System.lineSeparator());
	}
	
	/**
	 * Gets the file handler.
	 *
	 * @param fileName the file name
	 * @param  violatino type 
	 * @return the file handler
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private BufferedWriter getFileHandler(String fileName, String violationType) throws IOException {
		String absoluteFilePath = dirPath +File.separator+ violationType + File.separator+ fileName + "-" + new Random().nextInt() + "-" + System.nanoTime();
		BufferedWriter out = fileHandlerMap.get(absoluteFilePath);
		if (out == null) {
			File f = new File(absoluteFilePath);
			f.setReadable(true, false);
	        f.setWritable(true, false);		
			out = new BufferedWriter(new FileWriter(f));
			fileHandlerMap.put( absoluteFilePath, out);
		}
		return out;
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#cleanup(org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void cleanup(Reducer.Context context) throws IOException, InterruptedException {		
		StringBuffer stringBuffer;
		for (String fileName : fileNames) {

		if(nullMap != null && !nullMap.isEmpty() && nullMap.getAllElements(fileName) != null){
			stringBuffer = new StringBuffer();
		for (ViolationPersistenceBean bean : nullMap.getAllElements(fileName)) {
			if(bean != null && bean.getViolationType() != null) {  
			appendViolationToBuffer(stringBuffer, bean.getFieldNum(), bean.getLineNum(), bean.getExpectedValue(), bean.getActualValue());
			}
		}
		BufferedWriter out = null;
		out = getFileHandler(fileName, DataValidationConstants.USER_DEFINED_NULL_CHECK);
		out.write(stringBuffer.toString());
		out.flush();
		}
		if(dataTypeMap !=null  && !dataTypeMap.isEmpty() && dataTypeMap.getAllElements(fileName)!=null){
			stringBuffer = new StringBuffer();
		for (ViolationPersistenceBean bean : dataTypeMap.getAllElements(fileName)) {
			if(bean != null && bean.getViolationType() != null) {
			appendViolationToBuffer(stringBuffer, bean.getFieldNum(), bean.getLineNum(), bean.getExpectedValue(), bean.getActualValue());
			}
		}
			BufferedWriter out = null;
			out = getFileHandler(fileName, DataValidationConstants.USER_DEFINED_DATA_TYPE);
			out.write(stringBuffer.toString());
			out.flush();
		}
		if(regexMap != null && !regexMap.isEmpty() && regexMap.getAllElements(fileName) != null){
			stringBuffer = new StringBuffer();
		for (ViolationPersistenceBean bean : regexMap.getAllElements(fileName)) {
			if(bean != null && bean.getViolationType() != null) {
			appendViolationToBuffer(stringBuffer, bean.getFieldNum(), bean.getLineNum(), bean.getExpectedValue(), bean.getActualValue());
			}
		}
			BufferedWriter out = null;
			out = getFileHandler(fileName, DataValidationConstants.USER_DEFINED_REGEX_CHECK);
			out.write(stringBuffer.toString());
			out.flush();
		}
		if(numFieldsMap != null && !numFieldsMap.isEmpty() && numFieldsMap.getAllElements(fileName) != null){
			stringBuffer = new StringBuffer();
		for (ViolationPersistenceBean bean : numFieldsMap.getAllElements(fileName)) {
			if(bean != null && bean.getViolationType() != null) {
			appendViolationToBuffer(stringBuffer, bean.getFieldNum(), bean.getLineNum(), bean.getExpectedValue(), bean.getActualValue());				
			}
		}
			BufferedWriter out = null;
			out = getFileHandler(fileName, DataValidationConstants.NUM_OF_FIELDS_CHECK);
			out.write(stringBuffer.toString());
			out.flush();		
		}
		}		
		for (BufferedWriter bw : fileHandlerMap.values()) {
			bw.close();
		}
		super.cleanup(context);
	}
	

	/**
	 * The Class FileOffsetKey contains filename and the offset.
	 */
	private static class FileOffsetKey implements Comparable<FileOffsetKey> {
		Integer i;
		private String fileName;
		private long offset;

		FileOffsetKey(String fileName, long offset) {
			this.fileName = fileName;
			this.offset = offset;
		}

		public String getFileName() {
			return fileName;
		}

		public long getOffset() {
			return offset;
		}

		@Override
		public int compareTo(FileOffsetKey o) {
			int i = this.fileName.compareTo(o.getFileName());
			if (i == 0) {
				if (this.offset == o.getOffset()) {
					i = 0;
				} else if (this.offset < o.getOffset()) {
					i = -1;
				} else if (this.offset > o.getOffset()) {
					i = 1;
				}
			}
			return i;
		}

		@Override
		public String toString() {
			return "Com [" + fileName + "," + offset + "]";
		}

	}
	
	class ViolationPersistenceBean implements Comparable<ViolationPersistenceBean> {
		
		private int fieldNum;
		private long lineNum;
		private String expectedValue;
		private String actualValue;
		private String violationType;
		private String fileName;
		private long splitEndOffset;
		
		public ViolationPersistenceBean() {
		
		}
		
  	public ViolationPersistenceBean(int fieldNum, long lineNum, String expectedValue, String actualValue,
			String violationType, String fileName, long splitEndOffset) {
		this.fieldNum = fieldNum;
		this.lineNum = lineNum;
		this.expectedValue = expectedValue;
		this.actualValue = actualValue;
		this.violationType = violationType;
		this.fileName = fileName;
		this.splitEndOffset = splitEndOffset;
	}



		
		

		public int getFieldNum() {
			return fieldNum;
		}



		public String getExpectedValue() {
			return expectedValue;
		}



		public String getActualValue() {
			return actualValue;
		}



		public String getViolationType() {
			return violationType;
		}



		public String getFileName() {
			return fileName;
		}



		public long getSplitEndOffset() {
			return splitEndOffset;
		}

		@Override
		public String toString() {
			return "ViolationPersistenceBean [fieldNum=" + fieldNum + ", lineNum=" + getLineNum() + ", expectedValue="
					+ expectedValue + ", actualValue=" + actualValue + ", violationType=" + violationType
					+ ", fileName=" + fileName + ", splitEndOffset=" + splitEndOffset + "]";
		}

		public long getLineNum() {
			return lineNum;
		}

		public void setLineNum(long lineNum) {
			this.lineNum = lineNum;
		}

		@Override
		public int compareTo(ViolationPersistenceBean otherViolations) {
			int compareResult = calculateActualLineNo(otherViolations)<(calculateActualLineNo(this))?1:calculateActualLineNo(otherViolations)>(calculateActualLineNo(this))?-1:0;
			return compareResult;
		}

	}
	

		
		
		
		
	
	
}