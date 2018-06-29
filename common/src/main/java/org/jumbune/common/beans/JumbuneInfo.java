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

	private static final Logger LOGGER = LogManager.getLogger(JumbuneInfo.class);

	static {
		setHome(null);
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
