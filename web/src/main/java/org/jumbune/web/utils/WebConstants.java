package org.jumbune.web.utils;

/**
 * This class is used to refer constants required for web module.
 *
 */
public interface WebConstants {
	
	String ADD = "add";
	String AJAX_CALL = "AJAXCALL";
	String AJAX_STOP = "STOP";
	String AJAX_STOP_MSG = "{\"AJAXCALL\":\"STOP\"}";
	String ALLOCATED_MAP_MEMORY = "allocatedMapMemory";
	String ALLOCATED_REDUCE_MEMORY = "allocatedReduceMemory";
	String ALL_NODES = "All Nodes";
	String APPLICATION = "application";
	String CLUSTER_DIR = "/clusters";
	String CLUSTER_NAME = "clusterName";
	String CLUSTER_NAME_PARAM = "clusterName";
	String DATA_ANALYSIS_SOCKET_URL = "/results/dataanalysis";
	String DECIMAL_FORMAT = "#.00";
	String DRF = "DRF";
	String EFF_MAP_MEMORY_PERCENT = "effMapMemoryPercent";
	String EFF_REDUCE_MEMORY_PERCENT = "effReduceMemoryPercent";
	String END_TIME = "endTime";
	String EXECUTION_ENGINE = "executionEngine";
	String HOME_URL = "/jsp/Home.jsp";
	String JOB = "job";
	String JOBS_LIST = "jobsList";
	String JOB_DURATION = "jobDuration";
	String JOB_HISTORY = "jobHistory";
	String JOB_HISTORY_DETAILS_TABLE = "jobHistoryDetails";
	String JOB_ID = "jobId";
	String JOB_NAME_1 = "jobName";
	String JOB_REQUEST_JSON = "/request.json";
	String JOB_RESPONSE_JSON = "/response.json";
	String JOB_START_TIME = "jobStartTime";
	String JSON_EXTENSION = ".json";
	String JSON_REPO = "/jsonrepo/";
	String JUMBUNE_HOME = "JUMBUNE_HOME";
	String JUMBUNE_STATE_FILE = "/jumbuneState.txt";
	String LAST_CHECKPOINT = "lastCheckpoint";
	String LONG_RUNNING_APPS_KEY = "longRunningApps";
	String MAX = "max";
	String MB = " MB";
	String MEAN = "mean";
	String NUMBER = "number";
	String QUEUE_NAME = "queueName";
	String RELATIVE_PERCENT = "relativePercentUsage";
	String RESOURCE_OVER_USAGE_KEY = "resourceOverUsage";
	String RESULT_URL = "/jsp/YamlResult.jsp";
	String RUNNING_APPS_KEY = "runningApps";
	String RUNNING_CONTAINERS_KEY = "runningContainers";
	String RUNNING_JOBS_NAME = "runningJobNameList" ;
	String SESSION_MR_COMMUNICATORS = "mrCommunicators";
	String SESSION_RM_COMMUNICATORS = "rmCommunicators";
	String SPACE_REGEX = "\\s+";
	String START_TIME = "startTime";
	String STEADY_FAIR_SHARE = "steadyFairShare";
	String TMP_DIR_PATH = "/tmp";
	String USED_CONTAINERS = "usedContainers";
	String USED_CORES = "usedCores";
	String USED_MEMORY = "usedMemory";
	String USED_RESOURCES_MEMORY = "usedResourcesMemory";
	String USED_RESOURCES_V_CORES = "usedResourcesVCores";
	String USED_V_CORES = "usedVCores";
	String USER_NAME = "userName";
	String USER_QUEUE_UTILIZATION = "userQueueUtilization";
	String USER_QUEUE_UTILIZATION_URL = "/user-queue-utilization";
	String UTILIZAION_PERCENT_WRT_CLUSTER = "utilizationPercentWRTCluster";
	String UTILIZAION_PERCENT_WRT_QUEUE = "utilizationPercentWRTQueue";	
	String YARN_HIGH_AVAILABLITY_ALERT = "org.jumbune.common.yarn.alerts.HighAvailabilityAlert";
	String ZERO = "0";
	char DOT = '.';
	
