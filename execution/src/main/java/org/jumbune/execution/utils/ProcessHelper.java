package org.jumbune.execution.utils;

import static org.jumbune.execution.utils.ExecutionConstants.COUNTERS;
import static org.jumbune.execution.utils.ExecutionConstants.DEFAULT_JAR_EXECUTION_TYPE;
import static org.jumbune.execution.utils.ExecutionConstants.ERRORANDEXCEPTION;
import static org.jumbune.execution.utils.ExecutionConstants.EXECUTION_TYPE_CONCURRENT;
import static org.jumbune.execution.utils.ExecutionConstants.EXECUTION_TYPE_SEQUENTIAL;
import static org.jumbune.execution.utils.ExecutionConstants.MAPRED_JOBCLIENT;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_COULD_NOT_EXECUTE_JOB;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_EXECUTION_TYPE;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_VALID_INPUT;
import static org.jumbune.execution.utils.ExecutionConstants.RUNNING_JOB;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.LogConsolidationInfo;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.common.beans.SupportedApacheHadoopVersions;
import org.jumbune.common.utils.ArrayParamBuilder;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.MessageLoader;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.datavalidation.DataValidationConstants;
import org.jumbune.execution.beans.JobProcessBean;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.utils.YamlUtil;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;

/**
 * Helper class for all processors
 * 
 * 
 */
public class ProcessHelper {

	private static final Logger LOGGER = LogManager.getLogger(ProcessHelper.class);
	private static final MessageLoader MESSAGES = MessageLoader.getInstance();
	private static final String LIBDIR = "lib/";
	/**
	 * Method for writing service yaml file
	 * 
	 * @param info
	 */
	public boolean writetoServiceFile(ServiceInfo info) {

		String serviceYamlPath = org.jumbune.common.utils.YamlConfigUtil.getServiceYamlPath();

		try {

			String yamlString = YamlUtil.serializeObjectToYaml(info);
			ConfigurationUtil.writeToFile(serviceYamlPath, yamlString, true);
			LOGGER.debug("Persisted service yaml configuration[" + yamlString+"]");
			return true;

		} catch (IOException io) {
			LOGGER.error("Error in persisting services.yaml" + io.getMessage());
		}

		return false;
	}

	/**
	 * Method for reading service yaml file
	 * 
	 * @return
	 */
	public ServiceInfo readServiceInfo() {

		String serviceYamlPath = org.jumbune.common.utils.YamlConfigUtil.getServiceYamlPath();
		File serviceFile = new File(serviceYamlPath);
		ServiceInfo serviceInfo = null;
		if (serviceFile.exists()) {
			try {
				serviceInfo = (ServiceInfo) YamlUtil.loadYaml(serviceYamlPath);
			} catch (FileNotFoundException e) {
				LOGGER.warn("Not able to find services.yaml at :" + serviceYamlPath);
			}
		} else {
			serviceInfo = new ServiceInfo();
			LOGGER.warn("services.yaml does not exist at :" + serviceYamlPath);
		}

		return serviceInfo;
	}

