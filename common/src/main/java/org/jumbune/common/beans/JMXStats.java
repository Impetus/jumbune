package org.jumbune.common.beans;

public class JMXStats {

	private String host;
	private int port;

	public JMXStats(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	protected Long getAvg(Long past, Long present) {

		if (present == null && past != null && past > 0)
			return past;
		if (past == null || past == 0)
			return present;
		else if (present > 0 && past > 0)
			return (past + present) / 2;
		else
			return past;
	}

	protected Integer getAvg(Integer past, Integer present) {
		if (present == null && past != null && past > 0)
			return past;
		if (past == null || past == 0)
			return present;
		else if (present > 0 && past > 0)
			return (past + present) / 2;
		else
			return past;
	}

	protected Double getAvg(Double past, Double present) {

		if (present == null && past != null && past > 0)
			return past;
		if (past == null || past == 0)
			return present;
		else if (present > 0 && past > 0)
			return (past + present) / 2;
		else
			return past;
	}

}
