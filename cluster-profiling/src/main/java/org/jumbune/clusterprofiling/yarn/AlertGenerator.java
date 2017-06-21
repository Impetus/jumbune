package org.jumbune.clusterprofiling.yarn;

import org.jumbune.common.alerts.AlertConstants;
import  org.jumbune.profiling.utils.ProfilerConstants;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.remote.JMXServiceURL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.utils.JMXConnectorCache;
import org.jumbune.profiling.utils.JMXConnectorInstance;
import org.jumbune.profiling.utils.ProfilerJMXDump;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.AlertType;
import org.jumbune.utils.conf.beans.SeverityLevel;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.reflect.TypeToken;
import org.jumbune.clusterprofiling.yarn.AlertGenerator;

import org.jumbune.common.beans.Alert;
import org.jumbune.common.utils.ExtendedConstants;

/**
 * The Class AlertGenerator.
 */
public class AlertGenerator {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(AlertGenerator.class);

	private static volatile AlertGenerator INSTANCE = null;

	private Type type;
	
	public static AlertGenerator getInstance() {
		if (INSTANCE == null) {
			synchronized (AlertGenerator.class) {
				if (INSTANCE == null) {
					INSTANCE = new AlertGenerator();
				}
			}
		}
		return INSTANCE;
	}
	
	private AlertGenerator() {

		this.type = new TypeToken<Map<String, Map<String, String>>>() {
		}.getType();
	}

	/**
	 * Gets the alerts.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the alerts
	 * @throws Exception
	 *             the exception
	 */
	@Deprecated
	public List<List<Alert>> getAlerts(Cluster cluster) throws Exception {
		List<List<Alert>> alertList = new ArrayList<List<Alert>>();
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		if (!Constants.MAPR.equalsIgnoreCase(hadoopDistribution)) {
			// configurable DISK_SPACE_UTILIZATION
			alertList.add(getDiskSpaceUsageAlert(cluster));
			// configurable UNDER_REPLICATED_BLOCKS
			alertList.add(getUnderReplicatedBlockAlert(cluster));
			// configurable HDFS_UTILIZATION
			alertList.add(getHDFSSpaceUsageAlert(cluster));
			// non-configurable DN_VOLUME_FAILURE_CHECK
			alertList.add(getDataNodeVolumeFailureAlert(cluster));
			// non-configurable HADOOP_DAEMON_DOWN
			alertList.add(getDataNodeDownAlert(cluster));
			// non-configurable HADOOP_DAEMON_DOWN
			alertList.add(getNodeDownAlert(cluster));
			// non-configurable HADOOP_DAEMON_DOWN
			Alert alert = getNameNodeDownAlert(cluster);
			if (alert != null) {
				List<Alert> list = new ArrayList<Alert>(1);
				list.add(alert);
				alertList.add(list);
			}
		}
		return alertList;
	}

