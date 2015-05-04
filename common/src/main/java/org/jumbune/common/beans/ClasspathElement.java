package org.jumbune.common.beans;

import java.util.Arrays;

import org.jumbune.utils.JobUtil;



/**
 * This class is the bean for the classpath elements (user supplied and framework supplied) entries from yaml.
 */
public class ClasspathElement {
	
	/** The source. */
	private int source;
	
	/** The folders. */
	private String[] folders;
	
	/** The files. */
	private String[] files;
	
	/** The excludes. */
	private String[] excludes;

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public int getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 *
	 * @param source the new source
	 */
	public void setSource(int source) {
		this.source = source;
	}

	/**
	 * Gets the folders.
	 *
	 * @return the folders
	 */
	public String[] getFolders() {
		return folders;
	}

	/**
	 * Sets the folders.
	 *
	 * @param folders the new folders
	 */
	public void setFolders(String[] folders) {
		this.folders = JobUtil.getAndReplaceHolders(folders);
	}

	/**
	 * <p>
	 * See {@link #setFiles(String[])}
	 * </p>.
	 *
	 * @return Returns the files.
	 */
	public final String[] getFiles() {
		return files;
	}

	/**
	 * <p>
	 * Set the value of <code>files</code>.
	 * </p>
	 * 
	 * @param files
	 *            The resources to set.
	 */
	public final void setFiles(String[] files) {
		this.files = JobUtil.getAndReplaceHolders(files);
	}

	/**
	 * <p>
	 * See {@link #setExcludes(String[])}
	 * </p>.
	 *
	 * @return Returns the excludes.
	 */
	public String[] getExcludes() {
		return excludes;
	}

	/**
	 * <p>
	 * Set the value of <code>excludes</code>.
	 * </p>
	 * 
	 * @param excludes
	 *            The excludes to set.
	 */
	public void setExcludes(String[] excludes) {
		this.excludes = JobUtil.getAndReplaceHolders(excludes);
	}

	/**
	 * This method replaces the JUMBUNE_HOME specified in any path with an absolute JumbuneHome path.
	 *
	 * @param path the path
	 */
	

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClasspathElement [source=" + source + ", folders=" + Arrays.toString(folders) + ", files=" + Arrays.toString(files) + ", excludes="
				+ Arrays.toString(excludes) + "]";
	}
}
