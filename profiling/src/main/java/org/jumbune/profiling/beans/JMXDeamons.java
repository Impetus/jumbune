package org.jumbune.profiling.beans;

/**
 * The Enum JMXDeamons contains the enum for tasktracker,jobtracker,datanode and namenode.
 */
public enum JMXDeamons {
	TASK_TRACKER("TaskTracker"), NAME_NODE("NameNode"), JOB_TRACKER("JobTracker"), DATA_NODE("DataNode"),OPERATING_SYSTEM("OperatingSystem");
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
