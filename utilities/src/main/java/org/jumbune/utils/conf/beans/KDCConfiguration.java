package org.jumbune.utils.conf.beans;

import java.util.List;

public class KDCConfiguration {
	
	private String kdcType;
	private String activeDirSuffix;
	private String serverHost;
	private String lPort;
	private String securityRealm;
	private String activeAccPrefix;
	private List<String> encryptionTypes;
	
	public String getKdcType() {
		return kdcType;
	}

	public void setKdcType(String kdcType) {
		this.kdcType = kdcType;
	}

	public String getActiveDirSuffix() {
		return activeDirSuffix;
	}

	public void setActiveDirSuffix(String activeDirSuffix) {
		this.activeDirSuffix = activeDirSuffix;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public String getlPort() {
		return lPort;
	}

	public void setlPort(String lPort) {
		this.lPort = lPort;
	}

	public String getSecurityRealm() {
		return securityRealm;
	}

	public void setSecurityRealm(String securityRealm) {
		this.securityRealm = securityRealm;
	}

	public String getActiveAccPrefix() {
		return activeAccPrefix;
	}

	public void setActiveAccPrefix(String activeAccPrefix) {
		this.activeAccPrefix = activeAccPrefix;
	}

	public List<String> getEncryptionTypes() {
		return encryptionTypes;
	}

	public void setEncryptionTypes(List<String> encryptionTypes) {
		this.encryptionTypes = encryptionTypes;
	}

	@Override
	public String toString() {
		return "KDCConfiguration [kdcType=" + kdcType + ", activeDirSuffix=" + activeDirSuffix + ", serverHost="
				+ serverHost + ", lPort=" + lPort + ", securityRealm=" + securityRealm + ", activeAccPrefix="
				+ activeAccPrefix + ", encryptionTypes=" + encryptionTypes + "]";
	}

}
