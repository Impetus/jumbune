package org.jumbune.execution.utils;

import static org.jumbune.execution.utils.ExecutionConstants.COUNTERS;
import static org.jumbune.execution.utils.ExecutionConstants.ERRORANDEXCEPTION;
import static org.jumbune.execution.utils.ExecutionConstants.MAPRED_JOBCLIENT;
import static org.jumbune.execution.utils.ExecutionConstants.MESSAGE_COULD_NOT_EXECUTE_JOB;
import static org.jumbune.execution.utils.ExecutionConstants.RUNNING_JOB;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.jumbune.common.beans.DataProfilingBean;
import org.jumbune.common.beans.DataProfilingFileDetails;
import org.jumbune.common.beans.DataProfilingJson;
import org.jumbune.common.beans.DataValidationBean;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.FieldValidationBean;
import org.jumbune.common.beans.JobDefinition;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.LogSummaryLocation;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.ArrayParamBuilder;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.HadoopJobCounters;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.utils.MessageLoader;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.dataprofiling.utils.DataProfilingConstants;
import org.jumbune.execution.beans.JobProcessBean;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Helper class for all processors
 * 
 * 
 */
public class ProcessHelper {

	private static final Logger LOGGER = LogManager.getLogger(ProcessHelper.class);
	private static final MessageLoader MESSAGES = MessageLoader.getInstance();
	private static boolean isYarnJob = false;
	private static String jobJson = null;
	private static int noOfViolations = 0;

	private HadoopJobCounters hadoopJobCounters = null;

	public HadoopJobCounters getHadoopJobCounters() {
		return hadoopJobCounters;
	}

