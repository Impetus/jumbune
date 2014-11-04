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
import org.jumbune.common.utils.MessageLoader;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.yaml.config.Config;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.execution.processor.Processor;
import org.jumbune.execution.utils.ExecutionUtil;
import org.jumbune.execution.utils.ExportUtil;
import org.jumbune.utils.YamlUtil;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;

import com.jcraft.jsch.JSchException;

/**
 * Executor service for shell based user
 * 
 * 
 */
public class ShellExecutorService extends CoreExecutorService {

	private static final Logger LOGGER = LogManager.getLogger(ShellExecutorService.class);
	private static final MessageLoader MESSAGES = MessageLoader.getInstance();
	/** The Constant FORWARD_SLASH. */
	private final String FORWARD_SLASH = "/";
	/**
	 * public constructor
	 * @throws JumbuneException
	 */
	public ShellExecutorService() throws JumbuneException {
		super();
	}

	/**
	 * Asks user to provide the yaml file path. Validates the location given by user and then reads the file content to return InputStream
	 * 
	 * @return InputStream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private InputStream readFilePath() throws JumbuneException, FileNotFoundException {
		Scanner scanner = new Scanner(System.in);
		String filePath = ExecutionUtil.readInputFromConsole(scanner, MESSAGES.get(MESSAGE_VALID_INPUT), MESSAGES.get(MESSAGE_YAML_PATH));

		if (YamlUtil.validateFileSystemLocation(filePath)) {
			return readFile(filePath);
		} else {
			throw new JumbuneException(ErrorCodesAndMessages.MESSAGE_FILE_PATH_FORMAT_NOT_CORRECT);
		}
	}

	/**
	 * This method is called to get input stream to read a file.
	 * 
	 * @param filePath
	 * @return InputStream
	 * @throws JumbuneException
	 */
	private InputStream readFile(String filePath) throws JumbuneException{
		File file = new File(filePath);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOGGER.error("Yaml file not found." + e);
		}
		return inputStream;
	}

	/**
	 * main method for execution
	 * @param args
	 * @throws JumbuneException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws JumbuneException, InterruptedException {
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
		}finally{
			try {
				if(jsonFileStream != null){
					jsonFileStream.close();
				}
			}catch (IOException io) {
				LOGGER.error("Unable to close stream" + io.getMessage());
			}
		}
		System.exit(1);

	}

	/**
	 * 
	 * Method that create and executes the application flow in single thread This method should be used for shell based 
	 * application flow.
	 * 
	 * @param is
	 * @param reports
	 * @return YamlLoader
	 * @throws JumbuneException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InterruptedException 
	 * @throws JSchException 
	 */
	private Loader run(InputStream is, ReportsBean reports) throws JumbuneException,IOException, JSchException, InterruptedException {

		YamlLoader loader = new YamlLoader(is);
		/***
		 * Map<String, Map<String, List<String>>> validatedData = new ValidateInput().validateYaml(yamlConfig); if
		 * (validatedData.get(Constants.FAILURE_KEY) != null && !validatedData.get(Constants.FAILURE_KEY).isEmpty()) {
		 * ConsoleLogUtil.CONSOLELOGGER.debug(validatedData); throw new HTFException(ErrorCodesAndMessages.INVALID_YAML); }
		 */
		RemotingUtil.copyAndGetHadoopConfigurationFilePath(loader, "core-site.xml");
		loadInitialSetup(loader.getYamlConfiguration());
		disableModules(loader);
		loader.createJumbuneDirectories();
		createJobJarFolderOnAgent(loader);		
		startExecution(reports, loader);
	 
		 
		try {
			LOGGER.debug("clean up process slave tmp + agent home shell case ");
			cleanUpJumbuneAgentCurrentJobFolder(loader);
			cleanUpSlavesTempFldr(loader);
			}
		 catch (Exception e) {
			 LOGGER.error("Exception occurred in clean up slaves tmp folder ",e);
		}
		 ConsoleLogUtil.CONSOLELOGGER.debug("clean up done");
		return loader;
	}

	private void startExecution(ReportsBean reports, Loader loader) throws IOException, JumbuneException {
		YamlLoader yamlLoader = (YamlLoader)loader;
		String reportFolderPath = new StringBuilder().append(yamlLoader.getShellUserReportLocation()).append(Constants.DIR_SEPARATOR)
				.append(yamlLoader.getJumbuneJobName().split(FORWARD_SLASH)[0]).append(Constants.JUMBUNE_REPORT_EXTENTION).toString();
		List<Processor> processors = getProcessorChain(yamlLoader.getYamlConfiguration(), CONSOLE_BASED);
		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setRootDirectory(yamlLoader.getRootDirectoryName());
		serviceInfo.setJumbuneHome(YamlLoader.getjHome());
		YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
		serviceInfo.setSlaveJumbuneHome(yamlConfig.getsJumbuneHome());
		serviceInfo.setJumbuneJobName(yamlConfig.getFormattedJumbuneJobName());
		serviceInfo.setMaster(yamlLoader.getMasterInfo());
		serviceInfo.setSlaves(yamlLoader.getSlavesInfo());
		// pre-processing
		HELPER.writetoServiceFile(serviceInfo);
		int index = 0;
		for (Processor p : processors) {
			Map<Parameters, String> params = new HashMap<Parameters, String>();
			String processName = "PROCESS" + (++index);
			reports.addInitialStatus(processName);
			params.put(Parameters.PROCESSOR_KEY, processName);
			try {
				p.process(loader, reports, params);
			} catch (JumbuneException e) {
				LOGGER.error(processName + " completed with errors !!!", e);
			} finally {
				// marking the process as complete
				reports.markProcessAsComplete(processName);
			}
		}
		persistReportsInExcelFormat(reports, reportFolderPath, loader);
		

		ConsoleLogUtil.CONSOLELOGGER.info("!!! Jumbune Job Processing completed Successfully !!!\n ");
		ConsoleLogUtil.CONSOLELOGGER.info("Persisted summary reports at location: " + reportFolderPath);
	}

	private void disableModules(Loader loader) {
		YamlLoader yamlLoader = (YamlLoader)loader;
		YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
		yamlConfig.setEnableStaticJobProfiling(Enable.FALSE);
		yamlConfig.setHadoopJobProfile(Enable.FALSE);

	}

	/***
	 * This method load initial setup for yaml configuration.
	 * 
	 * @param yamlConfig
	 * @throws JumbuneException
	 */
	private void loadInitialSetup(Config config) throws JumbuneException {
		String agentHome = RemotingUtil.getAgentHome(config);
		ClasspathElement cse = ConfigurationUtil.loadJumbuneSuppliedJarList();
		processClassPathElement(cse, agentHome);
		YamlConfig yamlConfig = (YamlConfig)config;
		yamlConfig.getClasspath().setJumbuneSupplied(cse);
		if (!org.jumbune.common.utils.YamlConfigUtil.isJumbuneSuppliedJarPresent(config)){
			org.jumbune.common.utils.YamlConfigUtil.sendJumbuneSuppliedJarOnAgent(config, cse, agentHome);
		}
	}

	private void processClassPathElement(ClasspathElement cse, String agentHome) {
		String[] files = cse.getFiles();
		for (int iIndex = 0; iIndex < files.length; iIndex++) {
			files[iIndex] = files[iIndex].replace(Constants.AGENT_ENV_VAR_NAME, agentHome);

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
	private void persistReportsInExcelFormat(ReportsBean reports, String reportFolderPath, Loader loader) throws
			IOException, JumbuneException {
		YamlLoader yamlLoader = (YamlLoader)loader;
		Map<String, String> map = reports.getAllReports();
		@SuppressWarnings("unchecked")
		Map<String, String> reportsJson = (Map<String, String>) ((HashMap<String, String>) reports.getAllReports()).clone();
		if (map.containsKey(Constants.DATA_VALIDATION) && yamlLoader != null) {
			map.put(Constants.DATA_VALIDATION, yamlLoader.getJumbuneJobLoc());
		}
		ExportUtil.writesToExcelFile(map, reportFolderPath, reportsJson);
	}
	

}