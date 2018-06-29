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
 * The Class MessageLoader.
 */
public class MessageLoader {
	
	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger(MessageLoader.class);
	
	/** The instance. */
	private static MessageLoader instance;
	
	/** The prop. */
	private Properties prop;
	
	/** The is. */
	private InputStream is;

	

	/**
	 * Gets the single instance of MessageLoader.
	 *
	 * @return single instance of MessageLoader
	 */
	public static MessageLoader getInstance() {
		if (instance == null) {

			final String messageFileName = Constants.MESSAGE_FILE;
			final InputStream msgStream = MessageLoader.class.getClassLoader().getResourceAsStream(messageFileName);
			try {
				instance = new MessageLoader(msgStream);
			} catch (JumbuneException e) {
				LOGGER.error(e);
			}finally{
				if(msgStream != null){
					try {
						msgStream.close();
					} catch (IOException e) {
						LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
					}
				}
			}
		}

		return instance;
	}

	/**
	 * Instantiates a new message loader.
	 *
	 * @param inputStream the input stream
	 * @throws JumbuneException the hTF exception
	 */
	public MessageLoader(InputStream inputStream) throws JumbuneException {
		prop = new Properties();

		if (inputStream != null) {
			try {
				is = inputStream;
				prop.load(inputStream);

			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				throw JumbuneRuntimeException.throwFileNotLoadedException(e.getStackTrace());
			}
		} else {
			throw  new IllegalArgumentException("input stream is unexpectedly null");
		}
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the string
	 */
	public String get(String key) {
		return prop.getProperty(key);
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the string
	 */
	public String get(int key) {

		return prop.getProperty(String.valueOf(key));
		
	}

	/**
	 * Gets the checks if is.
	 *
	 * @return the checks if is
	 */
	public InputStream getIS() {
		return is;
	}
}
