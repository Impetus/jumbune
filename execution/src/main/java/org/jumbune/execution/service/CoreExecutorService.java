package org.jumbune.execution.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.HttpReportsBean;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.Module;
import org.jumbune.common.beans.ReportsBean;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.scheduler.ScheduleTaskUtil;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.execution.beans.CommunityModule;
import org.jumbune.execution.beans.DataQualityTaskEnum;
import org.jumbune.execution.processor.DataQualityProcessor;
import org.jumbune.execution.processor.DebugProcessor;
import org.jumbune.execution.processor.Processor;
import org.jumbune.execution.processor.ProfilingProcessor;
import org.jumbune.execution.utils.ExecutionConstants;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.BasicJobConfig;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;


/**
 * Class that contains the changing logic of processors based on modules selected
 * by user. This class can be extended to create new services
 *
 * 
 */
public abstract class CoreExecutorService {

	private static final Logger LOGGER = LogManager
			.getLogger(CoreExecutorService.class);
	protected static final String REATTEMPT_TASK_SCHEDULING_TIME = "*/10 * * * *";

	protected static final boolean HTTP_BASED = false;
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
		return chainProcessor(modules, isCommandBased,config);
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
		return chainProcessor(modules, isCommandBased,config);
	}

	/**
	 * This method decides which all modules to be added for execution. if
	 * DebugAnalysis,SelfTuning are selected by default profiling will be
	 * enabled.
	 * 
	 * @param config
	 * @return
	 */
	private List<Module> addModules(Config config) {
		List<Module> modules = new ArrayList<Module>();
		boolean isAddProfiling = false;
		JobConfig jobConfig = (JobConfig)config;
		if (jobConfig.getEnableDataValidation().equals(Enable.TRUE)){
			modules.add(CommunityModule.DATA_QUALITY);
		}

		if (jobConfig.getEnableStaticJobProfiling().equals(Enable.TRUE)){
			isAddProfiling = true;
		}

		if (jobConfig.getDebugAnalysis().equals(Enable.TRUE)) {
			modules.add(CommunityModule.DEBUG_ANALYSER);
		}

		if (isAddProfiling){
			modules.add(CommunityModule.PROFILING);
		}
		
		if (jobConfig.getEnableDataProfiling().equals(Enable.TRUE)){
			modules.add(CommunityModule.DATA_QUALITY);
		}
		if (jobConfig.getEnableDataQualityTimeline().equals(Enable.TRUE)){
			modules.add(CommunityModule.DATA_QUALITY);
		}
		if (jobConfig.getEnableJsonDataValidation().equals(Enable.TRUE)){
			modules.add(CommunityModule.DATA_QUALITY);
		}
		if (jobConfig.getEnableXmlDataValidation().equals(Enable.TRUE)){
			modules.add(CommunityModule.DATA_QUALITY);
		}
		if (jobConfig.getIsDataSourceComparisonEnabled().equals(Enable.TRUE)){
			modules.add(CommunityModule.DATA_QUALITY);
		}
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
		List<DataQualityTaskEnum> alreadyAdded = new ArrayList<>(3);
		
		if (modules != null && modules.size() > 0) {

			Processor previous = null;
			for (Iterator<Module> iterator = modules.iterator(); iterator
					.hasNext();) {

				Module module = iterator.next();

				Processor current = getProcessor(module, isCommand,config, alreadyAdded);
				
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
	private Processor getProcessor(Module module, boolean isCommand, Config config, List<DataQualityTaskEnum> alreadyAdded) {

		Processor processor = null;
		JobConfig jobConfig = (JobConfig) config;
				
		if(module.getEnumValue()==CommunityModule.DATA_QUALITY.getEnumValue()){
			if(jobConfig.getIsDataSourceComparisonEnabled().equals(Enable.TRUE) && !alreadyAdded.contains(DataQualityTaskEnum.DATA_SOURCE_COMPARISON)){
				processor = new DataQualityProcessor(isCommand,DataQualityTaskEnum.DATA_SOURCE_COMPARISON);
				alreadyAdded.add(DataQualityTaskEnum.DATA_SOURCE_COMPARISON);
			} else if(jobConfig.getEnableDataQualityTimeline().equals(Enable.TRUE) && !alreadyAdded.contains(DataQualityTaskEnum.DATA_QUALITY_TIMELINE)){
				processor = new DataQualityProcessor(isCommand,DataQualityTaskEnum.DATA_QUALITY_TIMELINE);
				alreadyAdded.add(DataQualityTaskEnum.DATA_QUALITY_TIMELINE);
			} else if(jobConfig.getEnableDataValidation().equals(Enable.TRUE) && !alreadyAdded.contains(DataQualityTaskEnum.DATA_VALIDATION)){
				processor = new DataQualityProcessor(isCommand,DataQualityTaskEnum.DATA_VALIDATION);
				alreadyAdded.add(DataQualityTaskEnum.DATA_VALIDATION);
			} else if(jobConfig.getEnableDataProfiling().equals(Enable.TRUE) && !alreadyAdded.contains(DataQualityTaskEnum.DATA_PROFILING)){
				processor = new DataQualityProcessor(isCommand,DataQualityTaskEnum.DATA_PROFILING);
				alreadyAdded.add(DataQualityTaskEnum.DATA_PROFILING);
			}else if(jobConfig.getEnableXmlDataValidation().equals(Enable.TRUE) && !alreadyAdded.contains(DataQualityTaskEnum.XML_DATA_VALIDATION)){
				processor = new DataQualityProcessor(isCommand,DataQualityTaskEnum.XML_DATA_VALIDATION);
				alreadyAdded.add(DataQualityTaskEnum.XML_DATA_VALIDATION);
			}else if(jobConfig.getEnableJsonDataValidation().equals(Enable.TRUE) && !alreadyAdded.contains(DataQualityTaskEnum.JSON_DATA_VALIDATION)){
				processor = new DataQualityProcessor(isCommand,DataQualityTaskEnum.JSON_DATA_VALIDATION);
				alreadyAdded.add(DataQualityTaskEnum.JSON_DATA_VALIDATION);
			}
		}
		
		if(module.getEnumValue()==CommunityModule.PROFILING.getEnumValue()){
			processor = new ProfilingProcessor(isCommand);
		}

		if(module.getEnumValue()==CommunityModule.DEBUG_ANALYSER.getEnumValue()){
			processor = new DebugProcessor(isCommand);
		}
		return processor;
	}

	/**
	 * This method deletes the token file generated in start of servicing any
	 * request. So that any other request can be served
	 */
	protected void deleteTokenFile() {
		String tokenFilePath = JumbuneInfo.getHome() + ExecutionConstants.TEMP_DIR + ExecutionConstants.TOKEN_FILE;
		LOGGER.debug("Since all the process are complete so deleting the token file kept at "
				+ tokenFilePath);
		File fToken = new File(tokenFilePath);

		if (fToken.exists()) {
			fToken.delete();
			LOGGER.debug("The token file is deleted successfully!");
		}
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

	public void scheduleTask(Config config, boolean isReAttempt)
			throws JumbuneException {
		JobConfig jobConfig = (JobConfig) config ;
		String scheduleJobTiming = jobConfig.getJumbuneScheduleTaskTiming();
		ScheduleTaskUtil scheduleTasks = new ScheduleTaskUtil();
		try {
			scheduleTasks.scheduleJumbuneTaskAndCopyResources(jobConfig,
					isReAttempt);
			LOGGER.info("Job Scheduled at [" + scheduleJobTiming
					+ "] executed successfully!");
		} catch (IOException e) {
			LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
		}
	}

	protected boolean checkProfilingState() throws JumbuneException {
		String tokenFilePath = JumbuneInfo.getHome() + ExecutionConstants.TEMP_DIR + ExecutionConstants.TOKEN_FILE;

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
				LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
				return false;
			}

			return true;
		}
		return false;
	}
	
	/**
	 * This method cleans up the job name folder in temp directory on slaves.
	 *
	 * @param config the config
	 * @throws JumbuneException the hTF exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	protected static  void cleanUpSlavesTempFldr(JumbuneRequest jumbuneRequest) throws JumbuneException, IOException, InterruptedException
	{
		Cluster cluster = jumbuneRequest.getCluster();
		ExecutorService cleanUpSlavesService = null;
		LOGGER.debug("Starting clean up process................");
		try {
			List<String> workerHosts = cluster.getWorkers().getHosts();
			cleanUpSlavesService = Executors.newFixedThreadPool(workerHosts.size());
			for (String workerHost : workerHosts) {
				CleanUpSlaves cleanUpSlaves = new CleanUpSlaves(jumbuneRequest, workerHost);
				cleanUpSlavesService.execute(cleanUpSlaves);
			}

		} catch (Exception e) {
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
		} finally {
			cleanUpSlavesService.shutdown();
		}
	}

	protected void cleanUpJumbuneAgentCurrentJobFolder(JumbuneRequest jumbuneRequest) {
		//Remove Agent Home/Job jar/Jobname folder, this is skipped only in case if cluster monitoring
		JobConfig jobConfig = (JobConfig) jumbuneRequest.getConfig();
		Cluster cluster = jumbuneRequest.getCluster();	
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster);
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
	}
	/**
	 * The Class CleanUpSlaves.
	 */
	private static class CleanUpSlaves implements Runnable {
	
	/** The Config. */
	private JumbuneRequest jumbuneRequest;
	
	/** The host node. */
	private String hostNode;
	
	/**
	 * Instantiates a new clean up slaves.
	 *
	 * @param config the config
	 * @param hostNode the host node
	 */
	public CleanUpSlaves(JumbuneRequest jumbuneRequest,String hostNode)
	{
		this.jumbuneRequest = jumbuneRequest;
		this.hostNode=hostNode;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	
	@Override
	public void run() {
		
		JobConfig jobConfig = (JobConfig) jumbuneRequest.getConfig();
		Cluster cluster = jumbuneRequest.getCluster();	
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		StringBuilder cleanLocationStrBuilder = new StringBuilder()
	    .append(RemotingConstants.REMOVE_FOLDER)
		.append(RemotingConstants.SINGLE_SPACE).append(jobConfig.getTempDirectory())
		.append(Constants.JOB_JARS_LOC).append(jobConfig.getJumbuneJobName());

		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, hostNode);
		builder.addCommand(cleanLocationStrBuilder.toString(), false, null, CommandType.FS);//.setApiInvokeHints(ApiInvokeHintsEnum.JOB_EXECUTION);
		remoter.fireAndForgetCommand(builder.getCommandWritable());

		LOGGER.debug("Cleaned jumbune generated temp directory and logs");
	}
	
}
	
	protected void persistJsonInfoForShutdownHook(JumbuneRequest jumbuneRequest, String agentHome) throws IOException{
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		JobConfig jobConfig = (JobConfig) jumbuneRequest.getConfig();
		Cluster cluster = jumbuneRequest.getCluster();
		
		Object[] workers = cluster.getWorkers().getHosts().toArray();
		String[] workerHosts = Arrays.copyOf(workers, workers.length, String[].class);
		BasicJobConfig agentConfig = new BasicJobConfig(jobConfig.getJumbuneJobName(),
				cluster.getNameNode(), cluster.getJumbuneAgent().getPort());		
		agentConfig.setUser(cluster.getHadoopUsers().getFsUser());
		agentConfig.setSshAuthKeysFile(cluster.getAgents().getSshAuthKeysFile());
		agentConfig.setWorkers(workerHosts);
		agentConfig.setTmpDir(jobConfig.getTempDirectory());
		try{
			//persisting  object
			String jsonFile = JumbuneInfo.getHome() + JSON_FILE;
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
		RemotingUtil.sendJsonInfoToAgent(cluster);
}
	
	protected void createJobJarFolderOnAgent(JumbuneRequest jumbuneRequest){

		Remoter remoter = RemotingUtil.getRemoter(jumbuneRequest.getCluster(),"");
		CommandWritableBuilder builder = new CommandWritableBuilder(jumbuneRequest.getCluster());
		JobConfig jobConfig = (JobConfig) jumbuneRequest.getConfig();
		String agentJobJarPath = MAKE_JOBJARS_DIR_ON_AGENT + jobConfig.getJumbuneJobName();
		builder.addCommand(agentJobJarPath, false, null, CommandType.FS);
		remoter.fireAndForgetCommand(builder.getCommandWritable());
		
	}
	

}
