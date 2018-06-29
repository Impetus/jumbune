package org.jumbune.utils.conf.beans;

public enum AlertLevel {
	
	Critical("Critical"), Warning("Warning");
	
	private String level;
	
	AlertLevel(String level) {
		this.level = level;
	}
	
	public String getLevel() {
		return level;
	}
	
}
