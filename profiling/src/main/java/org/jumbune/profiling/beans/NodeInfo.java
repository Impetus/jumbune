package org.jumbune.profiling.beans;

import java.util.List;

import org.jumbune.profiling.hprof.NodePerformance;



/**
 * Node info bean having complete node related Stats.
 * 

 */
public class NodeInfo {

	/** The node ip. */
	private String nodeIp;
	
	/** The node type. */
	private NodeType nodeType;
	
	/** The performance. */
	private NodePerformance performance;
	
	/** The stats. */
	private List<CategoryWiseInfo> stats;
	
	/** The data load stats. */
	private String dataLoadStats;
	
	// Message to display the user why the node performance is Average or Bad, used by data center heat map
	private String message;

	/**
	 * Gets the data load stats.
	 *
	 * @return the dataLoadStats
	 */
	public String getDataLoadStats() {
		return dataLoadStats;
	}

	/**
	 * Sets the data load stats.
	 *
	 * @param dataLoadStats the dataLoadStats to set
	 */
	public void setDataLoadStats(String dataLoadStats) {
		this.dataLoadStats = dataLoadStats;
	}

	/**
	 * Instantiates a new node info.
	 */
	public NodeInfo() {
	}

	/**
	 * Instantiates a new node info.
	 *
	 * @param nodeIp the node ip
	 * @param type the type
	 */
	public NodeInfo(String nodeIp, NodeType type) {
		this.nodeIp = nodeIp;
		this.nodeType = type;
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
	 * Gets the node type.
	 *
	 * @return the nodeType
	 */
	public NodeType getNodeType() {
		return nodeType;
	}

	/**
	 * Sets the node type.
	 *
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * Gets the stats.
	 *
	 * @return the stats
	 */
	public List<CategoryWiseInfo> getStats() {
		return stats;
	}

	/**
	 * Sets the stats.
	 *
	 * @param stats the stats to set
	 */
	public void setStats(List<CategoryWiseInfo> stats) {
		this.stats = stats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeIp == null) ? 0 : nodeIp.hashCode());
		result = prime * result
				+ ((nodeType == null) ? 0 : nodeType.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		NodeInfo other = (NodeInfo) obj;
		if (nodeIp == null) {
			if (other.nodeIp != null){
				return false;
			}
		} else if (!nodeIp.equals(other.nodeIp)){
			return false;
		}
		if (nodeType != other.nodeType){
			return false;
		}
		return true;
	}

	/**
	 * Gets the performance.
	 *
	 * @return the performance
	 */
	public NodePerformance getPerformance() {
		return performance;
	}

	/**
	 * Sets the performance.
	 *
	 * @param performance the new performance
	 */
	public void setPerformance(NodePerformance performance) {
		this.performance = performance;
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	
}