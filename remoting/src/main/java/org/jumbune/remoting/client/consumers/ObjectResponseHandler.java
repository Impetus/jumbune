package org.jumbune.remoting.client.consumers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class ObjectResponseHandler.
 */
public class ObjectResponseHandler extends SimpleChannelInboundHandler<Object> {
	
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

	@Override
	protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx,
			Object msg) throws Exception {
		this.responseObject = msg;
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

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception {
	    logger.error("Internal Server Error",cause);
	}
	
}