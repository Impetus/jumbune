/**
 * 
 */
package org.jumbune.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;




/**
 * The Class ErrorMessageLoader.
 */
public final class ErrorMessageLoader {
	
	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger(ErrorMessageLoader.class);
	
	/** The instance. */
	private static ErrorMessageLoader errorMessageLoader;
	
	/** The properties. */
	private Properties properties;

	/** message file which containing all the messages. */
	private static final String INPUT_VALIDATION_MESSAGE_FILE = "inputjson.error";
 
	/** Input validation Messages file name*/
	private static String inputValidationMessageFile=INPUT_VALIDATION_MESSAGE_FILE;
	
	/** setter for inputValidationMessageFile to override the default. */
	public static void setInputValidationMessageFile(
			String inputValidationMessageFile) {
		ErrorMessageLoader.inputValidationMessageFile = inputValidationMessageFile;
	}

	/**
	 * *
	 * create object of ErrorMessageLoader class if it is not instantiated otherwise returns already used object this method load message file as an
	 * input stream.
	 *
	 * @return single instance of ErrorMessageLoader
	 */
	public static ErrorMessageLoader getInstance() {
		if (errorMessageLoader == null) {
			final String messageFileName = inputValidationMessageFile;
			final InputStream msgStream = ErrorMessageLoader.class.getClassLoader().getResourceAsStream(messageFileName);
			try {
				errorMessageLoader = new ErrorMessageLoader(msgStream);
			} catch (JumbuneException e) {
				LOGGER.error(e);
			} finally {
				try {
					if (msgStream != null) {
						msgStream.close();
					}
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}

		return errorMessageLoader;
	}

	/**
	 * *
	 * constructor of InputValidationMessageLoader which is private and load properties from input stream.
	 *
	 * @param inputStream the input stream
	 * @throws JumbuneException the hTF exception
	 */
	private ErrorMessageLoader(InputStream inputStream) throws JumbuneException {
		properties = new Properties();
		
		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				throw JumbuneRuntimeException.throwFileNotLoadedException(e.getStackTrace());
			}
		} else {
			throw new IllegalArgumentException("input stream is unexpectedly null");
		}
	}

	/**
	 * *
	 * it is take key as an argument and return message of key.
	 *
	 * @param key the key
	 * @return message of perticular key
	 */
	public String get(int key) {
		return properties.getProperty(String.valueOf(key));
	}
}
