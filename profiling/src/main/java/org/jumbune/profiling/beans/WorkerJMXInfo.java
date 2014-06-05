package org.jumbune.profiling.beans;

import java.util.List;

/**
 * The Class WorkerJMXInfo is a bean class for setting the datanode and task tracker jmx info.
 */
public class WorkerJMXInfo {
	private List<String> dataNode;
	private List<String> taskTracker;
	
	/**
	 * Sets the data node.
	 *
	 * @param dataNode the dataNode to set
	 */
	public void setDataNode(List<String> dataNode) {
		this.dataNode = dataNode;
	}
	
	/**
	 * Gets the data node.
	 *
	 * @return the dataNode
	 */
	public List<String> getDataNode() {
		return dataNode;
	}
	
	/**
	 * Sets the task tracker.
	 *
	 * @param taskTracker the taskTracker to set
	 */
	public void setTaskTracker(List<String> taskTracker) {
		this.taskTracker = taskTracker;
	}
	
	/**
	 * Gets the task tracker.
	 *
	 * @return the taskTracker
	 */
	public List<String> getTaskTracker() {
		return taskTracker;
	}
}
