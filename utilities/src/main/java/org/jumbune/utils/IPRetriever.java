package org.jumbune.utils;

import static org.jumbune.utils.UtilitiesConstants.ENCLOSER_END;
import static org.jumbune.utils.UtilitiesConstants.ENCLOSER_START;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class has util methods to give IP and host on which current jvm is
 * running
 */
public final class IPRetriever {
	/** The LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(IPRetriever.class);

	private IPRetriever(){
		
	}
	/**
	 * Gets the IP of current jvm machine
	 * 
	 * @return IP
	 */
	public static String getCurrentIP() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress();
		} catch (UnknownHostException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Gets the IP of current jvm, enclosed in {}
	 * 
	 * @return IP
	 */
	public static String getEnclosedRunningIP() {
		StringBuilder formattedIP = new StringBuilder(ENCLOSER_START).append(
				getCurrentIP()).append(ENCLOSER_END);
		return formattedIP.toString();
	}

	/**
	 * Gets the host name of the current jvm
	 * 
	 * @return Host name
	 */
	public static String getHostName() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostName();
		} catch (UnknownHostException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
}
