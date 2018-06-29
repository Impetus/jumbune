/**
 * 
 */
package org.jumbune.execution.beans;

import java.util.List;

/**
 * This class is used to store Jobs details like Name, Process, Execution Parameters used for executing jobs
 * 
 */
public class JobProcessBean {
	private String jobName;
	private Process process;
	private List<String> jobExecParam;
	private String processResponse;
	
	public String getProcessResponse() {
		return processResponse;
	}

	public void setProcessResponse(String processResponse) {
		this.processResponse = processResponse;
	}

	/**
	 * This method is for accessing the instance variable jobName
	 * 
	 * @return String
	 */
	public final String getJobName() {
		return jobName;
	}

	/**
	 * This method is for setting the value of instance variable jobName
	 * 
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
	 * @return void
	 */
	public final void setJobExecParam(List<String> jobExecParam) {
		this.jobExecParam = jobExecParam;
	}

	/**
	 * This is constructor for JobProcessBean
	 * 
	 * @param String
	 *            jobName,List<String> jobExecParameter
	 */
	public JobProcessBean(String jobName, List<String> jobExecParam) {
		this.jobName = jobName;
		this.jobExecParam = jobExecParam;
	}
}
