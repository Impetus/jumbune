package org.jumbune.remoting.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.codecs.ArchiveDecoder;
import org.jumbune.remoting.codecs.ArchiveEncoder;
import org.jumbune.remoting.codecs.LogFilesDecoder;
import org.jumbune.remoting.codecs.LogFilesEncoder;
import org.jumbune.remoting.common.RemotingConstants;

/**
 * The Class JumbuneAgentDecoder  
 */
public class JumbuneAgentDecoder extends ByteToMessageDecoder {

	/** The Constant STREAMER. */
	private static final String STREAMER = "streamer";
	
	/** The Constant ACK_RESPONSER. */
	private static final String ACK_RESPONSER = "ackResponser";
	
	/** The Constant ENCODER. */
	private static final String ENCODER = "encoder";
	
	private static final String DECODER = "decoder";
	
	/** The receive directory. */
	private String receiveDirectory;
	
	public static final Logger LOGGER = LogManager.getLogger(JumbuneAgentDecoder.class);

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

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (in.readableBytes() < RemotingConstants.THREE) {
			return;
		}
		final int magic1 = in.getUnsignedByte(in.readerIndex());
		final int magic2 = in.getUnsignedByte(in.readerIndex()+1);
		final int magic3 = in.getUnsignedByte(in.readerIndex()+2);
		if (isJar(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(in);
			invokeJarReceiveHandler(ctx);
		} else if (isJas(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(in);
			invokeJarSendHandler(ctx);
		} else if (isTxr(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(in);
			invokeLogFilesReceiveHandler(ctx);
		} else if (isTxs(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(in);
			invokeLogFilesSendHandler(ctx);
		} else if (isCmd(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(in);
			invokeFireAndForgetCommandHandler(ctx);
		} else if(isCma(magic1, magic2, magic3)){
			throwAwayReadUnsignedBytes(in);
			invokeAsyncFireAndForgetCommandHandler(ctx);
		}else if (isCmg(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(in);
			invokeFireAndGetCommandHandler(ctx);
		} else if (isCmo(magic1, magic2, magic3)) {
			throwAwayReadUnsignedBytes(in);
			invokeFireAndGetObjectResponseCommandHandler(ctx);
		} else {
	        // Unknown protocol; discard everything and close the connection.
	        in.clear();
	        ctx.close();
		}
	}
	
/*	*//**
	 * Throw away read unsigned bytes.
	 *
	 * @param buffer the buffer
	 */
	private void throwAwayReadUnsignedBytes(ByteBuf buffer) {
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
		ChannelPipeline p = ctx.pipeline();

        // Enable stream compression (you can remove these two if unnecessary)
        p.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));

		
		p.addLast("stringDecoder", new StringDecoder());
		p.addLast("delegator", new Delegator(receiveDirectory));
//		p.addLast("stringEncoder", new StringEncoder());
		p.addLast("binaryEncoder", new ArchiveEncoder());
		p.remove(this);
	}

	
	/**
	 * Invoke jar send handler.
	 *
	 * @param ctx the ctx
	 */
	private void invokeJarSendHandler(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.pipeline();
/*        // Enable stream compression (you can remove these two if unnecessary)
        p.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
*/		
		p.addLast(STREAMER, new ArchiveDecoder(10485760, receiveDirectory));
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
		ChannelPipeline p = ctx.pipeline();
		
/*        // Enable stream compression (you can remove these two if unnecessary)
        p.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        p.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
*/
        // Enable stream compression (you can remove these two if unnecessary)
        p.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
//        p.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
		
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
		ChannelPipeline p = ctx.pipeline();
		
/*        // Enable stream compression (you can remove these two if unnecessary)
        p.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
*/		
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
		ChannelPipeline p = ctx.pipeline();
		p.addLast(DECODER, new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
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
		ChannelPipeline p = ctx.pipeline();
		p.addLast(DECODER, new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
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
		ChannelPipeline p = ctx.pipeline();
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
		ChannelPipeline p = ctx.pipeline();
		p.addLast(DECODER, new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		p.addLast("commandHandler", new CommandAsObjectResponser());
		p.addLast(ENCODER, new ObjectEncoder());
		p.remove(this);
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        LOGGER.error(cause);
        ctx.close();
    }    

}