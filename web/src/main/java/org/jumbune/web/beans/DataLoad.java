package org.jumbune.web.beans;

import java.util.List;

import org.jumbune.monitoring.beans.NodePerformance;

public class DataLoad {
	
	private Integer totalNodes;
	
	private Double meanPercentage;
	
	private NodePerformance performance;
	
	private List<String> nodes;
	
	private boolean isPositive;

	public DataLoad(Integer totalNodes, Double meanPercentage, NodePerformance performance, List<String> nodes) {
		this.totalNodes = totalNodes;
		this.meanPercentage = meanPercentage;
		this.performance = performance;
		this.nodes = nodes;
	}

	public DataLoad(Integer totalNodes, Double meanPercentage, NodePerformance performance, List<String> nodes, boolean isPositive) {
		this(totalNodes, meanPercentage, performance, nodes);
		this.isPositive = isPositive;
	}

	public Integer getTotalNodes() {
		return totalNodes;
	}
	
	public void setTotalNodes(Integer totalNodes) {
		this.totalNodes = totalNodes;
	}
	
	public Double getMeanPercentage() {
		return meanPercentage;
	}
	
	public void setMeanPercentage(Double meanPercentage) {
		this.meanPercentage = meanPercentage;
	}

	public NodePerformance getPerformance() {
		return performance;
	}

	public void setPerformance(NodePerformance performance) {
		this.performance = performance;
	}

	public List<String> getNodes() {
		return nodes;
	}

	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}

	public boolean isPositive() {
		return isPositive;
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}
}
