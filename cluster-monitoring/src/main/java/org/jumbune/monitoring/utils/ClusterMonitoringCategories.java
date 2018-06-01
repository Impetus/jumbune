package org.jumbune.monitoring.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.NameNodes;
import org.jumbune.common.beans.cluster.TaskManagers;
import org.jumbune.common.beans.cluster.Workers;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.monitoring.beans.CategoryInfo;
import org.jumbune.monitoring.beans.ClusterWideInfo;
import org.jumbune.monitoring.beans.JMXDeamons;
import org.jumbune.monitoring.beans.SystemStats;
import org.jumbune.monitoring.beans.WorkerJMXInfo;
import org.jumbune.monitoring.yarn.beans.YarnCategoryInfo;
import org.jumbune.monitoring.yarn.beans.YarnClusterWideInfo;
import org.jumbune.monitoring.yarn.beans.YarnWorkerJMXInfo;

public class ClusterMonitoringCategories {

	private static final String CONTAINER_RESOURCE_CONTAINER = "ContainerResource_container_";
	private static final String TAG = ".tag.";
	
	private static final Logger LOGGER = LogManager.getLogger(ClusterMonitoringCategories.class);
	private Cluster cluster;
	private String nodeIP;

	private List<String> clusterWideResourceManager;
	private List<String> clusterWideNameNode;
	private List<String> workerJMXInfoDataNode;
	String hadoopDistribution;
	boolean isMapr;

	public ClusterMonitoringCategories(Cluster cluster) {
		this.cluster = cluster;
		hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		isMapr = Constants.MAPR.equalsIgnoreCase(hadoopDistribution);
	}

	public ClusterMonitoringCategories(Cluster cluster, String nodeIP) {
		this(cluster);
		this.nodeIP = nodeIP;
	}

	/**
	 * This method return all categories of hadoop jmx exposed by hadoop cluster
	 * in form of json string.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCategoriesJSON() throws Exception {
		return Constants.gson.toJson(getProfilerCategoryJson(cluster));
	}

	/**
	 * This method return all categories of hadoop jmx exposed by hadoop cluster
	 * in form of json string.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getFilteredCategoriesJSON() throws Exception {
		CategoryInfo categoryInfo = getProfilerCategoryJson(cluster);
		setExcludedCategories();
		filterCategories(categoryInfo);
		return Constants.gson.toJson(categoryInfo);
	}

	public String getNodeSpecificCategories() throws Exception {
		CategoryInfo categoryInfo = getProfilerCategoryJson(cluster);
		YarnCategoryInfo yarnCategoryInfo = null;
		setExcludedCategories();
		filterCategories(categoryInfo);

		// Checking if node has NameNode daemon running, if not remove NameNode stats list
		if (!cluster.getNameNodes().getHosts().contains(nodeIP)) {
			categoryInfo.getClusterWide().setNameNode(null);
		}
		// Checking if node has DataNode daemon running, if not remove DateNode stats list 
		if (!cluster.getWorkers().getHosts().contains(nodeIP)) {
			categoryInfo.setWorkerJMXInfo(null);

		}
		
		/**
		 *  Checking if node has ResourceManager daemon or JobTracker daemon running,
		 *  , if not remove stats list
		 */
		yarnCategoryInfo = (YarnCategoryInfo) categoryInfo;
		if (!cluster.getTaskManagers().getHosts().contains(nodeIP)) {
			yarnCategoryInfo.getClusterWide().setResourceManager(null);
		}
		
		/**
		 * Checking if NameNodeStats and task manager stats are null or not,
		 * if null then set clusterwide to null
		 */
		if (categoryInfo.getClusterWide().getNameNode() == null
				&& ((yarnCategoryInfo.getClusterWide().getResourceManager() == null))) {
			categoryInfo.setClusterWide(null);
		}

