package org.jumbune.common.beans.cluster;

import java.util.List;

public class Agents {
	
	/** The common username across all agents */
	private String user;
	
	/**
	 * The list of agents
	 */
	private List<Agent> agents;
	
	/**
	 * Do we have password less access for running commands on Resource Manager or not?
	 */
	private boolean hasPasswordlessAccess;
	
	/**
	 * Password for running commands on Resource Manager
	 */
	private String password;
	
	/**
	 *  The ssh Auth Keys file. 
	 **/
	private String sshAuthKeysFile;

	private boolean haEnabled;
			
	public boolean isHaEnabled() {
		return haEnabled;
	}

	public void setHaEnabled(boolean haEnabled) {
		this.haEnabled = haEnabled;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}	
	
	public List<Agent> getAgents() {
		return agents;
	}

	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}

	public boolean isHasPasswordlessAccess() {
		return hasPasswordlessAccess;
	}

	public void setHasPasswordlessAccess(boolean hasPasswordlessAccess) {
		this.hasPasswordlessAccess = hasPasswordlessAccess;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSshAuthKeysFile() {
		return sshAuthKeysFile;
	}

	public void setSshAuthKeysFile(String sshAuthKeysFile) {
		this.sshAuthKeysFile = sshAuthKeysFile;
	}

	@Override
	public String toString() {
		return "Agents [user=" + user + ", agents=" + agents
				+ ", hasPasswordlessAccess=" + hasPasswordlessAccess
				+ ", password=Redacted, sshAuthKeysFile="
				+ sshAuthKeysFile + "]";
	}
	
}
