package org.jumbune.common.beans;

import java.util.Map;
import java.util.Set;

public abstract class AccumulatedJMXStats {

	protected Map<String, JMXStats> nodeStatsMapper;

	//assuming the same port is being listened on all datanodes
	protected int port;

	public Set<String> nodeList(){
		return nodeStatsMapper.keySet();
	}

	public JMXStats getNodeStats(String node){
		return nodeStatsMapper.get(node);
	}

	public void addOrUpdateDataNodeStats(String node, JMXStats jmxStats){
		if(nodeStatsMapper.containsKey(node)){
			nodeStatsMapper.put(node, jmxStats);
		}
	}
}
