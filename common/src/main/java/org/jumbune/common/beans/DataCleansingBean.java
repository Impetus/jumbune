package org.jumbune.common.beans;

//Bean class for persisting DLC and Clean Data Root Location

public class DataCleansingBean {

	/** The dlc root. */
	private String dlcRootLocation;
	
	/** The clean data root. */
	private String cleanDataRootLocation;

	public String getDlcRootLocation() {
		return dlcRootLocation;
	}

	public void setDlcRootLocation(String dlcRootLocation) {
		this.dlcRootLocation = dlcRootLocation;
	}

	public String getCleanDataRootLocation() {
		return cleanDataRootLocation;
	}

	public void setCleanDataRootLocation(String cleanDataRootLocation) {
		this.cleanDataRootLocation = cleanDataRootLocation;
	}

	@Override
	public String toString() {
		return "DataCleansingBean [dlcRootLocation=" + dlcRootLocation
				+ ", cleanDataRootLocation=" + cleanDataRootLocation + "]";
	}
	
	
}
