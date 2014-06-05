package org.jumbune.common.beans;

import java.util.List;


/**
 * The Class Validation.
 */
public class Validation {

	/** The classname. */
	private String classname;
	
	/** The key. */
	private String key;
	
	/** The value. */
	private String value;

	/**
	 * Gets the classname.
	 *
	 * @return the classname
	 */
	public String getClassname() {
		return classname;
	}

	/**
	 * Sets the classname.
	 *
	 * @param classname the new classname
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Validation [classname=" + classname + ", key=" + key + ", value=" + value + "]";
	}

	/**
	 * <p>
	 * This method returns the validator defined for a class from the collection of validators
	 * </p>.
	 *
	 * @param validations List of validators
	 * @param strClassName Class for which validator is required
	 * @return Validation Validator for the class
	 */
	public static Validation getValidation(List<Validation> validations, String strClassName) {
		if (validations != null) {
			for (Validation validation : validations) {
				if (strClassName.equals(validation.getClassname())) {
					return validation;
				}
			}
		}
		return null;
	}
}
