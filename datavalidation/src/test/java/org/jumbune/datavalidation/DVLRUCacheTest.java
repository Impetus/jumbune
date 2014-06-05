package org.jumbune.datavalidation;

import java.util.Map;
import org.easymock.EasyMock;
import org.jumbune.datavalidation.DVLRUCache;
import org.junit.*;
import static org.junit.Assert.*;

public class DVLRUCacheTest {
	private DVLRUCache fixture = new DVLRUCache(0);


	public DVLRUCache getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testDVLRUCache_1()
		throws Exception {
		int capacity = 0;

		DVLRUCache result = new DVLRUCache(capacity);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testDVLRUCache_2()
		throws Exception {
		int capacity = 1;

		DVLRUCache result = new DVLRUCache(capacity);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testDVLRUCache_3()
		throws Exception {
		int capacity = 7;

		DVLRUCache result = new DVLRUCache(capacity);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testRemoveEldestEntry_fixture_1()
		throws Exception {
		DVLRUCache fixture2 = getFixture();
		java.util.Map.Entry<String, java.io.BufferedWriter> eldestEntry = EasyMock.createMock(java.util.Map.Entry.class);
		// add mock object expectations here

		EasyMock.replay(eldestEntry);

		boolean result = fixture2.removeEldestEntry(eldestEntry);

		EasyMock.verify(eldestEntry);
		assertEquals(false, result);
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