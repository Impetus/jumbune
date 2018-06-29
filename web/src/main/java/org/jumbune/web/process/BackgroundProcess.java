package org.jumbune.web.process;

public interface BackgroundProcess {
	
	// This interface was created mainly because of this method otherwise Thread would be sufficient
	public void setOn(boolean isOn);
	
	public void start();
	
	public Thread.State getState();

}
