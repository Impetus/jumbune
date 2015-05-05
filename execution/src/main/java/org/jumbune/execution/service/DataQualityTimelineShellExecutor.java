package org.jumbune.execution.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Classpath;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.ReportsBean;
import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.ConsoleLogUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.execution.processor.Processor;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.common.utils.Constants;

import com.jcraft.jsch.JSchException;

public class DataQualityTimelineShellExecutor extends CoreExecutorService {
	private static final String TEMP = "tmp";
	private static final String SCHEDULED_JSON_FILE = "scheduledJson.json";
	private static final String USER_DATE_PATTERN = "HH:mm:SS MM/dd/yyyy";
	private static final Logger LOGGER = LogManager
			.getLogger(DataQualityTimelineShellExecutor.class);

	public DataQualityTimelineShellExecutor() throws JumbuneException {
	}

	private InputStream readFilePath(String filePath) throws JumbuneException,
			FileNotFoundException {
		if (new File(filePath).exists()) {
			return readFile(filePath);
		}
		throw new JumbuneException(
				ErrorCodesAndMessages.MESSAGE_FILE_PATH_FORMAT_NOT_CORRECT);
	}

	private InputStream readFile(String filePath) throws JumbuneException {
		File file = new File(filePath);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOGGER.error("Job json file not found.", e);
		}
		return inputStream;
	}

	public static void main(String[] args) throws JumbuneException,
			InterruptedException {
		DataQualityTimelineShellExecutor service = new DataQualityTimelineShellExecutor();
		InputStream jsonFileStream = null;
		try {
			ReportsBean reports = new ReportsBean();
			String jumbunehome = args[1];
			LOGGER.debug("Jumbune Home [" + jumbunehome + "]");
			File file = new File(new StringBuilder().append(jumbunehome)
					.append(File.separator).append(TEMP)
					.append(File.separator).append("DQScheduler.token")
					.toString());
			if(!file.getParentFile().exists() && file.getParentFile().mkdirs());
			if (file.exists()) {
				ConsoleLogUtil.CONSOLELOGGER
						.info("Other scheuled Data Quality job is running, could not execute this iteration !!!!!");
				ConsoleLogUtil.CONSOLELOGGER
						.info("Shutting down DataQuality job !!!!!");
				System.exit(1);
			} else {
				file.createNewFile();
			}
			String scheduledJobDirectoryPath = args[0];

			System.setProperty("JUMBUNE_HOME", jumbunehome);
			JobConfig.setJumbuneHome(args[1]);
			scheduledJobDirectoryPath = scheduledJobDirectoryPath.substring(0,
					scheduledJobDirectoryPath.lastIndexOf(File.separator));

			scheduledJobDirectoryPath = scheduledJobDirectoryPath
					+ File.separator + SCHEDULED_JSON_FILE;

			jsonFileStream = service.readFilePath(scheduledJobDirectoryPath);

			service.run(jsonFileStream, reports);

			try {
				if (jsonFileStream != null) {
					jsonFileStream.close();
				}
			} catch (IOException io) {
				LOGGER.error("Unable to close stream" + io.getMessage());
			}

			System.exit(1);
		} catch (Exception e) {
			
			LOGGER.error(e);
		} finally {
			try {
				if (jsonFileStream != null) {
					jsonFileStream.close();
				}
			} catch (IOException io) {
				LOGGER.error("Unable to close stream" + io.getMessage());
			}
		}
	}

	private Config run(InputStream is, ReportsBean reports)
			throws JumbuneException, IOException, JSchException,
			InterruptedException {
		JobConfig jobConfig = null;
		try{
			jobConfig = JobConfigUtil.jobConfig(is);
		JobConfigUtil jobConfigUtil = new JobConfigUtil(jobConfig);
		loadInitialSetup(jobConfig);
		disableModules(jobConfig);
		jobConfigUtil.createJumbuneDirectories();
		createJobJarFolderOnAgent(jobConfig);
		startExecution(reports, jobConfig);
		}finally{
			try {
				LOGGER.debug("Cleaning up agent and slaves temporary directories");
				cleanUpJumbuneAgentCurrentJobFolder(jobConfig);
				cleanUpSlavesTempFldr(jobConfig);
				deleteTokenFile();
			} catch (Exception e) {
				LOGGER.error("Exception occurred in clean up slaves tmp folder ", e);
			}
			ConsoleLogUtil.CONSOLELOGGER.debug("clean up done");
			
		}
		
		return jobConfig;
	}

	private void deleteTokenFile() {
		String jumbunehome = JobConfig.getJumbuneHome();
		File file = new File(new StringBuilder().append(jumbunehome)
				.append(File.separator).append(TEMP).append(File.separator)
				.append("DQScheduler.token").toString());
		LOGGER.debug("token file has been deleted status [" + file.delete()
				+ "]");

	}

	private void startExecution(ReportsBean reports, Config config)
			throws IOException, JumbuneException {
		JobConfig jobConfig = (JobConfig) config;
		List<Processor> processors = getProcessorChain(
				jobConfig, true);
		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setRootDirectory(Constants.JOB_JARS_LOC);
		serviceInfo.setJumbuneHome(JobConfig.getJumbuneHome());
		serviceInfo.setSlaveJumbuneHome(jobConfig.getSlaveWorkingDirectory());
		serviceInfo.setJumbuneJobName(jobConfig.getFormattedJumbuneJobName());
		serviceInfo.setMaster(jobConfig.getMaster());
		serviceInfo.setSlaves(jobConfig.getSlaves());

		HELPER.writetoServiceFile(serviceInfo);
		int index = 0;
		for (Processor p : processors) {
			Map<Parameters, String> params = new HashMap<Parameters, String>();
			String processName = "PROCESS" + ++index;
			reports.addInitialStatus(processName);
			params.put(Parameters.PROCESSOR_KEY, processName);
			try {
				p.process(config, reports, params);
			} catch (JumbuneException e) {
				LOGGER.error(processName + " completed with errors !!!", e);
			} finally {
				reports.markProcessAsComplete(processName);
				try{
					cleanUpJumbuneAgentCurrentJobFolder(jobConfig);
					cleanUpSlavesTempFldr(jobConfig);
					deleteTokenFile();
				}catch(InterruptedException e){
					LOGGER.error("Intererupted slave clean up ", e);
				}finally{
					LOGGER.info("clean up to all slaves has been completed");
				}
			}
		}
		ConsoleLogUtil.CONSOLELOGGER
				.info("!!! Data Quality timeline process has been finished succesfully !!!\n ");

	}

	private void disableModules(Config config) {
		JobConfig jobConfig = (JobConfig)config;
		jobConfig.setEnableStaticJobProfiling(Enable.FALSE);
		jobConfig.setHadoopJobProfile(Enable.FALSE);
		jobConfig.setEnableDataProfiling(Enable.FALSE);
	}

	private void loadInitialSetup(Config config) throws JumbuneException {
		String agentHome = RemotingUtil.getAgentHome(config);
		ClasspathElement cse = ConfigurationUtil.loadJumbuneSuppliedJarList();
		processClassPathElement(cse, agentHome);
		JobConfig jobConfig = (JobConfig) config;
		if (jobConfig.getClasspath() == null) {
			jobConfig.setClasspath(new Classpath());
		}
		jobConfig.getClasspath().setJumbuneSupplied(cse);
		if (!JobConfigUtil.isJumbuneSuppliedJarPresent(config)) {
			JobConfigUtil
					.sendJumbuneSuppliedJarOnAgent(config, cse, agentHome);
		}

		DateFormat dateFormat = new SimpleDateFormat(USER_DATE_PATTERN);
		Date date = new Date();
		jobConfig.getDataQualityTimeLineConfig().setTime(dateFormat.format(date));
	}

	private void processClassPathElement(ClasspathElement cse, String agentHome) {
		String[] files = cse.getFiles();
		for (int iIndex = 0; iIndex < files.length; iIndex++) {
			files[iIndex] = files[iIndex].replace("AGENT_HOME", agentHome);
		}
	}

}
