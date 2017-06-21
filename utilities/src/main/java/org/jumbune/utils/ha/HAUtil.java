package org.jumbune.utils.ha;

/**
 * The Class HAUtil. This class maintains the state of currently active NameNode and currently active agent.
 * Specifically this class is used to detect the change in leadership of NameNode or Agent.
 */
public final class HAUtil {

	/** The active nn host. */
	private static String activeNNHost;
	
	/** The active agent host. */
	private static String activeAgentHost;
	
	/** The active agent port. */
	private static int activeAgentPort;

	/**
	 * Gets the active nn host.
	 *
	 * @return the active nn host
	 */
	public static String getActiveNNHost() {
		return activeNNHost;
	}

	/**
	 * Sets the active nn host.
	 *
	 * @param activeNNHost the new active nn host
	 */
	public static synchronized void setActiveNNHost(String activeNNHost) {
		HAUtil.activeNNHost = activeNNHost;
	}

	/**
	 * Gets the active agent host.
	 *
	 * @return the active agent host
	 */
	public static String getActiveAgentHost() {
		return activeAgentHost;
	}

	/**
	 * Sets the active agent host.
	 *
	 * @param activeAgentHost the new active agent host
	 */
	public static synchronized void setActiveAgentHost(String activeAgentHost) {
		HAUtil.activeAgentHost = activeAgentHost;
	}

	/**
	 * Gets the active agent port.
	 *
	 * @return the active agent port
	 */
	public static int getActiveAgentPort() {
		return activeAgentPort;
	}

	/**
	 * Sets the active agent port.
	 *
	 * @param activeAgentPort the new active agent port
	 */
	public static synchronized void setActiveAgentPort(int activeAgentPort) {
		HAUtil.activeAgentPort = activeAgentPort;
	}

	/**
	 * Compare agent.
	 *
	 * @param host the host
	 * @param port the port
	 * @return true, if successful
	 */
	public static boolean compareAgent(String host, int port){
		return activeAgentPort == port && activeAgentHost.equals(host);
	}
    
}
