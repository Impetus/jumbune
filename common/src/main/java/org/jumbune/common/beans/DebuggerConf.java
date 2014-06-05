package org.jumbune.common.beans;

import java.util.Map;

import org.jumbune.utils.beans.LogLevel;



/**
 * This class is the bean for the instrumentation settings entries from yaml.
 */
public class DebuggerConf {
	//for create new sample and use existing samples
	/** The sample toggle. */
	private Enable sampleToggle;
	
	/** Debug on actual data or use sample. */
	private Enable useEntireWorkingSet;
	
	/** file path in hdfs where data is reside. */
	private String hDFSSourcePath;
	
	/** record separator of data. */
	private String recordSeparator;
	
	/** field separator of data. */
	private String fieldSeparator;

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

	/**
	 * Sets the h dfs source path.
	 *
	 * @param hDFSSourcePath the hDFSSourcePath to set
	 */
	public void sethDFSSourcePath(String hDFSSourcePath) {
		this.hDFSSourcePath = hDFSSourcePath;
	}

	/**
	 * Gets the h dfs source path.
	 *
	 * @return the hDFSSourcePath
	 */
	public String gethDFSSourcePath() {
		return hDFSSourcePath;
	}

	/**
	 * Gets the sample toggle.
	 *
	 * @return the sample toggle
	 */
	public Enable getSampleToggle() {
		return sampleToggle;
	}

	/**
	 * Sets the sample toggle.
	 *
	 * @param sampleToggle the new sample toggle
	 */
	public void setSampleToggle(Enable sampleToggle) {
		this.sampleToggle = sampleToggle;
	}

	/**
	 * Gets the use entire working set.
	 *
	 * @return the use entire working set
	 */
	public Enable getUseEntireWorkingSet() {
		return useEntireWorkingSet;
	}

	/**
	 * Sets the use entire working set.
	 *
	 * @param useEntireWorkingSet the new use entire working set
	 */
	public void setUseEntireWorkingSet(Enable useEntireWorkingSet) {
		this.useEntireWorkingSet = useEntireWorkingSet;
	}

	/**
	 * Gets the record separator.
	 *
	 * @return the record separator
	 */
	public String getRecordSeparator() {
		return recordSeparator;
	}

	/**
	 * Sets the record separator.
	 *
	 * @param recordSeparator the new record separator
	 */
	public void setRecordSeparator(String recordSeparator) {
		this.recordSeparator = recordSeparator;
	}

	/**
	 * Gets the field separator.
	 *
	 * @return the field separator
	 */
	public String getFieldSeparator() {
		return fieldSeparator;
	}

	/**
	 * Sets the field separator.
	 *
	 * @param fieldSeparator the new field separator
	 */
	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}
}
