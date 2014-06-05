package org.jumbune.profiling.beans;

import org.jumbune.profiling.beans.JMXDeamons;
import org.junit.*;


import static org.junit.Assert.*;

public class JMXDeamonsTest {
	private JMXDeamons fixture1 = JMXDeamons.DATA_NODE;


	private JMXDeamons fixture2 = JMXDeamons.JOB_TRACKER;


	private JMXDeamons fixture3 = JMXDeamons.NAME_NODE;


	private JMXDeamons fixture4 = JMXDeamons.TASK_TRACKER;


	public JMXDeamons getFixture1()
		throws Exception {
		return fixture1;
	}

	public JMXDeamons getFixture2()
		throws Exception {
		return fixture2;
	}

	public JMXDeamons getFixture3()
		throws Exception {
		return fixture3;
	}

	public JMXDeamons getFixture4()
		throws Exception {
		return fixture4;
	}

	@Test
	public void testToString_fixture1_1()
		throws Exception {
		JMXDeamons fixture = getFixture1();

		String result = fixture.toString();

		assertEquals("DataNode", result);
	}

	@Test
	public void testToString_fixture2_1()
		throws Exception {
		JMXDeamons fixture = getFixture2();

		String result = fixture.toString();

		assertEquals("JobTracker", result);
	}

	@Test
	public void testToString_fixture3_1()
		throws Exception {
		JMXDeamons fixture = getFixture3();

		String result = fixture.toString();

		assertEquals("NameNode", result);
	}

	@Test
	public void testToString_fixture4_1()
		throws Exception {
		JMXDeamons fixture = getFixture4();

		String result = fixture.toString();

		assertEquals("TaskTracker", result);
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