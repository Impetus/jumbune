package org.jumbune.remoting.client.consumers;

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
 * The Class ObjectResponseHandler.
 */
public class ObjectResponseHandler extends SimpleChannelUpstreamHandler {
	
	/** The logger. */
	private static Logger logger = LogManager
			.getLogger(IdleResponseHandler.class);

	/** The barrier. */
	private CyclicBarrier barrier;

	/** The response object. */
	private Object responseObject;

	/**
	 * Instantiates a new object response handler.
	 *
	 * @param barrier the barrier
	 */
	public ObjectResponseHandler(CyclicBarrier barrier) {
		this.barrier = barrier;
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelClosed(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			{
			logger.debug("FYI channelClosed...");
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
		this.responseObject = (Object) e.getMessage();
		if(responseObject==null){
			barrier.reset();
		}
		if(responseObject instanceof String && ((String)responseObject).trim().length()==0){
			barrier.reset();
		}
		try {
			barrier.await();
		} catch (InterruptedException e1) {
			logger.error(e1);
		} catch (BrokenBarrierException e1) {
			logger.error(e1);
		}
	}

	/**
	 * Gets the response object.
	 *
	 * @return the response object
	 */
	public Object getResponseObject() {
		return this.responseObject;
	}

}