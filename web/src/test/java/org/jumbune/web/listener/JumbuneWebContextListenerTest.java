package org.jumbune.web.listener;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.easymock.EasyMock;
import org.jumbune.web.listener.JumbuneWebContextListener;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXParseException;

import static org.junit.Assert.*;

public class JumbuneWebContextListenerTest {

	private File otherFolder;
	private File normalFolder;
	private File file;

	@Rule
	public TemporaryFolder myfolder = new TemporaryFolder();


	@Test
	public void testJumbuneWebContextListener_1() throws Exception {
		JumbuneWebContextListener result = new JumbuneWebContextListener();
		assertNotNull(result);
		// TODO: add additional test code here
	}

	@Test
	public void testContextInitialized_1() throws Exception {
		JumbuneWebContextListener fixture = new JumbuneWebContextListener();
		ServletContextEvent arg0 = new ServletContextEvent(
				EasyMock.createNiceMock(ServletContext.class));

		fixture.contextInitialized(arg0);

		// TODO: add additional test code here
	}

	@Test(expected = SAXParseException.class)
	public void testRemoveInstClazzEntry_10() throws Exception {
		JumbuneWebContextListener fixture = new JumbuneWebContextListener();


		String filePath = "/tmp";

		fixture.removeInstClazzEntry(filePath);

	}

	/*@Before
	public void setUp() throws Exception {

		File tempFolder = myfolder.newFolder("folder");
		File normalFolder = new File(tempFolder, "normal");
		normalFolder.mkdir();
		File file = new File(normalFolder, "file.xml");

		PrintWriter out = new PrintWriter(file);
		out.println("<xml/>");
		out.flush();
		out.close();
		// TODO: add additional set up code here
	}*/

	@After
	public void tearDown() throws Exception {
		// TODO: add additional tear down code here
	}
}