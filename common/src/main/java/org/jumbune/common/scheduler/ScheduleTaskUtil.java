package org.jumbune.common.scheduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.JobStatus;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ExtendedConfigurationUtil;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.Gson;


/**
 * This class will invoke JumbuneScheduler which will schedule various kinds of jobs like entire flow of tasks or sqoop jobs or data validation jobs.
 */
public class ScheduleTaskUtil {
	
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(ScheduleTaskUtil.class);

	/** The Constant SPACE. */
	private static final String SPACE = " ";
	
	/** The Constant NEW_LINE. */
	private static final String NEW_LINE = "\n";
	
	/** The Constant FILE_SEPARTOR. */
	private static final String FILE_SEPARTOR = System.getProperty("file.separator");
	
	/** The Constant CRONTAB_COMMAND. */
	private static final String CRONTAB_COMMAND = "crontab";
	
	/** The Constant CRONTAB_L_OPTION. */
	private static final String CRONTAB_L_OPTION = "-l";
	
	/** The Constant CURRENT_DIR. */
	private static final File CURRENT_DIR = new File(".");



	
	/**
	 * It schedules jumbune task and also copies resources like execution script and desired json file in destination folder.
	 *
	 * @param scheduleJobName the schedule job name
	 * @param scheduleJobDateTime - time of scheduling jumbune task
	 * @param jobConfigInputStream - input stream of JobConfig
	 * @param isReAttempt the is re attempt
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JumbuneException the hTF exception
	 */
	public void scheduleJumbuneTaskAndCopyResources(String scheduleJobName, String scheduleJobDateTime, InputStream jobConfigInputStream,
			boolean isReAttempt) throws IOException, JumbuneException {
		StringBuilder scheduleJobLoc = getScheduleJobLoc(scheduleJobName, isReAttempt);

		String jsonFileName = ExtendedConfigurationUtil.getScheduleJobJsonFileLoc(scheduleJobLoc.toString());

		scheduleJumbuneTask(scheduleJobDateTime, scheduleJobLoc);

		// Copy the command file and respective json to scheduledJobs folder...
		copyJsonFileToScheduleJobsLoc(jsonFileName, jobConfigInputStream);
	}

	/**
	 * It schedules jumbune task and also copies resources like execution script and desired yaml file in destination folder.
	 *
	 * @param Config the config
	 * @param isReAttempt the is re attempt
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JumbuneException the hTF exception
	 */
	public void scheduleJumbuneTaskAndCopyResources(JobConfig jobConfig, boolean isReAttempt) throws IOException, JumbuneException {
		String scheduleJobDateTime = jobConfig.getJumbuneScheduleTaskTiming();
		String scheduleJobName = jobConfig.getJumbuneJobName();

		StringBuilder scheduleJobLoc = getScheduleJobLoc(scheduleJobName, isReAttempt);

		updateStatusFile(scheduleJobLoc.toString(), JobStatus.SCHEDULED.toString());
		
		//Copy scheduled job jar and resources if at least one of following modules is enabled: flow debugging, tuning.
		//Skip this for HDFS validation. 
		if(Enable.TRUE.equals(jobConfig.getDebugAnalysis())){
			copyScheduledJobJarAndResources(jobConfig, scheduleJobLoc.toString());
		}
		String jsonFileName = ExtendedConfigurationUtil.getScheduleJobJsonFileLoc(scheduleJobLoc.toString());

		scheduleJumbuneTask(scheduleJobDateTime, scheduleJobLoc);

		// Copy the command file and respective json to scheduledJobs folder...
		copyJsonFileToScheduleJobsLoc(jsonFileName, jobConfig);
	}

	/**
	 * Update status file.
	 *
	 * @param currentJobLoc the current job loc
	 * @param message the message
	 */
	private void updateStatusFile(String currentJobLoc, String message) {
		String filePath = currentJobLoc + File.separator + ExtendedConstants.SCHEDULED_STATUS_FILE;

		try {
			ConfigurationUtil.writeToFile(filePath, message, true);
		} catch (IOException e) {
			LOG.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
		}
	}

