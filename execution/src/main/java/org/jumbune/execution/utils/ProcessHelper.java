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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.jumbune.common.beans.DataProfilingBean;
import org.jumbune.common.beans.DataProfilingFileDetails;
import org.jumbune.common.beans.DataProfilingJson;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.LogConsolidationInfo;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.common.utils.ArrayParamBuilder;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.HadoopJobCounters;
import org.jumbune.common.utils.MessageLoader;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.datavalidation.DataValidationConstants;
import org.jumbune.common.beans.JobProcessBean;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.dataprofiling.utils.DataProfilingConstants;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	private static String jobJson = null ;
	private HadoopJobCounters hadoopJobCounters=null;
		
	
		public HadoopJobCounters getHadoopJobCounters() {
			return hadoopJobCounters;
		}
	
		public void setHadoopJobCounters(HadoopJobCounters hadoopJobCounters) {
			this.hadoopJobCounters = hadoopJobCounters;
		}
	
	
	/**
	 * Method for writing service json file
	 * 
	 * @param info
	 */
	public boolean writetoServiceFile(ServiceInfo info) {
	    Gson gson = new Gson();
		String serviceYamlPath = JobConfigUtil.getServiceJsonPath();
		try {
			String jsonString = gson.toJson(info,ServiceInfo.class);
			ConfigurationUtil.writeToFile(serviceYamlPath, jsonString, true);
			LOGGER.debug("Persisted service job configuration[" + jsonString+"]");
			return true;

		} catch (IOException io) {
			LOGGER.error("Error in persisting services.job" + io.getMessage());
		}

		return false;
	}

	/**
	 * Method for reading service json file
	 * 
	 * @return
	 */
	public ServiceInfo readServiceInfo() {
	    Gson gson = new Gson();
		String serviceJsonPath = JobConfigUtil.getServiceJsonPath();
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
	public Map<String, Map<String, String>> executeJar(String inputJarPath, boolean isCommandBasedAllowed, Config config, boolean isDebugged)
			throws IOException {
		JobConfig jobConfig = (JobConfig)config;
	    isYarnJob=jobConfig.getEnableYarn().equals(Enable.TRUE);
	    List<JobDefinition> jobDefList = jobConfig.getJobs();
		Map<String, Map<String, String>> jobsCounterMap = new LinkedHashMap<String, Map<String, String>>();

		String location = jobConfig.getLogDefinition().getLogSummaryLocation().getProfilingFilesLocation();

		Scanner scanner = new Scanner(System.in);
		String jobName = jobConfig.getFormattedJumbuneJobName();
		if (jobDefList.size() > 1) {
			processMultipleJobDefRequest(inputJarPath, isCommandBasedAllowed,
					config, isDebugged, jobDefList, jobsCounterMap,
					location, scanner, jobName);

		} else if (jobDefList.size() == 1) {
			processSingleJobDefRequest(inputJarPath, config, isDebugged,
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
			boolean isCommandBasedAllowed, Config config,
			boolean isDebugged, List<JobDefinition> jobDefList,
			Map<String, Map<String, String>> jobsCounterMap, String location,
			Scanner scanner, String jobName) throws IOException {
		String executionType;
		if (isCommandBasedAllowed) {
			executionType = ExecutionUtil.readInputFromConsole(scanner, MESSAGES.get(MESSAGE_VALID_INPUT), MESSAGES.get(MESSAGE_EXECUTION_TYPE));
		} else {
			executionType = DEFAULT_JAR_EXECUTION_TYPE;
		}

		List<JobProcessBean> jobProcessList = populateJobProcessList(
				inputJarPath, config, jobDefList, jobName);
		LOGGER.debug("Execution type selected  " + executionType);
		if (MESSAGES.get(EXECUTION_TYPE_CONCURRENT).equalsIgnoreCase(executionType)) {
			processConcurrentRequest(config, isDebugged,
					jobsCounterMap, location, jobProcessList);
		} else if (MESSAGES.get(EXECUTION_TYPE_SEQUENTIAL).equalsIgnoreCase(executionType)) {
			processSequentialRequest(config, isDebugged,
					jobsCounterMap, location, jobProcessList);
		}
	}

	private void processSingleJobDefRequest(String inputJarPath,
			Config config, boolean isDebugged,
			List<JobDefinition> jobDefList,
			Map<String, Map<String, String>> jobsCounterMap, String location,
			String jobName) throws IOException {
		JobConfig jobConfig = (JobConfig)config;
		JobProcessBean bean = new JobProcessBean(jobDefList.get(0).getName(), getJobExecutionParams(RemotingUtil.getHadoopHome(jobConfig),
				jobDefList.get(0), inputJarPath, jobConfig.isMainClassDefinedInJobJar(), jobName));
		remoteLaunch(location, bean, config);
		populateJobCounterMap(bean, jobsCounterMap, config, isDebugged);
	}

	private void processSequentialRequest(Config config,
			boolean isDebugged,
			Map<String, Map<String, String>> jobsCounterMap, String location,
			List<JobProcessBean> jobProcessList) throws IOException {
		for (JobProcessBean bean : jobProcessList) {
			LOGGER.debug("Executing sequential MapReduce...");
			synchronized (this) {
				remoteLaunch(location, bean, config); 
															
				
				populateJobCounterMap(bean, jobsCounterMap, config, isDebugged);
			}
		}
	}

	private void processConcurrentRequest(Config config,
			boolean isDebugged,
			Map<String, Map<String, String>> jobsCounterMap, String location,
			List<JobProcessBean> jobProcessList) throws IOException {
		for (JobProcessBean bean : jobProcessList) {
			LOGGER.debug("Executing concurrent MapReduce...");
			remoteLaunch(location, bean, config);
		}

		for (JobProcessBean bean : jobProcessList) {
			populateJobCounterMap(bean, jobsCounterMap, config, isDebugged);
		}
	}

	private List<JobProcessBean> populateJobProcessList(String inputJarPath,
			Config config, List<JobDefinition> jobDefList,
			String jobName) {
		JobConfig jobConfig = (JobConfig)config;
		List<JobProcessBean> jobProcessList = new ArrayList<JobProcessBean>();
		for (JobDefinition jobDef : jobDefList) {
			JobProcessBean bean = new JobProcessBean(jobDef.getName(), getJobExecutionParams(RemotingUtil.getHadoopHome(jobConfig), jobDef,
					inputJarPath,

					jobConfig.isMainClassDefinedInJobJar(), jobName));
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
		jobExeParaList.add(hadoopHome +Constants.HADOOP_COMMAND);
		jobExeParaList.add(Constants.HADOOP_COMMAND_TYPE);
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
		String agentPath = System.getenv("AGENT_HOME") +File.separator+ Constants.JOB_JARS_LOC + File.separator
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
	 * This method matches the jobs that are either mentioned in json or user has provided through command with the once that are present in jar file.
	 * If jar doesn't contains Job classes that are provided by user it returns false else true is returned.
	 * 
	 * @param jarJobList
	 * @return boolean
	 */
	public boolean validateJobs(List<JobDefinition> jsonJobDefList, List<String> jarJobList) {
		// This is not a jar it might be a lib or some other folder
		if (jarJobList == null) {
			return false;
		}
		for (JobDefinition jobDef : jsonJobDefList) {
			String jsonJobName = jobDef.getJobClass();

			if (jsonJobName != null && (!jarJobList.contains(jsonJobName))) {
						return false;
			}
		}
		return true;
	}

	private String remoteLaunch(String location, JobProcessBean jobInfoBean, Config config) throws IOException {
		String appHome = JobConfig.getJumbuneHome() + File.separator;
		String relativePath = location.substring(appHome.length() - 1, location.length());
		Remoter remoter = RemotingUtil.getRemoter(config, appHome);
		JobConfig jobConfig = (JobConfig)config;
		String remoteHadoop = RemotingUtil.getHadoopHome(remoter, jobConfig) + "/bin/hadoop";
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
		String serviceJsonPath = JobConfigUtil.getServiceJsonPath();
		try {
			String jsonString = gson.toJson(logSummaryLoc, LogConsolidationInfo.class);
			ConfigurationUtil.writeToFile(serviceJsonPath, jsonString, true);

		} catch (IOException io) {
			// Don't terminate operation if unable to copy logSummary location
			LOGGER.error("Could not copy logSummaryLocation to a services json  " + io.getMessage());
		}
	}


	private static Map<String, Map<String, String>> getRemoteJobCounters(String processName, String response, Config config, boolean isDebugged)
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
			JobConfig jobConfig = (JobConfig)config;
			String fileName = jobConfig.getMasterConsolidatedLogLocation() + "jobChain-" + processName + "_instrumented.log";

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
	private void populateJobCounterMap(JobProcessBean bean, Map<String, Map<String, String>> jobCounterMap, Config config, Boolean isDebugged)
			throws IOException {
	    if(isYarnJob){
	      jobCounterMap.putAll(getRemoteYarnJobCounters(bean.getJobName(), bean.getProcessResponse(), config, isDebugged));	   
	    }else{
	      jobCounterMap.putAll(getRemoteJobCounters(bean.getJobName(), bean.getProcessResponse(), config, isDebugged)); 
	    }
	    hadoopJobCounters=new HadoopJobCounters();
	    hadoopJobCounters.setJobCounterBeans(bean.getJobName(), bean.getProcessResponse(), config);
	}
	
	
	private Map<? extends String, ? extends Map<String, String>> getRemoteYarnJobCounters(
			String processName, String response, Config config,
			Boolean isDebugged) throws IOException {
		JobConfig jobConfig = (JobConfig)config;
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
			String fileName = jobConfig.getMasterConsolidatedLogLocation()
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
		String jumbuneHome = JobConfig.getJumbuneHome();
		// building the command string
		StringBuilder sb = new StringBuilder(Constants.HADOOP_COMMAND);
		sb.append(" ").append(Constants.HADOOP_COMMAND_TYPE).append(" ").append(jumbuneHome).append(Constants.DV_JAR_PATH).append(" ")
				.append(Constants.DV_MAIN_CLASS).append(" ").append(Constants.LIB_JARS).append(" ").append(jumbuneHome).append(Constants.GSON_JAR)
				.append(",").append(jumbuneHome).append(Constants.COMMON_JAR).append(",").append(jumbuneHome).append(Constants.UTILITIES_JAR).append(hadoopHome)
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
	 * @param config
	 *            the Job Config object containg infomation about master node and agent port
	 * @param inputPath
	 *            the path to read data from hdfs
	 * @param dvBeanString
	 *            details regarding fetching data
	 * @return String data validation report
	 * @throws IOException
	 * @throws JumbuneException
	 */
	public String remoteValidateData(Config config, String inputPath, String dvFileDir, String dvBeanString) throws IOException, JumbuneException {

		LOGGER.debug("Inside validateData method");
		JobConfig jobConfig = (JobConfig)config;
		String jumbuneHome = JobConfig.getJumbuneHome();
		String hadoopHome=RemotingUtil.getHadoopHome(config);
		LogConsolidationInfo info = jobConfig.getDVDefinition();
		Master master = info.getMaster();
		Remoter remoter = new Remoter(master.getHost(), Integer.valueOf(master.getAgentPort()));
		sendDVJars(remoter, jumbuneHome);
		String jobName = jobConfig.getFormattedJumbuneJobName();
		String userSuppliedJars = addUserSuppliedDependencyJars(jobName);
		StringBuffer commandBuffer = new StringBuffer();
		String relativePath="jobJars/"+jobConfig.getJumbuneJobName()+"/dv/";
		commandBuffer=commandBuffer.append("remoteJobLaunch|").append("jobJars/").append(jobConfig.getJumbuneJobName()).append("|")
				.append(relativePath.subSequence(0, relativePath.lastIndexOf('/')));
		// building the command string
		ArrayParamBuilder sb = buildCommandString(config, inputPath, dvFileDir,
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
	
	
	
	/**
	 * Launch data profiling job and process output.
	 *
	 * @param config the config
	 * @param inputPath the input path
	 * @param dpBeanString the dp bean string
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JumbuneException the jumbune exception
	 */
	public String launchDataProfilingJobAndProcessOutput(Config config, String inputPath, String dpBeanString,DataProfilingBean dataProfilingBean) throws IOException, JumbuneException {

		
		String jumbuneHome = JobConfig.getJumbuneHome();
		JobConfig jobConfig = (JobConfig)config;
		boolean dataProfilingJbLaunch = false ;
		boolean mergeOutput  =  false ;
		int hashCode = 0 ;
		if(jobConfig.getCriteriaBasedDataProfiling().equals(Enable.TRUE)){
		  hashCode = dataProfilingBean.getFieldProfilingRules().hashCode();
		}
		String dataProfJsonDir = new StringBuilder(jumbuneHome).append(File.separator).append(DataProfilingConstants.DATA_PROFILES).append(File.separator).toString();
		File[] jsonFiles = getJsonFile(dataProfJsonDir);
		String lsrCommandResponse = getLsrCommandResponse(inputPath, jobConfig);	
		// If there is no Json files present inside data profile folder then we trigger new Data profiling job.
		if(jsonFiles == null){
			dataProfilingJbLaunch = true;	
		}
		else{
		
		Enable dataProEnable = null ;
		DataProfilingJson dataProfJson = populateDataProfilingJson(inputPath, dataProfJsonDir, jsonFiles ,jobConfig.getCriteriaBasedDataProfiling());
		//Here we check if data profiling is not null then we go for extracting and comparing the files to be profiled.
		if(dataProfJson!=null){
		Map<String, String> fileCheckSumMap = new HashMap<String, String>();
		Map<String, String> currentFileCheckSumMap = new HashMap<String, String>();
		List<DataProfilingFileDetails> dataProfilingFileDetails = dataProfJson.getDataProfilingFileDetails();
		for (DataProfilingFileDetails dataprofiles : dataProfilingFileDetails) {
			int verifyHashCode = dataprofiles.getHashCode();
			dataProEnable = dataprofiles.getDataProfilingType();
			fileCheckSumMap = dataprofiles.getFileCheckSumMap();
			//Here we perform the hdfs look up i.e. computing the checksum of the current files to be profiled and storing them in a map corresponding to their name.
			currentFileCheckSumMap = performHdfsLookUp(inputPath, jobConfig, lsrCommandResponse);
			String currentKey = null;
			String currentValue = null;
			if(currentFileCheckSumMap.size() < dataprofiles.getNoOfFiles()){
				dataProfilingJbLaunch = true ;
				break;
			}
			if(dataProEnable.equals(Enable.FALSE)){
				//Here we check if the number of files to be profiled is greater the previously profiled files, if true then
				 //we take only the new file and run data profiling job for it and merge both the outputs. 
				if(currentFileCheckSumMap.size() > dataprofiles.getNoOfFiles()){
				inputPath = getInputPaths(inputPath, jobConfig, lsrCommandResponse);
				String[] listOfFiles = inputPath.split(Constants.COMMA);
				List<String> fileList = new ArrayList<String>();
				for (String eachFile : listOfFiles) {
					fileList.add(eachFile.trim());
					for (Map.Entry<String, String> entrySet : fileCheckSumMap.entrySet()) {
						if(entrySet.getKey().equalsIgnoreCase(eachFile.trim())){
							fileList.remove(eachFile.trim());
						}
					}
				
				}
				inputPath = fileList.toString().substring(1, fileList.toString().length()-1);	
				inputPath = inputPath.replace(Constants.DOT,File.separator);
				mergeOutput = true ;
				jobJson = dataprofiles.getProfiledOutput();
				dataProfilingJbLaunch = true ;
				break;
				}
			}
			
			//This loop takes out the entries in the current file checksum map.
			for(Map.Entry<String, String> currentEntrySet : currentFileCheckSumMap.entrySet()){
				currentKey = currentEntrySet.getKey();
				//if the current key i.e. file on hdfs is there , then we check if the corresponding file is present already.
				if(currentKey!=null){
					currentValue = fileCheckSumMap.get(currentKey);
					//If we found a file match then we compare their checksum value.
					if(currentValue!=null && currentValue.equals(currentEntrySet.getValue())){
						//Here we check if Rule Based Profiling is enabled and the saved rule is same as current rule then we populate the saved output.
						if(dataProEnable.equals(Enable.TRUE) &&  hashCode == verifyHashCode){
								jobJson = dataprofiles.getProfiledOutput();
							}
							//if Non-Rule based profiling is enabled then we populate the already saved output.
							else if(dataProEnable.equals(Enable.FALSE)){
								jobJson = dataprofiles.getProfiledOutput();
							}
							// if both above conditions are not met then we trigger a new data profiling job.
							else{
								dataProfilingJbLaunch = true ;
							}
						}
						// this is the condition where file checksum doesn't match and hence we trigger data profiling job.  
						else{
								dataProfilingJbLaunch = true ;
							}
			}
			}
			
		}
	}
		//if the data profiling json is null then we trigger data profiling job.
		else{
			dataProfilingJbLaunch = true ;
		}
		}	
		if(dataProfilingJbLaunch){
		String hadoopHome=RemotingUtil.getHadoopHome(config);
		Remoter remoter = RemotingUtil.getRemoter(config,"");
		
		String jobName = jobConfig.getFormattedJumbuneJobName();
		String userSuppliedJars = addUserSuppliedDependencyJars(jobName);
		StringBuffer commandBuffer = new StringBuffer();
		sendDVJars(remoter, jumbuneHome);
		String relativePath="jobJars/"+jobConfig.getJumbuneJobName()+"/dp/";
		commandBuffer=commandBuffer.append("remoteJobLaunch|").append("jobJars/").append(jobConfig.getJumbuneJobName()).append("|")
				.append(relativePath.subSequence(0, relativePath.lastIndexOf('/')));
		// building the job trigger command
		ArrayParamBuilder sb = buildDataProfilingCommandString(jobConfig, inputPath, dpBeanString, hadoopHome, remoter, userSuppliedJars);
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		String [] stringArray=(String[]) sb.toList().toArray(new String [sb.toList().size()]);
		builder.addCommand(commandBuffer.toString(), true, Arrays.asList(stringArray), CommandType.HADOOP_JOB);
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		if (response == null || response.trim().equals("")){
			throw new IllegalArgumentException("Data Profiling::Invalid Hadoop Job Response!!!");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
		String line = null , dataProfilingJson = null;
		boolean errorFound = false;
		String [] dataProfilingReportArray ;
		//ToDO
		while ((line = reader.readLine()) != null) {
			String dataProfilingReport = DataProfilingConstants.DATA_PROFILING_REPORT;
			if (line.contains(dataProfilingReport)) {
				dataProfilingReportArray = line.split(DataProfilingConstants.DATA_PROFILING_REPORT); 
				dataProfilingJson = dataProfilingReportArray[1];
			}// checking for any exception or error
			else if (errorFound || (errorFound =(line.contains(Constants.EXCEPTION))) || (errorFound =(line.contains(Constants.ERROR)))) {
				sb.append(line).append("\n");
			}
			
		}
		//If merge output is true then we merge the output of the current job and saved job.
		if(mergeOutput){
			HashMap<String,Integer> persistedMapOutput = new ObjectMapper().readValue(jobJson, HashMap.class);
			HashMap<String,Integer> newMapOutput = new ObjectMapper().readValue(dataProfilingJson, HashMap.class);
			HashMap<String, Integer> mergedMap  = new HashMap<String, Integer>();
			mergedMap  = getMergeMapOutput(persistedMapOutput, newMapOutput, mergedMap);
			Map<String,Integer> sortedMap  = getSortedMap(mergedMap);
			Gson dataGson = new GsonBuilder().disableHtmlEscaping().create();
			dataProfilingJson = dataGson.toJson(sortedMap);
		}
		//Going to dump the data profiling details into a json file.
		if(!dataProfilingJson.isEmpty() && dataProfilingJson!=null){
		saveDataProfilingJson(jobConfig,dataProfilingJson,hashCode, lsrCommandResponse);
		}
		return dataProfilingJson;
		}
		return jobJson ;
	}

	/**
	 * Gets the sorted map.
	 *
	 * @param mergedMap the merged map
	 * @return the sorted map
	 */
	private Map<String, Integer> getSortedMap(
			HashMap<String, Integer> mergedMap) {
		
		List<Map.Entry<String, Integer>> sortedList = new LinkedList<Map.Entry<String, Integer>>( mergedMap.entrySet() );
        Collections.sort( sortedList, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
		 Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : sortedList)
        {
        	sortedMap.put( entry.getKey(), entry.getValue() );
         }
        return sortedMap;
	}

	
	/**
	 * Gets the merged map containing the results of both saved job and newly run file job.
	 *
	 * @param persistedMapOutput the persisted map output
	 * @param newMapOutput the new map output
	 * @param mergedMap the merged map
	 * @return the merge map output
	 */
	private HashMap<String, Integer> getMergeMapOutput(HashMap<String, Integer> persistedMapOutput,
			HashMap<String, Integer> newMapOutput,
			HashMap<String, Integer> mergedMap) {
		for (Map.Entry<String, Integer> persistedEntrySet : persistedMapOutput.entrySet()) {
			for (Map.Entry<String, Integer> newEntrySet : newMapOutput.entrySet()){
				if(persistedEntrySet.getKey().equalsIgnoreCase(newEntrySet.getKey())){
					int value = persistedEntrySet.getValue() + newEntrySet.getValue();
					mergedMap.put(persistedEntrySet.getKey(),value);
			}
				if(!mergedMap.containsKey(newEntrySet.getKey())){
					mergedMap.put(newEntrySet.getKey(), newEntrySet.getValue());
				}
			}
			if(!mergedMap.containsKey(persistedEntrySet.getKey())){
				mergedMap.put(persistedEntrySet.getKey(), persistedEntrySet.getValue());
			}
		}
		return mergedMap;
	}

	/**
	 * Populate data profiling json.
	 *
	 * @param inputPath the input path
	 * @param dataProfJsonDir the data prof json dir
	 * @param jsonFiles the json files
	 * @param dataProfilingType 
	 * @return the data profiling json
	 * @throws IOException 
	 */
	private DataProfilingJson populateDataProfilingJson(String inputPath,
			String dataProfJsonDir, File[] jsonFiles, Enable dataProfilingType)
			throws IOException {
		
		String fileName = null ;
		InputStream inputStream = null ;
		DataProfilingJson dataProfilingJson = null ;
		inputPath = inputPath.endsWith(File.separator)? inputPath : inputPath + File.separator ;
		inputPath = inputPath.replaceAll(File.separator, Constants.DOT).substring(1, inputPath.length());
		if(dataProfilingType.equals(Enable.TRUE)){
		inputPath = new StringBuilder(DataProfilingConstants.PROFILE).append(inputPath).append(DataProfilingConstants.CB).append(DataProfilingConstants.JSON).toString();	
		}else{
		inputPath = new StringBuilder(DataProfilingConstants.PROFILE).append(inputPath).append(DataProfilingConstants.JSON).toString();
		}
		try{
		for (File file : jsonFiles) {
			fileName = file.getName();
			if(fileName.equalsIgnoreCase(inputPath)){
				String filePath = dataProfJsonDir + file.getName() ;
				inputStream = new FileInputStream(filePath);
				Gson gson = new Gson();
				dataProfilingJson = gson.fromJson(new InputStreamReader(inputStream), DataProfilingJson.class);
			}
		}}catch (IOException ie) {
			LOGGER.error(ie);
		}finally{
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return dataProfilingJson;
	}

		

	private ArrayParamBuilder buildDataProfilingCommandString(JobConfig jobConfig,
			String inputPath, String dpBeanString, String hadoopHome,
			Remoter remoter, String userSuppliedJars) {
			
			ArrayParamBuilder arrayParamBuilder = new ArrayParamBuilder(8);
			if(jobConfig.getEnableDataProfiling().equals(Enable.TRUE) && jobConfig.getCriteriaBasedDataProfiling().equals(Enable.TRUE)){
			arrayParamBuilder.append("HADOOP_HOME"+Constants.HADOOP_COMMAND).append(Constants.HADOOP_COMMAND_TYPE).append(Constants.AGENT_ENV_VAR_NAME+Constants.DV_JAR_PATH)
			.append(DataProfilingConstants.DP_MAIN_CLASS).append(Constants.LIB_JARS).append(Constants.AGENT_ENV_VAR_NAME+
			Constants.GSON_JAR+","+Constants.AGENT_ENV_VAR_NAME+Constants.COMMON_JAR+","+
			Constants.AGENT_ENV_VAR_NAME+Constants.UTILITIES_JAR+userSuppliedJars+","+Constants.AGENT_ENV_VAR_NAME
			+Constants.LOG4J2_API_JAR+","+Constants.AGENT_ENV_VAR_NAME+Constants.LOG4J2_CORE_JAR).append(inputPath).append(dpBeanString);
			}else{
				arrayParamBuilder.append("HADOOP_HOME"+Constants.HADOOP_COMMAND).append(Constants.HADOOP_COMMAND_TYPE).append(Constants.AGENT_ENV_VAR_NAME+Constants.DV_JAR_PATH)
				.append(DataProfilingConstants.DP_NO_CRITERIA_MAIN_CLASS).append(Constants.LIB_JARS).append(Constants.AGENT_ENV_VAR_NAME+
				Constants.GSON_JAR+","+Constants.AGENT_ENV_VAR_NAME+Constants.COMMON_JAR+","+
				Constants.AGENT_ENV_VAR_NAME+Constants.UTILITIES_JAR+userSuppliedJars+","+Constants.AGENT_ENV_VAR_NAME
				+Constants.LOG4J2_API_JAR+","+Constants.AGENT_ENV_VAR_NAME+Constants.LOG4J2_CORE_JAR).append(inputPath).append(dpBeanString);
			}
			
			return arrayParamBuilder;
	}

	private ArrayParamBuilder buildCommandString(Config config,
			String inputPath, String dvFileDir, String dvBeanString,
			String hadoopHome, Master master, Remoter remoter,
			String userSuppliedJars) {
		ArrayParamBuilder sb=new ArrayParamBuilder(Constants.NINE);
		sb.append("HADOOP_HOME"+Constants.HADOOP_COMMAND).append(Constants.HADOOP_COMMAND_TYPE).append(Constants.AGENT_ENV_VAR_NAME+Constants.DV_JAR_PATH)
		.append(Constants.DV_MAIN_CLASS).append(Constants.LIB_JARS).append(Constants.AGENT_ENV_VAR_NAME+
		Constants.GSON_JAR+","+Constants.AGENT_ENV_VAR_NAME+Constants.COMMON_JAR+","+
		Constants.AGENT_ENV_VAR_NAME+Constants.UTILITIES_JAR+userSuppliedJars+","+Constants.AGENT_ENV_VAR_NAME
		+Constants.LOG4J2_API_JAR+","+Constants.AGENT_ENV_VAR_NAME+Constants.LOG4J2_CORE_JAR).
		append(inputPath).append(dvFileDir).append(dvBeanString);
		return sb;
	}

	private void sendDVJars(Remoter remoter, String jumbuneHome) {
		String jHomeTmp = jumbuneHome;
		
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.JUMBUNE_RELATIVE_DV_JAR_PATH);
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.GSON_JAR);
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.COMMON_JAR);
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.UTILITIES_JAR);
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.LOG4J2_API_JAR);
		remoter.sendJar(LIBDIR, jHomeTmp + Constants.LOG4J2_CORE_JAR);
	}

	/**
	 * This method Saves the data profiling details inside Jumbune Home folder.
	 *
	 * @param yamlConfig the yaml config
	 * @param dataProfilingJson the data profiling json
	 * @param dataProfilingType the data profiling type(denotes RuleBased or NonRule Based Profiling)
	 * @param loader the loader
	 * @param dpBeanString the dp bean string contain the data profiling parameters.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void saveDataProfilingJson(JobConfig jobConfig, String dataProfilingJson, int hashCode, String commandResponse) throws IOException {
		Gson gson = new Gson();
		DataProfilingJson daJson = new DataProfilingJson();
		DataProfilingFileDetails dataProfilingFileDetails = new DataProfilingFileDetails();
		Enable dataProfilingType = jobConfig.getCriteriaBasedDataProfiling();
		List<DataProfilingFileDetails> dataProfilingFileDetailsList = new ArrayList<DataProfilingFileDetails>();
		Map<String, String> fileCheckSumMap = new HashMap<String, String>();
		String jsonDir =  new StringBuilder(System.getenv("JUMBUNE_HOME")).append(File.separator).append(DataProfilingConstants.DATA_PROFILES).append(File.separator).toString();
		String hdfsFileName = jobConfig.getHdfsInputPath();
		hdfsFileName = hdfsFileName.endsWith(File.separator)? hdfsFileName : hdfsFileName + File.separator ;
		fileCheckSumMap = performHdfsLookUp(hdfsFileName,jobConfig,commandResponse);
		hdfsFileName = hdfsFileName.replaceAll(File.separator, Constants.DOT).substring(1, hdfsFileName.length());
		dataProfilingFileDetails.setFileName(hdfsFileName);
		dataProfilingFileDetails.setFileCheckSumMap(fileCheckSumMap);
		dataProfilingFileDetails.setDataProfilingType(dataProfilingType);
		dataProfilingFileDetails.setProfiledOutput(dataProfilingJson);
		dataProfilingFileDetails.setNoOfFiles(fileCheckSumMap.size());
		String fileName = DataProfilingConstants.PROFILE + hdfsFileName ; 
		if(dataProfilingType.equals(Enable.TRUE)){
			dataProfilingFileDetails.setHashCode(hashCode);
			fileName = fileName + DataProfilingConstants.CB ;
		}
		dataProfilingFileDetailsList.add(dataProfilingFileDetails);
		String [] folderName = hdfsFileName.split("\\.");
		daJson.setFolderName(folderName[0]);
		daJson.setDataProfilingFileDetails(dataProfilingFileDetailsList);
		
		File dataProfDir = new File(jsonDir);
		if (!dataProfDir.exists()) {
			dataProfDir.mkdir();
		}
		String jsonData = gson.toJson(daJson,DataProfilingJson.class);
		jsonDir = jsonDir + fileName + DataProfilingConstants.JSON;
		ConfigurationUtil.writeToFile(jsonDir, jsonData);
		LOGGER.debug("Persisted Data Profiling Json [" + jsonData + "]");

	}

	/**
	 * Performs hdfs look up and calculates the checksum of the files on hdfs is stored in a map.
	 *
	 * @param yamlConfig the yaml config
	 * @param hdfsFilePath the hdfs file path
	 * @param loader the loader
	 * @return the map containing file name and its checksum value.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Map<String, String> performHdfsLookUp(String hdfsFilePath, JobConfig jobConfig, String commmandResponse) throws IOException {
		Map<String, String> hashMap = new HashMap<String, String>();
		String[] fileResponse = commmandResponse.split(Constants.NEW_LINE);
		String filePath = null ;
		String dateTime = null ;
		for (int i = 0; i < fileResponse.length; i++) {
			String [] eachFileResponse = fileResponse[i].split("\\s+");
			filePath = eachFileResponse[eachFileResponse.length-1];
			dateTime = eachFileResponse[eachFileResponse.length-2] +  eachFileResponse[eachFileResponse.length-3];
			MessageDigest messageDigest = null;
			try {
				messageDigest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				LOGGER.error(e);
			}
			messageDigest.update(dateTime.getBytes(),0,dateTime.length());
			hashMap.put(filePath.replaceAll(File.separator, Constants.DOT), new BigInteger(1,messageDigest.digest()).toString(16));
			}
				
		return hashMap;
		
	}

	/** This method is responsible for giving the details of file that is present on HDFS.
	 * @param hdfsFilePath
	 * @param jobConfig
	 * @return
	 */
	private String getLsrCommandResponse(String hdfsFilePath,
			JobConfig jobConfig) {
		Remoter remoter = RemotingUtil.getRemoter(jobConfig, null);
		StringBuilder stringBuilder = new StringBuilder().append("HADOOP_HOME").append(Constants.HADOOP_COMMAND).append(" fs -lsr ").append(hdfsFilePath);
		CommandWritableBuilder commandWritableBuilder = new CommandWritableBuilder();
		commandWritableBuilder.addCommand(stringBuilder.toString(), false, null, CommandType.HADOOP_FS).populate(jobConfig, null);
		String commmandResponse = (String) remoter.fireCommandAndGetObjectResponse(commandWritableBuilder.getCommandWritable());
		return commmandResponse;
	}

	
	
	/**
	 * Gets the json file.
	 *
	 * @param filePath the file path at which the json files are kept.
	 * @return the json file kept at the specified path.
	 */
	private File[] getJsonFile(String filePath) {
		File[] getJsonFiles = null ;
			File jobFilePath = new File(filePath);

			if (jobFilePath.exists() && jobFilePath.isDirectory()) {
				getJsonFiles = jobFilePath.listFiles();
			}
		return getJsonFiles ;
	}
	
	private String getInputPaths(String hdfsFilePath,JobConfig jobConfig, String commandResponse) throws IOException{
		
		List<String> listOfFiles = new ArrayList<String>();
		String[] fileResponse = commandResponse.split(Constants.NEW_LINE);
		String filePath = null ;
		for (int i = 0; i < fileResponse.length; i++) {
			String [] eachFileResponse = fileResponse[i].split("\\s+");
			filePath = eachFileResponse[eachFileResponse.length-1];
			filePath = filePath.replaceAll(File.separator, Constants.DOT);
			listOfFiles.add(filePath);
		}
		return listOfFiles.toString().substring(1, listOfFiles.toString().length()-1);
		
	}

}
