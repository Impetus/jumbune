package org.jumbune.debugger.instrumentation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;


/**
 * This class loads messages for instrumentation module and provides api to get
 * any specific message
 */
public final class InstrumentationMessageLoader {
	/**
	 * Logger instance
	 */
	private static final Logger LOG = LogManager
			.getLogger(InstrumentationMessageLoader.class);
	
	private InstrumentationMessageLoader(){}

	private static Properties prop;

	private static InstrumentationMessageLoader messageLoader;

	static {

		final String messageFileName = "instrumentationMessages.en";
		final InputStream input = InstrumentationMessageLoader.class
				.getClassLoader().getResourceAsStream(messageFileName);
		messageLoader = new InstrumentationMessageLoader();
		try {
			messageLoader.loadMessages(input);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		} catch (JumbuneException e) {
			LOG.error(e.getErrorMessage());
		}finally{
			if(input != null){
				try {
					input.close();
				} catch (IOException e) {
					LOG.error("Unable to close stream" + e.getMessage());
				}
			}
		}

	}

	/**
	 * <p>
	 * This method loads all the messages from the given input stream
	 * </p>
	 * 
	 * @param inputStream
	 *            Stream containing the messages
	 */
	private void loadMessages(InputStream inputStream) throws IOException,
			JumbuneException {
		prop = new Properties();
		if (inputStream != null) {
			try {
				prop.load(inputStream);

			} catch (IOException e) {
				LOG.error(e.getMessage());
				throw JumbuneRuntimeException.throwFileNotLoadedException(e.getStackTrace());
			}
		} else {
			throw  new IllegalArgumentException("input stream is unexpectedly null");

		}
	}

	/**
	 * <p>
	 * This method gets the messages for the given key
	 * </p>
	 * 
	 * @param key
	 *            Key
	 * @return message
	 */
	public static String getMessage(String key) {
		return prop.getProperty(key);
	}

	/**
	 * <p>
	 * This method gets the messages for the given key
	 * </p>
	 * 
	 * @param key
	 *            Key
	 * @return message
	 */
	public static String getMessage(int key) {
		return prop.getProperty(String.valueOf(key));
	}
}
