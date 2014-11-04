package org.jumbune.deploy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.Versioning;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/** 
 * Class for deploying Jumbune at specific location
 */
public final class DeployUtil {
	
	public static final Logger LOGGER = LogManager.getLogger("EventLogger");

	private static final Map<String, String> FoundPaths = new HashMap<String, String>(3);
	private static final Set<String> agentJars = new HashSet<String>(12);
	private static final Set<String> subDirs = new HashSet<String>(6);
	
	private static final String[] FOLDERS = { "tmp/" };	
	private static final String[] ADDITIONAL_HADOOP_JARS_FOR_SHELL = {"commons-lang-*.jar", "commons-configuration-*.jar", "jackson-mapper-asl-*.jar", "jackson-core-asl-*.jar" };
	
	
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

	
	private static final Scanner SCANNER = new Scanner(System.in);

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
	
	private DeployUtil() {
		// hiding utility class constructor
	}	
	
	/**
	 * Main method for extracting the content of deployment jar file
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		String distributionType;
		if (args.length != 0) {
			LOGGER.info("Usage: java -jar <jar-name>");
			System.exit(1);
		}
		Properties prop = new Properties();
		prop.load(DeployUtil.class.getClassLoader().getResourceAsStream("distribution.properties"));
		distributionType = prop.getProperty("hadoop-distribution");
		Session session = null;
		try{			
			
			URLConnection jarConnection = performSanity();
			
			session = getSession();
						
			LOGGER.info("Extracting Jumbune...");
			
			extractJarDirectories(new File(FoundPaths.get("<JUMBUNE.HOME>")), jarConnection);
			
			checkJumbuneDirectoryCreation();
			
			Deployer deployer = DeployerFactory.getDeployer(distributionType);
			
			updateJumbuneAndHadoopDistribution(FoundPaths.get("<JAVA.HOME>"), session, FoundPaths.get("<JUMBUNE.HOME>"), deployer,distributionType);
			
			LOGGER.info("!!! Jumbune deployment got completed successfully. !!!");
			
		} catch (Exception e) {
			LOGGER.error("Error occurred while deploying jumbune.", e);
		} finally {
			if(session!=null){
				session.disconnect();
			}
			cleanup();
		}
	}

	private static boolean isExpected(String distributionType) {
		switch(distributionType){
		case "APACHE-NY":
			return true;
		case "APACHE-Y":
			return true;
		case "CDH":
			return true;
		case "HDP":
			return true;
		case "MAPR":
			return true;
		default:
			return false;	
		}
	}

	private static void checkJumbuneDirectoryCreation() {
		for (int j = 0; j < FOLDERS.length; j++) {
			final File f = new File(FoundPaths.get("<JUMBUNE.HOME>"), FOLDERS[j]);
			if (!DeployUtil.ensureDirectoryExists(f)) {
				LOGGER.error("Error occurred while creating: "+ f.getAbsolutePath());
			}
		}
	}
	
	private static Session getSession() throws IOException{
		Console console = System.console();
		return validateUserAuthentication(console);
	}
	
	
	private static URLConnection performSanity() throws IOException, URISyntaxException{
		
			new File("./WEB-INF/lib/").mkdirs();
			String javaHomeStr = null;
			javaHomeStr = getAndCheckDirectoryExistence(JAVA_ENV_VAR);
			FoundPaths.put("<JAVA.HOME>", javaHomeStr);

			String jumbuneHomeStr = null;
			jumbuneHomeStr = getAndCheckDirectoryExistence(JUMBUNE_ENV_VAR);
			File jumbuneHome = new File(jumbuneHomeStr);
			if (!ensureDirectoryExists(jumbuneHome)) {
				LOGGER.warn("Failed to create directory (may be already exist)" + jumbuneHomeStr);
			}
			FoundPaths.put("<JUMBUNE.HOME>", jumbuneHomeStr);

			String currentDirLib = System.getProperty(USER_DIR) + "/"+ "/lib/";
			new File(currentDirLib).mkdirs();

			CodeSource codeSource = DeployUtil.class.getProtectionDomain().getCodeSource();
			File distJarFile = new File(codeSource.getLocation().toURI().getPath());
			String jarPath = new File(".").getCanonicalPath() + "/"+ distJarFile.getName();
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

			LOGGER.info(enviromentVariable + " not set as environment variable.");
			if (enviromentVariable.equals(JUMBUNE_ENV_VAR)) {
				LOGGER.info("Please provide the absolute path to a folder where you want to deploy Jumbune");
			}
			directoryPath = SCANNER.nextLine().trim();
			file = new File(directoryPath);
			while (isNullOrEmpty(directoryPath)	|| !DeployUtil.ensureDirectoryExists(file)) {
				LOGGER.info("INVALID: Please provide a valid existing directory!!!");
				directoryPath = SCANNER.nextLine().trim();
				file = new File(directoryPath);
			}
		} else {
			directoryPath = System.getenv(enviromentVariable);
			file = new File(directoryPath);
			LOGGER.info(enviromentVariable + " linked to :- " + directoryPath);
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
	private static void updateJumbuneAndHadoopDistribution(String javaHomeStr, Session session, String jumbuneHomeStr, Deployer deployer, String distributionType) throws JSchException, IOException, InterruptedException {
		String currentDir = System.getProperty(USER_DIR) + "/";
		String currentLibDir = currentDir + "/lib/";
		new File(currentLibDir).mkdirs();
		String hadoopHome = getHadoopLocation(session);
		if(hadoopHome.endsWith(File.separator)){
			hadoopHome = hadoopHome.substring(0,hadoopHome.length()-1);
		}
		SessionEstablisher.fetchHadoopJarsFromNamenode(session, username, namenodeIP, hadoopHome, currentDir + WEB_FOLDER_STRUCTURE, deployer,distributionType);
		String updateJumbuneWar = append(javaHomeStr, "/bin/", UPDATE_JAR, jumbuneHomeStr, UPDATE_WAR_FILE, "/");
		String updateAgentJar = append(javaHomeStr, "/bin/", UPDATE_JAR, jumbuneHomeStr, UPDATE_AGENT_JAR);
		String copyHadoopJarsToLib = append("cp -r ", currentDir, WEB_FOLDER_STRUCTURE, " ", jumbuneHomeStr, "");
		
		executeCommand(copyHadoopJarsToLib);
		executeCommand(updateJumbuneWar);
		executeCommand(updateAgentJar);
		LOGGER.debug("Updated agent jar and war");
	}

	private static String getHadoopLocation(Session session) throws JSchException, IOException{
		LOGGER.debug("Trying to locate Hadoop with echo $HADOOP_HOME");
		String hadoopHome = SessionEstablisher.executeCommandUsingShell(session, SessionEstablisher.ECHO_HADOOP_HOME,"hadoop");
		LOGGER.debug("Hadoop location with echo $HADOOP_HOME " + hadoopHome);
		if (hadoopHome == null || hadoopHome.trim().isEmpty() || !hadoopHome.contains(File.separator)) {
			String possibleHome;
			LOGGER.debug("Trying to locate Hadoop with whereis hadoop");
			possibleHome = SessionEstablisher.executeCommand(session, SessionEstablisher.WHERE_IS_HADOOP);
			LOGGER.debug("Hadoop location with whereis hadoop" + possibleHome);
			validateHadoopLocation(possibleHome);
			String[] hadoopSplits = possibleHome.split("\\s+");
			LOGGER.debug("Found entries of whereis hadoop:"+ Arrays.toString(hadoopSplits));				
			for(String split: hadoopSplits){
				if(split.contains("/lib/") && containsHadoopLib(split, session)){
					hadoopHome = split;
				}
			}
			if(hadoopHome == null || hadoopHome.trim().isEmpty()){
				//Support in case of mapr is run through VM.
				String llResponse = SessionEstablisher.executeCommandUsingShell(session,SessionEstablisher.LL_COMMAND, "->");
				LOGGER.debug("<ll> command Response"+ llResponse);
				hadoopHome = getHadoopHome(llResponse);
			}
			validateHadoopLocation(hadoopHome);
		}
		hadoopHome = hadoopHome.replace("\n", "");
		LOGGER.info("Hadoop found at location " + hadoopHome);
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
			return llResponse.replaceAll("\\s+","");
		}
		return null;
	}
	
	private static void validateHadoopLocation(String hadoopHome){
		if (hadoopHome == null || hadoopHome.trim().isEmpty()
				|| !hadoopHome.contains(File.separator)) {
			LOGGER.info("Unable to find location of Hadoop! Please make sure Hadoop deployment instruction are followed as recommended, then retry running the deployment.");
			System.exit(1);
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
	private static Session validateUserAuthentication(Console console) throws IOException {
		char[] password;
		String privateKeyPath;
		Session tempSession;
		do {
			String masterNode = InetAddress.getLocalHost().getHostAddress();
			LOGGER.info("Please provide IP address of the machine designed to run hadoop namenode daemon ["+ masterNode + "]");
			namenodeIP = SCANNER.nextLine().trim();
			if ("".equals(namenodeIP)) {
				namenodeIP = masterNode;
			}

			String user = System.getProperty("user.name");
			LOGGER.info("Username: [" + user + "]");
			username = SCANNER.nextLine().trim();
			if ("".equals(username)) {
				username = user;
			}
			LOGGER.info("Password:");
			password = console.readPassword();
			while ("".equals(new String(password))) {
				LOGGER.info("Please enter a valid password");
				password = console.readPassword();
			}

			String defaultPrivateKeyPath = "/home/" + user + "/.ssh/id_rsa";
			LOGGER.info("Please provide private key file path ["+ defaultPrivateKeyPath + "]");
			privateKeyPath = SCANNER.nextLine().trim();
			if ("".equals(privateKeyPath)) {
				privateKeyPath = defaultPrivateKeyPath;
			}
			File privateKeyFile = new File(privateKeyPath);
			while (!privateKeyFile.exists() || privateKeyFile.isDirectory()) {
				LOGGER.info("private key file should exist, please provide file path");
				privateKeyPath = SCANNER.nextLine().trim();
				privateKeyFile = new File(privateKeyPath);
			}
				tempSession = SessionEstablisher.establishConnection(username,
						namenodeIP, new String(password), privateKeyPath); 
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
		final JarFile jarFile = ((JarURLConnection)jarConnection).getJarFile();
		for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
			final JarEntry entry = e.nextElement();
			String filename;
			if ((filename = entry.getName()) != null) {
				for (String subDirName : subDirs) {
					if ("lib".equals(subDirName) || "modules".equals(subDirName)) {
						includeAdditionalFileInLib(entry, filename, jarFile);
					}
					if (filename.startsWith(subDirName)) {
						copyJarResources(destDir, jarFile, entry, filename);
					}
				}
			}
		}
		return true;
	}

	private static boolean copyJarResources(final File destDir, final JarFile jarFile, final JarEntry entry, final String filename) throws IOException {
		final File f = new File(destDir, filename);
		if (!entry.isDirectory()) {
			InputStream entryInputStream = null;
			try {
				entryInputStream = jarFile.getInputStream(entry);
				if (!DeployUtil.copyStream(entryInputStream, f)) {
					return false;
				}

				if (filename.indexOf("htfconf") != -1 || filename.indexOf(".properties") != -1 || filename.indexOf(".yaml") != -1) {
					LOGGER.debug("Replacing placeholders for " + filename);
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
	private static void includeAdditionalFileInLib(JarEntry entry, String filename, JarFile jarFile) throws IOException {
		String fileToBeCopiedOnAgent = null;
		String agentDestDir = System.getProperty(USER_DIR) + "/";
		for (String jarName : agentJars) {
			int index = filename.indexOf('/');
			fileToBeCopiedOnAgent = filename.substring(index + 1, filename.length());
			if (filename.contains(jarName)) {
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
			LOGGER.error("error in copying stream", e);
		} finally {
			try {
				if (fStream != null) {
					fStream.close();
				}
			} catch (IOException ioe) {
				LOGGER.error("failed to close the stream", ioe);
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
			LOGGER.error("Failed to replace Placeholders for file "+f, ex);
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
}	

