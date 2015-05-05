package org.jumbune.common.beans;

/***
 * This class holds the Scheduling configuration from user.
 *
 */
public class DataQualityTimeLineConfig {
	
	/***
	 * Schedule occurrences of Data Quality task
	 */
	private SchedulingEvent schedulingEvent = SchedulingEvent.DAILY;
	
	/**
	 * Enable Cron Expression
	 * 
	 */
	private Enable enableCronExpression = Enable.FALSE;
	
	/***
	 * Cron Expression
	 */
	private String cronExpression;
	
	/***
	 * time of launching Data Quality job
	 */
	private String time = null;
	
	/**
	 * remove scheduled job.
	 */
	private Enable removeJob = Enable.FALSE;
	
	/**
	 * shows job result
	 */
	private Enable showJobResult = Enable.FALSE;
	
	/**
	 * Job name to show or delete.
	 */
	private String jobName;
	
	/**
	 * Interval at job should be run
	 */
	private int interval;
	
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return the schedulingEvent
	 */
	public SchedulingEvent getSchedulingEvent() {
		return schedulingEvent;
	}

	/**
	 * @param schedulingEvent the schedulingEvent to set
	 */
	public void setSchedulingEvent(SchedulingEvent schedulingEvent) {
		this.schedulingEvent = schedulingEvent;
	}

	/**
	 * @return the showJobResult
	 */
	public Enable getShowJobResult() {
		return showJobResult;
	}

	/**
	 * @param showJobResult the showJobResult to set
	 */
	public void setShowJobResult(Enable showJobResult) {
		this.showJobResult = showJobResult;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Enable getRemoveJob() {
		return removeJob;
	}

	public void setRemoveJob(Enable removeJob) {
		this.removeJob = removeJob;
	}

	/**
	 * @return the interval
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/**
	 * @return the cronExpression
	 */
	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * @param cronExpression the cronExpression to set
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 * @return the enableCronExpression
	 */
	public Enable getEnableCronExpression() {
		return enableCronExpression;
	}

	/**
	 * @param enableCronExpression the enableCronExpression to set
	 */
	public void setEnableCronExpression(Enable enableCronExpression) {
		this.enableCronExpression = enableCronExpression;
	}

}