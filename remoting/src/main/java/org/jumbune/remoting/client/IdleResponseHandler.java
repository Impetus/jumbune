package org.jumbune.remoting.client;

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

	@Override
	protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx,
			Object msg) throws Exception {
		logger.warn(msg);		
	}

}
