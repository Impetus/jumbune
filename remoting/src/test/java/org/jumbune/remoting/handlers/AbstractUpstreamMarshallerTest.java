package org.jumbune.remoting.handlers;

import org.easymock.EasyMock;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jumbune.remoting.handlers.AbstractUpstreamMarshaller;
import org.jumbune.remoting.handlers.ArchiveDecoder;
import org.jumbune.remoting.handlers.LogFilesDecoder;
import org.jumbune.remoting.handlers.ObjectDecoder;
import org.junit.*;
import static org.junit.Assert.*;

public class AbstractUpstreamMarshallerTest {
	private AbstractUpstreamMarshaller fixture1 = new ArchiveDecoder("");


	private AbstractUpstreamMarshaller fixture2 = new LogFilesDecoder("");


	private AbstractUpstreamMarshaller fixture3 = new ObjectDecoder();


	public AbstractUpstreamMarshaller getFixture1()
		throws Exception {
		return fixture1;
	}

	public AbstractUpstreamMarshaller getFixture2()
		throws Exception {
		return fixture2;
	}

	public AbstractUpstreamMarshaller getFixture3()
		throws Exception {
		return fixture3;
	}

	@Test
	public void testByteArrayToInt_fixture1_1()
		throws Exception {
		AbstractUpstreamMarshaller fixture = getFixture1();
		byte[] b = new byte[] {(byte) -1, (byte) 0, (byte) 1, Byte.MAX_VALUE, Byte.MIN_VALUE};

		int result = fixture.byteArrayToInt(b);

		assertEquals(-16776833, result);
	}

	
	@Test
	public void testByteArrayToInt_fixture3_2()
		throws Exception {
		AbstractUpstreamMarshaller fixture = getFixture3();
		byte[] b = new byte[] {(byte) -1, (byte) 0, (byte) 1, Byte.MAX_VALUE, Byte.MIN_VALUE};

		int result = fixture.byteArrayToInt(b);

		assertEquals(-16776833, result);
	}

	
	

	@Test
	public void testByteArrayToInt_fixture2_3()
		throws Exception {
		AbstractUpstreamMarshaller fixture = getFixture2();
		byte[] b = new byte[] {(byte) -1, (byte) 0, (byte) 1, Byte.MAX_VALUE, Byte.MIN_VALUE};

		int result = fixture.byteArrayToInt(b);

		assertEquals(-16776833, result);
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