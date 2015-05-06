package org.jumbune.remoting.common;

import java.util.concurrent.CyclicBarrier;

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
    String REMOVE_FOLDER = "rm -r";
	
	/** The ssh. */
	String SSH = "ssh";
	
	/** The hash. */
	String HASH = "#";
	
	/** The redirect symbol. */
	String REDIRECT_SYMBOL = ">";
	
	String DATA_VALIDATION_JAR = "jumbune-datavalidation-1.5.1-SNAPSHOT.jar";
	
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

}
