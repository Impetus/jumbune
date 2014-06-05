package org.jumbune.remoting.handlers;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;


/**
 * The Class ObjectEncoder is used to read the stream from the object and encoding the contents to output stream.
 */
public class ObjectEncoder extends AbstractDownstreamMashaller {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(ObjectEncoder.class);

	/* (non-Javadoc)
	 * @see org.jumbune.remoting.handlers.AbstractDownstreamMashaller#encode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object originalMessage) {
		Object object = null;
		try {
			object  = copiedBuffer(readStreamFromObject(originalMessage));
		} catch (IOException e) {
			LOGGER.error("Unable to copy object stream to channel", e);
		}
		return object;
	}


	/**
	 * Read stream from object.
	 *
	 * @param object the object
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private byte[] readStreamFromObject(Object object) throws IOException {
		// calculated length of jar stream
		ByteArrayOutputStream bos = null;
		ObjectOutputStream os = null;
		ChannelBuffer channelBuffer = null;
		if (object instanceof ChannelBuffer) {
			channelBuffer = (ChannelBuffer) object;
			if(channelBuffer.array().length<2){
				return channelBuffer.array();
			}
		}
		byte[] binaryData;
		try {
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			os.writeObject(object);
			binaryData = bos.toByteArray();
			LOGGER.debug("Sending back object stream of length ["
						+ binaryData.length + "]");
		} finally {
			if (os != null) {
				os.close();
			} else if (bos != null) {
				bos.close();
			}
		}
		// rewriting - first jar stream length then actual jar contents through
		// objectoutputstream
		byte[] data;
		try {
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			bos.write(toBytes(binaryData.length));
			os.writeObject(object);
			os.flush();
			data = bos.toByteArray();
		} finally {
			if (os != null) {
				os.close();
			} else if (bos != null) {
				bos.close();
			}
		}
		return data;
	}

	

}
