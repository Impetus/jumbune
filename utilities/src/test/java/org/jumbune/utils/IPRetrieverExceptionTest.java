package org.jumbune.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jumbune.utils.IPRetriever;

import junit.framework.Assert;
import mockit.Expectations;
import mockit.Mocked;

public class IPRetrieverExceptionTest {

	@Mocked
	InetAddress inetaddress;

	// @Test
	public void testGetCurrentIPException() throws Exception {
		new Expectations() {
			{
				InetAddress.getLocalHost();
				result = new UnknownHostException();
			}
		};

		Assert.assertEquals(null, IPRetriever.getCurrentIP());
	}

	// @Test
	public void testGetHostNameException() throws Exception {
		new Expectations() {
			{
				InetAddress.getLocalHost();
				result = new UnknownHostException();
			}
		};

		Assert.assertEquals(null, IPRetriever.getHostName());
	}
}