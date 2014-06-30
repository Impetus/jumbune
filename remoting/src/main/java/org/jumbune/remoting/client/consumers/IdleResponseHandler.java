package org.jumbune.remoting.client.consumers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class IdleResponseHandler.
 */
public class IdleResponseHandler extends SimpleChannelInboundHandler<Object> {
	
	/** The logger. */
	private static Logger logger = LogManager
			.getLogger(IdleResponseHandler.class);

	/* (non-Javadoc)
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx,
			Object msg) throws Exception {
		logger.warn(msg);
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception {
	    logger.error("Internal Server Error",cause);
	}
}
