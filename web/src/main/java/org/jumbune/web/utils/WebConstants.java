package org.jumbune.web.utils;

/**
 * This class is used to refer constants required for web module.
 *
 */
public interface WebConstants {

	/** The result url. */
	String RESULT_URL = "/jsp/YamlResult.jsp";
	
	/** The home url. */
	String HOME_URL = "/jsp/Home.jsp";

	/** The ajax stop msg. */
	String AJAX_STOP_MSG = "{\"AJAXCALL\":\"STOP\"}";
	
	/** The ajax call. */
	String AJAX_CALL = "AJAXCALL";
	
	/** The ajax stop. */
	String AJAX_STOP = "STOP";
	
	/** The scheduling successful. */
	String SCHEDULING_SUCCESSFUL = "Your job has been successfully scheduled";
	
	/** The scheduling default message. */
	String SCHEDULING_DEFAULT_MESSAGE = "Date picker here";

	/**  The Json File *. */
	String JSON_EXTENSION = ".json";
	
	/** The saved json dir name. */
	String SAVED_JSON_DIR_NAME = "/SavedJson";
	
	/** The tmp dir path. */
	String TMP_DIR_PATH = "/tmp";
	
	/** The resource dir path. */
	String RESOURCE_DIR_PATH = "/resources";
	
	/** The sample yaml name. */
	String SAMPLE_YAML_NAME = "Sample.yaml";
	
	/** The jumbune properties yaml. */
	String JUMBUNE_PROPERTIES_YAML = "/Jumbune-UserProperties.yaml";
	
	/** The jumbune state file. */
	String JUMBUNE_STATE_FILE = "/jumbuneState.txt";

	/** The dependent jar include. */
	String DEPENDENT_JAR_INCLUDE = "resource";
	
	/** The dependent jar exclude. */
	String DEPENDENT_JAR_EXCLUDE = "exclude";
	
	/** The dependent jar master machine path. */
	String DEPENDENT_JAR_MASTER_MACHINE_PATH = "folder";
	
	/** The dependent jar split regex exp. */
	String DEPENDENT_JAR_SPLIT_REGEX_EXP = "\\\\n";
	
	/** The dependnet jar resources dir. */
	String DEPENDNET_JAR_RESOURCES_DIR = "dependentJarResource/";
	
	/** The master machine path option. */
	int MASTER_MACHINE_PATH_OPTION = 3;
	
	/** The dependent jar include dir. */
	String DEPENDENT_JAR_INCLUDE_DIR = "resource/";
	
	/** The dependent jar exclude dir. */
	String DEPENDENT_JAR_EXCLUDE_DIR = "exclude/";
	
	/** The dependent jar master dir. */
	String DEPENDENT_JAR_MASTER_DIR = "folders/";
	
	/** The dependent jar include resource. */
	String DEPENDENT_JAR_INCLUDE_RESOURCE = "files";
	
	/** The dependent jar exclude resource. */
	String DEPENDENT_JAR_EXCLUDE_RESOURCE = "excludes";
	
	/** The dependent jar folder resource. */
	String DEPENDENT_JAR_FOLDER_RESOURCE = "folders";

	/** The file name. */
	String FILE_NAME = "fileName";
	
	/** The dv type. */
	String DV_TYPE = "dvType";
	
	/** The page number. */
	String PAGE_NUMBER = "page";
	
	/** The rows. */
	String ROWS = "rows";
	
	/** The copy command. */
	String COPY_COMMAND = "cp ";
	
	/** The lib directory. */
	String LIB_DIRECTORY = "/lib/";
	
	/** The xls ext. */
	String XLS_EXT = ".xls";
	
	/** The reports bean. */
	String REPORTS_BEAN = "ReportsBean";
	
	/** The report dir. */
	String REPORT_DIR = "/ExcelReports";
	
	/** The profiling property file. */
	String PROFILING_PROPERTY_FILE="jumbune-profiling.properties";
	
	/** The profiling system json. */
	String PROFILING_SYSTEM_JSON="system.stats.json";
	
	/** The jumbune home. */
	String JUMBUNE_HOME = "JUMBUNE_HOME";
	
	/** The cluster dir. */
	String CLUSTER_DIR = "/clusters";

