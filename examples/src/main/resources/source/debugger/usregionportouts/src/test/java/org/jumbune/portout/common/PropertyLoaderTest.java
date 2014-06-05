package org.jumbune.portout.common;

import java.util.Map;

import org.jumbune.portout.common.PropertyLoader;
import org.junit.*;
import static org.junit.Assert.*;

public class PropertyLoaderTest {
	@Test
	public void testGetAllSPIDNameMap_1()
		throws Exception {

		Map<String, String> result = PropertyLoader.getAllSPIDNameMap();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetIntProperty_1()
		throws Exception {
		String key = "";

		int result = PropertyLoader.getIntProperty(key);

		assertEquals(-100, result);
	}

	@Test
	public void testGetIntProperty_2()
		throws Exception {
		String key = "0123456789";

		int result = PropertyLoader.getIntProperty(key);

		assertEquals(-100, result);
	}

	@Test
	public void testGetProperty_1()
		throws Exception {
		String key = "";

		String result = PropertyLoader.getProperty(key);

		assertEquals(null, result);
	}

	@Test
	public void testGetProperty_2()
		throws Exception {
		String key = "0123456789";

		String result = PropertyLoader.getProperty(key);

		assertEquals(null, result);
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