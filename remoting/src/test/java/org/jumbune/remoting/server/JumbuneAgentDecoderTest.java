package org.jumbune.remoting.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jumbune.remoting.server.JumbuneAgentDecoder;
import org.junit.*;
import static org.junit.Assert.*;

public class JumbuneAgentDecoderTest {
	
	@Test
	public void testJumbuneAgentDecoder_2()
		throws Exception {
		String receiveDirectory =System.getProperty("user.name");

		JumbuneAgentDecoder result = new JumbuneAgentDecoder(receiveDirectory);

		assertNotNull(result);
		assertEquals(false, result.isUnfold());
		assertEquals(0, result.getMaxCumulationBufferCapacity());
		assertEquals(1024, result.getMaxCumulationBufferComponents());
	}

	@Test
	public void testJumbuneAgentDecoder_3()
		throws Exception {
		String receiveDirectory =System.getProperty("user.name");

		JumbuneAgentDecoder result = new JumbuneAgentDecoder(receiveDirectory);

		assertNotNull(result);
		assertEquals(false, result.isUnfold());
		assertEquals(0, result.getMaxCumulationBufferCapacity());
		assertEquals(1024, result.getMaxCumulationBufferComponents());
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