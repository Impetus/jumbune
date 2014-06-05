package org.jumbune.remoting.client.consumers;

import java.io.InputStream;
import org.easymock.EasyMock;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jumbune.remoting.client.consumers.StringResponseHandler;
import org.junit.*;
import static org.junit.Assert.*;

public class StringResponseHandlerTest {
	private StringResponseHandler fixture;


	public StringResponseHandler getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testStringResponseHandler_1()
		throws Exception {

		StringResponseHandler result = new StringResponseHandler();

		assertNotNull(result);
		assertEquals(null, result.getResponseStream());
	}

	

	@Test
	public void testGetResponseStream_fixture_1()
		throws Exception {
		StringResponseHandler fixture2 = getFixture();

		InputStream result = fixture2.getResponseStream();

		assertEquals(null, result);
	}



	

	@Before
	public void setUp()
		throws Exception {

		fixture = new StringResponseHandler();
	}

	@After
	public void tearDown()
		throws Exception {
	}
}