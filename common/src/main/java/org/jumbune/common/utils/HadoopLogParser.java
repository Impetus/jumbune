package org.jumbune.common.utils;

import static org.jumbune.common.utils.Constants.COLON;
import static org.jumbune.common.utils.Constants.SPACE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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
import org.jumbune.common.beans.SupportedHadoopDistributions;
import org.jumbune.common.beans.TaskDetails;
import org.jumbune.common.beans.TaskOutputDetails;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.RemotingMethodConstants;
import org.jumbune.utils.exception.JumbuneException;

import com.google.gson.Gson;


/**
 * Gets all details pertaining to a job, and its phases.
 */
public class HadoopLogParser {

	private static final String HADOOP_HOME = "HADOOP_HOME";

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
	
	/** The Constant RM_CMD. Command to remove Files and Directories recursively. */
	private static final String RM_CMD="rm -r ";
	
	/** The Constant JAVA_CP_CMD. */
	private static final String JAVA_CP_CMD = "java -cp ";
	
	/** The Constant HADOOP_JAR_CONFIF_FILE. */
	private static final String HADOOP_JAR_CONFIF_FILE = "RumenReqJars.properties";
	
	/** The Constant RUMEN_MAIN_CLASS_OLD. */
	private static final String RUMEN_MAIN_CLASS_OLD = "org.jumbune.org.apache.hadoop.tools.rumen.TraceBuilder";
	
	private static final String YARN_JOB_STATS_UTILITY_CLASS = "org.jumbune.common.yarn.utils.YarnJobStatsUtility";
	
	private static final String YARN_JOB_STATS_UTILITY_CLASS_PARSE_METHOD = "parseAndGetJobStats";	
	
	/** The Constant LOGS **/
	private static final String LOGS = "/logs/";
	
	/** The Constant HISTORY_DIR_SUFFIX. */
	private static final String HISTORY_DIR_SUFFIX = "/history/done/version-1";
	
	private static final String HISTORY_INT_DIR_SUFFIX_YARN = "/tmp/hadoop-yarn/staging/history/done_intermediate/*/";
	
	private static String HISTORY_DIR_SUFFIX_YARN = "/tmp/hadoop-yarn/staging/history/done/*/*/*/*/";
	
	private static final String HISTORY_DIR_SUFFIX_MAPR_NY = "/var/mapr/cluster/mapred/jobTracker/history/done/";
	
	private static String USER_INT_HISTORY_DIR_SUFFIX = "/history/done_intermediate/*/";
	
	private static String USER_HISTORY_DIR_SUFFIX = "/history/done/*/*/*/*/";
		
	private static final String HDFS_FILE_GET_COMMAND = "/bin/hadoop fs -get";
	
	private static final String HDFS_LS_COMMAND = "/bin/hadoop fs -ls";
	
	private static final String HDFS_RM_COMMAND="/bin/hadoop fs -rmr";
	
	/** The Constant wildcard**/
	private static final String WILDCARD="/*";

	private static final String CHMOD_CMD = "chmod o+w ";

