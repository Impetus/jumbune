package org.jumbune.remoting.handlers;

import org.easymock.EasyMock;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jumbune.remoting.handlers.LogFilesDecoder;
import org.junit.*;
import static org.junit.Assert.*;

public class LogFilesDecoderTest {
	private LogFilesDecoder fixture = new LogFilesDecoder("");


	public LogFilesDecoder getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testLogFilesDecoder_1()
		throws Exception {
		String receiveDirectory = "";

		LogFilesDecoder result = new LogFilesDecoder(receiveDirectory);

		assertNotNull(result);
	}

	@Test
	public void testLogFilesDecoder_2()
		throws Exception {
		String receiveDirectory = "0123456789";

		LogFilesDecoder result = new LogFilesDecoder(receiveDirectory);

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