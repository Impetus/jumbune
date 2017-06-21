package org.jumbune.remoting.jmx.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.jumbune.remoting.jmx.common.JMXStats;

import com.sun.tools.attach.VirtualMachine;

/**
 * The Class JMXStatsUtil.
 */
public class JMXStatsUtil {

	/** The Constant LOCAL_CONNECTOR_ADDRESS. */
	private static final String LOCAL_CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";

	/** The Constant MANAGEMENT_AGENT_JAR. */
	private static final String MANAGEMENT_AGENT_JAR = "management-agent.jar";

	/** The Constant JAVA_HOME_PROP. */
	private static final String JAVA_HOME_PROP = "java.home";

	/** The Constant LIB. */
	private static final String LIB = "lib";

	/** The Constant THRESHOLD. */
	private static final String THRESHOLD = "Threshold";

	/** The Constant JAVA_HOME. */
	private static final String JAVA_HOME = "java.home";

	/** The Constant JPS. */
	private static final String JPS = "/bin/jps";

	/** The Constant STRING_REGEX. */
	private static final String STRING_REGEX = "\\s+";

	private static final String NAME = "name";

	private static final String DOT = ".";

	private static final String COMMAND_PREFIX = "ps -ef | awk '/\\.";

	private static final String COMMAND_SUFFIX = "/{print $0}'";

	/**
	 * Gets the mbeans associated with this instance of MBeanServerConnection
	 *
	 * @param mbsc
	 *            the mbsc
	 * @param objectNamePrefix
	 *            the object name prefix
	 * @return the associated m beans
	 */
	public List<JMXStats> getAssociatedMBeans(MBeanServerConnection mbsc, String objectNamePrefix) {
		System.out.println("fetching mbeans with object prefix " + objectNamePrefix);
		List<JMXStats> statsLists = new ArrayList<>();
		try {
			Map<String, Object> attribMap = null;
			JMXStats stats = null;
			Set<ObjectName> mbeans = mbsc.queryNames(null, null);
			for (ObjectName name : mbeans) {
				attribMap = new HashMap<String, Object>();
				if (name.toString().contains(objectNamePrefix)) {
					stats = new JMXStats();
					MBeanAttributeInfo[] attributeInfos = mbsc.getMBeanInfo(name).getAttributes();
					// populating all the associated attributes
					for (MBeanAttributeInfo info : attributeInfos) {
						// some beans do not support threshold props,
						// consequently
						// throw an UnsupportedOperationException
						if (!info.getName().contains(THRESHOLD)) {
							attribMap.put(name.getKeyProperty(NAME) + DOT + info.getName(),
									mbsc.getAttribute(name, info.getName()));
						}
					}
					stats.setObjectNamePrefix(name.toString());
					stats.setAttributeMap(attribMap);
					statsLists.add(stats);
				}
			}
		} catch (Exception e) {
			System.out.println("Error occured due to " + e.getMessage());
		}
		return statsLists;
	}

	/**
	 * Gets the m bean server connection.
	 *
	 * @param processId
	 *            the process id
	 * @return the m bean server connection
	 */
	public MBeanServerConnection getMBeanServerConnection(String processId) {
		VirtualMachine vm = null;
		JMXConnector connector = null;
		MBeanServerConnection conn = null;
		try {
			vm = VirtualMachine.attach(processId);
			String connectorAddr = vm.getAgentProperties().getProperty(LOCAL_CONNECTOR_ADDRESS);
			if (connectorAddr == null) {
				String agent = vm.getSystemProperties().getProperty(JAVA_HOME_PROP) + File.separator + LIB
						+ File.separator + MANAGEMENT_AGENT_JAR;
				vm.loadAgent(agent);
				connectorAddr = vm.getAgentProperties().getProperty(LOCAL_CONNECTOR_ADDRESS);
			}
			JMXServiceURL serviceURL = new JMXServiceURL(connectorAddr);
			connector = JMXConnectorFactory.connect(serviceURL);
			conn = connector.getMBeanServerConnection();
		} catch (Exception e) {
			System.out.println("Error occured due to " + e.getMessage());
		}
		return conn;
	}

	/**
	 * Find pid by daemon name.
	 *
	 * @param daemonName
	 *            the daemon name
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Deprecated
	public String findPidByDaemonName(String daemonName) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		String javaHome = System.getProperty(JAVA_HOME);
		// path of jdk
		javaHome = javaHome.substring(0, javaHome.lastIndexOf(File.separator));
		Process p = runtime.exec(javaHome + JPS);
		String regex = "^\\d+\\s+" + daemonName + "\\s*";
		BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		for (String line = error.readLine(); line != null; line = error.readLine()) {
			System.out.println(line);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			if (line.matches(regex)) {
				System.out.println("found daemon: " + line);
				return line.split(STRING_REGEX)[0];
			}
		}
		if (br != null)
			br.close();
		return null;
	}

	public String findPidByDaemonNameUsingPS(String daemonName) throws IOException {

		Runtime runtime = Runtime.getRuntime();

		String finalCommand = COMMAND_PREFIX + daemonName + COMMAND_SUFFIX;

		Process p = runtime.exec(new String[] { "bash", "-c", finalCommand });

		BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		for (String line = error.readLine(); line != null; line = error.readLine()) {
			System.out.println(line);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			if (!line.contains("awk")) {
				return line.split(STRING_REGEX)[1];
			}

		}
		if (br != null)
			br.close();
		return null;
	}

	public Map<String, Object> getAndSetOSJmxStats(MBeanServerConnection conn, ObjectName objName) throws IOException,
			MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {

		Map<String, Object> statsMap = new HashMap<String, Object>();
		statsMap.put("AvailableProcessors", ((Integer) conn.getAttribute(objName, "AvailableProcessors")));
		statsMap.put("TotalPhysicalMemorySize", ((Long) conn.getAttribute(objName, "TotalPhysicalMemorySize")));
		statsMap.put("FreePhysicalMemorySize", ((Long) conn.getAttribute(objName, "FreePhysicalMemorySize")));
		statsMap.put("SystemCpuLoad", (conn.getAttribute(objName, "SystemCpuLoad")));
		return statsMap;
	}
}
