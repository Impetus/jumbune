package org.jumbune.common.beans.profiling;


/**
 * This class is the bean for the capturing profiling parameters.
 */
public class ProfilingParam {

/*	*//** The cpu. *//*
	private static String cpu = "samples";
	
	*//** The heap. *//*
	private static String heap = "sites";
*/	
	/** The mapers. */
	private String mappers;
	
	/** The reducers. */
	private String reducers;
	
	/** The master jmx port. */
	private String masterJmxPort;
	
	/** The job tracker jmx port. */
	private String jobTrackerJmxPort;
	
	/** The data node jmx port. */
	private String dataNodeJmxPort;
	
	/** The task tracker jmx port. */
	private String taskTrackerJmxPort;
	
	/** The stats interval. */
	private int statsInterval;

	/**
	 * Gets the hadoop job profile params.
	 *
	 * @return the hadoop job profile params
	 */
/*	public String getHadoopJobProfileParams() {
		StringBuffer b = new StringBuffer(2);
		b.append(",cpu=").append(cpu);
		b.append(",heap=").append(heap);
		return b.toString();
	}
*/
	/**
	 * Gets the cpu.
	 *
	 * @return the cpu
	 */
/*	public String getCpu() {
		return cpu;
	}
*/
	/**
	 * Gets the heap.
	 *
	 * @return the heap
	 */
/*	public String getHeap() {
		return heap;
	}
*/
	/**
	 * Gets the mappers.
	 *
	 * @return the mappers
	 */
	public String getMappers() {
		return mappers;
	}

	/**
	 * Sets the mappers.
	 *
	 * @param mappers the new mappers
	 */
	public void setMappers(String mappers) {
		this.mappers = mappers;
	}

	/**
	 * Gets the reducers.
	 *
	 * @return the reducers
	 */
	public String getReducers() {
		return reducers;
	}

	/**
	 * Sets the reducers.
	 *
	 * @param reducers the new reducers
	 */
	public void setReducers(String reducers) {
		this.reducers = reducers;
	}

	/**
	 * Gets the master jmx port.
	 *
	 * @return the master jmx port
	 */
	public String getMasterJmxPort() {
		return masterJmxPort;
	}

	/**
	 * Sets the master jmx port.
	 *
	 * @param masterJmxPort the new master jmx port
	 */
	public void setMasterJmxPort(String masterJmxPort) {
		this.masterJmxPort = masterJmxPort;
	}

	/**
	 * Gets the job tracker jmx port.
	 *
	 * @return the jobTrackerJmxPort
	 */
	public String getJobTrackerJmxPort() {
		return jobTrackerJmxPort;
	}

	/**
	 * Sets the job tracker jmx port.
	 *
	 * @param jobTrackerJmxPort the jobTrackerJmxPort to set
	 */
	public void setJobTrackerJmxPort(String jobTrackerJmxPort) {
		this.jobTrackerJmxPort = jobTrackerJmxPort;
	}

	/**
	 * Gets the data node jmx port.
	 *
	 * @return the data node jmx port
	 */
	public String getDataNodeJmxPort() {
		return dataNodeJmxPort;
	}

	/**
	 * Sets the data node jmx port.
	 *
	 * @param dataNodeJmxPort the new data node jmx port
	 */
	public void setDataNodeJmxPort(String dataNodeJmxPort) {
		this.dataNodeJmxPort = dataNodeJmxPort;
	}

	/**
	 * Gets the task tracker jmx port.
	 *
	 * @return the task tracker jmx port
	 */
	public String getTaskTrackerJmxPort() {
		return taskTrackerJmxPort;
	}

	/**
	 * Sets the task tracker jmx port.
	 *
	 * @param taskTrackerJmxPort the new task tracker jmx port
	 */
	public void setTaskTrackerJmxPort(String taskTrackerJmxPort) {
		this.taskTrackerJmxPort = taskTrackerJmxPort;
	}

	/**
	 * Gets the stats interval.
	 *
	 * @return the stats interval
	 */
	public int getStatsInterval() {
		return statsInterval;
	}

	/**
	 * Sets the stats interval.
	 *
	 * @param statsInterval the new stats interval
	 */
	public void setStatsInterval(int statsInterval) {
		this.statsInterval = statsInterval;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProfilingParam [mappers=" + mappers + ", reducers=" + reducers + ", masterJmxPort=" + masterJmxPort
				+ ", jobTrackerJmxPort=" + jobTrackerJmxPort + ", dataNodeJmxPort=" + dataNodeJmxPort + ", taskTrackerJmxPort=" + taskTrackerJmxPort
				+ ", statsInterval=" + statsInterval + "]";
	}

	
}
