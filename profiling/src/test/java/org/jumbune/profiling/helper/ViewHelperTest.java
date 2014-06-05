package org.jumbune.profiling.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.profiling.beans.DataCenterInfo;
import org.jumbune.profiling.beans.NodeInfo;
import org.jumbune.profiling.beans.NodeType;
import org.jumbune.profiling.beans.RackInfo;
import org.jumbune.profiling.healthview.NetworkLatencyInfo;
import org.jumbune.profiling.utils.ViewHelper;
import org.junit.*;
import static org.junit.Assert.*;

public class ViewHelperTest {
	private ViewHelper fixture = new ViewHelper();


	public ViewHelper getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testViewHelper_1()
		throws Exception {

		ViewHelper result = new ViewHelper();

		assertNotNull(result);
	}

	

	@Test
	public void testBindNodeToRack_fixture_2()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		NodeInfo node = new NodeInfo("0123456789", NodeType.JobTracker);
		String rackId = "0123456789";
		Map<String, RackInfo> racks = new HashMap<String, RackInfo>();

		fixture2.bindNodeToRack(node, rackId, racks);

	}

	@Test
	public void testBindNodeToRack_fixture_3()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		NodeInfo node = new NodeInfo("", NodeType.DataNode);
		String rackId = "";
		Map<String, RackInfo> racks = new HashMap<String, RackInfo>();

		fixture2.bindNodeToRack(node, rackId, racks);

	}

	
	@Test
	public void testBindNodeToRack_fixture_5()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		NodeInfo node = new NodeInfo("", NodeType.DataNode);
		String rackId = "0123456789";
		HashMap<String, RackInfo> racks = new HashMap<String, RackInfo>();
		racks.put("", new RackInfo());

		fixture2.bindNodeToRack(node, rackId, racks);

	}

	@Test
	public void testBindNodeToRack_fixture_6()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		NodeInfo node = new NodeInfo();
		String rackId = "";
		Map<String, RackInfo> racks = new HashMap<String, RackInfo>();

		fixture2.bindNodeToRack(node, rackId, racks);

	}

	@Test
	public void testBindNodeToRack_fixture_7()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		NodeInfo node = new NodeInfo("", NodeType.DataNode);
		String rackId = "0123456789";
		Map<String, RackInfo> racks = new HashMap<String, RackInfo>();

		fixture2.bindNodeToRack(node, rackId, racks);

	}

	@Test
	public void testBindNodeToRack_fixture_8()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		NodeInfo node = new NodeInfo();
		String rackId = "0123456789";
		HashMap<String, RackInfo> racks = new HashMap<String, RackInfo>();
		racks.put("", new RackInfo());

		fixture2.bindNodeToRack(node, rackId, racks);

	}

	

	@Test
	public void testBindNodeToRack_fixture_10()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		NodeInfo node = new NodeInfo();
		String rackId = "0123456789";
		Map<String, RackInfo> racks = new HashMap<String, RackInfo>();

		fixture2.bindNodeToRack(node, rackId, racks);

	}

	@Test
	public void testBindNodeToRack_fixture_11()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		NodeInfo node = new NodeInfo("0123456789", NodeType.JobTracker);
		String rackId = "";
		Map<String, RackInfo> racks = new HashMap<String, RackInfo>();

		fixture2.bindNodeToRack(node, rackId, racks);

	}

	@Test
	public void testBindNodeToRack_fixture_12()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		NodeInfo node = new NodeInfo("0123456789", NodeType.JobTracker);
		String rackId = "0123456789";
		HashMap<String, RackInfo> racks = new HashMap<String, RackInfo>();
		racks.put("", new RackInfo());

		fixture2.bindNodeToRack(node, rackId, racks);

	}

	

	@Test
	public void testBindRackToDC_fixture_2()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		RackInfo rack = new RackInfo();
		String dcId = "0123456789";
		String clusterId = "0123456789";
		Map<String, DataCenterInfo> dataCenters = new HashMap<String, DataCenterInfo>();

		fixture2.bindRackToDC(rack, dcId, clusterId, dataCenters);

	}

	@Test
	public void testBindRackToDC_fixture_3()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		RackInfo rack = new RackInfo();
		String dcId = "0123456789";
		String clusterId = "0123456789";
		HashMap<String, DataCenterInfo> dataCenters = new HashMap<String, DataCenterInfo>();
		dataCenters.put("", new DataCenterInfo());

		fixture2.bindRackToDC(rack, dcId, clusterId, dataCenters);

	}

	@Test
	public void testBindRackToDC_fixture_4()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		RackInfo rack = new RackInfo();
		String dcId = "0123456789";
		String clusterId = "";
		Map<String, DataCenterInfo> dataCenters = new HashMap<String, DataCenterInfo>();

		fixture2.bindRackToDC(rack, dcId, clusterId, dataCenters);

	}

	@Test
	public void testBindRackToDC_fixture_5()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		RackInfo rack = new RackInfo();
		String dcId = "0123456789";
		String clusterId = "";
		HashMap<String, DataCenterInfo> dataCenters = new HashMap<String, DataCenterInfo>();
		dataCenters.put("", new DataCenterInfo());

		fixture2.bindRackToDC(rack, dcId, clusterId, dataCenters);

	}

	@Test
	public void testBindRackToDC_fixture_6()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		RackInfo rack = new RackInfo();
		String dcId = "";
		String clusterId = "0123456789";
		Map<String, DataCenterInfo> dataCenters = new HashMap<String, DataCenterInfo>();

		fixture2.bindRackToDC(rack, dcId, clusterId, dataCenters);

	}

	
	@Test
	public void testBindRackToDC_fixture_8()
		throws Exception {
		ViewHelper fixture2 = getFixture();
		RackInfo rack = new RackInfo();
		String dcId = "";
		String clusterId = "";
		Map<String, DataCenterInfo> dataCenters = new HashMap<String, DataCenterInfo>();

		fixture2.bindRackToDC(rack, dcId, clusterId, dataCenters);

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