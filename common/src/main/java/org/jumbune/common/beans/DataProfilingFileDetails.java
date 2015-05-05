package org.jumbune.common.beans;

import java.util.Map;


/**
 * The Class DataProfilingFileDetails is a pojo containing all the details related to data profiling.
 */
public class DataProfilingFileDetails {
	
	private String fileName ;
	
	private String profiledOutput ;
	
	private Enable dataProfilingType ;
	
	private Map<String, String> fileCheckSumMap;
	
	private int noOfFiles ;
	
	private int hashCode ;

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setProfiledOutput(String profiledOutput) {
		this.profiledOutput = profiledOutput;
	}

	public String getProfiledOutput() {
		return profiledOutput;
	}

	public void setDataProfilingType(Enable dataProfilingType) {
		this.dataProfilingType = dataProfilingType;
	}

	public Enable getDataProfilingType() {
		return dataProfilingType;
	}

	public void setFileCheckSumMap(Map<String, String> fileCheckSumMap) {
		this.fileCheckSumMap = fileCheckSumMap;
	}

	public Map<String, String> getFileCheckSumMap() {
		return fileCheckSumMap;
	}


	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setNoOfFiles(int noOfFiles) {
		this.noOfFiles = noOfFiles;
	}

	public int getNoOfFiles() {
		return noOfFiles;
	}

	@Override
	public String toString() {
		return "DataProfilingFileDetails [fileName=" + fileName
				+ ", profiledOutput=" + profiledOutput + ", dataProfilingType="
				+ dataProfilingType + ", fileCheckSumMap=" + fileCheckSumMap
				+ ", noOfFiles=" + noOfFiles + ", hashCode=" + hashCode + "]";
	}

	

	
	

}
