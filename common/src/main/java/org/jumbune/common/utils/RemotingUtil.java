package org.jumbune.common.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.SupportedApacheHadoopVersions;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.ApiInvokeHintsEnum;
import org.jumbune.remoting.common.BasicYamlConfig;
import org.jumbune.utils.beans.VirtualFileSystem;



/***
 * This class provide  utility methods for remoting operation from jumbune.
 * 
 *
 */
public final class RemotingUtil {
	
	private static final String CLOUDERA_HADOOP_REGEX= "(^[hH]adoop)(\\s)* [0-9.]*-[c-h0-9.]*";
	
	
	/** The Constant hadoopVersionRegex. */
	private static final String HADOOP_VERSION_REGEX = "(^[hH]adoop)(\\s)* [0-9.]*";
	
	/** The hadoop version. */
	private static SupportedApacheHadoopVersions hadoopVersion;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(RemotingUtil.class);
	
	/** The Constant MAKE_JOBJARS_DIR_ON_AGENT. */
	private static final String MAKE_JOBJARS_DIR_ON_AGENT = "mkdir -p AGENT_HOME/jobJars/";
	
	private static final String YAML_FILE = "/yamlInfo.ser";
	/**
	 * Instantiates a new remoting util.
	 */
	private RemotingUtil(){
		
	}

	/**
	 * Gets the remoter.
	 *
	 * @param loader the loader
	 * @param receiveDirectory the receive directory
	 * @return the remoter
	 */
	public static Remoter getRemoter(YamlLoader loader, final String receiveDirectory) {
		String receivedDirectory = receiveDirectory;
		Master master = loader.getMasterInfo();
		String masterHost = master.getHost();
		int agentPort = Integer.valueOf(master.getAgentPort());
		if (receivedDirectory == null || receivedDirectory.trim().equals("")){
			receivedDirectory = System.getenv("JUMBUNE_HOME");}
		return new Remoter(masterHost, agentPort);
	}

	/**
	 * Gets the remoter.
	 *
	 * @param config the config
	 * @param receiveDirectory the receive directory
	 * @return the remoter
	 */
	public static Remoter getRemoter(YamlConfig config, String receiveDirectory) {
		Master master = config.getMaster();
		return new Remoter(master.getHost(), Integer.valueOf(master.getAgentPort()));
		
	}

	/**
	 * Gets the hadoop home.
	 *
	 * @param remoter the remoter
	 * @param config the config
	 * @return the hadoop home
	 */
	@Deprecated
	public static String getHadoopHome(Remoter remoter, YamlConfig config) {
		String command = "echo $HADOOP_HOME  \n \n";
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null).populate(config, null);
		
		return (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
	}

