package org.jumbune.utils.conf.beans;

public class ExclusionCondition {

	private String user;
	
	private String queue;
	
	private String appName;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	@Override
	public String toString() {
		return "ExclusionCondition [user=" + user + ", queue=" + queue + ", appName=" + appName + "]";
	}

	

}
