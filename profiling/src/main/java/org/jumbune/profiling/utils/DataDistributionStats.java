package org.jumbune.profiling.utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.profiling.beans.BlockInfo;
import org.jumbune.profiling.beans.DistributedDataInfo;
import org.jumbune.profiling.beans.NodeBlockStats;


import com.google.gson.Gson;


/**
 * Calculates the distributed data stats
 * 
 */
public class DataDistributionStats {
	private static final String COPY = "copy";
	private Config config;
	private int noOfBlocks = 0;
	private int underReplicatedBlocks = 0;
	private int corruptedBlocks = 0;
	private int misReplicatedBlock = 0;
	private short replicationFactor;
	private Map<String, NodeBlockStats> nodeWeight = null;
	private long lengthOfFile = 0l;

	/**
	 * Instantiates a new data distribution stats.
	 *
	 * @param yamlLoader the yaml loader
	 */
	public DataDistributionStats(Config config) {
		this.config =  config;
	}

	public DistributedDataInfo calculateBlockReport() {
		JobConfig jobConfig = (JobConfig)config;
	
		DistributedDataInfo distributedDataInfo = new DistributedDataInfo();
		String pathOfFileInHadoop = null;
		pathOfFileInHadoop = jobConfig.getDistributedHDFSPath();
		String commadResult = ProfilerUtil.getBlockStatusCommandResult(jobConfig,
				pathOfFileInHadoop);
		populateBlockInformationReport(jobConfig, commadResult, distributedDataInfo);
		return distributedDataInfo;
	}
	
	private void populateBlockInformationReport(JobConfig config,
			String response, DistributedDataInfo distributedDataInfo) {
		Long totlaDataOnNode = 0l;
		nodeWeight = new HashMap<String, NodeBlockStats>();
		List<BlockInfo> blockInfoList = extractBlockInfofromFileStatusReport(response);
		totlaDataOnNode = calculateNodeWiseBlockInformation(blockInfoList,
				totlaDataOnNode);
		distributedDataInfo.setCorruptedBlocksInCluster(String
				.valueOf(corruptedBlocks));
		distributedDataInfo
				.setMissingBlocks(String.valueOf(misReplicatedBlock));
		distributedDataInfo.setUnderReplicatedBlocks(String
				.valueOf(underReplicatedBlocks));
		distributedDataInfo.setReplicationFactor(getReplicationFactor());
		distributedDataInfo.setTotalBlocksInCluster(noOfBlocks);
		distributedDataInfo
				.setTotalDataOnNode(changeLongDataToMB(totlaDataOnNode));
		distributedDataInfo.setFileSize(changeLongDataToMB(lengthOfFile));
		addSuggestionsToProfiler(distributedDataInfo, misReplicatedBlock,
				corruptedBlocks, underReplicatedBlocks);
	}
	
	private Long calculateNodeWiseBlockInformation(
			List<BlockInfo> blockInfoList, Long totalDataOnNode) {
		String nodeIp;
		long tempSize;
		for (BlockInfo parseInformation : blockInfoList) {
			noOfBlocks++;
			List<String> nodeList = parseInformation.getNodeList();
			for (int nodeIndex = 0; nodeIndex < nodeList.size(); nodeIndex++) {
				totalDataOnNode = totalDataOnNode
						+ parseInformation.getLengthOfBlock();
				nodeIp = nodeList.get(nodeIndex);
				Map<String, String> fileWeight = null;
				Map<String, Integer> blockCopyInfo = null;
				NodeBlockStats nodeBlockStats = null;
				if (nodeWeight.containsKey(nodeIp)) {
					nodeBlockStats = nodeWeight.get(nodeIp);
					fileWeight = nodeBlockStats.getFileWeight();
					blockCopyInfo = nodeBlockStats.getBlockCopyInfo();
					if (fileWeight.containsKey(COPY + nodeIndex)) {
						blockCopyInfo.put(COPY + nodeIndex,
								blockCopyInfo.get(COPY + nodeIndex) + 1);
						tempSize = Long.parseLong(fileWeight.get(COPY
								+ nodeIndex));
						tempSize = tempSize
								+ parseInformation.getLengthOfBlock();
						fileWeight.put(COPY + nodeIndex,
								String.valueOf(tempSize));
					} else {
						fileWeight.put(COPY + nodeIndex, String
								.valueOf(parseInformation.getLengthOfBlock()));
						blockCopyInfo.put(COPY + nodeIndex, 1);
					}
					nodeBlockStats.setTotalBlocksOfFile(nodeBlockStats
							.getTotalBlocksOfFile() + 1);
				} else {
					nodeBlockStats = new NodeBlockStats();
					blockCopyInfo = new HashMap<String, Integer>();
					fileWeight = new HashMap<String, String>();
					blockCopyInfo.put(COPY + nodeIndex, 1);
					fileWeight
							.put(COPY + nodeIndex, String
									.valueOf(parseInformation
											.getLengthOfBlock()));
					nodeBlockStats.setTotalBlocksOfFile(1);
					nodeBlockStats.setFileWeight(fileWeight);
					nodeBlockStats.setBlockCopyInfo(blockCopyInfo);
					nodeBlockStats.setNodeIP(nodeIp);
					nodeWeight.put(nodeIp, nodeBlockStats);
				}
			}
		}
		return totalDataOnNode;
	}
	
