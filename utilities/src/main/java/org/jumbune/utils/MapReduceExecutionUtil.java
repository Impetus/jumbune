package org.jumbune.utils;

import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.exception.ErrorCodesAndMessages;


/**
 * This class sets and removes various variables in ThreadLocal so they can be
 * used within an instance for various purpose of logging.
 * 
 */
public final class MapReduceExecutionUtil {
	/** The logger */
	private static final Logger LOGGER = LogManager
			.getLogger(MapReduceExecutionUtil.class);

	private static final String RAW_TYPES = "rawtypes";
	
	/**
	 * Instantiates a new map reduce execution util.
	 */
	private MapReduceExecutionUtil(){
		
	}
	/**
	 * This field decides if jumbune logging is enabled or not
	 */
	private static final ThreadLocal<Boolean> IS_JUMBUNE_LOG_THREAD_LOCAL = new ThreadLocal<Boolean>() {
		/**
		 This method returns the intial value
		  */
		protected Boolean initialValue() {
			return false;
		}
	};

	/**
	 * This field tells the number of reducers associated with current job
	 */
	private static ThreadLocal<Integer> numReduceTasksThreadLocal = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return -1;
		}
	};

	/**
	 * The partitioner which is associated with a particular job
	 */
	@SuppressWarnings(RAW_TYPES)
	private static final ThreadLocal<Partitioner> PARTITIONER_THREAD_LOCAL = new ThreadLocal<Partitioner>() {
		@Override
		protected Partitioner initialValue() {
			return null;
		}
	};

	/**
	 * The partitioner which is associated with a particular jobConf which is
	 * written in hadoop 0.1.x versions
	 */
	@SuppressWarnings(RAW_TYPES)
	private static final ThreadLocal<org.apache.hadoop.mapred.Partitioner> OLD_PARTITIONER_THREAD_LOCAL= new ThreadLocal<org.apache.hadoop.mapred.Partitioner>() {
		@Override
		protected org.apache.hadoop.mapred.Partitioner initialValue() {
			return null;
		}
	};

	/**
	 * Time taken by the partitioner which is associated with this job in
	 * partitioning only sample keys
	 */
	private static final ThreadLocal<Long> PARTITIONER_TOTAL_TIME_TAKEN_THREAD_LOCAL = new ThreadLocal<Long>() {
		@Override
		protected Long initialValue() {
			return 0l;
		}
	};

	/**
	 * Total number of samples for which this partitioner has calculated time in
	 * partitioning
	 */
	private static final ThreadLocal<Long> PARTITIONER_SAMPLES_COUNT_THREAD_LOCAL = new ThreadLocal<Long>() {
		@Override
		protected Long initialValue() {
			return 0l;
		}
	};

	/**
	 * This field tells the number of reducers associated with current job
	 */
	private static ThreadLocal<Integer> loggerNumberThreadLocal = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
		}
	};

	/**
	 * Add given time in partitionerTotalTimeTaken
	 * 
	 * @param time
	 *            - time in milliseconds that this partitioner has taken in
	 *            partitioning a particular key
	 */
	public static void incrementPartitioningTotalTime(long time) {
		long totalTime = PARTITIONER_TOTAL_TIME_TAKEN_THREAD_LOCAL.get() + time;
		PARTITIONER_TOTAL_TIME_TAKEN_THREAD_LOCAL.set(totalTime);
	}

	/**
	 * Get the total time taken by partitioner in partitioning sample keys
	 * 
	 * @return
	 */
	public static Long getPartitionerTotalTimeTaken() {
		return PARTITIONER_TOTAL_TIME_TAKEN_THREAD_LOCAL.get();
	}

	/**
	 * Ever time a sample key's partition time is calculated increment the count
	 * of sample by 1
	 */
	public static void incrementPartitioningSampleCount() {
		long totalSamplesTaken = PARTITIONER_SAMPLES_COUNT_THREAD_LOCAL.get() + 1;
		PARTITIONER_SAMPLES_COUNT_THREAD_LOCAL.set(totalSamplesTaken);
	}

	/**
	 * Return the total number of samples on which partitioning time is
	 * calculated
	 * 
	 * @return
	 */
	public static Long getPartitioningSampleCount() {
		return PARTITIONER_SAMPLES_COUNT_THREAD_LOCAL.get();
	}

	/**
	 * This method removes the partitionerSamplesCount and
	 * partitionerTotalTimeTaken from threadLocal
	 */
	public static void removePartitionerTimeAndSampleCount() {
		PARTITIONER_SAMPLES_COUNT_THREAD_LOCAL.remove();
		PARTITIONER_TOTAL_TIME_TAKEN_THREAD_LOCAL.remove();
	}

	/**
	 * This method returns the current partitioner that is set for a map
	 * 
	 * @return
	 */
	@SuppressWarnings(RAW_TYPES)
	public static Partitioner getPartitioner() {
		return PARTITIONER_THREAD_LOCAL.get();
	}

	/**
	 * Removes partitioner that is set in thread Local
	 */
	public static void removePartitioner() {
		PARTITIONER_THREAD_LOCAL.remove();
	}

	/**
	 * Removes oldPartitioner that is set in thread Local
	 */
	public static void removeOldPartitioner() {
		OLD_PARTITIONER_THREAD_LOCAL.remove();
	}

	/**
	 * This method will return the Partitioner object which is used for
	 * partitioning map output
	 * 
	 * @param context
	 *            - Map context
	 * @return Partitioner object
	 */
	@SuppressWarnings(RAW_TYPES)
	public static void setPartitioner(TaskInputOutputContext context) {
		Partitioner partitioner = null;
		Class paritionerClass = null;

		try {
			paritionerClass = context.getPartitionerClass();
			partitioner = (Partitioner) paritionerClass.newInstance();

			PARTITIONER_THREAD_LOCAL.set(partitioner);

		} catch (ClassNotFoundException e) {
			LOGGER.error(ErrorCodesAndMessages.MESSAGE_PARTITIONER_NOT_SET
					+ "  " + e);
		} catch (InstantiationException e) {
			LOGGER.error(ErrorCodesAndMessages.MESSAGE_PARTITIONER_NOT_SET
					+ "  " + e);
		} catch (IllegalAccessException e) {
			LOGGER.error(ErrorCodesAndMessages.MESSAGE_PARTITIONER_NOT_SET
					+ "  " + e);
		}
	}

	/**
	 * This method will return the Partitioner object which is used for
	 * partitioning map output
	 * 
	 * @param context
	 *            - Map context
	 * @return Partitioner object
	 */
	@SuppressWarnings(RAW_TYPES)
	public static void setPartitioner(JobConf conf) {
		org.apache.hadoop.mapred.Partitioner oldPartitioner = null;
		Class paritionerClass = null;

		try {
			paritionerClass = conf.getPartitionerClass();
			oldPartitioner = (org.apache.hadoop.mapred.Partitioner) paritionerClass
					.newInstance();

			OLD_PARTITIONER_THREAD_LOCAL.set(oldPartitioner);

		} catch (InstantiationException e) {
			LOGGER.error(ErrorCodesAndMessages.MESSAGE_PARTITIONER_NOT_SET
					+ " : " + e);
		} catch (IllegalAccessException e) {
			LOGGER.error(ErrorCodesAndMessages.MESSAGE_PARTITIONER_NOT_SET
					+ " : " + e);
		}
	}

	/**
	 * This method calculates the time taken by Partitioner in calculating the
	 * partition for given key & value. It even handles which partitioner it
	 * should invoke either of mapreduce(new) or of mapred(old) package.
	 * 
	 * @param partitioner
	 * @param key
	 * @param value
	 * @return time taken in deciding to which partition does this key should go
	 */
	@SuppressWarnings("unchecked")
	public static void calculateParitioningTime(Writable key, Writable value) {
		if (numReduceTasksThreadLocal.get() != -1) {
			incrementPartitioningSampleCount();
			long startTime = System.currentTimeMillis();
			if (PARTITIONER_THREAD_LOCAL.get() != null) {
				// No need of the partition to which this key is sent. Just need
				// to
				// know how much time it took to calculate that partition
				getPartitioner().getPartition(key, value,
						numReduceTasksThreadLocal.get());
			} else if (OLD_PARTITIONER_THREAD_LOCAL.get() != null) {
				OLD_PARTITIONER_THREAD_LOCAL.get().getPartition(key, value,
						numReduceTasksThreadLocal.get());
			}
			long endTime = System.currentTimeMillis() - startTime;
			incrementPartitioningTotalTime(endTime);
		}
	}

	/**
	 * Get if jumbune logs are enabled or not
	 * 
	 * @return true if jumbune logging is enabled
	 */
	public static boolean isJumbuneLog() {
		return IS_JUMBUNE_LOG_THREAD_LOCAL.get();
	}

	/**
	 * Initialize jumbuneLog to tell LogUtil that now jumbune logging should
	 * begun as code entered in Map/reduce programs
	 */
	public static void initializeJumbuneLog() {
		if (!IS_JUMBUNE_LOG_THREAD_LOCAL.get()) {
			IS_JUMBUNE_LOG_THREAD_LOCAL.set(true);
		}
	}

	/**
	 * Remove JumbuneLog boolean to signify that there is no need of further
	 * logging from jumbune
	 */
	public static void removeJumbuneLog() {
		IS_JUMBUNE_LOG_THREAD_LOCAL.remove();
	}

	/**
	 * Set number of reducers associated with current job
	 * 
	 * @param numreducetasks
	 */
	public static void setNumReducetasksthreadlocal(int numreducetasks) {
		numReduceTasksThreadLocal.set(numreducetasks);
	}

	/**
	 * Remove the number of reducer tasks set in threadLocal
	 */
	public static void removeNumReducetasks() {
		numReduceTasksThreadLocal.remove();
	}

	/**
	 * This api is used to  get the logger number.
	 *
	 * @return the logger number
	 */
	public static int getLoggerNumber() {
		return loggerNumberThreadLocal.get();
	}

	/**
	 *  This api is used to  set the logger number.
	 *
	 * @param value the new logger number
	 */
	public static void setLoggerNumber(int value) {
		loggerNumberThreadLocal.set(value);
	}

	/**
	 *  This api is used to  remove the logger number.
	 */
	public static void removeLoggerNumbber() {
		loggerNumberThreadLocal.remove();
	}

	/**
	 * <p>
	 * Configures the logging for mapreduce (new api)
	 * </p>
	 * 
	 * @param logFileDir
	 *            Directory at slave node where log files will be created
	 * @param context
	 *            Context
	 * @param isMapper
	 *            true if mapper
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void configureLogging(String logFileDir,
			TaskInputOutputContext context, boolean isMapper)
			throws IOException {
		// combiner logs not required. They were logged in mapper log files.
		if (isMapper
				|| (!isMapper && !context.getConfiguration().getBoolean(
						"mapred.task.is.map", true))) {
			initializeJumbuneLog();
			try {
				LoggerUtil.loadLogger(logFileDir, context.getTaskAttemptID()
						.toString());
			} catch (Exception e) {
				LOGGER.debug(
						"Error ocurred while loading logger while running instrumented jar",
						e);
			}
		}
	}

	/**
	 * <p>
	 * Configures the logging for mapred (old api)
	 * </p>
	 * 
	 * @param logFileDir
	 *            Directory at slave node where log files will be created
	 * @param conf
	 *            Job conf
	 * @param isMapper
	 *            true if mapper
	 * @param loadLogger
	 *            whether logger are to be loaded
	 * @param loggerNumber
	 *            logged number
	 * @throws IOException
	 */
	public static void configureLogging(String logFileDir, JobConf conf,
			boolean isMapper, boolean loadLogger, int loggerNumber)
			throws IOException {
		// combiner logs not required. They were logged in mapper log files.
		if (isMapper
				|| (!isMapper && !conf.getBoolean("mapred.task.is.map", true)) && (LoggerUtil.getMapReduceLoggers() == null)) {
				initializeJumbuneLog();
				int loggerKount = conf.getInt("jLoggerKount", 1);
				try {

					if (loggerKount > 1) {
						LoggerUtil.loadChainLogger(logFileDir,
								conf.get("mapred.task.id"), loggerKount);
					} else {
						LoggerUtil.loadLogger(logFileDir,
								conf.get("mapred.task.id"));
					}
				} catch (Exception e) {
					LOGGER.debug(
							"Error ocurred while loading logger while running instrumented jar",
							e);
				}
			
		}
		setLoggerNumber(loggerNumber);
	}

	/**
	 * <p>
	 * Stops jumbune logging for mapred (old) api
	 * </p>
	 * 
	 * @param loadLogger
	 *            whether loggers are loaded or not
	 * @throws IOException
	 */
	public static void stopJumbuneLogging(boolean loadLogger)
			throws IOException {
		// for chained tasks, only the first mapper/reducer is required to close
		// the logging.
		if (loadLogger) {
			removeJumbuneLog();
		}
	}

	/**
	 * <p>
	 * Stops jumbune logging for mapreduce (new) api
	 * </p>
	 * 
	 * @throws IOException
	 */
	public static void stopJumbuneLogging() throws IOException {
		removeJumbuneLog();
	}

	/**
	 * This method adds the chain info.
	 *
	 * @param conf the conf
	 */
	public static void addChainInfo(JobConf conf) {
		String chainedClasses = conf.get("jChainedClasses");
		if (chainedClasses != null) {
			String[] classes = chainedClasses.split(",");
			LogUtil.addChainLoggerInfo(classes);
		}
	}
}
