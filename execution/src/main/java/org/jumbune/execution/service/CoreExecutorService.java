package org.jumbune.execution.service;

import static org.jumbune.execution.utils.ExecutionConstants.TEMP_DIR;
import static org.jumbune.execution.utils.ExecutionConstants.TOKEN_FILE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.HttpReportsBean;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.Module;
import org.jumbune.common.beans.ReportsBean;
import org.jumbune.common.beans.Slave;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.yaml.config.Config;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.execution.beans.CommunityModule;
import org.jumbune.execution.processor.DataValidationProcessor;
import org.jumbune.execution.processor.DebugProcessor;
import org.jumbune.execution.processor.Processor;
import org.jumbune.execution.processor.ProfilingProcessor;
import org.jumbune.execution.utils.ExecutionConstants;
import org.jumbune.execution.utils.ExecutionUtil;
import org.jumbune.execution.utils.ProcessHelper;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.BasicYamlConfig;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.utils.exception.JumbuneException;

/**
 * Class that contains the chaning logic of processors based on modules selected
 * by user. This class can be extended to create new services
 * 
 * 
 */
public abstract class CoreExecutorService {

	private static final Logger LOGGER = LogManager
			.getLogger(CoreExecutorService.class);
	protected static final String NO = "NO";

	protected static final boolean HTTP_BASED = false;
	protected static final boolean CONSOLE_BASED = true;
	protected static final ProcessHelper HELPER = new ProcessHelper();
	private static final String YAML_FILE = "/yamlInfo.ser";
	/** The Constant MAKE_JOBJARS_DIR_ON_AGENT. */
	private static final String MAKE_JOBJARS_DIR_ON_AGENT = "mkdir -p AGENT_HOME/jobJars/";
	/**
	 * Method that performs the desired chaining of the processors.
	 * 
	 * @param config
	 * @param isCommandBased
	 * @return List of processors
	 */
	protected List<Processor> getProcessorChain(Config config,
			boolean isCommandBased) {
		List<Module> modules = addModules(config);
		return chainProcessor(modules, isCommandBased);
	}

	/**
	 * Method that performs the desired chaining of the processors to be invoked
	 * by Http request
	 * 
	 * @param config
	 * @param isCommandBased
	 * @return List of processors
	 */
	protected List<Processor> getProcessorChain(Config config,
			HttpReportsBean bean, boolean isCommandBased) {
		List<Module> modules = addModules(config);
		return chainProcessor(modules, isCommandBased);
	}

	/**
	 * This method decides which all modules to be added for execution. if
	 * DebugAnalysis is selected by default profiling will be enabled.
	 * 
	 * @param config
	 * @return
	 */
	private List<Module> addModules(Config config) {
		List<Module> modules = new ArrayList<Module>();
		boolean isAddProfiling = false;

		YamlConfig yamlConfig = (YamlConfig)config;
		if (yamlConfig.getEnableDataValidation().equals(Enable.TRUE)) {
			modules.add(CommunityModule.DATA_VALIDATION);
		}

		if (yamlConfig.getEnableStaticJobProfiling().equals(Enable.TRUE)) {
			isAddProfiling = true;
		}

		if (yamlConfig.getDebugAnalysis().equals(Enable.TRUE)) {
			modules.add(CommunityModule.DEBUG_ANALYSER);
		}

		if (isAddProfiling) {
			modules.add(CommunityModule.PROFILING);
		}
		//Collections.sort(modules);
		LOGGER.debug("Executable Modules [" + modules + "]");
		return modules;
	}

	/**
	 * Method that performs the chaining of processors
	 * 
	 * @param modules
	 * @param isCommand
	 * @return List of processors
	 */
	private List<Processor> chainProcessor(List<Module> modules,
			boolean isCommand) {
		List<Processor> processors = new ArrayList<Processor>();

		if (modules != null && modules.size() > 0) {

			Processor previous = null;
			for (Iterator<Module> iterator = modules.iterator(); iterator
					.hasNext();) {

				Module module = iterator.next();

				Processor current = getProcessor(module, isCommand);

				if (previous == null) {
					processors.add(current);
				} else {
					previous.chain(current);
				}
				previous = current;
			}
		}

		return processors;

	}

