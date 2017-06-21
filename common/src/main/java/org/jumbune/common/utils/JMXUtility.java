package org.jumbune.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.jumbune.common.beans.cluster.Agent;
import org.jumbune.common.utils.CommandWritableBuilder;
import org.jumbune.common.utils.RemotingUtil;
import org.jumbune.remoting.client.Remoter;
import org.jumbune.remoting.client.RemoterFactory;
import org.jumbune.remoting.common.CommandType;
import org.jumbune.remoting.common.RemotingMethodConstants;

import org.jumbune.common.beans.cluster.EnterpriseCluster;

public final class JMXUtility {

	private static final String JMX_AGENT_DIR = "/jmx_agent/";
		
	public void sendJmxAgentToAllDaemons(EnterpriseCluster cluster){				
		Remoter remoter = RemotingUtil.getRemoter(cluster);

		// building commandWritable
	    List<String> commandParams = prepareCommandParamsForJMXOperations(cluster);
	    String jmxAgentDir = cluster.getWorkers().getWorkDirectory() + JMX_AGENT_DIR ;

	    RemotingUtil.mkDir(new CommandWritableBuilder(cluster), remoter, jmxAgentDir);
	  
	    CommandWritableBuilder builder = new CommandWritableBuilder(cluster); 
	    builder.setMethodToBeInvoked(RemotingMethodConstants.SEND_JUMBUNE_JMX_AGENT_TO_ALL_NODES);
        builder.addCommand("", true, commandParams, CommandType.FS);	    
	    remoter.fireAndForgetCommand(builder.getCommandWritable());		
	}
	
	
  private List<String> prepareCommandParamsForJMXOperations(EnterpriseCluster cluster){
	  List<String> commandParams = new ArrayList<>();
	   final String jmxAgentDir = cluster.getWorkers().getWorkDirectory() + JMX_AGENT_DIR ;	    
		commandParams.add(jmxAgentDir);
	    commandParams.addAll(cluster.getWorkers().getHosts());
	    commandParams.add(cluster.getNameNode());
	    commandParams.add(cluster.getTaskManagers().getActive());
	    //  commandParams.addAll(cluster.getNameNodes().getHosts());
	    return commandParams;
  }

  
  public boolean establishConnectionToJmxAgent(EnterpriseCluster cluster){

		Remoter remoter = RemotingUtil.getRemoter(cluster);
	    List<String> commandParams = prepareCommandParamsForJMXOperations(cluster);
	    CommandWritableBuilder builder = new CommandWritableBuilder(cluster); 
	    builder.setMethodToBeInvoked(RemotingMethodConstants.ESTABLISH_CONN_TO_JMX_AGENTS);
        builder.addCommand("", true, commandParams, CommandType.FS);	    
	    remoter.fireAndForgetCommand(builder.getCommandWritable());		
	   return false;	  
  }
  
  public void shutDownJMXAgents(EnterpriseCluster cluster) {

		Remoter remoter = RemotingUtil.getRemoter(cluster);
	    List<String> commandParams = prepareCommandParamsForJMXOperations(cluster);
	    
	    //removing working dir as prepareCommandParamsForJMXOperations adds it as first element. 
	    commandParams.remove(0);
	    
	    CommandWritableBuilder builder = new CommandWritableBuilder(cluster); 
	    builder.setMethodToBeInvoked(RemotingMethodConstants.SHUT_DOWN_JMX_AGENTS);
        builder.addCommand("", true, commandParams, CommandType.FS);	    
	    remoter.fireAndForgetCommand(builder.getCommandWritable());			  
  }
  
}
