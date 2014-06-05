package org.jumbune.debugger.instrumentation.utils;
import org.jumbune.common.utils.Constants;

/**
 * Constant class for instrument configuration
 *
 */
public final class InstrumentConfig {
	private InstrumentConfig() {	}
	// log level for various instrument aspects

	public static final String LOG_LEVEL_LOOP = Constants.LOG_LEVEL_INFO;
	public static final String LOG_LEVEL_CONTEXT_WRITE = Constants.LOG_LEVEL_INFO;
	public static final String LOG_LEVEL_RETURN = null;
	public static final String LOG_LEVEL_MAPREDUCE_ENTRYEXIT = Constants.LOG_LEVEL_INFO;
	public static final String LOG_LEVEL_MAPREDUCE_TIMER = null;
	public static final String LOG_LEVEL_MAPREDUCE_EXECUTION = Constants.LOG_LEVEL_INFO;

	public static final boolean INSTRUMENT_CONTEXT_WRITE = true;
	public static final boolean INSTRUMENT_RETURN = false;
	public static final boolean INSTRUMENT_MAPREDUCE_ENTRYEXIT = true;
	public static final boolean INSTRUMENT_MAPREDUCE_TIMER = false;
	public static final boolean INSTRUMENT_MAPREDUCE_EXECUTION = true;
}
