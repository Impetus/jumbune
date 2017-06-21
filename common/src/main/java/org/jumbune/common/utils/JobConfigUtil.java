package org.jumbune.common.utils;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.cluster.Agent;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.NameNodes;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.client.RemoterFactory;
import org.jumbune.remoting.common.CommandType;

import com.google.gson.Gson;
import com.jcraft.jsch.JSchException;



/**
 * The Class JobConfigUtil.
 */
public final class JobConfigUtil {
	
	/** The Constant CONSOLELOGGER. */
	public static final Logger CONSOLELOGGER = LogManager
			.getLogger("EventLogger");
	
	/** The Constant LOGGER. */
	public static final Logger LOGGER = LogManager.getLogger(JobConfigUtil.class);

	public static JumbuneRequest jumbuneRequest(InputStream is) {
		Gson gson = new Gson();
		return (JumbuneRequest) gson.fromJson(new InputStreamReader(is), JumbuneRequest.class);
	}
	
	
	/**
	 * The services json path is fixed and is in user's home directory.
	 *
	 * @return the service json path
	 */
	public static String getServiceJsonPath() {
	
		String serviceJobLoc = JobConfig.getJumbuneHome();
		File currentDir = new File(serviceJobLoc);
		String currentDirPath = currentDir.getAbsolutePath();
	
		currentDirPath = currentDirPath.substring(0, currentDirPath.length());
	
		StringBuilder sb = new StringBuilder(currentDirPath).append(System.getProperty("file.separator")).append("resources")
				.append(System.getProperty("file.separator")).append("services.json");
		return sb.toString();
	}

	/**
	 * Checks if is jumbune supplied jar present.
	 * 
	 * @param jumbuneRequest the jumbune Request
	 * @return true, if is jumbune supplied jar present
	 */
	public static boolean isJumbuneSuppliedJarPresent(Cluster cluster) {
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand("ls lib/", false, null, CommandType.FS);
		String result = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		return (result.length() > 0) ? true : false;
	}

	/**
	 * Checks if is mR job jar present.
	 *
	 * @param jumbuneRequest the jumbune Request
	 * @param jarFilepath the jar filepath
	 * @return true, if is mR job jar present
	 */
	public static boolean isMRJobJarPresent(Cluster cluster, String jarFilepath){
		File resourceDir = new File(jarFilepath);
		if(resourceDir.exists()){
			Remoter remoter = RemotingUtil.getRemoter(cluster);
			CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
			builder.addCommand("ls "+jarFilepath, false, null, CommandType.FS);
			String result = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
			remoter.close();
			return (result.length() > 0) ? true : false;
		}else{
			return false;
		}

	}

	/**
	 * Send lib jar command.
	 *
	 * @param remoter the remoter
	 * @param config the config
	 * @param command the command
	 */
	public static void sendLibJarCommand(
			Remoter remoter, Cluster cluster, String command) {
		
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, null);
		builder.addCommand(command, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
	
	}

	/**
	 * Send jumbune supplied jar on agent.
	 * 
	 * @param jumbuneRequest the jumbune Request
	 * @param cse
	 *            the cse
	 * @param agentHome
	 *            the agent home
	 */
	@SuppressWarnings("deprecation")
	public static void sendJumbuneSuppliedJarOnAgent(
			Cluster cluster, ClasspathElement cse, String agentHome) {
		
		String jumbuneHome = JobConfig.getJumbuneHome();
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		String hadoopHome = RemotingUtil.getHadoopHome(remoter, cluster);
		String[] files = cse.getFiles();
		for (String string : files) {
			remoter.sendJar("lib/", string.replace(agentHome, jumbuneHome));
	
			if (string.contains("log4j")) {
				StringBuilder copyJarToHadoopLib = new StringBuilder().append(Constants.COPY_COMMAND).append(string).append(" ").append(hadoopHome)
						.append(Constants.LIB_DIRECTORY);
				sendLibJarCommand(remoter, cluster, copyJarToHadoopLib.toString());
			}
		}
		remoter.close();
	}

	/**
	 * Send mr job jar on agent.
	 *
	 * @param jumbuneRequest the jumbune Request
	 * @param jarFilepath the jar filepath
	 */
	public static void sendMRJobJarOnAgent(
			JumbuneRequest jumbuneRequest, String jarFilepath){
		JobConfig jobConfig =  jumbuneRequest.getJobConfig();
		String jumbuneHome =JobConfig.getJumbuneHome() + File.separator;
		Cluster cluster = jumbuneRequest.getCluster();
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		File resourceDir =new File(jarFilepath);
		File[] files=resourceDir.listFiles();
			for(File file : files){
				if (file.getName().endsWith(".tmp")) {
					file.delete();
				}else{
					String filename = file.getAbsolutePath();
					
					String relativeAgentPath = Constants.JOB_JARS_LOC +File.separator +jobConfig.getFormattedJumbuneJobName()+Constants.MR_RESOURCES;
					String resourceFolder = System.getenv("AGENT_HOME") +File.separator +Constants.JOB_JARS_LOC+File.separator
						+jobConfig.getFormattedJumbuneJobName()+ Constants.MR_RESOURCES;
					File resourceDirAgent = new File(resourceFolder);
					if (!resourceDirAgent.exists()) {
						resourceDirAgent.mkdirs();
					}
					remoter.sendJar(relativeAgentPath, filename);
			}
			}
		remoter.close();
	}

