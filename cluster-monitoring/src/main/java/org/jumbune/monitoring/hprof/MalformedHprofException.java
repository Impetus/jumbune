package org.jumbune.monitoring.hprof;

import java.io.IOException;
import java.util.Arrays;

/**
 * The Class MalformedHprofException is an exception class for handling the malformed input/output exception.
 */
public final class MalformedHprofException extends IOException {

	private static final long serialVersionUID = 8558990237047894213L;
	
	private static int depth = 2;

	/**
	 * Instantiates a new malformed hprof exception.
	 *
	 * @param message the message
	 */
	MalformedHprofException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new malformed hprof exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	MalformedHprofException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new malformed hprof exception.
	 *
	 * @param cause the cause
	 */
	MalformedHprofException(Throwable cause) {
		super(cause);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getStackTrace()
	 */
	@Override
	public StackTraceElement[] getStackTrace() {
			StackTraceElement[] stackFrames = super.getStackTrace();
			if (stackFrames.length == 0) {
				return null;
			}
			if (stackFrames.length > 2) {
				stackFrames = Arrays.copyOfRange(stackFrames, 0, depth);
			}
			return stackFrames;
		}
	}

