package org.jumbune.common.beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.Constants;

/**
 * This Class contains information related to Jumbune like jumbune version and
 * jumbune home.
 */
public class JumbuneInfo {

	private static String home;

	private static String version;

	private static boolean secured;

	private static final Logger LOGGER = LogManager.getLogger(JumbuneInfo.class);

	static {
		setHome(null);
		secured = false;
	}

	/**
	 * Return the path of Jumbune Home
	 * 
	 * @return jumbune home path with '/' appended at the end
	 */
	public static String getHome() {
		return home;
	}

	public static void setHome(String home) {
		if (home == null) {
			home = System.getenv(Constants.JUMBUNE_ENV_VAR_NAME);
			if (home == null) {
				home = System.getProperty(Constants.JUMBUNE_ENV_VAR_NAME);
			}
			if (home != null && !(home.endsWith(Constants.FORWARD_SLASH))) {
				home += Constants.FORWARD_SLASH;
			}
		}
		
		JumbuneInfo.home = home;
		LOGGER.debug("JUMBUNE HOME [" + JumbuneInfo.home + "]");
	}

	/**
	 * Checks if Jumbune is secured or not This method is meant to be used only for
	 * ui purpose for login and dashboard related UI functionality. This method
	 * should not be used for checking whether a particular cluster is secured or
	 * not. For that purpose use cluster.isSecured() method.
	 * 
	 * @return true if secured otherwise flase;
	 */
	public static boolean isSecured() {
		return secured;
	}

	public static void setSecured(boolean secured) {
		JumbuneInfo.secured = secured;
	}

	/**
	 * Return Jumbuner Version
	 * 
	 * @return
	 */
	public static String getVersion() {
		return version;
	}

	public static void setVersion(String version) {
		JumbuneInfo.version = version;
	}

}