	public void setHadoopJobCounters(HadoopJobCounters hadoopJobCounters) {
		this.hadoopJobCounters = hadoopJobCounters;
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
	public Map<String, Map<String, String>> executeJar(String inputJarPath, boolean isCommandBasedAllowed,
			JumbuneRequest jumbuneRequest, boolean isDebugged) throws IOException {
		Config config = jumbuneRequest.getConfig();
		JobConfig jobConfig = (JobConfig) config;
		String hadoopType = FileUtil.getClusterInfoDetail(ExtendedConstants.HADOOP_TYPE);
		isYarnJob = hadoopType.equalsIgnoreCase(ExtendedConstants.YARN);
		List<JobDefinition> jobDefList = jobConfig.getJobs();

		Map<String, Map<String, String>> jobsCounterMap = new LinkedHashMap<String, Map<String, String>>();

		String location = jobConfig.getLogSummaryLocation().getProfilingFilesLocation();
		String jobName = jobConfig.getFormattedJumbuneJobName();
		if (jobDefList.size() > 1) {
			/*
			 * processMultipleJobDefRequest(inputJarPath, isCommandBasedAllowed,
			 * jumbuneRequest, isDebugged, jobDefList, jobsCounterMap, location, scanner,
			 * jobName);
			 */
		} else if (jobDefList.size() == 1) {
			processSingleJobDefRequest(inputJarPath, jumbuneRequest, isDebugged, jobDefList, jobsCounterMap, location,
					jobName);
		} else {
			LOGGER.debug(MESSAGES.get(MESSAGE_COULD_NOT_EXECUTE_JOB));
		}
		LOGGER.debug("Completed job jar execution, source path [+" + inputJarPath + "]");

		/**
		 * Collecting error information, group them, removing from original collection
		 * and putting back with new group id.
		 */
		if (jobsCounterMap.size() > 0) {
			return jobsCounterMap;
		} else {
			LOGGER.warn("Cannot create json as counter map is empty");
			return null;
		}
	}

	private void processSingleJobDefRequest(String inputJarPath, JumbuneRequest jumbuneRequest, boolean isDebugged,
			List<JobDefinition> jobDefList, Map<String, Map<String, String>> jobsCounterMap, String location,
			String jobName) throws IOException {

		JobConfig jobConfig = (JobConfig) jumbuneRequest.getConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		JobProcessBean bean = new JobProcessBean(jobDefList.get(0).getName(),
				getJobExecutionParams(RemotingUtil.getHadoopHome(cluster), jobDefList.get(0), inputJarPath,
						jobConfig.isMainClassDefinedInJobJar(), jobName));
		remoteLaunch(location, bean, jumbuneRequest);

		populateJobCounterMap(bean, jobsCounterMap, jumbuneRequest, isDebugged);
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

	private List<String> getJobExecutionParams(String hadoopHome, JobDefinition jobDef, String inputJarPath,
			boolean isMainClassDefined, String jobName) {

		List<String> jobExeParaList = new ArrayList<String>();
		jobExeParaList.add(hadoopHome + Constants.HADOOP_COMMAND);
		jobExeParaList.add(Constants.HADOOP_COMMAND_TYPE);
		jobExeParaList.add(inputJarPath);

		// if main class is defined in jar manifest, not including the job class
		// defined in json to the parameter list
		if (!isMainClassDefined) {
			jobExeParaList.add(jobDef.getJobClass());
		}
		jobExeParaList.add(Constants.LIB_JARS);
		jobExeParaList.add("AGENT_HOME" + Constants.LOG4J2_API_JAR + ",AGENT_HOME" + Constants.LOG4J2_CORE_JAR);
		if (jobDef.getParameters() != null) {
			populateJobExecParamList(jobDef, jobExeParaList);
		}

		return jobExeParaList;
	}

	private void populateJobExecParamList(JobDefinition jobDef, List<String> jobExeParaList) {
		String[] jobParam;
		if (jobDef.getParameters().contains("'")) {
			jobParam = jobDef.getParameters().split("'");
			if (jobParam.length % 2 == 0 && !jobDef.getParameters().endsWith("'")) {
				LOGGER.error("Wrong arguments passed. Reason - Either Even number of arguments passed ["
						+ Arrays.toString(jobParam) + "] OR Job Definition [" + jobDef.getParameters()
						+ "] doesn't ends with ' (a single quote)");
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

	/**
	 * This method matches the jobs that are either mentioned in config or user has
	 * provided through command with the once that are present in jar file. If jar
	 * doesn't contains Job classes that are provided by user it returns false else
	 * true is returned.
	 * 
	 * @param jarJobList
	 * @return boolean
	 */
	public boolean validateJobs(List<JobDefinition> configJobDefList, List<String> jarJobList) {
		// This is not a jar it might be a lib or some other folder
		if (jarJobList == null) {
			return false;
		}

		for (JobDefinition jobDef : configJobDefList) {
			String jsonJobName = jobDef.getJobClass();

			if (jsonJobName != null && (!jarJobList.contains(jsonJobName))) {
				return false;
			}
		}
		return true;
	}

	private String remoteLaunch(String location, JobProcessBean jobInfoBean, JumbuneRequest jumbuneRequest)
			throws IOException {

		Cluster cluster = jumbuneRequest.getCluster();
		String operatingUser = jumbuneRequest.getJobConfig().getOperatingUser();
		String appHome = JumbuneInfo.getHome() + File.separator;
		String relativePath = location.substring(appHome.length() - 1, location.length());
		Remoter remoter = RemotingUtil.getRemoter(cluster, appHome);
		String remoteHadoop = RemotingUtil.getHadoopHome(cluster) + "/bin/hadoop";
		StringBuilder params = new StringBuilder();
		List<String> jobParams = jobInfoBean.getJobExecParam();
		if (jobParams.isEmpty()) {
			throw new IllegalArgumentException("Invalid command parameters!!!");
		}
		String localPath = jobParams.get(2);
		String relativeJarLocation;
		if (localPath.contains("instrument")) {
			relativePath = localPath.substring(appHome.length() - 1, localPath.length());
			relativeJarLocation = relativePath.substring(0, relativePath.lastIndexOf('/'));
		} else {
			relativeJarLocation = relativePath.replace("/profiling", "/jar/profile/");// TODO:Relative path must check
																						// for proper path
		}
		remoter.sendJar(relativeJarLocation, localPath);
		jobParams.remove(0);
		jobParams.add(0, remoteHadoop);

		StringBuilder stringBuilder = new StringBuilder();
		for (String string : jobParams) {
			stringBuilder.append(string).append(RemotingConstants.JUMBUNE_REMOTE_COMMAND_SEPARATOR);// Jumbune Hadoop
																									// Remote Command
																									// Separator")
		}
		params.append(stringBuilder.substring(0,
				stringBuilder.lastIndexOf(RemotingConstants.JUMBUNE_REMOTE_COMMAND_SEPARATOR)));
		StringBuffer commandBuffer = new StringBuffer();
		commandBuffer.append(relativeJarLocation).append("|")
				.append(relativePath.contains("instrument") ? relativePath.subSequence(0, relativePath.lastIndexOf('/'))
						: relativePath);

		String response = null;
		try {
			CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
			String[] cmdParams = null;
			if (params.toString().contains("bin/hadoop")) {
				cmdParams = params.toString().split(RemotingConstants.JUMBUNE_REMOTE_COMMAND_SEPARATOR);
			}
			builder.addCommand(commandBuffer.toString(), true, Arrays.asList(cmdParams), CommandType.USER,
					operatingUser);
			response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
			jobInfoBean.setProcessResponse(response);
		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
		}
		return response;
	}

	/**
	 * This method will write the master log folder location to a file. The master
	 * log location information is required by services module. The file in which
	 * location is written is placed inside the lib/ directory lib directory being
	 * the one that holds all jars required by HTF framework If any change is made
	 * in location where services.properties file is created a similar change should
	 * be made in ServicesUtil class
	 * 
	 * @throws JumbuneException
	 */
	public void writeLogLocationToFile(LogSummaryLocation logSummaryLoc) {
		LOGGER.debug("writing logs information to file for services  ");
		String serviceJsonPath = org.jumbune.common.utils.JobConfigUtil.getServiceJsonPath();
		try {
			String jsonString = Constants.gson.toJson(logSummaryLoc, LogSummaryLocation.class);
			ConfigurationUtil.writeToFile(serviceJsonPath, jsonString, true);

		} catch (IOException e) {
			// Don't terminate operation if unable to copy logSummary location
			LOGGER.error("Unable to copy logSummary location", e);
		}
	}

	private static Map<String, Map<String, String>> getRemoteJobCounters(String processName, String response,
			Config config, boolean isDebugged) throws IOException {
		List<String> jobs = new LinkedList<String>();
		Map<String, String> map = null;
		Map<String, Map<String, String>> jobCounterMap = new LinkedHashMap<String, Map<String, String>>();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(response.getBytes())));

		String jobName = null;
		try {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				map = new HashMap<String, String>();
				if (line.contains(RUNNING_JOB)) {
					jobName = processJobName(jobs, line);
				} else if (line.contains(COUNTERS)) {
					jobName = processCounters(processName, map, jobCounterMap, reader, jobName, line);
				} else if (line.contains("Exception") || line.contains("Error")) {
					processExceptionCondition(processName, map, jobCounterMap, reader, jobName, line);
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		if (isDebugged && jobs.size() > 1) {
			JobConfig jobConfig = (JobConfig) config;
			String fileName = jobConfig.getMasterConsolidatedLogLocation() + "jobChain-" + processName
					+ "_instrumented.log";

			StringBuilder data = new StringBuilder();
			for (String string : jobs) {
				data.append(string).append("\n");
			}
			ConfigurationUtil.writeToFile(fileName, data.toString(), true);

		}
		return jobCounterMap;
	}

	private static void processExceptionCondition(String processName, Map<String, String> map,
			Map<String, Map<String, String>> jobCounterMap, BufferedReader reader, String jobName, String line)
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
		String jobName = line.split(RUNNING_JOB)[1];
		jobs.add(jobName);
		return jobName;
	}

	private static String processCounters(String processName, Map<String, String> map,
			Map<String, Map<String, String>> jobCounterMap, BufferedReader reader, String jobName, String line)
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
	 * 
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
	 * @param jobConfig
	 * @param isDebugged
	 * @throws IOException
	 */
	private void populateJobCounterMap(JobProcessBean bean, Map<String, Map<String, String>> jobCounterMap,
			JumbuneRequest jumbuneRequest, Boolean isDebugged) throws IOException {
		Config config = jumbuneRequest.getConfig();
		if (isYarnJob) {
			jobCounterMap
					.putAll(getRemoteYarnJobCounters(bean.getJobName(), bean.getProcessResponse(), config, isDebugged));
		} else {
			jobCounterMap
					.putAll(getRemoteJobCounters(bean.getJobName(), bean.getProcessResponse(), config, isDebugged));
		}
		hadoopJobCounters = new HadoopJobCounters();
		hadoopJobCounters.setJobCounterBeans(bean.getJobName(), bean.getProcessResponse(), jumbuneRequest);
	}

	private Map<? extends String, ? extends Map<String, String>> getRemoteYarnJobCounters(String processName,
			String response, Config config, Boolean isDebugged) throws IOException {
		JobConfig jobConfig = (JobConfig) config;
		List<String> jobs = new LinkedList<String>();
		Map<String, String> map = null;
		Map<String, Map<String, String>> jobCounterMap = new LinkedHashMap<String, Map<String, String>>();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
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
				processExceptionCondition(processName, map, jobCounterMap, reader, jobName, line);
			}
		}
		if (reader != null) {
			reader.close();
		}
		if (isDebugged && jobs.size() > 1) {
			String fileName = jobConfig.getMasterConsolidatedLogLocation() + "jobChain-" + processName
					+ "_instrumented.log";
			StringBuilder data = new StringBuilder();
			for (String string : jobs) {
				data.append(string).append("\n");
			}
			ConfigurationUtil.writeToFile(fileName, data.toString(), true);
		}
		return jobCounterMap;
	}

	public String remoteValidateDataDataSourceComparison(JumbuneRequest jumbuneRequest, String dscBean,
			String parameters) throws Exception {
		JobConfig jobConfig = (JobConfig) jumbuneRequest.getConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		sendDVJars(jumbuneRequest);
		sendDscJar(jumbuneRequest);
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		String commandToExecute = null;
		ArrayParamBuilder arrayParamBuilder = buildCommandStringDsc(jumbuneRequest, dscBean,
				getDscJobJarPathInAgent(jobConfig));
		String relativePath = "jobJars/" + jobConfig.getJumbuneJobName() + "/dv/";
		StringBuffer commandBuffer = new StringBuffer();

		commandBuffer = commandBuffer.append("jobJars/").append(jobConfig.getJumbuneJobName()).append("|")
				.append(relativePath.subSequence(0, relativePath.lastIndexOf('/')));
		String[] stringArray = (String[]) arrayParamBuilder.toList()
				.toArray(new String[arrayParamBuilder.toList().size()]);
		builder.addCommand(commandBuffer.toString(), true, Arrays.asList(stringArray), CommandType.USER,
				jobConfig.getOperatingUser()).setMethodToBeInvoked("executeRemoteCommandAsSudo");
		commandToExecute = arrayParamBuilder.toString();
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		if ((response == null) || (response.trim().equals(""))) {
			throw new IllegalArgumentException("DV::Invalid Hadoop Job Response!!!");
		}
		LOGGER.debug("Command executed [" + commandToExecute + "] and commandStrgot back response [" + response + "]");
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
		String line = null;
		String dscResultJson = null;
		boolean errorFound = false;
		StringBuilder errorString = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			String dvReport = "dvr:";
			if (line.contains(dvReport)) {
				int index = line.indexOf(dvReport);
				index += 4;
				dscResultJson = line.substring(index, line.length());
			} else if ((errorFound) || ((errorFound = line.contains("Exception")))
					|| ((errorFound = line.contains("Error")))) {
				errorString.append(line).append("\n");
			}
		}
		if ((dscResultJson == null) || (dscResultJson.isEmpty())) {
			LOGGER.error("Error string is: " + errorString.toString());
			throw new JumbuneException(ErrorCodesAndMessages.ERROR_EXECUTING_DV);
		}
		if (reader != null) {
			reader.close();
		}
		return dscResultJson;
	}

	private ArrayParamBuilder buildCommandStringDsc(JumbuneRequest jumbuneRequest, String dscBean,
			String jobJarPathInAgent) {
		JobConfig config = jumbuneRequest.getJobConfig();

		String prefix = config.getTempDirectory() + config.getFormattedJumbuneJobName();

		ArrayParamBuilder builder = new ArrayParamBuilder(9);
		builder.append("HADOOP_HOMEbin/hadoop").append("jar").append(prefix + "lib/jumbune-datavalidation-2.0.jar")
				.append("org.jumbune.datavalidation.dsc.DataSourceCompJobExecutor").append("-libjars");

		String libJars = prefix + "lib/gson-2.2.4.jar" + "," + prefix + "lib/jumbune-common-2.0.jar" + "," + prefix
				+ "lib/jumbune-utils-2.0.jar" + "," + prefix + "/lib/log4j-api-2.1.jar" + "," + prefix
				+ "/lib/log4j-core-2.1.jar";
		if (jobJarPathInAgent != null) {
			libJars = libJars + "," + prefix + jobJarPathInAgent;
		}
		builder.append(libJars).append(dscBean);
		return builder;
	}

	private void sendDscJar(JumbuneRequest jumbuneRequest) throws Exception {
		String inputPath = jumbuneRequest.getJobConfig().getInputFile();
		if ((inputPath != null) && (!inputPath.trim().isEmpty())) {
			JobConfigUtil.sendJars(jumbuneRequest, new String[] { inputPath });
		}
	}

	private String getDscJobJarPathInAgent(JobConfig jobConfig) {
		String inputPath = jobConfig.getInputFile();
		if ((inputPath == null) || (inputPath.trim().isEmpty())) {
			return null;
		}
		String jarName = inputPath.substring(inputPath.lastIndexOf('/') + 1);

		return jobConfig.getTempDirectory() + jobConfig.getJumbuneJobName() + "/lib/" + jarName;
	}

	/**
	 * <p>
	 * This method is used to apply data validation to the records fetched
	 * </p>
	 * 
	 * @param config
	 *            the job Config object containing infomation about master node and
	 *            agent port
	 * @param inputPath
	 *            the path to read data from hdfs
	 * @param dvBeanString
	 *            details regarding fetching data
	 * @return String data validation report
	 * @throws IOException
	 * @throws JumbuneException
	 */
	public String remoteValidateData(JumbuneRequest jumbuneRequest, String inputPath, String dvFileDir,
			String dvBeanString, String parameters) throws Exception {
		LOGGER.debug("Inside validateData method");
		Config config = jumbuneRequest.getConfig();
		JobConfig jobConfig = (JobConfig) config;
		Cluster cluster = jumbuneRequest.getCluster();
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		sendDVJars(jumbuneRequest);

		String relativePath = "jobJars/" + jobConfig.getJumbuneJobName() + "/dv/";
		// dynamic reducer logic starts
		// The number of reducers are deduced based on the max violations applied on
		// data and the minimum data that can be processed by reducer.
		// Max Violations are determined based on the data size to be processed and
		// number of violation that are applied.
		// Minimum data processed to be processed by reducer is determined on the basis
		// of java opts and the minimum size of an anomaly
		long dataSize = getDataSize(jumbuneRequest);
		long reduceOpts = getReduceChildJavaOpts(jumbuneRequest);
		long minDataProcByRed = reduceOpts / 4L;
		long threshold = minDataProcByRed * 1048576L / 100L;

		Type type = new TypeToken<DataValidationBean>() {
		}.getType();
		DataValidationBean dataValidationBean = (DataValidationBean) Constants.gson.fromJson(dvBeanString, type);
		List<FieldValidationBean> fieldValidationBeans = dataValidationBean.getFieldValidationList();
		for (FieldValidationBean fieldValidationBean : fieldValidationBeans) {
			String nullCheck = fieldValidationBean.getNullCheck();
			String dataType = fieldValidationBean.getDataType();
			String regex = fieldValidationBean.getRegex();
			checkViolations(nullCheck);
			checkViolations(dataType);
			checkViolations(regex);
		}
		long maxViolationForData = dataSize * noOfViolations / 60L;
		// Considering average number of violations to be reported
		long averageViolationsForData = maxViolationForData / 2L;
		long noOfreducers = averageViolationsForData / threshold;
		if (noOfreducers < 4L) {
			noOfreducers = 4L;
		}
		noOfViolations = 0; // reset the counter
		// dynamic reducer logic ends
		String commandToExecute = null;

		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		ArrayParamBuilder sb = buildTextValidationCommandString(jumbuneRequest, inputPath, dvFileDir, dvBeanString,
				noOfreducers, parameters);

		String[] stringArray = (String[]) sb.toList().toArray(new String[sb.toList().size()]);

		StringBuffer commandBuffer = new StringBuffer();

		commandBuffer = commandBuffer.append("jobJars/").append(jobConfig.getJumbuneJobName()).append("|")
				.append(relativePath.subSequence(0, relativePath.lastIndexOf('/')));

		builder.addCommand(commandBuffer.toString(), true, Arrays.asList(stringArray), CommandType.USER,
				jobConfig.getOperatingUser()).setMethodToBeInvoked("executeRemoteCommandAsSudo");
		commandToExecute = sb.toString();
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		if ((response == null) || (response.trim().isEmpty())) {
			throw new IllegalArgumentException("DV::Invalid Hadoop Job Response!!!");
		}
		LOGGER.debug("Command executed [" + commandToExecute + "] and commandStrgot back response [" + response + "]");

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
		String line = null;
		String dvJson = null;
		boolean errorFound = false;
		StringBuilder errorString = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			String dvReport = "dvr:";
			// checks the input stream for dvReport
			if (line.contains(dvReport)) {
				int index = line.indexOf(dvReport);
				index += 4;
				dvJson = line.substring(index, line.length());
			}
			// checking for any exception or error
			else if ((errorFound) || ((errorFound = line.contains("Exception")))
					|| ((errorFound = line.contains("Error")))) {
				errorString.append(line).append("\n");
			}
		}
		if ((dvJson == null) || (dvJson.isEmpty())) {
			LOGGER.error("Error string is: " + errorString.toString());
			throw new JumbuneException(ErrorCodesAndMessages.ERROR_EXECUTING_DV);
		}
		if (reader != null) {
			reader.close();
		}
		return dvJson;
	}

