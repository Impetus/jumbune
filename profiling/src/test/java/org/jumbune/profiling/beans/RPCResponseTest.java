package org.jumbune.profiling.beans;

import java.util.HashMap;
import java.util.Map;

import org.jumbune.profiling.beans.RPCResponse;
import org.junit.*;


import static org.junit.Assert.*;

public class RPCResponseTest {
	private RPCResponse fixture = new RPCResponse();


	public RPCResponse getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testGetDataNode_fixture_1()
		throws Exception {
		RPCResponse fixture2 = getFixture();

		Map<String, String> result = fixture2.getDataNode();

		assertEquals(null, result);
	}

	@Test
	public void testGetTaskTracker_fixture_1()
		throws Exception {
		RPCResponse fixture2 = getFixture();

		Map<String, String> result = fixture2.getTaskTracker();

		assertEquals(null, result);
	}

	@Test
	public void testSetDataNode_fixture_1()
		throws Exception {
		RPCResponse fixture2 = getFixture();
		HashMap<String, String> dataNode = new HashMap<String, String>();
		dataNode.put("", "");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_2()
		throws Exception {
		RPCResponse fixture2 = getFixture();
		HashMap<String, String> dataNode = new HashMap<String, String>();
		dataNode.put("", "");
		dataNode.put("0123456789", "0123456789");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_3()
		throws Exception {
		RPCResponse fixture2 = getFixture();
		HashMap<String, String> dataNode = new HashMap<String, String>();
		dataNode.put("0123456789", "0123456789");

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetDataNode_fixture_4()
		throws Exception {
		RPCResponse fixture2 = getFixture();
		Map<String, String> dataNode = new HashMap<String, String>();

		fixture2.setDataNode(dataNode);

	}

	@Test
	public void testSetTaskTracker_fixture_1()
		throws Exception {
		RPCResponse fixture2 = getFixture();
		HashMap<String, String> taskTracker = new HashMap<String, String>();
		taskTracker.put("", "");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_2()
		throws Exception {
		RPCResponse fixture2 = getFixture();
		HashMap<String, String> taskTracker = new HashMap<String, String>();
		taskTracker.put("", "");
		taskTracker.put("0123456789", "0123456789");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_3()
		throws Exception {
		RPCResponse fixture2 = getFixture();
		HashMap<String, String> taskTracker = new HashMap<String, String>();
		taskTracker.put("0123456789", "0123456789");

		fixture2.setTaskTracker(taskTracker);

	}

	@Test
	public void testSetTaskTracker_fixture_4()
		throws Exception {
		RPCResponse fixture2 = getFixture();
		Map<String, String> taskTracker = new HashMap<String, String>();

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