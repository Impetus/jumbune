package org.jumbune.remoting.common;

/***
 * This class provide utility methods to other classes
 * 
 */
public final class RemoterUtility {
	private RemoterUtility(){}
	private static String agentHome = null;
	private static String hadoopHome = null;

	/***
	 * This class finds out agent home on the local machine
	 * 
	 * @return AgentHome define in lcoal machine
	 */
	public static String getAgentHome() {
		if (agentHome == null) {
			agentHome = System.getenv("AGENT_HOME");
			if (agentHome.trim().endsWith("/")) {
				agentHome += "/";
			}
		}
		return agentHome;
	}

	/***
	 * This class finds out agent home on the local machine
	 * 
	 * @return AgentHome define in lcoal machine
	 */
	public static String getHadoopHome() {
		if (hadoopHome == null) {
			hadoopHome = System.getenv("HADOOP_HOME");
		}
		return hadoopHome;
	}

	/**
	 * checks if given input msg is empty or not
	 * @param msg
	 * @return
	 */
	public static boolean isNotEmpty(String msg) {
		if(msg!=null && !"".equals(msg)){
			return true;
		}
		return false;
	}

}
