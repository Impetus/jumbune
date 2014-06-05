package org.jumbune.utils;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.easymock.EasyMock;
import org.jumbune.utils.MapReduceExecutionUtil;
import org.junit.*;
import static org.junit.Assert.*;

public class MapReduceExecutionUtilTest {
	@Test
	public void testAddChainInfo_1()
		throws Exception {
		JobConf conf = new JobConf();

		MapReduceExecutionUtil.addChainInfo(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testAddChainInfo_2()
		throws Exception {
		JobConf conf = new JobConf();

		MapReduceExecutionUtil.addChainInfo(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCalculateParitioningTime_1()
		throws Exception {
		Writable key = EasyMock.createMock(Writable.class);
		Writable value = EasyMock.createMock(Writable.class);
		// TODO: add mock object expectations here

		EasyMock.replay(key);
		EasyMock.replay(value);

		MapReduceExecutionUtil.calculateParitioningTime(key, value);

		// TODO: add additional test code here
		EasyMock.verify(key);
		EasyMock.verify(value);
	}

	@Test
	public void testCalculateParitioningTime_2()
		throws Exception {
		Writable key = EasyMock.createMock(Writable.class);
		Writable value = EasyMock.createMock(Writable.class);
		// TODO: add mock object expectations here

		EasyMock.replay(key);
		EasyMock.replay(value);

		MapReduceExecutionUtil.calculateParitioningTime(key, value);

		// TODO: add additional test code here
		EasyMock.verify(key);
		EasyMock.verify(value);
	}

	@Test
	public void testCalculateParitioningTime_3()
		throws Exception {
		Writable key = EasyMock.createMock(Writable.class);
		Writable value = EasyMock.createMock(Writable.class);
		// TODO: add mock object expectations here

		EasyMock.replay(key);
		EasyMock.replay(value);

		MapReduceExecutionUtil.calculateParitioningTime(key, value);

		// TODO: add additional test code here
		EasyMock.verify(key);
		EasyMock.verify(value);
	}

	@Test
	public void testCalculateParitioningTime_4()
		throws Exception {
		Writable key = EasyMock.createMock(Writable.class);
		Writable value = EasyMock.createMock(Writable.class);
		// TODO: add mock object expectations here

		EasyMock.replay(key);
		EasyMock.replay(value);

		MapReduceExecutionUtil.calculateParitioningTime(key, value);

		// TODO: add additional test code here
		EasyMock.verify(key);
		EasyMock.verify(value);
	}

	@Test
	public void testConfigureLogging_1()
		throws Exception {
		String logFileDir = "";
		TaskInputOutputContext context = null;
		boolean isMapper = true;

		MapReduceExecutionUtil.configureLogging(logFileDir, context, isMapper);

		// TODO: add additional test code here
	}

	
	@Test
	public void testConfigureLogging_3()
		throws Exception {
		String logFileDir = "";
		TaskInputOutputContext context = null;
		boolean isMapper = true;

		MapReduceExecutionUtil.configureLogging(logFileDir, context, isMapper);

		// TODO: add additional test code here
	}

	

	@Test
	public void testConfigureLogging_5()
		throws Exception {
		String logFileDir = "";
		TaskInputOutputContext context = null;
		boolean isMapper = true;

		MapReduceExecutionUtil.configureLogging(logFileDir, context, isMapper);

		// TODO: add additional test code here
	}

	
	@Test
	public void testConfigureLogging_7()
		throws Exception {
		String logFileDir = "";
		TaskInputOutputContext context = null;
		boolean isMapper = true;

		MapReduceExecutionUtil.configureLogging(logFileDir, context, isMapper);

		// TODO: add additional test code here
	}

	

	@Test
	public void testConfigureLogging_9()
		throws Exception {
		String logFileDir = "";
		TaskInputOutputContext context = null;
		boolean isMapper = true;

		MapReduceExecutionUtil.configureLogging(logFileDir, context, isMapper);

		// TODO: add additional test code here
	}

	

	@Test
	public void testConfigureLogging_11()
		throws Exception {
		String logFileDir = "";
		TaskInputOutputContext context = null;
		boolean isMapper = true;

		MapReduceExecutionUtil.configureLogging(logFileDir, context, isMapper);

		// TODO: add additional test code here
	}

	

	@Test
	public void testConfigureLogging_14()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = true;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_15()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = true;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_16()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = false;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_17()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = false;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_18()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = true;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_19()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = false;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_20()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = true;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_21()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = false;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_22()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = false;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_23()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = false;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_24()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = false;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_25()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = false;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_26()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = false;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_27()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = false;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testConfigureLogging_28()
		throws Exception {
		String logFileDir = "";
		JobConf conf = new JobConf();
		boolean isMapper = false;
		boolean loadLogger = true;
		int loggerNumber = 1;

		MapReduceExecutionUtil.configureLogging(logFileDir, conf, isMapper, loadLogger, loggerNumber);

		// TODO: add additional test code here
	}

	
	@Test
	public void testGetPartitioner_1()
		throws Exception {

		Partitioner result = MapReduceExecutionUtil.getPartitioner();

		// TODO: add additional test code here
		assertEquals(null, result);
	}

	@Test
	public void testGetPartitionerTotalTimeTaken_1()
		throws Exception {

		Long result = MapReduceExecutionUtil.getPartitionerTotalTimeTaken();

		// TODO: add additional test code here
		assertNotNull(result);
		assertEquals("0", result.toString());
		assertEquals((byte) 0, result.byteValue());
		assertEquals((short) 0, result.shortValue());
		assertEquals(0, result.intValue());
		assertEquals(0L, result.longValue());
		assertEquals(0.0f, result.floatValue(), 1.0f);
		assertEquals(0.0, result.doubleValue(), 1.0);
	}

	//@Test
	public void testGetPartitioningSampleCount_1()
		throws Exception {

		Long result = MapReduceExecutionUtil.getPartitioningSampleCount();

		// TODO: add additional test code here
		assertNotNull(result);
		assertEquals("0", result.toString());
		assertEquals((byte) 0, result.byteValue());
		assertEquals((short) 0, result.shortValue());
		assertEquals(0, result.intValue());
		assertEquals(0L, result.longValue());
		assertEquals(0.0f, result.floatValue(), 1.0f);
		assertEquals(0.0, result.doubleValue(), 1.0);
	}

	@Test
	public void testIncrementPartitioningSampleCount_1()
		throws Exception {

		MapReduceExecutionUtil.incrementPartitioningSampleCount();

		// TODO: add additional test code here
	}

	@Test
	public void testIncrementPartitioningTotalTime_1()
		throws Exception {
		long time = 1L;

		MapReduceExecutionUtil.incrementPartitioningTotalTime(time);

		// TODO: add additional test code here
	}

	@Test
	public void testInitializeJumbuneLog_1()
		throws Exception {

		MapReduceExecutionUtil.initializeJumbuneLog();

		// TODO: add additional test code here
	}

	@Test
	public void testInitializeJumbuneLog_2()
		throws Exception {

		MapReduceExecutionUtil.initializeJumbuneLog();

		// TODO: add additional test code here
	}

	@Test
	public void testIsJumbuneLog_1()
		throws Exception {

		boolean result = MapReduceExecutionUtil.isJumbuneLog();

		// TODO: add additional test code here
		assertEquals(true, result);
	}

	@Test
	public void testRemoveJumbuneLog_1()
		throws Exception {

		MapReduceExecutionUtil.removeJumbuneLog();

		// TODO: add additional test code here
	}

	@Test
	public void testRemoveLoggerNumbber_1()
		throws Exception {

		MapReduceExecutionUtil.removeLoggerNumbber();

		// TODO: add additional test code here
	}

	@Test
	public void testRemoveNumReducetasks_1()
		throws Exception {

		MapReduceExecutionUtil.removeNumReducetasks();

		// TODO: add additional test code here
	}

	@Test
	public void testRemoveOldPartitioner_1()
		throws Exception {

		MapReduceExecutionUtil.removeOldPartitioner();

		// TODO: add additional test code here
	}

	@Test
	public void testRemovePartitioner_1()
		throws Exception {

		MapReduceExecutionUtil.removePartitioner();

		// TODO: add additional test code here
	}

	@Test
	public void testRemovePartitionerTimeAndSampleCount_1()
		throws Exception {

		MapReduceExecutionUtil.removePartitionerTimeAndSampleCount();

		// TODO: add additional test code here
	}

	@Test
	public void testSetLoggerNumber_1()
		throws Exception {
		int value = 1;

		MapReduceExecutionUtil.setLoggerNumber(value);

		// TODO: add additional test code here
	}

	@Test
	public void testSetNumReducetasksthreadlocal_1()
		throws Exception {
		int numreducetasks = 1;

		MapReduceExecutionUtil.setNumReducetasksthreadlocal(numreducetasks);

		// TODO: add additional test code here
	}

	@Test
	public void testSetPartitioner_1()
		throws Exception {
		JobConf conf = new JobConf();

		MapReduceExecutionUtil.setPartitioner(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testSetPartitioner_2()
		throws Exception {
		JobConf conf = new JobConf();

		MapReduceExecutionUtil.setPartitioner(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testSetPartitioner_3()
		throws Exception {
		JobConf conf = new JobConf();

		MapReduceExecutionUtil.setPartitioner(conf);

		// TODO: add additional test code here
	}

	
	@Test
	public void testStopJumbuneLogging_1()
		throws Exception {

		MapReduceExecutionUtil.stopJumbuneLogging();

		// TODO: add additional test code here
	}

	@Test
	public void testStopJumbuneLogging_2()
		throws Exception {
		boolean loadLogger = true;

		MapReduceExecutionUtil.stopJumbuneLogging(loadLogger);

		// TODO: add additional test code here
	}

	@Test
	public void testStopJumbuneLogging_3()
		throws Exception {
		boolean loadLogger = false;

		MapReduceExecutionUtil.stopJumbuneLogging(loadLogger);

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