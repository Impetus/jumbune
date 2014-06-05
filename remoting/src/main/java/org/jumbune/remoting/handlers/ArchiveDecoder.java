package org.jumbune.remoting.handlers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.writable.ArchiveFile;



/**
 * The Class ArchiveDecoder is responsible for decoding the archive files and writing them to streams
 */
public class ArchiveDecoder extends AbstractUpstreamMarshaller {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(ArchiveDecoder.class);

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
	 * Instantiates a new archive decoder.
	 *
	 * @param receiveDirectory the receive directory
	 */
	public ArchiveDecoder(String receiveDirectory) {
		this.receiveDirectory = receiveDirectory;
	}

	/* (non-Javadoc)
	 * @see org.jumbune.remoting.handlers.AbstractUpstreamMarshaller#decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws IOException, ClassNotFoundException, InterruptedException, BrokenBarrierException  {
		if (!(msg instanceof ChannelBuffer)) {
			return msg;
		}
		byte[] buffer = ((ChannelBuffer) msg).array();
		ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
		// dynamically adding the new ObjectResponseHandler so that new Barrier
		// object can be used.
		CyclicBarrier cyclicBarrier = (CyclicBarrier) channel.getAttachment();
		if (cyclicBarrier != null) {
			this.barrier = cyclicBarrier;
		}
		return writeJarFromStream(bis, receiveDirectory);
	}

	
	/**
	 * Write jar from stream.
	 *
	 * @param is the is
	 * @param location the location
	 * @return the object
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InterruptedException the interrupted exception
	 * @throws BrokenBarrierException the broken barrier exception
	 */
	private Object writeJarFromStream(InputStream is, String location)
			throws IOException, ClassNotFoundException, InterruptedException,
			BrokenBarrierException {
		dynamicBuffer.writeBytes(is, is.available());
		is.close();
		if (dynamicBuffer.readableBytes() < RemotingConstants.EIGHT) {
			return null;
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
			return null;
		}
		return getArchiveFile(location);
		
	}

	/**
	 * Gets the archive file.
	 *
	 * @param location the location
	 * @return the archive file
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InterruptedException the interrupted exception
	 * @throws BrokenBarrierException the broken barrier exception
	 */
	private Object getArchiveFile(String location) throws IOException, ClassNotFoundException, InterruptedException, BrokenBarrierException {
		ArchiveFile archiveFile = null;
		ByteArrayInputStream bis = null;
		PushbackInputStream pis = null;
		ObjectInputStream ois = null;
		JarOutputStream jos = null;
		FileOutputStream fos = null;
		File archive = null;
		try {
			bis = new ByteArrayInputStream(dynamicBuffer.array(),RemotingConstants.EIGHT,
					dynamicBuffer.readableBytes());
			pis = new PushbackInputStream(bis, RemotingConstants.FOUR);
			// pushing back unexpected occurances of bytes
			pis.unread(pushbackBytes, 0, RemotingConstants.FOUR);
			// now we have properly ordered bytes to create object, let's try
			// now
			ois = new ObjectInputStream(pis);
			archiveFile = (ArchiveFile) ois.readObject();
			File locationDir = getAndVarifyArchiveDirectoyPath(location, archiveFile);
			archive = new File(locationDir.getAbsolutePath(), archiveFile.getJarName());
			fos = new FileOutputStream(archive);
			jos = new JarOutputStream(fos);
			Map<String, byte[]> archieveEntries = archiveFile.getEntries();
			String entryName;
			byte[] entryContent;
			JarEntry jarEntry;
			for (Map.Entry<String, byte[]> entry : archieveEntries.entrySet()) {
				entryName = entry.getKey();
				entryContent = entry.getValue();
				jarEntry = new JarEntry(entryName);
				jos.putNextEntry(jarEntry);
				jos.write(entryContent);
				// Guilt: bad way to differentiate client from server
				if (barrier != null) {
					barrier.await();
				}
			}
		} finally {
			if (jos != null) {
				jos.close();
			} else if (fos != null) {
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
		return archive;
	}

	/**
	 * *
	 * Get directory path of archive file and verify if it is exist or not.
	 *
	 * @param location , archive directory location
	 * @param archiveFile , archieve file to varify
	 * @return file path of archive file in the form of File Object.
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @{@link IOException}
	 */
	private File getAndVarifyArchiveDirectoyPath(String location, ArchiveFile archiveFile) throws IOException {
		LOGGER.debug("Expected Binary streams of length [" + expectedData + "] arrived, Starting to write file:" + archiveFile.getJarName());
		File locationDir = new File(location + File.separator + archiveFile.getDestinationRelativePath());
		if (!locationDir.exists()) {
			locationDir.mkdirs();
		}
		if (!locationDir.isDirectory()) {
			throw new IOException("location must be a directory");
		}
		return locationDir;
	}

	/**
	 * Clear and close.
	 */
	private void clearAndClose() {
		dynamicBuffer.clear();
		expectedData = 0;
	}

	/**
	 * Read bytes from file.
	 *
	 * @param f the f
	 * @return the byte[]
	 */
	public byte[] readBytesFromFile(File f) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e1) {
			LOGGER.error("Unable to read file", e1);
		}
		byte[] buffer = null;
		try {
			buffer = new byte[fis.available()];
			fis.read(buffer);
		} catch (IOException e1) {
			LOGGER.error("Unable to read file", e1);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOGGER.error("Unable to close the stream", e);
				}
			}
		}
		return buffer;
	}
}
