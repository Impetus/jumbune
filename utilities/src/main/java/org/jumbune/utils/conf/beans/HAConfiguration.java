package org.jumbune.utils.conf.beans;

public class HAConfiguration {
	
	/**
	 * Heartbeats (from agent to jumbune) time interval in millisecs
	 */
	private int heartBeatMillis = 5000;
	
	/**
	 * Threshold(no. of beats missed) which declare jumbune-agent dead
	 */
	private int thresholdBeatsToMiss = 4;
	
	/**
	 * Number of retries to be made while picking active agent from zk
	 */
	private int numRetriesAgentConn = 10;
	
	/**
	 * Interval in millis to poll to verify a live agent connection
	 */
	private int agentConnMillis = 5000;
	
	/**
	 * Number of retries for a particular remoter API if it fails
	 */
	private int numRetriesRemoterApis = 3;
	
	/**
	 * Directory to record stdout and stderr or logs of various commands
	 */
	private String commandLogDir;

	public int getHeartBeatMillis() {
		return heartBeatMillis;
	}

	public void setHeartBeatMillis(int heartBeatMillis) {
		this.heartBeatMillis = heartBeatMillis;
	}

	public int getThresholdBeatsToMiss() {
		return thresholdBeatsToMiss;
	}

	public void setThresholdBeatsToMiss(int thresholdBeatsToMiss) {
		this.thresholdBeatsToMiss = thresholdBeatsToMiss;
	}

	public int getNumRetriesAgentConn() {
		return numRetriesAgentConn;
	}

	public void setNumRetriesAgentConn(int numRetriesAgentConn) {
		this.numRetriesAgentConn = numRetriesAgentConn;
	}

	public int getAgentConnMillis() {
		return agentConnMillis;
	}

	public void setAgentConnMillis(int agentConnMillis) {
		this.agentConnMillis = agentConnMillis;
	}

	public int getNumRetriesRemoterApis() {
		return numRetriesRemoterApis;
	}

	public void setNumRetriesRemoterApis(int numRetriesRemoterApis) {
		this.numRetriesRemoterApis = numRetriesRemoterApis;
	}

	public String getCommandLogDir() {
		return commandLogDir;
	}

	public void setCommandLogDir(String commandLogDir) {
		this.commandLogDir = commandLogDir;
	}

}
