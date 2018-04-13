package org.jumbune.common.beans;

import java.util.ArrayList;
import java.util.List;

public class CpuStats implements Stats {

	private long executionTime;
	private int no_of_nodes;
	private long max_memory_available; // job tracker
	private int threadSlotsPerCore;
	private int coresPerNode;
	private List<JobStats> jobstats = new ArrayList<JobStats>();

	public long getExecutionTime() {
		for (JobStats jobstat : jobstats) {
			executionTime += jobstat.getExecutionTime();
		}
		return executionTime;
	}

	public int getNo_of_nodes() {
		return no_of_nodes;
	}

	public void setNo_of_nodes(int no_of_nodes) {
		this.no_of_nodes = no_of_nodes;
	}

	public long getMaxMemoryAvailable() {
		return max_memory_available;
	}

	public void setMaxMemoryAvailable(long max_memory_availaile) {
		this.max_memory_available = max_memory_availaile;
	}

	public List<JobStats> getJobstats() {
		return jobstats;
	}

	public void setJobstats(List<JobStats> jobstats) {
		this.jobstats = jobstats;
	}

	public int getThreadSlotsPerCore() {
		return threadSlotsPerCore;
	}

	public void setThreadSlotsPerCore(int threadSlotsPerCore) {
		this.threadSlotsPerCore = threadSlotsPerCore;
	}

	public int getCoresPerNode() {
		return coresPerNode;
	}

	public void setCoresPerNode(int coresPerNode) {
		this.coresPerNode = coresPerNode;
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("CpuStats [ no_of_nodes : " + no_of_nodes + " || max_memory_availaile : " + max_memory_available + " || JobStats : "); // job tracker

		if (jobstats != null)
			for (JobStats js : this.jobstats) {
				b.append(js);
			}

		return b.toString();

	}

}
