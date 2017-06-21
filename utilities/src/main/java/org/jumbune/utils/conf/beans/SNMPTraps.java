package org.jumbune.utils.conf.beans;

public class SNMPTraps {

	private String trapOID;
	private String ipAddress;
	private Integer port;

	public String getTrapOID() {
		return trapOID;
	}

	public void setTrapOID(String trapOID) {
		this.trapOID = trapOID;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "SNMPTraps [trapOID=" + trapOID + ", ipAddress=" + ipAddress + ", port=" + port
				+ "]";
	}
}