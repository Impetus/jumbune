package org.jumbune.profiling.healthview;

/**
 * Pojo for storing stats corresponding to a node for various categories in Health View;
 * 
 */
public class NodeViewInfo {

	private String nodeIp;

	private ResultInfo nodeInfo;

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
	 * @return the nodeInfo
	 */
	public ResultInfo getNodeInfo() {
		return nodeInfo;
	}

	/**
	 * @param nodeInfo
	 *            the nodeInfo to set
	 */
	public void setNodeInfo(ResultInfo nodeInfo) {
		this.nodeInfo = nodeInfo;
	}

}
