package org.jumbune.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jumbune.common.beans.JumbuneInfo;

public class ExtendedConfigurationUtil extends ConfigurationUtil {

	private static final String PATH_SEPARATOR = System
			.getProperty("file.separator");

	/**
	 * It returns the complete path of scheduled job folder containing json
	 * file.
	 * 
	 * @param scheduledJobLoc
	 *            - Folder location where the scheduled job json should be
	 *            created
	 * @return - string specifying absolute path of scheduled job json
	 */
	public static String getScheduleJobJsonFileLoc(String scheduledJobLoc) {
		StringBuilder scheduleJobPath = new StringBuilder(scheduledJobLoc)
				.append(PATH_SEPARATOR).append(
						ExtendedConstants.SCHEDULE_TASK_JOB_FILE);

		return scheduleJobPath.toString();
	}

	/**
	 * This method gets the location of where to save or get scheduledJobs information.
	 *
	 * @return - String specifying path where scheduled job related data is kept
	 */
	public static String getScheduleJobLocation() {
		return JumbuneInfo.getHome() + ExtendedConstants.SCHEDULED_JOB_LOC;
	}
	
	/**
	 * This method gets the location of where to save or get user scheduledJobs information.
	 *
	 * @return - String specifying path where scheduled job related data is kept
	 */
	public static String getUserScheduleJobLocation() {
		return getScheduleJobLocation() + ExtendedConstants.USER_SCHEDULED_JOB_LOC;
	}

	/**
	 * This method gets the location of where to save or get re-attempt scheduledJobs information.
	 *
	 * @return - String specifying path where scheduled job related data is kept
	 */
	public static String getReAttemptScheduleJobLocation() {
		return getScheduleJobLocation() + ExtendedConstants.REATTEMPT_SCHEDULED_JOB_LOC;
	}
	
	/**
	 * This method gives the location of script file to execute scheduled jobs.
	 *
	 * @return - string location of scheduling job execution script
	 */
	public static String getSchedulingScriptLoc() {
		return JumbuneInfo.getHome() + ExtendedConstants.SCHEDULING_SCRIPT_LOC;
	}

	/**
	 * This method will read the scheduling script file and return its input stream.
	 *
	 * @return Input stream of script file to execute schedule jobs
	 * @throws FileNotFoundException - if unable to find scheduling script
	 */
	public static InputStream getSchedulingScriptInputStream() throws FileNotFoundException {
		String schedulingScriptFilePath = getSchedulingScriptLoc() + ExtendedConstants.SCHEDULING_SCRIPT_FILE;
		return  new FileInputStream(new File(schedulingScriptFilePath));
		
	}

	/**
	 * Updated upstream For setting new entry in crontab. Command takes in input a file path which has information about new cron jobs. This input
	 * file path is returned by this method
	 * 
	 * @return - return path of file to be written and then set in crontab
	 */
	public static String getJumbuneModifiedCronFilePath() {
		return JumbuneInfo.getHome() + ExtendedConstants.MODIFIED_CRON_FILE;
	}

}
