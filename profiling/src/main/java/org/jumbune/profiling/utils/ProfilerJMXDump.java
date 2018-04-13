package org.jumbune.profiling.utils;

import static org.jumbune.profiling.utils.ProfilerConstants.CPU_DETAILS_COMMAND;
import static org.jumbune.profiling.utils.ProfilerConstants.CPU_USAGE_COMMAND;
import static org.jumbune.profiling.utils.ProfilerConstants.CPU_USAGE_COMMAND_WITHOUT_CARET;
import static org.jumbune.profiling.utils.ProfilerConstants.EXECUTION_MODE;
import static org.jumbune.profiling.utils.ProfilerConstants.JMX_URL_POSTFIX;
import static org.jumbune.profiling.utils.ProfilerConstants.JMX_URL_PREFIX;
import static org.jumbune.profiling.utils.ProfilerConstants.JUMBUNE_CONTEXT_URL;
import static org.jumbune.profiling.utils.ProfilerConstants.NETWORK_LATENCY_COMMAND;
import static org.jumbune.profiling.utils.ProfilerConstants.PARTITION_LIST_COMMAND;
import static org.jumbune.profiling.utils.ProfilerConstants.PARTITION_STATS_COMMAND;
import static org.jumbune.profiling.utils.ProfilerConstants.READS_FROM_LOCAL_CLIENT;
import static org.jumbune.profiling.utils.ProfilerConstants.READS_FROM_REMOTE_CLIENT;
import static org.jumbune.profiling.utils.ProfilerConstants.READ_BLOCK_OP_MAX_TIME;
import static org.jumbune.profiling.utils.ProfilerConstants.RPC_PROCESSING_MAX_TIME;
import static org.jumbune.profiling.utils.ProfilerConstants.SERVICE_URL;
import static org.jumbune.profiling.utils.ProfilerConstants.VMSTAT_COMMAND;
import static org.jumbune.profiling.utils.ProfilerConstants.WRITE_BLOCK_OP_MAX_TIME;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ThreadInfo;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.common.utils.RemoteFileUtil.JumbuneUserInfo;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.beans.NodeInfo;
import org.jumbune.profiling.healthview.AdjacencyInfo;
import org.jumbune.profiling.healthview.DiskPartitionInfo;
import org.jumbune.profiling.healthview.NetworkLatencyInfo;
import org.jumbune.profiling.healthview.NodeDiskPartitionsInfo;
import org.jumbune.profiling.healthview.NodeThroughputInfo;
import org.jumbune.profiling.healthview.ResultInfo;
import org.jumbune.profiling.hprof.DFSEnum;
import org.jumbune.profiling.hprof.NodePerformance;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.jmx.client.JumbuneJMXClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;



/**
 * This class is used to fetch the JMX stats based on the pre-defined intervals through MXBean.
 * 
 */

public class ProfilerJMXDump {

	private static final String NAME = "name";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(ProfilerJMXDump.class);

	private static final String TAGCONTEXT = "tag.context";
	
	private static final String TAGHOSTNAME = "tag.hostName";
	
	private static final String TAGPORT = "tag.port";
	
	private static final String TAGSESSIONID = "tag.sessionId";

	private static final String LOGOUT = "logout";

	private static String weight = "weight";
	

	private Type mapType = new TypeToken<HashMap<String, String>>(){}.getType();


	/**
	 * Getting JMX parameters for different services are DATANODE,NAMENODE,TASKTRACKER. >>>>>>> Stashed changes
	 * 
	 * @param host
	 *            the host name
	 * @param port
	 *            identify the running port of the service
	 * @param service
	 *            identify the name of a running service
	 * @return the map containing all the JMX stats for a particular service
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws MBeanException
	 *             the m bean exception
	 * @throws ReflectionException
	 *             the reflection exception
	 * @throws IntrospectionException
	 *             the introspection exception
	 */
	@Deprecated
	public Map<String, String> getAllJmxStats(String host, String port, String service) throws IOException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException, IntrospectionException {

		JMXConnector connector = null;
		MBeanServerConnection connection = null;
		Map<String, String> serviceStats = null;
		JMXServiceURL url = new JMXServiceURL(JMX_URL_PREFIX + host + ":" + port + JMX_URL_POSTFIX);
		// cache is used here
		connector = JMXConnectorInstance.getJMXConnectorInstance(url);
		connection = connector.getMBeanServerConnection();
		String serviceUrl = SERVICE_URL + service;
		Set<ObjectName> names = connection.queryNames(null, null);
		String objectName;
		MBeanInfo info;
		MBeanAttributeInfo[] mbi;

		for (ObjectName objName : names) {
			objectName = objName.toString();
			if (JUMBUNE_CONTEXT_URL.equals(objectName) || (objectName.indexOf(serviceUrl) > -1)) {
				if (serviceStats == null) {
					serviceStats = new HashMap<String, String>();
				}
				info = connection.getMBeanInfo(objName);
				mbi = info.getAttributes();
				String name;

				for (int i = 0; i < info.getAttributes().length; i++) {
					name = mbi[i].getName();
					Object attributeValue = connection.getAttribute(objName, name);
					if (attributeValue != null) {
						serviceStats.put(getKeyName(objName, name), String.valueOf(attributeValue));
					}

				}
			}
		}
		return serviceStats;
	}

	private String getKeyName(ObjectName objectName,  String name) {
		return objectName.getKeyProperty(NAME)+"."+name;
	}

