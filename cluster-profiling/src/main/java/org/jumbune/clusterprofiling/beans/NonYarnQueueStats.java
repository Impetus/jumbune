package org.jumbune.clusterprofiling.beans;

/**
 * The Class NonYarnQueueStats populates the non yarn specific queue statistics.
 */
public class NonYarnQueueStats extends QueueStats{
	
	/** The queue state. */
	private String queueState;
	
	/** The waiting jobs. */
	private int waitingJobs ;

	/**
	 * Gets the queue state.
	 *
	 * @return the queue state
	 */
	public String getQueueState() {
		return queueState;
	}

	/**
	 * Sets the queue state.
	 *
	 * @param queueState the new queue state
	 */
	public void setQueueState(String queueState) {
		this.queueState = queueState;
	}

	/**
	 * Gets the waiting jobs.
	 *
	 * @return the waiting jobs
	 */
	public int getWaitingJobs() {
		return waitingJobs;
	}

	/**
	 * Sets the waiting jobs.
	 *
	 * @param waitingJobs the new waiting jobs
	 */
	public void setWaitingJobs(int waitingJobs) {
		this.waitingJobs = waitingJobs;
	}

	@Override
	public String toString() {
		return "NonYarnQueueStats [queueState=" + queueState + ", waitingJobs="
				+ waitingJobs + ", getQueueState()=" + getQueueState()
				+ ", getWaitingJobs()=" + getWaitingJobs()
				+ ", getQueueName()=" + getQueueName()
				+ ", getCurrentCapacity()=" + getCurrentCapacity()
				+ ", getMaximumCapacity()=" + getMaximumCapacity()
				+ ", getCapacity()=" + getCapacity() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + "]";
	}
	
	

}
