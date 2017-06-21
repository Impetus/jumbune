package org.jumbune.common.utils;

import static org.jumbune.common.utils.Constants.COLON;
import static org.jumbune.common.utils.Constants.SPACE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.profiling.AttemptDetails;
import org.jumbune.common.beans.profiling.JobDetails;
import org.jumbune.common.beans.profiling.JobOutput;
import org.jumbune.common.beans.profiling.PhaseDetails;
import org.jumbune.common.beans.profiling.PhaseOutput;
import org.jumbune.common.beans.profiling.TaskDetails;
import org.jumbune.common.beans.profiling.TaskOutputDetails;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.RemotingMethodConstants;
import org.jumbune.utils.Versioning;
import org.jumbune.utils.exception.JumbuneRuntimeException;

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

	private static final String HISTORY_DIR_SUFFIX_MAPR_NY = "/var/mapr/cluster/yarn/rm/staging/history/done/*/*/*/*/";

	private static final String HDFS_FILE_GET_COMMAND = "/bin/hadoop fs -get";

	private static final String HDFS_LS_COMMAND = "/bin/hadoop fs -ls";

	private static final String HDFS_RM_COMMAND="/bin/hadoop fs -rmr";

	/** The Constant wildcard**/
	private static final String WILDCARD="/*";

	private static final String CHMOD_CMD = "chmod o+w ";

	private static final Pattern CONTAINER_ID_EPOCH_PATTERN = Pattern.compile("container_(?:[e\\d]+)_([\\d]+)_([\\d]+)_([\\d]+)_([\\d]+)");

	private static final Pattern LINE_PATTERN = Pattern.compile("(.*)(container_(?:[e\\d]+)_(?:[\\d]+)_(?:[\\d]+)_(?:[\\d]+)_(?:[\\d]+))(.*)");
	
	/** The Constant JHIST. */
	private static final String JHIST = ".jhist";
	String localPath;

	public JobOutput getJobDetails(JumbuneRequest jumbuneRequest, String jobID) throws IOException{
		return getJobDetails(jumbuneRequest.getCluster(), jobID, jumbuneRequest.getJobConfig().getFormattedJumbuneJobName());
	}

	public JobOutput getJobDetails(Cluster cluster, String jobID) throws IOException{
		return getJobDetails(cluster, jobID, cluster.getClusterName() + File.separator);
	}


	/**
	 * Gets the job details.
	 *
	 * @param yamlLoader the loader
	 * @param jobID the job id
	 * @return the job details
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException 
	 */
	public JobOutput getJobDetails(Cluster cluster, String jobID, String jumbuneJobName) throws IOException{

		String appHome = JobConfig.getJumbuneHome() + File.separator;
		String agentHome = RemotingUtil.getAgentHome(cluster);
		Remoter remoter = RemotingUtil.getRemoter(cluster, appHome);
		String logsHistory = null;
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		String hadoopType = FileUtil.getClusterInfoDetail(Constants.HADOOP_TYPE);
		logsHistory = changeLogHistoryPathAccToHadoopVersion(HADOOP_HOME,
				hadoopDistribution,hadoopType);

		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);

		String logfilePath = null ;
		String relLocalPath = null;
		if(hadoopType.equalsIgnoreCase(Constants.NON_YARN)){

			//check if the hadoop distribution is non-yarn and MapR as well.
			if(hadoopDistribution.equalsIgnoreCase(Constants.MAPR))
			{  
				relLocalPath  = Constants.JOB_JARS_LOC + jumbuneJobName;
				String fileName=null;
				//getting history file name corresponding to jobID 
				fileName=getHistoryFileNameForMapR(cluster, remoter, logsHistory, jobID);

				//fetching job configuration xml file. 
				StringBuffer jobIdDir = new StringBuffer(agentHome).append(Constants.JOB_JARS_LOC).append(jumbuneJobName).append(jobID);				
				getJobFilesForMapR(cluster, remoter, jobIdDir.toString(), logsHistory, jobID);

				StringBuffer relativeJobIdDir = new StringBuffer(Constants.JOB_JARS_LOC).append(jumbuneJobName).append(jobID);
				remoter.receiveLogFiles(relLocalPath, relativeJobIdDir.toString());

				//Now starting rumen processing
				Properties props = loadHadoopJarConfigurationProperties();
				String rumenDirPath = agentHome+Constants.JOB_JARS_LOC + jumbuneJobName + RUMEN;				

				// make rumen related directory and files
				final String rumenTempDirOnMapRFS = "/jumbune/rumen-tmp/";
				final String jsonFilePath = rumenTempDirOnMapRFS + JSON_FILE;
				final String topologyFilePath = rumenTempDirOnMapRFS + TOPOLOGY_FILE;				

				//removing any previous rumen Directory and its contents
				builder.addCommand(RM_CMD+ rumenDirPath, false, null, CommandType.FS);
				remoter.fireAndForgetCommand(builder.getCommandWritable());			
				builder.clear();
				RemotingUtil.mkDir(builder,remoter, rumenDirPath);
				String historyFilePathOnMapRFS = logsHistory + File.separator
						+ fileName;

				//prepare rumen processing command and start processing
				StringBuilder sb = prepareRumenProcessingCommand(props,
						appHome, agentHome, jsonFilePath, topologyFilePath,
						historyFilePathOnMapRFS);

				startRumenProcessing(cluster, remoter, relLocalPath, rumenDirPath, sb);
				getAndRemoveRumenTempDir(remoter, cluster,
						rumenTempDirOnMapRFS, rumenDirPath);
				String relativeRemotePath = Constants.JOB_JARS_LOC
						+ jumbuneJobName + RUMEN;

				//receiving job-trace.json and topology files
				remoter.receiveLogFiles(relLocalPath, relativeRemotePath);
				LOGGER.info("Received log files from:" + relativeRemotePath);

				Gson gson = new Gson();
				JobDetails jobDetails = extractJobDetails(appHome,
						relLocalPath, gson);
				return convertToFinalOutput(jobDetails);			

			}else{


				logfilePath = getLogFilePath(jobID, remoter, logsHistory,
						builder);
				
			relLocalPath  = Constants.JOB_JARS_LOC + jumbuneJobName;
			String relRemotePath = relLocalPath + RUMEN;
			StringBuilder stringAppender = new StringBuilder(agentHome);
			stringAppender.append(File.separator).append(relRemotePath).append(File.separator);
			// make rumen related directory and files
			String pathToRumenDir = stringAppender.toString();
			String jsonFilepath = pathToRumenDir + JSON_FILE;
			String topologyFilePath = pathToRumenDir + TOPOLOGY_FILE;
			
			builder.clear();
			RemotingUtil.mkDir(builder,remoter, pathToRumenDir);
			// preparing command for rumen processing
			
			String remoteHadoopLib = HADOOP_HOME + LIB;
			Properties props = loadHadoopJarConfigurationProperties();
			
			String coreJar;
			if(hadoopDistribution.equalsIgnoreCase(Constants.APACHE)) {
				coreJar = HADOOP_HOME + props.getProperty("CORE_JAR");	
			}else {
				coreJar = HADOOP_HOME + WILDCARD;
			}
			
			String commonsLoggingJar = agentHome + LIB + props.getProperty("COMMONS_LOGGING_JAR");
			String commonsCliJar = remoteHadoopLib + props.getProperty("COMMONS_CLI_JAR");
			String commonsConfigurationJar = agentHome + LIB + props.getProperty("COMMONS_CONFIGURATION_JAR");
			String commonsLangJar = agentHome + LIB + props.getProperty("COMMONS_LANG_JAR");
			String jacksonMapperAslJar = agentHome + LIB + props.getProperty("JACKSON_MAPPER_ASL_JAR");
			String jacksonMapperCoreJar = agentHome + LIB + props.getProperty("JACKSON_MAPPER_CORE_JAR");
			String rumenJar = agentHome + LIB + props.getProperty("RUMEN_JAR")+"-"+Versioning.ENTERPRISE_BUILD_VERSION+Versioning.ENTERPRISE_DISTRIBUTION_NAME+".jar";
			
			StringBuilder sb = new StringBuilder(JAVA_CP_CMD);
			
			checkHadoopVersionsForRumen(logfilePath, jsonFilepath,
					topologyFilePath, coreJar, commonsLoggingJar,
					commonsCliJar, commonsConfigurationJar, commonsLangJar,
					jacksonMapperAslJar, jacksonMapperCoreJar, rumenJar, sb);
			LOGGER.debug("Rumen processing command [" + sb.toString()+"]");
			startRumenProcessing(cluster, remoter, relLocalPath, relRemotePath, sb);
			remoter = RemotingUtil.getRemoter(cluster, appHome);
			remoter.receiveLogFiles(relLocalPath, relRemotePath);
			LOGGER.debug("Received log files from:"+ relRemotePath);
			// process json
			Gson gson = new Gson();
			JobDetails jobDetails = extractJobDetails(appHome, relLocalPath, gson);
			return convertToFinalOutput(jobDetails);
	
				
			}

		}else{
			if(hadoopDistribution.equalsIgnoreCase(Constants.MAPR) || hadoopDistribution.equalsIgnoreCase(Constants.EMRMAPR))
			{  
				relLocalPath  = Constants.JOB_JARS_LOC + jumbuneJobName;
				String fileName=null;
				//getting history file name corresponding to jobID 
				fileName=getHistoryFileNameForMapR(cluster, remoter, logsHistory, jobID);

				//fetching job configuration xml file. 
				StringBuffer jobIdDir = new StringBuffer(agentHome).append(Constants.JOB_JARS_LOC).append(jumbuneJobName).append(jobID);				
				getJobFilesForMapR(cluster, remoter, jobIdDir.toString(), logsHistory, jobID);

				StringBuffer relativeJobIdDir = new StringBuffer(Constants.JOB_JARS_LOC).append(jumbuneJobName).append(jobID);
				remoter.receiveLogFiles(relLocalPath, relativeJobIdDir.toString());

				File dir = new  File(appHome + File.separator + relativeJobIdDir);
				File histFile = null;
				for(File f : dir.listFiles() ) {
					if(f.getAbsolutePath().endsWith(JHIST)) {
						histFile = f;
					}
				}
				localPath=histFile.getAbsolutePath();
				UserGroupInformation realUser = UserGroupInformation.createRemoteUser("mapr");
				UserGroupInformation.setLoginUser(realUser);
				preProcessHistFile(localPath);

			}else{

				String relativeRemotePath = Constants.JOB_JARS_LOC + jumbuneJobName + File.separator + jobID;
				String tmpPath = File.separator+"tmp"+File.separator+relativeRemotePath+File.separator;
				RemotingUtil.mkDir(builder,remoter,tmpPath);

				String remotePath = agentHome + relativeRemotePath;
				RemotingUtil.mkDir(builder,remoter,remotePath);

				checkAndgetCurrentLogFilePathForYarn(remoter,tmpPath, jobID, cluster);
				CommandWritableBuilder fsGetBuilder;
				//Added copy from tmp to agent home
				fsGetBuilder = new CommandWritableBuilder(cluster, null);
				StringBuffer copyFromTmpToAgent = new StringBuffer().append("cp -r").append(Constants.SPACE).append(tmpPath).append("*").append(Constants.SPACE).append(remotePath);
				fsGetBuilder.addCommand(copyFromTmpToAgent.toString(),false, null, CommandType.FS);
				remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());

				relLocalPath  = Constants.JOB_JARS_LOC + jumbuneJobName;
				remoter.receiveLogFiles(relLocalPath, relativeRemotePath);
				String absolutePath = appHome + relLocalPath + jobID + File.separator;
				String fileName = checkAndGetHistFile(absolutePath);
				localPath = absolutePath + fileName;

				if(hadoopDistribution.trim().equalsIgnoreCase(Constants.HORTONWORKS)) {  
					LOGGER.debug("preprocessing history file on path[for HDP] - " + localPath);
					preProcessHistFile(localPath);
				}

				
			}
			java.lang.reflect.Method method = null;
			Class<?> yarnJobStatsUtility = null;
			try {
				yarnJobStatsUtility = Class.forName(YARN_JOB_STATS_UTILITY_CLASS);
				method = yarnJobStatsUtility.getDeclaredMethod(YARN_JOB_STATS_UTILITY_CLASS_PARSE_METHOD, String.class);
				return 	(JobOutput) method.invoke(yarnJobStatsUtility.newInstance(), localPath);
			} catch (Exception e) {
				LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			}
		}
		return null;
	}


	/**
	 * This method pre processes jhist file to replace the extra container identification to normal container nomenclature.
	 *
	 * @param localPath denotes the path of the jhist file on jumbune system.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void preProcessHistFile(String localPath) throws IOException {

		File file = new File(localPath);
		String name = file.getName();
		String parentDir = file.getParent();
		String newJhist = parentDir + File.separator + "new.jhist";
		File dest = null;
		if (file.exists()) {
			dest = new File(newJhist);
		}
		BufferedWriter bufferedWriter = null ;
		try {
			bufferedWriter = Files.newBufferedWriter(Paths.get(dest.getAbsolutePath()), StandardCharsets.UTF_8,
					StandardOpenOption.CREATE);
			List<String> lines = Files.readAllLines(Paths.get(localPath), StandardCharsets.UTF_8);
			Matcher m = null;
			StringBuilder builder = null;
			for (String line : lines) {
				m = LINE_PATTERN.matcher(line);
				if (m.matches()) {
					builder = new StringBuilder();
					builder.append(m.group(1)).append(getProcessedContainerId(m.group(2))).append(m.group(3));
				} else {
					builder = new StringBuilder(line);
				}
				bufferedWriter.write(builder.toString());
				bufferedWriter.newLine();
				bufferedWriter.flush();
			}
		} finally{
			if(bufferedWriter != null){
				bufferedWriter.close();
			}
		}

		String newHistoryFile = parentDir + File.separator + name;
		file.delete();
		Files.copy(Paths.get(newJhist), Paths.get(newHistoryFile), StandardCopyOption.REPLACE_EXISTING);
		dest.delete();

	}


	/**
	 * Gets the processed container id.
	 *
	 * @param id the id
	 * @return the processed container id
	 */
	private String getProcessedContainerId(String id) {
		StringBuilder builder = new StringBuilder("container");
		Matcher m = CONTAINER_ID_EPOCH_PATTERN.matcher(id);
		if (m.matches()) {
			for (int i = 1; i <= m.groupCount(); i++) {
				builder.append(Constants.UNDERSCORE + m.group(i));
			}
		}
		return builder.toString();

	}


	private void getJobFilesForMapR(Cluster cluster, Remoter remoter, String jobIdDir,
			String logsHistory, String jobID) {

		StringBuilder getCommand = new StringBuilder()
				.append(Constants.HADOOP_HOME).append(HDFS_FILE_GET_COMMAND)
				.append(Constants.SPACE).append(logsHistory).append("*")
				.append(jobID).append("*")
				.append(Constants.SPACE).append(jobIdDir);

		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		RemotingUtil.mkDir(builder,remoter, jobIdDir);		
		builder.clear();
		builder.addCommand(getCommand.toString(), false, null, CommandType.HADOOP_FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());

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
	private String getHistoryFileNameForMapR(Cluster cluster, Remoter remoter, String logsHistory, String jobID) {

		StringBuilder lsCommand = new StringBuilder().append(Constants.HADOOP_HOME).append(HDFS_LS_COMMAND).append(Constants.SPACE).append(logsHistory)
		.append("*").append(jobID).append("*").append("[!'.xml']");
		LOGGER.debug("History FileName Fetch Command: "+lsCommand.toString());
		CommandWritableBuilder lsBuilder = new CommandWritableBuilder(cluster);
		lsBuilder.addCommand(lsCommand.toString(), false, null, CommandType.HADOOP_FS);
		String response = (String) remoter.fireCommandAndGetObjectResponse(lsBuilder.getCommandWritable());
		BufferedReader reader = new BufferedReader(new StringReader(response));
		String line = null;		
		String[] splits = null;
		String filePath =null;
		try{
			while ((line = reader.readLine()) != null) {    			
				if (line.contains(jobID)) {
					splits = line.split("\\s+");
				}
			}
		}catch(IOException exception)
		{
			LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(exception.getStackTrace()));
		}
		finally{
			if(reader!=null)
			{ 
				try {
					reader.close();
				}catch(IOException ioException)
				{
					LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(ioException.getStackTrace()));
				}
				finally{
				}
			}
			try {
				filePath=splits[7];		
			}catch(ArrayIndexOutOfBoundsException boundsException){
				LOGGER.error("Error reading the file name from command response: "+boundsException);
			}
			return filePath.substring(filePath.lastIndexOf("/")+1);
		}
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
	private StringBuilder prepareRumenProcessingCommand(Properties props, String jumbuneHome, 
			String agentHome, String jsonFilepath, String topologyFilePath, String historyFilePathOnMapRFS) 
	{  
		props.clear();
		final String propertyFilePath=jumbuneHome+"resources"+File.separator+HADOOP_JAR_CONFIF_FILE;
		FileReader propertyFileReader=null;
		try{
			propertyFileReader=new FileReader(propertyFilePath);	
			props.load(propertyFileReader);
		}catch(IOException exception){
			LOGGER.error("Cannot load property file from "+propertyFilePath+", "+exception.getMessage());
		}

		String[] requiredJars={
				HADOOP_HOME+LIB+props.getProperty("CORE_MAPR_JAR").trim(),
				HADOOP_HOME+LIB+props.getProperty("LOG4J_JAR").trim(),
				HADOOP_HOME+LIB+props.getProperty("JACKSON_CORE_V1.5_JAR").trim(),
				HADOOP_HOME+LIB+props.getProperty("JACKSON_MAPPER_V1.5_JAR").trim(),
				HADOOP_HOME+LIB+props.getProperty("COMMONS_CLI_JAR").trim(),
				HADOOP_HOME+LIB+props.getProperty("COMMONS_COLLECTIONS_JAR").trim(),
				HADOOP_HOME+LIB+props.getProperty("COMMONS_LOGGING_V1.0.4_JAR").trim(),
				HADOOP_HOME+LIB+props.getProperty("MAPRFS_JAR").trim(),
				HADOOP_HOME+LIB+props.getProperty("ZOOKEEPER_JAR").trim(),
				HADOOP_HOME+LIB+props.getProperty("COMMONS_EL_JAR").trim()

		};
		LOGGER.debug("Rumen Processing Command: "+Arrays.asList(requiredJars));

		String rumenJar = agentHome + LIB + props.getProperty("RUMEN_JAR")+"-"+Versioning.ENTERPRISE_BUILD_VERSION+Versioning.ENTERPRISE_DISTRIBUTION_NAME+".jar";				
		StringBuilder sb = new StringBuilder(JAVA_CP_CMD);

		//adding jars to classpath   
		for(String jar:requiredJars)
		{
			sb.append(jar).append(COLON);
		}

		sb.append(rumenJar);
		sb.append(SPACE).append(RUMEN_MAIN_CLASS_OLD).append(SPACE).append(jsonFilepath)
		.append(SPACE).append(topologyFilePath).append(SPACE).append(historyFilePathOnMapRFS);
		return sb;
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
	private void getAndRemoveRumenTempDir(
			Remoter remoter, Cluster cluster, String rumenTempDirOnMapRFS, String rumenDirPath){		

		StringBuilder commandToExecute = new StringBuilder().append(Constants.HADOOP_HOME).append(HDFS_FILE_GET_COMMAND)
				.append(Constants.SPACE).append(rumenTempDirOnMapRFS).append("*").append(SPACE).append(rumenDirPath);
		LOGGER.info("File get Command" + commandToExecute.toString());

		CommandWritableBuilder fsGetBuilder = new CommandWritableBuilder(cluster, null);
		fsGetBuilder.addCommand(commandToExecute.toString(),false, null, CommandType.MAPRED);
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
	 * This method fetches the intermediate and final .jhist file locations.
	 *
	 * @param remoter the remoter
	 * @param relRemotePath the rel remote path
	 * @param jobID the job id
	 * @param cluster the cluster
	 */
	private void checkAndgetCurrentLogFilePathForYarn( Remoter remoter,String relRemotePath, String jobID, Cluster cluster){
		String historyIntLocation = RemotingUtil.getHistoryIntermediateLocation(cluster);
		String historyDoneLocation = RemotingUtil.getHistoryDoneLocation(cluster);
		checkAndgetCurrentLogFilePathForYarn(cluster, remoter, historyIntLocation, historyDoneLocation ,relRemotePath, jobID);
	}




	/**
	 * 	This api is used to fetch the .hist files containing the job details.
	 * We first try in the intermediate folder and then fall back to done folder on HDFS.
	 * At least one of them is expected to fetch the file.
	 *
	 * @param jobID the job id is the jobname that ran on hadoop
	 * @param remoter the remoter
	 * @param historyInterLocation the intermediate location of .jhist files on hdfs
	 * @param historyDoneLocation  the final location of .jhist files on hdfs
	 * @param builder the builder
	 * @param JobConfig the job config
	 * @param agentHome the agent home
	 * @param relRemotePath the rel remote path
	 * @return the log file path for yarn
	 * @throws FileNotFoundException 
	 */
	private void checkAndgetCurrentLogFilePathForYarn(Cluster cluster, Remoter remoter, String historyInterLocation,String historyDoneLocation,
			String relRemotePath, String jobID){
		CommandWritableBuilder fsGetBuilder;
		StringBuffer doneCommandToExecute = new StringBuffer().append(Constants.HADOOP_HOME).append(HDFS_FILE_GET_COMMAND).append(Constants.SPACE).append(historyDoneLocation)
				.append(jobID).append("*").append(Constants.SPACE).append(relRemotePath);

		StringBuffer intermediateCommandToExecute = new StringBuffer().append(Constants.HADOOP_HOME).append(HDFS_FILE_GET_COMMAND).append(Constants.SPACE).append(historyInterLocation)
				.append(jobID).append("*").append(Constants.SPACE).append(relRemotePath);
		fsGetBuilder = new CommandWritableBuilder(cluster, null);
		if(!cluster.getHadoopUsers().isHasSingleUser()){
			fsGetBuilder.addCommand(intermediateCommandToExecute.toString(),false, null, CommandType.MAPRED).setMethodToBeInvoked(RemotingMethodConstants.EXECUTE_REMOTE_COMMAND_AS_SUDO);
			remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());

			fsGetBuilder = new CommandWritableBuilder(cluster, null);
			fsGetBuilder.addCommand(doneCommandToExecute.toString(),false, null, CommandType.MAPRED).setMethodToBeInvoked(RemotingMethodConstants.EXECUTE_REMOTE_COMMAND_AS_SUDO);
			remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());
		}else{
			fsGetBuilder.addCommand(intermediateCommandToExecute.toString(),false, null, CommandType.MAPRED);
			remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());

			fsGetBuilder = new CommandWritableBuilder(cluster, null);
			fsGetBuilder.addCommand(doneCommandToExecute.toString(),false, null, CommandType.MAPRED).populate(cluster, null);
			remoter.fireAndForgetCommand(fsGetBuilder.getCommandWritable());
		}
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
	private void startRumenProcessing(Cluster cluster, Remoter remoter, String relLocalPath,
			String relRemotePath, StringBuilder sb) {
		// Starting rumen processing on master
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand(sb.toString(), false, null, CommandType.FS);
		remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		LOGGER.debug("Completed Rumen processing");
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
	public String changeLogHistoryPathAccToHadoopVersion(String remoteHadoop,
			String hadoopDistribution, String hadoopType) {
		String logsHistory = null;
		if(hadoopType.equalsIgnoreCase(Constants.NON_YARN) && hadoopDistribution.equalsIgnoreCase(Constants.APACHE)) {
			logsHistory = remoteHadoop + LOGS + HISTORY_DIR_SUFFIX;
		}else if(hadoopDistribution.equalsIgnoreCase(Constants.MAPR) || (hadoopDistribution.equalsIgnoreCase(Constants.EMRMAPR))) {
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
			String logfilePath,
			String jsonFilepath, String topologyFilePath, String coreJar,
			String commonsLoggingJar, String commonsCliJar,
			String commonsConfigurationJar, String commonsLangJar,
			String jacksonMapperAslJar, String jacksonMapperCoreJar,
			String rumenJar, StringBuilder sb) {
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
		jobOutput.setLaunchTime(jobDetails.getLaunchTime());
		jobOutput.setSubmitTime(jobDetails.getSubmitTime());
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
			tod.setPreferredLocations(td.getPreferredLocations());
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
