package org.jumbune.profiling.helper;

import org.jumbune.profiling.utils.PropertyHelper;
import org.junit.*;


import static org.junit.Assert.*;

public class PropertyHelperTest {
	private PropertyHelper fixture;


	public PropertyHelper getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testPropertyHelper_1()
		throws Exception {

		PropertyHelper result = new PropertyHelper();

		assertNotNull(result);
	}

	@Test
	public void testGetProperty_fixture_1()
		throws Exception {
		PropertyHelper fixture2 = getFixture();
		String key = "";

		String result = fixture2.getProperty(key);

		assertEquals("", result);
	}

	@Test
	public void testGetProperty_fixture_2()
		throws Exception {
		PropertyHelper fixture2 = getFixture();
		String key = "0123456789";

		String result = fixture2.getProperty(key);

		assertEquals(null, result);
	}

	@Test
	public void testSplitPropertyValue_fixture_1()
		throws Exception {
		PropertyHelper fixture2 = getFixture();
		String key = "";
		String delimiter = "";

		String[] result = fixture2.splitPropertyValue(key, delimiter);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("", result[0]);
	}

	@Test
	public void testSplitPropertyValue_fixture_2()
		throws Exception {
		PropertyHelper fixture2 = getFixture();
		String key = "0123456789";
		String delimiter = "0123456789";

		String[] result = fixture2.splitPropertyValue(key, delimiter);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("", result[0]);
	}

	@Test
	public void testSplitPropertyValue_fixture_3()
		throws Exception {
		PropertyHelper fixture2 = getFixture();
		String key = "0123456789";
		String delimiter = "";

		String[] result = fixture2.splitPropertyValue(key, delimiter);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("", result[0]);
	}

	@Test
	public void testSplitPropertyValue_fixture_4()
		throws Exception {
		PropertyHelper fixture2 = getFixture();
		String key = "";
		String delimiter = "0123456789";

		String[] result = fixture2.splitPropertyValue(key, delimiter);

		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals("", result[0]);
	}

	@Before
	public void setUp()
		throws Exception {
		fixture = new PropertyHelper();
	}

	@After
	public void tearDown()
		throws Exception {
	}
}