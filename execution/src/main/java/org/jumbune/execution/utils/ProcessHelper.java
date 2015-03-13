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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.LogConsolidationInfo;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.common.beans.SupportedHadoopDistributions;
import org.jumbune.common.utils.ArrayParamBuilder;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.HadoopJobCounters;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.MessageLoader;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.utils.YamlConfigUtil;
import org.jumbune.common.yaml.config.Config;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.common.beans.JobProcessBean;
import org.jumbune.datavalidation.DataValidationConstants;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;

/**
 * Helper class for all processors
 * 
 * 
 */
public class ProcessHelper {

	private static final Logger LOGGER = LogManager.getLogger(ProcessHelper.class);
	private static final MessageLoader MESSAGES = MessageLoader.getInstance();
	private static final String LIBDIR = "lib/";
	private static boolean isYarnJob = false;	
	/**
	 * Method for writing service yaml file
	 * 
	 * @param info
	 */
	public boolean writetoServiceFile(ServiceInfo info) {
	    Gson gson = new Gson();
		String serviceYamlPath = org.jumbune.common.utils.YamlConfigUtil.getServiceJsonPath();
		try {
			String jsonString = gson.toJson(info,ServiceInfo.class);
			ConfigurationUtil.writeToFile(serviceYamlPath, jsonString, true);
			LOGGER.debug("Persisted service yaml configuration[" + jsonString+"]");
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
	    Gson gson = new Gson();
		String serviceJsonPath = YamlConfigUtil.getServiceJsonPath();
		File serviceFile = new File(serviceJsonPath);
		ServiceInfo serviceInfo = null;
		if (serviceFile.exists()) {
			FileReader fReader = null;
			try {
				fReader = new FileReader(serviceFile);
				serviceInfo = gson.fromJson(fReader,ServiceInfo.class);
			} catch (FileNotFoundException e) {
				LOGGER.warn("Not able to find services.json at :" + serviceJsonPath);
			}finally{
				if(fReader!=null){
					try{
						fReader.close();
					}catch(IOException ioe){
						LOGGER.error("Failed to close the File Reader instance", ioe);
					}
				}
			}
		} else {
			serviceInfo = new ServiceInfo();
			LOGGER.warn("services.yaml does not exist at :" + serviceJsonPath);
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
	public Map<String, Map<String, String>> executeJar(String inputJarPath, boolean isCommandBasedAllowed, Loader loader, boolean isDebugged)
			throws IOException {
		YamlLoader yamlLoader = (YamlLoader)loader;
	    isYarnJob=((YamlConfig)yamlLoader.getYamlConfiguration()).getEnableYarn().equals(Enable.TRUE);
	    List<JobDefinition> jobDefList = yamlLoader.getJobDefinitionList();
		Map<String, Map<String, String>> jobsCounterMap = new LinkedHashMap<String, Map<String, String>>();

		String location = yamlLoader.getLogDefinition().getLogSummaryLocation().getProfilingFilesLocation();

		Scanner scanner = new Scanner(System.in);
		YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
		String jobName = yamlConfig.getFormattedJumbuneJobName();
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
		LOGGER.info("Completed job jar execution, source path [+" + inputJarPath+"]");
		if (jobsCounterMap.size() > 0) {
			return jobsCounterMap;
		} else {
			LOGGER.warn("Cannot create json as counter map is empty");
			return null;
		}
	}

	private void processMultipleJobDefRequest(String inputJarPath,
			boolean isCommandBasedAllowed, Loader loader,
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
				inputJarPath, loader, jobDefList, jobName);
		LOGGER.debug("Execution type selected  " + executionType);
		if (MESSAGES.get(EXECUTION_TYPE_CONCURRENT).equalsIgnoreCase(executionType)) {
			processConcurrentRequest(loader, isDebugged,
					jobsCounterMap, location, jobProcessList);
		} else if (MESSAGES.get(EXECUTION_TYPE_SEQUENTIAL).equalsIgnoreCase(executionType)) {
			processSequentialRequest(loader, isDebugged,
					jobsCounterMap, location, jobProcessList);
		}
	}

	private void processSingleJobDefRequest(String inputJarPath,
			Loader loader, boolean isDebugged,
			List<JobDefinition> jobDefList,
			Map<String, Map<String, String>> jobsCounterMap, String location,
			String jobName) throws IOException {
		YamlLoader yamlLoader = (YamlLoader)loader;
		JobProcessBean bean = new JobProcessBean(jobDefList.get(0).getName(), getJobExecutionParams(yamlLoader.getHadoopHome(yamlLoader),
				jobDefList.get(0), inputJarPath, yamlLoader.isMainClassDefinedInJobJar(), jobName));
		remoteLaunch(location, bean, yamlLoader);
		populateJobCounterMap(bean, jobsCounterMap, loader, isDebugged);
	}

	private void processSequentialRequest(Loader loader,
			boolean isDebugged,
			Map<String, Map<String, String>> jobsCounterMap, String location,
			List<JobProcessBean> jobProcessList) throws IOException {
		for (JobProcessBean bean : jobProcessList) {
			LOGGER.debug("Executing sequential MapReduce...");
			synchronized (this) {
				remoteLaunch(location, bean, loader); 
															
				
				populateJobCounterMap(bean, jobsCounterMap, loader, isDebugged);
			}
		}
	}

	private void processConcurrentRequest(Loader loader,
			boolean isDebugged,
			Map<String, Map<String, String>> jobsCounterMap, String location,
			List<JobProcessBean> jobProcessList) throws IOException {
		for (JobProcessBean bean : jobProcessList) {
			LOGGER.debug("Executing concurrent MapReduce...");
			remoteLaunch(location, bean, loader);
		}

		for (JobProcessBean bean : jobProcessList) {
			populateJobCounterMap(bean, jobsCounterMap, loader, isDebugged);
		}
	}

	private List<JobProcessBean> poplulateJobProcessList(String inputJarPath,
			Loader loader, List<JobDefinition> jobDefList,
			String jobName) {
		YamlLoader yamlLoader = (YamlLoader)loader;
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
		jobExeParaList.add("AGENT_HOME" + Constants.LOG4J2_API_JAR + ",AGENT_HOME" + Constants.LOG4J2_CORE_JAR + jarNames);

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

	private String remoteLaunch(String location, JobProcessBean jobInfoBean, Loader loader) throws IOException {
		String appHome = YamlLoader.getjHome() + "/";
		String relativePath = location.substring(appHome.length() - 1, location.length());
		Remoter remoter = RemotingUtil.getRemoter(loader, appHome);
		YamlLoader yamlLoader = (YamlLoader)loader;
		String remoteHadoop = RemotingUtil.getHadoopHome(remoter, yamlLoader.getYamlConfiguration()) + "/bin/hadoop";
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
			builder.addCommand(commandBuffer.toString(), true, Arrays.asList(cmdParams), CommandType.HADOOP_JOB);
			response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
			if (relativePath.indexOf("profiling") != -1){
				remoter.receiveLogFiles(relativePath.substring(0, relativePath.indexOf("profiling")), relativePath);
			}
			remoter.close();
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
	    Gson gson = new Gson();
		String serviceJsonPath = org.jumbune.common.utils.YamlConfigUtil.getServiceJsonPath();
		try {
			String jsonString = gson.toJson(logSummaryLoc, LogConsolidationInfo.class);
			ConfigurationUtil.writeToFile(serviceJsonPath, jsonString, true);

		} catch (IOException io) {
			// Don't terminate operation if unable to copy logSummary location
			LOGGER.error("Could not copy logSummaryLocation to a services json  " + io.getMessage());
		}
	}


	private static Map<String, Map<String, String>> getRemoteJobCounters(String processName, String response, Loader loader, boolean isDebugged)
			throws IOException {
		List<String> jobs = new LinkedList<String>();
		Map<String, String> map = null;
		Map<String, Map<String, String>> jobCounterMap = new LinkedHashMap<String, Map<String, String>>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getBytes())));

		String jobName = null;
		try{
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
		}finally{
			if(reader!=null){
				reader.close();
			}
		}
		if (isDebugged && jobs.size() > 1) {
			YamlLoader yamlLoader = (YamlLoader)loader;
			String fileName = yamlLoader.getMasterConsolidatedLogLocation() + "jobChain-" + processName + "_instrumented.log";

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
		int count = Integer.valueOf((line.split(COUNTERS)[1].trim()));
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
	private void populateJobCounterMap(JobProcessBean bean, Map<String, Map<String, String>> jobCounterMap, Loader loader, Boolean isDebugged)
			throws IOException {
	    if(isYarnJob){
	      jobCounterMap.putAll(getRemoteYarnJobCounters(bean.getJobName(), bean.getProcessResponse(), loader, isDebugged));
	    }else{
	      jobCounterMap.putAll(getRemoteJobCounters(bean.getJobName(), bean.getProcessResponse(), loader, isDebugged)); 
	    }
	    HadoopJobCounters.setJobCounterBeans(bean.getJobName(), bean.getProcessResponse(), loader);
	}
	
	private Map<? extends String, ? extends Map<String, String>> getRemoteYarnJobCounters(
			String processName, String response, Loader loader,
			Boolean isDebugged) throws IOException {
		YamlLoader yamlLoader = (YamlLoader)loader;
		List<String> jobs = new LinkedList<String>();
		Map<String, String> map = null;
		Map<String, Map<String, String>> jobCounterMap = new LinkedHashMap<String, Map<String, String>>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(response.getBytes())));
		String line = null;
		String jobName = null;
		while ((line = reader.readLine()) != null) {
			map = new HashMap<String, String>();
			if (line.contains(ExecutionConstants.RUNNING_JOB)) {
				jobName = line.split(ExecutionConstants.RUNNING_JOB)[1].trim();
				jobCounterMap.put(processName + "_" + jobName, map);
			} else if (line.contains(COUNTERS)) {
				break;
			} else if (line.contains("Exception") || line.contains("Error")) {
				processExceptionCondition(processName, map, jobCounterMap,
						reader, jobName, line);
			}
		}
		if (reader != null) {
			reader.close();
		}
		if (isDebugged && jobs.size() > 1) {
			String fileName = yamlLoader.getMasterConsolidatedLogLocation()
					+ "jobChain-" + processName + "_instrumented.log";
			StringBuilder data = new StringBuilder();
			for (String string : jobs) {
				data.append(string).append("\n");
			}
			ConfigurationUtil.writeToFile(fileName, data.toString(), true);
		}
		return jobCounterMap;
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
			if(reader!=null){
				reader.close();
			}
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
	public String remoteValidateData(Loader loader, String inputPath, String dvFileDir, String dvBeanString) throws IOException, JumbuneException {

		LOGGER.debug("Inside validateData method");
		String jHome = YamlLoader.getjHome();
		YamlLoader yamlLoader = (YamlLoader)loader;
		String hadoopHome=yamlLoader.getHadoopHome(loader);
		LogConsolidationInfo info = yamlLoader.getDVDefinition();
		Master master = info.getMaster();
		Remoter remoter = new Remoter(master.getHost(), Integer.valueOf(master.getAgentPort()));
		sendDVJars(remoter, jHome);
		YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
		String jobName = yamlConfig.getFormattedJumbuneJobName();
		String userSuppliedJars = addUserSuppliedDependencyJars(jobName);
		StringBuffer commandBuffer = new StringBuffer();
		String relativePath="jobJars/"+yamlLoader.getJumbuneJobName()+"/dv/";
		commandBuffer=commandBuffer.append("remoteJobLaunch|").append("jobJars/").append(yamlLoader.getJumbuneJobName()).append("|")
				.append(relativePath.subSequence(0, relativePath.lastIndexOf('/')));
		// building the command string
		ArrayParamBuilder sb = buildCommandString(loader, inputPath, dvFileDir,
				dvBeanString, hadoopHome, master, remoter, userSuppliedJars);
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		String [] stringArray=(String[]) sb.toList().toArray(new String [sb.toList().size()]);
		builder.addCommand(commandBuffer.toString(), true, Arrays.asList(stringArray), CommandType.HADOOP_JOB);
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		if (response == null || response.trim().equals("")){
			throw new IllegalArgumentException("DV::Invalid Hadoop Job Response!!!");
		}
		LOGGER.debug("Command executed [" + sb.toString()+"] and commandStrgot back response ["+response+"]");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
		String line = null, dvJson = null;
		boolean errorFound = false;
		StringBuilder errorString = new StringBuilder();
		//ToDO
		while ((line = reader.readLine()) != null) {
			String dvReport = DataValidationConstants.DV_REPORT;
			// checks the input stream for dvReport
			if (line.contains(dvReport)) {
				int index = line.indexOf(dvReport);
				index += DataValidationConstants.TOKENS_FOR_DV_REPORT;
				dvJson = line.substring(index, line.length());
			}
			// checking for any exception or error
			else if (errorFound || (errorFound =(line.contains(Constants.EXCEPTION))) || (errorFound =(line.contains(Constants.ERROR)))) {
				sb.append(line).append("\n");
			}
		}
		if(dvJson == null || dvJson.isEmpty()){
		  LOGGER.error("Error string is: " + errorString.toString());
	        throw new JumbuneException(ErrorCodesAndMessages.ERROR_EXECUTING_DV);
		}
		if(reader!=null){
			reader.close();
		}
		return dvJson;
	}

	private ArrayParamBuilder buildCommandString(Loader loader,
			String inputPath, String dvFileDir, String dvBeanString,
			String hadoopHome, Master master, Remoter remoter,
			String userSuppliedJars) {
		ArrayParamBuilder sb=new ArrayParamBuilder(Constants.NINE);
		sb.append("HADOOP_HOME"+Constants.H_COMMAND).append(Constants.H_COMMAND_TYPE).append(Constants.AGENT_ENV_VAR_NAME+Constants.DV_JAR_PATH)
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
