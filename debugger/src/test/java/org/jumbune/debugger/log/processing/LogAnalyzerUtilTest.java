package org.jumbune.debugger.log.processing;

import java.util.Properties;
import org.junit.*;
import static org.junit.Assert.*;

public class LogAnalyzerUtilTest {
	private LogAnalyzerUtil fixture = new LogAnalyzerUtil(0);


	public LogAnalyzerUtil getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testLogAnalyzerUtil_1()
		throws Exception {
		int maxNumOfThreads = 0;

		LogAnalyzerUtil result = new LogAnalyzerUtil(maxNumOfThreads);

		assertNotNull(result);
	}

	@Test
	public void testLogAnalyzerUtil_2()
		throws Exception {
		int maxNumOfThreads = 1;

		LogAnalyzerUtil result = new LogAnalyzerUtil(maxNumOfThreads);

		assertNotNull(result);
	}

	@Test
	public void testGetSystemTable_1()
		throws Exception {

		Properties result = LogAnalyzerUtil.getSystemTable();

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