package org.jumbune.remoting.handlers;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.writable.TextFile;
import org.jumbune.remoting.writable.TextFiles;



/**
 * The Class LogFilesEncoder is responsible for reading the streams from the file.
 */
public class LogFilesEncoder extends AbstractDownstreamMashaller {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(LogFilesEncoder.class);	

	/* (non-Javadoc)
	 * @see org.jumbune.remoting.handlers.AbstractDownstreamMashaller#encode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object originalMessage) {
		String file = null;
		ChannelBuffer cb = null;
		if (originalMessage instanceof ChannelBuffer) {
			cb = (ChannelBuffer) originalMessage;
			String strMessage = new String(cb.array());
			file = (String) strMessage;
			String[] splits = file.split(RemotingConstants.PATH_DEMARKER);
			if (splits.length != 2) {
				LOGGER.warn("Escaping action as no file [" + file
						+ "] existed for given name.");
				return cb;
			}
			LOGGER.info("ChannelBuffer " + Arrays.toString(splits));
			return prepareChannelBuffer(splits, cb);
		} else if (originalMessage instanceof String) {
			file = (String) originalMessage;
			if (isEmpty(file)) {
				return originalMessage;
			}
			String[] splits = file.split(RemotingConstants.PATH_DEMARKER);
			if (splits.length != 2) {
				LOGGER.warn("Escaping action as no file [" + file
						+ "] existed for given name.");
				return cb;
			}
			LOGGER.info("Jumbune requested files [source path, destination relative path] " + Arrays.toString(splits));
			return prepareChannelBuffer(splits, cb);
		}
		return originalMessage;
	}

	/**
	 * Checks if is empty.
	 *
	 * @param str the str
	 * @return true, if is empty
	 */
	private boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * Prepare channel buffer.
	 *
	 * @param splits the splits
	 * @param cb the cb
	 * @return the object
	 */
	private Object prepareChannelBuffer(String[] splits,final  ChannelBuffer cb) {
		ChannelBuffer tempChannelBuffer=null;
		if (new File(splits[0]).isDirectory()) {
			try {
				tempChannelBuffer = copiedBuffer(readStreamFromDirectory(splits[0], splits[1]));
				return tempChannelBuffer;
			} catch (IOException e) {
				LOGGER.error("IOException Occured while reading Directory!", e);
			}
		} else {
			try {
				tempChannelBuffer = copiedBuffer(readStreamFromFile(splits[0], splits[1]));
				return tempChannelBuffer;
			} catch (IOException e) {
				LOGGER.error("IOException Occured while reading File!", e);
			}
		}
		return cb;
	}

	/**
	 * Read stream from file.
	 *
	 * @param fileName the file name
	 * @param destinationRelativePath the destination relative path
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private byte[] readStreamFromFile(String fileName,
			String destinationRelativePath) throws IOException {
		TextFile textFile = new TextFile(fileName, destinationRelativePath);
		textFile.setContent(getBytesFromFile(fileName));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bos);
		os.writeObject(textFile);
		byte[] binaryData = bos.toByteArray();
		LOGGER.debug("Sending text file stream for [" + fileName
					+ "] of length [" + binaryData.length + "]");
		os.close();
		// rewriting - first jar stream length then actual jar contents through
		bos = new ByteArrayOutputStream();
		os = new ObjectOutputStream(bos);
		bos.write(toBytes(binaryData.length));
		os.writeObject(textFile);
		os.flush();
		byte[] data = bos.toByteArray();
		bos.close();
		return data;
	}

	/**
	 * Read stream from directory.
	 *
	 * @param directoryName the directory name
	 * @param destinationRelativePath the destination relative path
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private byte[] readStreamFromDirectory(String directoryName,
			String destinationRelativePath) throws IOException {
		TextFiles textFiles = new TextFiles(directoryName,
				destinationRelativePath);
		File dir = new File(directoryName);
		String[] files = dir.list();
		if (files == null) {
			LOGGER.warn("Specified directory does not exist or is not a directory.");
			return null;
		} else {
			String fileName;
			for (int i = 0; i < files.length; i++) {
				fileName = files[i];
				textFiles.addEntry(fileName, getBytesFromFile(directoryName
						+ File.separator + fileName));
			}
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bos);
		os.writeObject(textFiles);
		byte[] binaryData = bos.toByteArray();
		LOGGER.debug("Sending directory stream for [" + directoryName
					+ "] of length [" + binaryData.length + "]");
		os.close();
		// rewriting - first jar stream length then actual jar contents through
		bos = new ByteArrayOutputStream();
		os = new ObjectOutputStream(bos);
		bos.write(toBytes(binaryData.length));
		os.writeObject(textFiles);
		os.flush();
		byte[] data = bos.toByteArray();
		bos.close();
		return data;
	}

	/**
	 * Gets the bytes from file.
	 *
	 * @param fileName the file name
	 * @return the bytes from file
	 */
	private byte[] getBytesFromFile(final String fileName) {
		File f = new File(fileName);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			LOGGER.error("unable to read file", e);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int ch;
		try {
			while ((ch = fis.read()) != -1) {
				bos.write(ch);
			}
		} catch (IOException e) {
			LOGGER.error("unable to read file", e);
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				LOGGER.error("unable to close the file stream", e);
			}
		}
		return bos.toByteArray();
	}

}
