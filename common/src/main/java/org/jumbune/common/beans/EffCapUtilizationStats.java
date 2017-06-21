package org.jumbune.common.beans;

/**
 * The Class EffCapUtilizationStats.
 */

public class EffCapUtilizationStats {
	
	/** The job id. */
	private String jobId ;
	
	/** The max map memory. */
	private Long allocatedMapMemory;
	
	/** The used map memory. */
	private Long usedMaxMapMemory;
	
	/** The max reduce memory. */
	private Long allocatedReduceMemory;
	
	/** The used reduce memory. */
	private Long usedMaxReduceMemory;
	
	/** The job name. */
	private String jobName; 
	
	private long jobStartTime;

	private long jobFinishTime;
	
	private Integer usedVCores;
	
	private Integer usedContainers;
	
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
	 * @param jobId the new job id
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	/**
	 * Gets the allocated map memory.
	 *
	 * @return the allocated map memory
	 */
	public Long getAllocatedMapMemory() {
		return allocatedMapMemory;
	}

	/**
	 * Sets the allocated map memory.
	 *
	 * @param allocatedMapMemory the new allocated map memory
	 */
	public void setAllocatedMapMemory(Long allocatedMapMemory) {
		this.allocatedMapMemory = allocatedMapMemory;
	}

	/**
	 * Gets the used max map memory.
	 *
	 * @return the used max map memory
	 */
	public Long getUsedMaxMapMemory() {
		return usedMaxMapMemory;
	}

	/**
	 * Sets the used max map memory.
	 *
	 * @param usedMaxMapMemory the new used max map memory
	 */
	public void setUsedMaxMapMemory(Long usedMaxMapMemory) {
		this.usedMaxMapMemory = usedMaxMapMemory;
	}

	/**
	 * Gets the allocated reduce memory.
	 *
	 * @return the allocated reduce memory
	 */
	public Long getAllocatedReduceMemory() {
		return allocatedReduceMemory;
	}

	/**
	 * Sets the allocated reduce memory.
	 *
	 * @param allocatedReduceMemory the new allocated reduce memory
	 */
	public void setAllocatedReduceMemory(Long allocatedReduceMemory) {
		this.allocatedReduceMemory = allocatedReduceMemory;
	}

	/**
	 * Gets the used max reduce memory.
	 *
	 * @return the used max reduce memory
	 */
	public Long getUsedMaxReduceMemory() {
		return usedMaxReduceMemory;
	}

	/**
	 * Sets the used max reduce memory.
	 *
	 * @param usedMaxReduceMemory the new used max reduce memory
	 */
	public void setUsedMaxReduceMemory(Long usedMaxReduceMemory) {
		this.usedMaxReduceMemory = usedMaxReduceMemory;
	}
	
	/**
	 * Sets the job details.
	 *
	 * @param jobDetails the new job details
	 */
	public void setJobName(String jobName){
		this.jobName = jobName;
	}	
	
	/**
	 * Gets the job name.
	 *
	 * @return the job name
	 */
	public String getJobName(){
		return jobName;
	}
	
	/**
	 * Sets the job start time.
	 *
	 * @param startTime the new job start time
	 */
	public void setJobStartTime(long startTime){
		this.jobStartTime = startTime;
	}
	
	/**
	 * Gets the job start time.
	 *
	 * @return the job start time
	 */
	public long getJobStartTime(){
		return this.jobStartTime;
	}
	
	/**
	 * Sets the job finish time.
	 *
	 * @param finishTime the new job finish time
	 */
	public void setJobFinishTime(long finishTime){
		this.jobFinishTime = finishTime;
	}
	
	/**
	 * Gets the job finish time.
	 *
	 * @return the job finish time
	 */
	public long getJobFinishTime(){
		return this.jobFinishTime;
	}

	public Integer getUsedVCores() {
		return usedVCores;
	}

	public void setUsedVCores(Integer usedVCores) {
		this.usedVCores = usedVCores;
	}

	public Integer getUsedContainers() {
		return usedContainers;
	}

	public void setUsedContainers(Integer usedContainers) {
		this.usedContainers = usedContainers;
	}
	
}
