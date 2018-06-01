package org.jumbune.deploy;

import static org.jumbune.common.influxdb.beans.InfluxDBConstants.AND_P_EQUAL_TO;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.AND_Q_EQUAL_TO;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.COLON;
import static org.jumbune.common.influxdb.beans.InfluxDBConstants.HTTP;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.utils.Versioning;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Class for deploying Jumbune at specific location
 */
public final class DeployUtil {
	
	private final String CLEAN_UNUSED_FILES_AND_DIRS;
	private final String DISTRIBUTION = "distribution";
	private final String HADOOP_VERSION_YARN_COMMAND = "bin/hadoop version";
	private final String JAVA_ENV_VAR = "JAVA_HOME";
	private final String JUMBUNE_ENV_VAR = "JUMBUNE_HOME";
	private final String JUMBUNE_HOME = "<JUMBUNE.HOME>";
	private final String META_INF_NOTICE_TXT = "META-INF/NOTICE.txt";
	private final String NAMENODE_IP = "namenodeIP";
	private final String PASSWORD = "password";
	
	private final String RESOURCES = "resources";
	private final String UPDATE_AGENT_JAR;
	private final String UPDATE_JAR = "jar -uvf ";
	private final String UPDATE_WAR_CLASSES_FILE;
	private final String UPDATE_WAR_FILE;
	private final String USER_DIR = "user.dir";
	private final String USER_NAME = "username";
	private final String WEB_FOLDER_STRUCTURE = "/WEB-INF/lib/";
	private final String[] FOLDERS = { "tmp/" };
	private final String[] agentJars, subDirs, executableFiles;
	private int MAX_RETRY_ATTEMPTS = 3;
	
	private Map<String, Object> influxDBConfMap;
	private Map<String, String> FoundPaths;
	private Scanner SCANNER;
	private String namenodeIP = null;
	private String username = null;
	private String sampleClusterJsonFormat;
	private String sampleClusterName;
	private String sampleClusterJson;
	
	private static final String PRIVATE_KEY_PATH;
	private static final Logger CONSOLE_LOGGER;
	private static final Logger DEBUG_FILE_LOGGER;
	
	static {
		PRIVATE_KEY_PATH = "privatekeypath";
		CONSOLE_LOGGER = LogManager.getLogger("EventLogger");
		DEBUG_FILE_LOGGER = LogManager.getLogger(DeployUtil.class);
	}

	private DeployUtil() {
		agentJars = new String[] { "jumbune-datavalidation", "log4j-core-", "log4j-api-", "jumbune-common-",
				"jumbune-utils-", "gson-", "commons-logging-", "commons-configuration-", "commons-lang-",
				"jackson-mapper-asl-", "jackson-core-asl-", "jumbune-rumen-",
				"remoting-common-", "remoting-jsch-", "curator-client-", "curator-framework-", "curator-recipes-",
				"zookeeper-", "jsch-", "guava-", "slf4j-api-", "slf4j-log4j12-", "slf4j-simple-" };
		
		subDirs = new String[] {"lib", "bin", "modules", "resources", "agent-distribution", "examples" };
		
		
		executableFiles = new String[] {"/bin/startWeb", "/bin/stopWeb", "/bin/runCli" };
		
		UPDATE_WAR_CLASSES_FILE = "/modules/jumbune-web-"
				+ Versioning.COMMUNITY_BUILD_VERSION + Versioning.COMMUNITY_DISTRIBUTION_NAME
				+ ".war WEB-INF/classes";
		
		CLEAN_UNUSED_FILES_AND_DIRS = "rm -rf WEB-INF/ skins META-INF/ jsp/ lib/";
		
		UPDATE_WAR_FILE = "/modules/jumbune-web-"
				
				+ Versioning.COMMUNITY_BUILD_VERSION + Versioning.COMMUNITY_DISTRIBUTION_NAME
				+ ".war WEB-INF/lib";
		
		UPDATE_AGENT_JAR = "/agent-distribution/jumbune-remoting-"
				+ Versioning.COMMUNITY_BUILD_VERSION + Versioning.COMMUNITY_DISTRIBUTION_NAME
				+ "-agent.jar lib/";
		
		influxDBConfMap = new HashMap<String, Object>();
		FoundPaths = new HashMap<String, String>(3);
		
		sampleClusterJsonFormat = "{\"zks\":[{\"host\":\"\",\"port\":\"2181\"}],\"clusterName\":\"%"
				+ "s\",\"nameNodes\":{\"nameNodeJmxPort\": \"5677\",\"hosts\":[\"%s\"],\"hasPasswor"
				+ "dlessAccess\":%s,\"haEnabled\":false},\"hadoopUsers\":{\"hdfsUser\":\"hdfs\",\"y"
				+ "arnUser\":\"yarn\",\"mapredUser\":\"mapred\",\"fsUser\":\"%s\",\"hasSingleUser\""
				+ ":true,\"fsPrivateKeyPath\": %s, \"fsUserPassword\" : %s},\"agents\":{\"user\":\""
				+ "%s\",\"agents\":[{\"host\":\"%s\",\"port\":\"2161\"}],\"hasPasswordlessAccess\":"
				+ " \"%s\",\"sshAuthKeysFile\":%s, \"password\" : %s, \"haEnabled\":false},\"enable"
				+ "HostRange\":\"FALSE\",\"taskManagers\":{\"taskManagerJmxPort\": \"5680\",\"hosts"
				+ "\":[\"%s\"],\"hasPasswordlessAccess\":%s},\"workers\":{\"dataNodeJmxPort\": \"56"
				+ "79\",\"taskExecutorJmxPort\": \"5678\",\"hosts\":[],\"workDirectory\":\"/home/%s"
				+ "/temp/\",\"user\":\"%s\"},\"jmxPluginEnabled\":false}";
		
	}

	/**
	 * Exit VM ubuntu 15.10
	 * 
	 * @param status
	 *            can be 0 or 1
	 */
	private static void exitVM(int status) {
		System.exit(status);
	}

