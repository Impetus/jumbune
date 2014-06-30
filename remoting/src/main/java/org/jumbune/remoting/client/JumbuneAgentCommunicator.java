package org.jumbune.remoting.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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
//	private static ChannelPipelineFactoryCache factoryCache = new ChannelPipelineFactoryCache(RemotingConstants.SEVEN);

	/** The bootstrap. */
	private Bootstrap bootstrap;
	
	private EventLoopGroup group;

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
	    group = new NioEventLoopGroup();		
	}

	/**
	 * Release bootstrap resources.
	 * @throws InterruptedException 
	 */
	public void releaseBootstrapResources() throws InterruptedException {
		group.shutdownGracefully();
//		group.terminationFuture().sync();
	}

	/**
	 * Creates the future.
	 *
	 * @param channelPipelineFactory the channel pipeline factory
	 * @return the channel future
	 * @throws InterruptedException 
	 */
	public ChannelFuture getChannelFuture(
			final List<ChannelHandler> handlers) throws InterruptedException {
		ChannelFuture future;
		bootstrap = new Bootstrap();
		bootstrap.group(group);
         bootstrap.channel(NioSocketChannel.class)
         .option(ChannelOption.SO_KEEPALIVE, true)
         .option(ChannelOption.TCP_NODELAY, true)
         .handler(new ChannelInitializer<SocketChannel>() {
             @Override
             protected void initChannel(SocketChannel ch) throws Exception {
                 ChannelPipeline p = ch.pipeline();
 				for (ChannelHandler handler : handlers) {
					 p.addLast(handler.toString(), handler);
				}
             }
           });
        // Make the connection attempt.         
        future = bootstrap.connect(host, port).sync();
        // Wait until the connection is closed.
//        future.channel().closeFuture().sync();
        return future;
	}

/*	*//**
	 * Creates the or get channel pipeline factory.
	 *
	 * @param requestedOperation the requested operation
	 * @param handlers the handlers
	 * @return the channel pipeline factory
	 *//*
	public ChannelPipelineFactory createOrGetChannelPipelineFactory(
			String requestedOperation, List<ChannelHandler> handlers) {
		
		if (!factoryCache.containsKey(requestedOperation)) {
			LOGGER.debug("Requested Operation keys ["+requestedOperation+"]");
			factoryCache.put(requestedOperation, factoryBuilder(handlers));
		}
		return factoryCache.get(requestedOperation);
	}
*/
}