	/** The cluster profiling result. */
	String CLUSTER_PROFILING_RESULT = "/jsp/ClusterProfiling.jsp" ;	
	
	/** The json repo. */
	String JSON_REPO = "/jsonrepo/";
	
	String JOB_REQUEST_JSON = "/request.json";
	
	String JOB_RESPONSE_JSON = "/response.json";
	
	//constants for urls of websocket
	
	/** The job analysis socket url. */
	String JOB_ANALYSIS_SOCKET_URL = "/results/jobanalysis";
	
	/** The data analysis socket url. */
	String DATA_ANALYSIS_SOCKET_URL = "/results/dataanalysis";
	
	//constants for urls of websocket(END)
	
	//	constants for web services url
	
	/** The clear logs service url. */
	String CLEAR_LOGS_SERVICE_URL = "/clearLogs";
	
	/** The cluster service url. */
	String CLUSTER_SERVICE_URL = "/cluster";
	
	/** The cluster profiling service url. */
	String CLUSTER_PROFILING_SERVICE_URL = "/clusterprofiling";
	
	/** The cluster analysis service url. */
	String CLUSTER_ANALYSIS_SERVICE_URL = "/clusteranalysis";
	
	/** The data load and distribution. */
	String DATA_LOAD_AND_DISTRIBUTION = "/dataloadanddistribution";
	
	/** The alerts. */
	String ALERTS = "/alerts";
	
	/** The alerts update interval. */
	String ALERTS_UPDATE_INTERVAL = "/alertsupdateinterval";
	
	/** The profilejob. */
	String PROFILEJOB = "/profilejob";
	
	/** The add thread. */
	String ADD_THREAD = "/addthread";
	
	/** The delete thread. */
	String DELETE_THREAD = "/deletethread";
	
	/** The categories. */
	String CATEGORIES = "/categories";
	
	/** The filtered categories. */
	String FILTERED_CATEGORIES = "/filteredcategories";
	
	/** The get influx db conf. */
	String GET_INFLUX_DB_CONF = "/influxdbconf";
	
	/** The cluster nodes. */
	String CLUSTER_NODES = "/clusternodes";
	
	/** The clusterwide majorcounters. */
	String CLUSTERWIDE_MAJORCOUNTERS = "/clusterwide-majorcounters";
	
	/** The dv report service url. */
	String DV_REPORT_SERVICE_URL = "/dvreport";
	
	/** The xml dv report service url. */
	String XML_DV_REPORT_SERVICE_URL = "/xmldvreport";
	
	/** The execution service url. */
	String EXECUTION_SERVICE_URL = "/execution";
	
	/** The export excel service url. */
	String EXPORT_EXCEL_SERVICE_URL = "/exportexcel";
	
	/** The gather scheduled job result service url. */
	String GATHER_SCHEDULED_JOB_RESULT_SERVICE_URL = "/gatherscheduledjobresult";
	
	/** The home service url. */
	String HOME_SERVICE_URL = "/home";
	
	String ADMIN_CONFIGURATION_URL = "/adminconfig";
	
	/** The jumbune history job picker service url. */
	String JUMBUNE_HISTORY_JOB_PICKER_SERVICE_URL = "/historical";
	
	/** The profiler service url. */
	String PROFILER_SERVICE_URL = "/profilerservice";
	
	/** The result service url. */
	String RESULT_SERVICE_URL = "/resultservice";
	
	/** The save json service url. */
	String SAVE_JSON_SERVICE_URL = "/savejsonservice";
	
	/** The scheduler info service url. */
	String SCHEDULED_TUNING_JOBS = "/scheduledtuningjobs";
	
	String SCHEDULED_DQT_JOBS = "/scheduleddqtjobs";
	
	/** The upload job jar service url. */
	String UPLOAD_JOB_JAR_SERVICE_URL = "/uploadjobjarservice";
	
	/** The upload service url. */
	String UPLOAD_SERVICE_URL = "/uploadservice";
	
	/** The validate json service url. */
	String VALIDATE_SERVICE_URL = "/validateservice";
	
	/** The data suite service url. */
	String DATA_SUITE_SERVICE_URL = "/datasuite"; 	
	
