package org.jumbune.profiling.beans;

import org.jumbune.profiling.beans.CategoryInfo;
import org.jumbune.profiling.beans.NodeConfig;
import org.junit.*;


import static org.junit.Assert.*;

public class NodeConfigTest {
	private NodeConfig fixture1 = new NodeConfig();


	private NodeConfig fixture2;

	{
		fixture2 = new NodeConfig();
		fixture2.setFavourities(new CategoryInfo());
		fixture2.setNodeIp("");
		fixture2.setTrends(new CategoryInfo());
	}

	private NodeConfig fixture3;

	{
		fixture3 = new NodeConfig();
		fixture3.setFavourities(new CategoryInfo());
		fixture3.setNodeIp("0123456789");
		fixture3.setTrends(new CategoryInfo());
	}

	private NodeConfig fixture4;

	{
		fixture4 = new NodeConfig();
		fixture4.setFavourities(new CategoryInfo());
		fixture4.setNodeIp("An��t-1.0.txt");
		fixture4.setTrends(new CategoryInfo());
	}

	public NodeConfig getFixture1()
		throws Exception {
		return fixture1;
	}

	public NodeConfig getFixture2()
		throws Exception {
		if (fixture2 == null) {
			fixture2 = new NodeConfig();
			fixture2.setFavourities(new CategoryInfo());
			fixture2.setNodeIp("");
			fixture2.setTrends(new CategoryInfo());
		}
		return fixture2;
	}

	public NodeConfig getFixture3()
		throws Exception {
		if (fixture3 == null) {
			fixture3 = new NodeConfig();
			fixture3.setFavourities(new CategoryInfo());
			fixture3.setNodeIp("0123456789");
			fixture3.setTrends(new CategoryInfo());
		}
		return fixture3;
	}

	public NodeConfig getFixture4()
		throws Exception {
		if (fixture4 == null) {
			fixture4 = new NodeConfig();
			fixture4.setFavourities(new CategoryInfo());
			fixture4.setNodeIp("An��t-1.0.txt");
			fixture4.setTrends(new CategoryInfo());
		}
		return fixture4;
	}

	@Test
	public void testGetFavourities_fixture1_1()
		throws Exception {
		NodeConfig fixture = getFixture1();

		CategoryInfo result = fixture.getFavourities();

		assertEquals(null, result);
	}

	@Test
	public void testGetFavourities_fixture2_1()
		throws Exception {
		NodeConfig fixture = getFixture2();

		CategoryInfo result = fixture.getFavourities();

		assertNotNull(result);
		assertEquals(null, result.getClusterWide());
		assertEquals(null, result.getSystemStats());
		assertEquals(null, result.getWorkerJMXInfo());
	}

	@Test
	public void testGetFavourities_fixture3_1()
		throws Exception {
		NodeConfig fixture = getFixture3();

		CategoryInfo result = fixture.getFavourities();

		assertNotNull(result);
		assertEquals(null, result.getClusterWide());
		assertEquals(null, result.getSystemStats());
		assertEquals(null, result.getWorkerJMXInfo());
	}

	@Test
	public void testGetFavourities_fixture4_1()
		throws Exception {
		NodeConfig fixture = getFixture4();

		CategoryInfo result = fixture.getFavourities();

		assertNotNull(result);
		assertEquals(null, result.getClusterWide());
		assertEquals(null, result.getSystemStats());
		assertEquals(null, result.getWorkerJMXInfo());
	}

	@Test
	public void testGetNodeIp_fixture1_1()
		throws Exception {
		NodeConfig fixture = getFixture1();

		String result = fixture.getNodeIp();

		assertEquals(null, result);
	}

	@Test
	public void testGetNodeIp_fixture2_1()
		throws Exception {
		NodeConfig fixture = getFixture2();

		String result = fixture.getNodeIp();

		assertEquals("", result);
	}

