package org.jumbune.profiling.hprof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jumbune.profiling.utils.ProfilerConstants;



/**
 * Represents sampling profiler data. Can be converted to ASCII or binary hprof-style output using {@link AsciiHprofWriter} or
 * {@link BinaryHprofWriter}.
 * <p>
 * The data includes:
 * <ul>
 * <li>the start time of the last sampling period
 * <li>the history of thread start and end events
 * <li>stack traces with frequency counts
 * <ul>
 */
public final class HprofData {

	/**
	 * The Enum ThreadEventType.
	 */
	public static enum ThreadEventType {
		
		/** The start. */
		START, 
		/** The end. */
		END
	};

	/**
	 * ThreadEvent represents thread creation and death events for reporting. It provides a record of the thread and thread group names for tying
	 * samples back to their source thread.
	 */
	public static final class ThreadEvent {

		/** The type. */
		private final ThreadEventType type;
		
		/** The object id. */
		private final int objectId;
		
		/** The thread id. */
		private final int threadId;
		
		/** The thread name. */
		private final String threadName;
		
		/** The group name. */
		private final String groupName;
		
		/** The parent group name. */
		private final String parentGroupName;

		/**
		 * This method starts a thread event.
		 *
		 * @param objectId the object id
		 * @param threadId the thread id
		 * @param threadName the thread name
		 * @param groupName the group name
		 * @param parentGroupName the parent group name
		 * @return the thread event
		 */
		public static ThreadEvent start(int objectId, int threadId, String threadName, String groupName, String parentGroupName) {
			return new ThreadEvent(objectId, threadId, threadName, groupName, parentGroupName);
		}

		/**
		 * This method ends a thread event.
		 *
		 * @param threadId the thread id
		 * @return the thread event
		 */
		public static ThreadEvent end(int threadId) {
			return new ThreadEvent(threadId);
		}

		/**
		 * Instantiates a new thread event.
		 *
		 * @param objectId the object id
		 * @param threadId the thread id
		 * @param threadName the thread name
		 * @param groupName the group name
		 * @param parentGroupName the parent group name
		 */
		private ThreadEvent(int objectId, int threadId, String threadName, String groupName, String parentGroupName) {
			if (threadName == null) {
				throw new IllegalArgumentException("threadName == null");
			}
			this.type = ThreadEventType.START;
			this.objectId = objectId;
			this.threadId = threadId;
			this.threadName = threadName;
			this.groupName = groupName;
			this.parentGroupName = parentGroupName;
		}

		/**
		 * Instantiates a new thread event.
		 *
		 * @param threadId the thread id
		 */
		private ThreadEvent(int threadId) {
			this.type = ThreadEventType.END;
			this.objectId = -1;
			this.threadId = threadId;
			this.threadName = null;
			this.groupName = null;
			this.parentGroupName = null;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			int result = ProfilerConstants.SEVENTEEN;
			result = ProfilerConstants.THIRTY_ONE * result + objectId;
			result = ProfilerConstants.THIRTY_ONE  * result + threadId;
			result = ProfilerConstants.THIRTY_ONE  * result + hashCode(threadName);
			result = ProfilerConstants.THIRTY_ONE  * result + hashCode(groupName);
			result = ProfilerConstants.THIRTY_ONE  * result + hashCode(parentGroupName);
			return result;
		}

