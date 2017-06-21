package org.jumbune.remoting.common;

import java.util.concurrent.CyclicBarrier;

import org.jumbune.utils.Versioning;

import io.netty.channel.ChannelInboundHandler;
import io.netty.util.AttributeKey;



// TODO: Auto-generated Javadoc
/**
 * The Interface RemotingConstants.
 */
public interface RemotingConstants {

	/** The barrier key. */
	AttributeKey<CyclicBarrier> barrierKey = AttributeKey.valueOf("barrier");
	
	/** The handler key. */
	AttributeKey<ChannelInboundHandler> handlerKey = AttributeKey.valueOf("handler");
	
	/** The command channel. */
	String COMMAND_CHANNEL = "CommandChannel";
	
	/** The binary channel. */
	String BINARY_CHANNEL = "BinaryChannel";
	
	/** The filetransfer channel. */
	String FILETRANSFER_CHANNEL = "FileTransferChannel";

	// Jar Receive
	/** The JA r_ magi c_1. */
	char JAR_MAGIC_1 = 'J';
	
	/** The JA r_ magi c_2. */
	char JAR_MAGIC_2 = 'A';
	
	/** The JA r_ magi c_3. */
	char JAR_MAGIC_3 = 'R';

	// Jar Send
	 /** The JA s_ magi c_1. */
	char JAS_MAGIC_1 = 'J';
	 
 	/** The JA s_ magi c_2. */
 	char JAS_MAGIC_2 = 'A';
	 
 	/** The JA s_ magi c_3. */
 	char JAS_MAGIC_3 = 'S';

	// Text Receive
	/** The TX r_ magi c_1. */
	char TXR_MAGIC_1 = 'T';
	
	/** The TX r_ magi c_2. */
	char TXR_MAGIC_2 = 'X';
	
	/** The TX r_ magi c_3. */
	char TXR_MAGIC_3 = 'R';

	// Text Send
	/** The TX s_ magi c_1. */
	char TXS_MAGIC_1 = 'T';
	
	/** The TX s_ magi c_2. */
	char TXS_MAGIC_2 = 'X';
	
	/** The TX s_ magi c_3. */
	char TXS_MAGIC_3 = 'S';

	// Command Fire and Forget
	/** The CM d_ magi c_1. */
	char CMD_MAGIC_1 = 'C';
	
	/** The CM d_ magi c_2. */
	char CMD_MAGIC_2 = 'M';
	
	/** The CM d_ magi c_3. */
	char CMD_MAGIC_3 = 'D';
	
	// Command Fire and Forget Async
	/** The CM d_ magi c_1. */
	char CMA_MAGIC_1 = 'C';
	
	/** The CM d_ magi c_2. */
	char CMA_MAGIC_2 = 'M';
	
	/** The CM d_ magi c_3. */
	char CMA_MAGIC_3 = 'A';

	// Command Fire and Get String Response
	/** The CM g_ magi c_1. */
	char CMG_MAGIC_1 = 'C';
	
	/** The CM g_ magi c_2. */
	char CMG_MAGIC_2 = 'M';
	
	/** The CM g_ magi c_3. */
	char CMG_MAGIC_3 = 'G';

	// Command Fire and Get Object Response
	/** The CM o_ magi c_1. */
	char CMO_MAGIC_1 = 'C';
	
	/** The CM o_ magi c_2. */
	char CMO_MAGIC_2 = 'M';
	
	/** The CM o_ magi c_3. */
	char CMO_MAGIC_3 = 'O';

	// Command Fire and Get Object Response in case of HA
	char CMO_HA_MAGIC_1 = 'c';
	
	char CMO_HA_MAGIC_2 = 'm';
	
	char CMO_HA_MAGIC_3 = 'o';
	
	// Command Fire and Forget in case of HA
	char CMD_HA_MAGIC_1 = 'c';

	char CMD_HA_MAGIC_2 = 'm';

	char CMD_HA_MAGIC_3 = 'd';	
	
	// Text Receive in case of HA
	/** The TX r_ magi c_1. */
	char TXR_HA_MAGIC_1 = 't';
	
