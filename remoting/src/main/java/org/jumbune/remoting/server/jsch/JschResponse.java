package org.jumbune.remoting.server.jsch;

public class JschResponse {

	private int channelId;
	private Object response;
	private int channelExitStatus;

	public int getChannelId() {
		return channelId;
	}
	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}
	public Object getResponse() {
		return response;
	}
	public void setResponse(Object response) {
		this.response = response;
	}
	public int getChannelExitStatus() {
		return channelExitStatus;
	}
	public void setChannelExitStatus(int channelExitStatus) {
		this.channelExitStatus = channelExitStatus;
	}
}
