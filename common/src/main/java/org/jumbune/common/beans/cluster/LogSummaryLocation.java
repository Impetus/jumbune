package org.jumbune.common.beans.cluster;

import org.jumbune.utils.JobUtil;


/**
 * All these locations are only on Master.
 */
public class LogSummaryLocation {
	
	/** The pure jar counter location. */
	private String pureJarCounterLocation;
	
	/** The pure jar profiling counters location. */
	private String pureJarProfilingCountersLocation;
	
	/** The instrumented jar counters location. */
	private String instrumentedJarCountersLocation;
	
	/** The logs consolidated summary location. */
	private String logsConsolidatedSummaryLocation;
	
	/** The profiling files location. */
	private String profilingFilesLocation;

	/**
	 * Gets the profiling files location.
	 *
	 * @return the profiling files location
	 */
	public final String getProfilingFilesLocation() {
		return profilingFilesLocation;
	}

	/**
	 * Sets the profiling files location.
	 *
	 * @param profilingFilesLocation the new profiling files location
	 */
	public final void setProfilingFilesLocation(String profilingFilesLocation) {
		this.profilingFilesLocation = JobUtil.getAndReplaceHolders(profilingFilesLocation);
	}

	/**
	 * Gets the pure jar counter location.
	 *
	 * @return the pure jar counter location
	 */
	public final String getPureJarCounterLocation() {
		return pureJarCounterLocation;
	}

	/**
	 * Sets the pure jar counter location.
	 *
	 * @param pureJarCounterLocation the new pure jar counter location
	 */
	public final void setPureJarCounterLocation(String pureJarCounterLocation) {
		this.pureJarCounterLocation = JobUtil.getAndReplaceHolders(pureJarCounterLocation);
	}

	/**
	 * Gets the pure jar profiling counters location.
	 *
	 * @return the pure jar profiling counters location
	 */
	public final String getPureJarProfilingCountersLocation() {
		return pureJarProfilingCountersLocation;
	}

	/**
	 * Sets the pure jar profiling counters location.
	 *
	 * @param pureJarProfilingCountersLocation the new pure jar profiling counters location
	 */
	public final void setPureJarProfilingCountersLocation(String pureJarProfilingCountersLocation) {
		this.pureJarProfilingCountersLocation = JobUtil.getAndReplaceHolders(pureJarProfilingCountersLocation);
	}

	/**
	 * Gets the instrumented jar counters location.
	 *
	 * @return the instrumented jar counters location
	 */
	public final String getInstrumentedJarCountersLocation() {
		return instrumentedJarCountersLocation;
	}

	/**
	 * Sets the instrumented jar counters location.
	 *
	 * @param instrumentedJarCountersLocation the new instrumented jar counters location
	 */
	public final void setInstrumentedJarCountersLocation(String instrumentedJarCountersLocation) {
		this.instrumentedJarCountersLocation = JobUtil.getAndReplaceHolders(instrumentedJarCountersLocation);
	}

	/**
	 * Gets the logs consolidated summary location.
	 *
	 * @return the logs consolidated summary location
	 */
	public final String getLogsConsolidatedSummaryLocation() {
		return logsConsolidatedSummaryLocation;
	}

	/**
	 * Sets the logs consolidated summary location.
	 *
	 * @param logsConsolidatedSummaryLocation the new logs consolidated summary location
	 */
	public final void setLogsConsolidatedSummaryLocation(String logsConsolidatedSummaryLocation) {
		this.logsConsolidatedSummaryLocation = JobUtil.getAndReplaceHolders(logsConsolidatedSummaryLocation);
	}
}