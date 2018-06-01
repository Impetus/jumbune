package org.jumbune.web.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.monitoring.beans.SystemStats;
import org.jumbune.monitoring.yarn.beans.YarnCategoryInfo;
import org.jumbune.monitoring.yarn.beans.YarnClusterWideInfo;
import org.jumbune.monitoring.yarn.beans.YarnWorkerJMXInfo;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.InfluxDBConf;

import org.jumbune.web.services.ClusterAnalysisService;

public class NodeStatsMetadata {

	private static final String DATA_NODE = "dataNode";

	private static final String MEMORY2 = "memory";

	private static final String CPU2 = "cpu";

	private static final String CLUSTER_WIDE = "clusterWide";

	private static final String WORKER_JMX_INFO = "workerJMXInfo";

	private static final String SYSTEM_STATS = "systemStats";

	private static final String NAME_NODE = "nameNode";

	private static final String REGEX = "\\.";

	String nodeIP;

	String clusterName;

	/**
	 * Map<String, Tab> here key represent statName (eg. CpuUsage,
	 * NumberOfCores) and value is Tab. Tab can be BOTH, NODE_SPECIFIC,
	 * ALL_NODES. If a stat is opened in both 'All Nodes' and individual tab
	 * then value is BOTH. If a tab is opened only in ALL_NODES then value is
	 * ALL_NODES and if it is only opened in NODE_SPECIFIC then this map entity
	 * value is NODE_SPECIFIC
	 */
	/* ClusterWideInfo */
	Map<String, Tab> nameNode, resourceManager;
	/* SystemStats */
	Map<String, Tab> cpu, memory, os;
	/* WorkerJMXInfo */
	Map<String, Tab> dataNode, nodeManager;

	InfluxDBConf influxDBConf;

	boolean isNameNodestatsAvailable = true;
	boolean isResourceManagerStatsAvailable = true;
	boolean isDataNodeStatsAvailable = true;
	boolean isNodeManagerStatsAvailable = true;

	boolean isBackgroundProcess = false;

	public NodeStatsMetadata(String nodeIP, String clusterName) throws Exception {
		cpu = new HashMap<>(3);
		memory = new HashMap<>(5);
		os = new HashMap<>(5);
		nameNode = new HashMap<>(3);
		resourceManager = new HashMap<>(3);
		dataNode = new HashMap<>(3);
		nodeManager = new HashMap<>(3);

		this.nodeIP = nodeIP;
		this.clusterName = clusterName;
		influxDBConf = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
		checkAvalability();
	}

	private void checkAvalability() {
		Cluster cluster = ClusterAnalysisService.cache.get(this.clusterName);
		if (cluster.getNameNodes().getHosts().indexOf(nodeIP) == -1) {
			isNameNodestatsAvailable = false;
		}
		if (cluster.getTaskManagers().getHosts().indexOf(nodeIP) == -1) {
			isResourceManagerStatsAvailable = false;
		}
		if (cluster.getWorkers().getHosts().indexOf(nodeIP) == -1) {
			isDataNodeStatsAvailable = false;
		}
		if (cluster.getWorkers().getHosts().indexOf(nodeIP) == -1) {
			isNodeManagerStatsAvailable = false;
		}
	}

	public void updateStats(YarnCategoryInfo categoryInfo) {

		YarnClusterWideInfo clusterWideInfo = categoryInfo.getClusterWide();
		if (clusterWideInfo != null) {
			if (clusterWideInfo.getNameNode() != null) {
				addAllToNodeSpecific(nameNode, clusterWideInfo.getNameNode());

			}
			if (clusterWideInfo.getResourceManager() != null) {
				addAllToNodeSpecific(resourceManager, clusterWideInfo.getResourceManager());
			}
		}

		SystemStats systemStats = categoryInfo.getSystemStats();
		if (systemStats != null) {
			if (systemStats.getCpu() != null) {
				addAllToNodeSpecific(cpu, systemStats.getCpu());
			}
			if (systemStats.getMemory() != null) {
				addAllToNodeSpecific(memory, systemStats.getMemory());
			}
			if (systemStats.getOs() != null) {
				addAllToNodeSpecific(os, systemStats.getOs());
			}

		}

		YarnWorkerJMXInfo workerJmxInfo = categoryInfo.getWorkerJMXInfo();
		if (workerJmxInfo != null) {
			if (workerJmxInfo.getDataNode() != null) {
				addAllToNodeSpecific(dataNode, workerJmxInfo.getDataNode());
			}
			if (workerJmxInfo.getNodeManager() != null) {
				addAllToNodeSpecific(nodeManager, workerJmxInfo.getNodeManager());
			}
		}

	}

