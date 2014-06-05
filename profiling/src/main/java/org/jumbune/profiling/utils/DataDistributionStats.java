package org.jumbune.profiling.utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.fs.BlockLocation;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.profiling.beans.ClusterInfo;
import org.jumbune.profiling.beans.DistributedDataInfo;
import org.jumbune.profiling.beans.NodeBlockStats;
import org.jumbune.utils.beans.VirtualFileStatus;
import org.jumbune.utils.beans.VirtualFileSystem;

import com.google.gson.Gson;


/**
 * Calculates the distributed data stats
 * 
 */
public class DataDistributionStats {
	private YamlLoader yamlLoader = null;
	private int noOfBlocks = 0;
	private static final String REGEXEXP = "\\:";
	private static final String UNDERREPLICATEDBLCOK = "Under replicated blocks", CORRUPTREPLICATEDBLOCK = "Blocks with corrupt replicas",
			MISSINGBLOCK = "Missing blocks";
	private short replicationFactor;
	private Map<String, NodeBlockStats> nodeWeight = null;

	/**
	 * Instantiates a new data distribution stats.
	 *
	 * @param yamlLoader the yaml loader
	 */
	public DataDistributionStats(YamlLoader yamlLoader) {
		this.yamlLoader = yamlLoader;
	}

	/***
	 * This method calculate data across distributed nodes and also calculate no of copies of file a node contains and other block usefull information
	 * 
	 * @param config
	 *            YamlConfig
	 * @throws IOException
	 */
	public void calculateDistributedDataList(YamlLoader loader, ClusterInfo clusterInfo) throws IOException{
		YamlConfig config = loader.getYamlConfiguration();
		VirtualFileStatus fileStatus = null;

		DistributedDataInfo distributedDataInfo = new DistributedDataInfo();
		String pathOfFileInHadoop = null;
		long lengthOfFile = 0l;
		VirtualFileSystem fs = RemotingUtil.getVirtualFileSystem(yamlLoader);
		pathOfFileInHadoop = config.getDistributedHDFSPath();
		fileStatus = fs.getFileStatus(pathOfFileInHadoop);
		lengthOfFile = fs.getLengthOfFile(pathOfFileInHadoop);
		// setting replication factor here
		calculateNodeStats(fs, fileStatus, config);
		distributedDataInfo.setReplicationFactor(getReplicationFactor());
		distributedDataInfo.setTotalBlocksInCluster(noOfBlocks);
		distributedDataInfo.setTotalDataOnNode(changeLongDataToMB(fs.getSpaceConsumed(pathOfFileInHadoop)));
		distributedDataInfo.setFileSize(changeLongDataToMB(lengthOfFile));
		clusterInfo.setDistributedDataInfo(distributedDataInfo);
		parseBlockCheckInfo(distributedDataInfo);
	}

	/***
	 * Parses the distributed data info and sets block information
	 * @param dataInfo
	 * @throws IOException
	 */
	private void parseBlockCheckInfo(DistributedDataInfo dataInfo) throws IOException {
		int totalMissingBlock = 0, totalCorruptedBlock = 0, totalUnderReplicatedBlock = 0;
		String[] commandResult = ProfilerUtil.getDFSAdminReportCommandResult(yamlLoader);
		String tempValue = null;

		for (String line : commandResult) {
			if (line.contains(MISSINGBLOCK)) {
				tempValue = line.split(REGEXEXP)[1].trim().toString();
				if (tempValue != null) {
					totalMissingBlock = Integer.parseInt(tempValue);
					dataInfo.setMissingBlocks(tempValue);
				}
			}
			if (line.contains(CORRUPTREPLICATEDBLOCK)) {
				tempValue = line.split(REGEXEXP)[1].trim().toString();
				if (tempValue != null) {
					totalCorruptedBlock = Integer.parseInt(tempValue);
					dataInfo.setCorruptedBlocksInCluster(tempValue);
				}
			}
			if (line.contains(UNDERREPLICATEDBLCOK)) {
				tempValue = line.split(REGEXEXP)[1].trim().toString();
				if (tempValue != null) {
					totalUnderReplicatedBlock = Integer.parseInt(tempValue);
					dataInfo.setUnderReplicatedBlocks(tempValue);
				}
			}
		}
		addSuggestionsToProfiler(dataInfo, totalMissingBlock, totalCorruptedBlock, totalUnderReplicatedBlock);

	}

