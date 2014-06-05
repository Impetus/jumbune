package org.jumbune.web.servlet;

import static org.junit.Assert.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jumbune.web.servlet.UploadJobJarServlet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UploadJobJarServletTest {
	private HttpServletResponse response;
	private HttpServletRequest request;
	UploadJobJarServlet fixture;
	@Before
	public void setUp() throws Exception {
		request = Mockito.mock(HTTPServletRequestStub.class);
		response = Mockito.mock(HttpServletResponseStub.class);
		fixture=  new UploadJobJarServlet();
	}

//	@Test(expected=javax.servlet.ServletException.class)
	public void testProcessRequest() throws ServletException {
		fixture.processRequest(request, response);
		
	}

}
