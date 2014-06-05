package org.jumbune.datavalidation.report;


/**
 * The Class DataValidationDashBoardReport is bean class for displaying violations on dashboard report.
 */
public class DataValidationDashBoardReport {
	
	/** The null checks. */
	private String nullChecks;
	
	/** The regex violations. */
	private String regexViolations;
	
	/** The data type violations. */
	private String dataTypeViolations;
	
	/** The number of fields. */
	private String numberOfFields;

	/**
	 * Gets the null checks.
	 *
	 * @return the null checks
	 */
	public String getNullChecks() {
		return nullChecks;
	}
	
	/**
	 * Sets the null violations.
	 * 
	 * @param nullChecks
	 *            the new null violations
	 */
	public void setNullChecks(String nullChecks) {
		this.nullChecks = nullChecks;
	}

	/**
	 * Gets the regex violations.
	 *
	 * @return the regex violations
	 */
	public String getRegexViolations() {
		return regexViolations;
	}

	/**
	 * Sets the regex violations.
	 * 
	 * @param regexViolations
	 *            the new regex violations
	 */
	public void setRegexViolations(String regexViolations) {
		this.regexViolations = regexViolations;
	}

	/**
	 * Gets the data type violations.
	 *
	 * @return the data type violations
	 */
	public String getDataTypeViolations() {
		return dataTypeViolations;
	}

	/**
	 * Sets the data type violations.
	 * 
	 * @param dataTypeViolations
	 *            the new data type violations
	 */
	public void setDataTypeViolations(String dataTypeViolations) {
		this.dataTypeViolations = dataTypeViolations;
	}

	
	/**
	 * Sets the number of fields violations.
	 *
	 * @param numberOfFields the new number of fields
	 */
	public void setNumberOfFields(String numberOfFields) {
		this.numberOfFields = numberOfFields;
	}

	/**
	 * Gets the number of fields.
	 *
	 * @return the number of fields
	 */
	public String getNumberOfFields() {
		return numberOfFields;
	}
}
