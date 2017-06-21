package org.jumbune.web.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.profiling.utils.HTFProfilingException;
import org.jumbune.profiling.utils.ProfilerStats;

import org.jumbune.common.influxdb.InfluxDBUtil;
import org.jumbune.common.influxdb.beans.InfluxDBConstants;
import org.jumbune.web.services.ClusterAnalysisService;

public class StatsWriterThread implements Runnable {
	
	private static final String BACKSLASH_SPACE = "\\ ";

	private static final String SPACE = " ";

	private NodeStatsMetadata nodeStatsManager;

	private static final String SQUARE_BRACKET = "]";

	private static final String ERROR_WHILE_FETCHING_NAMENODE_STATS_OF_NODE = "Error while fetching namenode stats of node [";

	private static final String ERROR_WHILE_FETCHING_RESOURCE_MANAGER_STATS_OF_NODE = "Error while fetching resource manager stats of node [";

	private static final String ERROR_WHILE_FETCHING_CPU_STATS_OF_NODE = "Error while fetching cpu stats of node [";

	private static final String ERROR_WHILE_FETCHING_MEMORY_STATS_OF_NODE = "Error while fetching memory stats of node [";

	private static final String ERROR_WHILE_FETCHING_OS_STATS_OF_NODE = "Error while fetching os stats of node [";

	private static final String ERROR_WHILE_FETCHING_DATA_NODE_STATS_OF_NODE = "Error while fetching data node stats of node [";

	private static final String ERROR_WHILE_FETCHING_NODE_MANAGER_STATS_OF_NODE = "Error while fetching node manager stats of node [";

	private static final Logger LOGGER = LogManager.getLogger(StatsWriterThread.class);

	public StatsWriterThread(NodeStatsMetadata nodeStatsManager) {
		this.nodeStatsManager = nodeStatsManager;
	}

	@Override
	public void run() {
		Map<String, String> map = new HashMap<String, String>();
		ProfilerStats profilerStats = new ProfilerStats(
				ClusterAnalysisService.cache.get(nodeStatsManager.clusterName),nodeStatsManager.nodeIP);

		try {
			/* ClusterWideInfo */
			fetchNamenodeStatsValues(map, profilerStats);
			fetchResourceManagerStatsValues(map, profilerStats);

			/* SystemStats */
			fetchCpuStatsValues(map, profilerStats);
			fetchMemoryStatsValues(map, profilerStats);
			fetchOsStatsValues(map, profilerStats);

			/* WorkerJMXInfo */
			fetchDatanodeStatsValues(map, profilerStats);
			fetchNodeManagerStatsValues(map, profilerStats);
			
			InfluxDBUtil.writeClusterChartDataForNodeSpecific(nodeStatsManager.influxDBConf, nodeStatsManager.nodeIP,
					map);
		} catch (Exception e) {
			LOGGER.error("Error while writing node stats to influxdb", e.getMessage());
		}

	}

	private void fetchNodeManagerStatsValues(Map<String, String> map, ProfilerStats profilerStats) {
		if (nodeStatsManager.isNodeManagerStatsAvailable && !nodeStatsManager.nodeManager.isEmpty()
				&& profilerStats.isNodeManagerStatsAvailable()) {
			try {
				for (String nmStat : nodeStatsManager.nodeManager.keySet()) {
					if (nmStat.contains(SPACE)) {
						map.put(InfluxDBConstants.WORKERJMX_NODE_MANAGER + nmStat.replace(SPACE, BACKSLASH_SPACE), profilerStats.getNmStats(nmStat));
					} else {
						map.put(InfluxDBConstants.WORKERJMX_NODE_MANAGER + nmStat, profilerStats.getNmStats(nmStat));
					}
					
				}
			} catch (HTFProfilingException e) {
				LOGGER.error(ERROR_WHILE_FETCHING_NODE_MANAGER_STATS_OF_NODE + nodeStatsManager.nodeIP + SQUARE_BRACKET, e.getMessage());
			}
		}
	}

	private void fetchDatanodeStatsValues(Map<String, String> map, ProfilerStats profilerStats) {
		if (nodeStatsManager.isDataNodeStatsAvailable && !nodeStatsManager.dataNode.isEmpty()
				&& profilerStats.isDatanodeStatsAvailable()) {
			try {
				for (String dfsStat : nodeStatsManager.dataNode.keySet()) {
					if (dfsStat.contains(SPACE)) {
						map.put(InfluxDBConstants.WORKERJMX_DATANODE + dfsStat.replace(SPACE, BACKSLASH_SPACE), profilerStats.getDnStats(dfsStat));
					} else {
						map.put(InfluxDBConstants.WORKERJMX_DATANODE + dfsStat, profilerStats.getDnStats(dfsStat));
					}
					
				}
			} catch (HTFProfilingException e) {
				LOGGER.error(ERROR_WHILE_FETCHING_DATA_NODE_STATS_OF_NODE + nodeStatsManager.nodeIP + SQUARE_BRACKET, e.getMessage());
			}
		}
	}

