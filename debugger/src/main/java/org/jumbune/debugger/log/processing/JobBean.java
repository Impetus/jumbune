package org.jumbune.debugger.log.processing;

import java.util.Map;

/**
 * This is the class which contains parameters required corresponding to a Job.
 */
public class JobBean extends AbstractLogAnalysisBean {

	/**
	 * jobMap - the map containing analysis results of various Mappers and Reducers for the Job.
	 */
	private Map<String, MapReduceBean> jobMap;

	/**
	 * @return the jobMap
	 */
	public final Map<String, MapReduceBean> getJobMap() {
		return jobMap;
	}

	/**
	 * @param jobMap
	 *            the jobMap to set
	 */
	public final void setJobMap(final Map<String, MapReduceBean> jobMap) {
		this.jobMap = jobMap;
	}

}
