package org.jumbune.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.easymock.EasyMock;
import org.jumbune.web.servlet.HomeServlet;
import org.junit.*;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class HomeServletTest {
	
	private HttpServletResponse response;
	private HttpServletRequest request;
	
	
	@Test
	public void testHomeServlet_1()
		throws Exception {
		HomeServlet result = new HomeServlet();
		assertNotNull(result);
		
	}

	@Test
	public void testDoGet_1()
		throws Exception {
		HomeServlet fixture = new HomeServlet();
		

		// TODO: add mock object expectations here

		


		fixture.doGet(request, response);

		


	}

	@Test
	public void testDoPost_1()
		throws Exception {
		HomeServlet fixture = new HomeServlet();
		

		// TODO: add mock object expectations here

		


		fixture.doPost(request, response);

		


	}

	//@Test
	public void testService_1()
		throws Exception {
		HomeServlet fixture = new HomeServlet();
		

		// TODO: add mock object expectations here

		


		fixture.service(request, response);

		


		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at javax.servlet.http.HttpServlet.service(HttpServlet.java:684)
		//       at org.jumbune.web.servlet.HomeServlet.service(HomeServlet.java:48)
	}

	//@Test
	public void testService_2()
		throws Exception {
		HomeServlet fixture = new HomeServlet();
		

		// TODO: add mock object expectations here

		


		fixture.service(request, response);

		


		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at javax.servlet.http.HttpServlet.service(HttpServlet.java:684)
		//       at org.jumbune.web.servlet.HomeServlet.service(HomeServlet.java:48)
	}

	//@Test
	public void testService_3()
		throws Exception {
		HomeServlet fixture = new HomeServlet();
		

		// TODO: add mock object expectations here

		


		fixture.service(request, response);

		


		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at javax.servlet.http.HttpServlet.service(HttpServlet.java:684)
		//       at org.jumbune.web.servlet.HomeServlet.service(HomeServlet.java:48)
	}

	//@Test
	public void testService_4()
		throws Exception {
		HomeServlet fixture = new HomeServlet();
		

		// TODO: add mock object expectations here

		


		fixture.service(request, response);

		


		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at javax.servlet.http.HttpServlet.service(HttpServlet.java:684)
		//       at org.jumbune.web.servlet.HomeServlet.service(HomeServlet.java:48)
	}

	//@Test
	public void testService_5()
		throws Exception {
		HomeServlet fixture = new HomeServlet();
		

		// TODO: add mock object expectations here

		


		fixture.service(request, response);

		


		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at javax.servlet.http.HttpServlet.service(HttpServlet.java:684)
		//       at org.jumbune.web.servlet.HomeServlet.service(HomeServlet.java:48)
	}

	//@Test
	public void testService_6()
		throws Exception {
		HomeServlet fixture = new HomeServlet();
		

		// TODO: add mock object expectations here

		


		fixture.service(request, response);

		
	
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at javax.servlet.http.HttpServlet.service(HttpServlet.java:684)
		//       at org.jumbune.web.servlet.HomeServlet.service(HomeServlet.java:48)
	}

	@Before
	public void setUp()
		throws Exception {
		request = Mockito.mock(HTTPServletRequestStub.class);
		response = Mockito.mock(HttpServletResponseStub.class);
		// TODO: add additional set up code here
	}

	@After
	public void tearDown()
		throws Exception {
		// TODO: add additional tear down code here
	}
}