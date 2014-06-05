package org.jumbune.utils;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.jumbune.utils.LogUtil;
import org.junit.*;
import static org.junit.Assert.*;

public class LogUtilTest {
	@Test
	public void testAddChainLoggerInfo_1()
		throws Exception {

		LogUtil.addChainLoggerInfo();

		// TODO: add additional test code here
	}

	

	@Before
	public void setUp()
		throws Exception {
		// TODO: add additional set up code here
	}

	@After
	public void tearDown()
		throws Exception {
		// TODO: add additional tear down code here
	}
}