	/***
	 * This method add suggestion to the profiler based on the result contains any missingblock,corrupted block,under replicated block
	 * 
	 * @param dataInfo
	 * @param inconsistentBlockArray
	 */
	private void addSuggestionsToProfiler(DistributedDataInfo dataInfo, Integer... inconsistentBlockArray) {
		// new suggestion can be added later.for now we added only one suggestion for all inconsistency in cluster
		List<String> suggestionList = new ArrayList<String>();
		for (Integer inconsistentBlock : inconsistentBlockArray) {
			if (0 < inconsistentBlock && suggestionList.isEmpty()) {
				suggestionList.add("It seems your cluster is inconsistent,Please run fsck command ");
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
	 * This method Calculates node stats.
	 *
	 * @param fs the fs
	 * @param fileStatus the file status
	 * @param config the config
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void calculateNodeStats(VirtualFileSystem fs, VirtualFileStatus fileStatus, YamlConfig config) throws IOException {
		long lengthOfFile = 0l;
		String pathOfFileInHadoop = null;
		long tempSize = 0l;
		long lengthOfBlock = 0l;
		nodeWeight = new HashMap<String, NodeBlockStats>();
		List<String> fileCopyName = new ArrayList<String>();
		this.replicationFactor = fileStatus.getReplication();
		for (int i = 0; i < fileStatus.getReplication(); i++) {
			fileCopyName.add("copy" + i);
		}
		pathOfFileInHadoop = config.getDistributedHDFSPath();
		lengthOfFile = fs.getLengthOfFile(pathOfFileInHadoop);

		for (BlockLocation bklocation : (BlockLocation[]) fs.getFileBlockLocations(fileStatus, 0, lengthOfFile)) {
			noOfBlocks++;
			lengthOfBlock = bklocation.getLength();
			int count = 0;
			String nodeIP;
			for (String names : bklocation.getNames()) {
				nodeIP = names.split("\\:")[0];
				Map<String, String> fileWeight = null;
				Map<String, Integer> blockCopyInfo = null;
				NodeBlockStats nodeBlockStats = null;
				if (nodeWeight.containsKey(nodeIP)) {
					nodeBlockStats = nodeWeight.get(nodeIP);
					fileWeight = nodeBlockStats.getFileWeight();
					blockCopyInfo = nodeBlockStats.getBlockCopyInfo();
					// old code
					if (fileWeight.containsKey(fileCopyName.get(count))) {
						blockCopyInfo.put(fileCopyName.get(count), blockCopyInfo.get(fileCopyName.get(count)) + 1);
						tempSize = Long.parseLong(fileWeight.get(fileCopyName.get(count)));
						tempSize = tempSize + lengthOfBlock;
						fileWeight.put(fileCopyName.get(count), String.valueOf(tempSize));
					} else {
						fileWeight.put(fileCopyName.get(count), String.valueOf(lengthOfBlock));
						blockCopyInfo.put(fileCopyName.get(count), 1);
					}
					nodeBlockStats.setTotalBlocksOfFile(nodeBlockStats.getTotalBlocksOfFile() + 1);
					// old code
				} else {
					nodeBlockStats = new NodeBlockStats();
					blockCopyInfo = new HashMap<String, Integer>();
					fileWeight = new HashMap<String, String>();
					blockCopyInfo.put(fileCopyName.get(count), 1);
					fileWeight.put(fileCopyName.get(count), String.valueOf(lengthOfBlock));
					nodeBlockStats.setTotalBlocksOfFile(1);
					nodeBlockStats.setFileWeight(fileWeight);
					nodeBlockStats.setBlockCopyInfo(blockCopyInfo);
					nodeBlockStats.setNodeIP(nodeIP);
					nodeWeight.put(nodeIP, nodeBlockStats);
				}
				count++;
			}
		}
	}

	/***
	 * This method calculate data across distributed nodes and also calculate no of copies of file a node contains and other block usefull information
	 * 
	 * @param config
	 *            YamlConfig
	 * @throws IOException
	 */
	public void calculateDistributedDataList(YamlConfig config) throws IOException {
		VirtualFileStatus fileStatus = null;
		VirtualFileSystem fs = RemotingUtil.getVirtualFileSystem(yamlLoader);
		fileStatus = fs.getFileStatus(config.getDistributedHDFSPath());
		calculateNodeStats(fs, fileStatus, config);
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
	 */
	public String getNodeStats(String nodeIP, YamlConfig config) throws IOException{
		calculateDistributedDataList(config);
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
			tempFileWeight.setValue(changeLongDataToMB(Long.parseLong(tempFileWeight.getValue())));
		}
	}

	/***
	 * This method checks whether a givne path is a file or not
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 *             if path is not exist
	 */
	boolean isHDFSFile(String path) throws IOException {
		VirtualFileSystem fs = RemotingUtil.getVirtualFileSystem(yamlLoader);
		return fs.isFile(path);
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