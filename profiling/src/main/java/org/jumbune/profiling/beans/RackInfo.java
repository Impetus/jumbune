package org.jumbune.profiling.beans;

import java.util.Set;

/**
 * Rack info bean having complete rack related stats.
 * 
 */
public class RackInfo {

	private String rackId;
	private Set<NodeInfo> nodes;

	/**
	 * @return the rackId
	 */
	public String getRackId() {
		return rackId;
	}

	/**
	 * @param rackId
	 *            the rackId to set
	 */
	public void setRackId(String rackId) {
		this.rackId = rackId;
	}

	/**
	 * @return the nodes
	 */
	public Set<NodeInfo> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes
	 *            the nodes to set
	 */
	public void setNodes(Set<NodeInfo> nodes) {
		this.nodes = nodes;
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
		result = prime * result + ((rackId == null) ? 0 : rackId.hashCode());
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
		RackInfo other = (RackInfo) obj;
		if (rackId == null) {
			if (other.rackId != null){
				return false;
			}
		} else if (!rackId.equals(other.rackId)){
			return false;
		}
		return true;
	}

}