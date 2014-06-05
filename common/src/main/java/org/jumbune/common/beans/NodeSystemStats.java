package org.jumbune.common.beans;

import java.util.Map;


/**
 * Contains cpu and memory usage of the node during the job run.
 */
public class NodeSystemStats {

	/** The host name. */
	private String hostName;
	
	/** The total map intervals. */
	private int totalMapIntervals;
	
	/** The total reduce intervals. */
	private int totalReduceIntervals;
	
	/** The avg cpu. */
	private float avgCpu;
	
	/** The max mem. */
	private float maxMem;
	
	/** The map phase max mem. */
	private float mapPhaseMaxMem;
	
	/** The reduce phase max mem. */
	private float reducePhaseMaxMem;
	
	/** The map phase avg cpu. */
	private float mapPhaseAvgCpu;
	
	/** The reduce phase avgcpu. */
	private float reducePhaseAvgcpu;
	
	/** The total map phase cpu. */
	private float totalMapPhaseCpu;
	
	/** The total reduce phase cpu. */
	private float totalReducePhaseCpu;
	
	/** The cpu usage. */
	private Map<Long, Float> cpuUsage;
	
	/** The mem usage. */
	private Map<Long, Float> memUsage;

	/**
	 * Gets the host name.
	 *
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Sets the host name.
	 *
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Gets the cpu usage.
	 *
	 * @return the cpuUsage
	 */
	public Map<Long, Float> getCpuUsage() {
		return cpuUsage;
	}

	/**
	 * Sets the cpu usage.
	 *
	 * @param cpuUsage the cpuUsage to set
	 */
	public void setCpuUsage(Map<Long, Float> cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	/**
	 * Gets the mem usage.
	 *
	 * @return the memUsage
	 */
	public Map<Long, Float> getMemUsage() {
		return memUsage;
	}

	/**
	 * Sets the mem usage.
	 *
	 * @param memUsage the memUsage to set
	 */
	public void setMemUsage(Map<Long, Float> memUsage) {
		this.memUsage = memUsage;
	}

	/**
	 * Gets the avg cpu.
	 *
	 * @return the avgCpu
	 */
	public float getAvgCpu() {
		return avgCpu;
	}

	/**
	 * Sets the avg cpu.
	 *
	 * @param avgCpu the avgCpu to set
	 */
	public void setAvgCpu(float avgCpu) {
		this.avgCpu = avgCpu;
	}

	/**
	 * Gets the max mem.
	 *
	 * @return the maxMem
	 */
	public float getMaxMem() {
		return maxMem;
	}

	/**
	 * Sets the max mem.
	 *
	 * @param maxMem the maxMem to set
	 */
	public void setMaxMem(float maxMem) {
		this.maxMem = maxMem;
	}

	/**
	 * Gets the map phase max mem.
	 *
	 * @return the mapPhaseMaxMem
	 */
	public float getMapPhaseMaxMem() {
		return mapPhaseMaxMem;
	}

	/**
	 * Sets the map phase max mem.
	 *
	 * @param mapPhaseMaxMem the mapPhaseMaxMem to set
	 */
	public void setMapPhaseMaxMem(float mapPhaseMaxMem) {
		this.mapPhaseMaxMem = mapPhaseMaxMem;
	}

	/**
	 * Gets the reduce phase max mem.
	 *
	 * @return the reducePhaseMaxMem
	 */
	public float getReducePhaseMaxMem() {
		return reducePhaseMaxMem;
	}

	/**
	 * Sets the reduce phase max mem.
	 *
	 * @param reducePhaseMaxMem the reducePhaseMaxMem to set
	 */
	public void setReducePhaseMaxMem(float reducePhaseMaxMem) {
		this.reducePhaseMaxMem = reducePhaseMaxMem;
	}

	/**
	 * Gets the map phase avg cpu.
	 *
	 * @return the mapPhaseAvgCpu
	 */
	public float getMapPhaseAvgCpu() {
		if (totalMapIntervals != 0) {
			mapPhaseAvgCpu = totalMapPhaseCpu / totalMapIntervals;
		}
		return mapPhaseAvgCpu;
	}

	/**
	 * Sets the map phase avg cpu.
	 *
	 * @param mapPhaseAvgCpu the mapPhaseAvgCpu to set
	 */
	public void setMapPhaseAvgCpu(float mapPhaseAvgCpu) {
		this.mapPhaseAvgCpu = mapPhaseAvgCpu;
	}

	/**
	 * Gets the reduce phase avgcpu.
	 *
	 * @return the reducePhaseAvgcpu
	 */
	public float getReducePhaseAvgcpu() {
		if (totalReduceIntervals != 0) {
			reducePhaseAvgcpu = totalReducePhaseCpu / totalReduceIntervals;
		}
		return reducePhaseAvgcpu;
	}

	/**
	 * Sets the reduce phase avgcpu.
	 *
	 * @param reducePhaseAvgcpu the reducePhaseAvgcpu to set
	 */
	public void setReducePhaseAvgcpu(float reducePhaseAvgcpu) {
		this.reducePhaseAvgcpu = reducePhaseAvgcpu;
	}

	/**
	 * Gets the total map intervals.
	 *
	 * @return the totalMapIntervals
	 */
	public int getTotalMapIntervals() {
		return totalMapIntervals;
	}

	/**
	 * Sets the total map intervals.
	 *
	 * @param totalMapIntervals the totalMapIntervals to set
	 */
	public void setTotalMapIntervals(int totalMapIntervals) {
		this.totalMapIntervals = totalMapIntervals;
	}

	/**
	 * Gets the total reduce intervals.
	 *
	 * @return the totalReduceIntervals
	 */
	public int getTotalReduceIntervals() {
		return totalReduceIntervals;
	}

	/**
	 * Sets the total reduce intervals.
	 *
	 * @param totalReduceIntervals the totalReduceIntervals to set
	 */
	public void setTotalReduceIntervals(int totalReduceIntervals) {
		this.totalReduceIntervals = totalReduceIntervals;
	}

	/**
	 * Gets the total map phase cpu.
	 *
	 * @return the totalMapPhaseCpu
	 */
	public float getTotalMapPhaseCpu() {
		return totalMapPhaseCpu;
	}

	/**
	 * Sets the total map phase cpu.
	 *
	 * @param totalMapPhaseCpu the totalMapPhaseCpu to set
	 */
	public void setTotalMapPhaseCpu(float totalMapPhaseCpu) {
		this.totalMapPhaseCpu = totalMapPhaseCpu;
	}

	/**
	 * Gets the total reduce phase cpu.
	 *
	 * @return the totalReducePhaseCpu
	 */
	public float getTotalReducePhaseCpu() {
		return totalReducePhaseCpu;
	}

	/**
	 * Sets the total reduce phase cpu.
	 *
	 * @param totalReducePhaseCpu the totalReducePhaseCpu to set
	 */
	public void setTotalReducePhaseCpu(float totalReducePhaseCpu) {
		this.totalReducePhaseCpu = totalReducePhaseCpu;
	}

}
