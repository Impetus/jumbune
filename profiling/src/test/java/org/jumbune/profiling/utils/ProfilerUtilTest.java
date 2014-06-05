package org.jumbune.profiling.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.jumbune.common.beans.JobOutput;
import org.jumbune.common.beans.SupportedApacheHadoopVersions;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.hprof.BinaryHprofReader;
import org.jumbune.profiling.hprof.HeapAllocSitesBean;
import org.jumbune.profiling.utils.ProfilerUtil;
import org.junit.*;
import static org.junit.Assert.*;

public class ProfilerUtilTest {
	
	@Test
	public void testConvertKBtoGB_1()
		throws Exception {
		long value = -1L;

		double result = ProfilerUtil.convertKBtoGB(value);

		assertEquals(-0.0, result, 0.1);
	}

	@Test
	public void testConvertKBtoGB_2()
		throws Exception {
		long value = 0L;

		double result = ProfilerUtil.convertKBtoGB(value);

		assertEquals(0.0, result, 0.1);
	}

	@Test
	public void testConvertKBtoGB_3()
		throws Exception {
		long value = 1L;

		double result = ProfilerUtil.convertKBtoGB(value);

		assertEquals(0.0, result, 0.1);
	}

	@Test
	public void testConvertKBtoMB_1()
		throws Exception {
		long valueinKB = -1L;

		double result = ProfilerUtil.convertKBtoMB(valueinKB);

		assertEquals(-0.0, result, 0.1);
	}

	@Test
	public void testConvertKBtoMB_2()
		throws Exception {
		long valueinKB = 0L;

		double result = ProfilerUtil.convertKBtoMB(valueinKB);

		assertEquals(0.0, result, 0.1);
	}

	@Test
	public void testConvertKBtoMB_3()
		throws Exception {
		long valueinKB = 1L;

		double result = ProfilerUtil.convertKBtoMB(valueinKB);

		assertEquals(0.0, result, 0.1);
	}


	@Test
	public void testGetHadoopJMXURLPrefix_1()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.HADOOP_0_20_2;
		JMXDeamons jmxDaemon = JMXDeamons.DATA_NODE;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);

		assertEquals("hadoop", result);
	}

	@Test
	public void testGetHadoopJMXURLPrefix_2()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.HADOOP_1_0_3;
		JMXDeamons jmxDaemon = JMXDeamons.JOB_TRACKER;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);

		assertEquals("Hadoop", result);
	}

	@Test
	public void testGetHadoopJMXURLPrefix_3()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.Hadoop_1_0_4;
		JMXDeamons jmxDaemon = JMXDeamons.NAME_NODE;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);

		assertEquals("Hadoop", result);
	}

	@Test
	public void testGetHadoopJMXURLPrefix_4()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.HADOOP_0_20_2;
		JMXDeamons jmxDaemon = JMXDeamons.TASK_TRACKER;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);

		assertEquals("hadoop", result);
	}

	@Test
	public void testGetHadoopJMXURLPrefix_5()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.Hadoop_1_0_4;
		JMXDeamons jmxDaemon = JMXDeamons.DATA_NODE;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);

		assertEquals("Hadoop", result);
	}

	@Test
	public void testGetHadoopJMXURLPrefix_6()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.HADOOP_0_20_2;
		JMXDeamons jmxDaemon = JMXDeamons.JOB_TRACKER;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);

		assertEquals("hadoop", result);
	}

	@Test
	public void testGetHadoopJMXURLPrefix_7()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.HADOOP_1_0_3;
		JMXDeamons jmxDaemon = JMXDeamons.NAME_NODE;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);

		assertEquals("Hadoop", result);
	}

	@Test
	public void testGetHadoopJMXURLPrefix_8()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.Hadoop_1_0_4;
		JMXDeamons jmxDaemon = JMXDeamons.TASK_TRACKER;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);
		assertEquals("Hadoop", result);
	}

	@Test
	public void testGetHadoopJMXURLPrefix_9()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.HADOOP_1_0_3;
		JMXDeamons jmxDaemon = JMXDeamons.DATA_NODE;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);

		assertEquals("Hadoop", result);
	}

	@Test
	public void testGetHadoopJMXURLPrefix_10()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.Hadoop_1_0_4;
		JMXDeamons jmxDaemon = JMXDeamons.JOB_TRACKER;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);

		assertEquals("Hadoop", result);
	}

	@Test
	public void testGetHadoopJMXURLPrefix_11()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.HADOOP_0_20_2;
		JMXDeamons jmxDaemon = JMXDeamons.NAME_NODE;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);

		assertEquals("hadoop", result);
	}

	@Test
	public void testGetHadoopJMXURLPrefix_12()
		throws Exception {
		SupportedApacheHadoopVersions hadoopVersion = SupportedApacheHadoopVersions.HADOOP_1_0_3;
		JMXDeamons jmxDaemon = JMXDeamons.TASK_TRACKER;

		String result = ProfilerUtil.getHadoopJMXURLPrefix(hadoopVersion, jmxDaemon);

		assertEquals("Hadoop", result);
	}

	@Test
	public void testRoundTwoDecimals_1()
		throws Exception {
		double d = -1.0;

		double result = ProfilerUtil.roundTwoDecimals(d);

		assertEquals(-1.0, result, 0.1);
	}

	@Test
	public void testRoundTwoDecimals_2()
		throws Exception {
		double d = 0.0;

		double result = ProfilerUtil.roundTwoDecimals(d);

		assertEquals(0.0, result, 0.1);
	}

	@Test
	public void testRoundTwoDecimals_3()
		throws Exception {
		double d = 1.0;

		double result = ProfilerUtil.roundTwoDecimals(d);

		assertEquals(1.0, result, 0.1);
	}

	

	@Test
	public void testTrimAndSpilt_2()
		throws Exception {
		String line = "0123456789";
		String attrib = "0";

		String result = ProfilerUtil.trimAndSpilt(line, attrib);

		assertEquals("23456789", result);
	}

	


	@Test
	public void testTrimAndSpilt_5()
		throws Exception {
		String line = "0123456789";
		String attrib = "0123456";

		String result = ProfilerUtil.trimAndSpilt(line, attrib);

		assertEquals("89", result);
	}

	@Test
	public void testTrimAndSpilt_6()
		throws Exception {
		String line = "0123456789";
		String attrib = "";

		String result = ProfilerUtil.trimAndSpilt(line, attrib);

		assertEquals("123456789", result);
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