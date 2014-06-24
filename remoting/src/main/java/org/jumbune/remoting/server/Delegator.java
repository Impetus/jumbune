package org.jumbune.remoting.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class Delegator.
 */
public class Delegator extends SimpleChannelInboundHandler<String> {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(Delegator.class);

	/** The receive directory. */
	private String receiveDirectory;

	/**
	 * Instantiates a new delegator.
	 * 
	 * @param receiveDirectory
	 *            the receive directory
	 */
	public Delegator(String receiveDirectory) {
		this.receiveDirectory = receiveDirectory;
	}

	@Override
	protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx,
			String relativePath) throws Exception {
		ctx.channel().writeAndFlush((String) this.receiveDirectory + File.separator + relativePath);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception {
	    LOGGER.error("Internal Server Error",cause);
	}	
}