	/**
	 * Getting JMX parameters for different services are DATANODE,NAMENODE,TASKTRACKER. >>>>>>> Stashed changes
	 * 
	 * @param host
	 *            the host name
	 * @param port
	 *            identify the running port of the service
	 * @param service
	 *            identify the name of a running service
	 * @return the map containing all the JMX stats for a particular service
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws MBeanException
	 *             the m bean exception
	 * @throws ReflectionException
	 *             the reflection exception
	 * @throws IntrospectionException
	 *             the introspection exception
	 */
	public Map<String, String> getAllJMXStats(JMXDeamons jmxDaemon, String host, String port, boolean jmxPluginEnabled) throws IOException,
			AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IntrospectionException {
	
		if (jmxPluginEnabled) {
			JumbuneJMXClient client = new JumbuneJMXClient();
			try {
				return client.fetchJmxStatsMap(host, jmxDaemon.toString());
			} catch (ClassNotFoundException e) {
				LOGGER.error("error fetching jmx stats through plugin - " + e.getMessage());
			}
		}
		
		List<String> jmxAttributeList = new ArrayList<String>();
		JMXConnector connector = null;
		MBeanServerConnection connection = null;
		Map<String, String> serviceStats = null;
		JMXServiceURL url = new JMXServiceURL(JMX_URL_PREFIX + host + ":" + port + JMX_URL_POSTFIX);
		String serviceUrl = null;
		if (jmxDaemon == JMXDeamons.CLDB) {
			serviceUrl = ProfilerConstants.COM_MAPR_CLDB + ProfilerConstants.HADOOP_SERVICE_URL + jmxDaemon;
		} else {
			serviceUrl = ProfilerConstants.HADOOP + ProfilerConstants.HADOOP_SERVICE_URL + jmxDaemon;
		}
		
		connector = JMXConnectorInstance.getJMXConnectorInstance(url);
		connection = connector.getMBeanServerConnection();
		Set<ObjectName> names = connection.queryNames(null, null);
		String objectName;
		MBeanInfo info;
		MBeanAttributeInfo[] mbi;

		for (ObjectName objName : names) {
			objectName = objName.toString();
			if(!objectName.contains("ContainerResource_container_")){
			if (ProfilerConstants.JUMBUNE_CONTEXT_URL.equals(objectName) || (objectName.indexOf(serviceUrl) > -1)) {
				if (serviceStats == null) {
					serviceStats = new HashMap<String, String>();
				}
				info = connection.getMBeanInfo(objName);
				mbi = info.getAttributes();
				String name = null;
				for (int i = 0; i < info.getAttributes().length; i++) {
					name = mbi[i].getName();
					jmxAttributeList.add(name);
					Object attributeValue = connection.getAttribute(objName, name);
					if (attributeValue != null) {
						serviceStats.put(objName.getKeyProperty(NAME)+"."+name, String.valueOf(attributeValue));
					}
					if("".equals(attributeValue) || "[]".equals(attributeValue)){
						serviceStats.put(getKeyName(objName, name), "-");
					}

				}
			}
		}}
		return serviceStats;

	}

	public Map<String, Object> getOSJMXStats(JMXDeamons jmxDaemon, String host, String port, boolean jmxPluginEnabled) throws IOException,
	AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IntrospectionException {
		
		if (jmxPluginEnabled) {
			JumbuneJMXClient client = new JumbuneJMXClient();
			try{
				return client.getOSStats(host, jmxDaemon.toString()+Constants.OS_IDENTIFIER);
			}catch(ClassNotFoundException e){
				LOGGER.error("error fetching jmx stats through plugin - " + e.getMessage());
			}
		}

	List<String> jmxAttributeList = new ArrayList<String>();
	JMXConnector connector = null;
	MBeanServerConnection connection = null;
	Map<String, Object> serviceStats = null;
	JMXServiceURL url = new JMXServiceURL(JMX_URL_PREFIX + host + ":" + port + JMX_URL_POSTFIX);
	String serviceUrl = ProfilerConstants.OS_URL;
	connector = JMXConnectorInstance.getJMXConnectorInstance(url);
	connection = connector.getMBeanServerConnection();
	Set<ObjectName> names = connection.queryNames(null, null);
	String objectName;
	MBeanInfo info;
	MBeanAttributeInfo[] mbi;
	
	for (ObjectName objName : names) {
		objectName = objName.toString();
		if(objectName.indexOf(serviceUrl) > -1) {
			if (serviceStats == null) {
				serviceStats = new HashMap<String, Object>();
			}
			info = connection.getMBeanInfo(objName);
			mbi = info.getAttributes();
			String name = null;
			for (int i = 0; i < info.getAttributes().length; i++) {
				name = mbi[i].getName();
				jmxAttributeList.add(name);
				Object attributeValue = connection.getAttribute(objName, name);
				if (attributeValue != null) {
					serviceStats.put(name, String.valueOf(attributeValue));
				}
				if("".equals(attributeValue) || "[]".equals(attributeValue)){
					serviceStats.put(getKeyName(objName, name), "-");
				}
	
			}
		}
	}
	return serviceStats;
	
	}
	/***
	 * This method calculate list of JMX attribute exposed by a given hadoop cluster.
	 * 
	 * @param jmxDaemon
	 * @param hadoopVersion
	 * @param host
	 * @param port
	 * @return list of calculate hadoop exposed jmx attribute.
	 * @throws IOException
	 * @throws AttributeNotFoundException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IntrospectionException
	 */
	public List<String> getAllJMXAttribute(JMXDeamons jmxDaemon, String host, String port, boolean jmxPluginEnabled) throws IOException,
			AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IntrospectionException {
		List<String> jmxAttributeList = new ArrayList<String>();
		JumbuneJMXClient client = new JumbuneJMXClient();
		if (jmxPluginEnabled) {
			try {
				Map<String, String> jmxStats = client.fetchJmxStatsMap(host, jmxDaemon.toString());
				for(Entry<String, String> entry : jmxStats.entrySet()) {
					jmxAttributeList.add(entry.getKey());
				}
			} catch (ClassNotFoundException e) {
				LOGGER.error("error fetching jmx stats through plugin - " + e.getMessage());
			}
		  return jmxAttributeList;
		}
		
		JMXConnector connector = null;
		MBeanServerConnection connection = null;
		String objectName;
		MBeanInfo info;
		MBeanAttributeInfo[] mbi;

		JMXServiceURL url = new JMXServiceURL(JMX_URL_PREFIX + host + ":" + port + JMX_URL_POSTFIX);
		String serviceUrl = ProfilerConstants.HADOOP+ ProfilerConstants.HADOOP_SERVICE_URL + jmxDaemon;
		connector = JMXConnectorInstance.getJMXConnectorInstance(url);
		connection = connector.getMBeanServerConnection();
		Set<ObjectName> names = connection.queryNames(null, null);
		for (ObjectName objName : names) {
			objectName = objName.toString();
			if(!objectName.contains("ContainerResource_container_")){
			if (ProfilerConstants.JUMBUNE_CONTEXT_URL.equals(objectName) || (objectName.indexOf(serviceUrl) > -1)) {
				info = connection.getMBeanInfo(objName);
				mbi = info.getAttributes();
				String name = null;
				for (int i = 0; i < info.getAttributes().length; i++) {
					name = mbi[i].getName();
					jmxAttributeList.add(getKeyName(objName, name));
				}
			}
		}}
		return suppressMultipleOccurrenceAttributes(jmxAttributeList);
		

	}

	/**
	 * suppressing some tag attributes to be added in list due to multiple entries from mbean info
	 * @param jmxAttributeList
	 * @return
	 */
	private List<String> suppressMultipleOccurrenceAttributes(
			List<String> jmxAttributeList) {
		
		List<String> resultList = jmxAttributeList;
		List<String> suppressedAttributesList = new ArrayList<String>(Constants.FOUR);
		suppressedAttributesList.add(TAGCONTEXT);
		suppressedAttributesList.add(TAGHOSTNAME);
		suppressedAttributesList.add(TAGPORT);
		suppressedAttributesList.add(TAGSESSIONID);
		
		resultList.removeAll(suppressedAttributesList);
		return resultList;
	}

