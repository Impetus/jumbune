package org.jumbune.common.scheduler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.utils.exception.JumbuneException;

/**
 * This class is used for creating new schedulers. This class provides various
 * methods which is very useful while doing scheduling eg. reading Cron file,
 * writing in Cron file and deleting a Cron entry.
 * 
 */
public abstract class Scheduler {

	private static final String CRONTAB_COMMAND = "crontab";
	private static final String CRONTAB_L_OPTION = "-l";
	private static final String NEW_LINE = "\n";
	private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);
	private static final File CURRENT_DIR = new File(".");
	private static final String JUMBUNE_HOME;

	static {
		JUMBUNE_HOME = (new StringBuilder()).append(JobConfig.getJumbuneHome())
				.append(File.separator).toString();
	}

	public Scheduler() {
	}

	/**
	 * This is main scheduling method of any scheduler and it should contain
	 * complete processing logic for scheduling.This method triggers or provide
	 * flow for scheduling a task. Orchestration of schedule DataQuality
	 * time line job, starts with creating directory to persist result followed
	 * by Cron expression generation, writing in Cron file and persisting meta
	 * info of Jumbune job.
	 * 
	 * @throws JumbuneException
	 */
	public abstract void scheduleJob(Config config) throws JumbuneException;

	/**
	 * This method removes a cronjob entry in cronfile identified by Jumbune
	 * jobname.
	 * 
	 * @param jobName
	 *            scheduled jumbune job name which user wants to stop
	 * @throws JumbuneException
	 */
	public void deleteCurrentJobEntryFromCron(String jobName)
			throws JumbuneException {
		InputStream cronInputStream;
		cronInputStream = getCurrentUserCronTabInfo();
		BufferedReader br = null;
		try {
			if (cronInputStream != null && cronInputStream.available() != 0) {
				StringBuilder cronBuilder = new StringBuilder();
				InputStreamReader isReader = new InputStreamReader(
						cronInputStream);
				br = new BufferedReader(isReader);
				do {
					String textinLine = br.readLine();
					if (textinLine == null)
						break;
					if (!textinLine.contains(jobName+File.separator)){
						cronBuilder.append(textinLine);
						cronBuilder.append("\n");
					}
				} while (true);
				writeLatestCronTabToFile(cronBuilder.toString());
				addUpdatedFileToCron();
			}
		} catch (IOException ioe) {
			LOGGER.warn(
					" Could not update crontab to remove entry of current job",
					ioe);
			throw new JumbuneException(ioe.getMessage());
		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LOGGER.error("could not close the cron file input stream ",
							e);
					throw new JumbuneException(e.getMessage());
				}
			}
		}
	}

	/**
	 * It will return the input stream of crontab file of the particular user.
	 *
	 * @return - input stream of crontab file
	 * @throws JumbuneException
	 *             - if could not read crontab file
	 */
	protected InputStream getCurrentUserCronTabInfo() {
		Process process = null;
		List<String> commandList = new ArrayList<String>();
		commandList.add(CRONTAB_COMMAND);
		commandList.add(CRONTAB_L_OPTION);

		ProcessBuilder processLauncher = new ProcessBuilder();
		processLauncher.directory(CURRENT_DIR);
		processLauncher.command(commandList);
		try {
			process = processLauncher.start();
			process.waitFor();
			if (process != null) {
				return process.getInputStream();
			}
		}catch (InterruptedException e){
			LOGGER.error(
					"Unable to wait for command response  ", e);
		}
		catch (IOException e) {
			LOGGER.error(
					"Could not read original crontab file for modifying it ", e);
		}
		return null;
	}

	/**
	 * It writes the updated cronInfo to a given file.
	 *
	 * @param cronInfo
	 *            - cron info to be written on file
	 */
	protected void writeLatestCronTabToFile(String cronInfo) {
		File jumbuneCronFile = new File((new StringBuilder())
				.append(JUMBUNE_HOME).append("JumbuneModifiedCron").toString());
		BufferedWriter outobj = null;
		try {
			FileWriter fstream = new FileWriter(jumbuneCronFile);
			outobj = new BufferedWriter(fstream);
			outobj.write(cronInfo);
			outobj.close();
		} catch (IOException e) {
			LOGGER.error("Error while write info to file ", e);
			try {
				if (outobj != null)
					outobj.close();
			} catch (IOException ioe) {
				LOGGER.error("Unable to close the writable object connection ",
						e);
			}
		}
	}

	/**
	 * This method will set the recently changed file to cron using the command
	 * crontab <filePath>.
	 */
	protected void addUpdatedFileToCron() {
		List<String> commandList = new ArrayList<String>();
		commandList.add(CRONTAB_COMMAND);
		commandList.add((new StringBuilder()).append(JUMBUNE_HOME)
				.append("JumbuneModifiedCron").toString());

		ProcessBuilder launch = new ProcessBuilder();
		launch.directory(CURRENT_DIR);
		launch.command(commandList);
		try {
			launch.start();
		} catch (IOException e) {
			LOGGER.error("Exception while updating cron file ", e);
		}
	}

	/**
	 * Converts the inputstream to String. If the input stream is null then it
	 * will return default string for crontab file
	 * 
	 * @param cronInputStream
	 *            - cron tab input stream
	 * @return - string which holds crontab file content
	 * @throws IOException
	 *             - if unable to read/close the cronInputStream
	 */
	protected String readCronTabFile(InputStream cronInputStream)
			throws IOException {
		final StringBuilder cronFileBasicInfo = new StringBuilder("#!/bin/bash")
				.append(NEW_LINE)
				.append("SHELL=/bin/sh")
				.append(NEW_LINE)
				.append("PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin")
				.append(NEW_LINE).append("# m h dom mon dow user	command")
				.append(NEW_LINE);

		if (cronInputStream != null) {
				InputStreamReader isReader = new InputStreamReader(
						cronInputStream);
				BufferedReader br = new BufferedReader(isReader);

				StringBuilder cronBuilder = new StringBuilder();
				String textinLine;
				while (true) {
					textinLine = br.readLine();

					if (textinLine == null) {
						break;
					}

					cronBuilder.append(textinLine);
					cronBuilder.append(NEW_LINE);
				}
				return cronBuilder.toString();
		}
		return cronFileBasicInfo.toString();
	}

	/**
	 * It copies the execution script file to destination folder.
	 * 
	 * @param scriptPath
	 *            script path which user wants to copy
	 * @param scheduleJobScriptDestLoc
	 *            the schedule job script dest loc
	 * @throws IOException
	 *             - if unable to write the script file to destination folder
	 * @throws JumbuneException
	 *             - if unable to copy the read the execution script from base
	 *             location
	 */
	protected void copyExecutionScriptToDestFolder(String scriptPath,
			String scheduleJobScriptDestLoc) throws IOException,
			JumbuneException {
		// Copy the scheduling execute script in this folder
		InputStream schedulingScriptIS = new FileInputStream(new File(
				(String) (scriptPath)));

		InputStreamReader isReader = new InputStreamReader(schedulingScriptIS);
		BufferedReader br = new BufferedReader(isReader);
		StringBuilder schedulerScripBuilder = new StringBuilder();
		try {
			String textinLine;
			while (true) {
				try {
					textinLine = br.readLine();

					if (textinLine == null) {
						break;
					}
					schedulerScripBuilder.append(textinLine);
					schedulerScripBuilder.append(NEW_LINE);
				} catch (IOException e1) {
					LOGGER.error(
							"Unable to read executionScript file from source ",
							e1);
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		String scriptWord = schedulerScripBuilder.toString().replaceAll(
				"<JUMBUNE.HOME>", JobConfig.getJumbuneHome());
		String javaHome = System.getenv("JAVA_HOME");
		LOGGER.info("java home " + javaHome);
		scriptWord = scriptWord.replaceAll("<JAVA.HOME>", javaHome);

		ConfigurationUtil.writeToFile(scheduleJobScriptDestLoc, scriptWord,
				true);
		File scheduledJobScript = new File(scheduleJobScriptDestLoc);
		scheduledJobScript.setExecutable(true);
	}
}