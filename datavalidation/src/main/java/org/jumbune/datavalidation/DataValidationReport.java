package org.jumbune.datavalidation;

import java.util.List;
import java.util.Map;


/**
 * Pojo to store data violation result.
 */
public class DataValidationReport {

	/** total number of violations for a particular type of check. */
	private long totalViolations;
	
	private long dirtyTuple;
	
	/** Map containing number of violations corresponding each field for a particular data validation check. */
	private Map<Integer, Long> fieldMap;

	/** List of violations to be displayed on UI. */
	private List<FileViolationsWritable> violationList;

	/**
	 * Gets the total violations.
	 *
	 * @return the totalViolations
	 */
	public long getTotalViolations() {
		return totalViolations;
	}

	/**
	 * Sets the total violations.
	 *
	 * @param totalViolations the totalViolations to set
	 */
	public void setTotalViolations(long totalViolations) {
		this.totalViolations = totalViolations;
	}

	/**
	 * Gets the field map.
	 *
	 * @return the fieldMap
	 */
	public Map<Integer, Long> getFieldMap() {
		return fieldMap;
	}

	/**
	 * Sets the field map.
	 *
	 * @param fieldMap the fieldMap to set
	 */
	public void setFieldMap(Map<Integer, Long> fieldMap) {
		this.fieldMap = fieldMap;
	}

	/**
	 * Gets the violation list.
	 *
	 * @return the violationList
	 */
	public List<FileViolationsWritable> getViolationList() {
		return violationList;
	}

	/**
	 * Sets the violation list.
	 *
	 * @param violationList the violationList to set
	 */
	public void setViolationList(List<FileViolationsWritable> violationList) {
		this.violationList = violationList;
	}

	/**
	 * @return the infectedTuple
	 */
	public long getDirtyTuple() {
		return dirtyTuple;
	}

	/**
	 * @param dirtyTuple the infectedTuple to set
	 */
	public void setDirtyTuple(long dirtyTuple) {
		this.dirtyTuple = dirtyTuple;
	}

	@Override
	public String toString() {
		return "DataValidationReport [totalViolations=" + totalViolations + ", dirtyTuple=" + dirtyTuple
				+ ", fieldMap=" + fieldMap + ", violationList=" + violationList + "]";
	}
	
}
