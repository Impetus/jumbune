/**
 * 
 */
package org.jumbune.profiling.service;

import static org.jumbune.profiling.utils.ProfilerConstants.DATANODE;
import static org.jumbune.profiling.utils.ProfilerConstants.DOT_SEPARATOR;
import static org.jumbune.profiling.utils.ProfilerConstants.JOBTRACKER;
import static org.jumbune.profiling.utils.ProfilerConstants.NAMENODE;
import static org.jumbune.profiling.utils.ProfilerConstants.SUBCAT_CPU;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Master;
import org.jumbune.common.beans.Slave;
import org.jumbune.common.beans.SlaveParam;
import org.jumbune.common.beans.SupportedApacheHadoopVersions;
import org.jumbune.common.beans.UnavailableHost;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.profiling.beans.CategoryInfo;
import org.jumbune.profiling.beans.ClusterInfo;
import org.jumbune.profiling.beans.ClusterWideInfo;
import org.jumbune.profiling.beans.ClusterWideResponse;
import org.jumbune.profiling.beans.DataCenterInfo;
import org.jumbune.profiling.beans.HadoopJMXResponse;
import org.jumbune.profiling.beans.NodeConfig;
import org.jumbune.profiling.beans.NodeInfo;
import org.jumbune.profiling.beans.NodeStats;
import org.jumbune.profiling.beans.PerformanceEval;
import org.jumbune.profiling.beans.PerformanceStats;
import org.jumbune.profiling.beans.RackInfo;
import org.jumbune.profiling.beans.StatCategory;
import org.jumbune.profiling.beans.StatsResult;
import org.jumbune.profiling.beans.SystemStats;
import org.jumbune.profiling.beans.SystemStatsResponse;
import org.jumbune.profiling.beans.WorkerJMXInfo;
import org.jumbune.profiling.hprof.NodePerformance;
import org.jumbune.profiling.utils.DataDistributionStats;
import org.jumbune.profiling.utils.HTFProfilingException;
import org.jumbune.profiling.utils.JMXConnectorInstance;
import org.jumbune.profiling.utils.ProfilerConstants;
import org.jumbune.profiling.utils.ProfilerStats;
import org.jumbune.profiling.utils.ViewHelper;
import org.jumbune.profiling.utils.ProfilerConstants.HADOOP_JMX_CAT;
import org.jumbune.profiling.utils.ProfilerConstants.Operator;

;

/**
 * Service to prepare various cluster view.
 */
public class ClusterViewServiceImpl implements ProfilingViewService {
	private YamlLoader yamlLoader = null;
	private SupportedApacheHadoopVersions hadoopVersions = null;

	private static final Logger LOGGER = LogManager
			.getLogger(ClusterViewServiceImpl.class);

