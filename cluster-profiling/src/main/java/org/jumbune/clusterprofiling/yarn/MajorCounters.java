package org.jumbune.clusterprofiling.yarn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.utils.ProfilerJMXDump;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import org.jumbune.common.utils.ExtendedConstants;

public class MajorCounters {

	private final String CLDB_METRICS_ENABLED = "cldbMetricsEnabled";
	private final String CLDB_SERVER_CLUSTER_DISK_SPACE_AVAILABLE_GB = "cldbServer.Cluster Disk Space Available GB";
	private final String CLDB_SERVER_CLUSTER_DISK_SPACE_USED_GB = "cldbServer.Cluster Disk Space Used GB";
	private final String CLDB_SERVER_NO_OF_CONTAINERS_ONE_VALID_REPLICA = "cldbServer.Number of Containers with One valid Replica";
	private final String CLDB_SERVER_NO_OF_STORAGE_POOLS_IN_CLUSTER = "cldbServer.Number of Storages Pools in Cluster";
	private final String CLDB_SERVER_NO_OF_STORAGE_POOL_OFFLINE = "cldbServer.Number of Storage Pools Offline";
	private final String CLDB_SERVER_NUMBER_OF_CONTAINERS = "cldbServer.Number of Containers";
	private final String CLDB_SERVER_NUMBER_OF_VOLUMES = "cldbServer.Number of Volumes";
	private final String CLUSTER_DISK_SPACE_AVAILABLE_GB = "clusterDiskSpaceAvailableGB";
	private final String CLUSTER_DISK_SPACE_USED_GB = "clusterDiskSpaceUsedGB";
	private final String CLUSTER_METRICS_NUM_ACTIVE_N_MS = "ClusterMetrics.NumActiveNMs";
	private final String DFS_REMAINING = "dfsRemaining";
	private final String DFS_USED = "dfsUsed";
	private final String FALSE = "false";
	private final String FS_NAMESYSTEM_CAPACITY_REMAINING_GB = "FSNamesystem.CapacityRemainingGB";
	private final String FS_NAMESYSTEM_CAPACITY_USED_GB = "FSNamesystem.CapacityUsedGB";
	private final String FS_NAMESYSTEM_LAST_CHECKPOINT_TIME = "FSNamesystem.LastCheckpointTime";
	private final String FS_NAMESYSTEM_UNDER_REPLICATED_BLOCKS = "FSNamesystem.UnderReplicatedBlocks";
	private final String HH_MM_DD_MMM = "HH:mm dd MMM";
	private final String JOB_TRACKER_METRICS_TRACKERS = "JobTrackerMetrics.trackers";
	private final String JVM_METRICS_MEM_HEAP_USED_M = "JvmMetrics.MemHeapUsedM";
	private final String LAST_CHECKPOINT_TIME = "lastCheckpointTime";
	private final String NAME_NODE_HEAP_USAGE = "nameNodeHeapUsage";
	private final String NAME_NODE_RPC_ACTIVITY_NUM_OPEN_CONNECTIONS = "nameNodeRpcActivityNumOpenConnections";
	private final String NO_OF_CONTAINERS_WITHOUT_ATLEAST_ONE_REPLICA = "containersWithoutAtleaseOneReplica";
	private final String NO_OF_STORAGE_POOLS_IN_CLUSTER = "noOfStoragesPoolsInCluster";
	private final String NO_OF_STORAGE_POOL_OFFLINE = "noOfStoragePoolsOffline";
	private final String NUMBER_OF_CONTAINERS = "noOfContainers";
	private final String NUMBER_OF_VOLUMES = "noOfVolumes";
	private final String NUM_ACTIVE_N_MS = "NumActiveNMs";
	private final String RESOURCE_MANAGER_HEAP_USAGE = "resourceManagerHeapUsage";
	private final String RESOURCE_MANAGER_RPC_ACTIVITY_NUM_OPEN_CONNECTIONS = "resourceManagerRpcActivityNumOpenConnections";
	private final String RPC_ACTIVITY_FOR_PORT54310_NUM_OPEN_CONNECTIONS = "RpcActivityForPort54310.NumOpenConnections";
	private final String RPC_ACTIVITY_FOR_PORT_NUM_OPEN_CONNECTIONS = "RpcActivityForPort(.*)NumOpenConnections";
	private final String TRACKERS = "Trackers";
	private final String TRUE = "true";
	private final String UNABLE_TO_GET_CLDB_METRICS_STATS = "Unable to get CLDB Metrics stats";
	private final String UNABLE_TO_GET_NAMENODE_STATS = "Unable to get Namenode stats";
	private final String UNABLE_TO_GET_RESOURCE_MANAGER_STATS = "Unable to get Resource Manager stats";
	private final String UNDER_REPLICATED_BLOCKS = "underReplicatedBlocks";
	
