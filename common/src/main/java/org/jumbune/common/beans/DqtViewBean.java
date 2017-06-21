package org.jumbune.common.beans;

public class DqtViewBean {
	
	private String hdfsPath ;
	
	private String jobName ;
	
	private String recuringInterval ;
	
	private String lastExecutedTime ;
	
	private String operatingCluster;

	public String getHdfsPath() {
		return hdfsPath;
	}

	public void setHdfsPath(String hdfsPath) {
		this.hdfsPath = hdfsPath;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getRecuringInterval() {
		return recuringInterval;
	}

	public void setRecuringInterval(String recuringInterval) {
		this.recuringInterval = recuringInterval;
	}

	public String getLastExecutedTime() {
		return lastExecutedTime;
	}

	public void setLastExecutedTime(String lastExecutedTime) {
		this.lastExecutedTime = lastExecutedTime;
	}

	public String getOperatingCluster() {
		return operatingCluster;
	}

	public void setOperatingCluster(String operatingCluster) {
		this.operatingCluster = operatingCluster;
	}

	@Override
	public String toString() {
		return "DqtViewBean [hdfsPath=" + hdfsPath + ", jobName=" + jobName + ", recuringInterval=" + recuringInterval
				+ ", lastExecutedTime=" + lastExecutedTime + ", operatingCluster=" + operatingCluster + "]";
	}	
	
}
