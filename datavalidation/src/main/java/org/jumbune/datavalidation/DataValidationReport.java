package org.jumbune.datavalidation;

import java.util.List;
import java.util.Map;


/**
 * Pojo to store data violation result.
 */
public class DataValidationReport {

	/** total number of violations for a particular type of check. */
	private int totalViolations;
	
	private int dirtyTuple;
	
	private int cleanTuple;

	/** Map containing number of violations corresponding each field for a particular data validation check. */
	private Map<Integer, Integer> fieldMap;

	/** List of violations to be displayed on UI. */
	private List<FileViolationsWritable> violationList;

	/**
	 * Gets the total violations.
	 *
	 * @return the totalViolations
	 */
	public int getTotalViolations() {
		return totalViolations;
	}

	/**
	 * Sets the total violations.
	 *
	 * @param totalViolations the totalViolations to set
	 */
	public void setTotalViolations(int totalViolations) {
		this.totalViolations = totalViolations;
	}

	/**
	 * Gets the field map.
	 *
	 * @return the fieldMap
	 */
	public Map<Integer, Integer> getFieldMap() {
		return fieldMap;
	}

	/**
	 * Sets the field map.
	 *
	 * @param fieldMap the fieldMap to set
	 */
	public void setFieldMap(Map<Integer, Integer> fieldMap) {
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
	 * @return the pureTuple
	 */
	public int getCleanTuple() {
		return cleanTuple;
	}

	/**
	 * @param cleanTuple the pureTuple to set
	 */
	public void setCleanTuple(int cleanTuple) {
		this.cleanTuple = cleanTuple;
	}

	/**
	 * @return the infectedTuple
	 */
	public int getDirtyTuple() {
		return dirtyTuple;
	}

	/**
	 * @param dirtyTuple the infectedTuple to set
	 */
	public void setDirtyTuple(int dirtyTuple) {
		this.dirtyTuple = dirtyTuple;
	}

}
