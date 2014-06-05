package org.jumbune.common.utils;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jumbune.common.utils.MessageLoader;
import org.jumbune.utils.exception.JumbuneException;
import org.junit.Test;


public class MessageLoaderTest {
	MessageLoader mloader;

	@Test
	public void testGetIS() throws FileNotFoundException, JumbuneException {
		InputStream inputStream = new ByteArrayInputStream(CommonConstantsTest.CONTACTYAMLFILE.getBytes());
		mloader = new MessageLoader(inputStream);
		InputStream is = mloader.getIS();
		assertTrue(is != null);
	}

	@Test
	public void testGetMessageKeyString() throws FileNotFoundException, JumbuneException {
		InputStream inputStream = new ByteArrayInputStream(CommonConstantsTest.CONTACTYAMLFILE.getBytes());
		mloader = new MessageLoader(inputStream);
		String value = mloader.get(CommonConstantsTest.MESSAGELOADER_KEYS_TRING);
		assertTrue("Nathan Sweet".equals(value));
	}

	@Test
	public void testGetMessageKeyInt() throws FileNotFoundException, JumbuneException {
		InputStream inputStream = new ByteArrayInputStream(CommonConstantsTest.CONTACTYAMLFILE.getBytes());
		mloader = new MessageLoader(inputStream);
		String value = mloader.get(CommonConstantsTest.MESSAGELOADER_KEY_INT);
		assertTrue("3322".equals(value));
	}


}
