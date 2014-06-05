package org.jumbune.remoting.writable;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The Class ArchiveFile for storing the contents into a archive file.
 */
@SuppressWarnings("serial")
public class ArchiveFile implements Serializable {

	/** The destination relative path. */
	private String destinationRelativePath;
	
	/** The jar name. */
	private String jarName;
	
	/** The entries. */
	private Map<String, byte[]> entries;
	
	private static final Logger LOGGER =LogManager.getLogger(ArchiveFile.class);
	/**
	 * Instantiates a new archive file.
	 *
	 * @param jarName the jar name
	 * @param destinationRelativePath the destination relative path
	 */
	public ArchiveFile(String jarName, String destinationRelativePath) {
		this.jarName = extractJarName(jarName);
		this.destinationRelativePath = destinationRelativePath;
		entries = new HashMap<String, byte[]>();
		LOGGER.debug("Sending back archived jar ["+this.jarName+"], Destination path inside <JUMBUNE_HOME> will be - ["+this.destinationRelativePath+"]");
	}

	/**
	 * Extract jar name.
	 *
	 * @param jarFileName the jar file name
	 * @return the string
	 */
	private String extractJarName(final String jarFileName) {
		
		String extractedJarName=jarFileName;
		if (jarFileName.charAt(jarFileName.length() - 1) == '/') {
			extractedJarName = jarFileName.substring(0, jarFileName.length() - 1);
		}
		if (extractedJarName.charAt(0) == '/') {
			extractedJarName = extractedJarName.substring(1);
		}
		if (File.separatorChar != '/') {
			extractedJarName = extractedJarName.replace('/', File.separatorChar);
		}
		if (extractedJarName.lastIndexOf(File.separator) > -1) {
			return extractedJarName.substring(extractedJarName.lastIndexOf(File.separator) + 1);
		}
		LOGGER.debug("Extracted jar ["+extractedJarName+"]");
		return extractedJarName;
	}

	/**
	 * Gets the jar name.
	 *
	 * @return the jarName
	 */
	public String getJarName() {
		return jarName;
	}

	/**
	 * Sets the jar name.
	 *
	 * @param jarName the jarName to set
	 */
	public void setJarName(String jarName) {
		this.jarName = jarName;
	}

	/**
	 * Gets the entries.
	 *
	 * @return the entries
	 */
	public Map<String, byte[]> getEntries() {
		return entries;
	}

	/**
	 * Adds the entry.
	 *
	 * @param entryName the entry name
	 * @param entryContent the entry content
	 */
	public void addEntry(String entryName, byte[] entryContent) {
		this.entries.put(entryName, entryContent);
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
		return "AchieveFile [jarName=" + jarName + ", entries=" + entries + "]";
	}
}