	/**
	 * For creating a JSCH session.
	 * 
	 * @param user
	 *            the user
	 * @param host
	 *            the host
	 * @param rsaFilePath
	 *            the rsa file path
	 * @param dsaFilePath
	 *            the dsa file path
	 * @return a JSCH session
	 * @throws JSchException
	 *             the j sch exception
	 */
	public Session createSession(String user, String host, String sshAuthKeyFilePath) throws JSchException {
		JSch jsch = new JSch();
		File sshAuthKeyFile = new File(sshAuthKeyFilePath);
		Session session = null;

		if (sshAuthKeyFile.exists()) {
			jsch.addIdentity(sshAuthKeyFile.getAbsolutePath());
		}
		session = jsch.getSession(user, host, ProfilerConstants.TWENTY_TWO);
		return session;
	}

	/**
	 * Gets vmstats for a particular node.
	 * 
	 * @param session
	 *            the session <<<<<<< Updated upstream
	 * @return A map containing vmstats attributes and their corresponding values =======
	 * @return A map containing vmstats attributes and their corresponding values >>>>>>> Stashed changes
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Map<String, String> getVmStats(Session session) throws JSchException, IOException {
		Channel channel = getChannel(session, VMSTAT_COMMAND);
		String line;
		String lineArray[];
		InputStream inputStream = null;
		BufferedReader bufferReader = null;
		Map<String, String> vmStats = null;

		try {
			inputStream = channel.getInputStream();
			bufferReader = new BufferedReader(new InputStreamReader(inputStream));
			vmStats = new HashMap<String, String>();
			while ((line = bufferReader.readLine()) != null) {
				line = line.trim();
				lineArray = line.split(" ");
				int len = lineArray.length;
				if (len < ProfilerConstants.THREE) {
					vmStats.put(lineArray[1], lineArray[0]);
				} else {
					StringBuffer sb = new StringBuffer(lineArray[2]);
					if (len > ProfilerConstants.THREE) {
						for (int i = ProfilerConstants.THREE; i < len; i++) {
							sb.append(lineArray[i]);
						}
					}
					vmStats.put(sb.toString(), lineArray[0]);
				}
			}
		} finally {
			
			if (bufferReader != null) {
				bufferReader.close();
			}
			channel.disconnect();
			session.disconnect();
		}
		return vmStats;
	}

	/**
	 * Gets the remote vm stats.
	 * 
	 * @param response
	 *            the response
	 * @return the remote vm stats
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Map<String, String> getRemoteVmStats(String response) throws JSchException, IOException {
		String[] words, lines = response.split(System.lineSeparator());
		Map<String, String> vmStats = new HashMap<String, String>();
		int unit, startingIndex, i;
		long finalValue;
		StringBuffer sb;
		
		//Parsing each line
		for (String line : lines) {
			// extract each word
			words = line.trim().split("\\s+");
			// Checking if total words should be greater than 2 and first word should be a number
			if ( words.length < 2 || ! StringUtils.isNumeric(words[0])) {
				continue;
			}
			// Checking  if the 2nd word represents the unit (ie. k, K , m or M) of that number
			unit = getValue(words[1]);
			// Creating stat name, if 2nd word is unit then start appending all the words from 3rd word, otherwise from 2nd word
			startingIndex = (unit == 1) ? 1 : 2;
			sb = new StringBuffer();
			for (i = startingIndex; i < words.length; i++) {
				sb.append(WordUtils.capitalize(words[i]));
			}
			// Get final value by multiplying it with its unit
			finalValue = Long.parseLong(words[0]) * unit;
			// Putting stat name and its value into map
			vmStats.put(sb.toString(), String.valueOf(finalValue));
		}
		return vmStats;
	}
	
	private int getValue(String unit) {
		switch (unit) {
			case "K" :
				return 1024;
			case "M" :
				return 1048576;
			case "k" :
				return 1000;
			case "m" :
				return 1000000;
			default :
				return 1;
		}
	}

	/**
	 * Gets the list of partitions on the disk.
	 * 
	 * @param session
	 *            the Session
	 * @return the list of partitions on the disk
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private List<DiskPartitionInfo> getPartitionStats(Session session) throws JSchException, IOException {

		Channel channel = getChannel(session, PARTITION_LIST_COMMAND);
		InputStream in = null;
		List<DiskPartitionInfo> partitions = null;

		try {
			in = channel.getInputStream();
			partitions = new ArrayList<DiskPartitionInfo>();
			ResultParser resultParser = new ResultParser();
			resultParser.parseDiskPartitionResult(in, partitions);
		} finally {
			if (in != null) {
				in.close();
			}
			channel.disconnect();
		}
		getVmStatsForPartition(session, partitions);
		return partitions;
	}


	/**
	 * Gets VM stats for a partition of the disk.
	 * 
	 * @param session
	 *            the session
	 * @param partitionStats
	 *            the partition stats
	 * @return VM stats for a partition of the disk
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void getVmStatsForPartition(Session session, List<DiskPartitionInfo> partitionStats) throws JSchException, IOException {

		StringBuffer sb = new StringBuffer();
		for (DiskPartitionInfo diskPartitionInfo : partitionStats) {
			sb.append(PARTITION_STATS_COMMAND).append(diskPartitionInfo.getName()).append(";");
		}

		Channel channel = getChannel(session, sb.toString());
		InputStream in = null;
		try {
			in = channel.getInputStream();
			ResultParser resultParser = new ResultParser();
			resultParser.parseVmStatsPartitionResult(in, partitionStats);
		} finally {
			if (in != null) {
				in.close();
			}
			channel.disconnect();
		}
	}



	/**
	 * Gets the information related to memory utilization for each node.
	 * 
	 * @param config
	 *            the config
	 * @param nodeIp
	 *            the node ip
	 * @return list of pojos containing memory utilization info for each node
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Map<String, String> getMemoryUtilisation(Cluster cluster, String nodeIp) throws JSchException, IOException {
		
		String user = cluster.getWorkers().getUser();
		String sshAuthKeyFilePath = cluster.getAgents().getSshAuthKeysFile();
		Session session = null;
		Map<String, String> vmStats = null;
		try {
			session = createSession(user, nodeIp, sshAuthKeyFilePath);
			UserInfo ui = new JumbuneUserInfo();
			session.setUserInfo(ui);
			session.connect();
			vmStats = getVmStats(session);
		} finally {
			session.disconnect();
		}
		return vmStats;
	}

	/**
	 * Gets the remote memory utilisation.
	 * 
	 * @param config
	 *            the config
	 * @param nodeIP
	 *            the node ip
	 * @return the remote memory utilisation
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Map<String, String> getRemoteMemoryUtilisation(Cluster cluster, String nodeIP) throws JSchException, IOException {
		List<String> hosts;
		if (nodeIP == null) {
			hosts = cluster.getWorkers().getHosts();
		} else {
			hosts = new ArrayList<String>();
			hosts.add(nodeIP);
		}
		Map<String, String> vmStats = null;

		Remoter remoter = RemotingUtil.getRemoter(cluster);

		for (String host : hosts) {
			
			CommandWritableBuilder builder = new CommandWritableBuilder(cluster, host);
			builder.addCommand(VMSTAT_COMMAND, false, null, CommandType.FS);
			String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
			if (response == null || "".equals(response.trim())){
				LOGGER.error("Invalid response!!!");
			}
			else{
				vmStats = getRemoteVmStats(response);
			}
		}
		remoter.close();
		return vmStats;
	}

	
	public String getFreeMemoryResponse(Cluster cluster, String nodeIP)throws JSchException, IOException{
		List<String> hosts;
		if (nodeIP == null) {
			hosts = cluster.getWorkers().getHosts();
		} else {
			hosts = new ArrayList<String>();
			hosts.add(nodeIP);
		}
		String response = null;
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		for (String host : hosts) {
			
			CommandWritableBuilder builder = new CommandWritableBuilder(cluster, host);
			builder.addCommand("free -m && exit", false, null, CommandType.FS);
			response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
			if (response == null || "".equals(response.trim())){
				LOGGER.error("Invalid response!!!");
			}
		}
		remoter.close();
		return response;
	}
	
	
	
	
	/**
	 * 
	 * 
	 * @param config
	 *            the config
	 * @param nodeIp
	 *            the node ip
	 * @return the partition efficiency
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	// TODO: Note used anywhere
	@Deprecated
	public List<ResultInfo> getPartitionEfficiency(Cluster cluster, String nodeIp) throws JSchException, IOException {

		List<String> hosts;
		if (nodeIp == null) {
			hosts = cluster.getWorkers().getHosts();
		} else {
			hosts = new ArrayList<String>();
			hosts.add(nodeIp);
		}
		String user = cluster.getWorkers().getUser();
		String sshAuthKeyFilePath = cluster.getAgents().getSshAuthKeysFile();
		Session session = null;
		UserInfo ui;
		NodeDiskPartitionsInfo ndpi = null;
		List<DiskPartitionInfo> partitionStats = null;
		List<ResultInfo> nodeInfoList = new ArrayList<ResultInfo>();

		for (String host : hosts) {
			try {
				session = createSession(user, host, sshAuthKeyFilePath);
				ui = new JumbuneUserInfo();
				session.setUserInfo(ui);
				session.connect();
				partitionStats = getPartitionStats(session);
				ndpi = new NodeDiskPartitionsInfo();
				ndpi.setDiskPartitionsList(partitionStats);
				nodeInfoList.add(ndpi);
			} finally {
				if (session != null) {
					session.disconnect();
				}
			}
		}

		return nodeInfoList;
	}

	// TODO: Note used anywhere
	/**
	 * Gets the remote partition efficiency.
	 * 
	 * @param config
	 *            the config
	 * @param nodeIp
	 *            the node ip
	 * @return the remote partition efficiency
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Deprecated
	public List<ResultInfo> getRemotePartitionEfficiency(Config config, String nodeIp) throws JSchException, IOException {
		
		List<ResultInfo> nodeInfoList = new ArrayList<ResultInfo>();

		return nodeInfoList;
	}

	/**
	 * Calculates read,write and speed throughput for all nodes of the cluster.
	 * 
	 * @param config
	 *            the config
	 * @param nodeIp
	 *            the node ip
	 * @return the node throughput
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws IntrospectionException
	 *             the introspection exception
	 * @throws MBeanException
	 *             the m bean exception
	 * @throws ReflectionException
	 *             the reflection exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<ResultInfo> getNodeThroughput(Cluster cluster,  String nodeIp) throws AttributeNotFoundException,
			InstanceNotFoundException, IntrospectionException, MBeanException, ReflectionException, IOException {

		List<String> hosts;
		if (nodeIp == null) {
			hosts = cluster.getWorkers().getHosts();
		} else {
			hosts = new ArrayList<String>();
			hosts.add(nodeIp);
		}
		List<ResultInfo> nodesThroughput = new ArrayList<ResultInfo>();
		Map<String, String> allJmxStats;
		NodeThroughputInfo throughput;

		for (String host : hosts) {
			throughput = new NodeThroughputInfo();
			allJmxStats = getAllJMXStats(JMXDeamons.DATA_NODE,  host, cluster.getWorkers().getDataNodeJmxPort(), cluster.isJmxPluginEnabled());
			throughput.setReadThroughput(Integer.parseInt(allJmxStats.get(READ_BLOCK_OP_MAX_TIME)));
			throughput.setWriteThroughput(Integer.parseInt(allJmxStats.get(WRITE_BLOCK_OP_MAX_TIME)));
			allJmxStats = getAllJMXStats(JMXDeamons.TASK_TRACKER, host, cluster.getWorkers().getTaskExecutorJmxPort(), cluster.isJmxPluginEnabled());
			throughput.setProcessingThroughput(Integer.parseInt(allJmxStats.get(RPC_PROCESSING_MAX_TIME)));
			nodesThroughput.add(throughput);
		}
		return nodesThroughput;
	}

	/**
	 * Gets the local data usage for the node.
	 * 
	 * @param config
	 *            the config
	 * @param nodeIp
	 *            the node ip
	 * @return the local data usage
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws IntrospectionException
	 *             the introspection exception
	 * @throws MBeanException
	 *             the m bean exception
	 * @throws ReflectionException
	 *             the reflection exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public double getLocalDataUsage(Cluster cluster, String nodeIp) throws AttributeNotFoundException, InstanceNotFoundException,
			IntrospectionException, MBeanException, ReflectionException, IOException {

		long localReads;
		long remoteReads;
		double localDataUsage;
		Map<String, String> allJmxStats;
		allJmxStats = getAllJMXStats(JMXDeamons.DATA_NODE, nodeIp, cluster.getWorkers().getDataNodeJmxPort(), cluster.isJmxPluginEnabled());
		localReads = Long.parseLong(allJmxStats.get(READS_FROM_LOCAL_CLIENT));
		remoteReads = Long.parseLong(allJmxStats.get(READS_FROM_REMOTE_CLIENT));
		if ((remoteReads == 0) && (localReads == 0)) {
			localDataUsage = 0.0;
		} else {
			localDataUsage = (double) localReads / (localReads + remoteReads) * ProfilerConstants.HUNDRED;
		}

		return localDataUsage;
	}

	/**
	 * Tells the percentage data load of cluster on the node.
	 * 
	 * @param config
	 *            the config
	 * @param nodeIp
	 *            the node ip
	 * @param node
	 *            the node
	 * @return list of pojos containing data load info for each node
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws IntrospectionException
	 *             the introspection exception
	 * @throws MBeanException
	 *             the m bean exception
	 * @throws ReflectionException
	 *             the reflection exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public double getDataLoadonNodes(String nodeIp, NodeInfo node, Cluster cluster, String[] dataLoadResult) throws Exception {
		
		int numOfWorkerNodes = cluster.getWorkers().getHosts().size();
		double idealDataLoadPercent = 100 / numOfWorkerNodes;
		long totalDfsUsed = 0;
		long localDfsUsed = 0;
		double dataLoadPercent = 0;
		Map<String, String> allStats;
		
		String hadoopDistribution = FileUtil.getClusterInfoDetail(Constants.HADOOP_DISTRIBUTION);
		boolean isMapr = Constants.MAPR.equalsIgnoreCase(hadoopDistribution) || Constants.EMRMAPR.equalsIgnoreCase(hadoopDistribution);
		String sLocalDfsUsed;
		if (isMapr) {
			allStats = parseMapRInformationFromNodes(cluster, nodeIp, dataLoadResult);
			sLocalDfsUsed = allStats.get(nodeIp);
		} else {
			// Getting info through ip address
			allStats = parseInformationFromNodes(nodeIp, dataLoadResult);
			sLocalDfsUsed = allStats.get(nodeIp);
			if (sLocalDfsUsed == null) {
				// Getting info through hostname
				String hostName = InetAddress.getByName(nodeIp).getHostName();
				allStats = parseInformationFromNodes(hostName, dataLoadResult);
				sLocalDfsUsed = allStats.get(hostName);
				if (sLocalDfsUsed == null) {
					throw new Exception(
							"Node [" + nodeIp + "] details not found in dfsadmin command");
				}
			}
		}
		totalDfsUsed = Long.parseLong(allStats.get("TotalDfsUsed"));
		localDfsUsed = Long.parseLong(sLocalDfsUsed);
		dataLoadPercent = (localDfsUsed * ProfilerConstants.HUNDRED  * 1.0 ) / totalDfsUsed;
		double differencePercent = dataLoadPercent - idealDataLoadPercent;
		dataLoadPercent = ProfilerUtil.roundTwoDecimals(dataLoadPercent);
		if (Math.abs(differencePercent) <= 10.0) {
			node.setPerformance(NodePerformance.Good);
		} else if (Math.abs(differencePercent) <= 20.0) {
			node.setPerformance(NodePerformance.Warn);
		} else {
			node.setPerformance(NodePerformance.Bad);
		}
		return dataLoadPercent;		
/*		
		int length = 0;
		long offsetData = 0;
		long avgDfsUsedOnNode = 0;
		long lowDataLevel = 0;
		long highDataLevel = 0;


		length = cluster.getWorkers().getHosts().size();
		avgDfsUsedOnNode = totalDfsUsed / length;
		
		offsetData = (long) (avgDfsUsedOnNode * (ProfilerConstants.DOT_TWO));
		lowDataLevel = avgDfsUsedOnNode - offsetData;
		highDataLevel = avgDfsUsedOnNode + offsetData;
		dataLoadPercent = ProfilerUtil.roundTwoDecimals(dataLoadPercent);
		if (lowDataLevel >= localDfsUsed) {
			node.setPerformance(NodePerformance.Good);
		} else if (highDataLevel <= localDfsUsed) {
			node.setPerformance(NodePerformance.Bad);
		} else {
			node.setPerformance(NodePerformance.Average);
		}
*/
	}
	
	

	/**
	* Parses the mapR information from nodes.
	*
	* @param cluster the cluster
	* @param hostName the host name
	* @param commandResult the command result
	* @return the map
	*/
	@SuppressWarnings("unchecked")
	private Map<String, String> parseMapRInformationFromNodes(Cluster cluster, String hostName, String[] commandResult) {
		Map<String, String> dfsUsedMap = new HashMap<String, String>();
		Map<String, String> processedMap = new HashMap<String, String>();
		Gson gson=new GsonBuilder().create();
		long totalDfsUsed=0;
		for (String line : commandResult) {
			if(line.contains("{")){
				dfsUsedMap.putAll((Map<? extends String, ? extends String>) gson.fromJson(line, mapType));
			}
		}
		/*Converting all the host names in the Map into IP Address*/

		for (Entry<String, String> entry : dfsUsedMap.entrySet()) {
			String value=entry.getValue();
			totalDfsUsed=totalDfsUsed+Long.parseLong(value);
			processedMap.put(RemotingUtil.getIPfromHostName(cluster, entry.getKey()), value);
		}
		processedMap.put("TotalDfsUsed", Long.toString(totalDfsUsed));
		LOGGER.debug("Result of the command is "+processedMap);
		return processedMap;
	}

	/**
	 * Parses the information from nodes.
	 *
	 * @param nodeIP the node ip
	 * @param commandResult the command result
	 * @return the map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Map<String, String> parseInformationFromNodes(String nodeIP, String[] commandResult) throws Exception{
		Map<String, String> dfsResp = new HashMap<String, String>();
		DFSEnum dfsEnum = DFSEnum.DfsUsed;
		String dfsUsedPercent = "DFS Used%";
		String tempValue = null;
		String[] tempArray = null;
		boolean flag = true, internalFlag = true, internalFlag2 = true;
		long dfsUsed = 0;
		for (String line : commandResult) {
			tempArray = line.split("\\:");
			if (tempArray.length > 1) {
				tempValue = tempArray[0].trim().toString();
				if (flag) {
					if (dfsEnum.getName().equals(tempValue)) {
						dfsResp.put("TotalDfsUsed", tempArray[1].split("\\(")[0].trim().toString());
						flag = false;
					}
				} else if (internalFlag) {
					if (nodeIP.equals(tempArray[1].trim().toString())) {
						internalFlag = false;
					}
				} else if (internalFlag2 && dfsEnum.getName().equals(tempValue)) {
					internalFlag2 = false;
					dfsUsed = Long.parseLong(tempArray[1].split("\\(")[0].trim());
					dfsResp.put(nodeIP, String.valueOf(dfsUsed));
				} else if (dfsUsedPercent.equals(tempValue)) {
						double percent = Double.parseDouble(tempArray[1].trim().replace("%", ""));
						if (dfsUsed == 0 && percent == 100) {
							throw new Exception("Node [" + nodeIP + "] down");
						}
						break;
				}
			}
		}

		return dfsResp;
	}

	/**
	 * <<<<<<< Updated upstream Returns CPU stats(usage,num of cores,threads per core) for each node of the cluster. ======= Returns CPU
	 * stats(usage,num of cores,threads per core) for each node of the cluster. >>>>>>> Stashed changes
	 * 
	 * @param config
	 *            the config
	 * @param nodeIp
	 *            the node ip
	 * @return the list of pojos for CPU stats for each node
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws IntrospectionException
	 *             the introspection exception
	 * @throws MBeanException
	 *             the m bean exception
	 * @throws ReflectionException
	 *             the reflection exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSchException
	 *             the j sch exception
	 */
	public Map<String, String> getCPUStats(Cluster cluster, String nodeIp) throws AttributeNotFoundException, InstanceNotFoundException,
			IntrospectionException, MBeanException, ReflectionException, IOException, JSchException {

		String user = cluster.getWorkers().getUser();
		String sshAuthKeyFilePath = cluster.getAgents().getSshAuthKeysFile();
		Session session = null;
		Map<String, String> cpuStats = new HashMap<String, String>();
		try {
			session = createSession(user, nodeIp, sshAuthKeyFilePath);
			UserInfo ui = new JumbuneUserInfo();
			session.setUserInfo(ui);
			session.connect();
			cpuStats.put("CpuUsage", String.valueOf(getCPUUsage(session)));
			List<Integer> cpuDetails = getCPUDetails(session);
			cpuStats.put("NumberOfCores", String.valueOf(cpuDetails.get(1)));
			cpuStats.put("ThreadsPerCore", String.valueOf(cpuDetails.get(0)));
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
		return cpuStats;

	}

	/**
	 * Gets the remote cpu stats.
	 * 
	 * @param config
	 *            the config
	 * @param nodeIp
	 *            the node ip
	 * @return the remote cpu stats
	 * @throws AttributeNotFoundException
	 *             the attribute not found exception
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws IntrospectionException
	 *             the introspection exception
	 * @throws MBeanException
	 *             the m bean exception
	 * @throws ReflectionException
	 *             the reflection exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JSchException
	 *             the j sch exception
	 */
	public Map<String, String> getRemoteCPUStats(Cluster cluster, String nodeIp) throws AttributeNotFoundException, InstanceNotFoundException,
			IntrospectionException, MBeanException, ReflectionException, IOException, JSchException {
		List<String> hosts;
		if (nodeIp == null) {
			hosts = cluster.getWorkers().getHosts();
		} else {
			hosts = new ArrayList<String>();
			hosts.add(nodeIp);
		}	
		
		List<Integer> cpuDetails;
		Map<String, String> cpuStats = null;
		Remoter remoter = null;
		String response = null;
	    remoter = RemotingUtil.getRemoter(cluster);
		
		for (String host : hosts) {
			
			
			CommandWritableBuilder builder = new CommandWritableBuilder(cluster, host);
			builder.addCommand(CPU_USAGE_COMMAND_WITHOUT_CARET, false, null, CommandType.FS);
			response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
			LOGGER.debug("CPU_USAGE_COMMAND response - " + response);
			if (response == null || "".equals(response.trim())) {
				LOGGER.warn("No response from remote machine on command " + CPU_USAGE_COMMAND);
				CommandWritableBuilder builderWoCaret = new CommandWritableBuilder(cluster, host);
				builderWoCaret.addCommand(CPU_USAGE_COMMAND, false, null, CommandType.FS);
				
				response = (String) remoter.fireCommandAndGetObjectResponse(builderWoCaret.getCommandWritable());

			}
			cpuStats = new HashMap<String, String>();
			ResultParser resultParser = new ResultParser();
			float usage = resultParser.parseRemoteCPUUSageResult(response);
			cpuStats.put("CpuUsage", String.valueOf(usage));
			cpuDetails = getRemoteCPUDetails(cluster, host);
			cpuStats.put("NumberOfCores", String.valueOf(cpuDetails.get(1)));
			cpuStats.put("ThreadsPerCore", String.valueOf(cpuDetails.get(0)));
		}
		remoter.close();
		return cpuStats;

	}

	/**
	 * Gets CPU usage for each node.
	 * 
	 * @param session
	 *            the session
	 * @return the cPU usage
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private float getCPUUsage(Session session) throws JSchException, IOException {

		Channel channel = getChannel(session, CPU_USAGE_COMMAND);
		InputStream in = null;
		float usage = 0.0f;
		try {
			in = channel.getInputStream();
			ResultParser resultParser = new ResultParser();
			usage = resultParser.parseCPUUSageResult(in);
		} finally {
			if (in != null) {
				in.close();
			}
			channel.disconnect();
		}
		return usage;
	}

	/**
	 * Gets CPU details: number of cores and threads per core.
	 * 
	 * @param session
	 *            the session
	 * @return the cpu details for each node
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private List<Integer> getCPUDetails(Session session) throws JSchException, IOException {

		Channel channel = getChannel(session, CPU_DETAILS_COMMAND);
		InputStream in = null;
		List<Integer> cpuStats = null;
		try {
			in = channel.getInputStream();
			ResultParser resultParser = new ResultParser();
			cpuStats = resultParser.parseCPUDetailsResult(in);
		} finally {
			if (in != null) {
				in.close();
			}
			channel.disconnect();
		}
		return cpuStats;

	}

	/**
	 * Gets CPU details: number of cores and threads per core.
	 * 
	 * @param config
	 *            the config
	 * @param host
	 *            the host
	 * @return the cpu details for each node
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private List<Integer> getRemoteCPUDetails(Cluster cluster, String host)
			throws JSchException, IOException {
		List<Integer> cpuStats = null;
		Remoter remoter = RemotingUtil.getRemoter(cluster);
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, host);
		builder.addCommand(ProfilerConstants.CPU_DETAILS_COMMAND, false, null, CommandType.FS);
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		ResultParser resultParser = new ResultParser();
		cpuStats = resultParser.parseRemoteCPUDetailsResult(response);
		return cpuStats;

	}

	/**
	 * <<<<<<< Updated upstream Opens a new channel,sets the command,input and error streams and return it. ======= Opens a new channel,sets the
	 * command,input and error streams and return it. >>>>>>> Stashed changes
	 * 
	 * @param session
	 *            the session
	 * @param command
	 *            the command
	 * @return the channel
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Channel getChannel(Session session, String command) throws JSchException, IOException {

		Channel channel = session.openChannel(EXECUTION_MODE);
		((ChannelExec) channel).setCommand(command);
		channel.setInputStream(null);
		((ChannelExec) channel).setErrStream(System.err);
		channel.connect();
		return channel;
	}

	/**
	 * Gets network latency among the selected list of nodes.
	 * 
	 * @param config
	 *            the config
	 * @param selectedNodesWithPassword
	 *            the selected nodes with password
	 * @return the network latency for selected nodes
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<NetworkLatencyInfo> getNetworkLatencyForSelectedNodes(Cluster cluster, Map<String, String> selectedNodesWithPassword)
			throws JSchException, IOException {

		// TODO: support for slaves with different user names
		LOGGER.debug("inside getNetworkLatencyForSelectedNodes method");
		String user = cluster.getWorkers().getUser();
		LOGGER.debug("user is:" + user);
		String sshAuthKeyFilePath = cluster.getAgents().getSshAuthKeysFile();
		Session session = null;
		UserInfo ui;

		List<NetworkLatencyInfo> networkLatencyList = new ArrayList<NetworkLatencyInfo>();
		NetworkLatencyInfo networkLatencyInfo1 = null;
		String node1;
		int outerCounter = 0;
		try {
			for (Map.Entry<String, String> nodeDetails : selectedNodesWithPassword.entrySet()) {

				if ((networkLatencyList.size() <= outerCounter)) {
					networkLatencyInfo1 = new NetworkLatencyInfo();
					networkLatencyList.add(networkLatencyInfo1);
				} else {
					networkLatencyInfo1 = networkLatencyList.get(outerCounter);
				}

				node1 = nodeDetails.getKey();

				session = createSession(user, node1, sshAuthKeyFilePath);
				ui = new JumbuneUserInfo();
				session.setUserInfo(ui);
				java.util.Properties conf = new java.util.Properties();
				conf.put("StrictHostKeyChecking", "no");
				session.setConfig(conf);
				session.setPassword(nodeDetails.getValue());
				session.connect();

				int innerCounter = 0;
				for (String node2 : selectedNodesWithPassword.keySet()) {
					getNWLatency(session, networkLatencyList,
							networkLatencyInfo1, node1, outerCounter,
							innerCounter, node2);
					innerCounter++;
				}

				outerCounter++;
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			if (session != null){
				session.disconnect();
			}
		}
		return networkLatencyList;
	}


	/**
	 * Gets the Network latency.
	 *
	 * @param session the session
	 * @param networkLatencyList the network latency list
	 * @param networkLatencyInfo1 the network latency info1
	 * @param node1 the node1
	 * @param outerCounter the outer counter
	 * @param innerCounter the inner counter
	 * @param node2 the node2
	 * @return the nW latency
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void getNWLatency(Session session,
			List<NetworkLatencyInfo> networkLatencyList,
			NetworkLatencyInfo networkLatencyInfo1, String node1,
			int outerCounter, int innerCounter, String node2)
			throws JSchException, IOException {
		NetworkLatencyInfo networkLatencyInfo2;
		List<AdjacencyInfo> adjacencies1;
		List<AdjacencyInfo> adjacencies2;
		AdjacencyInfo adjacencyInfo1;
		AdjacencyInfo adjacencyInfo2;
		Map<String, Float> data1;
		Map<String, Float> data2;
		float latency;
		if (innerCounter > outerCounter) {
			if ((networkLatencyList.size() <= innerCounter)) {
				networkLatencyInfo2 = new NetworkLatencyInfo();
				networkLatencyList.add(networkLatencyInfo2);
			} else {
				networkLatencyInfo2 = networkLatencyList.get(innerCounter);
			}
			latency = getNetworkLatency(session, node2);
			networkLatencyInfo1.setId(node1);
			networkLatencyInfo1.setName(node1);
			adjacencies1 = networkLatencyInfo1.getAdjacencies();
			if (adjacencies1 == null) {
				adjacencies1 = new ArrayList<AdjacencyInfo>();
			}
			// for node1
			adjacencyInfo1 = new AdjacencyInfo();
			adjacencyInfo1.setNodeTo(node2);
			data1 = new HashMap<String, Float>();
			data1.put(weight, latency);
			adjacencyInfo1.setData(data1);
			adjacencies1.add(adjacencyInfo1);
			networkLatencyInfo1.setAdjacencies(adjacencies1);

			// for node2
			networkLatencyInfo2.setId(node2);
			networkLatencyInfo2.setName(node2);
			adjacencies2 = networkLatencyInfo2.getAdjacencies();
			if (adjacencies2 == null) {
				adjacencies2 = new ArrayList<AdjacencyInfo>();
			}
			adjacencyInfo2 = new AdjacencyInfo();
			adjacencyInfo2.setNodeTo(node1);
			data2 = new HashMap<String, Float>();
			data2.put(weight, latency);
			adjacencyInfo2.setData(data2);
			adjacencies2.add(adjacencyInfo2);
			networkLatencyInfo2.setAdjacencies(adjacencies2);
		}
	}

	/**
	 * Gets the remote network latency for selected nodes.
	 * 
	 * @param config
	 *            the config
	 * @param selectedNodesWithPassword
	 *            the selected nodes with password
	 * @return the remote network latency for selected nodes
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<NetworkLatencyInfo> getRemoteNetworkLatencyForSelectedNodes(
			JumbuneRequest jumbuneRequest, Map<String, String> selectedNodesWithPassword)
			throws JSchException, IOException {

		// TODO: support for slaves with different user names
		LOGGER.debug("inside getRemoteNetworkLatencyForSelectedNodes method");
		List<NetworkLatencyInfo> networkLatencyList = new ArrayList<NetworkLatencyInfo>();
		NetworkLatencyInfo networkLatencyInfo1 = null;
		String node1;
		int outerCounter = 0;
		try {
			for (Map.Entry<String, String> nodeDetails : selectedNodesWithPassword.entrySet()) {

				if ((networkLatencyList.size() <= outerCounter)) {
					networkLatencyInfo1 = new NetworkLatencyInfo();
					networkLatencyList.add(networkLatencyInfo1);
				} else {
					networkLatencyInfo1 = networkLatencyList.get(outerCounter);
				}
				node1 = nodeDetails.getKey();
				int innerCounter = 0;
				for (String node2 : selectedNodesWithPassword.keySet()) {
					calNWLatencyForGreaterInnerCounter(jumbuneRequest, networkLatencyList,
							networkLatencyInfo1, node1, outerCounter,
							innerCounter, node2);
					innerCounter++;
				}
				outerCounter++;
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			// TODO: Need to thru some another exception to take out of this
			// flow...
		}
		return networkLatencyList;
	}


	/**
	 * This api Calculates network latency for greater inner counter.
	 *
	 * @param config the config
	 * @param user the user
	 * @param rsaFilePath the rsa file path
	 * @param dsaFilePath the dsa file path
	 * @param networkLatencyList the network latency list
	 * @param networkLatencyInfo1 the network latency info1
	 * @param node1 the node1
	 * @param outerCounter the outer counter
	 * @param innerCounter the inner counter
	 * @param node2 the node2
	 * @throws JSchException the j sch exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void calNWLatencyForGreaterInnerCounter(JumbuneRequest jumbuneRequest,
			List<NetworkLatencyInfo> networkLatencyList,
			NetworkLatencyInfo networkLatencyInfo1, String node1,
			int outerCounter, int innerCounter, String node2)
			throws JSchException, IOException {
		NetworkLatencyInfo networkLatencyInfo2;
		List<AdjacencyInfo> adjacencies1;
		List<AdjacencyInfo> adjacencies2;
		AdjacencyInfo adjacencyInfo1;
		AdjacencyInfo adjacencyInfo2;
		Map<String, Float> data1;
		Map<String, Float> data2;
		float latency;
		if (innerCounter > outerCounter) {
			if ((networkLatencyList.size() <= innerCounter)) {
				networkLatencyInfo2 = new NetworkLatencyInfo();
				networkLatencyList.add(networkLatencyInfo2);
			} else {
				networkLatencyInfo2 = networkLatencyList.get(innerCounter);
			}
			latency = getRemoteNetworkLatency(jumbuneRequest, node1,  node2);
			networkLatencyInfo1.setId(node1);
			networkLatencyInfo1.setName(node1);
			adjacencies1 = networkLatencyInfo1.getAdjacencies();
			if (adjacencies1 == null) {
				adjacencies1 = new ArrayList<AdjacencyInfo>();
			}
			// for node1
			adjacencyInfo1 = new AdjacencyInfo();
			adjacencyInfo1.setNodeTo(node2);
			data1 = new HashMap<String, Float>();
			data1.put(weight, latency);
			adjacencyInfo1.setData(data1);
			adjacencies1.add(adjacencyInfo1);
			networkLatencyInfo1.setAdjacencies(adjacencies1);

			// for node2
			networkLatencyInfo2.setId(node2);
			networkLatencyInfo2.setName(node2);
			adjacencies2 = networkLatencyInfo2.getAdjacencies();
			if (adjacencies2 == null) {
				adjacencies2 = new ArrayList<AdjacencyInfo>();
			}
			adjacencyInfo2 = new AdjacencyInfo();
			adjacencyInfo2.setNodeTo(node1);
			data2 = new HashMap<String, Float>();
			data2.put(weight, latency);
			adjacencyInfo2.setData(data2);
			adjacencies2.add(adjacencyInfo2);
			networkLatencyInfo2.setAdjacencies(adjacencies2);
		}
	}

	/**
	 * Returns the network latency between two nodes.
	 * 
	 * @param session
	 *            the Session to node-1
	 * @param node2
	 *            the node2
	 * @return the network latency
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private float getNetworkLatency(Session session, String node2) throws JSchException, IOException {

		Channel channel = getChannel(session, NETWORK_LATENCY_COMMAND + " " + node2);
		InputStream in = null;
		float latency = 0.0f;
		try {
			in = channel.getInputStream();
			ResultParser resultParser = new ResultParser();
			latency = resultParser.parseNetworkLatencyResult(in);
		} finally {
			if (in != null) {
				in.close();
			}
			channel.disconnect();
		}

		return latency;
	}

	/**
	 * Gets the remote network latency.
	 * 
	 * @param config
	 *            the config
	 * @param user
	 *            the user
	 * @param host
	 *            the host
	 * @param rsaFilePath
	 *            the rsa file path
	 * @param dsaFilePath
	 *            the dsa file path
	 * @param node2
	 *            the node2
	 * @return the remote network latency
	 * @throws JSchException
	 *             the j sch exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private float getRemoteNetworkLatency(JumbuneRequest jumbuneRequest,  String host, String node2)
			throws JSchException, IOException {
		Cluster cluster = jumbuneRequest.getCluster();
		float latency;
		Remoter remoter = RemotingUtil.getRemoter(cluster);
				
		StringBuilder sb = new StringBuilder();
				sb.append(NETWORK_LATENCY_COMMAND).append(" ").append(node2);
		CommandWritableBuilder builder = new CommandWritableBuilder(cluster, host);
		builder.addCommand(sb.toString(), false, null, CommandType.FS);
		String response = (String) remoter.fireCommandAndGetObjectResponse(builder.getCommandWritable());
		remoter.close();
		latency = 0.0f;
		ResultParser resultParser = new ResultParser();
		latency = resultParser.parseRemoteNetworkLatencyResult(response);
		return latency;
	}

	/**
	 * This inner class holds the thread info used by hot thread infos.
	 */
	class MyThreadInfo {

		/** The cpu time. */
		private long cpuTime;

		/** The blocked count. */
		private long blockedCount;

		/** The blocked time. */
		private long blockedTime;

		/** The waited count. */
		private long waitedCount;

		/** The waitedtime. */
		private long waitedtime;

		/** The delta done. */
		private boolean deltaDone;

		/** The info. */
		private ThreadInfo info;

		/**
		 * Instantiates a new my thread info.
		 * 
		 * @param cpuTime
		 *            the cpu time
		 * @param info
		 *            the info
		 */
		MyThreadInfo(long cpuTime, ThreadInfo info) {
			blockedCount = info.getBlockedCount();
			blockedTime = info.getBlockedTime();
			waitedCount = info.getWaitedCount();
			waitedtime = info.getWaitedTime();
			this.cpuTime = cpuTime;
			this.info = info;
		}

		/**
		 * Sets the delta.
		 * 
		 * @param cpuTime
		 *            the cpu time
		 * @param info
		 *            the info
		 */
		void setDelta(long cpuTime, ThreadInfo info) {
			if (deltaDone){
				throw new IllegalStateException("setDelta already called once");
			}
			blockedCount = info.getBlockedCount() - blockedCount;
			blockedTime = info.getBlockedTime() - blockedTime;
			waitedCount = info.getWaitedCount() - waitedCount;
			waitedtime = info.getWaitedTime() - waitedtime;
			this.cpuTime = cpuTime - this.cpuTime;
			deltaDone = true;
			this.info = info;
		}
	}

	

}