	/**
	 * It copies the jar to a given location and updates location in yamlConfig.
	 *
	 * @param jobConfig the config
	 * @param scheduleJobLoc the schedule job loc
	 * @throws JumbuneException the hTF exception
	 */
	private void copyScheduledJobJarAndResources(JobConfig jobConfig, String scheduleJobLoc) {
		try {
			if(null==jobConfig.getInputFile()){
				return;
			}
			ConfigurationUtil.copyFileToDestinationLocation(jobConfig.getInputFile(), scheduleJobLoc);
			String inputFile = jobConfig.getInputFile();
			String fileName = inputFile.substring(inputFile.lastIndexOf(File.separator));

			String updatedFileLoc = scheduleJobLoc + fileName;
			LOG.debug("updated file location  " + updatedFileLoc);
			jobConfig.setInputFile(updatedFileLoc);

		} catch (IOException ie) {
			LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(ie.getStackTrace()));
		}
	}

	/**
	 * It prepares the location of schedule job i.e, where the schedule Job would execute and its reports would be saved
	 *
	 * @param scheduleJobName the schedule job name
	 * @param isReAttempt the is re attempt
	 * @return the schedule job loc
	 */
	private StringBuilder getScheduleJobLoc(String scheduleJobName, boolean isReAttempt) {
		StringBuilder scheduleJobLoc = new StringBuilder();

		if (isReAttempt) {
			scheduleJobLoc.append(ExtendedConfigurationUtil.getReAttemptScheduleJobLocation());
		} else {
			scheduleJobLoc.append(ExtendedConfigurationUtil.getUserScheduleJobLocation());
		}

		if (!scheduleJobName.startsWith(FILE_SEPARTOR)){
			scheduleJobLoc.append(FILE_SEPARTOR);
			}

		scheduleJobLoc.append(scheduleJobName.trim());

		return scheduleJobLoc;

	}

	/**
	 * It performs various steps for scheduling a jumbune task like reading the existing crontab file modifying this file to append a jumbune task and
	 * then copying the execution script file to d destination folder.
	 *
	 * @param scheduledJobDateTime the scheduled job date time
	 * @param scheduleJobLoc the schedule job loc
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JumbuneException the hTF exception
	 */
	private void scheduleJumbuneTask(String scheduledJobDateTime, StringBuilder scheduleJobLoc) throws IOException {
		InputStream cronInputStream = getCurrentUserCronTabInfo();

		StringBuilder updatedCronInfoBuilder = new StringBuilder(readCronTabFile(cronInputStream));

		String schedulingScriptDestPath = scheduleJobLoc.append(FILE_SEPARTOR).append(ExtendedConstants.SCHEDULING_SCRIPT_FILE).toString();
		updatedCronInfoBuilder.append(getJumbuneScheduleTaskInfo(scheduledJobDateTime, schedulingScriptDestPath));

		writeLatestCronTabToFile(updatedCronInfoBuilder.toString());

		addUpdatedFileToCron();
		copyExecutionScriptToDestFolder(schedulingScriptDestPath);
	}

	

	/**
	 * It copies the execution script file to destination folder.
	 *
	 * @param scheduleJobScriptDestLoc the schedule job script dest loc
	 * @throws IOException - if unable to write the script file to destination folder
	 * @throws JumbuneException - if unable to copy the read the execution script from base location
	 */
	private void copyExecutionScriptToDestFolder(String scheduleJobScriptDestLoc) throws IOException {
		// Copy the scheduling execute script in this folder
		InputStream schedulingScriptIs = ExtendedConfigurationUtil.getSchedulingScriptInputStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(schedulingScriptIs));

		StringBuilder schedulerScripBuilder = new StringBuilder();
		String textinLine;
		while (true) {
			try {
				textinLine = br.readLine();

				if (textinLine == null){
					break;
				}
				
				schedulerScripBuilder.append(textinLine);
				schedulerScripBuilder.append(NEW_LINE);
			} catch (IOException e1) {
				LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e1.getStackTrace()));
			}
		}
		textinLine = new String(schedulerScripBuilder);
		textinLine = textinLine.replaceAll("<JUMBUNE.HOME>", System.getenv(Constants.JUMBUNE_ENV_VAR_NAME));
		textinLine = textinLine.replaceAll("<JAVA.HOME>", System.getenv("JAVA_HOME"));
		
		LOG.debug("Copying script file to destination folder");
		ConfigurationUtil.writeToFile(scheduleJobScriptDestLoc, textinLine, true);
		LOG.debug("Successfully copied script file to destination folder");
		File scheduledJobScript = new File(scheduleJobScriptDestLoc);
		scheduledJobScript.setExecutable(true);
	}

	/**
	 * It copies the user's json file to schedulerJobs/<currentJob>/ location. So that this file could be read when executing the scheduled job
	 *
	 * @param jsonFileDestLoc - destination location where the file should be copied
	 * @param inputStream - input stream of the user's json file
	 * @throws JumbuneException the hTF exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void copyJsonFileToScheduleJobsLoc(String jsonFileDestLoc, InputStream inputStream) throws IOException {
		LOG.debug("Copying json file to destination folder " + jsonFileDestLoc);
		File userJsonFile = new File(jsonFileDestLoc);
		OutputStream out = null;
		try {
			if (!userJsonFile.exists()) {
				userJsonFile.mkdirs();
			}

			out = new FileOutputStream(userJsonFile);
			byte buf[] = new byte[Constants.ONE_ZERO_TWO_FOUR];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

		} catch (FileNotFoundException e) {
			LOG.error(JumbuneRuntimeException.throwFileNotLoadedException(e.getStackTrace()));
		} catch (IOException e) {
			LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
		} finally {
			if (out != null) {
				out.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}

	}

	private void copyJsonFileToScheduleJobsLoc(String jsonFileDestLoc, Config config) throws IOException {
		LOG.debug("Copying json file to destination folder " + jsonFileDestLoc);
		// Write jobConfig to file
		Gson gson = new Gson();
		ConfigurationUtil.writeToFile(jsonFileDestLoc, gson.toJson(config));
	}
	
	
	 
	
	/**
	 * It will return the input stream of crontab file of the particular user.
	 *
	 * @return - input stream of crontab file
	 * @throws JumbuneException - if could not read crontab file
	 */
	private InputStream getCurrentUserCronTabInfo() {
		Process process = null;
		List<String> commandList = new ArrayList<String>();
		commandList.add(CRONTAB_COMMAND);
		commandList.add(CRONTAB_L_OPTION);

		ProcessBuilder processLauncher = new ProcessBuilder();
		processLauncher.directory(CURRENT_DIR);
		processLauncher.command(commandList);

		try {
			process = processLauncher.start();
			if (process != null) {
				return process.getInputStream();
			}
		} catch (IOException e) {
			LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
		}
		return null;
	}

	/**
	 * Converts the inputstream to String. If the input stream is null then it will return default string for crontab file
	 * 
	 * @param cronInputStream
	 *            - cron tab input stream
	 * @return - string which holds crontab file content
	 * @throws IOException
	 *             - if unable to read/close the cronInputStream
	 */
	private String readCronTabFile(InputStream cronInputStream) throws IOException {
		final StringBuilder cronFileBasicInfo = new StringBuilder("#!/bin/bash").append(NEW_LINE).append("SHELL=/bin/sh").append(NEW_LINE)
				.append("PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin").append(NEW_LINE)
				.append("# m h dom mon dow user	command").append(NEW_LINE);

		if (cronInputStream != null) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(cronInputStream));

				StringBuilder cronBuilder = new StringBuilder();
				String textinLine;
				while (true) {
					textinLine = br.readLine();

					if (textinLine == null){
						break;}

					cronBuilder.append(textinLine);
					cronBuilder.append(NEW_LINE);
				}
				return cronBuilder.toString();

			} finally {
				cronInputStream.close();
			}
		}
		return cronFileBasicInfo.toString();
	}

	/**
	 * Creates a jumbuneTask scheduling info, appends the scheduleJob date time along with command.
	 *
	 * @param scheduledJobDateTime the scheduled job date time
	 * @param jumbuneScheduleTaskCommand the jumbune schedule task command
	 * @return - return string which can be placed in crontab file for scheduling a job
	 */
	private String getJumbuneScheduleTaskInfo(String scheduledJobDateTime, String jumbuneScheduleTaskCommand) {
	StringBuilder jumbuneSchedulerdBuilder = new StringBuilder(scheduledJobDateTime).append(SPACE).append(jumbuneScheduleTaskCommand)
				.append(NEW_LINE).append("#");

		LOG.debug("jumbuneTaskCommand " + jumbuneSchedulerdBuilder.toString());
		return jumbuneSchedulerdBuilder.toString();
	}

	/**
	 * It writes the updated cronInfo to a given file.
	 *
	 * @param cronInfo - cron info to be written on file
	 * @throws JumbuneException the hTF exception
	 */
	private void writeLatestCronTabToFile(String cronInfo) {
		File jumbuneCronFile = new File(ExtendedConfigurationUtil.getJumbuneModifiedCronFilePath());
		try {
			BufferedWriter bw = new BufferedWriter( new FileWriter(jumbuneCronFile));
			bw.write(cronInfo);
			bw.close();
		} catch (IOException e) {
			LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			}
	}

	/**
	 * This method will set the recently changed file to cron using the command crontab <filePath>.
	 *
	 * @throws JumbuneException the hTF exception
	 */
	private void addUpdatedFileToCron() {
		List<String> commandList1 = new ArrayList<String>();
		commandList1.add(CRONTAB_COMMAND);
		commandList1.add(ExtendedConfigurationUtil.getJumbuneModifiedCronFilePath());

		ProcessBuilder launch = new ProcessBuilder();
		launch.directory(CURRENT_DIR);
		launch.command(commandList1);

		try {
			launch.start();
		} catch (IOException e) {
			LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
		}
	}
	
	/**
	 * Delete current job entry from cron.
	 *
	 * @param currentJobLoc the current job loc
	 * @throws JumbuneException the hTF exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void deleteCurrentJobEntryFromCron(String currentJobLoc) throws IOException {
		
		InputStream cronInputStream = getCurrentUserCronTabInfo();
		
		if (cronInputStream != null && cronInputStream.available() != 0) {
			InputStreamReader isReader = new InputStreamReader(cronInputStream);
			BufferedReader br = new BufferedReader(isReader);
			StringBuilder cronBuilder = new StringBuilder();
			String textinLine;
			while (true) {
				try {
					textinLine = br.readLine();
					if (textinLine == null){
						break;
					}
					if (!textinLine.contains(currentJobLoc)) {
						cronBuilder.append(textinLine);
					}

					cronBuilder.append(NEW_LINE);
				} catch (IOException e1) {
					LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e1.getStackTrace()));
				}
			}
			cronInputStream.close();
			
			writeLatestCronTabToFile(cronBuilder.toString());
			addUpdatedFileToCron();
		}
	}

	
}