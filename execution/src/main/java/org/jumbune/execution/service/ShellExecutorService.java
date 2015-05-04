package org.jumbune.execution.service;

import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_VALID_INPUT;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_YAML_PATH;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.ReportsBean;
import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.ConsoleLogUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.utils.MessageLoader;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.execution.processor.Processor;
import org.jumbune.execution.utils.ExecutionUtil;
import org.jumbune.execution.utils.ReportGenerator;
import org.jumbune.utils.JobUtil;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;

import com.jcraft.jsch.JSchException;

/**
 * Executor service for shell based user
 * 
 * 
 */
public class ShellExecutorService extends CoreExecutorService {

	private static final Logger LOGGER = LogManager
			.getLogger(ShellExecutorService.class);
	private static final MessageLoader MESSAGES = MessageLoader.getInstance();
	/** The Constant FORWARD_SLASH. */
	private final String FORWARD_SLASH = "/";

	/**
	 * public constructor
	 * 
	 * @throws JumbuneException
	 */
	public ShellExecutorService() throws JumbuneException {
		super();
	}

	/**
	 * Asks user to provide the json file path. Validates the location given by
	 * user and then reads the file content to return InputStream
	 * 
	 * @return InputStream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private InputStream readFilePath() throws JumbuneException,
			FileNotFoundException {
		Scanner scanner = new Scanner(System.in);
		String filePath = ExecutionUtil.readInputFromConsole(scanner,
				MESSAGES.get(MESSAGE_VALID_INPUT),
				MESSAGES.get(MESSAGE_YAML_PATH));

		if (JobUtil.validateFileSystemLocation(filePath)) {
			return readFile(filePath);
		} else {
			throw new JumbuneException(
					ErrorCodesAndMessages.MESSAGE_FILE_PATH_FORMAT_NOT_CORRECT);
		}
	}

	/**
	 * This method is called to get input stream to read a file.
	 * 
	 * @param filePath
	 * @return InputStream
	 * @throws JumbuneException
	 */
	private InputStream readFile(String filePath) throws JumbuneException {
		File file = new File(filePath);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOGGER.error("Json file not found." + e);
		}
		return inputStream;
	}

	/**
	 * main method for execution
	 * 
	 * @param args
	 * @throws JumbuneException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws JumbuneException,
			InterruptedException {
		// wait for execution of Signature validation
		ShellExecutorService service = new ShellExecutorService();
		InputStream jsonFileStream = null;
		try {
			ReportsBean reports = new ReportsBean();
			jsonFileStream = service.readFilePath();
			service.run(jsonFileStream, reports);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (JumbuneException e) {
			ConsoleLogUtil.CONSOLELOGGER.error(e);
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
		System.exit(1);

	}

	/**
	 * 
	 * Method that create and executes the application flow in single thread
	 * This method should be used for shell based application flow.
	 * 
	 * @param is
	 * @param reports
	 * @return JobConfig
	 * @throws JumbuneException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 * @throws JSchException
	 */
	private Config run(InputStream is, ReportsBean reports)
			throws JumbuneException, IOException, JSchException,
			InterruptedException {

		JobConfig jobConfig = JobConfigUtil.jobConfig(is);
		/***
		 * Map<String, Map<String, List<String>>> validatedData = new
		 * ValidateInput().validateYaml(yamlConfig); if
		 * (validatedData.get(Constants.FAILURE_KEY) != null &&
		 * !validatedData.get(Constants.FAILURE_KEY).isEmpty()) {
		 * ConsoleLogUtil.CONSOLELOGGER.debug(validatedData); throw new
		 * HTFException(ErrorCodesAndMessages.INVALID_YAML); }
		 */

		loadInitialSetup(jobConfig);
		disableModules(jobConfig);
		JobConfigUtil jobConfigUtil = new JobConfigUtil(jobConfig);
		jobConfigUtil.createJumbuneDirectories();
		createJobJarFolderOnAgent(jobConfig);
		startExecution(reports, jobConfig);

		try {
			LOGGER.debug("clean up process slave tmp + agent home shell case ");
			cleanUpJumbuneAgentCurrentJobFolder(jobConfig);
			cleanUpSlavesTempFldr(jobConfig);
		} catch (Exception e) {
			LOGGER.error("Exception occurred in clean up slaves tmp folder ", e);
		}
		ConsoleLogUtil.CONSOLELOGGER.debug("clean up done");
		return jobConfig;
	}

	private void startExecution(ReportsBean reports, Config config)
			throws IOException, JumbuneException {
		JobConfig jobConfig = (JobConfig) config;
		String reportFolderPath = new StringBuilder()
				.append(jobConfig.getShellUserReportLocation())
				.append(Constants.DIR_SEPARATOR)
				.append(jobConfig.getJumbuneJobName().split(FORWARD_SLASH)[0])
				.append(Constants.JUMBUNE_REPORT_EXTENTION).toString();
		List<Processor> processors = getProcessorChain(jobConfig, CONSOLE_BASED);
		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setRootDirectory(Constants.JOB_JARS_LOC);
		serviceInfo.setJumbuneHome(JobConfig.getJumbuneHome());
		serviceInfo.setSlaveJumbuneHome(jobConfig.getSlaveWorkingDirectory());
		serviceInfo.setJumbuneJobName(jobConfig.getFormattedJumbuneJobName());
		serviceInfo.setMaster(jobConfig.getMaster());
		serviceInfo.setSlaves(jobConfig.getSlaves());
		// pre-processing
		HELPER.writetoServiceFile(serviceInfo);
		int index = 0;
		for (Processor p : processors) {
			Map<Parameters, String> params = new HashMap<Parameters, String>();
			String processName = "PROCESS" + (++index);
			reports.addInitialStatus(processName);
			params.put(Parameters.PROCESSOR_KEY, processName);
			try {
				p.process(config, reports, params);
			} catch (JumbuneException e) {
				LOGGER.error(processName + " completed with errors !!!", e);
			} finally {
				// marking the process as complete
				reports.markProcessAsComplete(processName);
			}
		}
		persistReportsInExcelFormat(reports, reportFolderPath, config);

		ConsoleLogUtil.CONSOLELOGGER
				.info("!!! Jumbune Job Processing completed Successfully !!!\n ");
		ConsoleLogUtil.CONSOLELOGGER
				.info("Persisted summary reports at location: "
						+ reportFolderPath);
	}

	private void disableModules(Config config) {
		JobConfig jobConfig = (JobConfig) config;
		jobConfig.setEnableStaticJobProfiling(Enable.FALSE);
		jobConfig.setHadoopJobProfile(Enable.FALSE);

	}

	/***
	 * This method load initial setup for json configuration.
	 * 
	 * @param config
	 * @throws JumbuneException
	 */
	private void loadInitialSetup(Config config) throws JumbuneException {
		String agentHome = RemotingUtil.getAgentHome(config);
		ClasspathElement cse = ConfigurationUtil.loadJumbuneSuppliedJarList();
		processClassPathElement(cse, agentHome);
		JobConfig jobConfig = (JobConfig) config;
		jobConfig.getClasspath().setJumbuneSupplied(cse);
		if (!org.jumbune.common.utils.JobConfigUtil
				.isJumbuneSuppliedJarPresent(config)) {
			org.jumbune.common.utils.JobConfigUtil
					.sendJumbuneSuppliedJarOnAgent(config, cse, agentHome);
		}
	}

	private void processClassPathElement(ClasspathElement cse, String agentHome) {
		String[] files = cse.getFiles();
		for (int iIndex = 0; iIndex < files.length; iIndex++) {
			files[iIndex] = files[iIndex].replace(Constants.AGENT_ENV_VAR_NAME,
					agentHome);

		}
	}

	/***
	 * This method persist reports in excel format
	 * 
	 * @param reports
	 * @param reportFolderPath
	 * @throws JumbuneException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void persistReportsInExcelFormat(ReportsBean reports,
			String reportFolderPath, Config config) throws IOException,
			JumbuneException {
		JobConfig jobConfig = (JobConfig) config;
		Map<String, String> map = reports.getAllReports();
		@SuppressWarnings("unchecked")
		Map<String, String> reportsJson = (Map<String, String>) ((HashMap<String, String>) reports
				.getAllReports()).clone();
		if (map.containsKey(Constants.DATA_VALIDATION) && jobConfig != null) {
			map.put(Constants.DATA_VALIDATION, jobConfig.getJumbuneJobLoc());
		}
		ReportGenerator.writesToExcelFile(map, reportFolderPath, reportsJson);
	}

}