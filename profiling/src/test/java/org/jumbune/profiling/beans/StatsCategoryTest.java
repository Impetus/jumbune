package org.jumbune.profiling.beans;

import org.jumbune.profiling.beans.StatsCategory;
import org.junit.*;


import static org.junit.Assert.*;

public class StatsCategoryTest {
	private StatsCategory fixture1 = StatsCategory.JMX_STATS;


	private StatsCategory fixture2 = StatsCategory.OS_STATS;


	public StatsCategory getFixture1()
		throws Exception {
		return fixture1;
	}

	public StatsCategory getFixture2()
		throws Exception {
		return fixture2;
	}

	@Test
	public void testGetDisplayValue_fixture1_1()
		throws Exception {
		StatsCategory fixture = getFixture1();

		String result = fixture.getDisplayValue();

		assertEquals("JMX Stats", result);
	}

	@Test
	public void testGetDisplayValue_fixture2_1()
		throws Exception {
		StatsCategory fixture = getFixture2();

		String result = fixture.getDisplayValue();

		assertEquals("OS Stats", result);
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