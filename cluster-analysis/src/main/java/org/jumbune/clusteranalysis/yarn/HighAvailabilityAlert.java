package org.jumbune.clusteranalysis.yarn;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.NameNodes;
import org.jumbune.common.alerts.HAAlert;
import org.jumbune.common.beans.AlertInfo;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.monitoring.beans.JMXDeamons;
import org.jumbune.monitoring.utils.ProfilerJMXDump;

/**
 * The Class HighAvailabilityAlert.
 */
public class HighAvailabilityAlert implements HAAlert{

	/** The Constant NAME_NODE_STATE. */
	private static final String NAME_NODE_STATE = "NameNodeStatus.State";
	
	/** The Constant ACTIVE. */
	private static final String ACTIVE = "active";
	
	/**
	 * Gets the name node status.
	 *
	 * @param cluster the cluster
	 * @return the name node status
	 * @throws AttributeNotFoundException the attribute not found exception
	 * @throws InstanceNotFoundException the instance not found exception
	 * @throws IntrospectionException the introspection exception
	 * @throws MBeanException the m bean exception
	 * @throws ReflectionException the reflection exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List <AlertInfo> getNameNodeStatus (Cluster cluster) throws AttributeNotFoundException, InstanceNotFoundException, IntrospectionException
	 ,MBeanException, ReflectionException, IOException{
		
		ProfilerJMXDump jmxDump = new ProfilerJMXDump();
		List<AlertInfo> alertList = new ArrayList<AlertInfo>();
		NameNodes nameNodes = cluster.getNameNodes();
		List <String> hosts = nameNodes.getHosts();
		for(String host:hosts){
			Map<String, String> response = jmxDump.getAllJMXStats(JMXDeamons.NAME_NODE, host, nameNodes.getNameNodeJmxPort(), cluster.isJmxPluginEnabled());
			if(response == null){
				Map <String, String> statusResponse = jmxDump.getAllJMXStats(JMXDeamons.NAME_NODE, cluster.getNameNode() , nameNodes.getNameNodeJmxPort(), cluster.isJmxPluginEnabled());
				if(statusResponse.get(NAME_NODE_STATE).equals(ACTIVE)){
					AlertInfo alertInfo = new AlertInfo(ExtendedConstants.WARNING_LEVEL,"NameNode Switchover","Name node switched from " +host+ " to "+cluster.getClusterName(), getDate());
					alertList.add(alertInfo);
					return alertList;
				}
			}
		}
		return alertList;
	} 
	
	private String getDate(){
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat(ExtendedConstants.TIME_FORMAT);	
		return sdf.format(date);
	}
}