	/**
	 * Gets the hadoop home.
	 *
	 * @param config the config
	 * @return the hadoop home
	 */
	public static String getHadoopHome(YamlConfig config) {
		Remoter remoter = getRemoter(config, "");
		
		String command = "echo $HADOOP_HOME  \n \n";
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null).populate(config, null);
		
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		return response;
		
	}

	/**
	 * Gets the agent home.
	 * 
	 * @param config
	 *            the config
	 * @return the agent home
	 */
	public static String getAgentHome(YamlConfig config) {

		String agentHome = null;
		if (config != null) {
			Master master = config.getMaster();
			Remoter remoter = new Remoter(master.getHost(), Integer.valueOf(master.getAgentPort()));
			
			CommandWritableBuilder builder = new CommandWritableBuilder();
			builder.addCommand(Constants.ECHO_AGENT_HOME, false, null).populate(config, null);
			
			agentHome =  (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
			remoter.close();
		}
		if (agentHome == null || "".equals(agentHome.trim())) {
			throw new IllegalArgumentException("Agent home found null or empty!!!");
			
		}
		return agentHome;
	}

	/**
	 * Gets the job client.
	 *
	 * @param loader the loader
	 * @return the job client
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static JobClient getJobClient(YamlLoader loader) throws IOException {
		String jobTrackerURI = RemotingUtil.getHadoopConfigurationValue(loader, "mapred-site.xml", "mapred.job.tracker");
		JobClient client = null;
		Configuration config = new Configuration();
		config.set(loader.getYamlConfiguration().getMaster().getUser(), "");
		try {
			client = new JobClient(new InetSocketAddress(jobTrackerURI.split(":")[0], Integer.parseInt(jobTrackerURI.split(":")[1])), config);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return client;
	}

	/**
	 * *
	 * This method fireCommand and get String response to user using remoting.
	 *
	 * @param yamlConfig the yaml config
	 * @param command the command
	 * @return the string
	 */
	
	public static String fireCommandOnSupporteHadoopVersionAndGetStringResponse(YamlConfig yamlConfig, String command) {
		String hadoopHome = RemotingUtil.getHadoopHome(yamlConfig);
		Remoter remoter = getRemoter(yamlConfig, " ");
		Master master = yamlConfig.getMaster();

		String hadoopDir = fireWhereIsHadoopCommand(remoter, master, yamlConfig);
		List<String> host = new ArrayList<String>();
		host.add(master.getHost());
		String commandToExecute = null;
		if(hadoopDir != null){
			commandToExecute = hadoopDir+" "+command;
		}else{
			commandToExecute = hadoopHome+"/bin/hadoop  "+command;
		}
			
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(commandToExecute, false, null).populate(yamlConfig, null);
		
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		return response;
	}

	/**
	 * this method fires whereis hadoop command to get hadoop location
	 * @param remoter
	 * @param master
	 * @param yamlConfig
	 * @return
	 */
	public static String fireWhereIsHadoopCommand(Remoter remoter, Master master, YamlConfig yamlConfig) {
		String hadoopDir = null;
		String command = "whereis hadoop  ";
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null).populate(yamlConfig, null);
		
		String wherIsHadoopResponse=(String)remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		if(2<wherIsHadoopResponse.split(" ").length){
			hadoopDir = wherIsHadoopResponse.split(" ")[1];
		}
		return hadoopDir;
	}
	

	/**
	 * *
	 * This method retrieves out a Hadoop configuration parameters value. User has to specify appropriate Hadoop configuration file and configuration
	 * name. This method assumes that given configuration file is present in HADOOP_HOME/conf directory.
	 *
	 * @param loader the loader
	 * @param hadoopConfigurationFile configuration file in Hadoop configuration directory
	 * @param configurationToGet Hadoop configuration parameter.
	 * @return configuration value.
	 */
	public static String getHadoopConfigurationValue(YamlLoader loader, String hadoopConfigurationFile, String configurationToGet) {
		String destinationReceiveDir = copyAndGetHadoopConfigurationFilePath(loader, hadoopConfigurationFile);
		return parseConfiguration(destinationReceiveDir + "/" + hadoopConfigurationFile, configurationToGet);
	}
		

	/**
	 * Parses the configuration.
	 *
	 * @param destinationRelativePathOnLocal the destination relative path on local
	 * @param configurationToGet the configuration to get
	 * @return the string
	 */
	public static String parseConfiguration(String destinationRelativePathOnLocal, String configurationToGet) {
		Configuration conf = new Configuration();
		LOGGER.debug("Parsed configuration file path [" + destinationRelativePathOnLocal+"]");
		conf.addResource(new Path(destinationRelativePathOnLocal));
		return conf.get(configurationToGet);
	}

	/**
	 * *
	 * This method returns the absoluate path of a given configuration file on the remote HADOOP machine. This expects that the given configuration
	 * file name to be correct and the file resides in <HADOOP_HOME>/conf directory.
	 *
	 * @param loader the loader
	 * @param hadoopConfigurationFile which we wants to receive the path of.
	 * @return the string
	 */
	public static String copyAndGetHadoopConfigurationFilePath(YamlLoader loader, String hadoopConfigurationFile) {

		String jumbuneHome = YamlLoader.getjHome();
		String dirInJumbuneHome = jumbuneHome + File.separator + Constants.JOB_JARS_LOC + loader.getJumbuneJobName();
		File dir = new File(dirInJumbuneHome);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String remoteHadoopHome = getHadoopHome(loader.getYamlConfiguration());
		String jumbuneJobName = loader.getJumbuneJobName() + File.separator;
		YamlConfig config = loader.getYamlConfiguration();
		jumbuneHome = new String(jumbuneHome+File.separator);
		String destinationReceiveDir = jumbuneHome + Constants.JOB_JARS_LOC  + jumbuneJobName;
		Remoter remoter = getRemoter(config, jumbuneHome);
		String copyCommand = new StringBuilder().append("cp ").append(remoteHadoopHome).append("/conf/").append(hadoopConfigurationFile).append(" ").append(getAgentHome(config)).append("/jobJars/").append(jumbuneJobName).toString();
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(MAKE_JOBJARS_DIR_ON_AGENT + jumbuneJobName, false, null)
		.addCommand(copyCommand, false, null);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		//If execution happended to fast, we won't be able to get a directory to find files for next command
		remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC  + jumbuneJobName, File.separator + Constants.JOB_JARS_LOC + jumbuneJobName + hadoopConfigurationFile);
		remoter.close();
		return destinationReceiveDir;
	}
	
	public static String copyAndGetHadoopConfigurationFilePath(String remoteAbsolutePath, YamlLoader loader){
		String jumbuneHome = YamlLoader.getjHome();
		String dirInJumbuneHome = jumbuneHome + File.separator + Constants.JOB_JARS_LOC + loader.getJumbuneJobName();
		File dir = new File(dirInJumbuneHome);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String jumbuneJobName = loader.getJumbuneJobName() + File.separator;
		YamlConfig config = loader.getYamlConfiguration();
		jumbuneHome = new String(jumbuneHome+File.separator);
		String destinationReceiveDir = jumbuneHome + Constants.JOB_JARS_LOC  + jumbuneJobName;
		
		Remoter remoter = getRemoter(config, "");
		String copyCommand = new StringBuilder().append("cp ").append(remoteAbsolutePath).append(" ").append(getAgentHome(config)).append("/jobJars/").append(jumbuneJobName).toString();
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(MAKE_JOBJARS_DIR_ON_AGENT + jumbuneJobName, false, null).addCommand(copyCommand, false, null);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		String fileName = remoteAbsolutePath.substring(remoteAbsolutePath.lastIndexOf(File.separator)+1);
		remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC  + jumbuneJobName, File.separator + Constants.JOB_JARS_LOC + jumbuneJobName + fileName);
		return destinationReceiveDir+File.separator+fileName;		
	}
	
	/**
	 * *
	 * This method receives files inside <AGENT_HOME>/<JOB_NAME>/ directory . This expects that the given configuration
	 * file name to be correct and the file resides in <AGENT_HOME>/<JOB_NAME>/directory.
	 *
	 * @param loader the loader
	 * @param hadoopConfigurationFile file name to recieved from <AGENT_HOME>/<JOB_NAME> directory
	 * @return the string
	 */
	public static String receiveLogFilesFromAgent(YamlLoader loader, String hadoopConfigurationFile) {

		String jumbuneHome = YamlLoader.getjHome();
		String dirInJumbuneHome = jumbuneHome + File.separator + Constants.JOB_JARS_LOC + loader.getJumbuneJobName();
		File dir = new File(dirInJumbuneHome);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String jumbuneJobName = loader.getJumbuneJobName() + File.separator;
		YamlConfig config = loader.getYamlConfiguration();
		String destinationReceiveDir = jumbuneHome + File.separator + Constants.JOB_JARS_LOC  + jumbuneJobName;
		Remoter remoter = getRemoter(config, jumbuneHome + File.separator);
		remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC  + jumbuneJobName, File.separator + Constants.JOB_JARS_LOC + jumbuneJobName + hadoopConfigurationFile);
		remoter.close();
		return destinationReceiveDir;
	}
	
	/**
	 * Gets the virtual file system.
	 *
	 * @param loader the loader
	 * @return the virtual file system
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static VirtualFileSystem getVirtualFileSystem(YamlLoader loader) throws IOException {
		VirtualFileSystem fs = null;
		HadoopFileSystemUtility utility = null;
		utility = new HadoopFileSystemUtility(loader);
		String nameNodeURI = RemotingUtil.getHadoopConfigurationValue(loader, "core-site.xml", "fs.default.name");
		fs = utility.getVirtualFileSystem(nameNodeURI, loader.getYamlConfiguration().getMaster().getUser());
		return fs;

	}

	/**
	 * Gets the file system.
	 *
	 * @param loader the loader
	 * @return the file system
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static FileSystem getFileSystem(YamlLoader loader) throws IOException {
		FileSystem fs = null;
		try {
			org.apache.hadoop.conf.Configuration config = new org.apache.hadoop.conf.Configuration();
			String nameNodeURI = RemotingUtil.getHadoopConfigurationValue(loader, "core-site.xml", "fs.default.name");
			fs = FileSystem.get(URI.create(nameNodeURI), config, loader.getYamlConfiguration().getMaster().getUser());
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return fs;
	}

	/**
	 * Gets the ip from host name.
	 *
	 * @param loader the loader
	 * @param hostName the host name
	 * @return the ip from host name
	 */
	public static String getIPfromHostName(YamlLoader loader, String hostName) {
		String jumbuneHome = System.getenv("JUMBUNE_HOME") + File.separator;
		Remoter remoter = getRemoter(loader, jumbuneHome);
		String command =  hostName;
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null).setApiInvokeHints(ApiInvokeHintsEnum.HOST_TO_IP_OP);
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		return response;
	}
	
	/**
	 * Sends the yaml info to agent for shutdown hook
	 * @param loader
	 * @param config
	 */
	public static void sendYamlInfoToAgent(YamlLoader loader, BasicYamlConfig config){
		String jumbuneHome = System.getenv("JUMBUNE_HOME");
		String yamlInfoPath = jumbuneHome + YAML_FILE;
		Remoter remote = new Remoter(config.getHost(), Integer.parseInt(config.getPort()));
		remote.sendLogFiles(File.separator,yamlInfoPath);
		}
	
	
	/**
	 * Gets the hadoop version.
	 *
	 * @return the hadoop version
	 */
	public static SupportedApacheHadoopVersions getHadoopVersion(YamlConfig yamlConfig) {

		if (hadoopVersion != null) {
			return hadoopVersion;
		}
		
		String commandResponse = fireCommandOnSupporteHadoopVersionAndGetStringResponse(yamlConfig, "version");
		
		for (String line : commandResponse.split("\\n")) {
			if (line.matches(HADOOP_VERSION_REGEX)|| line.matches(CLOUDERA_HADOOP_REGEX)) {
				hadoopVersion = SupportedApacheHadoopVersions.getEnumByValue(line.trim());
			}
		}
		return hadoopVersion;

	}

}
