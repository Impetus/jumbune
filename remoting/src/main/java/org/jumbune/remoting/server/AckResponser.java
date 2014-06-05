package org.jumbune.remoting.server;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;


/**
 * The Class AckResponser is used for acknowledging the response when something is written into a channel.
 */
public class AckResponser extends SimpleChannelUpstreamHandler {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(AckResponser.class);

	/**
	 * Invoked when something was written into a {@link Channel}.
	 *
	 * @param ctx the ctx
	 * @param e the e
	 */
	@Override
	public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e){
		ctx.sendUpstream(e);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		LOGGER.debug("Message received "+e.getMessage());
		Object obj = e.getMessage();
		File fileHandle = (File) obj;
		if (fileHandle.exists()) {
			e.getChannel().write("Ack");
		} else {
			LOGGER.warn("Failed to save the sent file(s) from client, unable to get handle of the file(s) on JumbuneAgent");
			e.getChannel().write("FailAck");
		}
	}
}
