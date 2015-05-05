package org.jumbune.common.beans;

public enum SchedulingEvent {
	MINUTE("minute"), HOURLY("hourly"), WEEKLY("weekly"), DAILY("daily"), MONTHLY(
			"monthly");

	String event;

	SchedulingEvent(String event) {
		this.event = event;
	}

}
