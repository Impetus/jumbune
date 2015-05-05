package org.jumbune.common.utils;

import java.util.Calendar;
import java.util.Date;

import org.jumbune.common.beans.SchedulingEvent;

 /*
 *  This class is used for generating Cron expressions.
 */
public class CronGenerator {
	
	/* White space */
	private static final String WHITE_SPACE = " ";
	
	/*
	 * Calendar reference
	 */
	private final Calendar mCal;
	
	/**
	 * Constructor for creating CronGenerator instance 
	 * @param date takes Date class object as input
	 */
	public CronGenerator(Date date) {
		mCal = Calendar.getInstance();
		mCal.setTime(date);
	}
	
	/**
	 * Generates Cron expressions for the date given at the time of creating instance of this class.
	 * @return String Cron expression for executing job in given time. 
	 */
	public String generateCronExpression() {
		StringBuilder cronExpression = new StringBuilder();
		cronExpression.append(mCal.get(Calendar.MINUTE)).append(WHITE_SPACE)
				.append(mCal.get(Calendar.HOUR_OF_DAY)).append(WHITE_SPACE)
				.append("* * *");
		return cronExpression.toString();
	}
	
	/**
	 * Generate Cron expressions based on scheduling event and frequency of executing it.
	 * @param schedulingEvent {@link SchedulingEvent}
	 * @return String Cron expression for executing job in given time. 
	 */
	public String generateCronExpression(SchedulingEvent schedulingEvent) {
		StringBuilder cronExpression = new StringBuilder();
		cronExpression.append(mCal.get(Calendar.MINUTE)).append(WHITE_SPACE);
		switch (schedulingEvent) {
		case MINUTE:
			cronExpression.setLength(0);
			cronExpression.append(" * * * * *");
			break;
		case HOURLY:
			cronExpression.append(" * * * *");
			break;
		case DAILY:
			cronExpression.append(mCal.get(Calendar.HOUR_OF_DAY))
					.append(WHITE_SPACE).append("* * *");
			break;
		case WEEKLY:
			cronExpression.append(mCal.get(Calendar.HOUR_OF_DAY))
					.append(" * * ").append(mCal.get(Calendar.DAY_OF_WEEK)-1);
			break;
		case MONTHLY:
			cronExpression.append(WHITE_SPACE)
					.append(mCal.get(Calendar.DAY_OF_MONTH)).append(" * *");
			break;
		}
		return cronExpression.toString();
	}
	
	/**
	 * Generates Cron expressions based on scheduling event and frequency of executing it.
	 * @param schedulingEvent {@link SchedulingEvent}
	 * @param interval number of occurrences of a repeating event.
	 * @return String Cron expression for executing job in given time. 
	 */
	public String generateCronExpression(SchedulingEvent schedulingEvent,
			int interval) {
		
		String tempCronExpression = null;
		StringBuilder cronExpression = new StringBuilder();
		cronExpression.append(mCal.get(Calendar.MINUTE)).append(WHITE_SPACE);
		
		switch (schedulingEvent) {
		case HOURLY:
			tempCronExpression = "0-23/"+ interval;
			cronExpression.append(tempCronExpression).append(WHITE_SPACE).append("* * *");
			break;
		case DAILY:
			cronExpression.append(mCal.get(Calendar.HOUR_OF_DAY))
					.append(WHITE_SPACE);
			tempCronExpression = "1-31/"+interval;
			cronExpression.append(tempCronExpression).append(WHITE_SPACE).append("* *");
			break;
		}
		return cronExpression.toString();
	}

}
