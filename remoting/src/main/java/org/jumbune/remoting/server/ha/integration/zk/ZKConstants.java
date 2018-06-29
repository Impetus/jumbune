package org.jumbune.remoting.server.ha.integration.zk;

/**
 * The Interface defining ZK related constants.
 */
public interface ZKConstants {

		/** The Constant ROOT_NODE_PATH. */
		String ROOT_NODE_PATH = "/jumbune";

		/** The Constant AGENT_NODE_PATH. */
		String AGENT_NODE_PATH = ROOT_NODE_PATH + "/agent";

		/** The session timeout in milliseconds. */
		int SESSION_TIMEOUT = 5000;
		
		/** The Constant AGENT_MONITOR_DELAY. */
		long AGENT_MONITOR_DELAY = 5;
		
		/** The connect timeout in milliseconds. */
		int CONNECT_TIMEOUT = 5000;
		
		/** The hadoop node path. */
		String HADOOP_NODE_PATH = "/hadoop-ha";
		
		/** The active bread crumb. */
		String ACTIVE_BREAD_CRUMB = "/ActiveBreadCrumb" ;
		
		/** The agent leader path. */
		String AGENT_LEADER_PATH = "/jumbune/agent/leader";

		/** The agent follower path. */
		String AGENT_FOLLOWER_PATH = "/jumbune/agent/follower";
	
}
