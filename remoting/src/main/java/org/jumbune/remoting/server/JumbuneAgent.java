package org.jumbune.remoting.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.BufferedInputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jumbune.remoting.common.BasicJobConfig;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.JschUtil;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.StringUtil;
import org.jumbune.remoting.writable.CommandWritable;
import org.jumbune.remoting.writable.CommandWritable.Command;

import com.jcraft.jsch.JSchException;

/**
 * The Class JumbuneAgent is used for running the remoting jar on agent
 */
public final class JumbuneAgent {

	/** M represents MapR Hadoop distribution */
	private static final String M = "m";

	/** C represents CDH Hadoop distribution */
	private static final String C = "c";

	/** A represents APACHE Hadoop distribution */
	private static final String A = "a";

	/** H represents HDP Hadoop distribution */
	private static final String H = "h";

	/** Name of Hadoop distribution file */
	private static final String DISTRIBUTION_PROPERTIES = "distribution.properties";

	/** The Hadoop distribution */
	private static final String HADOOP_DISTRIBUTION = "hadoop-distribution";

	/** Represent Yarn distribution of Hadoop */
	private static final String YARN = "Yarn";

	/** Represents Non-Yarn distribution of Hadoop */
	private static final String NON_YARN = "Non-Yarn";

	private static final String VERBOSE = "-verbose";

	private static final String ROLLING_FILE_APPENDER = "rollingFileAppender";

	/** The jars. */
	private static List<String> jars = new ArrayList<String>(2);

	/** The Constant LOGGER. */
	public static final Logger CONSOLE_LOGGER = LogManager
			.getLogger("EventLogger");

	public static final Logger LOGGER = LogManager
			.getLogger(JumbuneAgent.class);

	private static final Scanner SCANNER = new Scanner(System.in);

	/** The Constant CAT_CMD. */
	private static final String CAT_CMD = "cat";

	/** The Constant PID_FILE. */
	private static final String PID_FILE = "pid.txt";

	private static final String SPACE = " ";

	private static final String JSON_INFO = "/jsonInfo.ser";

	private static EventLoopGroup bossGroup;

	private static EventLoopGroup workerGroup;

	/**
	 * Instantiates a new jumbune agent.
	 */
	private JumbuneAgent() {
	}

	static {
		jars.add("/lib/log4j-api-2.1.jar");
		jars.add("/lib/log4j-core-2.1.jar");
	}