	String hadoopType;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = 
			LogManager.getLogger(MajorCounters.class);
	
	private static volatile MajorCounters instance;
	
	private MajorCounters() {
		hadoopType = FileUtil.getClusterInfoDetail(ExtendedConstants.HADOOP_TYPE);
	}
	
	public static MajorCounters getInstance() {
		if (instance == null) {
			synchronized (MajorCounters.class) {
				if (instance == null) {
					instance = new MajorCounters();
				}
			}
		}
		return instance;
	}
	
	public Map<String, String> getMajorCounters(Cluster cluster)  {
		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> nameNodeStats = null;
		try {
			nameNodeStats = jmxDump.getAllJMXStats(JMXDeamons.NAME_NODE,cluster.getNameNode(),
					cluster.getNameNodes().getNameNodeJmxPort(), cluster.isJmxPluginEnabled());
			
		} catch (Exception e) {
			LOGGER.error(UNABLE_TO_GET_NAMENODE_STATS, e.getMessage());
			LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
		}
		if (nameNodeStats != null) {
			map.put(DFS_USED, nameNodeStats.get(FS_NAMESYSTEM_CAPACITY_USED_GB));
			map.put(DFS_REMAINING, nameNodeStats.get(FS_NAMESYSTEM_CAPACITY_REMAINING_GB));
			map.put(UNDER_REPLICATED_BLOCKS, nameNodeStats.get(FS_NAMESYSTEM_UNDER_REPLICATED_BLOCKS));
			map.put(LAST_CHECKPOINT_TIME,  getLastCheckpointTime(nameNodeStats));
			map.put(NAME_NODE_HEAP_USAGE, nameNodeStats.get(JVM_METRICS_MEM_HEAP_USED_M));
			String nameNodeRPC = nameNodeStats.get(RPC_ACTIVITY_FOR_PORT54310_NUM_OPEN_CONNECTIONS);
			if (nameNodeRPC == null) {
				String rpcStatNameRegex = RPC_ACTIVITY_FOR_PORT_NUM_OPEN_CONNECTIONS;
				for (Entry<String, String> e : nameNodeStats.entrySet()) {
					if (e.getKey().matches(rpcStatNameRegex)) {
						nameNodeRPC = e.getValue();
						break;
					}
				}
			}
			if (nameNodeRPC != null) {
				map.put(NAME_NODE_RPC_ACTIVITY_NUM_OPEN_CONNECTIONS, nameNodeRPC);
			}
			
		}
		
		if (hadoopType.equalsIgnoreCase(ExtendedConstants.YARN)) {
			Map<String, String> resourceManagerStats = null;
			try {
				
				resourceManagerStats = jmxDump.getAllJMXStats(JMXDeamons.RESOURCE_MANAGER,
					cluster.getTaskManagers().getHosts().get(0), cluster.getTaskManagers().getTaskManagerJmxPort(),
					cluster.isJmxPluginEnabled());
				
			} catch (Exception e) {
				LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			}
			
			if (resourceManagerStats != null) {
				// active resource
				map.put(NUM_ACTIVE_N_MS, resourceManagerStats.get(CLUSTER_METRICS_NUM_ACTIVE_N_MS));
				map.put(RESOURCE_MANAGER_HEAP_USAGE, resourceManagerStats.get(JVM_METRICS_MEM_HEAP_USED_M));
			}
			
			
		} else {
			
			Map<String, String> jobTrackerStats =null;
			try {
				jobTrackerStats = jmxDump.getAllJMXStats(JMXDeamons.JOB_TRACKER,
						cluster.getTaskManagers().getHosts().get(0), cluster.getTaskManagers().getTaskManagerJmxPort(), cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			}
			
			if (jobTrackerStats != null) {
				// active resource
				map.put(TRACKERS, jobTrackerStats.get(JOB_TRACKER_METRICS_TRACKERS));
			}
		}
		
		return map;
	}
	
	public String getLastCheckpointTime(Map<String, String> nameNodeStats) {
		Long unixTime = Long.parseLong(nameNodeStats.get(FS_NAMESYSTEM_LAST_CHECKPOINT_TIME));
		Date date = new Date(unixTime);
		SimpleDateFormat sdf = new SimpleDateFormat(HH_MM_DD_MMM);
		return sdf.format(date);
	}
	
	public Map<String, String> getMaprCldbMetrics(Cluster cluster) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		
		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		
		Map<String, String> cldbStats = null;
		try {
			cldbStats = jmxDump.getAllJMXStats(JMXDeamons.CLDB, cluster.getNameNode(), 
					"7220", false);
			map.put(NO_OF_STORAGE_POOLS_IN_CLUSTER, cldbStats.get(CLDB_SERVER_NO_OF_STORAGE_POOLS_IN_CLUSTER));
			map.put(NUMBER_OF_VOLUMES, cldbStats.get(CLDB_SERVER_NUMBER_OF_VOLUMES));
			map.put(CLUSTER_DISK_SPACE_AVAILABLE_GB, cldbStats.get(CLDB_SERVER_CLUSTER_DISK_SPACE_AVAILABLE_GB));
			map.put(CLUSTER_DISK_SPACE_USED_GB, cldbStats.get(CLDB_SERVER_CLUSTER_DISK_SPACE_USED_GB));
			map.put(NO_OF_STORAGE_POOL_OFFLINE, cldbStats.get(CLDB_SERVER_NO_OF_STORAGE_POOL_OFFLINE));
			try {
				String sNumberOfContainers = cldbStats.get(CLDB_SERVER_NUMBER_OF_CONTAINERS);
				map.put(NUMBER_OF_CONTAINERS, sNumberOfContainers);
				
				long numberOfContainers = Long.parseLong(sNumberOfContainers);
				long containersWithAtleastOneReplica = Long.parseLong(cldbStats.get(CLDB_SERVER_NO_OF_CONTAINERS_ONE_VALID_REPLICA));
				long containersWithoutAtleastOneReplica = numberOfContainers - containersWithAtleastOneReplica;
				
				map.put(NO_OF_CONTAINERS_WITHOUT_ATLEAST_ONE_REPLICA, String.valueOf(containersWithoutAtleastOneReplica));
				
				map.put(CLDB_METRICS_ENABLED, TRUE);
			} catch (Exception e) {
				map.put(CLDB_METRICS_ENABLED, FALSE);
				// In case null pointer exception or parsing exception
			}
			
		} catch (Exception e) {
			LOGGER.error(UNABLE_TO_GET_CLDB_METRICS_STATS, e);
			map.put(CLDB_METRICS_ENABLED, FALSE);
		}
		
		Map<String, String> resourceManagerStats = null;
		
		
		try {
			resourceManagerStats = jmxDump.getAllJMXStats(JMXDeamons.RESOURCE_MANAGER,
				cluster.getTaskManagers().getHosts().get(0), cluster.getTaskManagers().getTaskManagerJmxPort(),
				cluster.isJmxPluginEnabled());
		
		} catch (Exception e) {
			LOGGER.error(UNABLE_TO_GET_RESOURCE_MANAGER_STATS, e.getMessage());
			LOGGER.error(e);
		}
		
		if (resourceManagerStats != null) {
			
			map.put(RESOURCE_MANAGER_HEAP_USAGE,
					resourceManagerStats.get(JVM_METRICS_MEM_HEAP_USED_M));
			
			String resourceManagerRPC = 
					resourceManagerStats.get(RPC_ACTIVITY_FOR_PORT54310_NUM_OPEN_CONNECTIONS);
			
			if (resourceManagerRPC == null) {
				String rpcStatNameRegex = RPC_ACTIVITY_FOR_PORT_NUM_OPEN_CONNECTIONS;
				for (Entry<String, String> e : resourceManagerStats.entrySet()) {
					if (e.getKey().matches(rpcStatNameRegex)) {
						resourceManagerRPC = e.getValue();
						break;
					}
				}
			}
			resourceManagerRPC = String.valueOf((int) Double.parseDouble(resourceManagerRPC));
			map.put(RESOURCE_MANAGER_RPC_ACTIVITY_NUM_OPEN_CONNECTIONS, resourceManagerRPC);
		}
		return map;
	}

}
