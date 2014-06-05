/**
 * 
 */
package org.jumbune.execution.beans;

/**
 * This is enum to validate user input as a Yes or No;
 * 

 * 
 */
public enum YesNo {
	YES("yes"), NO("no"), Y("y"), N("n");

	private String enumValue;

	/**
	 * This is constructor method
	 * 
	 * @param enumValue
	 */
	private YesNo(String enumValue) {
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
