package org.jumbune.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.StringUtil;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;



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
	
		StringBuilder sb = new StringBuilder(JumbuneInfo.getHome()).append("resources")
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
			return result.length() > 0;
		} else {
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
		
		String jumbuneHome = JumbuneInfo.getHome();
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
		if (path != null) {
			for (int i = 0; i < path.length; i++) {
				String filePath = path[i];
				if (filePath.contains(Constants.JUMBUNE_ENV_VAR_NAME)) {
					filePath = filePath.replace(Constants.JUMBUNE_ENV_VAR_NAME, JumbuneInfo.getHome());
					path[i] = filePath;
				}
			}
		}
		return path;
	}
	
/**
	 * Make remote slave temp log directory.
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
	public static void makeTempDirectory(JumbuneRequest jumbuneRequest, String tempDirLocation)
			throws IOException{

		Workers workers = jumbuneRequest.getCluster().getWorkers();
		String parentOfLocationNode = null;
		parentOfLocationNode = tempDirLocation.substring(0, tempDirLocation.lastIndexOf(File.separator));
		boolean loop = tempDirLocation.lastIndexOf(File.separator) > -1;
		Cluster cluster = jumbuneRequest.getCluster();
		if (loop) {
			parentOfLocationNode = tempDirLocation.substring(0, tempDirLocation.lastIndexOf(File.separator));
			LOGGER.debug("Location Node " + tempDirLocation);
			LOGGER.debug("Parent of Location Node " + parentOfLocationNode);
		}
		String command = Constants.MKDIR_P_CMD + getFolderName(tempDirLocation);
		LOGGER.debug("Log directory generation command on WorkerNode [" + command + "]");
		
		for (String workerHost : workers.getHosts()) {
			CommandWritableBuilder builder = new CommandWritableBuilder(cluster, workerHost);
			builder.addCommand(command, false, null, CommandType.FS, jumbuneRequest.getJobConfig().getOperatingUser());

			if (loop) {
				builder.addCommand(Constants.CHMOD_CMD + parentOfLocationNode, false, null, CommandType.FS,
						jumbuneRequest.getJobConfig().getOperatingUser())
						.populate(cluster, workerHost);
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
	
	/**
	 * @param This method removes the job jar from jumbune home location 
	 */
	public static void removeJar(String jobName) {
		String jobJarsPath = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC + jobName;
		File dir = new File(jobJarsPath);
		if (!dir.exists()) {
			return;
		}
		removeJarRecursively(dir);
	}
	
	/**
	 * @param This method removes the job jar from jumbune home location 
	 */
	private static void removeJarRecursively(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				removeJarRecursively(file);
			} else if (file.getName().endsWith(Constants.JAR)) {
				file.delete();
			}
		}
	}
	

	public static void sendJars(JumbuneRequest jumbuneRequest, String[] jarsPath) throws Exception {
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();

		String privateKeyPath = null;
		String password;
		String username;
		username = cluster.getAgents().getUser();
		password = cluster.getAgents().getPassword();
		if ((password != null) && (!password.trim().isEmpty())) {
			password = StringUtil.getPlain(cluster.getAgents().getPassword());
		} else {
			privateKeyPath = cluster.getAgents().getSshAuthKeysFile();
		}
		Session session = getSession(cluster.getNameNode(), username, password, privateKeyPath);
		ChannelSftp sftp = getChannel(session);

		String remoteDir = jumbuneRequest.getJobConfig().getTempDirectory() + jobConfig.getJumbuneJobName() + "/lib/";
		createPathInRemote(sftp, new File(remoteDir));
		for (String localJarPath : jarsPath) {
			String remoteFilePath = remoteDir + "/" + localJarPath.substring(localJarPath.lastIndexOf("/") + 1);
			sftp.put(new FileInputStream(localJarPath), remoteFilePath, 0);
		}
		sftp.disconnect();
		session.disconnect();
	}
	
	/**
	 * Create directory in remote machine using sftp
	 * @param sftp
	 * @param file file (even though it is a remote path)
	 * @throws SftpException
	 */
	private static void createPathInRemote(ChannelSftp sftp, File file) throws SftpException {
		if (file.getAbsolutePath().equals("/")) {
			return;
		}
		if (file.getParentFile() != null) {
			createPathInRemote(sftp, file.getParentFile());
		}
		LOGGER.debug("Creating directory [" + file.getAbsolutePath() + "] on server");
		try {
			sftp.mkdir(file.getAbsolutePath());
		} catch (SftpException e) {
			LOGGER.debug("Unable to create directory [" + file.getAbsolutePath() + "] on server.");
		}
	}

	private static Session getSession(String host, String username, String password, String privateKeyPath)
			throws JSchException {
		JSch jsch = new JSch();
		Session session = null;
		if (StringUtils.isNotBlank(privateKeyPath)) {
			jsch.addIdentity(privateKeyPath);
		}
		session = jsch.getSession(username, host, 22);

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		if (StringUtils.isNotBlank(password)) {
			session.setPassword(password);
			config.put("PreferredAuthentications", "password");
		}
		session.setConfig(config);
		session.connect();
		return session;
	}

	private static ChannelSftp getChannel(Session session) throws JSchException {
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftp = (ChannelSftp) channel;
		return sftp;
	}
	
}
