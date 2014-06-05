package org.jumbune.web.servlet;

import org.jumbune.web.servlet.DVFileReport;
import org.junit.*;
import static org.junit.Assert.*;

public class DVFileReportTest {
	@Test
	public void testGetActualValue_1()
		throws Exception {
		DVFileReport fixture = new DVFileReport();
		fixture.setFileName("");
		fixture.setLineNumber(1);
		fixture.setActualValue("");
		fixture.setFieldNumber("");
		fixture.setExpectedValue("");

		String result = fixture.getActualValue();

		// TODO: add additional test code here
		assertEquals("", result);
	}

	@Test
	public void testGetExpectedValue_1()
		throws Exception {
		DVFileReport fixture = new DVFileReport();
		fixture.setFileName("");
		fixture.setLineNumber(1);
		fixture.setActualValue("");
		fixture.setFieldNumber("");
		fixture.setExpectedValue("");

		String result = fixture.getExpectedValue();

		// TODO: add additional test code here
		assertEquals("", result);
	}

	@Test
	public void testGetFieldNumber_1()
		throws Exception {
		DVFileReport fixture = new DVFileReport();
		fixture.setFileName("");
		fixture.setLineNumber(1);
		fixture.setActualValue("");
		fixture.setFieldNumber("");
		fixture.setExpectedValue("");

		String result = fixture.getFieldNumber();

		// TODO: add additional test code here
		assertEquals("", result);
	}

	@Test
	public void testGetFileName_1()
		throws Exception {
		DVFileReport fixture = new DVFileReport();
		fixture.setFileName("");
		fixture.setLineNumber(1);
		fixture.setActualValue("");
		fixture.setFieldNumber("");
		fixture.setExpectedValue("");

		String result = fixture.getFileName();

		// TODO: add additional test code here
		assertEquals("", result);
	}

	@Test
	public void testGetLineNumber_1()
		throws Exception {
		DVFileReport fixture = new DVFileReport();
		fixture.setFileName("");
		fixture.setLineNumber(1);
		fixture.setActualValue("");
		fixture.setFieldNumber("");
		fixture.setExpectedValue("");

		int result = fixture.getLineNumber();

		// TODO: add additional test code here
		assertEquals(1, result);
	}

	@Test
	public void testSetActualValue_1()
		throws Exception {
		DVFileReport fixture = new DVFileReport();
		fixture.setFileName("");
		fixture.setLineNumber(1);
		fixture.setActualValue("");
		fixture.setFieldNumber("");
		fixture.setExpectedValue("");
		String actualValue = "";

		fixture.setActualValue(actualValue);

		// TODO: add additional test code here
	}

	@Test
	public void testSetExpectedValue_1()
		throws Exception {
		DVFileReport fixture = new DVFileReport();
		fixture.setFileName("");
		fixture.setLineNumber(1);
		fixture.setActualValue("");
		fixture.setFieldNumber("");
		fixture.setExpectedValue("");
		String expectedValue = "";

		fixture.setExpectedValue(expectedValue);

		// TODO: add additional test code here
	}

	@Test
	public void testSetFieldNumber_1()
		throws Exception {
		DVFileReport fixture = new DVFileReport();
		fixture.setFileName("");
		fixture.setLineNumber(1);
		fixture.setActualValue("");
		fixture.setFieldNumber("");
		fixture.setExpectedValue("");
		String fieldNumber = "";

		fixture.setFieldNumber(fieldNumber);

		// TODO: add additional test code here
	}

	@Test
	public void testSetFileName_1()
		throws Exception {
		DVFileReport fixture = new DVFileReport();
		fixture.setFileName("");
		fixture.setLineNumber(1);
		fixture.setActualValue("");
		fixture.setFieldNumber("");
		fixture.setExpectedValue("");
		String fileName = "";

		fixture.setFileName(fileName);

		// TODO: add additional test code here
	}

	@Test
	public void testSetLineNumber_1()
		throws Exception {
		DVFileReport fixture = new DVFileReport();
		fixture.setFileName("");
		fixture.setLineNumber(1);
		fixture.setActualValue("");
		fixture.setFieldNumber("");
		fixture.setExpectedValue("");
		int lineNumber = 1;

		fixture.setLineNumber(lineNumber);

		// TODO: add additional test code here
	}

	@Before
	public void setUp()
		throws Exception {
		// TODO: add additional set up code here
	}

	@After
	public void tearDown()
		throws Exception {
		// TODO: add additional tear down code here
	}

	
}