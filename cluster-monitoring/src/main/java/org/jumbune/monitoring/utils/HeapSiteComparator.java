/**
 * 
 */
package org.jumbune.monitoring.utils;

import java.util.Comparator;

/**
 * The Class HeapSiteComparator is used to compare the current and next heap allocation stack trace.
 */
public class HeapSiteComparator implements Comparator<HTFHeapAllocStackTraceBean> {

	@Override
	public int compare(final HTFHeapAllocStackTraceBean currentBean, final HTFHeapAllocStackTraceBean nextBean) {
		final Float nextBeanBytesAlloc = nextBean.getHeapAllocSiteBean().getBytesAllocated();
		return nextBeanBytesAlloc.compareTo(currentBean.getHeapAllocSiteBean().getBytesAllocated());
	}

}
