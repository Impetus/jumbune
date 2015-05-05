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
import java.util.concurrent.TimeUnit;

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
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.execution.beans.CommunityModule;
import org.jumbune.execution.beans.DataQualityTaskEnum;
import org.jumbune.execution.processor.DataQualityProcessor;
import org.jumbune.execution.processor.DebugProcessor;
import org.jumbune.execution.processor.Processor;
import org.jumbune.execution.processor.ProfilingProcessor;
import org.jumbune.execution.utils.ExecutionConstants;
import org.jumbune.execution.utils.ExecutionUtil;
import org.jumbune.execution.utils.ProcessHelper;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.BasicJobConfig;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.utils.exception.JumbuneException;

/**
 * Class that contains the changing logic of processors based on modules selected
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
	private static final String JSON_FILE = "/jsonInfo.ser";
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
		return chainProcessor(modules, isCommandBased, config);
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
		return chainProcessor(modules, isCommandBased, config);
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

		JobConfig jobConfig = (JobConfig)config;
		if (jobConfig.getEnableDataValidation().equals(Enable.TRUE)) {
			modules.add(CommunityModule.DATA_QUALITY);
		}

		if (jobConfig.getEnableStaticJobProfiling().equals(Enable.TRUE)) {
			isAddProfiling = true;
		}

		if (jobConfig.getDebugAnalysis().equals(Enable.TRUE)) {
			modules.add(CommunityModule.DEBUG_ANALYSER);
		}

		if (isAddProfiling) {
			modules.add(CommunityModule.PROFILING);
		}
		
		if (jobConfig.getEnableDataProfiling().equals(Enable.TRUE)){
			modules.add(CommunityModule.DATA_QUALITY);
		}
		if (jobConfig.getEnableDataQualityTimeline().equals(Enable.TRUE)){
			modules.add(CommunityModule.DATA_QUALITY);
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
			boolean isCommand,Config config) {
		List<Processor> processors = new ArrayList<Processor>();

		if (modules != null && modules.size() > 0) {

			Processor previous = null;
			for (Iterator<Module> iterator = modules.iterator(); iterator
					.hasNext();) {

				Module module = iterator.next();

				Processor current = getProcessor(module, isCommand, config);

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
	 * @param config 
	 * @return processor
	 */
	private Processor getProcessor(Module module, boolean isCommand, Config config) {

		Processor processor = null;
		JobConfig jobConfig = (JobConfig) config;
		
		if(module.getEnumValue()==CommunityModule.DATA_QUALITY.getEnumValue() && jobConfig.getEnableDataQualityTimeline().equals(Enable.TRUE)){
			processor = new DataQualityProcessor(isCommand,DataQualityTaskEnum.DATA_QUALITY_TIMELINE);
		}
	
		if(module.getEnumValue()==CommunityModule.DATA_QUALITY.getEnumValue() && jobConfig.getEnableDataValidation().equals(Enable.TRUE)){
			processor = new DataQualityProcessor(isCommand,DataQualityTaskEnum.DATA_VALIDATION);
		}

		if(module.getEnumValue()==CommunityModule.PROFILING.getEnumValue()){
			processor = new ProfilingProcessor(isCommand);
		}

		if(module.getEnumValue()==CommunityModule.DEBUG_ANALYSER.getEnumValue()){
			processor = new DebugProcessor(isCommand);
		}
		
		if(module.getEnumValue()==CommunityModule.DATA_QUALITY.getEnumValue() && jobConfig.getEnableDataProfiling().equals(Enable.TRUE)){
			processor = new DataQualityProcessor(isCommand,DataQualityTaskEnum.DATA_PROFILING);
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
		String tokenFilePath = JobConfig.getJumbuneHome() + TEMP_DIR + TOKEN_FILE;
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
	protected void cleanUpSlavesTempFldr(Config config)
			throws JumbuneException, IOException, InterruptedException {

		JobConfig jobConfig = (JobConfig)config;
		ExecutorService cleanUpSlavesservice = null;
		List<Slave> listSlave = jobConfig.getLogDefinition().getSlaves();
		LOGGER.info("Starting clean up process................");
		for (Slave slaveDefinition : listSlave) {
			String[] hostsNode = slaveDefinition.getHosts();
			try {
				cleanUpSlavesservice = Executors.newFixedThreadPool(listSlave
						.size());
				for (String hostNode : hostsNode) {
					CleanUpSlaves cleanUpSlaves = new CleanUpSlaves(config,
							hostNode);
					cleanUpSlavesservice.execute(cleanUpSlaves);
				}

			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				cleanUpSlavesservice.shutdown();
				cleanUpSlavesservice.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			}
		}
	}
	
	protected void cleanUpJumbuneAgentCurrentJobFolder(Config config){
		//Remove Agent Home/Job jar/Jobname folder, this is skipped only in case if cluster monitoring
		JobConfig jobConfig = (JobConfig)config;
		Master master = jobConfig.getLogDefinition().getMaster();
		String hostMaster = master.getHost();
		Remoter remoter = new Remoter(hostMaster, Integer.valueOf(master
				.getAgentPort()));
		CommandWritableBuilder builder = new CommandWritableBuilder();
		if (!jobConfig.getHadoopJobProfile()
				.getEnumValue()) {
			StringBuilder cleanLocationAgentStrBuilder = new StringBuilder()
					.append(RemotingConstants.REMOVE_FOLDER)
					.append(RemotingConstants.SINGLE_SPACE)
					.append(RemotingConstants.AGENT_HOME)
					.append(Constants.JOB_JARS_LOC)
					.append(jobConfig.getJumbuneJobName());
			LOGGER.debug("Cleanup agent temporary directories command ["
					+ cleanLocationAgentStrBuilder + "]");
			
			builder.addCommand(cleanLocationAgentStrBuilder.toString(),
					false, null, CommandType.FS);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
		}
		remoter.close();
	}

	/**
	 * The Class CleanUpSlaves.
	 */
	private static class CleanUpSlaves implements Runnable {

		/** The loader. */
		private JobConfig config;

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
		public CleanUpSlaves(Config config, String hostNode) {
			this.config = (JobConfig) config;
			this.hostNode = hostNode;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			Master master = config.getLogDefinition().getMaster();
			String hostMaster = master.getHost();
			JobConfig jobConfig = (JobConfig) config;
			Remoter remoter = new Remoter(hostMaster, Integer.valueOf(master
					.getAgentPort()));
			String jumbuneHome = jobConfig.getSlaveWorkingDirectory();
			//In "working directory in worker node/jobjars", remove the currently ran job name folder
			StringBuilder cleanLocationStrBuilder = new StringBuilder()
					.append(RemotingConstants.REMOVE_FOLDER)
					.append(RemotingConstants.SINGLE_SPACE).append(jumbuneHome)
					.append(Constants.JOB_JARS_LOC)
					.append(jobConfig.getJumbuneJobName());
			LOGGER.debug("Cleanup temporary directories command ["
					+ cleanLocationStrBuilder + "]");
			CommandWritableBuilder builder = new CommandWritableBuilder();
			builder.populate(config, hostNode);
			builder.addCommand(cleanLocationStrBuilder.toString(), false, null, CommandType.FS);
			remoter.fireAndForgetCommand(builder.getCommandWritable());
			
			//removing top.txt file under slave working directory
			cleanLocationStrBuilder = new StringBuilder()
					.append(RemotingConstants.REMOVE)
					.append(RemotingConstants.SINGLE_SPACE)
					.append(jumbuneHome)
					.append(ExecutionConstants.TOPTXTFILE);
			LOGGER.debug("Cleanup top txt file on slave command ["+ cleanLocationStrBuilder + "]");
			builder = new CommandWritableBuilder();
			builder.addCommand(cleanLocationStrBuilder.toString(), false, null, CommandType.FS);
			builder.populate(config, hostNode);
			remoter.fireAndForgetCommand(builder.getCommandWritable());

			remoter.close();
			LOGGER.info("Cleaned jumbune generated temp directory and logs");
		}

	}
	
	protected void persistJsonInfoForShutdownHook(Config config, String agentHome) throws IOException{
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		JobConfig jobConfig = (JobConfig)config;
	
		
		List<String> hosts = new ArrayList<String>();
		String[] slaveAgentList = null;
		for(Slave host: jobConfig.getSlaves()){
			for (String str : host.getHosts()) {
				hosts.add(str);
			}
		}
		slaveAgentList = hosts.toArray(new String[hosts.size()]);
		BasicJobConfig agentConfig = new BasicJobConfig(jobConfig.getJumbuneJobName(),
				jobConfig.getMaster().getHost(), jobConfig.getMaster().getAgentPort());
		
		agentConfig.setUser(jobConfig.getMaster().getUser());
		agentConfig.setRsaFile(jobConfig.getMaster().getRsaFile());
		agentConfig.setDsaFile(jobConfig.getMaster().getDsaFile());
		agentConfig.setSlaves(slaveAgentList);
		agentConfig.setTmpDir(jobConfig.getSlaveWorkingDirectory());
		try{
			//persisting  object
			String jsonFile = JobConfig.getJumbuneHome()+JSON_FILE;
			File file = new File(jsonFile);
			if(file.exists()){
				file.delete();
			}
			fout = new FileOutputStream(jsonFile, true);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(agentConfig);
		}finally{
			if(oos != null){
			oos.close();
			}
		}
		//sends the file to Agent_Home
		RemotingUtil.sendJsonInfoToAgent(config, agentConfig);
}
	
	protected void createJobJarFolderOnAgent(JobConfig jobConfig){
		
		
		Remoter remoter = RemotingUtil.getRemoter(jobConfig,"");
		CommandWritableBuilder builder = new CommandWritableBuilder();
		String agentJobJarPath = MAKE_JOBJARS_DIR_ON_AGENT + jobConfig.getJumbuneJobName();
		builder.addCommand(agentJobJarPath, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		
	}

}
