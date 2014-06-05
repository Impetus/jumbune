package org.jumbune.profiling.beans;

import java.util.List;

/**
 * Pojo to store the various cluster wide profiling stats
 * 
 */
public class ClusterWideInfo {

	private List<String> jobTracker;
	private List<String> nameNode;


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

}
