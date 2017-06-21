package org.jumbune.web.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;

import com.google.gson.Gson;
import org.jumbune.common.beans.cluster.EnterpriseClusterDefinition;
import org.jumbune.web.services.ClusterAnalysisService;
import org.jumbune.web.utils.StatsManager;

/**
 * It calls StatsManager write() method at regular interval. Affected section is
 * [Analyze Cluster Dashboard -> Hadoop Metrics and System Stats] in UI
 */
public class SystemMetricsProcess extends Thread implements BackgroundProcess {

	private String clusterName;

	private boolean isOn;

	private long repeatInterval;

	private int maxErrors;

	private int totalErrors;

	private StatsManager statsManager;

	private static Logger LOGGER = LogManager.getLogger(SystemMetricsProcess.class);

	public SystemMetricsProcess(String clusterName, long repeatInterval, int maxErrors) {
		this.clusterName = clusterName;
		this.repeatInterval = repeatInterval;
		this.maxErrors = maxErrors;
		this.totalErrors = 0;
		this.statsManager = StatsManager.getInstance();
		this.isOn = true;

		try {
			this.statsManager.startBackgroundProcess(getCluster(clusterName));
		} catch (Exception e) {
			this.isOn = false;
			LOGGER.error("Unable to start cluster metrics process of cluster '" + clusterName, e);
		}
	}

	public Cluster getCluster(String clusterName) throws IOException {
		EnterpriseClusterDefinition cluster = ClusterAnalysisService.cache.getCluster(clusterName);
		if (cluster == null) {
			File file = new File(System.getenv("JUMBUNE_HOME") + "/clusters/" + clusterName + ".json");
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
			cluster = new Gson().fromJson(json.toString(), EnterpriseClusterDefinition.class);
			ClusterAnalysisService.updateClusterCache(clusterName, cluster);
		}
		return cluster;
	}

	public boolean isOn() {
		return isOn;
	}

	public void setOn(boolean isOn) {
		this.isOn = isOn;
		if (!this.isOn) {
			try {
				this.statsManager.stopBackgroundProcess(getCluster(clusterName));
			} catch (Exception e) {
				LOGGER.error("Unable to stop cluster metrics process of cluster '" + this.clusterName, e);
			}
		}
	}

	@Override
	public void run() {
		while (isOn) {
			if (totalErrors > maxErrors) {
				isOn = false;
			}
			this.statsManager.write();
			try {
				Thread.sleep(repeatInterval);
			} catch (InterruptedException e) {
				LOGGER.error("Unable to sleep the ClusterMetricsProcess, cluster [" + clusterName + "]",
						e.getMessage());
				return;
			}
		}
	}

}
