package org.jumbune.clusterprofiling.service;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.jumbune.clusterprofiling.beans.JobQueueBean;
import org.jumbune.clusterprofiling.beans.LiveCapacityStats;
import org.jumbune.clusterprofiling.beans.QueueStats;
import org.jumbune.clusterprofiling.beans.RackAwareStats;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.profiling.JobOutput;

import org.jumbune.common.beans.EffCapUtilizationStats;

/**
 * The Interface ClusterProfilingService is responsible for populating all the cluster profiling beans.
 */
public interface ClusterProfilingService {
	
	
	/**
	 * Gets the queue stats.
	 *
	 * @param cluster the cluster
	 * @return the queue stats
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	List <QueueStats> getQueueStats(Cluster cluster) throws Exception;
	
	/**
	 * Gets the rack aware stats.
	 *
	 * @param cluster the cluster
	 * @return the rack aware stats
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	RackAwareStats getRackAwareStats(Cluster cluster) throws Exception;
	
	/**
	 * Gets the effective cap utilization stats.
	 *
	 * @param cluster the cluster
	 * @return the eff cap utilization stats
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	List <EffCapUtilizationStats> getEffCapUtilizationStats(Cluster cluster, List<ApplicationReport> list) throws Exception;

	/**
	 * Gets the container status.
	 *
	 * @param cluster the cluster
	 * @return the container status
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	LiveCapacityStats getContainerStatus (Cluster cluster) throws IOException;
	
	/**
	 * Gets the job stats as per JobId.
	 *
	 * @param cluster the cluster
	 * @param jobId the job id
	 * @return the job stats
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	
	JobOutput getJobStats (Cluster cluster, String jobId) throws IOException;
	
	/**
	 * Copy job history file.
	 *
	 * @param cluster the cluster
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	void copyJobHistoryFile (Cluster cluster) throws Exception;
	
	
	/**
	 * Gets the queue user stats.
	 *
	 * @param cluster the cluster
	 * @return the queue user stats
	 * @throws Exception 
	 */
	List<JobQueueBean> getQueueUserStats(Cluster cluster) throws Exception ;
}
