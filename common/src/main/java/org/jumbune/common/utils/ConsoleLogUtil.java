package org.jumbune.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <p>
 * This class provides common logger to use instead of sysouts
 * </p>.
 */
public interface ConsoleLogUtil {
	
	/** The Constant LOGGER. */
	Logger LOGGER = LogManager.getLogger("EventLogger");
	
	/** The Constant CONSOLELOGGER. */
	Logger CONSOLELOGGER = LogManager.getLogger("EventLogger");
}
