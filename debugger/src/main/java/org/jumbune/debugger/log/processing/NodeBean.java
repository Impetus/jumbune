package org.jumbune.debugger.log.processing;

import java.util.Map;

/**
 * This is the class which contains parameters required corresponding to a Node.
*/
public class NodeBean extends AbstractLogAnalysisBean {

	/**
	 * nodeMap - the map containing analysis results of various instances of Mappers and Reducers running on the node.
	 */
	private Map<String, MapReduceInstanceBean> nodeMap;

	/**
	 * @return the nodeMap
	 */
	public final Map<String, MapReduceInstanceBean> getNodeMap() {
		return nodeMap;
	}

	/**
	 * @param nodeMap
	 *            the nodeMap to set
	 */
	public final void setNodeMap(final Map<String, MapReduceInstanceBean> nodeMap) {
		this.nodeMap = nodeMap;
	}

}
