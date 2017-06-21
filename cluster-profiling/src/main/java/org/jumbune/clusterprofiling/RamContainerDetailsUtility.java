package org.jumbune.clusterprofiling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.utils.Constants;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.utils.JMXConnectorInstance;
import org.jumbune.remoting.jmx.client.JumbuneJMXClient;


/**
 * The Class GetRamContainerDetails expose out the method to get the node specific ram and minimum container recommendation for the worker nodes.
 */
public class RamContainerDetailsUtility {
	
	
	
	/** The Constant ConvertToGb. */
	private static final Integer ConvertToGb = 1073741824;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(RamContainerDetailsUtility.class);
	
	/**
	 * This method returns the ram corresponding to the node in the cluster.
	 *
	 * @param cluster the cluster
	 * @return the ram per node
	 */
	public Map<Double,List<String>> getRamPerNode(Cluster cluster) {
		JMXConnector connector = null;
		List<String> nodeIps = new ArrayList<String>();
		double memory = 0 ;
		Map<Double,List<String>> ramIpMap = new HashMap<Double, List<String>>();
		try {
			
			for (String hosts : cluster.getWorkers().getHosts()) {
				if (cluster.isJmxPluginEnabled()) {
					JumbuneJMXClient jmxClient = new JumbuneJMXClient();
					String daemonName = JMXDeamons.NODE_MANAGER.toString() + Constants.OS_IDENTIFIER;
					Map<String, Object> statsMap = jmxClient.getOSStats(hosts, daemonName);
					memory = (Long) statsMap.get("TotalPhysicalMemorySize");
				} else {
					String objOS = "java.lang:type=OperatingSystem";
					ObjectName objName = new ObjectName(objOS);
					connector = getConnection(hosts, cluster.getWorkers().getTaskExecutorJmxPort());
					memory = getPhysicalMemory(connector, objName);
				}
				memory = convertToGB(memory);
				nodeIps = new ArrayList<String>();
				if (ramIpMap.containsKey(memory)) {
					nodeIps.add(hosts);
					nodeIps.add(ramIpMap.get(memory).toString());
					ramIpMap.put(memory, nodeIps);
				} else {
					nodeIps.add(hosts);
					ramIpMap.put(memory, nodeIps);
				}
			}
					
			
		} catch (Exception e) {
			LOGGER.error("Unable to get the physical memory value " + e);
		}
		return ramIpMap; 
		
	}

	/**
	 * Gets the ram node table.
	 *
	 * @param cluster the cluster
	 * @return the ram node table
	 */
	public Map<String,Double> getRamNodeTable(Cluster cluster) {
		JMXConnector connector = null;
		List<String> nodeIps = new ArrayList<String>();
		double memory ;
		Map<String,Double> ramIpMap = new HashMap<String,Double>();
		try {
		
			for (String host : cluster.getWorkers().getHosts()) {
				if (cluster.isJmxPluginEnabled()) {
					JumbuneJMXClient jmxClient = new JumbuneJMXClient();
					String daemonName = JMXDeamons.NODE_MANAGER.toString() + Constants.OS_IDENTIFIER;
					Map<String, Object> statsMap = jmxClient.getOSStats(host, daemonName);
					memory = (Long) statsMap.get("TotalPhysicalMemorySize");
				} else {
					String objOS = "java.lang:type=OperatingSystem";
					ObjectName objName = new ObjectName(objOS);
					connector = getConnection(host, cluster.getWorkers().getTaskExecutorJmxPort());
					memory = getPhysicalMemory(connector, objName);
				}
					memory = convertToGB(memory);
					ramIpMap.put(host, memory);
				}
					
			
		} catch (Exception e) {
			LOGGER.error("Unable to get the physical memory value " + e);
		}
		return ramIpMap; 
		
	}
	

	/**
	 * This method converts the memory in bytes to gb.
	 *
	 * @param memory the memory
	 * @return the double
	 */
	private double convertToGB(double memory) {
		memory = memory/ConvertToGb ;
		memory = Math.ceil(memory);
		return memory;
	}


