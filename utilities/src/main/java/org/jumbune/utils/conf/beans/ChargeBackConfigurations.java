package org.jumbune.utils.conf.beans;

import java.util.Collections;
import java.util.List;

/**
 * The Class ChargeBackConfigurations holds the cost of memory and vcore for a particular queue.
 */
public class ChargeBackConfigurations {
	
	private List<ChargeBackConf> chargeBackConfList ;

	@SuppressWarnings("unchecked")
	public List<ChargeBackConf> getChargeBackConfList() {
		if (chargeBackConfList == null) {
			return Collections.EMPTY_LIST;
		}
		
		return chargeBackConfList;
	}

	public void setChargeBackConfList(List<ChargeBackConf> chargeBackConfList) {
		this.chargeBackConfList = chargeBackConfList;
	}
	
	

}
