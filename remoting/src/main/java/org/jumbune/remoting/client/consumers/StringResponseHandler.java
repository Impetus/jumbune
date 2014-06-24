package org.jumbune.remoting.client.consumers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.RemotingConstants;

/**
 * The Class StringResponseHandler.
 */
public class StringResponseHandler extends SimpleChannelInboundHandler<String> {
	
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


	@Override
	protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx,
			String msg) throws Exception {
		CyclicBarrier barrier = (CyclicBarrier) ctx.channel().attr(RemotingConstants.barrierKey).get();
		if (barrier != null) {
			logger.debug("Message received ["+msg+"]");
			byte[] responseBytes = msg.getBytes();
			if(responseBytes.length>0){
				if("FailAck".equals(new String(responseBytes))){
					logger.warn("Jumbune Agent failed to write file on other side, refer Agent logs for details");
				}
			}
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

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception {
	    logger.error("Internal Server Error",cause);
	}
}
