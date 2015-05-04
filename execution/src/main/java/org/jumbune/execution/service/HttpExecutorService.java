package org.jumbune.execution.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.HttpReportsBean;
import org.jumbune.common.beans.ReportsBean;
import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ValidateInput;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.execution.processor.Processor;
import org.jumbune.execution.utils.ExecutionConstants;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;


/**
 * 
 * Service class that should be used for executing application flow from
 * servlet.
 * 
 * 
 */
public class HttpExecutorService extends CoreExecutorService {

	private static final Logger LOGGER = LogManager
			.getLogger(HttpExecutorService.class);

	private ReportsBean reports;
	
	private ExecutorService service;

	/**
	 * public constructor for HttpExecutorService
	 * @throws JumbuneException
	 */
	public HttpExecutorService() throws JumbuneException {
		super();
	}

	/**
	 * stops the execution of current job in Jumbune
	 */
	public void stopExecution() {
		LOGGER.info("Stopping execution of [Http Executor] service");

		// TODO : implementing logic of killing jobs using job id
		reports.stopExectution();

		if (service != null) {
			try {
				service.shutdown();
				service.awaitTermination(ExecutionConstants.TEN, TimeUnit.SECONDS);

			} catch (InterruptedException e) {

			} finally {

				if (!service.isShutdown()) {
					List<Runnable> tasks = service.shutdownNow();
					LOGGER.debug(tasks.size()
							+ "tasks were shutdown forcefully.");
				}
			}
		}
	}



	/**
	 * This method will identify application flow and execute each processor
	 * chain in a separate thread
	 * 
	 * @param config
	 * @param reports
	 * @return JobConfig
	 * @throws JumbuneException
	 */
	public Config runInSeperateThread(Config config,
			HttpReportsBean reports) throws JumbuneException {
		List<Processor> processors;
		JobConfig jobConfig = (JobConfig)config;
		if (ValidateInput.isEnable(jobConfig.getEnableStaticJobProfiling()) && !checkProfilingState()) {
			throw new JumbuneException(ErrorCodesAndMessages.COULD_NOT_EXECUTE_PROGRAM);
		}
		this.reports = reports;

		
		processors = getProcessorChain(jobConfig, reports, HTTP_BASED);
		createJobJarFolderOnAgent(jobConfig);
		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setRootDirectory(Constants.JOB_JARS_LOC);
		serviceInfo.setJumbuneHome(JobConfig.getJumbuneHome());
		serviceInfo.setJumbuneJobName(jobConfig
				.getFormattedJumbuneJobName());
		if (jobConfig.getSlaveWorkingDirectory() != null){
			serviceInfo.setSlaveJumbuneHome(jobConfig
					.getSlaveWorkingDirectory());
		}
		if (jobConfig.getMaster() != null){
			serviceInfo.setMaster(jobConfig.getMaster());
		}
		if (jobConfig.getSlaves() != null){
			serviceInfo.setSlaves(jobConfig.getSlaves());
		}
		try {
			persistJsonInfoForShutdownHook(config,JobConfig.getJumbuneHome());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
		}
		HELPER.writetoServiceFile(serviceInfo);
		if (processors.size() > 0) {
			service = Executors.newFixedThreadPool(processors.size());
			int index = 1;
			for (Processor p : processors) {
				String processName = "PROCESS" + (index++);
				reports.addInitialStatus(processName);
				
				Handler handler = new Handler(p, config, reports, processName);

				// setting handler to stop services after execution
				handler.setProcessCompletionHandler(new Thread() {
					@Override
					public void run() {
						//used to stop services, as of now we have no services to stop so block left emptied 
					}
				});
				service.execute(handler);
			}
		} else {
			LOGGER.error("No processors identified to execute");			
		}
		return config;
	}

	/**
	 * 
	 * Thread for running each process in a separate thread
	 *
	 * 
	 */
	private class Handler implements Runnable {

		private Processor processor;
		private ReportsBean reports;
		private String processName;
		private Runnable processCompletionHandler;
		private Config config;

		/**
		 * Constructor for Handler
		 * @param processor
		 * @param loader
		 * @param reports
		 * @param name
		 */
		public Handler(Processor processor, Config config,
				ReportsBean reports, String name) {
			this.processor = processor;
			this.config = config;
			this.reports = reports;
			this.processName = name;	
		}

		public void setProcessCompletionHandler(
				Runnable processCompletionHandler) {
			this.processCompletionHandler = processCompletionHandler;
		}	

		/**
		 * executes the job for a given processor
		 */
		@Override
		public void run() {
			try {
				Map<Parameters, String> params = new HashMap<Parameters, String>();
				params.put(Parameters.PROCESSOR_KEY, processName);
				processor.process(config, reports, params);
			} catch (JumbuneException e) {
				LOGGER.error(processName + " completed with errors !!!", e);
			} finally {
				// marking the process as complete
				reports.markProcessAsComplete(processName);
				if (!reports.isAnyProcessRunning()
						&& processCompletionHandler != null) {
					LOGGER.info("Job Processing Completed. Shutting down service...");
					new Thread(processCompletionHandler).start();

				}
				try {
					stopExecution();
					cleanUpJumbuneAgentCurrentJobFolder(config);
					cleanUpSlavesTempFldr(config);
					LOGGER.info("Completed worker node cleanup");
				} catch (Exception e) {
					LOGGER.error("An exception occurred: "+e);
				}
			}
		}
	}
}