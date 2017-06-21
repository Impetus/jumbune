package org.jumbune.clusterprofiling.service;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.jumbune.clusterprofiling.NonYarnClusterProfilingHelper;
import org.jumbune.clusterprofiling.beans.JobQueueBean;
import org.jumbune.clusterprofiling.beans.LiveCapacityStats;
import org.jumbune.clusterprofiling.beans.QueueStats;
import org.jumbune.clusterprofiling.beans.RackAwareStats;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.profiling.JobOutput;
import org.jumbune.common.utils.RemotingUtil;

import org.jumbune.common.beans.EffCapUtilizationStats;

/**
 * The Class ClusterProfilingServiceImpl contains all the methods responsible for populating non yarn cluster profiling stats.
 */
public class NonYarnClusterProfilingServiceImpl implements ClusterProfilingService{

	
	NonYarnClusterProfilingHelper clusterProfilingHelper = null ;
	JobClient jobClient = null ;
	
	
	 public NonYarnClusterProfilingServiceImpl(Cluster cluster) throws IOException {
		clusterProfilingHelper = new NonYarnClusterProfilingHelper();		
		jobClient = RemotingUtil.getJobClient(cluster);
	}

	/* (non-Javadoc)
	 * @see com.impetus.jumbune.clusterprofiling.service.ClusterProfilingService#getQueueStats(org.jumbune.common.job.Config)
	 */
	@Override
	public List<QueueStats> getQueueStats(Cluster cluster) throws IOException, InterruptedException{
		List<QueueStats> queueStatList = clusterProfilingHelper.getQueueStats(cluster, jobClient);
		return queueStatList;
		
		
	}

	/* (non-Javadoc)
	 * @see com.impetus.jumbune.clusterprofiling.service.ClusterProfilingService#getRackAwareStats(org.jumbune.common.job.Config)
	 */
	@Override
	public RackAwareStats getRackAwareStats(Cluster cluster) throws IOException {
		
		RackAwareStats rackAwareStats = clusterProfilingHelper.getRackStats(cluster, jobClient);
			
		return rackAwareStats;
	}

	/* (non-Javadoc)
	 * @see com.impetus.jumbune.clusterprofiling.service.ClusterProfilingService#getEffCapUtilizationStats(org.jumbune.common.job.Config)
	 */
	@Override
	public List<EffCapUtilizationStats> getEffCapUtilizationStats(Cluster cluster, List<ApplicationReport> list) throws IOException, InterruptedException {
		List<EffCapUtilizationStats> effCapUtilizationStatsList = clusterProfilingHelper.getEffUtilizationStats(cluster, jobClient);
		return effCapUtilizationStatsList;
	}

	/* (non-Javadoc)
	 * @see com.impetus.jumbune.clusterprofiling.service.ClusterProfilingService#getJobStats(org.jumbune.common.job.JumbuneRequest, java.lang.String)
	 */
	@Override
	public JobOutput getJobStats(Cluster cluster, String jobID) throws IOException {
		return clusterProfilingHelper.getJobStats(cluster, jobID);
	}

	@Override
	public LiveCapacityStats getContainerStatus(Cluster cluster) throws IOException {		
		return clusterProfilingHelper.computeTaskSlots(cluster);
	}

	@Override
	public void copyJobHistoryFile(Cluster cluster) throws Exception {
		
	}

	@Override
	public List<JobQueueBean> getQueueUserStats(Cluster cluster) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
