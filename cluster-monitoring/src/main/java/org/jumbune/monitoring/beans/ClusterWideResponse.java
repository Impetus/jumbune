package org.jumbune.monitoring.beans;

import java.util.Map;

/**
 * Pojo to store response of various cluster wide profiling stats
 * 
 */
public class ClusterWideResponse {

	private Map<String, String> jobTracker;
	private Map<String, String> nameNode;
	private Map<String, String> resourceManager;

	/**
	 * @return the jobTracker
	 */
	public Map<String, String> getJobTracker() {
		return jobTracker;
	}

	/**
	 * @param jobTracker
	 *            the jobTracker to set
	 */
	public void setJobTracker(Map<String, String> jobTracker) {
		this.jobTracker = jobTracker;
	}

	/**
	 * @return the nameNode
	 */
	public Map<String, String> getNameNode() {
		return nameNode;
	}

	/**
	 * @param nameNode
	 *            the nameNode to set
	 */
	public void setNameNode(Map<String, String> nameNode) {
		this.nameNode = nameNode;
	}

  /**
   * @return the resourceManager
   */
  public Map<String, String> getResourceManager() {
    return resourceManager;
  }

  /**
   * @param resourceManager the resourceManager to set
   */
  public void setResourceManager(Map<String, String> resourceManager) {
    this.resourceManager = resourceManager;
  }

}
