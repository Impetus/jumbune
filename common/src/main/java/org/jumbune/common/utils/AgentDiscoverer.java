package org.jumbune.common.utils;

public class AgentDiscoverer {

/*	*//**
	 * Update master details.
	 *//*
	public void updateMasterDetails() {

		// initially copy zk details from host to zkHost and agent
		if (getZkHost() == null && getZkPort() == null) {
			setZkHost(host);
			setZkPort(agentPort);
		}

		// get the zookeeper connection
		ZKConnector zkc = null;
		try {
			zkc = ZKConnector.getInstance();
			zkc.getZooKeeper();
		} catch (Exception e) {
			String connectString = getZkHost() + ":" + getZkPort();
			try {
				zkc.connect(connectString);
			} catch (IOException | InterruptedException e1) {
				throw new IllegalArgumentException(
						"Zookeeper connect string is not valid!!");
			}
		}
		// update master object with leader agent details
		AgentNode agentNode = null;
		try {
			agentNode = ZKUtils.getLeaderAgentNodeFromZK();
			host = agentNode.getAgentHost();
			agentPort = agentNode.getAgentPort().toString();
		} catch (ConnectException e) {
			// this will be handled in remoter
			logger.warn("Connect exception while connecting to jumbune agent",
					e);
		}
	}
*/}
