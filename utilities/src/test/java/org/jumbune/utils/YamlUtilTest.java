package org.jumbune.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jumbune.utils.YamlUtil;
import org.junit.Test;

public class YamlUtilTest {

	Object obj;

	@Test
	public void testGetPlaceHolder() throws IOException {
		int[] value = YamlUtil.getPlaceHolder(UtilitiesConstantsTest.STR, 0);
		boolean check = value != null;
		assertTrue(check);
	}

	@Test
	public void testGetPlaceHolderNull() throws IOException {
		int[] value = YamlUtil.getPlaceHolder(UtilitiesConstantsTest.STRING, 0);
		assertEquals(value, null);
	}

	@Test
	public void testGetPlaceHolders() {
		String[] array = YamlUtil.getPlaceHolders(UtilitiesConstantsTest.STR);
		boolean check = array != null;
		assertTrue(check);
	}

	@Test
	public void testGetPlaceHoldersNull() {
		String[] array = YamlUtil.getPlaceHolders(UtilitiesConstantsTest.STRING);
		boolean check = array == null;
		assertTrue(check);
	}

	@Test
	public void testGetAndReplacereplaceHolder() {
		String[] strArray = new String[] { UtilitiesConstantsTest.REGEX, UtilitiesConstantsTest.REG };
		String[] array = YamlUtil.getAndReplaceHolders(strArray);

		assertTrue(array != null);

	}

	@Test
	public void testValidateIPAddress() {
		assertTrue(YamlUtil.validateIPAddress(UtilitiesConstantsTest.VALIDIPADDRRESS));
	}

	@Test
	public void testValidateFileSystemLocation() {
		assertTrue(YamlUtil.validateFileSystemLocation(UtilitiesConstantsTest.VALID_FILE_LOCATION));
	}

	@Test
	public void testSerializeObjectToYaml() {
		String str = YamlUtil.serializeObjectToYaml(obj);
		assertTrue(str != null);
	}
}
