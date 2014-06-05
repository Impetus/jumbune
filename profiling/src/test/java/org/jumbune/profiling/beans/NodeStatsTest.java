package org.jumbune.profiling.beans;

import java.util.HashMap;
import java.util.Map;

import org.jumbune.profiling.beans.NodeStats;
import org.jumbune.profiling.beans.StatsResult;
import org.jumbune.profiling.hprof.NodePerformance;
import org.junit.*;
import static org.junit.Assert.*;


public class NodeStatsTest {
	private NodeStats fixture1 = new NodeStats("");


	private NodeStats fixture2;

	{
		fixture2 = new NodeStats("");
		fixture2.setColorConfig(new HashMap<String, NodePerformance>());
		fixture2.setFavourities(new StatsResult());
		fixture2.setTrends(new StatsResult());
	}

	public NodeStats getFixture1()
		throws Exception {
		return fixture1;
	}

	public NodeStats getFixture2()
		throws Exception {
		if (fixture2 == null) {
			fixture2 = new NodeStats("");
			fixture2.setColorConfig(new HashMap<String, NodePerformance>());
			fixture2.setFavourities(new StatsResult());
			fixture2.setTrends(new StatsResult());
		}
		return fixture2;
	}

	@Test
	public void testNodeStats_1()
		throws Exception {
		String nodeIp = "";

		NodeStats result = new NodeStats(nodeIp);

		assertNotNull(result);
		assertEquals(null, result.getColorConfig());
		assertEquals("", result.getNodeIp());
		assertEquals(null, result.getFavourities());
		assertEquals(null, result.getTrends());
	}

	@Test
	public void testNodeStats_2()
		throws Exception {
		String nodeIp = "0123456789";

		NodeStats result = new NodeStats(nodeIp);

		assertNotNull(result);
		assertEquals(null, result.getColorConfig());
		assertEquals("0123456789", result.getNodeIp());
		assertEquals(null, result.getFavourities());
		assertEquals(null, result.getTrends());
	}

	@Test
	public void testGetColorConfig_fixture1_1()
		throws Exception {
		NodeStats fixture = getFixture1();

		Map<String, NodePerformance> result = fixture.getColorConfig();

		assertEquals(null, result);
	}

	@Test
	public void testGetColorConfig_fixture2_1()
		throws Exception {
		NodeStats fixture = getFixture2();

		Map<String, NodePerformance> result = fixture.getColorConfig();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetFavourities_fixture1_1()
		throws Exception {
		NodeStats fixture = getFixture1();

		StatsResult result = fixture.getFavourities();

		assertEquals(null, result);
	}

	@Test
	public void testGetFavourities_fixture2_1()
		throws Exception {
		NodeStats fixture = getFixture2();

		StatsResult result = fixture.getFavourities();

		assertNotNull(result);
		assertEquals(null, result.getClusterWide());
		assertEquals(null, result.getHadoopJMX());
		assertEquals(null, result.getSystemStats());
	}

	@Test
	public void testGetNodeIp_fixture1_1()
		throws Exception {
		NodeStats fixture = getFixture1();

		String result = fixture.getNodeIp();

		assertEquals("", result);
	}

	@Test
	public void testGetNodeIp_fixture2_1()
		throws Exception {
		NodeStats fixture = getFixture2();

		String result = fixture.getNodeIp();

		assertEquals("", result);
	}

	@Test
	public void testGetTrends_fixture1_1()
		throws Exception {
		NodeStats fixture = getFixture1();

		StatsResult result = fixture.getTrends();

		assertEquals(null, result);
	}

	@Test
	public void testGetTrends_fixture2_1()
		throws Exception {
		NodeStats fixture = getFixture2();

		StatsResult result = fixture.getTrends();

		assertNotNull(result);
		assertEquals(null, result.getClusterWide());
		assertEquals(null, result.getHadoopJMX());
		assertEquals(null, result.getSystemStats());
	}

	@Test
	public void testSetColorConfig_fixture1_1()
		throws Exception {
		NodeStats fixture = getFixture1();
		Map<String, NodePerformance> colorConfig = new HashMap<String, NodePerformance>();

		fixture.setColorConfig(colorConfig);

	}

	@Test
	public void testSetColorConfig_fixture2_1()
		throws Exception {
		NodeStats fixture = getFixture2();
		Map<String, NodePerformance> colorConfig = new HashMap<String, NodePerformance>();

		fixture.setColorConfig(colorConfig);

	}

	@Test
	public void testSetFavourities_fixture1_1()
		throws Exception {
		NodeStats fixture = getFixture1();
		StatsResult favourities = new StatsResult();

		fixture.setFavourities(favourities);

	}

	@Test
	public void testSetFavourities_fixture2_1()
		throws Exception {
		NodeStats fixture = getFixture2();
		StatsResult favourities = new StatsResult();

		fixture.setFavourities(favourities);

	}

	@Test
	public void testSetNodeIp_fixture1_1()
		throws Exception {
		NodeStats fixture = getFixture1();
		String nodeIp = "";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture2_1()
		throws Exception {
		NodeStats fixture = getFixture2();
		String nodeIp = "0123456789";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture2_2()
		throws Exception {
		NodeStats fixture = getFixture2();
		String nodeIp = "";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetNodeIp_fixture1_2()
		throws Exception {
		NodeStats fixture = getFixture1();
		String nodeIp = "0123456789";

		fixture.setNodeIp(nodeIp);

	}

	@Test
	public void testSetTrends_fixture1_1()
		throws Exception {
		NodeStats fixture = getFixture1();
		StatsResult trends = new StatsResult();

		fixture.setTrends(trends);

	}

	@Test
	public void testSetTrends_fixture2_1()
		throws Exception {
		NodeStats fixture = getFixture2();
		StatsResult trends = new StatsResult();

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