package org.jumbune.clusterprofiling.yarn.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="clusterMetrics")
public class ClusterMetrics {
	
	private long totalMB;
	
	private long totalVirtualCores ;

	public long getTotalMB() {
		return totalMB;
	}

	@XmlElement
	public void setTotalMB(long totalMB) {
		this.totalMB = totalMB;
	}

	public long getTotalVirtualCores() {
		return totalVirtualCores;
	}

	@XmlElement
	public void setTotalVirtualCores(long totalVirtualCores) {
		this.totalVirtualCores = totalVirtualCores;
	}

}
