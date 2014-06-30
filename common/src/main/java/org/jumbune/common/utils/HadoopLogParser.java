/*
 * 
 */
package org.jumbune.common.utils;

import static org.jumbune.common.utils.Constants.COLON;
import static org.jumbune.common.utils.Constants.SPACE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.AttemptDetails;
import org.jumbune.common.beans.JobDetails;
import org.jumbune.common.beans.JobOutput;
import org.jumbune.common.beans.PhaseDetails;
import org.jumbune.common.beans.PhaseOutput;
import org.jumbune.common.beans.SupportedApacheHadoopVersions;
import org.jumbune.common.beans.TaskDetails;
import org.jumbune.common.beans.TaskOutputDetails;
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
	private static final String HADOOP_JAR_CONFIF_FILE = "hadoop-jarConfig.properties";
	
	/** The Constant RUMEN_MAIN_CLASS. */
	private static final String RUMEN_MAIN_CLASS = "org.apache.hadoop.tools.rumen.TraceBuilder";
	
	/** The Constant RUMEN_MAIN_CLASS_OLD. */
	private static final String RUMEN_MAIN_CLASS_OLD = "org.jumbune.org.apache.hadoop.tools.rumen.TraceBuilder";
	
	/** The Constant LOGS **/
	private static final String LOGS = "/logs/";
	
	/** The Constant HISTORY_DIR_SUFFIX. */
	private static final String HISTORY_DIR_SUFFIX = "/history/done/version-1";
	
	/** The Constant HISTORY_DIR_SUFFIX_OLD. */
	private static final String HISTORY_DIR_SUFFIX_OLD = "/history/";
	
	/** The Constant wildcard**/
	private static final String WILDCARD="/*";

	/**
	 * Gets the job details.
	 *
	 * @param loader the loader
	 * @param jobID the job id
	 * @return the job details
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public JobOutput getJobDetails(YamlLoader loader, String jobID) throws IOException {

		String appHome = YamlLoader.getjHome() + File.separator;
		YamlConfig config = loader.getYamlConfiguration();
		String agentHome = RemotingUtil.getAgentHome(config);
		Remoter remoter = RemotingUtil.getRemoter(loader, appHome);
		String remoteHadoop = RemotingUtil.getHadoopHome(remoter, config) + File.separator;
		String logsHistory = null;
		SupportedApacheHadoopVersions hadoopVersion = RemotingUtil.getHadoopVersion(loader.getYamlConfiguration());
		String user = config.getMaster().getUser();
		logsHistory = changeLogHistoryPathAccToHadoopVersion(remoteHadoop,
				hadoopVersion, user);
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		String logfilePath = getLogFilePath(jobID, remoter, logsHistory,
				builder);
		String relLocalPath = Constants.JOB_JARS_LOC + loader.getJumbuneJobName();
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
		String toolsJar = null;
		if(SupportedApacheHadoopVersions.Hadoop_1_0_4.equals(hadoopVersion)) {
			coreJar = remoteHadoop + props.getProperty("coreJar");	
			toolsJar = remoteHadoop + props.getProperty("toolsJar");
		}else if(SupportedApacheHadoopVersions.HADOOP_1_0_3.equals(hadoopVersion)){
			coreJar = remoteHadoop + props.getProperty("coreJar_1.0.3.15");	
			toolsJar = remoteHadoop + props.getProperty("toolsJar_1.0.3.15");
		}else if(SupportedApacheHadoopVersions.HADOOP_0_20_2.equals(hadoopVersion)){
			coreJar = remoteHadoop + props.getProperty("coreJarOld");
		}else {
			coreJar = remoteHadoop + WILDCARD;
		}
		
		String commonsLoggingJar = agentHome + LIB + props.getProperty("commonsLoggingJar");
		String commonsCliJar = remoteHadoopLib + props.getProperty("commonsCliJar");
		String commonsConfigurationJar = agentHome + LIB + props.getProperty("commonsConfigurationJar");
		String commonsLangJar = agentHome + LIB + props.getProperty("commonsLangJar");
		String jacksonMapperAslJar = agentHome + LIB + props.getProperty("jacksonMapperAslJar");
		String jacksonMapperCoreJar = agentHome + LIB + props.getProperty("jacksonMapperCoreJar");
		String rumenJar = agentHome + LIB + props.getProperty("rumenJar")+"-"+Versioning.BUILD_VERSION+"-SNAPSHOT.jar";
		
		StringBuilder sb = new StringBuilder(JAVA_CP_CMD);
		
		checkHadoopVersionsForRumen(hadoopVersion, logfilePath, jsonFilepath,
				topologyFilePath, coreJar, toolsJar, commonsLoggingJar,
				commonsCliJar, commonsConfigurationJar, commonsLangJar,
				jacksonMapperAslJar, jacksonMapperCoreJar, rumenJar, sb);
		LOGGER.debug("Rumen processing command [" + sb.toString()+"]");
		startRumenProcessing(remoter, relLocalPath, relRemotePath, sb);
		remoter.close();
		remoter = RemotingUtil.getRemoter(loader, appHome);
		remoter.receiveLogFiles(relLocalPath, relRemotePath);
		remoter.close();
		LOGGER.debug("Received log files from:"+ relRemotePath);
		// process json
		Gson gson = new Gson();
		JobDetails jobDetails = extractJobDetails(appHome, relLocalPath, gson);
		return convertToFinalOutput(jobDetails);
		
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
		if(SupportedApacheHadoopVersions.Hadoop_1_0_4.equals(hadoopVersion)|| SupportedApacheHadoopVersions.HADOOP_1_0_3.equals(hadoopVersion)) {
			logsHistory = remoteHadoop + LOGS + HISTORY_DIR_SUFFIX;
		}else if(SupportedApacheHadoopVersions.HADOOP_0_20_2.equals(hadoopVersion)){
			logsHistory = remoteHadoop + LOGS + HISTORY_DIR_SUFFIX_OLD;
		}else{
			logsHistory = remoteHadoop + LOGS + user + HISTORY_DIR_SUFFIX;
		}
		return logsHistory;
	}

	/**
	 * Check hadoop versions for rumen.
	 *
	 * @param hadoopVersion the hadoop version
	 * @param logfilePath the logfile path
	 * @param jsonFilepath the json filepath
	 * @param topologyFilePath the topology file path
	 * @param coreJar the core jar
	 * @param toolsJar the tools jar
	 * @param commonsLoggingJar the commons logging jar
	 * @param commonsCliJar the commons cli jar
	 * @param commonsConfigurationJar the commons configuration jar
	 * @param commonsLangJar the commons lang jar
	 * @param jacksonMapperAslJar the jackson mapper asl jar
	 * @param jacksonMapperCoreJar the jackson mapper core jar
	 * @param rumenJar the rumen jar
	 * @param sb the sb
	 */
	private void checkHadoopVersionsForRumen(
			SupportedApacheHadoopVersions hadoopVersion, String logfilePath,
			String jsonFilepath, String topologyFilePath, String coreJar,
			String toolsJar, String commonsLoggingJar, String commonsCliJar,
			String commonsConfigurationJar, String commonsLangJar,
			String jacksonMapperAslJar, String jacksonMapperCoreJar,
			String rumenJar, StringBuilder sb) {
		if(SupportedApacheHadoopVersions.HADOOP_0_20_2.equals(hadoopVersion)) {
			// need to provide Jumbune modified rumen jar explicitly for older versions of hadoop 
			 sb.append(coreJar).append(COLON).append(rumenJar).append(COLON).append(commonsLoggingJar).append(COLON)
				.append(COLON).append(commonsCliJar).append(COLON).append(commonsConfigurationJar).append(COLON).append(commonsLangJar)
				.append(COLON).append(jacksonMapperAslJar).append(COLON)
				.append(jacksonMapperCoreJar).append(SPACE).append(RUMEN_MAIN_CLASS_OLD).append(SPACE).append(FILE_PREFIX).append(jsonFilepath)
				.append(SPACE).append(FILE_PREFIX).append(topologyFilePath).append(SPACE).append(FILE_PREFIX).append(logfilePath);
		}else if(SupportedApacheHadoopVersions.HADOOP_DEFAULT.equals(hadoopVersion)){
			// fallback for default hadoop version
			 sb.append(coreJar).append(COLON).append(commonsLoggingJar).append(COLON).append(commonsCliJar).append(COLON)
				.append(commonsConfigurationJar).append(COLON).append(commonsLangJar).append(COLON).append(jacksonMapperAslJar).append(COLON)
				.append(jacksonMapperCoreJar).append(SPACE).append(RUMEN_MAIN_CLASS).append(SPACE).append(FILE_PREFIX).append(jsonFilepath)
				.append(SPACE).append(FILE_PREFIX).append(topologyFilePath).append(SPACE).append(FILE_PREFIX).append(logfilePath);
		}else{
			 sb.append(coreJar).append(COLON).append(toolsJar).append(COLON).append(commonsLoggingJar).append(COLON).append(commonsCliJar).append(COLON)
				.append(commonsConfigurationJar).append(COLON).append(commonsLangJar).append(COLON).append(jacksonMapperAslJar).append(COLON)
				.append(jacksonMapperCoreJar).append(SPACE).append(RUMEN_MAIN_CLASS).append(SPACE).append(FILE_PREFIX).append(jsonFilepath)
				.append(SPACE).append(FILE_PREFIX).append(topologyFilePath).append(SPACE).append(FILE_PREFIX).append(logfilePath);
		}
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
