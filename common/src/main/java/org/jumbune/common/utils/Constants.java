package org.jumbune.common.utils;

import org.jumbune.utils.Versioning;

import com.google.gson.Gson;

/**
 * The Interface Constants.
 */
public interface Constants {

	String JOB_RESULT_LOC = "/results/";
	/** The data validation. */
	String DATA_VALIDATION = "DATA_VALIDATION";
	
	/**  Data Quality Timeline module *. */
	String DATA_QUALITY_TIMELINE = "DATA_QUALITY_TIMELINE";

	/** The h command. */
	String HADOOP_COMMAND = "bin/hadoop";
	
	/** The h command type. */
	String HADOOP_COMMAND_TYPE = "jar";
	
	/** The h profile param. */
	String H_PROFILE_PARAM = "-agentlib:hprof=format=b,force=n,thread=y,verbose=n,file=%s";
	
	/** The m summary file. */
	String M_SUMMARY_FILE = "ConsolidatedSummary";
	
	/** The m pure counter file. */
	String M_PURE_COUNTER_FILE = "PureJarCounters";
	
	/** The m prof counter file. */
	String M_PROF_COUNTER_FILE = "PureJarProfilingCounters";
	
	/** The m inst counter file. */
	String M_INST_COUNTER_FILE = "InstrumentedJarCounters";
	
	/** The m service yaml file. */
	String M_SERVICE_YAML_FILE = "services.yaml";
	
	/** The profiled file suffix. */
	String PROFILED_FILE_SUFFIX = "_p";
	
	/** The instrumented file suffix. */
	String INSTRUMENTED_FILE_SUFFIX = "_i";

	/** The jmx file loc. */
	String JMX_FILE_LOC = "logs/jmxfiles/";
	
	/** The consolidated log loc. */
	String CONSOLIDATED_LOG_LOC = "logs/consolidated/";
	
	/** The profiling file loc. */
	String PROFILING_FILE_LOC = "profiling/";
	
	/** The summary file loc. */
	String SUMMARY_FILE_LOC = "logs/summary/";
	
	/** The jumbune lib loc. */
	String JUMBUNE_LIB_LOC = "lib/";
	
	/** The user lib loc. */
	String USER_LIB_LOC = "userLib/";
	
	/** The profiled jar loc. */
	String PROFILED_JAR_LOC = "jar/profile/";
	
	/** The instrumented jar loc. */
	String INSTRUMENTED_JAR_LOC = "jar/instrument/";
	
	/** The pure jar loc. */
	String PURE_JAR_LOC = "jar/";
	
	/** The slave log loc. */
	String SLAVE_LOG_LOC = "logs/*.log*";
	
	/** The service yaml loc. */
	String SERVICE_YAML_LOC = "resources/";
	
	/** The job jars loc. */
	String JOB_JARS_LOC = "jobJars/";
	
	/** The LO g4 j2_ ap i_ jar. */
	String LOG4J2_API_JAR = "/lib/log4j-api-2.1.jar";
	
	/** The LO g4 j2_ cor e_ jar. */
	String LOG4J2_CORE_JAR = "/lib/log4j-core-2.1.jar";
	
	/**  The apache xbean jar. */
	String XBEAN__JAR = "/lib/xbean-2.2.0.jar";
	
	String XSOM__JAR = "/lib/xsom-20140925.jar";
	
	String RELAXNG__JAR = "/lib/relaxngDatatype-1.0.jar";
	
	/** The echo agent home. */
	String ECHO_AGENT_HOME = "echo $AGENT_HOME \n \n";
	
	/** The agent env var name. */
	String AGENT_ENV_VAR_NAME = "AGENT_HOME";
	
	/** The jumbune env var name. */
	String JUMBUNE_ENV_VAR_NAME = "JUMBUNE_HOME";
	
	/** The hadoop env var name. */
	String HADOOP_ENV_VAR_NAME = "HADOOP_HOME";
	
	/** The default user configuration. */
	String DEFAULT_USER_CONFIGURATION = "Jumbune-UserProperties.yaml";
	
