package org.jumbune.remoting.server;

import org.easymock.EasyMock;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jumbune.remoting.server.Delegator;
import org.junit.*;
import static org.junit.Assert.*;

public class DelegatorTest {
	private Delegator fixture = new Delegator("");


	public Delegator getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testDelegator_1()
		throws Exception {
		String receiveDirectory = "";

		Delegator result = new Delegator(receiveDirectory);

		assertNotNull(result);
	}

	@Test
	public void testDelegator_2()
		throws Exception {
		String receiveDirectory = "0123456789";

		Delegator result = new Delegator(receiveDirectory);

		assertNotNull(result);
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