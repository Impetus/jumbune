package org.jumbune.remoting.handlers;

import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.writable.ArchiveFile;



/**
 * The Class ArchiveEncoder is responsible for reading the stream from the jar and archiving it to the output stream
 */
public class ArchiveEncoder extends AbstractDownstreamMashaller {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(ArchiveEncoder.class);

	/* (non-Javadoc)
	 * @see org.jumbune.remoting.handlers.AbstractDownstreamMashaller#encode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
	 */
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object originalMessage) {
		String file = null;
		ChannelBuffer channelBuffer = null;
		if (originalMessage instanceof ChannelBuffer) {
			channelBuffer = (ChannelBuffer) originalMessage;
			String strMessage = new String(channelBuffer.array());
			file = (String) strMessage;
			String[] splits = file.split(RemotingConstants.PATH_DEMARKER);
			if (splits.length != 2) {
				LOGGER.debug("Escaping action as no file [" + file
						+ "] existed for given name.");
				return originalMessage;
			}
			try {
				return copiedBuffer(readStreamFromJar(splits[0], splits[1]));
			} catch (IOException e) {
				LOGGER.error("IOException Occured while reading Jar!", e);
			}
		} else if (originalMessage instanceof String && (((String) originalMessage).contains(File.separator))) {
			
				file = (String) originalMessage;
				LOGGER.info(file);
				String[] splits = file.split(RemotingConstants.PATH_DEMARKER);
				if (splits.length != 2) {
					LOGGER.debug("Escaping action as no file [" + file
							+ "] existed for given name");
					return null;
				}
				try {
					return copiedBuffer(readStreamFromJar(splits[0], splits[1]));
				} catch (IOException e) {
					LOGGER.error("IOException Occured while reading Jar!", e);
				}
			
		}
		return originalMessage;
	}

	/**
	 * Read stream from jar.
	 *
	 * @param file the file
	 * @param destinationRelativePath the destination relative path
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private byte[] readStreamFromJar(String file, String destinationRelativePath)
			throws IOException {
		ArchiveFile archieve = new ArchiveFile(file, destinationRelativePath);
		JarFile jarFile = new JarFile(new File(file));
		Enumeration<? extends JarEntry> entries = jarFile.entries();
		byte[] entryContent = null;
		BufferedInputStream bis = null;
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (!entry.isDirectory()) {
				String entryName = entry.getName();
				try {
					bis = new BufferedInputStream(jarFile.getInputStream(entry));
					entryContent = new byte[bis.available()];
					bis.read(entryContent);
				} finally {
					if (bis != null) {
						bis.close();
					}
				}
				archieve.addEntry(entryName, entryContent);
			}
		}
		// calculated length of jar stream
		ByteArrayOutputStream bos = null;
		ObjectOutputStream os = null;
		byte[] binaryData = calculateJarLength(file, archieve);
		// rewriting - first jar stream length then actual jar contents through
		// objectoutputstream
		byte[] data;
		try {
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			bos.write(toBytes(binaryData.length));
			os.writeObject(archieve);
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

	/**
	 * @param file
	 * @param archive
	 * @return
	 * @throws IOException
	 */
	private byte[] calculateJarLength(String file, ArchiveFile archive) throws IOException {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream os = null;
		byte[] binaryData;
		try {
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			os.writeObject(archive);
			binaryData = bos.toByteArray();
			LOGGER.debug("Sending binary stream for [" + file
						+ "] of length [" + binaryData.length + "]");
		} finally {
			if (os != null) {
				os.close();
			} else if (bos != null) {
				bos.close();
			}
		}
		return binaryData;
	}

	

}
