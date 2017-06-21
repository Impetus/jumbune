package org.jumbune.common.utils;

import org.jumbune.common.beans.cluster.Agent;
import org.jumbune.common.beans.cluster.Agents;
import org.jumbune.remoting.common.AgentNode;

public class AgentNodeUtil {

	/**
	 * This method created Agent instance from given Agent Node instance
	 * @param node, the agent node
	 * @return
	 */
	public static Agent convertAgentNodeToAgent(AgentNode node) {
		Agent agent = new Agent();
		agent.setHost(node.getHost());
		agent.setPort(String.valueOf(node.getPort()));
		return agent;
	}
	
	/**
	 * This method tries to build AgentNode from Agent. This doesn't fills AgentHomeDir in the returned AgentNode instance
	 * @param agents, use SSH information and user name from agents
	 * @param agent, use host, port and status 
	 * @return
	 */
	public static AgentNode convertAgentToAgentNode(Agents agents, Agent agent) {
		AgentNode node = new AgentNode();
		node.setHost(agent.getHost());
		node.setPort(Integer.parseInt(agent.getPort()));
		node.setPrivateKey(agents.getSshAuthKeysFile());
		node.setAgentUser(agents.getUser());
		return node;
	}
	
}