	private void addAllToNodeSpecific(Map<String, Tab> stats, Collection<String> list) {
		for (String stat : list) {
			add(stats, stat, Tab.NODE_SPECIFIC);
		}
	}

	public void addStat(String fullStatName, Tab tabToAdd) {
		String[] str = fullStatName.split(REGEX, 3);
		String category = str[0];
		String subCategory = str[1];
		String stat = str[2];
		switch (category) {
		case CLUSTER_WIDE: {
			if (NAME_NODE.equals(subCategory)) {
				add(nameNode, stat, tabToAdd);
			} else {
				add(resourceManager, stat, tabToAdd);
			}
			break;
		}
		case SYSTEM_STATS: {
			if (CPU2.equals(subCategory)) {
				add(cpu, stat, tabToAdd);
			} else if (MEMORY2.equals(subCategory)) {
				add(memory, stat, tabToAdd);
			} else {
				add(os, stat, tabToAdd);
			}
			break;
		}
		case WORKER_JMX_INFO: {
			if (DATA_NODE.equals(subCategory)) {
				add(dataNode, stat, tabToAdd);
			} else {
				add(nodeManager, stat, tabToAdd);
			}
		}
		}
	}

	/**
	 * Suppose if a stat is already added in ALL_NODES and we have just added
	 * that stat in a individual tab (in browser) then in map (Map<String, Tab>)
	 * value is BOTH.
	 * 
	 * @param stats
	 * @param stat
	 * @param tabToAdd
	 */
	private void add(Map<String, Tab> stats, String stat, Tab tabToAdd) {
		Tab existingTab = stats.get(stat);
		if (existingTab == Tab.BOTH) {
			return;
		}

		if (existingTab == null) {
			stats.put(stat, tabToAdd);
			return;
		}

		if (existingTab != tabToAdd) {
			stats.put(stat, Tab.BOTH);
		}
	}

	public void removeStat(String fullStatName, Tab tabToRemove) {
		String[] str = fullStatName.split(REGEX, 3);
		String category = str[0];
		String subCategory = str[1];
		String stat = str[2];

		switch (category) {
		case CLUSTER_WIDE: {
			if (NAME_NODE.equals(subCategory)) {
				remove(nameNode, stat, tabToRemove);
			} else {
				remove(resourceManager, stat, tabToRemove);
			}
			break;
		}
		case SYSTEM_STATS: {
			if (CPU2.equals(subCategory)) {
				remove(cpu, stat, tabToRemove);
			} else if (MEMORY2.equals(subCategory)) {
				remove(memory, stat, tabToRemove);
			} else {
				remove(os, stat, tabToRemove);
			}
			break;
		}
		case WORKER_JMX_INFO: {
			if (DATA_NODE.equals(subCategory)) {
				remove(dataNode, stat, tabToRemove);
			} else {
				remove(nodeManager, stat, tabToRemove);
			}
		}
		}
	}

	/**
	 * Suppose if a stat is already added in 'BOTH' 'All Nodes' tab and node
	 * specific tab in brower then here map (Map<String, Tab>) value is 'BOTH'
	 * and now user is removing that stat from 'All Nodes' stat then its value
	 * at last should be 'NODE_SPECIFIC'. value is BOTH.
	 * 
	 * @param stats
	 * @param stat
	 * @param tabToRemove
	 */
	private void remove(Map<String, Tab> stats, String stat, Tab tabToRemove) {
		if (stats.get(stat) == Tab.BOTH) {
			if (tabToRemove == Tab.ALL_NODES) {
				stats.put(stat, Tab.NODE_SPECIFIC);
			} else {
				stats.put(stat, Tab.ALL_NODES);
			}
			return;
		}
		if (stats.get(stat) == tabToRemove) {
			stats.remove(stat);
		}
	}

	/**
	 * Used if background process of cluster metrics is enabled Suppose if user
	 * closes a tab in Hadoop metrics and system stats, then it means all the
	 * stats which are opened in that tab have to be closed. So in this method
	 * we are removing all the stat names that are opened in that tab / nodeIP
	 */
	public void removeNodeSpecificAllStats() {
		removeAll(nameNode);
		removeAll(resourceManager);
		removeAll(cpu);
		removeAll(memory);
		removeAll(os);
		removeAll(dataNode);
		removeAll(nodeManager);
	}

	private void removeAll(Map<String, Tab> stats) {
		for (Entry<String, Tab> e : stats.entrySet()) {
			remove(stats, e.getKey(), Tab.NODE_SPECIFIC);
		}
	}

