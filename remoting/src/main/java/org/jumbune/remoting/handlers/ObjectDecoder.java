package org.jumbune.remoting.handlers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PushbackInputStream;
import java.util.concurrent.BrokenBarrierException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jumbune.remoting.common.RemotingConstants;




/**
 * The Class ObjectDecoder is responsible for creating the object stream.
 */
public class ObjectDecoder extends AbstractUpstreamMarshaller {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(ObjectDecoder.class);
	
	/** The local buff. */
	private ThreadLocal<ChannelBuffer> localBuff = new ThreadLocal<ChannelBuffer>() {
		protected ChannelBuffer initialValue() {
			return ChannelBuffers.dynamicBuffer(RemotingConstants.EIGHT_ONE_NINE_TWO);
		}
	};

	/** The flag. */
	private ThreadLocal<Boolean> flag = new ThreadLocal<Boolean>() {
		protected Boolean initialValue() {
			return false;
		}
	};

	/** The local expected data. */
	private ThreadLocal<Integer> localExpectedData = new ThreadLocal<Integer>() {
		protected Integer initialValue() {
			return 0;
		}
	};
	
	/** The pushback bytes. */
	private byte[] pushbackBytes;

	/**
	 * Instantiates a new object decoder.
	 */
	public ObjectDecoder() {
	}

	/* (non-Javadoc)
	 * @see org.jumbune.remoting.handlers.AbstractUpstreamMarshaller#decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws IOException, ClassNotFoundException, InterruptedException, BrokenBarrierException  {
		if (!(msg instanceof ChannelBuffer)) {
			return msg;
		}
		byte[] buffer = ((ChannelBuffer) msg).array();
		ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
		// dynamically adding the new ObjectResponseHandler so that new Barrier
		// object can be used.
		ChannelHandler attachedHandler = (ChannelHandler) channel.getAttachment();
		if (attachedHandler != null) {
			channel.getPipeline().removeLast();
			channel.getPipeline().addLast(attachedHandler.toString(), attachedHandler);
		}
		return createObjectStream(bis);
	}

	/**
	 * Creates the object stream.
	 *
	 * @param is the is
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InterruptedException the interrupted exception
	 * @throws BrokenBarrierException the broken barrier exception
	 */
	private Object createObjectStream(InputStream is) throws IOException, ClassNotFoundException, InterruptedException, BrokenBarrierException {

		localBuff.get().writeBytes(is, is.available());
		is.close();
		if (localBuff.get().readableBytes() < RemotingConstants.EIGHT) {
			flag.set(false);
			return null;
		}
		if (localExpectedData.get().intValue() == 0) {
			pushbackBytes = new byte[RemotingConstants.FOUR];
			byte[] dataLengthByteArray = new byte[RemotingConstants.FOUR];
			localBuff.get().readBytes(pushbackBytes);
			localBuff.get().readBytes(dataLengthByteArray);
			localExpectedData.set(byteArrayToInt(dataLengthByteArray));
			LOGGER.debug("Expected binary data:" + localExpectedData.get());
		}
		
		if (localExpectedData.get().intValue() > 0 && localBuff.get().readableBytes() < (localExpectedData.get().intValue() - pushbackBytes.length)) {
			// ignoring the stream as it's not completed yet!
			flag.set(false);
			return null;
		}
		ByteArrayInputStream bis = null;
		PushbackInputStream pis = null;
		ObjectInputStream ois = null;
		
		Object object = null;
		try {
			bis = new ByteArrayInputStream(localBuff.get().array(), RemotingConstants.EIGHT, localBuff.get().readableBytes());
			pis = new PushbackInputStream(bis, RemotingConstants.FOUR);
			// pushing back unexpected occurances of bytes
			pis.unread(pushbackBytes, 0, RemotingConstants.FOUR);
			// now we have properly ordered bytes to create object, let's try
			// now
			ois = new ObjectInputStream(pis);
			LOGGER.debug("Expected object streams of length [" + localExpectedData.get().intValue() + "] arrived, creating Object");
			flag.set(true);
			object = ois.readObject();
		}finally {
			if (ois != null) {
				ois.close();
			} else if (pis != null) {
				pis.close();
			} else if (bis != null) {
				bis.close();
			}
			boolean canClearAndClose = flag.get();
			if (canClearAndClose) {
				clearAndClose();
			}
		}
		return object;
	}

	/**
	 * Clear and close.
	 */
	private void clearAndClose() {

		localBuff.get().clear();
		localBuff.remove();
		localExpectedData.set(0);
		localExpectedData.remove();
		flag.remove();
	}
}