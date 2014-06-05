package org.jumbune.portout;

import java.util.List;

import org.jumbune.portout.PortoutHelper;
import org.junit.*;
import static org.junit.Assert.*;

public class PortoutHelperTest {
	@Test
	public void testIsEmptyOrNull_1()
		throws Exception {
		String s = "";

		boolean result = PortoutHelper.isEmptyOrNull(s);

		assertEquals(true, result);
	}

	@Test
	public void testIsEmptyOrNull_2()
		throws Exception {
		String s = "0";

		boolean result = PortoutHelper.isEmptyOrNull(s);

		assertEquals(false, result);
	}

	@Test
	public void testIsEmptyOrNull_3()
		throws Exception {
		String s = "null";

		boolean result = PortoutHelper.isEmptyOrNull(s);

		assertEquals(true, result);
	}

	@Test
	public void testIsEmptyOrNull_4()
		throws Exception {
		String s = null;

		boolean result = PortoutHelper.isEmptyOrNull(s);

		assertEquals(true, result);
	}

	@Test
	public void testIsValid_1()
		throws Exception {
		String value = "";
		String requiredValue = "";

		boolean result = PortoutHelper.isValid(value, requiredValue);

		assertEquals(true, result);
	}

	@Test
	public void testIsValid_2()
		throws Exception {
		String value = "0123456789";
		String requiredValue = "0123456789";

		boolean result = PortoutHelper.isValid(value, requiredValue);

		assertEquals(true, result);
	}

	@Test
	public void testIsValid_3()
		throws Exception {
		String value = "0123456789";
		String requiredValue = "";

		boolean result = PortoutHelper.isValid(value, requiredValue);

		assertEquals(true, result);
	}

	@Test
	public void testIsValid_4()
		throws Exception {
		String value = "";
		String requiredValue = "0123456789";

		boolean result = PortoutHelper.isValid(value, requiredValue);

		assertEquals(false, result);
	}

	@Test
	public void testTokenize_1()
		throws Exception {
		String source = "";
		String delim = "";

		List<String> result = PortoutHelper.tokenize(source, delim);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testTokenize_2()
		throws Exception {
		String source = "0123456789";
		String delim = "0123456789";

		List<String> result = PortoutHelper.tokenize(source, delim);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testTokenize_3()
		throws Exception {
		String source = "0123456789";
		String delim = "";

		List<String> result = PortoutHelper.tokenize(source, delim);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.contains("0123456789"));
	}

	@Test
	public void testTokenize_4()
		throws Exception {
		String source = "";
		String delim = "0123456789";

		List<String> result = PortoutHelper.tokenize(source, delim);

		assertNotNull(result);
		assertEquals(0, result.size());
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