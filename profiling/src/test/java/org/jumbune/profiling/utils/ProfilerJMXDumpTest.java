package org.jumbune.profiling.utils;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumbune.common.beans.LogConsolidationInfo;
import org.jumbune.common.beans.SupportedApacheHadoopVersions;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.profiling.beans.JMXDeamons;
import org.jumbune.profiling.beans.NodeInfo;
import org.jumbune.profiling.beans.NodeType;
import org.jumbune.profiling.healthview.NetworkLatencyInfo;
import org.jumbune.profiling.healthview.ResultInfo;
import org.jumbune.profiling.utils.ProfilerJMXDump;
import org.junit.*;
import static org.junit.Assert.*;
import com.jcraft.jsch.Session;

public class ProfilerJMXDumpTest {
	private ProfilerJMXDump fixture1;

	private ProfilerJMXDump fixture2;

	@Before
	public void setUp() throws Exception {
		fixture1 = new ProfilerJMXDump();

	}

	public ProfilerJMXDump getFixture1() throws Exception {
		return fixture1;
	}


	@Test
	public void testProfilerJMXDump_1() throws Exception {

		ProfilerJMXDump result = new ProfilerJMXDump();

		assertNotNull(result);
	}

	@Test
	public void testCreateSession_fixture1_1() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "";
		String host = "";
		String rsaFilePath = "";
		String dsaFilePath = "";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_2() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "0123456789";
		String host = "0123456789";
		String rsaFilePath = "0123456789";
		String dsaFilePath = "0123456789";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("0123456789", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("0123456789", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_3() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "0123456789";
		String host = "0123456789";
		String rsaFilePath = "0123456789";
		String dsaFilePath = "";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("0123456789", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("0123456789", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_4() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "0123456789";
		String host = "0123456789";
		String rsaFilePath = "";
		String dsaFilePath = "0123456789";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("0123456789", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("0123456789", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_5() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "0123456789";
		String host = "0123456789";
		String rsaFilePath = "";
		String dsaFilePath = "";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("0123456789", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("0123456789", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_6() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "0123456789";
		String host = "";
		String rsaFilePath = "0123456789";
		String dsaFilePath = "0123456789";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("0123456789", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_7() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "0123456789";
		String host = "";
		String rsaFilePath = "0123456789";
		String dsaFilePath = "";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("0123456789", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_8() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "0123456789";
		String host = "";
		String rsaFilePath = "";
		String dsaFilePath = "0123456789";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("0123456789", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_9() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "0123456789";
		String host = "";
		String rsaFilePath = "";
		String dsaFilePath = "";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("0123456789", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_10() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "";
		String host = "0123456789";
		String rsaFilePath = "0123456789";
		String dsaFilePath = "0123456789";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("0123456789", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_11() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "";
		String host = "0123456789";
		String rsaFilePath = "0123456789";
		String dsaFilePath = "";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("0123456789", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_12() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "";
		String host = "0123456789";
		String rsaFilePath = "";
		String dsaFilePath = "0123456789";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("0123456789", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_13() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "";
		String host = "0123456789";
		String rsaFilePath = "";
		String dsaFilePath = "";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("0123456789", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testCreateSession_fixture1_14() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		String user = "";
		String host = "";
		String rsaFilePath = "0123456789";
		String dsaFilePath = "0123456789";

		Session result = fixture.createSession(user, host, rsaFilePath,
				dsaFilePath);

		assertNotNull(result);
		assertEquals("", result.getHost());
		assertEquals(22, result.getPort());
		assertEquals(null, result.getUserInfo());
		assertEquals(0, result.getTimeout());
		assertEquals(false, result.isConnected());
		assertEquals("", result.getUserName());

		assertEquals(null, result.getHostKey());
		assertEquals(null, result.getHostKeyAlias());
		assertEquals(0, result.getServerAliveInterval());
		assertEquals(1, result.getServerAliveCountMax());
	}

	@Test
	public void testGetRemotePartitionEfficiency_fixture1_1() throws Exception {
		ProfilerJMXDump fixture = getFixture1();
		YamlConfig config = new YamlConfig();
		String nodeIp = "1";

		List<ResultInfo> result = fixture.getRemotePartitionEfficiency(config,
				nodeIp);

		assertNotNull(result);
		assertEquals(0, result.size());
	}


	
	@After
	public void tearDown() throws Exception {
	}
}