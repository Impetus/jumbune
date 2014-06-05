package org.jumbune.utils.beans;

/**
 * The Enum UserSuppliedDependencyInputType is used for marking the user supplied jar option.
 */
public enum UserSuppliedDependencyInputType {
	PLEASESELECT(-1), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);
	private int dependentJarOption;

	/**
	 * This constructor for this dependentJarOption value.
	 * 
	 * @param enumValue
	 */
	private UserSuppliedDependencyInputType(int dependentJarOption) {
		this.dependentJarOption = dependentJarOption;
	}

	
	/**
	 * Gets the dependent jar option.
	 *
	 * @return the dependent jar option
	 */
	public int getDependentJarOption() {
		return dependentJarOption;
	}


	/**
	 * Sets the dependent jar option.
	 *
	 * @param dependentJarOption the new dependent jar option
	 */
	public void setDependentJarOption(int dependentJarOption) {
		this.dependentJarOption = dependentJarOption;
	}


	/**
	 * this is getter method for enumValue
	 * 
	 * @return
	 */
	public int getOptionValue() {
		return dependentJarOption;
	}

	/**
	 * check if a given value exist in enum
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean isValid(int value) {
		for (final UserSuppliedDependencyInputType option : UserSuppliedDependencyInputType.values()) {
			if (value == option.getOptionValue()) {
				return true;
			}
		}
		return false;
	}
}