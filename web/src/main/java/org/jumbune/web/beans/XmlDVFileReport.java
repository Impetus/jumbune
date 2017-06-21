package org.jumbune.web.beans;

/**
 * For fetching data validation reports corresponding to a violation failed in a particular file.
 * 

 */
public class XmlDVFileReport {

	/**
	 * lineNumber - the line number in the file where the violation occurred.
	 */
	private int lineNumber;
	/**
	 * fileName - the name of the file where the violation occurred.
	 */
	private String fileName;
	/**
	 * message - the error message .
	 */
	private String message;

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
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
