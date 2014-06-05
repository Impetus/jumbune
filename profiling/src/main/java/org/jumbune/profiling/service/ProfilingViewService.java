package org.jumbune.profiling.service;

import java.util.List;

import org.jumbune.profiling.beans.ClusterInfo;
import org.jumbune.profiling.beans.NodeConfig;
import org.jumbune.profiling.beans.NodeStats;
import org.jumbune.profiling.beans.PerformanceStats;
import org.jumbune.profiling.utils.HTFProfilingException;




/**
 * Service to return different profiling view.
 * 
 */
public interface ProfilingViewService {

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
	 *            {@link YamlConfig}
	 * @return {@link NodeInfo}
	 */
	NodeStats getNodeView(NodeConfig nodeConfig, List<PerformanceStats> clrSettings) throws HTFProfilingException;

}
