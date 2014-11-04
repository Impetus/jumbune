package org.jumbune.profiling.utils;


/**
 * Message constants of Profiler module.
 */
public interface ProfilerConstants {
	int MESSAGE_HEAP_SAMPLE = 3000;
	int MESSAGE_CPU_SAMPLE = 3001;
	int PROFILER_FILE_EXTN = 3002;
	int DEFAULT_DN_PORT = 5679;
	int DEFAULT_TT_PORT = 5678;

	String DATANODE = "DataNode";
	String NAMENODE = "NameNode";
	String TASKTRACKER = "TaskTracker";
	String JOBTRACKER = "JobTracker";
	String RESOURCEMANAGER = "ResourceManager";
	String NODEMANAGER = "NodeManager";
	String READS_FROM_LOCAL_CLIENT = "reads_from_local_client";
	String READS_FROM_REMOTE_CLIENT = "reads_from_remote_client";
	String WRITEBLOCKOPAVGTIME = "writeBlockOpAvgTime";
	String BLOCK_READ = "blocks_read";
	String BYTES_WRITTEN = "bytes_written";
	String RPCPROCESSINGTIMEMAXTIME = "RpcProcessingTimeMaxTime";
	String RPCQUEUETIMEMAXTIME = "RpcQueueTimeMaxTime";
	String CAPACITYTOTAL = "CapacityTotal";
	String BLOCKSTOTAL = "BlocksTotal";
	String NAMENODE_JMXSTATS_STARTING_NAME = "NN_jmxstats_";
	String DATANODE_TASKTRACKER_JMXSTATS_STARTING_NAME = "DNTT_jmxstats_";
	String ERRORANDEXCEPTION = "ErrorAndException";
	String JMX_EXCEPTION_DETAIL = "JmxException";
	String PURE_PROFILING_EXCEPTION_DETAIL = "ProfilingException";

	String JMX_URL_PREFIX = "service:jmx:rmi:///jndi/rmi://";
	String JMX_URL_POSTFIX = "/jmxrmi";
	String SERVICE_URL = "Hadoop:service=";
	String HADOOP_SERVICE_URL = ":service=";
	String OS_URL ="java.lang:type=OperatingSystem";
	String JUMBUNE_CONTEXT_URL = "org.jumbune.context:type=ExposedJumbuneMetrics";
	String PARTITION_LIST_COMMAND = "df -h";
	String NO_PARTITION = "none";
	String PARTITION_STATS_COMMAND = "vmstat -p ";
	String VMSTAT_COMMAND = "vmstat -s";
	String EXECUTION_MODE = "exec";
	String HOME = "/home/";
	String RSA_FILE = "/.ssh/id_rsa";
	String DSA_FILE = "/.ssh/id_dsa";
	String USED_MEMORY = "usedmemory";
	String FREE_MEMORY = "freememory";
	String USED_SWAP = "usedswap";
	String FREE_SWAP = "freeswap";
	String READ_BLOCK_OP_MAX_TIME = "readBlockOpMaxTime";
	String WRITE_BLOCK_OP_MAX_TIME = "writeBlockOpMaxTime";
	String RPC_PROCESSING_AVG_TIME = "RpcProcessingTimeAvgTime";
	String RPC_PROCESSING_MAX_TIME = "RpcProcessingTimeMaxTime";
	String DFS_USED = "DfsUsed";
	String CPU_USAGE_COMMAND = "top -b -n 2 |grep ^Cpu";
	String CPU_USAGE_COMMAND_WITHOUT_CARET = "top -b -n 2 |grep Cpu";
	String CPU_DETAILS_COMMAND = "cat /proc/cpuinfo";
	String REMAINING = "Remaining";
	String NETWORK_LATENCY_COMMAND = "mtr -l -c 1";
	String JUMBUNE_CONTEXT_SERVICE = "ExposedJumbuneMetrics";
	String MAP_SLOTS_AVALIABLE = "MapTaskSlots";
	String REDUCE_SLOTS_AVALIABLE = "ReduceTaskSlots";
	String MAPS_RUNNING = "Maps_running";
	String REDUCES_RUNNING = "Reduces_running";
	String THREADS_PER_CORE = "Thread(s) per core:";
	String CORES_PER_SCOKET = "Core(s) per socket:";

	String KEY_NAMENODE_JMX_ATTRIBUTES = "namenode.selected.jmx.attributes";
	String KEY_JOBTRACKER_JMX_ATTRIBUTES = "jobtracker.selected.jmx.attributes";
	String KEY_TASKTRACKER_JMX_ATTRIBUTES = "tasktracker.selected.jmx.attributes";
	String KEY_DATANODE_JMX_ATTRIBUTES = "datanode.selected.jmx.attributes";
	String KEY_BYTE_TO_GB_ATTRIBUTES = "regex.byte.to.gb.attributes";
	String KEY_BYTE_TO_MB_ATTRIBUTES = "regex.byte.to.mb.attributes";
	String DOT_SEPARATOR = ".";
	String NODE_NOT_REACHABLE = "Node is not reachable";
	String TT_NOT_REACHABLE = "Tasktracker is not reachable";
	String NM_NOT_REACHABLE = "NodeManager is not reachable";
	String DN_NOT_REACHABLE = "Datanode is not reachable";
	String DN_TT_NOT_REACHABLE = "Datanode and Tasktracker are not reachable";
	String DN_NM_NOT_REACHABLE = "Datanode and NodeManager are not reachable";
	String SUBCAT_MEMORY = "memory";
	String SUBCAT_CPU = "cpu";
	String SUBCAT_OS = "os";
	String H_TOOLS_JAR = "hadoop-tools-1.0.4.jar";
	int FIVE_ONE_TWO = 512;
	int FOUR = 4;
	int EIGHT = 8;
	int FIFTEEN = 15;
	int TWENTY_TWO= 22;
	int THREE = 3;
	int HUNDRED = 100;
	double DOT_TWO = .2;
	int SEVENTEEN = 17;
	int THIRTY_ONE = 31;
	int THOUSAND = 1000;
	int ONE_ZERO_TWO_FOUR = 1024;
	long THOUSAND_L = 1000l;	

	/**
	 * The Enum HEALTH_CATEGORIES.
	 */
	enum HEALTH_CATEGORIES {
		MEMORY_INFO, DISK_PARTITION_INFO, NODE_THROUGHPUT, CPU_STATS, CLUSTER_LOAD, LOCAL_DATA_UTILISATION
	}

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
