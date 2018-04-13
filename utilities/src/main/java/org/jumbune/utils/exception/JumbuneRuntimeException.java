package org.jumbune.utils.exception;

import java.util.Arrays;


/**
 * This is expected to be made comprehensive so that we can use it, update the serialver as you add.
 */
public class JumbuneRuntimeException extends RuntimeException {

	/** The Constant serialVersionUID. */
	static final long serialVersionUID = -3852071606023630352L;
										
	/** The depth. */
	private static int depth = 3;

	/** The no data on io exception. */
	private static JumbuneRuntimeException noDataOnIOException = new JumbuneRuntimeException("Error, Unable to get any response on I/O channel. One of the probable reason could be that Jumbune lost the connection to Jumbune Agent");

	/** The parse exception. */
	private static JumbuneRuntimeException parseException = new JumbuneRuntimeException("Error, Unable to parse");
	
	/** The unable to load file exception. */
	private static JumbuneRuntimeException unableToLoadFileException = new JumbuneRuntimeException("Error, Unable to load the given file");
	
	/** The unable to analyse logs exception. */
	private static JumbuneRuntimeException unableToAnalyseLogsException = new JumbuneRuntimeException("Error, Unable to analyse the log files");
	
	/** The some thing went wrong exception. */
	private static JumbuneRuntimeException someThingWentWrongException = new JumbuneRuntimeException("Error, Something went wrong");
	
	/** The yarn deamon not working exception. */
	private static JumbuneRuntimeException yarnDeamonNotWorkingException = new JumbuneRuntimeException("Error, Please check cluster yarn deamon is working properly");

	/** The thread interrupted exception. */
	private static JumbuneRuntimeException threadInterruptedException = new JumbuneRuntimeException("Error, Thread stopped working");
	
	/** The unknown host exception. */
	private static JumbuneRuntimeException unKnownHostException = new JumbuneRuntimeException("Error, Can not get network by InetAddress");

	/** The socket exception. */
	private static JumbuneRuntimeException socketException = new JumbuneRuntimeException("Error, Can not get network interface:");
	
	/** The no such provider exception. */
	private static JumbuneRuntimeException noSuchProviderException = new JumbuneRuntimeException("Error, Failed to get expected security provider");
	
	/** The no such algorithm exception. */
	private static JumbuneRuntimeException noSuchAlgorithmException = new JumbuneRuntimeException("Error, No such Algorithm found");
	
	/** The class not found exception. */
	private static JumbuneRuntimeException classNotFoundException = new JumbuneRuntimeException("Error, No such class or constructor found");
	
	/** The illegal argument exception. */
	private static JumbuneRuntimeException illegalArgumentException = new JumbuneRuntimeException("Error: please check arugment");
	
	
	
	/** The stack trace. */
	private static StackTraceElement[] stackTrace;
	
	/**
	 * constructor for JumbuneRuntimeException.
	 *
	 * @param message the message
	 */
	public JumbuneRuntimeException(String message){
		super(message);
	}
	
	/**
	 * Fills in the execution stack trace. This method records within this Throwable object information about 
	 * the current state of the stack frames for the current thread. 
	 * If the stack trace of this Throwable is not writable, calling this method has no effect.
	 *
	 * @return the throwable
	 */
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
	
	/**
	 * Trim stack trace.
	 *
	 * @param stackFrames the stack frames
	 * @return the stack trace element[]
	 */
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
	 * Gets the stack trace information.
	 *
	 * @return the stack trace
	 */
	@Override
	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	/**
	 * Typically thrown when there is no response received from the I/O channel on which we were waiting to get response.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return JumbuneRuntimeException
	 */
	public static JumbuneRuntimeException throwUnresponsiveIOException(StackTraceElement[] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw noDataOnIOException;
	}

	/**
	 * Typically thrown when Unable to load a file.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return JumbuneRuntimeException
	 */
	public static JumbuneRuntimeException throwFileNotLoadedException(StackTraceElement[] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw unableToLoadFileException;
	}
	
	/**
	 * Throw parse exception.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return the jumbune runtime exception
	 */
	public static JumbuneRuntimeException throwParseException(StackTraceElement[] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw parseException;
	}

	/**
	 * Typically throw when no such algorithm exception occur.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return the jumbune runtime exception
	 */
	public static JumbuneRuntimeException throwNoSuchAlgorithmException(StackTraceElement[] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw noSuchAlgorithmException;
	}
	/**
	 * Typically throw when no interrupted exception occur.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return the jumbune runtime exception
	 */
	public static JumbuneRuntimeException throwInterruptedException(StackTraceElement [] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw threadInterruptedException;
	}
	
	/**
	 * Typically throw when class not found exception occur.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return the jumbune runtime exception
	 */
	public static JumbuneRuntimeException throwClassNotFoundException(StackTraceElement[] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw classNotFoundException;
	}
	
	/**
	 * Typically throw when class not found exception occur.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return the jumbune runtime exception
	 */
	public static JumbuneRuntimeException throwIllegalArgumentException(StackTraceElement[] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw illegalArgumentException;
	}
	
	
	/**
	 * Typically thrown when known host exception occur.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return the jumbune runtime exception
	 */
	public static JumbuneRuntimeException throwUnKnownHostException(StackTraceElement [] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw unKnownHostException;
	}
	
	/**
	 * Typically thrown when no such provider exception occur.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return the jumbune runtime exception
	 */
	public static JumbuneRuntimeException throwNoSuchProviderException(StackTraceElement [] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw noSuchProviderException;
	}
	
	/**
	 * Typically thrown when socket exception occur.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return the jumbune runtime exception
	 */
	public static JumbuneRuntimeException throwSocketException(StackTraceElement [] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw socketException;
	}
	
	/**
	 * Typically thrown when generic exception occur.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return JumbuneRuntimeException
	 */
	public static JumbuneRuntimeException throwException(StackTraceElement[] stackTraceArray){
		stackTrace = stackTraceArray;
		throw someThingWentWrongException;
	}
	
	
	/**
	 * Typically thrown when yarn exception occur.
	 *
	 * @param stackTraceArray the stack trace array
	 * @return the jumbune runtime exception
	 */
	public static JumbuneRuntimeException throwYarnException (StackTraceElement[] stackTraceArray){
		StackTraceElement[] stackTraceArrayTmp = stackTraceArray;
		stackTrace = trimStackTrace(stackTraceArrayTmp);
		throw yarnDeamonNotWorkingException;
	}
	
	/**
	 * Typically thrown when there is an error in processing the log files.
	 *
	 * @param stackTraceArray the stack trace array
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
	 * @param declaringClass refers to the fully qualified name of the class containing the execution point represented by the stack trace element
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