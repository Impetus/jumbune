package org.jumbune.common.beans;


/**
 * This class contains all validation checks to be applied on different fields by the user.
 * 
 * 
 */
public class FieldValidationBean {

	/**
	 * fieldNumber - the field number in the record.
	 */
	private int fieldNumber;
	/**
	 * nullCheck - whether the field must be null,may be null or must not be null.
	 */
	private String nullCheck;
	/**
	 * dataType - the data type of the field.
	 */
	private String dataType;
	/**
	 * regex - the regular expression provided by the user to match the value of the field.
	 */
	private String regex;

	/**
	 * <p>
	 * See {@link #setfieldNumber(int)}
	 * </p>.
	 *
	 * @return Returns the fieldNumber.
	 */
	public int getFieldNumber() {
		return fieldNumber;
	}

	/**
	 * <p>
	 * Set the value of <code>fieldNumber</code>.
	 * </p>
	 * 
	 * @param fieldNumber
	 *            The fieldNumber to set.
	 */
	public void setFieldNumber(int fieldNumber) {
		this.fieldNumber = fieldNumber;
	}

	/**
	 * <p>
	 * See {@link #setnullCheck(String)}
	 * </p>.
	 *
	 * @return Returns the nullCheck.
	 */
	public String getNullCheck() {
		return nullCheck;
	}

	/**
	 * <p>
	 * Set the value of <code>nullCheck</code>.
	 * </p>
	 * 
	 * @param nullCheck
	 *            The nullCheck to set.
	 */
	public void setNullCheck(String nullCheck) {
		this.nullCheck = nullCheck;
	}

	/**
	 * <p>
	 * See {@link #setdataType(String)}
	 * </p>.
	 *
	 * @return Returns the dataType.
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * <p>
	 * Set the value of <code>dataType</code>.
	 * </p>
	 * 
	 * @param dataType
	 *            The dataType to set.
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * <p>
	 * See {@link #setregex(String)}
	 * </p>.
	 *
	 * @return Returns the regex.
	 */
	public String getRegex() {
		return regex;
	}

	/**
	 * <p>
	 * Set the value of <code>regex</code>.
	 * </p>
	 * 
	 * @param regex
	 *            The regex to set.
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FieldValidationBean [fieldNumber=" + fieldNumber + ", nullCheck=" + nullCheck + ", dataType=" + dataType + ", regex=" + regex + "]";
	}
}
