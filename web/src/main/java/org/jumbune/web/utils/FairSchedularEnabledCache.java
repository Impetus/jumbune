package org.jumbune.web.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.clusteranalysis.queues.SchedulerService;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.utils.LRUCache;
import org.jumbune.web.services.ClusterAnalysisService;

/**
 * 
 * FairSchedularEnabledCache checks whether fair scheduler is enable on a
 * particular cluster or not key = clusterName value = boolean
 *
 */
public class FairSchedularEnabledCache extends LRUCache<String, Boolean> {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LogManager.getLogger(FairSchedularEnabledCache.class);

	public FairSchedularEnabledCache(int capacity) {
		super(capacity);
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<String, Boolean> eldest) {
		if (size() > super.getCapacity()) {
			remove(eldest.getKey());
			return true;
		}
		return false;
	}
	
	public boolean isFairScheduler(String clusterName) {
		return isFairScheduler(ClusterAnalysisService.cache.get(clusterName));
	}
	
	public synchronized boolean isFairScheduler(Cluster cluster) {
		Boolean flag = get(cluster.getClusterName());
		if (flag == null) {
			try {
				flag = SchedulerService.getInstance().isFairScheduler(cluster);
			} catch (Exception e) {
				LOGGER.error("Unable to fetch queue info through api", e);
				String value = RemotingUtil.getHadoopConfigurationValue(
						cluster, "yarn-site.xml", "yarn.resourcemanager.scheduler.class");
				if (value == null || value.isEmpty()) {
					String hadoopDistributionType = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
					if (hadoopDistributionType.equals(Constants.APACHE)) {
						flag = false;
					} else {
						flag = true;
					}
				} else if (value.contains("FairScheduler")) {
					flag = true;
				} else {
					flag = false;
				}
			}
			put(cluster.getClusterName(), flag);
		}
		return flag;
	}

}