	/**
	 * Gets the job details.
	 *
	 * @param yamlLoader the loader
	 * @param jobID the job id
	 * @return the job details
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException 
	 */
	public JobOutput getJobDetails(Config config, String jobID) throws IOException{
		JobConfig jobConfig = (JobConfig) config;
		String appHome = JobConfig.getJumbuneHome() + File.separator;
		String agentHome = RemotingUtil.getAgentHome(jobConfig);
		Remoter remoter = RemotingUtil.getRemoter(jobConfig, appHome);
		String logsHistory = null;
		SupportedHadoopDistributions hadoopVersion = RemotingUtil.getHadoopVersion(jobConfig);
		String user = jobConfig.getMaster().getUser();
		logsHistory = changeLogHistoryPathAccToHadoopVersion(HADOOP_HOME,
				hadoopVersion, user);
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		
		boolean isYarn = jobConfig.getEnableYarn().equals(Enable.TRUE);
		String logfilePath = null ;
		String relLocalPath = null;
		if(!isYarn){
		
			//check if the hadoop distribution is non-yarn and MapR as well.
			if(SupportedHadoopDistributions.HADOOP_MAPR.equals(hadoopVersion))
			{  
				relLocalPath  = Constants.JOB_JARS_LOC + jobConfig.getFormattedJumbuneJobName();
				String fileName=null;
				//getting history file name corresponding to jobID 
				 fileName=getHistoryFileNameForMapR(remoter, logsHistory, jobID);
				
				//Now starting rumen processing
				Properties hadoopJarProperties = loadHadoopJarConfigurationProperties();
				String rumenDirPath = agentHome+Constants.JOB_JARS_LOC + jobConfig.getFormattedJumbuneJobName()+ RUMEN;				
			
				// make rumen related directory and files
				final String rumenTempDirOnMapRFS = "/jumbune/rumen-tmp/";
				final String jsonFilePath = rumenTempDirOnMapRFS + JSON_FILE;
				final String topologyFilePath = rumenTempDirOnMapRFS + TOPOLOGY_FILE;				
				
				//removing any previous rumen Directory and its contents
				builder.addCommand(RM_CMD+ rumenDirPath, false, null, CommandType.FS);
				remoter.fireAndForgetCommand(builder.getCommandWritable());			
				builder.getCommandBatch().clear();
                mkDir(builder, remoter, rumenDirPath); 
				
				String historyFilePathOnMapRFS = logsHistory + File.separator
						+ fileName;

				//prepare rumen processing command and start processing
				StringBuilder rumenProcessingCommand = prepareRumenProcessingCommand(hadoopJarProperties,
						agentHome, jsonFilePath, topologyFilePath,
						historyFilePathOnMapRFS);

				startRumenProcessing(remoter, relLocalPath, rumenDirPath, rumenProcessingCommand);
				getAndRemoveRumenTempDir(remoter, jobConfig,
						rumenTempDirOnMapRFS, rumenDirPath);
     			String relativeRemotePath = Constants.JOB_JARS_LOC
						+ jobConfig.getFormattedJumbuneJobName()
						+ RUMEN;

			    //receiving job-trace.json and topology files
				remoter.receiveLogFiles(relLocalPath, relativeRemotePath);
				LOGGER.debug("Received log files from:" + relativeRemotePath);

				Gson gson = new Gson();
				JobDetails jobDetails = extractJobDetails(appHome,
						relLocalPath, gson);
				return convertToFinalOutput(jobDetails);			
				
			}else{
				
				logfilePath = getLogFilePath(jobID, remoter, logsHistory,
						builder);
			relLocalPath  = Constants.JOB_JARS_LOC + jobConfig.getFormattedJumbuneJobName();
			String relRemotePath = relLocalPath + RUMEN;
			StringBuilder stringAppender = new StringBuilder(agentHome);
			stringAppender.append(File.separator).append(relRemotePath).append(File.separator);
			// make rumen related directory and files
			String pathToRumenDir = stringAppender.toString();
			String jsonFilepath = pathToRumenDir + JSON_FILE;
			String topologyFilePath = pathToRumenDir + TOPOLOGY_FILE;
			
			builder.getCommandBatch().clear();
			mkDir(builder, remoter, pathToRumenDir);
			// preparing command for rumen processing
			
			String remoteHadoopLib = HADOOP_HOME + LIB;
			Properties hadoopJarProperties = loadHadoopJarConfigurationProperties();
			
			String coreJar;
			if(SupportedHadoopDistributions.HADOOP_NON_YARN.equals(hadoopVersion)) {
				coreJar = HADOOP_HOME + hadoopJarProperties.getProperty("CORE_JAR");	
			}else {
				coreJar = HADOOP_HOME + WILDCARD;
			}
			
			String commonsLoggingJar = agentHome + LIB + hadoopJarProperties.getProperty("COMMONS_LOGGING_JAR");
			String commonsCliJar = remoteHadoopLib + hadoopJarProperties.getProperty("COMMONS_CLI_JAR");
			String commonsConfigurationJar = agentHome + LIB + hadoopJarProperties.getProperty("COMMONS_CONFIGURATION_JAR");
			String commonsLangJar = agentHome + LIB + hadoopJarProperties.getProperty("COMMONS_LANG_JAR");
			String jacksonMapperAslJar = agentHome + LIB + hadoopJarProperties.getProperty("JACKSON_MAPPER_ASL_JAR");
			String jacksonMapperCoreJar = agentHome + LIB + hadoopJarProperties.getProperty("JACKSON_MAPPER_CORE_JAR");
			String rumenJar = agentHome + LIB + hadoopJarProperties.getProperty("RUMEN_JAR")+"-"+Versioning.BUILD_VERSION+Versioning.DISTRIBUTION_NAME+".jar";
			
			StringBuilder rumenProcessingCommand = new StringBuilder(JAVA_CP_CMD);
			
			checkHadoopVersionsForRumen(hadoopVersion, logfilePath, jsonFilepath,
					topologyFilePath, coreJar, commonsLoggingJar,
					commonsCliJar, commonsConfigurationJar, commonsLangJar,
					jacksonMapperAslJar, jacksonMapperCoreJar, rumenJar, rumenProcessingCommand);
			LOGGER.debug("Rumen processing command [" + rumenProcessingCommand.toString()+"]");
			startRumenProcessing(remoter, relLocalPath, relRemotePath, rumenProcessingCommand);
			remoter = RemotingUtil.getRemoter(jobConfig, appHome);
			remoter.receiveLogFiles(relLocalPath, relRemotePath);
			LOGGER.debug("Received log files from:"+ relRemotePath);
			// process json
			Gson gson = new Gson();
			JobDetails jobDetails = extractJobDetails(appHome, relLocalPath, gson);
			return convertToFinalOutput(jobDetails);
	
				
			}

		}else{
	 		
			String relativeRemotePath = Constants.JOB_JARS_LOC + jobConfig.getJumbuneJobName() + File.separator + jobID;
			String remotePath = agentHome + relativeRemotePath;
			mkDir(builder, remoter, remotePath);
			checkAndgetCurrentLogFilePathForYarn(remoter,logsHistory,remotePath,jobID,config);
			relLocalPath  = Constants.JOB_JARS_LOC + jobConfig.getFormattedJumbuneJobName();
			remoter.receiveLogFiles(relLocalPath, relativeRemotePath);
			String absolutePath = appHome + relLocalPath + jobID + File.separator;
			String fileName = checkAndGetHistFile(absolutePath);
			String localPath = absolutePath + fileName;
			java.lang.reflect.Method method = null;
			Class<?> yarnJobStatsUtility = null;
			try {
				yarnJobStatsUtility = Class.forName(YARN_JOB_STATS_UTILITY_CLASS);
				method = yarnJobStatsUtility.getDeclaredMethod(YARN_JOB_STATS_UTILITY_CLASS_PARSE_METHOD, String.class);
				return 	(JobOutput) method.invoke(yarnJobStatsUtility.newInstance(), localPath);
			} catch (Exception e) {
				LOGGER.error("Error while instanting class", e);
			}
			}
			return null;
	}
	
