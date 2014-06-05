package org.jumbune.remoting.handlers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.writable.TextFile;
import org.jumbune.remoting.writable.TextFiles;



/**
 * The Class LogFilesDecoder is responsible for processing and decoding the log files.
 */
public class LogFilesDecoder extends AbstractUpstreamMarshaller {

	/** The logger. */
	private static final  Logger LOGGER = LogManager.getLogger(LogFilesDecoder.class);
	
	/** The receive directory. */
	private String receiveDirectory;
	
	/** The dynamic buffer. */
	private final ChannelBuffer dynamicBuffer = ChannelBuffers
			.dynamicBuffer(8192);
	
	/** The expected data. */
	private int expectedData;
	
	/** The pushback bytes. */
	private byte[] pushbackBytes;
	
	/** The barrier. */
	private CyclicBarrier barrier;

	/**
	 * Instantiates a new log files decoder.
	 *
	 * @param receiveDirectory the receive directory
	 */
	public LogFilesDecoder(String receiveDirectory) {
		this.receiveDirectory = receiveDirectory;
	}

	/* (non-Javadoc)
	 * @see org.jumbune.remoting.handlers.AbstractUpstreamMarshaller#decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws IOException, ClassNotFoundException, InterruptedException, BrokenBarrierException {
		if (!(msg instanceof ChannelBuffer)) {
			return msg;
		}
		byte[] buffer = ((ChannelBuffer) msg).array();
		ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
		// dynamically adding the new Barrier object
		CyclicBarrier cyclicBarrier = (CyclicBarrier) channel.getAttachment();
		if (cyclicBarrier != null) {
			this.barrier = cyclicBarrier;
		}
		return writeTextFileOrFilesFromStream(bis);
	}

	/**
	 * Write text file or files from stream.
	 *
	 * @param is the is
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InterruptedException the interrupted exception
	 * @throws BrokenBarrierException the broken barrier exception
	 */
	private Object writeTextFileOrFilesFromStream(InputStream is) throws IOException, ClassNotFoundException,
			InterruptedException, BrokenBarrierException {
		dynamicBuffer.writeBytes(is, is.available());
		is.close();
		try{
		checkForExpectedData();
		}catch(IllegalArgumentException e){
			return null;
		}
		
		ByteArrayInputStream bis = null;
		PushbackInputStream pis = null;
		ObjectInputStream ois = null;
		FileOutputStream fos = null;
		File fileHandle = null;
		try {
			bis = new ByteArrayInputStream(dynamicBuffer.array(), RemotingConstants.EIGHT,
					dynamicBuffer.readableBytes());
			pis = new PushbackInputStream(bis, RemotingConstants.FOUR);
			// pushing back unexpected occurances of bytes
			pis.unread(pushbackBytes, 0, RemotingConstants.FOUR);
			// now we have properly ordered bytes to create object, let's try
			// now
			ois = new ObjectInputStream(pis);
			Object object = ois.readObject();
			if (object instanceof TextFiles) {
					fileHandle=processTextFiles(object);
			} else if (object instanceof TextFile) {
				TextFile textFile = (TextFile) object;
				String fileName = textFile.getFileName();
				LOGGER.debug("Expected Text File streams of length ["
							+ expectedData
							+ "] arrived, starting to write file: " + fileName
							+ " at location [" + receiveDirectory
							+ File.separator
							+ textFile.getDestinationRelativePath() + "]");
				File locationDir = new File(receiveDirectory + File.separator
						+ textFile.getDestinationRelativePath());
				varifyAndMakeDirectory(locationDir);
				
				fileHandle = new File(locationDir.getAbsolutePath(), fileName);
				fos = new FileOutputStream(fileHandle);
				fos.write(textFile.getContent());
				fos.flush();
				// Guilt: bad way to differentiate client from server
				if (barrier != null) {
					barrier.await();
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if(fos!=null){
				fos.close();
			}
			if (ois != null) {
				ois.close();
			} else if (pis != null) {
				pis.close();
			} else if (bis != null) {
				bis.close();
			}
			clearAndClose();
		}
		return fileHandle;
	}
	
	/**
	 * Process text files.
	 *
	 * @param object the object
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 * @throws BrokenBarrierException the broken barrier exception
	 */
	private File processTextFiles(Object object) throws IOException,
			InterruptedException, BrokenBarrierException {
		FileOutputStream fos = null;
		File fileHandle;
		TextFiles textFiles = (TextFiles) object;
		try{
		
		String directoryLocation = textFiles.getDirectoryName();
		LOGGER.debug("Expected Directory streams of length ["
					+ expectedData
					+ "] arrived, starting to write Directory & files at:"
					+ directoryLocation);
		fileHandle = new File(receiveDirectory + File.separator
				+ textFiles.getDestinationRelativePath(),
				directoryLocation);
		varifyAndMakeDirectory(fileHandle);
		File fileEntry;
		Map<String, byte[]> dirEntries = textFiles
				.getDirectoryEntries();
		for (Map.Entry<String, byte[]> entry : dirEntries.entrySet()) {
			fileEntry = new File(fileHandle, entry.getKey());
			fos = new FileOutputStream(fileEntry);
			fos.write(entry.getValue());
			fos.flush();
			fos.close();
		}
		
		if (barrier != null) {
			barrier.await();
		}
		}finally{
			if(fos!=null){
				fos.close();
			}
			
		}
		return fileHandle;
	}
	
	/**
	 * *
	 * make directory from filepath if it is not exist and if it is a file.
	 *
	 * @param locationDir the location dir
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void varifyAndMakeDirectory(File locationDir) throws IOException {
		if (!locationDir.exists()) {
			locationDir.mkdirs();
		}
		if (!locationDir.isDirectory()) {
			throw new IOException("location must be a directory");
		}
	}
	
	/**
	 * Check for expected data.
	 */
	private void checkForExpectedData() {
		if (dynamicBuffer.readableBytes() < RemotingConstants.EIGHT) {
			throw new IllegalArgumentException(); 
		}
		if (expectedData == 0) {
			pushbackBytes = new byte[RemotingConstants.FOUR];
			byte[] dataLengthByteArray = new byte[RemotingConstants.FOUR];
			dynamicBuffer.readBytes(pushbackBytes);
			dynamicBuffer.readBytes(dataLengthByteArray);
			expectedData = byteArrayToInt(dataLengthByteArray);
			LOGGER.debug("Expected binary data:" + expectedData);
		}
		if (expectedData > 0
				&& dynamicBuffer.readableBytes() < (expectedData - pushbackBytes.length)) {
			// ignoring the stream as it's not completed yet!
			throw new IllegalArgumentException(); 
		}
		
	}

	/**
	 * Clear and close.
	 */
	private void clearAndClose() {
		dynamicBuffer.clear();
		expectedData = 0;
	}
}
