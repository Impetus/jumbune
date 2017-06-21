package org.jumbune.common.beans.cluster;

import java.util.Collection;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Agent;
import org.jumbune.common.beans.cluster.ClusterDefinition;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.ActiveNodeInfo;
import org.jumbune.remoting.common.AgentNode;
import org.jumbune.remoting.common.ZKUtils;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.ha.HAUtil;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.hadoop.conf.Configuration;
import org.jumbune.common.utils.RMInfoService;

import org.jumbune.common.utils.AgentNodeUtil;

public class EnterpriseClusterDefinition extends ClusterDefinition implements EnterpriseCluster {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(EnterpriseClusterDefinition.class);
	
	private static final String DEFAULT_HISTORY_SERVER_PORT = ":10020";

	private static final String COLON = ":";

	private transient Boolean isDefaultMRAddressAvailable = null;
	
	private transient String defaultMRAddress = null;
	
	private transient String historyServer = null;
	
	private transient RMInfoService  rmInfoService = new RMInfoService(this);
	
	private Set<ZK> zks;

	public Set<ZK> getZks() {
		return zks;
	}

	public void setZks(Set<ZK> zks) {
		this.zks = zks;
	}
	
	public String getNameNode(){

		if(getNameNodes().isHaEnabled()){
	     return performAgentFailoverIfNecessary();	
	    }
		// return the first and only element, in case HA is not there
    	return getNameNodes().getHosts().get(0);
	}
	
	/**
	 * Perform agent failover if necessary. This method gets the active NameNode
	 * from Zookeeper and if a leadership change is detected in NameNode(i.e. if
	 * the active NameNode has changed), agent failover is initiated. Agents are
	 * made to fail until the agent residing on NameNode takes the leadership.
	 *
	 * @return currently active NameNode.
	 */
	private String performAgentFailoverIfNecessary() {
		String activeNNHost = null;
		Agent activeAgent = null;
		String activeAgentHost = null;
		synchronized (EnterpriseClusterDefinition.class) {
			activeNNHost = getActiveNNFromZK(getZkHosts(getZks()));
			if (HAUtil.getActiveNNHost() == null || HAUtil.getActiveNNHost().isEmpty()) {
				HAUtil.setActiveNNHost(activeNNHost);
			}
			if (HAUtil.getActiveAgentHost() == null || HAUtil.getActiveAgentHost().isEmpty()) {
				activeAgent = getJumbuneAgent();
				activeAgentHost = activeAgent.getHost();
				HAUtil.setActiveAgentHost(activeAgentHost);
				HAUtil.setActiveAgentPort(Integer.parseInt(activeAgent.getPort()));
			} else {
				activeAgentHost = HAUtil.getActiveAgentHost();
			}

			Agent newAgent = null;
			int agentRetryAttempts = 5;
			int i = 1;
			// check if the namenode leadership has changed
			if (!HAUtil.getActiveNNHost().equals(activeNNHost)) {
                // check if active agent is residing on active NameNode
				if (!activeNNHost.equals(activeAgentHost)) {
					// iterating and killing agents till the agent on active NameNode becomes active
					do {
						LOGGER.warn("Forcing agent leader election");
						LOGGER.warn("Leader agent can not be found on active namenode. will retry "
								+ (agentRetryAttempts - i) + " more time(s)");
						newAgent = forceAgentElection();
						LOGGER.debug("Active namenode host - " + activeNNHost + " Newly elected agent - " + newAgent);
						if (newAgent == null) {
							throw new IllegalStateException(
									"It seems that no jumbune agents are alive on active namenode");
						} else {
							// updating global state of currently active agent
							// and NN
							HAUtil.setActiveAgentHost(newAgent.getHost());
							HAUtil.setActiveAgentPort(Integer.parseInt(newAgent.getPort()));
							HAUtil.setActiveNNHost(activeNNHost);
						}
						i++;
					} while (!newAgent.getHost().equals(activeNNHost) && i <= agentRetryAttempts);
				} else {
					return activeNNHost;
				}
			}
		}

		return activeNNHost;

	}
	
	/**
	 * Forces agent election. It kills the agent which is currently active.
	 * When the leader agent goes down, some other agent(if available) is elected as leader.
	 *
	 * @return the active agent
	 */
	private Agent forceAgentElection() {

		Agent agent = getJumbuneAgent();
		// send self destruct command to currently active agent so that it
		// relinquishes leadership
		Remoter remoter = RemotingUtil.getRemoter(this);
		remoter.shutdownAgent();
		
		// block till a change in leadership of agent
		while (agent != null && HAUtil.compareAgent(agent.getHost(), Integer.parseInt(agent.getPort()))) {
			try {
				LOGGER.debug("Blocking till a new leader agent is elected...");
				agent = getJumbuneAgent();
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				LOGGER.error("Error waiting for new agent to be elected");
			}
		}
		return agent;
	}
	
	private String[] getZkHosts(Collection<ZK> zks) {
		String[] zkHosts = new String[zks.size()];
        int i = 0;
		for (ZK zk : zks) {
			zkHosts[i++] = zk.getHost() + COLON + zk.getPort();
		}
		return zkHosts;
	}
	
