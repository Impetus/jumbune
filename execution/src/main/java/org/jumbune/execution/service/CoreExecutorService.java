package org.jumbune.execution.service;

import static org.jumbune.execution.utils.ExecutionConstants.TEMP_DIR;
import static org.jumbune.execution.utils.ExecutionConstants.TOKEN_FILE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import org.jumbune.common.beans.ReportsBean.ReportName;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.execution.processor.DataValidationProcessor;
import org.jumbune.execution.processor.DebugProcessor;
import org.jumbune.execution.processor.Processor;
import org.jumbune.execution.processor.ProfilingProcessor;
import org.jumbune.execution.utils.ExecutionConstants;
import org.jumbune.execution.utils.ExecutionUtil;
import org.jumbune.execution.utils.ProcessHelper;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.ApiInvokeHintsEnum;
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

	/**
	 * Method that performs the desired chaining of the processors.
	 * 
	 * @param config
	 * @param isCommandBased
	 * @return List of processors
	 */
	protected List<Processor> getProcessorChain(YamlConfig config,
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
	protected List<Processor> getProcessorChain(YamlConfig config,
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
	private List<Module> addModules(YamlConfig config) {
		List<Module> modules = new ArrayList<Module>();
		boolean isAddProfiling = false;

		if (config.getEnableDataValidation().equals(Enable.TRUE)) {
			modules.add(Module.DATA_VALIDATION);
		}

		if (config.getEnableStaticJobProfiling().equals(Enable.TRUE)) {
			isAddProfiling = true;
		}

		if (config.getDebugAnalysis().equals(Enable.TRUE)) {
			modules.add(Module.DEBUG_ANALYSER);
		}

		if (isAddProfiling) {
			modules.add(Module.PROFILING);
		}
		Collections.sort(modules);
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
		switch (module) {

		case DATA_VALIDATION:
			processor = new DataValidationProcessor(isCommand);
			break;

		case PROFILING:
			processor = new ProfilingProcessor(isCommand);
			break;

		case DEBUG_ANALYSER:
			processor = new DebugProcessor(isCommand);
			break;

		}

		return processor;
	}

	protected void persistReports(ReportsBean reports, String reportFolderPath)
			throws IOException {
		List<Module> completedReports = reports.getCompletedReports();

		for (Module reportType : completedReports) {

			Map<ReportName, String> report = reports.getReport(reportType);

			if (report != null) {
				for (ReportName key : report.keySet()) {
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
	protected void cleanUpSlavesTempFldr(YamlLoader loader)
			throws JumbuneException, IOException, InterruptedException {

		ExecutorService cleanUpSlavesservice = null;
		List<Slave> listSlave = loader.getLogDefinition().getSlaves();
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
	
	protected void cleanUpJumbuneAgentCurrentJobFolder(YamlLoader loader){
		//Remove Agent Home/Job jar/Jobname folder, this is skipped only in case if cluster monitoring

		Master master = loader.getLogDefinition().getMaster();
		String hostMaster = master.getHost();
		Remoter remoter = new Remoter(hostMaster, Integer.valueOf(master
				.getAgentPort()));
		CommandWritableBuilder builder = new CommandWritableBuilder();
		
		if (!loader.getYamlConfiguration().getHadoopJobProfile()
				.getEnumValue()) {
			StringBuilder cleanLocationAgentStrBuilder = new StringBuilder()
					.append(RemotingConstants.REMOVE_FOLDER)
					.append(RemotingConstants.SINGLE_SPACE)
					.append(RemotingConstants.AGENT_HOME)
					.append(Constants.JOB_JARS_LOC)
					.append(loader.getJumbuneJobName());
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
		public CleanUpSlaves(YamlLoader loader, String hostNode) {
			this.loader = loader;
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
			String jumbuneHome = loader.getYamlConfiguration()
					.getsJumbuneHome();
			YamlConfig config = loader.getYamlConfiguration();
			Remoter remoter = new Remoter(hostMaster, Integer.valueOf(master
					.getAgentPort()));
			
			//In "working directory in worker node/jobjars", remove the currently ran job name folder
			StringBuilder cleanLocationStrBuilder = new StringBuilder()
					.append(RemotingConstants.REMOVE_FOLDER)
					.append(RemotingConstants.SINGLE_SPACE).append(jumbuneHome)
					.append(Constants.JOB_JARS_LOC)
					.append(loader.getJumbuneJobName());
			LOGGER.debug("Cleanup temporary directories command ["
					+ cleanLocationStrBuilder + "]");
			CommandWritableBuilder builder = new CommandWritableBuilder();
			builder.populate(config, hostNode);
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
			builder.populate(config, hostNode);
			remoter.fireAndForgetCommand(builder.getCommandWritable());

			remoter.close();
			LOGGER.info("Cleaned jumbune generated temp directory and logs");
		}

	}

}
