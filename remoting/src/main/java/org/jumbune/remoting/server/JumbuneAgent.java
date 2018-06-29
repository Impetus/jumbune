package org.jumbune.remoting.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jumbune.remoting.common.AgentNode;
import org.jumbune.remoting.common.AgentNodeStatus;
import org.jumbune.remoting.common.BasicJobConfig;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.command.CommandWritable;
import org.jumbune.remoting.common.command.CommandWritable.Command;
import org.jumbune.remoting.server.ha.integration.zk.AgentLeaderElector;
import org.jumbune.remoting.server.ha.sync.SyncExecutor;
import org.jumbune.remoting.server.ha.sync.SyncExecutorFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

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
	
	/** The Constant E represents Amazon EMR distribution. */
	private static final String E = "e" ;
	
	/** The Constant EA represents Amazon Apache distribution. */
	private static final String EA = "ea" ;
	
	/** The Constant EM represent Amazon Mapr distribution. */
	private static final String EM = "em" ;

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
	public static final Logger CONSOLE_LOGGER = LogManager.getLogger("EventLogger");
	
	public static final Logger LOGGER = LogManager.getLogger(JumbuneAgent.class);
	
	private static final Scanner SCANNER = new Scanner(System.in);

	/** The Constant CAT_CMD. */
	private static final String CAT_CMD = "cat";
	
	/** The Constant PID_FILE. */
	private static final String PID_FILE = "pid.txt";

	private static final String SPACE = " ";
	
	private static final String JSON_INFO = "/jsonInfo.ser";
	
	private static EventLoopGroup bossGroup;
    
    private static EventLoopGroup workerGroup;
    
    private static SyncExecutor syncExecutor;
       
    private static String agentDirPath ;

	private static final String HADOOP_VERSION_YARN_COMMAND = "bin/hadoop version";
    
    private static boolean startedInHA;
    
    private static String[] zkHosts;
    
	private static Properties haProps;
	
	private static int heartBeatMillis;
    /**
	 * Instantiates a new jumbune agent.
	 */

    private static List<String> agentLibEntries;
    
    
   private static AgentLeaderElector leaderElector = null;
	
    private JumbuneAgent() {
	}

	static {
		jars.add("/lib/log4j-api-2.1.jar");
		jars.add("/lib/log4j-core-2.1.jar");
	}
	
	public static boolean isStartedInHA() {
		return startedInHA;
	}

	public static Properties getHAProps() {
		return haProps;
	}
		
	public static int getHeartBeatMillis() {
		return heartBeatMillis;
	}
	
	public static List<String> getAgentLibEntries() {
		return agentLibEntries;
	}

	/**
	 * *
	 * This method is used to up and run the  agent on namenode.
	 *
	 * @param jumbuneAgentArgs which is port on which user wants to up jumbune agent on hadoop namenode
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 * @throws URISyntaxException the uRI syntax exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static void main(final String[] args) throws IOException, InterruptedException, URISyntaxException, ClassNotFoundException {
		registerShutdownHook();
		CommandLine cmd = parseCommandLineArguments(args);
		startedInHA = cmd.hasOption("zkHost:Port");
		int port = 0;
		try {
			port = Integer.parseInt(cmd.getOptionValue("agent-port"));
		} catch (NumberFormatException e) {
			CONSOLE_LOGGER.info("Please specify a valid Agent Port");
			System.exit(0);
		}		

		String agentHome = cmd.getOptionValue("agent-dir");
        File agentDir = new File(agentHome).getCanonicalFile();
        agentHome = agentDir.getAbsolutePath();     
        if(agentHome.equals(System.getProperty("user.home"))) {
			CONSOLE_LOGGER.info("<agent-dir> should not be same as $HOME. It can be any other location. A new location(directory) is always recommended.");
			System.exit(0);        	
        }					

		final String agentHomeDir = agentHome.endsWith(File.separator) ? agentHome : agentHome + File.separator;		
		setAgentDirPath(agentHomeDir);
		// if path assigned as agent home doesn't exists then creating it on
		// file system.
		File file = new File(agentHomeDir);
		if (checkNullEmptyORNotADirectory(file)) {
			file.mkdirs();
			file.setReadable(true, false);
			file.setWritable(true, false);
		}
		validateHadoopConfiguration(cmd);
	
		// Creating lib directory and extract all contents in jar's lib into $AGENT_HOME/lib directory
		String libLocation = agentHomeDir + "/lib/";
		file = new File(libLocation);
		if(file.exists()){
			String[] files = file.list();
			for(String fileJars: files){
			    File jarFiles = new File(file.getPath(),fileJars);
			    jarFiles.delete();
			}
			file.delete();
		}
		if (checkNullEmptyORNotADirectory(file)) {
			file.mkdir();
			file.setReadable(true, false);
			file.setWritable(true, false);
			extractlibJars(libLocation);
			updateFilePermissionRecursively(file.getAbsolutePath());
		}
		
		String confLocation = agentHomeDir + RemotingConstants.CONF_DIR;
		File confDir = new File(confLocation);
		if(!confDir.exists()) {
			confDir.mkdir();
			confDir.setReadable(true, false);
			confDir.setWritable(true, false);
			extractConf(confLocation, RemotingConstants.HA_CONF_PROPERTIES);
			updateFilePermissionRecursively(confDir.getAbsolutePath());
		}

		//in case HA only
		if(startedInHA){
			haProps = new Properties();
			haProps.load(Files.newInputStream(Paths.get(confLocation + File.separator + RemotingConstants.HA_CONF_PROPERTIES), StandardOpenOption.READ));
			heartBeatMillis = Integer.parseInt(haProps.getProperty(RemotingConstants.HEART_BEAT_MILLIS));
			String zkConnectString = null;
			AgentNode agentNode = createAgentNodeData();
			agentNode.setAgentHomeDir(agentHomeDir);

			// Getting IPAddress of localhost & host name for this IP address
			InetAddress addr = InetAddress.getLocalHost();
			String agentHost = addr.getHostName();
			agentNode.setHost(agentHost);
			agentNode.setPort(port);
			
			zkConnectString = cmd.getOptionValue("zkHost:Port");
			zkHosts = parseZKConnectString(zkConnectString);
			
			//assuming zkConnectString to be a comma separated list of zookeeper hosts.  	
	        // ex. zk_host1,zk_host2,zk_host3
			
		  leaderElector = new AgentLeaderElector(agentNode, zkConnectString);
			try {
				if ((zkConnectString != null) && !zkConnectString.isEmpty()) {
					leaderElector.participateInElection();
				}
			} catch (Exception e) {
				CONSOLE_LOGGER.error("Error connecting to zookeeper", e);
				System.exit(1);
			}
			
			//creating object for SyncExecutor
	        syncExecutor = new SyncExecutorFactory(zkConnectString).createSyncExecutor(RemotingConstants.RSYNC);
		}

		copyAgentLibJarsToHadoopLib(jars, agentHomeDir);
		populateLibEntries();
		
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
                 public void initChannel(SocketChannel ch) throws Exception {
                	 ch.pipeline().addLast("JaDecoder", new JumbuneAgentDecoder(agentHomeDir, syncExecutor, startedInHA));
                 }
             });
        	CONSOLE_LOGGER.info("Jumbune Agent started successfully on port [" + port + "]");
             // Bind and start to accept incoming connections.
             bootstrap.bind(port).sync().channel().closeFuture().sync();
        }finally{
	         bossGroup.shutdownGracefully();
	         workerGroup.shutdownGracefully();
	      
	    }
	}
	
	// Build command line options
	private static Options buildOptions() {
		Options options = new Options();
		
		options.addOption(Option.builder()
				.longOpt("agent-dir")
				.desc("Agent Work Directory Path")
				.argName("agent-dir")
				.hasArg().required().build());
		
		options.addOption(Option.builder()
				.longOpt("agent-port")
				.desc("Port Number on which agent to be run")
				.argName("agent-port")
				.hasArg().required().numberOfArgs(1).build());
		
		options.addOption(Option.builder()
				.longOpt("hadoop-dir")
				.desc("Hadoop installation directory, typically it's the directory "
						+ "containing sub directories & files like (bin, sbin, lib,"
						+ " libexec, hadoop-common-*.jar), [/usr/lib/hadoop]")
				.argName("hadoop-dir")
				.hasArg().numberOfArgs(1).build());
		
		options.addOption(Option.builder()
				.longOpt("distribution")
				.desc("Hadoop Distribution Type\n"
						+ "e.g.\n'--distribution a' for Apache,\n"
						+ "'--distribution c' for Cloudera,\n"
						+ "'--distribution e' for EMR,\n"
						+ "'--distribution h' for HortonWorks,\n"
						+ "'--distribution m' for MapR")
				.argName("distribution")
				.hasArg().numberOfArgs(1).build());
		
		options.addOption(Option.builder()
				.longOpt("high-availability")
				.desc("Zookeeper connection information. Set this option if you are starting Jumbune Agent in High Availability mode. "
						+ "In argument Provide the zookeeper daemon hostname and port separated by colon (:)\n"
						+ "e.g.:\n"
						+ "--high-availability 127.0.0.1:2181\n"
						+ "In case of multiple host, hosts should be separated by comma ','\n"
						+ "e.g.:"
						+ "--high-availability ZKHost1:Port1,ZKHost2:Port2 ")
				.argName("zkHost:Port")
				.hasArg().numberOfArgs(1).build());
		
		options.addOption(Option.builder()
				.longOpt("verbose")
				.desc("Increases the verbosity of the Jumbune Agent Logs")
				.argName("verbose")
				.build());
		return options;
	}
	
	private static CommandLine parseCommandLineArguments(String[] args) throws IOException {
		// Creating options
		Options options = buildOptions();
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (MissingOptionException e) {
			CONSOLE_LOGGER.error("Missing Options(s)");
			displayOptions(options);
			exitVM(1);
		} catch (MissingArgumentException e) {
			CONSOLE_LOGGER.error("Missing Argument(s)");
			displayOptions(options);
			exitVM(1);
		} catch (ParseException e) {
			CONSOLE_LOGGER.error("Invalid option(s)");
			displayOptions(options);
			exitVM(1);
		}
		
		// Checking if any invalid option
		if ((args.length > 0 && cmd.getOptions().length == 0)) {
			CONSOLE_LOGGER.error("Invalid option(s)");
			displayOptions(options);
		}
		
		// Displaying help
		if (cmd.hasOption("help")) {
			displayOptions(options);
		}
		
		if (cmd.hasOption("verbose")) {
			turnLoggingLevelToDebug(VERBOSE);
		}
		
		return cmd;
		
	}
	
	private static void displayOptions(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator(null);
		formatter.setWidth(80);
		formatter.printHelp("java -jar <jumbune jar file>.jar", options, true);
		exitVM(0);
	}

	private static void exitVM(int i) {
		System.exit(i);
		
	}
	
	public static void shutdown() {
        CONSOLE_LOGGER.warn("initiating shutdown.....");
		leaderElector.stop();
		
        if(!bossGroup.isShutdown()) {
        	bossGroup.shutdownGracefully();
        }
        if(!workerGroup.isShutdown()){
        	workerGroup.shutdownGracefully();
        }
        CONSOLE_LOGGER.warn("shutdown successful.....exiting..");
        System.exit(0);
	}
	
	/**
	 * Populate lib entries. Stores all the jars present in agent lib in memory which can later be used 
	 * for classpath entires for various command execution. <code>getAgentLibEntries()</code> method should be called 
	 * to access those entries.
	 */
	private static void populateLibEntries() {
		agentLibEntries = new ArrayList<>();
		try (DirectoryStream<Path> paths = Files
				.newDirectoryStream(Paths.get(agentDirPath + RemotingConstants.LIB_DIR))) {
			Iterator<Path> itr = paths.iterator();
			while (itr.hasNext()) {
                agentLibEntries.add(itr.next().toString());
			}
		} catch (IOException e) {
			CONSOLE_LOGGER.error("unable to load agent library entries from location - " + (agentDirPath + RemotingConstants.LIB_DIR));
		}
	}
	
	
	public static String[] getZKHosts(){
		return zkHosts;
	}
	
	private static String[] parseZKConnectString(String zkConnectString){
		return zkConnectString.trim().split(",");
	}

	private static void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Invoking shutdown hook...");
				try {
				// killing top command
				String agentHome = getAgentDirPath();
					ObjectInputStream objectinputstream = null;
					InputStream streamIn = null;
					try {
						File file = new File(agentHome + JSON_INFO);
						if (file.exists()) {
							streamIn = new FileInputStream(agentHome + JSON_INFO);
							objectinputstream = new ObjectInputStream(streamIn);
							BasicJobConfig basicJobConfig = (BasicJobConfig) objectinputstream.readObject();
							// shutTopCmdOnSlaves(basicJobConfig);
						}
					} finally {
						if (objectinputstream != null) {
							objectinputstream.close();
						}
						if (streamIn != null) {
							streamIn.close();
						}
					}
					if(bossGroup != null){
						bossGroup.shutdownGracefully();						
					}
					if(workerGroup != null){
						workerGroup.shutdownGracefully();
					}				
					if (startedInHA) {
						synchronized (leaderElector) {
							if (!leaderElector.getState()
									.equals(org.apache.curator.framework.recipes.leader.LeaderLatch.State.CLOSED)) {
								System.out.println(
										"trying to close leader latch, current state - " + leaderElector.getState());
								leaderElector.stop();
							}
						}
						syncExecutor.shutdown();
					}
					LOGGER.debug("Released external resources !");
				} catch (IOException e) {
					LOGGER.error(e);
				} catch (ClassNotFoundException e) {
					LOGGER.error(e);
				}  catch (Exception e) {
					LOGGER.error(e);
				} 
			}
		});
	}	
	
	/**
	 * Validate agent configuration.
	 *
	 * @return the agent node
	 */
    private static AgentNode createAgentNodeData() {

        AgentNode agentNode = new AgentNode();
        CONSOLE_LOGGER.info("Please enter Jumbune Agent User");
        String agentUser = SCANNER.nextLine().trim();
        while ("".equals(agentUser)) {
        	agentUser = SCANNER.nextLine().trim();
        }

        agentNode.setAgentUser(agentUser);

        String privateKey = "/home/" + agentUser + "/.ssh/id_rsa";
        String promptedPrivateKey;
        CONSOLE_LOGGER.info("Please verify Agent User private key [" + privateKey + "]");
        promptedPrivateKey = SCANNER.nextLine().trim();
        if ("".equals(promptedPrivateKey)) {
            promptedPrivateKey = privateKey;
        }

        privateKey = validateAgentPrivateKey(promptedPrivateKey);
        agentNode.setPrivateKey(privateKey);

        agentNode.setStatus(AgentNodeStatus.FOLLOWER);

        return agentNode;
    }

	/**
	 * Validate agent private key.
	 *
	 * @param privateKey the private key
	 * @return the string
	 */
	private static String validateAgentPrivateKey(String privateKey) {
		if (privateKey == null || privateKey.isEmpty()) {
			CONSOLE_LOGGER
					.info("Agent's private key is not valid !! Please specify valid private key file");
			privateKey = SCANNER.nextLine().trim();
			privateKey = validateAgentPrivateKey(privateKey);
		}
		File file = new File(privateKey);
		if (!file.exists()) {
			privateKey = validateAgentPrivateKey(null);
		}
		return privateKey;
	}

	/***
	 * Validates and loades hadoop cluster configuration 
	 * @param cmd 
	 * @param jumbuneAgentArgs
	 * @param agentHome
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static void validateHadoopConfiguration(CommandLine cmd) throws IOException, ClassNotFoundException {
		
		String hadoopHome = null;
		String hadoopType = null;
		String distributionType = null;
		
		HadoopConfigurationPropertyLoader hcpl = HadoopConfigurationPropertyLoader.getInstance();
		if(!hcpl.isPropertyLoaded()){			
			getHadoopConfigurationFromUser(cmd, hcpl);
		}else{
			hadoopHome = hcpl.getHadoopHome();
			hadoopType = hcpl.getHadoopType();
			distributionType = hcpl.getDistributionType();
			CONSOLE_LOGGER.info("HADOOP HOME: ["+hadoopHome+"]");
			CONSOLE_LOGGER.info("HADOOP TYPE: ["+hadoopType+"]");
			switch(distributionType){
			case C:
				distributionType = "CDH";
				break;
			case H:
				distributionType = "HDP";
				break;
			case M:
				distributionType = "MapR";
				break;
			case EA:
				distributionType = "EmrApache";
				break;
			case EM:
				distributionType = "EmrMapr";
				break;				
			default:
				distributionType = "Apache";				
			}
			CONSOLE_LOGGER.info("DISTRIBUTION : ["+distributionType+"]");
		}
	}

	private static void getHadoopConfigurationFromUser(CommandLine cmd, HadoopConfigurationPropertyLoader hcpl)
			throws IOException {
		String hadoopHome;
		String hadoopType;
		String distributionType;
		Properties prop = new Properties();
		prop.load(JumbuneAgent.class.getClassLoader().getResourceAsStream(DISTRIBUTION_PROPERTIES));
		hadoopType = prop.getProperty(HADOOP_DISTRIBUTION);
		
		boolean isYarn = hadoopType.equalsIgnoreCase(YARN);
		
		distributionType = getHadoopDistributionType(isYarn, cmd);
		
		hadoopHome = getHadoopHome(cmd);	
		
		if (distributionType.equals(E)){
			try {
				distributionType = getDeploymentTypeForEMR(hadoopHome);
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
		}
		
		hcpl.setHadoopHome(hadoopHome);
		hcpl.setDistributionType(distributionType);
		hcpl.setHadoopType(hadoopType);		
		hcpl.persistPropertiesToDisk();
	}
	
	private static String getHadoopHome(CommandLine cmd) {
		String hadoopHome = cmd.getOptionValue("hadoop-dir");

		if (hadoopHome == null) {
			hadoopHome = System.getenv("HADOOP_HOME");
			String promptedHadoopHome;
			if (hadoopHome != null && !hadoopHome.trim().isEmpty()) {	
				CONSOLE_LOGGER.info("Please verify Hadoop installation directory ["+hadoopHome+"]");
				promptedHadoopHome = SCANNER.nextLine().trim();
				if(promptedHadoopHome.isEmpty()){
					promptedHadoopHome = hadoopHome;
				}			
			} else {
				CONSOLE_LOGGER.info("Please specify Hadoop installation directory, typically it's the directory containing sub directories & files like (bin, sbin, lib, libexec, hadoop-common-*.jar), [/usr/lib/hadoop]");
				promptedHadoopHome= SCANNER.nextLine().trim();
				if (promptedHadoopHome.isEmpty()) {
					promptedHadoopHome = "/usr/lib/hadoop";
				}			
			}

			hadoopHome = validateHadoopHome(promptedHadoopHome);
		} else {
			hadoopHome = hadoopHome.endsWith(File.separator) ? hadoopHome : hadoopHome + File.separator;
			File file = new File(hadoopHome);
			if(!file.exists()){
				CONSOLE_LOGGER.info("Provided Hadoop installation directory is not valid !!");
				exitVM(1);
			}
		}
		return hadoopHome;

	}
	
	private static String getHadoopDistributionType(boolean isYarn, CommandLine cmd) {
		String hadoopDistributionType = cmd.getOptionValue("distribution");
		String supportedDistributions;
		
		if ( isYarn) {
			supportedDistributions = "(a)Apache | (c)Cloudera | (e)EMR | (h)HortonWorks | (m)MapR";
		} else {
			supportedDistributions = "(a)Apache | (m)MapR";
		}
		
		if (hadoopDistributionType != null) {
			if (isCorrectDistribution(isYarn, hadoopDistributionType)) {
				return hadoopDistributionType;
			} else {
				CONSOLE_LOGGER.error("Invalid: Hadoop distribution [" + hadoopDistributionType
						+ "] passed in command line arguments. Available are : " + supportedDistributions);
				exitVM(1);
			}
			
		} else {
			CONSOLE_LOGGER.info("Choose the Hadoop Distribution Type : " + supportedDistributions);
			hadoopDistributionType = SCANNER.nextLine().trim();
			while (hadoopDistributionType.isEmpty() || !isCorrectDistribution(isYarn, hadoopDistributionType)) {
				CONSOLE_LOGGER.info(
						"Invalid input! Choose from the given Hadoop Distribution Type : " + supportedDistributions);
				hadoopDistributionType = SCANNER.nextLine().trim();
			}
		}
		return hadoopDistributionType.toLowerCase().substring(0, 1);
	}
	
	private static boolean isCorrectDistribution(boolean isYarn, String hadoopDistributionType) {
		if (isYarn) {
			switch (hadoopDistributionType.toLowerCase().charAt(0)) {
			case 'a' :
			case 'h' :
			case 'c' :
			case 'm' :
			case 'e' :
				return true;
			default :
				return false;
			}
		} else {
			switch (hadoopDistributionType.toLowerCase().charAt(0)) {
			case 'a' :
			case 'm' :
				return true;
			default :
				return false;
			}
		}
	}
	
	/***
	 * Validate Hadoop installed directory location 
	 * @param hadoopHome
	 * @return
	 */
	private static String validateHadoopHome(String hadoopHome) {
		if(hadoopHome == null || hadoopHome.isEmpty()){
			CONSOLE_LOGGER.info("Hadoop installation directory is not valid !! Please specify Hadoop installation directory");
			hadoopHome= SCANNER.nextLine().trim();
			hadoopHome = validateHadoopHome(hadoopHome);
		}
		hadoopHome = hadoopHome.endsWith(File.separator) ? hadoopHome : hadoopHome + File.separator;
		File file = new File(hadoopHome);
		if(!file.exists()){
			hadoopHome = validateHadoopHome(null);
		}
		return hadoopHome;
	}

	private static void turnLoggingLevelToDebug(String verboseMode) {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(ROLLING_FILE_APPENDER);
		loggerConfig.setLevel(Level.DEBUG);
		ctx.updateLoggers();
		LOGGER.debug("logging level changed to [DEBUG]");
	}
	
	/**
	 *
	 * This method copies specified jars from Agent's lib to hadoop's lib directory.
	 *
	 * @param jars the jars
	 * @param agentLibDir the storage dir
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 * @throws ClassNotFoundException 
	 */
	private static void copyAgentLibJarsToHadoopLib(List<String> jars, String agentLibDir) throws IOException, InterruptedException, ClassNotFoundException {
		String pathToHadoopLib  = getHadoopLibPath(agentLibDir);
		for (String jar : jars) {
				StringBuilder copyToHadoopJar = new StringBuilder().append("cp ")
						.append(agentLibDir).append(jar).append(" ").append(pathToHadoopLib);
			executeCommand(copyToHadoopJar.toString());
		}		
	}
	
	private static String getHadoopLibPath(String agentLibDir) throws FileNotFoundException, ClassNotFoundException, IOException {
		HadoopConfigurationPropertyLoader hcpl = HadoopConfigurationPropertyLoader.getInstance();
		String hadoopHomeDir = hcpl.getHadoopHome();
		String hadoopType = hcpl.getHadoopType();
		String distributionType = hcpl.getDistributionType();
		StringBuilder destinationPath = new StringBuilder();
		destinationPath.append(hadoopHomeDir);
		switch(hadoopType){
			case YARN :
				if(distributionType.equalsIgnoreCase(H)){
					destinationPath = new StringBuilder(hadoopHomeDir.endsWith(File.separator) ? hadoopHomeDir.substring(0,
							hadoopHomeDir.lastIndexOf(File.separator)) : hadoopHomeDir);
					destinationPath.append("-yarn").append(File.separator).append("lib").append(File.separator);
				}else if(distributionType.equals(A)){
					destinationPath.append("share").append(File.separator).append("hadoop").append(File.separator).append("yarn")
					.append(File.separator).append("lib").append(File.separator);
				}else if(distributionType.equalsIgnoreCase(C)){
					destinationPath = new StringBuilder(hadoopHomeDir.endsWith(File.separator) ? hadoopHomeDir.substring(0,
							hadoopHomeDir.lastIndexOf(File.separator)) : hadoopHomeDir);
					destinationPath.append("-yarn").append(File.separator).append("lib").append(File.separator);
					//mapr code changes.
				}else if(distributionType.equalsIgnoreCase(M)){
					destinationPath.append("share").append(File.separator).append("hadoop").append(File.separator).append("yarn")
					.append(File.separator).append("lib").append(File.separator);
				}else if(distributionType.equalsIgnoreCase(EA)){
					destinationPath = new StringBuilder(hadoopHomeDir.endsWith(File.separator) ? hadoopHomeDir.substring(0,
							hadoopHomeDir.lastIndexOf(File.separator)) : hadoopHomeDir);
					destinationPath.append("-yarn").append(File.separator).append("lib").append(File.separator);
				}
				else if(distributionType.equalsIgnoreCase(EM)){
					destinationPath.append("share").append(File.separator).append("hadoop").append(File.separator).append("yarn")
					.append(File.separator).append("lib").append(File.separator);
				}
				break;
			case NON_YARN :
				if(distributionType.equalsIgnoreCase(A)){
					destinationPath.append("lib").append(File.separator);
				}else if(distributionType.equalsIgnoreCase(M)){
					destinationPath.append("lib").append(File.separator);
				}
				break;
			default : 
				new IllegalArgumentException("Hadoop type not supported");
		}
		return destinationPath.toString();
	}

	/**
	 * *
	 * This method checks whether a particular file is null or empty or a directory.
	 *
	 * @param file the file
	 * @return boolean
	 */
	private static boolean checkNullEmptyORNotADirectory(File file) {
		return file == null || !file.exists() || !file.isDirectory();
	}

	/**
	 * Extractlib jars.
	 *
	 * @param destinationDir the destination dir
	 * @throws URISyntaxException the uRI syntax exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private static void extractlibJars(String destinationDir) throws URISyntaxException, IOException, InterruptedException {
		CodeSource codeSource = JumbuneAgent.class.getProtectionDomain().getCodeSource();
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
				if (entry.getName().startsWith("lib") && !entry.getName().equals("lib/")) {
					String entryName = entry.getName();
					try {
						bis = new BufferedInputStream(jarFile.getInputStream(entry));
						entryContent = new byte[bis.available()];
						bis.read(entryContent);
						fos = new FileOutputStream(new File(destinationDir, entryName.substring(RemotingConstants.FOUR)));
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
			if(jarFile!=null){
				jarFile.close();
			}
		}
	}

	private static void updateFilePermissionRecursively(String path) {
		File file = new File(path);
		file.setReadable(true, false);
		file.setWritable(true, false);
		for(File f : file.listFiles()){
			f.setExecutable(true, false);
			f.setWritable(true, false);
          if(f.isDirectory()) {
        	  updateFilePermissionRecursively(f.getAbsolutePath());
          }
		}
	}
	
	/**
	 * Extractlib jars.
	 *
	 * @param destinationDir the destination dir
	 * @param fileName the file Name to be extracted from JAR
	 * @throws URISyntaxException the uRI syntax exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private static void extractConf(String destinationDir, String fileName) throws URISyntaxException, IOException, InterruptedException {
		CodeSource codeSource = JumbuneAgent.class.getProtectionDomain().getCodeSource();
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
				String entryName = entry.getName();
				if (entryName.equals(fileName)) {
					try {
						bis = new BufferedInputStream(jarFile.getInputStream(entry));
						entryContent = new byte[bis.available()];
						bis.read(entryContent);
						fos = new FileOutputStream(new File(destinationDir, entryName));
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
			if(jarFile!=null){
				jarFile.close();
			}
		}
	}
	
	/**
	 * execute command using system.getRuntime method
	 *
	 * @param command the command
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private static void executeCommand(String command) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		p.destroy();
	}

	private static void shutTopCmdOnSlaves(BasicJobConfig basicJobConfig) {
		String slaveTmpDir = basicJobConfig.getTmpDir();
		StringBuilder command = new StringBuilder();
		command.append(CAT_CMD).append(SPACE).append(slaveTmpDir).append(File.separator).append(PID_FILE);
		try{
		List<String> params = new ArrayList<String>(1);
		params.add(slaveTmpDir);
		
		for (String host : basicJobConfig.getWorkers()) {
			CommandWritable commandWritable = new CommandWritable();	
			CommandWritable.Command cmd = new CommandWritable.Command();
			List<Command> commands= new ArrayList<Command>(1);
			
			cmd.setCommandString(command.toString());
			cmd.setHasParams(true);
			cmd.setParams(params);
			cmd.setCommandType(CommandType.FS);
			commands.add(cmd);

			commandWritable.setBatchedCommands(commands);
			commandWritable.setAuthenticationRequired(true);
			commandWritable.setCommandForMaster(false);
			commandWritable.setSshAuthKeysFile(basicJobConfig.getSshAuthKeysFile());
			commandWritable.setUsername(basicJobConfig.getUser());
			commandWritable.setWorkerHost(host);
			CommandDelegator cmdDelegator = new CommandDelegator();
			cmdDelegator.performAction(commandWritable);
		}
		LOGGER.debug("Executed command [ShutTop] on worker nodes..");
		}catch (IOException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Gets the agent dir path.
	 *
	 * @return the agent dir path
	 */
	public static String getAgentDirPath() {
		return agentDirPath.endsWith(File.separator)? agentDirPath: agentDirPath+File.separator;
	}

	/**
	 * Sets the agent dir path.
	 *
	 * @param agentDirPath the new agent dir path
	 */
	public static void setAgentDirPath(String agentDirPath) {
		JumbuneAgent.agentDirPath = agentDirPath ;
	}
	
	/**
	 * Gets the deployment type for emr (either apache or mapr).
	 *
	 * @param hadoopHome the hadoop home
	 * @return the deployment type for emr
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private static String getDeploymentTypeForEMR(String hadoopHome)
			throws IOException, InterruptedException {
		String osVersion = null;
		String hadoopVersionForAmzn = null;
		String hadoopVersionForAmznResponse = null;
		String[] individualHVResponses = null;
		boolean isMapRForAmazon = false;
		Process executeCmdForOsVersion = null;
		Process executeCmdForhadoopVersion = null;

		executeCmdForOsVersion = Runtime.getRuntime().exec("lsb_release -si");
		osVersion = readCommandOutput(executeCmdForOsVersion.getInputStream());

		if (osVersion.contains("AMI")) {
			String commandString = hadoopHome + File.separator + HADOOP_VERSION_YARN_COMMAND;
			executeCmdForhadoopVersion = Runtime.getRuntime().exec(commandString);
			hadoopVersionForAmznResponse = readCommandOutput(executeCmdForhadoopVersion.getInputStream());
			individualHVResponses = hadoopVersionForAmznResponse.split("\n");
			for (String indiviResp : individualHVResponses) {
				if (indiviResp.trim().contains("mapr")) {
					hadoopVersionForAmzn = EM;
					isMapRForAmazon = true;
					break;
				}
			}
			if (!isMapRForAmazon) {
				hadoopVersionForAmzn = EA;
			}
		}
		return hadoopVersionForAmzn;

	}

		
	
	
	
	/**
	 * This method reads the command output of the commands run through Runtime exec.
	 *
	 * @param inputStream the input stream
	 * @return the string
	 */
	public static String readCommandOutput(InputStream inputStream) {
		if (inputStream == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = bufferedReader.readLine();
			while (line != null) {
				sb.append(line);
				line = bufferedReader.readLine();
			}
			return sb.toString();
		} catch (IOException e) {
			LOGGER.error(e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}
		return sb.toString();
	}

}