	/**
	 * This method gets the Job History file name corresponding to 
	 * provided jobID. It assumes the presence of history file on MapRFS.
	 * 
	 * @param remoter
	 * @param logsHistory
	 * @param jobID
	 * @return
	 */
	private String getHistoryFileNameForMapR(Remoter remoter,
			String logsHistory, String jobID) {

		StringBuilder lsCommand = new StringBuilder()
				.append(Constants.HADOOP_HOME).append(HDFS_LS_COMMAND)
				.append(Constants.SPACE).append(logsHistory).append("*")
				.append(jobID).append("*").append("[!'.xml']");
		CommandWritableBuilder lsBuilder = new CommandWritableBuilder();
		lsBuilder.addCommand(lsCommand.toString(), false, null,
				CommandType.HADOOP_FS);
		String response = (String) remoter
				.fireCommandAndGetObjectResponse(lsBuilder.getCommandWritable());
		BufferedReader reader = new BufferedReader(new StringReader(response));
		String line = null;
		String[] splits = null;
		String filePath = null;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.contains(jobID)) {
					splits = line.split("\\s+");
				}
			}
		} catch (IOException exception) {
			LOGGER.error("Error reading command response: "+exception);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioException) {
					LOGGER.error("Error while closing the reader: "
							+ ioException);
				}
			}
		}
		try {
			filePath = splits[7];
		} catch (ArrayIndexOutOfBoundsException boundsException) {
			LOGGER.error("Error reading the file name from command response: "
					+ boundsException);
		}
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * This method creates a directory at agent side and gives the writing permission(chmod o+w) to others. 
	 * 
	 * @param builder
	 * @param remoter
	 * @param dirPath
	 */
	private void mkDir(CommandWritableBuilder builder, Remoter remoter,
			String dirPath) {
		builder.addCommand(MKDIR_CMD + dirPath, false, null, CommandType.FS);
		builder.addCommand(CHMOD_CMD + dirPath, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());

	}
	
	/**
	 * This method prepares command necessary for rumen processing of
	 * job history file. Being specific to MapR distribution, this method 
	 * assumes job history file to be on MapRFS.
	 * 
	 * @param props
	 * @param agentHome
	 * @param jsonFilepath
	 * @param topologyFilePath
	 * @param historyFilePathOnMapRFS
	 * @return
	 */
	private StringBuilder prepareRumenProcessingCommand(Properties hadoopJarProperties, String agentHome, String jsonFilepath, String topologyFilePath, String historyFilePathOnMapRFS)
	{
		String[] requiredJars={
				HADOOP_HOME+LIB+hadoopJarProperties.getProperty("CORE_MAPR_JAR"),
				HADOOP_HOME+LIB+hadoopJarProperties.getProperty("LOG4J_JAR"),
				HADOOP_HOME+LIB+hadoopJarProperties.getProperty("JACKSON_CORE_V1.5_JAR"),
				HADOOP_HOME+LIB+hadoopJarProperties.getProperty("JACKSON_MAPPER_V1.5_JAR"),
				HADOOP_HOME+LIB+hadoopJarProperties.getProperty("COMMONS_CLI_JAR"),
				HADOOP_HOME+LIB+hadoopJarProperties.getProperty("COMMONS_COLLECTIONS_JAR"),
				HADOOP_HOME+LIB+hadoopJarProperties.getProperty("COMMONS_LOGGING_V1.0.4_JAR"),
				HADOOP_HOME+LIB+hadoopJarProperties.getProperty("MAPRFS_JAR"),
				HADOOP_HOME+LIB+hadoopJarProperties.getProperty("ZOOKEEPER_JAR"),
				HADOOP_HOME+LIB+hadoopJarProperties.getProperty("COMMONS_EL_JAR")
			};
	
		String rumenJar = agentHome + LIB + hadoopJarProperties.getProperty("RUMEN_JAR")+"-"+Versioning.BUILD_VERSION+Versioning.DISTRIBUTION_NAME+".jar";				
		StringBuilder rumenProcessingCommand = new StringBuilder(JAVA_CP_CMD);
        
		//adding jars to classpath   
		for(String jar:requiredJars)
		{
			rumenProcessingCommand.append(jar).append(COLON);
		}
	
		rumenProcessingCommand.append(rumenJar);
		rumenProcessingCommand.append(SPACE).append(RUMEN_MAIN_CLASS_OLD).append(SPACE).append(jsonFilepath)
		.append(SPACE).append(topologyFilePath).append(SPACE).append(historyFilePathOnMapRFS);
		return rumenProcessingCommand;
	}
	
	
	/**
	 * This method gets the contents (job-trace.json and topology file) of temporary directory generated 
	 * on MapRFS during the rumen processing and eventually removes the temporary directory.
	 * 
	 * @param remoter
	 * @param jobConfig
	 * @param rumenTempDirOnMapRFS
	 * @param rumenDirPath
	 */
	private void getAndRemoveRumenTempDir(Remoter remoter, JobConfig jobConfig, String rumenTempDirOnMapRFS, String rumenDirPath)
	{		
	StringBuilder commandToExecute = new StringBuilder().append(Constants.HADOOP_HOME).append(HDFS_FILE_GET_COMMAND)
	.append(Constants.SPACE).append(rumenTempDirOnMapRFS).append("*").append(SPACE).append(rumenDirPath);
	CommandWritableBuilder fsGetBuilder = new CommandWritableBuilder();
	fsGetBuilder.addCommand(commandToExecute.toString(),false, null, CommandType.MAPRED).populate(jobConfig, null);
    StringBuilder rmCommand=new StringBuilder().append(Constants.HADOOP_HOME).append(HDFS_RM_COMMAND).append(SPACE).append("/jumbune");
	fsGetBuilder.addCommand(rmCommand.toString(), false, null, CommandType.HADOOP_FS);

	remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());
		
	}
	
	private String checkAndGetHistFile(String remotePath) {
		String fileName = null;
			
			File jobFilePath = new File(remotePath);

			if (jobFilePath.exists() && jobFilePath.isDirectory()) {
				File[] getJobFiles = jobFilePath.listFiles();
				for (File file : getJobFiles) {
					if (file.getName().endsWith(".jhist")) {
						fileName = file.getName();
					}
				}
			}
		return fileName;
	}
	/**
	 * 	This api is used to fetch the .hist files containing the job details.
	 * We first try in the intermediate folder and then fall back to done folder on HDFS.
	 * At least one of them is expected to fetch the file.
	 *
	 * @param jobID the job id is the jobname that ran on hadoop
	 * @param remoter the remoter
	 * @param logsHistory the logs history is the location of the .hist files on hdfs
	 * @param builder the builder
	 * @param JobConfig the job config
	 * @param agentHome the agent home
	 * @param relRemotePath the rel remote path
	 * @return the log file path for yarn
	 * @throws FileNotFoundException 
	 */
	private void checkAndgetCurrentLogFilePathForYarn(Remoter remoter,
			String logsHistory,String relRemotePath,String jobID,Config config){
			JobConfig jobConfig = (JobConfig)config ;
			String historyLocation = RemotingUtil.getHadoopConfigurationValue(config,"mapred-site.xml","yarn.app.mapreduce.am.staging-dir");
			if(historyLocation!=null && !historyLocation.isEmpty()){
				LOGGER.debug("Log history location" + historyLocation);
				logsHistory = historyLocation + USER_INT_HISTORY_DIR_SUFFIX ; 
				HISTORY_DIR_SUFFIX_YARN = historyLocation + USER_HISTORY_DIR_SUFFIX ;
			}
			
			CommandWritableBuilder fsGetBuilder = new CommandWritableBuilder();
			StringBuffer commandToExecute = new StringBuffer().append(Constants.HADOOP_HOME).append(HDFS_FILE_GET_COMMAND).append(Constants.SPACE).append(logsHistory)
			.append(jobID).append("*").append(Constants.SPACE).append(relRemotePath);
			LOGGER.debug("File get Command" + commandToExecute.toString());
			fsGetBuilder.addCommand(commandToExecute.toString(),false, null, CommandType.MAPRED).populate(jobConfig, null);
			remoter = RemotingUtil.getRemoter(jobConfig, "");
			
			commandToExecute = new StringBuffer().append(Constants.HADOOP_HOME).append(HDFS_FILE_GET_COMMAND).append(Constants.SPACE).append(HISTORY_DIR_SUFFIX_YARN)
			.append(jobID).append("*").append(Constants.SPACE).append(relRemotePath);
			LOGGER.debug("File get Command" + commandToExecute.toString());
			fsGetBuilder.addCommand(commandToExecute.toString(),false, null, CommandType.MAPRED).populate(jobConfig, null);
			remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());
	}

	private String getLogFilePath(String jobID, Remoter remoter,
			String logsHistory, CommandWritableBuilder builder) {
		String command = jobID + RemotingConstants.SINGLE_SPACE + logsHistory;
		builder.addCommand(command, false, null, CommandType.FS).setMethodToBeInvoked(RemotingMethodConstants.GET_JOB_HISTORY_FILE_FROM_JOB_ID);
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
		builder.addCommand(sb.toString(), false, null, CommandType.HADOOP_JOB);
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
	private JobDetails extractJobDetails(String appHome,  String relLocalPath,
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
			SupportedHadoopDistributions hadoopVersion, String user) {
		String logsHistory = null;
		if(SupportedHadoopDistributions.HADOOP_NON_YARN.equals(hadoopVersion)) {
			logsHistory = remoteHadoop + LOGS + HISTORY_DIR_SUFFIX;
		}else if(SupportedHadoopDistributions.HADOOP_YARN.equals(hadoopVersion) || SupportedHadoopDistributions.CDH_5.equals(hadoopVersion) || SupportedHadoopDistributions.APACHE_02X.equals(hadoopVersion)){
			logsHistory = HISTORY_INT_DIR_SUFFIX_YARN;
		}else if(SupportedHadoopDistributions.HADOOP_MAPR.equals(hadoopVersion)) {
			logsHistory =HISTORY_DIR_SUFFIX_MAPR_NY;
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
			SupportedHadoopDistributions hadoopVersion, String logfilePath,
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
			long diff=endPoint - startPoint;
			long dataFlowRate = td.getInputBytes() / ((diff==0)?1:diff);
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
		long size=taskOutputDetails.size();
		return (totalDataFlow / (size==0?1:size)) / CONVERSION_FACTOR_BYTES_TO_KB;
		
	}

}
