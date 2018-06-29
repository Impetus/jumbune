package org.jumbune.common.beans.dsc;

import java.util.List;

public class DataSourceCompValidationInfo {

	private List<DataSourceCompValidation> validationsList;
	
	private String recordSeparator;

	private String fieldSeparator;

	/**
	 * Lets suppose fields 1, 3 and 4 makes primary key (composite key), then
	 * the sourcePrimaryKey is "1,3,4". If the primary key is only field no. 3
	 * then sourcePrimaryKey is "3"
	 */
	private String sourcePrimaryKey;

	/**
	 * Lets suppose fields 4 and 5 makes primary key (composite key), then the
	 * destinationPrimaryKey is "4,5". If the primary key is only field no. 4
	 * then destinationPrimaryKey is "4"
	 */
	private String destinationPrimaryKey;

	/**
	 * source directory or file path in hdfs
	 */
	private String sourcePath;

	/**
	 * destination directory or file path in hdfs
	 */
	private String destinationPath;

	/**
	 * To be filled from server side, will be used in job executor
	 */
	private String slaveFileLoc;

	/**
	 * To be filled from server side, will be used in job executor
	 */
	private String jobName;

	public List<DataSourceCompValidation> getValidationsList() {
		return validationsList;
	}

	public void setValidationsList(List<DataSourceCompValidation> validationsList) {
		this.validationsList = validationsList;
	}

	public String getRecordSeparator() {
		return recordSeparator;
	}

	public void setRecordSeparator(String recordSeparator) {
		this.recordSeparator = recordSeparator;
	}

	public String getFieldSeparator() {
		return fieldSeparator;
	}

	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	public String getSourcePrimaryKey() {
		return sourcePrimaryKey;
	}

	public void setSourcePrimaryKey(String sourcePrimaryKey) {
		this.sourcePrimaryKey = sourcePrimaryKey;
	}

	public String getDestinationPrimaryKey() {
		return destinationPrimaryKey;
	}

	public void setDestinationPrimaryKey(String destinationPrimaryKey) {
		this.destinationPrimaryKey = destinationPrimaryKey;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	/**
	 * @return the slaveFileLoc
	 */
	public String getSlaveFileLoc() {
		return slaveFileLoc;
	}

	/**
	 * @param slaveFileLoc
	 *            the slaveFileLoc to set
	 */
	public void setSlaveFileLoc(String slaveFileLoc) {
		this.slaveFileLoc = slaveFileLoc;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

}
