package org.jumbune.common.beans;

import org.jumbune.utils.JobUtil;



/**
 * This class is the bean for the master entries for log collection and lib distribution.
 */
public class Master {
	
	/** The user. */
	private String user;
	
	/** The host. */
	private String host;
	
	/** The location. */
	private String location;
	
	/** The rsa file. */
	private String rsaFile;
	
	/** The dsa file. */
	private String dsaFile;

	/** The agent port. */
	private String agentPort;
	
	/** The receive directory. */
	private String receiveDirectory;
	
	/** The name node jmx port. */
	private String nameNodeJmxPort;
	
	/** The job tracker jmx port. */
	private String jobTrackerJmxPort;
	
	/** The availability of node */
	private boolean isNodeAvailable;
	

	/**
	 * Gets the agent port.
	 *
	 * @return the agent port
	 */
	public String getAgentPort() {
		return agentPort;
	}

	/**
	 * Sets the agent port.
	 *
	 * @param agentPort the new agent port
	 */
	public void setAgentPort(String agentPort) {
		this.agentPort = agentPort;
	}

	/**
	 * Gets the receive directory.
	 *
	 * @return the receive directory
	 */
	public String getReceiveDirectory() {
		return receiveDirectory;
	}

	/**
	 * Sets the receive directory.
	 *
	 * @param receiveDirectory the new receive directory
	 */
	public void setReceiveDirectory(String receiveDirectory) {
		this.receiveDirectory = receiveDirectory;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(String user) {
		this.user = JobUtil.getAndReplaceHolders(user);
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public final String getHost() {
		return host;
	}

	/**
	 * Sets the host.
	 *
	 * @param host the new host
	 */
	public final void setHost(String host) {
		this.host = host;
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public final String getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 *
	 * @param location the new location
	 */
	public final void setLocation(final String location) {
		String locationTemp =location;
		if (locationTemp != null) {
			locationTemp = JobUtil.getAndReplaceHolders(locationTemp);
			if (!JobUtil.validateFileSystemLocation(locationTemp)) {
				throw new IllegalArgumentException("Location provided in Master is not in correct format!!");
			}
			this.location = locationTemp;
		}
	}

	/**
	 * <p>
	 * See {@link #setrsaFile(String)}
	 * </p>.
	 *
	 * @return Returns the rsaFile.
	 */
	public final String getRsaFile() {
		return rsaFile;
	}

	/**
	 * <p>
	 * Set the value of <code>rsaFile</code>.
	 * </p>
	 * 
	 * @param rsaFile
	 *            The rsaFile to set.
	 */
	public final void setRsaFile(String rsaFile) {
		this.rsaFile = JobUtil.getAndReplaceHolders(rsaFile);
	}

	/**
	 * <p>
	 * See {@link #setdsaFile(String)}
	 * </p>.
	 *
	 * @return Returns the dsaFile.
	 */
	public final String getDsaFile() {
		return dsaFile;
	}

	/**
	 * <p>
	 * Set the value of <code>dsaFile</code>.
	 * </p>
	 * 
	 * @param dsaFile
	 *            The dsaFile to set.
	 */
	public final void setDsaFile(String dsaFile) {
		this.dsaFile = JobUtil.getAndReplaceHolders(dsaFile);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Master [user=" + user + ", host=" + host + ", location=" + location + ", rsaFile=" + rsaFile + ", dsaFile=" + dsaFile + "]";
	}

	

	/**
	 * Sets the job tracker jmx port.
	 *
	 * @param jobTrackerJmxPort the new job tracker jmx port
	 */
	public void setJobTrackerJmxPort(String jobTrackerJmxPort) {
		this.jobTrackerJmxPort = jobTrackerJmxPort;
	}

	/**
	 * Gets the job tracker jmx port.
	 *
	 * @return the job tracker jmx port
	 */
	public String getJobTrackerJmxPort() {
		return jobTrackerJmxPort;
	}

	/**
	 * Sets the name node jmx port.
	 * @param nameNodeJmxPort the new name node jmx port
	 */
	public void setNameNodeJmxPort(String nameNodeJmxPort) {
		this.nameNodeJmxPort = nameNodeJmxPort;
	}

	/**
	 * Gets the name node jmx port.
	 *
	 * @return the name node jmx port
	 */
	public String getNameNodeJmxPort() {
		return nameNodeJmxPort;
	}

	/**
	 * Sets the node available property
	 * @param isNodeAvailable
	 */
	public void setIsNodeAvailable(boolean isNodeAvailable) {
		this.isNodeAvailable = isNodeAvailable;
	}

	/**
	 * Gets the node availability
	 * @return boolean for node availability
	 */
	public boolean isAvailable() {
		return isNodeAvailable;
	}
}