package org.jumbune.web.servlet;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.jumbune.web.servlet.ClearLogsServlet;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;

import com.google.gson.Gson;

import static org.junit.Assert.*;

public class ClearLogsServletTest {
	private ClearLogsServlet fixture;
	private File tempFile;
	private HttpServletResponse response;
	private HttpServletRequest request;
	PrintWriter wOut;

	@Before
	public void setUp() throws Exception {
		fixture = new ClearLogsServlet();
		tempFile = testFolder.newFile("file.txt");
		request = Mockito.mock(HTTPServletRequestStub.class);
		response = Mockito.mock(HttpServletResponseStub.class);
		wOut = new PrintWriter(tempFile);
		Mockito.when(response.getWriter()).thenReturn(wOut);
	}

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	public ClearLogsServlet getFixture() throws Exception {
		return fixture;
	}

	@Test
	public void testDoGet_fixture_1() throws Exception {
		ClearLogsServlet fixture2 = getFixture();
		// Mockito.verify(response).setContentType(Mockito.anyString());
		// Mockito.verify(response).setCharacterEncoding(Mockito.anyString());
		fixture2.doGet(request, response);

	}

	@Test
	public void testDoPost_fixture_1() throws Exception {
		ClearLogsServlet fixture2 = getFixture();
		fixture2.doPost(request, response);

	}

	@After
	public void tearDown() throws Exception {
	}
}