	/**
	 * Gets the data node down alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the data node down alert
	 */
	public List<Alert> getDataNodeDownAlert(Cluster cluster) {
		List<String> workers = cluster.getWorkers().getHosts();
		List<Alert> alertList = new ArrayList<Alert>();
		for (String worker : workers) {
			String dataNodeInstance = null;
			dataNodeInstance = RemotingUtil.getDaemonProcessId(cluster, worker, JMXDeamons.DATA_NODE.toString());
			if (dataNodeInstance.isEmpty()) {
				Alert alert = new Alert(ExtendedConstants.CRITICAL_LEVEL, worker, AlertConstants.DATA_NODE + AlertConstants.DEAMON_WENT_DOWN,
						getDate());
				alertList.add(alert);
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
	public List<Alert> getDiskSpaceUsageAlert(Cluster cluster) {

		List<String> workers = cluster.getWorkers().getHosts();
		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		SeverityLevel severityLevel = AdminConfigurationUtil.getAlertConfiguration(cluster.getClusterName())
				.getConfigurableAlerts().get(AlertType.DISK_SPACE_UTILIZATION);

		List<Alert> alertList = new ArrayList<Alert>();
		Map<String, String> dataNodeJMXStats = null;

		int criticalLevel = severityLevel.getCriticalLevel();
		int warningLevel = severityLevel.getWarningLevel();

		String statName, diskCapacity, remainingCapacity;

		Alert alert = null;

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
							alert = new Alert(ExtendedConstants.CRITICAL_LEVEL, worker,
									getDiskStorageId(statName) + AlertConstants.DISKSPACE_ALERT_MESSAGE, getDate());
							alertList.add(alert);
						} else if ((diskSpaceUsed < criticalLevel) && (diskSpaceUsed >= warningLevel)) {
							alert = new Alert(ExtendedConstants.WARNING_LEVEL, worker,
									getDiskStorageId(statName) + AlertConstants.DISKSPACE_ALERT_MESSAGE, getDate());
							alertList.add(alert);
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
	public List<Alert> getDataNodeVolumeFailureAlert(Cluster cluster) {

		List<String> workers = cluster.getWorkers().getHosts();
		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		List<Alert> alertList = new ArrayList<Alert>();
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
							Alert alert = new Alert(ExtendedConstants.CRITICAL_LEVEL, worker,
									AlertConstants.DATA_NODE_WITH_STORAGE_ID + storageId + AlertConstants.HAS + numFailedVolumes + AlertConstants.VOLUME_FAILURE_S,
									getDate());
							alertList.add(alert);
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
	private void jmxConnectionCacheClear(Cluster cluster, String worker) {
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
	public List<Alert> getUnderReplicatedBlockAlert(Cluster cluster) {

		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		List<Alert> alertList = new ArrayList<Alert>(1);

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
					Alert alert = new Alert(ExtendedConstants.CRITICAL_LEVEL, ExtendedConstants.ALL,
							underReplicatedBlock + AlertConstants.UNDER_REPLICATED_BLOCK_MESSAGE, getDate());
					alertList.add(alert);
				} else if (underReplicatedBlockPercent >= warningLevel && underReplicatedBlockPercent < criticalLevel) {
					Alert alert = new Alert(ExtendedConstants.WARNING_LEVEL, ExtendedConstants.ALL,
							underReplicatedBlock + AlertConstants.UNDER_REPLICATED_BLOCK_MESSAGE, getDate());
					alertList.add(alert);
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
	public List<Alert> getNodeDownAlert(Cluster cluster) {

		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		List<Alert> alertList = new ArrayList<Alert>(1);

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
				Alert alert = new Alert(ExtendedConstants.WARNING_LEVEL, ExtendedConstants.HYPHEN,
						nodeDown + AlertConstants.NODE_S_UNAVAILABLE, getDate());
				alertList.add(alert);
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
	public List<Alert> getHDFSSpaceUsageAlert(Cluster cluster) {

		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		List<Alert> alertList = new ArrayList<Alert>(2);
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
				Alert alert = new Alert(ExtendedConstants.CRITICAL_LEVEL, ExtendedConstants.ALL,
						AlertConstants.HDFS_SPACE_USAGE_MESSAGE, getDate());
				alertList.add(alert);
			} else if (hdfsCapacityUtilization >= hdfsWarningLevel && hdfsCapacityUtilization < hdfsCriticalLevel) {
				Alert alert = new Alert(ExtendedConstants.WARNING_LEVEL, ExtendedConstants.ALL,
						AlertConstants.HDFS_SPACE_USAGE_MESSAGE, getDate());
				alertList.add(alert);
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
	public Alert getNameNodeDownAlert(Cluster cluster) {

		String nameNodeInstance = null;
		nameNodeInstance = RemotingUtil.getDaemonProcessId(cluster, cluster.getNameNode(),
				JMXDeamons.NAME_NODE.toString());
		if (nameNodeInstance.isEmpty()) {
			return new Alert(ExtendedConstants.CRITICAL_LEVEL, cluster.getNameNode(), AlertConstants.NAME_NODE + AlertConstants.DEAMON_WENT_DOWN,
					getDate());
		}

		return null;
	}

	/**
	 * @param cluster
	 * @param hdfsPaths : list of hdfs paths to be checked
	 * @return : the alert if the maximum files in a hdfs directory crosses 90 % of threshold value
	 */
	public List<Alert> getHDFSMaxFilesInDirAlert(Cluster cluster, List<String> hdfsPaths){
		List<Alert> alerts = new ArrayList<>(hdfsPaths.size());
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
					Alert alert = new Alert(ExtendedConstants.CRITICAL_LEVEL, ExtendedConstants.ALL, "The directory count inside HDFS folder ["+ hdfsPath + "]  has reached 85% of the threshold value, kindly clean to avoid MaxDirectoryItemsExceeded issue", getDate());
					alerts.add(alert);
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
	public List<Alert> getFragmenedFilesAlert(Cluster cluster){
		List<Alert> alerts = new ArrayList<Alert>();
		try {
			String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(cluster);
			String hdfsSitePath = RemotingUtil.copyAndGetConfigurationFilePath(cluster, hadoopConfDir, Constants.HDFS_SITE_XML);
			String nameNodeDfsPath = RemotingUtil.parseConfiguration(hdfsSitePath + File.separator + Constants.HDFS_SITE_XML, AlertConstants.NAMENODE_DATA_DIRECTORY);
			if(nameNodeDfsPath == null || nameNodeDfsPath.isEmpty()){
				String coreSitePath = RemotingUtil.copyAndGetConfigurationFilePath(cluster, hadoopConfDir, Constants.CORE_SITE_XML);
				String hadoopTmpPath = RemotingUtil.parseConfiguration(hdfsSitePath + File.separator + Constants.CORE_SITE_XML, AlertConstants.NAMENODE_TEMP_DIRECTORY);
				nameNodeDfsPath = hadoopTmpPath + "/dfs/name" ;
			}
			String fileCountCommand = null;
			String commandResponse = null ;
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

	protected void getFragmFileAlerts(Cluster cluster, List<Alert> alerts, String nameNodeDfsPath) {
		String commandResponse;
		StringBuilder fileCountCmd = new StringBuilder().append("find").append(Constants.SPACE).append(nameNodeDfsPath)
									.append(Constants.SPACE).append("-type").append(Constants.SPACE).append("f").append(Constants.SPACE)
									.append("|").append(Constants.SPACE).append("wc").append(Constants.SPACE).append("-l");
			
			commandResponse = RemotingUtil.executeCommand(cluster, fileCountCmd.toString());
	
			double dfsLimitPer = (Double.parseDouble(commandResponse)/1048576) * 100 ;
			if( dfsLimitPer >= 85 ){
				Alert alert = new Alert(ExtendedConstants.CRITICAL_LEVEL, cluster.getNameNode(),  "The threshold for fragmented files has reached 85%, kindly clean[" + nameNodeDfsPath + "] to avoid data insertion issues", getDate());
				alerts.add(alert);
			}
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