	/** The message file. */
	String MESSAGE_FILE = "executionMessage.en";
	
	/** The user yaml loc. */
	String USER_YAML_LOC = "/yaml";
	
	/** The user json loc. */
    String USER_JSON_LOC = "/json";
    
	/** The jar. */
	String JAR = ".jar";

	/** The log level true. */
	String LOG_LEVEL_TRUE = "TRUE";
	
	/** The log level info. */
	String LOG_LEVEL_INFO = "INFO";

	/** The dv jar path. */
	String DV_JAR_PATH = "lib/jumbune-datavalidation-"+Versioning.COMMUNITY_BUILD_VERSION+Versioning.COMMUNITY_DISTRIBUTION_NAME + JAR;
	
	/** The dv main class. */
	String DV_MAIN_CLASS = "org.jumbune.datavalidation.DataValidationJobExecutor";
	
	/** The dv main class. */
	String XML_DV_MAIN_CLASS = "org.jumbune.datavalidation.xml.XmlDataValidationJobExecutor";
	
	/** The json dv main class. */
	String JSON_DV_MAIN_CLASS = "org.jumbune.datavalidation.json.JsonDataValidationExecutor";
	
	/** The lib jars. */
	String LIB_JARS = "-libjars";
	
	/** The gson jar. */
	String GSON_JAR = "lib/gson-2.2.4"+ JAR;
	
	/** The common jar. */
	String COMMON_JAR = "lib/jumbune-common-"+Versioning.COMMUNITY_BUILD_VERSION+Versioning.COMMUNITY_DISTRIBUTION_NAME + JAR;
	
	/** The utilities jar. */
	String UTILITIES_JAR = "lib/jumbune-utils-"+Versioning.COMMUNITY_BUILD_VERSION+Versioning.COMMUNITY_DISTRIBUTION_NAME + JAR;
	
	/** The exception. */
	String EXCEPTION = "Exception";
	
	/** The error. */
	String ERROR = "Error";
	
	/** The log processor error. */
	String LOG_PROCESSOR_ERROR = "{\"ErrorAndException\": {\"Error occured during Debug Analysis\" : \" Error occured during Debug Analysis \"}}";

	/** The pipe separator. */
	String PIPE_SEPARATOR = "|";
	
	/** The dv details. */
	String DV_DETAILS = "dv";

	/** The consolidated dv loc. */
	String CONSOLIDATED_DV_LOC = "dv/";
	
	/** The slave dv loc. */
	String SLAVE_DV_LOC = "dv/*";
	
	/** The consolidated json dv loc. */
	String CONSOLIDATED_JSON_DV_LOC ="jdv/";
	
	/** The consolidated dv loc. */
	String CONSOLIDATED_XML_DV_LOC = "xdv/";
	
	/** The slave dv loc. */
	String SLAVE_XML_DV_LOC = "xdv/*";
	
	/** The slave json dv loc. */
	String SLAVE_JSON_DV_LOC ="jdv/*";

	/** The null check. */
	String NULL_CHECK = "Null_Check";
	
	/** The data type. */
	String DATA_TYPE = "Data_Type";
	
	/** The regex. */
	String REGEX = "Regex";
	
	/** The num of fields. */
	String NUM_OF_FIELDS = "Number_of_Fields";

	/** The profiling max heap sample count. */
	int PROFILING_MAX_HEAP_SAMPLE_COUNT = 10;
	
	/** The profiling max cpu sample count. */
	int PROFILING_MAX_CPU_SAMPLE_COUNT = 10;
	
	/** The zero. */
	int ZERO = 0;
	
	/** This field tells till what level the packages should be filtered for showing results in static profiling. */
	int PROFILING_PACKAGE_FILTERING_LEVEL = 2;
	
	/** * job profiling key in input validation. */
	String JOB_PROFILING = "Profiling";
	
	/** Data validate key in input validation. */
	String DATA_VALIDATE = "HDFS-Validation";
	
	/** job validation key in input validation. */
	String JOBS_VALIDATION = "M/R-Jobs";
	
