package org.jumbune.common.beans;

import java.util.List;

/**
 * The Class DataProfilingJson is a bean to dump all the data profiling details.
 */
public class DataProfilingJson {
	
	private String folderName ;
	
	
	private List<DataProfilingFileDetails> dataProfilingFileDetails ;

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getFolderName() {
		return folderName;
	}

	
	public void setDataProfilingFileDetails(List<DataProfilingFileDetails> dataProfilingFileDetails) {
		this.dataProfilingFileDetails = dataProfilingFileDetails;
	}

	public List<DataProfilingFileDetails> getDataProfilingFileDetails() {
		return dataProfilingFileDetails;
	}

	@Override
	public String toString() {
		return "DataProfilingJson [folderName=" + folderName
				+ ", dataProfilingFileDetails=" + dataProfilingFileDetails
				+ "]";
	}

	
}	
