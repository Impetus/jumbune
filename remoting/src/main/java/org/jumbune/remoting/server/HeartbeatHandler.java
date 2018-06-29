package org.jumbune.remoting.server;

import org.jumbune.remoting.common.RemotingConstants;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class HeartbeatHandler extends IdleStateHandler {

	private long lastBeatTime;
	
	private static final int DIFF_BETWEEN_BEATS = JumbuneAgent.getHeartBeatMillis() / 2; 
	
	public HeartbeatHandler(int readerIdleTimeMillis, int writerIdleTimeMillis, int allIdleTimeMillis) {
		super(readerIdleTimeMillis/1000, writerIdleTimeMillis/1000, allIdleTimeMillis/1000);
	}

	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
		//send heart beats only if there is some significant difference between the timings of two successive beats
		if((System.currentTimeMillis() - lastBeatTime) > DIFF_BETWEEN_BEATS){
			ctx.channel().writeAndFlush(RemotingConstants.HEART_BEAT_MSG);			
		}
		lastBeatTime = System.currentTimeMillis();
	}
}