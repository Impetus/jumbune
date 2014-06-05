package org.jumbune.profiling.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.jumbune.profiling.hprof.HeapAllocSitesBean;
import org.jumbune.profiling.utils.HTFHeapAllocStackTraceBean;
import org.junit.*;
import static org.junit.Assert.*;

public class HTFHeapAllocStackTraceBeanTest {
	private HTFHeapAllocStackTraceBean fixture;


	public HTFHeapAllocStackTraceBean getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testGetHeapAllocSiteBean_fixture_1()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();

		org.jumbune.profiling.hprof.HeapAllocSitesBean.SiteDescriptor result = fixture2.getHeapAllocSiteBean();

		assertEquals(null, result);
	}

	@Test
	public void testGetStackTraceList_fixture_1()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();

		List<String> result = fixture2.getStackTraceList();

		assertEquals(null, result);
	}

	@Test
	public void testSetHeapAllocSiteBean_fixture_1()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		org.jumbune.profiling.hprof.HeapAllocSitesBean.SiteDescriptor heapAllocSiteBean = new org.jumbune.profiling.hprof.HeapAllocSitesBean.SiteDescriptor();

		fixture2.setHeapAllocSiteBean(heapAllocSiteBean);

	}

	@Test
	public void testSetStackTraceList_fixture_1()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		ArrayList<String> stackTraceList = new ArrayList<String>();
		stackTraceList.add("");

		fixture2.setStackTraceList(stackTraceList);

	}

	@Test
	public void testSetStackTraceList_fixture_2()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		ArrayList<String> stackTraceList = new ArrayList<String>();
		stackTraceList.add("");
		stackTraceList.add("0123456789");

		fixture2.setStackTraceList(stackTraceList);

	}

	@Test
	public void testSetStackTraceList_fixture_3()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		ArrayList<String> stackTraceList = new ArrayList<String>();
		stackTraceList.add("0123456789");

		fixture2.setStackTraceList(stackTraceList);

	}

	@Test
	public void testSetStackTraceList_fixture_4()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		LinkedList<String> stackTraceList = new LinkedList<String>();
		stackTraceList.add("");

		fixture2.setStackTraceList(stackTraceList);

	}

	@Test
	public void testSetStackTraceList_fixture_5()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		LinkedList<String> stackTraceList = new LinkedList<String>();
		stackTraceList.add("");
		stackTraceList.add("0123456789");

		fixture2.setStackTraceList(stackTraceList);

	}

	@Test
	public void testSetStackTraceList_fixture_6()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		LinkedList<String> stackTraceList = new LinkedList<String>();
		stackTraceList.add("0123456789");

		fixture2.setStackTraceList(stackTraceList);

	}

	@Test
	public void testSetStackTraceList_fixture_7()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		Vector<String> stackTraceList = new Vector<String>();
		stackTraceList.add("");

		fixture2.setStackTraceList(stackTraceList);

	}

	@Test
	public void testSetStackTraceList_fixture_8()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		Vector<String> stackTraceList = new Vector<String>();
		stackTraceList.add("");
		stackTraceList.add("0123456789");

		fixture2.setStackTraceList(stackTraceList);

	}

	@Test
	public void testSetStackTraceList_fixture_9()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		Vector<String> stackTraceList = new Vector<String>();
		stackTraceList.add("0123456789");

		fixture2.setStackTraceList(stackTraceList);

	}

	@Test
	public void testSetStackTraceList_fixture_10()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		List<String> stackTraceList = new ArrayList<String>();

		fixture2.setStackTraceList(stackTraceList);

	}

	@Test
	public void testSetStackTraceList_fixture_11()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		List<String> stackTraceList = new LinkedList<String>();

		fixture2.setStackTraceList(stackTraceList);

	}

	@Test
	public void testSetStackTraceList_fixture_12()
		throws Exception {
		HTFHeapAllocStackTraceBean fixture2 = getFixture();
		List<String> stackTraceList = new Vector<String>();

		fixture2.setStackTraceList(stackTraceList);

	}

	@Before
	public void setUp()
		throws Exception {
		fixture = new HTFHeapAllocStackTraceBean();
	}

	@After
	public void tearDown()
		throws Exception {
	}
}