	/**
	 * Return 'All Nodes' tab stats names that are / were opened last time in
	 * browser
	 * 
	 * @return
	 */
	public YarnCategoryInfo getAllNodesStatsNames() {
		YarnCategoryInfo categoryInfo = new YarnCategoryInfo();

		YarnClusterWideInfo clusterWide = new YarnClusterWideInfo();

		for (Entry<String, Tab> e : nameNode.entrySet()) {
			if (e.getValue() != Tab.NODE_SPECIFIC) {
				clusterWide.addNameNodeStat(e.getKey());
			}
		}
		for (Entry<String, Tab> e : resourceManager.entrySet()) {
			if (e.getValue() != Tab.NODE_SPECIFIC) {
				clusterWide.addResourceManagerStat(e.getKey());
			}
		}
		categoryInfo.setClusterWide(clusterWide);

		SystemStats systemStats = new SystemStats();

		for (Entry<String, Tab> e : cpu.entrySet()) {
			if (e.getValue() != Tab.NODE_SPECIFIC) {
				systemStats.addCpuStat(e.getKey());
			}
		}
		for (Entry<String, Tab> e : memory.entrySet()) {
			if (e.getValue() != Tab.NODE_SPECIFIC) {
				systemStats.addMemoryStat(e.getKey());
			}
		}
		for (Entry<String, Tab> e : os.entrySet()) {
			if (e.getValue() != Tab.NODE_SPECIFIC) {
				systemStats.addOsStat(e.getKey());
			}
		}

		categoryInfo.setSystemStats(systemStats);

		YarnWorkerJMXInfo workerJMXInfo = new YarnWorkerJMXInfo();
		for (Entry<String, Tab> e : nodeManager.entrySet()) {
			if (e.getValue() != Tab.NODE_SPECIFIC) {
				workerJMXInfo.addNodeManagerStat(e.getKey());
			}
		}
		for (Entry<String, Tab> e : dataNode.entrySet()) {
			if (e.getValue() != Tab.NODE_SPECIFIC) {
				workerJMXInfo.addDataNodeStat(e.getKey());
			}
		}

		categoryInfo.setWorkerJMXInfo(workerJMXInfo);

		return categoryInfo;
	}

	/**
	 * Return node specific stats names that are / were opened last time in
	 * browser
	 * 
	 * @return
	 */
	public YarnCategoryInfo getNodeSpecificStatsNames() {
		YarnCategoryInfo categoryInfo = new YarnCategoryInfo();

		if (this.isNameNodestatsAvailable || this.isResourceManagerStatsAvailable) {
			
			YarnClusterWideInfo clusterWide = new YarnClusterWideInfo();
			
			if (this.isNameNodestatsAvailable) {
				for (Entry<String, Tab> e : nameNode.entrySet()) {
					if (e.getValue() != Tab.ALL_NODES) {
						clusterWide.addNameNodeStat(e.getKey());
					}
				}
			}
			if (this.isResourceManagerStatsAvailable) {
				for (Entry<String, Tab> e : resourceManager.entrySet()) {
					if (e.getValue() != Tab.ALL_NODES) {
						clusterWide.addResourceManagerStat(e.getKey());
					}
				}
			}
			
			categoryInfo.setClusterWide(clusterWide);
		}

		SystemStats systemStats = new SystemStats();

		for (Entry<String, Tab> e : cpu.entrySet()) {
			if (e.getValue() != Tab.ALL_NODES) {
				systemStats.addCpuStat(e.getKey());
			}
		}
		for (Entry<String, Tab> e : memory.entrySet()) {
			if (e.getValue() != Tab.ALL_NODES) {
				systemStats.addMemoryStat(e.getKey());
			}
		}
		for (Entry<String, Tab> e : os.entrySet()) {
			if (e.getValue() != Tab.ALL_NODES) {
				systemStats.addOsStat(e.getKey());
			}
		}

		categoryInfo.setSystemStats(systemStats);

		if (this.isDataNodeStatsAvailable || this.isNodeManagerStatsAvailable) {
			
			YarnWorkerJMXInfo workerJMXInfo = new YarnWorkerJMXInfo();
			
			if (this.isNodeManagerStatsAvailable) {
				for (Entry<String, Tab> e : nodeManager.entrySet()) {
					if (e.getValue() != Tab.ALL_NODES) {
						workerJMXInfo.addNodeManagerStat(e.getKey());
					}
				}
			}
			if (this.isDataNodeStatsAvailable) {
				for (Entry<String, Tab> e : dataNode.entrySet()) {
					if (e.getValue() != Tab.ALL_NODES) {
						workerJMXInfo.addDataNodeStat(e.getKey());
					}
				}
			}
			
			categoryInfo.setWorkerJMXInfo(workerJMXInfo);
		}
		
		return categoryInfo;
	}

	public void clearSelectedStats() {
		if (!isBackgroundProcess) {
			cpu.clear();
			memory.clear();
			os.clear();
			nameNode.clear();
			resourceManager.clear();
			dataNode.clear();
			nodeManager.clear();
		}
	}
}
