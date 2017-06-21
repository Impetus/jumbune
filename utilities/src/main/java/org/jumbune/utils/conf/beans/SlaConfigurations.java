package org.jumbune.utils.conf.beans;

import java.util.Collections;
import java.util.List;

public class SlaConfigurations {
	
	private List<SlaConf> slaConfList;

	@SuppressWarnings("unchecked")
	public List<SlaConf> getSlaConfList() {
		if (slaConfList == null) {
			return Collections.EMPTY_LIST;
		}
		return slaConfList;
	}

	public void setSlaConfList(List<SlaConf> slaConfList) {
		this.slaConfList = slaConfList;
	}
	
}
