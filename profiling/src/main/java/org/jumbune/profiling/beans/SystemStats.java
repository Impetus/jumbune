package org.jumbune.profiling.beans;

import java.util.List;

/**
 * Pojo to store the list of System stats for a node
 */
public class SystemStats {

	private List<String> cpu;
	private List<String> memory;
	private List<String> os;

	/**
	 * @return the cpu
	 */
	public List<String> getCpu() {
		return cpu;
	}

	/**
	 * @param cpu
	 *            the cpu to set
	 */
	public void setCpu(List<String> cpu) {
		this.cpu = cpu;
	}

	/**
	 * @return the memory
	 */
	public List<String> getMemory() {
		return memory;
	}

	/**
	 * @param memory
	 *            the memory to set
	 */
	public void setMemory(List<String> memory) {
		this.memory = memory;
	}

	/**
	 * @return the os
	 */
	public List<String> getOs() {
		return os;
	}

	/**
	 * @param os
	 *            the os to set
	 */
	public void setOs(List<String> os) {
		this.os = os;
	}

}
