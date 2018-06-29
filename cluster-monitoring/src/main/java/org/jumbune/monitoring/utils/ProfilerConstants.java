package org.jumbune.monitoring.utils;


/**
 * Message constants of Profiler module.
 */
public interface ProfilerConstants {
	int PROFILER_FILE_EXTN = 3002;
	int DEFAULT_DN_PORT = 5679;
	int DEFAULT_TT_PORT = 5678;

	String HADOOP = "Hadoop";
	String DATANODE = "DataNode";
	String NAMENODE = "NameNode";
	String TASKTRACKER = "TaskTracker";
	String JOBTRACKER = "JobTracker";
	String RESOURCEMANAGER = "ResourceManager";
	String NODEMANAGER = "NodeManager";
	String READS_FROM_LOCAL_CLIENT = "reads_from_local_client";
	String READS_FROM_REMOTE_CLIENT = "reads_from_remote_client";
	String ERRORANDEXCEPTION = "ErrorAndException";
	String PURE_PROFILING_EXCEPTION_DETAIL = "ProfilingException";

	String JMX_URL_PREFIX = "service:jmx:rmi:///jndi/rmi://";
	String JMX_URL_POSTFIX = "/jmxrmi";
	String SERVICE_URL = "Hadoop:service=";
	String HADOOP_SERVICE_URL = ":service=";
	String OS_URL ="java.lang:type=OperatingSystem";
	String JUMBUNE_CONTEXT_URL = "org.jumbune.context:type=ExposedJumbuneMetrics";
	String PARTITION_LIST_COMMAND = "df -h";
	String PARTITION_STATS_COMMAND = "vmstat -p ";
	String VMSTAT_COMMAND = "vmstat -s && exit";
	String EXECUTION_MODE = "exec";
	String READ_BLOCK_OP_MAX_TIME = "readBlockOpMaxTime";
	String WRITE_BLOCK_OP_MAX_TIME = "writeBlockOpMaxTime";
	String RPC_PROCESSING_MAX_TIME = "RpcProcessingTimeMaxTime";
	String CPU_USAGE_COMMAND = "top -d 0.8 -b -n 2 |grep ^Cpu && exit";
	String CPU_USAGE_COMMAND_WITHOUT_CARET = "top -d 0.8 -b -n 2 |grep Cpu && exit";
	String CPU_DETAILS_COMMAND = "cat /proc/cpuinfo && exit";
	String NETWORK_LATENCY_COMMAND = "mtr -l -c 1";
	String DOT_SEPARATOR = ".";
	String NODE_NOT_REACHABLE = "Node is not reachable";
	String TT_NOT_REACHABLE = "Tasktracker is not reachable";
	String NM_NOT_REACHABLE = "NodeManager is not reachable";
	String DN_NOT_REACHABLE = "Datanode is not reachable";
	String DN_TT_NOT_REACHABLE = "Datanode and Tasktracker are not reachable";
	String DN_NM_NOT_REACHABLE = "Datanode and NodeManager are not reachable";
	String SUBCAT_CPU = "cpu";
	int FIVE_ONE_TWO = 512;
	int FOUR = 4;
	int EIGHT = 8;
	int TWENTY_TWO= 22;
	int THREE = 3;
	int HUNDRED = 100;
	int SEVENTEEN = 17;
	int THIRTY_ONE = 31;
	int THOUSAND = 1000;
	int ONE_ZERO_TWO_FOUR = 1024;
	long THOUSAND_L = 1000l;
	String COM_MAPR_CLDB = "com.mapr.cldb";	

	/**
	 * The Enum Operator.
	 */
	enum Operator {
		LESS_THAN_OP, LESS_THAN_EQUALTO_OP, EQUALT0_OP, GREATER_THAN_OP, GREATER_THAN_EQUALTO_OP
	}

	/**
	 * The Enum HADOOP_JMX_CAT.
	 */
	enum HADOOP_JMX_CAT {
		dfs, rpc, io, dataNodeMisc, ttMisc
	}

}
