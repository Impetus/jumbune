package org.jumbune.common.utils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Agent;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.locators.ApacheYarnLocator;
import org.jumbune.common.utils.locators.CDHLocator;
import org.jumbune.common.utils.locators.EMRApacheLocator;
import org.jumbune.common.utils.locators.EMRMaprLocator;
import org.jumbune.common.utils.locators.HDPLocator;
import org.jumbune.common.utils.locators.HadoopDistributionLocator;
import org.jumbune.common.utils.locators.MapRYarnLocator;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.client.RemoterFactory;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.RemotingMethodConstants;
import org.jumbune.remoting.common.StringUtil;



/***
 * This class provide  utility methods for remoting operation from jumbune.
 * 
 *
 */
public final class RemotingUtil {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(RemotingUtil.class);
	
	/** The Constant MAKE_JOBJARS_DIR_ON_AGENT. */
	private static final String MAKE_JOBJARS_DIR_ON_AGENT = "mkdir -p AGENT_HOME/jobJars/";
	
	/** The Constant JSON_FILE. */
	private static final String JSON_FILE = "/jsonInfo.ser";
	
	/** The Constant HISTORY_INT_DIR_SUFFIX_YARN_DEFAULT. */
	private static final String HISTORY_INT_DIR_SUFFIX_YARN_DEFAULT = "/tmp/hadoop-yarn/staging/history/done_intermediate/*/";
	
	/** The history dir suffix yarn default. */
	private static String HISTORY_DIR_SUFFIX_YARN_DEFAULT = "/tmp/hadoop-yarn/staging/history/done/*/*/*/*/";
	
	/** The history dir suffix mapr yarn default. */
	private static String HISTORY_DIR_SUFFIX_MAPR_YARN_DEFAULT = "/var/mapr/cluster/yarn/rm/staging/history/done/*/*/*/*/";//mapr code changes
	
	/** The user intermediate history dir suffix. */
	private static String USER_INT_HISTORY_DIR_SUFFIX = "/history/done_intermediate/*/";
	
	/** The user history dir suffix. */
	private static String USER_HISTORY_DIR_SUFFIX = "/history/done/*/*/*/*/";
	
	/** The conf dir. */
	private static String CONF_DIR = null ;
	
	/**
	 * Instantiates a new remoting util.
	 */
	private RemotingUtil(){
		
	}
	
	/**
	 * Gets the remoter.
	 *
	 * @param cluster the cluster
	 * @param receiveDirectory the receive directory
	 * @return the remoter
	 */
	public static Remoter getRemoter(Cluster cluster, String receiveDirectory) {
		Agent agent = cluster.getJumbuneAgent();
	    return RemoterFactory.getRemoter(agent.getHost(), Integer.parseInt(agent.getPort()), null,
				cluster.getAgents().isHaEnabled(), cluster.getNameNodes().isHaEnabled(), cluster.getZkHosts(), cluster.getClusterName());		
	}
	
	/**
	 * Gets the remoter.
	 *
	 * @param cluster the cluster
	 * @return the remoter
	 */
	public static Remoter getRemoter(Cluster cluster) {
		Agent agent = cluster.getJumbuneAgent();
	    return RemoterFactory.getRemoter(agent.getHost(), Integer.parseInt(agent.getPort()), null,
				cluster.getAgents().isHaEnabled(), cluster.getNameNodes().isHaEnabled(), cluster.getZkHosts(), cluster.getClusterName());		
	}
	
	/**
	 * Gets the hadoop home.
	 *
	 * @param remoter the remoter
	 * @param cluster the cluster
	 * @return the hadoop home
	 */
	@Deprecated
	public static String getHadoopHome(Remoter remoter, Cluster cluster) {
		String command = "echo $HADOOP_HOME  \n \n";		
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, null);
		builder.addCommand(command, false, null, CommandType.FS);		
		return (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
	}