	//	constants for web services url
	
	String ADMIN_CONFIGURATION_URL = "/adminconfig";
	String ALERTS = "/alerts";
	String ALERTS_UPDATE_INTERVAL = "/alertsupdateinterval";
	String ANALYZE_DATA = "analyzeData";
	String ANALYZE_JOB = "analyzeJob";
	String BACKGROUND_PROCESSES = "/background-processes";
	String CATEGORIES = "/categories";
	String CLUSTERWIDE_MAJORCOUNTERS = "/clusterwide-majorcounters";
	String CLUSTER_ANALYSIS_SERVICE_URL = "/clusteranalysis";
	String CLUSTER_NODES = "/clusternodes";
	String CLUSTER_PROFILING = "/clusterprofiling";
	String CLUSTER_SERVICE_URL = "/cluster";
	String DATA_CENTER_HEAT_MAP = "/datacenterheatmap";
	String DATA_LOAD_AND_DISTRIBUTION = "/dataloadanddistribution";
	String DV_REPORT_SERVICE_URL = "/dvreport";
	String FILTERED_CATEGORIES = "/filteredcategories";
	String GATHER_SCHEDULED_JOB_RESULT_SERVICE_URL = "/gatherscheduledjobresult";
	String HADOOP_TYPE = "/hadooptype";
	String HOME_SERVICE_URL = "/home";
	String INIT_CLUSTER = "/initcluster";
	String IS_FAIR_SCHEDULER = "/is-fair-scheduler";
	String IS_INFLUXDB_LIVE = "/is-influxdb-live";
	String IS_MAPR = "/is-mapr";
	String JOB_ANALYSIS = "/jobanalysis";
	String JOB_ANALYSIS_SOCKET_URL = "/results/jobanalysis";
	String JOB_HDFS_DETAILS = "/jobhdfsdetails" ;
	String JOB_HISTORY_URL = "/job-history";
	String JOB_NAME = "/jobname";
	String JSON = "/json";
	String JSON_TABLE= "/jsondvtable";
	String LONG_RUNNING_APPS_URL = "/longrunningapps/{clusterName}";
	String MAPR_CLDB_METRICS = "/mapr-cldb-metrics";
	String PROFILEJOB = "/profilejob";
	String RESOURCE_OVER_USAGE_URL = "/resourceoverusage/{clusterName}";
	String RESULT_SERVICE_URL = "/resultservice";
	String SAVE = "/save";
	String SCHEDULED_DQT_JOBS = "/scheduleddqtjobs";
	String SCHEDULED_TUNING_JOBS = "/scheduledtuningjobs";
	String SCHEDULED_TUNING_JOB_RESULT = "/scheduledtuningjobresult";
	String SLA_APPS = "/sla-apps";
	String VALIDATE_JOB_INPUT = "/validatejobinput";
	String VALIDATE_SERVICE_URL = "/validateservice";
	String XML_DV_REPORT_SERVICE_URL = "/xmldvreport";

//	constants for web services url (END)

	String DATA_CENTER_DEFAULT_CONFIG = "{\"color\":[{\"stat\":\"CpuUsage\",\"category\":\"systemStats.c"
			+ "pu\",\"good\":{\"operator\":\"LESS_THAN_OP\",\"val\":\"25\"}"
			+ ",\"bad\":{\"operator\":\"GREATER_THAN_OP\",\"val\":\"75\"}},"
			+ "{\"stat\":\"UsedMemory\",\"category\":\"systemStats.memory\""
			+ ",\"good\":{\"operator\":\"LESS_THAN_OP\",\"val\":\"25\"},\"b"
			+ "ad\":{\"operator\":\"GREATER_THAN_OP\",\"val\":\"75\"}}]}";
	
	/**
	 * Queue capacity (in percent) with respect to cluster
	 */
	String ABSOLUTE_QUEUE_CAPACITY_PERCENT = "absoluteQueueCapacityPercent";

}
