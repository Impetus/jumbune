package org.jumbune.remoting.handlers;

import static org.jboss.netty.channel.Channels.fireMessageReceived;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jumbune.remoting.common.RemotingConstants;



/**
 * The Class AbstractUpstreamMarshaller.
 */
public abstract class AbstractUpstreamMarshaller implements ChannelUpstreamHandler{

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelUpstreamHandler#handleUpstream(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent evt) throws IOException, ClassNotFoundException, InterruptedException, BrokenBarrierException{
		if (!(evt instanceof MessageEvent)) {
			ctx.sendUpstream(evt);
			return;
		}

		MessageEvent e = (MessageEvent) evt;
		Object originalMessage = e.getMessage();
		Object decodedMessage = decode(ctx, e.getChannel(), originalMessage);
		if (originalMessage.equals(decodedMessage)) {
			ctx.sendUpstream(evt);
		} else if (decodedMessage != null) {
			fireMessageReceived(ctx, decodedMessage, e.getRemoteAddress());
		}
	}
		
	/**
	 * Byte array to int.
	 *
	 * @param b the b
	 * @return the int
	 */
	protected int byteArrayToInt(byte[] b) {
		return prefixByteArrayExpressionToInt(b,RemotingConstants.THREE,2,RemotingConstants.EIGHT) | evaluateMiddleOrderByteArrayExpressions(b, 1, RemotingConstants.SIXTEEN)| (b[0] & RemotingConstants.ZERO_CROSS_FF) << RemotingConstants.TWENTY_FOUR;
	}

	/**
	 * Prefix byte array expression to int.
	 *
	 * @param b the b
	 * @param byteIndex the byte index
	 * @return the int
	 */
	private int prefixByteArrayExpressionToInt(byte[] b,Integer ... byteIndex) {
		return b[byteIndex[0]] & RemotingConstants.ZERO_CROSS_FF | (b[byteIndex[1]] & RemotingConstants.ZERO_CROSS_FF) << byteIndex[2];
	}

	/**
	 * Msb byte array to int.
	 *
	 * @param b the b
	 * @return the int
	 */
	protected int msbByteArrayToInt(byte[] b) {
		return prefixByteArrayExpressionToInt(b, RemotingConstants.SEVEN ,RemotingConstants.SIX ,RemotingConstants.EIGHT) | evaluateMiddleOrderByteArrayExpressions(b, RemotingConstants.FIVE,RemotingConstants.SIXTEEN) | (b[RemotingConstants.FOUR] & RemotingConstants.ZERO_CROSS_FF) << RemotingConstants.TWENTY_FOUR;
	}

	/**
	 * Evaluate middle order byte array expressions.
	 *
	 * @param b the b
	 * @param byteIndex the byte index
	 * @return the int
	 */
	private int evaluateMiddleOrderByteArrayExpressions(byte[] b,Integer ... byteIndex) {
		return (b[byteIndex[0]] & RemotingConstants.ZERO_CROSS_FF) << byteIndex[1];
	}

	
	/**
	 * Decode.
	 *
	 * @param ctx the ctx
	 * @param channel the channel
	 * @param msg the msg
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InterruptedException the interrupted exception
	 * @throws BrokenBarrierException the broken barrier exception
	 */
	protected abstract Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws IOException, ClassNotFoundException, InterruptedException, BrokenBarrierException ;	
}