	/**
	 * Main method for extracting the content of deployment jar file
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		
		Map<String, String> argMap = parseCommandLineArguments(args);
		
		if (argMap.get(PRIVATE_KEY_PATH) != null) {
			File privateKeyFile = new File(argMap.get(PRIVATE_KEY_PATH));
			if (!privateKeyFile.exists() || privateKeyFile.isDirectory()) {
				CONSOLE_LOGGER.error("Invalid private key file path [" + privateKeyFile + "]");
				exitVM(1);
			}
		}

		DeployUtil util = new DeployUtil();
		util.deploy(argMap);
	}
	
	public void deploy(Map<String, String> argMap) throws IOException, InterruptedException {
		Properties prop = new Properties();
		prop.load(DeployUtil.class.getClassLoader().getResourceAsStream("distribution.properties"));
		String distributionType = prop.getProperty("hadoop-distribution");
		boolean isYarn = !distributionType.equalsIgnoreCase("Non-Yarn") ? true : false;
		if (!isYarn) {
			CONSOLE_LOGGER.info("Sorry, Jumbune doesn't support Non-Yarn based Hadoop distributions");
			return;
		}
		Session session = null;
		SCANNER = new Scanner(System.in);
		try {
			CONSOLE_LOGGER.info(
					"--Jumbune built for [" + distributionType + " based Hadoop] distributions--");
			URLConnection jarConnection = performSanity();

			String hadoopDistributionType = getHadoopDistributionType(isYarn, argMap);
			
			setSampleClusterName(hadoopDistributionType.charAt(0));
			
			session = getSession(argMap);

			CONSOLE_LOGGER.info("Extracting Jumbune...");
			
			String hadoopHome = getHadoopLocation(session, hadoopDistributionType);			
			FoundPaths.put("<HADOOP.HOME>", hadoopHome);
			
			if (hadoopDistributionType.equalsIgnoreCase("e")) {
				hadoopDistributionType = getDeploymentTypeForEMR(session,hadoopHome);
			}
				
			extractJarDirectories(new File(FoundPaths.get(JUMBUNE_HOME)), jarConnection);

			checkJumbuneDirectoryCreation();

			createConfigurationFile(distributionType, hadoopDistributionType);

			changeRunnablePermissions();

			serializeDistributionType(distributionType, hadoopDistributionType);

			CONSOLE_LOGGER.info(
					"Jumbune Extraction, Directories creation, Configuration update.......[SUCCESS]");

			updateJumbuneAndHadoopDistribution(FoundPaths.get("<JAVA.HOME>"), session,
					FoundPaths.get(JUMBUNE_HOME), hadoopDistributionType, distributionType);

			createInfluxDBRelatedFiles(argMap);

			moveFromResourceToConf(RemotingConstants.HA_CONF_PROPERTIES);
			moveConfigurationDirectory();
			
			copyFilesForSchedulerJarsIntoLib(distributionType, hadoopDistributionType);
			changeFilePermissionOfAgentScript();
			changeFilePermissionOfInfluxDBScript();
			createDefaultInfluxDBJsonFile();
			
			saveSampleJson();
			createConfigurationFilesForClusters();
			CONSOLE_LOGGER.info("!!! Jumbune Deployment at [" + FoundPaths.get(JUMBUNE_HOME)
					+ "].......[SUCCESS]");

		} catch (Exception e) {
			CONSOLE_LOGGER.error("Error occurred while deploying jumbune.", e);
		} finally {
			if (session != null) {
				session.disconnect();
			}
			cleanup();
			SCANNER.close();
		}
	}
	
	private String getHadoopDistributionType(boolean isYarn, Map<String, String> argMap) {
		String hadoopDistributionType = argMap.get(DISTRIBUTION);
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
						+ "] passed during deploy. Available are : " + supportedDistributions);
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
	
	private boolean isCorrectDistribution(boolean isYarn, String hadoopDistributionType) {
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
	
	private String getDeploymentTypeForEMR(Session session,String hadoopHome) throws JSchException, IOException{
		String osVersionResponse = null;
		String hadoopVersionForAmzn = null;
		boolean isMapRForAmazon = false;
		String hadoopVerResponse = null;
		String[] individualHVResponses = null;
		
		osVersionResponse = SessionEstablisher.executeCommand(session,"lsb_release -si");
		
		if (osVersionResponse.contains("AMI")){
			String commandString = hadoopHome + File.separator + HADOOP_VERSION_YARN_COMMAND;			
			hadoopVerResponse = SessionEstablisher.executeCommand(session,commandString);
			individualHVResponses = hadoopVerResponse.split(Constants.NEW_LINE);
			for (String indiviResp : individualHVResponses){	
				if (indiviResp.trim().contains("mapr")){
					hadoopVersionForAmzn = Constants.EMRMAPR;
					CONSOLE_LOGGER.info("Detected MapR distribution of Hadoop under EMR");
					isMapRForAmazon = true;
					break;
				}			
			}
			if (!isMapRForAmazon){
				hadoopVersionForAmzn = Constants.EMRAPACHE;
				CONSOLE_LOGGER.info("Found Amazon Distribution of Hadoop under EMR");
			}			
		}else{
			CONSOLE_LOGGER.warn("Found unexpected response of OS Detection, expected was Amazon AMI. This may cause Deployment issues");
		}
		
		return hadoopVersionForAmzn;
	}
	
	/**
	 * Extracts the arguments from configuration file and put in map
	 * 
	 * @param argMap
	 *            Map containing properties and their values
	 * @throws IOException
	 */
	private static void readArgumentsFromPropertyFile(Map<String, String> argMap)
			throws IOException {
		if (argMap.containsKey("propertyfile")) {
			Properties props = new Properties();
			props.keySet();
			String filePath = argMap.get("propertyfile");
			
			FileInputStream in = null;
			try {
				in = new FileInputStream(filePath);
				props.load(in);
			} catch (IOException e) {
				CONSOLE_LOGGER.error("Unable to read file " + argMap.get("propertyfilepath"));
				DEBUG_FILE_LOGGER.error(e);
				exitVM(1);
			} finally {
				if (in != null) {
					in.close();
				}
			}
			String value = null, key = null, doubleQuotes = "\"";
			for (Object propertyKeyObject : props.keySet()) {
				key = (String) propertyKeyObject;
				if (key != null && argMap.get(key) == null) {
					value = props.getProperty(key);
					if (value != null && value.startsWith(doubleQuotes) && value.endsWith(doubleQuotes)) {
						argMap.put(key, value.substring(1, value.length() - 1));
					}
					argMap.put(key, props.getProperty(key));
				}
			}
		}
	}
	
	// Creates an Option for command line arguments
	private static Option createOption(String argument, String description) {
		return Option.builder()
				.longOpt(argument)
				.desc(description)
				.argName(argument)
				.hasArg()
				.numberOfArgs(1)
				.build();
	}
	
	// Build command line options
	private static Options buildOptions() {
		Options options = new Options();
		options.addOption(createOption("distribution", "Hadoop Distribution Type\n"
						+ "e.g. '--distribution a' for Apache,\n"
						+ "'--distribution c' for Cloudera,\n"
						+ "'--distribution e' for EMR,\n"
						+ "'--distribution h' for HortonWorks,\n"
						+ "'--distribution m' for MapR\n\t"));
		
		options.addOption(createOption("namenodeIP", "Address of the namenode machine\n"
						+ "e.g. '--namenodeIP 127.0.0.1'"));
		
		options.addOption(createOption("username", "Username of the namenode machine\n"
				+ "e.g. '--username user'"));
		
		options.addOption(createOption("password", "ENCRYPTED password of the namenode machine\n"
				+ "e.g. '--password 57496854Gz/Efde79nctrA=='. "
				+ "Alternatively you can provide private key file path instead of providing namenode password"));
		
		options.addOption(createOption("privatekeypath", "Private key file path\n"
				+ "e.g. '--privatekeypath /home/user/.ssh/id_rsa'"));
		
		options.addOption(createOption("influxdbhost", "InfluxDB Host"));
		
		options.addOption(createOption("influxdbport", "InfluxDB Port"));
		
		options.addOption(createOption("influxdbusername", "InfluxDB username"));
		
		options.addOption(createOption("influxdbpassword", "InfluxDB ENCRYPTED password"));
		
//		options.addOption(Option.builder().longOpt("install-influxdb")
//				.desc("It will install influxdb on the current machine if influxdb is not installed."
//						+ " You have to provide current user system password (in ENCRYPTED form) along with the option.\n"
//						+ "e.g. --install-influxdb 574as6854Gz/Efde79nctass.")
//				.argName("system-password")
//				.hasArg()
//				.build());
		
		options.addOption(createOption("propertyfile", "Properties file path containing options above (properties names without '--')\n"
				+ "e.g. --propertyfile /home/user/jumbunedeploy.properties"));
		
		options.addOption(Option.builder().longOpt("encryption")
				.desc("Use this option to run password encryption utility\n"
						+ "e.g. java -jar <jumbunejarfile>.jar --encryption")
				.argName("encryption")
				.hasArg(false)
				.build());
		
		options.addOption(Option.builder().longOpt("verbose")
				.argName("verbose")
				.hasArg(false)
				.build());
		
		options.addOption(Option.builder().longOpt("help")
				.desc("Display this help and exit")
				.argName("help")
				.hasArg(false)
				.build());
		return options;
	}
	
	private static Map<String, String> parseCommandLineArguments(String[] args) throws IOException {
		
		// If no argument is provided then reture empty map
		if (args.length == 0) {
			return new HashMap<>(0);
		}

		// Creating options
		Options options = buildOptions();
		
		// Parsing command line arguments
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (MissingArgumentException e) {
			CONSOLE_LOGGER.error("Missing Argument(s)");
			exitVM(1);
		} catch (ParseException e) {
			CONSOLE_LOGGER.error("Invalid option(s)");
			exitVM(1);
		}
		
		// Checking if any invalid option
		if ((args.length > 0 && cmd.getOptions().length == 0)) {
			CONSOLE_LOGGER.error("Invalid option(s)");
			HelpFormatter formatter = new HelpFormatter();
			formatter.setOptionComparator(null);
			formatter.printHelp("java -jar <jumbune jar file>.jar", options, true);
			exitVM(1);
		}
		
		// Displaying help
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.setOptionComparator(null);
			formatter.printHelp("java -jar <jumbune jar file>.jar", options, true);
			exitVM(0);
		}
		
		// Enabling debug level logging (in separate file)
		if (cmd.hasOption("verbose")) {
			turnLoggingLevelToDebug();
		}
		
		// Run password encryption utility if encryption option is provided and then exit
		if (cmd.hasOption("encryption")) {
			runEncryptionUtillity();
			exitVM(0);
		}
		
		// Creating map
		Map<String, String> argMap = new HashMap<>(cmd.getOptions().length);
		for (Option o : cmd.getOptions()) {
			argMap.put(o.getArgName(), o.getValue());
		}
		
		// Read options/arguments from properties file if provided
		readArgumentsFromPropertyFile(argMap);
		
