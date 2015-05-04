package org.jumbune.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jumbune.utils.JobUtil;
import org.junit.Test;

public class JobUtilTest {

	Object obj;

	@Test
	public void testGetPlaceHolder() throws IOException {
		int[] value = JobUtil.getPlaceHolder(UtilitiesConstantsTestInterface.STR, 0);
		boolean check = value != null;
		assertTrue(check);
	}

	@Test
	public void testGetPlaceHolderNull() throws IOException {
		int[] value = JobUtil.getPlaceHolder(UtilitiesConstantsTestInterface.STRING, 0);
		assertEquals(value, null);
	}

	@Test
	public void testGetPlaceHolders() {
		String[] array = JobUtil.getPlaceHolders(UtilitiesConstantsTestInterface.STR);
		boolean check = array != null;
		assertTrue(check);
	}

	@Test
	public void testGetPlaceHoldersNull() {
		String[] array = JobUtil.getPlaceHolders(UtilitiesConstantsTestInterface.STRING);
		boolean check = array == null;
		assertTrue(check);
	}

	@Test
	public void testGetAndReplacereplaceHolder() {
		String[] strArray = new String[] { UtilitiesConstantsTestInterface.REGEX, UtilitiesConstantsTestInterface.REG };
		String[] array = JobUtil.getAndReplaceHolders(strArray);

		assertTrue(array != null);

	}

	@Test
	public void testValidateIPAddress() {
		assertTrue(JobUtil.validateIPAddress(UtilitiesConstantsTestInterface.VALIDIPADDRRESS));
	}

}
