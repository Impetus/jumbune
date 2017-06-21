package org.jumbune.utils.conf.beans;

public class InfluxDBConf {

	private String host;

	private String port;

	private String username;

	private String password;

	private String database;

	/**
	 * Retention period is the duration after which the old records (records
	 * greater than retention period / days) will be deleted. retentionPeriod is
	 * in days. If 0 then it means infinity
	 */
	private String retentionPeriod = "90";

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getRetentionPeriod() {
		return retentionPeriod;
	}

	/**
	 * Retention period is the duration after which the old records (records
	 * greater than retention period / days) will be deleted. retentionPeriod is
	 * in days. If 0 then it means records (cluster monitoring data ) will never
	 * be deleted.
	 */
	public void setRetentionPeriod(String retentionPeriod) {
		this.retentionPeriod = retentionPeriod;
	}

	public String getDecryptedPassword() throws Exception {
		return EncryptionUtil.getPlain(password);
	}

	@Override
	public String toString() {
		return "InfluxDBConf [host=" + host + ", port=" + port + ", username=" + username
				+ ", password=******, database=" + database + ", retentionPeriod=" + retentionPeriod + "]";
	}
}