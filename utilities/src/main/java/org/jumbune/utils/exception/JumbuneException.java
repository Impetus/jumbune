package org.jumbune.utils.exception;
/**
 * The Class JumbuneException is a wrapper for handling exception in various modules.
 */

public class JumbuneException extends Exception {

	private static final long serialVersionUID = 3363777670339355751L;
	private int errorCode;
	private String errorMessage;

	/**
	 * Instantiates a new Jumbune exception.
	 *
	 * @param message the message
	 */
	public JumbuneException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Jumbune exception.
	 *
	 * @param errorCodeAndMessage the error code and message
	 */
	public JumbuneException(ErrorCodesAndMessages errorCodeAndMessage) {
		super(errorCodeAndMessage.getDescription());
		errorCode = errorCodeAndMessage.getCode();
		errorMessage = errorCodeAndMessage.getDescription();
	}

	/**
	 * Instantiates a new Jumbune exception.
	 *
	 * @param errorCodeAndMessage the error code and message
	 * @param cause the cause
	 */
	public JumbuneException(ErrorCodesAndMessages errorCodeAndMessage ,Exception cause) {
		super(errorCodeAndMessage.getDescription(),cause);
		errorCode = errorCodeAndMessage.getCode();
		errorMessage = errorCodeAndMessage.getDescription();
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the error code.
	 *
	 * @param errorCode the new error code
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Gets the error message.
	 *
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the error message.
	 *
	 * @param errorMessage the new error message
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * This method provides string implementation of the object
	 */
	public String toString() {
		return "Errorcode #" + errorCode + "-ErrorMessage :" + errorMessage;
	}

}