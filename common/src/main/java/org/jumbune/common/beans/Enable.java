/**
 * 
 */
package org.jumbune.common.beans;


/**
 * This is enum type to validate user input as a True or false.
 */
public enum Enable {
	
	/** The true. */
	TRUE(true), 
	/** The false. */
	FALSE(false);

	/** The enum value. */
	private boolean enumValue;

	/**
	 * This constructor for this enum.
	 *
	 * @param enumValue the enum value
	 */
	private Enable(boolean enumValue) {
		this.enumValue = enumValue;
	}

	/**
	 * this is getter method for enumValue.
	 *
	 * @return the enum value
	 */
	public boolean getEnumValue() {
		return enumValue;
	}
}
