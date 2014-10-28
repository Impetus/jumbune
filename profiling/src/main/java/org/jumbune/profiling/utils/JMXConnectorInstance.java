package org.jumbune.profiling.utils;

import java.io.IOException;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * This class basically for object creation of JMXConnector and get and put
 * object of {@link JMXConnector} in cache.
 * 
 * 
 */
public final class JMXConnectorInstance {
	private static final Logger LOGGER = LogManager
			.getLogger(JMXConnectorInstance.class);
	private static JMXConnector connector = null;
	
	/***
	 * private constructor
	 */
	private JMXConnectorInstance(){
		
	}

	/***
	 * This method provide access to LinkedHashMapCahche and get and put the
	 * JmxConnector Object into it.
	 * 
	 * @param url
	 *            {@link JMXServiceURL} In cache url is works as a key and it
	 *            also create {@link JMXConnector} object if it is not in the
	 *            cache.
	 * @param cache
	 *            is object of {@link JMXConnectorCache}
	 * @return
	 */
	public synchronized static JMXConnector getJMXConnectorInstance(JMXServiceURL url) throws IOException {
		JMXConnectorCache cache = JMXConnectorCache.getJMXCacheInstance();
		connector = cache.get(url.toString());
		if (connector != null) {
			try {
				if (connector.getConnectionId() != null) {
					return connector;
				}
			} catch (IOException ioe) {
				LOGGER.warn("Found connector not null with no connectionId, now attempting to create new connector");
			}
		}
		connector = JMXConnectorFactory.connect(url, null);
		cache.put(url.toString(), connector);
		return connector;
	}

}