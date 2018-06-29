package org.jumbune.common.beans;

public class JobInfo {
	
	private long submitTime;
	private long startTime;
	private long finishTime;
	private String id;
	private String name;
	private String queue ;
	private String user;
	private String state;
	private long mapsTotal;
	private long mapsCompleted;
	private long reducesTotal;
	private long reducesCompleted ;
	public long getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(long submitTime) {
		this.submitTime = submitTime;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQueue() {
		return queue;
	}
	public void setQueue(String queue) {
		this.queue = queue;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public long getMapsTotal() {
		return mapsTotal;
	}
	public void setMapsTotal(long mapsTotal) {
		this.mapsTotal = mapsTotal;
	}
	public long getMapsCompleted() {
		return mapsCompleted;
	}
	public void setMapsCompleted(long mapsCompleted) {
		this.mapsCompleted = mapsCompleted;
	}
	public long getReducesTotal() {
		return reducesTotal;
	}
	public void setReducesTotal(long reducesTotal) {
		this.reducesTotal = reducesTotal;
	}
	public long getReducesCompleted() {
		return reducesCompleted;
	}
	public void setReducesCompleted(long reducesCompleted) {
		this.reducesCompleted = reducesCompleted;
	}
	@Override
	public String toString() {
		return "JobInfo [submitTime=" + submitTime + ", startTime=" + startTime + ", finishTime=" + finishTime + ", id="
				+ id + ", name=" + name + ", queue=" + queue + ", user=" + user + ", state=" + state + ", mapsTotal="
				+ mapsTotal + ", mapsCompleted=" + mapsCompleted + ", reducesTotal=" + reducesTotal
				+ ", reducesCompleted=" + reducesCompleted + "]";
	}
	
	

}
