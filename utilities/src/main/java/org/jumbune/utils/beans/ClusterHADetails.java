package org.jumbune.utils.beans;

public class ClusterHADetails {

	/** The active nn host. */
	private String activeNNHost = null;

	/** The active agent host. */
	private String activeAgentHost = null;

	/** The active agent port. */
	private int activeAgentPort;

	public String getActiveNNHost() {
		return activeNNHost;
	}

	public void setActiveNNHost(String activeNNHost) {
		this.activeNNHost = activeNNHost;
	}

	public String getActiveAgentHost() {
		return activeAgentHost;
	}

	public void setActiveAgentHost(String activeAgentHost) {
		this.activeAgentHost = activeAgentHost;
	}

	public int getActiveAgentPort() {
		return activeAgentPort;
	}

	public void setActiveAgentPort(int activeAgentPort) {
		this.activeAgentPort = activeAgentPort;
	}

}
