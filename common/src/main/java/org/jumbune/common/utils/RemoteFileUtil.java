package org.jumbune.common.utils;

import static org.jumbune.common.utils.Constants.AT_OP;
import static org.jumbune.common.utils.Constants.COLON;
import static org.jumbune.common.utils.Constants.CPU_DUMP_FILE;
import static org.jumbune.common.utils.Constants.MEM_DUMP_FILE;
import static org.jumbune.common.utils.Constants.SPACE;
import static org.jumbune.common.utils.Constants.UNDERSCORE;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Agent;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.NameNodes;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.client.RemoterFactory;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.RemotingMethodConstants;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UserInfo;

/**
 * This class provides methods to collect or distribute files from to/from
 * master nodes.
 * 
 */
public class RemoteFileUtil {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(RemoteFileUtil.class);

	/** The Constant SCP_R_CMD. */
	private static final String SCP_R_CMD = "scp -r";

	/** The Constant AGENT_HOME. */
	private static final String AGENT_HOME = "AGENT_HOME";

	/** The Constant MKDIR_P_CMD. */
	private static final String MKDIR_P_CMD = "mkdir -p ";

	private static final String CHMOD_CMD = "chmod a+w ";

	private static final String RM_CMD = "rm";

	/** The Constant TOP_DUMP_FILE. */
	private static final String TOP_DUMP_FILE = "top.txt";

