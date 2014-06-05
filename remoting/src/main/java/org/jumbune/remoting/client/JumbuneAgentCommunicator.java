package org.jumbune.remoting.client;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jumbune.remoting.common.RemotingConstants;




/**
 * The Class JumbuneAgentCommunicator.
 */
public final class JumbuneAgentCommunicator {

	/** The host. */
	private final String host;
	
	/** The port. */
	private final int port;
	
	private  static final Logger LOGGER = LogManager.getLogger(JumbuneAgentCommunicator.class);
	
	/** The factory cache. */
	private static ChannelPipelineFactoryCache factoryCache = new ChannelPipelineFactoryCache(
			RemotingConstants.SEVEN);;

	/** The bootstrap. */
	private ClientBootstrap bootstrap;

	/**
	 * Instantiates a new jumbune agent communicator.
	 *
	 * @param host the host
	 * @param port the port
	 */
	public JumbuneAgentCommunicator(String host,
			int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Release bootstrap resources.
	 */
	public void releaseBootstrapResources() {
		bootstrap.releaseExternalResources();
	}

	/**
	 * Creates the future.
	 *
	 * @param channelPipelineFactory the channel pipeline factory
	 * @return the channel future
	 */
	private ChannelFuture createFuture(
			final ChannelPipelineFactory channelPipelineFactory) {
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(channelPipelineFactory);
		return bootstrap.connect(new InetSocketAddress(host, port));
	}

	/**
	 * Gets the channel future.
	 *
	 * @param channelPipelineFactory the channel pipeline factory
	 * @return the channel future
	 */
	public ChannelFuture getChannelFuture(
			ChannelPipelineFactory channelPipelineFactory) {
		return createFuture(channelPipelineFactory);
	}

	/**
	 * Creates the or get channel pipeline factory.
	 *
	 * @param requestedOperation the requested operation
	 * @param handlers the handlers
	 * @return the channel pipeline factory
	 */
	public ChannelPipelineFactory createOrGetChannelPipelineFactory(
			String requestedOperation, List<ChannelHandler> handlers) {
		
		if (!factoryCache.containsKey(requestedOperation)) {
			LOGGER.debug("Requested Operation keys ["+requestedOperation+"]");
			factoryCache.put(requestedOperation, factoryBuilder(handlers));
		}
		return factoryCache.get(requestedOperation);
	}

	/**
	 * Factory builder.
	 *
	 * @param handlers the handlers
	 * @return the channel pipeline factory
	 */
	private ChannelPipelineFactory factoryBuilder(
			final List<ChannelHandler> handlers) {
		return new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeLine = Channels.pipeline();
				for (ChannelHandler handler : handlers) {
					pipeLine.addLast(handler.toString(), handler);
				}
				return pipeLine;
			}
		};
	}
}