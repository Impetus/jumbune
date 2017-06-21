package org.jumbune.common.beans.cluster;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.LRUCache;

import org.jumbune.common.utils.JobRequestUtil;
	
/**
 * The Class ClusterCache. This class acts as a cache for cluster as reading the cluster configurations from disk time and again, is pretty expensive.
 * Method {@code getCluster(final String clusterName)} should be called on cache instance. If cluster is already present it returns the cluster or else it brings the 
 * cluster in cache for further use. This class should be used when there is a possibility of exploiting temporal locality for cluster i.e. when the same cluster is likely 
 * to be used time and again.
 */
@SuppressWarnings("serial")
public class ClusterCache extends LRUCache<String, EnterpriseCluster> {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(ClusterCache.class);

	/**
	 * Instantiates a new cluster cache.
	 *
	 * @param capacity the capacity
	 */
	public ClusterCache(int capacity) {
		super(capacity);
	}

	/**
	 * @see org.jumbune.utils.LRUCache#removeEldestEntry(java.util.Map.Entry)
	 */
	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<String, EnterpriseCluster> eldest) {
		if (size() > super.getCapacity()) {
			remove(eldest.getKey());
			return true;
		}
		return false;
	}
	
	@Override
    public EnterpriseClusterDefinition get(Object key) {
		try {
			return getCluster((String) key);
		} catch (IOException e) {
			LOGGER.error("Unable to get Cluster.", e.getMessage());
			return null;
		}
    }

	/**
	 * Gets the cluster. This method guarantees to return Cluster instance provided the valid one exists on the disk if it does not already reside in memory.
	 *
	 * @param clusterName the cluster name
	 * @return the cluster
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public synchronized EnterpriseClusterDefinition getCluster(final String clusterName) throws IOException {
		EnterpriseClusterDefinition cluster = (EnterpriseClusterDefinition) super.get(clusterName);
		if (cluster == null) {
			cluster = JobRequestUtil.getClusterByName(clusterName);
			super.put(clusterName, cluster);
		} 
		return cluster;
	}

}
