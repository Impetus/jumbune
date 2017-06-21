package org.jumbune.common.beans.cluster;

import java.util.List;

public class TaskManagers {
	
	/** JobTrackerJmxPort, ResourceManagerJmxPort. */
	private String taskManagerJmxPort;
	
	/** The user. */
	private String user;
	
	/** The host. */
	private List<String> hosts;
	
	
	private String active;

	private boolean rmHaEnabled;

	/**
	 * Do we have password less access for running commands on Resource Manager or not?
	 */
	private boolean hasPasswordlessAccess;
	
	/**
	 * Password for running commands on Resource Manager
	 */
	private String password;
	
	/** The ssh Auth Keys file. */
	private String sshAuthKeysFile;
	
	public String getTaskManagerJmxPort() {
		return taskManagerJmxPort;
	}

	public void setTaskManagerJmxPort(String taskManagerJmxPort) {
		this.taskManagerJmxPort = taskManagerJmxPort;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<String> getHosts() {
		return hosts;
	}

	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}

	/**
	 * @return the active
	 */
	public String getActive() {
		return hosts.get(0);
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(String active) {
		this.active = active;
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

	public boolean isRmHaEnabled() {
		return rmHaEnabled;
	}

	public void setRmHaEnabled(boolean rmHaEnabled) {
		this.rmHaEnabled = rmHaEnabled;
	}
	
	@Override
	public String toString() {
		return "TaskManagers [taskManagerJmxPort=" + taskManagerJmxPort
				+ ", user=" + user + ", hosts=" + hosts
				+ ", hasPasswordlessAccess=" + hasPasswordlessAccess
				+ ", password=Redacted, sshAuthKeysFile="
				+ sshAuthKeysFile + "]";
	}
	
	
}
