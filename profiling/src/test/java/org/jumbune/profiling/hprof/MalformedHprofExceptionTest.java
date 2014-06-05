package org.jumbune.profiling.hprof;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jumbune.profiling.hprof.MalformedHprofException;
import org.junit.Test;

public class MalformedHprofExceptionTest {

	
	@Test
	public void testMalformedHprofException_1()
		throws Exception {
		String message = "";

		MalformedHprofException result = new MalformedHprofException(message);

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getCause());
		assertEquals("org.jumbune.profiling.hprof.MalformedHprofException: ", result.toString());
		assertEquals("", result.getMessage());
	}
	
	@Test
	public void testMalformedHprofException_2()
		throws Exception {
		Throwable cause = new Throwable();

		MalformedHprofException result = new MalformedHprofException(cause);

		// add additional test code here
		assertNotNull(result);
		assertEquals("org.jumbune.profiling.hprof.MalformedHprofException: java.lang.Throwable", result.toString());
		assertEquals("java.lang.Throwable", result.getMessage());
	}
}
