package org.jumbune.monitoring.beans;

import java.util.Map;



/**
 * Pojo to store node IP and response for profiling stats in favorites and trends.
 */
public class NodeStats {

	/** The node ip. */
	private String nodeIp;
	
	/** The favourities. */
	private StatsResult favourities;
	
	/** The trends. */
	private StatsResult trends;
	
	/** The color config. */
	private Map<String, NodePerformance> colorConfig;

	/**
	 * Gets the color config.
	 *
	 * @return the colorConfig
	 */
	public Map<String, NodePerformance> getColorConfig() {
		return colorConfig;
	}

	/**
	 * Sets the color config.
	 *
	 * @param colorConfig the colorConfig to set
	 */
	public void setColorConfig(Map<String, NodePerformance> colorConfig) {
		this.colorConfig = colorConfig;
	}

	/**
	 * Instantiates a new node stats.
	 *
	 * @param nodeIp the node ip
	 */
	public NodeStats(String nodeIp) {
		this.nodeIp = nodeIp;
	}

	/**
	 * Gets the node ip.
	 *
	 * @return the nodeIp
	 */
	public String getNodeIp() {
		return nodeIp;
	}

	/**
	 * Sets the node ip.
	 *
	 * @param nodeIp the nodeIp to set
	 */
	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
	}

	/**
	 * Gets the favourities.
	 *
	 * @return the favourities
	 */
	public StatsResult getFavourities() {
		return favourities;
	}

	/**
	 * Sets the favourities.
	 *
	 * @param favourities the favourities to set
	 */
	public void setFavourities(StatsResult favourities) {
		this.favourities = favourities;
	}

	/**
	 * Gets the trends.
	 *
	 * @return the trends
	 */
	public StatsResult getTrends() {
		return trends;
	}

	/**
	 * Sets the trends.
	 *
	 * @param trends the trends to set
	 */
	public void setTrends(StatsResult trends) {
		this.trends = trends;
	}

}
