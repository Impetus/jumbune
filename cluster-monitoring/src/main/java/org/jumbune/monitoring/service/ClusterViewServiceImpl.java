package org.jumbune.monitoring.service;

import static org.jumbune.monitoring.utils.ProfilerConstants.DATANODE;
import static org.jumbune.monitoring.utils.ProfilerConstants.DOT_SEPARATOR;
import static org.jumbune.monitoring.utils.ProfilerConstants.NAMENODE;
import static org.jumbune.monitoring.utils.ProfilerConstants.NODEMANAGER;
import static org.jumbune.monitoring.utils.ProfilerConstants.RESOURCEMANAGER;
import static org.jumbune.monitoring.utils.ProfilerConstants.SUBCAT_CPU;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.ClusterDefinition;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.monitoring.beans.CategoryInfo;
import org.jumbune.monitoring.beans.ClusterInfo;
import org.jumbune.monitoring.beans.ClusterWideInfo;
import org.jumbune.monitoring.beans.ClusterWideResponse;
import org.jumbune.monitoring.beans.DataCenterInfo;
import org.jumbune.monitoring.beans.HadoopJMXResponse;
import org.jumbune.monitoring.beans.JMXDeamons;
import org.jumbune.monitoring.beans.NodeConfig;
import org.jumbune.monitoring.beans.NodeInfo;
import org.jumbune.monitoring.beans.NodePerformance;
import org.jumbune.monitoring.beans.NodeStats;
import org.jumbune.monitoring.beans.PerformanceEval;
import org.jumbune.monitoring.beans.PerformanceStats;
import org.jumbune.monitoring.beans.RackInfo;
import org.jumbune.monitoring.beans.StatCategory;
import org.jumbune.monitoring.beans.StatsResult;
import org.jumbune.monitoring.beans.SystemStats;
import org.jumbune.monitoring.beans.SystemStatsResponse;
import org.jumbune.monitoring.beans.WorkerJMXInfo;
import org.jumbune.monitoring.utils.HTFProfilingException;
import org.jumbune.monitoring.utils.ProfilerConstants;
import org.jumbune.monitoring.utils.ProfilerConstants.HADOOP_JMX_CAT;
import org.jumbune.monitoring.utils.ProfilerConstants.Operator;
import org.jumbune.monitoring.utils.ProfilerJMXDump;
import org.jumbune.monitoring.utils.ProfilerStats;
import org.jumbune.monitoring.utils.ProfilerUtil;
import org.jumbune.monitoring.utils.ViewHelper;
import org.jumbune.monitoring.yarn.beans.YarnClusterWideInfo;
import org.jumbune.monitoring.yarn.beans.YarnWorkerJMXInfo;
import org.jumbune.utils.exception.JumbuneRuntimeException;

/**
 * Service to prepare various cluster view.
 */
