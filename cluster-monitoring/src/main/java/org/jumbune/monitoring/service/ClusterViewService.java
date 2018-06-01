package org.jumbune.monitoring.service;

import java.util.List;

import org.jumbune.monitoring.beans.ClusterInfo;
import org.jumbune.monitoring.beans.NodeConfig;
import org.jumbune.monitoring.beans.NodeInfo;
import org.jumbune.monitoring.beans.NodeStats;
import org.jumbune.monitoring.beans.PerformanceStats;
import org.jumbune.monitoring.utils.HTFProfilingException;




/**
 * Service to return different profiling view.
 * 
 */
public interface ClusterViewService {

	/**
	 * Return cluster information with node performance
	 * 
	 * @param config
	 * @param genSettings
	 * @return
	 * @throws HTFProfilingException
	 */
	ClusterInfo getMainView(List<PerformanceStats> genSettings, String viewName) throws HTFProfilingException;

	/**
	 * Return node information as per main view selected.
	 * 
	 * @param nodeIp
	 *            the ip address of node
	 * @param config
	 *            {@link Config}
	 * @return {@link NodeInfo}
	 */
	NodeStats getNodeView(NodeConfig nodeConfig, List<PerformanceStats> clrSettings) throws HTFProfilingException;
	
	/**
	 * Returns the relative percentage of data loads on nodes in the cluster
	 * @return {@link List<NodeInfo>}
	 */
	List<NodeInfo> getDataLoadAndDistributionDetails();
	
	/**
	 * Returns the UI consumable ClusterInfo JSON bean which contains node details (ip, cpu, memory, etc.) and with health state (good, bad or average)
	 * @param genSettings
	 * @return
	 */
	ClusterInfo getDataCenterDetails(List<PerformanceStats> genSettings);

}
