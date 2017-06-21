package org.jumbune.clusterprofiling.beans;

import java.util.List;

import org.jumbune.common.beans.EffCapUtilizationStats;


/**
 * The Class ClusterProfilingBean is a POJO containing all the beans required for cluster profiling.
 */
public class ClusterProfilingBean {
	
	
	/** The queue stats. */
	private List<QueueStats> queueStats ;	
	
	/** The rack aware stats. */
	private RackAwareStats rackAwareStats ;
	
	/** The eff cap utilization stats. */
	private List <EffCapUtilizationStats> effCapUtilizationStats ;

	/** The container stats. */
	private LiveCapacityStats liveContainerStats; 

	

	/**
	 * Gets the rack aware stats.
	 *
	 * @return the rack aware stats
	 */
	public RackAwareStats getRackAwareStats() {
		return rackAwareStats;
	}

	/**
	 * Sets the rack aware stats.
	 *
	 * @param rackAwareStats the new rack aware stats
	 */
	public void setRackAwareStats(RackAwareStats rackAwareStats) {
		this.rackAwareStats = rackAwareStats;
	}

	/**
	 * Gets the eff cap utilization stats.
	 *
	 * @return the eff cap utilization stats
	 */
	public List<EffCapUtilizationStats> getEffCapUtilizationStats() {
		return effCapUtilizationStats;
	}

	/**
	 * Sets the eff cap utilization stats.
	 *
	 * @param effCapUtilizationStats the new eff cap utilization stats
	 */
	public void setEffCapUtilizationStats(
			List<EffCapUtilizationStats> effCapUtilizationStats) {
		this.effCapUtilizationStats = effCapUtilizationStats;
	}

	/**
	 * Gets the queue stats.
	 *
	 * @return the queue stats
	 */
	public List<QueueStats> getQueueStats() {
		return queueStats;
	}

	/**
	 * Sets the queue stats.
	 *
	 * @param queueStats the new queue stats
	 */
	public void setQueueStats(List<QueueStats> queueStats) {
		this.queueStats = queueStats;
	}

	/**
	 * Gets the live container stats.
	 *
	 * @return the live container stats
	 */
	public LiveCapacityStats getLiveContainerStats() {
		return liveContainerStats;
	}

	/**
	 * Sets the live container stats.
	 *
	 * @param liveContainerStats the new live container stats
	 */
	public void setLiveContainerStats(LiveCapacityStats liveContainerStats) {
		this.liveContainerStats = liveContainerStats;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClusterProfilingBean [queueStats=" + queueStats
				+ ", rackAwareStats=" + rackAwareStats
				+ ", effCapUtilizationStats=" + effCapUtilizationStats
				+ ", containerStats=" + liveContainerStats + "]";
	}
	

}
