package org.jumbune.execution.utils;

import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_COMBINE_INPUT_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_COMBINE_OUTPUT_RECORDES;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_DATA_LOCAL_MAP_TASKS;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_FILE_BYTES_READ;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_FILE_BYTES_WRITTEN;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_HDFS_BYTES_READ;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_HDFS_BYTES_WRITTEN;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_LAUNCHED_MAP_TASKS;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_LAUNCHED_REDUCE_TASKS;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_MAP_INPUT_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_MAP_OUTPUT_BYTES;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_MAP_OUTPUT_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_REDUCE_INPUT_GROUPS;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_REDUCE_INPUT_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_REDUCE_OUTPUT_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_REDUCE_SHUFFLE_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.CAMELCASE_SPILLED_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.COMBINE_INPUT_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.COMBINE_OUTPUT_RECORDES;
import static org.jumbune.execution.utils.ExecutionConstants.COMMA;
import static org.jumbune.execution.utils.ExecutionConstants.DATA_LOCAL_MAP_TASKS;
import static org.jumbune.execution.utils.ExecutionConstants.FILE_BYTES_READ;
import static org.jumbune.execution.utils.ExecutionConstants.FILE_BYTES_WRITTEN;
import static org.jumbune.execution.utils.ExecutionConstants.HDFS_BYTES_READ;
import static org.jumbune.execution.utils.ExecutionConstants.HDFS_BYTES_WRITTEN;
import static org.jumbune.execution.utils.ExecutionConstants.LAUNCHED_MAP_TASKS;
import static org.jumbune.execution.utils.ExecutionConstants.LAUNCHED_REDUCE_TASKS;
import static org.jumbune.execution.utils.ExecutionConstants.MAP_INPUT_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.MAP_OUTPUT_BYTES;
import static org.jumbune.execution.utils.ExecutionConstants.MAP_OUTPUT_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_ENTER_INDEX;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_EXCLUDE_JOBS;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_VALID_INPUT;
import static org.jumbune.execution.utils.ExecutionConstants.REDUCE_INPUT_GROUPS;
import static org.jumbune.execution.utils.ExecutionConstants.REDUCE_INPUT_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.REDUCE_OUTPUT_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.REDUCE_SHUFFLE_RECORDS;
import static org.jumbune.execution.utils.ExecutionConstants.SPILLED_RECORDS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.utils.ConsoleLogUtil;
import org.jumbune.common.utils.MessageLoader;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.execution.beans.YesNo;
import org.jumbune.utils.beans.LogLevel;



/**
 * This class has utility methods which are specifically used by Execution module.
 * 
 */
public final class ExecutionUtil {
	public static final Logger LOGGER = LogManager.getLogger("EventLogger");
	private static Map<String, String> counterMaps;

	static {
		counterMaps = new HashMap<String, String>();
		counterMaps.put(LAUNCHED_REDUCE_TASKS, CAMELCASE_LAUNCHED_REDUCE_TASKS);
		counterMaps.put(LAUNCHED_MAP_TASKS, CAMELCASE_LAUNCHED_MAP_TASKS);
		counterMaps.put(DATA_LOCAL_MAP_TASKS, CAMELCASE_DATA_LOCAL_MAP_TASKS);
		counterMaps.put(FILE_BYTES_READ, CAMELCASE_FILE_BYTES_READ);
		counterMaps.put(HDFS_BYTES_READ, CAMELCASE_HDFS_BYTES_READ);
		counterMaps.put(FILE_BYTES_WRITTEN, CAMELCASE_FILE_BYTES_WRITTEN);
		counterMaps.put(HDFS_BYTES_WRITTEN, CAMELCASE_HDFS_BYTES_WRITTEN);
		counterMaps.put(REDUCE_INPUT_GROUPS, CAMELCASE_REDUCE_INPUT_GROUPS);
		counterMaps.put(COMBINE_OUTPUT_RECORDES, CAMELCASE_COMBINE_OUTPUT_RECORDES);
		counterMaps.put(MAP_INPUT_RECORDS, CAMELCASE_MAP_INPUT_RECORDS);
		counterMaps.put(REDUCE_SHUFFLE_RECORDS, CAMELCASE_REDUCE_SHUFFLE_RECORDS);
		counterMaps.put(REDUCE_OUTPUT_RECORDS, CAMELCASE_REDUCE_OUTPUT_RECORDS);
		counterMaps.put(SPILLED_RECORDS, CAMELCASE_SPILLED_RECORDS);
		counterMaps.put(MAP_OUTPUT_BYTES, CAMELCASE_MAP_OUTPUT_BYTES);
		counterMaps.put(COMBINE_INPUT_RECORDS, CAMELCASE_COMBINE_INPUT_RECORDS);
		counterMaps.put(MAP_OUTPUT_RECORDS, CAMELCASE_MAP_OUTPUT_RECORDS);
		counterMaps.put(REDUCE_INPUT_RECORDS, CAMELCASE_REDUCE_INPUT_RECORDS);

	}
	
