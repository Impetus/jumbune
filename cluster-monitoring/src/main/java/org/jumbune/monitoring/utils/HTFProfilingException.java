package org.jumbune.monitoring.utils;

/**
 * Wrapper exception for profiling module.
 * 
 */
public class HTFProfilingException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2614522314369849832L;

	/**
	 * Instantiates a new hTF profiling exception.
	 */
	public HTFProfilingException() {
		super();
	}

	/**
	 * Instantiates a new hTF profiling exception.
	 *
	 * @param message the message
	 */
	public HTFProfilingException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new hTF profiling exception.
	 *
	 * @param throwable the throwable
	 */
	public HTFProfilingException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Instantiates a new hTF profiling exception.
	 *
	 * @param message the message
	 * @param throwable the throwable
	 */
	public HTFProfilingException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
