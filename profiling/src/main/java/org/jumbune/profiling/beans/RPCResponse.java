package org.jumbune.profiling.beans;

import java.util.Map;

/**
 * Pojo to store response for RPC stats for Datanode and Tasktracker
 * 
 */
public class RPCResponse {

	private Map<String, String> dataNode;
	private Map<String, String> taskTracker;

	/**
	 * @return the dataNode
	 */
	public Map<String, String> getDataNode() {
		return dataNode;
	}

	/**
	 * @param dataNode
	 *            the dataNode to set
	 */
	public void setDataNode(Map<String, String> dataNode) {
		this.dataNode = dataNode;
	}

	/**
	 * @return the taskTracker
	 */
	public Map<String, String> getTaskTracker() {
		return taskTracker;
	}

	/**
	 * @param taskTracker
	 *            the taskTracker to set
	 */
	public void setTaskTracker(Map<String, String> taskTracker) {
		this.taskTracker = taskTracker;
	}

}
