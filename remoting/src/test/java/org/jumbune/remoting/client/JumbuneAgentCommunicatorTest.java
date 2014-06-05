package org.jumbune.remoting.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import org.easymock.EasyMock;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jumbune.remoting.client.JumbuneAgentCommunicator;
import org.junit.*;
import static org.junit.Assert.*;

public class JumbuneAgentCommunicatorTest {
	private JumbuneAgentCommunicator fixture1 ;


	private JumbuneAgentCommunicator fixture2;


	private JumbuneAgentCommunicator fixture3;

	@Before
	public void setUp()
		throws Exception {
		fixture1 = new JumbuneAgentCommunicator( "", 0);
		fixture2 = new JumbuneAgentCommunicator( "0123456789", 1);
		fixture3 = new JumbuneAgentCommunicator("An��t-1.0.txt", 7);
	}


	public JumbuneAgentCommunicator getFixture1()
		throws Exception {
		return fixture1;
	}

	public JumbuneAgentCommunicator getFixture2()
		throws Exception {
		return fixture2;
	}

	public JumbuneAgentCommunicator getFixture3()
		throws Exception {
		return fixture3;
	}

	@Test
	public void testJumbuneAgentCommunicator_1()
		throws Exception {
		String receiveDirectory = "";
		String host = "";
		int port = 0;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testJumbuneAgentCommunicator_2()
		throws Exception {
		String receiveDirectory = "0123456789";
		String host = "0123456789";
		int port = 1;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testJumbuneAgentCommunicator_3()
		throws Exception {
		String receiveDirectory = "0123456789";
		String host = "";
		int port = 7;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testJumbuneAgentCommunicator_4()
		throws Exception {
		String receiveDirectory = "0123456789";
		String host = "";
		int port = 0;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testJumbuneAgentCommunicator_5()
		throws Exception {
		String receiveDirectory = "";
		String host = "0123456789";
		int port = 1;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testJumbuneAgentCommunicator_6()
		throws Exception {
		String receiveDirectory = "";
		String host = "";
		int port = 7;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testJumbuneAgentCommunicator_7()
		throws Exception {
		String receiveDirectory = "";
		String host = "";
		int port = 1;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testJumbuneAgentCommunicator_8()
		throws Exception {
		String receiveDirectory = "0123456789";
		String host = "0123456789";
		int port = 7;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testJumbuneAgentCommunicator_9()
		throws Exception {
		String receiveDirectory = "0123456789";
		String host = "0123456789";
		int port = 0;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testJumbuneAgentCommunicator_10()
		throws Exception {
		String receiveDirectory = "0123456789";
		String host = "";
		int port = 1;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testJumbuneAgentCommunicator_11()
		throws Exception {
		String receiveDirectory = "";
		String host = "0123456789";
		int port = 7;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testJumbuneAgentCommunicator_12()
		throws Exception {
		String receiveDirectory = "";
		String host = "0123456789";
		int port = 0;

		JumbuneAgentCommunicator result = new JumbuneAgentCommunicator(host, port);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_1()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "";
		ArrayList<ChannelHandler> handlers = new ArrayList<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture2_1()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture2();
		String requestedOperation = "0123456789";
		LinkedList<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_2()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "";
		Vector<ChannelHandler> handlers = new Vector<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture2_2()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture2();
		String requestedOperation = "0123456789";
		List<ChannelHandler> handlers = new ArrayList<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_3()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "";
		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture2_3()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture2();
		String requestedOperation = "0123456789";
		List<ChannelHandler> handlers = new Vector<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_4()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "0123456789";
		ArrayList<ChannelHandler> handlers = new ArrayList<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture3_1()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture3();
		String requestedOperation = "";
		LinkedList<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_5()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "0123456789";
		Vector<ChannelHandler> handlers = new Vector<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture3_2()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture3();
		String requestedOperation = "";
		List<ChannelHandler> handlers = new ArrayList<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_6()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "0123456789";
		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture3_3()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture3();
		String requestedOperation = "";
		List<ChannelHandler> handlers = new Vector<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture2_4()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture2();
		String requestedOperation = "";
		ArrayList<ChannelHandler> handlers = new ArrayList<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture3_4()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture3();
		String requestedOperation = "0123456789";
		LinkedList<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture2_5()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture2();
		String requestedOperation = "";
		Vector<ChannelHandler> handlers = new Vector<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture3_5()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture3();
		String requestedOperation = "0123456789";
		List<ChannelHandler> handlers = new ArrayList<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture2_6()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture2();
		String requestedOperation = "";
		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture3_6()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture3();
		String requestedOperation = "0123456789";
		List<ChannelHandler> handlers = new Vector<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture2_7()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture2();
		String requestedOperation = "0123456789";
		ArrayList<ChannelHandler> handlers = new ArrayList<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_7()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "";
		LinkedList<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture2_8()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture2();
		String requestedOperation = "0123456789";
		Vector<ChannelHandler> handlers = new Vector<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_8()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "";
		List<ChannelHandler> handlers = new ArrayList<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture2_9()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture2();
		String requestedOperation = "0123456789";
		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_9()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "";
		List<ChannelHandler> handlers = new Vector<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture3_7()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture3();
		String requestedOperation = "";
		ArrayList<ChannelHandler> handlers = new ArrayList<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_10()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "0123456789";
		LinkedList<ChannelHandler> handlers = new LinkedList<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture3_8()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture3();
		String requestedOperation = "";
		Vector<ChannelHandler> handlers = new Vector<ChannelHandler>();
		handlers.add(EasyMock.createNiceMock(ChannelHandler.class));

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_11()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "0123456789";
		List<ChannelHandler> handlers = new ArrayList<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture3_9()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture3();
		String requestedOperation = "";
		List<ChannelHandler> handlers = new LinkedList<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	@Test
	public void testCreateOrGetChannelPipelineFactory_fixture1_12()
		throws Exception {
		JumbuneAgentCommunicator fixture = getFixture1();
		String requestedOperation = "0123456789";
		List<ChannelHandler> handlers = new Vector<ChannelHandler>();

		ChannelPipelineFactory result = fixture.createOrGetChannelPipelineFactory(requestedOperation, handlers);

		assertNotNull(result);
	}

	

	

	@After
	public void tearDown()
		throws Exception {
	}
}