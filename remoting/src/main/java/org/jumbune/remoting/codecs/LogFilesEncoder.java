package org.jumbune.remoting.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.writable.TextFile;
import org.jumbune.remoting.writable.TextFiles;

/**
 * The Class LogFilesEncoder is responsible for reading the streams from the file.
 */
public class LogFilesEncoder extends ObjectEncoder {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(LogFilesEncoder.class);	

	
	/* (non-Javadoc)
	 * @see io.netty.handler.codec.serialization.ObjectEncoder#encode(io.netty.channel.ChannelHandlerContext, java.io.Serializable, io.netty.buffer.ByteBuf)
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, Serializable originalMessage, ByteBuf out) throws Exception {
		String file = (String)originalMessage;
			String[] splits = file.split(RemotingConstants.PATH_DEMARKER);
			if (splits.length != 2) {
				LOGGER.warn("Escaping action as no file [" + file
						+ "] existed for given name.");
				return;
			}
			LOGGER.info("ChannelBuffer " + Arrays.toString(splits));
			Serializable fileOrfiles = prepareTextFileORFiles(splits, out);
			super.encode(ctx, fileOrfiles, out);
	}

	/**
	 * Prepare channel buffer.
	 *
	 * @param splits the splits
	 * @param cb the cb
	 * @return the object
	 */
	private Serializable prepareTextFileORFiles(String[] splits, ByteBuf out) {
		if (new File(splits[0]).isDirectory()) {
			try {
				return createTextFilesFromDirectory(splits[0], splits[1]);
			} catch (IOException e) {
				LOGGER.error("IOException Occured while reading Directory!", e);
			}
		} else {
			try {
				return createTextFileFromFile(splits[0], splits[1]);
			} catch (IOException e) {
				LOGGER.error("IOException Occured while reading File!", e);
			}
		}
		LOGGER.warn("Unexpected reachable code, returning null");
		return null;
	}

	/**
	 * Read stream from file.
	 *
	 * @param fileName the file name
	 * @param destinationRelativePath the destination relative path
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private TextFile createTextFileFromFile(String fileName,
			String destinationRelativePath) throws IOException {
		TextFile textFile = new TextFile(fileName, destinationRelativePath);
		textFile.setContent(getBytesFromFile(fileName));
		LOGGER.debug("Sending text file stream for [" + fileName+ "]");
		return textFile;
	}

	/**
	 * Read stream from directory.
	 *
	 * @param directoryName the directory name
	 * @param destinationRelativePath the destination relative path
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private TextFiles createTextFilesFromDirectory(String directoryName,
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
		LOGGER.debug("Sending directory stream for [" + directoryName+ "]");		
		return textFiles;
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
