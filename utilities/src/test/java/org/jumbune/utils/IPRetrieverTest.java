package org.jumbune.utils;

import java.net.InetAddress;

import junit.framework.Assert;

import org.jumbune.utils.IPRetriever;
import org.junit.Before;
import org.junit.Test;

public class IPRetrieverTest {

	String hostAddress;
	String hostName;

	@Before
	public void setUp() throws Exception {
		hostAddress = InetAddress.getLocalHost().getHostAddress();
		hostName = InetAddress.getLocalHost().getHostName();
	}

	//@Test
	public void testGetCurrentIP() {
		Assert.assertEquals(hostAddress, IPRetriever.getCurrentIP());
	}

	//@Test
	public void testGetHostName() {
		Assert.assertEquals(hostName, IPRetriever.getHostName());
	}
}