package org.jumbune.monitoring.beans;

import java.util.Map;

/***
 * This class contains all the block related information of a node .
 * 
 */
public class NodeBlockStats {

	private String nodeIP;
	private int totalBlocksOfFile;
	private Map<String, Integer> blockCopyInfo;
	private Map<String, String> fileWeight;

	/**
	 * @return the blockCopyInfo
	 */
	public Map<String, Integer> getBlockCopyInfo() {
		return blockCopyInfo;
	}

	/**
	 * @param blockCopyInfo
	 *            the blockCopyInfo to set
	 */
	public void setBlockCopyInfo(Map<String, Integer> blockCopyInfo) {
		this.blockCopyInfo = blockCopyInfo;
	}

	/**
	 * @param totalBlocksOfFile
	 *            the totalBlocksOfFile to set
	 */
	public void setTotalBlocksOfFile(int totalBlocksOfFile) {
		this.totalBlocksOfFile = totalBlocksOfFile;
	}

	/**
	 * @return the totalBlocksOfFile
	 */
	public int getTotalBlocksOfFile() {
		return totalBlocksOfFile;
	}

	/**
	 * @param nodeIP
	 *            the nodeIP to set
	 */
	public void setNodeIP(String nodeIP) {
		this.nodeIP = nodeIP;
	}

	/**
	 * @return the nodeIP
	 */
	public String getNodeIP() {
		return nodeIP;
	}

	/**
	 * @param fileWeight
	 *            the fileWeight to set
	 */
	public void setFileWeight(Map<String, String> fileWeight) {
		this.fileWeight = fileWeight;
	}

	/**
	 * @return the fileWeight
	 */
	public Map<String, String> getFileWeight() {
		return fileWeight;
	}

}
