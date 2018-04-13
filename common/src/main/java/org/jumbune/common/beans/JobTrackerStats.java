package org.jumbune.common.beans;

import java.util.ArrayList;
import java.util.List;

import org.jumbune.utils.beans.NativeJobId;

public class JobTrackerStats extends JMXStats{

	private long usedMemory;
	private List<NativeJobId> jobs;

	public JobTrackerStats(String host, int port) {
		super(host, port);
		this.jobs = new ArrayList<NativeJobId>();
	}

	public List<NativeJobId> getJobs() {
		return jobs;
	}

	public void setJobs(List<NativeJobId> jobs) {
		this.jobs = jobs;
	}

	public long getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(long usedMemory) {
		this.usedMemory = getAvg(this.usedMemory, usedMemory);
	}

	protected Long getAvg(Long past, Long present) {

		if (present == null && past != null && past > 0)
			return past;
		if (past == null || past == 0)
			return present;
		else if (present > 0 && past > 0)
			return (past + present) / 2;
		else
			return past;
	}

}
