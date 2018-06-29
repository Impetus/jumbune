package org.jumbune.common.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clusterInfo")
public class RMInfo {

	private String haState;

	public String getHaState() {
		return haState;
	}

	public void setHaState(String haState) {
		this.haState = haState;
	}

}
