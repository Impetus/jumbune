package org.jumbune.utils.exception;

import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.junit.*;
import static org.junit.Assert.*;

public class JumbuneExceptionTest {
	@Test
	public void testJumbuneException_1()
		throws Exception {
		ErrorCodesAndMessages errorCodeAndMessage = ErrorCodesAndMessages.COULD_NOT_CREATE_DIRECTORY;

		JumbuneException result = new JumbuneException(errorCodeAndMessage);

		// TODO: add additional test code here
		assertNotNull(result);

	}


	@Test
	public void testGetErrorCode_1()
		throws Exception {
		JumbuneException fixture = new JumbuneException(ErrorCodesAndMessages.COULD_NOT_CREATE_DIRECTORY);
		fixture.setErrorCode(1);
		fixture.setErrorMessage("");

		int result = fixture.getErrorCode();

		// TODO: add additional test code here
		assertEquals(1, result);
	}

	@Test
	public void testGetErrorMessage_1()
		throws Exception {
		JumbuneException fixture = new JumbuneException(ErrorCodesAndMessages.COULD_NOT_CREATE_DIRECTORY);
		fixture.setErrorCode(1);
		fixture.setErrorMessage("");

		String result = fixture.getErrorMessage();

		// TODO: add additional test code here
		assertEquals("", result);
	}

	@Test
	public void testSetErrorCode_1()
		throws Exception {
		JumbuneException fixture = new JumbuneException(ErrorCodesAndMessages.COULD_NOT_CREATE_DIRECTORY);
		fixture.setErrorCode(1);
		fixture.setErrorMessage("");
		int errorCode = 1;

		fixture.setErrorCode(errorCode);

		// TODO: add additional test code here
	}

	@Test
	public void testSetErrorMessage_1()
		throws Exception {
		JumbuneException fixture = new JumbuneException(ErrorCodesAndMessages.COULD_NOT_CREATE_DIRECTORY);
		fixture.setErrorCode(1);
		fixture.setErrorMessage("");
		String errorMessage = "";

		fixture.setErrorMessage(errorMessage);

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
