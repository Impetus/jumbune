package org.jumbune.monitoring.beans;

/**
 * The Enum JMXDeamons contains the enum for tasktracker,jobtracker,datanode, namenode and CLDB(for mapr).
 */
public enum JMXDeamons {
	TASK_TRACKER("TaskTracker"), NAME_NODE("NameNode"), JOB_TRACKER(
			"JobTracker"), DATA_NODE("DataNode"), RESOURCE_MANAGER(
			"ResourceManager"), NODE_MANAGER("NodeManager"), CLDB("CLDB"),
	JOB_HISTORY_SERVER("JobHistoryServer");

	private String name;

	/**
	 * Instantiates a new jMX deamons.
	 *
	 * @param jmxStats the jmx stats
	 */
	private JMXDeamons(String jmxStats) {
		name = jmxStats;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

}
