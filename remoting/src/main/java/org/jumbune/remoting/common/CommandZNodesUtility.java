package org.jumbune.remoting.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class CommandZNodesUtility. This class encapsulates all the functionalities related to command znodes those 
 * have to be created on zk as and when a new command comes in for the execution. It provides functionalities for 
 * creating, deleting and modifying the data for command znodes which are command ZNode, status ZNode and response ZNode, it does
 * not provide any mechanism to access the paths of all these ZNodes outside this class however. 
 * 
 */
public class CommandZNodesUtility {

	/** The logger. */
	private static final Logger LOGGER = LogManager
			.getLogger(CommandZNodesUtility.class);
	
	/** The Constant EMPTY_STRING. */
	private static final String EMPTY_STRING = "";
	
	/** The Constant CMD_ZNODE. */
	private static final String CMD_ZNODE = "/jumbune/commands/commandString";

	/** The Constant STATUS_ZNODE. */
	private static final String STATUS_ZNODE = "/jumbune/commands/status";

	/** The Constant RESPONSE_ZNODE. */
	private static final String RESPONSE_ZNODE = "/jumbune/commands/response";
	
	/** The zk. */
	private CuratorConnector zk = null;

	/** The command ZNode. */
	private String commandZNode = null;
	
	/** The status ZNode. */
	private String statusZNode = null;
	
	/** The response ZNode. */
	private String responseZNode = null;
	
	/**
	 * The Enum NodeType. Type of ZNodes which can be mutated,
	 * corresponding to a command.
	 */
	public enum NodeType {

		/** The response. */
		RESPONSE, 
		/** The status. */
		STATUS
	};

	/**
	 * Instantiates a new command znodes utility.
	 *
	 * @param zk the zk
	 * @param commandId the command id
	 */
	public CommandZNodesUtility(CuratorConnector zk, String commandId) {
		this.zk = zk;
        setZNodes(commandId);
	}

	/**
	 * Sets the paths of znodes.
	 *
	 * @param commandId the new ZNodes
	 */
	private void setZNodes(String commandId) {
		commandZNode = CMD_ZNODE + RemotingConstants.SLASH + commandId;
		statusZNode = STATUS_ZNODE + RemotingConstants.SLASH + commandId;
		responseZNode = RESPONSE_ZNODE + RemotingConstants.SLASH + commandId;
	}
	
	/**
	 * Creates the command znodes on zk.
	 */
	public void createCommandZnodesOnZK(){
		try {
			if(!zk.exists(commandZNode)){
				zk.createPath(commandZNode);				
			}
			if(!zk.exists(statusZNode)) {
				zk.createPath(statusZNode);				
			}
			if(!zk.exists(responseZNode)) {
				zk.createPath(responseZNode);				
			}
		} catch (Exception e) {
			LOGGER.warn("Nodes ["+commandZNode+", "+statusZNode+", "+responseZNode +"] cannot be created " + e.getMessage());
		}
	}
	
	/**
	 * Sets the command ZNode data. 
	 *
	 * @param data the new command ZNode data
	 */
	public void setCommandZNodeData(String data) {
		try {
			zk.setData(commandZNode, data);
		} catch (Exception e) {
			  LOGGER.error("Data cannot be set for znode " + commandZNode);
		}
	}

	/**
	 * Sets the status ZNode data.
	 *
	 * @param data the new status ZNode data
	 */
	public void setStatusZNodeData(String data) {
		try {
			zk.setData(statusZNode, data);
		} catch (Exception e) {
			  LOGGER.error("Data cannot be set for znode " + statusZNode);
		}
	}

	/**
	 * Sets the response ZNode data.
	 *
	 * @param data the new response ZNode data
	 */
	public void setResponseZNodeData(String data) {
		try {
			zk.setData(responseZNode, data);
		} catch (Exception e) {
			  LOGGER.error("Data cannot be set for znode " + responseZNode);
		}
	}

	/**
	 * Removes the associated znodes. 
	 * This method removes i.e deletes all the znodes that are bound to current instance of the class.
	 */
	public void removeAssociatedZNodes() {
		try {
			zk.deletePath(commandZNode);
			zk.deletePath(statusZNode);
			zk.deletePath(responseZNode);

		} catch (Exception e) {
			LOGGER.warn("Nodes ["+commandZNode+", "+statusZNode+", "+responseZNode +"] cannot be removed");
		}
	}
	
	/**
	 * Gets the command ZNode data.
	 *
	 * @return the command ZNode data
	 */
	public String getCommandZNodeData() {
		try {
			return zk.getData(commandZNode);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Gets the status ZNode data.
	 *
	 * @return the status ZNode data
	 */
	public String getStatusZNodeData() {
		try {
			return zk.getData(statusZNode);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Gets the response ZNode data.
	 *
	 * @return the response ZNode data
	 */
	public String getResponseZNodeData() {
		try {
			return zk.getData(responseZNode);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Poll for data changes. 
	 * This method keeps polling for any changes in data of ZNode which can either be a Status or Response Node
	 * corresponding to a command.
	 * <br><br>
	 * This is a blocking call.
	 * 
	 *
	 * @param nodeType the node type, see {@link NodeType}
	 * @param pollIntervalSecs the interval in seconds to poll and check ZNode.
	 * @return the contents of znode after a change has been detected.
	 */
	public String pollForDataChanges(NodeType nodeType, long pollIntervalSecs) {
		String path = getPathByNodeType(nodeType);
		String data = null;
		String updatedData = null;
		try{
    		data = zk.getData(path);
    		data = data == null ? EMPTY_STRING : data;
    		updatedData = data;
    		LOGGER.debug("polling for data changes on nodeType " + nodeType + " initial data - " + data);
			while(data.equals(updatedData)){
				Thread.sleep(pollIntervalSecs * 1000);
				updatedData = zk.getData(path);
			}						
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);			
		}
		LOGGER.debug("Data change detected on nodeType " + nodeType+"["+ path + "] final data - " + updatedData);
		return updatedData;
	}
	
	/**
	 * Gets the path by node type.
	 *
	 * @param nodeType the node type
	 * @return the path by node type
	 */
	private String getPathByNodeType(NodeType nodeType) {
	  if(NodeType.RESPONSE == nodeType) {
			return responseZNode;
		} else {
			return statusZNode;
		} 
	
	}

}
