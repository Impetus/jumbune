package org.jumbune.clusterprofiling.yarn.service;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.jumbune.clusterprofiling.beans.JobQueueBean;
import org.jumbune.clusterprofiling.beans.LiveCapacityStats;
import org.jumbune.clusterprofiling.beans.QueueStats;
import org.jumbune.clusterprofiling.beans.RackAwareStats;
import org.jumbune.clusterprofiling.service.ClusterProfilingService;
import org.jumbune.clusterprofiling.yarn.helper.ClusterProfilingHelper;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.profiling.JobOutput;
import org.jumbune.utils.yarn.communicators.MRCommunicator;
import org.jumbune.utils.yarn.communicators.RMCommunicator;

import org.jumbune.common.beans.EffCapUtilizationStats;


/**
 * The Class ClusterProfilingServiceImpl contains all the methods responsible for populating  yarn cluster profiling stats.
 */
public class ClusterProfilingServiceImpl implements ClusterProfilingService {

	private static ClusterProfilingHelper helper;
	private RMCommunicator rmCommunicator;
	private MRCommunicator mrCommunicator;
	
	static {
		 helper = ClusterProfilingHelper.getInstance();
	}
	
	public ClusterProfilingServiceImpl(RMCommunicator rmCommunicator, MRCommunicator mrCommunicator) {
		this.rmCommunicator = rmCommunicator;
		this.mrCommunicator = mrCommunicator;
	}

	
	/* (non-Javadoc)
	 * @see com.impetus.jumbune.clusterprofiling.service.ClusterProfilingService#getQueueStats(org.jumbune.common.job.JumbuneRequest)
	 */
	@Override
	public List <QueueStats> getQueueStats(Cluster cluster) throws IOException, InterruptedException {
		return helper.populateQueueStats(cluster.getClusterName(), rmCommunicator);
	}

	/* (non-Javadoc)
	 * @see com.impetus.jumbune.clusterprofiling.service.ClusterProfilingService#getRackAwareStats(org.jumbune.common.job.JumbuneRequest)
	 */
	@Override
	public RackAwareStats getRackAwareStats(Cluster cluster) throws Exception {
		return helper.getRackAwareStats(cluster, rmCommunicator);
	}

	/* (non-Javadoc)
	 * @see com.impetus.jumbune.clusterprofiling.service.ClusterProfilingService#getEffCapUtilizationStats(org.jumbune.common.job.JumbuneRequest, java.lang.String)
	 */
	@Override
	public List <EffCapUtilizationStats> getEffCapUtilizationStats(Cluster cluster, List<ApplicationReport> list) throws Exception {
		
		return helper.prepareAndPersistRunningContainerUtilization(cluster, mrCommunicator, list);
	}

	/* (non-Javadoc)
	 * @see com.impetus.jumbune.clusterprofiling.service.ClusterProfilingService#getJobStats(org.jumbune.common.job.JumbuneRequest, java.lang.String)
	 */
	@Override
	public JobOutput getJobStats(Cluster cluster, String jobId) {
		return helper.getJobDetails(cluster, jobId);
	}

	/* (non-Javadoc)
	 * @see com.impetus.jumbune.clusterprofiling.service.ClusterProfilingService#getContainerStatus(org.jumbune.common.beans.cluster.Cluster)
	 */
	@Override
	public LiveCapacityStats getContainerStatus(Cluster cluster) throws IOException {
		return helper.getContainerStatus(cluster, rmCommunicator);
	}

	
	public void copyJobHistoryFile(Cluster cluster) throws Exception {
		helper.copyJobHistoryFile(cluster, rmCommunicator);
		
	}

	@Override
	public List<JobQueueBean> getQueueUserStats(Cluster cluster) throws Exception {
		return helper.getQueueUserStats(cluster, rmCommunicator);
		
	}
}