public class ClusterViewServiceImpl implements ClusterViewService {
	private static final String DC = "DC/";
	private static final String NODE_UNAVAILABLE = "Node Unavailable";
	private static final String PERCENT = " %";
	private static final String CPU = "CPU : ";
	private static final String MEMORY = "Memory : ";
	private static final String GB = " GB";
	private static final String CPU_USAGE = "CpuUsage";
	private static final String USED_MEMORY = "UsedMemory";
	private static final String JOB_TRACKER = "jobTracker";
	private static final String RESOURCE_MANAGER = "resourceManager";
	private Cluster cluster;
	private static final Logger LOGGER = LogManager.getLogger(ClusterViewServiceImpl.class);
	/** The Constant DEFAULT_RACK_SUFFIX. */
	private static final String DEFAULT_RACK_SUFFIX = "/default-rack/";
	/**
	 * Instantiates a new cluster view service impl.
	 * 
	 * @param yamlLoader
	 *            the yaml loader
	 */
	public ClusterViewServiceImpl(Cluster cluster) {
		this.cluster = cluster;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jumbune.profiling.service.ClusterViewService#getClusterDCView
	 * (YamlConfig config)
	 */
	@Override
	public ClusterInfo getMainView(List<PerformanceStats> genSettings, String viewName)
			throws JumbuneRuntimeException, HTFProfilingException {

		return getClusterInfo(genSettings, viewName);
	}

	private ClusterInfo getClusterInfo(List<PerformanceStats> genSettings, String viewName)
			throws HTFProfilingException {

		ClusterDefinition clusterDefinition = (ClusterDefinition) cluster;
		ViewHelper viewHelper = new ViewHelper();
		ClusterInfo clusterInfo = new ClusterInfo();
		String dcId;
		String rackId;
		String clusterId = null;
		boolean isUnavailable;
		NodeInfo node;
		HashMap<String, DataCenterInfo> dataCenters = new HashMap<String, DataCenterInfo>();
		HashMap<String, RackInfo> racks = new HashMap<String, RackInfo>();
		ProfilerStats profilerStats = new ProfilerStats(cluster);
		Workers workers = cluster.getWorkers();
		String nameNodeHost = cluster.getNameNode();
		List<String> unavailableHosts = new ArrayList<String>();
		String dataNodeInstance = null;
		String nameNodeInstance = null;
		String clusterwideDaemonInstance = null;
		String workerDaemonInstance = null;
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);//mapr code changes
		boolean isMapr = Constants.MAPR.equalsIgnoreCase(hadoopDistribution);//mapr code changes

		//begin mapr code changes
		if(!isMapr){
			nameNodeInstance = RemotingUtil.getDaemonProcessId(cluster, nameNodeHost, JMXDeamons.NAME_NODE.toString()); 
		}
		//end mapr code changes
		
		clusterwideDaemonInstance = RemotingUtil.getDaemonProcessId(cluster,
				cluster.getTaskManagers().getActive(), JMXDeamons.RESOURCE_MANAGER.toString());

		if (!StringUtils.isBlank(nameNodeHost)) {
			// master.setIsNodeAvailable(true);
			//begin mapr code changes
			if ((isMapr && clusterwideDaemonInstance.isEmpty()) || (nameNodeInstance.isEmpty() && clusterwideDaemonInstance.isEmpty()) ) {
					unavailableHosts.add(nameNodeHost);
			}
			//end mapr code changes
		}
		for (String workerHost : workers.getHosts()) {
			if (!StringUtils.isBlank(workerHost)) {
				isUnavailable = false;
				//begin mapr code changes
				if(!isMapr){
					dataNodeInstance = RemotingUtil.getDaemonProcessId(cluster, workerHost, JMXDeamons.DATA_NODE.toString());	
				}
				//end mapr code changes
				workerDaemonInstance = RemotingUtil.getDaemonProcessId(cluster, workerHost,
						JMXDeamons.NODE_MANAGER.toString());

				if (nameNodeHost != workerHost) {
					//begin mapr code changes
				
					if ((isMapr && workerDaemonInstance.isEmpty()) || (dataNodeInstance.isEmpty() && workerDaemonInstance.isEmpty())) { 
						unavailableHosts.add(workerHost);
						isUnavailable = true ;
					}
					
					//end mapr code changes
				} else if (nameNodeHost.equalsIgnoreCase(workerHost)) {
					isUnavailable = checkHadoopDeamonsAlive(unavailableHosts, dataNodeInstance,
							nameNodeInstance, clusterwideDaemonInstance, workerDaemonInstance,
							workerHost, isUnavailable);
				}
				if (!isUnavailable) {
					node = new NodeInfo();
					node.setNodeIp(workerHost);
					profilerStats.setNodeIp(workerHost);
					rackId = RemoteFileUtil.getRackId(workerHost);
					dcId = RemoteFileUtil.getDataCentreId(workerHost);
					if (viewName.equals("PRE_CLUSTER_VIEW")) {
						node.setPerformance(NodePerformance.Average);
					} else {
						try {
							node.setPerformance(getNodePerformance(profilerStats, genSettings));
						} catch (Exception e) {
							LOGGER.error(e.getMessage());
							node.setPerformance(NodePerformance.Unavailable);
						}
					}
					clusterId = DC + dcId;
					viewHelper.bindNodeToRack(node, rackId, racks);
					viewHelper.bindRackToDC(racks.get(rackId), dcId, clusterId, dataCenters);
				}
				clusterDefinition.setUnavailableHosts(unavailableHosts);
			}
		}

		// Case for data center heat map, for nodes other than worker nodes
		if (viewName.contains("CLUSTER_VIEW")) {
			// For namenodes
			List<String> nameNodesHost = new ArrayList<String>();
			nameNodesHost.addAll(cluster.getNameNodes().getHosts());
			nameNodesHost.removeAll(cluster.getWorkers().getHosts());
			String nodeInstance = null;
			for (String host : nameNodesHost) {
				nodeInstance = null;
				if (!StringUtils.isBlank(host)) {
					nodeInstance = RemotingUtil.getDaemonProcessId(cluster, host,
							JMXDeamons.NAME_NODE.toString());
				}
				if (!nodeInstance.isEmpty()) {
					node = new NodeInfo();
					node.setNodeIp(host);
					profilerStats.setNodeIp(host);
					rackId = RemoteFileUtil.getRackId(host);
					dcId = RemoteFileUtil.getDataCentreId(host);
					if (viewName.equals("PRE_CLUSTER_VIEW")) {
						node.setPerformance(NodePerformance.Average);
					} else {
						try {
							node.setPerformance(getNodePerformance(profilerStats, genSettings));
						} catch (Exception e) {
							LOGGER.error(e.getMessage());
							node.setPerformance(NodePerformance.Unavailable);
						}
					}
					clusterId = DC + dcId;
					viewHelper.bindNodeToRack(node, rackId, racks);
					viewHelper.bindRackToDC(racks.get(rackId), dcId, clusterId, dataCenters);
				}
			}

			// For task manager nodes
			List<String> taskManagerHosts = new ArrayList<String>();
			taskManagerHosts.addAll(cluster.getTaskManagers().getHosts());
			taskManagerHosts.removeAll(cluster.getNameNodes().getHosts());
			taskManagerHosts.removeAll(cluster.getWorkers().getHosts());

			for (String host : taskManagerHosts) {
				nodeInstance = null;
				if (!StringUtils.isBlank(host)) {
					nodeInstance = RemotingUtil.getDaemonProcessId(cluster, host,
							JMXDeamons.RESOURCE_MANAGER.toString());
				}
				if (!nodeInstance.isEmpty()) {
					node = new NodeInfo();
					node.setNodeIp(host);
					profilerStats.setNodeIp(host);
					rackId = RemoteFileUtil.getRackId(host);
					dcId = RemoteFileUtil.getDataCentreId(host);
					if (viewName.equals("PRE_CLUSTER_VIEW")) {
						node.setPerformance(NodePerformance.Average);
					} else {
						node.setPerformance(
								getNodePerformanceForTaskManager(profilerStats, genSettings));
					}
					clusterId = DC + dcId;
					viewHelper.bindNodeToRack(node, rackId, racks);
					viewHelper.bindRackToDC(racks.get(rackId), dcId, clusterId, dataCenters);
				}
			}
		}

		List<String> unavailableHostsAll = clusterDefinition.getUnavailableHosts();
		unavailableHosts.addAll(unavailableHostsAll);
		clusterId = processUnavailableHosts(viewHelper, clusterId, dataCenters, racks,
				unavailableHosts);

		clusterInfo.setClusterId(clusterId);
		clusterInfo.setDataCenters(dataCenters.values());

		return clusterInfo;

	}
	
