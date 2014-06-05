package org.jumbune.profiling.utils;

import org.jumbune.profiling.utils.FetchPhaseStats;
import org.jumbune.utils.exception.JumbuneException;
import org.junit.*;
import static org.junit.Assert.*;


public class FetchPhaseStatsTest {
	private FetchPhaseStats fixture1 = new FetchPhaseStats();


	private FetchPhaseStats fixture2;

	{
		fixture2 = new FetchPhaseStats();
		fixture2.setJobId("");
		fixture2.setNodeIP("1");
		fixture2.setTtPort("1");
	}

	private FetchPhaseStats fixture3;

	{
		fixture3 = new FetchPhaseStats();
		fixture3.setJobId("0123456789");
		fixture3.setNodeIP("1");
		fixture3.setTtPort("1");
	}

	private FetchPhaseStats fixture4;

	{
		fixture4 = new FetchPhaseStats();
		fixture4.setJobId("An��t-1.0.txt");
		fixture4.setNodeIP("1");
		fixture4.setTtPort("1");
	}

	public FetchPhaseStats getFixture1()
		throws Exception {
		return fixture1;
	}

	public FetchPhaseStats getFixture2()
		throws Exception {
		if (fixture2 == null) {
			fixture2 = new FetchPhaseStats();
			fixture2.setJobId("");
			fixture2.setNodeIP("1");
			fixture2.setTtPort("1");
		}
		return fixture2;
	}

	public FetchPhaseStats getFixture3()
		throws Exception {
		if (fixture3 == null) {
			fixture3 = new FetchPhaseStats();
			fixture3.setJobId("0123456789");
			fixture3.setNodeIP("1");
			fixture3.setTtPort("1");
		}
		return fixture3;
	}

	public FetchPhaseStats getFixture4()
		throws Exception {
		if (fixture4 == null) {
			fixture4 = new FetchPhaseStats();
			fixture4.setJobId("An��t-1.0.txt");
			fixture4.setNodeIP("1");
			fixture4.setTtPort("1");
		}
		return fixture4;
	}

	@Test
	public void testGetJobId_fixture2_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture2();

		String result = fixture.getJobId();

		assertEquals("", result);
	}

	@Test
	public void testGetJobId_fixture3_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture3();

		String result = fixture.getJobId();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetJobId_fixture4_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture4();

		String result = fixture.getJobId();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetJobId_fixture1_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture1();

		String result = fixture.getJobId();

		assertNotNull(result);
	}

	@Test
	public void testGetNodeIP_fixture2_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture2();

		String result = fixture.getNodeIP();

		assertEquals("1", result);
	}

	@Test
	public void testGetNodeIP_fixture3_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture3();

		String result = fixture.getNodeIP();

		assertEquals("1", result);
	}

	@Test
	public void testGetNodeIP_fixture4_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture4();

		String result = fixture.getNodeIP();

		assertEquals("1", result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNodeIP_fixture1_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture1();

		String result = fixture.getNodeIP();

		assertNotNull(result);
	}

	@Test
	public void testGetTtPort_fixture2_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture2();

		String result = fixture.getTtPort();

		assertEquals("1", result);
	}

	@Test
	public void testGetTtPort_fixture3_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture3();

		String result = fixture.getTtPort();

		assertEquals("1", result);
	}

	@Test
	public void testGetTtPort_fixture4_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture4();

		String result = fixture.getTtPort();

		assertEquals("1", result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetTtPort_fixture1_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture1();

		String result = fixture.getTtPort();

		assertNotNull(result);
	}

	@Test
	public void testRun_fixture1_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture1();

		fixture.run();

	}

	@Test
	public void testRun_fixture2_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture2();

		fixture.run();

	}

	@Test
	public void testRun_fixture3_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture3();

		fixture.run();

	}

	@Test
	public void testRun_fixture4_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture4();

		fixture.run();

	}

	@Test
	public void testSetJobId_fixture1_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture1();
		String jobId = "";

		fixture.setJobId(jobId);

	}

	@Test
	public void testSetJobId_fixture2_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture2();
		String jobId = "0123456789";

		fixture.setJobId(jobId);

	}

	@Test
	public void testSetJobId_fixture3_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture3();
		String jobId = "0123456789";

		fixture.setJobId(jobId);

	}

	@Test
	public void testSetJobId_fixture4_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture4();
		String jobId = "0123456789";

		fixture.setJobId(jobId);

	}

	@Test
	public void testSetJobId_fixture2_2()
		throws Exception {
		FetchPhaseStats fixture = getFixture2();
		String jobId = "";

		fixture.setJobId(jobId);

	}

	@Test
	public void testSetJobId_fixture3_2()
		throws Exception {
		FetchPhaseStats fixture = getFixture3();
		String jobId = "";

		fixture.setJobId(jobId);

	}

	@Test
	public void testSetJobId_fixture4_2()
		throws Exception {
		FetchPhaseStats fixture = getFixture4();
		String jobId = "";

		fixture.setJobId(jobId);

	}

	@Test
	public void testSetJobId_fixture1_2()
		throws Exception {
		FetchPhaseStats fixture = getFixture1();
		String jobId = "0123456789";

		fixture.setJobId(jobId);

	}

	@Test
	public void testSetNodeIP_fixture1_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture1();
		String nodeIP = "";

		fixture.setNodeIP(nodeIP);

	}

	@Test
	public void testSetNodeIP_fixture2_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture2();
		String nodeIP = "0123456789";

		fixture.setNodeIP(nodeIP);

	}

	@Test
	public void testSetNodeIP_fixture3_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture3();
		String nodeIP = "0123456789";

		fixture.setNodeIP(nodeIP);

	}

	@Test
	public void testSetNodeIP_fixture4_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture4();
		String nodeIP = "0123456789";

		fixture.setNodeIP(nodeIP);

	}

	@Test
	public void testSetNodeIP_fixture2_2()
		throws Exception {
		FetchPhaseStats fixture = getFixture2();
		String nodeIP = "";

		fixture.setNodeIP(nodeIP);

	}

	@Test
	public void testSetNodeIP_fixture3_2()
		throws Exception {
		FetchPhaseStats fixture = getFixture3();
		String nodeIP = "";

		fixture.setNodeIP(nodeIP);

	}

	@Test
	public void testSetNodeIP_fixture4_2()
		throws Exception {
		FetchPhaseStats fixture = getFixture4();
		String nodeIP = "";

		fixture.setNodeIP(nodeIP);

	}

	@Test
	public void testSetNodeIP_fixture1_2()
		throws Exception {
		FetchPhaseStats fixture = getFixture1();
		String nodeIP = "0123456789";

		fixture.setNodeIP(nodeIP);

	}

	@Test
	public void testSetTtPort_fixture1_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture1();
		String ttPort = null;

		fixture.setTtPort(ttPort);

	}

	@Test
	public void testSetTtPort_fixture2_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture2();
		String ttPort = null;

		fixture.setTtPort(ttPort);

	}

	@Test
	public void testSetTtPort_fixture3_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture3();
		String ttPort = null;

		fixture.setTtPort(ttPort);

	}

	@Test
	public void testSetTtPort_fixture4_1()
		throws Exception {
		FetchPhaseStats fixture = getFixture4();
		String ttPort = null;

		fixture.setTtPort(ttPort);

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
