package org.jumbune.common.beans.dsc;

public class DataSourceCompMapperInfo {

	private String fieldSeparator;
	
	private String sourcePath;

	private int[] sourcePrimaryKey;

	private int[] destinationPrimaryKey;
	
	private Integer noOfFieldsInSource;
	
	private Integer noOfFieldsInDestination;
	
	public String getFieldSeparator() {
		return fieldSeparator;
	}

	public void setFieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public int[] getSourcePrimaryKey() {
		return sourcePrimaryKey;
	}

	public void setSourcePrimaryKey(int[] sourcePrimaryKey) {
		this.sourcePrimaryKey = sourcePrimaryKey;
	}

	public int[] getDestinationPrimaryKey() {
		return destinationPrimaryKey;
	}

	public void setDestinationPrimaryKey(int[] destinationPrimaryKey) {
		this.destinationPrimaryKey = destinationPrimaryKey;
	}

	public Integer getNoOfFieldsInSource() {
		return noOfFieldsInSource;
	}

	public void setNoOfFieldsInSource(Integer noOfFieldsInSource) {
		this.noOfFieldsInSource = noOfFieldsInSource;
	}

	public Integer getNoOfFieldsInDestination() {
		return noOfFieldsInDestination;
	}

	public void setNoOfFieldsInDestination(Integer noOfFieldsInDestination) {
		this.noOfFieldsInDestination = noOfFieldsInDestination;
	}

}
