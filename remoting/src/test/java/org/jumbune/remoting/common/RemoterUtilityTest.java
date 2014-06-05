package org.jumbune.remoting.common;

import java.util.Map;
import java.util.Properties;

import org.jumbune.remoting.common.RemoterUtility;
import org.junit.*;

import static org.junit.Assert.*;

public class RemoterUtilityTest {
//	@Test(expected = NullPointerException.class)
	public void testGetAgentHome_1()
		throws Exception {
	/*	System.setProperty("AGENT_HOME",System.getProperty("user.home"));
		Process p = Runtime.getRuntime().exec("export AGENT_HOME="+System.getProperty("user.home")+"/ \n \n ");
		p.waitFor();*/
	 	
		String result = RemoterUtility.getAgentHome();
		assertNotNull(result);
	}

	//@Test
	public void testGetHadoopHome_1()
		throws Exception {
		String result = RemoterUtility.getHadoopHome();

		assertEquals(null, result);
	}

	@Before
	public void setUp()
		throws Exception {
	/*	ProcessBuilder process = new ProcessBuilder();
	 	Map<String, String> env = process.environment();
	 	 env.put("AGENT_HOME",System.getProperty("user.home"));
	 	 Process p = process.start();	*/
	}

	@After
	public void tearDown()
		throws Exception {
	}
}