	@Test
	public void testGetNodeIp_fixture3_1()
		throws Exception {
		NodeConfig fixture = getFixture3();

		String result = fixture.getNodeIp();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetNodeIp_fixture4_1()
		throws Exception {
		NodeConfig fixture = getFixture4();

		String result = fixture.getNodeIp();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetTrends_fixture1_1()
		throws Exception {
		NodeConfig fixture = getFixture1();

		CategoryInfo result = fixture.getTrends();

		assertEquals(null, result);
	}

	@Test
	public void testGetTrends_fixture2_1()
		throws Exception {
		NodeConfig fixture = getFixture2();

		CategoryInfo result = fixture.getTrends();

		assertNotNull(result);
		assertEquals(null, result.getClusterWide());
		assertEquals(null, result.getSystemStats());
		assertEquals(null, result.getWorkerJMXInfo());
	}

	@Test
	public void testGetTrends_fixture3_1()
		throws Exception {
		NodeConfig fixture = getFixture3();

		CategoryInfo result = fixture.getTrends();

		assertNotNull(result);
		assertEquals(null, result.getClusterWide());
		assertEquals(null, result.getSystemStats());
		assertEquals(null, result.getWorkerJMXInfo());
	}

	@Test
	public void testGetTrends_fixture4_1()
		throws Exception {
		NodeConfig fixture = getFixture4();

		CategoryInfo result = fixture.getTrends();

		assertNotNull(result);
		assertEquals(null, result.getClusterWide());
		assertEquals(null, result.getSystemStats());
		assertEquals(null, result.getWorkerJMXInfo());
	}

	@Test
	public void testSetFavourities_fixture1_1()
		throws Exception {
		NodeConfig fixture = getFixture1();
		CategoryInfo favourities = new CategoryInfo();

		fixture.setFavourities(favourities);

	}

	@Test
	public void testSetFavourities_fixture2_1()
		throws Exception {
		NodeConfig fixture = getFixture2();
		CategoryInfo favourities = new CategoryInfo();

		fixture.setFavourities(favourities);

	}

	@Test
	public void testSetFavourities_fixture3_1()
		throws Exception {
		NodeConfig fixture = getFixture3();
		CategoryInfo favourities = new CategoryInfo();

		fixture.setFavourities(favourities);

	}

	@Test
	public void testSetFavourities_fixture4_1()
		throws Exception {
		NodeConfig fixture = getFixture4();
		CategoryInfo favourities = new CategoryInfo();

		fixture.setFavourities(favourities);

	}

	@Test
	public void testSetNodeIp_fixture1_1()
		throws Exception {
		NodeConfig fixture = getFixture1();
		String nodeIp = "";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture2_1()
		throws Exception {
		NodeConfig fixture = getFixture2();
		String nodeIp = "0123456789";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture3_1()
		throws Exception {
		NodeConfig fixture = getFixture3();
		String nodeIp = "0123456789";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture4_1()
		throws Exception {
		NodeConfig fixture = getFixture4();
		String nodeIp = "0123456789";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture2_2()
		throws Exception {
		NodeConfig fixture = getFixture2();
		String nodeIp = "";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture3_2()
		throws Exception {
		NodeConfig fixture = getFixture3();
		String nodeIp = "";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture4_2()
		throws Exception {
		NodeConfig fixture = getFixture4();
		String nodeIp = "";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture1_2()
		throws Exception {
		NodeConfig fixture = getFixture1();
		String nodeIp = "0123456789";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetTrends_fixture1_1()
		throws Exception {
		NodeConfig fixture = getFixture1();
		CategoryInfo trends = new CategoryInfo();

		fixture.setTrends(trends);

	}

	@Test
	public void testSetTrends_fixture2_1()
		throws Exception {
		NodeConfig fixture = getFixture2();
		CategoryInfo trends = new CategoryInfo();

		fixture.setTrends(trends);

	}

	@Test
	public void testSetTrends_fixture3_1()
		throws Exception {
		NodeConfig fixture = getFixture3();
		CategoryInfo trends = new CategoryInfo();

		fixture.setTrends(trends);

	}

	@Test
	public void testSetTrends_fixture4_1()
		throws Exception {
		NodeConfig fixture = getFixture4();
		CategoryInfo trends = new CategoryInfo();

		fixture.setTrends(trends);

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