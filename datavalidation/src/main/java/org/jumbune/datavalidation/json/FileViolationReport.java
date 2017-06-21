package org.jumbune.datavalidation.json;


/**
 * The Class FileViolationReport.
 */
public class FileViolationReport {

	/** The violated tuplein file. */
	Long violatedTupleinFile;
	
	/** The file name. */
	String fileName;

	/**
	 * Gets the violated tuple in file.
	 *
	 * @return the violated tuple in file
	 */
	public Long getViolatedTupleinFile() {
		return violatedTupleinFile;
	}

	/**
	 * Sets the violated tuple in file.
	 *
	 * @param violatedTupleinFile the new violated tuple in file
	 */
	public void setViolatedTupleinFile(Long violatedTupleinFile) {
		this.violatedTupleinFile = violatedTupleinFile;
	}

	/**
	 * Gets the file name.
	 *
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName the new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileViolationReport [violatedTupleinFile="
				+ violatedTupleinFile + ", fileName=" + fileName + "]";
	}
	
	
	
}
