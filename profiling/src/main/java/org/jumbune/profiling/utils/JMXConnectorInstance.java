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
	public static JMXConnector getJMXConnectorInstance(JMXServiceURL url,
			JMXConnectorCache cache) {
		connector = cache.get(url.toString());
		if (connector == null) {
			try {
				connector = JMXConnectorFactory.connect(url, null);
				cache.put(url.toString(), connector);
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}
		return connector;

	}
	
	/**
	 * This method is used to close JMX connections after use.
	 * @param instance
	 */
	public static void closeJMXConnection(JMXConnector instance){
		try{
			if(instance!= null){
				instance.close();
			}
		}catch (IOException e) {
			LOGGER.error(e);
		}
	}

}