	/** The analyse data. */
	String ANALYZE_DATA = "analyzeData";
	
	/** The analyze job. */
	String ANALYZE_JOB = "analyzeJob";
	
	/** The job analysis. */
	String JOB_ANALYSIS = "/jobanalysis";
	
	/** The save. */
	String SAVE = "/save";
	
	/** The hadoop type. */
	String HADOOP_TYPE = "/hadooptype";

	/** The cluster profiling. */
	String CLUSTER_PROFILING = "/clusterprofiling";

	/** The data center heat map. */
	String DATA_CENTER_HEAT_MAP = "/datacenterheatmap";
	
	/** The job hdfs details. */
	String JOB_HDFS_DETAILS = "/jobhdfsdetails" ;

	/** The delete table. */
	String DELETE_TABLE = "/deletetable";

	String SCHEDULED_TUNING_JOB_RESULT = "/scheduledtuningjobresult";

	String INIT_CLUSTER = "/initcluster";

	String VALIDATE_JOB_INPUT_DETAILS = "/validatejobinputdetails";

	String VALIDATE_JOB_INPUT = "/validatejobinput";

	String JOB_NAME = "/jobname";
	
	String JSON = "/json";
	
	String JSON_TABLE= "/jsondvtable";

	String RESOURCE_OVER_USAGE_URL = "/resourceoverusage/{clusterName}";

	String LONG_RUNNING_APPS_URL = "/longrunningapps/{clusterName}";

	String CLUSTER_METRICS_URL = "/clustermetrics/{clusterName}";

//	constants for web services url (END)

	String CLUSTER_NAME_PARAM = "clusterName";

	String IS_MAPR = "/is-mapr";

	String MAPR_CLDB_METRICS = "/mapr-cldb-metrics";

	String SLA_APPS = "/sla-apps";
	
	/** The recommendations. */
	String RECOMMENDATIONS = "/recommendations";

	String IS_INFLUXDB_LIVE = "/is-influxdb-live";
	
	String JOB_NAME_1 = "jobName";

	String JOB_ID = "jobId";

	String DECIMAL_FORMAT = "#.00";

	String EFF_REDUCE_MEMORY_PERCENT = "effReduceMemoryPercent";

	String EFF_MAP_MEMORY_PERCENT = "effMapMemoryPercent";

	String ALLOCATED_REDUCE_MEMORY = "allocatedReduceMemory";

	String ALLOCATED_MAP_MEMORY = "allocatedMapMemory";
	
	String JOB_DURATION = "jobDuration";

	String JOB_HISTORY = "jobHistory";

	String JOB_HISTORY_URL = "/job-history";
	
	String JOB_START_TIME = "jobStartTime";
	
	String USER_QUEUE_UTILIZATION = "userQueueUtilization";
	
	String QUEUE_NAME = "queueName";
	
	String USER_NAME = "userName";
	
	String UTILIZAION_PERCENT = "utilizationPercent";

	String USER_QUEUE_UTILIZATION_URL = "/user-queue-utilization";
	
	String CHARGE_BACK_URL = "/charge-back-utilization";

	String USED_CORES = "usedCores";

	String USED_MEMORY = "usedMemory";
	
	String RELATIVE_PERCENT = "relativePercentUsage";
	
	String SPACE_REGEX = "\\s+";
	
	String CURRENT_CAPACITY = "currentCapacity";
	
	String MAXIMUM_CAPACITY = "maximumCapacity";
	
	String CAPACITY = "capacity";
	
	String AVERAGE_WAITING_TIME = "averageWaitingTime";

	String JOB_HISTORY_DETAILS_TABLE = "jobHistoryDetails";

	String USED_CONTAINERS = "usedContainers";

	String USED_V_CORES = "usedVCores";
	
	String JOB = "job";

	String APPLICATION = "application";

	String MAX = "max";
	
	String IS_FAIR_SCHEDULER = "/is-fair-scheduler";
	
	char DOT = '.';
	
	String NUMBER = "number";
	
	String MB = " MB";
	
	/** The Constant YARN_CLUSTER_PROFILING_SERVICE. */
	String YARN_CLUSTER_PROFILING_SERVICE = "org.jumbune.clusterprofiling.yarn.service.ClusterProfilingServiceImpl";

