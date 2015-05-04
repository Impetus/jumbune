package org.jumbune.common.beans;

import java.util.Map;

import org.jumbune.utils.beans.LogLevel;



/**
 * This class is the bean for the instrumentation settings entries from yaml.
 */
public class DebuggerConf {
	//for create new sample and use existing samples

	/** The log level. */
	private Map<String, LogLevel> logLevel;
	
	/** The max if block nesting level. */
	private int maxIfBlockNestingLevel;

	/**
	 * <p>
	 * See {@link #setlogLevel(Map<String,LogLevel>)}
	 * </p>.
	 *
	 * @return Returns the logLevel.
	 */
	public final Map<String, LogLevel> getLogLevel() {
		return logLevel;
	}

	/**
	 * <p>
	 * Set the value of <code>logLevel</code>.
	 * </p>
	 * 
	 * @param logLevel
	 *            The logLevel to set.
	 */
	public final void setLogLevel(Map<String, LogLevel> logLevel) {
		this.logLevel = logLevel;
	}

	/**
	 * <p>
	 * See {@link #setmaxIfBlockNestingLevel(int)}
	 * </p>.
	 *
	 * @return Returns the maxIfBlockNestingLevel.
	 */
	public int getMaxIfBlockNestingLevel() {
		return maxIfBlockNestingLevel;
	}

	/**
	 * <p>
	 * Set the value of <code>maxIfBlockNestingLevel</code>.
	 * </p>
	 * 
	 * @param maxIfBlockNestingLevel
	 *            The maxIfBlockNestingLevel to set.
	 */
	public void setMaxIfBlockNestingLevel(int maxIfBlockNestingLevel) {
		this.maxIfBlockNestingLevel = maxIfBlockNestingLevel;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DebuggerConf [logLevel=" + logLevel + ", maxIfBlockNestingLevel=" + maxIfBlockNestingLevel + "]";
	}

}