	/**
	 * Gets the physical memory from the jmx os attribute.
	 *
	 * @param connector the connector
	 * @param objName the obj name
	 * @return the physical memory
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws MBeanException the m bean exception
	 * @throws AttributeNotFoundException the attribute not found exception
	 * @throws InstanceNotFoundException the instance not found exception
	 * @throws ReflectionException the reflection exception
	 */
	private Long getPhysicalMemory(JMXConnector connector, ObjectName objName) throws IOException, MBeanException,
			AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
		Long memory;
		MBeanServerConnection  connection = connector.getMBeanServerConnection();
		memory = ((Long) connection.getAttribute(objName,
				"TotalPhysicalMemorySize"));
		return memory;
	}
	
	
	/**
	 * Gets the jmx connection.
	 *
	 * @param host the host
	 * @param port the port
	 * @return the connection
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private JMXConnector getConnection(String host , String port) throws IOException {
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
				+ host + ":" + port + "/jmxrmi");
		return JMXConnectorInstance.getJMXConnectorInstance(url);
	}
	
	/**
	 * Gets the recommended container size according to the ram present in the node.
	 *
	 * @param ramIpMap the ram ip map
	 * @return the recommended container size
	 */
	public Map<Double,List<String>> getRecommendedContainerSize(Map<Double,List<String>> ramIpMap){
		
		Map<Double,List<String>> ramMap = new HashMap<Double,List<String>>();
				
		for (Map.Entry<Double,List<String>> entries : ramIpMap.entrySet()) {
		
			if(entries.getKey()<=4){
				ramMap.put(512.0,entries.getValue());
			}else if (entries.getKey()<=24){
				ramMap.put(1024.0,entries.getValue());
			}else if (entries.getKey()>24){
				ramMap.put(2048.0,entries.getValue());
			}
		}
		return ramMap;
	}
	
	/**
	 * Gets the recommended container look up table.
	 *
	 * @param ramIpMap the ram ip map
	 * @return the recommended container look up table
	 */
	public Map<String,Double> getRecommendedContainerLookUpTable(Map<String,Double> ramIpMap){
		
		Map<String,Double> ramMap = new HashMap<String,Double>();
				
		for (Map.Entry<String,Double> entries : ramIpMap.entrySet()) {
		
			if(entries.getValue()<=4){
				ramMap.put(entries.getKey(),512.0);
			}else if(entries.getValue()<=24){
				ramMap.put(entries.getKey(),1024.0);
			}else if (entries.getValue()>24){
				ramMap.put(entries.getKey(),2048.0);
			}
		}
		return ramMap;
	}

	/**
	 * Gets the system reserved memory.
	 *
	 * @param memoryInGB the memory
	 * @return the system reserved memory
	 */
	public Double getSystemReservedMemory(Double memoryInGB){
		int divisor = 1;
		if(memoryInGB <= 8){
			divisor = 4;
		}else{
			divisor = 8 ;
		}
		return memoryInGB/divisor;
	}
	
	/**
	 * Gets the balanced mem vcore.
	 *
	 * @param memoryInGB the memory
	 * @param vCores the cores
	 * @param confMemoryInMB 
	 * @return the balanced mem vcore
	 */
	public Map<Double,Integer> getBalancedMemVcore(Double memoryInGB, int vCores, double confMemoryInMB){
		Map<Double, Integer> memVcoreMap = new HashMap<Double,Integer>();
		double optsinGb = (confMemoryInMB / 1024) ;
		double optsVCores = optsinGb * vCores ;
		if (optsVCores < memoryInGB){
			memVcoreMap.put(optsVCores, vCores);
		}else if (memoryInGB < optsVCores){
			memVcoreMap.put(memoryInGB, (int) Math.floor(memoryInGB/optsinGb));
		}else {
			memVcoreMap.put(memoryInGB, vCores);
		}
		return memVcoreMap;
		
	}

	/**
	 * Gets the spark driver memory.
	 *
	 * @param confMem the conf mem is the configured node manager memory capacity
	 * @return the spark driver memory
	 */
	public Double getSparkDriverMemory(double confMem) {

		double confMemInGb = (confMem/1024);
		double  sparkDriverMem = 0 ;
		if(confMemInGb < 1){
			sparkDriverMem = 0.25 ;
		}else if(confMemInGb > 1 && confMemInGb < 12){
			sparkDriverMem = 1 ;
		}else if(confMemInGb > 12 && confMemInGb < 50){
			sparkDriverMem = 4 ;
		}else if(confMemInGb > 50){
			sparkDriverMem = 12 ;
		}
		return sparkDriverMem;
	}
}
