package org.jumbune.profiling.beans;

import java.util.Collection;

/**
 * Cluster info bean having complete cluster related stats.
 * 
*/
public class ClusterInfo {
	private String nameNodeIP;
	private String clusterId;
	private DistributedDataInfo distributedDataInfo;

	private Collection<DataCenterInfo> dataCenters;

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
	 * @return the dataCenters
	 */
	public Collection<DataCenterInfo> getDataCenters() {
		return dataCenters;
	}

	/**
	 * @param dataCenters
	 *            the dataCenters to set
	 */
	public void setDataCenters(Collection<DataCenterInfo> dataCenters) {
		this.dataCenters = dataCenters;
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
		result = prime * result + ((clusterId == null) ? 0 : clusterId.hashCode());
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
		ClusterInfo other = (ClusterInfo) obj;
		if (clusterId == null) {
			if (other.clusterId != null){
				return false;
			}
		} else if (!clusterId.equals(other.clusterId)){
			return false;
		}
		return true;
	}

	/**
	 * @param nameNodeIP
	 *            the nameNodeIP to set
	 */
	public void setNameNodeIP(String nameNodeIP) {
		this.nameNodeIP = nameNodeIP;
	}

	/**
	 * @return the nameNodeIP
	 */
	public String getNameNodeIP() {
		return nameNodeIP;
	}

	/**
	 * @param distributedDataInfo the distributedDataInfo to set
	 */
	public void setDistributedDataInfo(DistributedDataInfo distributedDataInfo) {
		this.distributedDataInfo = distributedDataInfo;
	}

	/**
	 * @return the distributedDataInfo
	 */
	public DistributedDataInfo getDistributedDataInfo() {
		return distributedDataInfo;
	}
}