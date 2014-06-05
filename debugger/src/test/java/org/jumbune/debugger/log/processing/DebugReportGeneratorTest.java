package org.jumbune.debugger.log.processing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumbune.debugger.log.processing.DebugAnalysisBean;
import org.jumbune.debugger.log.processing.JobBean;
import org.jumbune.debugger.log.processing.PartitionerInfoBean;
import org.junit.*;
import static org.junit.Assert.*;

public class DebugReportGeneratorTest {

	@Test
	public void testDebugReportGenerator_1() throws Exception {
		DebugReportGenerator result = new DebugReportGenerator();
		assertNotNull(result);
		// add additional test code here
	}

	@Test
	public void testGenerateReportFromDebugAnalysisBean_1() throws Exception {
		DebugReportGenerator fixture = new DebugReportGenerator();
		DebugAnalysisBean analysisBean = new DebugAnalysisBean();
		analysisBean.setPartitionerMap(new HashMap());
		analysisBean.setLogMap(new HashMap());

		DebugReport result = fixture
				.generateReportFromDebugAnalysisBean(analysisBean);

		// add additional test code here
		assertNotNull(result);
	}

	@Test
	public void testGenerateReportFromDebugAnalysisBean_2() throws Exception {
		DebugReportGenerator fixture = new DebugReportGenerator();
		DebugAnalysisBean analysisBean = new DebugAnalysisBean();
		analysisBean.setPartitionerMap(new HashMap());
		analysisBean.setLogMap(new HashMap());

		DebugReport result = fixture
				.generateReportFromDebugAnalysisBean(analysisBean);

		// add additional test code here
		assertNotNull(result);
	}

	@Test
	public void testGenerateReportFromDebugAnalysisBean_3() throws Exception {
		DebugReportGenerator fixture = new DebugReportGenerator();
		DebugAnalysisBean analysisBean = new DebugAnalysisBean();
		analysisBean.setPartitionerMap(new HashMap());
		analysisBean.setLogMap(new HashMap());

		DebugReport result = fixture
				.generateReportFromDebugAnalysisBean(analysisBean);

		// add additional test code here
		assertNotNull(result);
	}

	@Before
	public void setUp() throws Exception {
		// add additional set up code here
	}

	@After
	public void tearDown() throws Exception {
		// Add additional tear down code here
	}

	/*public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(DebugReportGeneratorTest.class);
	}*/
}