		return argMap;
	}

	/**
	 * Run password encryption utility
	 * 
	 * @throws Exception
	 */
	private static void runEncryptionUtillity() {
		CONSOLE_LOGGER.info("Enter your password");
		Console console = System.console();
		char[] password = console.readPassword();
		while ((new String(password)).isEmpty()) {
			CONSOLE_LOGGER.info("Please enter a valid password");
			password = console.readPassword();
		}
		CONSOLE_LOGGER.info(
				"Your encrypted password is\n" + StringUtil.getEncrypted(new String(password)));
	}

	private static void turnLoggingLevelToDebug() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig("rollingFileAppender");
		loggerConfig.setLevel(Level.DEBUG);
		ctx.updateLoggers();
		CONSOLE_LOGGER.info("logging level changed to [DEBUG]");
		CONSOLE_LOGGER.info("Further details can be found in log file");
	}

	private void changeFilePermissionOfInfluxDBScript() {
		String jumbuneHome = FoundPaths.get(JUMBUNE_HOME);
		if (!jumbuneHome.endsWith("/")) {
			jumbuneHome = jumbuneHome + "/";
		}
		File file = new File(jumbuneHome + "bin/install-influxdb.sh");
		file.setExecutable(true, true);
	}
	
	private void changeFilePermissionOfAgentScript() {
		String jumbuneHome = FoundPaths.get(JUMBUNE_HOME);
		if (!jumbuneHome.endsWith("/")) {
			jumbuneHome = jumbuneHome + "/";
		}
		File file = new File(jumbuneHome + "agent-distribution/bin/startAgent");
		file.setExecutable(true, true);
	}
	
	// create influxdb.json file in $JUMBUNE_HOME/conf/defaultConfigurations/
	private void createDefaultInfluxDBJsonFile() {
		String jumbuneHome = FoundPaths.get(JUMBUNE_HOME);
		try {

			if (!jumbuneHome.endsWith("/")) {
				jumbuneHome = jumbuneHome + "/";
			}
			PrintWriter out = new PrintWriter(
					jumbuneHome + "conf/defaultConfigurations/influxdb.json");
			out.println(new Gson().toJson(influxDBConfMap));
			out.flush();
			out.close();
		} catch (Exception e) {
			CONSOLE_LOGGER.error("Unable to save json in " + jumbuneHome
					+ "conf/defaultConfigurations/influxdb.json");
		}
	}

	private void createConfigurationFilesForClusters() throws IOException {
		/*
		 * Grab the names of all clusters 'clusters' dir. if
		 * ('conf/clusterConfigurations' dir not exists) { Create all
		 * configuration files from scratch. } else { Grab all the file names
		 * and check anyone cluster missing. if (missing) { create configuration
		 * file from scratch for that cluster. } }
		 * 
		 * create configuration file from scratch for that cluster => create
		 * directory inside 'conf/clusterConfigurations'. copy all json files
		 * from 'conf/defaultConfigurations' to
		 * 'conf/clusterConfigurations/clusterName' dir. Open
		 * 'conf/clusterConfigurations/clusterName/influxdb.json' and add field
		 * 'database' (database == clusterName) and save json. After that create
		 * database.
		 */
		String string = "/influxdb.json";
		String database = "database";
		String host = "host";
		String port = "port";
		String password = "password";
		String username = "username";
		String retentionPeriod = "retentionPeriod";
		String influxdbjsonPath;
		FileWriter fw = null;
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> obj;

		String jumbuneHome = FoundPaths.get(JUMBUNE_HOME);
		if (!jumbuneHome.endsWith("/")) {
			jumbuneHome = jumbuneHome + "/";
		}

		String clustersPath = jumbuneHome + "clusters";
		String defaultConfPath = jumbuneHome + "conf/defaultConfigurations/";
		String clusterConfPath = jumbuneHome + "conf/clustersConfigurations/";
		File clustersDir = new File(clustersPath);
		if (!clustersDir.exists()) {
			return;
		}
		File clusterConfDir = new File(clusterConfPath);
		if (!clusterConfDir.exists()) {
			clusterConfDir.mkdirs();
		}
		File[] clusters = clustersDir.listFiles();
		for (File cluster : clusters) {
			if (!isExists(cluster, clusterConfDir)) {
				int dotIndex = cluster.getName().lastIndexOf('.');
				if (dotIndex == -1) {
					continue;
				}
				String clusterName = cluster.getName().substring(0, dotIndex);
				copyDir(new File(defaultConfPath), new File(clusterConfPath + clusterName));
				influxdbjsonPath = clusterConfPath + clusterName + string;
				try {
					obj = gson.fromJson(readFile(influxdbjsonPath), type);
				} catch (Exception e) {
					obj = new HashMap<String, String>(5);
				}
				obj.put(database, clusterName);

				if (obj.get(host) == null) {
					obj.put(host, "localhost");
					obj.put(port, "8086");
					obj.put(username, "root");
					obj.put(password, StringUtil.getEncrypted("root"));
					obj.put(database, clusterName);
					obj.put(retentionPeriod, "90");
				}
				createInfluxdbDatabase(obj.get(host), obj.get(port), obj.get(username),
						StringUtil.getPlain(obj.get(password)), obj.get(database));
				
				createInfluxdbRetentionPolicy(obj.get(host), obj.get(port), obj.get(username),
						StringUtil.getPlain(obj.get(password)), obj.get(database), retentionPeriod);
				updateRetentionPolicy(obj.get(host), obj.get(port), obj.get(username),
						StringUtil.getPlain(obj.get(password)), obj.get(database), retentionPeriod);
				
				try {
					fw = new FileWriter(influxdbjsonPath);
					fw.write(gson.toJson(obj));
				} finally {
					fw.close();
				}
			}
		}
	}
	
	private void createInfluxdbRetentionPolicy(String host, String port, String username, String password,
			String database, String retentionPeriod) {
		try {
			StringBuffer url = new StringBuffer();
			url.append(HTTP).append(host.trim()).append(COLON).append(port).append("/query?u=").append(username)
					.append(AND_P_EQUAL_TO).append(password).append(AND_Q_EQUAL_TO)
					.append("CREATE%20RETENTION%20POLICY%20ret_" + database + "%20on%20" + database + "%20DURATION%20"
							+ retentionPeriod + "d%20REPLICATION%201%20DEFAULT");

			URL obj = new URL(url.toString());
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.getContent();
			con.disconnect();
		} catch (Exception e) {
		}
	}
	
	private void updateRetentionPolicy(String host, String port, String username, String password,
			String database, String retentionPeriod) {
		try {
			StringBuffer url = new StringBuffer();
			url.append(HTTP).append(host.trim()).append(COLON).append(port).append("/query?u=").append(username)
			.append(AND_P_EQUAL_TO).append(password).append(AND_Q_EQUAL_TO);

			url.append("ALTER%20RETENTION%20POLICY%20ret_" + database + "%20on%20" + database + "%20DURATION%20"
					+ retentionPeriod + "d");

			URL obj = new URL(url.toString());
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.getContent();
			con.disconnect();
		} catch (Exception e) {
		}
	}

	private boolean isExists(File file, File dir) {
		for (File temp : dir.listFiles()) {
			if (temp.getName().equals(file.getName())) {
				return true;
			}
		}
		return false;
	}

	private String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, Charset.defaultCharset());
	}

	private int runCommand(Runtime runtime, String command)
			throws IOException, InterruptedException {
		if (runtime == null) {
			new String("");
		}
		DEBUG_FILE_LOGGER.info("Executing command \"" + command + "\"");
		Process process = runtime.exec(command);
		process.waitFor();
		String output = readFile(process.getInputStream());
		if (output != null && !output.isEmpty()) {
			DEBUG_FILE_LOGGER.debug(output);
		}
		String error = readFile(process.getErrorStream());
		if (error != null && !error.isEmpty()) {
			DEBUG_FILE_LOGGER.debug(error);
		}
		return process.exitValue();
	}

	private int runCommand(Runtime runtime, String[] command)
			throws IOException, InterruptedException {
		if (runtime == null) {
			new String("");
		}
		DEBUG_FILE_LOGGER.info("Executing command \"" + command + "\"");
		Process process = runtime.exec(command);
		process.waitFor();
		String output = readFile(process.getInputStream());
		if (output != null && !output.isEmpty()) {
			DEBUG_FILE_LOGGER.debug(output);
		}
		String error = readFile(process.getErrorStream());
		if (error != null && !error.isEmpty()) {
			DEBUG_FILE_LOGGER.debug(error);
		}
		return process.exitValue();
	}

	private boolean installAndConfigureInfluxDB(Map<String, String> argMap) throws Exception {
		Process process;
		Runtime runtime = Runtime.getRuntime();
		process = runtime.exec("lsb_release -si");
		process.waitFor();
		String error = readFile(process.getErrorStream());
		if (process.exitValue() != 0 && !error.isEmpty()) {
			throw new Exception(error);
		}
		String option = readFile(process.getInputStream()).toLowerCase();
		if (System.getProperty("sun.arch.data.model").equalsIgnoreCase("32")) {
			CONSOLE_LOGGER.info("Influxdb do not provide packaged 32-bit binaries. "
					+ "You can compile the source for a 32-bit x86 architecture. "
					+ "For more information see https://influxdb.com/download/index.html");
			throw new Exception("Can't install Influxdb");
		}

		int exitStatus = 0;
		if (option.contains("ubuntu") || option.contains("debian")) {
			File file = new File("influxdb_1.1.0_amd64.deb");

			if (!file.exists()) {
				CONSOLE_LOGGER.info("Downloading influxdb...\n");
				// runCommand(runtime,
				// "wget
				// https://dl.influxdata.com/influxdb/releases/influxdb_1.1.0_amd64.deb");

				try {
					download("https://dl.influxdata.com/influxdb/releases/influxdb_1.1.0_amd64.deb", "influxdb_1.1.0_amd64.deb");
				} catch (Exception e) {
					CONSOLE_LOGGER.info("Problem while downloading influxdb from https://dl.influxdata.com/influxdb/releases/influxdb_1.1.0_amd64.deb");
					return false;
				}

			}
			CONSOLE_LOGGER.info("Installing influxdb...");
			exitStatus = runCommand(runtime, "sudo dpkg -i influxdb_1.1.0_amd64.deb");
			
			if (exitStatus != 0) {
				CONSOLE_LOGGER.info("Problem while installing influxdb");
				return false;
			}
		} else if (option.contains("redhat") || option.contains("centos") || option.contains("amazonami")) {
			File file = new File("influxdb-1.1.0.x86_64.rpm");
			if (!file.exists()) {
				CONSOLE_LOGGER.info("Downloading influxdb...\n");
				try {
					download("https://dl.influxdata.com/influxdb/releases/influxdb-1.1.0.x86_64.rpm", "influxdb-1.1.0.x86_64.rpm");
				} catch (Exception e) {
					CONSOLE_LOGGER.info("Problem while downloading influxdb from https://dl.influxdata.com/influxdb/releases/influxdb-1.1.0.x86_64.rpm");
					return false;
				}
				
			/*	runCommand(runtime,
						"wget https://dl.influxdata.com/influxdb/releases/influxdb-1.1.0.x86_64.rpm");*/
			}
			CONSOLE_LOGGER.info("Installing influxdb...");
			exitStatus = runCommand(runtime, "sudo yum localinstall -y influxdb-1.1.0.x86_64.rpm");

			if (exitStatus != 0) {
				CONSOLE_LOGGER.info("Problem while installing influxdb");
				return false;
			}
		} else {
			try {
				createInfluxDBMap("localhost", "8086", "root", StringUtil.getEncrypted("root"),
						null);
			} catch (IOException e) {
			}
			CONSOLE_LOGGER.info("Unable to detect linux distribution on your system."
					+ "You can install by downloading binary package from  "
					+ "[https://dl.influxdata.com/influxdb/releases/influxdb-1.1.0_linux_amd64.tar.gz "
					+ "for 64 bit or https://dl.influxdata.com/influxdb/releases/influxdb-1.1.0_linux_armhf.tar.gz for arm]. Some services "
					+ "will not run properly without it.");
			return false;
		}

		CONSOLE_LOGGER.info("Configuring influxdb..");
		
		String influxdbConfFilePath = "/etc/influxdb/influxdb.conf";
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(influxdbConfFilePath));

			if (! "bind-address = \":8882\"".equalsIgnoreCase(br.readLine())) {
				CONSOLE_LOGGER.info("Changing default meta port 8088 to 8882");
				String[] command = { "sudo", "sed", "-i", "1s/^/bind-address = \":8882\"\\n/",
						influxdbConfFilePath };
				exitStatus = runCommand(runtime, command);
				if (exitStatus != 0) {
					CONSOLE_LOGGER.info("Unable to change port");
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		Thread.sleep(5000);
		CONSOLE_LOGGER.info("InfluxDB Username [root]");
		String influxDBUser = SCANNER.nextLine().trim();
		if (influxDBUser.isEmpty()) {
			influxDBUser = "root";
		}
		CONSOLE_LOGGER.info("InfluxDB Password [root]");
		Console console = System.console();
		String influxDBPassword = new String(console.readPassword());
		if (influxDBPassword.isEmpty()) {
			influxDBPassword = "root";
		}

		CONSOLE_LOGGER.info("InfluxDB http bind-address/port [8086]");
		String influxDBPort = SCANNER.nextLine().trim();
		if (influxDBPort.isEmpty()) {
			influxDBPort = "8086";
		}
		try {
			Integer.parseInt(influxDBPort);
		} catch (NumberFormatException e) {
			CONSOLE_LOGGER.info("Invalid port : " + influxDBPort + ". Considering 8086 as port");
		}

		if (Integer.parseInt(influxDBPort) != 8086) {
			CONSOLE_LOGGER.info("Changing influxdb default http port 8086 to " + influxDBPort);

			exitStatus = runCommand(runtime,
					"sudo sed -i s/8086/" + influxDBPort + "/ /etc/influxdb/influxdb.conf");
			if (exitStatus != 0) {
				CONSOLE_LOGGER.info("Unable to change port");
			}
		}
		
		// Disabling reporting
		CONSOLE_LOGGER.info("Disabling influxdb anonymous reporting");
		runCommand(runtime,
				"sudo sed -i 's/reporting-disabled = false/reporting-disabled = true/' /etc/influxdb/influxdb.conf");
		
		// Disabling meta data storing (preventing influxdb to store meta data at regular interval)
		CONSOLE_LOGGER.info("Disabling influxdb meta data storing");
		runCommand(runtime,
				"sudo sed -i 's/store-enabled = true/store-enabled = false/' /etc/influxdb/influxdb.conf");
		
		CONSOLE_LOGGER.info("Starting influxdb..");
		runtime.exec("sudo service influxdb start");

		Thread.sleep(5000);
		if (exitStatus != 0) {
			CONSOLE_LOGGER.info("Unable to start influxdb.");
			return false;
		}
		CONSOLE_LOGGER.info("Creating User \'" + influxDBUser + "\' in influxdb");
		createInfluxdbUser("localhost", influxDBPort, influxDBUser, influxDBPassword);
		
		configureInfluxDB(argMap, "localhost", influxDBPort, influxDBUser, influxDBPassword);
		return true;
	}

	private void download(String address, String localFileName) throws IOException {
		OutputStream out = null;
		URLConnection conn = null;
		InputStream in = null;

		try {
			URL url = new URL(address);
			out = new BufferedOutputStream(new FileOutputStream(localFileName));
			conn = url.openConnection();
			in = conn.getInputStream();
			byte[] buffer = new byte[1024];

			int numRead;

			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
			}

		} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
		}
	}

	private void createInfluxDBRelatedFiles(Map<String, String> argMap) {
		if (argMap.get("influxdbhost") != null) {
			configureInfluxDB(argMap, null, null, null, null);
			return;
		}

		CONSOLE_LOGGER.info("Influxdb is required for persistence of Cluster Analysis stats.");
		String selectedOption = "";
		
		boolean justConfigure = false;
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec("service influxdb status");
			process.waitFor();
			String output = readFile(process.getInputStream()).toLowerCase();
			if (output.toLowerCase().contains("influxdb Process is running [ OK ]".toLowerCase())) {
				justConfigure = true;
			}
		} catch (Exception e) {
			DEBUG_FILE_LOGGER.error("Unable to fetch the status of service [influxdb]");
		}
		
		if (justConfigure) {
			selectedOption = "c";
		} else {
			while (!(selectedOption.equalsIgnoreCase("i") || selectedOption.equalsIgnoreCase("c")
					|| selectedOption.equalsIgnoreCase("l"))) {
				CONSOLE_LOGGER.info(
						"(i) Install & Configure InfluxDB | (c) Just Configure InfluxDB | (l) Install Later");
				selectedOption = SCANNER.nextLine().trim();
			}
		}
		if (selectedOption.equalsIgnoreCase("i")) {
			try {
				boolean isCompleted = installAndConfigureInfluxDB(argMap);
				if (isCompleted) {
					CONSOLE_LOGGER.info("InfluxDB Deployment on ["
						+ InetAddress.getLocalHost().getHostAddress() + "].......[SUCCESS]");
				}
			} catch (Exception e) {
				CONSOLE_LOGGER.error("Problem while installing influxdb");
				DEBUG_FILE_LOGGER.error("Problem while installing influxdb", e);
			}
		} else if (selectedOption.equalsIgnoreCase("c")) {
			boolean isDeployed = configureInfluxDB(argMap, null, null, null, null);
			while (!isDeployed) {
				CONSOLE_LOGGER.info("Do you want to configure influxdb again? (y)/(n)");
				selectedOption = SCANNER.nextLine().trim();
				if (selectedOption.equalsIgnoreCase("y")) {
					isDeployed = configureInfluxDB(argMap, null, null, null, null);
					CONSOLE_LOGGER.info("Configuring InfluxDB instance.......[SUCCESS]");
				} else if (selectedOption.equalsIgnoreCase("n")) {
					CONSOLE_LOGGER.info(
							"Some modules will be having problem without influxdb. Continue deploying..");
					break;
				}
			}
		} else {
			try {
				createInfluxDBMap("localhost", "8086", "root", StringUtil.getEncrypted("root"),
						null);
			} catch (IOException e) {
				CONSOLE_LOGGER.error("Error while creating influxdb.properties file");
				DEBUG_FILE_LOGGER.error("Error while creating influxdb.properties file", e);
				return;
			}
			CONSOLE_LOGGER.info(
					"You can later install influxdb using $JUMBUNE_HOME/bin/install-influxdb.sh");
		}
	}

	private boolean configureInfluxDB(Map<String, String> argMap, String influxDBHost,
			String influxDBPort, String influxDBUsername, String influxDBPassword) {

		if (influxDBHost == null && argMap.get("influxdbhost") != null) {
			influxDBHost = argMap.get("influxdbhost");
		} else if (influxDBHost == null) {
			String masterNode;
			try {
				masterNode = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				masterNode = "127.0.0.1";
			}
			CONSOLE_LOGGER
					.info("Please provide hostname or ip address in which InfluxDB is installed ["
							+ masterNode + "]");
			influxDBHost = SCANNER.nextLine().trim();
			if (influxDBHost.isEmpty()) {
				influxDBHost = masterNode;
			}
		}

		if (influxDBPort == null && argMap.get("influxdbport") != null) {
			influxDBPort = argMap.get("influxdbport");
		} else if (influxDBPort == null) {
			CONSOLE_LOGGER.info("InfluxDB http bind-address/port [8086]");
			influxDBPort = SCANNER.nextLine().trim();
			if (influxDBPort.isEmpty()) {
				influxDBPort = "8086";
			}
		}

		if (influxDBUsername == null && argMap.get("influxdbusername") != null) {
			influxDBUsername = argMap.get("influxdbusername");
		} else if (influxDBUsername == null) {
			CONSOLE_LOGGER.info("InfluxDB Username [root]");
			influxDBUsername = SCANNER.nextLine().trim();
			if (influxDBUsername.isEmpty()) {
				influxDBUsername = "root";
			}
		}

		if (influxDBPassword == null && argMap.get("influxdbpassword") != null) {
			influxDBPassword = argMap.get("influxdbpassword");
		} else if (influxDBPassword == null) {
			CONSOLE_LOGGER.info("InfluxDB Password [root]");
			Console console = System.console();
			influxDBPassword = new String(console.readPassword());
			if (influxDBPassword.isEmpty()) {
				influxDBPassword = "root";
			}
			influxDBPassword = StringUtil.getEncrypted(influxDBPassword);
		} else {
			influxDBPassword = StringUtil.getEncrypted(influxDBPassword);
		}

		// String influxDatabaseName = "jumbune";
		try {
			createInfluxDBMap(influxDBHost, influxDBPort, influxDBUsername, influxDBPassword, null);
		} catch (IOException e) {
			CONSOLE_LOGGER.error("Error while creating influxdb.properties file");
			DEBUG_FILE_LOGGER.error("Error while creating influxdb.properties file", e);
			return false;
		}
		/*
		 * try { createInfluxdbDatabase(influxDBHost, influxDBPort,
		 * influxDBUsername, influxDBPassword, influxDatabaseName); } catch
		 * (IOException e) { CONSOLE_LOGGER.error(
		 * "Error while creating database in influx"); DEBUG_FILE_LOGGER.error(
		 * "Error while creating database in influx", e); return false; }
		 */
		return true;
	}

	private String readFile(InputStream inputStream) {
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
			DEBUG_FILE_LOGGER.error(e);
			return sb.toString();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					DEBUG_FILE_LOGGER.error(e);
				}
			}
		}
	}

	private void createInfluxDBMap(String host, String port, String username, String password,
			String database) throws IOException {
		/* Properties properties = new Properties(); */
		influxDBConfMap.put("host", host);
		influxDBConfMap.put("port", port);
		influxDBConfMap.put("username", username);
		influxDBConfMap.put("password", password);
		influxDBConfMap.put("retentionPeriod", 90);
		// influxDBConfMap.put("database", database);
		/*
		 * String jumbuneHome = FoundPaths.get("<JUMBUNE.HOME>"); File file =
		 * new File(jumbuneHome + "/conf"); file.mkdirs(); file = new
		 * File(jumbuneHome + "/conf/influxdb.properties"); FileOutputStream
		 * fileOutputStream = null; try { fileOutputStream = new
		 * FileOutputStream(file); properties.store(fileOutputStream,
		 * "Influx DB Configurations\n" +
		 * "Here the password is in encrypted form. You can get encrypted password by running password encyption utility using \"java -jar <jumbunejarfile> --encryption\""
		 * ); } finally { if (fileOutputStream != null) {
		 * fileOutputStream.close(); } }
		 */
	}

	private void createInfluxdbUser(String host, String port, String username,
			String password) {
		StringBuffer url = new StringBuffer();
		if (host.equalsIgnoreCase("127.0.0.1")) {
			host = "localhost";
		}
		boolean exceptionOccurs = false;
		url.append(HTTP).append(host).append(COLON).append(port).append("/query?q=");
		url.append("CREATE%20USER%20" + username + "%20WITH%20PASSWORD%20%27" + password
				+ "%27%20WITH%20ALL%20PRIVILEGES");
		HttpURLConnection con = null;
		try {
			URL obj = new URL(url.toString());
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.getContent();
		} catch (java.net.ConnectException e) {
			CONSOLE_LOGGER.error("Error while creating influxdb user. Trying again..");
			try {
				Thread.sleep(5000);
				URL obj = new URL(url.toString());
				con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
				con.getContent();
				con.disconnect();
			} catch (Exception e1) {
				CONSOLE_LOGGER.error("Unable to create influxdb user");
				DEBUG_FILE_LOGGER.error(e1);
				exceptionOccurs = true;
			}
		} catch (Exception e) {
			exceptionOccurs = true;
			CONSOLE_LOGGER.error("Unable to create influxdb user");
			DEBUG_FILE_LOGGER.error(e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		if ( !exceptionOccurs ) {
			CONSOLE_LOGGER.info("User creation.......[SUCCESS]");
		}
	}

	public void createInfluxdbDatabase(String host, String port, String username,
			String password, String database) {
		try {
			StringBuffer url = new StringBuffer();
			url.append(HTTP).append(host).append(COLON).append(port).append("/query?");
			url.append("u=").append(username).append(AND_P_EQUAL_TO).append(password);
			url.append(AND_Q_EQUAL_TO).append("create%20database%20" + database);

			URL obj = new URL(url.toString());
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.getContent();
			con.disconnect();
		} catch (Exception e) {

		}
	}

	private void copyFilesForSchedulerJarsIntoLib(String distributionType,
			String hadoopDistributionType) throws IOException {
		Deployer deployer = DeployerFactory.getDeployer(distributionType, hadoopDistributionType);
		String[] SchedularJars = deployer.getSchedularJars();
		String jumbuneHome = FoundPaths.get(JUMBUNE_HOME);
		String warPath = jumbuneHome + "/modules/jumbune-web-" + Versioning.COMMUNITY_BUILD_VERSION
				+ Versioning.COMMUNITY_DISTRIBUTION_NAME + ".war";
		String libLocation = jumbuneHome + "/lib/";
		URL url = new URL("jar:file:" + warPath + "!/");
		JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
		final JarFile jarFile = ((JarURLConnection) jarConnection).getJarFile();
		InputStream inputStream = null;
		try {
			for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
				final JarEntry entry = e.nextElement();
				String jarToExtract = entry.getName();
				if (jarToExtract.endsWith(".jar")) {
					for (String jarToAdd : SchedularJars) {
						if (jarToExtract.startsWith(jarToAdd)) {
							inputStream = jarFile.getInputStream(entry);
							String locationToCopy = libLocation + jarToExtract.substring(
									jarToExtract.lastIndexOf(File.separator),
									jarToExtract.length());
							Files.copy(inputStream, Paths.get(locationToCopy),
									StandardCopyOption.REPLACE_EXISTING);
						}
					}
				}
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	private void serializeDistributionType(String distributionType,
			String HadoopDistribution) throws IOException {
		FileWriter writer = new FileWriter(
				new File("./WEB-INF/classes/distributionInfo.properties"));
		writer.write("DistributionType=" + distributionType);
		writer.write("\n");
		writer.write("HadoopDistribution=" + HadoopDistribution);
		writer.close();
	}

	// Create configuration file and folder in Jumbune Home while deploying
	private void createConfigurationFile(String hadoopType, String hadoopDistribution)
			throws IOException {

		String configurationpath;
		FileWriter writer = null;
		try {
			configurationpath = FoundPaths.get(JUMBUNE_HOME) + File.separator
					+ ExtendedConstants.CONFIGURATION;
			File configuration = new File(configurationpath);
			if (!configuration.exists()) {
				configuration.mkdirs();
			}
			File clusterfile = new File(
					configurationpath + File.separator + ExtendedConstants.CLUSTER_INFO);

			if (clusterfile.exists()) {
				clusterfile.delete();
			}
			writer = new FileWriter(clusterfile);
			writer.write("HadoopType=" + hadoopType);
			writer.write("\n");
			writer.write("HadoopDistribution=" + hadoopDistribution);
		} catch (IOException e) {
			CONSOLE_LOGGER.error("IO Exception");
			DEBUG_FILE_LOGGER.error(e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void moveFromResourceToConf(String fileName) throws IOException {

		StringBuffer dest = new StringBuffer().append(FoundPaths.get(JUMBUNE_HOME))
				.append(File.separator).append(Constants.CONFIGURATION).append(File.separator)
				.append(fileName);
		StringBuffer source = new StringBuffer().append(FoundPaths.get(JUMBUNE_HOME))
				.append(File.separator).append(RESOURCES).append(File.separator).append(fileName);
		File destination = new File(dest.toString());
		File target = new File(source.toString());
		if (destination.exists()) {
			destination.delete();
		}
		Files.move(target.toPath(), destination.toPath());
	}

	private void moveConfigurationDirectory() throws IOException {
		String srcDirPath = FoundPaths.get(JUMBUNE_HOME) + "/resources/defaultConfigurations";
		File srcDir = new File(srcDirPath);
		String destDirPath = FoundPaths.get(JUMBUNE_HOME) + "/conf/defaultConfigurations";
		File destDir = new File(destDirPath);
		copyDir(srcDir, destDir);
	}

	public void copyDir(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdirs();
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyDir(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
	}

	private void checkJumbuneDirectoryCreation() {
		for (int j = 0; j < FOLDERS.length; j++) {
			final File f = new File(FoundPaths.get(JUMBUNE_HOME), FOLDERS[j]);
			if (!ensureDirectoryExists(f)) {
				CONSOLE_LOGGER.error("Error occurred while creating: " + f.getAbsolutePath());
			}
		}
	}

	private void changeRunnablePermissions() {
		File f;
		for (String file : executableFiles) {
			f = new File(FoundPaths.get(JUMBUNE_HOME) + file);
			f.setExecutable(true);
		}
	}

	private Session getSession(Map<String, String> argMap) throws IOException {
		Console console = System.console();
		return validateUserAuthentication(console, argMap);
	}

	private URLConnection performSanity() throws IOException, URISyntaxException {
		new File("./WEB-INF/classes/").mkdirs();
		new File("./WEB-INF/lib/").mkdirs();
		String javaHomeStr = null;
		javaHomeStr = getAndCheckDirectoryExistence(JAVA_ENV_VAR);
		FoundPaths.put("<JAVA.HOME>", javaHomeStr);

		String jumbuneHomeStr = null;
		jumbuneHomeStr = getAndCheckDirectoryExistence(JUMBUNE_ENV_VAR);
		File jumbuneHome = new File(jumbuneHomeStr);
		if (!ensureDirectoryExists(jumbuneHome)) {
			CONSOLE_LOGGER
					.warn("Failed to create directory (may be already exist)" + jumbuneHomeStr);
		}
		FoundPaths.put(JUMBUNE_HOME, jumbuneHomeStr);

		String currentDirLib = System.getProperty(USER_DIR) + "/" + "/lib/";
		new File(currentDirLib).mkdirs();

		CodeSource codeSource = DeployUtil.class.getProtectionDomain().getCodeSource();
		File distJarFile = new File(codeSource.getLocation().toURI().getPath());
		String path = getJarContainingFolder(codeSource, DeployUtil.class);
		String jarPath = path + "/" + distJarFile.getName();
		URL url = new URL("jar:file:" + jarPath + "!/");
		JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
		return jarConnection;
	}

	/**
	 * This takes directory from user and checks whether it is exists or not.it
	 * also takes directory path if it is set in environment variable.
	 * 
	 * @param enviromentVariable
	 *            , the expected environment variable
	 * @return Directory name if it is valid and exist.
	 */
	private String getAndCheckDirectoryExistence(String enviromentVariable) {
		String directoryPath = null;
		File file = null;
		if (!System.getenv().containsKey((enviromentVariable))) {

			CONSOLE_LOGGER.info(enviromentVariable + " not set as environment variable.");
			if (enviromentVariable.equals(JUMBUNE_ENV_VAR)) {
				CONSOLE_LOGGER.info(
						"Please provide the absolute path to a folder where you want to deploy Jumbune");
			}
			directoryPath = SCANNER.nextLine().trim();
			file = new File(directoryPath);
			while (isNullOrEmpty(directoryPath) || !ensureDirectoryExists(file)) {
				CONSOLE_LOGGER.info("INVALID: Please provide a valid existing directory!!!");
				directoryPath = SCANNER.nextLine().trim();
				file = new File(directoryPath);
			}
		} else {
			directoryPath = System.getenv(enviromentVariable);
			file = new File(directoryPath);
			CONSOLE_LOGGER.info(enviromentVariable + " linked to :- " + directoryPath);
		}
		return directoryPath;
	}

	/***
	 * This method does other addition task like fetching hadoop core jars and
	 * other required jar from libs of namenode. and adds essential agent
	 * specific jars to distribution of agent.
	 * 
	 * @param javaHomeStr,
	 *            absolute path of java home
	 * @param session,
	 *            jschSession instance
	 * @param jumbuneHomeStr
	 * @param deployer
	 * @param distributionType
	 * @param currentDir
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void updateJumbuneAndHadoopDistribution(String javaHomeStr, Session session,
			String jumbuneHomeStr, String hadoopDistributionType, String distributionType)
			throws JSchException, IOException, InterruptedException {
		String currentDir = System.getProperty(USER_DIR) + "/";
		String currentLibDir = currentDir + "/lib/";
		new File(currentLibDir).mkdirs();
		String hadoopHome = getHadoopLocation(session, hadoopDistributionType);
		if (hadoopHome.endsWith(File.separator)) {
			hadoopHome = hadoopHome.substring(0, hadoopHome.length() - 1);
		}
		try {
			SessionEstablisher.fetchHadoopJarsFromNamenode(session, username, namenodeIP,
					hadoopHome, currentDir + WEB_FOLDER_STRUCTURE, hadoopDistributionType,
					distributionType);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			CONSOLE_LOGGER.error("Invalid: Hadoop distribution [" + hadoopDistributionType
					+ "] passed during deploy doesn't match with the deployed distribution of Hadoop");
			exitVM(1);
		}
		String updateJumbuneWar = append(javaHomeStr, "/bin/", UPDATE_JAR, jumbuneHomeStr,
				UPDATE_WAR_FILE, "/");
		String updateJumbuneWarClasses = append(javaHomeStr, "/bin/", UPDATE_JAR, jumbuneHomeStr,
				UPDATE_WAR_CLASSES_FILE, "/");
		String updateAgentJar = append(javaHomeStr, "/bin/", UPDATE_JAR, jumbuneHomeStr,
				UPDATE_AGENT_JAR);
		String copyHadoopJarsToLib = append("cp -r ", currentDir, WEB_FOLDER_STRUCTURE, " ",
				FoundPaths.get(JUMBUNE_HOME), Path.SEPARATOR, " ");
		executeCommand(copyHadoopJarsToLib);
		executeCommand(updateJumbuneWar);
		executeCommand(updateJumbuneWarClasses);
		executeCommand(updateAgentJar);

		DEBUG_FILE_LOGGER.debug("Updated agent jar and war");
	}

	private String getHadoopLocation(Session session, String hadoopDistributionType)
			throws JSchException, IOException {
		DEBUG_FILE_LOGGER.debug("Trying to locate Hadoop with echo $HADOOP_HOME");
		String hadoopHome = SessionEstablisher.executeCommandUsingShell(session,
				SessionEstablisher.ECHO_HADOOP_HOME, "hadoop");
		DEBUG_FILE_LOGGER.debug("Hadoop location with echo $HADOOP_HOME [" + hadoopHome + "]");
		if (hadoopHome == null || hadoopHome.trim().isEmpty()
				|| !hadoopHome.contains(File.separator)) {
			String possibleHome;
			DEBUG_FILE_LOGGER.debug("Trying to locate Hadoop with whereis hadoop");
			possibleHome = SessionEstablisher.executeCommand(session,
					SessionEstablisher.WHERE_IS_HADOOP);
			DEBUG_FILE_LOGGER.debug("Hadoop location with whereis hadoop" + possibleHome);
			validateHadoopLocation(possibleHome);
			String[] hadoopSplits = possibleHome.split("\\s+");
			DEBUG_FILE_LOGGER
					.debug("Found entries of whereis hadoop:" + Arrays.toString(hadoopSplits));
			for (String split : hadoopSplits) {
				if (split.contains("/lib/") && containsHadoopLib(split, session)) {
					hadoopHome = split;
				}
			}
			if ((hadoopHome == null || hadoopHome.trim().isEmpty())
					&& hadoopDistributionType.equalsIgnoreCase("c")) {
				//trying for parcel based detection
				CONSOLE_LOGGER.info("Now trying for parcel based Hadoop");
				for (String split : hadoopSplits) {
					if (split.contains("/bin/")) {
						String parcelLocation = getAbsoluteConfDirPath(split, session);
						hadoopHome = parcelLocation.replace("bin", "lib");
					}
				}
			}
			if ((hadoopHome == null || hadoopHome.trim().isEmpty())
					&& hadoopDistributionType.equalsIgnoreCase("m")) {
				// Support in case of mapr is run through VM.
				String llResponse = SessionEstablisher.executeCommandUsingShell(session,
						SessionEstablisher.LL_COMMAND, "->");
				DEBUG_FILE_LOGGER.debug("<ll> command Response" + llResponse);
				hadoopHome = getHadoopHome(llResponse);
			}
			validateHadoopLocation(hadoopHome);
		}
		hadoopHome = hadoopHome.replace("\n", "");
		CONSOLE_LOGGER.info("Using Hadoop: [" + hadoopHome + "]");
		return hadoopHome;
	}

	private String getAbsoluteConfDirPath(String location, Session session) throws JSchException, IOException {

		String response = null;
		if (location == null || location.trim().isEmpty() || !location.contains("/")) {
			throw new IllegalArgumentException("Passed an expected argument as a directory ["+location+"]");
		}
		String result = null;
		String llLocation = new StringBuilder().append("ll ").append(location).append(" \n \n").toString();
		response = SessionEstablisher.executeCommandUsingShell(session, llLocation, "->");
		if (response != null && !response.isEmpty() && response.indexOf(">")!=-1) {
	    result = response.substring((response.indexOf(">") + 1),
					response.length());
		result = result.endsWith(File.separator)?result:result.trim();
		result.replaceAll("\u001B\\[01;32m", "");
		result = result.substring(result.indexOf(File.separator));
		}
		CONSOLE_LOGGER.info("Found linked Hadoop bin path:"+result);
		if(result!=null){
			String recursiveResponse = getAbsoluteConfDirPath(result, session);
			if(recursiveResponse!=null){
				result = recursiveResponse;
			}
		}
		return result;
	}

	/**
	 * Gets the hadoop home in case where HADOOP_HOME is not set and hadoop lib
	 * is not installed.
	 *
	 * @param llResponse
	 *            the ll response
	 * @return the hadoop home
	 */
	private String getHadoopHome(String llResponse) {
		if (llResponse != null) {
			llResponse = llResponse.substring((llResponse.indexOf(">") + 1), llResponse.length());
			llResponse = llResponse.substring(0, llResponse.indexOf("bin") - 1);
			return llResponse.replaceAll("\u001B\\[01;32m", "");
		}
		return null;
	}

	private void validateHadoopLocation(String hadoopHome) {
		if (hadoopHome == null || hadoopHome.trim().isEmpty()
				|| !hadoopHome.contains(File.separator)) {
			CONSOLE_LOGGER.info("Unable to find location of Hadoop! Please make"
					+ " sure Hadoop deployment instruction are followed as recommended,"
					+ " then retry running the deployment.");
			exitVM(1);
		}
	}

	private boolean containsHadoopLib(String location, Session session)
			throws JSchException, IOException {
		boolean result = false;
		String listedDirectory = SessionEstablisher.executeCommand(session,
				SessionEstablisher.LS_PREFIX_PART + location + SessionEstablisher.LS_POSTFIX_COLON_PART);
		if (listedDirectory != null && !listedDirectory.isEmpty()) {
			String[] directoryList = listedDirectory.split("\n");
			for (int index = 1; index < directoryList.length; index++) {
				if (directoryList[index].contains("lib")) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * validate user authentication and ask for username ip of namenode and
	 * password.
	 * 
	 * @param session
	 *            {@link Session} established user session using jsch
	 * @param console
	 *            Console for reading password on terminal
	 * @return Session, established session after successfull user
	 *         authentication.
	 * @throws Exception
	 */
	private Session validateUserAuthentication(Console console, Map<String, String> argMap)
			throws IOException {
		char[] password = null;
		String privateKeyPath;
		Session tempSession;
		boolean sysexit;
		int retryAttempts = 0;
		CONSOLE_LOGGER.info(
				"\r\nJumbune needs to calibrate itself according to the installed Hadoop distribution, please provide details about hadoop namenode machine");
		do {
			sysexit = false;
			String masterNode = InetAddress.getLocalHost().getHostAddress();

			namenodeIP = argMap.get(NAMENODE_IP);
			if (namenodeIP == null) {
				CONSOLE_LOGGER.info("\r\nIP address of the namenode machine [" + masterNode + "]");
				namenodeIP = SCANNER.nextLine().trim();
				if (namenodeIP.isEmpty()) {
					namenodeIP = masterNode;
				}
			} else {
				sysexit = true;
			}

			username = argMap.get(USER_NAME);
			String user = System.getProperty("user.name");
			if (username == null) {
				CONSOLE_LOGGER.info("Username of the namenode machine: [" + user + "]");
				username = SCANNER.nextLine().trim();
				if (username.isEmpty()) {
					username = user;
				}
			} else {
				sysexit = true;
			}
			String isPasswordlessSSH = null;
			privateKeyPath = argMap.get(PRIVATE_KEY_PATH);

			if (argMap.get(PASSWORD) == null && privateKeyPath == null) {
				CONSOLE_LOGGER.info("Do we have passwordless SSH between [" + masterNode + "] - ["
						+ namenodeIP + "] machines? (y)/(n)");
				isPasswordlessSSH = SCANNER.nextLine().trim();
				while (!"y".equalsIgnoreCase(isPasswordlessSSH)
						&& !"n".equalsIgnoreCase(isPasswordlessSSH)) {
					CONSOLE_LOGGER.info("Do we have passwordless SSH between [" + masterNode
							+ "] - [" + namenodeIP + "] machines? (y)/(n)");
					isPasswordlessSSH = SCANNER.nextLine().trim();
				}
				if ("n".equalsIgnoreCase(isPasswordlessSSH)) {
					CONSOLE_LOGGER.info("Password of the namenode machine:");
					password = console.readPassword();
					while ((new String(password)).isEmpty()) {
						CONSOLE_LOGGER.info("Please enter a valid password");
						password = console.readPassword();
					}
				}
			} else if (privateKeyPath != null) {
				password = null;
				sysexit = true;
			} else {
				password = StringUtil.getPlain(argMap.get(PASSWORD)).toCharArray();
				sysexit = true;
			}
			String defaultPrivateKeyPath = null;
			if (privateKeyPath == null && password == null) {
				String userHome = System.getProperty("user.home");
				if (userHome.contains(user)) {
					defaultPrivateKeyPath = userHome + "/.ssh/id_rsa";
				} else {
					defaultPrivateKeyPath = "/home/" + user + "/.ssh/id_rsa";
				}
				CONSOLE_LOGGER.info(
						"Please provide private key file path [" + defaultPrivateKeyPath + "]");
				privateKeyPath = SCANNER.nextLine().trim();
				if (privateKeyPath.isEmpty()) {
					privateKeyPath = defaultPrivateKeyPath;
				}
				File privateKeyFile = new File(privateKeyPath);
				while (!privateKeyFile.exists() || privateKeyFile.isDirectory()) {
					CONSOLE_LOGGER.info("private key file should exist, please provide file path");
					privateKeyPath = SCANNER.nextLine().trim();
					privateKeyFile = new File(privateKeyPath);
				}
			} else {
				sysexit = true;
			}
			
			setSampleClusterDetails(privateKeyPath, password, namenodeIP, user);
			
			if (password != null) {
				DEBUG_FILE_LOGGER.debug("Authenticating username[" + username + "], namenodeIP["
						+ namenodeIP + "], with password based authentication");
				tempSession = SessionEstablisher.establishConnection(username, namenodeIP,
						new String(password), null);
			} else {
				DEBUG_FILE_LOGGER.debug("Authenticating username[" + username + "], namenodeIP["
						+ namenodeIP + "], and privatekeyPath[" + privateKeyPath + "]");
				tempSession = SessionEstablisher.establishConnection(username, namenodeIP, null,
						privateKeyPath);
			}
			if (tempSession != null && tempSession.isConnected()) {
				DEBUG_FILE_LOGGER.debug("Session Established");
			}
			if (++retryAttempts == MAX_RETRY_ATTEMPTS) {
				CONSOLE_LOGGER.error(
						"Exiting Installation as maximum number of authentication attempts are exhaused!");
				exitVM(1);
			}

			if (sysexit == true && (tempSession == null || !tempSession.isConnected())) {
				CONSOLE_LOGGER.error("Failed to authenticate, check username and password");
				exitVM(1);
			}
		} while (tempSession == null || !tempSession.isConnected());
		CONSOLE_LOGGER
				.info("Establishing session between [" + InetAddress.getLocalHost().getHostAddress()
						+ "] - [" + namenodeIP + "] machines.......[SUCCESS]");
		return tempSession;
	}

	private void cleanup() throws IOException, InterruptedException {
		executeCommand(CLEAN_UNUSED_FILES_AND_DIRS);
	}
	
	private void setSampleClusterDetails(String privateKeyPath,
			char[] password, String namenodeIP, String username) {
		
		String pass = null;
		String isPasswordlessSSH = null; 
		String doubleQuote = "\"";
		
		if (!isNullOrEmpty(privateKeyPath)) {
			privateKeyPath = doubleQuote + privateKeyPath + doubleQuote;
			isPasswordlessSSH = "true";
		} else {
			pass = doubleQuote + StringUtil.getEncrypted(new String(password)) + doubleQuote;
			isPasswordlessSSH = "false";
		}
		
		this.sampleClusterJson = String.format(this.sampleClusterJsonFormat, 
				this.sampleClusterName, namenodeIP, isPasswordlessSSH, username, 
				privateKeyPath, pass, username,
				namenodeIP, isPasswordlessSSH, privateKeyPath, pass,
				namenodeIP, isPasswordlessSSH, username, username);
	}
	
	private void setSampleClusterName(char distributionType) {
		String distribution = "";
		switch (distributionType) {
			case 'a' :
					distribution = "Apache";
					break;
			case 'c' :
					distribution = "Cloudera";
					break;
			case 'e' :
					distribution = "EMR";
					break;
			case 'h' : 
					distribution = "HortonWorks";
					break;
			case 'm' :
					distribution = "MapR";
					break;
		}
		this.sampleClusterName = "Sample" + distribution + "Cluster";
	}
	
	private void saveSampleJson() {
		File jsonDirectory = new File(System.getenv("JUMBUNE_HOME") + "/clusters");
		if (!jsonDirectory.exists()) {
			jsonDirectory.mkdir();
		}
		
		String jsonFilePath = jsonDirectory.getAbsolutePath() + File.separator + this.sampleClusterName + ".json";
		if (new File(jsonFilePath).exists()) {
			return;
		}
		try {
			PrintWriter out = new PrintWriter(jsonFilePath);
			out.print(this.sampleClusterJson);
			out.flush();
			out.close();
		} catch (Exception e) {
			CONSOLE_LOGGER.error("Unable to create sample cluster json in Jumbune Home", e.getMessage());
			DEBUG_FILE_LOGGER.error(e);
		}
	}

	/**
	 * execute command using system.getRuntime method
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void executeCommand(String command) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		p.destroy();
	}

	/**
	 * This method checks whether a given string is null or empty
	 * 
	 * @param str
	 *            given string
	 * @return true if the given string is null or empty
	 */
	private static boolean isNullOrEmpty(String str) {
		if (str == null) {
			return true;
		}

		if (((String) str).trim().length() == 0) {
			return true;
		}

		return false;
	}
	
	private boolean contains(String[] arr, String str) {
		for (String s : arr) {
			if (str.equals(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method copy the content of jar file recursively to a destination
	 * directory
	 * 
	 * @param destDir
	 * @param jarConnection
	 * @return boolean
	 * @throws IOException
	 */
	private boolean extractJarDirectories(final File destDir,
			final URLConnection jarConnection) throws IOException {
		DEBUG_FILE_LOGGER.debug("Extracting jar directories to [" + destDir + "] location");
		final JarFile jarFile = ((JarURLConnection) jarConnection).getJarFile();
		for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
			final JarEntry entry = e.nextElement();
			String filename;
			if ((filename = entry.getName()) != null) {
				if (filename.startsWith("lib") || filename.startsWith("modules")) {
					includeAdditionalFileInLib(entry, jarFile);
				}
				String dirSplit = filename.split(File.separator)[0];
				if (contains(subDirs, dirSplit)) {
					copyJarResources(destDir, jarFile, entry);
				}
				if(filename.endsWith("NOTICE.txt") || filename.endsWith("TERMS.txt") || filename.endsWith("jumbune.version")){	
					if (filename.startsWith(META_INF_NOTICE_TXT)) {
						continue;
					}
					final File f = new File(destDir, filename);
					InputStream entryInputStream = null;
						try {
							entryInputStream = jarFile.getInputStream(entry);
							copyStream(entryInputStream, f);
						} finally {
							if (entryInputStream != null) {
								entryInputStream.close();
							}
						}
				}
			}
		}
		return true;
	}

	private boolean copyJarResources(final File destDir, final JarFile jarFile,
			final JarEntry entry) throws IOException {
		String filename = entry.getName();
		final File f = new File(destDir, filename);
		if (!entry.isDirectory()) {
			InputStream entryInputStream = null;
			try {
				entryInputStream = jarFile.getInputStream(entry);
				if (!copyStream(entryInputStream, f)) {
					return false;
				}

				if (filename.indexOf("htfconf") != -1 || filename.indexOf(".properties") != -1
						|| filename.indexOf(".yaml") != -1) {
					DEBUG_FILE_LOGGER.debug("Replacing placeholders for [" + filename + "]");
					replacePlaceHolders(f, FoundPaths);
				}
			} finally {
				if (entryInputStream != null) {
					entryInputStream.close();
				}
			}

		} else {
			if (!ensureDirectoryExists(f)) {
				throw new IOException("Error occurred while extracting : " + f.getAbsolutePath());
			}
		}
		return true;
	}

	/**
	 * Add addition file to the library
	 * 
	 * @param entry
	 * @throws IOException
	 */
	private void includeAdditionalFileInLib(JarEntry entry, JarFile jarFile)
			throws IOException {
		String fileToBeCopiedOnAgent = null;
		String filename = entry.getName();
		String agentDestDir = System.getProperty(USER_DIR) + "/";
		for (String jarName : agentJars) {
			int index = filename.indexOf('/');
			fileToBeCopiedOnAgent = filename.substring(index + 1, filename.length());
			if (filename.contains(jarName)) {
				DEBUG_FILE_LOGGER
						.debug("Copied [" + fileToBeCopiedOnAgent + "]  to Agent distribution");
				File fJar = new File(agentDestDir, "lib/" + fileToBeCopiedOnAgent);
				InputStream entryInputStream = null;
				try {
					entryInputStream = jarFile.getInputStream(entry);
					copyStream(entryInputStream, fJar);
				} finally {
					if (entryInputStream != null) {
						entryInputStream.close();
					}
				}
			}
		}
	}

	/**
	 * Method for copying the content of file
	 * 
	 * @param source
	 *            file stream
	 * @param destination
	 *            directory where user wants to copy file
	 * @return boolean if copied then return true otherwise false
	 */
	private boolean copyStream(final InputStream is, final File f) {
		OutputStream fStream = null;
		try {
			fStream = new FileOutputStream(f);
			final byte[] buf = new byte[Constants.ONE_ZERO_TWO_FOUR];
			int len = 0;
			while ((len = is.read(buf)) > 0) {
				fStream.write(buf, 0, len);
			}
			return true;
		} catch (IOException e) {
			CONSOLE_LOGGER.error("error in copying stream");
			DEBUG_FILE_LOGGER.error(e);
		} finally {
			try {
				if (fStream != null) {
					fStream.close();
				}
			} catch (IOException ioe) {
				CONSOLE_LOGGER.error("failed to close the stream");
				DEBUG_FILE_LOGGER.error(ioe);
			}
		}
		return false;
	}

	/**
	 * Method for replacing the token/placeholders of the file
	 * 
	 * @param file,
	 *            where placeholders are to be replaced
	 * @param placeHolders
	 * @return boolean
	 */
	private boolean replacePlaceHolders(final File f, Map<String, String> placeHolders) {
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader input = new BufferedReader(new FileReader(f));
			try {

				String line = null;
				while ((line = input.readLine()) != null) {
					boolean replaced = false;
					if (line.length() > 0) {
						for (String placeHolder : placeHolders.keySet()) {
							if (line.indexOf(placeHolder) != -1) {
								String val = line.replaceAll(placeHolder,
										placeHolders.get(placeHolder));
								builder.append(val);
								replaced = true;
							}
						}
					}
					if (!replaced) {
						builder.append(line);
					}
					builder.append(System.getProperty("line.separator"));
				}
			} finally {
				if (input != null) {
					input.close();
				}
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			try {
				out.write(builder.toString());
			} finally {
				if (out != null) {
					out.close();
				}
			}
		} catch (IOException ex) {
			CONSOLE_LOGGER.error("Failed to replace Placeholders for file " + f);
			DEBUG_FILE_LOGGER.error(ex);
			return false;
		}
		return true;
	}

	/**
	 * Method that ensures existence of directory
	 * 
	 * @param f
	 * @return
	 */
	private boolean ensureDirectoryExists(final File f) {
		return f.exists() || f.mkdir();
	}

	private String append(String... commands) {
		StringBuilder builder = new StringBuilder();
		for (String string : commands) {
			builder.append(string);
		}
		return builder.toString();

	}

	@SuppressWarnings("rawtypes")
	private String getJarContainingFolder(CodeSource codeSource, Class aClass)
			throws URISyntaxException, UnsupportedEncodingException {
		DEBUG_FILE_LOGGER.debug("Getting jar containing folder...");
		File jarFile;
		if (codeSource.getLocation() != null) {
			jarFile = new File(codeSource.getLocation().toURI());
		} else {
			String path = aClass.getResource(aClass.getSimpleName() + ".class").getPath();
			String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
			jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
			jarFile = new File(jarFilePath);
		}
		return jarFile.getParentFile().getAbsolutePath();
	}

}

