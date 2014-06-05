package org.jumbune.profiling.beans;

import org.jumbune.profiling.beans.CategoryInfo;

/**
 * Pojo to store node IP and profiling stats in favourites and trends
 * 
*/
public class NodeConfig {

	private String nodeIp;
	private CategoryInfo favourities;
	private CategoryInfo trends;

	/**
	 * @return the nodeIp
	 */
	public String getNodeIp() {
		return nodeIp;
	}

	/**
	 * @param nodeIp
	 *            the nodeIp to set
	 */
	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
	}

	/**
	 * @return the favourities
	 */
	public CategoryInfo getFavourities() {
		return favourities;
	}

	/**
	 * @param favourities
	 *            the favourities to set
	 */
	public void setFavourities(CategoryInfo favourities) {
		this.favourities = favourities;
	}

	/**
	 * @return the trends
	 */
	public CategoryInfo getTrends() {
		return trends;
	}

	/**
	 * @param trends
	 *            the trends to set
	 */
	public void setTrends(CategoryInfo trends) {
		this.trends = trends;
	}

}
