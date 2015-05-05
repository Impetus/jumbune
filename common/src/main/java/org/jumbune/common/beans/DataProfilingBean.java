package org.jumbune.common.beans;

import java.util.List;

public class DataProfilingBean {

	
	
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

	/** The data profiling rules. */
	private List<FieldProfilingBean> fieldProfilingRules = null;
	
	/**
	 * Sets the record separator.
	 *
	 * @param recordSeparator the new record separator
	 */
	public void setRecordSeparator(String recordSeparator) {
		this.recordSeparator = recordSeparator;
	}

	/**
	 * Gets the record separator.
	 *
	 * @return the record separator
	 */
	public String getRecordSeparator() {
		return recordSeparator;
	}

	/**
	 * Sets the field separator.
	 *
	 * @param fieldSeparator the new field separator
	 */
	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	/**
	 * Gets the field separator.
	 *
	 * @return the field separator
	 */
	public String getFieldSeparator() {
		return fieldSeparator;
	}

	/**
	 * Sets the num of fields.
	 *
	 * @param numOfFields the new num of fields
	 */
	public void setNumOfFields(int numOfFields) {
		this.numOfFields = numOfFields;
	}

	/**
	 * Gets the num of fields.
	 *
	 * @return the num of fields
	 */
	public int getNumOfFields() {
		return numOfFields;
	}

	/**
	 * Sets the field profiling rules.
	 *
	 * @param fieldProfilingRules the new field profiling rules
	 */
	public void setFieldProfilingRules(List<FieldProfilingBean> fieldProfilingRules) {
		this.fieldProfilingRules = fieldProfilingRules;
	}

	/**
	 * Gets the field profiling rules.
	 *
	 * @return the field profiling rules
	 */
	public List<FieldProfilingBean> getFieldProfilingRules() {
		return fieldProfilingRules;
	}

	@Override
	public String toString() {
		return "DataProfilingBean [fieldProfilingRules=" + fieldProfilingRules
				+ ", recordSeparator=" + recordSeparator + ", fieldSeparator="
				+ fieldSeparator + ", numOfFields=" + numOfFields + "]";
	}

	
	
	
	

}
