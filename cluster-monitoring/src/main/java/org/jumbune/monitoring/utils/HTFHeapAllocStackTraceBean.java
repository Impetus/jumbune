/**
 * 
 */
package org.jumbune.monitoring.utils;

import java.util.List;

import org.jumbune.monitoring.hprof.HeapAllocSitesBean;


/**
 * A wrapper on HeapAllocSitesBean and combines StackTrace info along with HeapAllocSitesBean
 */
public class HTFHeapAllocStackTraceBean {
	/** The HeapAllocSitesBean.SiteDescriptor and it holds information about HeapUsage */
	private HeapAllocSitesBean.SiteDescriptor heapAllocSiteBean;

	/** The stack trace linked with a HeapAllocSitesBean.SiteDescriptor */
	private List<String> stackTraceList;

	/**
	 * Get the HeapAllocSitesBean.SiteDescriptor
	 * 
	 * @return HeapAllocSitesBean.SiteDescriptor
	 */
	public HeapAllocSitesBean.SiteDescriptor getHeapAllocSiteBean() {
		return heapAllocSiteBean;
	}

	/**
	 * Set HeapAllocSitesBean.SiteDescriptor
	 * 
	 * @param heapAllocSiteBean
	 */
	public void setHeapAllocSiteBean(final HeapAllocSitesBean.SiteDescriptor heapAllocSiteBean) {
		this.heapAllocSiteBean = heapAllocSiteBean;
	}

	/**
	 * Get stack trace linked to this HeapAllocSitesBean.SiteDescriptor
	 * 
	 * @return List<String> of stacktrace
	 */
	public List<String> getStackTraceList() {
		return stackTraceList;
	}

	/**
	 * Set StackTrace of this HeapAllocSitesBean.SiteDescriptor
	 * 
	 * @param stackTraceList
	 */
	public void setStackTraceList(final List<String> stackTraceList) {
		this.stackTraceList = stackTraceList;
	}
}
