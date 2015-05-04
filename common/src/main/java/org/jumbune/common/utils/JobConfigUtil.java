package org.jumbune.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.beans.LogConsolidationInfo;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.Slave;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.remoting.client.Remoter;
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

	
	/** Specify the Job Configuration **/
	private JobConfig jobConfig;
	
	/** Specify the static Job Configuration **/
	private static JobConfig staticJobConfig;
	
	/** The Constant LOGGER. */
	public static final Logger LOGGER = LogManager.getLogger(JobConfigUtil.class);
	
	
	
	public JobConfigUtil(Config config) {
		this.jobConfig = (JobConfig) config;
	}


	public static JobConfig jobConfig(InputStream is) {
		Gson gson = new Gson();
		staticJobConfig = (JobConfig) gson.fromJson(new InputStreamReader(is),
				JobConfig.class);
		return staticJobConfig;
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
	 * @param config
	 *            the config
	 * @return true, if is jumbune supplied jar present
	 */
	public static boolean isJumbuneSuppliedJarPresent(Config config) {
		JobConfig jobConfig = (JobConfig)config;
		Master master = jobConfig.getMaster();
		Remoter remoter = new Remoter(master.getHost(), Integer.valueOf(master.getAgentPort()));
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand("ls lib/", false, null, CommandType.FS);
		String result = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		return (result.length() > 0) ? true : false;
	}

	/**
	 * Checks if is mR job jar present.
	 *
	 * @param config the config
	 * @param jarFilepath the jar filepath
	 * @return true, if is mR job jar present
	 */
	public static boolean isMRJobJarPresent(Config config, String jarFilepath){
		JobConfig jobConfig = (JobConfig)config;
		Master master = jobConfig.getMaster();
		File resourceDir = new File(jarFilepath);
		if(resourceDir.exists()){
			Remoter remoter = new Remoter(master.getHost(), Integer.valueOf(master.getAgentPort()));
			CommandWritableBuilder builder = new CommandWritableBuilder();
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
	public static void sendLibJarCommand(Remoter remoter, Config config, String command) {
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null, CommandType.FS).populate(config, null);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
	
	}

	/**
	 * Send jumbune supplied jar on agent.
	 * 
	 * @param config
	 *            the config
	 * @param cse
	 *            the cse
	 * @param agentHome
	 *            the agent home
	 */
	public static void sendJumbuneSuppliedJarOnAgent(Config config, ClasspathElement cse, String agentHome) {
		String jumbuneHome = JobConfig.getJumbuneHome();
		Remoter remoter = RemotingUtil.getRemoter(config, jumbuneHome);
		String hadoopHome = RemotingUtil.getHadoopHome(remoter, config);
		String[] files = cse.getFiles();
		for (String string : files) {
			remoter.sendJar("lib/", string.replace(agentHome, jumbuneHome));
	
			if (string.contains("log4j")) {
				StringBuilder copyJarToHadoopLib = new StringBuilder().append(Constants.COPY_COMMAND).append(string).append(" ").append(hadoopHome)
						.append(Constants.LIB_DIRECTORY);
				sendLibJarCommand(remoter, config, copyJarToHadoopLib.toString());
			}
		}
		remoter.close();
	}

	/**
	 * Send mr job jar on agent.
	 *
	 * @param config the config
	 * @param jarFilepath the jar filepath
	 */
	public static void sendMRJobJarOnAgent(Config config, String jarFilepath){
		JobConfig jobConfig = (JobConfig)config;
		String jumbuneHome =JobConfig.getJumbuneHome() + File.separator;
		Remoter remoter = RemotingUtil.getRemoter(config, jumbuneHome);
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
	 * Check if jumbune home ends with slash.
	 *
	 * @param config checks of Jumbune Home ends with slash or not.
	 */
	public static  void checkIfJumbuneHomeEndsWithSlash(Config config) {
		JobConfig jobConfig = (JobConfig)config;
		if (!(jobConfig.getSlaveWorkingDirectory().endsWith(File.separator))) {
			String jumbuneHome = jobConfig.getSlaveWorkingDirectory();
			jobConfig.setSlaveWorkingDirectory(jumbuneHome + File.separator);
		}
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
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void createJumbuneDirectories() throws JSchException, IOException,
			InterruptedException {
		createMasterDirectories();
		if (jobConfig.getSlaves().size() > 0) {
			createSlaveDirectories();
		}
	}
	
	/**
	 * Creates the master directories.
	 */
	public void createMasterDirectories() {
		File joblocaion = new File(jobConfig.getJobJarLoc()
				+ jobConfig.getFormattedJumbuneJobName());
		joblocaion.mkdirs();

		File reportLocation = new File(jobConfig.getShellUserReportLocation());
		reportLocation.mkdirs();

		File profilejarLocation = new File(getProfiledJarLocation());
		profilejarLocation.mkdirs();

		File insturmentedjarLocation = new File(getInstrumentedJarLocation());
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
	private void createSlaveDirectories() throws JSchException, IOException,
			InterruptedException {
		makeRemoteSlaveLogDirectory(jobConfig.getLogDefinition());
		makeRemoteSlaveLogDirectory(jobConfig.getDVDefinition());
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
	private static void makeRemoteSlaveLogDirectory(
			LogConsolidationInfo logCollection) throws JSchException,
			IOException, InterruptedException {

		List<Slave> listSlave = logCollection.getSlaves();
		Master master = logCollection.getMaster();
		String masterHost = master.getHost();
		Integer agentPort = Integer.valueOf(master.getAgentPort());

		Remoter remoter = new Remoter(masterHost, agentPort);
		for (Slave slaveDefinition : listSlave) {

			String[] hostsNode = slaveDefinition.getHosts();
			String locationNode = slaveDefinition.getLocation();

			for (String hostNode : hostsNode) {
				String command = Constants.MKDIR_P_CMD + getFolderName(locationNode);
				LOGGER.debug("Log directory generation command on WorkerNode ["
						+ command + "]");

				CommandWritableBuilder builder = new CommandWritableBuilder();
				builder.addCommand(command, false, null, CommandType.FS)
						.populateFromLogConsolidationInfo(logCollection,
								hostNode);
				String parentOfLocationNode = null;
				if (locationNode.lastIndexOf(File.separator) > -1) {
					parentOfLocationNode = locationNode.substring(0,
							locationNode.lastIndexOf(File.separator));
					LOGGER.info("Location Node " + locationNode);
					LOGGER.info("Parent of Location Node "
							+ parentOfLocationNode);
					builder.addCommand(Constants.CHMOD_CMD + parentOfLocationNode, false,
							null, CommandType.FS)
							.populateFromLogConsolidationInfo(logCollection,
									hostNode);
				}
				remoter.fireAndForgetCommand(builder.getCommandWritable());
			}
			LOGGER.info("Log directory created on WorkerNodes ");
			CONSOLELOGGER.info("Log directory generation on WorkerNodes ");

		}
		remoter.close();
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
	public final String getProfiledJarLocation() {
		return jobConfig.getJobJarLoc() + jobConfig.getFormattedJumbuneJobName()
				+ Constants.PROFILED_JAR_LOC;
	}
	
	/**
	 * Gets the instrumented jar location.
	 *
	 * @return the instrumented jar location
	 */
	public final String getInstrumentedJarLocation() {
		return jobConfig.getJobJarLoc() + jobConfig.getFormattedJumbuneJobName()
				+ Constants.INSTRUMENTED_JAR_LOC;
	}
}
