package org.jumbune.profiling.healthview;

/**
 * This class provides load(work and data load) partition of the cluster for each node.
 */
public class ClusterLoadInfo implements ResultInfo {

	private double workLoadPartition;

	private double dataLoadPartition;

	/**
	 * @return the workLoadPartition
	 */
	public double getWorkLoadPartition() {
		return workLoadPartition;
	}

	/**
	 * @param workLoadPartition
	 *            the workLoadPartition to set
	 */
	public void setWorkLoadPartition(double workLoadPartition) {
		this.workLoadPartition = workLoadPartition;
	}

	/**
	 * @return the dataLoadPartition
	 */
	public double getDataLoadPartition() {
		return dataLoadPartition;
	}

	/**
	 * @param dataLoadPartition
	 *            the dataLoadPartition to set
	 */
	public void setDataLoadPartition(double dataLoadPartition) {
		this.dataLoadPartition = dataLoadPartition;
	}

}
