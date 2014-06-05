package org.jumbune.common.beans;

import java.util.List;



/**
 * This class contains all validation checks to be applied by the user.
 * 

 * 
 */
public class DataValidationBean {

	/**
	 * recordSeparator - a character or a sequence of characters to split the records in a file.
	 */
	private String recordSeparator;
	/**
	 * fieldSeparator - a character or a sequence of characters used to separate fields in a file.
	 */
	private String fieldSeparator;
	/**
	 * numOfFields - the expected number of fields in a record.
	 */
	private int numOfFields;
	/**
	 * fieldValidationList - the map containing validation details for each field.
	 */
	private List<FieldValidationBean> fieldValidationList;

	/**
	 * <p>
	 * See {@link #setrecordSeparator(String)}
	 * </p>.
	 *
	 * @return Returns the recordSeparator.
	 */
	public final String getRecordSeparator() {
		return recordSeparator;
	}

	/**
	 * <p>
	 * Set the value of <code>recordSeparator</code>.
	 * </p>
	 * 
	 * @param recordSeparator
	 *            The recordSeparator to set.
	 */
	public final void setRecordSeparator(final String recordSeparator) {
		this.recordSeparator = recordSeparator;
	}

	/**
	 * <p>
	 * See {@link #setfieldSeparator(String)}
	 * </p>.
	 *
	 * @return Returns the fieldSeparator.
	 */
	public final String getFieldSeparator() {
		return fieldSeparator;
	}

	/**
	 * <p>
	 * Set the value of <code>fieldSeparator</code>.
	 * </p>
	 * 
	 * @param fieldSeparator
	 *            The fieldSeparator to set.
	 */
	public final void setFieldSeparator(final String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	/**
	 * <p>
	 * See {@link #setnumOfFields(int)}
	 * </p>.
	 *
	 * @return Returns the numOfFields.
	 */
	public final int getNumOfFields() {
		return numOfFields;
	}

	/**
	 * <p>
	 * Set the value of <code>numOfFields</code>.
	 * </p>
	 * 
	 * @param numOfFields
	 *            The numOfFields to set.
	 */
	public final void setNumOfFields(final int numOfFields) {
		this.numOfFields = numOfFields;
	}

	/**
	 * <p>
	 * See {@link #setfieldValidationList(List<FieldValidationBean>)}
	 * </p>.
	 *
	 * @return Returns the fieldValidationList.
	 */
	public List<FieldValidationBean> getFieldValidationList() {
		return fieldValidationList;
	}

	/**
	 * <p>
	 * Set the value of <code>fieldValidationList</code>.
	 * </p>
	 * 
	 * @param fieldValidationList
	 *            The fieldValidationList to set.
	 */
	public void setFieldValidationList(List<FieldValidationBean> fieldValidationList) {
		this.fieldValidationList = fieldValidationList;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DataValidationBean [recordSeparator=" + recordSeparator + ", fieldSeparator=" + fieldSeparator + ", numOfFields=" + numOfFields
				+ ", fieldValidationList=" + fieldValidationList + "]";
	}
}
