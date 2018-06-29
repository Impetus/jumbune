package org.jumbune.remoting.common.codecs.writable;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The Class TextFile is used for extracting the text file name and setting the contents read from the stream
 */
@SuppressWarnings("serial")
public class TextFile implements Serializable {

	/** The file name. */
	private String fileName;

	/** The content. */
	private byte[] content;

	/** The destination relative path. */
	private String destinationRelativePath;
	
	private static final Logger LOGGER =LogManager.getLogger(TextFile.class);

	/**
	 * Instantiates a new text file.
	 *
	 * @param fileName the file name
	 * @param destinationRelativePath the destination relative path
	 */
	public TextFile(String fileName, String destinationRelativePath) {
		this.fileName = extractFileName(fileName);
		this.destinationRelativePath = destinationRelativePath;
		LOGGER.debug("Sending back file ["+this.fileName+"] to relative destination inside <Jumbune Home>["+this.destinationRelativePath+"]");
	}

	/**
	 * Extract file name.
	 *
	 * @param fileName the file name
	 * @return the string
	 */
	private String extractFileName(final String fileName) {
		String extractedFileName=fileName;
		if (fileName.charAt(fileName.length() - 1) == '/') {
			extractedFileName = fileName.substring(0, fileName.length() - 1);
		}
		if (extractedFileName.charAt(0) == '/') {
			extractedFileName = extractedFileName.substring(1);
		}
		if (File.separatorChar != '/') {
			extractedFileName = extractedFileName.replace('/', File.separatorChar);
		}
		if (extractedFileName.lastIndexOf(File.separator) > -1) {
			return extractedFileName.substring(extractedFileName.lastIndexOf(File.separator) + 1);
		}
		LOGGER.debug("Extracted jar ["+extractedFileName+"]");
		return extractedFileName;
	}

	/**
	 * Gets the file name.
	 *
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * Sets the content.
	 *
	 * @param content the content to set
	 */
	public void setContent(final byte[] content) {
			this.content = content.clone();

	}

	/**
	 * Gets the destination relative path.
	 *
	 * @return the destinationRelativePath
	 */
	public String getDestinationRelativePath() {
		return destinationRelativePath;
	}

	/**
	 * Sets the destination relative path.
	 *
	 * @param destinationRelativePath the destinationRelativePath to set
	 */
	public void setDestinationRelativePath(String destinationRelativePath) {
		this.destinationRelativePath = destinationRelativePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TextFile [fileName=" + fileName + ", content=" + Arrays.toString(content) + "]";
	}

}
