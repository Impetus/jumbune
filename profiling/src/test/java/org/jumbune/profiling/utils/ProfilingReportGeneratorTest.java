package org.jumbune.profiling.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import org.easymock.EasyMock;
import org.jumbune.profiling.utils.ProfilerBean;
import org.jumbune.profiling.utils.ProfilerDashBoardReport;
import org.jumbune.profiling.utils.ProfilingReportGenerator;
import org.junit.*;


import static org.junit.Assert.*;

public class ProfilingReportGeneratorTest {
	private ProfilingReportGenerator fixture1;


	private ProfilingReportGenerator fixture2;

	{
		fixture2 = new ProfilingReportGenerator();
		fixture2.setBoardReport(new ProfilerDashBoardReport());
		fixture2.setCpuSampleMapper(EasyMock.createNiceMock(SortedMap.class));
		fixture2.setCpuSampleReducer(EasyMock.createNiceMock(SortedMap.class));
	}

	public ProfilingReportGenerator getFixture1()
		throws Exception {
		return fixture1;
	}

	public ProfilingReportGenerator getFixture2()
		throws Exception {
		if (fixture2 == null) {
			fixture2 = new ProfilingReportGenerator();
			fixture2.setBoardReport(new ProfilerDashBoardReport());
			fixture2.setCpuSampleMapper(EasyMock.createNiceMock(SortedMap.class));
			fixture2.setCpuSampleReducer(EasyMock.createNiceMock(SortedMap.class));
		}
		return fixture2;
	}

	@Test
	public void testGenerateProfilingReport_fixture1_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture1();
		HashMap<String, ProfilerBean> profilingBeanMap = new HashMap<String, ProfilerBean>();
		profilingBeanMap.put("", new ProfilerBean());

		ProfilerDashBoardReport result = fixture.generateProfilingReport(profilingBeanMap);

		assertNotNull(result);
		assertEquals(null, result.getCpuSampleReducer());
		assertEquals(null, result.getCpuSamplesMapper());
	}

	@Test
	public void testGenerateProfilingReport_fixture2_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture2();
		Map<String, ProfilerBean> profilingBeanMap = new HashMap<String, ProfilerBean>();

		ProfilerDashBoardReport result = fixture.generateProfilingReport(profilingBeanMap);

		assertNotNull(result);
		assertEquals(null, result.getCpuSampleReducer());
		assertEquals(null, result.getCpuSamplesMapper());
	}

	@Test
	public void testGenerateProfilingReport_fixture2_2()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture2();
		HashMap<String, ProfilerBean> profilingBeanMap = new HashMap<String, ProfilerBean>();
		profilingBeanMap.put("", new ProfilerBean());

		ProfilerDashBoardReport result = fixture.generateProfilingReport(profilingBeanMap);

		assertNotNull(result);
		assertEquals(null, result.getCpuSampleReducer());
		assertEquals(null, result.getCpuSamplesMapper());
	}

	@Test
	public void testGenerateProfilingReport_fixture1_2()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture1();
		Map<String, ProfilerBean> profilingBeanMap = new HashMap<String, ProfilerBean>();

		ProfilerDashBoardReport result = fixture.generateProfilingReport(profilingBeanMap);

		assertNotNull(result);
		assertEquals(null, result.getCpuSampleReducer());
		assertEquals(null, result.getCpuSamplesMapper());
	}

	@Test
	public void testGetBoardReport_fixture1_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture1();

		ProfilerDashBoardReport result = fixture.getBoardReport();

		assertEquals(null, result);
	}

	@Test
	public void testGetBoardReport_fixture2_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture2();

		ProfilerDashBoardReport result = fixture.getBoardReport();

		assertNotNull(result);
		assertEquals(null, result.getCpuSampleReducer());
		assertEquals(null, result.getCpuSamplesMapper());
	}

	@Test
	public void testGetCpuSampleMapper_fixture1_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture1();

		SortedMap<Float, String> result = fixture.getCpuSampleMapper();

		assertEquals(null, result);
	}

	@Test
	public void testGetCpuSampleMapper_fixture2_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture2();

		SortedMap<Float, String> result = fixture.getCpuSampleMapper();

		assertNotNull(result);
	}

	@Test
	public void testGetCpuSampleReducer_fixture1_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture1();

		SortedMap<Float, String> result = fixture.getCpuSampleReducer();

		assertEquals(null, result);
	}

	@Test
	public void testGetCpuSampleReducer_fixture2_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture2();

		SortedMap<Float, String> result = fixture.getCpuSampleReducer();

		assertNotNull(result);
	}

	@Test
	public void testSetBoardReport_fixture1_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture1();
		ProfilerDashBoardReport boardReport = new ProfilerDashBoardReport();

		fixture.setBoardReport(boardReport);

	}

	@Test
	public void testSetBoardReport_fixture2_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture2();
		ProfilerDashBoardReport boardReport = new ProfilerDashBoardReport();

		fixture.setBoardReport(boardReport);

	}

	@Test
	public void testSetCpuSampleMapper_fixture1_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture1();
		SortedMap<Float, String> cpuSampleMapper = EasyMock.createMock(SortedMap.class);
		// add mock object expectations here

		EasyMock.replay(cpuSampleMapper);

		fixture.setCpuSampleMapper(cpuSampleMapper);

		EasyMock.verify(cpuSampleMapper);
	}

	@Test
	public void testSetCpuSampleMapper_fixture2_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture2();
		SortedMap<Float, String> cpuSampleMapper = EasyMock.createMock(SortedMap.class);
		// add mock object expectations here

		EasyMock.replay(cpuSampleMapper);

		fixture.setCpuSampleMapper(cpuSampleMapper);

		EasyMock.verify(cpuSampleMapper);
	}

	@Test
	public void testSetCpuSampleReducer_fixture1_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture1();
		SortedMap<Float, String> cpuSampleReducer = EasyMock.createMock(SortedMap.class);
		// add mock object expectations here

		EasyMock.replay(cpuSampleReducer);

		fixture.setCpuSampleReducer(cpuSampleReducer);

		EasyMock.verify(cpuSampleReducer);
	}

	@Test
	public void testSetCpuSampleReducer_fixture2_1()
		throws Exception {
		ProfilingReportGenerator fixture = getFixture2();
		SortedMap<Float, String> cpuSampleReducer = EasyMock.createMock(SortedMap.class);
		// add mock object expectations here

		EasyMock.replay(cpuSampleReducer);

		fixture.setCpuSampleReducer(cpuSampleReducer);

		EasyMock.verify(cpuSampleReducer);
	}

	@Before
	public void setUp()
		throws Exception {
		 fixture1 = new ProfilingReportGenerator();
	}

	@After
	public void tearDown()
		throws Exception {
	}
}