	/**
	 * Method that created the instance of processor class.
	 * 
	 * @param module
	 * @param isCommand
	 * @return processor
	 */
	private Processor getProcessor(Module module, boolean isCommand) {

		Processor processor = null;
		
		
		if(module.getEnumValue()==CommunityModule.DATA_VALIDATION.getEnumValue()){
			processor = new DataValidationProcessor(isCommand);
		}

		if(module.getEnumValue()==CommunityModule.PROFILING.getEnumValue()){
			processor = new ProfilingProcessor(isCommand);
		}

		if(module.getEnumValue()==CommunityModule.DEBUG_ANALYSER.getEnumValue()){
			processor = new DebugProcessor(isCommand);
		}

		

		return processor;
	}

	protected void persistReports(ReportsBean reports, String reportFolderPath)
			throws IOException {
		List<Module> completedReports = reports.getCompletedReports();

		for (Module reportType : completedReports) {

			Map<String, String> report = reports.getReport(reportType);

			if (report != null) {
				for (String key : report.keySet()) {
					String filepath = reportFolderPath + key.toString();
					String value = report.get(key);
					ConfigurationUtil.writeToFile(filepath, value, true);
				}
			}
		}
	}

	protected static synchronized boolean checkProfilingState()
			throws JumbuneException {
		String tokenFilePath = YamlLoader.getjHome() + TEMP_DIR + TOKEN_FILE;
		File fToken = new File(tokenFilePath);

		if (!fToken.exists()) {
			LOGGER.warn("Token file does not exist, proceeding with execution ..... location of token file was"
					+ tokenFilePath);

			File parentDir = new File(fToken.getParent());
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}
			try {
				fToken.createNewFile();
			} catch (IOException e) {
				LOGGER.error("error creating file ", e);
				return false;
			}

			return true;
		}
		return false;
	}

	protected String isQueueTask() {
		Scanner scanner = new Scanner(System.in);
		String question = "Some other job is currently executing, should this job be queued. If yes please provide name for task, else type 'no'";
		String answer = ExecutionUtil.readInputFromConsole(scanner,
				"Enter a valid name", question);
		return answer;
	}

	/**
	 * This method cleans up the job name folder in temp directory on slaves.
	 * 
	 * @param loader
	 *            the loader
	 * @throws JumbuneException
	 *             the hTF exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	protected void cleanUpSlavesTempFldr(Loader loader)
			throws JumbuneException, IOException, InterruptedException {

		YamlLoader yamlLoader = (YamlLoader)loader;
		ExecutorService cleanUpSlavesservice = null;
		List<Slave> listSlave = yamlLoader.getLogDefinition().getSlaves();
		LOGGER.info("Starting clean up process................");
		for (Slave slaveDefinition : listSlave) {
			String[] hostsNode = slaveDefinition.getHosts();
			try {
				cleanUpSlavesservice = Executors.newFixedThreadPool(listSlave
						.size());
				for (String hostNode : hostsNode) {
					CleanUpSlaves cleanUpSlaves = new CleanUpSlaves(loader,
							hostNode);
					cleanUpSlavesservice.execute(cleanUpSlaves);
				}

			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				cleanUpSlavesservice.shutdown();
			}
		}
	}
	
	protected void cleanUpJumbuneAgentCurrentJobFolder(Loader loader){
		//Remove Agent Home/Job jar/Jobname folder, this is skipped only in case if cluster monitoring
		YamlLoader yamlLoader = (YamlLoader)loader;
		Master master = yamlLoader.getLogDefinition().getMaster();
		String hostMaster = master.getHost();
		Remoter remoter = new Remoter(hostMaster, Integer.valueOf(master
				.getAgentPort()));
		CommandWritableBuilder builder = new CommandWritableBuilder();
		YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
		if (!yamlConfig.getHadoopJobProfile()
				.getEnumValue()) {
			StringBuilder cleanLocationAgentStrBuilder = new StringBuilder()
					.append(RemotingConstants.REMOVE_FOLDER)
					.append(RemotingConstants.SINGLE_SPACE)
					.append(RemotingConstants.AGENT_HOME)
					.append(Constants.JOB_JARS_LOC)
					.append(yamlLoader.getJumbuneJobName());
			LOGGER.debug("Cleanup agent temporary directories command ["
					+ cleanLocationAgentStrBuilder + "]");
			
			builder.addCommand(cleanLocationAgentStrBuilder.toString(),
					false, null);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
		}
		remoter.close();
	}

	/**
	 * The Class CleanUpSlaves.
	 */
	private static class CleanUpSlaves implements Runnable {

		/** The loader. */
		private YamlLoader loader;

		/** The host node. */
		private String hostNode;

		/**
		 * Instantiates a new clean up slaves.
		 * 
		 * @param loader
		 *            the loader
		 * @param hostNode
		 *            the host node
		 */
		public CleanUpSlaves(Loader loader, String hostNode) {
			this.loader = (YamlLoader) loader;
			this.hostNode = hostNode;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			Master master = loader.getLogDefinition().getMaster();
			String hostMaster = master.getHost();
			
			YamlConfig yamlConfig = (YamlConfig) loader.getYamlConfiguration();
			Remoter remoter = new Remoter(hostMaster, Integer.valueOf(master
					.getAgentPort()));
			String jumbuneHome = yamlConfig.getsJumbuneHome();
			//In "working directory in worker node/jobjars", remove the currently ran job name folder
			StringBuilder cleanLocationStrBuilder = new StringBuilder()
					.append(RemotingConstants.REMOVE_FOLDER)
					.append(RemotingConstants.SINGLE_SPACE).append(jumbuneHome)
					.append(Constants.JOB_JARS_LOC)
					.append(loader.getJumbuneJobName());
			LOGGER.debug("Cleanup temporary directories command ["
					+ cleanLocationStrBuilder + "]");
			CommandWritableBuilder builder = new CommandWritableBuilder();
			builder.populate(yamlConfig, hostNode);
			builder.addCommand(cleanLocationStrBuilder.toString(), false, null);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
			
			//removing top.txt file under slave working directory
			cleanLocationStrBuilder = new StringBuilder()
					.append(RemotingConstants.REMOVE)
					.append(RemotingConstants.SINGLE_SPACE)
					.append(jumbuneHome)
					.append(ExecutionConstants.TOPTXTFILE);
			LOGGER.debug("Cleanup top txt file on slave command ["+ cleanLocationStrBuilder + "]");
			builder = new CommandWritableBuilder();
			builder.addCommand(cleanLocationStrBuilder.toString(), false, null);
			builder.populate(yamlConfig, hostNode);
			remoter.fireAndForgetCommand(builder.getCommandWritable());

			remoter.close();
			LOGGER.info("Cleaned jumbune generated temp directory and logs");
		}

	}
	
	protected void persistYamlInfoForShutdownHook(Loader loader, String agentHome) throws IOException{
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		YamlLoader yamlLoader = (YamlLoader)loader;
		YamlConfig yamlConfig = (YamlConfig) yamlLoader.getYamlConfiguration();
		
		
		List<String> hosts = new ArrayList<String>();
		String[] slaveAgentList = null;
		for(Slave host: yamlConfig.getSlaves()){
			for (String str : host.getHosts()) {
				hosts.add(str);
			}
		}
		slaveAgentList = hosts.toArray(new String[hosts.size()]);
		BasicYamlConfig agentConfig = new BasicYamlConfig(yamlConfig.getJumbuneJobName(),
				yamlConfig.getMaster().getHost(), yamlConfig.getMaster().getAgentPort());
		
		agentConfig.setUser(yamlConfig.getMaster().getUser());
		agentConfig.setRsaFile(yamlConfig.getMaster().getRsaFile());
		agentConfig.setDsaFile(yamlConfig.getMaster().getDsaFile());
		agentConfig.setSlaves(slaveAgentList);
		agentConfig.setTmpDir(yamlConfig.getsJumbuneHome());
		try{
			//persisting  object
			String yamlFile = YamlLoader.getjHome()+YAML_FILE;
			File file = new File(yamlFile);
			if(file.exists()){
				file.delete();
			}
			fout = new FileOutputStream(yamlFile, true);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(agentConfig);
		}finally{
			if(oos != null){
			oos.close();
			}
		}
		//sends the file to Agent_Home
		RemotingUtil.sendYamlInfoToAgent(loader, agentConfig);
}
	
	protected void createJobJarFolderOnAgent(YamlLoader yamlLoader){
		
		Master master = yamlLoader.getLogDefinition().getMaster();
		String hostMaster = master.getHost();
		Remoter remoter = new Remoter(hostMaster, Integer.valueOf(master
				.getAgentPort()));
		CommandWritableBuilder builder = new CommandWritableBuilder();
		String agentJobJarPath = MAKE_JOBJARS_DIR_ON_AGENT + yamlLoader.getJumbuneJobName();
		builder.addCommand(agentJobJarPath, false, null);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		
	}

}
