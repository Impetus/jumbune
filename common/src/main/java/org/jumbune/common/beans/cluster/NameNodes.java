package org.jumbune.common.beans.cluster;

import java.util.List;

public class NameNodes {

	/** The name node jmx port. */
	private String nameNodeJmxPort;

	/** The location. */
	private String location;

	/** The receive directory. */
	private String receiveDirectory;
	
	/** The user. */
	private String user;
	
	/** The host. */
	private List<String> hosts;
	
	private String relativeWorkingDirectory;
	
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

 private boolean haEnabled;
			
	public boolean isHaEnabled() {
		return haEnabled;
	}

	public void setHaEnabled(boolean haEnabled) {
		this.haEnabled = haEnabled;
	}

	public String getNameNodeJmxPort() {
		return nameNodeJmxPort;
	}

	public void setNameNodeJmxPort(String nameNodeJmxPort) {
		this.nameNodeJmxPort = nameNodeJmxPort;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getReceiveDirectory() {
		return receiveDirectory;
	}

	public void setReceiveDirectory(String receiveDirectory) {
		this.receiveDirectory = receiveDirectory;
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

	public String getRelativeWorkingDirectory() {
		return relativeWorkingDirectory;
	}

	public void setRelativeWorkingDirectory(String workingDirectory) {
		this.relativeWorkingDirectory = workingDirectory;
	}

	@Override
	public String toString() {
		return "NameNodes [nameNodeJmxPort=" + nameNodeJmxPort + ", location="
				+ location + ", receiveDirectory=" + receiveDirectory
				+ ", user=" + user + ", hosts=" + hosts
				+ ", relativeWorkingDirectory=" + relativeWorkingDirectory
				+ ", hasPasswordlessAccess=" + hasPasswordlessAccess
				+ ", password=Redacted, sshAuthKeysFile="
				+ sshAuthKeysFile +"]";
	}
	
}
