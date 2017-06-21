package org.jumbune.clusterprofiling.beans;

/**
 * The Class LiveCapacityStats to dump container availability in cluster.
 */
public class LiveCapacityStats {

	/** The capacity. */
	private String capacity;
	
	/** The message. */
	private String message;

	/**
	 * Gets the capacity.
	 *
	 * @return the capacity
	 */
	public String getCapacity() {
		return capacity;
	}

	/**
	 * Sets the capacity.
	 *
	 * @param capacity the new capacity
	 */
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ContainerStats [capacity=" + capacity + ", message=" + message
				+ "]";
	}
	
}
