package org.jumbune.monitoring.utils;

import java.util.Map;

import org.jumbune.monitoring.hprof.CPUSamplesBean.SampleDescriptor;


/**
 * This class holds HeapSample Info and CPU sample info
 * 
  */
public class ProfilerBean {

	/**
	 * A map that holds CPU Sample info based on rank. The method that takes maximum CPU time will be ranked 1.
	 */
	private Map<Integer, SampleDescriptor> cpuSample;

	/**
	 * A map that stores HeapAllocStackTraceBean according to rank. The key will be rank based on descending order of bytesAllocated in
	 * HeapAllocSitesBean.SiteDescriptor
	 */
	private Map<Integer, HTFHeapAllocStackTraceBean> heapAllocation;

	/**
	 * Get CPU sample map
	 * 
	 * @return Map of ranked CPU sample
	 */
	public Map<Integer, SampleDescriptor> getCpuSample() {
		return cpuSample;
	}

	/**
	 * Set CPU sample Map
	 * 
	 * @param cpuSample
	 */
	public void setCpuSample(final Map<Integer, SampleDescriptor> cpuSample) {
		this.cpuSample = cpuSample;
	}

	/**
	 * Get HeapSite sample map
	 * 
	 * @return
	 */
	public Map<Integer, HTFHeapAllocStackTraceBean> getHeapAllocation() {
		return heapAllocation;
	}

	/**
	 * Set map of Heap Site
	 * 
	 * @param heapAllocation
	 */
	public void setHeapAllocation(final Map<Integer, HTFHeapAllocStackTraceBean> heapAllocation) {
		this.heapAllocation = heapAllocation;
	}
}