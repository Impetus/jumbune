package org.jumbune.common.utils;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mockit.Expectations;
import mockit.Mocked;

import org.jumbune.common.utils.RemoteFileUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;

public class RemoteFileUtilTest {
	String ip=null;
	
	@Before
	public void setup() {
		ip="192.168.49.67";
	}
	@Test
	public void testGetFolderName() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IOException {
		RemoteFileUtil cUtil = new RemoteFileUtil();
		Assert.assertTrue("/home/impadmin".equals(cUtil.getFolderName("/home/impadmin/test.txt")));
	}

	@Test
	public void testGetFolderNameFileisFolder() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IOException {
		RemoteFileUtil cUtil = new RemoteFileUtil();
		Assert.assertTrue("/home/impadmin/test".equals(cUtil.getFolderName("/home/impadmin/test")));
	}

	@Test
	public void testGetFolderNameForDotSeprator() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IOException {
		RemoteFileUtil cUtil = new RemoteFileUtil();
		Assert.assertTrue("/home/imp.admin/test".equals(cUtil.getFolderName("/home/imp.admin/test")));
	}

	@Mocked
	Channel channel;
	
	@Test
	public void testGetRackId(){
		String expected="192.168.49";
		RemoteFileUtil cUtil = new RemoteFileUtil();
			assertEquals(expected,RemoteFileUtil.getRackId(ip));
	}
	
	@Test
	public void testGetDataCentreId(){
		String expected="192.168";
		RemoteFileUtil cUtil = new RemoteFileUtil();
			assertEquals(expected,RemoteFileUtil.getDataCentreId(ip));
	}
}
