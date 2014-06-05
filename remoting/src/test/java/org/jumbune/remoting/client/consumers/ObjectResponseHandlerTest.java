package org.jumbune.remoting.client.consumers;

import java.util.concurrent.CyclicBarrier;
import org.easymock.EasyMock;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jumbune.remoting.client.consumers.ObjectResponseHandler;
import org.junit.*;
import static org.junit.Assert.*;

public class ObjectResponseHandlerTest {
	
	@Test
	public void testObjectResponseHandler_3()
		throws Exception {
		CyclicBarrier barrier = new CyclicBarrier(1, (Runnable) null);

		ObjectResponseHandler result = new ObjectResponseHandler(barrier);

		assertNotNull(result);
		assertEquals(null, result.getResponseObject());
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