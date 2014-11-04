/*
 * 
 */
package org.jumbune.common.utils;

import static org.jumbune.common.utils.Constants.COLON;
import static org.jumbune.common.utils.Constants.SPACE;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.AttemptDetails;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.JobDetails;
import org.jumbune.common.beans.JobOutput;
import org.jumbune.common.beans.PhaseDetails;
import org.jumbune.common.beans.PhaseOutput;
import org.jumbune.common.beans.SupportedApacheHadoopVersions;
import org.jumbune.common.beans.TaskDetails;
import org.jumbune.common.beans.TaskOutputDetails;
import org.jumbune.common.yaml.config.Config;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.ApiInvokeHintsEnum;
import org.jumbune.remoting.common.RemotingConstants;

import com.google.gson.Gson;


/**
 * Gets all details pertaining to a job, and its phases.
 */
public class HadoopLogParser {

	/** The LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(HadoopLogParser.class);

	/** The Constant CONVERSION_FACTOR_BYTES_TO_KB. */
	private static final int CONVERSION_FACTOR_BYTES_TO_KB = 1000;
	
	/** The Constant LIB. */
	private static final String LIB = "lib/";
	
	/** The Constant RUMEN. */
	private static final String RUMEN = "rumen";
	
	/** The Constant SETUP. */
	private static final String SETUP = "SETUP";
	
	/** The Constant REDUCE. */
	private static final String REDUCE = "REDUCE";
	
	/** The Constant SUCCESS. */
	private static final String SUCCESS = "SUCCESS";
	
	/** The Constant FILE_PREFIX. */
	private static final String FILE_PREFIX = "file://";
	
	/** The Constant TOPOLOGY_FILE. */
	private static final String TOPOLOGY_FILE = "topology";
	
	/** The Constant JSON_FILE. */
	private static final String JSON_FILE = "job-trace.json";
	
	
	/** The Constant MKDIR_CMD. */
	private static final String MKDIR_CMD = "mkdir -p ";
	
	/** The Constant JAVA_CP_CMD. */
	private static final String JAVA_CP_CMD = "java -cp ";
	
	/** The Constant HADOOP_JAR_CONFIF_FILE. */
	private static final String HADOOP_JAR_CONFIF_FILE = "RumenReqJars.properties";
	
	/** The Constant RUMEN_MAIN_CLASS_OLD. */
	private static final String RUMEN_MAIN_CLASS_OLD = "org.jumbune.org.apache.hadoop.tools.rumen.TraceBuilder";
	
	private static final String YARN_JOB_STATS_UTILITY_CLASS = "org.jumbune.common.utils.yarn.YarnJobStatsUtility";
	
	private static final String YARN_JOB_STATS_UTILITY_CLASS_PARSE_METHOD = "parseAndGetJobStats";	
	
	/** The Constant LOGS **/
	private static final String LOGS = "/logs/";
	
	/** The Constant HISTORY_DIR_SUFFIX. */
	private static final String HISTORY_DIR_SUFFIX = "/history/done/version-1";
	
	/** The Constant HISTORY_DIR_SUFFIX_OLD. */
	private static final String HISTORY_DIR_SUFFIX_OLD = "/history/";
	
	private static final String HISTORY_DIR_SUFFIX_YARN = "/tmp/hadoop-yarn/staging/history/done/";
		
	private static final String LOG_DIR_SUFFIX = "000000/*";
	
	private static final String HDFS_FILE_GET_COMMAND = "/bin/hadoop fs -get";
	
	/** The Constant wildcard**/
	private static final String WILDCARD="/*";

