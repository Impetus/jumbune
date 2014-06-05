/**
 * 
 */
package org.jumbune.common.beans;

import java.util.List;


/**
 * This class is used to store Jobs details like Name, Process, Execution
 * Parameters used for executing jobs.
 */
public class JobProcessBean {
	
	/** The job name. */
	private String jobName;
	
	/** The process. */
	private Process process;
	
	/** The job exec param. */
	private List<String> jobExecParam;
	
	/** The process response. */
	private String processResponse;
	
	/**
	 * Gets the process response.
	 *
	 * @return the process response
	 */
	public String getProcessResponse() {
		return processResponse;
	}

	/**
	 * Sets the process response.
	 *
	 * @param processResponse the new process response
	 */
	public void setProcessResponse(String processResponse) {
		this.processResponse = processResponse;
	}

	/**
	 * This method is for accessing the instance variable jobName.
	 *
	 * @return String
	 */
	public final String getJobName() {
		return jobName;
	}

	/**
	 * This method is for setting the value of instance variable jobName.
	 *
	 * @param jobName the new job name
	 * @return String
	 */
	public final void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * This method is for accessing the instance variable process.
	 * 
	 * @return Process
	 */
	public final Process getProcess() {
		return process;
	}

	/**
	 * This method is for setting the value of instance variable process.
	 *
	 * @param process the new process
	 * @return void
	 */
	public final void setProcess(Process process) {
		this.process = process;
	}

	/**
	 * This method is for accessing the instance variable process.
	 * 
	 * @return List<String>
	 */
	public final List<String> getJobExecParam() {
		return jobExecParam;
	}

	/**
	 * This method is for setting the value of instance variable jobExecParam.
	 *
	 * @param jobExecParam the new job exec param
	 * @return void
	 */
	public final void setJobExecParam(List<String> jobExecParam) {
		this.jobExecParam = jobExecParam;
	}

	/**
	 * This is constructor for JobProcessBean.
	 *
	 * @param jobName the job name
	 * @param jobExecParam the job exec param
	 */
	public JobProcessBean(String jobName, List<String> jobExecParam) {
		this.jobName = jobName;
		this.jobExecParam = jobExecParam;
	}
}
