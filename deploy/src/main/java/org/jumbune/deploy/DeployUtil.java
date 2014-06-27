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
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
 * 
 * Class for deploying jumbune at specific location
 * 
 */
public final class DeployUtil {
	public static final Logger LOGGER = LogManager.getLogger("EventLogger");

	private static final String JUMBUNE_ENV_VAR = "JUMBUNE_HOME";
	private static final String JAVA_ENV_VAR = "JAVA_HOME";

	private static final Set<String> AGENT_JARS_LIST = new HashSet<String>();
	private static final Set<String> INCLUSIONS = new HashSet<String>();
	private static final String[] FOLDERS = { "tmp/" };
	private static final Scanner SCANNER = new Scanner(System.in);
	private static final String CLEAN_UNUSED_FILES_AND_DIRS = "rm -rf WEB-INF/ skins META-INF/ jsp/ lib/";
	private static final String UPDATE_WAR_FILE = "/modules/jumbune-web-" + Versioning.BUILD_VERSION + Versioning.DISTRIBUTION_NAME
			+ ".war WEB-INF/lib";
	private static final String UPDATE_AGENT_JAR = "/agent-distribution/jumbune-remoting-" + Versioning.BUILD_VERSION 
			+ Versioning.DISTRIBUTION_NAME + "-agent.jar lib/";
	private static final String UPDATE_JAR = "jar -uvf ";
	private static final String USER_DIR = "user.dir";
	public static final String WEB_FOLDER_STRUCTURE = "/WEB-INF/lib/";
	private static final Map<String, String> TOKENS = new HashMap<String, String>();
	private static final String[] ADDITIONAL_HADOOP_JARS_FOR_SHELL = { "commons-lang-*.jar", "commons-configuration-*.jar",
			"jackson-mapper-asl-*.jar", "jackson-core-asl-*.jar" };
	private static final String[] ADDITIONAL_HADOOP_JARS_FOR_WEB = { "/lib/jackson-mapper-asl-*.jar", "/lib/jackson-core-asl-*.jar",
			"/hadoop*core*.jar" };
	private static String namenodeIP = null;
	private static String username;

	private DeployUtil() {
		// hide utility class constructor
	}

	static {
		AGENT_JARS_LIST.add("jumbune-datavalidation");
		AGENT_JARS_LIST.add("log4j-core-");
		AGENT_JARS_LIST.add("log4j-api-");
		AGENT_JARS_LIST.add("jumbune-common-");
		AGENT_JARS_LIST.add("jumbune-utils-");
		AGENT_JARS_LIST.add("gson-");
		AGENT_JARS_LIST.add("commons-logging-");
		AGENT_JARS_LIST.add("commons-configuration-");
		AGENT_JARS_LIST.add("commons-lang-");
		AGENT_JARS_LIST.add("jackson-mapper-asl-");
		AGENT_JARS_LIST.add("jackson-core-asl-");
		AGENT_JARS_LIST.add("jumbune-rumen-");
	}

	static {
		INCLUSIONS.add("lib");
		INCLUSIONS.add("bin");
		INCLUSIONS.add("modules");
		INCLUSIONS.add("resources");
		INCLUSIONS.add("agent-distribution");
		INCLUSIONS.add("examples");
	}

