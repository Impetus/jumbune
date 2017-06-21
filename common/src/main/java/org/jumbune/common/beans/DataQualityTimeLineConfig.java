package org.jumbune.common.beans;

/***
 * This class holds the Scheduling configuration from user.
 *
 */
public class DataQualityTimeLineConfig {
	
	/**
	 * Enable Cron Expression
	 * 
	 */
	private String scheduleJob ;
	
	/***
	 * Cron Expression
	 */
	private String cronExpression;

	/***
	 * Schedule occurrences of Data Quality task
	 */
	private SchedulingEvent schedulingEvent = SchedulingEvent.DAILY;
	
	/**
	 * Interval at job should be run
	 */
	private Integer interval;
	
	/***
	 * time of launching Data Quality job (if schedulingEvent is DAILY)
	 */
	private String time = null;
	
	private String browserGMT;
	
	/**
	 * remove scheduled job.
	 */
	private Enable removeJob = Enable.FALSE;
	
	/**
	 * shows job result
	 */
	private Enable showJobResult = Enable.FALSE;
	
	
	
	/** The data validation. */
	private DataValidationBean dataValidation ;
	
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

	public Enable getRemoveJob() {
		return removeJob;
	}

	public void setRemoveJob(Enable removeJob) {
		this.removeJob = removeJob;
	}

	/**
	 * @return the interval
	 */
	public Integer getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(Integer interval) {
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
	 * Gets the data validation.
	 *
	 * @return the data validation
	 */
	public DataValidationBean getDataValidation() {
		return dataValidation;
	}

	/**
	 * Sets the data validation.
	 *
	 * @param dataValidation the new data validation
	 */
	public void setDataValidation(DataValidationBean dataValidation) {
		this.dataValidation = dataValidation;
	}

	public String getScheduleJob() {
		return scheduleJob;
	}

	public void setScheduleJob(String scheduleJob) {
		this.scheduleJob = scheduleJob;
	}

	@Override
	public String toString() {
		return "DataQualityTimeLineConfig [schedulingEvent=" + schedulingEvent + ", scheduleJob=" + scheduleJob
				+ ", cronExpression=" + cronExpression + ", time=" + time + ", removeJob=" + removeJob
				+ ", showJobResult=" + showJobResult + ", interval=" + interval + ", dataValidation=" + dataValidation
				+ "]";
	}

	public String getBrowserGMT() {
		return browserGMT;
	}

	public void setBrowserGMT(String browserGMT) {
		this.browserGMT = browserGMT;
	}

	

}