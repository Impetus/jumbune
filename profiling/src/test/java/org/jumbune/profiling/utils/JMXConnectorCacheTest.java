package org.jumbune.profiling.utils;

import java.util.Map;
import org.easymock.EasyMock;
import org.jumbune.profiling.utils.JMXConnectorCache;
import org.junit.*;


import static org.junit.Assert.*;

public class JMXConnectorCacheTest {
	private JMXConnectorCache fixture;


	public JMXConnectorCache getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testJMXConnectorCache_1()
		throws Exception {
		int capacity = 0;

		JMXConnectorCache result = new JMXConnectorCache(capacity);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testJMXConnectorCache_2()
		throws Exception {
		int capacity = 1;

		JMXConnectorCache result = new JMXConnectorCache(capacity);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testJMXConnectorCache_3()
		throws Exception {
		int capacity = 7;

		JMXConnectorCache result = new JMXConnectorCache(capacity);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testRemoveEldestEntry_fixture_1()
		throws Exception {
		JMXConnectorCache fixture2 = getFixture();
		java.util.Map.Entry<String, javax.management.remote.JMXConnector> eldest = EasyMock.createMock(java.util.Map.Entry.class);
		// add mock object expectations here

		EasyMock.replay(eldest);

		boolean result = fixture2.removeEldestEntry(eldest);

		EasyMock.verify(eldest);
		assertEquals(false, result);
	}

	@Before
	public void setUp()
		throws Exception {
		fixture = new JMXConnectorCache(0);
		}

	@After
	public void tearDown()
		throws Exception {
	}
}