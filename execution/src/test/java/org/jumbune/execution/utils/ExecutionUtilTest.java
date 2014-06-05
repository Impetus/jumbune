package org.jumbune.execution.utils;

import java.util.Scanner;

import junit.framework.Assert;

import org.jumbune.execution.utils.ExecutionUtil;
import org.junit.Test;


public class ExecutionUtilTest {

	private static final String MESSAGE = "This is sample message";
	private static final String QUESTION = "This is sample question?";
	private Scanner input;

	@Test
	public void testreadInputFromConsole() {
		input = new Scanner("jjj");
		Assert.assertEquals("jjj", ExecutionUtil.readInputFromConsole(input, MESSAGE, QUESTION));
	}

	@Test
	public void testValidAskYesNoInfo() {
		input = new Scanner("yes");
		Assert.assertTrue(ExecutionUtil.askYesNoInfo(input, MESSAGE, QUESTION));
	}

	@Test
	public void testInValidAskYesNoInfo() {
		input = new Scanner("no");
		Assert.assertFalse(ExecutionUtil.askYesNoInfo(input, MESSAGE, QUESTION));
	}

	@Test
	public void testaskLogLevelInfo() {
		input = new Scanner("info");
		Assert.assertNotNull(ExecutionUtil.askLogLevelInfo(input, MESSAGE, QUESTION));
	}
}