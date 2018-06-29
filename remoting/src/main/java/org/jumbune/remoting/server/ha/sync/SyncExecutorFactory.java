package org.jumbune.remoting.server.ha.sync;

/**
 * A factory for creating SyncExecutor objects.
 */
public class SyncExecutorFactory {
	
	private String zkConnectionString;

	public SyncExecutorFactory(String zkConnectionString) {
		this.zkConnectionString = zkConnectionString;
	}

	/**
	 * Creates a new SyncExecutor object.
	 *
	 * @param syncExecutorType the sync executor type
	 * @return the sync executor
	 */
	public SyncExecutor createSyncExecutor(String syncExecutorType) {
		if (syncExecutorType == null) {
			return null;
		} else if (syncExecutorType.equals("rsync")) {
			return new RSyncExecutor(this.zkConnectionString);
		}
		return null;
	}
}