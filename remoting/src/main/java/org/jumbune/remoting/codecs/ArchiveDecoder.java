package org.jumbune.remoting.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.writable.ArchiveFile;

/**
 * The Class ArchiveDecoder is responsible for decoding the archive files and writing them to streams
 */
public class ArchiveDecoder extends ObjectDecoder {

	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(ArchiveDecoder.class);

	/** The receive directory. */
	private String receiveDirectory;
		
	/** The barrier. */
	private CyclicBarrier barrier;

	/**
	 * Instantiates a new archive decoder.
	 *
	 * @param receiveDirectory the receive directory
	 */
	public ArchiveDecoder(String receiveDirectory) {
		super(10485760, ClassResolvers.cacheDisabled(null));
		this.receiveDirectory = receiveDirectory;
	}
	
	public ArchiveDecoder(int maxObjectSize, String receiveDirectory){
		super(maxObjectSize, ClassResolvers.cacheDisabled(null));
		this.receiveDirectory = receiveDirectory;		
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ArchiveFile out = (ArchiveFile)super.decode(ctx, in);
		// dynamically adding the new ObjectResponseHandler so that new Barrier
		// object can be used.
		CyclicBarrier cyclicBarrier = ctx.channel().attr(RemotingConstants.barrierKey).get();
		if (cyclicBarrier != null && this.barrier==null) {
			this.barrier = cyclicBarrier;
		}
		writeArchiveFile(out, receiveDirectory);
		return out;
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
	private void writeArchiveFile(ArchiveFile archiveFile, String location) throws IOException, ClassNotFoundException, InterruptedException, BrokenBarrierException {
		JarOutputStream jos = null;
		FileOutputStream fos = null;
		File archive = null;
		if(archiveFile==null)
			return;
		try {
			File locationDir = getAndVerifyArchiveDirectoyPath(location, archiveFile);
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
			}
			jos.flush();
			if (barrier != null) {
				barrier.await();
			}			
		} finally {
			if (jos != null) {
				jos.close();
			} else if (fos != null) {
				fos.close();
			}
		}
	}
	
	/**
	 * *
	 * Get directory path of archive file and verify if it is exist or not.
	 *
	 * @param location , archive directory location
	 * @param archiveFile , archieve file to verify
	 * @return file path of archive file in the form of File Object.
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @{@link IOException}
	 */
	private File getAndVerifyArchiveDirectoyPath(String location, ArchiveFile archiveFile) throws IOException {
		LOGGER.debug("Starting to write file:" + archiveFile.getJarName());
		File locationDir = new File(location + File.separator + archiveFile.getDestinationRelativePath());
		if (!locationDir.exists()) {
			locationDir.mkdirs();
		}
		if (!locationDir.isDirectory()) {
			throw new IOException("location must be a directory");
		}
		return locationDir;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	    throws Exception {
	    LOGGER.error("Internal Server Error",cause);
	}	
	
}
