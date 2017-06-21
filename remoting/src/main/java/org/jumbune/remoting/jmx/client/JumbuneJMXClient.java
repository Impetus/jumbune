package org.jumbune.remoting.jmx.client;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.remoting.jmx.common.JMXStats;

/**
 * The Class JumbuneJMXClient.
 */
public class JumbuneJMXClient {

	/** The Constant PORT. */
	public static final int PORT = 2507;
	
	public static final int MAX_ATTEMPTS = 3;

	/** The Constant DAEMON_IDENTIFIER. */
	private static final String DAEMON_IDENTIFIER = "DAEMON=";
	
	private static final String OS_IDENTIFIER  = "OS=" ;
	
	/** The logger. */
	private static final Logger LOGGER = LogManager.getLogger(JumbuneJMXClient.class);
	
	
	
	
	public Map<String, String> fetchJmxStatsMap(String serverHost, String daemonName) throws ClassNotFoundException {
		List<JMXStats> stats = fetchJmxStats(serverHost, daemonName);
		Map<String, String> result = new HashMap<>();
		Map<String, Object> statsSubset = java.util.Collections.emptyMap();
		for (JMXStats jmxStats : stats) {
			statsSubset = jmxStats.getAttributeMap();
			for (Entry<String, Object> entry : statsSubset.entrySet()) {
	           try {
				result.put(entry.getKey(), entry.getValue().toString());
	           }catch (Exception e){
	        	   //killing them softly....
	           }
			}
		}
		return result;
	}	
	
	
	/**
	 * Fetch jmx stats from a machine on which a hadoop daemon is running.
	 *
	 * @param serverHost the server host
	 * @param daemonName the daemon name
	 * @return the list
	 * @throws ClassNotFoundException the class not found exception
	 */
	public List<JMXStats> fetchJmxStats(String serverHost, String daemonName) throws ClassNotFoundException {
		List<JMXStats> stats = fetchStats(serverHost, daemonName);
		if(stats == null) {		 
			int retryInterval = 100;
			for(int i = 0; i < MAX_ATTEMPTS && stats == null; i++) {
				LOGGER.warn("Got JMXStats for (" + daemonName +", "+serverHost+") = null. Retrying in "+retryInterval+" ms,  attempt - " + (i+1));
				try {
					Thread.sleep(retryInterval);
				} catch (InterruptedException e) {
				LOGGER.error("Error while retrying to fetch JMX stats", e.getMessage());
				}
				stats = fetchStats(serverHost, daemonName);
				retryInterval += retryInterval*(i+1);
			}
		}
		return stats;
	}
	
	@SuppressWarnings("unchecked")
	private List<JMXStats> fetchStats(String serverHost, String daemonName) throws ClassNotFoundException {
		Socket client = null;
		List<JMXStats> stats = null;
		try {
			client = new Socket(serverHost, PORT);
			LOGGER.debug("JMX Agent[" + client.getRemoteSocketAddress()+"], fetching stats of " + daemonName);
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			out.writeObject(DAEMON_IDENTIFIER + daemonName);
			InputStream inFromServer = client.getInputStream();
			ObjectInputStream in = new ObjectInputStream(inFromServer);
			stats = (List<JMXStats>) in.readObject();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (client != null)
					client.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return stats;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getOSStats(String serverHost, String daemonName) throws ClassNotFoundException {
		Socket client = null;

		Map<String, Object> statsMap = null;
		try {
			client = new Socket(serverHost, PORT);
			LOGGER.debug("JMX Agent[" + client.getRemoteSocketAddress() + "], fetching stats of " + daemonName);
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			out.writeObject(OS_IDENTIFIER + daemonName);
			InputStream inFromServer = client.getInputStream();
			ObjectInputStream in = new ObjectInputStream(inFromServer);
			statsMap = (Map<String, Object>) in.readObject();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (client != null)
					client.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return statsMap;

	}

		
}