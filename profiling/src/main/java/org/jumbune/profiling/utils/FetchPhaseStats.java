package org.jumbune.profiling.utils;

import static org.jumbune.profiling.utils.ProfilerConstants.JMX_URL_POSTFIX;
import static org.jumbune.profiling.utils.ProfilerConstants.JMX_URL_PREFIX;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.exception.JumbuneException;


/**
 * The Class FetchPhaseStats is responsible for fetching the phase stats.
 */
public class FetchPhaseStats extends TimerTask {

	/** The Constant PHASE_STATS. */
	private static final String PHASE_STATS = "PhaseStats";
	
	/** The Constant PHASE_STATS_URL. */
	private static final String PHASE_STATS_URL = "org.apache.hadoop.mapred:type=PhaseMetrics";
	
	/** The node ip. */
	private String nodeIP;
	
	/** The tt port. */
	private String ttPort;
	
	/** The job id. */
	private String jobId;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(FetchPhaseStats.class);
	
	@Override
	public void run() {
		try {
			JMXServiceURL url = new JMXServiceURL(JMX_URL_PREFIX + getNodeIP() + ":" + getTtPort() + JMX_URL_POSTFIX);
			JMXConnector jmxConnector = JMXConnectorFactory.connect(url, null);
			MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
			Map<String, List<String>> phaseStats = (Map<String, List<String>>) connection.getAttribute(new ObjectName(PHASE_STATS_URL), PHASE_STATS);
			LOGGER.debug("Phase stats:: "+phaseStats);
		} catch (Exception ex) {
			LOGGER.error("Exception occurred in fetching stats:: "+ex);
		}

	}


	/**
	 * @return the nodeIP
	 * @throws JumbuneException
	 */
	public String getNodeIP() throws JumbuneException {
		if (nodeIP == null) {
			throw new IllegalArgumentException("Node IP not set");
		}
		return nodeIP;
	}

	/**
	 * @param nodeIP
	 *            the nodeIP to set
	 */
	public void setNodeIP(String nodeIP) {
		this.nodeIP = nodeIP;
	}

	/**
	 * @return the ttPort
	 * @throws JumbuneException
	 */
	public String getTtPort() throws JumbuneException {
		if (ttPort == null) {
			throw new IllegalArgumentException("Tasktacker port not set");
		}
		return ttPort;
	}

	/**
	 * @param ttPort
	 *            the ttPort to set
	 */
	public void setTtPort(String ttPort) {
		this.ttPort = ttPort;
	}

	/**
	 * @return the jobId
	 * @throws JumbuneException
	 */
	public String getJobId() throws JumbuneException {
		if (ttPort == null) {
			throw new IllegalArgumentException("Job Id not set");
		}
		return jobId;
	}

	/**
	 * @param jobId
	 *            the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

}