	/**
	 * <p>
	 * Create a new instance of ClusterUtil.
	 * </p>
	 */
	public RemoteFileUtil() {
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
	public String getFolderName(String file) {
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
	 * Clear remote log files on master.
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
	public void clearRemoteLogFilesOnMaster(Cluster cluster)
			throws JSchException, IOException, InterruptedException {
		//ToDo:  Change from getRelativeWorkingDirectory() to getLocation() if error occurs
		String nameNodeLocation = cluster.getNameNodes().getRelativeWorkingDirectory();
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		String command = "rm -r " + getFolderName(nameNodeLocation);
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand(command, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		remoter.close();
		LOGGER.debug("Sent Async command on master machine [" + command + "]");
	}

	/**
	 * Clear remote log files on nodes.
	 * 
	 * @param logCollection
	 *            the log collection
	 */
	public void clearRemoteLogFilesOnNodes(Cluster cluster) {

		Workers workers = cluster.getWorkers();
		//ToDo:  Change from getRelativeWorkingDirectory() to getLocation() if error occurs
		String workerLocation = workers.getRelativeWorkingDirectory();
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		for (String workerHost : workers.getHosts()) {
			CommandWritableBuilder builder = new CommandWritableBuilder(cluster, workerHost);
			// connecting to slave
			
			String command = RemotingConstants.REMOVE_FOLDER
					+ RemotingConstants.SINGLE_SPACE
					+ getFolderName(workerLocation);
			builder.addCommand(command, false, null, CommandType.FS);
			LOGGER.debug("Removing log file from Worker node [" + workerHost
					+ "]" + ", command [" + "] command");
			remoter.fireAndForgetCommand(builder.getCommandWritable());
		}
		remoter.close();
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
	public void makeRemoteSlaveLogDirectory(Cluster cluster)
			throws JSchException, IOException, InterruptedException {

		Workers workers = cluster.getWorkers();
		String workerLocation = workers.getWorkDirectory();
		String command = MKDIR_P_CMD + getFolderName(workerLocation);
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		for (String workerHost : workers.getHosts()) {
			LOGGER.debug("Executing command on Worker node [" + command + "]");
			CommandWritableBuilder builder = new CommandWritableBuilder(cluster, workerHost);
			builder.addCommand(command, false, null, CommandType.FS);
			builder.addCommand(CHMOD_CMD + getFolderName(workerLocation),
					false, null, CommandType.FS).populate(cluster, workerHost);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
		}
		remoter.close();
	}

	/**
	 * Copy remote log files
	 * 
	 * @param logCollection
	 *            the log collection
	 * @throws JumbuneException
	 *             the hTF exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@SuppressWarnings("unchecked")
	public void copyRemoteLogFiles(Cluster cluster)
			throws JumbuneException, IOException, InterruptedException {
		NameNodes nameNodes = cluster.getNameNodes();
		String nameNodeLocation = nameNodes.getRelativeWorkingDirectory();
		String hadoopFSUser = cluster.getHadoopUsers().getFsUser();
		String nameNodeHost = cluster.getNameNode();
		String appHome = JobConfig.getJumbuneHome();

		String relativePath = nameNodeLocation.substring(appHome.length() - 1,
				nameNodeLocation.length());
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		String remoteMkdir = MKDIR_P_CMD + AGENT_HOME + relativePath;
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand(remoteMkdir, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		// Creating local directories in Jumbune Working Dir
		String mkdirCmd = MKDIR_P_CMD + nameNodeLocation;
		execute(mkdirCmd.split(" "), null);

		Workers workers = cluster.getWorkers();
		String workerUser = workers.getUser();
		String workerLocation = workers.getRelativeWorkingDirectory();
		
		LOGGER.debug("Starting to copy remote log files...");
		for (String workerHost : workers.getHosts()) {
			LOGGER.debug("Copy log files from: " + workerHost + ":" + workerLocation);
			String command ;
			if(cluster.getAgents().getSshAuthKeysFile() != null && cluster.getAgents().getSshAuthKeysFile().endsWith(".pem")){
			command = "scp -i " + cluster.getAgents().getSshAuthKeysFile() + " -r " + workerUser + "@" + workerHost + ":"
					+ workerLocation + " " + AGENT_HOME + relativePath;}
			else{
				command = "scp -r " + workerUser + "@" + workerHost + ":"
						+ workerLocation + " " + AGENT_HOME + relativePath;
			}
			CommandWritableBuilder copyBuilder = new CommandWritableBuilder(cluster, null);
			copyBuilder.addCommand(command, false, null, CommandType.FS);
			remoter.fireAndForgetCommand(copyBuilder.getCommandWritable());
		}
		builder.clear();
		builder.addCommand(AGENT_HOME + relativePath, false, null,
				CommandType.FS).setMethodToBeInvoked(
				RemotingMethodConstants.PROCESS_GET_FILES);
		List<String> fileList = (List<String>) remoter
				.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		for (String string : fileList) {
			remoter.receiveLogFiles(relativePath, relativePath + "/" + string);
		}
		remoter.close();
	}

	/**
	 * Copy remote log files.
	 * 
	 * @param logCollection
	 *            the log collection
	 * @throws JumbuneException
	 *             the hTF exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	// TODO:
	@SuppressWarnings("unchecked")
	public void copyRemoteDBLogFiles(Cluster cluster)
			throws JumbuneException, IOException, InterruptedException {

		NameNodes nameNodes = cluster.getNameNodes();
		String nameNodeLocation = nameNodes.getRelativeWorkingDirectory();
		String hadoopFSUser = cluster.getHadoopUsers().getFsUser();
		String appHome = JobConfig.getJumbuneHome();
		String relativePath = nameNodeLocation.substring(appHome.length() - 1,
				nameNodeLocation.length());
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		String remoteMkdir = MKDIR_P_CMD + AGENT_HOME + relativePath;

		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, null);
		builder.addCommand(remoteMkdir, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());

		String mkdirCmd = MKDIR_P_CMD + nameNodeLocation;
		execute(mkdirCmd.split(" "), null);

		Workers workers = cluster.getWorkers();
		String workerUser = workers.getUser();
		String workerLocation = workers.getRelativeWorkingDirectory();
		
		for (String workerHost : workers.getHosts()) {
			ConsoleLogUtil.LOGGER.debug("Copy log file from: [" + workerHost
					+ "] to [" + workerLocation + "]");
			LOGGER.debug("Copy log file from: [" + workerHost + "] to ["
					+ workerLocation + "]");
			StringBuilder lsSb = new StringBuilder()
					.append("-")
					.append(workerHost)
					.append("-")
					.append(workerLocation.substring(0,
							workerLocation.indexOf("*.log*"))).append("-")
					.append(relativePath);

			builder.clear();
			builder.addCommand(lsSb.toString(), false, null, CommandType.FS)
					.setMethodToBeInvoked(RemotingMethodConstants.PROCESS_DB_OPT_STEPS);
			remoter.fireCommandAndGetObjectResponse(builder
					.getCommandWritable());
			String command ;
			if(cluster.getAgents().getSshAuthKeysFile()!=null && cluster.getAgents().getSshAuthKeysFile().endsWith(".pem")){
			 command = "scp -i " + cluster.getAgents().getSshAuthKeysFile() + " -r " + workerUser + "@" + workerHost + ":"
					+ workerLocation + " " + AGENT_HOME + relativePath;
			}else{
				command = "scp -r " + workerUser + "@" + workerHost + ":"
						+ workerLocation + " " + AGENT_HOME + relativePath;
			}
			builder.clear();
			builder.addCommand(command, false, null, CommandType.FS);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
		}
		builder.clear();
		builder.addCommand(AGENT_HOME + relativePath, false, null,
				CommandType.FS).setMethodToBeInvoked(
				RemotingMethodConstants.PROCESS_GET_FILES);
		List<String> fileList = (List<String>) remoter
				.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		for (String string : fileList) {
			remoter.receiveLogFiles(relativePath, relativePath + "/" + string);
		}
		for (String string : fileList) {
			if (!string.contains("mrChain")) {
				execute(new String[] { "unzip", string }, appHome + relativePath + "/");
				execute(new String[] { "rm", string }, appHome + relativePath + "/");
			}
		}
	}

	/**
	 * Copy remote lib files to master.
	 * 
	 * @param config
	 *            the loader
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void copyRemoteLibFilesToMaster(
			JumbuneRequest jumbuneRequest) throws IOException, InterruptedException {
		
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		String userLibLoc = jobConfig.getUserLibLocationAtMaster();
		String jobName = jobConfig.getJumbuneJobName();

		Cluster cluster = jumbuneRequest.getCluster();
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		String mkdir = MKDIR_P_CMD + userLibLoc;
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand(mkdir, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());

		Workers workers= cluster.getWorkers();
		if (workers.getHosts().isEmpty()) {
			LOGGER.error("No Worker found in cluster");
			return;
		}
		String workerHost = workers.getHosts().get(0);

		List<String> fileList = ConfigurationUtil.getAllClasspathFiles(
				jobConfig.getClasspathFolders(ClasspathUtil.USER_SUPPLIED),
				jobConfig.getClasspathExcludes(ClasspathUtil.USER_SUPPLIED),
				jobConfig.getClasspathFiles(ClasspathUtil.USER_SUPPLIED));
		String hadoopFSUser = cluster.getHadoopUsers().getFsUser();
		for (String file : fileList) {
			String command = "scp " + workers.getUser() + "@" + workerHost + ":" + file
					+ " " + hadoopFSUser + "@" + cluster.getNameNode()+ ":"
					+ userLibLoc;
			LOGGER.debug("Executing the cmd: " + command);
			builder.clear();
			builder.addCommand(command, false, null, CommandType.FS).populate(cluster, null);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
		}
		remoter.close();
	}

	/**
	 * <p>
	 * UserInfo as required by the JSch library
	 * </p>
	 * .
	 * 
	 */
	public static class JumbuneUserInfo implements UserInfo {

		/**
		 * gets the password
		 */
		public String getPassword() {
			return null;
		}

		/**
		 * set prompt YES/NO
		 */
		public boolean promptYesNo(String str) {
			return true;
		}

		/**
		 * gets the passphrase
		 */
		public String getPassphrase() {
			return null;
		}

		/**
		 * set the passphrase
		 */
		public boolean promptPassphrase(String message) {
			return true;
		}

		/**
		 * set password
		 */
		public boolean promptPassword(String message) {
			return true;
		}

		/**
		 * set the message
		 */
		public void showMessage(String message) {
		}
	}

	/**
	 * Gets the remote threads per core.
	 *
	 * @param loader
	 *            the loader
	 * @param coreOrThread
	 *            the core or thread
	 * @return the remote threads per core
	 * @throws JumbuneException
	 *             the hTF exception
	 */
	public static int getRemoteThreadsOrCore(JumbuneRequest jumbuneRequest, String coreOrThread)
			throws JumbuneException {

		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		Cluster cluster = jumbuneRequest.getCluster();

		String jobName = jobConfig.getJumbuneJobName();
		String command = "lscpu | grep " + coreOrThread;
		Remoter remoter = RemotingUtil.getRemoter(cluster);

		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, null);
		builder.addCommand(command, false, null, CommandType.FS);
		String line = (String) remoter.fireCommandAndGetObjectResponse(builder
				.getCommandWritable());
		remoter.close();
		if (line == null || "".equals(line.trim())) {
			throw JumbuneRuntimeException.throwUnresponsiveIOException(
					RemoteFileUtil.class.getName(), "getRemoteThreadsOrCore",
					"", Constants.FOUR_HUNDERED_FIFTY_SEVEN);
		}
		String[] array = line.split(":");
		if (array.length != 2) {
			throw JumbuneRuntimeException.throwUnresponsiveIOException(
					RemoteFileUtil.class.getName(), "getRemoteThreadsOrCore",
					"", Constants.FOUR_HUNDERED_SIXTY_ONE);
		}
		return Integer.parseInt(array[1].trim());
	}

	/**
	 * Return rack id from IP.
	 * 
	 * @param ip
	 *            example: 192.168.169.52
	 * @return the rack id, example: 192.168.169
	 */
	public static String getRackId(String ip) {
		int lastIndex = ip.lastIndexOf('.');
		return ip.substring(0, lastIndex);
	}

	/**
	 * Return data centre id from IP.
	 * 
	 * @param ip
	 *            example: 192.168.169.52
	 * @return the data centre id, example: 192.168
	 */
	public static String getDataCentreId(String ip) {
		String[] octats = ip.split("\\.");

		if (octats.length > 0) {
			return octats[0] + "." + octats[1];
		} else {
			return null;
		}
	}

	/**
	 * Execute the given command.
	 *
	 * @param commands
	 *            the commands
	 * @param directory
	 *            the directory
	 */
	private void execute(String[] commands, String directory) {

		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		if (directory != null && !directory.isEmpty()) {
			processBuilder.directory(new File(directory));
		} else {
			processBuilder.directory(new File(JobConfig.getJumbuneHome()));
		}
		Process process = null;
		InputStream inputStream = null;
		BufferedReader bufferReader = null;
		try {
			process = processBuilder.start();
			inputStream = process.getInputStream();
			if (inputStream != null) {
				bufferReader = new BufferedReader(new InputStreamReader(
						inputStream));
				String line = bufferReader.readLine();
				while (line != null) {
					line = bufferReader.readLine();
				}
			}

		} catch (IOException e) {
			LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
		} finally {
			try {
				if (bufferReader != null) {
					bufferReader.close();
				}
			} catch (IOException e) {
				LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			}
		}
	}

	/**
	 * Execute response list.
	 *
	 * @param commands
	 *            the commands
	 * @param directory
	 *            the directory
	 * @return the list
	 */
	public static List<String> executeResponseList(String[] commands,
			String directory) {

		List<String> responseList = new ArrayList<String>();
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		if (directory != null && !directory.isEmpty()) {
			processBuilder.directory(new File(directory));
		} else {
			processBuilder.directory(new File(JobConfig.getJumbuneHome()));
		}
		Process process = null;
		InputStream inputStream = null;
		BufferedReader bufferReader = null;
		try {
			process = processBuilder.start();
			inputStream = process.getInputStream();
			if (inputStream != null) {
				bufferReader = new BufferedReader(new InputStreamReader(
						inputStream));
				String line = bufferReader.readLine();
				while (line != null) {
					responseList.add(line);
					line = bufferReader.readLine();
				}
			}
			return responseList;
		} catch (IOException e) {
			LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
		} finally {
			try {
				if (bufferReader != null) {
					bufferReader.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			}
		}
		return responseList;
	}

	/**
	 * Copy System stats files from slaves to Jumbune deploy directory.
	 * 
	 * @param logCollection
	 *            the log collection
	 * @throws JumbuneException
	 *             the hTF exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@SuppressWarnings("unchecked")
	public void copyRemoteSysStatsFiles(Cluster cluster)
			throws JumbuneException, IOException, InterruptedException {

		NameNodes nameNodes = cluster.getNameNodes();
		String nameNodeLocation = nameNodes.getRelativeWorkingDirectory();
		String appHome = JobConfig.getJumbuneHome();
		String relativePath = nameNodeLocation.substring(appHome.length() - 1,
				nameNodeLocation.length());
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		String remoteMkdir = MKDIR_P_CMD + AGENT_HOME + relativePath;

		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand(remoteMkdir, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());

		String mkdirCmd = MKDIR_P_CMD + nameNodeLocation;
		execute(mkdirCmd.split(SPACE), appHome);

		Workers workers = cluster.getWorkers();
		String workerUser = workers.getUser();
		String workerLocation = workers.getRelativeWorkingDirectory();
		
		for (String workerHost : workers.getHosts()) {

			// copy cpu stats file from slaves
			StringBuffer copyCpuFile = new StringBuffer(SCP_R_CMD);
			
			copyCpuFile.append(SPACE).append(workerUser).append(AT_OP)
					.append(workerHost).append(COLON).append(workerLocation)
					.append(File.separator).append(CPU_DUMP_FILE)
					.append(UNDERSCORE).append(workerHost).append(SPACE)
                    .append(AGENT_HOME).append(relativePath);
			
			// copy memory stats file from slaves
			StringBuffer copyMemFile = new StringBuffer(SCP_R_CMD);
			
			copyMemFile.append(SPACE).append(workerUser).append(AT_OP)
					.append(workerHost).append(COLON).append(workerLocation)
					.append(File.separator).append(MEM_DUMP_FILE)
					.append(UNDERSCORE).append(workerHost).append(SPACE)
                    .append(AGENT_HOME).append(relativePath);
			
			String topDumpFile = workerLocation + File.separator
					+ TOP_DUMP_FILE;
			
			StringBuffer rmTopDumpFile = new StringBuffer(RM_CMD);
			rmTopDumpFile.append(SPACE).append(topDumpFile);
			
			StringBuffer rmCpuFile = new StringBuffer(RM_CMD);
			rmCpuFile.append(SPACE).append(workerLocation)
					.append(File.separator).append(CPU_DUMP_FILE)
					.append(UNDERSCORE).append(workerHost);
			
			// copy memory stats file from slaves
			StringBuffer rmMemFile = new StringBuffer(SCP_R_CMD);
			rmMemFile.append(SPACE).append(workerLocation)
					.append(File.separator).append(MEM_DUMP_FILE)
					.append(UNDERSCORE).append(workerHost);
			
			builder.clear();
			
			builder.addCommand(copyCpuFile.toString(), false, null,
					CommandType.FS)
					.addCommand(copyMemFile.toString(), false, null,
							CommandType.FS)
					.addCommand(rmCpuFile.toString(), false, null,
							CommandType.FS)
					.addCommand(rmMemFile.toString(), false, null,
							CommandType.FS)
					.addCommand(rmTopDumpFile.toString(), false, null,
							CommandType.FS);
			
			remoter.fireAndForgetCommand(builder.getCommandWritable());
		}
		builder.clear();
		builder.addCommand(AGENT_HOME + relativePath, false, null,
				CommandType.FS).setMethodToBeInvoked(
				RemotingMethodConstants.PROCESS_GET_FILES);
		List<String> fileList = (List<String>) remoter
				.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		for (String string : fileList) {
			remoter.receiveLogFiles(relativePath, relativePath + File.separator
					+ string);
		}
		remoter.close();
	}

	/**
	 * <p>
	 * This method Makes the log folder on nodes
	 * </p>
	 * .
	 *
	 * @param logCollection
	 *            log collection details
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void makeSlaveLogDirectory(Cluster cluster)
			throws JSchException, IOException, InterruptedException {

		makeRemoteSlaveLogDirectory(cluster);

	}

	/**
	 * <p>
	 * This method clears all the log files on master and nodes
	 * </p>
	 * .
	 *
	 * @param logCollection
	 *            log collection details
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void clearAllLogFiles(Cluster cluster)
			throws JSchException, IOException, InterruptedException {

		clearRemoteLogFilesOnMaster(cluster);
		clearRemoteLogFilesOnNodes(cluster);

	}

	/**
	 * <p>
	 * This method clears all the log files on master
	 * </p>
	 * .
	 *
	 * @param logCollection
	 *            log collection details
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void clearLogFilesOnMaster(Cluster cluster)
			throws JSchException, IOException, InterruptedException {

		clearRemoteLogFilesOnMaster(cluster);
	}

	/**
	 * <p>
	 * This method clears all the log files on all the nodes
	 * </p>
	 * .
	 *
	 * @param logCollection
	 *            log collection details
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void clearLogFilesOnNodes(Cluster cluster)
			throws JSchException, IOException, InterruptedException {

		clearRemoteLogFilesOnNodes(cluster);
	}

	/**
	 * <p>
	 * This method collects all the log files from all the cluster nodes
	 * </p>
	 * .
	 *
	 * @param logCollection
	 *            log collection details
	 * @throws JumbuneException
	 *             If any error occurred
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void copyLogFilesToMaster(Cluster cluster)
			throws JumbuneException, IOException, InterruptedException {

		copyRemoteLogFiles(cluster);
	}
	
	/**
	 * Gets the HDFS paths recursively. The output of this method depends on the output of command {@code hdfs dfs -ls -R  }.
	 * Additionally, this parses and filters the output based on whether {@code includeDirs }  flag is true or false, if true, 
	 * this includes directory entries also in the resulting list. 
	 * 
	 * The output is a list of all the files/directories in the following format. </br></br>
	 * 
	 * -rw-r--r--   1 impadmin supergroup    2805600 2016-04-29 14:53 /Jumbune/Demo/input/PREPROCESSED/data1
	 *
	 * @param cluster the cluster
	 * @param parentPath the parent path
	 * @param includeDirs whether to include directory entries in resulting list
	 * @return the HDFS paths recursively
	 */
	public List<String> getHDFSPathsRecursively(Cluster cluster, String parentPath, boolean includeDirs) {
		List<String> paths = null;
		String response = fireLSRCommandOnHDFS(cluster, parentPath);
		if (response != null && !response.isEmpty()) {
			paths = new ArrayList<>();
			String attrib[] = null;
			try (BufferedReader br = new BufferedReader(new StringReader(response))) {
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					attrib = line.split(Constants.SPACE_REGEX);
					if (attrib.length == 8) {
						if (includeDirs) {
							paths.add(line);
						} else {
							if (!attrib[0].startsWith("d")) {
								paths.add(line);
							}
						}
					}
				}
			} catch (IOException e) {
				LOGGER.error("unable to parse response of lsr command fired on HDFS for path " + parentPath);
			}
		}
		return paths;
	}

	/**
	 * Fires {@code hdfs dfs -ls -R  }command on hdfs on the given path.
	 *
	 * @param cluster the cluster
	 * @param parentPath the parent path
	 * @return the string
	 */
	private String fireLSRCommandOnHDFS(Cluster cluster, String parentPath) {
		Remoter remoter = RemotingUtil.getRemoter(cluster, null);
		StringBuilder command = new StringBuilder().append(Constants.HADOOP_HOME).append(Constants.BIN_HDFS)
				.append(Constants.DFS_LSR).append(parentPath);
		CommandWritableBuilder commandWritableBuilder = new CommandWritableBuilder(cluster);
		commandWritableBuilder.addCommand(command.toString(), false, null, CommandType.HADOOP_FS);
		return (String) remoter.fireCommandAndGetObjectResponse(commandWritableBuilder.getCommandWritable());
	}

	/**
	 * Copies the validation files to Jumbune home 
	 *
	 * @param cluster the cluster
	 * @param jumbuneRequest the jumbune request
	 */
	public void copyLogFilesToMasterForDV(Cluster cluster, JumbuneRequest jumbuneRequest) {
		NameNodes nameNodes = cluster.getNameNodes();
		String nameNodeLocation = nameNodes.getRelativeWorkingDirectory();
		String appHome = JobConfig.getJumbuneHome();

		String relativePath = nameNodeLocation.substring(appHome.length() - 1,
				nameNodeLocation.length());
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		String remoteMkdir = MKDIR_P_CMD + AGENT_HOME + relativePath;
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand(remoteMkdir, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		// Creating local directories in Jumbune Working Dir
		String mkdirCmd = MKDIR_P_CMD + nameNodeLocation;
		execute(mkdirCmd.split(" "), null);

		Workers workers = cluster.getWorkers();
		String workerUser = workers.getUser();
		String workerLocation = workers.getRelativeWorkingDirectory();
		
		LOGGER.debug("Starting to copy remote log files...");
		for (String workerHost : workers.getHosts()) {
			LOGGER.debug("Copy log files from: " + workerHost + ":" + workerLocation);
		String command ;
		if(cluster.getAgents().getSshAuthKeysFile() != null && cluster.getAgents().getSshAuthKeysFile().endsWith(".pem")){
			 command = "scp -i " + cluster.getAgents().getSshAuthKeysFile() + " -r " + workerUser + "@" + workerHost + ":"
					+ workerLocation + " " + AGENT_HOME + relativePath;
		}else{
			command = "scp -r " + workerUser + "@" + workerHost + ":"
					+ workerLocation + " " + AGENT_HOME + relativePath;
		}
			CommandWritableBuilder copyBuilder = new CommandWritableBuilder(cluster, null);
			copyBuilder.addCommand(command, false, null, CommandType.FS);
			remoter.fireAndForgetCommand(copyBuilder.getCommandWritable());
		}
		builder.clear();
		builder.addCommand(AGENT_HOME + relativePath, false, null,
				CommandType.FS).setMethodToBeInvoked(
				RemotingMethodConstants.PROCESS_GET_FILES);
		List<String> fileList = (List<String>) remoter
				.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		try {
			accumulateFileOutput(jumbuneRequest.getJobConfig().getHdfsInputPath(), jumbuneRequest.getJobConfig(), cluster);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		for (String file : fileList) {
			remoter.receiveLogFiles(relativePath, relativePath + "/" + file);
		}
		
		
	}
	
	
	/**
	 * Accumulate file output into single file with sorted first thousand keys mantained.
	 *
	 * @param inputPath the input path
	 * @param jobConfig the job config
	 * @param cluster the cluster
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void accumulateFileOutput(String inputPath, JobConfig jobConfig, Cluster cluster) throws IOException {		
		String dataValidationDirPath = null;
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder DtPath = new StringBuilder();
		
		List<String> dataViolationTypes = new ArrayList<>();
		dataViolationTypes.add(Constants.NUM_OF_FIELDS_CHECK);
		dataViolationTypes.add(Constants.USER_DEFINED_NULL_CHECK);
		dataViolationTypes.add(Constants.USER_DEFINED_DATA_TYPE);
		dataViolationTypes.add(Constants.USER_DEFINED_REGEX_CHECK);	
		
		CommandWritableBuilder builder = null;
		Remoter remoter = null;
		
		String filePaths = getInputPaths(inputPath, jobConfig, getLsrCommandResponse(inputPath, cluster));	
		
		String[] listofFilesFromHDFS = filePaths.split(Constants.COMMA);		
		
		for (String fileFromList : listofFilesFromHDFS) {
				
			for (String violationType : dataViolationTypes) {	
				
				DtPath.append("AGENT_HOME").append(Constants.JOB_JARS_LOC).append(jobConfig.getFormattedJumbuneJobName()).append("dv/")
				.append(violationType).append(Constants.FORWARD_SLASH);		
				
				stringBuilder.append("if ! [ `ls ").append(DtPath).append(" | wc -l` == 0 ]; then ").append("cat ").append(DtPath)				
				.append(fileFromList.replaceFirst("\\.", "").trim()).append("-*").append(" >> ").append(DtPath)
				.append("a1 && sort -n -t \"|\" -k 1 ").append(DtPath).append("a1 -o ").
				append(DtPath).append("a1 && head -n 1000 ").append(DtPath).append("a1 >> ").append(DtPath).append(fileFromList.replaceFirst("\\.", "").trim()).append("&& rm ")
				.append(DtPath).append("a1 ")
				.append(DtPath).append(fileFromList.replaceFirst("\\.", "").trim()).append("-*;")
				.append(" else echo \"No files found hence exiting\"; fi");
				
				dataValidationDirPath = stringBuilder.toString();
				
				builder = new CommandWritableBuilder(cluster);
				builder.addCommand(dataValidationDirPath.trim(), false, null, CommandType.FS).populate(cluster, cluster.getNameNode());		
				remoter = RemotingUtil.getRemoter(cluster);
				remoter.fireAndForgetCommand(builder.getCommandWritable());				
				DtPath.delete(0, DtPath.length());
				stringBuilder.delete(0, stringBuilder.length());				
			}
		}
	}

	/**
	 * Gets the lsr command response containing all the files in the given path.
	 *
	 * @param hdfsFilePath the hdfs file path
	 * @param cluster the cluster
	 * @return the lsr command response
	 */
	private String getLsrCommandResponse(String hdfsFilePath,
			Cluster cluster) {
		Remoter remoter = RemotingUtil.getRemoter(cluster, null);	
		StringBuilder stringBuilder = new StringBuilder().append(Constants.HADOOP_HOME).append(Constants.BIN_HDFS).append(Constants.DFS_LSR).append(hdfsFilePath)
		.append(" | sed 's/  */ /g' | cut -d\\  -f 1,8 --output-delimiter=',' | grep ^- | cut -d, -f2 ");
		CommandWritableBuilder commandWritableBuilder = new CommandWritableBuilder(cluster, null);
		commandWritableBuilder.addCommand(stringBuilder.toString(), false, null, CommandType.HADOOP_FS);
		String commmandResponse = (String) remoter.fireCommandAndGetObjectResponse(commandWritableBuilder.getCommandWritable());		
		return commmandResponse;
	}
	
	
	/**
 * Gets the input paths.
 *
 * @param hdfsFilePath the hdfs file path
 * @param jobConfig the job config
 * @param commandResponse the command response
 * @return the input paths
 * @throws IOException Signals that an I/O exception has occurred.
 */
	private String getInputPaths(String hdfsFilePath,JobConfig jobConfig, String commandResponse) throws IOException{
		
		List<String> listOfFiles = new ArrayList<String>();
		String[] fileResponse = commandResponse.split(Constants.NEW_LINE);
		String filePath = null ;
		for (int i = 0; i < fileResponse.length; i++) {			
			String [] eachFileResponse = fileResponse[i].split("\\s+");
			filePath = eachFileResponse[eachFileResponse.length-1];
			if(filePath.contains(hdfsFilePath)){
			filePath = filePath.replaceAll(File.separator, Constants.DOT);
			listOfFiles.add(filePath);
			}
		}
		return listOfFiles.toString().substring(1, listOfFiles.toString().length()-1);
		
	}
	
	/**
	 * Gets the remote threads per core.
	 *
	 * @param loader
	 *            the loader
	 * @param coreOrThread
	 *            the core or thread
	 * @return the remote threads per core
	 * @throws JumbuneException
	 *             the hTF exception
	 */
	public static int getRemoteThreadsOrCore(Cluster cluster, String coreOrThread, String host)
			throws JumbuneException {

		String command = "lscpu |grep " + coreOrThread + " && exit";
		Remoter remoter = RemotingUtil.getRemoter(cluster);

		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, host);
		builder.addCommand(command, false, null, CommandType.FS);
		String line = (String) remoter.fireCommandAndGetObjectResponse(builder
				.getCommandWritable());
		if (line == null || "".equals(line.trim())) {
			throw JumbuneRuntimeException.throwUnresponsiveIOException(
					RemoteFileUtil.class.getName(), "getRemoteThreadsOrCore",
					"", 1019);
		}
		String[] array = line.split(":");
		if (array.length != 2) {
			throw JumbuneRuntimeException.throwUnresponsiveIOException(
					RemoteFileUtil.class.getName(), "getRemoteThreadsOrCore",
					"", 1026);
		}
		return Integer.parseInt(array[1].trim());
	}
	
}
