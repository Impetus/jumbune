package org.jumbune.profiling.beans;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jumbune.common.beans.SupportedApacheHadoopVersions;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.profiling.utils.HTFProfilingException;
import org.jumbune.profiling.utils.ProfilerStats;
import org.junit.*;
import static org.junit.Assert.*;

public class ProfilerStatsTest {
	private ProfilerStats fixture1 = new ProfilerStats(new YamlConfig());


	private ProfilerStats fixture2 = new ProfilerStats(new YamlConfig(), "", SupportedApacheHadoopVersions.HADOOP_0_20_2);


	private ProfilerStats fixture3 = new ProfilerStats(new YamlConfig(), "An��t-1.0.txt", SupportedApacheHadoopVersions.Hadoop_1_0_4);


	private ProfilerStats fixture4 = new ProfilerStats((YamlConfig) null, "0123456789", SupportedApacheHadoopVersions.HADOOP_1_0_3);


	public ProfilerStats getFixture1()
		throws Exception {
		return fixture1;
	}

	public ProfilerStats getFixture2()
		throws Exception {
		return fixture2;
	}

	public ProfilerStats getFixture3()
		throws Exception {
		return fixture3;
	}

	public ProfilerStats getFixture4()
		throws Exception {
		return fixture4;
	}

	@Test
	public void testProfilerStats_1()
		throws Exception {
		YamlConfig config = new YamlConfig();

		ProfilerStats result = new ProfilerStats(config);

		assertNotNull(result);
		assertEquals(null, result.getNodeIp());
	}

	@Test
	public void testProfilerStats_2()
		throws Exception {
		YamlConfig config = new YamlConfig();
		String nodeIp = "";
		SupportedApacheHadoopVersions version = SupportedApacheHadoopVersions.HADOOP_0_20_2;

		ProfilerStats result = new ProfilerStats(config, nodeIp, version);

		assertNotNull(result);
		assertEquals("", result.getNodeIp());
	}

	@Test
	public void testProfilerStats_3()
		throws Exception {
		YamlConfig config = new YamlConfig();
		String nodeIp = "0123456789";
		SupportedApacheHadoopVersions version = SupportedApacheHadoopVersions.HADOOP_1_0_3;

		ProfilerStats result = new ProfilerStats(config, nodeIp, version);

		assertNotNull(result);
		assertEquals("0123456789", result.getNodeIp());
	}

	@Test
	public void testProfilerStats_4()
		throws Exception {
		YamlConfig config = new YamlConfig();
		String nodeIp = "";
		SupportedApacheHadoopVersions version = SupportedApacheHadoopVersions.Hadoop_1_0_4;

		ProfilerStats result = new ProfilerStats(config, nodeIp, version);

		assertNotNull(result);
		assertEquals("", result.getNodeIp());
	}

	@Test
	public void testProfilerStats_5()
		throws Exception {
		YamlConfig config = new YamlConfig();
		String nodeIp = "";
		SupportedApacheHadoopVersions version = SupportedApacheHadoopVersions.HADOOP_1_0_3;

		ProfilerStats result = new ProfilerStats(config, nodeIp, version);

		assertNotNull(result);
		assertEquals("", result.getNodeIp());
	}

	@Test
	public void testProfilerStats_6()
		throws Exception {
		YamlConfig config = new YamlConfig();
		String nodeIp = "0123456789";
		SupportedApacheHadoopVersions version = SupportedApacheHadoopVersions.Hadoop_1_0_4;

		ProfilerStats result = new ProfilerStats(config, nodeIp, version);

		assertNotNull(result);
		assertEquals("0123456789", result.getNodeIp());
	}

	@Test
	public void testProfilerStats_7()
		throws Exception {
		YamlConfig config = new YamlConfig();
		String nodeIp = "0123456789";
		SupportedApacheHadoopVersions version = SupportedApacheHadoopVersions.HADOOP_0_20_2;

		ProfilerStats result = new ProfilerStats(config, nodeIp, version);

		assertNotNull(result);
		assertEquals("0123456789", result.getNodeIp());
	}

	

	@Test(expected = org.jumbune.profiling.utils.HTFProfilingException.class)
	public void testGetCpuStats_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String attribute = "";

		String result = fixture.getCpuStats(attribute);

