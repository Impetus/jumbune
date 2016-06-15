package org.jumbune.deploy;

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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.Versioning;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/** 
 * Class for deploying Jumbune at specific location
 */
public final class DeployUtil {
	
	private static final String USER_NAME = "username";

	private static final String NAMENODE_IP = "namenodeIP";

	private static final String PRIVATE_KEY_PATH = "privatekeypath";

	private static final String PASSWORD = "password";

	private static final String DISTRIBUTION = "distribution";

	private static final String PROPERTY_FILE = "propertyfile";

	private static final Logger DEBUG_LOGGER = LogManager.getLogger(DeployUtil.class);
	
	public static final Logger CONSOLE_LOGGER = LogManager.getLogger("EventLogger");

	private static final Map<String, String> FoundPaths = new HashMap<String, String>(3);
	private static final Set<String> agentJars = new HashSet<String>(12);
	private static final Set<String> subDirs = new HashSet<String>(6);
	private static final Set<String> executableFiles = new HashSet<String>(3);
	
	private static final String[] FOLDERS = { "tmp/" };	
	
	private static final String CLEAN_UNUSED_FILES_AND_DIRS = "rm -rf WEB-INF/ skins META-INF/ jsp/ lib/";
	private static final String UPDATE_WAR_FILE = "/modules/jumbune-web-"+ Versioning.BUILD_VERSION + Versioning.DISTRIBUTION_NAME + ".war WEB-INF/lib";
	private static final String UPDATE_AGENT_JAR = "/agent-distribution/jumbune-remoting-" + Versioning.BUILD_VERSION + Versioning.DISTRIBUTION_NAME + "-agent.jar lib/";
	
	private static final String UPDATE_JAR = "jar -uvf ";
	private static final String USER_DIR = "user.dir";
	private static final String WEB_FOLDER_STRUCTURE = "/WEB-INF/lib/";
	private static final String JUMBUNE_ENV_VAR = "JUMBUNE_HOME";
	private static final String JAVA_ENV_VAR = "JAVA_HOME";
	private static String namenodeIP = null;
	private static String username = null;
	private static int MAX_RETRY_ATTEMPTS = 3;

	
	private static final Scanner SCANNER = new Scanner(System.in);

	private static final String UPDATE_WAR_CLASSES_FILE = "/modules/jumbune-web-"+ Versioning.BUILD_VERSION + Versioning.DISTRIBUTION_NAME + ".war WEB-INF/classes";

	private static final String ROLLING_FILE_APPENDER = "rollingFileAppender";

	static {
		agentJars.add("jumbune-datavalidation");
		agentJars.add("log4j-core-");
		agentJars.add("log4j-api-");
		agentJars.add("jumbune-common-");
		agentJars.add("jumbune-utils-");
		agentJars.add("gson-");
		agentJars.add("commons-logging-");
		agentJars.add("commons-configuration-");
		agentJars.add("commons-lang-");
		agentJars.add("jackson-mapper-asl-");
		agentJars.add("jackson-core-asl-");
		agentJars.add("jumbune-rumen-");
	}

	static {
		subDirs.add("lib");
		subDirs.add("bin");
		subDirs.add("modules");
		subDirs.add("resources");
		subDirs.add("agent-distribution");
		subDirs.add("examples");
	}
	
	static {
		executableFiles.add("/bin/startWeb");
		executableFiles.add("/bin/stopWeb");
		executableFiles.add("/bin/runCli");
	}
	
	private DeployUtil() {
		// hiding utility class constructor
	}
	
	/**
	 * Exit VM
	 * 
	 * @param status can be 0 or 1
	 */
	private static void exitVM(int status) {
		System.exit(status);
	}
	
