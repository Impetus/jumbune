package org.jumbune.portout.execution;

import org.apache.hadoop.mapred.JobConf;
import org.jumbune.portout.execution.ChainMRMJobExecutor;
import org.junit.*;
import static org.junit.Assert.*;

public class ChainMRMJobExecutorTest {
	private ChainMRMJobExecutor fixture = new ChainMRMJobExecutor();


	public ChainMRMJobExecutor getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testChainMRMJobExecutor_1()
		throws Exception {

		ChainMRMJobExecutor result = new ChainMRMJobExecutor();

		assertNotNull(result);
		assertEquals(null, result.getParser());
		assertEquals(null, result.getHdfsOutputPath());
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