package org.jumbune.common.beans;

import java.util.List;



/**
 * This class is the bean for the lib distribution entries from yaml.
 */
public class LibDistributionInfo {
	
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
}
