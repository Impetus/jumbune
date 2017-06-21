package org.jumbune.common.alerts;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.yarn.exceptions.YarnException;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.utils.yarn.communicators.RMCommunicator;
import org.jumbune.common.beans.Alert;



/**
 * The Interface YarnAlert.
 */
public interface YarnAlert {

	/**
	 * Gets the queue utilisation alert.
	 *
	 * @return the queue utilisation alert
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	List <Alert> getQueueUtilisationAlert(String clusterName, RMCommunicator rmCommunicator);	
	/**
	 * Gets the application failed alert.
	 *
	 * @return the application failed alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws YarnException the yarn exception
	 */
	List <Alert> getApplicationFailedAlert(RMCommunicator rmCommunicator, long finishFrom, long finishTo) ;
	
	/**
	 * Gets the container utilization alert.
	 *
	 * @param cluster the cluster
	 * @return the container utilization alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws YarnException the yarn exception
	 */
	List <Alert> getContainerUtilizationAlert(Cluster cluster, RMCommunicator rmCommunicator);
	
	/**
	 * Gets the effective utlilzation alert.
	 *
	 * @param string the cluster
	 * @return the effective utlilzation alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 * @throws YarnException the yarn exception
	 */
	List <Alert> getEffectiveUtlilzationAlert(String clusterName);
	
	/**
	 * Gets the resource manager down alert.
	 *
	 * @param cluster the cluster
	 * @return the resource manager down alert
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws YarnException the yarn exception
	 * @throws InterruptedException the interrupted exception
	 */
	Alert getResourceManagerDownAlert (Cluster cluster);
	
	/**
	 * Gets the node manager down alert.
	 *
	 * @param cluster the cluster
	 * @return the node manager down alert
	 */
	List<Alert> getNodeManagerDownAlert (Cluster cluster,RMCommunicator rmCommunicator);
	
	/**
	 * Gets the node manager down alert.
	 *
	 * @param cluster the cluster
	 * @return the node manager went unhealthy alert
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	List <Alert> getNodeUnhealthyAlert(Cluster cluster, RMCommunicator rmCommunicator) ;
	
	
	
	/**
	 * Check yarn property set correctly.
	 *
	 * @param cluster the cluster
	 * @return the list
	 */
	List<Alert> checkYarnPropertySetCorrectly(Cluster cluster);

	/**
	 * Gets the history server down alert.
	 *
	 * @param cluster the cluster
	 * @return the history server down alert
	 * @throws InterruptedException the interrupted exception
	 */
	Alert getHistoryServerDownAlert(Cluster cluster) ;

	/**
	 * Gets the resource utilization alert.
	 *
	 * @param cluster the cluster
	 * @return the resource utilization alert
	 */
	List<Alert> getResourceUtilizationAlert(Cluster cluster, List<Alert> nmAlerts);
	
	/**
	 * Gets the cluster time desync alert.
	 *
	 * @return the cluster time desync alert
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	List<Alert> getClusterTimeDesyncAlert(String clusterName) ;
	
	/**
	 * Gets the child capacity alert.
	 *
	 * @return the child capacity alert
	 * @throws YarnException the yarn exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	List <Alert> getChildCapacityAlert(RMCommunicator rmCommunicator);

}
