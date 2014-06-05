package org.jumbune.remoting.server;


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.handlers.ArchiveDecoder;
import org.jumbune.remoting.handlers.ArchiveEncoder;
import org.jumbune.remoting.handlers.LogFilesDecoder;
import org.jumbune.remoting.handlers.LogFilesEncoder;
import org.jumbune.remoting.handlers.ObjectDecoder;
import org.jumbune.remoting.handlers.ObjectEncoder;




/**
 * The Class JumbuneAgentDecoder  
 */
public class JumbuneAgentDecoder extends FrameDecoder {

	/** The Constant STREAMER. */
	private static final String STREAMER = "streamer";
	
	/** The Constant ACK_RESPONSER. */
	private static final String ACK_RESPONSER = "ackResponser";
	
	/** The Constant ENCODER. */
	private static final String ENCODER = "encoder";
	
	private static final String DECODER = "decoder";
	
	/** The receive directory. */
	private String receiveDirectory;

	/**
	 * Instantiates a new jumbune agent decoder.
	 *
	 * @param receiveDirectory the receive directory
	 */
	public JumbuneAgentDecoder(final String receiveDirectory) {
		String tempDir=receiveDirectory;
		if (receiveDirectory.charAt(receiveDirectory.length() - 1) == '/') {
			tempDir = receiveDirectory.substring(0,
					receiveDirectory.length() - 1);
		}
		this.receiveDirectory = tempDir;
	}

	/* (non-Javadoc)
	 * @see org.jboss.netty.handler.codec.frame.FrameDecoder#decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, org.jboss.netty.buffer.ChannelBuffer)
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) {
		if (buffer.readableBytes() < RemotingConstants.THREE) {
			return null;
		}
		final int magic1 = buffer.getUnsignedByte(0);
		final int magic2 = buffer.getUnsignedByte(1);
		final int magic3 = buffer.getUnsignedByte(2);
		if (isJar(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(buffer);
			invokeJarReceiveHandler(ctx);
		} else if (isJas(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(buffer);
			invokeJarSendHandler(ctx);
		} else if (isTxr(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(buffer);
			invokeLogFilesReceiveHandler(ctx);
		} else if (isTxs(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(buffer);
			invokeLogFilesSendHandler(ctx);
		} else if (isCmd(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(buffer);
			invokeFireAndForgetCommandHandler(ctx);
		} else if(isCma(magic1, magic2, magic3)){
			throwAwayReadUnsignedBytes(buffer);
			invokeAsyncFireAndForgetCommandHandler(ctx);
		}else if (isCmg(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(buffer);
			invokeFireAndGetCommandHandler(ctx);
		} else if (isCmo(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(buffer);
			invokeFireAndGetObjectResponseCommandHandler(ctx);
		}
		// Forward the current read buffer as is to the new handlers.
		return buffer.readBytes(buffer.readableBytes());
	}

	/**
	 * Throw away read unsigned bytes.
	 *
	 * @param buffer the buffer
	 */
	private void throwAwayReadUnsignedBytes(ChannelBuffer buffer) {
		buffer.readUnsignedByte();
		buffer.readUnsignedByte();
		buffer.readUnsignedByte();
	}

	/**
	 * Checks if is jar.
	 *
	 * @param magic1 the magic1
	 * @param magic2 the magic2
	 * @param magic3 the magic3
	 * @return true, if is jar
	 */
	private boolean isJar(int magic1, int magic2, int magic3) {
		return magic1 == RemotingConstants.JAR_MAGIC_1
				&& magic2 == RemotingConstants.JAR_MAGIC_2
				&& magic3 == RemotingConstants.JAR_MAGIC_3;
	}

	/**
	 * Checks if is jas.
	 *
	 * @param magic1 the magic1
	 * @param magic2 the magic2
	 * @param magic3 the magic3
	 * @return true, if is jas
	 */
	private boolean isJas(int magic1, int magic2, int magic3) {
		return magic1 == RemotingConstants.JAS_MAGIC_1
				&& magic2 == RemotingConstants.JAS_MAGIC_2
				&& magic3 == RemotingConstants.JAS_MAGIC_3;
	}

	/**
	 * Checks if is txr.
	 *
	 * @param magic1 the magic1
	 * @param magic2 the magic2
	 * @param magic3 the magic3
	 * @return true, if is txr
	 */
	private boolean isTxr(int magic1, int magic2, int magic3) {
		return magic1 == RemotingConstants.TXR_MAGIC_1
				&& magic2 == RemotingConstants.TXR_MAGIC_2
				&& magic3 == RemotingConstants.TXR_MAGIC_3;
	}

	/**
	 * Checks if is txs.
	 *
	 * @param magic1 the magic1
	 * @param magic2 the magic2
	 * @param magic3 the magic3
	 * @return true, if is txs
	 */
	private boolean isTxs(int magic1, int magic2, int magic3) {
		return magic1 == RemotingConstants.TXS_MAGIC_1
				&& magic2 == RemotingConstants.TXS_MAGIC_2
				&& magic3 == RemotingConstants.TXS_MAGIC_3;
	}

