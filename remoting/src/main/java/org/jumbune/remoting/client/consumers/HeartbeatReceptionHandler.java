package org.jumbune.remoting.client.consumers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.RemotingConstants;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * The Class HeartbeatReceptionHandler. This class acts a handler(receiver in this case) for heartbeats coming from
 * jumbune-agent. Heartbeats are prolonged till a particular is in execution at jumbune-agent.
 * The method {@code getLastBeatTime()} returns the point of time(currentTimeMillis) when the heartbeat was last
 * received which helps in the detection of agent state(live or dead) while a command is in execution.
 *  
 */
public class HeartbeatReceptionHandler extends SimpleChannelInboundHandler<String> {
	
	/** The logger. */
	private static final Logger LOGGER = LogManager
			.getLogger(HeartbeatReceptionHandler.class);

	/** The last beat time. */
	private long lastBeatTime;

	/**
	 * Instantiates a new heartbeat reception handler.
	 */
	public HeartbeatReceptionHandler() {
		lastBeatTime = System.currentTimeMillis();
	}

	/**
	 * Gets the last beat time.
	 *
	 * @return the last beat time
	 */
	public long getLastBeatTime() {
		return lastBeatTime;
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx,
			String msg) throws Exception {
	   if(RemotingConstants.HEART_BEAT_MSG.equals(msg)) {
			LOGGER.debug("HEART_BEAT received");
		lastBeatTime = System.currentTimeMillis();
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception {
	    LOGGER.error("Internal Server Error",cause);
	}
	
}