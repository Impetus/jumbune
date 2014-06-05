package org.jumbune.profiling.healthview;

import java.util.Map;

/**
 * The Class AdjacencyInfo is a pojo for setting and retrieving the node information in case of network latency.
 */
public class AdjacencyInfo {
	private String nodeTo;
	private Map<String, Float> data;

	/**
	 * @return the nodeTo
	 */
	public String getNodeTo() {
		return nodeTo;
	}

	/**
	 * @param nodeTo
	 *            the nodeTo to set
	 */
	public void setNodeTo(String nodeTo) {
		this.nodeTo = nodeTo;
	}

	/**
	 * @return the data
	 */
	public Map<String, Float> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Map<String, Float> data) {
		this.data = data;
	}

}