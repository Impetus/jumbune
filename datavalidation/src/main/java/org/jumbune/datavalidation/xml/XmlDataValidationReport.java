package org.jumbune.datavalidation.xml;

import java.util.List;


/**
 * Pojo to store data violation result.
 */
public class XmlDataValidationReport {

	/** total number of violations for a particular type of check. */
	private long totalViolations;
	/** number of violations for a particular type of check. */
	private long individualViolations;
	
	/** List of violations to be displayed on UI. */
	private List<XmlFileViolationsWritable> violationList;

	

	/**
	 * @return the totalViolations
	 */
	public long getTotalViolations() {
		return totalViolations;
	}



	/**
	 * @param totalViolations the totalViolations to set
	 */
	public void setTotalViolations(long totalViolations) {
		this.totalViolations = totalViolations;
	}



	/**
	 * @return the individualViolations
	 */
	public long getIndividualViolations() {
		return individualViolations;
	}



	/**
	 * @param individualViolations the individualViolations to set
	 */
	public void setIndividualViolations(long individualViolations) {
		this.individualViolations = individualViolations;
	}



	/**
	 * @return the violationList
	 */
	public List<XmlFileViolationsWritable> getViolationList() {
		return violationList;
	}



	/**
	 * @param violationList the violationList to set
	 */
	public void setViolationList(List<XmlFileViolationsWritable> violationList) {
		this.violationList = violationList;
	}



	@Override
	public String toString() {
		return "XmlDataValidationReport [totalViolations=" + totalViolations + ", individualViolations=" + individualViolations
				+ " , violationList=" + violationList + "]";
	}
	
}