	/**
	 * private constructor for ExecutionUtil
	 */
	private ExecutionUtil(){
		
	}

	/**
	 * Method shows a question on console and then expects an answer for it. No validation is performed on user's answer
	 * 
	 * @param reader
	 * @param validMessage
	 * @param question
	 * @return
	 
	 */
	public static String readInputFromConsole(Scanner scanner, String validMessage, String question) {
		askQuestion(question);
		String input = readFromReader(scanner);
		while (CollectionUtil.isNullOrEmpty(input)) {
			LOGGER.info(validMessage);
			input = readFromReader(scanner);
		}
		return input;
	}

	/**
	 * This method will return true if user enters Yes and will return false if user enters No or simply presses Enter key i.e. leaves blank answer
	 * 
	 * @param reader
	 * @param validMessage
	 * @param question
	 *            TODO
	 * @return
	 
	 */
	public static boolean askYesNoInfo(Scanner scanner, String validMessage, String question) {
		YesNo yesNo;
		askQuestion(question);
		while (true) {
			try {
				String input;

				input = readFromReader(scanner);

				if (input.length() == 0) {
					return false;
				}
				yesNo = YesNo.valueOf(input.toUpperCase());
				break;
			} catch (IllegalArgumentException ilEx) {
				LOGGER.error(validMessage);
			}
		}

		if (YesNo.YES.equals(yesNo) || YesNo.Y.equals(yesNo)) {
			return true;
		}
		return false;
	}

	/**
	 * This method shows a question to user and expects an answer for the same. The valid answers for the question can only be the once specified in
	 * enum LogLevel.
	 * 
	 * @param reader
	 * @param validMessage
	 * @param question
	 * @return
	 
	 */
	public static LogLevel askLogLevelInfo(Scanner scanner, String validMessage, String question) {
		LogLevel logLevel;
		askQuestion(question);
		while (true) {
			try {
				String input;
				input = readFromReader(scanner);
				logLevel = LogLevel.valueOf(input.toUpperCase());
				break;
			} catch (IllegalArgumentException ilEx) {
				LOGGER.error(validMessage);
				throw ilEx;
			}
		}
		return logLevel;
	}

	/**
	 * This method shows a question given in parameter on console.
	 * 
	 * @param question
	 */
	private static void askQuestion(String question) {
		ConsoleLogUtil.CONSOLELOGGER.info(question);
	}

	/**
	 * This method is used to read input from buffered reader.
	 * 
	 * @param reader
	 * @return String
	 
	 */
	private static String readFromReader(Scanner scanner) {
		String input;
		input = scanner.nextLine().trim();
		return input;
	}

	/**
	 * This is used to convert Hadoop standard counter in Camel case.
	 * 
	 * @param String
	 * @return String
	 */
	public static String convertInCamelCase(String counter) {
		String counterTmp = counter;
		if (counterMaps.containsKey(counterTmp)) {
			counterTmp = counterMaps.get(counterTmp);
		}

		return counterTmp;

	}

	/**
	 * This method is util method to show all jobs declared in yaml.
	 * 
	 * @param loader
	 * @param scanner
	 * @param validMessage
	 * @param question
	 */
	public static void showDefinedJobs(List<JobDefinition> jobDefList) {
		Scanner scanner = new Scanner(System.in);
		MessageLoader msgLoader = MessageLoader.getInstance();

		int srNo = 0;
		if (jobDefList.size() > 1) {
			for (JobDefinition jobdef : jobDefList) {
				srNo++;
				LOGGER.info("Sr. No. : " + srNo + " Job Name : " + jobdef.getName() + " Job Class : " + jobdef.getJobClass());
			}
			if (askYesNoInfo(scanner, msgLoader.get(MESSAGE_VALID_INPUT), msgLoader.get(MESSAGE_EXCLUDE_JOBS))) {
				LOGGER.info(msgLoader.get(MESSAGE_ENTER_INDEX));
				String input = readFromReader(scanner);
				String[] index = input.split(COMMA);
				excludeJobs(jobDefList, index);
			}
		}
	}

	/**
	 * This method is used to exclude the given jobs from JobDefinition list.
	 * 
	 * @param loader
	 * @param index
	 */
	public static void excludeJobs(Config config, String[] index) {
		JobConfig jobConfig = (JobConfig)config;
		List<JobDefinition> jobList = jobConfig.getJobs();
		excludeJobs(jobList, index);
	}

	/**
	 * This method is used to exclude the given jobs from JobDefinition list.
	 * 
	 * @param jobList
	 * @param index
	 */
	public static void excludeJobs(List<JobDefinition> jobList, String[] index) {
		for (String str : index) {
			for (Iterator<JobDefinition> iterater = jobList.iterator(); iterater.hasNext();) {
				if (iterater.next().getName().equalsIgnoreCase(str)){
					iterater.remove();
				}
			}
		}
	}


}
