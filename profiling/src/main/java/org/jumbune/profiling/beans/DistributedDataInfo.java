package org.jumbune.profiling.beans;

import java.util.List;

/***
 * This class contains information like percentage out of total data contains by a copy of file in HDFS.
 * 
 */
public class DistributedDataInfo {
	private String totalDataOnNode;
	private int totalBlocksInCluster;
	private short replicationFactor;
	private String fileSize;
	private String corruptedBlocksInCluster;
	private String underReplicatedBlocks;
	private String missingBlocks;
	private List<String> suggestionList;

	/**
	 * @return the corruptedBlocksInCluster
	 */
	public String getCorruptedBlocksInCluster() {
		return corruptedBlocksInCluster;
	}

	/**
	 * @param corruptedBlocksInCluster
	 *            the corruptedBlocksInCluster to set
	 */
	public void setCorruptedBlocksInCluster(String corruptedBlocksInCluster) {
		this.corruptedBlocksInCluster = corruptedBlocksInCluster;
	}

	/**
	 * @return the underReplicatedBlocks
	 */
	public String getUnderReplicatedBlocks() {
		return underReplicatedBlocks;
	}

	/**
	 * @param underReplicatedBlocks
	 *            the underReplicatedBlocks to set
	 */
	public void setUnderReplicatedBlocks(String underReplicatedBlocks) {
		this.underReplicatedBlocks = underReplicatedBlocks;
	}

	public short getReplicationFactor() {
		return replicationFactor;
	}

	public void setReplicationFactor(short replicationFactor) {
		this.replicationFactor = replicationFactor;
	}

	/**
	 * @param totalDataOnNode
	 *            the totalDataOnNode to set
	 */
	public void setTotalDataOnNode(String totalDataOnNode) {
		this.totalDataOnNode = totalDataOnNode;
	}

	/**
	 * @return the totalDataOnNode
	 */
	public String getTotalDataOnNode() {
		return totalDataOnNode;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the fileSize
	 */
	public String getFileSize() {
		return fileSize;
	}

	/**
	 * @param totalBlocksInCluster
	 *            the totalBlocksInCluster to set
	 */
	public void setTotalBlocksInCluster(int totalBlocksInCluster) {
		this.totalBlocksInCluster = totalBlocksInCluster;
	}

	/**
	 * @return the totalBlocksInCluster
	 */
	public int getTotalBlocksInCluster() {
		return totalBlocksInCluster;
	}

	/**
	 * @param missingBlocks
	 *            the missingBlocks to set
	 */
	public void setMissingBlocks(String missingBlocks) {
		this.missingBlocks = missingBlocks;
	}

	/**
	 * @return the missingBlocks
	 */
	public String getMissingBlocks() {
		return missingBlocks;
	}

	/**
	 * @param suggestionList the suggestionList to set
	 */
	public void setSuggestionList(List<String> suggestionList) {
		this.suggestionList = suggestionList;
	}

	/**
	 * @return the suggestionList
	 */
	public List<String> getSuggestionList() {
		return suggestionList;
	}

}