	/**
	 * This method is called to execute pure and instrumented jobs.
	 * 
	 * @param inputJarPath
	 * @param isCommandBasedAllowed
	 * @param isDebugged
	 *            Whether the jar is processed for debugging
	 * @param counterOutputPath
	 * @throws JumbuneException
	 * @return void
	 */
	public Map<String, Map<String, String>> executeJar(String inputJarPath, boolean isCommandBasedAllowed, YamlLoader yamlLoader, boolean isDebugged)
			throws IOException {
		List<JobDefinition> jobDefList = yamlLoader.getJobDefinitionList();

		Map<String, Map<String, String>> jobsCounterMap = new LinkedHashMap<String, Map<String, String>>();

		String location = yamlLoader.getLogDefinition().getLogSummaryLocation().getProfilingFilesLocation();

		Scanner scanner = new Scanner(System.in);
		String jobName = yamlLoader.getYamlConfiguration().getFormattedJumbuneJobName();
		if (jobDefList.size() > 1) {
			processMultipleJobDefRequest(inputJarPath, isCommandBasedAllowed,
					yamlLoader, isDebugged, jobDefList, jobsCounterMap,
					location, scanner, jobName);

		} else if (jobDefList.size() == 1) {
			processSingleJobDefRequest(inputJarPath, yamlLoader, isDebugged,
					jobDefList, jobsCounterMap, location, jobName);
		} else {
			LOGGER.debug(MESSAGES.get(MESSAGE_COULD_NOT_EXECUTE_JOB));
		}
		LOGGER.info("Completed jobjar execution, source path [+" + inputJarPath+"]");

		/**
		 * Collecting error information, group them, removing from original collection and putting back with new group id.
		 */
		if (jobsCounterMap.size() > 0) {
			Iterator<String> it1 = jobsCounterMap.keySet().iterator();

			Map<String, String> errorMap = null;

			while (it1.hasNext()) {
				String key = it1.next();
				Map<String, String> map = jobsCounterMap.get(key);
				if (map.containsKey(ERRORANDEXCEPTION)) {
					if (errorMap == null) {
						errorMap = new LinkedHashMap<String, String>();
					}
					errorMap.put(key, map.get(ERRORANDEXCEPTION));
				}
			}
			if (errorMap != null) {
				Iterator<String> it2 = errorMap.keySet().iterator();
				while (it2.hasNext()) {
					jobsCounterMap.remove(it2.next());
				}

				jobsCounterMap.put(ERRORANDEXCEPTION, errorMap);
			}
		}

		if (jobsCounterMap.size() > 0) {
			return jobsCounterMap;
		} else {
			LOGGER.warn("Cannot create json as counter map is empty");
			return null;
		}
	}

	private void processMultipleJobDefRequest(String inputJarPath,
			boolean isCommandBasedAllowed, YamlLoader yamlLoader,
			boolean isDebugged, List<JobDefinition> jobDefList,
			Map<String, Map<String, String>> jobsCounterMap, String location,
			Scanner scanner, String jobName) throws IOException {
		String executionType;
		if (isCommandBasedAllowed) {
			executionType = ExecutionUtil.readInputFromConsole(scanner, MESSAGES.get(MESSAGE_VALID_INPUT), MESSAGES.get(MESSAGE_EXECUTION_TYPE));
		} else {
			executionType = DEFAULT_JAR_EXECUTION_TYPE;
		}

		List<JobProcessBean> jobProcessList = poplulateJobProcessList(
				inputJarPath, yamlLoader, jobDefList, jobName);
		LOGGER.debug("Execution type selected  " + executionType);
		if (MESSAGES.get(EXECUTION_TYPE_CONCURRENT).equalsIgnoreCase(executionType)) {
			processConcurrentRequest(yamlLoader, isDebugged,
					jobsCounterMap, location, jobProcessList);
		} else if (MESSAGES.get(EXECUTION_TYPE_SEQUENTIAL).equalsIgnoreCase(executionType)) {
			processSequentialRequest(yamlLoader, isDebugged,
					jobsCounterMap, location, jobProcessList);
		}
	}

	private void processSingleJobDefRequest(String inputJarPath,
			YamlLoader yamlLoader, boolean isDebugged,
			List<JobDefinition> jobDefList,
			Map<String, Map<String, String>> jobsCounterMap, String location,
			String jobName) throws IOException {
		JobProcessBean bean = new JobProcessBean(jobDefList.get(0).getName(), getJobExecutionParams(yamlLoader.getHadoopHome(yamlLoader),
				jobDefList.get(0), inputJarPath, yamlLoader.isMainClassDefinedInJobJar(), jobName));
		remoteLaunch(location, bean, yamlLoader);
												

		populateJobCounterMap(bean, jobsCounterMap, yamlLoader, isDebugged);
	}

	private void processSequentialRequest(YamlLoader yamlLoader,
			boolean isDebugged,
			Map<String, Map<String, String>> jobsCounterMap, String location,
			List<JobProcessBean> jobProcessList) throws IOException {
		for (JobProcessBean bean : jobProcessList) {
			LOGGER.debug("Executing sequential MapReduce...");
			synchronized (this) {
				remoteLaunch(location, bean, yamlLoader); 
															
				
				populateJobCounterMap(bean, jobsCounterMap, yamlLoader, isDebugged);
			}
		}
	}

