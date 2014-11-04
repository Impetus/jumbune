package org.jumbune.profiling.beans;

import java.util.Map;


/**
 * Pojo to store the response of Hadoop JMX stats for a node
 */
public class HadoopJMXResponse {
	private Map<String, String> dataNode;
	private Map<String, String> taskTracker;
	private Map<String, String> nodeManager;


	/**
	 * @param dataNode the dataNode to set
	 */
	public void setDataNode(Map<String, String> dataNode) {
		this.dataNode = dataNode;
	}

	/**
	 * @return the dataNode
	 */
	public Map<String, String> getDataNode() {
		return dataNode;
	}

	/**
	 * @param taskTracker the taskTracker to set
	 */
	public void setTaskTracker(Map<String, String> taskTracker) {
		this.taskTracker = taskTracker;
	}

	/**
	 * @return the taskTracker
	 */
	public Map<String, String> getTaskTracker() {
		return taskTracker;
	}
	@Override
	public String toString() {
		StringBuilder stringBuilder=new StringBuilder();
		if(dataNode!=null && !dataNode.isEmpty()){
			for (Map.Entry<String, String> entry : dataNode.entrySet()) {
				stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append(", ");
			}
		}
		if(taskTracker!=null && !taskTracker.isEmpty()){
			for (Map.Entry<String, String> entry : taskTracker.entrySet()) {
				stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append(", ");
			}
		}
		if(nodeManager!=null && !nodeManager.isEmpty()){
          for (Map.Entry<String, String> entry : nodeManager.entrySet()) {
              stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append(", ");
          }
        }
		stringBuilder.substring(0,stringBuilder.length()-1);
		
		return stringBuilder.toString();
	}

  /**
   * @return the nodeManager
   */
  public Map<String, String> getNodeManager() {
    return nodeManager;
  }

  /**
   * @param nodeManager the nodeManager to set
   */
  public void setNodeManager(Map<String, String> nodeManager) {
    this.nodeManager = nodeManager;
  }

}