	/**
	 * Instantiates a new cluster view service impl.
	 * 
	 * @param yamlLoader
	 *            the yaml loader
	 */
	public ClusterViewServiceImpl(YamlLoader yamlLoader) {
		this.yamlLoader = yamlLoader;
		this.hadoopVersions = RemotingUtil.getHadoopVersion(yamlLoader.getYamlConfiguration());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jumbune.profiling.service.ClusterViewService#getClusterDCView
	 * (YamlConfig config)
	 */
	@Override
	public ClusterInfo getMainView(List<PerformanceStats> genSettings,
			String viewName) throws HTFProfilingException {
		YamlConfig config = yamlLoader.getYamlConfiguration();
		ViewHelper viewHelper = new ViewHelper();
		ClusterInfo clusterInfo = new ClusterInfo();
		DataDistributionStats dataDistributionStats = null;
		String dcId;
		String rackId;
		String clusterId = null;
		HashMap<String, DataCenterInfo> dataCenters = new HashMap<String, DataCenterInfo>();
		HashMap<String, RackInfo> racks = new HashMap<String, RackInfo>();
		ProfilerStats pfStats = new ProfilerStats(config);
		SlaveParam slaveParam = config.getSlaveParam();
		Master master = config.getMaster();
		String masterIp = master.getHost();
		String dataNodePort = slaveParam.getDataNodeJmxPort();
		String taskTrackerPort = slaveParam.getTaskTrackerJmxPort();
		String nameNodePort = master.getNameNodeJmxPort();
		String jobTrackerPort = master.getJobTrackerJmxPort();
		Set<UnavailableHost> unavailableHosts = new HashSet<UnavailableHost>();
		JMXConnector dataNodeInstance = null;
		JMXConnector nameNodeInstance = null;
		JMXConnector jobTrackerInstance = null;
		JMXConnector taskTrackerInstance = null;
		UnavailableHost unavailableHost;

		if (viewName.equals("DATA_DISTRIBUTION_VIEW")) {
			dataDistributionStats = new DataDistributionStats(yamlLoader);
			try {
				dataDistributionStats.calculateDistributedDataList(yamlLoader,
						clusterInfo);
			} catch (Exception e) {
				throw new HTFProfilingException(
						"File operation can not be performed ", e);
			}
		}
		try {
			nameNodeInstance = getJMXConnectorInstance(masterIp, nameNodePort);
		} catch (IOException e) {
		}
		try {
			jobTrackerInstance = getJMXConnectorInstance(masterIp,
					jobTrackerPort);
		} catch (IOException ioExceptione) {

		}
		if (!StringUtils.isBlank(masterIp)) {
			master.setIsAvailable(true);
			if (nameNodeInstance == null && jobTrackerInstance == null) {
				master.setIsAvailable(false);
				unavailableHost = new UnavailableHost();
				unavailableHost.setNodeIp(masterIp);
				unavailableHost.setMessage("Hadoop Daemons are down");
				unavailableHosts.add(unavailableHost);
			}
		}
	
		for (Slave slave : config.getSlaves()) {
			for (String slaveIp : slave.getHosts()) {
				boolean isUnavailable = false;
				if (!StringUtils.isBlank(slaveIp)) {
					try {
						dataNodeInstance = getJMXConnectorInstance(slaveIp,
								dataNodePort);
					} catch (IOException e) {
					}
					try {
						taskTrackerInstance = getJMXConnectorInstance(slaveIp,
								taskTrackerPort);
					} catch (IOException e) {
					}
					if (masterIp != slaveIp) {
						if (dataNodeInstance == null
								&& taskTrackerInstance == null) {
							unavailableHost = new UnavailableHost();
							unavailableHost.setNodeIp(slaveIp);
							unavailableHost
									.setMessage("Hadoop Daemons are down");
							unavailableHosts.add(unavailableHost);
							isUnavailable = true ;
						}
					} else if (masterIp.equalsIgnoreCase(slaveIp)) {
						isUnavailable = checkHadoopDeamonsAlive(
								unavailableHosts, dataNodeInstance,
								nameNodeInstance, jobTrackerInstance,
								taskTrackerInstance, slaveIp, isUnavailable);
					}

					if (!isUnavailable) {
						NodeInfo node = new NodeInfo();
						node.setNodeIp(slaveIp);
						pfStats.setNodeIp(slaveIp);
						rackId = RemoteFileUtil.getRackId(slaveIp);
						dcId = RemoteFileUtil.getDataCentreId(slaveIp);
						node.setPerformance(getNodePerformance(pfStats,
								genSettings));
						if (viewName.equals("DATALOAD_VIEW")) {
							node.setDataLoadStats(pfStats
									.getDataLoadPartitionStats(slaveIp, node,
											yamlLoader));
						}
						if (viewName.equals("DATA_DISTRIBUTION_VIEW")) {
							if (dataDistributionStats.getNodeWeight()
									.containsKey(node.getNodeIp())) {
								node.setPerformance(NodePerformance.Good);
							} else {
								node.setPerformance(NodePerformance.Unavailable);
							}
						}

						clusterId = "DC/" + dcId;
						viewHelper.bindNodeToRack(node, rackId, racks);
						viewHelper.bindRackToDC(racks.get(rackId), dcId,
								clusterId, dataCenters);
					}
				}
				UnavailableHost[] unAvailableHostsArray = new UnavailableHost[unavailableHosts.size()];
				unavailableHosts.toArray(unAvailableHostsArray);
				slave.setUnavailableHosts(Arrays.asList(unAvailableHostsArray));
				
				//terminating JMX instances for slave after use
				if(taskTrackerInstance!=null){
					terminateJMXConnection(taskTrackerInstance);
				}
				if(dataNodeInstance!=null){
					terminateJMXConnection(dataNodeInstance);
				}
			}
			List<UnavailableHost> unavailableHostsAll = slave
					.getUnavailableHosts();
			unavailableHosts.addAll(unavailableHostsAll);
			clusterId = processUnavailableHosts(viewHelper, clusterId,
					dataCenters, racks, unavailableHosts);	
			
		}
		clusterInfo.setClusterId(clusterId);
		clusterInfo.setDataCenters(dataCenters.values());
		
		//terminating JMX instances for master after use
		if(nameNodeInstance!=null){
			terminateJMXConnection(nameNodeInstance);
		}
		if(jobTrackerInstance!=null){
			terminateJMXConnection(jobTrackerInstance);
		}
		
		return clusterInfo;
	}

	private boolean checkHadoopDeamonsAlive(
			Set<UnavailableHost> unavailableHosts,
			JMXConnector dataNodeInstance, JMXConnector nameNodeInstance,
			JMXConnector jobTrackerInstance, JMXConnector taskTrackerInstance,
			String slaveIp, boolean isUnavailable) {
		UnavailableHost unavailableHost;
		boolean isUnavailableVal = isUnavailable;
		if (nameNodeInstance == null
				&& jobTrackerInstance == null
				&& dataNodeInstance == null
				&& taskTrackerInstance == null) {
			unavailableHost = new UnavailableHost();
			unavailableHost.setNodeIp(slaveIp);
			unavailableHost
					.setMessage("Hadoop Daemons are down");
			unavailableHosts.add(unavailableHost);
			isUnavailableVal = true ;
		}
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
	private String processUnavailableHosts(ViewHelper viewHelper,
			String clusterId, Map<String, DataCenterInfo> dataCenters,
			Map<String, RackInfo> racks, Set<UnavailableHost> unavailableHosts) {
		String dcId;
		String rackId;
		String slaveIp;
		String clusterIdTmp = clusterId;
		if (unavailableHosts != null) {
			for (UnavailableHost unavailableHost : unavailableHosts) {
				slaveIp = unavailableHost.getNodeIp();
				if (!StringUtils.isBlank(slaveIp)) {
					NodeInfo node = new NodeInfo();
					node.setNodeIp(slaveIp);
					node.setPerformance(NodePerformance.Unavailable);
					node.setMessage(unavailableHost.getMessage());

					rackId = RemoteFileUtil.getRackId(slaveIp);
					dcId = RemoteFileUtil.getDataCentreId(slaveIp);

					clusterIdTmp = "DC/" + dcId;
					viewHelper.bindNodeToRack(node, rackId, racks);
					viewHelper.bindRackToDC(racks.get(rackId), dcId,
							clusterIdTmp, dataCenters);
				}
			}
		}
		return clusterIdTmp;
	}

	private JMXConnector getJMXConnectorInstance(String masterNode,
			String dataNodePort) throws IOException {

		JMXServiceURL url = new JMXServiceURL(ProfilerConstants.JMX_URL_PREFIX
				+ masterNode + ":" + dataNodePort
				+ ProfilerConstants.JMX_URL_POSTFIX);
		return JMXConnectorFactory.connect(url, null);

	}
	
	private void terminateJMXConnection(JMXConnector instance){

		JMXConnectorInstance.closeJMXConnection(instance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jumbune.profiling.service.ClusterViewService#getClusterDCNodeView
	 * (java.lang.String)
	 */
	@Override
	public NodeStats getNodeView(NodeConfig nodeConfig,
			List<PerformanceStats> clrSettings) throws HTFProfilingException {
		YamlConfig config = yamlLoader.getYamlConfiguration();
		String nodeIp = nodeConfig.getNodeIp();
		ProfilerStats pfStats = new ProfilerStats(config, nodeIp,
				hadoopVersions);
		CategoryInfo favourities = nodeConfig.getFavourities();
		CategoryInfo trends = nodeConfig.getTrends();
		NodeStats nodeStats = null;

		if (favourities != null) {
			StatsResult fav = calculateStats(favourities, pfStats);

			nodeStats = new NodeStats(nodeIp);
			nodeStats.setFavourities(fav);
		}
		if (trends != null) {
			StatsResult trnds = calculateStats(trends, pfStats);
			if (nodeStats == null) {
				nodeStats = new NodeStats(nodeIp);
			}
			nodeStats.setTrends(trnds);
		}
		if (clrSettings != null) {
			Map<String, NodePerformance> favColorPerf = new HashMap<String, NodePerformance>();
			for (PerformanceStats settings : clrSettings) {

				nodeStats.setColorConfig(getFavPerformance(pfStats, settings,
						favColorPerf));
			}
		}
		return nodeStats;
	}

	/**
	 * Calculate profiling stats (favourities and trends selected by user) for a
	 * given node
	 * 
	 * @param info
	 * @param pfStats
	 * @return
	 * @throws HTFProfilingException
	 */
	private StatsResult calculateStats(CategoryInfo info, ProfilerStats pfStats)
			throws HTFProfilingException {
		StatsResult stats = new StatsResult();

		// check and calculate stats for various categories
		calculateClusterWideStats(info.getClusterWide(), pfStats, stats);

		calculateHadoopJMXStats(info.getWorkerJMXInfo(), pfStats, stats);

		calculateSystemStats(info.getSystemStats(), pfStats, stats);

		return stats;
	}

	/**
	 * Checks and calculates Worker Hadoop JMX stats
	 * 
	 * @param workerJmxStats
	 * @param pfStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateHadoopJMXStats(WorkerJMXInfo workerJmxInfo,
			ProfilerStats pfStats, StatsResult stats)
			throws HTFProfilingException {
		if (workerJmxInfo != null) {
			HadoopJMXResponse hadoopJMXResp = new HadoopJMXResponse();
			calculateDataNodeStats(workerJmxInfo.getDataNode(), pfStats,
					hadoopJMXResp);
			calculateTaskTrackerStats(workerJmxInfo.getTaskTracker(), pfStats,
					hadoopJMXResp);
			stats.setHadoopJMX(hadoopJMXResp);
			LOGGER.debug("Worker hadoop JMX stats response [" + hadoopJMXResp
					+ "]");
		}

	}

	/**
	 * Checks and calculates System stats
	 * 
	 * @param hadoopJMX
	 * @param pfStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateSystemStats(SystemStats systemStats,
			ProfilerStats pfStats, StatsResult stats)
			throws HTFProfilingException {
		if (systemStats != null) {
			SystemStatsResponse systemStatsResp = new SystemStatsResponse();
			calculateCPUStats(systemStats.getCpu(), pfStats, systemStatsResp);
			calculateMemoryStats(systemStats.getMemory(), pfStats,
					systemStatsResp);
			calculateOsStats(systemStats.getOs(), pfStats, systemStatsResp);
			stats.setSystemStats(systemStatsResp);
			LOGGER.debug("System level stats response [" + systemStats + "]");
		}
	}

	/**
	 * Checks and calculates profiling stats for DataNode category
	 * 
	 * @param statList
	 * @param pfStats
	 * @param hadoopJMXResp
	 * @throws HTFProfilingException
	 */
	private void calculateDataNodeStats(List<String> statList,
			ProfilerStats pfStats, HadoopJMXResponse hadoopJMXResp)
			throws HTFProfilingException {
		if (statList != null) {
			Map<String, String> dfsResp = new HashMap<String, String>();
			for (String dfsStat : statList) {
				dfsResp.put(dfsStat, pfStats.getDnStats(dfsStat));
			}
			hadoopJMXResp.setDataNode(dfsResp);
		}
	}

	/**
	 * Checks and calculates profiling stats for TaskTracker category
	 * 
	 * @param statList
	 * @param pfStats
	 * @param hadoopJMXResp
	 * @throws HTFProfilingException
	 */
	private void calculateTaskTrackerStats(List<String> statList,
			ProfilerStats pfStats, HadoopJMXResponse hadoopJMXResp)
			throws HTFProfilingException {
		if (statList != null) {
			Map<String, String> dfsResp = new HashMap<String, String>();
			for (String dfsStat : statList) {
				dfsResp.put(dfsStat, pfStats.getTtStats(dfsStat));
			}
			hadoopJMXResp.setTaskTracker(dfsResp);
		}
	}

	/**
	 * Checks and calculates profiling stats for CPU category
	 * 
	 * @param statList
	 * @param pfStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateCPUStats(List<String> statList,
			ProfilerStats pfStats, SystemStatsResponse systemStatsResp)
			throws HTFProfilingException {
		if (statList != null) {
			Map<String, String> cpuResp = new HashMap<String, String>();
			for (String cpuStat : statList) {
				cpuResp.put(cpuStat, pfStats.getCpuStats(cpuStat));
			}
			systemStatsResp.setCpu(cpuResp);
		}
	}

	/**
	 * Checks and calculates profiling stats for Memory category
	 * 
	 * @param statList
	 * @param pfStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateMemoryStats(List<String> statList,
			ProfilerStats pfStats, SystemStatsResponse systemStatsResp)
			throws HTFProfilingException {
		if (statList != null) {
			Map<String, String> memResp = new HashMap<String, String>();
			for (String memStat : statList) {
				memResp.put(memStat, pfStats.getMemoryStats(memStat));
			}
			systemStatsResp.setMemory(memResp);
		}
	}

	/**
	 * Checks and calculates profiling stats for OS category
	 * 
	 * @param statList
	 * @param pfStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateOsStats(List<String> statList, ProfilerStats pfStats,
			SystemStatsResponse systemStatsResp) throws HTFProfilingException {
		if (statList != null) {
			Map<String, String> osResp = new HashMap<String, String>();
			for (String osStat : statList) {
				osResp.put(osStat, pfStats.getMemoryStats(osStat));
			}
			systemStatsResp.setOs(osResp);
		}
	}

	/**
	 * Checks and calculates profiling stats for Cluster Wide category
	 * 
	 * @param clusterWide
	 * @param pfStats
	 * @param stats
	 * @throws HTFProfilingException
	 */
	private void calculateClusterWideStats(ClusterWideInfo clusterWide,
			ProfilerStats pfStats, StatsResult stats)
			throws HTFProfilingException {

		if (clusterWide != null) {
			ClusterWideResponse clusterWideResp = new ClusterWideResponse();
			if (clusterWide.getJobTracker() != null) {
				Map<String, String> jobTrackerResp = new HashMap<String, String>();
				for (String jobTrackerStat : clusterWide.getJobTracker()) {
					jobTrackerResp.put(jobTrackerStat,
							pfStats.getJtStats(jobTrackerStat));
				}
				clusterWideResp.setJobTracker(jobTrackerResp);
			}
			if (clusterWide.getNameNode() != null) {
				Map<String, String> nameNodeResp = new HashMap<String, String>();
				for (String nameNodeStat : clusterWide.getNameNode()) {
					nameNodeResp.put(nameNodeStat,
							pfStats.getNnStats(nameNodeStat));
				}
				clusterWideResp.setNameNode(nameNodeResp);
			}
			stats.setClusterWide(clusterWideResp);
			LOGGER.debug("Calculated cluster wide stats");
		}
	}

	/**
	 * Gets the node performance on the basis of criteria defined by the user
	 * 
	 * @param pfStats
	 * @param genSettings
	 * @return
	 * @throws HTFProfilingException
	 */
	private NodePerformance getNodePerformance(ProfilerStats pfStats,
			List<PerformanceStats> genSettings) throws HTFProfilingException {

		String catName;
		String stat;
		String statValue = null;
		List<NodePerformance> performances = new ArrayList<NodePerformance>();
		for (PerformanceStats settings : genSettings) {
			stat = settings.getStat();
			catName = settings.getCategory();
			statValue = calculateStatValue(pfStats, stat, catName);
			if (statValue == null || "".equals(statValue.trim())) {
				throw new HTFProfilingException("invalid statvalue !!!!!!!!1");
			}
			PerformanceEval good = settings.getGood();
			PerformanceEval bad = settings.getBad();
			if (comparePerformance(statValue, good)) {
				performances.add(NodePerformance.Good);
			} else if (comparePerformance(statValue, bad)) {
				performances.add(NodePerformance.Bad);
			} else {
				performances.add(NodePerformance.Average);
			}

		}
		return calculatePerformance(performances);
	}

	/**
	 * This api gets the favourite performance.
	 * 
	 * @param pfStats
	 *            the pf stats
	 * @param settings
	 *            the settings
	 * @param favColorPerf
	 *            the fav color perf
	 * @return the fav performance
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	private Map<String, NodePerformance> getFavPerformance(
			ProfilerStats pfStats, PerformanceStats settings,
			Map<String, NodePerformance> favColorPerf)
			throws HTFProfilingException {

		String statValue = null;
		PerformanceEval good = settings.getGood();
		PerformanceEval bad = settings.getBad();
		String catName = settings.getCategory();
		String stat = settings.getStat();
		statValue = calculateStatValue(pfStats, stat, catName);
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
	 * @param pfStats
	 *            the pf stats
	 * @param stat
	 *            the stat
	 * @param catName
	 *            the cat name
	 * @return the string
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	private String calculateStatValue(ProfilerStats pfStats, String stat,
			String catName) throws HTFProfilingException {

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
		statValue = processStatValueByCategory(pfStats, stat, catName,
				statValue, subCat, category);
		return statValue;
	}

	/**
	 * This api processes the stats value by category.
	 * 
	 * @param pfStats
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
	private String processStatValueByCategory(ProfilerStats pfStats,
			String stat, String catName, String statValue, String subCat,
			StatCategory category) throws HTFProfilingException {

		String statValueTmp = statValue;
		switch (category) {
		case jumbuneInferences:
			statValueTmp = pfStats.getJumbuneContextStats(stat);
			break;
		case clusterWide:
			if (NAMENODE.equalsIgnoreCase(subCat)) {
				statValueTmp = pfStats.getNnStats(stat);
			} else if (JOBTRACKER.equalsIgnoreCase(subCat)) {
				statValueTmp = pfStats.getJtStats(stat);
			} else {
				statValueTmp = pfStats.getClusterWideStats(stat);
			}
			break;
		case hadoopJMX:
			statValueTmp = getHadoopJMXStats(stat, catName, pfStats);
			break;
		case systemStats:
			if (SUBCAT_CPU.equals(subCat)) {
				statValueTmp = pfStats.getCpuStats(stat);
			} else {
				statValueTmp = pfStats.getMemoryStats(stat);
			}
			break;
		case workerJMXInfo:
			if (ProfilerConstants.TASKTRACKER.equalsIgnoreCase(subCat)) {
				statValueTmp = pfStats.getTtStats(stat);
			} else {
				statValueTmp = pfStats.getDnStats(stat);
			}
		}
		return statValueTmp;
	}

	/**
	 * Gets Hadoop JMX stats
	 * 
	 * @param stat
	 * @param subCat
	 * @param pfStats
	 * @return
	 * @throws HTFProfilingException
	 */
	public String getHadoopJMXStats(String stat, String catName,
			ProfilerStats pfStats) throws HTFProfilingException {
		String[] catArray = catName.split("\\.");
		String subCat = catArray[1];
		String statValue = null;
		HADOOP_JMX_CAT hadoopJmxCat = HADOOP_JMX_CAT.valueOf(subCat);
		switch (hadoopJmxCat) {
		case dfs:
			statValue = pfStats.getDnStats(stat);
			break;
		case rpc:
			if (DATANODE.equalsIgnoreCase(catArray[2])) {
				statValue = pfStats.getDnStats(stat);
			} else {
				statValue = pfStats.getTtStats(stat);
			}
			break;
		case io:
			statValue = pfStats.getDnStats(stat);
			break;
		case dataNodeMisc:
			statValue = pfStats.getDnStats(stat);
			break;
		case ttMisc:
			statValue = pfStats.getTtStats(stat);
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
	private boolean comparePerformance(String statValue,
			PerformanceEval perfStat) {
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

	private boolean processSmallStatVal(double perfval, double statVal,
			boolean eval) {
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
	private NodePerformance calculatePerformance(
			List<NodePerformance> performances) {
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

}
