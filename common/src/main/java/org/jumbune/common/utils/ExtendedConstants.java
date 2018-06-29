package org.jumbune.common.utils;

import org.jumbune.common.utils.Constants;


/**
 * The Interface ExtendedConstants.
 */
public interface ExtendedConstants extends Constants{

	/** key for self tuning information. */
	String SELF_TUNING_JOB = "Self Tuning Job";

	/** key for scheduling information. */
	String SCHEDULING = "Scheduling Job";

	/** The scheduled status file. */
	String SCHEDULED_STATUS_FILE = "status";

	/** The scheduled status completed. */
	String SCHEDULED_STATUS_COMPLETED = "Completed";

	/** The scheduled status scheduled. */
	String SCHEDULED_STATUS_SCHEDULED = "Scheduled";

	/** On master the scheduled jobs related data is kept. */
	String SCHEDULED_JOB_LOC = "scheduledJobs";

	/**
	 * On master the user scheduled jobs related data is kept. Based on the
	 * folder name it would be decided if the job is to be removed from cron tab
	 * file once executed
	 */
	String USER_SCHEDULED_JOB_LOC = "/userScheduled";

	/**
	 * On master the re-attempt scheduled jobs related data is kept. Based on
	 * the folder name it would be decided if the job is to be removed from
	 * crontab file once executed
	 */
	String REATTEMPT_SCHEDULED_JOB_LOC = "/re-attempt";

	/** On master location where the script to execute schedule job is kept. */
	String SCHEDULING_SCRIPT_LOC = "bin/";

	/** On master name of script to execute schedule jobs. */
	String SCHEDULING_SCRIPT_FILE = "runScheduler.sh";

	/** On master name of folder which holds results of scheduled jobs. */
	String SCHEDULING_REPORT_FOLDER = "/reports/";

	/** Contains the status of scheduled job. */
	String SCHEDULED_JOB_STATUS_FILE = "/status";

	/**
	 * When a scheduled job folder is created the corresponding job's json is
	 * also kept. The name of the json is
	 */
	String SCHEDULE_TASK_JOB_FILE = "ScheduledJobProperties.json";

	/** The modified cron file. */
	String MODIFIED_CRON_FILE = "JumbuneModifiedCron";
	
	/** The SEL f_ tunin g_3 d. */
	String SELF_TUNING_3D = "SELF_TUNING_3D";
	
	/** The self tuning. */
	String SELF_TUNING = "SELF_TUNING";
	
	/** The quick tuning. */
	String QUICK_TUNING = "QUICK_TUNING";
	
	/** The alert configuration. */
	String ALERT_CONFIGURATION = "alertconfiguration.xml";
	
	/** The Constant CRITICAL_LEVEL. */
	String CRITICAL_LEVEL = "Critical";
	
	/** The Constant WARNING_LEVEL. */
	String WARNING_LEVEL = "Warning";
	
	/** The Constant COLON. */
	String COLON = ":"; 
	
	/** The Constant DEFAULT_RM_IPC_PORT. */
	String DEFAULT_RM_IPC_PORT = "8032";
	
	/** The Constant DEFAULT_MR_IPC_PORT. */
	String DEFAULT_MR_IPC_PORT = "10020";
	
	/** The Constant CONFIGURATION. */
	String CONFIGURATION = "conf";
	
	/** The Constant NAME. */
	String NAME ="name";
	
	/** The Constant VALUE. */
	String VALUE ="value";
	
	/** The Constant PROPERTY. */
	String PROPERTY ="property";
	
	/** The Constant ROOT. */
	String ROOT = "root";
	
	/** The queue limit. */
	String QUEUE_LIMIT = "exceeded threshold limit";
	
	/** The Constant YARN_SITE_XML. */
	String YARN_SITE_XML =	"yarn-site.xml";
	
	/** The Constant MAPRED_SITE_XML. */
	String MAPRED_SITE_XML = "mapred-site.xml";
	
	/** The Constant YARN_MINIMUM_VCORE. */
	String YARN_CONTAINER_MINIMUM_VCORE = "yarn.scheduler.minimum-allocation-vcores";
	
	/** The Constant YARN_CONTAINER_MINIMUM_MEMORY. */
	String YARN_CONTAINER_MINIMUM_MEMORY = "yarn.scheduler.minimum-allocation-mb";
	
	/** The time format. */
	String TIME_FORMAT = "dd MMM YYYY HH:mm:ss";
	
	/** The Constant RM_ADDRESS_KEY. */
	String RM_ADDRESS_KEY = "yarn.resourcemanager.address";

	/** The Constant HISTORY_SERVER_ADDRESS_KEY. */
	String HISTORY_SERVER_ADDRESS_KEY = "mapreduce.jobhistory.address";
	
	/**  The Constant QUEUE. */
	String QUEUE = "_queue";
	
	/**  The Constant Alert Name ALL. */
	String ALL = "ALL";
	
	/**  The Constant -. */
	String HYPHEN = "-";
	
	/** The vm swappiness standard val. */
	int VM_SWAPPINESS_STANDARD_VAL = 10;
}
