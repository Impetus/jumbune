package org.jumbune.remoting.client;

import org.jboss.netty.channel.ChannelPipelineFactory;



/**
 * The Class ChannelPipelineFactoryCache is used for initializing the factory pipeline cache.
 */
@SuppressWarnings("serial")
public class ChannelPipelineFactoryCache extends
		LRUCache<String, ChannelPipelineFactory> {

	
	/**
	 * Instantiates a new channel pipeline factory cache.
	 *
	 * @param capacity the capacity
	 */
	public ChannelPipelineFactoryCache(int capacity) {
		super(capacity);
	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.LRUCache#removeEldestEntry(java.util.Map.Entry)
	 */
	@Override
	protected boolean removeEldestEntry(
			java.util.Map.Entry<String, ChannelPipelineFactory> eldest) {
		if (size() > super.getCapacity()) {
			eldest.getValue();
			// TODO: clean up
			return true;
		}
		return false;
	}
}
