package org.jumbune.remoting.server.ha.sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class RsyncExecutor that will perform rsync execution in async manner.
 */
public class RSyncExecutor implements SyncExecutor {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(RSyncExecutor.class);

	/** The executor service. */
	static ExecutorService executorService;

	/** The is shutdown. */
	static boolean isShutdown = false;
	
	private String zkConnectionString;

	/**
	 * Instantiates a new rsync executor.
	 */
	public RSyncExecutor(String zkConnectionString) {
		this.zkConnectionString = zkConnectionString;
		if (executorService == null && !isShutdown) {
			synchronized (RSyncExecutor.class) {
				if (executorService == null && !isShutdown) {
					executorService = new ThreadPoolExecutor(1, 1, 30,
							TimeUnit.SECONDS,
							new LinkedBlockingDeque<Runnable>(1)); // LinkedBlockingDeque will hole only single rsync command and ignore multiple calls
				}
			}
		}
	}

	/*
	 * sync method will run rsync task in async manner
	 *
	 * @see org.jumbune.remoting.ha.sync.SyncExecutor#sync()
	 */
	@Override
	public boolean sync() {
		// check for executorService is not null and terminated/shutdown
		if (executorService == null) {
			LOGGER.error("Can not execute task with null executorService");
		} else if (executorService.isTerminated()
				|| executorService.isShutdown()) {
			LOGGER.error("Can not hand over task to terminated executorService");
		} else {
			RSyncTask rsyncTask = new RSyncTask(zkConnectionString);
			try {
				executorService.execute(rsyncTask);
				return true;
			} catch (RejectedExecutionException e) {
				LOGGER.warn("Rsync task queue is full.Ignoring multiple rsync tasks.");
			} catch (Exception e) {
				LOGGER.error("Failed to invoke rsync execution", e);
			}
		}
		return false;
	}

	/*
	 * shutdown the executorService used to run rsync tasks
	 *
	 * @see org.jumbune.remoting.ha.sync.SyncExecutor#shutdown()
	 */
	@Override
	public void shutdown() {
		LOGGER.info("Received a request to shutdown RsyncExecutor");
		try {
			if (executorService != null) {
				synchronized (RSyncExecutor.class) {
					if (executorService != null) {
						executorService.shutdown();
						executorService.awaitTermination(Long.MAX_VALUE,
								TimeUnit.SECONDS);
						isShutdown = true;
						LOGGER.info("RsyncExecutor shutdown completed");
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to shutdown RsyncExecutor executorService", e);
		}
	}
	
}