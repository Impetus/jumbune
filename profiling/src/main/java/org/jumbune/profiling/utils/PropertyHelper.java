package org.jumbune.profiling.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Helper to read jumbune profiling peroperties file.
 * 

 */
public class PropertyHelper {
	private static final Logger LOGGER = LogManager
			.getLogger(PropertyHelper.class);
	private static final String PROPERTY_FILE = "jumbune-profiling.properties";

	private Properties properties;

	/**
	 * Instantiates a new property helper.
	 */
	public PropertyHelper() {
		properties = new Properties();
		try {
			properties.load(ViewHelper.class.getClassLoader()
					.getResourceAsStream(PROPERTY_FILE));
		} catch (IOException ex) {
			LOGGER.error("Unable to load jumbune-profiling.properties", ex);
		}
	}

	/**
	 * @param key
	 *            the key.
	 * @return the value if found else empty string.
	 */
	public String getProperty(String key) {
		String value = "";

		if (!StringUtils.isBlank(key)) {
			value = properties.getProperty(key);
		}

		return value;
	}

	/**
	 * @param key
	 *            the key.
	 * @param delimiter
	 *            the delimiter to split.
	 * @return the string array if found else empty string array.
	 */
	public String[] splitPropertyValue(String key, String delimiter) {
		String[] value = { "" };

		if (!StringUtils.isBlank(key)) {
			String val = properties.getProperty(key);

			if (!StringUtils.isBlank(val)) {
				value = val.split(delimiter);
			}
		}

		return value;
	}
}
