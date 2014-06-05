package org.jumbune.profiling.beans;

import java.util.List;

/**
 * Pojo to store the various RPC stats for Datanode and Tasktracker
 * 
 */
public class RPCInfo {

	private List<String> dataNode;
	private List<String> taskTracker;

	/**
	 * @return the dataNode
	 */
	public List<String> getDataNode() {
		return dataNode;
	}

	/**
	 * @param dataNode
	 *            the dataNode to set
	 */
	public void setDataNode(List<String> dataNode) {
		this.dataNode = dataNode;
	}

	/**
	 * @return the taskTracker
	 */
	public List<String> getTaskTracker() {
		return taskTracker;
	}

	/**
	 * @param taskTracker
	 *            the taskTracker to set
	 */
	public void setTaskTracker(List<String> taskTracker) {
		this.taskTracker = taskTracker;
	}

}
