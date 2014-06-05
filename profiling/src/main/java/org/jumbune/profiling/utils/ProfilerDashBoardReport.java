package org.jumbune.profiling.utils;

import java.util.Map;

/**
 * The Class ProfilerDashBoardReport is bean class for storing the cpu samples for mapper and reducer for displaying on profiler dashboard.
 */
public class ProfilerDashBoardReport {
	
	/** The cpu samples mapper. */
	private Map<Float, String> cpuSamplesMapper;

	/** The heap sites. */
	private Map<Float, String> cpuSampleReducer;

	/**
	 * Gets the cpu samples mapper.
	 *
	 * @return the cpu samples mapper
	 */
	public Map<Float, String> getCpuSamplesMapper() {
		return cpuSamplesMapper;
	}

	/**
	 * Sets the cpu samples mapper.
	 *
	 * @param cpuSamplesMapper the cpu samples mapper
	 */
	public void setCpuSamplesMapper(Map<Float, String> cpuSamplesMapper) {
		this.cpuSamplesMapper = cpuSamplesMapper;
	}

	/**
	 * Gets the cpu sample reducer.
	 * 
	 * @return the cpu sample reducer
	 */
	public Map<Float, String> getCpuSampleReducer() {
		return cpuSampleReducer;
	}

	/**
	 * Sets the cpu sample reducer.
	 *
	 * @param cpuSampleReducer the cpu sample reducer
	 */
	public void setCpuSampleReducer(Map<Float, String> cpuSampleReducer) {
		this.cpuSampleReducer = cpuSampleReducer;
	}

}
