/**
 * 
 */
package org.jumbune.execution.beans;

/**
 * This is enum type to validate user input as a True or false
 * 
 */
public enum Enable {
	TRUE(true), FALSE(false);

	private boolean enumValue;

	/**
	 * This constructor for this enum.
	 * 
	 * @param enumValue
	 */
	private Enable(boolean enumValue) {
		this.enumValue = enumValue;
	}

	/**
	 * this is getter method for enumValue
	 * 
	 * @return
	 */
	public boolean getEnumValue() {
		return enumValue;
	}
}
