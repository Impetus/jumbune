package org.jumbune.common.beans;

public enum JobStatus {
	
	SCHEDULED("SCHEDULED"), IN_PROGRESS("IN_PROGRESS"), COMPLETED("COMPLETED");
	
	private String status;
	
	JobStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String toString() {
		return status;
	}
	
	
}
