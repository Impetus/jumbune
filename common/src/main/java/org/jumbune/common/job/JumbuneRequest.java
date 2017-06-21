package org.jumbune.common.job;

import org.jumbune.common.beans.cluster.Cluster;

public class JumbuneRequest {
	
	private Config config;
	private Cluster cluster;
	
	public Config getConfig() {
		return config;
	}
	public void setConfig(Config config) {
		this.config = config;
	}
	public Cluster getCluster() {
		return cluster;
	}
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	public JobConfig getJobConfig(){
		return (JobConfig) config;
	}
	@Override
	public String toString() {
		return "JumbuneRequest [config=" + config + ", cluster=" + cluster + "]";
	}
	
}
