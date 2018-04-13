package org.jumbune.utils;

import static org.jumbune.utils.UtilitiesConstants.DEFAULT_MESSAGE_SEPARATOR;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.apache.logging.log4j.Logger;

/**
 * This class does the actual work of logging when MR jobs execute. Only call to
 * its methods are injected in compiled classes so these methods will be called
 * from instrumented jar
 */
public final class LogUtil {
	private static final String MAPRED_COUNTER = "org.apache.hadoop.mapred.Task$Counter";
	private static final String MAP_INPUT_RECORDS = "MAP_INPUT_RECORDS";
	private static final String MAP_OUTPUT_RECORDS = "MAP_OUTPUT_RECORDS";
	private static final String REDUCE_INPUT_RECORDS = "REDUCE_INPUT_RECORDS";
	private static final String REDUCE_OUTPUT_RECORDS = "REDUCE_OUTPUT_RECORDS";
	private static final String COUNTERS = "Counter";
	private static final String INFO = "INFO";
	private static final int MAX_VALUES_IN_LOG = 5;
	private static final String RAW_TYPES="rawtypes";
	
	/**
	 * Instantiates a new log util.
	 */
	private LogUtil(){
		
	}
	
	/**
	 * Add logging in map cleanup method
	 * 
	 * @param context
	 *            - map context
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 */
	@SuppressWarnings(RAW_TYPES)
	public static void getMapContextInfoCleanup(Context context,
			String className, String methodName) {
		Counter counter = context.getCounter(MAPRED_COUNTER, MAP_INPUT_RECORDS);
		getLogMsg(className, methodName, counter.getDisplayName(), COUNTERS,
				counter.getValue());

		counter = context.getCounter(MAPRED_COUNTER, MAP_OUTPUT_RECORDS);
		getLogMsg(className, methodName, counter.getDisplayName(), COUNTERS,
				counter.getValue());
	}

	/**
	 * Get desired information from reducer's context like job, reduce instance,
	 * etc
	 * 
	 * @param context
	 *            - reduce context to get all the required information of
	 *            reducer like its job, reduce instance, etc
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 */
	@SuppressWarnings(RAW_TYPES)
	public static void getReduceContextInfo(TaskInputOutputContext context,
			String className, String methodName) {
		Counter counter = context.getCounter(MAPRED_COUNTER,
				REDUCE_INPUT_RECORDS);
		getLogMsg(className, methodName, counter.getDisplayName(), COUNTERS,
				counter.getValue());

		counter = context.getCounter(MAPRED_COUNTER, REDUCE_OUTPUT_RECORDS);
		getLogMsg(className, methodName, counter.getDisplayName(), COUNTERS,
				counter.getValue());
	}

	/**
	 * This method tells the number of times map/reduce executed.
	 * 
	 * @param context
	 *            - reduce context to get all the required information of
	 *            reducer like its job, reduce instance, etc
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 * @param logMsg
	 *            - Message to be shown along with counters
	 * @param count
	 *            - No. of times map/reduce executed
	 */
	@SuppressWarnings(RAW_TYPES)
	public static void getMapReduceExecutionInfo(
			TaskInputOutputContext context, String className,
			String methodName, String logMsg, int count) {
		getLogMsg(className, methodName, logMsg, count);
	}

	/**
	 * This method tells the number of times map/reduce executed.
	 * 
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 * @param logMsg
	 *            - Message to be shown along with counters
	 * @param count
	 *            - No. of times map/reduce executed
	 * @param context
	 *            - reduce context to get all the required information of
	 *            reducer like its job, reduce instance, etc
	 */
	public static void getMapReduceExecutionInfoOldApi(String className,
			String methodName, String logMsg, int count) {
		getLogMsg(className, methodName, logMsg, count);
	}

	/**
	 * A log statement will be added on entry and exit of map/reduce
	 * 
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 * @param logMsg1
	 *            - Message1 to be logged
	 * @param logMsg2
	 *            - Message2 to be logged
	 * @param key
	 *            - Key of map/reduce that will help in uniquely identifying
	 *            every map/reduce entry
	 */
	public static void getMapReduceEntryExitInfo(String className,
			String methodName, String logMsg1, String logMsg2, Object key) {
		getLogMsg(className, methodName, logMsg1, logMsg2, key);
	}

	/**
	 * Logs information about context.write() method calls
	 * 
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 * @param logMsg1
	 *            - Message1 to be logged
	 * @param logMsg2
	 *            - Message2 to be logged
	 * @param logMsg3
	 *            - Message3 to be logged
	 */
	public static void getMapReduceContextWriteInfo(String className,
			String methodName, String logMsg) {
		getLogMsg(className, methodName, logMsg);
	}

	/**
	 * Logs information about the time needed in executing map/reduce
	 * 
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 * @param logMsgPrefix
	 *            - message prefix to be shown
	 * @param time
	 *            - time for executing map/reduce
	 */
	public static void getMapReduceTimerInfo(String className,
			String methodName, String logMsgPrefix, long time) {
		getLogMsg(className, methodName, logMsgPrefix, time);
	}

	/**
	 * Logs information related various loops like for, while, etc
	 * 
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 * @param logMsg1
	 *            - Message1 to be logged
	 * @param counter
	 *            - Index of loop
	 */
	public static void getLoopCounterInfo(String className, String methodName,
			String logMsg1, int counter, String info) {
		getLogMsg(className, methodName, logMsg1, counter, info);
	}

	/**
	 * This method will be if user wants to log return statement of every method
	 * 
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 * @param logMsg
	 *            - Message to be logged
	 */
	public static void getMethodReturn(String className, String methodName,
			String logMsg) {
		getLogMsg(className, methodName, logMsg);
	}

