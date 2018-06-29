package org.jumbune.datavalidation.report;


/**
 * The Class DataValidationDashBoardReport is bean class for displaying violations on dashboard report.
 */
public class XmlDataValidationDashBoardReport {
	
	/** The null checks. */
	private String nullChecks;
	
	/** The regex violations. */
	private String regexViolations;
	
	/** The data type violations. */
	private String dataTypeViolations;
	
	/** The Fatal violations. */
	private String fatalViolations;
	
	/** The Other violations. */
	private String otherViolations;
	
	
	private Long dirtyTuples;
	
	private Long cleanTuples;

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

	
	public Long getDirtyTuples() {
		return dirtyTuples;
	}

	public void setDirtyTuples(Long dirtyTuples) {
		this.dirtyTuples = dirtyTuples;
	}

	public Long getCleanTuples() {
		return cleanTuples;
	}

	public void setCleanTuples(Long cleanTuples) {
		this.cleanTuples = cleanTuples;
	}

	/**
	 * @return the fatalViolations
	 */
	public String getFatalViolations() {
		return fatalViolations;
	}

	/**
	 * @param fatalViolations the fatalViolations to set
	 */
	public void setFatalViolations(String fatalViolations) {
		this.fatalViolations = fatalViolations;
	}

	/**
	 * @return the otherViolations
	 */
	public String getOtherViolations() {
		return otherViolations;
	}

	/**
	 * @param otherViolations the otherViolations to set
	 */
	public void setOtherViolations(String otherViolations) {
		this.otherViolations = otherViolations;
	}
}