	/**
	 * Main method for extracting the content of deployment jar file
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length != 0) {
			LOGGER.info("Usage: java -jar <jar-name>");
			System.exit(1);
		}
		String javaHomeStr = null;
		Session session = null;
		try {
			Console c = System.console();
			File localFile = new File("");
			localFile.mkdir();
			new File(localFile.getAbsoluteFile() + "/WEB-INF/lib/").mkdirs();
			// checking jdk installation directory
			javaHomeStr = getAndCheckDirectoryExistence(JAVA_ENV_VAR);

			TOKENS.put("<JAVA.HOME>", javaHomeStr);
			// checking hadoop installation directory
			session = validateUserAuthentication(session, c);

			String jumbuneHomeStr = null;
			File jumbuneHome = null;

			// checking jumbune installation directory
			jumbuneHomeStr = getAndCheckDirectoryExistence(JUMBUNE_ENV_VAR);
			jumbuneHome = new File(jumbuneHomeStr);

			if (!ensureDirectoryExists(jumbuneHome)) {
				LOGGER.error("Failed to create " + jumbuneHomeStr, new Exception("Not able to create jumbune home directory."));
			}

			TOKENS.put("<JUMBUNE.HOME>", jumbuneHomeStr);

			File directory = new File(".");
			String currentDir = System.getProperty(USER_DIR) + "/";
			new File(currentDir + "/lib/").mkdirs();
			CodeSource codeSource = DeployUtil.class.getProtectionDomain().getCodeSource();
			File file = new File(codeSource.getLocation().toURI().getPath());
			String jarPath = directory.getCanonicalPath() + "/" + file.getName();
			LOGGER.debug("Jumbune distribution path : " + jarPath);
			URL url = new URL("jar:file:" + jarPath + "!/");
			JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
			LOGGER.info("Extracting Jumbune...");
			copyJarResourcesRecursively(jumbuneHome, jarConnection);
			for (int j = 0; j < FOLDERS.length; j++) {
				final File f = new File(jumbuneHome, FOLDERS[j]);
				LOGGER.debug("Creating directory..." + f.getAbsolutePath());
				if (!DeployUtil.ensureDirectoryExists(f)) {
					throw new IOException("Error occurred while creating : " + f.getAbsolutePath());
				}
			}
			
			doAdditionalTaskDuringDeployment(javaHomeStr, session, jumbuneHomeStr);

			LOGGER.info("!!! Jumbune deployment got completed successfully. !!!");

		} catch (Exception e) {
			LOGGER.error("Error occurred while deploying jumbune.", e);
		} finally {
			session.disconnect();
			cleanup();
		}
	}

	/**
	 * This takes directory from user and checks whether it is exists or not.it also takes directory path if it is set in envrioment variable.
	 * 
	 * @param envriomentVariable
	 *            , the expected environment variable
	 * @return Directory name if it is valid and exist.
	 */
	private static String getAndCheckDirectoryExistence(String envriomentVariable) {
		String directoryPath = null;
		File file = null;
		if (!System.getenv().containsKey((envriomentVariable))) {

			LOGGER.info(envriomentVariable + "  not found as environment variable.");
			if (envriomentVariable.equals(JUMBUNE_ENV_VAR)) {
				LOGGER.info("Please provide the absolute path to a folder where you want to deploy Jumbune");
			}
			directoryPath = readFromReader();
			file = new File(directoryPath);
			while (isNullOrEmpty(directoryPath) || !DeployUtil.ensureDirectoryExists(file)) {

				LOGGER.info("INVALID INPUT : Please provide a valid existing directory!!!");
				directoryPath = readFromReader();
				file = new File(directoryPath);
			}
		} else {
			directoryPath = System.getenv(envriomentVariable);
			file = new File(directoryPath);
			LOGGER.info(envriomentVariable + " Path : " + directoryPath);
		}
		return directoryPath;
	}