	/**
	 * * This method is used to up and run the agent on namenode.
	 * 
	 * @param jumbuneAgentArgs
	 *            which is port on which user wants to up jumbune agent on
	 *            hadoop namenode
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] jumbuneAgentArgs) throws IOException,
			InterruptedException, URISyntaxException, ClassNotFoundException {
		String agentHome = null;
		if (jumbuneAgentArgs.length < 1 || jumbuneAgentArgs.length > 2) {
			CONSOLE_LOGGER
					.info("Usage: java -jar <jar-name> <agent-port>  [|-verbose ]");
			System.exit(0);
		}		
		agentHome = System.getenv("AGENT_HOME");				

		if (agentHome == null || "".equals(agentHome.trim())) {
			throw new IllegalArgumentException(
					"$AGENT_HOME is not set properly!!");
		}

		final String agentHomeDir = agentHome.endsWith(File.separator) ? agentHome
				: agentHome + File.separator;
		if (jumbuneAgentArgs.length > 1) {
			if (jumbuneAgentArgs.length == 2 && jumbuneAgentArgs[1] != null
					&& jumbuneAgentArgs[1].equals(VERBOSE)) {
				turnLoggingLevelToDebug(jumbuneAgentArgs[1]);
			} else {
				CONSOLE_LOGGER
						.info("Usage: java -jar <jar-name> <agent-port>  [|-verbose]");
			}
		}

		// if path assigned as agent home doesn't exists then creating it on
		// file system.
		File file = new File(agentHomeDir);
		if (checkNullEmptyORNotADirectory(file)) {
			file.mkdirs();
		}

		validateHadoopConfiguration();
		int port = Integer.parseInt(jumbuneAgentArgs[0]);

		// Creating lib directory and extract all contents in jar's lib into
		// $AGENT_HOME/lib directory
		String libLocation = agentHomeDir + "/lib/";
		file = new File(libLocation);
		if (file.exists()) {
			String[] files = file.list();
			for (String fileJars : files) {
				File jarFiles = new File(file.getPath(), fileJars);
				jarFiles.delete();
			}
			file.delete();
		}
		if (checkNullEmptyORNotADirectory(file)) {
			file.mkdir();
			extractlibJars(libLocation);
		}
		copyAgentLibJarsToHadoopLib(jars, agentHomeDir);
		ServerBootstrap bootstrap;
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		try {
			bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast("JaDecoder",
									new JumbuneAgentDecoder(agentHomeDir));
						}
					});
			CONSOLE_LOGGER.info("Jumbune Agent started successfully on port ["
					+ port + "]");
			// Bind and start to accept incoming connections.
			bootstrap.bind(port).sync().channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				// killing top command
				String agentHome = System.getenv("AGENT_HOME");
				try {
					ObjectInputStream objectinputstream = null;
					InputStream streamIn = null;
					try {
						File file = new File(agentHome + JSON_INFO);
						if (file.exists()) {
							streamIn = new FileInputStream(agentHome
									+ JSON_INFO);
							objectinputstream = new ObjectInputStream(streamIn);
							BasicJobConfig basicJobConfig = (BasicJobConfig) objectinputstream
									.readObject();
							shutTopCmdOnSlaves(basicJobConfig);
						}
					} finally {
						if (objectinputstream != null) {
							objectinputstream.close();
						}
						if (streamIn != null) {
							streamIn.close();
						}
					}
					bossGroup.shutdownGracefully();
					workerGroup.shutdownGracefully();
					LOGGER.debug("Released external resources !");
				} catch (IOException e) {
					LOGGER.error(e);
				} catch (ClassNotFoundException e) {
					LOGGER.error(e);
				}
			}
		});
	}

	/***
	 * Validates and loades hadoop cluster configuration
	 * 
	 * @param jumbuneAgentArgs
	 * @param agentHome
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static void validateHadoopConfiguration() throws IOException,
			ClassNotFoundException {

		String hadoopHome = null;
		String hadoopType = null;
		String distributionType = null;

		HadoopConfigurationPropertyLoader hcpl = HadoopConfigurationPropertyLoader
				.getInstance();
		if (!hcpl.isPropertyLoaded()) {
			getHadoopConfigurationFromUser(hcpl);
		} else {
			hadoopHome = hcpl.getHadoopHome();
			hadoopType = hcpl.getHadoopType();
			distributionType = hcpl.getDistributionType();
			CONSOLE_LOGGER.info("HADOOP HOME: [" + hadoopHome + "]");
			CONSOLE_LOGGER.info("HADOOP TYPE: [" + hadoopType + "]");
			switch (distributionType) {
			case "c":
				distributionType = "CDH";
				break;
			case "h":
				distributionType = "HDP";
				break;
			case "m":
				distributionType = "MapR";
				break;
			default:
				distributionType = "Apache";
			}
			CONSOLE_LOGGER.info("DISTRIBUTION : [" + distributionType + "]");
		}
	}

	private static void getHadoopConfigurationFromUser(
			HadoopConfigurationPropertyLoader hcpl) throws IOException {
		String hadoopHome;
		String hadoopType;
		String distributionType;
		Properties prop = new Properties();
		prop.load(JumbuneAgent.class.getClassLoader().getResourceAsStream(
				DISTRIBUTION_PROPERTIES));
		hadoopType = prop.getProperty(HADOOP_DISTRIBUTION);

		if (hadoopType.equalsIgnoreCase(YARN)) {
			CONSOLE_LOGGER
					.info("Choose the Hadoop Distribution Type : (a)Apache | (c)Cloudera | (h)HortonWorks");
			distributionType = SCANNER.nextLine().trim();
			while (distributionType.isEmpty()
					|| (!distributionType.equalsIgnoreCase(A)
							&& !distributionType.equalsIgnoreCase(H) && !distributionType
								.equalsIgnoreCase(C))) {
				CONSOLE_LOGGER
						.info("Invalid input! Choose from the given Hadoop Distribution Type : (a)Apache | (c)Cloudera | (h)HortonWorks");
				distributionType = SCANNER.nextLine().trim();
			}
		} else {
			CONSOLE_LOGGER
					.info("Choose the Hadoop Distribution Type : (a)Apache | (m)MapR");
			distributionType = SCANNER.nextLine().trim();
			while (distributionType.isEmpty()
					|| (!distributionType.equalsIgnoreCase(A) && !distributionType
							.equalsIgnoreCase(M))) {
				CONSOLE_LOGGER
						.info("Invalid input! Choose from the given Hadoop Distribution Type : (a)Apache | (m)MapR");
				distributionType = SCANNER.nextLine().trim();
			}
		}
		hadoopHome = System.getenv("HADOOP_HOME");
		String promptedHadoopHome;
		if (hadoopHome != null && !"".equals(hadoopHome.trim())) {
			CONSOLE_LOGGER.info("Please verify Hadoop installation directory ["
					+ hadoopHome + "]");
			promptedHadoopHome = SCANNER.nextLine().trim();
			if ("".equals(promptedHadoopHome)) {
				promptedHadoopHome = hadoopHome;
			}
		} else {
			CONSOLE_LOGGER
					.info("Please specify Hadoop installation directory, typically it's the directory containing sub directories & files like (bin, sbin, lib, libexec, hadoop-common-*.jar), [/usr/lib/hadoop]");
			promptedHadoopHome = SCANNER.nextLine().trim();
			if ("".equals(promptedHadoopHome)) {
				promptedHadoopHome = "/usr/lib/hadoop";
			}
		}
		hadoopHome = validateHadoopHome(promptedHadoopHome);

		hcpl.setHadoopHome(hadoopHome);
		hcpl.setDistributionType(distributionType);
		hcpl.setHadoopType(hadoopType);

		String currentWorkingUser = System.getProperty("user.name");

		CONSOLE_LOGGER
				.info("Do you have separate users for MapReduce, Yarn and HDFS? (y)Yes/(n)No");
		String hasSeparateUsers = SCANNER.nextLine().trim();
		while (!("n".equalsIgnoreCase(hasSeparateUsers) || "y"
				.equalsIgnoreCase(hasSeparateUsers))) {
			CONSOLE_LOGGER
					.info("Do you have separate users for MapReduce, Yarn and HDFS? (y)Yes/(n)No");
			hasSeparateUsers = SCANNER.nextLine().trim();
		}
		String hdfsUser;
		String yarnUser;
		String mapredUser;
		String hdfsPasswd = null;
		String yarnPasswd = null;
		String mapredPasswd = null;
		// String isSingleUser;
		if ("n".equalsIgnoreCase(hasSeparateUsers)) {
			// isSingleUser = "y";
			CONSOLE_LOGGER.info("Please provide the username ["
					+ currentWorkingUser + "]:");
			hdfsUser = SCANNER.nextLine().trim();
			if ("".equals(hdfsUser)) {
				hdfsUser = currentWorkingUser;
				yarnUser = currentWorkingUser;
				mapredUser = currentWorkingUser;
			} else {
				mapredUser = yarnUser = hdfsUser;
			}
			hdfsPasswd = promptPassword(hdfsUser);
			mapredPasswd = yarnPasswd = hdfsPasswd;

		} else {
			CONSOLE_LOGGER.info("Please provide the hdfs user [hdfs]:");
			hdfsUser = SCANNER.nextLine().trim();
			if ("".equals(hdfsUser)) {
				hdfsUser = "hdfs";
			}
			hdfsPasswd = promptPassword(hdfsUser);

			CONSOLE_LOGGER.info("Please provide the yarn user [yarn]:");
			yarnUser = SCANNER.nextLine().trim();

			if ("".equals(yarnUser)) {
				yarnUser = "yarn";
			}
			yarnPasswd = promptPassword(yarnUser);

			CONSOLE_LOGGER.info("Please provide the mapred user [mapred]:");
			mapredUser = SCANNER.nextLine().trim();

			if ("".equals(mapredUser)) {
				mapredUser = "mapred";
			}
			mapredPasswd = promptPassword(mapredUser);
		}
		hcpl.setYarnUser(yarnUser);
		hcpl.setYarnPasswd(yarnPasswd);
		hcpl.setHdfsUser(hdfsUser);
		hcpl.setHdfsPasswd(hdfsPasswd);
		hcpl.setMapredUser(mapredUser);
		hcpl.setMapredPasswd(mapredPasswd);
		hcpl.persistPropertiesToDisk();
	}

	private static String promptPassword(String user) {
		char[] passwd;
		Console console = System.console();
		boolean verified = false;
		String encryptedPassword = null;
		int passwordRetryAttempts = 0;		
		int maxPasswdRetryAttempts = 3;
		do {
			CONSOLE_LOGGER.info("Please provide the password for " + user
					+ " user:");
			passwd = console.readPassword();
			try {
				encryptedPassword = StringUtil.getEncrypted(new String(passwd));
				verified = JschUtil.verifyPassword(user, encryptedPassword);

			} catch (JSchException e) {
				verified = false;
			}
			if (!verified) {
				passwordRetryAttempts++;				
				if (passwordRetryAttempts == maxPasswdRetryAttempts) {					
					CONSOLE_LOGGER
							.info("Max attempts of password verification has been reached hence exiting");
					exitVM(1);
				} else {
					CONSOLE_LOGGER
							.info("Password verification failed for user ["
									+ user
									+ "] , total number of attempts left ["
									+ (maxPasswdRetryAttempts - passwordRetryAttempts) + "]");
				}
			}
		} while (!verified);
		return encryptedPassword;
	}

	/***
	 * Validate Hadoop installed directory location
	 * 
	 * @param hadoopHome
	 * @return
	 */
	private static String validateHadoopHome(String hadoopHome) {
		if (hadoopHome == null || hadoopHome.isEmpty()) {
			CONSOLE_LOGGER
					.info("Hadoop installation directory is not valid !! Please specify Hadoop installation directory");
			hadoopHome = SCANNER.nextLine().trim();
			hadoopHome = validateHadoopHome(hadoopHome);
		}
		hadoopHome = hadoopHome.endsWith(File.separator) ? hadoopHome
				: hadoopHome + File.separator;
		File file = new File(hadoopHome);
		if (!file.exists()) {
			hadoopHome = validateHadoopHome(null);
		}
		return hadoopHome;
	}

