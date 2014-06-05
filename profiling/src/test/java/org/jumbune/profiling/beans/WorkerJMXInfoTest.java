package org.jumbune.profiling.beans;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.jumbune.profiling.beans.WorkerJMXInfo;
import org.junit.*;


import static org.junit.Assert.*;

public class WorkerJMXInfoTest {
	private WorkerJMXInfo fixture = new WorkerJMXInfo();


	public WorkerJMXInfo getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testGetDataNode_fixture_1()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();

		List<String> result = fixture2.getDataNode();

		assertEquals(null, result);
	}

	@Test
	public void testGetTaskTracker_fixture_1()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();

		List<String> result = fixture2.getTaskTracker();

		assertEquals(null, result);
	}

	@Test
	public void testSetDataNode_fixture_1()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		ArrayList<String> dataNode = new ArrayList<String>();
		dataNode.add("");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_2()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		ArrayList<String> dataNode = new ArrayList<String>();
		dataNode.add("");
		dataNode.add("0123456789");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_3()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		ArrayList<String> dataNode = new ArrayList<String>();
		dataNode.add("0123456789");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_4()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		LinkedList<String> dataNode = new LinkedList<String>();
		dataNode.add("");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_5()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		LinkedList<String> dataNode = new LinkedList<String>();
		dataNode.add("");
		dataNode.add("0123456789");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_6()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		LinkedList<String> dataNode = new LinkedList<String>();
		dataNode.add("0123456789");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_7()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		Vector<String> dataNode = new Vector<String>();
		dataNode.add("");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_8()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		Vector<String> dataNode = new Vector<String>();
		dataNode.add("");
		dataNode.add("0123456789");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_9()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		Vector<String> dataNode = new Vector<String>();
		dataNode.add("0123456789");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_10()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		List<String> dataNode = new ArrayList<String>();

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_11()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		List<String> dataNode = new LinkedList<String>();

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_12()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		List<String> dataNode = new Vector<String>();

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetTaskTracker_fixture_1()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		ArrayList<String> taskTracker = new ArrayList<String>();
		taskTracker.add("");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_2()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		ArrayList<String> taskTracker = new ArrayList<String>();
		taskTracker.add("");
		taskTracker.add("0123456789");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_3()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		ArrayList<String> taskTracker = new ArrayList<String>();
		taskTracker.add("0123456789");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_4()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		LinkedList<String> taskTracker = new LinkedList<String>();
		taskTracker.add("");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_5()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		LinkedList<String> taskTracker = new LinkedList<String>();
		taskTracker.add("");
		taskTracker.add("0123456789");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_6()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		LinkedList<String> taskTracker = new LinkedList<String>();
		taskTracker.add("0123456789");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_7()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		Vector<String> taskTracker = new Vector<String>();
		taskTracker.add("");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_8()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		Vector<String> taskTracker = new Vector<String>();
		taskTracker.add("");
		taskTracker.add("0123456789");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_9()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		Vector<String> taskTracker = new Vector<String>();
		taskTracker.add("0123456789");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_10()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		List<String> taskTracker = new ArrayList<String>();

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_11()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		List<String> taskTracker = new LinkedList<String>();

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_12()
		throws Exception {
		WorkerJMXInfo fixture2 = getFixture();
		List<String> taskTracker = new Vector<String>();

		fixture2.setTaskTracker(taskTracker);

	}

	@Before
	public void setUp()
		throws Exception {
	}

	@After
	public void tearDown()
		throws Exception {
	}
}