	/***
	 * This method does other addition task like fetching hadoop core jars and other required jar from libs of namenode. and adds essential agent
	 * specific jars to distribution of agent.
	 * 
	 * @param javaHomeStr
	 *            , absolute path of java home
	 * @param session
	 *            , jschSession instance
	 * @param jumbuneHomeStr
	 * @param currentDir
	 * @throws JSchException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void doAdditionalTaskDuringDeployment(String javaHomeStr, Session session, String jumbuneHomeStr) throws JSchException,
			IOException, InterruptedException {
		String currentDir = System.getProperty(USER_DIR) + "/";
		new File(currentDir + "/lib/").mkdirs();
		String hadoopHome = null;
		hadoopHome = SessionEstablisher.executeCommandUsingShell(session, SessionEstablisher.ECHO_HADOOP_HOME);
		if (hadoopHome == null || hadoopHome.trim().isEmpty() || !hadoopHome.contains(File.separator)) {
			LOGGER.info("Unable to find HADOOP_HOME on Namenode! Please set HADOOP_HOME environment variable and then run deployment again");
			System.exit(1);
		}
		SessionEstablisher.fetchHadoopJarsFromNamenode(session, username, namenodeIP, hadoopHome, currentDir + WEB_FOLDER_STRUCTURE,
				ADDITIONAL_HADOOP_JARS_FOR_WEB);

		SessionEstablisher.fetchHadoopJarsFromNamenode(session, username, namenodeIP, hadoopHome + "/lib/", currentDir + WEB_FOLDER_STRUCTURE,
				ADDITIONAL_HADOOP_JARS_FOR_SHELL);
		
		String copyJarsToLib = buildCommand("cp -r ", currentDir, "WEB-INF/lib/ ", jumbuneHomeStr, "/");
		String updateJumbuneWar = buildCommand(javaHomeStr, "/bin/", UPDATE_JAR, jumbuneHomeStr, UPDATE_WAR_FILE, "/");
		String updateAgentJar = buildCommand(javaHomeStr, "/bin/", UPDATE_JAR, jumbuneHomeStr, UPDATE_AGENT_JAR);
		String copyHadoopJarsToLib = buildCommand("cp -r ", currentDir, WEB_FOLDER_STRUCTURE, " ", jumbuneHomeStr, "");
		executeCommandCurrentRuntime(updateJumbuneWar);
		executeCommandCurrentRuntime(copyJarsToLib);
		executeCommandCurrentRuntime(copyHadoopJarsToLib);
		executeCommandCurrentRuntime(updateJumbuneWar);
		executeCommandCurrentRuntime(updateAgentJar);
	}

	/**
	 * validate user authentication and ask for username ip of namenode and password.
	 * 
	 * @param session
	 *            {@link Session} established user session using jsch
	 * @param console
	 *            Console for reading password on terminal
	 * @return Session, established session after successfull user authentication.
	 */
	private static Session validateUserAuthentication(final Session session, Console console) throws IOException{
		char[] password;
		String privateKeyPath;
		Session tempSession = session;
		do {
			String masterNode = InetAddress.getLocalHost().getHostAddress();
			LOGGER.info("Please provide IP address of the machine designed to run hadoop namenode daemon ["+masterNode+"]");
			namenodeIP = readFromReader();
			if("".equals(namenodeIP)){
				namenodeIP = masterNode;
			}
			
			String user = System.getProperty("user.name");
			LOGGER.info("Username: ["+user+"]");
			username = readFromReader();
			if("".equals(username)){
				username = user;
			}
			LOGGER.info("Password:");
			password = console.readPassword();
			while("".equals(new String(password))){
				LOGGER.info("Please enter a valid password");
				password = console.readPassword();
			}
			
			String defaultPrivateKeyPath = "/home/"+user+"/.ssh/id_rsa";
			LOGGER.info("Please provide private key file path ["+defaultPrivateKeyPath+"]");
			privateKeyPath = readFromReader();
			if("".equals(privateKeyPath)){
				privateKeyPath = defaultPrivateKeyPath;
			}
			File privateKeyFile = new File(privateKeyPath);
			while (!privateKeyFile.exists() || privateKeyFile.isDirectory()) {
				LOGGER.info("private key file should exist, please provide file path");
				privateKeyPath = readFromReader();
				privateKeyFile = new File(privateKeyPath);
			}
			try {
				tempSession = SessionEstablisher.establishConnection(username, namenodeIP, new String(password), privateKeyPath);
			} catch (Exception e) {
				LOGGER.info("Failed to establish a connection to namenode! Please verify inputs");
			}
		} while (tempSession == null || !tempSession.isConnected());
		return tempSession;
	}

	private static void cleanup() throws IOException, InterruptedException {
		executeCommandCurrentRuntime(CLEAN_UNUSED_FILES_AND_DIRS);
	}

	/**
	 * execute command using system.getRuntime method
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void executeCommandCurrentRuntime(String command) throws IOException, InterruptedException {
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
	public static boolean isNullOrEmpty(String str) {
		if (str == null) {
			return true;
		}

		if (((String) str).trim().length() == 0) {
			return true;
		}

		return false;
	}

	/**
	 * This method is used to read input from buffered reader.
	 * 
	 * @param reader
	 * @return String
	 * @throws JumbuneException
	 */
	private static String readFromReader() {
		String input;
		input = SCANNER.nextLine().trim();
		return input;
	}