	/**
	 * This method replaces path with jumbune home.
	 *
	 * @param path the path
	 * @return the string[]
	 */
	public static  String[] replaceJumbuneHome(String[] path) {
		String jumbuneHome = JobConfig.getJumbuneHome();
		if (path != null) {
			for (int i = 0; i < path.length; i++) {
				String filePath = path[i];
				if (filePath.contains(Constants.JUMBUNE_ENV_VAR_NAME)) {
					filePath = filePath.replace(Constants.JUMBUNE_ENV_VAR_NAME, jumbuneHome);
					path[i] = filePath;
				}
			}
		}
		return path;
	}
	

	/**
	 * Creates the jumbune directories.
	 *
	 *@param cluster cluster
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public static void createJumbuneDirectories(JumbuneRequest jumbuneRequest)
			throws JSchException, IOException, InterruptedException {
		createNameNodeDirectories(jumbuneRequest.getJobConfig());
		Cluster cluster = jumbuneRequest.getCluster();
		if (! cluster.getWorkers().getHosts().isEmpty()) {
			createWorkingDirectories(jumbuneRequest);
		}
	}
	
	/**
	 * Creates the master directories.
	 */
	public static void createNameNodeDirectories(JobConfig jobConfig) {
		File joblocaion = new File(jobConfig.getJobJarLoc()
				+ jobConfig.getFormattedJumbuneJobName());
		joblocaion.mkdirs();

		File reportLocation = new File(jobConfig.getShellUserReportLocation());
		reportLocation.mkdirs();

		File profilejarLocation = new File(getProfiledJarLocation(jobConfig));
		profilejarLocation.mkdirs();

		File insturmentedjarLocation = new File(getInstrumentedJarLocation(jobConfig));
		insturmentedjarLocation.mkdirs();

		File consolidationLocation = new File(
				jobConfig.getMasterConsolidatedLogLocation());
		consolidationLocation.mkdirs();

		File consolidationDVLocation = new File(
				jobConfig.getMasterConsolidatedDVLocation());
		consolidationDVLocation.mkdirs();

	}

	/**
	 * Creates the slave directories.
	 *
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private static void createWorkingDirectories(JumbuneRequest jumbuneRequest)
			throws JSchException, IOException, InterruptedException {
		
		setRelativeWorkingDirectoryForLog(jumbuneRequest);
		makeWorkerLogDirectory(jumbuneRequest.getCluster());
		
		setRelativeWorkingDirectoryForDV(jumbuneRequest);
		makeWorkerLogDirectory(jumbuneRequest.getCluster());
	}
	
	/**
	 * Make remote slave log directory.
	 * 
	 * @param logCollection
	 *            the log collection
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private static void makeWorkerLogDirectory(Cluster cluster)
			throws JSchException, IOException, InterruptedException {

		Workers workers = cluster.getWorkers();
		String nameNodeHost = cluster.getNameNode();
		Integer agentPort = Integer.valueOf(cluster.getJumbuneAgent().getPort());
		String locationNode = workers.getRelativeWorkingDirectory();
		String parentOfLocationNode = null;
		parentOfLocationNode = locationNode.substring(0, locationNode.lastIndexOf(File.separator));
		boolean loop = locationNode.lastIndexOf(File.separator) > -1 ? true : false;
		
		if (loop) {
			parentOfLocationNode = locationNode.substring(0, locationNode.lastIndexOf(File.separator));
			LOGGER.debug("Location Node " + locationNode);
			LOGGER.debug("Parent of Location Node " + parentOfLocationNode);
		}
		String command = Constants.MKDIR_P_CMD + getFolderName(locationNode);
		LOGGER.debug("Log directory generation command on WorkerNode [" + command + "]");

		for (String workerHost : workers.getHosts()) {
			CommandWritableBuilder builder = new CommandWritableBuilder(cluster, workerHost);
			builder.addCommand(command, false, null, CommandType.FS);

			if (loop) {
				builder.addCommand(Constants.CHMOD_CMD + parentOfLocationNode, false,
						null, CommandType.FS).populate(cluster, workerHost);
			}
			Remoter remoter = RemotingUtil.getRemoter(cluster);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
			LOGGER.debug("Log directory created on WorkerNodes ");
			CONSOLELOGGER.info("Log directory generation on WorkerNodes ");
		}
		
	}
	/**
	 * <p>
	 * Gets folder name from file name
	 * </p>
	 * .
	 * 
	 * @param file
	 *            file name
	 * @return folder name
	 */
	private static String getFolderName(String file) {
		String folderName = null;
		int lastIndexOfDot = file.lastIndexOf('.');
		int lastIndexOfSeparator = file.lastIndexOf('/');
		if (lastIndexOfDot == -1) {
			folderName = file;
		} else if (lastIndexOfSeparator != -1) {
			if (lastIndexOfSeparator > lastIndexOfDot) {
				folderName = file;
			} else {
				folderName = file.substring(0, lastIndexOfSeparator);
			}
		}

		return folderName;
	}
	
