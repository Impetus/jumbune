package org.jumbune.remoting.client;

import org.jumbune.remoting.client.ha.RemoterHA;
import org.jumbune.remoting.client.ha.RemoterNNHA;

public class RemoterFactory {

	public static Remoter getRemoter(String host, int port, String jobName, boolean agentHAEnabled,
			boolean nameNodeHAEnabled, String[] zkHosts, String clusterName) {
		if (agentHAEnabled) {
			return new RemoterHA(host, port, jobName, zkHosts, clusterName);
		} else if (nameNodeHAEnabled) {
			return new RemoterNNHA(host, port, jobName, zkHosts, clusterName);
		} else {
			return new RemoterNonHA(host, port, jobName);
		}
	}

}
