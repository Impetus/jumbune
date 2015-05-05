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

}