	public ClusterInfo getDataCenterDetails(List<PerformanceStats> genSettings) {
		
		NodeInfo node = null;
		NodePerformance nodePerformance = null;
		ViewHelper viewHelper = new ViewHelper();
		ClusterInfo clusterInfo = new ClusterInfo();
		Set<String> clusterNodes = getClusterNodes();
		ProfilerStats profilerStats = new ProfilerStats(cluster);
		HashMap<String, RackInfo> racks = new HashMap<String, RackInfo>();
		HashMap<String, DataCenterInfo> dataCenters = new HashMap<String, DataCenterInfo>();
		String rackId = null, dcId = null,  clusterId = null, statValue = null, statName = null;
		
		for (String nodeIP : clusterNodes) {
			try {
				nodeIP = convertHostNameToIP(nodeIP);
				profilerStats.setNodeIp(nodeIP);
				
				rackId = RemoteFileUtil.getRackId(nodeIP);
				dcId = RemoteFileUtil.getDataCentreId(nodeIP);
				node = new NodeInfo();
				node.setNodeIp(nodeIP);
				
				for (PerformanceStats settings : genSettings) {
					statName = settings.getStat();
					statValue = calculateStatValue(profilerStats, statName,
							settings.getCategory());
					if (statValue == null || statValue.trim().isEmpty()) {
						throw new Exception("Invalid stat value !");
					}
					if (comparePerformance(statValue, settings.getGood())) {
						nodePerformance = NodePerformance.Good;
						continue;
					} else if (comparePerformance(statValue, settings.getBad())) {
						nodePerformance = NodePerformance.Bad;
						break;
					} else {
						nodePerformance = NodePerformance.Average;
						break;
					}
				}
				node.setPerformance(nodePerformance);
				if (nodePerformance != NodePerformance.Good) {
					if (statName.equals(USED_MEMORY)) {
						node.setMessage(MEMORY + (Long.parseLong(statValue) / 1073741824) + GB);
					} else if (statName.equals(CPU_USAGE)) {
						node.setMessage(CPU + statValue + PERCENT);
					} 
					
				}
				
				
			//	return nodePerformance;
			} catch (Exception e) {
				node.setPerformance(NodePerformance.Unavailable);
				node.setMessage(NODE_UNAVAILABLE);
			}
			clusterId = DC + dcId;
			viewHelper.bindNodeToRack(node, rackId, racks);
			viewHelper.bindRackToDC(racks.get(rackId), dcId, clusterId, dataCenters);
		}
		clusterId = processUnavailableHosts(viewHelper, clusterId, dataCenters, racks,
				new ArrayList<String>());
		clusterInfo.setClusterId(clusterId);
		clusterInfo.setDataCenters(dataCenters.values());
		return clusterInfo;
	}
	
