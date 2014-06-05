package org.jumbune.common.beans;

import java.util.List;



/**
 * The Class ServiceInfo.
 */
public class ServiceInfo {

	/** The pure jar counter location. */
	private String pureJarCounterLocation;
	
	/** The pure jar profiling counters location. */
	private String pureJarProfilingCountersLocation;
	
	/** The instrumented jar counters location. */
	private String instrumentedJarCountersLocation;
	
	/** The data validation result location. */
	private String dataValidationResultLocation;
	
	
	/** The jumbune home. */
	private String jumbuneHome;
	
	/** The jumbune job name. */
	private String jumbuneJobName;
	
	/** The slave jumbune home. */
	private String slaveJumbuneHome;
	
	/** The root directory. */
	private String rootDirectory;

	/** The master. */
	private Master master;
	
	/** The slaves. */
	private List<Slave> slaves;

	/**
	 * Gets the root directory.
	 *
	 * @return the root directory
	 */
	public String getRootDirectory() {
		return rootDirectory;
	}

	/**
	 * Sets the root directory.
	 *
	 * @param rootDirectory the new root directory
	 */
	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	/**
	 * Gets the master.
	 *
	 * @return the master
	 */
	public Master getMaster() {
		return master;
	}

	/**
	 * Sets the master.
	 *
	 * @param master the new master
	 */
	public void setMaster(Master master) {
		this.master = master;
	}

	/**
	 * Gets the slaves.
	 *
	 * @return the slaves
	 */
	public List<Slave> getSlaves() {
		return slaves;
	}

	/**
	 * Sets the slaves.
	 *
	 * @param slaves the new slaves
	 */
	public void setSlaves(List<Slave> slaves) {
		this.slaves = slaves;
	}

	/**
	 * Gets the slave jumbune home.
	 *
	 * @return the slave jumbune home
	 */
	public String getSlaveJumbuneHome() {
		return slaveJumbuneHome;
	}

	/**
	 * Sets the slave jumbune home.
	 *
	 * @param slaveJumbuneHome the new slave jumbune home
	 */
	public void setSlaveJumbuneHome(String slaveJumbuneHome) {
		this.slaveJumbuneHome = slaveJumbuneHome;
	}

	/**
	 * Gets the jumbune home.
	 *
	 * @return the jumbune home
	 */
	public String getJumbuneHome() {
		return jumbuneHome;
	}

	/**
	 * Sets the jumbune home.
	 *
	 * @param jumbuneHome the new jumbune home
	 */
	public void setJumbuneHome(String jumbuneHome) {
		this.jumbuneHome = jumbuneHome;
	}

	/**
	 * Gets the jumbune job name.
	 *
	 * @return the jumbune job name
	 */
	public String getJumbuneJobName() {
		return jumbuneJobName;
	}

	/**
	 * Sets the jumbune job name.
	 *
	 * @param jumbuneJobName the new jumbune job name
	 */
	public void setJumbuneJobName(String jumbuneJobName) {
		this.jumbuneJobName = jumbuneJobName;
	}

	/**
	 * Gets the pure jar counter location.
	 *
	 * @return the pure jar counter location
	 */
	public String getPureJarCounterLocation() {
		return pureJarCounterLocation;
	}

	/**
	 * Sets the pure jar counter location.
	 *
	 * @param pureJarCounterLocation the new pure jar counter location
	 */
	public void setPureJarCounterLocation(String pureJarCounterLocation) {
		this.pureJarCounterLocation = pureJarCounterLocation;
	}

	/**
	 * Gets the pure jar profiling counters location.
	 *
	 * @return the pure jar profiling counters location
	 */
	public String getPureJarProfilingCountersLocation() {
		return pureJarProfilingCountersLocation;
	}

	/**
	 * Sets the pure jar profiling counters location.
	 *
	 * @param pureJarProfilingCountersLocation the new pure jar profiling counters location
	 */
	public void setPureJarProfilingCountersLocation(String pureJarProfilingCountersLocation) {
		this.pureJarProfilingCountersLocation = pureJarProfilingCountersLocation;
	}

	/**
	 * Gets the instrumented jar counters location.
	 *
	 * @return the instrumented jar counters location
	 */
	public String getInstrumentedJarCountersLocation() {
		return instrumentedJarCountersLocation;
	}

	/**
	 * Sets the instrumented jar counters location.
	 *
	 * @param instrumentedJarCountersLocation the new instrumented jar counters location
	 */
	public void setInstrumentedJarCountersLocation(String instrumentedJarCountersLocation) {
		this.instrumentedJarCountersLocation = instrumentedJarCountersLocation;
	}

	/**
	 * Gets the data validation result location.
	 *
	 * @return the data validation result location
	 */
	public String getDataValidationResultLocation() {
		return dataValidationResultLocation;
	}

	/**
	 * Sets the data validation result location.
	 *
	 * @param dataValidationResultLocation the new data validation result location
	 */
	public void setDataValidationResultLocation(String dataValidationResultLocation) {
		this.dataValidationResultLocation = dataValidationResultLocation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return ServiceInfo.class.getSimpleName() + "[pureJarCounterLocation=" + pureJarCounterLocation + ", instrumentedJarCountersLocation="
				+ instrumentedJarCountersLocation + ", dataValidationResultLocation=" + dataValidationResultLocation + ", jumbuneHome=" + jumbuneHome
				+ ", slaveJumbuneHome=" + slaveJumbuneHome + ", jumbuneJobName=" + jumbuneJobName 
				+ "]";

	}

}
