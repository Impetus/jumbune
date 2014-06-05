package org.jumbune.remoting.writable;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The Class TextFiles is used for extracting the directory name and setting the contents read from the stream
 */
@SuppressWarnings("serial")
public class TextFiles implements Serializable {

	/** The directory name. */
	private String directoryName;

	/** The directory entries. */
	private Map<String, byte[]> directoryEntries;
	
	/** The destination relative path. */
	private String destinationRelativePath;	
	
	private static final Logger LOGGER= LogManager.getLogger(TextFiles.class);

	/**
	 * Instantiates a new text files.
	 *
	 * @param directoryName2 the directory name2
	 * @param destinationRelativePath the destination relative path
	 */
	public TextFiles(String directoryName2, String destinationRelativePath) {
		this.directoryName = extractDirectoryName(directoryName2);
		this.destinationRelativePath = destinationRelativePath;
		directoryEntries = new HashMap<String, byte[]>();
		LOGGER.debug("Sending back text files of directory ["+this.directoryName+"], Destination path inside <JUMBUNE_HOME> will be -["+this.destinationRelativePath+"]");
	}

	/**
	 * Extract directory name.
	 *
	 * @param directoryName the directory name
	 * @return the string
	 */
	private String extractDirectoryName(final String directoryName) {
		String extractedDirName=directoryName;
		if (directoryName.charAt(directoryName.length() - 1) == '/') {
			extractedDirName = directoryName.substring(0, directoryName.length() - 1);
		}
		if (extractedDirName.charAt(0) == '/') {
			extractedDirName = extractedDirName.substring(1);
		}
		if (File.separatorChar != '/') {
			extractedDirName = extractedDirName.replace('/', File.separatorChar);
		}
		if (extractedDirName.lastIndexOf(File.separator) > -1) {
			return extractedDirName.substring(extractedDirName.lastIndexOf(File.separator) + 1);
		}
		LOGGER.debug("Extracted jar ["+extractedDirName+"]");
		return extractedDirName;
	}

	/**
	 * Gets the directory name.
	 *
	 * @return the directoryName
	 */
	public String getDirectoryName() {
		return directoryName;
	}

	/**
	 * Sets the directory name.
	 *
	 * @param directoryName the directoryName to set
	 */
	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	/**
	 * Gets the directory entries.
	 *
	 * @return the directoryEntries
	 */
	public Map<String, byte[]> getDirectoryEntries() {
		return directoryEntries;
	}

	/**
	 * Sets the directory entries.
	 *
	 * @param directoryEntries the directoryEntries to set
	 */
	public void setDirectoryEntries(Map<String, byte[]> directoryEntries) {
		this.directoryEntries = directoryEntries;
	}

	/**
	 * Adds the entry.
	 *
	 * @param entryName the entry name
	 * @param entryContent the entry content
	 */
	public void addEntry(String entryName, byte[] entryContent) {
		this.directoryEntries.put(entryName, entryContent);
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
		return "TextFiles [directoryName=" + directoryName + ", directoryEntries=" + directoryEntries + "]";
	}

}
