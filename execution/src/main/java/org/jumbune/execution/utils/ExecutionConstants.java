/**
 * 
 */
package org.jumbune.execution.utils;

/**
 * This interface holds the mapping for message mentioned in executionMessage.en
 * 
 */
public interface ExecutionConstants {
	int MESSAGE_INFO_CHANGE_QUESTION = 1004;
	int MESSAGE_LOG_LEVEL = 1005;
	int MESSAGE_ALLOWED_LOG_LEVEL = 1009;
	int MESSAGE_EXECUTION_YAML_COMMAND = 1008;
	int MESSAGE_COULD_NOT_EXECUTE_JOB = 1014;
	int MESSAGE_JOB_CLASS_NAME = 1016;
	int MESSAGE_JOB_PARAMETERS = 1017;
	int MESSAGE_MORE_JOBS = 1018;
	int MESSAGE_VALID_INPUT = 1019;
	int MESSAGE_JOB_NAME = 1020;
	int MESSAGE_EXCLUDE_JOBS = 1030;
	int MESSAGE_ENTER_INDEX = 1040;

	String COMMA = ",";
	String TEMP_DIR = "tmp";
	String TOKEN_FILE = "/jumbuneState.txt";
	String ERRORANDEXCEPTION = "ErrorAndException";

	String LAUNCHED_REDUCE_TASKS = "Launched reduce tasks";
	String LAUNCHED_MAP_TASKS = "Launched map tasks";
	String DATA_LOCAL_MAP_TASKS = "Data-local map tasks";
	String FILE_BYTES_READ = "FILE_BYTES_READ";
	String HDFS_BYTES_READ = "HDFS_BYTES_READ";
	String FILE_BYTES_WRITTEN = "FILE_BYTES_WRITTEN";
	String HDFS_BYTES_WRITTEN = "HDFS_BYTES_WRITTEN";
	String REDUCE_INPUT_GROUPS = "Reduce input groups";
	String COMBINE_OUTPUT_RECORDES = "Combine output records";
	String MAP_INPUT_RECORDS = "Map input records";
	String REDUCE_SHUFFLE_RECORDS = "Reduce shuffle bytes";
	String REDUCE_OUTPUT_RECORDS = "Reduce output records";
	String SPILLED_RECORDS = "Spilled Records";
	String MAP_OUTPUT_BYTES = "Map output bytes";
	String COMBINE_INPUT_RECORDS = "Combine input records";
	String MAP_OUTPUT_RECORDS = "Map output records";
	String REDUCE_INPUT_RECORDS = "Reduce input records";

	String CAMELCASE_LAUNCHED_REDUCE_TASKS = "Launched Reduce Tasks";
	String CAMELCASE_LAUNCHED_MAP_TASKS = "Launched Map Tasks";
	String CAMELCASE_DATA_LOCAL_MAP_TASKS = "Data Local Map Tasks";
	String CAMELCASE_FILE_BYTES_READ = "File Bytes Read";
	String CAMELCASE_HDFS_BYTES_READ = "HDFS Bytes Read";
	String CAMELCASE_FILE_BYTES_WRITTEN = "File Bytes Written";
	String CAMELCASE_HDFS_BYTES_WRITTEN = "HDFS Bytes Written";
	String CAMELCASE_REDUCE_INPUT_GROUPS = "Reduce Input Groups";
	String CAMELCASE_COMBINE_OUTPUT_RECORDES = "Combine Output Records";
	String CAMELCASE_MAP_INPUT_RECORDS = "Map Input Records";
	String CAMELCASE_REDUCE_SHUFFLE_RECORDS = "Reduce Shuffle Bytes";
	String CAMELCASE_REDUCE_OUTPUT_RECORDS = "Reduce Output Records";
	String CAMELCASE_SPILLED_RECORDS = "Spilled Records";
	String CAMELCASE_MAP_OUTPUT_BYTES = "Map Output Bytes";
	String CAMELCASE_COMBINE_INPUT_RECORDS = "Combine Input Records";
	String CAMELCASE_MAP_OUTPUT_RECORDS = "Map Output Records";
	String CAMELCASE_REDUCE_INPUT_RECORDS = "Reduce Input Records";

	String RUNNING_JOB = "Running job: ";
	String COUNTERS = "Counters:";
	String MAPRED_JOBCLIENT = "mapred.JobClient:";
	int TEN = 10;
	int THREE = 3 ;

}
