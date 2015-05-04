package org.jumbune.profiling.utils;

import java.io.IOException;
import java.util.Map;

import org.jumbune.common.beans.Slave;
import org.jumbune.common.beans.SupportedHadoopDistributions;
import org.jumbune.common.job.Config;
import org.jumbune.common.yarn.beans.YarnMaster;
import org.jumbune.common.yarn.beans.YarnSlaveParam;
import org.jumbune.common.job.JobConfig;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.beans.JumbuneInferredStats;
import org.jumbune.profiling.beans.NodeInfo;
import org.jumbune.profiling.beans.NodeType;

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

	/** The config. */
	private JobConfig jobConfig;

	/** The profiler jmx dump. */
	private ProfilerJMXDump profilerJMXDump;

	/** The node ip. */
	private String nodeIp;

	/** The reset. */
	private boolean reset;

	private SupportedHadoopDistributions version;
	
	private static final String COLLECTJMXSTATSFAILED = "Collecting JMX stats failed for Node ";

	/**
	 * Instantiates a new profiler stats.
	 * 
	 * @param config
	 *            the config
	 * @param nodeIp
	 *            the node ip
	 */
	public ProfilerStats(Config config, String nodeIp, SupportedHadoopDistributions version) {
		this.jobConfig = (JobConfig) config;
		this.nodeIp = nodeIp;
		profilerJMXDump = new ProfilerJMXDump();
		this.version = version;

	}

	/**
	 * Gets the name node ip.
	 * 
	 * @return the name node ip
	 */
	public String getNameNodeIP() {
		return jobConfig.getMaster().getHost();
	}

	/**
	 * Instantiates a new profiler stats.
	 * 
	 * @param config
	 *            the config
	 */
	public ProfilerStats(Config config) {
		this.jobConfig = (JobConfig) config;
		profilerJMXDump = new ProfilerJMXDump();
	}

	/**
	 * Gets the dn port.
	 * 
	 * @return the dnPort
	 */
	public String getDnPort() {
		if (dnPort == null) {
			dnPort = jobConfig.getSlaveParam().getDataNodeJmxPort();
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
			ttPort = jobConfig.getSlaveParam().getTaskTrackerJmxPort();
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
			nnPort = jobConfig.getMaster().getNameNodeJmxPort();
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
			jtPort = jobConfig.getMaster().getJobTrackerJmxPort();
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
				dnStats = profilerJMXDump.getAllJMXStats(JMXDeamons.DATA_NODE, version, nodeIp, getDnPort());
			} catch (Exception e) {
				/*
				 * Catching generic exception as profilerJMXDump.getAllJmxStats(...) throwing lots of exceptions.
				 */
				throw new HTFProfilingException( COLLECTJMXSTATSFAILED + nodeIp + ", " + NodeType.DataNode.toString() + ", "
						+ getDnPort() , e);
			}
		}
		return dnStats.get(attribute);
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
				ttStats = profilerJMXDump.getAllJMXStats(JMXDeamons.TASK_TRACKER, version, nodeIp, getTtPort());
			} catch (Exception e) {
				/*
				 * Catching generic exception as profilerJMXDump.getAllJmxStats(...) throwing lots of exceptions.
				 */
				throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIp + ", " + NodeType.TaskTracker.toString() + ", "
						+ getTtPort() , e);
			}

		}
		return ttStats.get(attribute);
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
				nnStats = profilerJMXDump.getAllJMXStats(JMXDeamons.NAME_NODE, version, getNameNodeIP(), getNnPort());
			} catch (Exception e) {
				/*
				 * Catching generic exception as profilerJMXDump.getAllJmxStats(...) throwing lots of exceptions.
				 */
				throw new HTFProfilingException(COLLECTJMXSTATSFAILED+ nodeIp + ", " + NodeType.NameNode.toString() + ", "
						+ getNnPort() , e);
			}

		}
		return nnStats.get(attribute);
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
	 * Gets the jt stats.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return the jtStats
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	public String getJtStats(String attribute) throws HTFProfilingException {
		if ((jtStats == null) || reset) {
			try {
				jtStats = profilerJMXDump.getAllJMXStats(JMXDeamons.JOB_TRACKER, version, getNameNodeIP(), getJtPort());
			} catch (Exception e) {
				/*
				 * Catching generic exception as profilerJMXDump.getAllJmxStats(...) throwing lots of exceptions.
				 */
				throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIp + ", " + NodeType.JobTracker.toString() + ", "
						+ getJtPort() , e);
			}

		}
		return jtStats.get(attribute);
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
				double localUsage = profilerJMXDump.getLocalDataUsage(jobConfig, nodeIp);
				statValue = String.valueOf(localUsage);
				break;
			default:
				if ((jumbuneContextStats == null) || reset) {
					jumbuneContextStats = profilerJMXDump.getAllJMXStats(JMXDeamons.TASK_TRACKER, version, nodeIp, getTtPort());
				}
				statValue = jumbuneContextStats.get(jumbuneStat);
			}
		} catch (Exception e) {
			/*
			 * Catching generic exception as profilerJMXDump.getAllJmxStats(...) throwing lots of exceptions.
			 */

			throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIp , e);
		}
		return statValue;
	}

	/**
	 * Gets the data load partition stats.
	 * 
	 * @param nodeIp
	 *            the node ip
	 * @param node
	 *            the node
	 * @return the data load partition stats
	 * @throws HTFProfilingException
	 *             the hTF profiling exception
	 */
	public String getDataLoadPartitionStats(String nodeIp, NodeInfo node, Config config) throws HTFProfilingException {
		double dataLoad;
		String statValue;
		try {
			dataLoad = profilerJMXDump.getDataLoadonNodes(nodeIp, node, config);
			statValue = String.valueOf(dataLoad);
		} catch (Exception e) {
			/*
			 * Catching generic exception as profilerJMXDump.getAllJmxStats(...) throwing lots of exceptions.
			 */
			throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIp , e);
		}
		return statValue;
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
				for (Slave slave : jobConfig.getSlaves()) {
					for (String hostIp : slave.getHosts()) {
						clusterWideStats = profilerJMXDump.getAllJMXStats(JMXDeamons.TASK_TRACKER, version, hostIp, getTtPort());
						tempMapSlot = clusterWideStats.get("MapTaskSlots");
						tempReduceSlot = clusterWideStats.get("ReduceTaskSlots");
						tempMapRunning = clusterWideStats.get("Maps_running");
						tempReduceRuning = clusterWideStats.get("Reduces_running");
						if (tempMapSlot != null) {
							totalMapSlotsAvailable = totalMapSlotsAvailable + Integer.parseInt(tempMapSlot);
						}
						if (tempMapRunning != null) {
							totalMapRunning = totalMapRunning + Integer.parseInt(tempMapRunning);
						}
						if (tempReduceRuning != null) {
							totalReducerRunning = totalReducerRunning + Integer.parseInt(tempReduceRuning);
						}
						if (tempReduceSlot != null) {
							totalReduceSlotsAvailable = totalReduceSlotsAvailable + Integer.parseInt(tempReduceSlot);
						}
					}
					totalMapSlotsAvailable = totalMapSlotsAvailable - totalMapRunning;
					totalReduceSlotsAvailable = totalReduceSlotsAvailable - totalReducerRunning;
					clusterWideStats.put("totalMapSlotsAvailable", Integer.toString(totalMapSlotsAvailable));
					clusterWideStats.put("totalReduceSlotsAvailable", Integer.toString(totalReduceSlotsAvailable));

				}
			} catch (Exception e) {
				/*
				 * Catching generic exception as profilerJMXDump.getAllJmxStats(...) throwing lots of exceptions.
				 */
				throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIp + ", " + NodeType.JobTracker.toString() + ", "
						+ getJtPort() , e);
			}
		}
		return clusterWideStats.get(attribute);
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
				memoryStats = profilerJMXDump.getRemoteMemoryUtilisation(jobConfig, nodeIp);

			} catch (JSchException jsche) {
				throw new HTFProfilingException("Unable to connect and make the session", jsche);
			} catch (IOException ioe) {
				throw new HTFProfilingException("Error executing shell command to get memeory stats", ioe);
			}
		}
		if(memoryStats!= null){
			return memoryStats.get(attribute);
		}
		return null;
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
				cpuStats = profilerJMXDump.getRemoteCPUStats(jobConfig, nodeIp);
			} catch (Exception e) {
				/*
				 * Catching generic exception as profilerJMXDump.getCPUStats(...) throwing lots of exceptions.
				 */
				throw new HTFProfilingException("Error ocurred while fetching cpu stats", e);
			}

		}
		return cpuStats.get(attribute);
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
		return nodeIp;
	}

	/**
	 * Sets the node ip.
	 * 
	 * @param nodeIp
	 *            the nodeIp to set
	 */
	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
		reset = true;
	}
	
	/**
	 * @return the nmStats
	 * @throws HTFProfilingException
	 */
	public String getNmStats(String attribute) throws HTFProfilingException {
		if ((nmStats == null) || reset) {
			try {
				nmStats = profilerJMXDump.getAllJMXStats(
						JMXDeamons.NODE_MANAGER, version, nodeIp, getNmPort());
			} catch (Exception e) {
				/*
				 * Catching generic exception as
				 * profilerJMXDump.getAllJmxStats(...) throwing lots of
				 * exceptions.
				 */
				throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIp
						+ ", " + NodeType.NodeManager.toString() + ", "
						+ getNmPort(), e);
			}
		}
		return nmStats.get(attribute);
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
				rmStats = profilerJMXDump.getAllJMXStats(
						JMXDeamons.RESOURCE_MANAGER, version, getNameNodeIP(),
						getRmPort());
			} catch (Exception e) {
				/*
				 * Catching generic exception as
				 * profilerJMXDump.getAllJmxStats(...) throwing lots of
				 * exceptions.
				 */
				throw new HTFProfilingException(COLLECTJMXSTATSFAILED + nodeIp
						+ ", " + NodeType.ResourceManager.toString() + ", "
						+ getRmPort(), e);
			}
		}
		return rmStats.get(attribute);
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
			YarnSlaveParam ySlaveParam = jobConfig.getSlaveParam();
			nmPort = ySlaveParam.getNodeManagerJmxPort();
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
			YarnMaster yMaster = jobConfig.getMaster();
			rmPort = yMaster.getResourceManagerJmxPort();
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

}