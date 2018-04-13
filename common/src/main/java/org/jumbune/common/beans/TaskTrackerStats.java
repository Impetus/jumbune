package org.jumbune.common.beans;

public class TaskTrackerStats extends JMXStats implements Stats {

	// tasktracker stats
	private Integer processors;
	private Long memory_available;
	private Long totalPhysicalMemory;

	/**
	 * @return the totalPhysicalMemory
	 */
	public Long getTotalPhysicalMemory() {
		return totalPhysicalMemory;
	}

	/**
	 * @param totalPhysicalMemory
	 *            the totalPhysicalMemory to set
	 */
	public void setTotalPhysicalMemory(Long totalPhysicalMemory) {
		this.totalPhysicalMemory = totalPhysicalMemory;
	}

	public TaskTrackerStats(String host, Integer port) {
		super(host, port);
	}

	public Integer getProcessors() {
		return processors;
	}

	public void setProcessors(Integer processors) {
		this.processors = processors;
	}

	/**
	 * @return the memory_available
	 */
	public Long getMemory_available() {
		return memory_available;
	}

	/**
	 * @param memory_available
	 *            the memory_available to set
	 */
	public void setMemory_available(Long memory_available) {
		this.memory_available = memory_available;
	}

	@Override
	public String toString() {
		return "TaskTrackerStats [processors=" + processors + ", memory_available=" + memory_available
				+ ", totalPhysicalMemory=" + totalPhysicalMemory + "]";
	}

}
