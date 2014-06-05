package org.jumbune.remoting.handlers;

import java.io.File;
import java.net.URI;
import org.easymock.EasyMock;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jumbune.remoting.handlers.ArchiveDecoder;
import org.junit.*;
import static org.junit.Assert.*;

public class ArchiveDecoderTest {
	private ArchiveDecoder fixture = new ArchiveDecoder("");


	public ArchiveDecoder getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testArchiveDecoder_1()
		throws Exception {
		String receiveDirectory = "";

		ArchiveDecoder result = new ArchiveDecoder(receiveDirectory);

		assertNotNull(result);
	}

	@Test
	public void testArchiveDecoder_2()
		throws Exception {
		String receiveDirectory = "0123456789";

		ArchiveDecoder result = new ArchiveDecoder(receiveDirectory);

		assertNotNull(result);
	}
	
	@Test
	public void testReadBytesFromFile_fixture_3()
		throws Exception {
		ArchiveDecoder fixture2 = getFixture();
		File f = File.createTempFile("0123456789", "0123456789");

		byte[] result = fixture2.readBytesFromFile(f);

		assertNotNull(result);
		assertEquals(0, result.length);
	}

	@Test
	public void testReadBytesFromFile_fixture_4()
		throws Exception {
		ArchiveDecoder fixture2 = getFixture();
		File f = File.createTempFile("0123456789", "0123456789", (File) null);

		byte[] result = fixture2.readBytesFromFile(f);

		assertNotNull(result);
		assertEquals(0, result.length);
	}

	@Test
	public void testReadBytesFromFile_fixture_5()
		throws Exception {
		ArchiveDecoder fixture2 = getFixture();
		File f = File.createTempFile("An��t-1.0.txt", "An��t-1.0.txt", (File) null);

		byte[] result = fixture2.readBytesFromFile(f);

		assertNotNull(result);
		assertEquals(0, result.length);
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