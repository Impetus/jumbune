package org.jumbune.remoting.common;


/**
 * The Interface RemotingMethodConstants.
 * This interface contains the method names which can be invoked at agent side by 
 * setting the methodToBeInvoked variable in the command through CommandWritableBuilder's instance.
 */
public interface RemotingMethodConstants {

	/** The get job history file from job id. */
	String GET_JOB_HISTORY_FILE_FROM_JOB_ID="getJobHistoryFilefromJobID";
	
	/** The process get files. */
	String PROCESS_GET_FILES="processGetFiles";
	
	/** The process db opt steps. */
	String PROCESS_DB_OPT_STEPS ="processDBOptSteps";
	
	/** The get hadoop config file from jobid. */
	String GET_HADOOP_CONFIG_FILE_FROM_JOBID="getHadoopConfigFilefromJobID";
	
	/** The convert host name to ip. */
	String CONVERT_HOST_NAME_TO_IP="convertHostNameToIP";
	
	/** The send jumbune jmx agent to all nodes. */
	String SEND_JUMBUNE_JMX_AGENT_TO_ALL_NODES = "sendJumbuneJmxServerToAllNodes";
	
	/** The process awk command to get max used memory. */
	String PROCESS_AWK_COMMAND_TO_GET_MAX_USED_MEMORY = "getMaxUtilisationMemoryFromAwkCommand";

	String EXECUTE_REMOTE_COMMAND_AS_SUDO = "executeRemoteCommandAsSudo";

	String ESTABLISH_CONN_TO_JMX_AGENTS = "establishConnToJMXAgents";
	
	String GET_DAEMON_PROCESS_ID = "getDaemonProcessId";
	
	String SHUT_DOWN_JMX_AGENTS = "shutDownJMXAgents";
	
	String GET_HADOOP_CLUSTER_TIME_MILLIS = "getHadoopClusterTimeMillis";

}
