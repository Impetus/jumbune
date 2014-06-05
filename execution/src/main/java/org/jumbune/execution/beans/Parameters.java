package org.jumbune.execution.beans;

/**
 * Enum illustrating various parameters that might be available in parameter map to any processor.
 * 

 * 
 */
public enum Parameters {

	ISFIRST("ISFIRST"), ISLAST("ISLAST"), PURE_JOB_EXECUTED("PURE_JOB_EXECUTED"), PROCESSOR_KEY("KEY"), FIRST_MODULE("FIRST_MODULE"), LAST_MODULE(
			"LAST_MODULE");
	private String enumValue;

	/**
	 * This is constructor method
	 * 
	 * @param enumValue
	 */
	private Parameters(String enumValue) {
		this.enumValue = enumValue;
	}
	
	/**
	 * @return
	 */
	public String getEnumValue() {
		return enumValue;
	}

	/**
	 * @param enumValue
	 */
	public void setEnumValue(String enumValue) {
		this.enumValue = enumValue;
	}
	

}
