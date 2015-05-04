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

	/** The bootstrap. */
	private Bootstrap bootstrap;

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
	 * Creates the future. This is used for each of the Remoter instance command. We are initializing event group only once. 
	 *
	 * @param channelPipelineFactory the channel pipeline factory
	 * @return the channel future
	 * @throws InterruptedException 
	 */
	public ChannelFuture getChannelFuture(
			final List<ChannelHandler> handlers) throws InterruptedException {
		ChannelFuture future;
		bootstrap = new Bootstrap();
		bootstrap.group(SingleNIOEventGroup.eventLoopGroup());
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
        return future;
	}
}