package org.jumbune.clusteranalysis.beans;

public class RackAwareStats {
	
	private String jobID;
	
	private Long dataLocalJob;  
	
	private Long rackLocalJob;
	
	private Long otherLocalJob;

	public String getJobID() {
		return jobID;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public long getDataLocalJob() {
		return dataLocalJob;
	}

	public void setDataLocalJob(long dataLocalJob) {
		this.dataLocalJob = dataLocalJob;
	}

	public long getRackLocalJob() {
		return rackLocalJob;
	}

	public void setRackLocalJob(long rackLocalJob) {
		this.rackLocalJob = rackLocalJob;
	}

	public long getOtherLocalJob() {
		return otherLocalJob;
	}

	public void setOtherLocalJob(long otherLocalJob) {
		this.otherLocalJob = otherLocalJob;
	}
	
	

}
