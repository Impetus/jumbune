package org.jumbune.common.beans;

import java.util.List;


/**
 * POJO to store details of a MapReduce phase.
 */

public class PhaseDetails {

	/** The task output details. */
	private List<TaskOutputDetails> taskOutputDetails;
	
	/** The avg data flow rate. */
	private long avgDataFlowRate;

	/**
	 * Gets the task output details.
	 *
	 * @return the taskOutputDetails
	 */
	public List<TaskOutputDetails> getTaskOutputDetails() {
		return taskOutputDetails;
	}

	/**
	 * Sets the task output details.
	 *
	 * @param taskOutputDetails the taskOutputDetails to set
	 */
	public void setTaskOutputDetails(List<TaskOutputDetails> taskOutputDetails) {
		this.taskOutputDetails = taskOutputDetails;
	}

	/**
	 * Gets the avg data flow rate.
	 *
	 * @return the avgDataFlowRate
	 */
	public long getAvgDataFlowRate() {
		return avgDataFlowRate;
	}

	/**
	 * Sets the avg data flow rate.
	 *
	 * @param avgDataFlowRate the avgDataFlowRate to set
	 */
	public void setAvgDataFlowRate(long avgDataFlowRate) {
		this.avgDataFlowRate = avgDataFlowRate;
	}

	@Override
	public String toString() {
		return "PhaseDetails [taskOutputDetails=" + taskOutputDetails
				+ ", avgDataFlowRate=" + avgDataFlowRate + "]";
	}

	

}
