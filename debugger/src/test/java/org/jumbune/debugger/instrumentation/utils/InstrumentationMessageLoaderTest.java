package org.jumbune.debugger.instrumentation.utils;

import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.junit.*;


import static org.junit.Assert.*;

public class InstrumentationMessageLoaderTest {
//	@Test
//	public void testInstrumentationMessageLoader_1()
//		throws Exception {
//		InstrumentationMessageLoader result = new InstrumentationMessageLoader();
//		assertNotNull(result);
//	}

	@Test
	public void testGetMessage_1()
		throws Exception {
		int key = 0;

		String result = InstrumentationMessageLoader.getMessage(key);

		assertEquals(null, result);
	}

	@Test
	public void testGetMessage_2()
		throws Exception {
		int key = 1;

		String result = InstrumentationMessageLoader.getMessage(key);

		assertEquals(null, result);
	}

	@Test
	public void testGetMessage_3()
		throws Exception {
		int key = 7;

		String result = InstrumentationMessageLoader.getMessage(key);

		assertEquals(null, result);
	}

	@Test
	public void testGetMessage_4()
		throws Exception {
		String key = "";

		String result = InstrumentationMessageLoader.getMessage(key);

		assertEquals(null, result);
	}

	@Test
	public void testGetMessage_5()
		throws Exception {
		String key = "0123456789";

		String result = InstrumentationMessageLoader.getMessage(key);

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