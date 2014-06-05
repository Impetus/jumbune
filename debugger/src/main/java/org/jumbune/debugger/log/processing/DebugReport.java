package org.jumbune.debugger.log.processing;

import java.util.List;


/**
 * The Class DebugReport.
 */
public class DebugReport {

	/** The mapper reducer names. */
	private List<MapReduceJobBean> mapperReducerNames;
	
	/** The reducer info. */
	private List<ReducerInfo> reducerInfo;

	/**
	 * Sets the mapper reducer names.
	 *
	 * @param mapperReducerNames the mapperReducerNames to set
	 */
	public void setMapperReducerNames(List<MapReduceJobBean> mapperReducerNames) {
		this.mapperReducerNames = mapperReducerNames;
	}

	/**
	 * Gets the mapper reducer names.
	 *
	 * @return the mapperReducerNames
	 */
	public List<MapReduceJobBean> getMapperReducerNames() {
		return mapperReducerNames;
	}

	/**
	 * Sets the reducer info.
	 *
	 * @param reducerInfo the reducerInfo to set
	 */
	public void setReducerInfo(List<ReducerInfo> reducerInfo) {
		this.reducerInfo = reducerInfo;
	}

	/**
	 * Gets the reducer info.
	 *
	 * @return the reducerInfo
	 */
	public List<ReducerInfo> getReducerInfo() {
		return reducerInfo;
	}

}
