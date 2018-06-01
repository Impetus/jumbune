package org.jumbune.clusteranalysis.beans;

/**
 * The Class JobQueueBean is a pojo containing the job and queue utilization
 * details.
 */
public class JobQueueBean {

	/** The job Id. */
	private String jobId;

	/** The queue name. */
	private String queueName;

	/** The user. */
	private String user;

	/** The used cores. */
	private int usedCores;

	/** The used memory. */
	private int usedMemory;
	
	private String executionEngine ;
	
	private String jobName ;
	
	
	/**
	 * Gets the queue name.
	 *
	 * @return the queue name
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * Sets the queue name.
	 *
	 * @param queueName
	 *            the new queue name
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user
	 *            the new user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Gets the used cores.
	 *
	 * @return the used cores
	 */
	public int getUsedCores() {
		return usedCores;
	}

	/**
	 * Sets the used cores.
	 *
	 * @param usedCores
	 *            the new used cores
	 */
	public void setUsedCores(int usedCores) {
		this.usedCores = usedCores;
	}

	/**
	 * Gets the used memory.
	 *
	 * @return the used memory
	 */
	public int getUsedMemory() {
		return usedMemory;
	}

	/**
	 * Sets the used memory.
	 *
	 * @param usedMemory
	 *            the new used memory
	 */
	public void setUsedMemory(int usedMemory) {
		this.usedMemory = usedMemory;
	}

	/**
	 * Gets the job id.
	 *
	 * @return the job id
	 */
	public String getJobId() {
		return jobId;
	}

	/**
	 * Sets the job id.
	 *
	 * @param jobId
	 *            the new job id
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getExecutionEngine() {
		return executionEngine;
	}

	public void setExecutionEngine(String executionEngine) {
		this.executionEngine = executionEngine;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Override
	public String toString() {
		return "JobQueueBean [jobId=" + jobId + ", queueName=" + queueName + ", user=" + user + ", usedCores="
				+ usedCores + ", usedMemory=" + usedMemory + ", executionEngine=" + executionEngine + ", jobName="
				+ jobName + "]";
	}

	
	

}
