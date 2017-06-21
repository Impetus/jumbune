package org.jumbune.remoting.client;

import java.net.ConnectException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.ChannelConnectException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * The Class JumbuneAgentCommunicator.
 */
public class JumbuneAgentCommunicator {

	/** The host. */
	protected String host;
	
	/** The port. */
	protected int port;
	
	private  static final Logger LOGGER = LogManager.getLogger(JumbuneAgentCommunicator.class);

	/** The bootstrap. */
	private Bootstrap bootstrap;

	/**
	 * Instantiates a new jumbune agent communicator.
	 *
	 * @param host the host
	 * @param port the port
	 */
	public JumbuneAgentCommunicator(String zkHost,
			int zkPort) {
		this.host = zkHost;
		this.port = zkPort;
	}
		
	/**
	 * Creates the future. This is used for each of the Remoter instance command. We are initializing event group only once. 
	 *
	 * @param channelPipelineFactory the channel pipeline factory
	 * @return the channel future
	 * @throws InterruptedException 
	 */
	public ChannelFuture getChannelFuture(
			final List<ChannelHandler> handlers) throws InterruptedException, ConnectException, ChannelConnectException {
		ChannelFuture future;
   		LOGGER.debug("Going to connect to leader Jumbune Agent on ["+ host + ":"
					+ port +"]");

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
        if(future==null){
        	throw new ChannelConnectException("Failed to get Future");
        }
        return future;
	}

	public void close(){
		EventLoopGroup loopGroup = bootstrap.group();
		loopGroup.shutdownGracefully();
		try {
			loopGroup.terminationFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}