	/**
	 * Gets the profiled jar location.
	 *
	 * @return the profiled jar location
	 */
	public static String getProfiledJarLocation(Config config) {
		JobConfig jobConfig = (JobConfig) config;
		return jobConfig.getJobJarLoc() + jobConfig.getFormattedJumbuneJobName()
				+ Constants.PROFILED_JAR_LOC;
	}
	
	/**
	 * Gets the instrumented jar location.
	 *
	 * @return the instrumented jar location
	 */
	public static String getInstrumentedJarLocation(Config config) {
		JobConfig jobConfig = (JobConfig) config;
		return jobConfig.getJobJarLoc() + jobConfig.getFormattedJumbuneJobName()
				+ Constants.INSTRUMENTED_JAR_LOC;
	}
	
	public static void setRelativeWorkingDirectoryForLog(JumbuneRequest jumbuneRequest) {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		NameNodes nameNodes = cluster.getNameNodes();
		nameNodes.setRelativeWorkingDirectory(jobConfig.getMasterConsolidatedLogLocation());
		Workers workers = cluster.getWorkers();
		String relativeWorkingDirWorkers = workers.getWorkDirectory() + Constants.JOB_JARS_LOC
				+ getFormattedJumbuneJobName(jobConfig.getJumbuneJobName())  + Constants.SLAVE_LOG_LOC;
		workers.setRelativeWorkingDirectory(relativeWorkingDirWorkers);
	}
	
	public static void setRelativeWorkingDirectoryForDV(JumbuneRequest jumbuneRequest) {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		NameNodes nameNodes = cluster.getNameNodes();
		nameNodes.setRelativeWorkingDirectory(jobConfig.getMasterConsolidatedDVLocation());
		Workers workers = cluster.getWorkers();
		String relativeWorkingDirWorkers = workers.getWorkDirectory() + Constants.JOB_JARS_LOC
				+ getFormattedJumbuneJobName(jobConfig.getJumbuneJobName())  + Constants.SLAVE_DV_LOC;
		workers.setRelativeWorkingDirectory(relativeWorkingDirWorkers);
	}
	
	public static void setRelativeWorkingDirectoryForXmlDV(JumbuneRequest jumbuneRequest) {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		NameNodes nameNodes = cluster.getNameNodes();
		nameNodes.setRelativeWorkingDirectory(jobConfig.getMasterConsolidatedXmlDVLocation());
		Workers workers = cluster.getWorkers();
		String relativeWorkingDirWorkers = workers.getWorkDirectory() + Constants.JOB_JARS_LOC
				+ getFormattedJumbuneJobName(jobConfig.getJumbuneJobName())  + Constants.SLAVE_XML_DV_LOC;
		workers.setRelativeWorkingDirectory(relativeWorkingDirWorkers);
	}
	
	public static void setRelativeWorkingDirectoryForJsonDV(JumbuneRequest jumbuneRequest) {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		NameNodes nameNodes = cluster.getNameNodes();
		nameNodes.setRelativeWorkingDirectory(jobConfig.getMasterConsolidatedJsonDVLocation());
		Workers workers = cluster.getWorkers();
		String relativeWorkingDirWorkers = workers.getWorkDirectory() + Constants.JOB_JARS_LOC
				+ getFormattedJumbuneJobName(jobConfig.getJumbuneJobName())  + Constants.SLAVE_JSON_DV_LOC;
		workers.setRelativeWorkingDirectory(relativeWorkingDirWorkers);
	}
	
	/**
	 * Gets the formatted jumbune job name.
	 * 
	 * @return the formatted jumbune job name
	 */
	public static String getFormattedJumbuneJobName(String jobNameTemp) {
		if (jobNameTemp == null) {
			return null;
		}

		if (!jobNameTemp.endsWith(File.separator)) {
			jobNameTemp += File.separator;
		}
		return jobNameTemp;
	}
	
	public static boolean isEnable(Enable enable) {
		return (enable != null && Enable.TRUE.equals(enable) ? true : false);

	}
	
}
