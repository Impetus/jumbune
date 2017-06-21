package org.jumbune.remoting.server.jsch;

import java.io.Reader;

import com.jcraft.jsch.Channel;

public class ChannelReaderResponse {
	
	private Channel channel;
	private Reader reader;
	private int exitCode;
	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public Reader getReader() {
		return reader;
	}
	public void setReader(Reader reader) {
		this.reader = reader;
	}
	public int getExitCode() {
		return exitCode;
	}
	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}
	
}
