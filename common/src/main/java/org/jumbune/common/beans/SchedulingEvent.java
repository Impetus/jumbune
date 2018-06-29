package org.jumbune.common.beans;

public enum SchedulingEvent {
	MINUTE("MINUTE"), HOURLY("HOURLY"), WEEKLY("WEEKLY"), DAILY("DAILY"), MONTHLY(
			"MONTHLY");

	String event;

	SchedulingEvent(String event) {
		this.event = event;
	}

}
