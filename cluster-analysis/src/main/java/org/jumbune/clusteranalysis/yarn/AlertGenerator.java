package org.jumbune.clusteranalysis.yarn;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.remote.JMXServiceURL;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.alerts.AlertConstants;
import org.jumbune.common.alerts.Alerts;
import org.jumbune.common.beans.AlertInfo;
import org.jumbune.common.beans.EffCapUtilizationStats;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.monitoring.beans.JMXDeamons;
import org.jumbune.monitoring.utils.JMXConnectorCache;
import org.jumbune.monitoring.utils.JMXConnectorInstance;
import org.jumbune.monitoring.utils.ProfilerConstants;
import org.jumbune.monitoring.utils.ProfilerJMXDump;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.AlertConfiguration;
import org.jumbune.utils.conf.beans.AlertType;
import org.jumbune.utils.conf.beans.SeverityLevel;
import org.jumbune.utils.exception.JumbuneRuntimeException;
import org.jumbune.utils.yarn.communicators.RMCommunicator;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * The Class AlertGenerator.
 */
public class AlertGenerator implements Alerts {

	private final String FAILED = " Failed";

	/** The Constant CLUSTER_PROFILING. */
	private final String CLUSTER_PROFILING = "/clusterprofiling/";

	/** The Constant UTILIZATION_STATS. */
	private final String UTILIZATION_STATS = "/utilizationStats/";

	/** The Constant JSON. */
	private final String JSON = ".json";

	/** The Constant EFFECTIVE_MAX_UTILISATION. */
	private final String EFFECTIVE_MAX_UTILISATION = "maxUtilisation";

	/** The Constant DEAMON_WENT_DOWN. */
	private final String DEAMON_WENT_DOWN = " went down";
	
	/** The Constant DEAMON_WENT_UNHEALTHY. */
	private final String DEAMON_IS_UNHEALTHY = " is unhealthy";
	
	private Type type = new TypeToken<Map<String, Map<String, String>>>() {
	}.getType();
	
	private final Logger LOGGER = LogManager.getLogger(AlertGenerator.class);

	private static volatile AlertGenerator instance = null;
	
	public static AlertGenerator getInstance() {
		if (instance == null) {
			synchronized (AlertGenerator.class) {
				if (instance == null) {
					instance = new AlertGenerator();
				}
			}
		}
		return instance;
	}
	
	private AlertGenerator() {
	}
	
	@Override
	public List<AlertInfo> getAllAlerts(Cluster cluster, RMCommunicator rmCommunicator, long oldTime, long newTime) {
		String clusterName = cluster.getClusterName();
		List<AlertInfo> alertsList = new ArrayList<>();
		try {
			AlertConfiguration alertConf = AdminConfigurationUtil.getAlertConfiguration(clusterName); 
			Map<AlertType, Boolean> nonConfigurableAlerts = alertConf.getNonConfigurableAlerts();

			String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
			AlertInfo namenodeDownAlert = null;
			if(!Constants.MAPR.equalsIgnoreCase(hadoopDistribution) && !Constants.EMRMAPR.equalsIgnoreCase(hadoopDistribution)){
				namenodeDownAlert = getNameNodeDownAlert(cluster);
				if (namenodeDownAlert != null) {
					alertsList.add(namenodeDownAlert);
				} else {
					//configurable UNDER_REPLICATED_BLOCKS
					alertsList.addAll(getUnderReplicatedBlockAlert(cluster));
					//configurable HDFS_UTILIZATION
					alertsList.addAll(getHDFSSpaceUsageAlert(cluster));
					if (nonConfigurableAlerts.get(AlertType.HADOOP_DAEMON_DOWN)) {
						//non-configurable HADOOP_DAEMON_DOWN
						LOGGER.debug("Checking for Node Down Alert");
						alertsList.addAll(getNodeDownAlert(cluster));
					}
				}
				if (nonConfigurableAlerts.get(AlertType.HADOOP_DAEMON_DOWN)) {				
					//non-configurable HADOOP_DAEMON_DOWN
					alertsList.addAll(getDataNodeDownAlert(cluster));
				}
				//configurable DISK_SPACE_UTILIZATION
				alertsList.addAll(getDiskSpaceUsageAlert(cluster));

				//non-configurable DN_VOLUME_FAILURE_CHECK
				if(nonConfigurableAlerts.get(AlertType.DN_VOLUME_FAILURE_CHECK)) {
					alertsList.addAll(getDataNodeVolumeFailureAlert(cluster));
				}
			}

			//Added alert for max files  in a hdfs directory
			AlertConfiguration alertConfiguration = AdminConfigurationUtil.getAlertConfiguration(clusterName);
			if(!alertConfiguration.getHdfsDirPaths().isEmpty()){
				alertsList.addAll(getHDFSMaxFilesInDirAlert(cluster, alertConfiguration.getHdfsDirPaths()));
			}

			// fragmented files alert added for non-secured cluster and non-mapr cluster only
			//			if(!Constants.MAPR.equalsIgnoreCase(hadoopDistribution) && !Constants.EMRMAPR.equalsIgnoreCase(hadoopDistribution)){
			//				alertsList.addAll(alertGenerator.getFragmenedFilesAlert(cluster));
			//			}

			AlertInfo resourceManagerDownAlert = null;
			AlertInfo historyServerDownAlert = null;
			if(nonConfigurableAlerts.get(AlertType.HADOOP_DAEMON_DOWN)) {
				//non-configurable HADOOP_DAEMON_DOWN : Check History server

				historyServerDownAlert = getHistoryServerDownAlert(cluster);
				if(historyServerDownAlert != null){
					alertsList.add(historyServerDownAlert);
				}
				//non-configurable HADOOP_DAEMON_DOWN : Check Resource Manager
				resourceManagerDownAlert = getResourceManagerDownAlert(cluster);
			}

			if (resourceManagerDownAlert != null) {
				alertsList.add(resourceManagerDownAlert);
			} else {
				if(nonConfigurableAlerts.get(AlertType.CLUSTER_TIME_DESYNC)) {
					//non-configurable CLUSTER_TIME_DESYNC
					alertsList.addAll(getClusterTimeDesyncAlert(clusterName));
				}
				//configurable QUEUE_UTILIZATION
				alertsList.addAll(getQueueUtilisationAlert(clusterName,
						rmCommunicator));

				if(nonConfigurableAlerts.get(AlertType.QUEUE_CHILD_CAPACITY_OVERFLOW)) {
					//non-configurable QUEUE_CHILD_CAPACITY_OVERFLOW
					alertsList.addAll(getChildCapacityAlert(
							rmCommunicator));
				}
				if(nonConfigurableAlerts.get(AlertType.MAP_REDUCE_APP_FAILURE)) {
					//non-configurable MAP_REDUCE_APP_FAILURE 
					alertsList.addAll(getApplicationFailedAlert(
							rmCommunicator, oldTime, newTime));
				}
				
				//non-configurable 
				alertsList.addAll(getEffectiveUtlilzationAlert(
						cluster.getClusterName()));

				if(nonConfigurableAlerts.get(AlertType.YARN_PROPERTY_CHECK)) {
					//non-configurable YARN_PROPERTY_CHECK
					alertsList.addAll(checkYarnPropertySetCorrectly(cluster));
				}
				
			}
			List<AlertInfo> nmDownAlerts = null;
			if (nonConfigurableAlerts.get(AlertType.HADOOP_DAEMON_DOWN)) {
				//non-configurable HADOOP_DAEMON_DOWN : Check Node Managers
				nmDownAlerts = getNodeManagerDownAlert(cluster,rmCommunicator);
				alertsList.addAll(nmDownAlerts);
			}
			if( nonConfigurableAlerts.get(AlertType.RESOURCE_UTILIZATION_CHECK)
					&& nmDownAlerts.size() != cluster.getWorkers().getHosts().size()) {
				//non-configurable RESOURCE_UTILIZATION_CHECK
				alertsList.addAll(getResourceUtilizationAlert(cluster, nmDownAlerts));
			}

		} catch (Exception e) {

		}
		return alertsList;
	}
	
