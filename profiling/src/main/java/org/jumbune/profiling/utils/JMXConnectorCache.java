package org.jumbune.profiling.utils;

import java.io.IOException;

import javax.management.remote.JMXConnector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.LRUCache;


/**
 * This class provides a very simple implementation of an object cache using
 * LinkedHash Map.
 * 
 */
@SuppressWarnings("serial")
public class JMXConnectorCache extends LRUCache<String, JMXConnector> {

	private static final Logger LOGGER = LogManager
			.getLogger(JMXConnectorCache.class);

	/***
	 * @see #removeEldestEntry(java.util.Map.Entry)
	 */
	@Override
	protected boolean removeEldestEntry(
			java.util.Map.Entry<String, JMXConnector> eldest) {
		if (size() > super.getCapacity()) {
			try {
				eldest.getValue().close();
			} catch (IOException ioe) {
				LOGGER.error(
						"Error occurred while removing eldest entry from LRU cache: ",
						ioe);
			}
			return true;
		}
		return false;
	}

	/**
	 * Instantiates a new jMX connector cache.
	 *
	 * @param capacity the capacity
	 */
	public JMXConnectorCache(int capacity) {
		super(capacity);
	}

}