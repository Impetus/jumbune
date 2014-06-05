package org.jumbune.web.servlet;

/**
 * For fetching data validation reports corresponding to a violation failed in a particular file.
 * 

 */
public class DVFileReport {

	/**
	 * lineNumber - the line number in the file where the violation occurred.
	 */
	private int lineNumber;
	/**
	 * fieldNumber - the field number of the record where the violation occurred.
	 */
	private String fieldNumber;
	/**
	 * fileName - the name of the file where the violation occurred.
	 */
	private String fileName;
	/**
	 * expectedValue - the expected value for the field.
	 */
	private String expectedValue;
	/**
	 * actualValue - the actual value for the field.
	 */
	private String actualValue;

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the expectedValue
	 */
	public String getExpectedValue() {
		return expectedValue;
	}

	/**
	 * @param expectedValue
	 *            the expectedValue to set
	 */
	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}

	/**
	 * @return the actualValue
	 */
	public String getActualValue() {
		return actualValue;
	}

	/**
	 * @param actualValue
	 *            the actualValue to set
	 */
	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}

	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @param lineNumber
	 *            the lineNumber to set
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * @return the fieldNumber
	 */
	public String getFieldNumber() {
		return fieldNumber;
	}

	/**
	 * @param fieldNumber
	 *            the fieldNumber to set
	 */
	public void setFieldNumber(String fieldNumber) {
		this.fieldNumber = fieldNumber;
	}

}