	/** * Home validation key in input validation. */
	String BASIC_VALIDATION = "Basic";
	
	/** * master machine jar path option is 4. */
	int MASTER_MAC_PATH = 3;
	
	/** * min job size while input variable. */
	int MIN_JOB_SIZE = 1;
	
	/** * minimum partion sample level. */
	int MIN_PARTITION_SAMPLE_INTER = 0;
	
	/** * minimum stats interval. */
	int STATS_INTERVAL = 2000;
	
	/** * sudo <code>apt-get install meld min port range. */
	int PORT_MIN = 0;
	/***
	 * minimum no of slave .all slave values greater than this value
	 */
	int MIN_SLAVE_SIZE = 1;
	
	/** * representing minus one. */
	int MINUS_ONE = -1;
	
	/** * maximum number of option is allowed in dependent jar source. */
	int MAX_OPTION = 4;
	
	/** * represent forward slash. */
	String FORWARD_SLASH = "/";
	
	/** represent white space. */
	String WHITE_SPACE = " ";
	
	/** * represent empty string. */
	String EMPTY_STRING = "";
	
	/** debug validation key. */
	String DEBUGGER_VALIDATION = "Flow-Debugging";
	
	/** representing TRUE value. */
	String TRUE = "TRUE";
	
	/** key for instrument User Define Validation. */
	String DEBUG_INST_USER_KEY = "instrumentUserDefValidate";
	
	/** key for partioner sample in debug. */
	String DEBUG_PARTITION_KEY = "partitioner";
	
	/** key for instrument Regex validation while debugging. */
	String DEBUG_INSTR_REGEX_KEY = "instrumentRegex";
	
	/** key for map which contain all the failures list. */
	String FAILURE_KEY = "Failures";
	
	/** key for map which contain all the suggestion list. */
	String SUGGESSION_KEY = "Suggestions";
	
	/** message file which containing all the messages. */
	String INPUT_VALIDATION_MESSAGE_FILE = "inputyaml.error";
	
	/** * key for if block in debugger. */
	String DEBUG_IF_BLOCK = "ifblock";
	
	/** regular expression for map and reducers field. */
	String REGULAR_EXPRESSION = "\\d+-\\d+";
	
	/** max if block level in debug field. */
	int DEBUG_MAX_IF = 5;

	/** The default stats interval. */
	int DEFAULT_STATS_INTERVAL = 10000;
	
	/** The hdfs is not runing msg. */
	String HDFS_IS_NOT_RUNING_MSG = "Retrying connect to server";
	
	/** The hdfs is not up msg. */
	String HDFS_IS_NOT_UP_MSG = "Haddop is not up";
	
	/** The name. */
	String NAME = "name";
	
	/** The value. */
	String VALUE = "value";
	
	/** The property. */
	String PROPERTY = "property";
	
	/** The hadoop home. */
	String HADOOP_HOME = "HADOOP_HOME";
	
	/** The mapred file name. */
	String MAPRED_FILE_NAME = "mapred-site.xml";
	
	/** The core site xml. */
	String CORE_SITE_XML = "core-site.xml";
	
	/** The hdfs site xml. */
	String HDFS_SITE_XML = "hdfs-site.xml";
	
	/** The space. */
	String SPACE = " ";
	
	/** The dot. */
	String DOT = ".";
	
	/** The on. */
	String ON = "on";
	
	/** The lib directory. */
	String LIB_DIRECTORY = "/lib/";
	
	/** The copy command. */
	String COPY_COMMAND = "cp ";
	
	/** The jumbune report extention. */
	String JUMBUNE_REPORT_EXTENTION = ".xls";
	
	/** The dir separator. */
	String DIR_SEPARATOR = "/";
	
	/** The jumbune relative dv jar path. */
	String JUMBUNE_RELATIVE_DV_JAR_PATH = "modules/jumbune-datavalidation-"+Versioning.COMMUNITY_BUILD_VERSION+Versioning.COMMUNITY_DISTRIBUTION_NAME+".jar";
	
