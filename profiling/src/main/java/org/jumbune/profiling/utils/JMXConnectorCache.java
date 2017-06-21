package org.jumbune.profiling.utils;

import java.io.IOException;

import javax.management.remote.JMXConnector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.LRUCache;
import org.jumbune.utils.exception.JumbuneRuntimeException;

/**
 * This class provides a very simple implementation of an object cache using
 * LinkedHash Map.
 * 
 */
@SuppressWarnings("serial")
public class JMXConnectorCache extends LRUCache<String, JMXConnector> {
	private static JMXConnectorCache jmxConnectorCache;
	private static final Logger LOGGER = LogManager
			.getLogger(JMXConnectorCache.class);

	private JMXConnectorCache() {
		// JMX connector cache capacity
		super(ProfilerConstants.THREE);
	}

	public synchronized static JMXConnectorCache getJMXCacheInstance() {
		if (jmxConnectorCache == null) {
			jmxConnectorCache = new JMXConnectorCache();
		}
		return jmxConnectorCache;

	}

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
				LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(ioe.getStackTrace()));
			}
			return true;
		}
		return false;
	}

}