	/**
	 * Gets the job details.
	 *
	 * @param yamlLoader the loader
	 * @param jobID the job id
	 * @return the job details
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException 
	 */
	public JobOutput getJobDetails(Loader loader, String jobID) throws IOException{
		YamlLoader yamlLoader = (YamlLoader)loader;
		String appHome = yamlLoader.getjHome() + File.separator;
		YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
		String agentHome = RemotingUtil.getAgentHome(yamlConfig);
		Remoter remoter = RemotingUtil.getRemoter(yamlLoader, appHome);
		String remoteHadoop = RemotingUtil.getHadoopHome(remoter, yamlConfig) + File.separator;
		String logsHistory = null;
		SupportedApacheHadoopVersions hadoopVersion = RemotingUtil.getHadoopVersion(yamlConfig);
		String user = yamlConfig.getMaster().getUser();
		logsHistory = changeLogHistoryPathAccToHadoopVersion(remoteHadoop,
				hadoopVersion, user);
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		
		boolean isYarn = yamlConfig.getEnableYarn().equals(Enable.TRUE);
		String logfilePath = null ;
		String relLocalPath = null;
		if(!isYarn){
			logfilePath = getLogFilePath(jobID, remoter, logsHistory,
					builder);
		relLocalPath  = Constants.JOB_JARS_LOC + yamlLoader.getJumbuneJobName();
		String relRemotePath = relLocalPath + RUMEN;
		StringBuilder stringAppender = new StringBuilder(agentHome);
		stringAppender.append(File.separator).append(relRemotePath).append(File.separator);
		// make rumen related directory and files
		String pathToRumenDir = stringAppender.toString();
		String jsonFilepath = pathToRumenDir + JSON_FILE;
		String topologyFilePath = pathToRumenDir + TOPOLOGY_FILE;
		
		builder.getCommandBatch().clear();
		builder.addCommand(MKDIR_CMD + pathToRumenDir, false, null);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		
		// preparing command for rumen processing
		remoteHadoop = remoteHadoop + File.separator;
		String remoteHadoopLib = remoteHadoop + LIB;
		Properties props = loadHadoopJarConfigurationProperties();
		
		String coreJar;
		if(SupportedApacheHadoopVersions.HADOOP_NON_YARN.equals(hadoopVersion)) {
			coreJar = remoteHadoop + props.getProperty("CORE_JAR");	
		}else {
			coreJar = remoteHadoop + WILDCARD;
		}
		
		String commonsLoggingJar = agentHome + LIB + props.getProperty("COMMONS_LOGGING_JAR");
		String commonsCliJar = remoteHadoopLib + props.getProperty("COMMONS_CLI_JAR");
		String commonsConfigurationJar = agentHome + LIB + props.getProperty("COMMONS_CONFIGURATION_JAR");
		String commonsLangJar = agentHome + LIB + props.getProperty("COMMONS_LANG_JAR");
		String jacksonMapperAslJar = agentHome + LIB + props.getProperty("JACKSON_MAPPER_ASL_JAR");
		String jacksonMapperCoreJar = agentHome + LIB + props.getProperty("JACKSON_MAPPER_CORE_JAR");
		String rumenJar = agentHome + LIB + props.getProperty("RUMEN_JAR")+"-"+Versioning.BUILD_VERSION+Versioning.DISTRIBUTION_NAME+".jar";
		
		StringBuilder sb = new StringBuilder(JAVA_CP_CMD);
		
		checkHadoopVersionsForRumen(hadoopVersion, logfilePath, jsonFilepath,
				topologyFilePath, coreJar, commonsLoggingJar,
				commonsCliJar, commonsConfigurationJar, commonsLangJar,
				jacksonMapperAslJar, jacksonMapperCoreJar, rumenJar, sb);
		LOGGER.debug("Rumen processing command [" + sb.toString()+"]");
		startRumenProcessing(remoter, relLocalPath, relRemotePath, sb);
		remoter = RemotingUtil.getRemoter(yamlLoader, appHome);
		remoter.receiveLogFiles(relLocalPath, relRemotePath);
		LOGGER.debug("Received log files from:"+ relRemotePath);
		// process json
		Gson gson = new Gson();
		JobDetails jobDetails = extractJobDetails(appHome, relLocalPath, gson);
		return convertToFinalOutput(jobDetails);
		}else{
	 		
			String relativeRemotePath = Constants.JOB_JARS_LOC + yamlLoader.getJumbuneJobName() + File.separator + jobID;
			String remotePath = agentHome + relativeRemotePath;
			getLogFilePathForYarn(jobID, remoter, logsHistory, builder,yamlLoader.getYamlConfiguration(),agentHome,remotePath);
			
			relLocalPath  = Constants.JOB_JARS_LOC + yamlLoader.getJumbuneJobName();
			remoter = RemotingUtil.getRemoter(yamlLoader, appHome);
			remoter.receiveLogFiles(relLocalPath, relativeRemotePath);
			String localPath =	appHome + relLocalPath + jobID;
			java.lang.reflect.Method method = null;
			Class<?> yarnJobStatsUtility = null;
			try {
				yarnJobStatsUtility = Class.forName(YARN_JOB_STATS_UTILITY_CLASS);
				method = yarnJobStatsUtility.getDeclaredMethod(YARN_JOB_STATS_UTILITY_CLASS_PARSE_METHOD, String.class);
				return 	(JobOutput) method.invoke(yarnJobStatsUtility.newInstance(), localPath);
			} catch (Exception e) {
				LOGGER.error("Error while instanting class", e.getCause());
			}
			}
			return null;
	}
	/**
	 * 	This api is used to fetch the .hist files containing the job details
	 *
	 * @param jobID the job id is the jobname that ran on hadoop
	 * @param remoter the remoter
	 * @param logsHistory the logs history is the location of the .hist files on hdfs
	 * @param builder the builder
	 * @param yamlConfig the yaml config
	 * @param agentHome the agent home
	 * @param relRemotePath the rel remote path
	 * @return the log file path for yarn
	 */
	private void getLogFilePathForYarn(String jobID, Remoter remoter,
			String logsHistory, CommandWritableBuilder builder, Config config, String agentHome,String relRemotePath) {
		YamlConfig yamlConfig = (YamlConfig)config;
		builder.addCommand(MKDIR_CMD + relRemotePath, false, null);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		String[] dateArr = currentDate.split("-");
		CommandWritableBuilder fsGetBuilder = new CommandWritableBuilder();
		String hadoopHome = RemotingUtil.getHadoopHome(yamlConfig);
		StringBuffer commandToExecute = new StringBuffer().append(hadoopHome).append(HDFS_FILE_GET_COMMAND).append(Constants.SPACE).append(logsHistory).append(dateArr[0]).append(File.separator).append(dateArr[1]).append(File.separator).append(dateArr[2])
		.append(File.separator).append(LOG_DIR_SUFFIX).append(Constants.SPACE).append(relRemotePath);
		LOGGER.info("File get Command" + commandToExecute.toString());
		fsGetBuilder.addCommand(commandToExecute.toString(),false,null).populate(yamlConfig, null);;
		remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());
	}

	private String getLogFilePath(String jobID, Remoter remoter,
			String logsHistory, CommandWritableBuilder builder) {
		String command = jobID + RemotingConstants.SINGLE_SPACE + logsHistory;
		builder.addCommand(command, false, null).setApiInvokeHints(ApiInvokeHintsEnum.GET_JOB_LOG_FILE_OP);
		return (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		
	}

	/**
	 * Start rumen processing.
	 *
	 * @param remoter the remoter
	 * @param relLocalPath the rel local path
	 * @param relRemotePath the rel remote path
	 * @param sb the sb
	 */
	private void startRumenProcessing(Remoter remoter, String relLocalPath,
			String relRemotePath, StringBuilder sb) {
		// Starting rumen processing on master
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(sb.toString(), false, null);
		remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		LOGGER.info("Completed Rumen processing");
	}

	/**
	 * Load hadoop jar configuration properties.
	 *
	 * @return the properties
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Properties loadHadoopJarConfigurationProperties()
			throws IOException {
		Properties props = new Properties();
		props.load(HadoopLogParser.class.getClassLoader().getResourceAsStream(HADOOP_JAR_CONFIF_FILE));
		return props;
	}

	/**
	 * Extract job details.
	 *
	 * @param appHome the app home
	 * @param relLocalPath the rel local path
	 * @param gson the gson
	 * @return the job details
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private JobDetails extractJobDetails(String appHome, String relLocalPath,
			Gson gson) throws IOException {
		StringBuilder localJsonPath = new StringBuilder(appHome);
		localJsonPath.append(File.separator).append(relLocalPath).append(RUMEN).append(File.separator).append(JSON_FILE);
		String json = FileUtil.readFileIntoString(localJsonPath.toString());
		return gson.fromJson(json, JobDetails.class);

	}

	/**
	 * Change log history path acc to hadoop version.
	 *
	 * @param remoteHadoop the remote hadoop
	 * @param hadoopVersion constants for hadoop specific versions.
	 * @param user TODO
	 * @return the string
	 */
	private String changeLogHistoryPathAccToHadoopVersion(String remoteHadoop,
			SupportedApacheHadoopVersions hadoopVersion, String user) {
		String logsHistory;
		if(SupportedApacheHadoopVersions.HADOOP_NON_YARN.equals(hadoopVersion)) {
			logsHistory = remoteHadoop + LOGS + HISTORY_DIR_SUFFIX;
		}else if(SupportedApacheHadoopVersions.HADOOP_MAPR.equals(hadoopVersion) || SupportedApacheHadoopVersions.HADOOP_YARN.equals(hadoopVersion)){
			logsHistory = HISTORY_DIR_SUFFIX_YARN;
		}else{
			logsHistory = remoteHadoop + LOGS + user + HISTORY_DIR_SUFFIX;
		}
		return logsHistory;
	}

	/**
	 * 
	 * @param hadoopVersion
	 * @param logfilePath
	 * @param jsonFilepath
	 * @param topologyFilePath
	 * @param coreJar
	 * @param commonsLoggingJar
	 * @param commonsCliJar
	 * @param commonsConfigurationJar
	 * @param commonsLangJar
	 * @param jacksonMapperAslJar
	 * @param jacksonMapperCoreJar
	 * @param rumenJar
	 * @param sb
	 */
	private void checkHadoopVersionsForRumen(
			SupportedApacheHadoopVersions hadoopVersion, String logfilePath,
			String jsonFilepath, String topologyFilePath, String coreJar,
			String commonsLoggingJar, String commonsCliJar,
			String commonsConfigurationJar, String commonsLangJar,
			String jacksonMapperAslJar, String jacksonMapperCoreJar,
			String rumenJar, StringBuilder sb) {
		/*if(SupportedApacheHadoopVersions.HADOOP_MAPR.equals(hadoopVersion)) {
			// need to provide Jumbune modified rumen jar explicitly for older versions of hadoop 
			 sb.append(coreJar).append(COLON).append(rumenJar).append(COLON).append(commonsLoggingJar).append(COLON)
				.append(COLON).append(commonsCliJar).append(COLON).append(commonsConfigurationJar).append(COLON).append(commonsLangJar)
				.append(COLON).append(jacksonMapperAslJar).append(COLON)
				.append(jacksonMapperCoreJar).append(SPACE).append(RUMEN_MAIN_CLASS_OLD).append(SPACE).append(FILE_PREFIX).append(jsonFilepath)
				.append(SPACE).append(FILE_PREFIX).append(topologyFilePath).append(SPACE).append(FILE_PREFIX).append(logfilePath);
			}*/
			// fallback for default hadoop version
			 sb.append(coreJar).append(COLON).append(rumenJar).append(COLON).append(commonsLoggingJar).append(COLON).append(commonsCliJar).append(COLON)
				.append(commonsConfigurationJar).append(COLON).append(commonsLangJar).append(COLON).append(jacksonMapperAslJar).append(COLON)
				.append(jacksonMapperCoreJar).append(SPACE).append(RUMEN_MAIN_CLASS_OLD).append(SPACE).append(FILE_PREFIX).append(jsonFilepath)
				.append(SPACE).append(FILE_PREFIX).append(topologyFilePath).append(SPACE).append(FILE_PREFIX).append(logfilePath);
	}

	/**
	 * Process the json and convert to a POJO.
	 *
	 * @param jobDetails the job details
	 * @return the job output
	 */
	private JobOutput convertToFinalOutput(JobDetails jobDetails) {
		JobOutput jobOutput = new JobOutput();
		jobOutput.setJobID(jobDetails.getJobID());
		jobOutput.setJobName(jobDetails.getJobName());
		jobOutput.setUser(jobDetails.getUser());
		jobOutput.setOutcome(jobDetails.getOutcome());
		long startTime = jobDetails.getSubmitTime();
		long finishTime = jobDetails.getFinishTime();
		long timeInMilliSec = (finishTime - startTime);
		long timeInSecs = timeInMilliSec / CONVERSION_FACTOR_BYTES_TO_KB;
		jobOutput.setTotalTime(timeInSecs);
		jobOutput.setTotalTimeInMilliSec(timeInMilliSec);
		PhaseOutput phaseOutput = new PhaseOutput();

		// add details for map tasks
		List<TaskDetails> mapTasks = jobDetails.getMapTasks();
		PhaseDetails mapDetails = addPhaseDetails(mapTasks, startTime);
		phaseOutput.setMapDetails(mapDetails);
		// add details for reduce tasks
		List<TaskDetails> reduceTasks = jobDetails.getReduceTasks();
		PhaseDetails reduceDetails = addPhaseDetails(reduceTasks, startTime);
		phaseOutput.setReduceDetails(reduceDetails);

		// separation of setup and cleanup tasks
		List<TaskDetails> otherTasks = jobDetails.getOtherTasks();
		List<TaskDetails> setupTasks = new ArrayList<TaskDetails>();
		List<TaskDetails> cleanupTasks = new ArrayList<TaskDetails>();
		for (TaskDetails td : otherTasks) {
			if (SETUP.equals(td.getTaskType())) {
				setupTasks.add(td);
			} else {
				cleanupTasks.add(td);
			}
		}
		// add details for setup
		PhaseDetails setupDetails = addPhaseDetails(setupTasks, startTime);
		phaseOutput.setSetupDetails(setupDetails);

		// add details for cleanup tasks
		PhaseDetails cleanupDetails = addPhaseDetails(cleanupTasks, startTime);
		phaseOutput.setCleanupDetails(cleanupDetails);
		jobOutput.setPhaseOutput(phaseOutput);
		LOGGER.debug("Converted the Json to final output");
		return jobOutput;
	}

	/**
	 * Adds detail for a MapReduce phase.
	 *
	 * @param tasks the tasks
	 * @param startTime the start time
	 * @return the phase details
	 */
	private PhaseDetails addPhaseDetails(List<TaskDetails> tasks, long startTime) {
		if ((tasks == null) || (tasks.size() == 0)) {
			return null;
		}
		PhaseDetails pd = new PhaseDetails();
		List<TaskOutputDetails> taskOutputDetails = new ArrayList<TaskOutputDetails>();
		TaskOutputDetails tod;
		for (TaskDetails td : tasks) {
			tod = new TaskOutputDetails();
			tod.setTaskStatus(td.getTaskStatus());
			tod.setTaskType(td.getTaskType());
			tod.setTaskID(td.getTaskID());
			long startPoint = (td.getStartTime() - startTime) / CONVERSION_FACTOR_BYTES_TO_KB;
			tod.setStartPoint(startPoint);
			long endPoint = (td.getFinishTime() - startTime) / CONVERSION_FACTOR_BYTES_TO_KB;
			tod.setEndPoint(endPoint);
			tod.setTimeTaken(endPoint - startPoint);
			long dataFlowRate = td.getInputBytes() / (endPoint - startPoint);
			tod.setDataFlowRate(dataFlowRate);
			tod.setOutputBytes(td.getOutputBytes());
			tod.setOutputRecords(td.getOutputRecords());
			setAttemptDetails(td, tod);
			taskOutputDetails.add(tod);
		}
		pd.setTaskOutputDetails(taskOutputDetails);
		pd.setAvgDataFlowRate(calculateAvgDataFlow(taskOutputDetails));
		return pd;
	}

	/**
	 * Sets the details for an attempt of a MapReduce phase.
	 *
	 * @param td the td
	 * @param pd the pd
	 */
	private void setAttemptDetails(TaskDetails td, TaskOutputDetails pd) {
		List<AttemptDetails> attempts = td.getAttempts();
		for (AttemptDetails attempt : attempts) {
			if (SUCCESS.equals(attempt.getResult())) {
				pd.setLocation(attempt.getHostName());
				pd.setResourceUsageMetrics(attempt.getResourceUsageMetrics());
				// adding parameters for shuffle and sort
				if (REDUCE.equals(td.getTaskType())) {
					long attemptStartPoint = pd.getStartPoint();
					long shuffleEnd = attemptStartPoint + (attempt.getShuffleFinished() - attempt.getStartTime()) / CONVERSION_FACTOR_BYTES_TO_KB;
					pd.setShuffleStart(attemptStartPoint);
					pd.setShuffleEnd(shuffleEnd);
					long sortEnd = attemptStartPoint + (attempt.getSortFinished() - attempt.getStartTime()) / CONVERSION_FACTOR_BYTES_TO_KB;
					pd.setSortStart(shuffleEnd);
					pd.setSortEnd(sortEnd);
					pd.setReduceStart(sortEnd);
					pd.setReduceEnd(pd.getEndPoint());
				}
				return;
			}
		}
	}

	/**
	 * Calulates average data flow for a MapReduce phase.
	 *
	 * @param taskOutputDetails the task output details
	 * @return the long
	 */
	private long calculateAvgDataFlow(List<TaskOutputDetails> taskOutputDetails) {
		long totalDataFlow = 0;
		for (TaskOutputDetails tod : taskOutputDetails) {
			totalDataFlow += tod.getDataFlowRate();
		}
		return (totalDataFlow / taskOutputDetails.size()) / CONVERSION_FACTOR_BYTES_TO_KB;
		
	}

}
