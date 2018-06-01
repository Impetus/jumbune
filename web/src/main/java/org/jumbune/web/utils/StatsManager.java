package org.jumbune.web.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.monitoring.beans.CategoryInfo;
import org.jumbune.monitoring.yarn.beans.YarnCategoryInfo;

/**
 * [Related to 'Hadoop Daemons and System Metrics' section in Analyze Cluster
 * Dashboard]. Stats manager maintains information of all opened tabs
 * stats/graphs in browsers. When called its write() method, it fetches values
 * of those stats (that are opened in browser) and persists in database. The
 * main purpose of StatsManager is that no other class function has to fetch
 * stats values and write data individually. To write data, functions just have
 * to call write() method. Its a singleton class.
 */
public class StatsManager {

	private int STATS_WRITING_TIME_INTERVAL = 15000; // in seconds

	private final int STATS_RESET_TIME = 10; // in minutes

	private long lastLaunched, lastClearTime;

	// Key is nodeIP
	private Map<String, NodeStatsMetadata> statsCollection;

	private ExecutorService executor;

	private static volatile StatsManager instance;

	private StatsManager() {
		lastLaunched = 0;
		lastClearTime = 0;
		statsCollection = new HashMap<String, NodeStatsMetadata>();
		executor = Executors.newFixedThreadPool(10);
	}

	public static StatsManager getInstance() {
		if (instance == null) {
			synchronized (StatsManager.class) {
				if (instance == null) {
					instance = new StatsManager();
				}
			}
		}
		return instance;
	}
	
	public void removeNode(String nodeIP) {
		this.statsCollection.remove(nodeIP);
	}

	/**
	 * This method will be called (regularly) when SystemMetricsProcess /
	 * background process is not enabled. It updates the corresponding stats of
	 * that node ip (or tab opened in browser under 'Hadoop Daemons and System
	 * Metrics' section) of SystemManager. It also writes the data (also see
	 * canStartWriting() method)
	 * 
	 * @param clusterName
	 * @param nodeIP
	 *            node ip /tab name in browser
	 * @param categoryInfo
	 *            stats/ graphs that are opened in browser (eg.
	 *            {"workerJMXInfo": {"nodeManager": ["JvmMetrics.GcCount"],
	 *            "dataNode": ["JvmMetrics.GcCount", "JvmMetrics.LogFatal"]}})
	 * @throws Exception
	 */
	public void submit(String clusterName, String nodeIP, YarnCategoryInfo categoryInfo) throws Exception {
		resetSelectedStats();

		updateSelectedStats(nodeIP, clusterName, categoryInfo);

		write();
	}

	/**
	 * To be called by SystemMetricsProcess thead or submit() method.
	 * 
	 * @see org.jumbune.web.process.SystemMetricsProcess
	 */
	public void write() {
		if (canStartWriting()) {
			startWriting();
		}
	}

	private synchronized void resetSelectedStats() {
		long currTime = System.currentTimeMillis();

		// Clear selected stats if (current Time - last Clear Time) is greater
		// than 10 minutes
		if ((currTime - lastClearTime) / 60000 > STATS_RESET_TIME) {
			for (Entry<String, NodeStatsMetadata> e : statsCollection.entrySet()) {
				e.getValue().clearSelectedStats();
			}
			lastClearTime = currTime;
		}
	}

	private void updateSelectedStats(String nodeIP, String clusterName, YarnCategoryInfo categoryInfo)
			throws Exception {
		NodeStatsMetadata statsManager = getNodeStatsMetadata(nodeIP, clusterName);
		statsManager.updateStats(categoryInfo);
	}

	private NodeStatsMetadata getNodeStatsMetadata(String nodeIP, String clusterName) throws Exception {
		NodeStatsMetadata statsManager = statsCollection.get(nodeIP);
		if (statsManager == null) {
			statsManager = new NodeStatsMetadata(nodeIP, clusterName);
			statsCollection.put(nodeIP, statsManager);
		}
		return statsManager;
	}

	/**
	 * Checks if the difference of current time and [the last time when stats
	 * data persisted] is greater than STATS_WRITING_TIME_INTERVAL. If yes then
	 * it allows to write (fetch stat values and persist into influxdb) data
	 * otherwise not
	 * 
	 * @return
	 */
	private synchronized boolean canStartWriting() {
		long currTime = System.currentTimeMillis();
		if ((currTime - lastLaunched) > STATS_WRITING_TIME_INTERVAL) {
			lastLaunched = currTime;
			return true;
		}
		return false;
	}

	/**
	 * It executes threads for all nodes which fetches stat/graph values and
	 * persist in database. We are using one thread for one node because it
	 * could take time for fetching values of a single node. (eg. it takes 4 sec
	 * for fetching CpuUsage of a single node, jmx values take about .3 seconds)
	 */
	private void startWriting() {
		for (Entry<String, NodeStatsMetadata> e : statsCollection.entrySet()) {
			executor.execute(new StatsWriterThread(e.getValue()));
		}
	}

