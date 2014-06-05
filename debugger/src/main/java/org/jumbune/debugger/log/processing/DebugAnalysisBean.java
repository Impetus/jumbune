package org.jumbune.debugger.log.processing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is for storing all the information to be returned consisting information regarding log analysis, MapReduce and Job chaining, and Partitioner
 * 
 */
public class DebugAnalysisBean {
	/**
	 * sampled hdfs path which stores result of sampling
	 */
	private String sampledHDFSPath;
	/**
	 * logMap - Map to store the final cluster-wide result of log analysis.
	 */
	private Map<String, JobBean> logMap;

	/**
	 * jobChain - List to store job chaining information.
	 */
	private List<ChainingInfoBean> jobChain;

	/**
	 * mrChain - Map to store MapReduce chaining information.
	 */
	private Map<String, Map<String, List<ChainingInfoBean>>> mrChain = new HashMap<String, Map<String, List<ChainingInfoBean>>>();

	/**
	 * partitionerMap - Map to store information related to the performance of partitioner
	 */
	private Map<String, List<PartitionerInfoBean>> partitionerMap = new HashMap<String, List<PartitionerInfoBean>>();

	/**
	 * @return the logMap
	 */
	public Map<String, JobBean> getLogMap() {
		return logMap;
	}

	/**
	 * @param logMap
	 *            the logMap to set
	 */
	public void setLogMap(Map<String, JobBean> logMap) {
		this.logMap = logMap;
	}

	/**
	 * @return the jobChainappers.old.PortOutReportMapper", "inputKeys": 100000, "contextWrites": 229 }, { "name":
	 *         "org.neustar.portps.mappers.old.Mapper1", "inputKeys": 229, "contextWrites": 229 }, { "name":
	 *         "org.neustar.portps.mappers.old.Mapper2", "inputKeys": 229, "contextWrites": 229 }, { "name":
	 *         "org.neustar.portps.mappers.old.Mapper3", "inputKeys": 229, "contextWrites": 229 } ]
	 */
	public List<ChainingInfoBean> getJobChain() {
		return jobChain;
	}

	/**
	 * @param jobChain
	 *            the jobChain to set
	 */
	public void setJobChain(List<ChainingInfoBean> jobChain) {
		this.jobChain = jobChain;
	}

	/**
	 * @return the mrChain
	 */
	public Map<String, Map<String, List<ChainingInfoBean>>> getMrChain() {
		return mrChain;
	}

	/**
	 * @param mrChain
	 *            the mrChain to set
	 */
	public void setMrChain(Map<String, Map<String, List<ChainingInfoBean>>> mrChain) {
		this.mrChain = mrChain;
	}

	/**
	 * @return the partitionerMap
	 */
	public Map<String, List<PartitionerInfoBean>> getPartitionerMap() {
		return partitionerMap;
	}

	/**
	 * @param partitionerMap
	 *            the partitionerMap to set
	 */
	public void setPartitionerMap(Map<String, List<PartitionerInfoBean>> partitionerMap) {
		this.partitionerMap = partitionerMap;
	}

	/**
	 * @param sampledHDFSPath the sampledHDFSPath to set
	 */
	public void setSampledHDFSPath(String sampledHDFSPath) {
		this.sampledHDFSPath = sampledHDFSPath;
	}

	/**
	 * @return the sampledHDFSPath
	 */
	public String getSampledHDFSPath() {
		return sampledHDFSPath;
	}

}
