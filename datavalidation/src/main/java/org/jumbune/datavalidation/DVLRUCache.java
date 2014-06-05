package org.jumbune.datavalidation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.LRUCache;



/**
 * Implementation of LRU cache.
 * 

 * 
 */
@SuppressWarnings("serial")
public class DVLRUCache extends LRUCache<String, BufferedWriter> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(DVLRUCache.class);

	/**
	 * Instantiates a new dVLRU cache.
	 *
	 * @param capacity the capacity
	 */
	public DVLRUCache(int capacity) {
		super(capacity);
	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.LRUCache#removeEldestEntry(java.util.Map.Entry)
	 */
	@Override
	protected boolean removeEldestEntry(
			Map.Entry<String, BufferedWriter> eldestEntry) {
		if (size() > super.getCapacity()) {
			try {
				eldestEntry.getValue().close();
			} catch (IOException ioe) {
				LOGGER.error(
						"Error occurred while removing eldest entry from LRU cache: ",
						ioe);
			}
			return true;
		}
		return false;
	}
}