	/**
	 * This method copy the content of jar file recursively to a destination directory
	 * 
	 * @param destDir
	 * @param jarConnection
	 * @return boolean
	 * @throws IOException
	 */
	public static boolean copyJarResourcesRecursively(final File destDir, final JarURLConnection jarConnection) throws IOException {

		final JarFile jarFile = jarConnection.getJarFile();

		for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
			final JarEntry entry = e.nextElement();

			if (entry.getName() != null) {

				final String filename = entry.getName();

				for (String str : INCLUSIONS) {

					if ("lib".equals(str) || "modules".equals(str)) {
						includeAdditionalFileInLib(entry, filename, jarFile);
					}
					if (filename.startsWith(str)) {
						copyJarResources(destDir, jarFile, entry, filename);
					}

				}

			}
		}
		return true;
	}

	private static boolean copyJarResources(final File destDir, final JarFile jarFile, final JarEntry entry, final String filename)
			throws IOException {
		final File f = new File(destDir, filename);
		if (!entry.isDirectory()) {
			final InputStream entryInputStream = jarFile.getInputStream(entry);
			if (!DeployUtil.copyStream(entryInputStream, f)) {
				return false;
			}

			if (filename.indexOf("htfconf") != -1 || filename.indexOf(".properties") != -1 || filename.indexOf(".yaml") != -1) {
				LOGGER.debug("Replacing placeholders for " + filename);
				setPlaceHolders(f, TOKENS);
			}
			entryInputStream.close();
		} else {
			if (!DeployUtil.ensureDirectoryExists(f)) {
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
	private static void includeAdditionalFileInLib(JarEntry entry, String filename, JarFile jarFile) throws IOException {

		String fileToBeCopyOnAgent = null;
		String agentDestDir = System.getProperty(USER_DIR) + "/";
		for (String jarName : AGENT_JARS_LIST) {
			int index = filename.indexOf('/');
			fileToBeCopyOnAgent = filename.substring(index + 1, filename.length());
			if (filename.contains(jarName)) {
				File fJar = new File(agentDestDir, "lib/" + fileToBeCopyOnAgent);
				InputStream entryInputStream = jarFile.getInputStream(entry);
				DeployUtil.copyStream(entryInputStream, fJar);
				entryInputStream.close();
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
		try {
			return DeployUtil.copyStream(is, new FileOutputStream(f));

		} catch (FileNotFoundException e) {
			LOGGER.error("file could not be copied.", e);
		}
		return false;
	}

	/**
	 * Method for replacing the token/placeholders of the file
	 * 
	 * @param file
	 *            where placeholders are to be replaced
	 * @param placeHolders
	 * @return boolean
	 */
	private static boolean setPlaceHolders(final File f, Map<String, String> placeHolders) {
		StringBuilder builder = new StringBuilder();
		try {

			BufferedReader input = new BufferedReader(new FileReader(f));
			try {
				String line = input.readLine();
				while (line != null) {
					boolean flag = true;
					if (line.length() > 0) {
						for (String placeHolder : placeHolders.keySet()) {
							if (line.length() > 0 && line.indexOf(placeHolder) != -1) {
								String val = line.replaceAll(placeHolder, placeHolders.get(placeHolder));
								builder.append(val);
								flag = false;
							}
						}
					}

					if (flag) {
						builder.append(line);
					}

					builder.append(System.getProperty("line.separator"));
					line = input.readLine();
				}

			} finally {
				if(input!=null){
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
			LOGGER.error("cant get placeholders", ex);
			return false;
		}
		return true;
	}

	/**
	 * Method for copying content of file
	 * 
	 * @param is
	 *            InputStream to be copied
	 * @param os
	 *            OutputStream In which to file going to be written
	 * @return boolean true if file succesfully copied on stream otherwise return false.
	 */
	private static boolean copyStream(final InputStream is, final OutputStream os) {
		try {
			final byte[] buf = new byte[Constants.ONE_ZERO_TWO_FOUR];

			int len = 0;
			while ((len = is.read(buf)) > 0) {
				os.write(buf, 0, len);
			}
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
			return true;
		} catch (final IOException e) {
			LOGGER.error("error in copying stream", e);
		}
		return false;
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

	private static String buildCommand(String... commands) {
		StringBuilder builder = new StringBuilder();
		for (String string : commands) {
			builder.append(string);
		}
		return builder.toString();

	}
}
