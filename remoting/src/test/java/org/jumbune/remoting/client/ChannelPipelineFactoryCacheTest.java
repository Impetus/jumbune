package org.jumbune.remoting.client;

import java.util.Map;
import org.easymock.EasyMock;
import org.jumbune.remoting.client.ChannelPipelineFactoryCache;
import org.junit.*;
import static org.junit.Assert.*;

public class ChannelPipelineFactoryCacheTest {
	private ChannelPipelineFactoryCache fixture;


	public ChannelPipelineFactoryCache getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testChannelPipelineFactoryCache_1()
		throws Exception {
		int capacity = 0;

		ChannelPipelineFactoryCache result = new ChannelPipelineFactoryCache(capacity);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testChannelPipelineFactoryCache_2()
		throws Exception {
		int capacity = 1;

		ChannelPipelineFactoryCache result = new ChannelPipelineFactoryCache(capacity);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testChannelPipelineFactoryCache_3()
		throws Exception {
		int capacity = 7;

		ChannelPipelineFactoryCache result = new ChannelPipelineFactoryCache(capacity);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testRemoveEldestEntry_fixture_1()
		throws Exception {
		ChannelPipelineFactoryCache fixture2 = getFixture();
		java.util.Map.Entry<String, org.jboss.netty.channel.ChannelPipelineFactory> eldest = EasyMock.createMock(java.util.Map.Entry.class);
		// add mock object expectations here

		EasyMock.replay(eldest);

		boolean result = fixture2.removeEldestEntry(eldest);

		EasyMock.verify(eldest);
		assertEquals(false, result);
	}

	@Before
	public void setUp()
		throws Exception {
		fixture = new ChannelPipelineFactoryCache(0);
	}

	@After
	public void tearDown()
		throws Exception {
	}
}