	private static void turnLoggingLevelToDebug(String verboseMode) {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config
				.getLoggerConfig(ROLLING_FILE_APPENDER);
		loggerConfig.setLevel(Level.DEBUG);
		ctx.updateLoggers();
		LOGGER.info("logging level changed to [DEBUG]");
	}

	/**
	 * 
	 * This method copies specified jars from Agent's lib to hadoop's lib
	 * directory.
	 * 
	 * @param jars
	 *            the jars
	 * @param agentLibDir
	 *            the storage dir
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws ClassNotFoundException
	 */
	private static void copyAgentLibJarsToHadoopLib(List<String> jars,
			String agentLibDir) throws IOException, InterruptedException,
			ClassNotFoundException {
		String pathToHadoopLib = getHadoopLibPath(agentLibDir);
		for (String jar : jars) {
			StringBuilder copyToHadoopJar = new StringBuilder().append("cp ")
					.append(agentLibDir).append(jar).append(" ")
					.append(pathToHadoopLib);
			executeCommand(copyToHadoopJar.toString());
		}
	}

	private static String getHadoopLibPath(String agentLibDir)
			throws FileNotFoundException, ClassNotFoundException, IOException {
		HadoopConfigurationPropertyLoader hcpl = HadoopConfigurationPropertyLoader
				.getInstance();
		String hadoopHomeDir = hcpl.getHadoopHome();
		String hadoopType = hcpl.getHadoopType();
		String distributionType = hcpl.getDistributionType();
		StringBuilder destinationPath = new StringBuilder();
		destinationPath.append(hadoopHomeDir);
		switch (hadoopType) {
		case YARN:
			if (distributionType.equalsIgnoreCase(H)) {
				destinationPath = new StringBuilder(
						hadoopHomeDir.endsWith(File.separator) ? hadoopHomeDir
								.substring(0, hadoopHomeDir
										.lastIndexOf(File.separator))
								: hadoopHomeDir);
				destinationPath.append("-yarn").append(File.separator)
						.append("lib").append(File.separator);
			} else if (distributionType.equals(A)) {
				destinationPath.append("share").append(File.separator)
						.append("hadoop").append(File.separator).append("yarn")
						.append(File.separator).append("lib")
						.append(File.separator);
			} else if (distributionType.equalsIgnoreCase(C)) {
				destinationPath = new StringBuilder(
						hadoopHomeDir.endsWith(File.separator) ? hadoopHomeDir
								.substring(0, hadoopHomeDir
										.lastIndexOf(File.separator))
								: hadoopHomeDir);
				destinationPath.append("-yarn").append(File.separator)
						.append("lib").append(File.separator);
			}
			break;
		case NON_YARN:
			if (distributionType.equalsIgnoreCase(A)) {
				destinationPath.append("lib").append(File.separator);
			} else if (distributionType.equalsIgnoreCase(M)) {
				destinationPath.append("lib").append(File.separator);
			}
			break;
		default:
			new IllegalArgumentException("Hadoop type not supported");
		}
		return destinationPath.toString();
	}

