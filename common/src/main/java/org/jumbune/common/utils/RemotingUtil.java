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
import org.jumbune.common.beans.SupportedHadoopDistributions;
import org.jumbune.common.utils.locators.ApacheNonYarnLocator;
import org.jumbune.common.utils.locators.ApacheYarnLocator;
import org.jumbune.common.utils.locators.CDHLocator;
import org.jumbune.common.utils.locators.HadoopDistributionLocator;
import org.jumbune.common.utils.locators.MapRLocator;
import org.jumbune.common.yaml.config.Config;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.ApiInvokeHintsEnum;
import org.jumbune.remoting.common.BasicYamlConfig;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.common.beans.Enable;
import org.jumbune.remoting.common.StringUtil;


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
	private static SupportedHadoopDistributions hadoopVersion;
	
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
	public static Remoter getRemoter(Loader loader, final String receiveDirectory) {
		YamlLoader yamlLoader = (YamlLoader)loader;
		String receivedDirectory = receiveDirectory;
		Master master = yamlLoader.getMasterInfo();
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
	public static Remoter getRemoter(Config config, String receiveDirectory) {
		YamlConfig 	yamlConfig = (YamlConfig)config;
		Master master = yamlConfig.getMaster();
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
	public static String getHadoopHome(Remoter remoter, Config config) {
		String command = "echo $HADOOP_HOME  \n \n";
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null, CommandType.FS).populate(config, null);
		
		return (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
	}

	/**
	 * Gets the hadoop home.
	 *
	 * @param config the config
	 * @return the hadoop home
	 */
	public static String getHadoopHome(Config config) {
		Remoter remoter = getRemoter(config, "");
		
		String command = "echo $HADOOP_HOME  \n \n";
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null, CommandType.FS).populate(config, null);
		
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
	public static String getAgentHome(Config config) {

		String agentHome = null;
		if (config != null) {
			YamlConfig yamlConfig = (YamlConfig)config;
			Master master = yamlConfig.getMaster();
			Remoter remoter = new Remoter(master.getHost(), Integer.valueOf(master.getAgentPort()));
			
			CommandWritableBuilder builder = new CommandWritableBuilder();
			builder.addCommand(Constants.ECHO_AGENT_HOME, false, null, CommandType.FS).populate(config, null);
			
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
	public static JobClient getJobClient(Loader loader) throws IOException {
		String jobTrackerURI = RemotingUtil.getHadoopConfigurationValue(loader, "mapred-site.xml", "mapred.job.tracker");
		JobClient client = null;
		Configuration config = new Configuration();
		YamlLoader yamlLoader = (YamlLoader)loader;
		YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
		config.set(yamlConfig.getMaster().getUser(), "");
		if(Enable.TRUE.equals(yamlConfig.getEnableYarn())){
			config.set("mapreduce.framework.name", "yarn");
		}

		try {
			
			if(StringUtil.emptyOrNull(jobTrackerURI)){
				LOGGER.debug("No Job Tracker configuration found in mapred-site.xml, attempting to create job client with default uri");
				client = new JobClient(new InetSocketAddress("0:0:0:0", 8032), config);
			}else {
			client = new JobClient(new InetSocketAddress(jobTrackerURI.split(":")[0], Integer.parseInt(jobTrackerURI.split(":")[1])), config);
 			}

		} catch (Exception e) {
			LOGGER.error(e);
		}
		return client;
	}
	
	/**
	 * Abstraction to FireCommandAndGetObjectResponse to simplify usage
	 * @param config, the Yaml Config
	 * @param command, the command to be executed using remoting
	 * @return, the command response as String
	 */
	public static String executeCommand(YamlConfig config, String command) {
		Remoter remoter = getRemoter(config, "");
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null, CommandType.FS).populate(config, null);
		return (String) remoter.fireCommandAndGetObjectResponse(builder
				.getCommandWritable());
	}		

	/**
	 * *
	 * This method fireCommand and get String response to user using remoting.
	 *
	 * @param yamlConfig the yaml config
	 * @param command the command
	 * @return the string
	 */
	
	public static String fireCommandAsHadoopDistribution(Config config, String command, CommandType commandType) {
		String hadoopHome = RemotingUtil.getHadoopHome(config);
		Remoter remoter = getRemoter(config, " ");
		YamlConfig yamlConfig = (YamlConfig)config;
		Master master = yamlConfig.getMaster();
		String hadoopDir = fireWhereIsHadoopCommand(remoter, master, config);
		List<String> host = new ArrayList<String>();
		host.add(master.getHost());
		String commandToExecute = null;
		if(hadoopDir != null){
			commandToExecute = hadoopDir + " " + command;
		} else {
			commandToExecute = hadoopHome + "/bin/hadoop  " + command;
		}
		LOGGER.debug("Command to be executed:" + commandToExecute);
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(commandToExecute, false, null, commandType).populate(config, null);
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
	public static String fireWhereIsHadoopCommand(Remoter remoter, Master master, Config config) {
		String hadoopDir = null;
		String command = "whereis hadoop  ";
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null, CommandType.FS).populate(config, null);
		
		String wherIsHadoopResponse=(String)remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		if(wherIsHadoopResponse!=null && 2<wherIsHadoopResponse.split(" ").length){
			hadoopDir = wherIsHadoopResponse.split(" ")[1];
		}
		if(hadoopDir!= null && hadoopDir.trim().length()==0){
			return null;
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
	public static String getHadoopConfigurationValue(Loader loader, String hadoopConfigurationFile, String configurationToGet) {
		String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(loader);
		String destinationReceiveDir = copyAndGetConfigurationFilePath(loader, hadoopConfDir, hadoopConfigurationFile);
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
	@Deprecated
	public static String copyAndGetHadoopConfigurationFilePath(Loader loader, String hadoopConfigurationFile) {
		YamlLoader yamlLoader = (YamlLoader)loader;
		String jumbuneHome = yamlLoader.getjHome();
		YamlConfig yamlConfig= (YamlConfig)yamlLoader.getYamlConfiguration();
		String jumbuneJobName = yamlLoader.getJumbuneJobName() + File.separator;
		String dirInJumbuneHome = jumbuneHome + File.separator + Constants.JOB_JARS_LOC + yamlLoader.getJumbuneJobName();
		File dir = new File(dirInJumbuneHome);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		HadoopDistributionLocator hadoopUtility = getDistributionLocator(getHadoopVersion(yamlConfig));
		String hadoopConfDir = hadoopUtility.getHadoopConfDirPath(yamlConfig);
		
		jumbuneHome = new String(jumbuneHome+File.separator);
		String destinationReceiveDir = jumbuneHome + Constants.JOB_JARS_LOC  + jumbuneJobName;
		Remoter remoter = getRemoter(yamlConfig, jumbuneHome);
		String copyCommand = new StringBuilder().append("cp ").append(hadoopConfDir).append(hadoopConfigurationFile).append(" ").append(getAgentHome(yamlConfig)).append("/jobJars/").append(jumbuneJobName).toString();
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(MAKE_JOBJARS_DIR_ON_AGENT + jumbuneJobName, false, null,CommandType.FS)
		.addCommand(copyCommand, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		//If execution happended to fast, we won't be able to get a directory to find files for next command
		remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC  + jumbuneJobName, File.separator + Constants.JOB_JARS_LOC + jumbuneJobName + hadoopConfigurationFile);
		return destinationReceiveDir;
	}
	
	
	
	
	/**
	 * 
	 * This method returns the absoluate path of a given configuration file on the remote HADOOP machine. This expects that the given configuration
	 * file name to be correct and the file resides in <HADOOP_HOME>/conf directory.
	 * 
	 * This method is different overridden copyAndGetHadoopConfigurationFilePath(Loader loader, String hadoopConfigurationFile) as this method
	 * assumes that we already know the configuration dir which is passed as an argument, get's appended to the hadoop configuration file argument.
	 * 
	 * This is helpful to use in cases where we require to fetch multiple configuration files, since all of them reside in the same configuration 
	 * directory, it makes more sense to not to re-discover the configuration directory again and again. 
	 *
	 * @param loader the loader
	 * @param hadoopConfDir, the hadoop configuration dir path
	 * @param hadoopConfigurationFile which we wants to receive the path of.
	 * @return the string
	 */
	public static String copyAndGetConfigurationFilePath(Loader loader, String hadoopConfDir, String hadoopConfigurationFile) {
		YamlLoader yamlLoader = (YamlLoader)loader;
		String jumbuneHome = yamlLoader.getjHome();
		YamlConfig yamlConfig= (YamlConfig)yamlLoader.getYamlConfiguration();
		String jumbuneJobName = yamlLoader.getJumbuneJobName() + File.separator;
		
		jumbuneHome = new String(jumbuneHome+File.separator);
		String destinationReceiveDir = jumbuneHome + Constants.JOB_JARS_LOC  + jumbuneJobName;
		Remoter remoter = getRemoter(yamlConfig, jumbuneHome);
		String copyCommand = new StringBuilder().append("cp ").append(hadoopConfDir).append(hadoopConfigurationFile).append(" ").append(getAgentHome(yamlConfig)).append("/jobJars/").append(jumbuneJobName).toString();
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(MAKE_JOBJARS_DIR_ON_AGENT + jumbuneJobName, false, null,CommandType.FS)
		.addCommand(copyCommand, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		//If execution happended to fast, we won't be able to get a directory to find files for next command
		remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC  + jumbuneJobName, File.separator + Constants.JOB_JARS_LOC + jumbuneJobName + hadoopConfigurationFile);
		return destinationReceiveDir;
	}
	
	/**
	 * This method is helpful to retrieve hadoop configuration directory in advance and can be helpful to 
	 * methods like, RemotingUtil.copyAndGetHadoopConfigurationFilePath(Loader loader, String hadoopConfDir, String hadoopConfigurationFile) 
	 * @param loader, the yaml loader instance
	 * @return
	 */
	public static String getHadoopConfigurationDirPath(Loader loader){
		YamlLoader yamlLoader = (YamlLoader)loader;
		YamlConfig yamlConfig= (YamlConfig)yamlLoader.getYamlConfiguration();
		String jumbuneHome = yamlLoader.getjHome();
		String dirInJumbuneHome = jumbuneHome + File.separator + Constants.JOB_JARS_LOC + yamlLoader.getJumbuneJobName();
		File dir = new File(dirInJumbuneHome);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		HadoopDistributionLocator hadoopUtility = getDistributionLocator(getHadoopVersion(yamlConfig));
		return hadoopUtility.getHadoopConfDirPath(yamlConfig);
	}
	
	@Deprecated
	public static String copyAndGetRemoteFileWithAbsolutePath(Loader loader, String remoteAbsolutePath){
		
		YamlLoader yamlLoader = (YamlLoader)loader;
		String jumbuneHome = yamlLoader.getjHome();
		String dirInJumbuneHome = jumbuneHome + File.separator + Constants.JOB_JARS_LOC + yamlLoader.getJumbuneJobName();
		File dir = new File(dirInJumbuneHome);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String jumbuneJobName = yamlLoader.getJumbuneJobName() + File.separator;
		YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
		jumbuneHome = new String(jumbuneHome+File.separator);
		String destinationReceiveDir = jumbuneHome + Constants.JOB_JARS_LOC  + jumbuneJobName;
		
		Remoter remoter = getRemoter(yamlConfig, "");
		String copyCommand = new StringBuilder().append("cp ").append(remoteAbsolutePath).append(" ").append(getAgentHome(yamlConfig)).append("/jobJars/").append(jumbuneJobName).toString();
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(MAKE_JOBJARS_DIR_ON_AGENT + jumbuneJobName, false, null, CommandType.FS).addCommand(copyCommand, false, null, CommandType.FS);
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
	public static String receiveLogFilesFromAgent(Loader loader, String hadoopConfigurationFile) {

		YamlLoader yamlLoader = (YamlLoader)loader;
		String jumbuneHome = yamlLoader.getjHome();
		String dirInJumbuneHome = jumbuneHome + File.separator + Constants.JOB_JARS_LOC + yamlLoader.getJumbuneJobName();
		File dir = new File(dirInJumbuneHome);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String jumbuneJobName = yamlLoader.getJumbuneJobName() + File.separator;
		YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
		String destinationReceiveDir = jumbuneHome + File.separator + Constants.JOB_JARS_LOC  + jumbuneJobName;
		Remoter remoter = getRemoter(yamlConfig, jumbuneHome + File.separator);
		remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC  + jumbuneJobName, File.separator + Constants.JOB_JARS_LOC + jumbuneJobName + hadoopConfigurationFile);
		remoter.close();
		return destinationReceiveDir;
	}
	
	/**
	 * Gets the file system.
	 *
	 * @param loader the loader
	 * @return the file system
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static FileSystem getFileSystem(Loader loader) throws IOException {
		FileSystem fs = null;
		try {
			YamlLoader yamlLoader = (YamlLoader)loader;
			YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
			org.apache.hadoop.conf.Configuration config = new org.apache.hadoop.conf.Configuration();
			String nameNodeURI;
			if(Enable.FALSE.equals(yamlConfig.getEnableYarn())){
				nameNodeURI = RemotingUtil.getHadoopConfigurationValue(loader, "core-site.xml", "fs.default.name");
			}else{
				nameNodeURI = RemotingUtil.getHadoopConfigurationValue(loader, "core-site.xml", "fs.defaultFS");
			}
			fs = FileSystem.get(URI.create(nameNodeURI), config);
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
	public static String getIPfromHostName(Loader loader, String hostName) {
		String jumbuneHome = System.getenv("JUMBUNE_HOME") + File.separator;
		Remoter remoter = getRemoter(loader, jumbuneHome);
		String command =  hostName;
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null, CommandType.FS).setApiInvokeHints(ApiInvokeHintsEnum.HOST_TO_IP_OP);
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		return response;
	}
	
	/**
	 * Sends the yaml info to agent for shutdown hook
	 * @param loader
	 * @param config
	 */
	public static void sendYamlInfoToAgent(Loader loader, BasicYamlConfig config){
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
	public static SupportedHadoopDistributions getHadoopVersion(Config config) {

		if (hadoopVersion != null) {
			return hadoopVersion;
		}
		
		String commandResponse = fireCommandAsHadoopDistribution(config, "version", CommandType.HADOOP_FS);
		
		for (String line : commandResponse.split("\\n")) {
			if (line.matches(HADOOP_VERSION_REGEX)|| line.matches(CLOUDERA_HADOOP_REGEX)) {
				hadoopVersion = SupportedHadoopDistributions.getEnumByValue(line.trim());
			}
		}
		return hadoopVersion;

	}
	
	/***
	 * Return hadoop locator instance based on hadoop version. 
	 * @param version
	 * @return
	 */
	public static HadoopDistributionLocator getDistributionLocator(SupportedHadoopDistributions version) {
		HadoopDistributionLocator hadoopLocator = null;
	    switch (version) {
	      case HADOOP_NON_YARN:
	    	  hadoopLocator = new ApacheNonYarnLocator();
	        break;
	      case HADOOP_MAPR:
	    	  hadoopLocator = new MapRLocator();
	        break;
	      case HADOOP_YARN:
	    	  hadoopLocator = new ApacheYarnLocator();
	        break;
	      case CDH_5:
	    	  hadoopLocator = new CDHLocator();
	    	  break;
	      case APACHE_02X:
	      	  hadoopLocator = new MapRLocator();
	      	  break;
	      default:
	    	  hadoopLocator =  new ApacheNonYarnLocator();
	      break;
	    }
	    return hadoopLocator;
	  }
	

}
