package org.jumbune.remoting.common;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.jumbune.utils.conf.beans.HAConfiguration;
import org.jumbune.remoting.server.ha.integration.zk.ZKConstants;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This class ZKUtils contains utility methods to communicate with zookeeper.
 *
 */
public class ZKUtils {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(ZKUtils.class);
	
	/** The gson. */
	private static Gson gson = new Gson();

	private static final Type AGENT_NODES_LIST_TYPE = new TypeToken<ArrayList<AgentNode>>(){}.getType();
	
	/**
	 * Used to convert AgentNode object into gson String
	 * @param agent
	 * @return
	 */
	public static String convertAgentToGson(AgentNode agent){
		return gson.toJson(agent);
	}

	/**
	 * Used to convert gson String to AgentNode object
	 * @param json
	 * @return
	 */
	public static AgentNode convertGsonToAgent(String json){
		AgentNode agent = gson.fromJson(json, AgentNode.class);
		return agent;
	}


	/**
	 * Sets the agent node data on znode.
	 *
	 * @param nodePath the node path
	 * @param agent the Agent
	 */
	public static void setAgentData(String nodePath, AgentNode agent) {
		try {
			String hosts = null;
			CuratorConnector connector = CuratorConnector.getInstance(hosts);
			String json = convertAgentToGson(agent);
			connector.setData(nodePath, json.getBytes(Charset.forName("UTF-8")));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);		
		}
	}


	/**
	 * Gets the agent data from znode.
	 * @param zkHosts 
	 *
	 * @param nodePath the node path
	 * @return the agent node data
	 */
	public static AgentNode getAgentData(String[] zkHosts, String nodePath){		
		CuratorConnector connector = CuratorConnector.getInstance(zkHosts);
		byte data[] = null;
		final int retryTimes = 10;
		int retryCount = 1;
		AgentNode agent = null;
		try {
			do {
			   data = connector.getDataBytes(nodePath);		
			   if(data == null) {
				   LOGGER.warn("found active Agent null from ZK, retrying after " + 100 * retryCount + " millisecs");
				   Thread.sleep(100*retryCount);
			   }
			retryCount++;
			} while(data == null && retryCount <= retryTimes);
			agent = convertGsonToAgent(new String(data, Charset.defaultCharset()));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return agent;
	}
	
	
	
	/**
	 * Gets the active namenode name.
	 * @param zkHosts 
	 *
	 * @param nodePath the node path
	 * @return the active nn data
	 */
	public static byte[] getActiveNameNode(String[] zkHosts, String nodePath){
		byte data[] = null;
		CuratorConnector connector = CuratorConnector.getInstance(zkHosts);
		try {
			data = connector.getDataBytes(nodePath);
		}catch(Exception e) {		
			LOGGER.error("unable to find active namenode from zk", e.getMessage());			
		}
		return data;
	}
		
    /**
     * Gets the node children list.
     * @param zkHosts 
     *
     * @param nodePath the node path
     * @return the node children list
     */
    public static List<String> getNodeChildrenList(String[] zkHosts, String nodePath) { 	
		CuratorConnector connector = CuratorConnector.getInstance(zkHosts);
    	List<String> childNodes = new ArrayList<String>();
		try {
			childNodes = connector.getChildren(nodePath);
		} catch (KeeperException | InterruptedException e) {
			LOGGER.error("unable to get children for znode " + nodePath +", probably the specified znode path does not exist " , e.getMessage());
		} catch (Exception e) {	
			LOGGER.error("unable to get children for znode " + nodePath , e.getMessage());
		}
		return childNodes;
    }

    /**
     * Gets the agent node list.
     * @param zkHosts 
     *
     * @return the agent node list
     */
    public static List<AgentNode> getAgents(String[] zkHosts) {
    	CuratorConnector connector = CuratorConnector.getInstance(zkHosts);
    	String followersJson = null;
    	try {
			 followersJson = new String (connector.getDataBytes(ZKConstants.AGENT_FOLLOWER_PATH), StandardCharsets.UTF_8);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
        List<AgentNode> agentList = new ArrayList<AgentNode>(1);
        if(followersJson != null && !followersJson.isEmpty()) {
        	agentList = gson.fromJson(followersJson, AGENT_NODES_LIST_TYPE);        	
        }
        return agentList;
    }

    /**
     * Gets the leader agent node from zk.
     * @param zkHosts 
     * @param haProps2 
     *
     * @return the leader agent node from zk
     * @throws ConnectException 
     */
    public static AgentNode getLeaderAgentfromZK(String[] zkHosts, HAConfiguration haConf) throws ConnectException {
    	AgentNode leaderAgentNode = null;
    	leaderAgentNode = getAgentData(zkHosts, ZKConstants.AGENT_LEADER_PATH);
    	boolean connectionFlag = true;
    	Socket socket = null;
    	try {
			 socket = new Socket(leaderAgentNode.getHost(), leaderAgentNode.getPort());
		} catch (UnknownHostException e) {
		LOGGER.error(e.getMessage(), e);
		} catch(ConnectException e){
			LOGGER.warn("connection failed for - " + leaderAgentNode.getHost()+":"+leaderAgentNode.getPort());
			                 //blocking code
			connectionFlag = retryConnection(zkHosts, leaderAgentNode, haConf);		
		} catch (IOException e) {
		} finally {
			if(socket != null&& !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
    	if(!connectionFlag) {
    		LOGGER.error("can not find active agent, make sure you have at least one standby agent up");
    		throw new IllegalStateException("can not find active agent, make sure you have at least one standby agent up");
    	}
    	return leaderAgentNode;
    }
    
    
   private static boolean retryConnection(String[] zkHosts, AgentNode leaderAgentNode, HAConfiguration haConf) {
       boolean connectionFlag = false; 
       Socket socket = null;
       int numRetries = haConf.getNumRetriesAgentConn();
	   int reconnectMillis = haConf.getAgentConnMillis();
       for(int i = 1; i < numRetries && !connectionFlag; i++) {
		   connectionFlag = true;
			leaderAgentNode = getAgentData(zkHosts, ZKConstants.AGENT_LEADER_PATH);
			try {
				socket = new Socket(leaderAgentNode.getHost(), leaderAgentNode.getPort());
			} catch (ConnectException e){
				connectionFlag = false;
				LOGGER.warn("connection failed for - " + leaderAgentNode.getHost()+":"+leaderAgentNode.getPort()+ " retrying in " + (i*reconnectMillis) +" millis");
			} catch (IOException e) {			
			} finally {
				if (socket != null && !socket.isClosed()) {
					try {
						socket.close();
					} catch (IOException e) {
					}
				}
			}
			try {
				Thread.sleep(i*reconnectMillis*1000);
			} catch (InterruptedException e) {		
			}
		}
	   return connectionFlag;
    }
    

	/**
	 * Gets the leader name node from zookeeper.
	 * @param zkHosts 
	 *
	 * @return the leader name node from zookeeper
	 */
   public static byte[] getLeaderNameNodeFromZK(String[] zkHosts) {
	   final int retryTimes = 10;
	   byte[] nameHostName = null;
	   int retryCount = 1;
	   do {
		   List<String> nameNodeNameList = getNodeChildrenList(zkHosts, ZKConstants.HADOOP_NODE_PATH);
		   for (String nameNodeName : nameNodeNameList) {
			   nameHostName = getActiveNameNode(zkHosts,
					   ZKConstants.HADOOP_NODE_PATH + File.separator + nameNodeName + ZKConstants.ACTIVE_BREAD_CRUMB);
		   }
		   if (nameHostName == null) {
			   try {
				   LOGGER.warn("found active NN null from ZK, retrying after " + 100 * retryCount + " millisecs");
				   Thread.sleep(100 * retryCount);
			   } catch (InterruptedException e) {
			   }
		   }
		   retryCount++;
	   } while (nameHostName == null && retryCount <= retryTimes);
	   return nameHostName;
   }
	
	public static String getActiveNNHost(String[] zkHosts){
		byte[] activeHost = null;
		String activeNameNode = null ;
		activeHost = ZKUtils.getLeaderNameNodeFromZK(zkHosts);
		try {
			ActiveNodeInfo activeNodeInfo = PARSER.parsePartialFrom(activeHost);
			activeNameNode = activeNodeInfo.getHostname();
		} catch (InvalidProtocolBufferException e) {
			LOGGER.error(e);			
		}
		return activeNameNode;
	}
	
	private static com.google.protobuf.Parser<ActiveNodeInfo> PARSER =
            new com.google.protobuf.AbstractParser<ActiveNodeInfo>() {
          public ActiveNodeInfo parsePartialFrom(
              com.google.protobuf.CodedInputStream input,
              com.google.protobuf.ExtensionRegistryLite extensionRegistry)
              throws com.google.protobuf.InvalidProtocolBufferException {
            return new ActiveNodeInfo(input, extensionRegistry);
          }
        };	

	
}