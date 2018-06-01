package org.jumbune.clusteranalysis.queues.beans;


/**
 * The Class QueueStats is a pojo to store the information regarding the queues configured in the cluster.
 */
public class QueueStats {
	
	/** The queue name. */
	private String queueName ;
	
	/** The current capacity. */
	private float currentCapacity ;
	
	/** The maximum capacity. */
	private float maximumCapacity = 0f;
	
	/** The capacity. */
	private float capacity ;


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
	 * @param queueName the new queue name
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	
	/**
	 * Gets the current capacity.
	 *
	 * @return the current capacity
	 */
	public float getCurrentCapacity() {
		return currentCapacity;
	}

	/**
	 * Sets the current capacity.
	 *
	 * @param currentCapacity the new current capacity
	 */
	public void setCurrentCapacity(float currentCapacity) {
		this.currentCapacity = currentCapacity;
	}

	/**
	 * Gets the maximum capacity.
	 *
	 * @return the maximum capacity
	 */
	public float getMaximumCapacity() {
		return maximumCapacity;
	}

	/**
	 * Sets the maximum capacity.
	 *
	 * @param maximumCapacity the new maximum capacity
	 */
	public void setMaximumCapacity(float maximumCapacity) {
		this.maximumCapacity = maximumCapacity;
	}

	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 */
	public float getCapacity() {
		return capacity;
	}

	/**
	 * Sets the capacity.
	 *
	 * @param capacity the new capacity
	 */
	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}
	
	


	@Override
	public String toString() {
		return "QueueStats [queueName=" + queueName + ", currentCapacity="
				+ currentCapacity + ", maximumCapacity=" + maximumCapacity
				+ ", capacity=" + capacity + "]";
	}



}