	private void processConcurrentRequest(YamlLoader yamlLoader,
			boolean isDebugged,
			Map<String, Map<String, String>> jobsCounterMap, String location,
			List<JobProcessBean> jobProcessList) throws IOException {
		for (JobProcessBean bean : jobProcessList) {
			LOGGER.debug("Executing concurrent MapReduce...");
			remoteLaunch(location, bean, yamlLoader);
		}

		for (JobProcessBean bean : jobProcessList) {
			populateJobCounterMap(bean, jobsCounterMap, yamlLoader, isDebugged);
		}
	}

	private List<JobProcessBean> poplulateJobProcessList(String inputJarPath,
			YamlLoader yamlLoader, List<JobDefinition> jobDefList,
			String jobName) {
		List<JobProcessBean> jobProcessList = new ArrayList<JobProcessBean>();
		for (JobDefinition jobDef : jobDefList) {
			JobProcessBean bean = new JobProcessBean(jobDef.getName(), getJobExecutionParams(yamlLoader.getHadoopHome(yamlLoader), jobDef,
					inputJarPath,

					yamlLoader.isMainClassDefinedInJobJar(), jobName));
			jobProcessList.add(bean);
		}
		return jobProcessList;
	}

	/**
	 * This method is called to get job parameters.
	 * 
	 * @param jobDef
	 * @param inputJarPath
	 * @param isMainClassDefined
	 *            Is main class defined in jar manifest
	 * 
	 * @return List<String>
	 */
	private List<String> getJobExecutionParams(String hadoopHome, JobDefinition jobDef, String inputJarPath, boolean isMainClassDefined, String jobName) {
		List<String> jobExeParaList = new ArrayList<String>();
		jobExeParaList.add(hadoopHome + YamlLoader.getHadoopCommand());
		jobExeParaList.add(YamlLoader.getHadoopCommandType());
		jobExeParaList.add(inputJarPath);

		// if main class is defined in jar manifest, not including the job class
		// defined in yaml to the parameter list
		if (!isMainClassDefined) {
			jobExeParaList.add(jobDef.getJobClass());
		}
		jobExeParaList.add(Constants.LIB_JARS);
		
		String jarNames = addUserSuppliedDependencyJars(jobName);
		jobExeParaList.add(hadoopHome + Constants.LOG4J2_API_JAR + "," + hadoopHome + Constants.LOG4J2_CORE_JAR + jarNames);

		if (jobDef.getParameters() != null) {
			populateJobExecParamList(jobDef, jobExeParaList);
		}

		return jobExeParaList;
	}

	private void populateJobExecParamList(JobDefinition jobDef,
			List<String> jobExeParaList) {
		String[] jobParam;
		if (jobDef.getParameters().contains("'")) {
			jobParam = jobDef.getParameters().split("'");
			if (jobParam.length % 2 == 0 && !jobDef.getParameters().endsWith("'")) {
				LOGGER.error("Wrong arguments passed. Reason - Either Even number of arguments passed ["+ Arrays.toString(jobParam)+"] OR Job Definition ["+jobDef.getParameters()+"] doesn't ends with ' (a single quote)");
			} else {
				for (int i = 0; i < jobParam.length; i++) {
					if (i % 2 == 0) {
						String c[] = jobParam[i].split(" ");
						for (int j = 0; j < c.length; j++) {
							if (!c[j].trim().equals("")) {
								jobExeParaList.add(c[j]);
							}

						}
					} else {
						jobExeParaList.add(jobParam[i].trim());
					}
				}
			}
		} else {
			jobParam = jobDef.getParameters().split(" ");
			for (int i = 0; i < jobParam.length; i++) {
				if (!jobParam[i].trim().equals("")) {
					jobExeParaList.add(jobParam[i]);
				}
			}
		}
	}

	private String addUserSuppliedDependencyJars(String jobName) {
		// Adding user supplied MR job jars
		String agentPath = System.getenv("AGENT_HOME") +"/"+ Constants.JOB_JARS_LOC + "/"
			+ jobName + Constants.MR_RESOURCES;
		File resourceDir =new File(agentPath);
		File[] files=resourceDir.listFiles();
		String jarNames = "";
		if(files!= null){
			for(File file : files){
				String filename = ","+ file.getPath();
				jarNames = jarNames + filename;
			}
		}
		return jarNames;
	}

