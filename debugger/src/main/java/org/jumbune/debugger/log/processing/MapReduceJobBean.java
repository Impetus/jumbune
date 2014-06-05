package org.jumbune.debugger.log.processing;

/**
 * The Class MapReduceJobBean.
 */
public class MapReduceJobBean {

	/** job/mapper/reducer class name */
	private String jobMapReduceName;

	public String getJobMapReduceName() {
		return jobMapReduceName;
	}

	public void setJobMapReduceName(String jobMapReduceName) {
		this.jobMapReduceName = jobMapReduceName;
	}

	public int getTotalUnmatchedKeys() {
		return totalUnmatchedKeys;
	}

	public void setTotalUnmatchedKeys(int totalUnmatchedKeys) {
		this.totalUnmatchedKeys = totalUnmatchedKeys;
	}

	public int getTotalUnmatchedValues() {
		return totalUnmatchedValues;
	}

	public void setTotalUnmatchedValues(int totalUnmatchedValues) {
		this.totalUnmatchedValues = totalUnmatchedValues;
	}

	/** totalInputKeys - Total number of unmatched keys compared against regex provided by user. */
	private int totalUnmatchedKeys = -1;

	/** totalInputKeys - Total number of unmatched values compared against regex provided by user. */
	private int totalUnmatchedValues = -1;
}
