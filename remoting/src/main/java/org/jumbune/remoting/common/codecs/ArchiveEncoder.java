package org.jumbune.remoting.common.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.codecs.writable.ArchiveFile;

/**
 * The Class ArchiveEncoder is responsible for reading the stream from the jar and archiving it to the output stream
 */
public class ArchiveEncoder extends ObjectEncoder {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(ArchiveEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {		
		String file = null;
		ByteBuf channelBuffer = null;
		if (msg instanceof ByteBuf) {
			channelBuffer = (ByteBuf) msg;
			String strMessage = new String(channelBuffer.array());
			file = (String) strMessage;
			String[] splits = file.split(RemotingConstants.PATH_DEMARKER);
			if (splits.length != 2) {
				LOGGER.debug("Escaping action as no file [" + file
						+ "] existed for given name.");
				out = (ByteBuf)msg;
			}
			try {
				ArchiveFile archive = readStreamFromJar(splits[0], splits[1]);
				super.encode(ctx, archive, out);
			} catch (IOException e) {
				LOGGER.error("IOException Occured while reading Jar!", e);
			}
		} else if (msg instanceof String && (((String) msg).contains(File.separator))) {
			
				file = (String) msg;
				LOGGER.debug(file);
				String[] splits = file.split(RemotingConstants.PATH_DEMARKER);
				if (splits.length != 2) {
					LOGGER.debug("Escaping action as no file [" + file
							+ "] existed for given name");
					out = null;
				}
				try {
					ArchiveFile archive = readStreamFromJar(splits[0], splits[1]);
					super.encode(ctx, archive, out);
				} catch (IOException e) {
					LOGGER.error("IOException Occured while reading Jar!", e);
				}			
		}
	}
	
	/**
	 * Read stream from jar.
	 *
	 * @param file the file
	 * @param destinationRelativePath the destination relative path
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private ArchiveFile readStreamFromJar(String file, String destinationRelativePath)
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
		jarFile.close();
		return archieve;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception {
	    LOGGER.error("Internal Server Error",cause);
	}	
}
