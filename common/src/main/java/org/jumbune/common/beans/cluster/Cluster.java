package org.jumbune.common.beans.cluster;

public interface Cluster {
	
	String getClusterName();
		
	NameNodes getNameNodes();
	
	Agents getAgents();
	
	Workers getWorkers();
	
	TaskManagers getTaskManagers();
	
	String getHostRangeFromValue();
	
	String getHostRangeToValue();
	
	String getEnableHostRange();
	
	String getNameNode();
	
	Agent getJumbuneAgent();
	
	HadoopUsers getHadoopUsers();
	
	boolean isJmxPluginEnabled();
	
	public String[] getZKs();
	
	/**
	 * It returns history server ip
	 * @return
	 */
	String getHistoryServer();
	
	/**
	 * @return history server's socket address
	 */
	String getMRSocketAddress();
	
	
	/**
	 * Returns current active resource manager ip
	 * @return
	 */
	String getResourceManager();
	
	/**
	 *  It is used to create RMCommunicator
	 * @return resourcemanager's socket address
	 */
	String getRMSocketAddress();
	
	
	/**
	 * It is used to create resource manager webapp address
	 * @return
	 */
	String getRMWebAppAddress();	
}