		/**
		 * This method returns the  Hash code of the Object.
		 *
		 * @param o the o
		 * @return the int
		 */
		private static int hashCode(Object o) {
			return (o == null) ? 0 : o.hashCode();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof ThreadEvent)) {
				return false;
			}
			ThreadEvent event = (ThreadEvent) o;
			return (this.type == event.type && this.objectId == event.objectId && this.threadId == event.threadId
					&& equal(this.threadName, event.threadName) && equal(this.groupName, event.groupName) && equal(this.parentGroupName,
						event.parentGroupName));
		}

		/**
		 * Equal.
		 *
		 * @param a the a
		 * @param b the b
		 * @return true, if successful
		 */
		private static boolean equal(Object a, Object b) {
			return a != null && a.equals(b);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			switch (type) {
			case START:
				return String.format("THREAD START (obj=%d, id = %d, name=\"%s\", group=\"%s\")", objectId, threadId, threadName, groupName);
			case END:
				return String.format("THREAD END (id = %d)", threadId);
			}
			throw new IllegalStateException(type.toString());
		}
	}

	/**
	 * A unique stack trace for a specific thread.
	 */
	public static final class StackTrace {

		/** The stack trace id. */
		private final int stackTraceId;
		
		/** The thread id. */
		private int threadId;
		
		/** The stack frames. */
		private StackTraceElement[] stackFrames;

		/**
		 * Instantiates a new stack trace.
		 */
		StackTrace() {
			this.stackTraceId = -1;
		}

		/**
		 * Instantiates a new stack trace.
		 *
		 * @param stackTraceId the stack trace id
		 * @param threadId the thread id
		 * @param stackFrames the stack frames
		 */
		public StackTrace(int stackTraceId, int threadId, StackTraceElement[] stackFrames) {
			if (stackFrames == null) {
				throw new IllegalArgumentException("stackFrames == null");
			}
			this.stackTraceId = stackTraceId;
			this.threadId = threadId;
			this.stackFrames = Arrays.copyOf(stackFrames, stackFrames.length);
		}

		/**
		 * Gets the thread id.
		 *
		 * @return the thread id
		 */
		public int getThreadId() {
			return threadId;
		}

		/**
		 * Gets the stack frames.
		 *
		 * @return the stack frames
		 */
		public StackTraceElement[] getStackFrames() {
			return stackFrames;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			int result = ProfilerConstants.SEVENTEEN;
			result = ProfilerConstants.THIRTY_ONE  * result + threadId;
			result = ProfilerConstants.THIRTY_ONE  * result + Arrays.hashCode(stackFrames);
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof StackTrace)) {
				return false;
			}
			StackTrace s = (StackTrace) o;
			return threadId == s.threadId && Arrays.equals(stackFrames, s.stackFrames);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder frames = new StringBuilder();
			if (stackFrames.length > 0) {
				frames.append('\n');
				for (StackTraceElement stackFrame : stackFrames) {
					frames.append("\t at ");
					frames.append(stackFrame);
					frames.append('\n');
				}
			} else {
				frames.append("<empty>");
			}
			return "StackTrace[stackTraceId=" + stackTraceId + ", threadId=" + threadId + ", frames=" + frames + "]";

		}

		/**
		 * Gets the trace string.
		 *
		 * @return the trace string
		 */
		public String getTraceString() {
			StringBuilder frames = new StringBuilder();
			if (stackFrames.length > 0) {
				frames.append("\n\t");
				for (StackTraceElement stackFrame : stackFrames) {
					frames.append(stackFrame);
					frames.append("\n\t");
				}
			} else {
				frames.append("<empty>");
			}
			return frames.toString();
		}

		/**
		 * This method will convert StackTraceElement[] to list of String which are required for json format.
		 *
		 * @return the stack trace list
		 */
		public List<String> getStackTraceList() {
			List<String> stackTraceList = new ArrayList<String>();

			if (stackFrames.length > 0) {
				for (StackTraceElement stackFrame : stackFrames) {
					stackTraceList.add(stackFrame.toString());
				}
			}
			return stackTraceList;
		}
	}

	/**
	 * A read only container combining a stack trace with its frequency.
	 */
	public static final class Sample {

		/** The stack trace. */
		private final StackTrace stackTrace;
		
		/** The count. */
		private final int count;

		/**
		 * Instantiates a new sample.
		 *
		 * @param stackTrace the stack trace
		 * @param count the count
		 */
		private Sample(StackTrace stackTrace, int count) {
			if (stackTrace == null) {
				throw new IllegalArgumentException("stackTrace == null");
			}
			if (count < 0) {
				throw new IllegalArgumentException("count < 0:" + count);
			}
			this.stackTrace = stackTrace;
			this.count = count;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			int result = ProfilerConstants.SEVENTEEN;
			result = ProfilerConstants.THIRTY_ONE  * result + stackTrace.hashCode();
			result = ProfilerConstants.THIRTY_ONE  * result + count;
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Sample)) {
				return false;
			}
			Sample s = (Sample) o;
			return count == s.count && stackTrace.equals(s.stackTrace);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Sample[count=" + count + " " + stackTrace + "]";
		}

	}

	/**
	 * Start of last sampling period.
	 */
	private long startMillis;

	/** CONTROL_SETTINGS flags. */
	private int flags;

	/** stack sampling depth. */
	private int depth;

	/**
	 * List of thread creation and death events.
	 */
	private final List<ThreadEvent> threadHistory = new ArrayList<ThreadEvent>();

	/** Map of thread id to a start ThreadEvent. */
	private final Map<Integer, ThreadEvent> threadIdToThreadEvent = new HashMap<Integer, ThreadEvent>();

	/**
	 * Map of stack traces to a mutable sample count. The map is provided by the creator of the HprofData so only have mutable access to the int[]
	 * cells that contain the sample count. Only an unmodifiable iterator view is available to users of the HprofData.
	 */
	private final Map<HprofData.StackTrace, int[]> stackTraces;

	/**
	 * Instantiates a new hprof data.
	 *
	 * @param stackTraces the stack traces
	 */
	public HprofData(Map<StackTrace, int[]> stackTraces) {
		if (stackTraces == null) {
			throw new IllegalArgumentException("stackTraces == null");
		}
		this.stackTraces = stackTraces;
	}

	/**
	 * The start time in milliseconds of the last profiling period.
	 *
	 * @return the start millis
	 */
	public long getStartMillis() {
		return startMillis;
	}

	/**
	 * Set the time for the start of the current sampling period.
	 *
	 * @param startMillis the new start millis
	 */
	public void setStartMillis(long startMillis) {
		this.startMillis = startMillis;
	}

	/**
	 * Get the {@link BinaryHprof.ControlSettings} flags
	 *
	 * @return the flags
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Set the {@link BinaryHprof.ControlSettings} flags
	 *
	 * @param flags the new flags
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}

	/**
	 * Get the stack sampling depth.
	 *
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Set the stack sampling depth.
	 *
	 * @param depth the new depth
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * Return an unmodifiable history of start and end thread events.
	 *
	 * @return the thread history
	 */
	public List<ThreadEvent> getThreadHistory() {
		return Collections.unmodifiableList(threadHistory);
	}

	/**
	 * Return a new set containing the current sample data.
	 *
	 * @return the samples
	 */
	public Set<Sample> getSamples() {
		Set<Sample> samples = new HashSet<Sample>(stackTraces.size());
		for (Entry<StackTrace, int[]> e : stackTraces.entrySet()) {
			StackTrace stackTrace = e.getKey();
			int countCell[] = e.getValue();
			int count = countCell[0];
			Sample sample = new Sample(stackTrace, count);
			samples.add(sample);
		}
		return samples;
	}

	/**
	 * Record an event in the thread history.
	 *
	 * @param event the event
	 */
	public void addThreadEvent(ThreadEvent event) {
		if (event == null) {
			throw new IllegalArgumentException("event == null");
		}
		ThreadEvent old = threadIdToThreadEvent.put(event.threadId, event);
		switch (event.type) {
		case START:
			if (old != null) {
				throw new IllegalArgumentException("ThreadEvent already registered for id " + event.threadId);
			}
			break;
		case END:
			if (old != null && old.type == ThreadEventType.END) {
				throw new IllegalArgumentException("Duplicate ThreadEvent.end for id " + event.threadId);
			}
			break;
		}
		threadHistory.add(event);
	}

	/**
	 * Record an stack trace and an associated int[] cell of sample cound for the stack trace. The caller is allowed retain a pointer to the cell to
	 * update the count. The SamplingProfiler intentionally does not present a mutable view of the count.
	 *
	 * @param stackTrace the stack trace
	 * @param countCell the count cell
	 */
	public void addStackTrace(StackTrace stackTrace, int[] countCell) {
		int[] old = stackTraces.put(stackTrace, countCell);
	}
}