	private List<BlockInfo> extractBlockInfofromFileStatusReport(String response) {
		String[] commandResult = response.split("\n");
		List<BlockInfo> blockInfoList = new ArrayList<BlockInfo>();
		List<String> nodeInfo;
		BlockInfo bInfo;
		for (String line : commandResult) {
			if (line.contains("blk") && line.contains("repl=")) {
				nodeInfo = new ArrayList<String>();
				bInfo = new BlockInfo();
				String splittedArrayBasedOnReplication[] = line.split("repl=");
				bInfo.setLengthOfBlock(Long
						.parseLong(splittedArrayBasedOnReplication[0]
								.split("len=")[1].trim()));
				String[] splitsBasedOnNodePrefix = splittedArrayBasedOnReplication[1]
						.split("\\[");
				bInfo.setReplicationFactor(Short
						.parseShort(splitsBasedOnNodePrefix[0].trim()));
				String nodelistSequence = splitsBasedOnNodePrefix[1].substring(
						0, splitsBasedOnNodePrefix[1].indexOf("]"));
				String[] nodeArray = nodelistSequence.split(",");
				for (String node : nodeArray) {
					node = node.trim();
					node = node.contains(":") ? node.split(":")[0].trim()
							: node;
					nodeInfo.add(node);
				}
				bInfo.setNodeList(nodeInfo);
				blockInfoList.add(bInfo);
			}
			if (line.contains("Total size")) {
				lengthOfFile = Long.parseLong(line.substring(
						line.indexOf(":") + 1, line.indexOf("B")).trim());
			}
			if (line.contains("Under-replicated")) {
				underReplicatedBlocks = Integer.parseInt(line.substring(
						line.indexOf(":") + 1, line.indexOf("(")).trim());
			}
			if (line.contains("Mis-replicated")) {
				misReplicatedBlock = Integer.parseInt(line.substring(
						line.indexOf(":") + 1, line.indexOf("(")).trim());
			}
			if (line.contains("Corrupt")) {
				corruptedBlocks = Integer.parseInt(line.substring(
						line.indexOf(":") + 1, line.length()).trim());
			}
			if (line.contains("replication factor")) {
				replicationFactor = Short.parseShort(line.substring(
						line.indexOf(":") + 1, line.length()).trim());
			}
		}
		return blockInfoList;
	}
	
	/***
	 * This method add suggestion to the profiler based on the result contains
	 * any missingblock,corrupted block,under replicated block
 	 * 	 @param dataInfo
	 * @param inconsistentBlockArray
	 */
	private void addSuggestionsToProfiler(DistributedDataInfo dataInfo,
			Integer... inconsistentBlockArray) {
		// new suggestion can be added later.for now we added only one
		// suggestion for all inconsistency in cluster
		List<String> suggestionList = new ArrayList<String>();
		for (Integer inconsistentBlock : inconsistentBlockArray) {
			if (0 < inconsistentBlock && suggestionList.isEmpty()) {
				suggestionList
					.add("It seems the cluster is in inconsistent state, Please run fsck command to get further details");
			}
		}
		if (!suggestionList.isEmpty()) {
			dataInfo.setSuggestionList(suggestionList);
		}
	}

	/***
	 * Change the Long value In MB
	 * 
	 * @param value
	 * @return
	 */
	private String changeLongDataToMB(Long value) {
		long mb = ProfilerConstants.THOUSAND_L * ProfilerConstants.THOUSAND_L;
		NumberFormat numberFormat = new DecimalFormat("#.#######");
		return numberFormat.format((double) value / mb) + " MB";

	}

	
	/**
	 * @return the noOfBlocks
	 */
	public int getNoOfBlocks() {
		return noOfBlocks;
	}

	/***
	 * This method shows data distribution of a particular node it takes
	 * 
	 * @param nodeIP
	 * @return
	 * @throws IOException
	 * @throws InterruptedException  
	 */
	public String getNodeStats(String nodeIP)
			throws IOException, InterruptedException {
		calculateBlockReport();
		convertNodeWeightBytesToMB(nodeWeight.get(nodeIP));
		return new Gson().toJson(nodeWeight.get(nodeIP));

	}

	/**
	 * Convert node weight bytes to mb.
	 *
	 * @param blockStats the block stats
	 */
	private void convertNodeWeightBytesToMB(NodeBlockStats blockStats) {
		Map<String, String> fileWeight = blockStats.getFileWeight();
		for (Entry<String, String> tempFileWeight : fileWeight.entrySet()) {
			tempFileWeight.setValue(changeLongDataToMB(Long
				.parseLong(tempFileWeight.getValue())));
		}
	}

	/***
	 * Fetches the node weight
	 * @return
	 */
	public Map<String, NodeBlockStats> getNodeWeight() {
		return nodeWeight;
	}

	/**
	 * @return the replicationFactor
	 */
	public short getReplicationFactor() {
		return replicationFactor;
	}

}