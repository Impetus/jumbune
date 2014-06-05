package org.jumbune.debugger.log.processing;

import java.util.Map;

/**
 * This is the class which contains parameters required corresponding to a Mapper or Reducer.
 */
public class MapReduceBean extends AbstractLogAnalysisBean {

	/**
	 * mapReduceMap - the map containing analysis results of various nodes on which the Mapper or Reducer is running.
	 */
	private Map<String, NodeBean> mapReduceMap;

	/**
	 * @return the mapReduceMap
	 */
	public final Map<String, NodeBean> getMapReduceMap() {
		return mapReduceMap;
	}

	/**
	 * @param mapReduceMap
	 *            the mapReduceMap to set
	 */
	public final void setMapReduceMap(final Map<String, NodeBean> mapReduceMap) {
		this.mapReduceMap = mapReduceMap;
	}

}