		return Constants.gson.toJson(categoryInfo);
	}

	/**
	 * This method return all categories of hadoop jmx exposed by hadoop cluster
	 * 
	 * @param cluster
	 * @param isYarnEnable
	 * @return
	 * @throws Exception
	 */
	private CategoryInfo getProfilerCategoryJson(Cluster cluster) throws Exception {

		CategoryInfo categoryInfo = null;

		categoryInfo = new YarnCategoryInfo();

		WorkerJMXInfo levelJMXInfo = null;
		ClusterWideInfo clusterWideInfo = new ClusterWideInfo();
		clusterWideInfo = new YarnClusterWideInfo();
		levelJMXInfo = new YarnWorkerJMXInfo();
		ProfilerJMXDump dump = new ProfilerJMXDump();

		setClusterLevelDaemons(cluster, clusterWideInfo, dump);
		setNodeLevelDaemons(cluster, dump, levelJMXInfo);
		String systemStatsJson = getPropertyFromResource(Constants.PROFILING_PROPERTY_FILE,
				Constants.PROFILING_SYSTEM_JSON);
		SystemStats stats = Constants.gson.fromJson(systemStatsJson, SystemStats.class);

		categoryInfo.setClusterWide(clusterWideInfo);
		categoryInfo.setWorkerJMXInfo(levelJMXInfo);
		categoryInfo.setSystemStats(stats);
		return categoryInfo;
	}

	private void setNodeLevelDaemons(Cluster cluster, ProfilerJMXDump dump, WorkerJMXInfo levelJMXInfo) {

		Workers workers = cluster.getWorkers();
		boolean jmxPluginEnabled = cluster.isJmxPluginEnabled();
		for (String workerHost : workers.getHosts()) {
			try {
				setYarnAndNonYarnDaemons(workerHost, workers, dump, levelJMXInfo, jmxPluginEnabled);
			} catch (Exception e) {
				LOGGER.error("Unable to get Node Manager Stats for Node [" + workerHost + "]", e.getMessage());
			}
		}
	}

	private void setYarnAndNonYarnDaemons(String host, Workers workers, ProfilerJMXDump dump,
			WorkerJMXInfo levelJMXInfo, boolean jmxPluginEnabled) throws Exception {

		try {
			if (!isMapr) {
				levelJMXInfo.setDataNode(dump.getAllJMXAttribute(
						JMXDeamons.DATA_NODE, host, workers.getDataNodeJmxPort(), jmxPluginEnabled));
			}
		} catch (Exception e) {
			LOGGER.error("Unable to Data Node Stats for Node [" + host + "]", e.getMessage());
		}
		
		YarnWorkerJMXInfo yarnWorkerJMXInfo = (YarnWorkerJMXInfo) levelJMXInfo;
		yarnWorkerJMXInfo.addStatsToNodeManager(dump.getAllJMXAttribute(JMXDeamons.NODE_MANAGER, host,
				workers.getTaskExecutorJmxPort(), jmxPluginEnabled));

	}

	private void setClusterLevelDaemons(Cluster cluster, ClusterWideInfo clusterWideInfo, ProfilerJMXDump dump) {

		NameNodes nameNodes = cluster.getNameNodes();
		String nameNodeHost = cluster.getNameNode();
		TaskManagers taskManagers = cluster.getTaskManagers();
		String nameNodeJmxPort = nameNodes.getNameNodeJmxPort();
		String taskManagerJmxPort = taskManagers.getTaskManagerJmxPort();

		try {
			if (!isMapr) {
				clusterWideInfo.setNameNode(dump.getAllJMXAttribute(JMXDeamons.NAME_NODE, nameNodeHost, nameNodeJmxPort,
						cluster.isJmxPluginEnabled()));
			}
		} catch (Exception e) {
			LOGGER.error("Unable to get Namenode stats for Node [" + nameNodeHost + "]", e.getMessage());
		}
		
		try {
			YarnClusterWideInfo yarnClusterWideInfo = (YarnClusterWideInfo) clusterWideInfo;
			yarnClusterWideInfo.setResourceManager(dump.getAllJMXAttribute(JMXDeamons.RESOURCE_MANAGER,
					cluster.getResourceManager(), taskManagerJmxPort, cluster.isJmxPluginEnabled()));
			removeDuplicates(yarnClusterWideInfo.getResourceManager());
		} catch (Exception e) {
			LOGGER.error("Unable to get Resource Manager stats for Node [" + cluster.getResourceManager() + "]", e.getMessage());
		}
	}

	private void removeDuplicates(List<String> list) {
		Map<String, Integer> map = new HashMap<String, Integer>();

		Iterator<String> it = list.iterator();
		String category;
		while (it.hasNext()) {
			category = it.next();
			if (map.get(category) == null) {
				map.put(category, 1);
			} else {
				it.remove();
			}
		}
	}

	private void filterCategories(CategoryInfo categoryInfo) {

		ClusterWideInfo clusterWideInfo = categoryInfo.getClusterWide();
		removeStatsContainingParticularKeywords(clusterWideInfo.getJobTracker());
		removeStatsContainingParticularKeywords(clusterWideInfo.getNameNode());
		removeStatsContainingParticularKeywords(clusterWideInfo.getNameNode());
		removeAll(clusterWideInfo.getNameNode(), clusterWideNameNode);

		WorkerJMXInfo workerJMXInfo = categoryInfo.getWorkerJMXInfo();
		removeStatsContainingParticularKeywords(workerJMXInfo.getDataNode());
		removeStatsContainingParticularKeywords(workerJMXInfo.getDataNode());
		removeAll(workerJMXInfo.getDataNode(), workerJMXInfoDataNode);
		removeStatsContainingParticularKeywords(workerJMXInfo.getTaskTracker());

		SystemStats systemStats = categoryInfo.getSystemStats();
		removeStatsContainingParticularKeywords(systemStats.getCpu());
		removeStatsContainingParticularKeywords(systemStats.getMemory());
		removeStatsContainingParticularKeywords(systemStats.getOs());

		YarnCategoryInfo yarnCategoryInfo = (YarnCategoryInfo) categoryInfo;
		removeStatsContainingParticularKeywords(yarnCategoryInfo.getClusterWide().getResourceManager());
		removeStatsContainingParticularKeywords(yarnCategoryInfo.getClusterWide().getResourceManager());
		removeAll(yarnCategoryInfo.getClusterWide().getResourceManager(), clusterWideResourceManager);
		removeStatsContainingParticularKeywords(yarnCategoryInfo.getWorkerJMXInfo().getNodeManager());

	}

	/**
	 * Removing stats that contains "tag" substring
	 * @param list
	 */
	private void removeStatsContainingParticularKeywords(List<String> list) {
		if (list == null) {
			return;
		}
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String str = it.next();
			if (str.contains(TAG) || str.contains(CONTAINER_RESOURCE_CONTAINER)) {
				it.remove();
			}
		}
	}
	
	private void removeStatsContainingParticularKeywords(Set<String> list) {
		if (list == null) {
			return;
		}
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String str = it.next();
			if (str.contains(TAG) || str.contains(CONTAINER_RESOURCE_CONTAINER)) {
				it.remove();
			}
		}
	}

	private void removeAll(List<String> list, List<String> exclude) {
		if (list != null) {
			list.removeAll(exclude);
		}
	}

	private void setExcludedCategories() {
		clusterWideResourceManager = new ArrayList<String>(25);
		clusterWideNameNode = new ArrayList<String>(60);
		workerJMXInfoDataNode = new ArrayList<String>(4);

		workerJMXInfoDataNode.add("FSDatasetState-null.StorageInfo");
		workerJMXInfoDataNode.add("DataNodeInfo.VolumeInfo");
		workerJMXInfoDataNode.add("DataNodeInfo.NamenodeAddresses");
		workerJMXInfoDataNode.add("DataNodeInfo.ClusterId");
		workerJMXInfoDataNode.add("DataNodeInfo.RpcPort");
		workerJMXInfoDataNode.add("DataNodeInfo.HttpPort");
		workerJMXInfoDataNode.add("DataNodeInfo.Version");

		clusterWideResourceManager.add("QueueMetrics.running_0");
		clusterWideResourceManager.add("QueueMetrics.running_60");
		clusterWideResourceManager.add("QueueMetrics.running_300");
		clusterWideResourceManager.add("QueueMetrics.running_1440");
		clusterWideResourceManager.add("QueueMetrics.AppsSubmitted");
		clusterWideResourceManager.add("QueueMetrics.AppsRunning");
		clusterWideResourceManager.add("QueueMetrics.AppsPending");
		clusterWideResourceManager.add("QueueMetrics.AppsCompleted");
		clusterWideResourceManager.add("QueueMetrics.AppsKilled");
		clusterWideResourceManager.add("QueueMetrics.AppsFailed");
		clusterWideResourceManager.add("QueueMetrics.AllocatedMB");
		clusterWideResourceManager.add("QueueMetrics.AllocatedVCores");
		clusterWideResourceManager.add("QueueMetrics.AllocatedContainers");
		clusterWideResourceManager.add("QueueMetrics.AggregateContainersAllocated");
		clusterWideResourceManager.add("QueueMetrics.AggregateContainersReleased");
		clusterWideResourceManager.add("QueueMetrics.AvailableMB");
		clusterWideResourceManager.add("QueueMetrics.AvailableVCores");
		clusterWideResourceManager.add("QueueMetrics.PendingMB");
		clusterWideResourceManager.add("QueueMetrics.PendingVCores");
		clusterWideResourceManager.add("QueueMetrics.PendingContainers");
		clusterWideResourceManager.add("QueueMetrics.ReservedMB");
		clusterWideResourceManager.add("QueueMetrics.ReservedVCores");
		clusterWideResourceManager.add("QueueMetrics.ReservedContainers");
		clusterWideResourceManager.add("QueueMetrics.ActiveUsers");
		clusterWideResourceManager.add("QueueMetrics.ActiveApplications");
		
		clusterWideNameNode.add("UgiMetrics.CapacityTotalGB");
		clusterWideNameNode.add("UgiMetrics.CapacityUsedGB");
		clusterWideNameNode.add("UgiMetrics.CapacityRemainingGB");
		clusterWideNameNode.add("UgiMetrics.LoginSuccessNumOps");
		clusterWideNameNode.add("UgiMetrics.LoginSuccessAvgTime");
		clusterWideNameNode.add("UgiMetrics.LoginFailureNumOps");
		clusterWideNameNode.add("UgiMetrics.LoginFailureAvgTime");
		clusterWideNameNode.add("UgiMetrics.GetGroupsNumOps");
		clusterWideNameNode.add("UgiMetrics.GetGroupsAvgTime");
		clusterWideNameNode.add("JvmMetrics.MemNonHeapUsedM");
		clusterWideNameNode.add("JvmMetrics.MemNonHeapCommittedM");
		clusterWideNameNode.add("JvmMetrics.MemNonHeapMaxM");
		clusterWideNameNode.add("JvmMetrics.MemHeapUsedM");
		clusterWideNameNode.add("JvmMetrics.MemHeapCommittedM");
		clusterWideNameNode.add("JvmMetrics.MemHeapMaxM");
		clusterWideNameNode.add("JvmMetrics.MemMaxM");
		clusterWideNameNode.add("JvmMetrics.GcCountPS Scavenge");
		clusterWideNameNode.add("JvmMetrics.GcTimeMillisPS Scavenge");
		clusterWideNameNode.add("JvmMetrics.GcCountPS MarkSweep");
		clusterWideNameNode.add("JvmMetrics.GcTimeMillisPS MarkSweep");
		clusterWideNameNode.add("JvmMetrics.GcCount");
		clusterWideNameNode.add("JvmMetrics.GcTimeMillis");
		clusterWideNameNode.add("JvmMetrics.ThreadsNew");
		clusterWideNameNode.add("JvmMetrics.ThreadsRunnable");
		clusterWideNameNode.add("JvmMetrics.ThreadsBlocked");
		clusterWideNameNode.add("JvmMetrics.ThreadsWaiting");
		clusterWideNameNode.add("JvmMetrics.ThreadsTimedWaiting");
		clusterWideNameNode.add("JvmMetrics.ThreadsTerminated");
		clusterWideNameNode.add("JvmMetrics.LogFatal");
		clusterWideNameNode.add("JvmMetrics.LogError");
		clusterWideNameNode.add("JvmMetrics.LogWarn");
		clusterWideNameNode.add("JvmMetrics.LogInfo");
		clusterWideNameNode.add("MetricsSystem.NumActiveSources");
		clusterWideNameNode.add("MetricsSystem.NumAllSources");
		clusterWideNameNode.add("MetricsSystem.NumActiveSinks");
		clusterWideNameNode.add("MetricsSystem.NumAllSinks");
		clusterWideNameNode.add("MetricsSystem.SnapshotNumOps");
		clusterWideNameNode.add("MetricsSystem.SnapshotAvgTime");
		clusterWideNameNode.add("MetricsSystem.PublishNumOps");
		clusterWideNameNode.add("MetricsSystem.PublishAvgTime");
		clusterWideNameNode.add("MetricsSystem.DroppedPubAll");
		clusterWideNameNode.add("NameNodeStatus.NNRole");
		clusterWideNameNode.add("NameNodeStatus.SecurityEnabled");
		clusterWideNameNode.add("NameNodeStatus.State");
		clusterWideNameNode.add("NameNodeStatus.HostAndPort");
		clusterWideNameNode.add("FSNamesystemState.SnapshotStats");
		clusterWideNameNode.add("FSNamesystemState.FSState");
		clusterWideNameNode.add("NameNodeInfo.ClusterId");
		clusterWideNameNode.add("NameNodeInfo.Safemode");
		clusterWideNameNode.add("NameNodeInfo.LiveNodes");
		clusterWideNameNode.add("NameNodeInfo.DecomNodes");
		clusterWideNameNode.add("NameNodeInfo.BlockPoolId");
		clusterWideNameNode.add("NameNodeInfo.NameDirStatuses");
		clusterWideNameNode.add("NameNodeInfo.NodeUsage");
		clusterWideNameNode.add("NameNodeInfo.NameJournalStatus");
		clusterWideNameNode.add("NameNodeInfo.JournalTransactionInfo");
		clusterWideNameNode.add("NameNodeInfo.NNStarted");
		clusterWideNameNode.add("NameNodeInfo.CompileInfo");
		clusterWideNameNode.add("NameNodeInfo.CorruptFiles");
		clusterWideNameNode.add("NameNodeInfo.DistinctVersions");
		clusterWideNameNode.add("NameNodeInfo.SoftwareVersion");
		clusterWideNameNode.add("NameNodeInfo.RollingUpgradeStatus");
		clusterWideNameNode.add("NameNodeInfo.UpgradeFinalized");
		clusterWideNameNode.add("NameNodeInfo.Version");

	}

	/**
	 * * This method load properties from a file which is type of key value
	 * paired and return value against specific key.
	 *
	 * @param propertyFile
	 *            property file name
	 * @param propertyName
	 *            property name which value user wants to get.
	 * @return String property value
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String getPropertyFromResource(String propertyFile, String propertyName)throws IOException {
		InputStream msgStream = null;
		try {
			msgStream = getClass().getClassLoader().getResourceAsStream(propertyFile);
			Properties properties = new Properties();
			properties.load(msgStream);
			return (String) properties.get(propertyName);
		} finally {
			if (msgStream != null) {
				msgStream.close();
			}
		}

	}
}
