package org.jumbune.profiling.healthview;

import org.jumbune.profiling.beans.ClusterInfo;

/**
 * The Class TaskTrackerViewInfo is a bean class for setting and retrieving the total map and reduce slots available.
 */
public class TaskTrackerViewInfo {

	private int totalMapSlotsAvailable;
	private int totalReduceSlotsAvailable;
	private ClusterInfo clusterInfo;

	/**
	 * @return the clusterInfo
	 */
	public ClusterInfo getClusterInfo() {
		return clusterInfo;
	}

	/**
	 * @param clusterInfo
	 *            the clusterInfo to set
	 */
	public void setClusterInfo(ClusterInfo clusterInfo) {
		this.clusterInfo = clusterInfo;
	}

	/**
	 * @return the totalMapSlotsAvailable
	 */
	public int getTotalMapSlotsAvailable() {
		return totalMapSlotsAvailable;
	}

	/**
	 * @param totalMapSlotsAvailable
	 *            the totalMapSlotsAvailable to set
	 */
	public void setTotalMapSlotsAvailable(int totalMapSlotsAvailable) {
		this.totalMapSlotsAvailable = totalMapSlotsAvailable;
	}

	/**
	 * @return the totalReduceSlotsAvailable
	 */
	public int getTotalReduceSlotsAvailable() {
		return totalReduceSlotsAvailable;
	}

	/**
	 * @param totalReduceSlotsAvailable
	 *            the totalReduceSlotsAvailable to set
	 */
	public void setTotalReduceSlotsAvailable(int totalReduceSlotsAvailable) {
		this.totalReduceSlotsAvailable = totalReduceSlotsAvailable;
	}

}
