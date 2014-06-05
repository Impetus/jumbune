package org.jumbune.debugger.log.processing;

import java.util.Map;

/**
 * This is the class which contains parameters required corresponding to counters(if,else,contextWrites,etc) of the Instance of Mapper or Reducer.
   */
public class ExpressionCounterBean extends AbstractLogAnalysisBean {

	/**
	 * totalFilteredIn - Total number of keys filtered (which satisfied the condition and traversed inside the block)
	 */
	private int totalFilteredIn;
	/**
	 * totalFilteredOut - Total number of keys filtered out (by return statement inside the block)
	 */
	private int totalFilteredOut;
	/**
	 * totalExitKeys - Total number of keys exited from the block(used for calculating total number of filtered out keys)
	 */
	private int totalExitKeys;
	/**
	 * currentKey - It specifies the value of the current key for which the instance has entered inside the block.It is set to not available when the
	 * instance exits from the block.
	 */
	private String currentKey;

	/**
	 * counterDetails - checks if the counter is a method,If not contains the line number of the counter.
	 */
	private String counterDetails;

	/**
	 * counterMap - It contains the nested counters.
	 */
	private Map<String, ExpressionCounterBean> counterMap;

	/**
	 * @return the totalFilteredIn
	 */
	public final int getTotalFilteredIn() {
		return totalFilteredIn;
	}

	/**
	 * @param totalFilteredIn
	 *            the totalFilteredIn to set
	 */
	public final void setTotalFilteredIn(final int totalFilteredIn) {
		this.totalFilteredIn = totalFilteredIn;
	}

	/**
	 * @return the totalFilteredOut
	 */
	public final int getTotalFilteredOut() {
		return totalFilteredOut;
	}

	/**
	 * @param totalFilteredOut
	 *            the totalFilteredOut to set
	 */
	public final void setTotalFilteredOut(final int totalFilteredOut) {
		this.totalFilteredOut = totalFilteredOut;
	}

	/**
	 * @return the totalExitKeys
	 */
	public final int getTotalExitKeys() {
		return totalExitKeys;
	}

	/**
	 * @param totalExitKeys
	 *            the totalExitKeys to set
	 */
	public final void setTotalExitKeys(final int totalExitKeys) {
		this.totalExitKeys = totalExitKeys;
	}

	/**
	 * @return the currentKey
	 */
	public final String getCurrentKey() {
		return currentKey;
	}

	/**
	 * @param currentKey
	 *            the currentKey to set
	 */
	public final void setCurrentKey(final String currentKey) {
		this.currentKey = currentKey;
	}

	/**
	 * <p>
	 * See {@link #setcounterMap(Map<String,ExpressionCounterBean>)}
	 * </p>
	 * 
	 * @return Returns the counterMap.
	 */
	public Map<String, ExpressionCounterBean> getCounterMap() {
		return counterMap;
	}

	/**
	 * <p>
	 * Set the value of <code>counterMap</code>.
	 * </p>
	 * 
	 * @param counterMap
	 *            The counterMap to set.
	 */
	public void setCounterMap(Map<String, ExpressionCounterBean> counterMap) {
		this.counterMap = counterMap;
	}

	/**
	 * @return the counterDetails
	 */
	public String getCounterDetails() {
		return counterDetails;
	}

	/**
	 * @param counterDetails
	 *            the counterDetails to set
	 */
	public void setCounterDetails(String counterDetails) {
		this.counterDetails = counterDetails;
	}

}
