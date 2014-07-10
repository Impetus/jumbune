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
import org.jumbune.common.utils.ValidateInput;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
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
	 * @return YamlLoader
	 * @throws JumbuneException
	 */
	public YamlLoader runInSeperateThread(YamlConfig config,
			HttpReportsBean reports) throws JumbuneException {
		List<Processor> processors;
		if (ValidateInput.isEnable(config.getEnableStaticJobProfiling()) && !checkProfilingState()) {
			throw new JumbuneException(ErrorCodesAndMessages.COULD_NOT_EXECUTE_PROGRAM);
		}
		this.reports = reports;

		YamlLoader loader = new YamlLoader(config);
		processors = getProcessorChain(config, reports, HTTP_BASED);
		
		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setRootDirectory(loader.getRootDirectoryName());
		serviceInfo.setJumbuneHome(loader.getjHome());
		serviceInfo.setJumbuneJobName(loader.getYamlConfiguration()
				.getFormattedJumbuneJobName());
		if (loader.getYamlConfiguration().getsJumbuneHome() != null){
			serviceInfo.setSlaveJumbuneHome(loader.getYamlConfiguration()
					.getsJumbuneHome());
		}
		if (loader.getMasterInfo() != null){
			serviceInfo.setMaster(loader.getMasterInfo());
		}
		if (loader.getSlavesInfo() != null){
			serviceInfo.setSlaves(loader.getSlavesInfo());
		}
		try {
			persistYamlInfoForShutdownHook(loader,loader.getjHome());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage(),e);
		}
		HELPER.writetoServiceFile(serviceInfo);
		if (processors.size() > 0) {
			service = Executors.newFixedThreadPool(processors.size());
			int index = 1;
			for (Processor p : processors) {
				String processName = "PROCESS" + (index++);
				reports.addInitialStatus(processName);
				Handler handler = new Handler(p, loader, reports, processName);

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
		return loader;
	}

	/**
	 * 
	 * Thread for running each process in a separate thread
	 *
	 * 
	 */
	private class Handler implements Runnable {

		private Processor processor;
		private YamlLoader loader;
		private ReportsBean reports;
		private String processName;
		private Runnable processCompletionHandler;

		/**
		 * Constructor for Handler
		 * @param processor
		 * @param loader
		 * @param reports
		 * @param name
		 */
		public Handler(Processor processor, YamlLoader loader,
				ReportsBean reports, String name) {
			this.processor = processor;
			this.loader = loader;
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
				processor.process(loader, reports, params);
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
					cleanUpJumbuneAgentCurrentJobFolder(loader);
					cleanUpSlavesTempFldr(loader);
					LOGGER.info("Completed worker node cleanup");
				} catch (Exception e) {
					LOGGER.error("An exception occurred: "+e);
				}
			}
		}
	}
}