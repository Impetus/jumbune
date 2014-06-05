package org.jumbune.profiling.beans;

/**
 * Pojo to store the various profiling stats in corresponding category lists
 * 
*/
public class CategoryInfo {

	private ClusterWideInfo clusterWide;
	private SystemStats systemStats;
	private WorkerJMXInfo workerJMXInfo;

	/**
	 * @return the clusterWide
	 */
	public ClusterWideInfo getClusterWide() {
		return clusterWide;
	}

	/**
	 * @param clusterWide
	 *            the clusterWide to set
	 */
	public void setClusterWide(ClusterWideInfo clusterWide) {
		this.clusterWide = clusterWide;
	}

	
	/**
	 * @return the systemStats
	 */
	public SystemStats getSystemStats() {
		return systemStats;
	}

	/**
	 * @param systemStats
	 *            the systemStats to set
	 */
	public void setSystemStats(SystemStats systemStats) {
		this.systemStats = systemStats;
	}

	/**
	 * @param workerJMXInfo the workerJMXInfo to set
	 */
	public void setWorkerJMXInfo(WorkerJMXInfo workerJMXInfo) {
		this.workerJMXInfo = workerJMXInfo;
	}

	/**
	 * @return the workerJMXInfo
	 */
	public WorkerJMXInfo getWorkerJMXInfo() {
		return workerJMXInfo;
	}

	

}
