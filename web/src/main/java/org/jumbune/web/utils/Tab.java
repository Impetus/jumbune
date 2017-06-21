package org.jumbune.web.utils;

public enum Tab {
	
	BOTH, NODE_SPECIFIC, ALL_NODES;
	
	public static Tab getEnum(String str) {
		if (Tab.ALL_NODES.toString().equals(str)) {
			return ALL_NODES;
		} else {
			return NODE_SPECIFIC;
		}
	}

}
