package org.jumbune.profiling.utils;

import java.lang.reflect.Field;

import org.jumbune.profiling.utils.HeapAllocStackTraceExclStrat;
import org.junit.*;

import static org.junit.Assert.*;

import com.google.gson.FieldAttributes;

public class HeapAllocStackTraceExclStratTest {
	private HeapAllocStackTraceExclStrat fixture;

	public HeapAllocStackTraceExclStrat getFixture()
		throws Exception {
		return fixture;
	}

	
	@Test
	public void testShouldSkipField_fixture_1()
		throws Exception {
		HeapAllocStackTraceExclStrat fixture2 = getFixture();
		FieldAttributes fieldAttrib = new FieldAttributes(Boolean.class.getFields()[0]);

		boolean result = fixture2.shouldSkipField(fieldAttrib);

		assertEquals(false, result);
	}

	@Before
	public void setUp()
		throws Exception {
		fixture = new HeapAllocStackTraceExclStrat();

	}

	@After
	public void tearDown()
		throws Exception {
	}
}