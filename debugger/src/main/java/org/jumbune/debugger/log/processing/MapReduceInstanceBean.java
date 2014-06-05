package org.jumbune.debugger.log.processing;

import java.util.Map;

/**
 * This is the class which contains parameters required corresponding to an instance of a Mapper or Reducer.
 */
public class MapReduceInstanceBean extends AbstractLogAnalysisBean {

	/**
	 * numOfSamples - the number of samples taken
	 */
	private int numOfSamples;

	/**
	 * time - the time taken to process these samples
	 */
	private int time;

	/**
	 * instanceMap - the map containing analysis results of various counters(if,else,etc) of the instance of Mapper or Reducer.
	 */
	private Map<String, ExpressionCounterBean> instanceMap;

	/**
	 * @return the instanceMap
	 */
	public final Map<String, ExpressionCounterBean> getInstanceMap() {
		return instanceMap;
	}

	/**
	 * @param instanceMap
	 *            the instanceMap to set
	 */
	public final void setInstanceMap(final Map<String, ExpressionCounterBean> instanceMap) {
		this.instanceMap = instanceMap;
	}

	/**
	 * @return the numOfSamples
	 */
	public int getNumOfSamples() {
		return numOfSamples;
	}

	/**
	 * @param numOfSamples
	 *            the numOfSamples to set
	 */
	public void setNumOfSamples(int numOfSamples) {
		this.numOfSamples = numOfSamples;
	}

	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

}
