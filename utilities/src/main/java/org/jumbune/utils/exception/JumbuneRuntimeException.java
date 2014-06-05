package org.jumbune.utils.exception;

import java.util.Arrays;

/**
 * This is expected to be made comprehensive so that we can use it, update the serialver as you add
 */
public class JumbuneRuntimeException extends RuntimeException {

	
	private static int depth = 2;
	
	private static final long serialVersionUID = 8217087945691382285L;

	private static JumbuneRuntimeException noDataOnIOException = new JumbuneRuntimeException("Unable to get any response on I/O channel from the remote operation");
	
	private static JumbuneRuntimeException unableToLoadFileException = new JumbuneRuntimeException("Unable to load the given file");
	
	private static JumbuneRuntimeException unableToAnalyseLogsException = new JumbuneRuntimeException("Unable to analyse the log files");
	


	private static StackTraceElement[] stackTrace;
	
	/**
	 * constructor for JumbuneRuntimeException
	 * @param message
	 */
	public JumbuneRuntimeException(String message){
		super(message);
	}
	
	/**
	 * Fills in the execution stack trace. This method records within this Throwable object information about 
	 * the current state of the stack frames for the current thread. 
	 * If the stack trace of this Throwable is not writable, calling this method has no effect.
	 */
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getStackTrace()
	 */
	public static StackTraceElement[] trimStackTrace(StackTraceElement[] stackFrames) {
			if (stackFrames.length == 0) {
				return null;
			}
			if (stackFrames.length > depth) {
				stackFrames = Arrays.copyOfRange(stackFrames, 0, depth);
			}
			return stackFrames;
		}	
	
	/**
	 * Gets the stack trace information 
	 */
	@Override
	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	/**
	 * Typically thrown when there is no response received from the I/O channel on which we were waiting to get response.
	 * @return JumbuneRuntimeException
	 */
	public static JumbuneRuntimeException throwUnresponsiveIOException(StackTraceElement[] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw noDataOnIOException;
	}

	/**
	 * Typically thrown when Unable to load a file.
	 * @param stackTraceArray
	 * @return JumbuneRuntimeException
	 */
	public static JumbuneRuntimeException throwFileNotLoadedException(StackTraceElement[] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw unableToLoadFileException;
	}
	
	/**
	 * Typically thrown when there is an error in processing the log files.
	 * @param stackTraceArray
	 * @return JumbuneRuntimeException
	 */
	public static JumbuneRuntimeException throwDebugAnalysisFailedException(StackTraceElement[] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw unableToAnalyseLogsException;
	}
	
	/**
	 * Typically thrown when there is no response received from the I/O channel on which we were waiting to get response.
	 * 
	 * @param declaringClass refers to the fully qualified name of the class containingthe execution point represented by the stack trace element
	 * @param methodName  refers to the name of the method containing the execution point represented by the stack trace element.
	 * @param fileName refers to the name of the file containing the execution point represented by the stack trace element.
	 * @param lineNumber refers to the the line number of the source line containing the execution point represented by this stack trace element.
	 * @return JumbuneRuntimeException
	 */
	public static JumbuneRuntimeException throwUnresponsiveIOException(String declaringClass, String methodName, String fileName, int lineNumber){
		StackTraceElement stackTraceElement = new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
		stackTrace = new StackTraceElement[] {stackTraceElement};
		throw noDataOnIOException;
	}
}