	/**
	 * This method matches the jobs that are either mentioned in yaml or user has provided through command with the once that are present in jar file.
	 * If jar doesn't contains Job classes that are provided by user it returns false else true is returned.
	 * 
	 * @param jarJobList
	 * @return boolean
	 */
	public boolean validateJobs(List<JobDefinition> yamlJobDefList, List<String> jarJobList) {
		// This is not a jar it might be a lib or some other folder
		if (jarJobList == null) {
			return false;
		}

		
		for (JobDefinition jobDef : yamlJobDefList) {
			String yamlJobName = jobDef.getJobClass();

			if (yamlJobName != null && (!jarJobList.contains(yamlJobName))) {
						return false;
			}
		}
		return true;
	}



	// TODO: Remoting..
	private String remoteLaunch(String location, JobProcessBean jobInfoBean, YamlLoader loader) throws IOException {

		String appHome = YamlLoader.getjHome() + "/";
		String relativePath = location.substring(appHome.length() - 1, location.length());
		Remoter remoter = RemotingUtil.getRemoter(loader, appHome);
		String remoteHadoop = RemotingUtil.getHadoopHome(remoter, loader.getYamlConfiguration()) + "/bin/hadoop";
		StringBuilder params = new StringBuilder();
		List<String> jobParams = jobInfoBean.getJobExecParam();
		if (jobParams.isEmpty()){
			throw new IllegalArgumentException("Invalid command parameters!!!");
		}
		String localPath = jobParams.get(2);
		String relativeJarLocation;
		if (localPath.contains("instrument")) {
			relativePath = localPath.substring(appHome.length() - 1, localPath.length());
			relativeJarLocation = relativePath.substring(0, relativePath.lastIndexOf('/'));
		} else {
			relativeJarLocation = relativePath.replace("/profiling", "/jar/profile/");
																						
		}
		remoter.sendJar(relativeJarLocation, localPath);
		jobParams.remove(0);
		jobParams.add(0, remoteHadoop);

		StringBuilder jp = new StringBuilder();
		for (String string : jobParams){
			jp.append(string).append(RemotingConstants.JUMBUNE_REMOTE_COMMAND_SEPARATOR);
		}
		params.append(jp.substring(0, jp.lastIndexOf(RemotingConstants.JUMBUNE_REMOTE_COMMAND_SEPARATOR)));
		StringBuffer commandBuffer = new StringBuffer();
		commandBuffer.append("remoteJobLaunch|").append(relativeJarLocation).append("|")
				.append(relativePath.contains("instrument") ? relativePath.subSequence(0, relativePath.lastIndexOf('/')) : relativePath);
		
		String response = null;
		try {
			CommandWritableBuilder builder = new CommandWritableBuilder();
			String[] cmdParams = null;
			if (params.toString().contains("bin/hadoop")) {
				cmdParams = params.toString().split(RemotingConstants.JUMBUNE_REMOTE_COMMAND_SEPARATOR);
			}
			builder.addCommand(commandBuffer.toString(), true, Arrays.asList(cmdParams));
			response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
			if (relativePath.indexOf("profiling") != -1){
				remoter.receiveLogFiles(relativePath.substring(0, relativePath.indexOf("profiling")), relativePath);
			}
			jobInfoBean.setProcessResponse(response);
		} catch (Exception e) {
			LOGGER.error("Working directory does not exist so could not execute jar !!! ", e);
		}
		return response;
	}

