package org.jumbune.common.beans;

import java.util.List;



/**
 * This class is the bean for the log collection entries from yaml.
 */
public class LogConsolidationInfo {
	
	/** The log summary location. */
	private LogSummaryLocation logSummaryLocation;
	
	/** The master. */
	private Master master;
	
	/** The slaves. */
	private List<Slave> slaves;

	/**
	 * Gets the master.
	 *
	 * @return the master
	 */
	public final Master getMaster() {
		return master;
	}

	/**
	 * Sets the master.
	 *
	 * @param master the new master
	 */
	public final void setMaster(Master master) {
		this.master = master;
	}

	/**
	 * Gets the slaves.
	 *
	 * @return the slaves
	 */
	public final List<Slave> getSlaves() {
		return slaves;
	}

	/**
	 * Sets the slaves.
	 *
	 * @param slaves the new slaves
	 */
	public final void setSlaves(List<Slave> slaves) {
		this.slaves = slaves;
	}

	/**
	 * Gets the log summary location.
	 *
	 * @return the log summary location
	 */
	public final LogSummaryLocation getLogSummaryLocation() {
		return logSummaryLocation;
	}

	/**
	 * Sets the log summary location.
	 *
	 * @param logSummaryLocation the new log summary location
	 */
	public final void setLogSummaryLocation(LogSummaryLocation logSummaryLocation) {
		this.logSummaryLocation = logSummaryLocation;
	}

}