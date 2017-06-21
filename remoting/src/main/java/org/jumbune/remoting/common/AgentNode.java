package org.jumbune.remoting.common;

import java.io.Serializable;

/**
 * The Class AgentNode stores information about jumbune agent.
 */
public class AgentNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8059906118761574341L;

	/** The agent host. */
	private String agentHost;

	/** The agent port. */
	private int agentPort;

	/** The agent user. */
	private String agentUser;

	/** The agent home dir. */
	private String agentHomeDir;

	/** The private key required for password-less ssh between agents. */
	private String privateKey;

	/** The status. */
	private AgentNodeStatus status;

	/**
	 * Gets the agent host.
	 *
	 * @return the agent host
	 */
	public String getHost() {
		return agentHost;
	}

	/**
	 * Sets the agent host.
	 *
	 * @param agentHost
	 *            the new agent host
	 */
	public void setHost(String agentHost) {
		this.agentHost = agentHost;
	}

	/**
	 * Gets the agent port.
	 *
	 * @return the agent port
	 */
	public Integer getPort() {
		return agentPort;
	}

	/**
	 * Sets the agent port.
	 *
	 * @param agentPort
	 *            the new agent port
	 */
	public void setPort(Integer agentPort) {
		this.agentPort = agentPort;
	}

	/**
	 * Gets the agent user.
	 *
	 * @return the agent user
	 */
	public String getAgentUser() {
		return agentUser;
	}

	/**
	 * Sets the agent user.
	 *
	 * @param agentUser
	 *            the new agent user
	 */
	public void setAgentUser(String agentUser) {
		this.agentUser = agentUser;
	}

	/**
	 * Gets the agent home dir.
	 *
	 * @return the agent home dir
	 */
	public String getAgentHomeDir() {
		return agentHomeDir;
	}

	/**
	 * Sets the agent home dir.
	 *
	 * @param agentHomeDir
	 *            the new agent home dir
	 */
	public void setAgentHomeDir(String agentHomeDir) {
		this.agentHomeDir = agentHomeDir;
	}

	/**
	 * Gets the private key.
	 *
	 * @return the private key
	 */
	public String getPrivateKey() {
		return privateKey;
	}

	/**
	 * Sets the private key.
	 *
	 * @param privateKey
	 *            the new private key
	 */
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public AgentNodeStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status
	 *            the new status
	 */
	public void setStatus(AgentNodeStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "AgentNode [agentHost=" + agentHost + ", agentPort=" + agentPort + ", agentUser=" + agentUser
				+ ", agentHomeDir=" + agentHomeDir + ", privateKey=" + privateKey + ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agentHost == null) ? 0 : agentHost.hashCode());
		result = prime * result + agentPort;
		result = prime * result + ((agentUser == null) ? 0 : agentUser.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgentNode other = (AgentNode) obj;
		if (agentHost == null) {
			if (other.agentHost != null)
				return false;
		} else if (!agentHost.equals(other.agentHost))
			return false;
		if (agentPort != other.agentPort)
			return false;
		if (agentUser == null) {
			if (other.agentUser != null)
				return false;
		} else if (!agentUser.equals(other.agentUser))
			return false;
		return true;
	}

	

}