	/**
	 * Checks if is cmd.
	 *
	 * @param magic1 the magic1
	 * @param magic2 the magic2
	 * @param magic3 the magic3
	 * @return true, if is cmd
	 */
	private boolean isCmd(int magic1, int magic2, int magic3) {
		return magic1 == RemotingConstants.CMD_MAGIC_1
				&& magic2 == RemotingConstants.CMD_MAGIC_2
				&& magic3 == RemotingConstants.CMD_MAGIC_3;
	}

	
	private boolean isCma(int magic1, int magic2, int magic3){
		return magic1 == RemotingConstants.CMA_MAGIC_1
		&& magic2 == RemotingConstants.CMA_MAGIC_2
		&& magic3 == RemotingConstants.CMA_MAGIC_3;
	}
	
	/**
	 * Checks if is cmg.
	 *
	 * @param magic1 the magic1
	 * @param magic2 the magic2
	 * @param magic3 the magic3
	 * @return true, if is cmg
	 */
	private boolean isCmg(int magic1, int magic2, int magic3) {
		return magic1 == RemotingConstants.CMG_MAGIC_1
				&& magic2 == RemotingConstants.CMG_MAGIC_2
				&& magic3 == RemotingConstants.CMG_MAGIC_3;
	}

	/**
	 * Checks if is cmo.
	 *
	 * @param magic1 the magic1
	 * @param magic2 the magic2
	 * @param magic3 the magic3
	 * @return true, if is cmo
	 */
	private boolean isCmo(int magic1, int magic2, int magic3) {
		return magic1 == RemotingConstants.CMO_MAGIC_1
				&& magic2 == RemotingConstants.CMO_MAGIC_2
				&& magic3 == RemotingConstants.CMO_MAGIC_3;
	}

	
	/**
	 * Invoke jar receive handler.
	 *
	 * @param ctx the ctx
	 */
	private void invokeJarReceiveHandler(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.getPipeline();
		p.addLast("stringDecoder", new StringDecoder());
		p.addLast("delegator", new Delegator(receiveDirectory));
		p.addLast("stringEncoder", new StringEncoder());
		p.addLast("binaryEncoder", new ArchiveEncoder());
		p.remove(this);
	}

	
	/**
	 * Invoke jar send handler.
	 *
	 * @param ctx the ctx
	 */
	private void invokeJarSendHandler(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.getPipeline();
		p.addLast(STREAMER, new ArchiveDecoder(receiveDirectory));
		p.addLast(ACK_RESPONSER, new AckResponser());
		p.addLast(ENCODER, new StringEncoder());
		p.remove(this);
	}

	
	/**
	 * Invoke log files receive handler.
	 *
	 * @param ctx the ctx
	 */
	private void invokeLogFilesReceiveHandler(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.getPipeline();
		p.addLast("stringDecoder", new StringDecoder());
		p.addLast("delegator", new Delegator(receiveDirectory));
		p.addLast("stringEncoder", new StringEncoder());
		p.addLast("logStreamer", new LogFilesEncoder());
		p.remove(this);
	}

	
	/**
	 * Invoke log files send handler.
	 *
	 * @param ctx the ctx
	 */
	private void invokeLogFilesSendHandler(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.getPipeline();
		p.addLast(STREAMER, new LogFilesDecoder(receiveDirectory));
		p.addLast(ACK_RESPONSER, new AckResponser());
		p.addLast(ENCODER, new StringEncoder());
		p.remove(this);
	}

	
	/**
	 * Invoke fire and forget command handler.
	 *
	 * @param ctx the ctx
	 */
	private void invokeFireAndForgetCommandHandler(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.getPipeline();
		p.addLast(DECODER, new ObjectDecoder());
		p.addLast("commandExecutor", new CommandDelegator());
		p.addLast(ENCODER, new StringEncoder());
		p.remove(this);
	}

	/**
	 * Invoke fire and forget command handler.
	 *
	 * @param ctx the ctx
	 */
	private void invokeAsyncFireAndForgetCommandHandler(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.getPipeline();
		p.addLast(DECODER, new ObjectDecoder());
		p.addLast("commandExecutor", new CommandAsyncDelegator());
		p.addLast(ENCODER, new StringEncoder());
		p.remove(this);
	}
	
	/**
	 * Invoke fire and get command handler.
	 *
	 * @param ctx the ctx
	 */
	private void invokeFireAndGetCommandHandler(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.getPipeline();
		p.addLast(DECODER, new StringDecoder());
		p.addLast("commandHandler", new CommandResponser());
		p.addLast(ENCODER, new StringEncoder());
		p.remove(this);
	}

	/**
	 * Invoke fire and get object response command handler.
	 *
	 * @param ctx the ctx
	 */
	private void invokeFireAndGetObjectResponseCommandHandler(
			ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.getPipeline();
		p.addLast(DECODER, new ObjectDecoder());
		p.addLast("commandHandler", new CommandAsObjectResponser());
		p.addLast(ENCODER, new ObjectEncoder());
		p.remove(this);
	}

}

