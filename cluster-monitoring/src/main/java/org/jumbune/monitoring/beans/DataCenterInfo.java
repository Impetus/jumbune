package org.jumbune.monitoring.beans;

import java.util.Map;
import java.util.Set;

/**
 * The Class DataCenterInfo is a bean class for handling masternode and racks information.
 */
public class DataCenterInfo {

	private String id;
	private String clusterId;
	private Map<String, String> masterNode;
	private Set<RackInfo> racks;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the clusterId
	 */
	public String getClusterId() {
		return clusterId;
	}

	/**
	 * @param clusterId
	 *            the clusterId to set
	 */
	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	/**
	 * @return the masterNode
	 */
	public Map<String, String> getMasterNode() {
		return masterNode;
	}

	/**
	 * @param masterNode
	 *            the masterNode to set
	 */
	public void setMasterNode(Map<String, String> masterNode) {
		this.masterNode = masterNode;
	}

	/**
	 * @return the racks
	 */
	public Set<RackInfo> getRacks() {
		return racks;
	}

	/**
	 * @param racks
	 *            the racks to set
	 */
	public void setRacks(Set<RackInfo> racks) {
		this.racks = racks;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		DataCenterInfo other = (DataCenterInfo) obj;
		if (id == null) {
			if (other.id != null){
				return false;
			}
		} else if (!id.equals(other.id)){
			return false;
		}
		return true;
	}
}