	/** The TX r_ magi c_2. */
	char TXR_HA_MAGIC_2 = 'x';
	
	/** The TX r_ magi c_3. */
	char TXR_HA_MAGIC_3 = 'r';
	
	// Text Send in case of HA
	/** The TX s_ magi c_1. */
	char TXS_HA_MAGIC_1 = 't';
	
	/** The TX s_ magi c_2. */
	char TXS_HA_MAGIC_2 = 'x';
	
	/** The TX s_ magi c_3. */
	char TXS_HA_MAGIC_3 = 's';
	
	// Jar Receive in case of HA
	/** The JA r_ magi c_1. */
	char JAR_HA_MAGIC_1 = 'j';
	
	/** The JA r_ magi c_2. */
	char JAR_HA_MAGIC_2 = 'a';
	
	/** The JA r_ magi c_3. */
	char JAR_HA_MAGIC_3 = 'r';
	
	// Jar Send
	 /** The JA s_ magi c_1. */
	char JAS_HA_MAGIC_1 = 'j';
	 
	/** The JA s_ magi c_2. */
	char JAS_HA_MAGIC_2 = 'a';
	 
	/** The JA s_ magi c_3. */
	char JAS_HA_MAGIC_3 = 's';
	
	char SDA_HA_MAGIC_1 = 's';
	
	char SDA_HA_MAGIC_2 = 'd';
	
	char SDA_HA_MAGIC_3 = 'a';
	
	String[] CMO_HA = new String[] { "c", "m", "o" };
	
	String[] CMD_HA = new String[] { "c", "m", "d" };
	
	String[] TXR_HA = new String[] { "t", "x", "r" };
	
	String[] TXS_HA = new String[] { "t", "x", "s" };
	
	String[] JAR_HA = new String[] { "j", "a", "r" };
	
	String[] JAS_HA = new String[] { "j", "a", "s" };
	
	String[] SDA_HA = new String[] { "s", "d", "a" };
	
	/** The path demarker. */
	String PATH_DEMARKER = "--!--";
	
	/** The static hprof request. */
	String STATIC_HPROF_REQUEST = "static_hprof_request";


	/** The dfs used. */
	String DFS_USED = "DfsUsed";
	
	/** The cpu usage command. */
	String CPU_USAGE_COMMAND = "top -b -n 1 |grep ^Cpu";
	
	/** The cpu details command. */
	String CPU_DETAILS_COMMAND = "cat /proc/cpuinfo";
	
	/** The remaining. */
	String REMAINING = "Remaining";
	
	/** The network latency command. */
	String NETWORK_LATENCY_COMMAND = "mtr -l -c 1";
	
	/** The partition list command. */
	String PARTITION_LIST_COMMAND = "df -h";
	
	/** The no partition. */
	String NO_PARTITION = "none";
	
	/** The partition stats command. */
	String PARTITION_STATS_COMMAND = "vmstat -p ";
	
	/** The vmstat command. */
	String VMSTAT_COMMAND = "vmstat -s";
	
	/** The execution mode. */
	String EXECUTION_MODE = "exec";
		
	
	String CHMOD_CMD = "chmod o+w ";
	
	String CHMOD_GROUP = "chmod g+w ";
	
	/** The Constant MKDIR_CMD. */
	String MKDIR_CMD = "mkdir -p ";
	
	/** The fetch hadoop masters and slaves. */
	String FETCH_HADOOP_MASTERS_AND_SLAVES = "fetchHadoopMastersAndSlaves";
	
	/** The regex for pipe delimited. */
	String REGEX_FOR_PIPE_DELIMITED = "[|]";
	
	/** The double newline. */
	String DOUBLE_NEWLINE = "\n \n";
	
	/** The echo. */
	String ECHO = "echo";
	
	/** The double bang. */
	String DOUBLE_BANG = "!!";
	
	/** The vmstat. */
	String VMSTAT = "vmstat";
	
	/** The cpu. */
	String CPU = "cpu";
	
	/** The df. */
	String DF = "df";
	
	/** The new line. */
	String NEW_LINE = "\n";

	
	/** The agent home. */
	String AGENT_HOME = "AGENT_HOME";
	