		assertNotNull(result);
	}

	@Test(expected = org.jumbune.profiling.utils.HTFProfilingException.class)
	public void testGetCpuStats_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String attribute = "0123456789";

		String result = fixture.getCpuStats(attribute);

		assertNotNull(result);
	}

	@Test(expected = org.jumbune.profiling.utils.HTFProfilingException.class)
	public void testGetCpuStats_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String attribute = "0123456789";

		String result = fixture.getCpuStats(attribute);

		assertNotNull(result);
	}

	@Test(expected = org.jumbune.profiling.utils.HTFProfilingException.class)
	public void testGetCpuStats_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String attribute = "0123456789";

		String result = fixture.getCpuStats(attribute);

		assertNotNull(result);
	}

	@Test(expected = org.jumbune.profiling.utils.HTFProfilingException.class)
	public void testGetCpuStats_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String attribute = "";

		String result = fixture.getCpuStats(attribute);

		assertNotNull(result);
	}

	@Test(expected = org.jumbune.profiling.utils.HTFProfilingException.class)
	public void testGetCpuStats_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String attribute = "";

		String result = fixture.getCpuStats(attribute);

		assertNotNull(result);
	}

	@Test(expected = org.jumbune.profiling.utils.HTFProfilingException.class)
	public void testGetCpuStats_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String attribute = "";

		String result = fixture.getCpuStats(attribute);

		assertNotNull(result);
	}

	@Test(expected = org.jumbune.profiling.utils.HTFProfilingException.class)
	public void testGetCpuStats_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String attribute = "0123456789";

		String result = fixture.getCpuStats(attribute);

		assertNotNull(result);
	}


	@Test
	public void testGetNodeIp_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();

		String result = fixture.getNodeIp();

		assertEquals(null, result);
	}

	@Test
	public void testGetNodeIp_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();

		String result = fixture.getNodeIp();

		assertEquals("", result);
	}

	@Test
	public void testGetNodeIp_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();

		String result = fixture.getNodeIp();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetNodeIp_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();

		String result = fixture.getNodeIp();

		assertEquals("0123456789", result);
	}



	@Test
	public void testSetCpuStats_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("", "");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("", "");
		cpuStats.put("0123456789", "0123456789");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("0123456789", "0123456789");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		Map<String, String> cpuStats = new HashMap<String, String>();

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("", "");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("", "");
		cpuStats.put("0123456789", "0123456789");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("0123456789", "0123456789");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		Map<String, String> cpuStats = new HashMap<String, String>();

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture3_3()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("", "");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture4_3()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("", "");
		cpuStats.put("0123456789", "0123456789");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture1_3()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("0123456789", "0123456789");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture2_3()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		Map<String, String> cpuStats = new HashMap<String, String>();

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture4_4()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("", "");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture1_4()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("", "");
		cpuStats.put("0123456789", "0123456789");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture2_4()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> cpuStats = new HashMap<String, String>();
		cpuStats.put("0123456789", "0123456789");

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetCpuStats_fixture3_4()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		Map<String, String> cpuStats = new HashMap<String, String>();

		fixture.setCpuStats(cpuStats);

	}

	@Test
	public void testSetDnPort_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String dnPort = "";

		fixture.setDnPort(dnPort);

	}

	@Test
	public void testSetDnPort_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String dnPort = "0123456789";

		fixture.setDnPort(dnPort);

	}

	@Test
	public void testSetDnPort_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String dnPort = "0123456789";

		fixture.setDnPort(dnPort);

	}

	@Test
	public void testSetDnPort_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String dnPort = "0123456789";

		fixture.setDnPort(dnPort);

	}

	@Test
	public void testSetDnPort_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String dnPort = "";

		fixture.setDnPort(dnPort);

	}

	@Test
	public void testSetDnPort_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String dnPort = "";

		fixture.setDnPort(dnPort);

	}

	@Test
	public void testSetDnPort_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String dnPort = "";

		fixture.setDnPort(dnPort);

	}

	@Test
	public void testSetDnPort_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String dnPort = "0123456789";

		fixture.setDnPort(dnPort);

	}

	@Test
	public void testSetDnStats_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("", "");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("", "");
		dnStats.put("0123456789", "0123456789");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("0123456789", "0123456789");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		Map<String, String> dnStats = new HashMap<String, String>();

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("", "");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("", "");
		dnStats.put("0123456789", "0123456789");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("0123456789", "0123456789");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		Map<String, String> dnStats = new HashMap<String, String>();

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture3_3()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("", "");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture4_3()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("", "");
		dnStats.put("0123456789", "0123456789");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture1_3()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("0123456789", "0123456789");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture2_3()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		Map<String, String> dnStats = new HashMap<String, String>();

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture4_4()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("", "");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture1_4()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("", "");
		dnStats.put("0123456789", "0123456789");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture2_4()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> dnStats = new HashMap<String, String>();
		dnStats.put("0123456789", "0123456789");

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetDnStats_fixture3_4()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		Map<String, String> dnStats = new HashMap<String, String>();

		fixture.setDnStats(dnStats);

	}

	@Test
	public void testSetJtPort_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String jtPort = "";

		fixture.setJtPort(jtPort);

	}

	@Test
	public void testSetJtPort_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String jtPort = "0123456789";

		fixture.setJtPort(jtPort);

	}

	@Test
	public void testSetJtPort_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String jtPort = "0123456789";

		fixture.setJtPort(jtPort);

	}

	@Test
	public void testSetJtPort_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String jtPort = "0123456789";

		fixture.setJtPort(jtPort);

	}

	@Test
	public void testSetJtPort_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String jtPort = "";

		fixture.setJtPort(jtPort);

	}

	@Test
	public void testSetJtPort_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String jtPort = "";

		fixture.setJtPort(jtPort);

	}

	@Test
	public void testSetJtPort_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String jtPort = "";

		fixture.setJtPort(jtPort);

	}

	@Test
	public void testSetJtPort_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String jtPort = "0123456789";

		fixture.setJtPort(jtPort);

	}

	@Test
	public void testSetJtStats_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("", "");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("", "");
		jtStats.put("0123456789", "0123456789");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("0123456789", "0123456789");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		Map<String, String> jtStats = new HashMap<String, String>();

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("", "");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("", "");
		jtStats.put("0123456789", "0123456789");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("0123456789", "0123456789");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		Map<String, String> jtStats = new HashMap<String, String>();

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture3_3()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("", "");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture4_3()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("", "");
		jtStats.put("0123456789", "0123456789");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture1_3()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("0123456789", "0123456789");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture2_3()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		Map<String, String> jtStats = new HashMap<String, String>();

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture4_4()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("", "");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture1_4()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("", "");
		jtStats.put("0123456789", "0123456789");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture2_4()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> jtStats = new HashMap<String, String>();
		jtStats.put("0123456789", "0123456789");

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJtStats_fixture3_4()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		Map<String, String> jtStats = new HashMap<String, String>();

		fixture.setJtStats(jtStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("", "");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("", "");
		jumbuneContextStats.put("0123456789", "0123456789");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("0123456789", "0123456789");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		Map<String, String> jumbuneContextStats = new HashMap<String, String>();

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("", "");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("", "");
		jumbuneContextStats.put("0123456789", "0123456789");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("0123456789", "0123456789");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		Map<String, String> jumbuneContextStats = new HashMap<String, String>();

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture3_3()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("", "");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture4_3()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("", "");
		jumbuneContextStats.put("0123456789", "0123456789");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture1_3()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("0123456789", "0123456789");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture2_3()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		Map<String, String> jumbuneContextStats = new HashMap<String, String>();

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture4_4()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("", "");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture1_4()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("", "");
		jumbuneContextStats.put("0123456789", "0123456789");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture2_4()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> jumbuneContextStats = new HashMap<String, String>();
		jumbuneContextStats.put("0123456789", "0123456789");

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetJumbuneContextStats_fixture3_4()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		Map<String, String> jumbuneContextStats = new HashMap<String, String>();

		fixture.setJumbuneContextStats(jumbuneContextStats);

	}

	@Test
	public void testSetMemoryStats_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("", "");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("", "");
		memoryStats.put("0123456789", "0123456789");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("0123456789", "0123456789");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		Map<String, String> memoryStats = new HashMap<String, String>();

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("", "");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("", "");
		memoryStats.put("0123456789", "0123456789");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("0123456789", "0123456789");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		Map<String, String> memoryStats = new HashMap<String, String>();

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture3_3()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("", "");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture4_3()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("", "");
		memoryStats.put("0123456789", "0123456789");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture1_3()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("0123456789", "0123456789");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture2_3()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		Map<String, String> memoryStats = new HashMap<String, String>();

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture4_4()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("", "");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture1_4()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("", "");
		memoryStats.put("0123456789", "0123456789");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture2_4()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> memoryStats = new HashMap<String, String>();
		memoryStats.put("0123456789", "0123456789");

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetMemoryStats_fixture3_4()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		Map<String, String> memoryStats = new HashMap<String, String>();

		fixture.setMemoryStats(memoryStats);

	}

	@Test
	public void testSetNnPort_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String nnPort = "";

		fixture.setNnPort(nnPort);

	}

	@Test
	public void testSetNnPort_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String nnPort = "0123456789";

		fixture.setNnPort(nnPort);

	}

	@Test
	public void testSetNnPort_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String nnPort = "0123456789";

		fixture.setNnPort(nnPort);

	}

	@Test
	public void testSetNnPort_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String nnPort = "0123456789";

		fixture.setNnPort(nnPort);

	}

	@Test
	public void testSetNnPort_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String nnPort = "";

		fixture.setNnPort(nnPort);

	}

	@Test
	public void testSetNnPort_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String nnPort = "";

		fixture.setNnPort(nnPort);

	}

	@Test
	public void testSetNnPort_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String nnPort = "";

		fixture.setNnPort(nnPort);

	}

	@Test
	public void testSetNnPort_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String nnPort = "0123456789";

		fixture.setNnPort(nnPort);

	}

	@Test
	public void testSetNnStats_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("", "");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("", "");
		nnStats.put("0123456789", "0123456789");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("0123456789", "0123456789");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		Map<String, String> nnStats = new HashMap<String, String>();

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("", "");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("", "");
		nnStats.put("0123456789", "0123456789");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("0123456789", "0123456789");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		Map<String, String> nnStats = new HashMap<String, String>();

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture3_3()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("", "");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture4_3()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("", "");
		nnStats.put("0123456789", "0123456789");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture1_3()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("0123456789", "0123456789");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture2_3()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		Map<String, String> nnStats = new HashMap<String, String>();

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture4_4()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("", "");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture1_4()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("", "");
		nnStats.put("0123456789", "0123456789");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture2_4()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> nnStats = new HashMap<String, String>();
		nnStats.put("0123456789", "0123456789");

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNnStats_fixture3_4()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		Map<String, String> nnStats = new HashMap<String, String>();

		fixture.setNnStats(nnStats);

	}

	@Test
	public void testSetNodeIp_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String nodeIp = "";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String nodeIp = "0123456789";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String nodeIp = "0123456789";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String nodeIp = "0123456789";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String nodeIp = "";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String nodeIp = "";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String nodeIp = "";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String nodeIp = "0123456789";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetTtPort_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String ttPort = "";

		fixture.setTtPort(ttPort);

	}

	@Test
	public void testSetTtPort_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String ttPort = "0123456789";

		fixture.setTtPort(ttPort);

	}

	@Test
	public void testSetTtPort_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String ttPort = "0123456789";

		fixture.setTtPort(ttPort);

	}

	@Test
	public void testSetTtPort_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String ttPort = "0123456789";

		fixture.setTtPort(ttPort);

	}

	@Test
	public void testSetTtPort_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		String ttPort = "";

		fixture.setTtPort(ttPort);

	}

	@Test
	public void testSetTtPort_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		String ttPort = "";

		fixture.setTtPort(ttPort);

	}

	@Test
	public void testSetTtPort_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		String ttPort = "";

		fixture.setTtPort(ttPort);

	}

	@Test
	public void testSetTtPort_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		String ttPort = "0123456789";

		fixture.setTtPort(ttPort);

	}

	@Test
	public void testSetTtStats_fixture1_1()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("", "");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture2_1()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("", "");
		ttStats.put("0123456789", "0123456789");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture3_1()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("0123456789", "0123456789");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture4_1()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		Map<String, String> ttStats = new HashMap<String, String>();

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture2_2()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("", "");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture3_2()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("", "");
		ttStats.put("0123456789", "0123456789");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture4_2()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("0123456789", "0123456789");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture1_2()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		Map<String, String> ttStats = new HashMap<String, String>();

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture3_3()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("", "");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture4_3()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("", "");
		ttStats.put("0123456789", "0123456789");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture1_3()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("0123456789", "0123456789");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture2_3()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		Map<String, String> ttStats = new HashMap<String, String>();

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture4_4()
		throws Exception {
		ProfilerStats fixture = getFixture4();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("", "");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture1_4()
		throws Exception {
		ProfilerStats fixture = getFixture1();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("", "");
		ttStats.put("0123456789", "0123456789");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture2_4()
		throws Exception {
		ProfilerStats fixture = getFixture2();
		HashMap<String, String> ttStats = new HashMap<String, String>();
		ttStats.put("0123456789", "0123456789");

		fixture.setTtStats(ttStats);

	}

	@Test
	public void testSetTtStats_fixture3_4()
		throws Exception {
		ProfilerStats fixture = getFixture3();
		Map<String, String> ttStats = new HashMap<String, String>();

		fixture.setTtStats(ttStats);

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