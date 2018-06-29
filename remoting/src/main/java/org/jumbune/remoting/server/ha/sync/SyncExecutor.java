package org.jumbune.remoting.server.ha.sync;

/**
 * The Interface SyncExecutor provides methods to sync agent metadata.
 */
public interface SyncExecutor {

	/**
	 * Sync.
	 *
	 * @return the boolean
	 */
	boolean sync();

	/**
	 * Shutdown.
	 */
	void shutdown();
}