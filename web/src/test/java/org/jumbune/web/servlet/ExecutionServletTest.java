package org.jumbune.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.easymock.EasyMock;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.SlaveParam;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.web.servlet.ExecutionServlet;
import org.junit.*;
import static org.junit.Assert.*;

public class ExecutionServletTest {
	@Test
	public void testExecutionServlet_1()
		throws Exception {
		ExecutionServlet result = new ExecutionServlet();
		assertNotNull(result);
		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_1()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);
		conf.setSlaveParam(new SlaveParam());

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_2()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_3()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);
		conf.setSlaveParam(new SlaveParam());

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_4()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);
		conf.setSlaveParam(new SlaveParam());

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_5()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_6()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);
		conf.setSlaveParam(new SlaveParam());

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_7()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);
		conf.setSlaveParam(new SlaveParam());

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_8()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);
		conf.setSlaveParam(new SlaveParam());

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_9()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_10()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);
		conf.setSlaveParam(new SlaveParam());

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_11()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);
		conf.setSlaveParam(new SlaveParam());

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_12()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);
		conf.setSlaveParam(new SlaveParam());

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_13()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);
		conf.setSlaveParam(new SlaveParam());

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_14()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_15()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);
		conf.setSlaveParam(new SlaveParam());

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testCheckAvailableNodes_16()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		YamlConfig conf = new YamlConfig();
		conf.setHadoopJobProfile(Enable.FALSE);

		fixture.checkAvailableNodes(conf);

		// TODO: add additional test code here
	}

	@Test
	public void testDoGet_1()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		// TODO: add mock object expectations here

		EasyMock.replay(request);
		EasyMock.replay(response);

		fixture.doGet(request, response);

		// TODO: add additional test code here
		EasyMock.verify(request);
		EasyMock.verify(response);
	}

	@Test
	public void testDoPost_1()
		throws Exception {
		ExecutionServlet fixture = new ExecutionServlet();
		HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		// TODO: add mock object expectations here

		EasyMock.replay(request);
		EasyMock.replay(response);

		fixture.doPost(request, response);

		// TODO: add additional test code here
		EasyMock.verify(request);
		EasyMock.verify(response);
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