	private void fetchOsStatsValues(Map<String, String> map, ProfilerStats profilerStats) {
		if (!nodeStatsManager.os.isEmpty() && profilerStats.isMemoryStatsAvailable()) {
			try {
				for (String osResp : nodeStatsManager.os.keySet()) {
					map.put(InfluxDBConstants.SYSTEMSTATS_OS + osResp, profilerStats.getMemoryStats(osResp));
				}
			} catch (HTFProfilingException e) {
				LOGGER.error(ERROR_WHILE_FETCHING_OS_STATS_OF_NODE + nodeStatsManager.nodeIP + SQUARE_BRACKET, e.getMessage());
			}
		}
	}

	private void fetchMemoryStatsValues(Map<String, String> map, ProfilerStats profilerStats) {
		if (!nodeStatsManager.memory.isEmpty() && profilerStats.isMemoryStatsAvailable()) {
			try {
				for (String memStat : nodeStatsManager.memory.keySet()) {
					map.put(InfluxDBConstants.SYSTEMSTATS_MEMORY + memStat, profilerStats.getMemoryStats(memStat));
				}
			} catch (HTFProfilingException e) {
				LOGGER.error(ERROR_WHILE_FETCHING_MEMORY_STATS_OF_NODE + nodeStatsManager.nodeIP + SQUARE_BRACKET, e.getMessage());
			}
		}
	}

	private void fetchCpuStatsValues(Map<String, String> map, ProfilerStats profilerStats)
			throws HTFProfilingException {
		if (!nodeStatsManager.cpu.isEmpty() && profilerStats.isCpuStatsAvailable()) {
			try {
				for (String cpuStat : nodeStatsManager.cpu.keySet()) {
					map.put(InfluxDBConstants.SYSTEMSTATS_CPU + cpuStat, profilerStats.getCpuStats(cpuStat));
				}
			} catch (HTFProfilingException e) {
				LOGGER.error(ERROR_WHILE_FETCHING_CPU_STATS_OF_NODE + nodeStatsManager.nodeIP + SQUARE_BRACKET, e.getMessage());
			}
		}
	}

	public void fetchResourceManagerStatsValues(Map<String, String> map, ProfilerStats profilerStats)
			throws HTFProfilingException {
		if (nodeStatsManager.isResourceManagerStatsAvailable && !nodeStatsManager.resourceManager.isEmpty()
				&& profilerStats.isResourceManagerStatsAvailable()) {
			try {
				for (String resourceMangerStat : nodeStatsManager.resourceManager.keySet()) {
					if (resourceMangerStat.contains(SPACE)) {
						map.put(InfluxDBConstants.RESOURCE_MANAGER + resourceMangerStat.replace(SPACE, BACKSLASH_SPACE),
								profilerStats.getRmStats(resourceMangerStat));
					} else {
						map.put(InfluxDBConstants.RESOURCE_MANAGER + resourceMangerStat,
								profilerStats.getRmStats(resourceMangerStat));
					}
					
				}
			} catch (HTFProfilingException e) {
				LOGGER.error(ERROR_WHILE_FETCHING_RESOURCE_MANAGER_STATS_OF_NODE + nodeStatsManager.nodeIP + SQUARE_BRACKET, e.getMessage());
			}
		}
	}

	private void fetchNamenodeStatsValues(Map<String, String> map, ProfilerStats profilerStats)
			throws HTFProfilingException {
		if (nodeStatsManager.isNameNodestatsAvailable && !nodeStatsManager.nameNode.isEmpty()
				&& profilerStats.isNamenodeStatsAvailable()) {
			try {
				for (String nameNodeStat : nodeStatsManager.nameNode.keySet()) {
					map.put(InfluxDBConstants.NAMENODE + nameNodeStat, profilerStats.getNnStats(nameNodeStat));
				}
			} catch (HTFProfilingException e) {
				LOGGER.error(ERROR_WHILE_FETCHING_NAMENODE_STATS_OF_NODE + nodeStatsManager.nodeIP + SQUARE_BRACKET, e.getMessage());
			}
		}
	}

}
