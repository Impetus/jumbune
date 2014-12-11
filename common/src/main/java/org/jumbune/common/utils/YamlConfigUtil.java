package org.jumbune.common.utils;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.beans.Master;
import org.jumbune.common.yaml.config.Config;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;



/**
 * The Class YamlConfigUtil.
 */
public final class YamlConfigUtil {
	
	
	/** The Constant LOGGER. */
	public static final Logger LOGGER = LogManager.getLogger(YamlConfigUtil.class);
	
	/**
	 * Instantiates a new yaml config util.
	 */
	private YamlConfigUtil(){
	}

	/**
	 * The services yaml path is fixed and is in user's home directory.
	 *
	 * @return the service yaml path
	 */
	public static String getServiceJsonPath() {
	
		String serviceYamlLoc = YamlLoader.getjHome();
		File currentDir = new File(serviceYamlLoc);
		String currentDirPath = currentDir.getAbsolutePath();
	
		currentDirPath = currentDirPath.substring(0, currentDirPath.length());
	
		StringBuilder sb = new StringBuilder(currentDirPath).append(System.getProperty("file.separator")).append("resources")
				.append(System.getProperty("file.separator")).append("services.json");
		return sb.toString();
	}

	/**
	 * Checks if is jumbune supplied jar present.
	 * 
	 * @param config
	 *            the config
	 * @return true, if is jumbune supplied jar present
	 */
	public static boolean isJumbuneSuppliedJarPresent(Config config) {
		YamlConfig yamlConfig = (YamlConfig)config;
		Master master = yamlConfig.getMaster();
		Remoter remoter = new Remoter(master.getHost(), Integer.valueOf(master.getAgentPort()));
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand("ls lib/", false, null, CommandType.FS);
		String result = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		return (result.length() > 0) ? true : false;
	}

	/**
	 * Checks if is mR job jar present.
	 *
	 * @param config the config
	 * @param jarFilepath the jar filepath
	 * @return true, if is mR job jar present
	 */
	public static boolean isMRJobJarPresent(Config config, String jarFilepath){
		YamlConfig yamlConfig = (YamlConfig)config;
		Master master = yamlConfig.getMaster();
		File resourceDir = new File(jarFilepath);
		if(resourceDir.exists()){
			Remoter remoter = new Remoter(master.getHost(), Integer.valueOf(master.getAgentPort()));
			CommandWritableBuilder builder = new CommandWritableBuilder();
			builder.addCommand("ls "+jarFilepath, false, null, CommandType.FS);
			String result = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
			remoter.close();
			return (result.length() > 0) ? true : false;
		}else{
			return false;
		}

	}

	/**
	 * Send lib jar command.
	 *
	 * @param remoter the remoter
	 * @param config the config
	 * @param command the command
	 */
	public static void sendLibJarCommand(Remoter remoter, Config config, String command) {
		
		CommandWritableBuilder builder = new CommandWritableBuilder();
		builder.addCommand(command, false, null, CommandType.FS).populate(config, null);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
	
	}

	/**
	 * Send jumbune supplied jar on agent.
	 * 
	 * @param config
	 *            the config
	 * @param cse
	 *            the cse
	 * @param agentHome
	 *            the agent home
	 */
	public static void sendJumbuneSuppliedJarOnAgent(Config config, ClasspathElement cse, String agentHome) {
		String jh = YamlLoader.getjHome();
		Remoter remoter = RemotingUtil.getRemoter(config, jh);
		String hadoopHome = RemotingUtil.getHadoopHome(remoter, config);
		String[] files = cse.getFiles();
		for (String string : files) {
			remoter.sendJar("lib/", string.replace(agentHome, jh));
	
			if (string.contains("log4j")) {
				StringBuilder copyJarToHadoopLib = new StringBuilder().append(Constants.COPY_COMMAND).append(string).append(" ").append(hadoopHome)
						.append(Constants.LIB_DIRECTORY);
				sendLibJarCommand(remoter, config, copyJarToHadoopLib.toString());
			}
		}
		remoter.close();
	}

	/**
	 * Send mr job jar on agent.
	 *
	 * @param config the config
	 * @param jarFilepath the jar filepath
	 */
	public static void sendMRJobJarOnAgent(Config config, String jarFilepath){
		YamlConfig yamlConfig = (YamlConfig)config;
		String jh =YamlLoader.getjHome() + "/";
		Remoter remoter = RemotingUtil.getRemoter(config, jh);
		File resourceDir =new File(jarFilepath);
		File[] files=resourceDir.listFiles();
			for(File file : files){
				if (file.getName().endsWith(".tmp")) {
					file.delete();
				}else{
					String filename = file.getAbsolutePath();
					
					String relativeAgentPath = Constants.JOB_JARS_LOC + "/"+yamlConfig.getFormattedJumbuneJobName()+Constants.MR_RESOURCES;
					String resourceFolder = System.getenv("AGENT_HOME") + "/"+Constants.JOB_JARS_LOC+"/"
						+yamlConfig.getFormattedJumbuneJobName()+ Constants.MR_RESOURCES;
					File resourceDirAgent = new File(resourceFolder);
					if (!resourceDirAgent.exists()) {
						resourceDirAgent.mkdirs();
					}
					remoter.sendJar(relativeAgentPath, filename);
			}
			}
		remoter.close();
	}

	/**
	 * Check if jumbune home ends with slash.
	 *
	 * @param config checks of Jumbune Home ends with slash or not.
	 */
	public static  void checkIfJumbuneHomeEndsWithSlash(Config config) {
		YamlConfig yamlConfig = (YamlConfig)config;
		if (!(yamlConfig.getsJumbuneHome().endsWith(File.separator))) {
			String jumbuneHome = yamlConfig.getsJumbuneHome();
			yamlConfig.setsJumbuneHome(jumbuneHome + File.separator);
		}
	}

	/**
	 * This method replaces path with jumbune home.
	 *
	 * @param path the path
	 * @return the string[]
	 */
	public static  String[] replaceJumbuneHome(String[] path) {
		String jHome = YamlLoader.getjHome();
		if (path != null) {
			for (int i = 0; i < path.length; i++) {
				String filePath = path[i];
				if (filePath.contains(Constants.JUMBUNE_ENV_VAR_NAME)) {
					filePath = filePath.replace(Constants.JUMBUNE_ENV_VAR_NAME, jHome);
					path[i] = filePath;
				}
			}
		}
		return path;
	}
	
}