	/**
	 * This method will write the master log folder location to a file. The master log location information is required by services module. The file
	 * in which location is written is placed inside the lib/ directory lib directory being the one that holds all jars required by HTF framework If
	 * any change is made in location where services.properties file is created a similar change should be made in ServicesUtil class
	 * 
	 * @throws JumbuneException
	 */
	public void writeLogLocationToFile(LogConsolidationInfo logSummaryLoc) {
		LOGGER.debug("writing logs information to file for services  ");
		String serviceYamlPath = org.jumbune.common.utils.YamlConfigUtil.getServiceYamlPath();
		try {

			String yamlString = YamlUtil.serializeObjectToYaml(logSummaryLoc);
			ConfigurationUtil.writeToFile(serviceYamlPath, yamlString, true);

		} catch (IOException io) {
			// Don't terminate operation if unable to copy logSummary location
			LOGGER.error("Could not copy logSummaryLocation to a services.yaml  " + io.getMessage());
		}
	}


	private static Map<String, Map<String, String>> getRemoteJobCounters(String processName, String response, YamlLoader loader, boolean isDebugged)
			throws IOException {

		List<String> jobs = new LinkedList<String>();
		Map<String, String> map = null;
		Map<String, Map<String, String>> jobCounterMap = new LinkedHashMap<String, Map<String, String>>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getBytes())));

		String jobName = null;
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}

			map = new HashMap<String, String>();
			if (line.contains(RUNNING_JOB)) {
				jobName = processJobName(jobs, line);
			} else if (line.contains(COUNTERS)) {
				jobName = processCounters(processName, map, jobCounterMap,
						reader, jobName, line);
			} else if (line.contains("Exception") || line.contains("Error")) {
				processExceptionCondition(processName, map, jobCounterMap,
						reader, jobName, line);
			}
		}
		if (isDebugged && jobs.size() > 1) {
			
			String fileName = loader.getMasterConsolidatedLogLocation() + "jobChain-" + processName + "_instrumented.log";

				StringBuilder data = new StringBuilder();
				for (String string : jobs) {
					data.append(string).append("\n");
				}
				ConfigurationUtil.writeToFile(fileName, data.toString(), true);
			
		}
		return jobCounterMap;
	}

	private static void processExceptionCondition(String processName,
			Map<String, String> map,
			Map<String, Map<String, String>> jobCounterMap,
			BufferedReader reader, String jobName, String line)
			throws IOException {
		LOGGER.error("Exception occured while executing jar: " + line);
		map.put(ERRORANDEXCEPTION, line);
		while (true) {
			String lineTmp = line;
			lineTmp = reader.readLine();
			if (lineTmp == null) {
				break;
			}
			LOGGER.error(lineTmp);
		}
		jobCounterMap.put(processName + (jobName == null ? "" : "-" + jobName), map);
	}

	private static String processJobName(List<String> jobs, String line) {
		String jobName;
		jobName = line.split(RUNNING_JOB)[1];
		jobs.add(jobName);
		return jobName;
	}

	private static String processCounters(String processName,
			Map<String, String> map,
			Map<String, Map<String, String>> jobCounterMap,
			BufferedReader reader, String jobName, String line)
			throws IOException {
		int count = Integer.valueOf((line.split(COUNTERS)[1]));
		while (count > 0) {
			String lineTmp = line;
			lineTmp = reader.readLine();
			if (lineTmp == null) {
				break;
			}
			if (lineTmp.contains("=")) {
				String[] counterDetail = lineTmp.split(MAPRED_JOBCLIENT)[1].split("=");
				map.put(ExecutionUtil.convertInCamelCase(counterDetail[0].trim()), counterDetail[1].trim());
				count--;
			}
		}
		jobCounterMap.put(processName + "_" + jobName, map);
		return null;
	}

	/**
	 * loads the execution message 
	 * @return
	 * @throws JumbuneException
	 */
	public MessageLoader loadMessages() throws JumbuneException {
		final String messageFileName = Constants.MESSAGE_FILE;
		final InputStream msgStream = this.getClass().getClassLoader().getResourceAsStream(messageFileName);
		return new MessageLoader(msgStream);
	}

	/**
	 * <p>
	 * 
	 * </p>
	 * 
	 * @param bean
	 *            Job process bean
	 * @param jobCounterMap
	 *            Existing map
	 * @param yamlLoader
	 * @param isDebugged
	 * @throws IOException
	 */
	private void populateJobCounterMap(JobProcessBean bean, Map<String, Map<String, String>> jobCounterMap, YamlLoader yamlLoader, Boolean isDebugged)
			throws IOException {
		
		jobCounterMap.putAll(getRemoteJobCounters(bean.getJobName(), bean.getProcessResponse(), yamlLoader, isDebugged)); 
																															
																															
	}

	/**
	 * <p>
	 * This method is used to apply data validation to the records fetched
	 * </p>
	 * 
	 * @param inputPath
	 *            the path to read data from hdfs
	 * @param hadoopHome
	 *            the path to hadoop home
	 * @param dvBeanString
	 *            details regarding fetching data
	 * @return String data validation report
	 * @throws IOException
	 * @throws JumbuneException
	 */
	public String validateData(String inputPath, String hadoopHome, String dvFileDir, String dvBeanString) throws IOException, JumbuneException {
		String jHome = YamlLoader.getjHome();
		// building the command string
		StringBuilder sb = new StringBuilder(Constants.H_COMMAND);
		sb.append(" ").append(Constants.H_COMMAND_TYPE).append(" ").append(jHome).append(Constants.DV_JAR_PATH).append(" ")
				.append(Constants.DV_MAIN_CLASS).append(" ").append(Constants.LIB_JARS).append(" ").append(jHome).append(Constants.GSON_JAR)
				.append(",").append(jHome).append(Constants.COMMON_JAR).append(",").append(jHome).append(Constants.UTILITIES_JAR).append(hadoopHome)
				.append(Constants.LOG4J2_API_JAR).append(",").append(hadoopHome).append(Constants.LOG4J2_CORE_JAR).append(" ").append(inputPath)
				.append(" ").append(dvFileDir).append(" ").append(dvBeanString);
		String[] argsArr = sb.toString().split(" ");
		ProcessBuilder processBuilder = new ProcessBuilder(argsArr);
		processBuilder.directory(new File(hadoopHome));
		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();

		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is)));
		String line = null;
		String dvJson = null;
		try {
			while ((line = reader.readLine()) != null) {
				LOGGER.debug(line);
				String dvReport = DataValidationConstants.DV_REPORT;
				// checks the input stream for dvReport
				if (line.contains(dvReport)) {
					int index = line.indexOf(dvReport);
					index += DataValidationConstants.TOKENS_FOR_DV_REPORT;
					dvJson = line.substring(index, line.length());
				}
				// checking for any exception or error
				else if (line.contains(Constants.EXCEPTION) || line.contains(Constants.ERROR)) {
					StringBuilder errorString = new StringBuilder(line);
					while (true) {
						line = reader.readLine();
						if (line == null) {
							LOGGER.error("Error string is: " + errorString.toString());
							throw new JumbuneException(ErrorCodesAndMessages.ERROR_EXECUTING_DV);
						}
						errorString.append("\n");
						errorString.append(line);
					}
				}
			}
			LOGGER.debug("Data validation command ["+sb.toString()+"] and got back response ["+dvJson+"]");
		} finally {
			is.close();
		}
		return dvJson;
	}

	/**
	 * <p>
	 * This method is used to apply data validation to the records fetched
	 * </p>
	 * 
	 * @param loader
	 *            the yaml loader object containg infomation about master node and agent port
	 * @param inputPath
	 *            the path to read data from hdfs
	 * @param dvBeanString
	 *            details regarding fetching data
	 * @return String data validation report
	 * @throws IOException
	 * @throws JumbuneException
	 */
	public String remoteValidateData(YamlLoader loader, String inputPath, String dvFileDir, String dvBeanString) throws IOException, JumbuneException {

		LOGGER.debug("Inside validateData method");
		String jHome = YamlLoader.getjHome();
		String hadoopHome=loader.getHadoopHome(loader);
		LogConsolidationInfo info = loader.getDVDefinition();
		Master master = info.getMaster();
		Remoter remoter = new Remoter(master.getHost(), Integer.valueOf(master.getAgentPort()));
		sendDVJars(remoter, jHome);
		
		String jobName = loader.getYamlConfiguration().getFormattedJumbuneJobName();
		String userSuppliedJars = addUserSuppliedDependencyJars(jobName);
		StringBuffer commandBuffer = new StringBuffer();
		String relativePath="jobJars/"+loader.getJumbuneJobName()+"/dv/";
		commandBuffer=commandBuffer.append("remoteJobLaunch|").append("jobJars/").append(loader.getJumbuneJobName()).append("|")
				.append(relativePath.subSequence(0, relativePath.lastIndexOf('/')));
		// building the command string
		ArrayParamBuilder sb = buildCommandString(loader, inputPath, dvFileDir,
				dvBeanString, hadoopHome, master, remoter, userSuppliedJars);
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		String [] stringArray=(String[]) sb.toList().toArray(new String [sb.toList().size()]);
		builder.addCommand(commandBuffer.toString(), true, Arrays.asList(stringArray));
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		if (response == null || response.trim().equals("")){
			throw new IllegalArgumentException("DV::Invalid Hadoop Job Response!!!");
		}
		LOGGER.debug("Command executed [" + sb.toString()+"] and commandStrgot back response ["+response+"]");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
		String line = null;
		String dvJson = null;
		while ((line = reader.readLine()) != null) {
			String dvReport = DataValidationConstants.DV_REPORT;
			// checks the input stream for dvReport
			if (line.contains(dvReport)) {
				int index = line.indexOf(dvReport);
				index += DataValidationConstants.TOKENS_FOR_DV_REPORT;
				dvJson = line.substring(index, line.length());
			}
			// checking for any exception or error
			else if (line.contains(Constants.EXCEPTION) || line.contains(Constants.ERROR)) {
				StringBuilder errorString = new StringBuilder(line);
				while (true) {
					line = reader.readLine();
					if (line == null) {
						LOGGER.error("Error string is: " + errorString.toString());
						throw new JumbuneException(ErrorCodesAndMessages.ERROR_EXECUTING_DV);
					}
					errorString.append("\n");
					errorString.append(line);
				}
			}
		}
		return dvJson;
	}

	private ArrayParamBuilder buildCommandString(YamlLoader loader,
			String inputPath, String dvFileDir, String dvBeanString,
			String hadoopHome, Master master, Remoter remoter,
			String userSuppliedJars) {
		ArrayParamBuilder sb=new ArrayParamBuilder(Constants.NINE);
		if(SupportedApacheHadoopVersions.Hadoop_1_0_4.equals(RemotingUtil.getHadoopVersion(loader.getYamlConfiguration()))|| SupportedApacheHadoopVersions.HADOOP_1_0_3.equals(RemotingUtil.getHadoopVersion(loader.getYamlConfiguration()))||
				SupportedApacheHadoopVersions.HADOOP_0_20_2.equals(RemotingUtil.getHadoopVersion(loader.getYamlConfiguration()))){
			sb.append(hadoopHome+"/"+Constants.H_COMMAND);
		}else{
			String hadoopDir = RemotingUtil.fireWhereIsHadoopCommand(remoter, master, loader.getYamlConfiguration());
				sb.append(hadoopDir);
		}
		sb.append(Constants.H_COMMAND_TYPE).append(Constants.AGENT_ENV_VAR_NAME+Constants.DV_JAR_PATH)
		.append(Constants.DV_MAIN_CLASS).append(Constants.LIB_JARS).append(Constants.AGENT_ENV_VAR_NAME+
		Constants.GSON_JAR+","+Constants.AGENT_ENV_VAR_NAME+Constants.COMMON_JAR+","+
		Constants.AGENT_ENV_VAR_NAME+Constants.UTILITIES_JAR+userSuppliedJars+","+Constants.AGENT_ENV_VAR_NAME
		+Constants.LOG4J2_API_JAR+","+Constants.AGENT_ENV_VAR_NAME+Constants.LOG4J2_CORE_JAR).
		append(inputPath).append(dvFileDir).append(dvBeanString);
		return sb;
	}

	private void sendDVJars(Remoter remoter, String jHome) {
		String jHomeTmp = jHome;
		if (!jHomeTmp.trim().endsWith("/")){
			jHomeTmp += "/";
		}
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.JUMBUNE_RELATIVE_DV_JAR_PATH);
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.GSON_JAR);
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.COMMON_JAR);
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.UTILITIES_JAR);
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.LOG4J2_API_JAR);
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.LOG4J2_CORE_JAR);
	}

	


}
