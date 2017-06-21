package org.jumbune.datavalidation.json;

import java.util.List;


/**
 * The Class JsonViolationReport.
 */
public class JsonViolationReport {
	
	/** The total violation. */
	Long totalViolation;

	/** The total key violation. */
	Long totalKeyViolation;
	
	/** The file violation report. */
	List <FileViolationReport> fileViolationReport;


	/**
	 * Gets the total key violation.
	 *
	 * @return the total key violation
	 */
	public Long getTotalKeyViolation() {
		return totalKeyViolation;
	}

	/**
	 * Sets the total key violation.
	 *
	 * @param totalKeyViolation the new total key violation
	 */
	public void setTotalKeyViolation(Long totalKeyViolation) {
		this.totalKeyViolation = totalKeyViolation;
	}

	/**
	 * Gets the total violation.
	 *
	 * @return the total violation
	 */
	public Long getTotalViolation() {
		return totalViolation;
	}

	/**
	 * Sets the total violation.
	 *
	 * @param totalViolation the new total violation
	 */
	public void setTotalViolation(Long totalViolation) {
		this.totalViolation = totalViolation;
	}

	/**
	 * Gets the file violation report.
	 *
	 * @return the file violation report
	 */
	public List<FileViolationReport> getFileViolationReport() {
		return fileViolationReport;
	}

	/**
	 * Sets the file violation report.
	 *
	 * @param fileViolationReport the new file violation report
	 */
	public void setFileViolationReport(List<FileViolationReport> fileViolationReport) {
		this.fileViolationReport = fileViolationReport;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JsonViolationReport [totalViolation=" + totalViolation
				+ ", totalKeyViolation=" + totalKeyViolation
				+ ", fileViolationReport=" + fileViolationReport + "]";
	}
	
}
