package org.jumbune.common.beans;


/**
 * The Class SlaveParam.
 */
public class SlaveParam {
	
	/** The data node jmx port. */
	protected String dataNodeJmxPort;
	
	/** The task tracker jmx port. */
	private String taskTrackerJmxPort;
	
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
}
