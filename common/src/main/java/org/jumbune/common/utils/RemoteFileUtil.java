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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.LogConsolidationInfo;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.Slave;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.ApiInvokeHintsEnum;
import org.jumbune.remoting.common.RemotingConstants;
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
	private static final Logger LOGGER = LogManager.getLogger(RemoteFileUtil.class);

	/** The Constant SCP_R_CMD. */
	private static final String SCP_R_CMD = "scp -r";

	/** The Constant AGENT_HOME. */
	private static final String AGENT_HOME = "AGENT_HOME";

	/** The Constant MKDIR_P_CMD. */
	private static final String MKDIR_P_CMD = "mkdir -p ";

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
	public void clearRemoteLogFilesOnMaster(LogConsolidationInfo logCollection) throws JSchException, IOException, InterruptedException {

		Master master = logCollection.getMaster();
		String locationMaster = master.getLocation();
		String hostMaster = master.getHost();
		Integer agentPort = Integer.valueOf(master.getAgentPort());
		Remoter remoter = new Remoter(hostMaster, agentPort);
		String command = "rm -r " + getFolderName(locationMaster);
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null);
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
	public void clearRemoteLogFilesOnNodes(LogConsolidationInfo logCollection) {

		List<Slave> listSlave = logCollection.getSlaves();
		Master master = logCollection.getMaster();
		String masterHost = master.getHost();
		Integer agentPort = Integer.valueOf(master.getAgentPort());
		Remoter remoter = null;
		for (Slave slaveDefinition : listSlave) {
			String[] hostsNode = slaveDefinition.getHosts();
			String locationNode = slaveDefinition.getLocation();
			CommandWritableBuilder builder = new CommandWritableBuilder();
			// connecting to slave
			for (String hostNode : hostsNode) {
				remoter = new Remoter(masterHost, agentPort);
				String command = RemotingConstants.REMOVE_FOLDER + RemotingConstants.SINGLE_SPACE + getFolderName(locationNode);
				builder.addCommand(command, false, null).populateFromLogConsolidationInfo(logCollection, hostNode);
				LOGGER.debug("Removing log file from Worker node [" + hostNode + "]" + ", command [" + "] command");
				remoter.fireAndForgetCommand(builder.getCommandWritable());
				remoter.close();
			}
		}
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
	public void makeRemoteSlaveLogDirectory(LogConsolidationInfo logCollection) throws JSchException, IOException, InterruptedException {
		List<Slave> listSlave = logCollection.getSlaves();
		Master master = logCollection.getMaster();
		String masterHost = master.getHost();
		Integer agentPort = Integer.valueOf(master.getAgentPort());
		Remoter remoter = new Remoter(masterHost, agentPort);
		for (Slave slaveDefinition : listSlave) {
			String[] hostsNode = slaveDefinition.getHosts();
			String locationNode = slaveDefinition.getLocation();
			for (String hostNode : hostsNode) {
			    String command = MKDIR_P_CMD + getFolderName(locationNode);
				LOGGER.debug("Executing command on Worker node [" + command + "]");
				CommandWritableBuilder builder = new CommandWritableBuilder();
				builder.addCommand(command, false, null).populateFromLogConsolidationInfo(logCollection, null);
				remoter.fireAndForgetCommand(builder.getCommandWritable());
			}
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
	public void copyRemoteLogFiles(LogConsolidationInfo logCollection) throws JumbuneException, IOException, InterruptedException {

		Master master = logCollection.getMaster();
		String locationMaster = master.getLocation();
		String userMaster = master.getUser();
		String hostMaster = master.getHost();
		String appHome = YamlLoader.getjHome();
		String relativePath = locationMaster.substring(appHome.length() - 1, locationMaster.length());
		Remoter remoter = new Remoter(hostMaster, Integer.valueOf(master.getAgentPort()));
		String remoteMkdir = MKDIR_P_CMD + AGENT_HOME + relativePath;
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(remoteMkdir, false, null);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		//Creating local directories in Jumbune Working Dir
		String mkdirCmd = MKDIR_P_CMD + locationMaster;
		execute(mkdirCmd.split(" "), null);

		List<Slave> listSlave = logCollection.getSlaves();
		LOGGER.debug("Starting to copy remote log files...");
		for (Slave slaveDefinition : listSlave) {
			String userNode = slaveDefinition.getUser();
			String[] hostsNode = slaveDefinition.getHosts();
			String locationNode = slaveDefinition.getLocation();

			for (String hostNode : hostsNode) {
				LOGGER.debug("Copy log files from: " + hostNode + ":" + locationNode);
				String command = "scp -r " + userNode + "@" + hostNode + ":" + locationNode + " " + userMaster + "@" + hostMaster + ":" + AGENT_HOME
						+ relativePath;
				CommandWritableBuilder copyBuilder = new CommandWritableBuilder();
				copyBuilder.addCommand(command, false, null).populateFromLogConsolidationInfo(logCollection, null);
				remoter.fireAndForgetCommand(copyBuilder.getCommandWritable());
			}
		}
		builder.getCommandBatch().clear();
		builder.addCommand(AGENT_HOME + relativePath, false, null).setApiInvokeHints(ApiInvokeHintsEnum.GET_FILES);
		String[] files = (String[]) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		for (String string : files) {
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
	public void copyRemoteDBLogFiles(LogConsolidationInfo logCollection) throws JumbuneException, IOException, InterruptedException {

		Master master = logCollection.getMaster();
		String locationMaster = master.getLocation();
		String userMaster = master.getUser();
		String hostMaster = master.getHost();
		String appHome = YamlLoader.getjHome();
		String relativePath = locationMaster.substring(appHome.length() - 1, locationMaster.length());
		Remoter remoter = new Remoter(hostMaster, Integer.valueOf(master.getAgentPort()));
		String remoteMkdir = MKDIR_P_CMD + AGENT_HOME + relativePath;

		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(remoteMkdir, false, null);
		remoter.fireAndForgetCommand(builder.getCommandWritable());

		String mkdirCmd = MKDIR_P_CMD + locationMaster;
		execute(mkdirCmd.split(" "), null);

		List<Slave> listSlave = logCollection.getSlaves();
		for (Slave slaveDefinition : listSlave) {
			String userNode = slaveDefinition.getUser();
			String[] hostsNode = slaveDefinition.getHosts();
			String locationNode = slaveDefinition.getLocation();

			for (String hostNode : hostsNode) {
				ConsoleLogUtil.LOGGER.debug("Copy log file from: [" + hostNode + "] to [" + locationNode + "]");
				LOGGER.debug("Copy log file from: [" + hostNode + "] to [" + locationNode + "]");
				StringBuilder lsSb = new StringBuilder().append("-").append(hostNode).append("-")
						.append(locationNode.substring(0, locationNode.indexOf("*.log*"))).append("-").append(relativePath);

				builder.getCommandBatch().clear();
				builder.addCommand(lsSb.toString(), false, null).populateFromLogConsolidationInfo(logCollection, null)
						.setApiInvokeHints(ApiInvokeHintsEnum.DB_DOUBLE_HASH);
				remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
				String command = "scp -r " + userNode + "@" + hostNode + ":" + locationNode + " " + userMaster + "@" + hostMaster + ":" + AGENT_HOME
						+ relativePath;

				builder.getCommandBatch().clear();
				builder.addCommand(command, false, null);
				remoter.fireAndForgetCommand(builder.getCommandWritable());
			}
		}
		builder.getCommandBatch().clear();
		builder.addCommand(AGENT_HOME + relativePath, false, null).setApiInvokeHints(ApiInvokeHintsEnum.GET_FILES);
		String[] files = (String[]) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		for (String string : files) {
			remoter.receiveLogFiles(relativePath, relativePath + "/" + string);
		}
		remoter.close();
		for (String string : files) {
			if (!string.contains("mrChain")) {
				execute(new String[] { "unzip", string }, appHome + relativePath + "/");
				execute(new String[] { "rm", string }, appHome + relativePath + "/");
			}
		}
	}


	/**
	 * Copy remote lib files to master.
	 * 
	 * @param loader
	 *            the loader
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public void copyRemoteLibFilesToMaster(Loader loader) throws IOException, InterruptedException {
		YamlLoader yamlLoader = (YamlLoader)loader;
		String userLibLoc = yamlLoader.getUserLibLocatinAtMaster();
		Master master = yamlLoader.getMasterInfo();

		String jobName = yamlLoader.getJumbuneJobName();

		Remoter remoter = new Remoter(master.getHost(), Integer.valueOf(master.getAgentPort()), jobName);
		String mkdir = MKDIR_P_CMD + userLibLoc;
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(mkdir, false, null);
		remoter.fireAndForgetCommand(builder.getCommandWritable());

		Slave slave = yamlLoader.getSlavesInfo().get(0);
		String host = slave.getHosts()[0];

		List<String> fileList = ConfigurationUtil.getAllClasspathFiles(yamlLoader.getClasspathFolders(ClasspathUtil.USER_SUPPLIED),
				yamlLoader.getClasspathExcludes(ClasspathUtil.USER_SUPPLIED), yamlLoader.getClasspathFiles(ClasspathUtil.USER_SUPPLIED));

		for (String file : fileList) {
			String command = "scp " + slave.getUser() + "@" + host + ":" + file + " " + master.getUser() + "@" + master.getHost() + ":" + userLibLoc;
			LOGGER.debug("Executing the cmd: " + command);
			builder.getCommandBatch().clear();
			builder.addCommand(command, false, null).populate(yamlLoader.getYamlConfiguration(), null);
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
	public static int getRemoteThreadsOrCore(Loader loader, String coreOrThread) throws JumbuneException {

		YamlLoader yamlLoader = (YamlLoader)loader;
		Master master = yamlLoader.getLogMaster();
		String host = master.getHost();
		Integer agentPort = Integer.valueOf(master.getAgentPort());

		String jobName = yamlLoader.getJumbuneJobName();
		String command = "lscpu | grep " + coreOrThread;
		Remoter remoter = new Remoter(host, agentPort, jobName);

		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null).populate(yamlLoader.getYamlConfiguration(), null);
		String line = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		if (line == null || "".equals(line.trim())) {
			throw JumbuneRuntimeException.throwUnresponsiveIOException(RemoteFileUtil.class.getName(),"getRemoteThreadsOrCore","",Constants.FOUR_HUNDERED_FIFTY_SEVEN);
		}
		String[] array = line.split(":");
		if (array.length != 2) {
			throw JumbuneRuntimeException.throwUnresponsiveIOException(RemoteFileUtil.class.getName(),"getRemoteThreadsOrCore","",Constants.FOUR_HUNDERED_SIXTY_ONE);
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
	 * Find slave matched with IP.
	 * 
	 * @param nodeIp
	 *            the nodeIp.
	 * @param slaves
	 *            the slave list
	 * @return the matched slave.
	 */
	public static Slave findSlave(String nodeIp, List<Slave> slaves) {
		Slave slave = null;
		Iterator<Slave> itrSlave = slaves.iterator();

		while (itrSlave.hasNext() && slave == null) {
			Slave slaveNode = itrSlave.next();

			for (String ip : slaveNode.getHosts()) {
				if (nodeIp.equalsIgnoreCase(ip)) {
					slave = slaveNode;
					/*
					 * Break this for loop once slave found.
					 */
					break;
				}
			}
		}

		return slave;
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

		ProcessBuilder pb = new ProcessBuilder(commands);
		if (directory != null && !directory.isEmpty()) {
			pb.directory(new File(directory));
		} else {
			pb.directory(new File(YamlLoader.getjHome()));
		}
		Process p = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			p = pb.start();
			is = p.getInputStream();
			if (is != null) {
				br = new BufferedReader(new InputStreamReader(is));
				String line = br.readLine();
				while (line != null) {
					line = br.readLine();
				}
			}

		} catch (IOException e) {
			LOGGER.error(e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if(is != null){
					is.close();
				}
			} catch (IOException e) {
				LOGGER.error(e);
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
	public static List<String> executeResponseList(String[] commands, String directory) {

		List<String> responseList = new ArrayList<String>();
		ProcessBuilder pb = new ProcessBuilder(commands);
		if (directory != null && !directory.isEmpty()) {
			pb.directory(new File(directory));
		} else {
			pb.directory(new File(YamlLoader.getjHome()));
		}
		Process p = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			p = pb.start();
			is = p.getInputStream();
			if (is != null) {
				br = new BufferedReader(new InputStreamReader(is));
				String line = br.readLine();
				while (line != null) {
					responseList.add(line);
					line = br.readLine();
				}
			}
		  return responseList;
		} catch (IOException e) {
			LOGGER.error(e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if(is != null){
					is.close();
				}
			} catch (IOException e) {
				LOGGER.error(e);
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
	public void copyRemoteSysStatsFiles(LogConsolidationInfo logCollection) throws JumbuneException, IOException, InterruptedException {

		Master master = logCollection.getMaster();
		String locationMaster = master.getLocation();
		String userMaster = master.getUser();
		String hostMaster = master.getHost();

		String appHome = YamlLoader.getjHome();
		String relativePath = locationMaster.substring(appHome.length() - 1, locationMaster.length());
		Remoter remoter = new Remoter(hostMaster, Integer.valueOf(master.getAgentPort()));
		String remoteMkdir = MKDIR_P_CMD + AGENT_HOME + relativePath;

		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(remoteMkdir, false, null);
		remoter.fireAndForgetCommand(builder.getCommandWritable());

		String mkdirCmd = MKDIR_P_CMD + locationMaster;
		execute(mkdirCmd.split(SPACE), appHome);

		List<Slave> listSlave = logCollection.getSlaves();
		for (Slave slaveDefinition : listSlave) {
			String userNode = slaveDefinition.getUser();
			String[] hostsNode = slaveDefinition.getHosts();
			String locationNode = slaveDefinition.getLocation();

			for (String hostNode : hostsNode) {

				// copy cpu stats file from slaves
				StringBuffer copyCpuFile = new StringBuffer(SCP_R_CMD);
				copyCpuFile.append(SPACE).append(userNode).append(AT_OP).append(hostNode).append(COLON).append(locationNode).append(File.separator)
						.append(CPU_DUMP_FILE).append(UNDERSCORE).append(hostNode).append(SPACE).append(userMaster).append(AT_OP).append(hostMaster)
						.append(COLON).append(AGENT_HOME).append(relativePath);

				// copy memory stats file from slaves
				StringBuffer copyMemFile = new StringBuffer(SCP_R_CMD);
				copyMemFile.append(SPACE).append(userNode).append(AT_OP).append(hostNode).append(COLON).append(locationNode).append(File.separator)
						.append(MEM_DUMP_FILE).append(UNDERSCORE).append(hostNode).append(SPACE).append(userMaster).append(AT_OP).append(hostMaster)
						.append(COLON).append(AGENT_HOME).append(relativePath);

				builder.getCommandBatch().clear();
				builder.addCommand(copyCpuFile.toString(), false, null).addCommand(copyMemFile.toString(), false, null);
				remoter.fireAndForgetCommand(builder.getCommandWritable());
			}
		}
		builder.getCommandBatch().clear();
		builder.addCommand(AGENT_HOME + relativePath, false, null).setApiInvokeHints(ApiInvokeHintsEnum.GET_FILES);
		String[] files = (String[]) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		for (String string : files) {
			remoter.receiveLogFiles(relativePath, relativePath + File.separator + string);
		}
		remoter.close();
	}


	/**
	 * <p>
	 * This method Makes the log folder on nodes
	 * </p>.
	 *
	 * @param logCollection log collection details
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public void makeSlaveLogDirectory(LogConsolidationInfo logCollection) throws JSchException, IOException, InterruptedException {
	
		makeRemoteSlaveLogDirectory(logCollection);
	
	}


	/**
	 * <p>
	 * This method clears all the log files on master and nodes
	 * </p>.
	 *
	 * @param logCollection log collection details
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public  void clearAllLogFiles(LogConsolidationInfo logCollection) throws JSchException, IOException, InterruptedException {
		
		clearRemoteLogFilesOnMaster(logCollection);
		clearRemoteLogFilesOnNodes(logCollection);
	
	
	}


	/**
	 * <p>
	 * This method clears all the log files on master
	 * </p>.
	 *
	 * @param logCollection log collection details
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public void clearLogFilesOnMaster(LogConsolidationInfo logCollection) throws JSchException, IOException, InterruptedException {
	
		clearRemoteLogFilesOnMaster(logCollection);
	}


	/**
	 * <p>
	 * This method clears all the log files on all the nodes
	 * </p>.
	 *
	 * @param logCollection log collection details
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public void clearLogFilesOnNodes(LogConsolidationInfo logCollection) throws JSchException, IOException, InterruptedException {
	
		clearRemoteLogFilesOnNodes(logCollection);
	}


	/**
	 * Copy db log files to master.
	 *
	 * @param logCollection the log collection
	 * @throws JumbuneException the hTF exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public  void copyDBLogFilesToMaster(LogConsolidationInfo logCollection) throws JumbuneException, IOException, InterruptedException {
				copyRemoteDBLogFiles(logCollection);
			}


	/**
	 * <p>
	 * This method collects all the log files from all the cluster nodes
	 * </p>.
	 *
	 * @param logCollection log collection details
	 * @throws JumbuneException If any error occurred
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public  void copyLogFilesToMaster(LogConsolidationInfo logCollection) throws JumbuneException, IOException, InterruptedException {
	
		copyRemoteLogFiles(logCollection);
	}
}
