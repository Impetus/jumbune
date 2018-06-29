package org.jumbune.common.beans.profiling;

import java.util.List;

import org.jumbune.common.beans.cluster.Location;


/**
 * POJO to store MapReduce task details.
 */
public class TaskDetails {

	/** The start time. */
	private long startTime;
	
	/** The attempts. */
	private List<AttemptDetails> attempts;
	
	/** The finish time. */
	private long finishTime;
	
	/** The preferred locations. */
	private List<Location> preferredLocations;
	
	/** The task type. */
	private String taskType;
	
	/** The task status. */
	private String taskStatus;
	
	/** The task id. */
	private String taskID;
	
	/** The input bytes. */
	private long inputBytes;
	
	/** The input records. */
	private long inputRecords;
	
	/** The output bytes. */
	private long outputBytes;
	
	/** The output records. */
	private long outputRecords;

	/**
	 * Gets the start time.
	 *
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Sets the start time.
	 *
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * Gets the attempts.
	 *
	 * @return the attempts
	 */
	public List<AttemptDetails> getAttempts() {
		return attempts;
	}

	/**
	 * Sets the attempts.
	 *
	 * @param attempts the attempts to set
	 */
	public void setAttempts(List<AttemptDetails> attempts) {
		this.attempts = attempts;
	}

	/**
	 * Gets the finish time.
	 *
	 * @return the finishTime
	 */
	public long getFinishTime() {
		return finishTime;
	}

	/**
	 * Sets the finish time.
	 *
	 * @param finishTime the finishTime to set
	 */
	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	/**
	 * Gets the preferred locations.
	 *
	 * @return the preferredLocations
	 */
	public List<Location> getPreferredLocations() {
		return preferredLocations;
	}

	/**
	 * Sets the preferred locations.
	 *
	 * @param preferredLocations the preferredLocations to set
	 */
	public void setPreferredLocations(List<Location> preferredLocations) {
		this.preferredLocations = preferredLocations;
	}

	/**
	 * Gets the task type.
	 *
	 * @return the taskType
	 */
	public String getTaskType() {
		return taskType;
	}

	/**
	 * Sets the task type.
	 *
	 * @param taskType the taskType to set
	 */
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	/**
	 * Gets the task status.
	 *
	 * @return the taskStatus
	 */
	public String getTaskStatus() {
		return taskStatus;
	}

	/**
	 * Sets the task status.
	 *
	 * @param taskStatus the taskStatus to set
	 */
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	/**
	 * Gets the task id.
	 *
	 * @return the taskID
	 */
	public String getTaskID() {
		return taskID;
	}

	/**
	 * Sets the task id.
	 *
	 * @param taskID the taskID to set
	 */
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	/**
	 * Gets the input bytes.
	 *
	 * @return the inputBytes
	 */
	public long getInputBytes() {
		return inputBytes;
	}

	/**
	 * Sets the input bytes.
	 *
	 * @param inputBytes the inputBytes to set
	 */
	public void setInputBytes(long inputBytes) {
		this.inputBytes = inputBytes;
	}

	/**
	 * Gets the input records.
	 *
	 * @return the inputRecords
	 */
	public long getInputRecords() {
		return inputRecords;
	}

	/**
	 * Sets the input records.
	 *
	 * @param inputRecords the inputRecords to set
	 */
	public void setInputRecords(long inputRecords) {
		this.inputRecords = inputRecords;
	}

	/**
	 * Gets the output bytes.
	 *
	 * @return the outputBytes
	 */
	public long getOutputBytes() {
		return outputBytes;
	}

	/**
	 * Sets the output bytes.
	 *
	 * @param outputBytes the outputBytes to set
	 */
	public void setOutputBytes(long outputBytes) {
		this.outputBytes = outputBytes;
	}

	/**
	 * Gets the output records.
	 *
	 * @return the outputRecords
	 */
	public long getOutputRecords() {
		return outputRecords;
	}

	/**
	 * Sets the output records.
	 *
	 * @param outputRecords the outputRecords to set
	 */
	public void setOutputRecords(long outputRecords) {
		this.outputRecords = outputRecords;
	}

	@Override
	public String toString() {
		return "TaskDetails [startTime=" + startTime + ", attempts=" + attempts
				+ ", finishTime=" + finishTime + ", preferredLocations="
				+ preferredLocations + ", taskType=" + taskType
				+ ", taskStatus=" + taskStatus + ", taskID=" + taskID
				+ ", inputBytes=" + inputBytes + ", inputRecords="
				+ inputRecords + ", outputBytes=" + outputBytes
				+ ", outputRecords=" + outputRecords + "]";
	}



}