	public Set<String> getClusterNodes() {
		Set<String> clusterNodeSet = new HashSet<String>();
		clusterNodeSet.addAll(cluster.getWorkers().getHosts());
		clusterNodeSet.addAll(cluster.getNameNodes().getHosts());
		clusterNodeSet.addAll(cluster.getTaskManagers().getHosts());
		return clusterNodeSet;
	}
	
	
	public List<NodeInfo> getDataLoadAndDistributionDetails() {
		String[] dataLoadResult = ProfilerUtil.getDFSAdminReportCommandResult(cluster);
		List<String> workersHosts = cluster.getWorkers().getHosts();
		List<NodeInfo> list = new ArrayList<NodeInfo>(workersHosts.size());
		
		NodeInfo node;
		ProfilerJMXDump profilerJMXDump = new ProfilerJMXDump();
		double statValue;
		for (String workerHost : workersHosts) {
			node = new NodeInfo();
			node.setNodeIp(workerHost);
			try {
				statValue = profilerJMXDump.getDataLoadonNodes(workerHost, node, cluster, dataLoadResult);
				node.setDataLoadStats(String.valueOf(statValue));
			} catch (Exception e) {
				node.setDataLoadStats("0");
				node.setPerformance(NodePerformance.Unavailable);
			}
			list.add(node);
		}
		return list;
	}

	private boolean checkHadoopDeamonsAlive(List<String> unavailableHosts, String dataNodeInstance,
			String nameNodeInstance, String clusterwideDaemonInstance, String workerDaemonInstance,
			String workerHost, boolean isUnavailable) {
		boolean isUnavailableVal = isUnavailable;
		//begin mapr code changes
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		if(Constants.MAPR.equalsIgnoreCase(hadoopDistribution)){
			if (clusterwideDaemonInstance.isEmpty() && workerDaemonInstance.isEmpty()) {
				unavailableHosts.add(workerHost);
				isUnavailableVal = true;
			}
		} else {
			if (nameNodeInstance.isEmpty() && clusterwideDaemonInstance.isEmpty()
					&& dataNodeInstance.isEmpty() && workerDaemonInstance.isEmpty()) {
				unavailableHosts.add(workerHost);
				isUnavailableVal = true;
			}
		}
		//end mapr code changes
		return isUnavailableVal;
	}