	public String[] getZKs() {
		return getZkHosts(zks);		
	}
	
	public Agent getJumbuneAgent(){
		if(getAgents().isHaEnabled() || getNameNodes().isHaEnabled()){
			return getActiveJAFromZK(getZkHosts(getZks()));			
		}		
		// return the first and only element, in case HA is not there
		return getAgents().getAgents().get(0);
	}
	
	private Agent getActiveJAFromZK(String[] zkHosts){
		//update master object with leader agent details
		Agent agent = null;
		AgentNode agentNode = null;
		try {
			agentNode = ZKUtils.getLeaderAgentfromZK(zkHosts, AdminConfigurationUtil.getHAConfiguration(clusterName));
			agent = AgentNodeUtil.convertAgentNodeToAgent(agentNode);
			LOGGER.debug("Active agent["+agent.getHost()+":"+agent.getPort()+"]");
		} catch (Exception e) {
			LOGGER.warn("Exception while connecting to jumbune agent",e);
		}
		return agent;
	}
	
	/**
	 * Gets the active namenode host.
	 * @param strings 
	 *
	 * @return the active namenode host.
	 */
	private String getActiveNNFromZK(String[] zkHosts){
		byte[] activeHost = null;
		String activeNameNode = null ;
		activeHost = ZKUtils.getLeaderNameNodeFromZK(zkHosts);
		try {
			ActiveNodeInfo activeNodeInfo = PARSER.parsePartialFrom(activeHost);
			activeNameNode = activeNodeInfo.getHostname();
			LOGGER.debug("Active namenode - "+activeNameNode);
		} catch (InvalidProtocolBufferException e) {
			LOGGER.error(e);			
		}
		return activeNameNode;
	}
	
	public static com.google.protobuf.Parser<ActiveNodeInfo> PARSER =
            new com.google.protobuf.AbstractParser<ActiveNodeInfo>() {
          public ActiveNodeInfo parsePartialFrom(
              com.google.protobuf.CodedInputStream input,
              com.google.protobuf.ExtensionRegistryLite extensionRegistry)
              throws com.google.protobuf.InvalidProtocolBufferException {
            return new ActiveNodeInfo(input, extensionRegistry);
          }
        };	

	@Override
	public String toString() {
		return "EnterpriseClusterDefinition [zks=" + zks + ", toString()="
				+ super.toString() + "]";
	}

/*	*//**
	 * Sets the location.
	 *
	 * @param location
	 *            the new location
	 *//*
	private void setLocation(final String location) {
		String locationTemp = location;
		if (locationTemp != null) {
			locationTemp = JobUtil.getAndReplaceHolders(locationTemp);
			if (!JobUtil.validateFileSystemLocation(locationTemp)) {
				throw new IllegalArgumentException(
						"Location provided in Master is not in correct format!!");
			}
			this.location = locationTemp;
		}
	}
*/	

	@Override
	public String getHistoryServer() {
		if (this.historyServer == null) {
			Configuration c = new Configuration();
			String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(this);
			RemotingUtil.addHadoopResource(c, this, hadoopConfDir, "yarn-site.xml");
			RemotingUtil.addHadoopResource(c, this, hadoopConfDir, "mapred-site.xml");
			this.historyServer = c.get("mapreduce.jobhistory.address");
			if (this.historyServer == null || this.historyServer.trim().isEmpty()) {
				this.historyServer = this.getNameNode();
			} else {
				this.historyServer = this.historyServer.trim().split(COLON)[0];
			}
		}
		return this.historyServer;
	}
	
	@Override
	public String getMRSocketAddress() {
		if (this.isDefaultMRAddressAvailable == null) {
			Configuration c = new Configuration();
			String hadoopConfDir = RemotingUtil.getHadoopConfigurationDirPath(this);
			RemotingUtil.addHadoopResource(c, this, hadoopConfDir, "yarn-site.xml");
			RemotingUtil.addHadoopResource(c, this, hadoopConfDir, "mapred-site.xml");
			this.defaultMRAddress = c.get("mapreduce.jobhistory.address");
			if (this.defaultMRAddress == null || this.defaultMRAddress.trim().isEmpty()) {
				this.isDefaultMRAddressAvailable = false;
			} else {
				this.isDefaultMRAddressAvailable = true;
				this.defaultMRAddress = this.defaultMRAddress.trim();
			}
		}
		if (this.isDefaultMRAddressAvailable) {
			return this.defaultMRAddress;
		} else {
			return getNameNode() + DEFAULT_HISTORY_SERVER_PORT;
		}
	}
	
	@Override
	public String getResourceManager() {
		return rmInfoService.getActiveResourceMananager();
	}
	
	@Override
	public String getRMSocketAddress() {
		return getResourceManager() + COLON + rmInfoService.getRMPort();
	}
	
	@Override
	public String getRMWebAppAddress() {
		return rmInfoService.getRMWebAppProtocol() + getResourceManager() + COLON + rmInfoService.getRMWebAppPort();
	}   
	

}
