package org.jumbune.profiling.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.profiling.beans.DataCenterInfo;
import org.jumbune.profiling.beans.NodeInfo;
import org.jumbune.profiling.beans.RackInfo;
import org.jumbune.profiling.healthview.NetworkLatencyInfo;

import com.jcraft.jsch.JSchException;

/**
 * Helper class that will support cluster, hadoop view creation.
 * 
 */
public class ViewHelper {

	private static final Logger LOGGER = LogManager.getLogger(ViewHelper.class);

	private ProfilerJMXDump statsDump;

	/**
	 * Instantiates a new view helper.
	 */
	public ViewHelper() {
		statsDump = new ProfilerJMXDump();
	}

	/**
	 * To bind node with particular rack.
	 * 
	 * @param node
	 *            the node to bind
	 * @param rackId
	 *            the rackId to get rack
	 * @param racks
	 *            rack collection
	 */
	public void bindNodeToRack(NodeInfo node, String rackId,
			Map<String, RackInfo> racks) {
		RackInfo rack = racks.get(rackId);

		if (rack == null) {
			rack = new RackInfo();
			HashSet<NodeInfo> nodes = new HashSet<NodeInfo>();
			rack.setNodes(nodes);
			rack.setRackId(rackId);
			racks.put(rackId, rack);
		}

		rack.getNodes().add(node);
	}

	/**
	 * To bind rack with particular data center.
	 * 
	 * @param rack
	 *            the rack to bind
	 * @param dcId
	 *            the data center identifier to get data center
	 * @param clusterId
	 *            the clusterId
	 * @param dataCenters
	 *            data center collection
	 */
	public void bindRackToDC(RackInfo rack, String dcId, String clusterId,
			Map<String, DataCenterInfo> dataCenters) {
		DataCenterInfo dc = dataCenters.get(dcId);

		if (dc == null) {
			dc = getNewDC(dcId, clusterId);
			dataCenters.put(dcId, dc);
		}

		dc.getRacks().add(rack);
	}

	/**
	 * @param dcId
	 *            the data center identifier.
	 * @param clusterId
	 *            the cluster identifier.
	 * @return the new instance of {@link DataCenterInfo}
	 */
	private DataCenterInfo getNewDC(String dcId, String clusterId) {
		DataCenterInfo dc = new DataCenterInfo();
		Set<RackInfo> racks = new HashSet<RackInfo>();
		dc.setId(dcId);
		dc.setClusterId(clusterId);
		dc.setRacks(racks);

		return dc;
	}

	/**
	 * 
	 * @param config
	 * @param selectedNodesWithPassword
	 * @return
	 * @throws HTFProfilingException
	 * @throws IOException
	 * @throws JSchException
	 */
	public List<NetworkLatencyInfo> getNetworkLatency(JumbuneRequest jumbuneRequest,
			Map<String, String> selectedNodesWithPassword) throws HTFProfilingException {
		
		List<NetworkLatencyInfo> networkLatencyInfoList;
		try {
			LOGGER.debug("node details: " + selectedNodesWithPassword);
			networkLatencyInfoList = statsDump
					.getRemoteNetworkLatencyForSelectedNodes(jumbuneRequest,
							selectedNodesWithPassword);
		} catch (JSchException ex) {

			LOGGER.error(
					"Exception occured while making ssh connection to get network latency for nodes:"
							+ selectedNodesWithPassword, ex);
			throw new HTFProfilingException(
					"Exception occured while making ssh connection to get network latency for nodes:"
							+ selectedNodesWithPassword + ex);
		} catch (IOException e) {
			LOGGER.error("IO Exception occured while making ssh connection to get network latency for nodes:"
					+ selectedNodesWithPassword);
			throw new HTFProfilingException(
					"IO Exception occured while making ssh connection to get network latency for nodes:"
							+ selectedNodesWithPassword+e);
		}
		return networkLatencyInfoList;
	}

}