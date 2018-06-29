package org.jumbune.remoting.common;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

/**
 * The Class CuratorConnector. This class is responsible for communicating to zookeeper using curator apis.
 * Being Singleton, it defines {@code getInstance(String hosts)} method to acquire its instance. Besides, it 
 * also provides utility methods to perform various operations like getChildren, getData, setData, createPath, deletePath etc on zookeeper.</br></br>
 * It also guarantees that client connections to zookeeper, made through this class will never exceed {@code MAX_CONNECTIONS} which 
 * currently has a value of 20 and is not configurable.
 */
public class CuratorConnector {

	private static final String COMMA = ",";

	/** The max connections. */
	private final int MAX_CONNECTIONS = 20;
	
	/** The connection queue. */
	private BlockingQueue<CuratorFramework> connectionQueue = new LinkedBlockingQueue<>(MAX_CONNECTIONS);
	
	/** The base sleep time millis. */
	private final int BASE_SLEEP_TIME_MILLIS = 1000;
	
	/** The max retries. */
	private final int MAX_RETRIES = 3;
	
	/** The retry policy. */
	private RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME_MILLIS, MAX_RETRIES);

	/** The connector. */
	private static CuratorConnector connector;

	/** The hosts. */
	private String hosts;

	/**
	 * private constructor to prevent instantiation through constructor.
	 *
	 * @param hosts the hosts
	 */
	private CuratorConnector(String hosts) {
     this.hosts = hosts;   
	}
	
	/**
	 * Gets the single instance of CuratorConnector.
	 *
	 * @param hosts the hosts
	 * @return single instance of CuratorConnector
	 */
	public static CuratorConnector getInstance(String hosts) {
		if (connector == null) {
			synchronized (CuratorConnector.class) {
				if (connector == null) {
					connector = new CuratorConnector(hosts);
				}
			}
		}		
		return connector;
	}


	public static CuratorConnector getInstance(String[] hosts) {
		if (connector == null) {
			synchronized (CuratorConnector.class) {
				if (connector == null) {
					connector = new CuratorConnector(convertArrayToCSV(hosts));
				}
			}
		}		
		return connector;
	}
	
	private static String convertArrayToCSV(String[] hosts) {
		int length = hosts.length;
		StringBuilder hostBuilder = null;
		if (length == 1) {
			return hosts[0].trim();
		} else if (length > 1) {
			hostBuilder = new StringBuilder(hosts[0]);
			for (int i = 2; i < length; i++) {
				hostBuilder.append(COMMA).append(hosts[i]);
			}
		} else {
			return null;
		}

		return hostBuilder.toString();
	}
	
	/**
	 * Gets the or create client.
	 *
	 * @return the or create client
	 * @throws Exception the exception
	 */
	private synchronized CuratorFramework getOrCreateClient() throws Exception {
		CuratorFramework client;
		if((client = connectionQueue.peek()) !=null){ 
			return client;
		}else {
			client = CuratorFrameworkFactory.newClient(hosts, retryPolicy);
			connectionQueue.put(client);
			return connectionQueue.take();
		}
	}
	
	
	/**
	 * Creates the path.
	 *
	 * @param path the path
	 * @throws Exception the exception
	 */
	public void createPath(String path) throws Exception {
		CuratorFramework client = getOrCreateClient();
		client.start();		
		client.create().creatingParentsIfNeeded().forPath(path);
		client.close();
	}
	
	/**
	 * Delete path.
	 *
	 * @param path the path
	 * @throws Exception the exception
	 */
	public void deletePath(String path) throws Exception {
		CuratorFramework client = getOrCreateClient();
		client.start();
		client.delete().deletingChildrenIfNeeded().forPath(path); 
		client.close();
	}
	
	/**
	 * Sets the data.
	 *
	 * @param path the path
	 * @param data the data
	 * @throws Exception the exception
	 */
	public void setData(String path, String data) throws Exception {
		CuratorFramework client = getOrCreateClient();
		client.start();
		client.setData().forPath(path, data.getBytes());
		client.close();
	}
	
	/**
	 * Sets the data.
	 *
	 * @param path the path
	 * @param data the data
	 * @throws Exception the exception
	 */
	public void setData(String path, byte[] data) throws Exception {
		CuratorFramework client = getOrCreateClient();
		client.start();
		client.setData().forPath(path, data);
		client.close();
	}
	
	/**
	 * Gets the data.
	 *
	 * @param path the path
	 * @return the data
	 * @throws Exception the exception
	 */
	public String getData(String path) throws Exception {
		CuratorFramework client = getOrCreateClient();
		client.start();
		String data = new String(client.getData().forPath(path), StandardCharsets.UTF_8);
		client.close();
		return data;
	}
	
	public boolean exists(String path) throws Exception {
		CuratorFramework client = getOrCreateClient();
		client.start();
		Stat stat = client.checkExists().forPath(path);
		client.close();
		return stat != null;
	}
	
	public Stat getZNodeStats(String path) throws Exception {
		CuratorFramework client = getOrCreateClient();
		client.start();
		Stat stat = client.checkExists().forPath(path);
		client.close();
		return stat;
	}

	
	/**
	 * Gets the data.
	 *
	 * @param path the path
	 * @return the data
	 * @throws Exception the exception
	 */
	public byte[] getDataBytes(String path) throws Exception {
		CuratorFramework client = getOrCreateClient();
		client.start();
		byte[] data = client.getData().forPath(path);
		client.close();
		return data;
	}
	
	
	/**
	 * Gets the children.
	 *
	 * @param path the path
	 * @return the children
	 * @throws Exception the exception
	 */
	public List<String> getChildren(String path) throws Exception {
		CuratorFramework client = getOrCreateClient();
		client.start();
		List<String> children = client.getChildren().forPath(path);
		client.close();
		return children;
	}
	
	public CuratorZookeeperClient getZookeeperClient() throws Exception {
         CuratorFramework client = getOrCreateClient();
         client.start();
         CuratorZookeeperClient zkClient = client.getZookeeperClient();
         client.close();
         return zkClient;
		
	}
/*	public createEphemeralSequentialZNode(String path) {
		CuratorFramework client = getOrCreateClient();
		client.start();	
		PersistentEphemeralNode ephemeralNode = new PersistentEphemeralNode(client, Mode.EPHEMERAL_SEQUENTIAL, path, new byte[0]);
	}*/

	
	
}
