package org.jumbune.monitoring.utils;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.monitoring.beans.JMXDeamons;
import org.jumbune.monitoring.beans.JumbuneInferredStats;
import org.jumbune.monitoring.beans.NodeInfo;
import org.jumbune.monitoring.beans.NodeType;

import com.jcraft.jsch.JSchException;

/**
 * Gets profiling stats corresponding to a node.
 * 
 */
public class ProfilerStats {

	/** The dn port. */
	private String dnPort;

	/** The tt port. */
	private String ttPort;

	/** The nn port. */
	private String nnPort;

	/** The jt port. */
	private String jtPort;

	/** The nm port. */
	private String nmPort;

	/** The rm port. */
	private String rmPort;

	/** The dn stats. */
	private Map<String, String> dnStats;

	/** The tt stats. */
	private Map<String, String> ttStats;

	/** The nn stats. */
	private Map<String, String> nnStats;

	/** The jt stats. */
	private Map<String, String> jtStats;

	/** The jumbune context stats. */
	private Map<String, String> jumbuneContextStats;

	/** The cluster wide stats. */
	private Map<String, String> clusterWideStats;

	/** The memory stats. */
	private Map<String, String> memoryStats;

	/** The cpu stats. */
	private Map<String, String> cpuStats;

	/** The node manager stats. */
	private Map<String, String> nmStats;

	/** The resource manager stats **/
	private Map<String, String> rmStats;

	/** The cluster **/
	private Cluster cluster;

	/** The profiler jmx dump. */
	private ProfilerJMXDump profilerJMXDump;

	/** The node ip. */
	private String nodeIP;

	/** The reset. */
	private boolean reset;

	private static final String COLLECTJMXSTATSFAILED = "Collecting JMX stats failed for Node ";

	private static final Logger LOGGER = LogManager.getLogger(ProfilerStats.class);

	/**
	 * Instantiates a new profiler stats.
	 * 
	 * @param config
	 *            the config
	 * @param nodeIp
	 *            the node ip
	 */
	public ProfilerStats(Cluster cluster, String nodeIp) {
		this.cluster = cluster;
		this.nodeIP = nodeIp;
		profilerJMXDump = new ProfilerJMXDump();

	}

	/**
	 * Gets the name node ip.
	 * 
	 * @return the name node ip
	 */
	public String getNameNodeIP() {
		return cluster.getNameNode();
	}

	/**
	 * Instantiates a new profiler stats.
	 * 
	 * @param config
	 *            the config
	 */
	public ProfilerStats(Cluster cluster) {
		this.cluster = cluster;
		profilerJMXDump = new ProfilerJMXDump();
	}

	/**
	 * Gets the dn port.
	 * 
	 * @return the dnPort
	 */
	public String getDnPort() {
		if (dnPort == null) {
			dnPort = cluster.getWorkers().getDataNodeJmxPort();
		}
		return dnPort;
	}

	/**
	 * Sets the dn port.
	 * 
	 * @param dnPort
	 *            the dnPort to set
	 */
	public void setDnPort(String dnPort) {
		this.dnPort = dnPort;
	}

	/**
	 * Gets the tt port.
	 * 
	 * @return the ttPort
	 */
	public String getTtPort() {
		if (ttPort == null) {
			ttPort = cluster.getWorkers().getTaskExecutorJmxPort();
		}
		return ttPort;
	}

	/**
	 * Sets the tt port.
	 * 
	 * @param ttPort
	 *            the ttPort to set
	 */
	public void setTtPort(String ttPort) {
		this.ttPort = ttPort;
	}

	/**
	 * Gets the nn port.
	 * 
	 * @return the nnPort
	 */
	public String getNnPort() {
		if (nnPort == null) {
			nnPort = cluster.getNameNodes().getNameNodeJmxPort();
		}
		return nnPort;
	}

	/**
	 * Sets the nn port.
	 * 
	 * @param nnPort
	 *            the nnPort to set
	 */
	public void setNnPort(String nnPort) {
		this.nnPort = nnPort;
	}

	/**
	 * Gets the jt port.
	 * 
	 * @return the jtPort
	 */
	public String getJtPort() {
		if (jtPort == null) {
			jtPort = cluster.getTaskManagers().getTaskManagerJmxPort();
		}
		return jtPort;
	}

	/**
	 * Sets the jt port.
	 * 
	 * @param jtPort
	 *            the jtPort to set
	 */
	public void setJtPort(String jtPort) {
		this.jtPort = jtPort;
	}

