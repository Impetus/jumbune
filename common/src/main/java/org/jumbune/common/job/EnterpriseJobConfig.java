package org.jumbune.common.job;

/**
 * The Class EnterpriseJobConfig.
 */
public class EnterpriseJobConfig extends JobConfig {		
	
	/** String specifying time of jumbune task to be scheduled. */
	private String jumbuneScheduleTaskTiming;
	
	
	//private Enable isDataCleansingEnabled = Enable.FALSE;

	
	/**
	 * Instantiates a new enterprise job config.
	 */
	public EnterpriseJobConfig(){
		super();
		
	}
	
	/**
	 * Gets the jumbune schedule task timing.
	 *
	 * @return the jumbune schedule task timing
	 */
	public String getJumbuneScheduleTaskTiming() {
		return jumbuneScheduleTaskTiming;
	}
	
	/*public Enable getIsDataCleansingEnabled(){
		return isDataCleansingEnabled;
	}

	public void setIsDataCleansingEnabled(Enable isDataCleansingEnabled){
		this.isDataCleansingEnabled = isDataCleansingEnabled;
	}*/

	/**
	 * Sets the jumbune schedule task timing.
	 *
	 * @param jumbuneScheduleTaskTiming the new jumbune schedule task timing
	 */
	public void setJumbuneScheduleTaskTiming(String jumbuneScheduleTaskTiming) {
		this.jumbuneScheduleTaskTiming = jumbuneScheduleTaskTiming;
	}
	

	@Override
	public String toString() {
		return "EnterpriseJobConfig [enterpriseJobConfig="+super.toString()
				 + ", jumbuneScheduleTaskTiming=" + jumbuneScheduleTaskTiming
				+ "]";
	}
	
}
