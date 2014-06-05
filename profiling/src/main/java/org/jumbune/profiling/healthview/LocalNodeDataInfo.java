/**
 * 
 */
package org.jumbune.profiling.healthview;

/**
 * This class provides how the local data of a node is being utilized.
 * 
 */
public class LocalNodeDataInfo implements ResultInfo {

	private double localDataUsage;

	/**
	 * @return the localDataUsage
	 */
	public double getLocalDataUsage() {
		return localDataUsage;
	}

	/**
	 * @param localDataUsage
	 *            the localDataUsage to set
	 */
	public void setLocalDataUsage(double localDataUsage) {
		this.localDataUsage = localDataUsage;
	}

}
