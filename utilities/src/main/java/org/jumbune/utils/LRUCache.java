package org.jumbune.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of LRU cache.
 * 
 * 
 */
@SuppressWarnings("serial")
public abstract class LRUCache<K, V> extends LinkedHashMap<K, V> {

	
	/**
	 * Maximum number of element the cache can contain
	 */
	private final int capacity;

	
   protected int getCapacity() {
		return capacity;
	}

/**
    * Constructs an empty <tt>LinkedHashMap</tt> instance. Cache Capacity
    * argument is used to ensure the size of the cache.
    * 
    * @param cacheCapacity
    */	
	public LRUCache(int capacity) {
		super();
		this.capacity = capacity;
	}
	
	/**
	 * @param eldest
	 * @return <tt>true</tt> if element insertion increases the size of the cache
	 *         than cache capacity.
	*/
	@Override
	protected abstract boolean removeEldestEntry(Map.Entry<K, V> eldest);
	
}