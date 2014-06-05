package org.jumbune.common.beans;

import java.util.List;

/**
 * Stores the CPU and memory usage of different nodes for a particular interval.
 */
public class IntervalStats {

	/** The cpu stats. */
	private List<Float> cpuStats;
	
	/** The mem stats. */
	private List<Float> memStats;

	/**
	 * Gets the cpu stats.
	 *
	 * @return the cpuStats
	 */
	public List<Float> getCpuStats() {
		return cpuStats;
	}

	/**
	 * Sets the cpu stats.
	 *
	 * @param cpuStats the cpuStats to set
	 */
	public void setCpuStats(List<Float> cpuStats) {
		this.cpuStats = cpuStats;
	}

	/**
	 * Gets the mem stats.
	 *
	 * @return the memStats
	 */
	public List<Float> getMemStats() {
		return memStats;
	}

	/**
	 * Sets the mem stats.
	 *
	 * @param memStats the memStats to set
	 */
	public void setMemStats(List<Float> memStats) {
		this.memStats = memStats;
	}
}
