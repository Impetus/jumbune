package org.jumbune.remoting.server;

import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class AckResponser is used for acknowledging the response when something is written into a channel.
 */
public class AckResponser extends SimpleChannelInboundHandler<Serializable> {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(AckResponser.class);

	@Override
	protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx,
			Serializable msg) throws Exception {
		if (msg!=null) {
			ctx.channel().writeAndFlush("Ack");
		} else {
			LOGGER.warn("Failed to save the sent file(s) from client, unable to get handle of the file(s) on JumbuneAgent");
			ctx.channel().writeAndFlush("FailAck");
		}
	}
}