	/**
	 * Gets the dn stats.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return the dnStats
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	public String getDnStats(String attribute) throws HTFProfilingException {
		if ((dnStats == null) || reset) {
			try {
				dnStats = profilerJMXDump.getAllJMXStats(JMXDeamons.DATA_NODE, nodeIP, getDnPort(),
						cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				/*
				 * Catching generic exception as
				 * profilerJMXDump.getAllJmxStats(...) throwing lots of
				 * exceptions.
				 */
				throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIP + ", "
						+ NodeType.DataNode.toString() + ", " + getDnPort(), e);
			}
		}
		return dnStats.get(attribute);
	}

	public boolean isDatanodeStatsAvailable() {
		if ((dnStats == null) || reset) {
			try {
				dnStats = profilerJMXDump.getAllJMXStats(JMXDeamons.DATA_NODE, nodeIP, getDnPort(),
						cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				LOGGER.error(COLLECTJMXSTATSFAILED + nodeIP + ", " + NodeType.DataNode.toString()
						+ ", " + getDnPort(), e);
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets the dn stats.
	 * 
	 * @param dnStats
	 *            the dnStats to set
	 */
	public void setDnStats(Map<String, String> dnStats) {
		this.dnStats = dnStats;
	}

	/**
	 * Gets the tt stats.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return the ttStats
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	public String getTtStats(String attribute) throws HTFProfilingException {
		if ((ttStats == null) || reset) {
			try {
				ttStats = profilerJMXDump.getAllJMXStats(JMXDeamons.TASK_TRACKER, nodeIP,
						getTtPort(), cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				/*
				 * Catching generic exception as
				 * profilerJMXDump.getAllJmxStats(...) throwing lots of
				 * exceptions.
				 */
				throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIP + ", "
						+ NodeType.TaskTracker.toString() + ", " + getTtPort(), e);
			}

		}
		return ttStats.get(attribute);
	}

	public boolean isTaskTrackerStatsAvailable() {
		if ((ttStats == null) || reset) {
			try {
				ttStats = profilerJMXDump.getAllJMXStats(JMXDeamons.TASK_TRACKER, nodeIP,
						getTtPort(), cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				LOGGER.error(COLLECTJMXSTATSFAILED + nodeIP + ", " + NodeType.TaskTracker.toString()
						+ ", " + getTtPort(), e);
				return false;
			}

		}
		return true;
	}

	/**
	 * Sets the tt stats.
	 * 
	 * @param ttStats
	 *            the ttStats to set
	 */
	public void setTtStats(Map<String, String> ttStats) {
		this.ttStats = ttStats;
	}

	/**
	 * Gets the nn stats.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return the nnStats
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	public String getNnStats(String attribute) throws HTFProfilingException {
		if ((nnStats == null) || reset) {
			try {
				nnStats = profilerJMXDump.getAllJMXStats(JMXDeamons.NAME_NODE, nodeIP, getNnPort(),
						cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				/*
				 * Catching generic exception as
				 * profilerJMXDump.getAllJmxStats(...) throwing lots of
				 * exceptions.
				 */
				throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIP + ", "
						+ NodeType.NameNode.toString() + ", " + getNnPort(), e);
			}

		}
		return nnStats.get(attribute);
	}

	public boolean isNamenodeStatsAvailable() {
		if ((nnStats == null) || reset) {
			try {
				nnStats = profilerJMXDump.getAllJMXStats(JMXDeamons.NAME_NODE, nodeIP, getNnPort(),
						cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				LOGGER.error(COLLECTJMXSTATSFAILED + nodeIP + ", " + NodeType.NameNode.toString()
						+ ", " + getNnPort(), e);
				return false;
			}

		}
		return true;
	}

	/**
	 * Sets the nn stats.
	 * 
	 * @param nnStats
	 *            the nnStats to set
	 */
	public void setNnStats(Map<String, String> nnStats) {
		this.nnStats = nnStats;
	}

	/**
	 * Sets the jt stats.
	 * 
	 * @param jtStats
	 *            the jtStats to set
	 */
	public void setJtStats(Map<String, String> jtStats) {
		this.jtStats = jtStats;
	}

	/**
	 * Gets the jumbune context stats.
	 * 
	 * @param jumbuneStat
	 *            the jumbune stat
	 * @return the jumbuneContextStats
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	public String getJumbuneContextStats(String jumbuneStat) throws HTFProfilingException {

		JumbuneInferredStats jStat = JumbuneInferredStats.valueOf(jumbuneStat);
		String statValue;
		try {
			switch (jStat) {
			case localDataUsage:
				double localUsage = profilerJMXDump.getLocalDataUsage(cluster, nodeIP);
				statValue = String.valueOf(localUsage);
				break;
			default:
				if ((jumbuneContextStats == null) || reset) {
					jumbuneContextStats = profilerJMXDump.getAllJMXStats(JMXDeamons.TASK_TRACKER,
							nodeIP, getTtPort(), cluster.isJmxPluginEnabled());
				}
				statValue = jumbuneContextStats.get(jumbuneStat);
			}
		} catch (Exception e) {
			/*
			 * Catching generic exception as profilerJMXDump.getAllJmxStats(...)
			 * throwing lots of exceptions.
			 */

			throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIP, e);
		}
		return statValue;
	}

	/**
	 * Gets the data load partition stats.
	 * 
	 * @param nodeIP
	 *            the node ip
	 * @param node
	 *            the node
	 * @return the data load partition stats
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	public String getDataLoadPartitionStats(String nodeIP, NodeInfo node, Cluster cluster, String[] dataLoadResult)
			throws Exception {
		double dataLoad = profilerJMXDump.getDataLoadonNodes(nodeIP, node, cluster, dataLoadResult);
		return String.valueOf(dataLoad);
		
	}

	/**
	 * Gets the cluster wide stats.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return the cluster wide stats
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	public String getClusterWideStats(String attribute) throws HTFProfilingException {
		if ((clusterWideStats == null) || reset) {
			try {
				int totalMapSlotsAvailable = 0;
				int totalReduceSlotsAvailable = 0;
				int totalMapRunning = 0;
				int totalReducerRunning = 0;
				String tempMapSlot = null;
				String tempReduceSlot = null;
				String tempMapRunning = null;
				String tempReduceRuning = null;
				boolean jmxPluginEnabled = cluster.isJmxPluginEnabled();
				for (String workerHost : cluster.getWorkers().getHosts()) {
					clusterWideStats = profilerJMXDump.getAllJMXStats(JMXDeamons.TASK_TRACKER,
							workerHost, getTtPort(), jmxPluginEnabled);
					tempMapSlot = clusterWideStats.get("MapTaskSlots");
					tempReduceSlot = clusterWideStats.get("ReduceTaskSlots");
					tempMapRunning = clusterWideStats.get("Maps_running");
					tempReduceRuning = clusterWideStats.get("Reduces_running");
					if (tempMapSlot != null) {
						totalMapSlotsAvailable = totalMapSlotsAvailable
								+ Integer.parseInt(tempMapSlot);
					}
					if (tempMapRunning != null) {
						totalMapRunning = totalMapRunning + Integer.parseInt(tempMapRunning);
					}
					if (tempReduceRuning != null) {
						totalReducerRunning = totalReducerRunning
								+ Integer.parseInt(tempReduceRuning);
					}
					if (tempReduceSlot != null) {
						totalReduceSlotsAvailable = totalReduceSlotsAvailable
								+ Integer.parseInt(tempReduceSlot);
					}
					totalMapSlotsAvailable = totalMapSlotsAvailable - totalMapRunning;
					totalReduceSlotsAvailable = totalReduceSlotsAvailable - totalReducerRunning;
					clusterWideStats.put("totalMapSlotsAvailable",
							Integer.toString(totalMapSlotsAvailable));
					clusterWideStats.put("totalReduceSlotsAvailable",
							Integer.toString(totalReduceSlotsAvailable));

				}
			} catch (Exception e) {
				/*
				 * Catching generic exception as
				 * profilerJMXDump.getAllJmxStats(...) throwing lots of
				 * exceptions.
				 */
				throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIP + ", "
						+ NodeType.JobTracker.toString() + ", " + getJtPort(), e);
			}
		}
		return clusterWideStats.get(attribute);
	}

	public boolean isClusterWideStatsAvailable() {
		if ((clusterWideStats == null) || reset) {
			try {
				int totalMapSlotsAvailable = 0;
				int totalReduceSlotsAvailable = 0;
				int totalMapRunning = 0;
				int totalReducerRunning = 0;
				String tempMapSlot = null;
				String tempReduceSlot = null;
				String tempMapRunning = null;
				String tempReduceRuning = null;
				boolean jmxPluginEnabled = cluster.isJmxPluginEnabled();
				for (String workerHost : cluster.getWorkers().getHosts()) {
					clusterWideStats = profilerJMXDump.getAllJMXStats(JMXDeamons.TASK_TRACKER,
							workerHost, getTtPort(), jmxPluginEnabled);
					tempMapSlot = clusterWideStats.get("MapTaskSlots");
					tempReduceSlot = clusterWideStats.get("ReduceTaskSlots");
					tempMapRunning = clusterWideStats.get("Maps_running");
					tempReduceRuning = clusterWideStats.get("Reduces_running");
					if (tempMapSlot != null) {
						totalMapSlotsAvailable = totalMapSlotsAvailable
								+ Integer.parseInt(tempMapSlot);
					}
					if (tempMapRunning != null) {
						totalMapRunning = totalMapRunning + Integer.parseInt(tempMapRunning);
					}
					if (tempReduceRuning != null) {
						totalReducerRunning = totalReducerRunning
								+ Integer.parseInt(tempReduceRuning);
					}
					if (tempReduceSlot != null) {
						totalReduceSlotsAvailable = totalReduceSlotsAvailable
								+ Integer.parseInt(tempReduceSlot);
					}
					totalMapSlotsAvailable = totalMapSlotsAvailable - totalMapRunning;
					totalReduceSlotsAvailable = totalReduceSlotsAvailable - totalReducerRunning;
					clusterWideStats.put("totalMapSlotsAvailable",
							Integer.toString(totalMapSlotsAvailable));
					clusterWideStats.put("totalReduceSlotsAvailable",
							Integer.toString(totalReduceSlotsAvailable));

				}
			} catch (Exception e) {
				LOGGER.error(COLLECTJMXSTATSFAILED + nodeIP + ", " + NodeType.JobTracker.toString()
						+ ", " + getJtPort(), e);
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets the jumbune context stats.
	 * 
	 * @param jumbuneContextStats
	 *            the jumbuneContextStats to set
	 */
	public void setJumbuneContextStats(Map<String, String> jumbuneContextStats) {
		this.jumbuneContextStats = jumbuneContextStats;
	}

	/**
	 * Gets the memory stats.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return the memoryStats
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	public String getMemoryStats(String attribute) throws HTFProfilingException {
		if ((memoryStats == null) || reset) {
			try {
				memoryStats = profilerJMXDump.getRemoteMemoryUtilisation(cluster, nodeIP);

			} catch (JSchException jsche) {
				throw new HTFProfilingException("Unable to connect and make the session", jsche);
			} catch (IOException ioe) {
				throw new HTFProfilingException(
						"Error executing shell command to get memeory stats", ioe);
			}
		}
		if (memoryStats != null) {
			return memoryStats.get(attribute);
		}
		return null;
	}

	public boolean isMemoryStatsAvailable() {
		if (memoryStats == null || reset) {
			try {
				memoryStats = profilerJMXDump.getRemoteMemoryUtilisation(cluster, nodeIP);
			} catch (JSchException jsche) {
				LOGGER.error("Unable to connect and make the session", jsche);
			} catch (IOException ioe) {
				LOGGER.error("Error executing shell command to get memeory stats", ioe);
			}
		}
		return true;
	}

	/**
	 * Sets the memory stats.
	 * 
	 * @param memoryStats
	 *            the memoryStats to set
	 */
	public void setMemoryStats(Map<String, String> memoryStats) {
		this.memoryStats = memoryStats;
	}

	/**
	 * Gets the cpu stats.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return the cpuStats
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	public String getCpuStats(String attribute) throws HTFProfilingException {
		if ((cpuStats == null) || reset) {
			try {
				cpuStats = profilerJMXDump.getRemoteCPUStats(cluster, nodeIP);
			} catch (Exception e) {
				/*
				 * Catching generic exception as
				 * profilerJMXDump.getCPUStats(...) throwing lots of exceptions.
				 */

				throw new HTFProfilingException("Error ocurred while fetching cpu stats", e);
			}

		}
		return cpuStats.get(attribute);
	}

	public boolean isCpuStatsAvailable() {
		if (cpuStats == null || reset) {
			try {
				cpuStats = profilerJMXDump.getRemoteCPUStats(cluster, nodeIP);
			} catch (Exception e) {
				LOGGER.error("Error ocurred while fetching cpu stats", e);
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets the cpu stats.
	 * 
	 * @param cpuStats
	 *            the cpuStats to set
	 */
	public void setCpuStats(Map<String, String> cpuStats) {
		this.cpuStats = cpuStats;
	}

	/**
	 * Gets the node ip.
	 * 
	 * @return the nodeIp
	 */
	public String getNodeIp() {
		return nodeIP;
	}

	/**
	 * Sets the node ip.
	 * 
	 * @param nodeIp
	 *            the nodeIp to set
	 */
	public void setNodeIp(String nodeIp) {
		this.nodeIP = nodeIp;
		reset = true;
	}

	/**
	 * @return the nmStats
	 * @throws HTFProfilingException
	 */
	public String getNmStats(String attribute) throws HTFProfilingException {
		if ((nmStats == null) || reset) {
			try {
				nmStats = profilerJMXDump.getAllJMXStats(JMXDeamons.NODE_MANAGER, nodeIP,
						getNmPort(), cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				/*
				 * Catching generic exception as
				 * profilerJMXDump.getAllJmxStats(...) throwing lots of
				 * exceptions.
				 */
				throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIP + ", "
						+ NodeType.NodeManager.toString() + ", " + getNmPort(), e);
			}
		}
		return nmStats.get(attribute);
	}

	public boolean isNodeManagerStatsAvailable() {
		if ((nmStats == null) || reset) {
			try {
				nmStats = profilerJMXDump.getAllJMXStats(JMXDeamons.NODE_MANAGER, nodeIP,
						getNmPort(), cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				LOGGER.error(COLLECTJMXSTATSFAILED + nodeIP + ", " + NodeType.NodeManager.toString()
						+ ", " + getNmPort(), e);
				return false;
			}
		}
		return true;
	}

	/**
	 * @param nmStats
	 *            the nmStats to set
	 */
	public void setNmStats(Map<String, String> nmStats) {
		this.nmStats = nmStats;
	}

	/**
	 * @return the rmStats
	 * @throws HTFProfilingException
	 */
	public String getRmStats(String attribute) throws HTFProfilingException {
		if ((rmStats == null) || reset) {
			try {
				rmStats = profilerJMXDump.getAllJMXStats(JMXDeamons.RESOURCE_MANAGER, nodeIP,
						getRmPort(), cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				/*
				 * Catching generic exception as
				 * profilerJMXDump.getAllJmxStats(...) throwing lots of
				 * exceptions.
				 */

				throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIP + ", "
						+ NodeType.ResourceManager.toString() + ", " + getRmPort(), e);
			}
		}
		return rmStats.get(attribute);
	}

	public boolean isResourceManagerStatsAvailable() {
		if ((rmStats == null) || reset) {
			try {
				rmStats = profilerJMXDump.getAllJMXStats(JMXDeamons.RESOURCE_MANAGER, nodeIP,
						getRmPort(), cluster.isJmxPluginEnabled());
			} catch (Exception e) {
				LOGGER.error(COLLECTJMXSTATSFAILED + nodeIP + ", "
						+ NodeType.ResourceManager.toString() + ", " + getRmPort(), e);
				return false;
			}
		}
		return true;
	}

	/**
	 * @param rmStats
	 *            the rmStats to set
	 */
	public void setRmStats(Map<String, String> rmStats) {
		this.rmStats = rmStats;
	}

	/**
	 * @return the nmPort
	 */
	public String getNmPort() {
		if (nmPort == null) {
			nmPort = cluster.getWorkers().getTaskExecutorJmxPort();
		}
		return nmPort;
	}

	/**
	 * @param nmPort
	 *            the nmPort to set
	 */
	public void setNmPort(String nmPort) {
		this.nmPort = nmPort;
	}

	/**
	 * @return the rmPort
	 */
	public String getRmPort() {
		if (rmPort == null) {
			rmPort = cluster.getTaskManagers().getTaskManagerJmxPort();
		}
		return rmPort;
	}

	/**
	 * @param rmPort
	 *            the rmPort to set
	 */
	public void setRmPort(String rmPort) {
		this.rmPort = rmPort;
	}

	@Override
	public String toString() {
		return "ProfilerStats [dnPort=" + dnPort + ", ttPort=" + ttPort + ", nnPort=" + nnPort
				+ ", jtPort=" + jtPort + ", nmPort=" + nmPort + ", rmPort=" + rmPort + ", dnStats="
				+ dnStats + ", ttStats=" + ttStats + ", nnStats=" + nnStats + ", jtStats=" + jtStats
				+ ", jumbuneContextStats=" + jumbuneContextStats + ", clusterWideStats="
				+ clusterWideStats + ", memoryStats=" + memoryStats + ", cpuStats=" + cpuStats
				+ ", nmStats=" + nmStats + ", rmStats=" + rmStats + ", cluster=" + cluster
				+ ", profilerJMXDump=" + profilerJMXDump + ", nodeIp=" + nodeIP + ", reset=" + reset
				+ "]";
	}

}