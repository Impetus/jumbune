package org.jumbune.profiling.utils;

import static org.junit.Assert.assertTrue;

import org.jumbune.profiling.hprof.HeapAllocSitesBean.SiteDescriptor;
import org.jumbune.profiling.utils.HTFHeapAllocStackTraceBean;
import org.jumbune.profiling.utils.HeapSiteComparator;
import org.junit.Test;


public class HeapSiteComparatorTest {

	@Test
	public void comparTest() {
		HeapSiteComparator hsComaparator = new HeapSiteComparator();
		HTFHeapAllocStackTraceBean nextBean = new HTFHeapAllocStackTraceBean();
		HTFHeapAllocStackTraceBean currentBean = new HTFHeapAllocStackTraceBean();
		;
		SiteDescriptor heapAllocSiteBean = new SiteDescriptor();
		nextBean.setHeapAllocSiteBean(heapAllocSiteBean);
		currentBean.setHeapAllocSiteBean(heapAllocSiteBean);
		nextBean.getHeapAllocSiteBean().setBytesAllocated(4);
		currentBean.setHeapAllocSiteBean(heapAllocSiteBean);
		currentBean.getHeapAllocSiteBean().setBytesAllocated(4);
		float check = hsComaparator.compare(currentBean, nextBean);
		assertTrue(check == 0);
	}
}