	/** The hash. */
	String HASH = "#";
	
	/** The comma. */
	String COMMA = ",";
	
	/** The underscore. */
	String UNDERSCORE = "_";
	
	/** The sqr bracket open. */
	String SQR_BRACKET_OPEN = "[";
	
	/** The sqr bracket closed. */
	String SQR_BRACKET_CLOSED = "]";
	
	/** The cpu dump file. */
	String CPU_DUMP_FILE = "cpu";
	
	/** The mem dump file. */
	String MEM_DUMP_FILE = "mem";
	
	/** The at op. */
	String AT_OP = "@";
	
	/** The colon. */
	String COLON = ":";
	
	/** The mr resources. */
	String MR_RESOURCES = "/resources";
	
	/** The sort command. */
	String SORT_COMMAND = "ls -t -1";
	
	/** The space separator. */
	String SPACE_SEPARATOR = "SPACE_SEPARATOR";
	
	/** String for ls command. */
	String LS_COMMAND = "ls";
	
	/** The Pure Jar Counter String. */
	String PURE_JAR_COUNTER = "PURE_JAR_COUNTER";
	
	/** The Pure Profiling String. */
	String PURE_PROFILING = "PURE_PROFILING";
	
	/** The Instrumented jar Counter String. */
	String INSTRUMENTED_JAR_COUNTER = "INSTRUMENTED_JAR_COUNTER";
	
	/** The Debug Analyzer String. */
	String DEBUG_ANALYZER = "DEBUG_ANALYZER";	
	
	/** The map input record. */
	String MAP_INPUT_RECORD = "Map input records";
	
	/** The reduce output record. */
	String REDUCE_OUTPUT_RECORD= "Reduce output records";
	
	/** The Constant ECHO_HADOOP_HOME. */
	String ECHO_HADOOP_HOME = "echo $HADOOP_HOME \n \n";

	/** The Constant SYSTEM_STATS_DIR. */
	String SYSTEM_STATS_DIR = "SystemStats";
	
	/** The Constant MKDIR_P_CMD. */
	String MKDIR_P_CMD = "mkdir -p ";

	/** The Constant CHMOD_CMD. */
	String CHMOD_CMD = "chmod -R a+w ";
	
	/** The configuration. */
	String CONFIGURATION = "conf";
	
	/** The cluster info. */
	String CLUSTER_INFO ="clusterInfo.properties";
	
	/** The hadoop type. */
	String HADOOP_TYPE = "HadoopType";
	
	/** The non yarn. */
	String NON_YARN= "Non-Yarn";
	
	/** The yarn. */
	String YARN ="Yarn";
	
	/** The hadoop distribution. */
	String HADOOP_DISTRIBUTION = "HadoopDistribution";
	
	/** The apache. */
	String APACHE = "a";
	
	/** The mapr. */
	String MAPR = "m";
	
	/** The cloudera. */
	String CLOUDERA = "c";
	
	/**  The hortonworks. */
	String HORTONWORKS = "h";
	
	/** The emrapache. */
	String EMRAPACHE = "ea";
	
	/** The emrmapr. */
	String EMRMAPR = "em" ;
 	
	/** The profiling property file. */
	String PROFILING_PROPERTY_FILE="jumbune-profiling.properties";
	
	/** The database properties file. */
	String DATABASE_PROPERTIES_FILE = "/conf/influxdb.properties";
	
	/** The database properties file. */
	String ANALYSE_CLUSTER_PROPERTIES_FILE = "/resources/analyseClusterStats.properties";
	
	/** The profiling system json. */
	String PROFILING_SYSTEM_JSON="system.stats.json";
	
	/*
	 * Following are Number literals used in different classes, to avoid usage of magic numbers
	 */
	
	/** The one zero two four. */
	int ONE_ZERO_TWO_FOUR = 1024;	
	
	/** The five hundred. */
	int FIVE_HUNDRED = 500;
	
	/** The twenty two. */
	int TWENTY_TWO= 22;
	 
