package org.jumbune.utils.conf.beans;


/**
 * The Enum AlertType.
 */
public enum AlertType {

	//**********************Non-Configurable Alerts****************
	/** The excessive resource allocation. */
	EXCESSIVE_RESOURCE_ALLOCATION,
	
	/** The container pool utilization. */
	CONTAINER_POOL_UTILIZATION,
	
	/** The map reduce app failure. */
	MAP_REDUCE_APP_FAILURE,
	
	/** The cluster time desync. */
	CLUSTER_TIME_DESYNC,
	
	/** The queue child capacity overflow. */
	QUEUE_CHILD_CAPACITY_OVERFLOW,
	
	/** The node unhealthy. */
	NODE_UNHEALTHY,
	
	/** The hadoop daemon down. */
	HADOOP_DAEMON_DOWN,
	
	/** The yarn property check. */
	YARN_PROPERTY_CHECK,
	
	/** The resource utilization check. */
	RESOURCE_UTILIZATION_CHECK,
	
	/** The dn volume failure check. */
	DN_VOLUME_FAILURE_CHECK,
	
	//**********************Configurable Alerts****************
	
	/** The disk space utilization. */
	DISK_SPACE_UTILIZATION,
	
	/** The under replicated blocks. */
	UNDER_REPLICATED_BLOCKS,
	
	/** The hdfs utilization. */
	HDFS_UTILIZATION,
	
	/** The queue utilization. */
	QUEUE_UTILIZATION	
}
