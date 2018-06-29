package org.jumbune.remoting.jmx.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jumbune.remoting.jmx.common.JMXStats;

/**
 * The Class JumbuneJMXServer.
 */
public class JumbuneJMXServer {

	/** The Constant JUMBUNE_JMX_SERVER_PORT. */
	public static final int JUMBUNE_JMX_SERVER_PORT = 2507;

	/** The Constant DAEMON_IDENTIFIER. */
	private static final String DAEMON_IDENTIFIER = "DAEMON=";

	private static final String SHUTDOWN_SERVER = "SHUTDOWN_SERVER";

	private static final String OS_IDENTIFIER = "OS=";

	/** The server socket. */
	private ServerSocket serverSocket = null;

	/**
	 * Instantiates a new jumbune jmx server.
	 */
	public JumbuneJMXServer() {
		try {
			serverSocket = new ServerSocket(JUMBUNE_JMX_SERVER_PORT);
		} catch (IOException e) {
			System.out.println("Cannot start JMX Agent due to " + e.getMessage());
		}
	}

	public void start() {
		Socket server = null;
		while (serverSocket != null && serverSocket.isBound()) {
			try {
				System.out.println("Waiting for client on port - " + serverSocket.getLocalPort());
				server = serverSocket.accept();
				System.out.println("Connected Established - " + server.getRemoteSocketAddress());
				ObjectInputStream in = new ObjectInputStream(server.getInputStream());

				String message = (String) in.readObject();
				ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());

				if (message.contains(DAEMON_IDENTIFIER)) {
					out.writeObject(getJMXStats(message));
				} else if (message.equals(SHUTDOWN_SERVER)) {
					close(server);
				} else if (message.contains(OS_IDENTIFIER)) {
					ObjectName objectName = null;
					try {
						String objOS = "java.lang:type=OperatingSystem";
						objectName = new ObjectName(objOS);
					} catch (MalformedObjectNameException e) {
						System.err.println("Unable to create the object");
					}
					out.writeObject(getOSJmxStats(message, objectName));

				}
			} catch (SocketTimeoutException s) {
				System.err.println("Socket timed out!!!!!");
				break;
			} catch (IOException | ClassNotFoundException e) {
				System.err.println("Error occured due to " + e.getMessage());
				break;
			} finally {
				close(server);
			}
		}
	}

	private void close(Socket server) {
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				System.err.println("Error closing server connection " + e.getMessage());
			}
		}
	}

	private List<JMXStats> getJMXStats(String message) {
		JMXStatsUtil statsUtil = new JMXStatsUtil();
		String daemonName = message.split(DAEMON_IDENTIFIER)[1];
		String pid = null;
		try {
			pid = statsUtil.findPidByDaemonNameUsingPS(daemonName);
		} catch (IOException e1) {
			System.err.println("Unable to find daemon - " + daemonName);
		}
		try {
			MBeanServerConnection conn = statsUtil.getMBeanServerConnection(pid);
			return statsUtil.getAssociatedMBeans(conn, daemonName);
		} catch (Exception e) {
			System.err.println("Error occured due to " + e);
		}
		return null;
	}

	private Map<String, Object> getOSJmxStats(String message, ObjectName objName) {
		JMXStatsUtil statsUtil = new JMXStatsUtil();
		Map<String, Object> statsMap = null;
		String daemonName = message.split(OS_IDENTIFIER)[1].split(" OS")[0];
		String pid = null;
		try {
			pid = statsUtil.findPidByDaemonNameUsingPS(daemonName);
		} catch (IOException e1) {
			System.err.println("Unable to find daemon - " + daemonName);
		}
		try {
			MBeanServerConnection conn = statsUtil.getMBeanServerConnection(pid);
			statsMap = statsUtil.getAndSetOSJmxStats(conn, objName);
		} catch (Exception e) {
			System.err.println("Error occured due to " + e);
		}
		return statsMap;

	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		JumbuneJMXServer server = new JumbuneJMXServer();
		server.start();

	}

}