	/**
	 * * This method checks whether a particular file is null or empty or a
	 * directory.
	 * 
	 * @param file
	 *            the file
	 * @return boolean
	 */
	private static boolean checkNullEmptyORNotADirectory(File file) {
		return file == null || !file.exists() || !file.isDirectory();
	}

	/**
	 * Extractlib jars.
	 * 
	 * @param destinationDir
	 *            the destination dir
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private static void extractlibJars(String destinationDir)
			throws URISyntaxException, IOException, InterruptedException {
		CodeSource codeSource = JumbuneAgent.class.getProtectionDomain()
				.getCodeSource();
		File file = new File(codeSource.getLocation().toURI().getPath());
		byte[] entryContent = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
			Enumeration<? extends JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith("lib")
						&& !entry.getName().equals("lib/")) {
					String entryName = entry.getName();
					try {
						bis = new BufferedInputStream(
								jarFile.getInputStream(entry));
						entryContent = new byte[bis.available()];
						bis.read(entryContent);
						fos = new FileOutputStream(new File(destinationDir,
								entryName.substring(RemotingConstants.FOUR)));
						fos.write(entryContent);
					} finally {
						if (bis != null) {
							bis.close();
						}
						if (fos != null) {
							fos.close();
						}
					}
				}
			}

		} finally {
			if (jarFile != null) {
				jarFile.close();
			}
		}
	}

	/**
	 * execute command using system.getRuntime method
	 * 
	 * @param command
	 *            the command
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	private static void executeCommand(String command) throws IOException,
			InterruptedException {
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		p.destroy();
	}

	private static void shutTopCmdOnSlaves(BasicJobConfig basicJobConfig) {
		String slaveTmpDir = basicJobConfig.getTmpDir();
		StringBuilder command = new StringBuilder();
		command.append(CAT_CMD).append(SPACE).append(slaveTmpDir)
				.append(File.separator).append(PID_FILE);
		try {
			List<String> params = new ArrayList<String>(1);
			params.add(slaveTmpDir);

			for (String host : basicJobConfig.getSlaves()) {
				CommandWritable commandWritable = new CommandWritable();
				CommandWritable.Command cmd = new CommandWritable.Command();
				List<Command> commands = new ArrayList<Command>(1);

				cmd.setCommandString(command.toString());
				cmd.setHasParams(true);
				cmd.setParams(params);
				commands.add(cmd);

				commandWritable.setBatchedCommands(commands);
				commandWritable.setAuthenticationRequired(true);
				commandWritable.setCommandForMaster(false);
				commandWritable.setDsaFilePath(basicJobConfig.getDsaFile());
				commandWritable.setUsername(basicJobConfig.getUser());
				commandWritable.setRsaFilePath(basicJobConfig.getRsaFile());
				commandWritable.setSlaveHost(host);
				commandWritable.setCommandType(CommandType.FS);
				CommandDelegator cmdDelegator = new CommandDelegator();
				cmdDelegator.performAction(commandWritable);
			}
			LOGGER.debug("Executed command [ShutTop] on worker nodes..");
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	/**
	 * Exit VM
	 * 
	 * @param status can be 0 or 1
	 */
	private static void exitVM(int status) {
		System.exit(status);
	}

}

