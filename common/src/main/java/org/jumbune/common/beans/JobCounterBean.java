package org.jumbune.common.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class JobCounterBean.
 * This class keeps track of different job counters bounded to their job name. 
 */
public class JobCounterBean {

	/** The process name. */
	private String processName;
	
	/** The job name. */
	private String jobName;
	
	/** The job stats map. */
	private Map<String,String> jobStatsMap=new HashMap<String, String>();
	
	
	/**
	 * Gets the process name.
	 *
	 * @return the process name
	 */
	public String getProcessName() {
		return processName;
	}
	
	/**
	 * Sets the process name.
	 *
	 * @param processName the new process name
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	/**
	 * Gets the job name.
	 *
	 * @return the job name
	 */
	public String getJobName() {
		return jobName;
	}
	
	/**
	 * Sets the job name.
	 *
	 * @param jobName the new job name
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	/**
	 * Gets the job stats map.
	 *
	 * @return the job stats map
	 */
	public Map<String, String> getJobStatsMap() {
		return jobStatsMap;
	}
	
	/**
	 * Sets the job stats map.
	 *
	 * @param jobStatsMap the job stats map
	 */
	public void setJobStatsMap(Map<String, String> jobStatsMap) {
		this.jobStatsMap = jobStatsMap;
	}
	

}
