package org.jumbune.profiling.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The Class WorkerJMXInfo is a bean class for setting the datanode and task tracker jmx info.
 */
public class WorkerJMXInfo {
	/**
	 * The list holder for data nodes
	 */
	protected List<String> dataNode;
	private Set<String> taskTracker;
	
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
	
	public void addDataNodeStat(String stat) {
		if (this.dataNode == null) {
			this.dataNode = new ArrayList<String>(3);
		}
		this.dataNode.add(stat);
	}
	
	/**
	 * Sets the task tracker.
	 *
	 * @param taskTracker the taskTracker to set
	 */
	public void setTaskTracker(Set<String> taskTracker) {
		this.taskTracker = taskTracker;
	}
	
	public void addToTaskTracker(List<String> taskTracker) {
		if (this.taskTracker == null) {
			this.taskTracker = new TreeSet<String>();
		}
		this.taskTracker.addAll(taskTracker);
	}
	
	/**
	 * Gets the task tracker.
	 *
	 * @return the taskTracker
	 */
	public Set<String> getTaskTracker() {
		return taskTracker;
	}
}
