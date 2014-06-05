package org.jumbune.remoting.client.consumers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;


/**
 * The Class StringResponseHandler.
 */
public class StringResponseHandler extends SimpleChannelUpstreamHandler {
	
	/** The logger. */
	private static Logger logger = LogManager
			.getLogger(StringResponseHandler.class);

	/** The stream. */
	private InputStream stream;

	/**
	 * Instantiates a new string response handler.
	 */
	public StringResponseHandler() {
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelClosed(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			{
			logger.debug("Channel is now getting Closed...");
		ctx.sendUpstream(e);
	}

	/**
	 * Invoked when something was written into a {@link Channel}.
	 *
	 * @param ctx the ctx
	 * @param e the e
	 */
	@Override
	public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e)
			{
		ctx.sendUpstream(e);
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		CyclicBarrier barrier = (CyclicBarrier) ctx.getChannel()
				.getAttachment();
		if (barrier != null) {
			logger.debug("Message received ["+e.getMessage()+"]");
			byte[] responseBytes = ((String) e.getMessage()).getBytes();
			this.stream = new ByteArrayInputStream(responseBytes);
			try {
				barrier.await();
			} catch (InterruptedException e1) {
				logger.error(e1);
			} catch (BrokenBarrierException e1) {
				logger.error(e1);
			}
		} else {
			logger.warn("No attachment found...");
		}
	}

	/**
	 * Gets the response stream.
	 *
	 * @return the response stream
	 */
	public InputStream getResponseStream() {
		return this.stream;
	}
}
