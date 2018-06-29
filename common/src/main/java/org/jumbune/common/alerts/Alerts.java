package org.jumbune.common.alerts;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.yarn.exceptions.YarnException;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.utils.yarn.communicators.RMCommunicator;
import org.jumbune.common.beans.AlertInfo;



/**
 * The Interface YarnAlert.
 */
public interface Alerts {

	/**
	 * Gets the queue utilisation alert.
	 *
	 * @return the queue utilisation alert
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	List <AlertInfo> getQueueUtilisationAlert(String clusterName, RMCommunicator rmCommunicator);	
	/**
	 * Gets the application failed alert.
	 *
	 * @return the application failed alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws YarnException the yarn exception
	 */
	List <AlertInfo> getApplicationFailedAlert(RMCommunicator rmCommunicator, long finishFrom, long finishTo) ;
	
	/**
	 * Gets the container utilization alert.
	 *
	 * @param cluster the cluster
	 * @return the container utilization alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws YarnException the yarn exception
	 */
	List <AlertInfo> getContainerUtilizationAlert(Cluster cluster, RMCommunicator rmCommunicator);
	
	/**
	 * Gets the effective utlilzation alert.
	 *
	 * @param string the cluster
	 * @return the effective utlilzation alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 * @throws YarnException the yarn exception
	 */
	List <AlertInfo> getEffectiveUtlilzationAlert(String clusterName);
	
	/**
	 * Gets the resource manager down alert.
	 *
	 * @param cluster the cluster
	 * @return the resource manager down alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws YarnException the yarn exception
	 * @throws InterruptedException the interrupted exception
	 */
	AlertInfo getResourceManagerDownAlert (Cluster cluster);
	
	/**
	 * Gets the node manager down alert.
	 *
	 * @param cluster the cluster
	 * @return the node manager down alert
	 */
	List<AlertInfo> getNodeManagerDownAlert (Cluster cluster,RMCommunicator rmCommunicator);
	
	/**
	 * Gets the node manager down alert.
	 *
	 * @param cluster the cluster
	 * @return the node manager went unhealthy alert
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	List <AlertInfo> getNodeUnhealthyAlert(Cluster cluster, RMCommunicator rmCommunicator) ;
	
	/**
	 * Check yarn property set correctly.
	 *
	 * @param cluster the cluster
	 * @return the list
	 */
	List<AlertInfo> checkYarnPropertySetCorrectly(Cluster cluster);

	/**
	 * Gets the history server down alert.
	 *
	 * @param cluster the cluster
	 * @return the history server down alert
	 * @throws InterruptedException the interrupted exception
	 */
	AlertInfo getHistoryServerDownAlert(Cluster cluster) ;

	/**
	 * Gets the resource utilization alert.
	 *
	 * @param cluster the cluster
	 * @return the resource utilization alert
	 */
	List<AlertInfo> getResourceUtilizationAlert(Cluster cluster, List<AlertInfo> nmAlerts);
	
	/**
	 * Gets the cluster time desync alert.
	 *
	 * @return the cluster time desync alert
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	List<AlertInfo> getClusterTimeDesyncAlert(String clusterName) ;
	
	/**
	 * Gets the child capacity alert.
	 *
	 * @return the child capacity alert
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	List <AlertInfo> getChildCapacityAlert(RMCommunicator rmCommunicator);
	
	/**
	 * Gets the data node down alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the data node down alert
	 */
	List<AlertInfo> getDataNodeDownAlert(Cluster cluster);
	
	/**
	 * Gets the disk space usage alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the disk space usage alert
	 */
	List<AlertInfo> getDiskSpaceUsageAlert(Cluster cluster);
	
	/**
	 * Gets the data node volume failure alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the data node volume failure alert
	 */
	List<AlertInfo> getDataNodeVolumeFailureAlert(Cluster cluster);
	
	/**
	 * Jmx connection cache clear.
	 *
	 * @param cluster
	 *            the cluster
	 * @param worker
	 *            the worker
	 */
	void jmxConnectionCacheClear(Cluster cluster, String worker);
	
	/**
	 * Gets the under replicated block alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the under replicated block alert
	 */
	List<AlertInfo> getUnderReplicatedBlockAlert(Cluster cluster);
	
	/**
	 * Gets the node down alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the node down alert
	 */
	List<AlertInfo> getNodeDownAlert(Cluster cluster);
	
	/**
	 * Gets the HDFS space usage alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the HDFS space usage alert
	 */
	List<AlertInfo> getHDFSSpaceUsageAlert(Cluster cluster);
	
	/**
	 * Gets the name node down alert.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the name node down alert
	 */
	AlertInfo getNameNodeDownAlert(Cluster cluster);
	
	/**
	 * @param cluster
	 * @param hdfsPaths : list of hdfs paths to be checked
	 * @return : the alert if the maximum files in a hdfs directory crosses 90 % of threshold value
	 */
	List<AlertInfo> getHDFSMaxFilesInDirAlert(Cluster cluster, List<String> hdfsPaths);
	
	// dfs.namenode.name.dir alert
	/**
	 * @param cluster
	 * @return alert if the namenode dfs directory(containing the edit logs and fsimage location) exceeds the 90% threshold value
	 */
	List<AlertInfo> getFragmenedFilesAlert(Cluster cluster);
	
	/**
	 * Returns all types of alerts
	 * @param cluster
	 * @param rmCommunicator
	 * @param oldTime
	 * @param newTime
	 * @return
	 */
	List<AlertInfo> getAllAlerts(Cluster cluster, RMCommunicator rmCommunicator, long oldTime, long newTime);

}