	/**
	 * This methods Checks the number of violations that have been given to be
	 * processed.
	 *
	 * @param violationChk
	 *            the violation chk
	 */
	private void checkViolations(String violationChk) {

		if (!violationChk.isEmpty()) {
			noOfViolations++;
		}
	}

	public String remoteJsonValidateData(JumbuneRequest jumbuneRequest, String inputPath, String dvFileDir,
			String parameters) throws Exception {
		LOGGER.debug("Inside JsonValidateData method");
		Config config = jumbuneRequest.getJobConfig();
		JobConfig jobConfig = (JobConfig) config;
		Cluster cluster = jumbuneRequest.getCluster();
		List<Map<String, String>> listParam = jobConfig.getFieldValidationList();
		String dataParameter = Constants.gson.toJson(listParam.get(0));
		String nullParameter = Constants.gson.toJson(listParam.get(1));
		String regexParameter = Constants.gson.toJson(listParam.get(2));
		LOGGER.debug(
				"DATAJSON: " + dataParameter + "NULLPARAMTER: " + nullParameter + "REGEXPARAMETER: " + regexParameter);
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		sendDVJars(jumbuneRequest);
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		String commandToExecute = null;
		ArrayParamBuilder sb = buildJsonCommand(jumbuneRequest, inputPath, dvFileDir, dataParameter,
				nullParameter, regexParameter, parameters);

		LOGGER.debug("Command executing [" + sb.toString() + "]");
		String[] stringArray = (String[]) sb.toList().toArray(new String[sb.toList().size()]);
		StringBuffer commandBuffer = new StringBuffer();
		String relativePath = "jobJars/" + jobConfig.getFormattedJumbuneJobName() + "jdv/";

		commandBuffer = commandBuffer.append("jobJars/").append(jobConfig.getJumbuneJobName()).append("|")
				.append(relativePath.subSequence(0, relativePath.lastIndexOf('/')));
		builder.addCommand(commandBuffer.toString(), true, Arrays.asList(stringArray), CommandType.USER,
				jobConfig.getOperatingUser()).setMethodToBeInvoked("executeRemoteCommandAsSudo");
		commandToExecute = sb.toString();
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		if ((response == null) || (response.trim().equals(""))) {
			throw new IllegalArgumentException("JsonDV::Invalid Hadoop Job Response!!!");
		}
		LOGGER.debug("Command executed [" + commandToExecute + "] and commandStrgot back response [" + response + "]");

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
		String line = null;
		String dvJson = null;
		boolean errorFound = false;
		StringBuilder errorString = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			String dvReport = "jsondvr:";
			// checks the input stream for dvReport
			if (line.contains(dvReport)) {
				int index = line.indexOf(dvReport);
				index += 8;
				dvJson = line.substring(index, line.length());

				// checking for any exception or error
			} else if ((errorFound) || ((errorFound = line.contains("Exception")))
					|| ((errorFound = line.contains("Error")))) {
				errorString.append(line).append("\n");
			}
		}
		if ((dvJson == null) || (dvJson.isEmpty())) {
			LOGGER.error("Error string is: " + errorString.toString());
			throw new JumbuneException(ErrorCodesAndMessages.ERROR_EXECUTING_DV);
		}
		if (reader != null) {
			reader.close();
		}
		return dvJson;
	}

	/**
	 * <p>
	 * This method is used to apply data validation to the xml records fetched
	 * </p>
	 * 
	 * @param config
	 *            the job Config object containing infomation about master node and
	 *            agent port
	 * @param inputPath
	 *            the path to read data from hdfs
	 * @param dvBeanString
	 *            details regarding fetching data
	 * @return String data validation report
	 * @throws IOException
	 * @throws JumbuneException
	 */
	public String remoteXmlValidateData(JumbuneRequest jumbuneRequest, String inputPath, String dvFileDir)
			throws Exception {
		LOGGER.debug("Inside xmlValidateData method");
		Config config = jumbuneRequest.getConfig();
		JobConfig jobConfig = (JobConfig) config;
		Cluster cluster = jumbuneRequest.getCluster();
		String jumbuneHome = JumbuneInfo.getHome();
		Remoter remoter = RemotingUtil.getRemoter(cluster, null);
		// sending generated xsd file to agent
		String xsdSourcePath = jumbuneHome + "/jobJars/" + jobConfig.getJumbuneJobName() + "/xdv/";
		String xsdDestinationPath = "/jobJars/" + jobConfig.getJumbuneJobName() + File.separator;
		remoter.sendLogFiles(xsdDestinationPath, xsdSourcePath);
		sendDVJars(jumbuneRequest);
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		String commandToExecute = null;
		StringBuffer commandBuffer = new StringBuffer();
		String relativePath = "jobJars/" + jobConfig.getJumbuneJobName() + "/xdv/";

		commandBuffer = commandBuffer.append("jobJars/").append(jobConfig.getJumbuneJobName()).append("|")
				.append(relativePath.subSequence(0, relativePath.lastIndexOf('/')));

		ArrayParamBuilder sb = buildXmlCommandString(jumbuneRequest, inputPath, dvFileDir,
				"AGENT_HOME" + File.separator + xsdDestinationPath + "/xdv/template.xsd");

		String[] stringArray = (String[]) sb.toList().toArray(new String[sb.toList().size()]);
		builder.addCommand(commandBuffer.toString(), true, Arrays.asList(stringArray), CommandType.USER,
				jobConfig.getOperatingUser()).setMethodToBeInvoked("executeRemoteCommandAsSudo");
		commandToExecute = sb.toString();
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		if ((response == null) || (response.trim().equals(""))) {
			throw new IllegalArgumentException("XDV::Invalid Hadoop Job Response!!!");
		}
		LOGGER.debug("Command executed [" + commandToExecute + "] and commandStrgot back response [" + response + "]");
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
		String line = null;
		String dvJson = null;
		boolean errorFound = false;
		StringBuilder errorString = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			String dvReport = "xdvr:";
			if (line.contains(dvReport)) {
				int index = line.indexOf(dvReport);
				index += 5;
				dvJson = line.substring(index, line.length());
			} else if ((errorFound) || ((errorFound = line.contains("Exception")))
					|| ((errorFound = line.contains("Error")))) {
				errorString.append(line).append("\n");
			}
		}
		if ((dvJson == null) || (dvJson.isEmpty())) {
			LOGGER.error("Error string is: " + errorString.toString());
			throw new JumbuneException(ErrorCodesAndMessages.ERROR_EXECUTING_DV);
		}
		if (reader != null) {
			reader.close();
		}
		return dvJson;
	}

	private ArrayParamBuilder buildTextValidationCommandString(JumbuneRequest jumbuneRequest, String inputPath,
			String dvFileDir, String dvBeanString, long noOfReducers, String additionalParams) {
		JobConfig config = jumbuneRequest.getJobConfig();

		ArrayParamBuilder sb = new ArrayParamBuilder(9);
		String prefix = config.getTempDirectory() + config.getFormattedJumbuneJobName();

		sb.append("HADOOP_HOMEbin/hadoop").append("jar").append(prefix + "lib/jumbune-datavalidation-2.0.jar")
				.append("org.jumbune.datavalidation.DataValidationJobExecutor").append("-libjars")
				.append(prefix + "lib/gson-2.2.4.jar" + "," + prefix + "lib/jumbune-common-2.0.jar" + "," + prefix
						+ "lib/jumbune-utils-2.0.jar" + "," + prefix + "/lib/log4j-api-2.1.jar" + "," + prefix
						+ "/lib/log4j-core-2.1.jar")

				.append(additionalParams).append(inputPath).append(dvFileDir).append("1000")
				.append(String.valueOf(noOfReducers)).append(dvBeanString);
		if (sb.toList().contains(null)) {
			sb.toList().remove(sb.toList().indexOf(null));
		}
		return sb;
	}

	private ArrayParamBuilder buildXmlCommandString(JumbuneRequest jumbuneRequest, String inputPath,
			String dvFileDir, String schemaPath) {
		JobConfig config = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();

		String prefix = config.getTempDirectory() + config.getFormattedJumbuneJobName();

		ArrayParamBuilder sb = new ArrayParamBuilder(7);
		sb.append("HADOOP_HOMEbin/hadoop").append("jar").append(prefix + "lib/jumbune-datavalidation-2.0.jar")
				.append("org.jumbune.datavalidation.xml.XmlDataValidationJobExecutor").append("-libjars")
				.append(prefix + "lib/gson-2.2.4.jar" + "," + prefix + "lib/jumbune-common-2.0.jar" + "," + prefix
						+ "lib/jumbune-utils-2.0.jar" + "," + prefix + "/lib/log4j-api-2.1.jar" + "," + prefix
						+ "/lib/xbean-2.2.0.jar" + "," + prefix + "/lib/xsom-20140925.jar" + "," + prefix
						+ "/lib/relaxngDatatype-1.0.jar" + "," + prefix + "/lib/log4j-core-2.1.jar")

				.append(inputPath).append(dvFileDir).append("1000").append(schemaPath);
		if (sb.toList().contains(null)) {
			sb.toList().remove(sb.toList().indexOf(null));
		}
		return sb;
	}

	private ArrayParamBuilder buildJsonCommand(JumbuneRequest jumbuneRequest, String inputPath,
			String dvFileDir, String dataParameter, String nullParameter, String regexParameter,
			String additionalParams) {
		JobConfig config = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();

		String prefix = config.getTempDirectory() + config.getFormattedJumbuneJobName();

		ArrayParamBuilder sb = new ArrayParamBuilder();
		sb.append("HADOOP_HOMEbin/hadoop").append("jar").append(prefix + "lib/jumbune-datavalidation-2.0.jar")
				.append("org.jumbune.datavalidation.json.JsonDataValidationExecutor").append("-libjars")
				.append(prefix + "lib/gson-2.2.4.jar" + "," + prefix + "lib/jumbune-common-2.0.jar" + "," + prefix
						+ "lib/jumbune-utils-2.0.jar" + "," + prefix + "/lib/log4j-api-2.1.jar" + "," + prefix
						+ "/lib/log4j-core-2.1.jar")

				.append(additionalParams).append(inputPath).append(dataParameter).append(nullParameter)
				.append(regexParameter).append(dvFileDir).append("1000");
		if (sb.toList().contains(null)) {
			sb.toList().remove(sb.toList().indexOf(null));
		}
		return sb;
	}

	private void sendDVJars(JumbuneRequest jumbuneRequest) throws Exception {
		String jumbuneHome = JumbuneInfo.getHome();
		String[] paths = new String[9];

		paths[0] = (jumbuneHome + "modules/jumbune-datavalidation-2.0.jar");
		paths[1] = (jumbuneHome + "lib/gson-2.2.4.jar");
		paths[2] = (jumbuneHome + "lib/jumbune-common-2.0.jar");
		paths[3] = (jumbuneHome + "lib/jumbune-utils-2.0.jar");
		paths[4] = (jumbuneHome + "/lib/log4j-api-2.1.jar");
		paths[5] = (jumbuneHome + "/lib/log4j-core-2.1.jar");
		paths[6] = (jumbuneHome + "/lib/xbean-2.2.0.jar");
		paths[7] = (jumbuneHome + "/lib/xsom-20140925.jar");
		paths[8] = (jumbuneHome + "/lib/relaxngDatatype-1.0.jar");

		JobConfigUtil.sendJars(jumbuneRequest, paths);
	}

	/**
	 * Launch data profiling job and process output.
	 *
	 * @param config
	 *            the config
	 * @param inputPath
	 *            the input path
	 * @param dpBeanString
	 *            the dp bean string
	 * @return the string
	 * @throws Exception
	 */
	public String launchDataProfilingJobAndProcessOutput(JumbuneRequest jumbuneRequest, String inputPath,
			String dpBeanString, DataProfilingBean dataProfilingBean, String parameters) throws Exception {
		String jumbuneHome = JumbuneInfo.getHome();
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		boolean dataProfilingJbLaunch = false;
		boolean mergeOutput = false;
		int hashCode = 0;
		if (jobConfig.getCriteriaBasedDataProfiling().equals(Enable.TRUE)) {
			hashCode = dataProfilingBean.getFieldProfilingRules().hashCode();
		}

		String dataProfJsonDir = new StringBuilder(jumbuneHome).append(File.separator)
				.append(DataProfilingConstants.DATA_PROFILES).append(File.separator).toString();
		File[] jsonFiles = getJsonFile(dataProfJsonDir);
		String lsrCommandResponse = getLsrCommandResponse(inputPath, cluster);

		// If there is no Json files present inside data profile folder then we trigger
		// new Data profiling job.
		if (jsonFiles == null) {
			dataProfilingJbLaunch = true;
		} else {
			Enable dataProEnable = null;
			DataProfilingJson dataProfJson = populateDataProfilingJson(inputPath, dataProfJsonDir, jsonFiles,
					jobConfig.getCriteriaBasedDataProfiling());
			// Here we check if data profiling is not null then we go for extracting and
			// comparing the files to be profiled.
			if (dataProfJson != null) {
				Map<String, String> fileCheckSumMap = new HashMap<String, String>();
				Map<String, String> currentFileCheckSumMap = new HashMap<String, String>();
				List<DataProfilingFileDetails> dataProfilingFileDetails = dataProfJson.getDataProfilingFileDetails();
				for (DataProfilingFileDetails dataprofiles : dataProfilingFileDetails) {
					int verifyHashCode = dataprofiles.getHashCode();
					dataProEnable = dataprofiles.getDataProfilingType();
					fileCheckSumMap = dataprofiles.getFileCheckSumMap();
					// Here we perform the hdfs look up i.e. computing the checksum of the current
					// files to be profiled and storing them in a map corresponding to their name.
					currentFileCheckSumMap = performHdfsLookUp(inputPath, jobConfig, lsrCommandResponse);
					String currentKey = null;
					String currentValue = null;
					if (currentFileCheckSumMap.size() < dataprofiles.getNoOfFiles()) {
						dataProfilingJbLaunch = true;
						break;
					}
					if (dataProEnable.equals(Enable.FALSE)) {
						// Here we check if the number of files to be profiled is greater the previously
						// profiled files, if true then
						// we take only the new file and run data profiling job for it and merge both
						// the outputs.
						if (currentFileCheckSumMap.size() > dataprofiles.getNoOfFiles()) {
							inputPath = getInputPaths(inputPath, jobConfig, lsrCommandResponse);
							String[] listOfFiles = inputPath.split(Constants.COMMA);
							List<String> fileList = new ArrayList<String>();
							for (String eachFile : listOfFiles) {
								fileList.add(eachFile.trim());
								for (Map.Entry<String, String> entrySet : fileCheckSumMap.entrySet()) {
									if (entrySet.getKey().equalsIgnoreCase(eachFile.trim())) {
										fileList.remove(eachFile.trim());
									}
								}

							}
							inputPath = fileList.toString().substring(1, fileList.toString().length() - 1);
							inputPath = inputPath.replace(Constants.DOT, File.separator);
							mergeOutput = true;
							jobJson = dataprofiles.getProfiledOutput();
							dataProfilingJbLaunch = true;
							break;
						}
					}

					// This loop takes out the entries in the current file checksum map.
					for (Map.Entry<String, String> currentEntrySet : currentFileCheckSumMap.entrySet()) {
						currentKey = currentEntrySet.getKey();
						// if the current key i.e. file on hdfs is there , then we check if the
						// corresponding file is present already.
						if (currentKey != null) {
							currentValue = fileCheckSumMap.get(currentKey);
							// If we found a file match then we compare their checksum value.
							if (currentValue != null && currentValue.equals(currentEntrySet.getValue())) {
								// Here we check if Rule Based Profiling is enabled and the saved rule is same
								// as current rule then we populate the saved output.
								if (dataProEnable.equals(Enable.TRUE) && hashCode == verifyHashCode) {
									jobJson = dataprofiles.getProfiledOutput();
								}
								// if Non-Rule based profiling is enabled then we populate the already saved
								// output.
								else if (dataProEnable.equals(Enable.FALSE)) {
									jobJson = dataprofiles.getProfiledOutput();
								}
								// if both above conditions are not met then we trigger a new data profiling
								// job.
								else {
									dataProfilingJbLaunch = true;
								}
							}
							// this is the condition where file checksum doesn't match and hence we trigger
							// data profiling job.
							else {
								dataProfilingJbLaunch = true;
							}
						}
					}
				}
			}
			// if the data profiling json is null then we trigger data profiling job.
			else {
				dataProfilingJbLaunch = true;
			}
		}

		if (dataProfilingJbLaunch) {
			String hadoopHome = RemotingUtil.getHadoopHome(cluster);
			Remoter remoter = RemotingUtil.getRemoter(cluster);

			sendDVJars(jumbuneRequest);

			CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
			ArrayParamBuilder sb = buildDataProfilingCommand(jumbuneRequest, inputPath, dpBeanString,
					hadoopHome, remoter, parameters);

			String[] stringArray = (String[]) sb.toList().toArray(new String[sb.toList().size()]);
			String relativePath = "jobJars/" + jobConfig.getJumbuneJobName() + "/dp/";
			StringBuffer commandBuffer = new StringBuffer();

			commandBuffer = commandBuffer.append("jobJars/").append(jobConfig.getJumbuneJobName()).append("|")
					.append(relativePath.subSequence(0, relativePath.lastIndexOf('/')));
			builder.addCommand(commandBuffer.toString(), true, Arrays.asList(stringArray), CommandType.USER,
					jumbuneRequest.getJobConfig().getOperatingUser())
					.setMethodToBeInvoked("executeRemoteCommandAsSudo");
			String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
			if ((response == null) || (response.trim().isEmpty())) {
				throw new IllegalArgumentException("Data Profiling::Invalid Hadoop Job Response!!!");
			}
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
			String line = null;
			String dataProfilingJson = null;
			boolean errorFound = false;

			StringBuilder errorString = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				String dataProfilingReport = "dataprofilingreport:";
				if (line.contains(dataProfilingReport)) {
					String[] dataProfilingReportArray = line.split("dataprofilingreport:");
					dataProfilingJson = dataProfilingReportArray[1];
				} else if ((errorFound) || ((errorFound = line.contains("Exception")))
						|| ((errorFound = line.contains("Error")))) {
					errorString.append(line).append("\n");
				}
			}
			// If merge output is true then we merge the output of the current job and saved
			// job.
			if (mergeOutput) {
				HashMap<String, Integer> persistedMapOutput = new ObjectMapper().readValue(jobJson, HashMap.class);
				HashMap<String, Integer> newMapOutput = new ObjectMapper().readValue(dataProfilingJson, HashMap.class);
				HashMap<String, Integer> mergedMap = new HashMap<String, Integer>();
				mergedMap = getMergeMapOutput(persistedMapOutput, newMapOutput, mergedMap);
				Map<String, Integer> sortedMap = getSortedMap(mergedMap);
				Gson dataGson = new GsonBuilder().disableHtmlEscaping().create();
				dataProfilingJson = dataGson.toJson(sortedMap);
			}
			// Going to dump the data profiling details into a json file.
			if (!dataProfilingJson.isEmpty() && dataProfilingJson != null) {
				saveDataProfilingJson(jobConfig, dataProfilingJson, hashCode, lsrCommandResponse);
			}
			return dataProfilingJson;
		}
		return jobJson;
	}

	private ArrayParamBuilder buildDataProfilingCommand(JumbuneRequest jumbuneRequest, String inputPath,
			String dpBeanString, String hadoopHome, Remoter remoter, String params) {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();

		String prefix = jobConfig.getTempDirectory() + jobConfig.getFormattedJumbuneJobName();

		ArrayParamBuilder builder = new ArrayParamBuilder(10);
		String mainClass;
		if ((jobConfig.getEnableDataProfiling() == Enable.TRUE)
				&& (jobConfig.getCriteriaBasedDataProfiling() == Enable.TRUE)) {
			mainClass = "org.jumbune.dataprofiling.DataProfilingJobExecutor";
		} else {
			mainClass = "org.jumbune.dataprofiling.DataProfNoCriteriaJobExecutor";
		}
		builder.append("HADOOP_HOMEbin/hadoop").append("jar").append(prefix + "lib/jumbune-datavalidation-2.0.jar")
				.append(mainClass).append("-libjars")
				.append(prefix + "lib/gson-2.2.4.jar" + "," + prefix + "lib/jumbune-common-2.0.jar" + "," + prefix
						+ "lib/jumbune-utils-2.0.jar" + "," + prefix + "/lib/log4j-api-2.1.jar" + "," + prefix
						+ "/lib/log4j-core-2.1.jar")
				.append(params).append(inputPath).append(dpBeanString);
		if (builder.toList().contains(null)) {
			builder.toList().remove(builder.toList().indexOf(null));
		}
		return builder;
	}

	/**
	 * This method Saves the data profiling details inside Jumbune Home folder.
	 *
	 * @param jobConfig
	 *            the job config
	 * @param dataProfilingJson
	 *            the data profiling json
	 * @param dataProfilingType
	 *            the data profiling type(denotes RuleBased or NonRule Based
	 *            Profiling)
	 * @param loader
	 *            the loader
	 * @param dpBeanString
	 *            the dp bean string contain the data profiling parameters.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void saveDataProfilingJson(JobConfig jobConfig, String dataProfilingJson, int hashCode,
			String commandResponse) throws IOException {
		DataProfilingJson daJson = new DataProfilingJson();
		DataProfilingFileDetails dataProfilingFileDetails = new DataProfilingFileDetails();
		Enable dataProfilingType = jobConfig.getCriteriaBasedDataProfiling();
		List<DataProfilingFileDetails> dataProfilingFileDetailsList = new ArrayList<DataProfilingFileDetails>();
		Map<String, String> fileCheckSumMap = new HashMap<String, String>();
		String jsonDir = new StringBuilder(System.getenv("JUMBUNE_HOME")).append(File.separator)
				.append(DataProfilingConstants.DATA_PROFILES).append(File.separator).toString();
		String hdfsFileName = jobConfig.getHdfsInputPath();
		hdfsFileName = hdfsFileName.endsWith(File.separator) ? hdfsFileName : hdfsFileName + File.separator;
		fileCheckSumMap = performHdfsLookUp(hdfsFileName, jobConfig, commandResponse);
		hdfsFileName = hdfsFileName.replaceAll(File.separator, Constants.DOT).substring(1, hdfsFileName.length());
		dataProfilingFileDetails.setFileName(hdfsFileName);
		dataProfilingFileDetails.setFileCheckSumMap(fileCheckSumMap);
		dataProfilingFileDetails.setDataProfilingType(dataProfilingType);
		dataProfilingFileDetails.setProfiledOutput(dataProfilingJson);
		dataProfilingFileDetails.setNoOfFiles(fileCheckSumMap.size());
		String fileName = DataProfilingConstants.PROFILE + hdfsFileName;
		if (dataProfilingType.equals(Enable.TRUE)) {
			dataProfilingFileDetails.setHashCode(hashCode);
			fileName = fileName + DataProfilingConstants.CB;
		}
		dataProfilingFileDetailsList.add(dataProfilingFileDetails);
		String[] folderName = hdfsFileName.split("\\.");
		daJson.setFolderName(folderName[0]);
		daJson.setDataProfilingFileDetails(dataProfilingFileDetailsList);

		File dataProfDir = new File(jsonDir);
		if (!dataProfDir.exists()) {
			dataProfDir.mkdir();
		}
		String jsonData = Constants.gson.toJson(daJson, DataProfilingJson.class);
		jsonDir = jsonDir + fileName + DataProfilingConstants.JSON;
		ConfigurationUtil.writeToFile(jsonDir, jsonData);
		LOGGER.debug("Persisted Data Profiling Json [" + jsonData + "]");
	}

	/**
	 * Performs hdfs look up and calculates the checksum of the files on hdfs is
	 * stored in a map.
	 *
	 * @param jobConfig
	 *            the job config
	 * @param hdfsFilePath
	 *            the hdfs file path
	 * @param loader
	 *            the loader
	 * @return the map containing file name and its checksum value.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Map<String, String> performHdfsLookUp(String hdfsFilePath, JobConfig jobConfig, String commmandResponse)
			throws IOException {
		Map<String, String> hashMap = new HashMap<String, String>();
		String[] fileResponse = commmandResponse.split(Constants.NEW_LINE);
		String filePath = null;
		String fileDateTime = null;
		for (int i = 0; i < fileResponse.length; i++) {
			String[] eachFileResponse = fileResponse[i].split("\\s+");
			if (eachFileResponse.length >= 3) {
				filePath = eachFileResponse[eachFileResponse.length - 1];
				fileDateTime = filePath + eachFileResponse[eachFileResponse.length - 2]
						+ eachFileResponse[eachFileResponse.length - 3];
				MessageDigest messageDigest = null;
				try {
					messageDigest = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					LOGGER.error(JumbuneRuntimeException.throwNoSuchAlgorithmException(e.getStackTrace()));
				}
				messageDigest.update(fileDateTime.getBytes(), 0, fileDateTime.length());
				hashMap.put(filePath.replaceAll(File.separator, Constants.DOT),
						new BigInteger(1, messageDigest.digest()).toString(16));
			}
		}
		return hashMap;
	}

	/**
	 * This method is responsible for giving the details of file that is present on
	 * HDFS.
	 * 
	 * @param hdfsFilePath
	 * @param jobConfig
	 * @return
	 */
	private String getLsrCommandResponse(String hdfsFilePath, Cluster cluster) {
		Remoter remoter = RemotingUtil.getRemoter(cluster, null);
		StringBuilder stringBuilder = new StringBuilder().append(Constants.HADOOP_HOME).append(Constants.BIN_HDFS)
				.append(Constants.DFS_LSR).append(hdfsFilePath);
		CommandWritableBuilder commandWritableBuilder = new CommandWritableBuilder(cluster, null);
		commandWritableBuilder.addCommand(stringBuilder.toString(), false, null, CommandType.HADOOP_FS);
		String commmandResponse = (String) remoter
				.fireCommandAndGetObjectResponse(commandWritableBuilder.getCommandWritable());
		return commmandResponse;
	}

	/**
	 * Gets the json file.
	 *
	 * @param filePath
	 *            the file path at which the json files are kept.
	 * @return the json file kept at the specified path.
	 */
	private File[] getJsonFile(String filePath) {
		File[] getJsonFiles = null;
		File jobFilePath = new File(filePath);

		if (jobFilePath.exists() && jobFilePath.isDirectory()) {
			getJsonFiles = jobFilePath.listFiles();
		}
		return getJsonFiles;
	}

	private String getInputPaths(String hdfsFilePath, JobConfig jobConfig, String commandResponse) throws IOException {

		List<String> listOfFiles = new ArrayList<String>();
		String[] fileResponse = commandResponse.split(Constants.NEW_LINE);
		String filePath = null;
		for (int i = 0; i < fileResponse.length; i++) {
			String[] eachFileResponse = fileResponse[i].split("\\s+");
			filePath = eachFileResponse[eachFileResponse.length - 1];
			if (filePath.contains(hdfsFilePath)) {
				filePath = filePath.replaceAll(File.separator, Constants.DOT);
				listOfFiles.add(filePath);
			}
		}
		return listOfFiles.toString().substring(1, listOfFiles.toString().length() - 1);

	}

	/**
	 * Populate data profiling json.
	 *
	 * @param inputPath
	 *            the input path
	 * @param dataProfJsonDir
	 *            the data prof json dir
	 * @param jsonFiles
	 *            the json files
	 * @param dataProfilingType
	 * @return the data profiling json
	 * @throws IOException
	 */
	private DataProfilingJson populateDataProfilingJson(String inputPath, String dataProfJsonDir, File[] jsonFiles,
			Enable dataProfilingType) throws IOException {

		String fileName = null;
		InputStream inputStream = null;
		DataProfilingJson dataProfilingJson = null;
		inputPath = inputPath.endsWith(File.separator) ? inputPath : inputPath + File.separator;
		inputPath = inputPath.replaceAll(File.separator, Constants.DOT).substring(1, inputPath.length());
		if (dataProfilingType.equals(Enable.TRUE)) {
			inputPath = new StringBuilder(DataProfilingConstants.PROFILE).append(inputPath)
					.append(DataProfilingConstants.CB).append(DataProfilingConstants.JSON).toString();
		} else {
			inputPath = new StringBuilder(DataProfilingConstants.PROFILE).append(inputPath)
					.append(DataProfilingConstants.JSON).toString();
		}

		try {
			for (File file : jsonFiles) {
				fileName = file.getName();
				if (fileName.equalsIgnoreCase(inputPath)) {
					String filePath = dataProfJsonDir + file.getName();
					inputStream = new FileInputStream(filePath);
					dataProfilingJson = Constants.gson.fromJson(new InputStreamReader(inputStream),
							DataProfilingJson.class);
				}
			}
		} catch (IOException ie) {
			LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(ie.getStackTrace()));
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return dataProfilingJson;
	}

	/**
	 * Gets the merged map containing the results of both saved job and newly run
	 * file job.
	 *
	 * @param persistedMapOutput
	 *            the persisted map output
	 * @param newMapOutput
	 *            the new map output
	 * @param mergedMap
	 *            the merged map
	 * @return the merge map output
	 */
	private HashMap<String, Integer> getMergeMapOutput(HashMap<String, Integer> persistedMapOutput,
			HashMap<String, Integer> newMapOutput, HashMap<String, Integer> mergedMap) {
		for (Map.Entry<String, Integer> persistedEntrySet : persistedMapOutput.entrySet()) {
			for (Map.Entry<String, Integer> newEntrySet : newMapOutput.entrySet()) {
				if (persistedEntrySet.getKey().equalsIgnoreCase(newEntrySet.getKey())) {
					int value = persistedEntrySet.getValue() + newEntrySet.getValue();
					mergedMap.put(persistedEntrySet.getKey(), value);
				}
				if (!mergedMap.containsKey(newEntrySet.getKey())) {
					mergedMap.put(newEntrySet.getKey(), newEntrySet.getValue());
				}
			}
			if (!mergedMap.containsKey(persistedEntrySet.getKey())) {
				mergedMap.put(persistedEntrySet.getKey(), persistedEntrySet.getValue());
			}
		}
		return mergedMap;
	}

	/**
	 * Gets the sorted map.
	 *
	 * @param mergedMap
	 *            the merged map
	 * @return the sorted map
	 */
	private Map<String, Integer> getSortedMap(HashMap<String, Integer> mergedMap) {

		List<Map.Entry<String, Integer>> sortedList = new LinkedList<Map.Entry<String, Integer>>(mergedMap.entrySet());
		Collections.sort(sortedList, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : sortedList) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	/**
	 * Gets the size of the data(in Bytes) kept on the mentioned path on HDFS.
	 *
	 * @param jumbuneRequest
	 *            the jumbune request
	 * @return the data size
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private long getDataSize(JumbuneRequest jumbuneRequest) throws IOException {
		JobConfig jobConfig = (JobConfig) jumbuneRequest.getConfig();
		String inputDataPath = jobConfig.getHdfsInputPath();
		Remoter remoter = RemotingUtil.getRemoter(jumbuneRequest.getCluster(), "");
		StringBuilder command = new StringBuilder();
		command.append("HADOOP_HOME/bin/hadoop fs -du -s " + inputDataPath);
		CommandWritableBuilder builder = new CommandWritableBuilder(jumbuneRequest.getCluster());
		builder.addCommand(command.toString(), false, null, CommandType.USER, jobConfig.getOperatingUser());
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());

		long dataSize = 0L;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getBytes())));
			String line = null;
			String lastLine = null;
			dataSize = 0L;
			String[] splits = null;
			while ((line = reader.readLine()) != null) {
				lastLine = line;
			}
			splits = lastLine.split("\\s");
			if (splits.length < 2) {
				LOGGER.error("Failed to get data size for hdfs file [" + inputDataPath + "]");
				throw new RuntimeException("Failed to get data size for hdfs file [" + inputDataPath + "]");
			}
			if (splits[0].equalsIgnoreCase(inputDataPath)) {
				dataSize = Long.parseLong(splits[1]);
			} else {
				dataSize = Long.parseLong(splits[0]);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		LOGGER.debug("CALCULATED DATA SIZE, size of the given job input data location [" + dataSize + "]");
		return dataSize;
	}

	/**
	 * Gets Reduce child java opts in Mb.
	 *
	 * @param jumbuneRequest
	 *            the cluster
	 * @return the long
	 */
	private static long getReduceChildJavaOpts(JumbuneRequest jumbuneRequest) {
		String destinationRelativePathOnLocal = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC
				+ jumbuneRequest.getJobConfig().getJumbuneJobName();
		String reduceChildJavaOpts = RemotingUtil.getHadoopConfigurationValue(jumbuneRequest, "mapred-site.xml",
				"mapreduce.reduce.java.opts");
		long reduceChildJavaOptsinMB = 200;
		if (reduceChildJavaOpts != null) {
			reduceChildJavaOptsinMB = ConfigurationUtil.getJavaOptsinMB(reduceChildJavaOpts);
		}
		if (reduceChildJavaOptsinMB == 0) {
			destinationRelativePathOnLocal = destinationRelativePathOnLocal + File.separator + "mapred-site.xml";
			String childJavaOpts = RemotingUtil.parseConfiguration(destinationRelativePathOnLocal,
					"mapred.child.java.opts");
			if (childJavaOpts != null) {
				reduceChildJavaOptsinMB = ConfigurationUtil.getJavaOptsinMB(childJavaOpts);
			}
		}
		// setting to standard java opts if not set
		if (reduceChildJavaOptsinMB == 0) {
			reduceChildJavaOptsinMB = 200;
		}
		return reduceChildJavaOptsinMB;
	}

}