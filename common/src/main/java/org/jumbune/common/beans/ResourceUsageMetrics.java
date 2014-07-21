package org.jumbune.common.beans;


/**
 * POJO to store system resource usage by a MapReduce attempt.
 */
public class ResourceUsageMetrics {

	/** The cumulative cpu usage. */
	private float cumulativeCpuUsage;
	
	/** The virtual memory usage. */
	private float virtualMemoryUsage;
	
	/** The physical memory usage. */
	private float physicalMemoryUsage;
	
	/** The heap usage. */
	private float heapUsage;

	/**
	 * Gets the cumulative cpu usage.
	 *
	 * @return the cumulativeCpuUsage
	 */
	public float getCumulativeCpuUsage() {
		return cumulativeCpuUsage;
	}

	/**
	 * Sets the cumulative cpu usage.
	 *
	 * @param cumulativeCpuUsage the cumulativeCpuUsage to set
	 */
	public void setCumulativeCpuUsage(float cumulativeCpuUsage) {
		this.cumulativeCpuUsage = cumulativeCpuUsage;
	}

	/**
	 * Gets the virtual memory usage.
	 *
	 * @return the virtualMemoryUsage
	 */
	public float getVirtualMemoryUsage() {
		return virtualMemoryUsage;
	}

	/**
	 * Sets the virtual memory usage.
	 *
	 * @param virtualMemoryUsage the virtualMemoryUsage to set
	 */
	public void setVirtualMemoryUsage(float virtualMemoryUsage) {
		this.virtualMemoryUsage = virtualMemoryUsage;
	}

	/**
	 * Gets the physical memory usage.
	 *
	 * @return the physicalMemoryUsage
	 */
	public float getPhysicalMemoryUsage() {
		return physicalMemoryUsage;
	}

	/**
	 * Sets the physical memory usage.
	 *
	 * @param physicalMemoryUsage the physicalMemoryUsage to set
	 */
	public void setPhysicalMemoryUsage(float physicalMemoryUsage) {
		this.physicalMemoryUsage = physicalMemoryUsage;
	}

	/**
	 * Gets the heap usage.
	 *
	 * @return the heapUsage
	 */
	public float getHeapUsage() {
		return heapUsage;
	}

	/**
	 * Sets the heap usage.
	 *
	 * @param heapUsage the heapUsage to set
	 */
	public void setHeapUsage(float heapUsage) {
		this.heapUsage = heapUsage;
	}

}