	/**
	 * Extracts the arguments from configuration file and put in map
	 * 
	 * @param argMap Map containing properties and their values
	 * @throws IOException
	 */
	private static void getArgumentsFromPropertyFile(Map<String, String> argMap) throws IOException {
		if (argMap.containsKey(PROPERTY_FILE)) {
			Properties props = new Properties();
			props.keySet();
			String value = argMap.get(PROPERTY_FILE);
			if (value.startsWith("\"") && value.endsWith("\"")) {
				value = value.substring(1, value.length() - 1);
			}
			FileInputStream in = null;
			try {
				in = new FileInputStream(argMap.get(PROPERTY_FILE));
				props.load(in);
			} catch (IOException e) {
				CONSOLE_LOGGER.error("Unable to read file "+argMap.get("propertyfilepath"));
				DEBUG_LOGGER.error(e);
				exitVM(1);
			} finally {
				if (in != null) {
					in.close();
				}
			}
			for (Object propertyKeyObject : props.keySet() ) {
				String key = (String) propertyKeyObject;
				if (argMap.get(key) == null) {
					argMap.put(key, props.getProperty(key));
				}
			}
		}
	}
	
	/**
	 * Removes double quotes from arguments' value
	 * 
	 * @param argMap map containing arguments and their values
	 */
	private static void removeDoubleQuotes(Map<String, String> argMap) {
		String value = null;
		for (String key: argMap.keySet()) {
			value = argMap.get(key);
			if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
				argMap.put(key, value.substring(1, value.length()-1));
			}
		}
	}
	
	/**
	 * Check if their is any invalid argument or not
	 * 
	 * @param args arguments from command line
	 * @return map containing arguments
	 */
	private static Map<String, String> getAndVerifyArguments(String[] args) {
		List<String> options = new ArrayList<String>();
		options.add("distribution");
		options.add(NAMENODE_IP);
		options.add(USER_NAME);
		options.add("password");
		options.add("privatekeypath");
		options.add("propertyfile");
		
		Map<String, String> argMap = new HashMap<String, String>();
		String option = null;
		String[] temp= null;
		for (String arg:args) {
			try {
				if (arg.startsWith("-D")) {
					temp = arg.substring(2).split("=", 2);
					option = temp[0];
					if (!options.contains(option)) {
						CONSOLE_LOGGER.error("Invalid option "+arg+"\nTry  '--help' for more information.");
						exitVM(1);
					}
					argMap.put(option, temp[1]);
				} else if (arg.equals("-verbose")) {
					turnLoggingLevelToDebug(arg);
				} else {
					CONSOLE_LOGGER.error("Invalid option "+arg+"\nTry  '--help' for more information.");
					exitVM(1);
				}
			} catch (Exception e) {
				CONSOLE_LOGGER.error("Unable to read arguments");
				exitVM(1);
			}
		}
		return argMap;
	}
	
	/**
	 * Prints help and exit
	 */
	private static void printHelp() {
		CONSOLE_LOGGER.info("Usage: java -jar [Jumbune Jar File Name] [-verbose ] [Options]...\n"+
				"    -Dpropertyfile\tProperties file path containing options\n"+
				"    \t\t\te.g. -Dpropertyfile=/home/user/properties.txt\n"+
				"\n    -Ddistribution\tHadoop Distribution Type\n"+
				"    \t\t\te.g. \"-Ddistribution=a\" for Apache,\n\t\t\t \"-Ddistribution=c\" for Cloudera,\n\t\t\t \"-Ddistribution=h\" for HortonWorks,\n\t\t\t \"-Ddistribution=m\" for MapR\n"+
				"\n    -DnamenodeIP\tIP address of the namenode machine\n"+
				"    \t\t\t(e.g. DnamenodeIP=127.0.0.1)\n"+
				"\n    -Dusername\t\tUsername of the namenode machine\n"+
				"    \t\t\t(e.g. -Dusername=user)\n"+
				"\n    -Dpassword\t\tEncrypted password of the namenode machine\n"+
				"    \t\t\t(e.g. -Dpassword=imt2s23wgs)\n"+
				"\n    -Dprivatekeypath\tPrivate key file path\n"+
				"    \t\t\t(e.g. -Dprivatekeypath=/home/user/.ssh/id_rsa)\n"+
				"\n    --encryption\tUse this option to run password encryption utility\n"+
				"\n    --help\t\tdisplay this help and exit");
		exitVM(0);
	}
	
	/**
	 * Run password encryption utility
	 */
	private static void runEncryptionUtillity() {
		CONSOLE_LOGGER.info("Enter your password");	
		Console console = System.console();
		char[] password = console.readPassword();
		while ("".equals(new String(password))) {
			CONSOLE_LOGGER.info("Please enter a valid password");
			password = console.readPassword();
		}
		CONSOLE_LOGGER.info("Your encrypted password is\n"+StringUtil.getEncrypted(new String(password)));
		exitVM(0);
	}
	
	private static void turnLoggingLevelToDebug(String verboseMode) {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(ROLLING_FILE_APPENDER);
		loggerConfig.setLevel(Level.DEBUG);
		ctx.updateLoggers();
		CONSOLE_LOGGER.info("logging level changed to [DEBUG]");
		CONSOLE_LOGGER.info("Further details can be found in log file");
	}
	
	/**
	 * Main method for extracting the content of deployment jar file
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		String distributionType;
		String hadoopDistributionType;
	
		if (args.length != 0 && args[0].equalsIgnoreCase("--help")) {
			printHelp();
		}
		if (args.length !=0 && args[0].equalsIgnoreCase("--encryption")) {
			runEncryptionUtillity();
		}
		Map<String, String> argMap = getAndVerifyArguments(args);
		getArgumentsFromPropertyFile(argMap);
		removeDoubleQuotes(argMap);
		if (argMap.get(PRIVATE_KEY_PATH) != null) {
			File privateKeyFile = new File(argMap.get(PRIVATE_KEY_PATH));
			if (!privateKeyFile.exists() || privateKeyFile.isDirectory()) {
				CONSOLE_LOGGER.error("Invalid private key file path ["+privateKeyFile+"]");
				exitVM(1);
			}
		}
		
		Properties prop = new Properties();
		prop.load(DeployUtil.class.getClassLoader().getResourceAsStream("distribution.properties"));
		distributionType = prop.getProperty("hadoop-distribution");
		Session session = null;
		try{			
			CONSOLE_LOGGER.info("--Jumbune built for ["+distributionType+" based Hadoop] distributions--");
			URLConnection jarConnection = performSanity();
			
			hadoopDistributionType = argMap.get(DISTRIBUTION);
			
			if (hadoopDistributionType != null) {
				hadoopDistributionType = hadoopDistributionType.substring(0, 1).toLowerCase();
				if (distributionType.equalsIgnoreCase("Non-Yarn")
					&& !hadoopDistributionType.startsWith("a")
					&& !hadoopDistributionType.startsWith("m")) {
						CONSOLE_LOGGER.error("Invalid: Hadoop distribution ["+hadoopDistributionType
								+"] passed during deploy. Available are : (a)Apache | (m)MapR");
						exitVM(1);
				} else if(!hadoopDistributionType.startsWith("a")
					&& !hadoopDistributionType.startsWith("h")
					&& !hadoopDistributionType.startsWith("c")) {
					CONSOLE_LOGGER.error("Invalid: Hadoop distribution ["+hadoopDistributionType
								+"] passed during deploy. Available are : (a)Apache | (c)Cloudera | (h)HortonWorks");
					exitVM(1);
				}
			
			} else {
			
				if (distributionType.equalsIgnoreCase("Non-Yarn")) {
					CONSOLE_LOGGER.info("Choose the Hadoop Distribution Type : (a)Apache | (m)MapR");
					hadoopDistributionType = SCANNER.nextLine().trim();
					while (hadoopDistributionType.isEmpty()
							|| (!hadoopDistributionType.equalsIgnoreCase("a")
							&& !hadoopDistributionType.equalsIgnoreCase("m"))) {
						CONSOLE_LOGGER.info("Invalid input! Choose from the given Hadoop Distribution Type : (a)Apache | (m)MapR");
						hadoopDistributionType = SCANNER.nextLine().trim();
					}
				} else {
					CONSOLE_LOGGER.info("Choose the Hadoop Distribution Type : (a)Apache | (c)Cloudera | (h)HortonWorks");
					hadoopDistributionType = SCANNER.nextLine().trim();
					while (hadoopDistributionType.isEmpty()
							|| (!hadoopDistributionType.equalsIgnoreCase("a")
							&& !hadoopDistributionType.equalsIgnoreCase("h") && !hadoopDistributionType.equalsIgnoreCase("c"))) {
						CONSOLE_LOGGER.info("Invalid input! Choose from the given Hadoop Distribution Type : (a)Apache | (c)Cloudera | (h)HortonWorks");
						hadoopDistributionType = SCANNER.nextLine().trim();
					}
				}
				
			}
			
			session = getSession(argMap);
			
			CONSOLE_LOGGER.info("Extracting Jumbune...");
			
			extractJarDirectories(new File(FoundPaths.get("<JUMBUNE.HOME>")), jarConnection);
			
			checkJumbuneDirectoryCreation();
			
			createConfigurationFile(distributionType,hadoopDistributionType);
			
			changeRunnablePermissions();
			
			serializeDistributionType(distributionType, hadoopDistributionType);
			
			updateJumbuneAndHadoopDistribution(FoundPaths.get("<JAVA.HOME>"), session, FoundPaths.get("<JUMBUNE.HOME>"), hadoopDistributionType, distributionType);
			
			CONSOLE_LOGGER.info("!!! Jumbune successfully deployed at ["+FoundPaths.get("<JUMBUNE.HOME>")+"] !!!");
			
		} catch (Exception e) {
			CONSOLE_LOGGER.error("Error occurred while deploying jumbune.", e);
		} finally {
			if (session != null){
				session.disconnect();
			}
			cleanup();
		}
		
	}

	private static void serializeDistributionType(String distributionType, String HadoopDistribution) throws IOException{
		FileWriter writer = new FileWriter(new File("./WEB-INF/classes/distributionInfo.properties"));
		writer.write("DistributionType="+distributionType);
		writer.write("\n");
		writer.write("HadoopDistribution="+HadoopDistribution);
		writer.close();
	}
	
	
	// Create configuration file and folder in Jumbune Home while deploying
	private static void createConfigurationFile(String hadoopType,String hadoopDistribution) throws IOException{
		String configurationpath;
		FileWriter writer =null;
		try{
		configurationpath = FoundPaths.get("<JUMBUNE.HOME>")+File.separator+Constants.CONFIGURATION;
		File configuration = new File(configurationpath);
		if(!configuration.exists()){
			configuration.mkdirs();
		}
		File clusterfile = new File (configurationpath+File.separator+Constants.CLUSTER_INFO);
		
		if (clusterfile.exists()){
			clusterfile.delete();
		}
		writer = new FileWriter(clusterfile);
		writer.write("HadoopType="+hadoopType);
		writer.write("\n");
		writer.write("HadoopDistribution="+hadoopDistribution);
		}catch(IOException e){
			CONSOLE_LOGGER.error("IO Exception",e);
		}
		finally{
			if(writer!=null){
				writer.close();	
			}
		}
	}

	private static void checkJumbuneDirectoryCreation() {
		for (int j = 0; j < FOLDERS.length; j++) {
			final File f = new File(FoundPaths.get("<JUMBUNE.HOME>"), FOLDERS[j]);
			if (!DeployUtil.ensureDirectoryExists(f)) {
				CONSOLE_LOGGER.error("Error occurred while creating: "+ f.getAbsolutePath());
			}
		}
	}
	
	private static void changeRunnablePermissions(){
		File f;
		for(String file : executableFiles){
			f = new File(FoundPaths.get("<JUMBUNE.HOME>")+file);
			f.setExecutable(true);
		}
	}
	
	private static Session getSession(Map<String, String> argMap) throws IOException{
		Console console = System.console();
		return validateUserAuthentication(console, argMap);
	}
	
	
	private static URLConnection performSanity() throws IOException, URISyntaxException{
			new File("./WEB-INF/classes/").mkdirs();
			new File("./WEB-INF/lib/").mkdirs();
			String javaHomeStr = null;
			javaHomeStr = getAndCheckDirectoryExistence(JAVA_ENV_VAR);
			FoundPaths.put("<JAVA.HOME>", javaHomeStr);

			String jumbuneHomeStr = null;
			jumbuneHomeStr = getAndCheckDirectoryExistence(JUMBUNE_ENV_VAR);
			File jumbuneHome = new File(jumbuneHomeStr);
			if (!ensureDirectoryExists(jumbuneHome)) {
				CONSOLE_LOGGER.warn("Failed to create directory (may be already exist)" + jumbuneHomeStr);
			}
			FoundPaths.put("<JUMBUNE.HOME>", jumbuneHomeStr);

			String currentDirLib = System.getProperty(USER_DIR) + "/"+ "/lib/";
			new File(currentDirLib).mkdirs();

			CodeSource codeSource = DeployUtil.class.getProtectionDomain().getCodeSource();
			File distJarFile = new File(codeSource.getLocation().toURI().getPath());
			String path = getJarContainingFolder(codeSource, DeployUtil.class);
			String jarPath = path + "/"+ distJarFile.getName();
			URL url = new URL("jar:file:" + jarPath + "!/");
			JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
			return jarConnection;
	}

	/**
	 * This takes directory from user and checks whether it is exists or not.it also takes directory path if it is set in environment variable.
	 * 
	 * @param enviromentVariable
	 *            , the expected environment variable
	 * @return Directory name if it is valid and exist.
	 */
	private static String getAndCheckDirectoryExistence(String enviromentVariable) {
		String directoryPath = null;
		File file = null;
		if (!System.getenv().containsKey((enviromentVariable))) {

			CONSOLE_LOGGER.info(enviromentVariable + " not set as environment variable.");
			if (enviromentVariable.equals(JUMBUNE_ENV_VAR)) {
				CONSOLE_LOGGER.info("Please provide the absolute path to a folder where you want to deploy Jumbune");
			}
			directoryPath = SCANNER.nextLine().trim();
			file = new File(directoryPath);
			while (isNullOrEmpty(directoryPath)	|| !DeployUtil.ensureDirectoryExists(file)) {
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
	 * This method does other addition task like fetching hadoop core jars and other required jar from libs of namenode. and adds essential agent specific jars to distribution of agent.
	 * 
	 * @param javaHomeStr, absolute path of java home
	 * @param session, jschSession instance
	 * @param jumbuneHomeStr
	 * @param deployer 
	 * @param distributionType 
	 * @param currentDir
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void updateJumbuneAndHadoopDistribution(String javaHomeStr, Session session,
			String jumbuneHomeStr, String hadoopDistributionType, String distributionType)
					throws JSchException, IOException, InterruptedException {
		String currentDir = System.getProperty(USER_DIR) + "/";
		String currentLibDir = currentDir + "/lib/";
		new File(currentLibDir).mkdirs();
		String hadoopHome = getHadoopLocation(session,hadoopDistributionType);
		if(hadoopHome.endsWith(File.separator)){
			hadoopHome = hadoopHome.substring(0,hadoopHome.length()-1);
		}
		try {
			SessionEstablisher.fetchHadoopJarsFromNamenode(session, username, namenodeIP, hadoopHome, currentDir + WEB_FOLDER_STRUCTURE, hadoopDistributionType,distributionType);
		} catch(java.lang.ArrayIndexOutOfBoundsException e) {
			CONSOLE_LOGGER.error("Invalid: Hadoop distribution ["+hadoopDistributionType+"] passed during deploy doesn't match with the deployed distribution of Hadoop");
			exitVM(1);
		}
		String updateJumbuneWar = append(javaHomeStr, "/bin/", UPDATE_JAR, jumbuneHomeStr, UPDATE_WAR_FILE, "/");
		String updateJumbuneWarClasses = append(javaHomeStr, "/bin/", UPDATE_JAR, jumbuneHomeStr, UPDATE_WAR_CLASSES_FILE, "/");		
		String updateAgentJar = append(javaHomeStr, "/bin/", UPDATE_JAR, jumbuneHomeStr, UPDATE_AGENT_JAR);
		String copyHadoopJarsToLib = append("cp -r ", currentDir, WEB_FOLDER_STRUCTURE, " ", FoundPaths.get("<JUMBUNE.HOME>"), Path.SEPARATOR, " ");
		executeCommand(copyHadoopJarsToLib);
		executeCommand(updateJumbuneWar);
		executeCommand(updateJumbuneWarClasses);
		executeCommand(updateAgentJar);
		DEBUG_LOGGER.debug("Updated agent jar and war");
	}

	private static String getHadoopLocation(Session session,String hadoopDistributionType) throws JSchException, IOException{
		DEBUG_LOGGER.debug("Trying to locate Hadoop with echo $HADOOP_HOME");
		String hadoopHome = SessionEstablisher.getHadoopHome(session, SessionEstablisher.ECHO_HADOOP_HOME, "echo $HADOOP_HOME");
		DEBUG_LOGGER.debug("Hadoop location with echo $HADOOP_HOME [" + hadoopHome + "]");
		if (hadoopHome == null || hadoopHome.trim().isEmpty() || !hadoopHome.contains(File.separator)) {
			String possibleHome;
			DEBUG_LOGGER.debug("Trying to locate Hadoop with whereis hadoop");
			possibleHome = SessionEstablisher.executeCommand(session, SessionEstablisher.WHERE_IS_HADOOP);
			DEBUG_LOGGER.debug("Hadoop location with whereis hadoop" + possibleHome);
			validateHadoopLocation(possibleHome);
			String[] hadoopSplits = possibleHome.split("\\s+");
			DEBUG_LOGGER.debug("Found entries of whereis hadoop:"+ Arrays.toString(hadoopSplits));				
			for(String split: hadoopSplits){
				if(split.contains("/lib/") && containsHadoopLib(split, session)){
					hadoopHome = split;
				}
			}
			if((hadoopHome == null || hadoopHome.trim().isEmpty()) && hadoopDistributionType.equalsIgnoreCase("m")){
				//Support in case of mapr is run through VM.
				String llResponse = SessionEstablisher.getHadoopHome(session,SessionEstablisher.LL_COMMAND, "->");
				DEBUG_LOGGER.debug("<ll> command Response"+ llResponse);
				hadoopHome = getHadoopHome(llResponse);
			}
			validateHadoopLocation(hadoopHome);
		}
		hadoopHome = hadoopHome.replace("\n", "");
		CONSOLE_LOGGER.info("Using Hadoop: [" + hadoopHome+"]");
		return hadoopHome;
	}

	/**
	 * Gets the hadoop home in case where HADOOP_HOME is not set and hadoop lib is not installed.
	 *
	 * @param llResponse the ll response
	 * @return the hadoop home
	 */
	private static String getHadoopHome(String llResponse) {
		if(llResponse!=null){
			llResponse = llResponse.substring((llResponse.indexOf(">")+1), llResponse.length());
			llResponse = llResponse.substring(0,llResponse.indexOf("bin")-1);
			return llResponse.replaceAll("\u001B\\[01;32m","");
		}
		return null;
	}
	
	private static void validateHadoopLocation(String hadoopHome){
		if (hadoopHome == null || hadoopHome.trim().isEmpty()
				|| !hadoopHome.contains(File.separator)) {
			CONSOLE_LOGGER.info("Unable to find location of Hadoop! Please make"
					+ " sure Hadoop deployment instruction are followed as recommended,"
					+ " then retry running the deployment.");
			exitVM(1);
		}
	}
	
	private static boolean containsHadoopLib(String location, Session session)
			throws JSchException, IOException {
		boolean result = false;
		String listedDirectory = SessionEstablisher.executeCommand(session,
				SessionEstablisher.LS_PREFIX_PART + location
						+ SessionEstablisher.LS_POSTFIX_PART);
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
	 */
	private static Session validateUserAuthentication(Console console, Map<String, String> argMap) throws IOException {
		char[] password = null;
		String privateKeyPath;
		Session tempSession = null;
		boolean sysexit;
		boolean verified = false;
		int passwordRetryAttempts = 0;
		int passwdRetryAttempLeft = 0;
		int maxPasswdRetryAttempts = 3;
		int retryAttempts = 0;
		CONSOLE_LOGGER.info("\r\nJumbune needs to calibrate itself according to the installed Hadoop distribution, please provide details about hadoop namenode machine");
		do {
			sysexit = false;
			String masterNode = InetAddress.getLocalHost().getHostAddress();
			
			namenodeIP = argMap.get(NAMENODE_IP);
			if (namenodeIP  == null) {
				CONSOLE_LOGGER.info("\r\nIP address of the namenode machine ["+ masterNode + "]");
				namenodeIP = SCANNER.nextLine().trim();
				if ("".equals(namenodeIP)) {
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
				if ("".equals(username)) {
					username = user;
				}
			} else {
				sysexit = true;
			}
			String isPasswordlessSSH = null;
			privateKeyPath = argMap.get(PRIVATE_KEY_PATH);
			
			if (argMap.get(PASSWORD) == null && privateKeyPath == null) {
				CONSOLE_LOGGER.info("Do we have passwordless SSH between ["+masterNode+"] - ["+namenodeIP+"] machines? (y)/(n)");
				isPasswordlessSSH = SCANNER.nextLine().trim();
				while(!"y".equalsIgnoreCase(isPasswordlessSSH)&&!"n".equalsIgnoreCase(isPasswordlessSSH)){
					CONSOLE_LOGGER.info("Do we have passwordless SSH between ["+masterNode+"] - ["+namenodeIP+"] machines? (y)/(n)");
					isPasswordlessSSH = SCANNER.nextLine().trim();
				}
				if("n".equalsIgnoreCase(isPasswordlessSSH)){
					do {
						CONSOLE_LOGGER
								.info("Password of the namenode machine:");
						if (console != null) {
							password = console.readPassword();
						}
						while ("".equals(new String(password))) {
							CONSOLE_LOGGER
									.info("Please enter a valid password");
							if (console != null) {
								password = console.readPassword();
							}
						}
						if (password != null) {
							DEBUG_LOGGER.debug("Authenticating username["
									+ username + "], namenodeIP[" + namenodeIP
									+ "], with password based authentication");
							tempSession = SessionEstablisher
									.establishConnection(username, namenodeIP,
											new String(password), null);
						}
						if (!tempSession.isConnected()) {
							passwordRetryAttempts = passwordRetryAttempts + 1;
							passwdRetryAttempLeft = maxPasswdRetryAttempts
									- passwordRetryAttempts;
							if (passwdRetryAttempLeft == 0) {
								verified = true;
								CONSOLE_LOGGER
										.info("Max attempts of password verification has been reached hence exiting");
								exitVM(1);
							} else {
								CONSOLE_LOGGER
										.info("Password verification failed for user ["
												+ user
												+ "] , total number of attempts left ["
												+ passwdRetryAttempLeft + "]");
							}
						}

						if (tempSession != null && tempSession.isConnected()) {
							DEBUG_LOGGER.debug("Session Established");
							verified = true;
						}

					} while (!verified);
				}
			} else if (privateKeyPath != null) {
					password = null;
					sysexit = true;
			} else {
				password = StringUtil.getPlain(argMap.get(PASSWORD)).toCharArray();
				sysexit = true;
			}
			
			
			if (privateKeyPath == null && password==null) {
				String defaultPrivateKeyPath = "/home/" + user + "/.ssh/id_rsa";
				CONSOLE_LOGGER.info("Please provide private key file path ["+ defaultPrivateKeyPath + "]");
				privateKeyPath = SCANNER.nextLine().trim();
				if ("".equals(privateKeyPath)) {
					privateKeyPath = defaultPrivateKeyPath;
				}
				File privateKeyFile = new File(privateKeyPath);
				while (!privateKeyFile.exists() || privateKeyFile.isDirectory()) {
					CONSOLE_LOGGER.info("private key file should exist, please provide file path");
					privateKeyPath = SCANNER.nextLine().trim();
					privateKeyFile = new File(privateKeyPath);
				}
				DEBUG_LOGGER.debug("Authenticating username[" + username + "], namenodeIP[" + namenodeIP + "], and privatekeyPath[" + privateKeyPath + "]");
				tempSession = SessionEstablisher.establishConnection(username,
						namenodeIP, null, privateKeyPath);
			} else {
				sysexit = true;
			}
			
			if (tempSession!=null && tempSession.isConnected()) {
				DEBUG_LOGGER.debug("Session Established");
			}			
			
			if (sysexit == true && (tempSession == null || !tempSession.isConnected())) {
				CONSOLE_LOGGER.error("Failed to authenticate, check username and password");
				exitVM(1);
			}
		} while (tempSession == null || !tempSession.isConnected());
		return tempSession;
	}

	private static void cleanup() throws IOException, InterruptedException {
		executeCommand(CLEAN_UNUSED_FILES_AND_DIRS);
	}

	/**
	 * execute command using system.getRuntime method
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void executeCommand(String command) throws IOException, InterruptedException {
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

	/**
	 * This method copy the content of jar file recursively to a destination
	 * directory
	 * 
	 * @param destDir
	 * @param jarConnection
	 * @return boolean
	 * @throws IOException
	 */
	public static boolean extractJarDirectories(final File destDir, final URLConnection jarConnection) throws IOException {
		DEBUG_LOGGER.debug("Extracting jar directories to [" + destDir + "] location");
		final JarFile jarFile = ((JarURLConnection)jarConnection).getJarFile();
		for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
			final JarEntry entry = e.nextElement();
			String filename;
			if ((filename = entry.getName()) != null) {
					if (filename.startsWith("lib") || filename.startsWith("modules")) {
						includeAdditionalFileInLib(entry, jarFile);
					}
					String dirSplit = filename.split(File.separator)[0];
					if(subDirs.contains(dirSplit)){
						copyJarResources(destDir, jarFile, entry);
					}
			}
		}
		return true;
	}

	private static boolean copyJarResources(final File destDir, final JarFile jarFile, final JarEntry entry) throws IOException {
		String filename = entry.getName();
		final File f = new File(destDir, filename);
		if (!entry.isDirectory()) {
			InputStream entryInputStream = null;
			try {
				entryInputStream = jarFile.getInputStream(entry);
				if (!DeployUtil.copyStream(entryInputStream, f)) {
					return false;
				}

				if (filename.indexOf("htfconf") != -1 || filename.indexOf(".properties") != -1 || filename.indexOf(".yaml") != -1) {
					DEBUG_LOGGER.debug("Replacing placeholders for [" + filename + "]");
					replacePlaceHolders(f, FoundPaths);
				}
			} finally {
				if (entryInputStream != null) {
					entryInputStream.close();
				}
			}

		} else {
			if (!DeployUtil.ensureDirectoryExists(f)) {
				throw new IOException("Error occurred while extracting : "+ f.getAbsolutePath());
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
	private static void includeAdditionalFileInLib(JarEntry entry, JarFile jarFile) throws IOException {
		String fileToBeCopiedOnAgent = null;
		String filename = entry.getName();
		String agentDestDir = System.getProperty(USER_DIR) + "/";
		for (String jarName : agentJars) {
			int index = filename.indexOf('/');
			fileToBeCopiedOnAgent = filename.substring(index + 1, filename.length());
			if (filename.contains(jarName)) {
				DEBUG_LOGGER.debug("Copied ["+fileToBeCopiedOnAgent+"]  to Agent distribution");
				File fJar = new File(agentDestDir, "lib/" + fileToBeCopiedOnAgent);
				InputStream entryInputStream = null;
				try {
					entryInputStream = jarFile.getInputStream(entry);
					DeployUtil.copyStream(entryInputStream, fJar);
				} finally {
					if(entryInputStream!=null){
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
	private static boolean copyStream(final InputStream is, final File f) {
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
			CONSOLE_LOGGER.error("error in copying stream", e);
		} finally {
			try {
				if (fStream != null) {
					fStream.close();
				}
			} catch (IOException ioe) {
				CONSOLE_LOGGER.error("failed to close the stream", ioe);
			}
		}
		return false;
	}

	/**
	 * Method for replacing the token/placeholders of the file
	 * @param file, where placeholders are to be replaced
	 * @param placeHolders
	 * @return boolean
	 */
	private static boolean replacePlaceHolders(final File f, Map<String, String> placeHolders) {
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader input = new BufferedReader(new FileReader(f));
			try {
				
				String line = null;
				while ((line = input.readLine())!= null) {
					boolean replaced = false;
					if (line.length() > 0) {
						for (String placeHolder : placeHolders.keySet()) {
							if (line.indexOf(placeHolder) != -1) {
								String val = line.replaceAll(placeHolder, placeHolders.get(placeHolder));
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
			CONSOLE_LOGGER.error("Failed to replace Placeholders for file "+f, ex);
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
	private static boolean ensureDirectoryExists(final File f) {
		return f.exists() || f.mkdir();
	}

	private static String append(String... commands) {
		StringBuilder builder = new StringBuilder();
		for (String string : commands) {
			builder.append(string);
		}
		return builder.toString();

	}

	private static String getJarContainingFolder(CodeSource codeSource, Class aClass)
			throws URISyntaxException, UnsupportedEncodingException {
		DEBUG_LOGGER.debug("Getting jar containing folder...");
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