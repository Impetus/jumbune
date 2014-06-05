package org.jumbune.remoting.handlers;

import static org.jboss.netty.channel.Channels.write;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jumbune.remoting.common.RemotingConstants;


/**
 * The Class AbstractDownstreamMashaller.
 */
public abstract class AbstractDownstreamMashaller implements ChannelDownstreamHandler {

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelDownstreamHandler#handleDownstream(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) {
		if (!(evt instanceof MessageEvent)) {
			ctx.sendDownstream(evt);
			return;
		}

		MessageEvent e = (MessageEvent) evt;
		if (!doEncode(ctx, e)) {
			ctx.sendDownstream(e);
		}
	}
	
	/**
	 * Do encode.
	 *
	 * @param ctx the ctx
	 * @param e the e
	 * @return true, if successful
	 */
	private boolean doEncode(ChannelHandlerContext ctx, MessageEvent e) {
		Object originalMessage = e.getMessage();
		Object encodedMessage = encode(ctx, e.getChannel(), originalMessage);
		if (originalMessage.equals(encodedMessage)) {
			return false;
		} else if (encodedMessage != null) {
			write(ctx, e.getFuture(), encodedMessage, e.getRemoteAddress());
		}
		return true;
	}
	
	/**
	 * To bytes.
	 *
	 * @param i the i
	 * @return the byte[]
	 */
	protected byte[] toBytes(int i) {
		byte[] result = new byte[RemotingConstants.FOUR];
		result[0] = (byte) (i >> RemotingConstants.TWENTY_FOUR);
		result[1] = (byte) (i >> RemotingConstants.SIXTEEN);
		result[2] = (byte) (i >> RemotingConstants.EIGHT);
		result[RemotingConstants.THREE] = (byte) (i);
		return result;
	}	
	
	/**
	 * Encode.
	 *
	 * @param ctx the ctx
	 * @param channel the channel
	 * @param originalMessage the original message
	 * @return the object
	 */
	protected abstract Object encode(ChannelHandlerContext ctx, Channel channel, Object originalMessage);	
}