	/**
	 * Gets the data node down alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the data node down alert
	 */
	@Override
	public List<AlertInfo> getDataNodeDownAlert(Cluster cluster) {
		List<String> workers = cluster.getWorkers().getHosts();
		List<AlertInfo> alertList = new ArrayList<AlertInfo>();
		for (String worker : workers) {
			String dataNodeInstance = null;
			dataNodeInstance = RemotingUtil.getDaemonProcessId(cluster, worker, JMXDeamons.DATA_NODE.toString());
			if (dataNodeInstance.isEmpty()) {
				AlertInfo alertInfo = new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, worker, AlertConstants.DATA_NODE + AlertConstants.DEAMON_WENT_DOWN,
						getDate());
				alertList.add(alertInfo);
			}
		}
		return alertList;
	}

	/**
	 * Gets the disk space usage alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the disk space usage alert
	 */
	@Override
	public List<AlertInfo> getDiskSpaceUsageAlert(Cluster cluster) {

		List<String> workers = cluster.getWorkers().getHosts();
		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		SeverityLevel severityLevel = AdminConfigurationUtil.getAlertConfiguration(cluster.getClusterName())
				.getConfigurableAlerts().get(AlertType.DISK_SPACE_UTILIZATION);

		List<AlertInfo> alertList = new ArrayList<AlertInfo>();
		Map<String, String> dataNodeJMXStats = null;

		int criticalLevel = severityLevel.getCriticalLevel();
		int warningLevel = severityLevel.getWarningLevel();

		String statName, diskCapacity, remainingCapacity;

		AlertInfo alertInfo = null;

		for (String worker : workers) {
			try {
				dataNodeJMXStats = jmxDump.getAllJMXStats(JMXDeamons.DATA_NODE, worker,
						cluster.getWorkers().getDataNodeJmxPort(), cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				jmxConnectionCacheClear(cluster, worker);
				continue;
			}

			for (Map.Entry<String, String> usableStat : dataNodeJMXStats.entrySet()) {

				statName = usableStat.getKey();

				if (statName.startsWith(AlertConstants.FS_DATASET_STATE) && statName.endsWith(AlertConstants.CAPACITY)) {

					diskCapacity = dataNodeJMXStats.get(statName);
					remainingCapacity = dataNodeJMXStats.get(statName.replace(AlertConstants.CAPACITY, AlertConstants.REMAINING));

					if (diskCapacity != null && !diskCapacity.isEmpty() && remainingCapacity != null
							&& !remainingCapacity.isEmpty()) {
						Double diskSpaceUsed = ((Double.parseDouble(diskCapacity)
								- Double.parseDouble(remainingCapacity)) / Double.parseDouble(diskCapacity) * 100);

						if (diskSpaceUsed >= criticalLevel) {
							alertInfo = new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, worker,
									getDiskStorageId(statName) + AlertConstants.DISKSPACE_ALERT_MESSAGE, getDate());
							alertList.add(alertInfo);
						} else if ((diskSpaceUsed < criticalLevel) && (diskSpaceUsed >= warningLevel)) {
							alertInfo = new AlertInfo(ExtendedConstants.WARNING_LEVEL, worker,
									getDiskStorageId(statName) + AlertConstants.DISKSPACE_ALERT_MESSAGE, getDate());
							alertList.add(alertInfo);
						}
					}
				}
			}
		}
		return alertList;
	}

	private String getDiskStorageId(String statName) {
		String[] splits = statName.split(AlertConstants.FS_DATASET_STATE2);
		String id = null;
		if (splits.length == 2) {
			String lastSplit = splits[1];
			id = lastSplit.substring(0, lastSplit.indexOf(AlertConstants.CAPACITY));
		}

		if (id == null || id.isEmpty() || id.equals(AlertConstants.NULL)) {
			return Constants.EMPTY_STRING;
		}
		return AlertConstants.STORAGE_ID + id + AlertConstants.CLOSING_BRACKET;
	}

	/**
	 * Gets the data node volume failure alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the data node volume failure alert
	 */
	@Override
	public List<AlertInfo> getDataNodeVolumeFailureAlert(Cluster cluster) {

		List<String> workers = cluster.getWorkers().getHosts();
		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		List<AlertInfo> alertList = new ArrayList<AlertInfo>();
		Map<String, String> dataNodeJMXStats = null;

		for (String worker : workers) {
			try {
				dataNodeJMXStats = jmxDump.getAllJMXStats(JMXDeamons.DATA_NODE, worker,
						cluster.getWorkers().getDataNodeJmxPort(), cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				jmxConnectionCacheClear(cluster, worker);
				continue;
			}

			if (dataNodeJMXStats != null) {
				Set<String> keys = dataNodeJMXStats.keySet();
				for (String key : keys) {
					if (key.startsWith(AlertConstants.FS_DATASET_STATE) && key.endsWith(AlertConstants.NUM_FAILED_VOLUMES)) {
						String storageId = Constants.EMPTY_STRING;
						String[] splits = key.split(AlertConstants.FS_DATASET_STATE2);
						if (splits.length == 2) {
							String lastSplit = splits[1];
							storageId = lastSplit.substring(0, lastSplit.indexOf(AlertConstants.NUM_FAILED_VOLUMES));
						}
						if (storageId.isEmpty() || storageId.equals(AlertConstants.NULL)) {
							storageId = AlertConstants.N_A;
						}
						Double numFailedVolumes = Double.parseDouble(dataNodeJMXStats.get(key));
						if (numFailedVolumes > 0) {
							AlertInfo alertInfo = new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, worker,
									AlertConstants.DATA_NODE_WITH_STORAGE_ID + storageId + AlertConstants.HAS + numFailedVolumes + AlertConstants.VOLUME_FAILURE_S,
									getDate());
							alertList.add(alertInfo);
						}

					}
				}
			}
		}
		return alertList;
	}

	/**
	 * Jmx connection cache clear.
	 *
	 * @param cluster
	 *            the cluster
	 * @param worker
	 *            the worker
	 */
	public void jmxConnectionCacheClear(Cluster cluster, String worker) {
		JMXConnectorCache cache = JMXConnectorCache.getJMXCacheInstance();
		JMXServiceURL url;
		try {
			url = new JMXServiceURL(ProfilerConstants.JMX_URL_PREFIX + worker + Constants.COLON
					+ cluster.getWorkers().getDataNodeJmxPort() + ProfilerConstants.JMX_URL_POSTFIX);
			cache.remove(url);
			cache.clear();
			JMXConnectorInstance.nullifyConnector();
		} catch (MalformedURLException e) {
			LOGGER.info(JumbuneRuntimeException.throwException(e.getStackTrace()));
		}
	}

	/**
	 * Gets the under replicated block alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the under replicated block alert
	 */
	@Override
	public List<AlertInfo> getUnderReplicatedBlockAlert(Cluster cluster) {

		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		List<AlertInfo> alertList = new ArrayList<AlertInfo>(1);

		SeverityLevel severityLevel = AdminConfigurationUtil.getAlertConfiguration(cluster.getClusterName())
				.getConfigurableAlerts().get(AlertType.UNDER_REPLICATED_BLOCKS);

		Map<String, String> nameNodeStats = null;

		int criticalLevel = severityLevel.getCriticalLevel();
		int warningLevel = severityLevel.getWarningLevel();

		try {
			nameNodeStats = jmxDump.getAllJMXStats(JMXDeamons.NAME_NODE, cluster.getNameNode(),
					cluster.getNameNodes().getNameNodeJmxPort(), cluster.isJmxPluginEnabled());
		} catch (Exception e) {
			jmxConnectionCacheClear(cluster, cluster.getNameNode());
			return alertList;
		}
		if (nameNodeStats != null) {
			Double blockTotal = Double.parseDouble(nameNodeStats.get(AlertConstants.FS_NAMESYSTEM_BLOCKS_TOTAL));
			Double underReplicatedBlock = Double.parseDouble(nameNodeStats.get(AlertConstants.FS_NAMESYSTEM_UNDER_REPLICATED_BLOCKS));

			if (blockTotal > 0) {
				Double underReplicatedBlockPercent = (underReplicatedBlock / blockTotal) * 100;
				if (underReplicatedBlockPercent >= criticalLevel) {
					AlertInfo alertInfo = new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, ExtendedConstants.ALL,
							underReplicatedBlock + AlertConstants.UNDER_REPLICATED_BLOCK_MESSAGE, getDate());
					alertList.add(alertInfo);
				} else if (underReplicatedBlockPercent >= warningLevel && underReplicatedBlockPercent < criticalLevel) {
					AlertInfo alertInfo = new AlertInfo(ExtendedConstants.WARNING_LEVEL, ExtendedConstants.ALL,
							underReplicatedBlock + AlertConstants.UNDER_REPLICATED_BLOCK_MESSAGE, getDate());
					alertList.add(alertInfo);
				}
			}

		}
		return alertList;
	}

	/**
	 * Gets the node down alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the node down alert
	 */
	@Override
	public List<AlertInfo> getNodeDownAlert(Cluster cluster) {

		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		List<AlertInfo> alertList = new ArrayList<AlertInfo>(1);

		Map<String, String> nameNodeStats = null;

		try {
			nameNodeStats = jmxDump.getAllJMXStats(JMXDeamons.NAME_NODE, cluster.getNameNode(),
					cluster.getNameNodes().getNameNodeJmxPort(), cluster.isJmxPluginEnabled());
		} catch (Exception e) {
			jmxConnectionCacheClear(cluster, cluster.getNameNode());
		}
		if (nameNodeStats != null) {
			Integer nodeDown = Integer.parseInt(nameNodeStats.get(AlertConstants.FS_NAMESYSTEM_STATE_NUM_DEAD_DATA_NODES));
			if (nodeDown > 0) {
				AlertInfo alertInfo = new AlertInfo(ExtendedConstants.WARNING_LEVEL, ExtendedConstants.HYPHEN,
						nodeDown + AlertConstants.NODE_S_UNAVAILABLE, getDate());
				alertList.add(alertInfo);
			}

		}
		return alertList;
	}

	/**
	 * Gets the HDFS space usage alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the HDFS space usage alert
	 */
	@Override
	public List<AlertInfo> getHDFSSpaceUsageAlert(Cluster cluster) {

		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		List<AlertInfo> alertList = new ArrayList<AlertInfo>(2);
		SeverityLevel severityLevel = AdminConfigurationUtil.getAlertConfiguration(cluster.getClusterName())
				.getConfigurableAlerts().get(AlertType.HDFS_UTILIZATION);

		Map<String, String> nameNodeStats = null;

		int hdfsCriticalLevel = severityLevel.getCriticalLevel();
		int hdfsWarningLevel = severityLevel.getWarningLevel();

		try {
			nameNodeStats = jmxDump.getAllJMXStats(JMXDeamons.NAME_NODE, cluster.getNameNode(),
					cluster.getNameNodes().getNameNodeJmxPort(), cluster.isJmxPluginEnabled());
		} catch (Exception e) {
			jmxConnectionCacheClear(cluster, cluster.getNameNode());
			return alertList;
		}
		if (nameNodeStats != null) {

			Double hdfsCapacityUtilization = (Double.parseDouble(nameNodeStats.get(AlertConstants.FS_NAMESYSTEM_CAPACITY_USED))
					/ Double.parseDouble(nameNodeStats.get(AlertConstants.FS_NAMESYSTEM_CAPACITY_TOTAL))) * 100;

			if (hdfsCapacityUtilization >= hdfsCriticalLevel) {
				AlertInfo alertInfo = new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, ExtendedConstants.ALL,
						AlertConstants.HDFS_SPACE_USAGE_MESSAGE, getDate());
				alertList.add(alertInfo);
			} else if (hdfsCapacityUtilization >= hdfsWarningLevel && hdfsCapacityUtilization < hdfsCriticalLevel) {
				AlertInfo alertInfo = new AlertInfo(ExtendedConstants.WARNING_LEVEL, ExtendedConstants.ALL,
						AlertConstants.HDFS_SPACE_USAGE_MESSAGE, getDate());
				alertList.add(alertInfo);
			}
		}
		return alertList;
	}

	/**
	 * Gets the name node down alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the name node down alert
	 */
	@Override
	public AlertInfo getNameNodeDownAlert(Cluster cluster) {

		String nameNodeInstance = null;
		nameNodeInstance = RemotingUtil.getDaemonProcessId(cluster, cluster.getNameNode(),
				JMXDeamons.NAME_NODE.toString());
		if (nameNodeInstance.isEmpty()) {
			return new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, cluster.getNameNode(), AlertConstants.NAME_NODE + AlertConstants.DEAMON_WENT_DOWN,
					getDate());
		}

		return null;
	}

	/**
	 * @param cluster
	 * @param hdfsPaths : list of hdfs paths to be checked
	 * @return : the alert if the maximum files in a hdfs directory crosses 90 % of threshold value
	 */
	@Override
	public List<AlertInfo> getHDFSMaxFilesInDirAlert(Cluster cluster, List<String> hdfsPaths){
		List<AlertInfo> alerts = new ArrayList<>(hdfsPaths.size());
		try {
			String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(cluster);
			String hdfsSitePath = RemotingUtil.copyAndGetConfigurationFilePath(cluster, hadoopConfDir, Constants.HDFS_SITE_XML);
			String confDfsLimit = RemotingUtil.parseConfiguration(hdfsSitePath + File.separator + Constants.HDFS_SITE_XML, AlertConstants.DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY);
			double dfsLimit = 0 ;
			if(confDfsLimit == null || confDfsLimit.isEmpty()){
				dfsLimit = 1048576;
			}else{
				dfsLimit = Double.parseDouble(confDfsLimit);
			}
			String fileCountCommand = null;
			String commandResponse = null ;
			String line = null;
			String[] splits = null ;
			for (String hdfsPath : hdfsPaths) {
				fileCountCommand = Constants.HADOOP_HOME  + Constants.HADOOP_COMMAND + Constants.SPACE+ "fs" + Constants.SPACE + "-count" + Constants.SPACE + hdfsPath ;
				commandResponse = RemotingUtil.executeCommand(cluster, fileCountCommand);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new ByteArrayInputStream(commandResponse.getBytes())));
				if(hdfsPath.endsWith(File.separator)){
					hdfsPath = hdfsPath.substring(0, hdfsPath.length()-1);
				}
				try {
					while ((line = reader.readLine()) != null) {
							if(line.contains(hdfsPath)){
							splits = line.split(Constants.SPACE_REGEX);
							break;
						}
					}
				} catch (Exception e) {
					LOGGER.error("Unable to fetch max files in a hdfs directory alert due to: ",e);
				}finally {
					if(reader != null){
						reader.close();
					}
				}
				double dfsLimitPer = (Double.parseDouble(splits[1])/dfsLimit) * 100 ;
				if( dfsLimitPer >= 85 ){
					AlertInfo alertInfo = new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, ExtendedConstants.ALL, "The directory count inside HDFS folder ["+ hdfsPath + "]  has reached 85% of the threshold value, kindly clean to avoid MaxDirectoryItemsExceeded issue", getDate());
					alerts.add(alertInfo);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to fetch max files in a hdfs directory alert due to: ",e);
		}
		return alerts;
		
	}

	// dfs.namenode.name.dir alert
	/**
	 * @param cluster
	 * @return alert if the namenode dfs directory(containing the edit logs and fsimage location) exceeds the 90% threshold value
	 */
	@Override
	public List<AlertInfo> getFragmenedFilesAlert(Cluster cluster){
		List<AlertInfo> alerts = new ArrayList<AlertInfo>();
		try {
			String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(cluster);
			String hdfsSitePath = RemotingUtil.copyAndGetConfigurationFilePath(cluster, hadoopConfDir, Constants.HDFS_SITE_XML);
			String nameNodeDfsPath = RemotingUtil.parseConfiguration(hdfsSitePath + File.separator + Constants.HDFS_SITE_XML, AlertConstants.NAMENODE_DATA_DIRECTORY);
			if(nameNodeDfsPath == null || nameNodeDfsPath.isEmpty()){
				String coreSitePath = RemotingUtil.copyAndGetConfigurationFilePath(cluster, hadoopConfDir, Constants.CORE_SITE_XML);
				String hadoopTmpPath = RemotingUtil.parseConfiguration(hdfsSitePath + File.separator + Constants.CORE_SITE_XML, AlertConstants.NAMENODE_TEMP_DIRECTORY);
				nameNodeDfsPath = hadoopTmpPath + "/dfs/name" ;
			}
			if(nameNodeDfsPath.contains(Constants.COMMA)){
				String[] nStrings = nameNodeDfsPath.split(Constants.COMMA);
				for (String nameNodePath : nStrings) {
					if(nameNodePath.startsWith("file://")){
						nameNodePath = nameNodePath.replaceAll("file://", Constants.EMPTY_STRING);
					}
					getFragmFileAlerts(cluster, alerts, nameNodePath);
				}
			}else{
				if(nameNodeDfsPath.startsWith("file://")){
					nameNodeDfsPath = nameNodeDfsPath.replaceAll("file://", Constants.EMPTY_STRING);
				}
				getFragmFileAlerts(cluster, alerts, nameNodeDfsPath);
			}
		} catch (Exception e) {
			LOGGER.error("Unable to get the fragmented files alert",e);
		}
		
	
		
		return alerts;
		
	}

	private void getFragmFileAlerts(Cluster cluster, List<AlertInfo> alerts, String nameNodeDfsPath) {
		String commandResponse;
		StringBuilder fileCountCmd = new StringBuilder().append("find").append(Constants.SPACE).append(nameNodeDfsPath)
				.append(Constants.SPACE).append("-type").append(Constants.SPACE).append("f").append(Constants.SPACE)
				.append("|").append(Constants.SPACE).append("wc").append(Constants.SPACE).append("-l");

		commandResponse = RemotingUtil.executeCommand(cluster, fileCountCmd.toString());

		if (commandResponse.matches("-?\\d+(\\.\\d+)?")) {
			double dfsLimitPer = (Double.parseDouble(commandResponse)/1048576) * 100 ;
			if( dfsLimitPer >= 85 ){
				AlertInfo alertInfo = new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, cluster.getNameNode(),  "The threshold for fragmented files has reached 85%, kindly clean[" + nameNodeDfsPath + "] to avoid data insertion issues", getDate());
				alerts.add(alertInfo);
			}
		}

	}
	
	@Override
	public List <AlertInfo> getClusterTimeDesyncAlert(String clusterName) { 
		List<AlertInfo> queueAlert = new ArrayList<AlertInfo>();
		StringBuffer queueJsonFile = new StringBuffer().append(JumbuneInfo.getHome()).append(File.separator).append(ExtendedConstants.JOB_JARS_LOC)
				.append(clusterName).append(File.separator).append(clusterName).append(ExtendedConstants.QUEUE).append(JSON);
		File queueDataFile = new File(queueJsonFile.toString());

		try {
			Map <String,Map<String,String>> allQueue = new HashMap <String, Map<String,String>>(); 
			if(queueDataFile.exists()){
				String queueDataJsonReader;
				try {
					queueDataJsonReader = ConfigurationUtil.readFileData(queueDataFile.toString());
					allQueue = Constants.gson.fromJson(queueDataJsonReader, type);
				} catch (IOException e) {
					LOGGER.error("Error while reading queue json file : ", e);
				}
				for(Map.Entry<String, Map<String,String>> queueData : allQueue.entrySet()){
					Map<String, String>queueInformation = queueData.getValue();
					Long waitingTime = Long.parseLong(queueInformation.get("WaitingTime"));
					if(waitingTime <0){
						AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.ALL,"Cluster node's time needs to be synchronized", getDate());
						queueAlert.add(alertInfo);
						break;
					}
				}
			}
		} catch (JsonSyntaxException | NumberFormatException e) {
			LOGGER.error("Unable to getClusterTimeDesyncAlert : ",e);
		}
		return queueAlert;
	}
	
	
	/**
	 * Gets the queue utilisation alert.
	 * @param rmCommunicator 
	 * 
	 * @return the queue utilisation alert
	 * @throws YarnException
	 *             the yarn exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Override
	public List<AlertInfo> getQueueUtilisationAlert(String clusterName, RMCommunicator rmCommunicator) {
		
		List<AlertInfo> queueAlert = new ArrayList<AlertInfo>();
		AlertConfiguration alertConfiguration = AdminConfigurationUtil.getAlertConfiguration(clusterName);
		Map<String, SeverityLevel> mapForSeverity = alertConfiguration.getIndividualQueueAlerts();

		SeverityLevel severityLevelForIndivQAlertsGlobal = alertConfiguration
				.getConfigurableAlerts().get(AlertType.QUEUE_UTILIZATION);

		int criticalLevelGlobal = severityLevelForIndivQAlertsGlobal
				.getCriticalLevel();
		int warningLevelGlobal = severityLevelForIndivQAlertsGlobal
				.getWarningLevel();	

		List<QueueInfo> list = new ArrayList<QueueInfo>();
		
		try {
			addQueue(rmCommunicator.getQueueInfo(ExtendedConstants.ROOT), list);
			
			float finalCapacity;
			for (QueueInfo temp : list) {
				if(!temp.getQueueName().equalsIgnoreCase(ExtendedConstants.ROOT)){
				SeverityLevel severityLevelForIndivQAlerts = null;
				int criticalLevel = 0;
				int warningLevel = 0;

				if (mapForSeverity.containsKey(temp.getQueueName())) {					
					severityLevelForIndivQAlerts = mapForSeverity.get(temp
							.getQueueName());
					criticalLevel = severityLevelForIndivQAlerts
							.getCriticalLevel();
					warningLevel = severityLevelForIndivQAlerts
							.getWarningLevel();					
				} else {					
					criticalLevel = criticalLevelGlobal;
					warningLevel = warningLevelGlobal;
				}
				finalCapacity  = temp.getCurrentCapacity() > 0.0 ? temp.getCurrentCapacity() : temp.getCapacity();
				if ((finalCapacity * 100) >= criticalLevel) {
					AlertInfo alertInfo = new AlertInfo(ExtendedConstants.CRITICAL_LEVEL,
							ExtendedConstants.HYPHEN, "Queue "
									+ temp.getQueueName() + ":"
									+ ExtendedConstants.QUEUE_LIMIT, getDate());
					queueAlert.add(alertInfo);					
				} else if (((finalCapacity * 100) >= warningLevel)
						&& ((temp.getCurrentCapacity() * 100) < criticalLevel)) {
					AlertInfo alertInfo = new AlertInfo(ExtendedConstants.WARNING_LEVEL,
							ExtendedConstants.HYPHEN, "Queue "
									+ temp.getQueueName() + ":"
									+ ExtendedConstants.QUEUE_LIMIT, getDate());
					queueAlert.add(alertInfo);					
				}
			}}
		} catch (YarnException | IOException e) {
			LOGGER.error("Unable to getQueueUtilisationAlert due to : ",e);
		}

		return queueAlert;
	}
	
	private void addQueue(QueueInfo queueInfo, List<QueueInfo> list) {
		list.add(queueInfo);
		if (queueInfo.getChildQueues() != null) {
			for (QueueInfo childQueue : queueInfo.getChildQueues()) {
				addQueue(childQueue, list);
			}
		}
	}

	/**
   	 * Traverse queues bf.
   	 *
   	 * @param queueName the queue name
   	 * @param queueAlert the queue alert
	 * @param rmCommunicator 
   	 * @throws YarnException the yarn exception
   	 * @throws IOException Signals that an I/O exception has occurred.
   	 */
   	private void traverseQueuesBF(String queueName, List<AlertInfo> queueAlert, RMCommunicator rmCommunicator) throws YarnException, IOException {
		   final String parentQueue = queueName;
		   QueueInfo qi = rmCommunicator.getQueueInfo(queueName);
	       List<String> queueNames = new ArrayList<String>(5); 
		   float childrenCapacity = 0.0f;
		   for (QueueInfo info : qi.getChildQueues()) {
			   queueNames.add(info.getQueueName());
			   childrenCapacity += info.getCapacity();
		   }
			if(childrenCapacity > 1.0){
				AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"Queue "+parentQueue+":"+"child capacity exceeded 100 percent", getDate());
				queueAlert.add(alertInfo);
			}
		   for(String name : queueNames) {
			   traverseQueuesBF(name, queueAlert, rmCommunicator);
		   }
		   
	   }
	
	
	/**
	 * Gets the child capacity alert.
	 *
	 * @return the child capacity alert
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
   	@Override
	public List <AlertInfo> getChildCapacityAlert(RMCommunicator rmCommunicator) { 
		List<AlertInfo> queueAlert = new ArrayList<>();
		try {
			traverseQueuesBF(ExtendedConstants.ROOT, queueAlert, rmCommunicator);
		} catch (YarnException | IOException e) {
		LOGGER.error("Unable to get child queue capacity alert due to: ",e);
		}
		return queueAlert;
	}
	
	/**
	 * Gets the application failed alert.
	 *
	 * @return the application failed alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws YarnException the yarn exception
	 */
   	@Override
	public List <AlertInfo> getApplicationFailedAlert(RMCommunicator rmCommunicator,
			long finishFrom, long finishTo) {
		
		List<AlertInfo> appFailedAlert = new ArrayList<AlertInfo>();
		List<ApplicationReport> applicationReport = null;

		try {
			applicationReport = rmCommunicator.getApplications(
					null, null, null, null, null, EnumSet.of(
							YarnApplicationState.FINISHED, YarnApplicationState.FAILED), finishFrom, finishTo, null);
		} catch (YarnException | IOException e) {
			LOGGER.error("Unable to get failed applications from yarn api due to : ",e);
		}
		for (ApplicationReport ar:applicationReport) {
			if (ar.getFinalApplicationStatus().equals(FinalApplicationStatus.FAILED)) {
					AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL, ExtendedConstants.HYPHEN,
							ar.getApplicationId().toString() +FAILED, getDate());
					alertInfo.setSkipOccuringSince(true);
					appFailedAlert.add(alertInfo);
			}
		}

		return appFailedAlert;
	}


	/**
	 * Gets the container utilization alert.
	 *
	 * @param cluster the cluster
	 * @param rmCommunicator 
	 * @return the container utilization alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws YarnException the yarn exception
	 */
	public List <AlertInfo> getContainerUtilizationAlert(Cluster cluster, RMCommunicator rmCommunicator){

		List<AlertInfo> containerAlert = new ArrayList<AlertInfo>();
		float containerBasedonVcores, containerBasedonMemory, container;

		List<NodeReport> nodeReports;
		try {
			nodeReports = rmCommunicator.getNodeReports();
		
		for(NodeReport nr:nodeReports){

			if (nr.getNumContainers() > 0){
				containerBasedonVcores = nr.getCapability().getVirtualCores()/getMinimumParameterMandatoryForContainer(ExtendedConstants.YARN_CONTAINER_MINIMUM_VCORE,1,cluster);
				containerBasedonMemory = nr.getCapability().getMemory()/getMinimumParameterMandatoryForContainer(ExtendedConstants.YARN_CONTAINER_MINIMUM_MEMORY,1,cluster);
				container = Math.min(containerBasedonVcores, containerBasedonMemory);
				if (((nr.getNumContainers()/container)*100) > 80){
					String host = nr.getNodeId().getHost();
					if(!host.contains(".")){
						host = InetAddress.getByName(host).getHostAddress();
					}
					AlertInfo alertInfo = new AlertInfo(ExtendedConstants.CRITICAL_LEVEL,host,"Container capacity is over utilized", getDate() );
					containerAlert.add(alertInfo);
				}
			}
		}} catch (YarnException | IOException e) {
			LOGGER.error("Unable to get container utilization alert due to : ",e);
		}

		return containerAlert;
	}


	
	@Override
	public List<AlertInfo> getEffectiveUtlilzationAlert(String clusterName){
		List<AlertInfo> effectiveUtilisationAlert = new ArrayList<AlertInfo>(4);
		String[] jobIdList = getJobList(clusterName);
		if (jobIdList == null) {
			return effectiveUtilisationAlert;
		}
		for(String jobId:jobIdList){
			StringBuffer capUtilJsonFileName = new StringBuffer().append(JumbuneInfo.getHome()).append(File.separator).append(ExtendedConstants.JOB_JARS_LOC)
					.append(clusterName).append(CLUSTER_PROFILING).append(jobId).append(UTILIZATION_STATS).append(jobId).append(EFFECTIVE_MAX_UTILISATION).append(JSON);
			StringBuffer capUtilAlertJsonFileName = new StringBuffer().append(JumbuneInfo.getHome()).append(File.separator).append(ExtendedConstants.JOB_JARS_LOC)
					.append(clusterName).append(CLUSTER_PROFILING).append(jobId).append(UTILIZATION_STATS).append(jobId).append(EFFECTIVE_MAX_UTILISATION).append(ExtendedConstants.WARNING_LEVEL).append(JSON);

			File capUtilJsonFile = new File(capUtilJsonFileName.toString());
			File capUtilAlertJsonFile = new File(capUtilAlertJsonFileName.toString());
			try {
			if(capUtilJsonFile.exists()){
				if(!capUtilAlertJsonFile.exists()){
					
						Files.copy(capUtilJsonFile.toPath(), capUtilAlertJsonFile.toPath());
					
					String capUtilJsonReader = ConfigurationUtil.readFileData(capUtilJsonFileName.toString());
					EffCapUtilizationStats effCapUtilStats = new EffCapUtilizationStats();
					effCapUtilStats = Constants.gson.fromJson(capUtilJsonReader, EffCapUtilizationStats.class);
					boolean mapAlert = false, reduceAlert = false;
					if( effCapUtilStats.getUsedMaxMapMemory().floatValue() > 0){
						Float usedMapAlert = effCapUtilStats.getUsedMaxMapMemory().floatValue()/effCapUtilStats.getAllocatedMapMemory().floatValue();
						if(usedMapAlert<0.5){
							mapAlert = true;
						}
					}
					if(effCapUtilStats.getUsedMaxReduceMemory().floatValue() > 0){
						Float usedReduceAlert = effCapUtilStats.getUsedMaxReduceMemory().floatValue()/effCapUtilStats.getAllocatedReduceMemory().floatValue();
						if(usedReduceAlert<0.5){
							reduceAlert = true;
						}
					}
					String str = null;
					if (mapAlert && reduceAlert) {
						str = "Mapper & Reducer";
					} else if (mapAlert) {
						str = "Mapper";
					} else if (reduceAlert) {
						str = "Reducer";
					}
					if (mapAlert || reduceAlert) {
						AlertInfo alertInfo = new AlertInfo(ExtendedConstants.WARNING_LEVEL, ExtendedConstants.HYPHEN, 
								"Found excessive allocation of resource for " + str +
								", unused resource were over 50% in the containers for Job ("+jobId
								+"), recommended to tune the allocation to effectively utilize cluster capability.",getDate());
						alertInfo.setJobId(jobId);
						effectiveUtilisationAlert.add(alertInfo);
					}
				}
			}} catch (IOException e) {
				LOGGER.error("Unable to getEffectiveUtlilzationAlert due to : ",e);
			}
		}
		return effectiveUtilisationAlert;
	}


	
	@Override
	public AlertInfo getResourceManagerDownAlert(Cluster cluster){

		try {
			String resourceManagerInstance = null;
			resourceManagerInstance = RemotingUtil.getDaemonProcessId(cluster, cluster.getResourceManager(),
					JMXDeamons.RESOURCE_MANAGER.toString());
			if (resourceManagerInstance.isEmpty()) {
				return new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, cluster.getResourceManager(),
						AlertConstants.RESOURCE_MANAGER + DEAMON_WENT_DOWN, getDate());
			}
		} catch (Exception e) {
			LOGGER.error("Unable to getResourceManagerDownAlert due to :  ",e);
		}
		return null;

	}


	
	@Override
	public AlertInfo getHistoryServerDownAlert(Cluster cluster){
		
		try {
			String historyServerInstance = null;
			historyServerInstance = RemotingUtil.getDaemonProcessId(cluster, cluster.getHistoryServer(),
					JMXDeamons.JOB_HISTORY_SERVER.toString());
			if (historyServerInstance.isEmpty()) {
				return new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, cluster.getHistoryServer(),
						AlertConstants.HISTORY_SERVER + DEAMON_WENT_DOWN, getDate());
			}
		} catch (Exception e) {
			LOGGER.error("Unable to getHistoryServerDownAlert due to : ",e);
		}
		return null;

	}
	

	
	@Override
	public List<AlertInfo> getResourceUtilizationAlert(Cluster cluster, List<AlertInfo> nmAlerts) {
		
		Map<String, Object> nodeManagerOSStats = null;

		List<String> workers = cluster.getWorkers().getHosts();
		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		List<AlertInfo> alertList = new ArrayList<AlertInfo>();
		String response= null;

		for(String worker:workers){
			if (isNodeManagerDown(worker, nmAlerts)) {
				continue;
			}
			LOGGER.debug("Going to check for ResourceUtilizationAlert");
			try {
				nodeManagerOSStats = jmxDump.getOSJMXStats(JMXDeamons.NODE_MANAGER, worker, cluster.getWorkers().getTaskExecutorJmxPort(), cluster.isJmxPluginEnabled());
				response = jmxDump.getFreeMemoryResponse(cluster, worker).trim();
				String[] memory = response.split("\\s+");
				Double totalPhysicalMemorySize = Double.parseDouble(memory[1]);
				Double freePhysicalMemorySize = Double.parseDouble(memory[3]);
				Double cpuLoad = Double.parseDouble(nodeManagerOSStats.get("SystemCpuLoad").toString());
				
				Double freeMemory = ((totalPhysicalMemorySize-freePhysicalMemorySize)/totalPhysicalMemorySize);		
				if(cpuLoad > 0.8){
					AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL,worker,"CPU utilization exhausted threshold of 80%", getDate());
					alertList.add(alertInfo);			
				}
				if(freeMemory > 0.90){
					AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL,worker,"Memory utilization exhausted threshold of 90%", getDate());
					alertList.add(alertInfo);			
				}	
			} catch(Exception e) {
				LOGGER.error( "Error while retrieving Resource Utilization Alert",e);
			}
		}
		return alertList;
	}
	
	private boolean isNodeManagerDown(String worker, List<AlertInfo> nmAlerts) {
		if (nmAlerts == null) {
			return false;
		}
		for (AlertInfo alertInfo: nmAlerts) {
			if (worker.equals(alertInfo.getNodeIP())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<AlertInfo> getNodeManagerDownAlert(Cluster cluster, RMCommunicator rmCommunicator) {
		List<String> workers = cluster.getWorkers().getHosts();
		List<AlertInfo> alertList = new ArrayList<>(workers.size());
		try {
			for (NodeReport nodeReport : rmCommunicator.getNodeReports()) {

				if (nodeReport.getNodeState().toString().equalsIgnoreCase("LOST")) {
					AlertInfo alertInfo = new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, nodeReport.getNodeId().getHost(),
							AlertConstants.NODE_MANAGER + DEAMON_WENT_DOWN, getDate());
					alertList.add(alertInfo);
				}

			}
		} catch (YarnException | IOException e) {
			LOGGER.error("Unable to get node report details",e);		
		}
		return alertList;
	}
	
	@Override
	public List<AlertInfo> getNodeUnhealthyAlert(Cluster cluster, RMCommunicator rmCommunicator){

		List<NodeReport> nodeReportList;
		List<AlertInfo> alertList = new ArrayList<AlertInfo>();
		try {
			nodeReportList = rmCommunicator.getNodeReports();

		
			for (NodeReport nodeReport : nodeReportList) {
				String host = InetAddress.getByName(nodeReport.getNodeId().getHost()).getHostAddress();
				if (nodeReport.getNodeState().toString().equals("UNHEALTHY")) {
					AlertInfo alertInfo = new AlertInfo(ExtendedConstants.CRITICAL_LEVEL, host,
							" NodeManager" + DEAMON_IS_UNHEALTHY, getDate());
					alertList.add(alertInfo);
				}

			}
		} catch (YarnException | IOException e) {
			LOGGER.error("Unable to get unhealthy nodes from the api due to: ",e);		
		}
		return alertList;
	}

	
	@Override
	public List<AlertInfo> checkYarnPropertySetCorrectly (Cluster cluster){
		List<AlertInfo> alertList = new ArrayList<AlertInfo>();

		Configuration configuration = new Configuration();
		String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(cluster);
		String localConfFilePath = ConfigurationUtil.getLocalConfigurationFilePath(cluster)+ExtendedConstants.YARN_SITE_XML;
		File file = new File(localConfFilePath);
		if(!file.exists()){
			String filePath = RemotingUtil.addHadoopResource(configuration, cluster, hadoopConfDir, ExtendedConstants.YARN_SITE_XML);
			configuration.addResource(new Path(filePath));
		}
		configuration.addResource(new Path(localConfFilePath));
		Double minimumAllocatedMemory= Double.parseDouble(configuration.get("yarn.scheduler.minimum-allocation-mb"));
		Double maxVcores = Double.parseDouble(configuration.get("yarn.scheduler.maximum-allocation-vcores"));
		Double minVcores = Double.parseDouble(configuration.get("yarn.scheduler.minimum-allocation-vcores"));
		Double maxAllocatedMemory = Double.parseDouble(configuration.get("yarn.scheduler.maximum-allocation-mb"));
		Double nodeManagerCore = Double.parseDouble(configuration.get("yarn.nodemanager.resource.cpu-vcores"));
		Double nodeManagerMemory = Double.parseDouble(configuration.get("yarn.nodemanager.resource.memory-mb"));

		
		if(maxVcores < minVcores){
			file.delete();
			AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-vcores is greater than yarn.scheduler.maximum-allocation-vcores, change property value in yarn-site.xml", getDate());
			alertList.add(alertInfo);
		}
		if(maxAllocatedMemory < minimumAllocatedMemory){
			file.delete();
			AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-mb is greater than yarn.scheduler.maximum-allocation-mb, change property value in yarn-site.xml", getDate());
			alertList.add(alertInfo);
		}	
		if(minVcores < 1){
			file.delete();
			AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-vcores value is less than one, change property value in yarn-site.xml", getDate());
			alertList.add(alertInfo);
		}
		if(nodeManagerCore < minVcores){
			file.delete();
			AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-vcores is greater than yarn.nodemanager.resource.cpu-vcores, change property value in yarn-site.xml", getDate());
			alertList.add(alertInfo);
		}
		if(maxVcores < 1){
			file.delete();
			AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-vcores value is less than one,change property value in yarn-site.xml", getDate());
			alertList.add(alertInfo);
		}
		if(nodeManagerMemory < minimumAllocatedMemory){
			file.delete();
			AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-mb is greater than yarn.nodemanager.resource.memory-mb,change property value in yarn-site.xml", getDate());
			alertList.add(alertInfo);
		}
		if(minimumAllocatedMemory < 1024){
			file.delete();
			AlertInfo alertInfo = new AlertInfo (ExtendedConstants.WARNING_LEVEL,ExtendedConstants.HYPHEN,"yarn.scheduler.minimum-allocation-mb value is less than 1024,change property value in yarn-site.xml", getDate());
			alertList.add(alertInfo);
		}
		return alertList;
	}

	/**
	 * Gets the all completed job id.
	 *  
	 */
	private String[] getJobList(String clusterName) {
        StringBuffer jobIdsPath = new StringBuffer().append(JumbuneInfo.getHome()).append(File.separator).append(Constants.JOB_JARS_LOC)
                        .append(clusterName).append(CLUSTER_PROFILING);
        File f = new File(jobIdsPath.toString());
        String[] jobIds = f.list();
        return jobIds;
     }


	/**
	 * Gets the minimum parameter mandatory for container.
	 *
	 * @param parameter the parameter
	 * @param value the value
	 * @param cluster the cluster
	 * @return the minimum parameter mandatory for container
	 */
	private int getMinimumParameterMandatoryForContainer(String parameter , int value, Cluster cluster) {
		Configuration c = new Configuration();
		String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(cluster);
		String localConfFilePath = ConfigurationUtil.getLocalConfigurationFilePath(cluster)+ExtendedConstants.YARN_SITE_XML;
		File file = new File(localConfFilePath);
		if(!file.exists()){
			String filePath = RemotingUtil.addHadoopResource(c, cluster, hadoopConfDir, ExtendedConstants.YARN_SITE_XML);
			c.addResource(new Path(filePath));
		}
		c.addResource(new Path(localConfFilePath));
		return c.getInt(parameter, value);
	}

	/**
	 * Gets the current time and date.
	 *
	 * @return the date
	 */
	private String getDate() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat(ExtendedConstants.TIME_FORMAT);
		return sdf.format(date);
	}

}
