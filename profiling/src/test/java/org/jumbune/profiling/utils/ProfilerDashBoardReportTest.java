package org.jumbune.profiling.utils;

import java.util.HashMap;
import java.util.Map;

import org.jumbune.profiling.utils.ProfilerDashBoardReport;
import org.junit.*;


import static org.junit.Assert.*;

public class ProfilerDashBoardReportTest {
	private ProfilerDashBoardReport fixture = new ProfilerDashBoardReport();


	public ProfilerDashBoardReport getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testGetCpuSampleReducer_fixture_1()
		throws Exception {
		ProfilerDashBoardReport fixture2 = getFixture();

		Map<Float, String> result = fixture2.getCpuSampleReducer();

		assertEquals(null, result);
	}

	@Test
	public void testGetCpuSamplesMapper_fixture_1()
		throws Exception {
		ProfilerDashBoardReport fixture2 = getFixture();

		Map<Float, String> result = fixture2.getCpuSamplesMapper();

		assertEquals(null, result);
	}

	@Test
	public void testSetCpuSampleReducer_fixture_1()
		throws Exception {
		ProfilerDashBoardReport fixture2 = getFixture();
		HashMap<Float, String> cpuSampleReducer = new HashMap<Float, String>();
		cpuSampleReducer.put(new Float(-1.0f), "");

		fixture2.setCpuSampleReducer(cpuSampleReducer);

	}

	@Test
	public void testSetCpuSampleReducer_fixture_2()
		throws Exception {
		ProfilerDashBoardReport fixture2 = getFixture();
		HashMap<Float, String> cpuSampleReducer = new HashMap<Float, String>();
		cpuSampleReducer.put(new Float(-1.0f), "");
		cpuSampleReducer.put(new Float(0.0f), "0123456789");

		fixture2.setCpuSampleReducer(cpuSampleReducer);

	}

	@Test
	public void testSetCpuSampleReducer_fixture_3()
		throws Exception {
		ProfilerDashBoardReport fixture2 = getFixture();
		HashMap<Float, String> cpuSampleReducer = new HashMap<Float, String>();
		cpuSampleReducer.put(new Float(0.0f), "0123456789");

		fixture2.setCpuSampleReducer(cpuSampleReducer);

	}

	@Test
	public void testSetCpuSampleReducer_fixture_4()
		throws Exception {
		ProfilerDashBoardReport fixture2 = getFixture();
		Map<Float, String> cpuSampleReducer = new HashMap<Float, String>();

		fixture2.setCpuSampleReducer(cpuSampleReducer);

	}

	@Test
	public void testSetCpuSamplesMapper_fixture_1()
		throws Exception {
		ProfilerDashBoardReport fixture2 = getFixture();
		HashMap<Float, String> cpuSamplesMapper = new HashMap<Float, String>();
		cpuSamplesMapper.put(new Float(-1.0f), "");

		fixture2.setCpuSamplesMapper(cpuSamplesMapper);

	}

	@Test
	public void testSetCpuSamplesMapper_fixture_2()
		throws Exception {
		ProfilerDashBoardReport fixture2 = getFixture();
		HashMap<Float, String> cpuSamplesMapper = new HashMap<Float, String>();
		cpuSamplesMapper.put(new Float(-1.0f), "");
		cpuSamplesMapper.put(new Float(0.0f), "0123456789");

		fixture2.setCpuSamplesMapper(cpuSamplesMapper);

	}

	@Test
	public void testSetCpuSamplesMapper_fixture_3()
		throws Exception {
		ProfilerDashBoardReport fixture2 = getFixture();
		HashMap<Float, String> cpuSamplesMapper = new HashMap<Float, String>();
		cpuSamplesMapper.put(new Float(0.0f), "0123456789");

		fixture2.setCpuSamplesMapper(cpuSamplesMapper);

	}

	@Test
	public void testSetCpuSamplesMapper_fixture_4()
		throws Exception {
		ProfilerDashBoardReport fixture2 = getFixture();
		Map<Float, String> cpuSamplesMapper = new HashMap<Float, String>();

		fixture2.setCpuSamplesMapper(cpuSamplesMapper);

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