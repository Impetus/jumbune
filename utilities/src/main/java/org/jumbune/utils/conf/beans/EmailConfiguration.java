package org.jumbune.utils.conf.beans;


public class EmailConfiguration {

	private String senderName;
	private String senderEmailID;
	private String senderPassword;
	private String hostName;
	private Boolean authentication;
	private Integer portNumber;

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderEmailID() {
		return senderEmailID;
	}

	public void setSenderEmailID(String senderEmailID) {
		this.senderEmailID = senderEmailID;
	}

	public String getSenderPassword() {
		return senderPassword;
	}

	public void setSenderPassword(String senderPassword) {

		this.senderPassword = senderPassword;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Boolean getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Boolean authentication) {
		this.authentication = authentication;
	}

	public Integer getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(Integer portNumber) {
		this.portNumber = portNumber;
	}
	
	public String getDecryptedPassword() throws Exception {
		return EncryptionUtil.getPlain(senderPassword);
	}

	@Override
	public String toString() {
		return "EmailConfiguration [senderName=" + senderName + ", senderEmailID=" + senderEmailID
				+ ", senderPassword=******, hostName=" + hostName + ", authentication="
				+ authentication + ", portNumber=" + portNumber + "]";
	}

}