	/**
	 * Log information of if blocks in any method apart from map/reduce i.e.
	 * methods which don't contain context field
	 * 
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 * @param logMsg1
	 *            - message1 to be logged
	 * @param logMsg2
	 *            - Message2 to be logged
	 * @param count
	 *            - the index of if block in that method
	 */

	public static void getIfBlockInfo(String className, String methodName,
			String logMsg1, int lineNumber, String count) {
		getLogMsg(className, methodName, logMsg1, lineNumber, count);
	}

	/**
	 * Log information about the time taken by partitioner in partitioning
	 * sample keys
	 * 
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 * @param message
	 *            - message to be logged
	 * @param sampleCount
	 *            - The total number of samples whose partitioning time is
	 *            calculated
	 * @param timeTaken
	 *            - The time taken by all these samples in partitioning
	 */
	public static void getPartitionerInfo(String className, String methodName,
			String message, Long sampleCount, Long timeTaken) {
		getLogMsg(className, methodName, message, sampleCount, timeTaken);
	}

	/**
	 * This method is used to gets the regular expression info.
	 *
	 * @param className the class name
	 * @param methodName the method name
	 * @param message1 the message1
	 * @param message2 the message2
	 * @param result the result
	 * @return the reg ex info
	 */
	public static void getRegExInfo(String className, String methodName,
			String message1, String message2, boolean result) {
		if (result == false) {
			getLogMsg(className, methodName, message1, message2, "");
		}
	}
	
	/**
	 * This method is used to gets the regular expression info.
	 *
	 * @param className the class name
	 * @param methodName the method name
	 * @param message1 the message1
	 * @param message2 the message2
	 * @param result the result
	 * @param outputValue output key/value of map/reduce
	 * @param inputKey input key of map/reduce
	 * @return the reg ex info
	 */
	public static void getRegExInfo(String className, String methodName,
			String message1, String message2, boolean result, Object outputValue,
			Object inputKey) {
		if (result == false) {
			getLogMsg(className, methodName, message1, message2, outputValue, inputKey);
		}
	}

	/**
	 * Based on the logLevel select appropriate logMethod to log this message
	 * 
	 * @param logMessage
	 *            - message to be logged
	 */
	private static void logMessage(Logger logger, String logMessage) {
		if (logMessage != null) {
			logger.info(logMessage);
		}
	}

	/**
	 * This method logs the information if JumbuneLogging is enabled.
	 * 
	 * @param msgParts
	 *            - Message to be logged and its other related information
	 */
	private static void getLogMsg(Object... msgParts) {
		// If MRContextUtil's is enabled its means we are working in job related
		// method which should be logged
		if (MapReduceExecutionUtil.isJumbuneLog()) {
			StringBuilder msg = new StringBuilder();

			for (Object msgPart : msgParts) {
				msg.append(DEFAULT_MESSAGE_SEPARATOR).append(msgPart);
			}

			// add fillers
			for (int i = MAX_VALUES_IN_LOG - msgParts.length; i > 0; i--) {
				msg.append(DEFAULT_MESSAGE_SEPARATOR).append(UtilitiesConstants.EMPTY_STRING);
			}

			logMessage(LoggerUtil.getMapReduceLogger(MapReduceExecutionUtil
					.getLoggerNumber()), msg.substring(1));
		}
	}

	/**
	 * <p>
	 * This method adds header to the first log file for each of the tasks.
	 * </p>
	 * 
	 * @param context
	 *            Context
	 * @param className
	 *            calling class
	 */
	@SuppressWarnings(RAW_TYPES)
	public static void addLogHeader(TaskInputOutputContext context,
			String className) {
		getLogMsg(className, context.getJobName(), INFO, context.getJobID(),
				context.getTaskAttemptID());
	}

	/**
	 * <p>
	 * This method adds header to the first log file for each of the tasks.
	 * </p>
	 * 
	 * @param context
	 *            Context
	 * @param className
	 *            calling class
	 */
	public static void addLogHeader(JobConf context, String className) {
		getLogMsg(className, context.get("mapred.job.name"), INFO,
				context.get("mapred.job.id"), context.get("mapred.task.id"));
	}

	/**
	 * <p>
	 * Logs class names in the mapper/reducer chain to a log file
	 * </p>
	 * 
	 * @param classNames
	 *            class names
	 */
	public static void addChainLoggerInfo(String... classNames) {
		if (LoggerUtil.getChainLoggger() != null) {
			for (String str : classNames) {
				logMessage(LoggerUtil.getChainLoggger(), str);
			}
		}
	}

	/**
	 * This method will be if user wants to log return statement of every method
	 * 
	 * @param className
	 *            - Class which is calling this method
	 * @param methodName
	 *            - Class Method which is calling this method
	 * @param logMethod
	 *            - information should be logged as info, debug, verbose, etc
	 * @param logMsg
	 *            - Message to be logged
	 */
	public static void addLogMsg(Object msg1, Object msg2, Object msg3) {
		getLogMsg(msg1, msg2, msg3);
	}

	/**
	 * This method will add the log msg.
	 *
	 * @param msg1 the msg1
	 * @param msg2 the msg2
	 * @param msg3 the msg3
	 * @param msg4 the msg4
	 */
	public static void addLogMsg(Object msg1, Object msg2, Object msg3,
			Object msg4) {
		getLogMsg(msg1, msg2, msg3, msg4);
	}

	/**
	 *  This method will add the log msg.
	 *
	 * @param msg1 the msg1
	 * @param msg2 the msg2
	 * @param msg3 the msg3
	 * @param msg4 the msg4
	 * @param msg5 the msg5
	 */
	public static void addLogMsg(Object msg1, Object msg2, Object msg3,
			Object msg4, Object msg5) {
		getLogMsg(msg1, msg2, msg3, msg4, msg5);
	}
}