	/**
	 * Gets the hadoop home.
	 *
	 * @param cluster the cluster
	 * @return the hadoop home
	 */
	public static String getHadoopHome(Cluster cluster) {
		Remoter remoter = getRemoter(cluster, "");
		
		String command = "echo $HADOOP_HOME  \n \n";
		
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, null);
		builder.addCommand(command, false, null, CommandType.FS);
		
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		return response;		
	}

	/**
	 * Gets the agent home.
	 *
	 * @param cluster the cluster
	 * @return the agent home
	 */
	public static String getAgentHome(Cluster cluster) {

		String agentHome = null;
		if (cluster != null) {
			Remoter remoter = getRemoter(cluster);
			CommandWritableBuilder builder = new CommandWritableBuilder(cluster, null);
			builder.addCommand(Constants.ECHO_AGENT_HOME, false, null, CommandType.FS);
			
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
	 * @param cluster the cluster
	 * @param jobTrackerURI the job tracker uri
	 * @return the job client
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static JobClient getJobClient(Cluster cluster, String jobTrackerURI) throws IOException {
		JobClient client = null;
		Configuration configuration = new Configuration();
		configuration.set(cluster.getHadoopUsers().getFsUser(), "");
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		configuration.set("mapreduce.framework.name", "yarn");

		String nameNodeHost=cluster.getNameNode();
		try {
			if(StringUtil.emptyOrNull(jobTrackerURI)||isDefaultJobTrackerURI(jobTrackerURI)){
				LOGGER.debug("No Job Tracker configuration found in mapred-site.xml, attempting to create job client with default uri");

		
				if(hadoopDistribution.equalsIgnoreCase(Constants.MAPR))
				{  //default IPC port for jobtracker in MapR is 9001
					client = new JobClient(new InetSocketAddress(nameNodeHost, 9001), configuration);
				}else{
					client = new JobClient(new InetSocketAddress(nameNodeHost, 8032), configuration);	
				}					
				
			}else {
				LOGGER.debug("Attempting to create job client with uri: "+jobTrackerURI);
				client = new JobClient(new InetSocketAddress(jobTrackerURI.split(":")[0], Integer.parseInt(jobTrackerURI.split(":")[1])), configuration);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return client;
	}
	
	/**
	 * Checks if is default job tracker uri.
	 *
	 * @param jobTrackerURI the job tracker uri
	 * @return true, if is default job tracker uri
	 */
	private static boolean isDefaultJobTrackerURI(String jobTrackerURI)
	{
		if(jobTrackerURI.equalsIgnoreCase("maprfs:///")){
			return true;
		}
	return false;
	}
	
	/**
	 * Abstraction to FireCommandAndGetObjectResponse to simplify usage.
	 *
	 * @param cluster the cluster
	 * @param command the command
	 * @return the string
	 * @return, the command response as String
	 */
	public static String executeCommand(Cluster cluster, String command) {
		Remoter remoter = getRemoter(cluster, "");
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, null);
		builder.addCommand(command, false, null, CommandType.FS);
		return (String) remoter.fireCommandAndGetObjectResponse(builder
				.getCommandWritable());
	}	
	
	/**
	 * *
	 * This method fireCommand and get String response to user using remoting.
	 *
	 * @param cluster the cluster
	 * @param command the command
	 * @param commandType the command type
	 * @return the string
	 */
	
	public static String fireCommandAsHadoopDistribution(
			Cluster cluster, String command, CommandType commandType) {		
		return fireCommandAsHadoopDistribution(cluster, command, commandType, null);
	}

	
	/**
	 * *
	 * This method fireCommand and get String response to user using remoting.
	 *
	 * @param cluster the cluster
	 * @param command the command
	 * @param commandType the command type
	 * @param operatingUser the operating user
	 * @return the string
	 */
	
	public static String fireCommandAsHadoopDistribution(
			Cluster cluster, String command, CommandType commandType, String operatingUser) {
		
		String hadoopHome = RemotingUtil.getHadoopHome(cluster);
		Remoter remoter = getRemoter(cluster, " ");
		String hadoopDir = fireWhereIsHadoopCommand(remoter, cluster);		
		String commandToExecute = null;
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		boolean isMapr = Constants.MAPR.equalsIgnoreCase(hadoopDistribution);
		if(hadoopHome == null || hadoopHome.isEmpty()){			
			if(isMapr){
				commandToExecute = "/usr/bin/" + command;
			} else {
				commandToExecute = hadoopDir + " " + command;
			}
		} else {			
			commandToExecute = hadoopHome + "/bin/hadoop  " + command;
		}
		LOGGER.debug("Command to be executed:" + commandToExecute);
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, null);
		builder.addCommand(commandToExecute, false, null, commandType, operatingUser);
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());		
		remoter.close();
		return response;
	}

	/**
	 * this method fires where is hadoop command to get hadoop location.
	 *
	 * @param remoter the remoter
	 * @param cluster the cluster
	 * @return the string
	 */
	public static String fireWhereIsHadoopCommand(Remoter remoter, Cluster cluster) {
		String hadoopDir = null;
		String command = "whereis hadoop  ";
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, null);
		builder.addCommand(command, false, null, CommandType.FS);
		
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
	 * @param jumbuneRequest the loader
	 * @param hadoopConfigurationFile configuration file in Hadoop configuration directory
	 * @param configurationToGet Hadoop configuration parameter.
	 * @return configuration value.
	 */
	public static String getHadoopConfigurationValue(
			JumbuneRequest jumbuneRequest, String hadoopConfigurationFile, String configurationToGet) {
		
		String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(jumbuneRequest);
		String destinationReceiveDir = copyAndGetConfigurationFilePath(jumbuneRequest, hadoopConfDir, hadoopConfigurationFile);
		return parseConfiguration(destinationReceiveDir + File.separator + hadoopConfigurationFile, configurationToGet);
	}		
	
	/**
	 * Gets the hadoop configuration value.
	 *
	 * @param cluster the cluster
	 * @param hadoopConfigurationFile the hadoop configuration file
	 * @param configurationToGet the configuration to get
	 * @return the hadoop configuration value
	 */
	public static String getHadoopConfigurationValue(
			Cluster cluster, String hadoopConfigurationFile, String configurationToGet) {
		
		String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(cluster);
		String destinationReceiveDir = copyAndGetConfigurationFilePath(cluster, hadoopConfDir, hadoopConfigurationFile);
		return parseConfiguration(destinationReceiveDir + File.separator + hadoopConfigurationFile, configurationToGet);
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

	public static String getDaemonProcessId(Cluster cluster, String host, String daemonName) {
		String pid = null;
		Remoter remoter = getRemoter(cluster, null);
		String command =  host + Constants.SPACE + daemonName;
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand(command, false, null, CommandType.FS).setMethodToBeInvoked(RemotingMethodConstants.GET_DAEMON_PROCESS_ID);		
		pid = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		return pid;		
	}
	
	/**
	 * This method returns the absolute path of a given configuration file on the remote HADOOP machine. This expects that the given configuration
	 * file name to be correct and the file resides in <HADOOP_HOME>/conf directory.
	 * 
	 * This method is different overridden copyAndGetHadoopConfigurationFilePath(Loader loader, String hadoopConfigurationFile) as this method
	 * assumes that we already know the configuration dir which is passed as an argument, get's appended to the hadoop configuration file argument.
	 * 
	 * This is helpful to use in cases where we require to fetch multiple configuration files, since all of them reside in the same configuration 
	 * directory, it makes more sense to not to re-discover the configuration directory again and again. 
	 *
	 * @param jumbuneRequest the jumbune request
	 * @param hadoopConfDir the hadoop conf dir
	 * @param hadoopConfigurationFile which we wants to receive the path of.
	 * @return the string
	 */
	public static String copyAndGetConfigurationFilePath(
			JumbuneRequest jumbuneRequest, String hadoopConfDir, String hadoopConfigurationFile) {
		
		JobConfig jobConfig =  jumbuneRequest.getJobConfig();
		String jumbuneJobName = jobConfig.getJumbuneJobName() + File.separator;
		
		String destinationReceiveDir = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC  + jumbuneJobName;
		//Checking if configuration file exists or not
		File file = new File(destinationReceiveDir + File.separator + hadoopConfigurationFile);
		if (file.exists()) {
			return destinationReceiveDir;
		}
		Cluster cluster = jumbuneRequest.getCluster();
		Remoter remoter = getRemoter(cluster, JumbuneInfo.getHome());
		if(!hadoopConfDir.endsWith(File.separator)){
			hadoopConfDir = hadoopConfDir+File.separator;
		}
		String copyCommand = new StringBuilder().append("cp ").append(hadoopConfDir).append(hadoopConfigurationFile).append(" ").append(getAgentHome(cluster)).append("/jobJars/").
				append(jumbuneJobName).toString();
		CommandWritableBuilder builder = new CommandWritableBuilder(jumbuneRequest.getCluster());
		builder.addCommand(MAKE_JOBJARS_DIR_ON_AGENT + jumbuneJobName, false, null,CommandType.FS)
		.addCommand(copyCommand, false, null, CommandType.FS);
		if(hadoopConfigurationFile.contains(Constants.CORE_SITE_XML) || hadoopConfigurationFile.contains(Constants.HDFS_SITE_XML)){
			remoter.fireAndForgetCommand(builder.getCommandWritable());
			//If execution happended to fast, we won't be able to get a directory to find files for next command
			remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC  + jumbuneJobName, File.separator + Constants.JOB_JARS_LOC + jumbuneJobName + hadoopConfigurationFile);
		}else{
			//scp user@rm_host:hadoopConfDir/hadoopConfigurationFile AGENT_HOME/jobjars/clusterName/
			StringBuilder scpCommand;
			if(cluster.getAgents().getSshAuthKeysFile()!=null && cluster.getAgents().getSshAuthKeysFile().endsWith(".pem")){
				scpCommand = new StringBuilder("scp ").append("-i ").append(cluster.getAgents().getSshAuthKeysFile()).append(" ").append(cluster.getHadoopUsers().getFsUser())
					.append(Constants.AT_OP).append(cluster.getTaskManagers().getActive()).append(Constants.COLON)
					.append(hadoopConfDir).append(File.separator).append(hadoopConfigurationFile)
					.append(Constants.SPACE).append(getAgentHome(cluster)).append(File.separator)
					.append(Constants.JOB_JARS_LOC).append(jumbuneJobName).append(File.separator);
			}else{
				scpCommand = new StringBuilder("scp ").append("-o StrictHostKeyChecking=no ").append(cluster.getHadoopUsers().getFsUser())
						.append(Constants.AT_OP).append(cluster.getTaskManagers().getActive()).append(Constants.COLON)
						.append(hadoopConfDir).append(File.separator).append(hadoopConfigurationFile)
						.append(Constants.SPACE).append(getAgentHome(cluster)).append(File.separator)
						.append(Constants.JOB_JARS_LOC).append(jumbuneJobName).append(File.separator);
			}
			builder = new CommandWritableBuilder(cluster);
			builder.addCommand(MAKE_JOBJARS_DIR_ON_AGENT + jumbuneJobName, false, null, CommandType.FS)
					.addCommand(scpCommand.toString(), false, null, CommandType.FS);

			remoter.fireAndForgetCommand(builder.getCommandWritable());
			remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC + jumbuneJobName,
					File.separator + Constants.JOB_JARS_LOC + jumbuneJobName + File.separator + hadoopConfigurationFile);
		}
		
		return destinationReceiveDir;
	}
	
	
	/**
	 * Copy and get configuration file path.
	 *
	 * @param cluster the cluster
	 * @param hadoopConfDir the hadoop conf dir
	 * @param hadoopConfigurationFile the hadoop configuration file
	 * @return the string
	 */
	public static String copyAndGetConfigurationFilePath(
			Cluster cluster, String hadoopConfDir, String hadoopConfigurationFile) {
		
		
		String clusterName = cluster.getClusterName() + File.separator;
		
		String destinationReceiveDir = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC  + clusterName;
		//Checking if configuration file exists or not
		File file = new File(destinationReceiveDir + File.separator + hadoopConfigurationFile);
		if (file.exists()) {
			return destinationReceiveDir;
		}
		if(!hadoopConfDir.endsWith(File.separator)){
			hadoopConfDir = hadoopConfDir+File.separator;
		}
		String copyCommand = new StringBuilder().append("cp ").append(hadoopConfDir).append(hadoopConfigurationFile).append(" ").append(getAgentHome(cluster)).append("/jobJars/").
				append(clusterName).toString();
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand(MAKE_JOBJARS_DIR_ON_AGENT + clusterName, false, null,CommandType.FS)
		.addCommand(copyCommand, false, null, CommandType.FS);
		Remoter remoter = getRemoter(cluster, JumbuneInfo.getHome());
		
		if(hadoopConfigurationFile.contains(Constants.CORE_SITE_XML) || hadoopConfigurationFile.contains(Constants.HDFS_SITE_XML)){
			remoter.fireAndForgetCommand(builder.getCommandWritable());
			//If execution happended to fast, we won't be able to get a directory to find files for next command
			remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC  + clusterName, File.separator + Constants.JOB_JARS_LOC + clusterName + hadoopConfigurationFile);
		}else{
			//scp user@rm_host:hadoopConfDir/hadoopConfigurationFile AGENT_HOME/jobjars/clusterName/
			StringBuilder scpCommand;
			if(cluster.getAgents().getSshAuthKeysFile()!=null && cluster.getAgents().getSshAuthKeysFile().endsWith(".pem")){
				scpCommand = new StringBuilder("scp ").append("-i ").append(cluster.getAgents().getSshAuthKeysFile()).append(" ").append(cluster.getHadoopUsers().getFsUser())
						.append(Constants.AT_OP).append(cluster.getTaskManagers().getActive()).append(Constants.COLON)
						.append(hadoopConfDir).append(File.separator).append(hadoopConfigurationFile)
						.append(Constants.SPACE).append(getAgentHome(cluster)).append(File.separator)
						.append(Constants.JOB_JARS_LOC).append(clusterName).append(File.separator);
			}else{
			    scpCommand = new StringBuilder("scp ").append("-o StrictHostKeyChecking=no ").append(cluster.getHadoopUsers().getFsUser())
					.append(Constants.AT_OP).append(cluster.getTaskManagers().getActive()).append(Constants.COLON)
					.append(hadoopConfDir).append(File.separator).append(hadoopConfigurationFile)
					.append(Constants.SPACE).append(getAgentHome(cluster)).append(File.separator)
					.append(Constants.JOB_JARS_LOC).append(clusterName).append(File.separator);
			}
			builder = new CommandWritableBuilder(cluster);
			builder.addCommand(MAKE_JOBJARS_DIR_ON_AGENT + clusterName, false, null, CommandType.FS)
					.addCommand(scpCommand.toString(), false, null, CommandType.FS);

			remoter.fireAndForgetCommand(builder.getCommandWritable());
			remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC + clusterName,
					File.separator + Constants.JOB_JARS_LOC + clusterName + File.separator + hadoopConfigurationFile);
		}
		return destinationReceiveDir;
	}

	/**
	 * This method is helpful to retrieve hadoop configuration directory in advance and can be helpful to 
	 * methods like, RemotingUtil.copyAndGetHadoopConfigurationFilePath(Loader loader, String hadoopConfDir, String hadoopConfigurationFile) 
	 *
	 * @param jumbuneRequest the jumbune request
	 * @return the hadoop configuration dir path
	 */
	public static String getHadoopConfigurationDirPath(JumbuneRequest jumbuneRequest){
		JobConfig jobConfig = jumbuneRequest.getJobConfig();
		String dirInJumbuneHome = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC + jobConfig.getJumbuneJobName();
		File dir = new File(dirInJumbuneHome);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		HadoopDistributionLocator hadoopUtility = getDistributionLocator(hadoopDistribution);
		if(CONF_DIR == null || CONF_DIR.isEmpty()){
		CONF_DIR = hadoopUtility.getHadoopConfDirPath(jumbuneRequest.getCluster());
		}
		return CONF_DIR;
	}
	
	
	/**
	 * Gets the hadoop configuration dir path.
	 *
	 * @param cluster the cluster
	 * @return the hadoop configuration dir path
	 */
	public static String getHadoopConfigurationDirPath(Cluster cluster){
			
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		HadoopDistributionLocator hadoopUtility = getDistributionLocator(hadoopDistribution);
		if(CONF_DIR == null || CONF_DIR.isEmpty()){
		CONF_DIR = hadoopUtility.getHadoopConfDirPath(cluster);
		}
		return CONF_DIR;
	}

	/**
	 * Gets the ip from host name.
	 *
	 * @param cluster the cluster
	 * @param hostName the host name
	 * @return the ip from host name
	 */
	public static String getIPfromHostName(
			Cluster cluster, String hostName) {
		
		String jumbuneHome = System.getenv("JUMBUNE_HOME") + File.separator;
		Remoter remoter = getRemoter(cluster, jumbuneHome);
		String command =  hostName;
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand(command, false, null, CommandType.FS).setMethodToBeInvoked(RemotingMethodConstants.CONVERT_HOST_NAME_TO_IP);		
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		return response;
	}
	
	/**
	 * Sends the json info to agent for shutdown hook.
	 *
	 * @param configuration the configuration
	 */
	public static void sendJsonInfoToAgent(Cluster cluster){
		String jumbuneHome = System.getenv("JUMBUNE_HOME");
		String jsonInfoPath = jumbuneHome + JSON_FILE;
		Remoter remote = getRemoter(cluster, null);
		remote.sendLogFiles(File.separator,jsonInfoPath);
	}
	
	/**
	 * *
	 * Return hadoop locator instance based on hadoop version. 
	 *
	 * @param hadoopDistribution the hadoop distribution
	 * @param hadoopType the hadoop type
	 * @return the distribution locator
	 */
	public static HadoopDistributionLocator getDistributionLocator(String hadoopDistribution) {
		HadoopDistributionLocator hadoopLocator = null;
		if(hadoopDistribution.equalsIgnoreCase(Constants.APACHE)){
			hadoopLocator = new ApacheYarnLocator();
		}else if(hadoopDistribution.equalsIgnoreCase(Constants.CLOUDERA)){
			hadoopLocator = new CDHLocator();
		   }
		//begin mapr code changes
		else if(hadoopDistribution.equalsIgnoreCase(Constants.MAPR)){
			hadoopLocator = new MapRYarnLocator();
		   }else if(hadoopDistribution.equalsIgnoreCase(Constants.EMRMAPR)){
			hadoopLocator = new EMRMaprLocator();
		   }else if(hadoopDistribution.equalsIgnoreCase(Constants.EMRAPACHE)){
			hadoopLocator = new EMRApacheLocator();
		   }
		//end mapr code changes
		else{
			hadoopLocator = new HDPLocator();
		}
	    return hadoopLocator;
	  }
	
	/**
	 * This method creates a directory at agent side and gives the writing permission(chmod o+w) to others. 
	 *
	 * @param builder the builder
	 * @param remoter the remoter
	 * @param dirPath the dir path
	 */
	public static void mkDir(CommandWritableBuilder builder,Remoter remoter ,String dirPath) {
		builder.addCommand(RemotingConstants.MKDIR_CMD + dirPath, false, null, CommandType.FS);
		builder.addCommand(RemotingConstants.CHMOD_CMD + dirPath, false, null, CommandType.FS);
		builder.addCommand(RemotingConstants.CHMOD_GROUP + dirPath, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());

	}
	
	/**
	 * Gets the final location of the .jhist file on hdfs.
	 *
	 * @param cluster the cluster
	 * @return the history done location
	 */
	public static String getHistoryDoneLocation(Cluster cluster) {
		String historyDoneLocation = RemotingUtil.getHadoopConfigurationValue(cluster,"mapred-site.xml","mapreduce.jobhistory.done-dir");
		String historyStagDoneLocation = RemotingUtil.getHadoopConfigurationValue(cluster,"mapred-site.xml","yarn.app.mapreduce.am.staging-dir");
		//begin mapr code changes
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		if(Constants.MAPR.equalsIgnoreCase(hadoopDistribution) || Constants.EMRMAPR.equalsIgnoreCase(hadoopDistribution)){
			return HISTORY_DIR_SUFFIX_MAPR_YARN_DEFAULT;
		}
		//end mapr code changes
		if(historyDoneLocation!=null && !historyDoneLocation.isEmpty()){
			historyDoneLocation = historyDoneLocation + "/*/*/*/*/" ;
			 return historyDoneLocation;
		}else if(historyStagDoneLocation!=null && !historyStagDoneLocation.isEmpty()){
			historyStagDoneLocation = historyDoneLocation + USER_HISTORY_DIR_SUFFIX ;
			return historyStagDoneLocation;
		}else{
			historyDoneLocation = HISTORY_DIR_SUFFIX_YARN_DEFAULT ;
		}
		return historyDoneLocation;
	}

	/**
	 * Gets the intermediate location of the .jhist file on hdfs.
	 *
	 * @param cluster the cluster
	 * @return the history intermediate location
	 */
	public static String getHistoryIntermediateLocation(Cluster cluster) {
		String historyInterLocation = RemotingUtil.getHadoopConfigurationValue(cluster,"mapred-site.xml","mapreduce.jobhistory.intermediate-done-dir");
		String historyStagInterLocation = RemotingUtil.getHadoopConfigurationValue(cluster,"mapred-site.xml","yarn.app.mapreduce.am.staging-dir");
		if(historyInterLocation!=null && !historyInterLocation.isEmpty()){
			historyInterLocation = historyInterLocation + File.separator + "*" + File.separator;
			return historyInterLocation;
		}else if(historyStagInterLocation!=null && !historyStagInterLocation.isEmpty()){
			historyStagInterLocation = historyInterLocation + USER_INT_HISTORY_DIR_SUFFIX ;
			return historyStagInterLocation;
		}else{
			historyInterLocation = HISTORY_INT_DIR_SUFFIX_YARN_DEFAULT ;
		}
		return historyInterLocation;
	}
	
	
	/**
	 * Gets the hadoop cluster time millis.
	 * This method returns cluster current time millis(i.e. performs {@code System.currentTimeMillis()} at agent)
	 *
	 * @param cluster the cluster
	 * @return the hadoop cluster time millis
	 */
	public static long getHadoopClusterTimeMillis(Cluster cluster) {
		Remoter remoter = getRemoter(cluster, null);
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand("", false, null, CommandType.FS).setMethodToBeInvoked(RemotingMethodConstants.GET_HADOOP_CLUSTER_TIME_MILLIS);				
		long response = (long) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		return response;
	}
	
	/**
	 * Add the hadoop resource.
	 *
	 * @param configuration the configuration
	 * @param cluster the cluster
	 * @param hadoopConfDir the hadoop configuration directory
	 * @param fileName the file name
	 * @return the string
	 */
	public static String addHadoopResource(Configuration configuration, Cluster cluster, String hadoopConfDir, String fileName) {
		String filePath = RemotingUtil.copyAndGetConfigurationFilePath(cluster, hadoopConfDir, fileName);
		configuration.addResource(new Path(filePath + File.separator + fileName));
		return (filePath + File.separator + fileName);
	}
	
	/**
	 * Copy and get configuration file path for a particular worker node.
	 *
	 * @param cluster the cluster
	 * @param hadoopConfDir the hadoop conf dir
	 * @param hadoopConfigurationFile the hadoop configuration file
	 * @return the string
	 */
	public static String copyAndGetConfigurationFilePath(
			Cluster cluster, String hadoopConfDir, String hadoopConfigurationFile,String host) {
		
		
		String clusterName = cluster.getClusterName() + File.separator;
		
		String destinationReceiveDir = JumbuneInfo.getHome() + Constants.JOB_JARS_LOC  + clusterName + File.separator + host;
		//Checking if configuration file exists or not
		File file = new File(destinationReceiveDir + File.separator + hadoopConfigurationFile);
		if (file.exists()) {
			return destinationReceiveDir;
		}
		if(!hadoopConfDir.endsWith(File.separator)){
			hadoopConfDir = hadoopConfDir+File.separator;
		}
		String copyCommand = new StringBuilder().append("cp ").append(hadoopConfDir).append(hadoopConfigurationFile).append(" ").append(getAgentHome(cluster)).append("/jobJars/").
				append(clusterName).append(File.separator).append(host).toString();
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
		builder.addCommand(MAKE_JOBJARS_DIR_ON_AGENT + clusterName + File.separator + host, false, null,CommandType.FS)
		.addCommand(copyCommand, false, null, CommandType.FS);
		Remoter remoter = getRemoter(cluster, JumbuneInfo.getHome());
		
		if(hadoopConfigurationFile.contains(Constants.CORE_SITE_XML) || hadoopConfigurationFile.contains(Constants.HDFS_SITE_XML)){
			remoter.fireAndForgetCommand(builder.getCommandWritable());
			//If execution happended to fast, we won't be able to get a directory to find files for next command
			remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC  + clusterName, File.separator + Constants.JOB_JARS_LOC + clusterName + hadoopConfigurationFile);
		}else{
			//scp user@rm_host:hadoopConfDir/hadoopConfigurationFile AGENT_HOME/jobjars/clusterName/
			StringBuilder scpCommand;
			if(cluster.getAgents().getSshAuthKeysFile()!=null && cluster.getAgents().getSshAuthKeysFile().endsWith(".pem")){
				scpCommand = new StringBuilder("scp ").append("-i ").append(cluster.getAgents().getSshAuthKeysFile()).append(" ").append(cluster.getHadoopUsers().getFsUser())
						.append(Constants.AT_OP).append(host).append(Constants.COLON)
						.append(hadoopConfDir).append(File.separator).append(hadoopConfigurationFile)
						.append(Constants.SPACE).append(getAgentHome(cluster)).append(File.separator)
						.append(Constants.JOB_JARS_LOC).append(clusterName).append(File.separator).append(host).append(File.separator);
			}else{
			    scpCommand = new StringBuilder("scp ").append("-o StrictHostKeyChecking=no ").append(cluster.getHadoopUsers().getFsUser())
					.append(Constants.AT_OP).append(host).append(Constants.COLON)
					.append(hadoopConfDir).append(File.separator).append(hadoopConfigurationFile)
					.append(Constants.SPACE).append(getAgentHome(cluster)).append(File.separator)
					.append(Constants.JOB_JARS_LOC).append(clusterName).append(File.separator).append(host).append(File.separator);
			}
			builder = new CommandWritableBuilder(cluster);
			builder.addCommand(MAKE_JOBJARS_DIR_ON_AGENT + clusterName + File.separator + host, false, null, CommandType.FS)
					.addCommand(scpCommand.toString(), false, null, CommandType.FS);

			remoter.fireAndForgetCommand(builder.getCommandWritable());
			remoter.receiveLogFiles(File.separator + Constants.JOB_JARS_LOC + clusterName + File.separator + host,
					File.separator + Constants.JOB_JARS_LOC + clusterName + File.separator + host + File.separator + hadoopConfigurationFile);
		}
		return destinationReceiveDir;
	}

}