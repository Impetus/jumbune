package org.jumbune.common.beans;

/**
 * Enum for mentioning module.
 */
public enum Module {

	/** The data validation. */
	DATA_VALIDATION(0), 
	/** The profiling. */
 PROFILING(1), 
 /** The pure and instrumented. */
 PURE_AND_INSTRUMENTED(2), 
 /** The debug analyser. */
 DEBUG_ANALYSER(3); 


	/** The enum value. */
	private int enumValue;

	/**
	 * Gets the enum value.
	 *
	 * @return the enum value
	 */
	public int getEnumValue() {
		return enumValue;
	}

	/**
	 * Sets the enum value.
	 *
	 * @param enumValue the new enum value
	 */
	public void setEnumValue(int enumValue) {
		this.enumValue = enumValue;
	}

	/**
	 * This is constructor method.
	 *
	 * @param enumValue the enum value
	 */
	private Module(int enumValue) {
		this.enumValue = enumValue;
	}

}
