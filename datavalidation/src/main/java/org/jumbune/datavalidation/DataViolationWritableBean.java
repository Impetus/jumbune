package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;


/**
 * This is custom writable class for storing parameters required for data violation report.
 * 
 * 
 * 
 */
public class DataViolationWritableBean implements WritableComparable<DataViolationWritableBean> {
	
	
	private String violationType;
	
	/**
	 * lineNumber - the line number in the file where the violation occurred.
	 */
	private Integer lineNumber;
	/**
	 * fieldNumber - the field number of the record where the violation occurred.
	 */
	private Integer fieldNumber;
	/**
	 * expectedValue - the expected value for the field.
	 */
	private String expectedValue;
	/**
	 * actualValue - the actual value for the field.
	 */
	private String actualValue;

	/**
	 * Instantiates a new data violation writable bean.
	 */
	public DataViolationWritableBean() {

	}

	/**
	 * Instantiates a new data violation writable bean.
	 *
	 * @param dataViolationWritableBean the data violation writable bean
	 */
	public DataViolationWritableBean(DataViolationWritableBean dataViolationWritableBean) {
		this.lineNumber = dataViolationWritableBean.getLineNumber();
		this.fieldNumber = dataViolationWritableBean.getFieldNumber();
		this.expectedValue = dataViolationWritableBean.getExpectedValue();
		this.actualValue = dataViolationWritableBean.getActualValue();
		this.violationType = dataViolationWritableBean.getViolationType();
	}

	/**
	 * writes data validation results to an output stream
	 */
	public void write(DataOutput out) throws IOException {
		out.writeInt(lineNumber);
		out.writeInt(fieldNumber);
		out.writeUTF(expectedValue);
		out.writeUTF(actualValue);
		out.writeUTF(violationType);
	}

	/**
	 * reads data validation details from an input stream 
	 */
	public void readFields(DataInput in) throws IOException {
		lineNumber = in.readInt();
		fieldNumber = in.readInt();
		expectedValue = in.readUTF();
		actualValue = in.readUTF();
		violationType = in.readUTF();
	}

	/**
	 * <p>
	 * See {@link #setlineNumber(Integer)}
	 * </p>.
	 *
	 * @return Returns the lineNumber.
	 */
	public Integer getLineNumber() {
		return lineNumber;
	}

	/**
	 * <p>
	 * Set the value of <code>lineNumber</code>.
	 * </p>
	 * 
	 * @param lineNumber
	 *            The lineNumber to set.
	 */
	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * <p>
	 * See {@link #setfieldNumber(Integer)}
	 * </p>.
	 *
	 * @return Returns the fieldNumber.
	 */
	public Integer getFieldNumber() {
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
	public void setFieldNumber(Integer fieldNumber) {
		this.fieldNumber = fieldNumber;
	}


	/**
	 * <p>
	 * See {@link #setexpectedValue(String)}
	 * </p>.
	 *
	 * @return Returns the expectedValue.
	 */
	public String getExpectedValue() {
		return expectedValue;
	}

	/**
	 * <p>
	 * Set the value of <code>expectedValue</code>.
	 * </p>
	 * 
	 * @param expectedValue
	 *            The expectedValue to set.
	 */
	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}

	/**
	 * <p>
	 * See {@link #setactualValue(String)}
	 * </p>.
	 *
	 * @return Returns the actualValue.
	 */
	public String getActualValue() {
		return actualValue;
	}

	/**
	 * <p>
	 * Set the value of <code>actualValue</code>.
	 * </p>
	 * 
	 * @param actualValue
	 *            The actualValue to set.
	 */
	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DataViolationWritableBean arg0) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"lineNumber\":").append(lineNumber).append(",\"fieldNumber\":").append(fieldNumber)
				.append(",\"expectedValue\":").append(expectedValue).append(",\"actualValue\":").append(actualValue).append("}");
		return sb.toString();
	}

	/**
	 * @return the violationType
	 */
	public String getViolationType() {
		return violationType;
	}

	/**
	 * @param violationType the violationType to set
	 */
	public void setViolationType(String violationType) {
		this.violationType = violationType;
	}

}
