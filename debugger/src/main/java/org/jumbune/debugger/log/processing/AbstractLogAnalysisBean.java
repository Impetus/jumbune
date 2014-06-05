package org.jumbune.debugger.log.processing;

/**
 * This is the abstract class which contains parameters required corresponding to a Job, Mapper or Reducer, Node,an instance of Mapper or Reducer and
 * Counter.
 */
public abstract class AbstractLogAnalysisBean {

	/**
	 * totalInputKeys - Total number of input keys
	 */
	private int totalInputKeys;
	/**
	 * totalInputKeys - Total number of context writes
	 */
	private int totalContextWrites;
	/**
	 * totalInputKeys - Total number of unmatched keys compared against regex provided by user
	 */
	private int totalUnmatchedKeys = -1;
	/**
	 * totalInputKeys - Total number of unmatched values compared against regex provided by user
	 */
	private int totalUnmatchedValues = -1;

	/**
	 * @return the totalInputKeys
	 */
	public final int getTotalInputKeys() {
		return totalInputKeys;
	}

	/**
	 * @param totalInputKeys
	 *            the totalInputKeys to set
	 */
	public final void setTotalInputKeys(final int totalInputKeys) {
		this.totalInputKeys = totalInputKeys;
	}

	/**
	 * @return the totalContextWrites
	 */
	public final int getTotalContextWrites() {
		return totalContextWrites;
	}

	/**
	 * @param totalContextWrites
	 *            the totalContextWrites to set
	 */
	public final void setTotalContextWrites(final int totalContextWrites) {
		this.totalContextWrites = totalContextWrites;
	}

	/**
	 * @return the totalUnmatchedKeys
	 */
	public final int getTotalUnmatchedKeys() {
		return totalUnmatchedKeys;
	}

	/**
	 * @param totalUnmatchedKeys
	 *            the totalUnmatchedKeys to set
	 */
	public final void setTotalUnmatchedKeys(final int totalUnmatchedKeys) {
		this.totalUnmatchedKeys = totalUnmatchedKeys;
	}

	/**
	 * @return the totalUnmatchedValues
	 */
	public final int getTotalUnmatchedValues() {
		return totalUnmatchedValues;
	}

	/**
	 * @param totalUnmatchedValues
	 *            the totalUnmatchedValues to set
	 */
	public final void setTotalUnmatchedValues(final int totalUnmatchedValues) {
		this.totalUnmatchedValues = totalUnmatchedValues;
	}
}
