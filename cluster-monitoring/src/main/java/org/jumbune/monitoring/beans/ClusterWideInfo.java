package org.jumbune.monitoring.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Pojo to store the various cluster wide profiling stats
 * 
 */
public class ClusterWideInfo {

	private List<String> jobTracker;
	/**
	 * The list holder for namenode
	 */
	protected List<String> nameNode;
 

	/**
	 * @return the jobTracker
	 */
	public List<String> getJobTracker() {
		return jobTracker;
	}

	/**
	 * @param jobTracker
	 *            the jobTracker to set
	 */
	public void setJobTracker(List<String> jobTracker) {
		this.jobTracker = jobTracker;
	}

	/**
	 * @return the nameNode
	 */
	public List<String> getNameNode() {
		return nameNode;
	}

	/**
	 * @param nameNode
	 *            the nameNode to set
	 */
	public void setNameNode(List<String> nameNode) {
		this.nameNode = nameNode;
	}
	
	public void addNameNodeStat(String nameNodeStat) {
		if (this.nameNode == null) {
			this.nameNode = new ArrayList<String>(2);
		}
		this.nameNode.add(nameNodeStat);
	}

}
