package org.jumbune.remoting.common.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.common.RemotingConstants;
import org.jumbune.remoting.common.codecs.writable.TextFile;
import org.jumbune.remoting.common.codecs.writable.TextFiles;

/**
 * The Class LogFilesDecoder is responsible for processing and decoding the log files.
 */
public class LogFilesDecoder extends ObjectDecoder {

	/** The logger. */
	private static final  Logger LOGGER = LogManager.getLogger(LogFilesDecoder.class);
	
	/** The receive directory. */
	private String receiveDirectory;

	/** The barrier. */
	private CyclicBarrier barrier;

	/**
	 * Instantiates a new log files decoder.
	 *
	 * @param receiveDirectory the receive directory
	 */
	public LogFilesDecoder(String receiveDirectory) {
		super(314572800, ClassResolvers.cacheDisabled(null));
		this.receiveDirectory = receiveDirectory;
	}
	
	public LogFilesDecoder(int maxObjectSize, String receiveDirectory){
		super(maxObjectSize, ClassResolvers.cacheDisabled(null));
		this.receiveDirectory = receiveDirectory;		
	}
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		Serializable fileOrFiles = (Serializable) super.decode(ctx, in);
		// dynamically adding the new Barrier object
		CyclicBarrier cyclicBarrier = ctx.channel().attr(RemotingConstants.barrierKey).get();
		if (cyclicBarrier != null) {
			this.barrier = cyclicBarrier;
		}
		writeTextFileOrFilesFromStream(fileOrFiles);
		return fileOrFiles;
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
	private Object writeTextFileOrFilesFromStream(Serializable fileOrFiles) throws IOException, ClassNotFoundException,
			InterruptedException, BrokenBarrierException {
		ObjectInputStream ois = null;
		FileOutputStream fos = null;
		File fileHandle = null;
		try {
			if (fileOrFiles instanceof TextFiles) {
					fileHandle=processTextFiles(fileOrFiles);
			} else if (fileOrFiles instanceof TextFile) {
				TextFile textFile = (TextFile) fileOrFiles;
				String fileName = textFile.getFileName();
				LOGGER.debug("Starting to write file: " + fileName
							+ " at location [" + receiveDirectory
							+ File.separator
							+ textFile.getDestinationRelativePath() + "]");
				File locationDir = new File(receiveDirectory + File.separator
						+ textFile.getDestinationRelativePath());
				verifyAndMakeDirectory(locationDir);
				
				fileHandle = new File(locationDir.getAbsolutePath(), fileName);
				fileHandle.setReadable(true, false);
				fileHandle.setWritable(true, false);
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
			}
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
		LOGGER.debug("Starting to write Directory & files at:"
					+ directoryLocation);
		fileHandle = new File(receiveDirectory + File.separator
				+ textFiles.getDestinationRelativePath(),
				directoryLocation);
		verifyAndMakeDirectory(fileHandle);
		File fileEntry;
		Map<String, byte[]> dirEntries = textFiles
				.getDirectoryEntries();
		int directoryLocationIndex;
		for (Map.Entry<String, byte[]> entry : dirEntries.entrySet()) {
			directoryLocationIndex = entry.getKey().indexOf(directoryLocation);
			String relativeSubEntry = entry.getKey().substring(directoryLocationIndex + directoryLocation.length());
			fileEntry = new File(fileHandle, relativeSubEntry);
			fileEntry.setReadable(true, false);
			fileEntry.setWritable(true, false);
			if(null!=entry.getValue()){
				if (directoryLocationIndex != -1) {
					
					verifyAndMakeDirectory(new File(fileHandle+File.separator+relativeSubEntry.substring(0, relativeSubEntry.lastIndexOf(File.separator))));
				}
				fos = new FileOutputStream(fileEntry);
				fos.write(entry.getValue());
			}else{
				verifyAndMakeDirectory(fileEntry);
			}
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
	private void verifyAndMakeDirectory(File locationDir) throws IOException {
		if (!locationDir.exists()) {
			locationDir.mkdirs();
			locationDir.setReadable(true, false);
			locationDir.setWritable(true, false);
		}
		if (!locationDir.isDirectory()) {
			throw new IOException("location must be a directory");
		}
	}	
}
