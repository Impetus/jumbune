package org.jumbune.execution.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Module;
import org.jumbune.common.beans.ReportsBean;
import org.jumbune.common.beans.ServiceInfo;
import org.jumbune.common.utils.MessageLoader;
import org.jumbune.common.job.Config;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.execution.utils.ProcessHelper;
import org.jumbune.utils.exception.JumbuneException;


/**
 * Abstract processor class that should be extended to create any new processor.
 * This class maintains the complete life cycle of the jumbune job execution
 * process
 * 

 * 
 */
public abstract class BaseProcessor implements Processor {

	private static final Logger LOGGER = LogManager
			.getLogger(BaseProcessor.class);
	protected static final ProcessHelper processHelper = new ProcessHelper();
	protected static final MessageLoader MESSAGES = MessageLoader.getInstance();

	private Processor next;
	private ReportsBean reports;
	protected ServiceInfo serviceInfo;
	private boolean isCommandBased;
	private Config config;
	
	

	protected BaseProcessor(boolean isCommandBased) {
		this.isCommandBased = isCommandBased;
	}

		
	protected Config getConfig() {
		return config;
	}

	protected void setConfig(Config config) {
		this.config = config;
	}
	

	protected ReportsBean getReports() {
		return reports;
	}

	protected void setReports(ReportsBean reports) {
		this.reports = reports;
	}

	protected ServiceInfo getServiceInfo() {
		return serviceInfo;
	}

	protected void setServiceInfo(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	protected boolean isCommandBased() {
		return isCommandBased;
	}

	protected void setCommandBased(boolean isCommandBased) {
		this.isCommandBased = isCommandBased;
	}

	@Override
	public void process(Config config,ReportsBean reports,
			Map<Parameters, String> params) throws JumbuneException {
		this.reports = reports;
		this.config = config;
		Map<Parameters, String> paramsTmp = params;
		if (paramsTmp == null){
			paramsTmp = new HashMap<Parameters, String>();
		}
		boolean executed = false;
		try {
			// pre-execution
			preExecute(paramsTmp);
			if (!reports.isExectutionStopped()) {
				log(paramsTmp, "Processor(s) Initiating Execution");
				// main execution of module
				executed = execute(paramsTmp);
				if (executed){
					log(paramsTmp, "Execution Completed Successfully");
				}
				else{
					log(paramsTmp, "Execution Failed");
				}
				// update services only when execution is successful
				if (executed){
					updateServiceInfo(serviceInfo);
				}
			}
			// post executions
			postExecute(paramsTmp);
		} catch (JumbuneException e) {
			// adding exception to reports
			String processName = paramsTmp.get(Parameters.PROCESSOR_KEY);
			reports.addException(processName, e, getModuleName());
			log(paramsTmp, "Stopping further processing due to error", e);
			throw e;
		}

		if (next != null && executed && !reports.isExectutionStopped()) {
			next.process( config, reports, paramsTmp);
		}
	}

	@Override
	public void chain(Processor processor) {
		this.next = processor;
	}

	/**
	 * This method can be used for performing any pre-processing prior to main
	 * execution of processor
	 * 
	 * @param params
	 * @throws JumbuneException
	 */
	protected void preExecute(Map<Parameters, String> params)
			throws JumbuneException {

		log(params, "Pre-Execution phase of processors");
		serviceInfo = processHelper.readServiceInfo();
		log(params, serviceInfo.toString());
	}

	/**
	 * Method that returns module name
	 * 
	 * @return ModuleName
	 */
	protected abstract Module getModuleName();

	/**
	 * This is main execution method of any processor and it should contain
	 * complete processing logic for the process. Processor can use the params
	 * for inter process communication. After completing the process the
	 * processor should populate the respective report in reportsBean and should
	 * mark the report as complete so as to show them on UI ( if applicable ).
	 * 
	 * @param params
	 * @return
	 * @throws JumbuneException
	 */
	protected abstract boolean execute(Map<Parameters, String> params)
			throws JumbuneException;

	/**
	 * Method for updating service yaml cofiguration. This method should be
	 * implemented by processors who want to expose their results through jmx
	 * services.
	 * 
	 * @param serviceInfo
	 * @throws JumbuneException
	 */
	protected void updateServiceInfo(ServiceInfo serviceInfo)
			throws JumbuneException {
	};

	/**
	 * This method can be used for performing any post-processing after
	 * execution of processor
	 * 
	 * @param params
	 * @throws JumbuneException
	 */
	protected void postExecute(Map<Parameters, String> params)
			throws JumbuneException {

		log(params, "Post Execution phase of processors");

		if (serviceInfo != null) {
			boolean status = processHelper.writetoServiceFile(serviceInfo);

			if (status){
				log(params, "Service Info written successfully");
			}
			else{
				log(params, "Error occured while writing service info");
			}
		}

	}

	/**
	 * Method for logging info message
	 * 
	 * @param params
	 * @param message
	 */
	protected void log(Map<Parameters, String> params, String message) {
		LOGGER.debug(params.get(Parameters.PROCESSOR_KEY) + "  "
				+ getModuleName() + "  " + message);
	}

	/**
	 * Method for logging error message
	 * 
	 * @param params
	 * @param message
	 * @param t
	 */
	protected void log(Map<Parameters, String> params, String message,
			Throwable t) {
		LOGGER.error(params.get(Parameters.PROCESSOR_KEY) + "  "
				+ getModuleName() + "  " + message, t);
	}
	
	
}
