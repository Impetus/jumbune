package org.jumbune.common.utils;



/**
 * The Interface Constants.
 */
public interface Constants {

	/** The data validation. */
	String DATA_VALIDATION = "DATA_VALIDATION";

	/** The h command. */
	String H_COMMAND = "bin/hadoop";
	
	/** The h command type. */
	String H_COMMAND_TYPE = "jar";
	
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
	String LOG4J2_API_JAR = "/lib/log4j-api-2.0.jar";
	
	/** The LO g4 j2_ cor e_ jar. */
	String LOG4J2_CORE_JAR = "/lib/log4j-core-2.0.jar";
	
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
	
	/** The user yaml loc. */
    String USER_JSON_LOC = "/json";
    
	/** The jar. */
	String JAR = ".jar";

	/** The log level true. */
	String LOG_LEVEL_TRUE = "TRUE";
	
	/** The log level info. */
	String LOG_LEVEL_INFO = "INFO";

	/** The dv jar path. */
	String DV_JAR_PATH = "lib/jumbune-datavalidation-"+Versioning.BUILD_VERSION+Versioning.DISTRIBUTION_NAME + JAR;
	
	/** The dv main class. */
	String DV_MAIN_CLASS = "org.jumbune.datavalidation.DataValidationJobExecutor";
	
	/** The lib jars. */
	String LIB_JARS = "-libjars";
	
	/** The gson jar. */
	String GSON_JAR = "lib/gson-2.2.4"+ JAR;
	
	/** The common jar. */
	String COMMON_JAR = "lib/jumbune-common-"+Versioning.BUILD_VERSION+Versioning.DISTRIBUTION_NAME + JAR;
	
	/** The utilities jar. */
	String UTILITIES_JAR = "lib/jumbune-utils-"+Versioning.BUILD_VERSION+Versioning.DISTRIBUTION_NAME + JAR;
	
	/** The exception. */
	String EXCEPTION = "Exception";
	
	/** The error. */
	String ERROR = "Error";
	
	/** The log processor error. */
	String LOG_PROCESSOR_ERROR = "{\"ErrorAndException\": {\"Debug Analysis\" : \" Error occured during Debug Analysis \"}}";

	/** The pipe separator. */
	String PIPE_SEPARATOR = "|";
	
	/** The dv details. */
	String DV_DETAILS = "dv";

	/** The consolidated dv loc. */
	String CONSOLIDATED_DV_LOC = "dv/";
	
	/** The slave dv loc. */
	String SLAVE_DV_LOC = "dv/*";

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
	
	/** The property file. */
	String PROPERTY_FILE = "emailNotifyProperties.properties";
	
	/** The from. */
	String FROM = "prachi.gupta@impetus.co.in";
	
	/** The to. */
	String TO = "prachi.gupta@impetus.co.in";
	
	/** The subject. */
	String SUBJECT = "Jumbune Support";
	
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
	String JUMBUNE_RELATIVE_DV_JAR_PATH = "modules/jumbune-datavalidation-"+Versioning.BUILD_VERSION+Versioning.DISTRIBUTION_NAME+".jar";
	
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
	
	/**
	 * String for ls command
	 */
	String LS_COMMAND = "ls";
	
	/**
	 * The Pure Jar Counter String
	 */
	String PURE_JAR_COUNTER = "PURE_JAR_COUNTER";
	
	/**
	 * The Pure Profiling String
	 */
	String PURE_PROFILING = "PURE_PROFILING";
	
	/**
	 * The Instrumented jar Counter String
	 */
	String INSTRUMENTED_JAR_COUNTER = "INSTRUMENTED_JAR_COUNTER";
	
	/**
	 * The Debug Analyzer String
	 */
	String DEBUG_ANALYZER = "DEBUG_ANALYZER";	
	
	/*
	 * Following are Number literals used in different classes, to avoid usage of magic numbers
	 */
	
	int ONE_ZERO_TWO_FOUR = 1024;	
	
	int FIVE_HUNDRED = 500;
	
	int TWENTY_TWO= 22;
	 
	int THOUSAND = 1000;
	
	int THREE = 3;
	
	int FOUR = 4;
	
	int EIGHT = 8;
	
	int HUNDRED = 100;
	
	int TEN_THOUSAND = 10000;
	
	int FIVE_THOUNSAND = 5000;
		
 	int FIFTY = 50;

	int FIVE = 5;
	
	long TENL = 10L;

	byte ZERO_CROSS_ZERO_A = 0x0a;
	
	int NINE = 9;
	
	int FOUR_HUNDERED_FIFTY_SEVEN = 457;
	
	int FOUR_HUNDERED_SIXTY_ONE = 461;
	
	int ONE_THREE_THREE = 133;	
	
}