	/**
	 * This method will be called (regularly) when SystemMetricsProcess /
	 * background process is enabled. It returns the last time opened
	 * stats/graphs/tabs in browser.
	 * 
	 * @param cluster
	 * @return
	 * @throws Exception
	 */
	public Map<String, CategoryInfo> getOpenedStats(Cluster cluster) throws Exception {
		Set<String> nodes = getClusterNodes(cluster);
		Map<String, CategoryInfo> map = new HashMap<String, CategoryInfo>(2);
		map.put(WebConstants.ALL_NODES,
				getNodeStatsMetadata(nodes.iterator().next(), cluster.getClusterName()).getAllNodesStatsNames());
		for (String nodeIP : nodes) {
			map.put(nodeIP, getNodeStatsMetadata(nodeIP, cluster.getClusterName()).getNodeSpecificStatsNames());
		}

		/*
		 * Returns eg. { "ALL NODES" : {"systemStats": {"cpu": ["NumberOfCores",
		 * "CpuUsage"],"memory": ["TotalSwap", "FreeMemory"]}}, "127.0.0.1" :
		 * {"workerJMXInfo": {"nodeManager": ["JvmMetrics.GcCount"], "dataNode":
		 * ["JvmMetrics.GcCount", "JvmMetrics.LogFatal"]}} }
		 */
		return map;
	}

	/**
	 * It returns all cluster node ips (workers, task managers, name nodes)
	 * 
	 * @param cluster
	 * @return
	 */
	private Set<String> getClusterNodes(Cluster cluster) {
		Set<String> clusterNodeSet = new HashSet<String>();
		clusterNodeSet.addAll(cluster.getWorkers().getHosts());
		clusterNodeSet.addAll(cluster.getNameNodes().getHosts());
		clusterNodeSet.addAll(cluster.getTaskManagers().getHosts());
		return clusterNodeSet;
	}

	public void stopBackgroundProcess(Cluster cluster) throws Exception {
		Set<String> nodes = getClusterNodes(cluster);
		NodeStatsMetadata metadata = null;
		for (String nodeIP : nodes) {
			metadata = getNodeStatsMetadata(nodeIP, cluster.getClusterName());
			if (metadata.isBackgroundProcess) {
				metadata.isBackgroundProcess = false;
				metadata.clearSelectedStats();
			}
		}
	}

	public void startBackgroundProcess(Cluster cluster) throws Exception {
		Set<String> nodes = getClusterNodes(cluster);
		NodeStatsMetadata metadata = null;
		for (String nodeIP : nodes) {
			metadata = getNodeStatsMetadata(nodeIP, cluster.getClusterName());
			if (!metadata.isBackgroundProcess) {
				metadata.clearSelectedStats();
				metadata.isBackgroundProcess = true;
			}
		}
	}

	/**
	 * This method will be called when SystemMetricsProcess / background process
	 * is enabled. If a user adds a stat/graph in browser then browser will
	 * notify to server to update StatsManager repository (statsCollection ->
	 * NodeStatsMetadata)
	 * 
	 * @param cluster
	 * @param nodeIP
	 *            tab/nodeIP in which stat/graph is added. nodeIP can also be
	 *            'ALL NODES'
	 * 
	 * @param fullStatName
	 * @throws Exception
	 */
	public void addStat(Cluster cluster, String nodeIP, String fullStatName) throws Exception {
		if (nodeIP.equals(WebConstants.ALL_NODES)) {
			Set<String> nodes = getClusterNodes(cluster);
			for (String node : nodes) {
				getNodeStatsMetadata(node, cluster.getClusterName()).addStat(fullStatName, Tab.ALL_NODES);
			}
		} else {
			getNodeStatsMetadata(nodeIP, cluster.getClusterName()).addStat(fullStatName, Tab.NODE_SPECIFIC);
		}
	}

	/**
	 * This method will be called when SystemMetricsProcess / background process
	 * is enabled. If a user removes a stat/graph in browser then browser will
	 * notify to server to update StatsManager repository (statsCollection ->
	 * NodeStatsMetadata)
	 * 
	 * @param cluster
	 * @param nodeIP
	 *            tab/nodeIP in which stat/graph is removed. nodeIP can also be
	 *            'ALL NODES'
	 * 
	 * @param fullStatName
	 * @throws Exception
	 */
	public void removeStat(Cluster cluster, String nodeIP, String fullStatName) throws Exception {

		if (nodeIP.equals(WebConstants.ALL_NODES)) {
			Set<String> nodes = getClusterNodes(cluster);
			for (String node : nodes) {
				getNodeStatsMetadata(node, cluster.getClusterName()).removeStat(fullStatName, Tab.ALL_NODES);
			}
		} else {
			getNodeStatsMetadata(nodeIP, cluster.getClusterName()).removeStat(fullStatName, Tab.NODE_SPECIFIC);
		}
	}

	/**
	 * This method will be called when SystemMetricsProcess / background process
	 * is enabled. If a user removes a tab ['Hadoop Daemons and System Metrics'
	 * section] in browser then browser will notify to server to update
	 * StatsManager repository (statsCollection -> NodeStatsMetadata). If a tab
	 * is removed, then it means all the stats/graphs under that tab are also
	 * closed.
	 * 
	 * @param cluster
	 * @param nodeIP
	 *            tab/nodeIP in which stat/graph is removed. nodeIP can also be
	 *            'ALL NODES'
	 * 
	 * @param fullStatName
	 * @throws Exception
	 */
	public void removeTab(String nodeIP) {
		NodeStatsMetadata metadata = statsCollection.get(nodeIP);
		if (metadata != null) {
			metadata.removeNodeSpecificAllStats();
		}
	}

}
