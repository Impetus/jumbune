package org.jumbune.common.utils;

import com.google.gson.Gson;

/**
 * The Interface Constants.
 */
public interface Constants {

	Gson gson = new Gson();
	String AGENT_ENV_VAR_NAME = "AGENT_HOME";
	String ANALYSE_CLUSTER_PROPERTIES_FILE = "/resources/analyseClusterStats.properties";
	String ANALYZE_DATA = "analyzeData";
	String ANALYZE_JOB = "analyzeJob";
	String APACHE = "a";
	String APPLICATION = "application";
	String AT_OP = "@";
	String BIN_HDFS = "/bin/hdfs";
	String CHMOD_CMD = "chmod -R a+w ";
	String CLOUDERA = "c";
	String CLUSTER_INFO = "clusterInfo.properties";
	String COLON = ":";
	String COMMA = ",";
	String CONFIGURATION = "conf";
	String CONSOLIDATED_DV_LOC = "dv/";
	String CONSOLIDATED_JSON_DV_LOC = "jdv/";
	String CONSOLIDATED_LOG_LOC = "logs/consolidated/";
	String CONSOLIDATED_XML_DV_LOC = "xdv/";
	String COPY_COMMAND = "cp ";
	String CORE_SITE_XML = "core-site.xml";
	String CPU_DUMP_FILE = "cpu";
	String DATA_QUALITY_TIMELINE = "DATA_QUALITY_TIMELINE";
	String DATA_VALIDATION = "DATA_VALIDATION";
	String DEBUG_ANALYZER = "DEBUG_ANALYZER";
	String DFS_LSR = " dfs -ls -R ";
	String DOT = ".";
	String DOTJUMBUNE = "/.jumbune/";
	String DRF = "DRF";
	String EB = "EB";
	String ECHO_AGENT_HOME = "echo $AGENT_HOME \n \n";
	String EMPTY_STRING = "";
	String EMRAPACHE = "ea";
	String EMRMAPR = "em";
	String ERROR_ = "ERROR";
	String FAIR_SCHEDULER = "fairScheduler";
	String FORWARD_SLASH = "/";
	String GB = "GB";
	String HADOOP_COMMAND = "bin/hadoop";
	String HADOOP_COMMAND_TYPE = "jar";
	String HADOOP_DISTRIBUTION = "HadoopDistribution";
	String HADOOP_ENV_VAR_NAME = "HADOOP_HOME";
	String HADOOP_HOME = "HADOOP_HOME";
	String HDFS_SITE_XML = "hdfs-site.xml";
	String HOME = "/home/";
	String HORTONWORKS = "h";
	String INSTRUMENTED_FILE_SUFFIX = "_i";
	String INSTRUMENTED_JAR_COUNTER = "INSTRUMENTED_JAR_COUNTER";
	String INSTRUMENTED_JAR_LOC = "jar/instrument/";
	String JAR = ".jar";
	String JOB = "job";
	String JOB_JARS_LOC = "jobJars/";
	String JOB_STATUS = "JOB_STATUS";
	String JUMBUNE_ENV_VAR_NAME = "JUMBUNE_HOME";
	String LIB_DIRECTORY = "/lib/";
	String LIB_JARS = "-libjars";
	String LOG4J2_API_JAR = "/lib/log4j-api-2.8.2.jar";
	String LOG4J2_CORE_JAR = "/lib/log4j-core-2.8.2.jar";
	String LOG_LEVEL_INFO = "INFO";
	String LOG_LEVEL_TRUE = "TRUE";
	String LOG_PROCESSOR_ERROR = "{\"ErrorAndException\": {\"Error occured during Debug Analysis\" : \" Error occured during Debug Analysis \"}}";
	String MAP = "MAP";
	String MAPR = "m";
	String MAP_INPUT_RECORD = "Map input records";
	String MB = "MB";
	String MEM_DUMP_FILE = "mem";
	String MESSAGE_FILE = "executionMessage.en";
	String MKDIR_P_CMD = "mkdir -p ";
	String MR_RESOURCES = "/resources";
	String M_SUMMARY_FILE = "ConsolidatedSummary";
	String NAME_NODE_INFO_DEAD_NODES = "NameNodeInfo.DeadNodes";
	String NAME_NODE_INFO_LIVE_NODES = "NameNodeInfo.LiveNodes";
	String NEW_LINE = "\n";
	String NON_YARN = "Non-Yarn";
	String NUM_OF_FIELDS_CHECK = "Number\\ of\\ Fields";
	String ON = "on";
	String OS_IDENTIFIER = " OS";
	String PB = "PB";
	String PIPE_SEPARATOR = "|";
	String PROFILED_FILE_SUFFIX = "_p";
	String PROFILED_JAR_LOC = "jar/profile/";
	String PROFILING_FILE_LOC = "profiling/";
	String PROFILING_PROPERTY_FILE = "jumbune-profiling.properties";
	String PROFILING_SYSTEM_JSON = "system.stats.json";
	String PURE_JAR_COUNTER = "PURE_JAR_COUNTER";
	String PURE_PROFILING = "PURE_PROFILING";
	String REDUCE_OUTPUT_RECORD = "Reduce output records";
	String SLAVE_DV_LOC = "dv/*";
	String SLAVE_JSON_DV_LOC = "jdv/*";
	String SLAVE_LOG_LOC = "logs/*.log*";
	String SLAVE_XML_DV_LOC = "xdv/*";
	String SORT_COMMAND = "ls -t -1";
	String SPACE = " ";
	String SPACE_REGEX = "\\s+";
	String SPACE_SEPARATOR = "SPACE_SEPARATOR";
	String STATUS = "STATUS";
	String SUCCESS = "SUCCESS";
	String SUMMARY_FILE_LOC = "logs/summary/";
	String TB = "TB";
	String UNDERSCORE = "_";
	String USED_SPACE = "usedSpace";
	String USER_DEFINED_DATA_TYPE = "Data\\ Type";
	String USER_DEFINED_NULL_CHECK = "Null\\ Check";
	String USER_DEFINED_REGEX_CHECK = "Regex";
	String USER_LIB_LOC = "userLib/";
	String USER_YAML_LOC = "/yaml";
	String XFERADDR = "xferaddr";
	String YARN = "Yarn";
	byte ZERO_CROSS_ZERO_A = 0x0a;
	int FIVE = 5;
	int FIVE_HUNDRED = 500;
	int FIVE_THOUNSAND = 5000;
	int FOUR = 4;
	int HUNDRED = 100;
	int ONE_ZERO_TWO_FOUR = 1024;
	int PROFILING_MAX_CPU_SAMPLE_COUNT = 10;
	int PROFILING_MAX_HEAP_SAMPLE_COUNT = 10;
	int TEN_THOUSAND = 10000;
	int THOUSAND = 1000;
	int THREE = 3;
	int TWENTY_TWO = 22;
	long TENL = 10L;

	/**
	 * This field tells till what level the packages should be filtered for showing
	 * results in static profiling.
	 */
	int PROFILING_PACKAGE_FILTERING_LEVEL = 2;

}
