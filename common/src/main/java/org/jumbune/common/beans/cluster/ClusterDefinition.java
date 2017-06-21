package org.jumbune.common.beans.cluster;

import java.util.List;

public abstract class ClusterDefinition implements Cluster{

    protected String clusterName; 
	
	private NameNodes nameNodes;
	
	protected HadoopUsers hadoopUsers;
		
	private Agents agents;
	
	/** The host range from value. */
	protected String hostRangeFromValue;
	
	/** The host range to value. */
	protected String hostRangeToValue;
	
	/** The enable host range. */
	protected String enableHostRange;
	
	/** The unavailable workers. */
	private List<String> unavailableHosts;
	
	private TaskManagers taskManagers;
	
	private Workers workers;
	
	private boolean jmxPluginEnabled;
	
	public String[] getZKs() {
		return null;		
	}
	
	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public NameNodes getNameNodes() {
		return nameNodes;
	}

	public void setNameNodes(NameNodes nameNodes) {
		this.nameNodes = nameNodes;
	}

	public Agents getAgents() {
		return agents;
	}

	public void setAgents(Agents agents) {
		this.agents = agents;
	}

	public String getHostRangeFromValue() {
		return hostRangeFromValue;
	}

	public void setHostRangeFromValue(String hostRangeFromValue) {
		this.hostRangeFromValue = hostRangeFromValue;
	}

	public String getHostRangeToValue() {
		return hostRangeToValue;
	}

	public void setHostRangeToValue(String hostRangeToValue) {
		this.hostRangeToValue = hostRangeToValue;
	}

	public String getEnableHostRange() {
		return enableHostRange;
	}

	public void setEnableHostRange(String enableHostRange) {
		this.enableHostRange = enableHostRange;
	}

	public List<String> getUnavailableHosts() {
		return unavailableHosts;
	}

	public void setUnavailableHosts(List<String> unavailableHosts) {
		this.unavailableHosts = unavailableHosts;
	}

	public HadoopUsers getHadoopUsers() {
		return hadoopUsers;
	}

	public void setHadoopUsers(HadoopUsers hadoopUsers) {
		this.hadoopUsers = hadoopUsers;
	}

	public TaskManagers getTaskManagers() {
		return taskManagers;
	}

	public void setTaskManagers(TaskManagers taskManagers) {
		this.taskManagers = taskManagers;
	}

	public Workers getWorkers() {
		return workers;
	}

	public void setWorkers(Workers workers) {
		this.workers = workers;
	}

	@Override
	public String getNameNode() {
		return nameNodes.getHosts().get(0);
	}

	@Override
	public Agent getJumbuneAgent() {
		return agents.getAgents().get(0);
	}

	public boolean isJmxPluginEnabled() {
		return jmxPluginEnabled;
	}

	public void setJmxPluginEnabled(boolean jmxPluginEnabled) {
		this.jmxPluginEnabled = jmxPluginEnabled;
	}

	@Override
	public String toString() {
		return "ClusterDefinition [clusterName=" + clusterName + ", nameNodes=" + nameNodes + ", hadoopUsers="
				+ hadoopUsers + ", agents=" + agents + ", hostRangeFromValue=" + hostRangeFromValue
				+ ", hostRangeToValue=" + hostRangeToValue + ", enableHostRange=" + enableHostRange
				+ ", unavailableHosts=" + unavailableHosts + ", taskManagers=" + taskManagers + ", workers=" + workers
				+ ", jmxPluginEnabled=" + jmxPluginEnabled + "]";
	}
	
}