	/** The Constant NON_YARN_CLUSTER_PROFILING_SERVICE. */
	String NON_YARN_CLUSTER_PROFILING_SERVICE = "org.jumbune.clusterprofiling.service.ClusterProfilingServiceImpl";

	/** The Constant YARN_HIGH_AVAILABLITY_ALERT. */
	String YARN_HIGH_AVAILABLITY_ALERT = "org.jumbune.common.yarn.alerts.HighAvailabilityAlert";

	String DATA_CENTER_DEFAULT_CONFIG = "{\"color\":[{\"stat\":\"CpuUsage\",\"category\":\"systemStats.c"
			+ "pu\",\"good\":{\"operator\":\"LESS_THAN_OP\",\"val\":\"25\"}"
			+ ",\"bad\":{\"operator\":\"GREATER_THAN_OP\",\"val\":\"75\"}},"
			+ "{\"stat\":\"UsedMemory\",\"category\":\"systemStats.memory\""
			+ ",\"good\":{\"operator\":\"LESS_THAN_OP\",\"val\":\"25\"},\"b"
			+ "ad\":{\"operator\":\"GREATER_THAN_OP\",\"val\":\"75\"}}]}";

	String EMPTY_CATEGORY_JSON = "{\"trends\":{}}";

	String RUNNING_APPS_KEY = "runningApps";
	
	String RUNNING_JOBS_NAME = "runningJobNameList" ;

	String RUNNING_CONTAINERS_KEY = "runningContainers";

	String RESOURCE_OVER_USAGE_KEY = "resourceOverUsage";

	String LONG_RUNNING_APPS_KEY = "longRunningApps";
	
	String DRF = "DRF";
	
	String CLUSTER_NAME = "clusterName";
	
	String ALL_NODES = "All Nodes";
	
	String BACKGROUND_PROCESSES = "/background-processes";

	String MEMORY_COST = "memoryCost";
	
	String V_CORE_COST = "vCoreCost";

	String USER = "user";
	
	String TOTAL_COST = "totalCost";
	
	String CONFIGURED_MEMORY_COST = "configuredMemoryCost";
	
	String MEMORY_GB_HOURS_USED = "memoryGbHoursUsed";
	
	String CONFIGURED_VCORE_COST = "configuredVcoreCost";
	
	String V_CORE_HOURS_USED = "vCoreHoursUsed";
	
	String PREVIOUS_MONTH = "previous";
	
	String CURRENT_MONTH = "current";
	
	String YYYY_MM_DD_HH_MM = "yyyy/MM/dd HH:mm";
	
	String ADD = "add";

	String EXECUTION_ENGINE = "executionEngine";
	
	String EXECUTION_ENGINE_DETAILS = "executionEngineDetails";
	String QUEUE_COST = "queueCost";
	String QUEUES_DETAILS = "queuesDetails";
	String USER_COST = "userCost";
	String MAP_REDUCE = "MapReduce";
	String MAPREDUCE = "MAPREDUCE";
	String SPARK2 = "Spark";
	String SPARK = "SPARK";

	String TEZ = "TEZ";

	String APACHE_TEZ = "Tez";


	String USED_RESOURCES_MEMORY = "usedResourcesMemory";
	String USED_RESOURCES_V_CORES = "usedResourcesVCores";

	String UTILIZAION_PERCENT_WRT_QUEUE = "utilizationPercentWRTQueue";	
	String UTILIZAION_PERCENT_WRT_CLUSTER = "utilizationPercentWRTCluster";
	String STEADY_FAIR_SHARE = "steadyFairShare";
	/**
	 * Queue capacity (in percent) with respect to cluster
	 */
	String ABSOLUTE_QUEUE_CAPACITY_PERCENT = "absoluteQueueCapacityPercent";

	String START_TIME = "startTime";

	String LAST_CHECKPOINT = "lastCheckpoint";
	
	String END_TIME = "endTime";

	String SESSION_RM_COMMUNICATORS = "rmCommunicators";
	String SESSION_MR_COMMUNICATORS = "mrCommunicators";
	String ZERO = "0";
	String MEAN = "mean";

	String JOBS_LIST = "jobsList";
	
}
