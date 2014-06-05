package org.jumbune.profiling.beans;



/**
 * Pojo to store response of various profiling stats corresponding to their categories.
 */
public class StatsResult {

	/** The cluster wide. */
	private ClusterWideResponse clusterWide;
	
	/** The hadoop jmx. */
	private HadoopJMXResponse hadoopJMX;
	
	/** The system stats. */
	private SystemStatsResponse systemStats;


	/**
	 * Gets the cluster wide.
	 *
	 * @return the clusterWide
	 */
	public ClusterWideResponse getClusterWide() {
		return clusterWide;
	}

	/**
	 * Sets the cluster wide.
	 *
	 * @param clusterWide the clusterWide to set
	 */
	public void setClusterWide(ClusterWideResponse clusterWide) {
		this.clusterWide = clusterWide;
	}

	/**
	 * Gets the hadoop jmx.
	 *
	 * @return the hadoopJMX
	 */
	public HadoopJMXResponse getHadoopJMX() {
		return hadoopJMX;
	}

	/**
	 * Sets the hadoop jmx.
	 *
	 * @param hadoopJMX the hadoopJMX to set
	 */
	public void setHadoopJMX(HadoopJMXResponse hadoopJMX) {
		this.hadoopJMX = hadoopJMX;
	}

	/**
	 * Gets the system stats.
	 *
	 * @return the systemStats
	 */
	public SystemStatsResponse getSystemStats() {
		return systemStats;
	}

	/**
	 * Sets the system stats.
	 *
	 * @param systemStats the systemStats to set
	 */
	public void setSystemStats(SystemStatsResponse systemStats) {
		this.systemStats = systemStats;
	}

}
