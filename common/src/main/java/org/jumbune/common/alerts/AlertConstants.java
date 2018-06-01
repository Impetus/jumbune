package org.jumbune.common.alerts;

public interface AlertConstants {
	
	String VOLUME_FAILURE_S = " volume failure(s).";

	String HAS = " has ";

	String DATA_NODE_WITH_STORAGE_ID = "DataNode with storageID: ";

	String N_A = "N/A";

	String FS_NAMESYSTEM_CAPACITY_TOTAL = "FSNamesystem.CapacityTotal";

	String NAME_NODE = "NameNode";

	String FS_NAMESYSTEM_CAPACITY_USED = "FSNamesystem.CapacityUsed";

	String NODE_S_UNAVAILABLE = " Node(s) unavailable";

	String FS_NAMESYSTEM_STATE_NUM_DEAD_DATA_NODES = "FSNamesystemState.NumDeadDataNodes";

	String FS_NAMESYSTEM_UNDER_REPLICATED_BLOCKS = "FSNamesystem.UnderReplicatedBlocks";

	String FS_NAMESYSTEM_BLOCKS_TOTAL = "FSNamesystem.BlocksTotal";

	String FS_DATASET_STATE2 = "FSDatasetState-";

	String NUM_FAILED_VOLUMES = ".NumFailedVolumes";

	String DATA_NODE = "DataNode";
	
	/** The Constant DISKSPACE_ALERT_MESSAGE. */
	String DISKSPACE_ALERT_MESSAGE ="Running out of diskspace";
	
	/** The Constant UNDER_REPLICATED_BLOCK_MESSAGE. */
	String UNDER_REPLICATED_BLOCK_MESSAGE =" under replicated block(s) found";
	
	/** The Constant HDFS_SPACE_USAGE_MESSAGE. */
	String HDFS_SPACE_USAGE_MESSAGE = " HDFS is running out of space";
	
	/** The Constant DEAMON_WENT_DOWN. */
	String DEAMON_WENT_DOWN = " went down";
	
	String NULL = "null";

	String STORAGE_ID = "[ StorageID: ";

	String REMAINING = ".Remaining";

	String CAPACITY = ".Capacity";

	String FS_DATASET_STATE = "FSDatasetState";
	
	String CLOSING_BRACKET = "] ";
	
	String RESOURCE_MANAGER = "ResourceManager" ;
	
	String HISTORY_SERVER = "JobHistoryServer" ;
	
	String NODE_MANAGER = "NodeManager";

	String NAMENODE_DATA_DIRECTORY = "dfs.namenode.name.dir";

	String NAMENODE_TEMP_DIRECTORY = "hadoop.tmp.dir";

	String DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY = "dfs.namenode.fs-limits.max-directory-items";

}