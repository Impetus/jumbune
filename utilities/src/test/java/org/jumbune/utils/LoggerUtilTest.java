package org.jumbune.utils;

import java.io.File;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.LoggerUtil;
import org.junit.*;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

public class LoggerUtilTest {
	@Test
	public void testGetChainLoggger_1()
		throws Exception {

		Logger result = LoggerUtil.getChainLoggger();

		// TODO: add additional test code here
		assertEquals(null, result);
	}

	
	@Test
	public void testGetMapReduceLoggers_1()
		throws Exception {

		List<Logger> result = LoggerUtil.getMapReduceLoggers();

		// TODO: add additional test code here
		assertEquals(null, result);
	}

	@Test(expected = org.xml.sax.SAXParseException.class)
	public void testGetXmlDocumentFromFile_1()
		throws Exception {
		File file = new File("");

		Document result = LoggerUtil.getXmlDocumentFromFile(file);

		// TODO: add additional test code here
		assertNotNull(result);
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