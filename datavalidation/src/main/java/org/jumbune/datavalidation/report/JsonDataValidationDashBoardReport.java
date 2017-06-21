package org.jumbune.datavalidation.report;


/**
 * The Class JsonValidationDashBoardReport.
 */
public class JsonDataValidationDashBoardReport {
	
	/** The null checks. */
	private String nullChecks;
	
	/** The regex violations. */
	private String regexViolations;
	
	/** The data type violations. */
	private String dataTypeViolations;
	
	/** The Fatal violations. */
	private String schemaViolations;
	
	/** The Other violations. */
	private String missingViolations;
	
	/** The dirty tuples. */
	private Long dirtyTuples;
	
	/** The clean tuples. */
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
	 * Sets the null checks.
	 *
	 * @param nullChecks the new null checks
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
	 * @param regexViolations the new regex violations
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
	 * @param dataTypeViolations the new data type violations
	 */
	public void setDataTypeViolations(String dataTypeViolations) {
		this.dataTypeViolations = dataTypeViolations;
	}

	/**
	 * Gets the schema violations.
	 *
	 * @return the schema violations
	 */
	public String getSchemaViolations() {
		return schemaViolations;
	}

	/**
	 * Sets the schema violations.
	 *
	 * @param schemaViolations the new schema violations
	 */
	public void setSchemaViolations(String schemaViolations) {
		this.schemaViolations = schemaViolations;
	}

	/**
	 * Gets the missing violations.
	 *
	 * @return the missing violations
	 */
	public String getMissingViolations() {
		return missingViolations;
	}

	/**
	 * Sets the missing violations.
	 *
	 * @param missingViolations the new missing violations
	 */
	public void setMissingViolations(String missingViolations) {
		this.missingViolations = missingViolations;
	}

	/**
	 * Gets the dirty tuples.
	 *
	 * @return the dirty tuples
	 */
	public Long getDirtyTuples() {
		return dirtyTuples;
	}

	/**
	 * Sets the dirty tuples.
	 *
	 * @param dirtyTuples the new dirty tuples
	 */
	public void setDirtyTuples(Long dirtyTuples) {
		this.dirtyTuples = dirtyTuples;
	}

	/**
	 * Gets the clean tuples.
	 *
	 * @return the clean tuples
	 */
	public Long getCleanTuples() {
		return cleanTuples;
	}

	/**
	 * Sets the clean tuples.
	 *
	 * @param cleanTuples the new clean tuples
	 */
	public void setCleanTuples(Long cleanTuples) {
		this.cleanTuples = cleanTuples;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JsonValidationDashBoardReport [nullChecks=" + nullChecks
				+ ", regexViolations=" + regexViolations
				+ ", dataTypeViolations=" + dataTypeViolations
				+ ", schemaViolations=" + schemaViolations
				+ ", missingViolations=" + missingViolations + ", dirtyTuples="
				+ dirtyTuples + ", cleanTuples=" + cleanTuples + "]";
	}

}
