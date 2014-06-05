package org.jumbune.remoting.handlers;

import org.easymock.EasyMock;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jumbune.remoting.handlers.ObjectDecoder;
import org.junit.*;
import static org.junit.Assert.*;

public class ObjectDecoderTest {
	private ObjectDecoder fixture = new ObjectDecoder();


	public ObjectDecoder getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testObjectDecoder_1()
		throws Exception {

		ObjectDecoder result = new ObjectDecoder();

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