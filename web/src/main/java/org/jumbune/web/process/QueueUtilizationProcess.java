package org.jumbune.web.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.CommunicatorFactory;
import org.jumbune.common.utils.Constants;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.yarn.communicators.RMCommunicator;

import org.jumbune.clusterprofiling.SchedulerService;
import org.jumbune.clusterprofiling.beans.JobQueueBean;
import org.jumbune.clusterprofiling.beans.QueueStats;
import org.jumbune.clusterprofiling.yarn.beans.FairSchedulerQueueInfo;
import org.jumbune.clusterprofiling.yarn.beans.Scheduler;
import org.jumbune.clusterprofiling.yarn.helper.ClusterProfilingHelper;
import org.jumbune.common.beans.cluster.EnterpriseCluster;
import org.jumbune.common.beans.cluster.EnterpriseClusterDefinition;
import org.jumbune.web.services.ClusterAnalysisService;
import org.jumbune.web.utils.YarnQueuesUtils;

/**
 * This process / thread, at regular interval [repeatInterval], fetches queue
 * utilization information and persists that data into influxdb. In UI, Analyze
 * Cluster Dashboard, the affected section is 'Yarn Queues'
 */
public class QueueUtilizationProcess extends Thread
		implements
			BackgroundProcess {

	private String clusterName;

	private boolean isOn;

	private long repeatInterval;

	private int maxErrors;

	private int totalErrors;

	/**
	 * Temporary variables
	 */

	private EnterpriseCluster cluster;
	private ClusterProfilingHelper cph;
	private List<QueueStats> queueStats;
	private List<JobQueueBean> jobQueueBeans;
	private SchedulerService schedulerService;
	private YarnQueuesUtils yarnQueuesUtils;
	private RMCommunicator rmCommunicator = null;
	private CommunicatorFactory cf;

	private static Logger LOGGER = LogManager
			.getLogger(QueueUtilizationProcess.class);

	public QueueUtilizationProcess(String clusterName, long repeatInterval,
			int maxErrors) {
		this.clusterName = clusterName;
		this.repeatInterval = repeatInterval;
		this.maxErrors = maxErrors;
		this.isOn = true;
		this.totalErrors = 0;

		// Getting Cluster object
		try {
			setClusterObject();
		} catch (IOException e) {
			LOGGER.error("Unable to get cluster [" + clusterName + "]",
					e.getMessage());
			this.isOn = false;
			return;
		}

		this.cf = CommunicatorFactory.getInstance();
		try {
			this.rmCommunicator = this.cf.createRMCommunicator(this.cluster);
		} catch (Exception e) {
			LOGGER.error("Unable to create RMCommunicator object [" + this.clusterName + "]", e);
			isOn = false;
			return;
		}

		// Creating Cluster Profiling Helper instance
		cph = ClusterProfilingHelper.getInstance();

		schedulerService = SchedulerService.getInstance();
		this.yarnQueuesUtils = YarnQueuesUtils.getInstance();
	}

	@Override
	public void run() {
		while (isOn) {
			persist();
			if (totalErrors > maxErrors) {
				isOn = false;
			}
			try {
				Thread.sleep(repeatInterval);
			} catch (InterruptedException e) {
				LOGGER.error(
						"Unable to sleep the QueueUtilizationProcess thread, cluster ["
								+ clusterName + "]",
						e);
				return;
			}
		}
	}

	private void persist() {
		try {
			setClusterObject();
		} catch (IOException e) {
			LOGGER.error("Unable to get cluster [" + clusterName + "]", e);
			totalErrors++;
			return;
		}

		try {
			//this.cph.copyJobHistoryFile(cluster, rmCommunicator);
		} catch (Exception e) {
			LOGGER.error("Unable to copy history files , cluster ["
					+ clusterName + "]", e);
			totalErrors++;
		}
		
		if (!rmCommunicator.getNodeIP().equals(cluster.getResourceManager())) {
			try {
				this.rmCommunicator = cf.createRMCommunicator(this.cluster);
			} catch (Exception e) {
				LOGGER.error("Unable to create RMCommunicator object [" + this.clusterName + "]", e);
				isOn = false;
				return;
			}
		}

		try {
			Scheduler scheduler = schedulerService.fetchSchedulerInfo(cluster);
			if (scheduler.isFairScheduler()) {
				this.yarnQueuesUtils.persistFairSchedulerData(scheduler.getFairSchedulerLeafQueues(),
						clusterName);
			} else {
				this.yarnQueuesUtils.persistCapacitySchedulerData(scheduler.getCapcitySchedulerLeafQueues(),
						clusterName);
			}
		} catch (Exception e) {
			LOGGER.error(
					"Unable to fetch queue stats from api or persist into influxdb",
					e);
			totalErrors++;
		}

		
		try {
			// Fetching queue utilization by individual user.
			jobQueueBeans = cph.getQueueUserStats(this.cluster, rmCommunicator);
			this.yarnQueuesUtils.persistUserQueueUtilizationData(jobQueueBeans,
					clusterName);
		} catch (Exception e) {
			LOGGER.error(
					"Unable to write data (queue utilization by individual user.) to influxdb, cluster ["
							+ clusterName + "]",
					e);
			totalErrors++;
		}
	}


	private void setClusterObject() throws IOException {
		this.cluster = ClusterAnalysisService.cache.getCluster(clusterName);
		if (this.cluster == null) {
			File file = new File(System.getenv("JUMBUNE_HOME") + "/clusters/"
					+ clusterName + ".json");
			StringBuffer json = new StringBuffer();
			BufferedReader br = null;
			try {
				String line;
				br = new BufferedReader(new FileReader(file));
				while ((line = br.readLine()) != null) {
					json.append(line);
				}
			} finally {
				if (br != null) {
					br.close();
				}
			}
			this.cluster = Constants.gson.fromJson(json.toString(),
					EnterpriseClusterDefinition.class);
			ClusterAnalysisService.updateClusterCache(clusterName,
					this.cluster);
		}
	}

	public boolean isOn() {
		return isOn;
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}

}