	/** The single space. */
	String SINGLE_SPACE = " ";
	
	/** The start curly bracket. */
	String START_CURLY_BRACKET = "{";
	
	/** The end curly bracket. */
	String END_CURLY_BRACKET = "}";
	
	/** The command separator. */
	String COMMAND_SEPARATOR = ",";
	
	/** The jumbune remote command separator. */
	String JUMBUNE_REMOTE_COMMAND_SEPARATOR = "@JRSC@";
    
    /** The remove folder. */
    String REMOVE_FOLDER = "rm -rf";
	
	/** The ssh. */
	String SSH = "ssh";
	
	/** The hash. */
	String HASH = "#";
	
	/** The redirect symbol. */
	String REDIRECT_SYMBOL = ">";
	
	/** The data validation jar. */
	String DATA_VALIDATION_JAR = "jumbune-datavalidation-" + Versioning.ENTERPRISE_BUILD_VERSION+Versioning.ENTERPRISE_DISTRIBUTION_NAME + ".jar";
	
	/** The job jars loc. */
	String JOB_JARS_LOC = "jobJars/";
	
	/** The three. */
	int THREE = 3;
	
	/** The zero cross ff. */
	int ZERO_CROSS_FF = 0xFF;
	
	/** The eight. */
	int EIGHT = 8 ;
	
	/** The twenty four. */
	int TWENTY_FOUR= 24;

	/** The sixteen. */
	int SIXTEEN = 16;
	
	/** The two. */
	int TWO = 2;

	/** The one zero two four. */
	int ONE_ZERO_TWO_FOUR = 1024;

	/** The five. */
	int FIVE = 5;

	/** The ten. */
	int TEN = 10 ;
	
	/** The thousand. */
	int THOUSAND = 1000;
	
	/** The twenty two. */
	int TWENTY_TWO= 22;

	/** The seven. */
	int SEVEN = 7;

	/** The six. */
	int SIX = 6;

	/** The four. */
	int FOUR = 4;

	/** The eight one nine two. */
	int EIGHT_ONE_NINE_TWO = 8192;

	/** The eight zero nine six zero. */
	int EIGHT_ZERO_NINE_SIX_ZERO = 80960;

	/** The remove. */
	String REMOVE = "rm";

	String RSYNC = "rsync";
	
	String JUMBUNE_JMX_JAR = "jumbune-jmx-agent.jar";

	String COLON = ":";

	String LIB_DIR = "/lib/";

	String SCP = "scp";

	String LOCALHOST = "localhost";

	String AT_SIGN = "@";
	
	String PIPE_OP = "|";

	String sudoEoption = "-E";

	String BASH = "bash";
	
	String BASH_ARG_C = "-c";
	
	/** The mapr. */
	String MAPR = "m";//mapr code changes
	
	/** The datanode */
	String DATANODE = "datanode";//mapr code changes
		
	String SLASH = "/";

	String HEART_BEAT_MSG = "HEART_BEAT";
	
	String COMMAND_EXEC_DRIVER = "org.jumbune.remoting.server.jsch.CommandExecDriver";
	
	String JAVA_HOME_PROP_KEY = "java.home";

	String CLASSPATH_ARG = "-cp"; 
	
	String HA_CONF_PROPERTIES = "ha_conf.properties";
	
	String CONF_DIR = "/conf/";
	
	String CLUSTERS_CONFIGURATIONS_DIR = "clustersConfigurations/";
	
	String HEART_BEAT_MILLIS = "HEART_BEAT_MILLIS";
	
	String THRESHOLD_BEATS_TO_MISS = "THRESHOLD_BEATS_TO_MISS";
	
	String NUM_RETRIES_AGENT_CONN = "NUM_RETRIES_AGENT_CONN";
	
	String AGENT_CONN_MILLIS = "AGENT_CONN_MILLIS";
	
	String NUM_RETRIES_REMOTER_APIS = "NUM_RETRIES_REMOTER_APIS";
	
	String COMMAND_LOG_DIR = "COMMAND_LOG_DIR";
	
	String ACK = "Ack";
	
	String AMPERSAND = "&";

	String NAMENODE = "NameNode";

}