	/** The thousand. */
	int THOUSAND = 1000;
	
	/** The three. */
	int THREE = 3;
	
	/** The four. */
	int FOUR = 4;
	
	/** The eight. */
	int EIGHT = 8;
	
	/** The hundred. */
	int HUNDRED = 100;
	
	/** The ten thousand. */
	int TEN_THOUSAND = 10000;
	
	/** The five thousand. */
	int FIVE_THOUNSAND = 5000;
		
 	/** The fifty. */
	 int FIFTY = 50;

	/** The five. */
	int FIVE = 5;
	
	/** The tenl. */
	long TENL = 10L;

	/** The zero cross zero a. */
	byte ZERO_CROSS_ZERO_A = 0x0a;
	
	/** The nine. */
	int NINE = 9;
	
	/** The four hundred fifty seven. */
	int FOUR_HUNDERED_FIFTY_SEVEN = 457;
	
	/** The four hundred sixty one. */
	int FOUR_HUNDERED_SIXTY_ONE = 461;
	
	/** The one three three. */
	int ONE_THREE_THREE = 133;	
	
	/** The new line. */
	String NEW_LINE = "\n" ;
	
	/** The scheduled jobs. */
	String SCHEDULED_JOBS = "/scheduledJobs/userScheduled/";
	
	/** The job input errors. */
	String JOB_INPUT_ERRORS = "jobInputErrors";

	/** The cluster errors. */
	String CLUSTER_ERRORS = "clusterErrors";
	
	/** The status. */
	String STATUS = "STATUS";
	
	/** The success. */
	String SUCCESS = "SUCCESS";
	
	/** The error. */
	String ERROR_ = "ERROR";
	
	/** The os identifier. */
	String OS_IDENTIFIER = " OS" ;
	
	/** The covert to gb. */
	Integer COVERT_TO_GB = 1073741824 ;
	
	String BIN_HDFS = "/bin/hdfs";
	
	String DFS_LSR = " dfs -ls -R ";
	
	String SPACE_REGEX = "\\s+";
	
	/** The num of fields check. */
	String NUM_OF_FIELDS_CHECK = "Number\\ of\\ Fields";
	
	/** User defined null check */
	String USER_DEFINED_NULL_CHECK = "Null\\ Check";
	
	/** User defined Data type check */
	String USER_DEFINED_DATA_TYPE = "Data\\ Type";
	
	/** User defined regular expression check */
	String USER_DEFINED_REGEX_CHECK = "Regex";
	
	String UNIT = "unit";

	String IPC_CLIENT_FALLBACK = "ipc.client.fallback-to-simple-auth-allowed";
	
	String HIVE_ACCESS_SUBJECT_NAME = "hive.access.subject.name" ;
	String NAME_NODE_INFO_LIVE_NODES = "NameNodeInfo.LiveNodes";
	String NAME_NODE_INFO_DEAD_NODES = "NameNodeInfo.DeadNodes";
	
	String USED_SPACE = "usedSpace";
	
	String XFERADDR = "xferaddr";
	
	String SPARK = "SPARK";
	
	String APPLICATION = "application";
	
	String JOB = "job";
	
	String TB = "TB";
	
	String GB = "GB";
	
	String KB = "KB";
	
	String EB = "EB";
	
	String PB = "PB";
	
	String MB = "MB";
	
	String MAP = "MAP";
	
	String AESTRIC = "*";
	
	String HOME = "/home/" ;
	
	String DOTJUMBUNE = "/.jumbune/";

	String TMP = "/tmp/";
	
	String CAPACITY_SCHEDULER_LEAF_QUEUE_INFO = "capacitySchedulerLeafQueueInfo";
	
	String FAIR_SCHEDULER = "fairScheduler";
	
	String DRF = "DRF";
	
	/** The analyse data. */
	String ANALYZE_DATA = "analyzeData";
	
	/** The analyze job. */
	String ANALYZE_JOB = "analyzeJob";
		
	String JOB_STATUS = "JOB_STATUS";
	
	Gson gson = new Gson();
	
}