	/**
	 * This method processes the unavailable hosts.
	 * 
	 * @param viewHelper
	 *            the view helper
	 * @param clusterId
	 *            the cluster id
	 * @param dataCenters
	 *            the data centers
	 * @param racks
	 *            the racks
	 * @param unavailableHosts
	 *            the unavailable hosts
	 * @return the string
	 */
	private String processUnavailableHosts(ViewHelper viewHelper, String clusterId,
			Map<String, DataCenterInfo> dataCenters, Map<String, RackInfo> racks,
			List<String> unavailableHosts) {
		String dcId;
		String rackId;
		String clusterIdTmp = clusterId;
		if (unavailableHosts != null) {
			for (String unavailableHost : unavailableHosts) {
				if (!StringUtils.isBlank(unavailableHost)) {
					NodeInfo node = new NodeInfo();
					node.setNodeIp(unavailableHost);
					node.setPerformance(NodePerformance.Unavailable);

					rackId = RemoteFileUtil.getRackId(unavailableHost);
					dcId = RemoteFileUtil.getDataCentreId(unavailableHost);

					clusterIdTmp = DC + dcId;
					viewHelper.bindNodeToRack(node, rackId, racks);
					viewHelper.bindRackToDC(racks.get(rackId), dcId, clusterIdTmp, dataCenters);
				}
			}
		}
		return clusterIdTmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jumbune.profiling.service.ClusterViewService#getClusterDCNodeView
	 * (java.lang.String)
	 */
	@Override
	public NodeStats getNodeView(NodeConfig nodeConfig, List<PerformanceStats> clrSettings)
			throws HTFProfilingException {
		String nodeIp = nodeConfig.getNodeIp();
		ProfilerStats profilerStats = new ProfilerStats(cluster, nodeIp);
		CategoryInfo favourities = nodeConfig.getFavourities();
		CategoryInfo trends = nodeConfig.getTrends();
		NodeStats nodeStats = null;

		if (favourities != null) {
			StatsResult fav = calculateStats(favourities, profilerStats);

			nodeStats = new NodeStats(nodeIp);
			nodeStats.setFavourities(fav);
		}
		if (trends != null) {
			StatsResult trnds = calculateStats(trends, profilerStats);
			if (nodeStats == null) {
				nodeStats = new NodeStats(nodeIp);
			}
			nodeStats.setTrends(trnds);
		}
		if (clrSettings != null) {
			Map<String, NodePerformance> favColorPerf = new HashMap<String, NodePerformance>();
			for (PerformanceStats settings : clrSettings) {

				nodeStats.setColorConfig(getFavPerformance(profilerStats, settings, favColorPerf));
			}
		}
		return nodeStats;
	}

	/**
	 * Calculate profiling stats (favourities and trends selected by user) for a
	 * given node
	 * 
	 * @param info
	 * @param profilerStats
	 * @return
	 * @throws HTFProfilingException
	 */
	private StatsResult calculateStats(CategoryInfo info, ProfilerStats profilerStats)
			throws HTFProfilingException {
		StatsResult stats = new StatsResult();

		// check and calculate stats for various categories
		calculateClusterWideStats(info.getClusterWide(), profilerStats, stats);

		calculateHadoopJMXStats(info.getWorkerJMXInfo(), profilerStats, stats);

		calculateSystemStats(info.getSystemStats(), profilerStats, stats);

		return stats;
	}

	/**
	 * Checks and calculates Worker Hadoop JMX stats
	 * 
	 * @param workerJmxStats
	 * @param profilerStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateHadoopJMXStats(WorkerJMXInfo workerJmxInfo, ProfilerStats profilerStats,
			StatsResult stats) throws HTFProfilingException {
		if (workerJmxInfo != null) {
			HadoopJMXResponse hadoopJMXResp = new HadoopJMXResponse();
			calculateNodeManagerStats(workerJmxInfo, profilerStats, hadoopJMXResp);
			calculateDataNodeStats(workerJmxInfo.getDataNode(), profilerStats, hadoopJMXResp);
			stats.setHadoopJMX(hadoopJMXResp);
			LOGGER.debug("Worker hadoop JMX stats response [" + hadoopJMXResp + "]");
		}

	}

	private void calculateNodeManagerStats(WorkerJMXInfo workerJmxInfo, ProfilerStats profilerStats,
			HadoopJMXResponse hadoopJMXResp) throws HTFProfilingException {
		YarnWorkerJMXInfo yarnWorker = (YarnWorkerJMXInfo) workerJmxInfo;
		Set<String> statList = yarnWorker.getNodeManager();
		if (statList != null && profilerStats.isNodeManagerStatsAvailable()) {
			Map<String, String> dfsResp = new HashMap<String, String>();
			for (String dfsStat : statList) {
				dfsResp.put(dfsStat, profilerStats.getNmStats(dfsStat));
			}
			hadoopJMXResp.setNodeManager(dfsResp);
		}

	}

	/**
	 * Checks and calculates System stats
	 * 
	 * @param hadoopJMX
	 * @param profilerStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateSystemStats(SystemStats systemStats, ProfilerStats profilerStats,
			StatsResult stats) throws HTFProfilingException {
		if (systemStats != null) {
			SystemStatsResponse systemStatsResp = new SystemStatsResponse();
			if (profilerStats.isCpuStatsAvailable()) {
				calculateCPUStats(systemStats.getCpu(), profilerStats, systemStatsResp);
			}
			if (profilerStats.isMemoryStatsAvailable()) {
				calculateMemoryStats(systemStats.getMemory(), profilerStats, systemStatsResp);
				calculateOsStats(systemStats.getOs(), profilerStats, systemStatsResp);
			}
			stats.setSystemStats(systemStatsResp);
			LOGGER.debug("System level stats response [" + systemStats + "]");
		}
	}

	/**
	 * Checks and calculates profiling stats for DataNode category
	 * 
	 * @param statList
	 * @param profilerStats
	 * @param hadoopJMXResp
	 * @throws HTFProfilingException
	 */
	private void calculateDataNodeStats(List<String> statList, ProfilerStats profilerStats,
			HadoopJMXResponse hadoopJMXResp) throws HTFProfilingException {
		if (statList != null && profilerStats.isDatanodeStatsAvailable()) {
			Map<String, String> dfsResp = new HashMap<String, String>();
			for (String dfsStat : statList) {
				dfsResp.put(dfsStat, profilerStats.getDnStats(dfsStat));
			}
			hadoopJMXResp.setDataNode(dfsResp);
		}
	}

	/**
	 * Checks and calculates profiling stats for CPU category
	 * 
	 * @param statList
	 * @param profilerStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateCPUStats(List<String> statList, ProfilerStats profilerStats,
			SystemStatsResponse systemStatsResp) throws HTFProfilingException {
		if (statList != null) {
			Map<String, String> cpuResp = new HashMap<String, String>();
			for (String cpuStat : statList) {
				cpuResp.put(cpuStat, profilerStats.getCpuStats(cpuStat));
			}
			systemStatsResp.setCpu(cpuResp);
		}
	}

	/**
	 * Checks and calculates profiling stats for Memory category
	 * 
	 * @param statList
	 * @param profilerStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateMemoryStats(List<String> statList, ProfilerStats profilerStats,
			SystemStatsResponse systemStatsResp) throws HTFProfilingException {
		if (statList != null) {
			Map<String, String> memResp = new HashMap<String, String>();
			for (String memStat : statList) {
				memResp.put(memStat, profilerStats.getMemoryStats(memStat));
			}
			systemStatsResp.setMemory(memResp);
		}
	}

	/**
	 * Checks and calculates profiling stats for OS category
	 * 
	 * @param statList
	 * @param profilerStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateOsStats(List<String> statList, ProfilerStats profilerStats,
			SystemStatsResponse systemStatsResp) throws HTFProfilingException {
		if (statList != null) {
			Map<String, String> osResp = new HashMap<String, String>();
			for (String osStat : statList) {
				osResp.put(osStat, profilerStats.getMemoryStats(osStat));
			}
			systemStatsResp.setOs(osResp);
		}
	}

	/**
	 * Checks and calculates profiling stats for Cluster Wide category
	 * 
	 * @param clusterWide
	 * @param profilerStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateClusterWideStats(ClusterWideInfo clusterWide, ProfilerStats profilerStats,
			StatsResult stats) throws HTFProfilingException {

		if (clusterWide != null) {
			ClusterWideResponse clusterWideResp = new ClusterWideResponse();
			calculateYarnClusterWideInfo(profilerStats, clusterWide, clusterWideResp);
			if (clusterWide.getNameNode() != null) {
				Map<String, String> nameNodeResp = new HashMap<String, String>();
				for (String nameNodeStat : clusterWide.getNameNode()) {
					nameNodeResp.put(nameNodeStat, profilerStats.getNnStats(nameNodeStat));
				}
				clusterWideResp.setNameNode(nameNodeResp);
			}
			stats.setClusterWide(clusterWideResp);
			LOGGER.debug("Calculated cluster wide stats");
		}
	}

	private void calculateYarnClusterWideInfo(ProfilerStats profilerStats,
			ClusterWideInfo clusterWide, ClusterWideResponse clusterWideResp)
					throws HTFProfilingException {
		YarnClusterWideInfo yarnCluster = (YarnClusterWideInfo) clusterWide;
		if (yarnCluster.getResourceManager() != null
				&& profilerStats.isResourceManagerStatsAvailable()) {
			Map<String, String> resourceManagerResp = new HashMap<String, String>();
			for (String resourceMangerStat : yarnCluster.getResourceManager()) {
				resourceManagerResp.put(resourceMangerStat,
						profilerStats.getRmStats(resourceMangerStat));
			}
			clusterWideResp.setResourceManager(resourceManagerResp);
		}
	}

	/**
	 * Gets the node performance on the basis of criteria defined by the user
	 * 
	 * @param profilerStats
	 * @param genSettings
	 * @return
	 * @throws HTFProfilingException
	 */
	private NodePerformance getNodePerformance(ProfilerStats profilerStats,
			List<PerformanceStats> genSettings) throws HTFProfilingException {

		String statValue = null;
		List<NodePerformance> performances = new ArrayList<NodePerformance>();
		for (PerformanceStats settings : genSettings) {
			
			statValue = calculateStatValue(profilerStats, settings.getStat(),
					settings.getCategory());
			if (statValue == null || statValue.trim().isEmpty()) {
				throw new HTFProfilingException("Invalid stat value !");
			}
			if (comparePerformance(statValue, settings.getGood())) {
				performances.add(NodePerformance.Good);
			} else if (comparePerformance(statValue, settings.getBad())) {
				performances.add(NodePerformance.Bad);
			} else {
				performances.add(NodePerformance.Average);
			}

		}
		return calculatePerformance(performances);
	}

	private NodePerformance getNodePerformanceForTaskManager(ProfilerStats profilerStats,
			List<PerformanceStats> genSettings) throws HTFProfilingException {

		String catName;
		String statValue = null;
		List<NodePerformance> performances = new ArrayList<NodePerformance>();
		for (PerformanceStats settings : genSettings) {

			catName = settings.getCategory();

			if (catName.contains(RESOURCE_MANAGER) || catName.contains(JOB_TRACKER)) {

				statValue = calculateStatValue(profilerStats, settings.getStat(), catName);

				if (statValue == null || statValue.trim().isEmpty()) {
					throw new HTFProfilingException("Invalid stat value !");
				}
				if (comparePerformance(statValue, settings.getGood())) {
					performances.add(NodePerformance.Good);
				} else if (comparePerformance(statValue, settings.getBad())) {
					performances.add(NodePerformance.Bad);
				} else {
					performances.add(NodePerformance.Average);
				}
			}
		}
		return calculatePerformance(performances);
	}

	/**
	 * This api gets the favourite performance.
	 * 
	 * @param profilerStats
	 *            the pf stats
	 * @param settings
	 *            the settings
	 * @param favColorPerf
	 *            the fav color perf
	 * @return the fav performance
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	private Map<String, NodePerformance> getFavPerformance(ProfilerStats profilerStats,
			PerformanceStats settings, Map<String, NodePerformance> favColorPerf)
					throws HTFProfilingException {

		String statValue = null;
		PerformanceEval good = settings.getGood();
		PerformanceEval bad = settings.getBad();
		String catName = settings.getCategory();
		String stat = settings.getStat();
		statValue = calculateStatValue(profilerStats, stat, catName);
		if (comparePerformance(statValue, good)) {
			favColorPerf.put(stat, NodePerformance.Good);
		} else if (comparePerformance(statValue, bad)) {
			favColorPerf.put(stat, NodePerformance.Bad);
		} else {
			favColorPerf.put(stat, NodePerformance.Average);
		}
		return favColorPerf;
	}

	/**
	 * This method calculates the stats value.
	 * 
	 * @param profilerStats
	 *            the pf stats
	 * @param stat
	 *            the stat
	 * @param catName
	 *            the cat name
	 * @return the string
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	private String calculateStatValue(ProfilerStats profilerStats, String stat, String catName)
			throws HTFProfilingException {
		String statValue = null;
		String[] catArray = null;
		String subCat = null;
		String categoryName;

		if (catName.contains(DOT_SEPARATOR)) {
			catArray = catName.split("\\.");
			categoryName = catArray[0];
			subCat = catArray[1];
		} else {
			categoryName = catName;
		}
		StatCategory category = StatCategory.valueOf(categoryName);
		if (category == null) {
			throw new HTFProfilingException("Invalid Category");
		}
		statValue = processStatValueByCategory(profilerStats, stat, catName, statValue, subCat,
				category);
		return statValue;
	}

	/**
	 * This api processes the stats value by category.
	 * 
	 * @param profilerStats
	 *            the pf stats
	 * @param stat
	 *            the stat
	 * @param catName
	 *            the cat name
	 * @param statValue
	 *            the stat value
	 * @param subCat
	 *            the sub cat
	 * @param category
	 *            the category
	 * @return the string
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	private String processStatValueByCategory(ProfilerStats profilerStats, String stat,
			String catName, String statValue, String subCat, StatCategory category)
					throws HTFProfilingException {

		String statValueTmp = statValue;
		switch (category) {
		case jumbuneInferences:
			statValueTmp = profilerStats.getJumbuneContextStats(stat);
			break;
		case clusterWide:
			if (NAMENODE.equalsIgnoreCase(subCat)) {
				statValueTmp = profilerStats.getNnStats(stat);
			} else if (RESOURCEMANAGER.equalsIgnoreCase(subCat)) {
				statValueTmp = profilerStats.getRmStats(stat);
			} else {
				statValueTmp = profilerStats.getClusterWideStats(stat);
			}
			break;
		case hadoopJMX:
			statValueTmp = getHadoopJMXStats(stat, catName, profilerStats);
			break;
		case systemStats:
			if (SUBCAT_CPU.equals(subCat)) {
				statValueTmp = profilerStats.getCpuStats(stat);
			} else {
				statValueTmp = profilerStats.getMemoryStats(stat);
			}
			break;
		case workerJMXInfo:
			if (ProfilerConstants.TASKTRACKER.equalsIgnoreCase(subCat)) {
				statValueTmp = profilerStats.getTtStats(stat);
			} else if (NODEMANAGER.equalsIgnoreCase(subCat)) {
				statValueTmp = profilerStats.getNmStats(stat);
			} else {
				statValueTmp = profilerStats.getDnStats(stat);
			}
		}
		return statValueTmp;
	}

	/**
	 * Gets Hadoop JMX stats
	 * 
	 * @param stat
	 * @param subCat
	 * @param profilerStats
	 * @return
	 * @throws HTFProfilingException
	 */
	public String getHadoopJMXStats(String stat, String catName, ProfilerStats profilerStats)
			throws HTFProfilingException {
		String[] catArray = catName.split("\\.");
		String subCat = catArray[1];
		String statValue = null;
		HADOOP_JMX_CAT hadoopJmxCat = HADOOP_JMX_CAT.valueOf(subCat);
		switch (hadoopJmxCat) {
		case dfs:
			statValue = profilerStats.getDnStats(stat);
			break;
		case rpc:
			if (DATANODE.equalsIgnoreCase(catArray[2])) {
				statValue = profilerStats.getDnStats(stat);
			} else {
				statValue = profilerStats.getTtStats(stat);
			}
			break;
		case io:
			statValue = profilerStats.getDnStats(stat);
			break;
		case dataNodeMisc:
			statValue = profilerStats.getDnStats(stat);
			break;
		case ttMisc:
			statValue = profilerStats.getTtStats(stat);
			break;
		}
		return statValue;
	}

	/**
	 * Compare the performance of the node on the basis of good or bad criteria
	 * specified by the user statValue is the actual value of the profilng stat,
	 * perfStat is the value criteria defined by user
	 * 
	 * @param statValue
	 * @param perfStat
	 * @return
	 */
	private boolean comparePerformance(String statValue, PerformanceEval perfStat) {
		double perfval = Double.parseDouble(perfStat.getVal());
		double statVal = Double.parseDouble(statValue);
		Operator operator = Operator.valueOf(perfStat.getOperator());
		boolean eval = false;
		switch (operator) {
		case LESS_THAN_OP:
			eval = processSmallStatVal(perfval, statVal, eval);
			break;
		case LESS_THAN_EQUALTO_OP:
			if (statVal <= perfval) {
				eval = true;
			}
			break;
		case EQUALT0_OP:
			if (statVal == perfval) {
				eval = true;
			}
			break;
		case GREATER_THAN_OP:
			if (statVal > perfval) {
				eval = true;
			}
			break;
		case GREATER_THAN_EQUALTO_OP:
			if (statVal >= perfval) {
				eval = true;
			}
			break;
		}
		return eval;
	}

	private boolean processSmallStatVal(double perfval, double statVal, boolean eval) {
		boolean evalTmp = eval;
		if (statVal < perfval) {
			evalTmp = true;
		}
		return evalTmp;
	}

	/**
	 * Calculate the average performance for the node
	 * 
	 * @param performances
	 * @return
	 */
	private NodePerformance calculatePerformance(List<NodePerformance> performances) {
		int perfSum = 0;
		boolean isBad = false;
		NodePerformance nodePerformance;
		for (NodePerformance perf : performances) {
			if (NodePerformance.Good.equals(perf)) {
				perfSum += 1;
			} else if (NodePerformance.Bad.equals(perf)) {
				isBad = true;
			}
		}
		if (isBad) {
			nodePerformance = NodePerformance.Bad;
		} else if (perfSum == performances.size()) {
			nodePerformance = NodePerformance.Good;
		} else {
			nodePerformance = NodePerformance.Average;
		}
		return nodePerformance;
	}

	/**
	 * Replaces defualt rack suffix and converts host name to node IP.
	 *
	 * @param hostName
	 *            the host name
	 * @return the string
	 * @throws UnknownHostException
	 *             the unknown host exception
	 */
	private String convertHostNameToIP(final String hostName)
			throws UnknownHostException {
		String hostNameTemp = hostName;
		hostNameTemp = hostNameTemp.replace(DEFAULT_RACK_SUFFIX, "");
		return RemotingUtil.getIPfromHostName(cluster, hostNameTemp);
	}


}
