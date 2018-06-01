package org.jumbune.utils.ha;

import java.util.HashMap;
import java.util.Map;

import org.jumbune.utils.beans.ClusterHADetails;

/**
 * The Class HAUtil. This class maintains the state of currently active NameNode and currently active agent.
 * Specifically this class is used to detect the change in leadership of NameNode or Agent.
 */
public final class HAUtil {
	
	/**
	 * Key = Cluster name
	 * Value = Cluster ha information
	 */
	private static Map<String, ClusterHADetails> clusterHADetailsMap = new HashMap<>(1);
	
	private static ClusterHADetails getClusterHADetails(String clusterName) {
		ClusterHADetails details = clusterHADetailsMap.get(clusterName);
		if (details == null) {
			details = new ClusterHADetails();
			clusterHADetailsMap.put(clusterName, details);
		}
		return details;
	}

	/**
	 * Gets the active nn host.
	 *
	 * @return the active nn host
	 */
	public static String getActiveNNHost(String clusterName) {
		return getClusterHADetails(clusterName).getActiveNNHost();
	}

	/**
	 * Sets the active nn host.
	 *
	 * @param activeNNHost the new active nn host
	 */
	public static synchronized void setActiveNNHost(String clusterName, String activeNNHost) {
		getClusterHADetails(clusterName).setActiveNNHost(activeNNHost);
	}

	/**
	 * Gets the active agent host.
	 *
	 * @return the active agent host
	 */
	public static String getActiveAgentHost(String clusterName) {
		return getClusterHADetails(clusterName).getActiveAgentHost();
	}

	/**
	 * Sets the active agent host.
	 *
	 * @param activeAgentHost the new active agent host
	 */
	public static synchronized void setActiveAgentHost(String clusterName, String activeAgentHost) {
		getClusterHADetails(clusterName).setActiveAgentHost(activeAgentHost);
	}

	/**
	 * Gets the active agent port.
	 *
	 * @return the active agent port
	 */
	public static int getActiveAgentPort(String clusterName) {
		return getClusterHADetails(clusterName).getActiveAgentPort();
	}

	/**
	 * Sets the active agent port.
	 *
	 * @param activeAgentPort the new active agent port
	 */
	public static synchronized void setActiveAgentPort(String clusterName, int activeAgentPort) {
		getClusterHADetails(clusterName).setActiveAgentPort(activeAgentPort);
	}

	/**
	 * Compare agent.
	 *
	 * @param host the host
	 * @param port the port
	 * @return true, if successful
	 */
	public static boolean compareAgent(String clusterName, String host, int port) {
		ClusterHADetails details = getClusterHADetails(clusterName);
		return details.getActiveAgentHost().equals(host) && details.getActiveAgentPort() == port;
	}
    
}
