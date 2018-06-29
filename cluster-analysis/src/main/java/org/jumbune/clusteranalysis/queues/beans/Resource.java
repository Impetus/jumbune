package org.jumbune.clusteranalysis.queues.beans;

public class Resource {

	private long memory;
	private long vCores;

	public long getMemory() {
		return memory;
	}

	public void setMemory(long memory) {
		this.memory = memory;
	}

	public long getvCores() {
		return vCores;
	}

	public void setvCores(long vCores) {
		this.vCores = vCores;
	}
}
