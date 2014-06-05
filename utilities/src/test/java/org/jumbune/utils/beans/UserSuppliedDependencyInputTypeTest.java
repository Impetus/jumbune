package org.jumbune.utils.beans;

import org.jumbune.utils.beans.UserSuppliedDependencyInputType;
import org.junit.*;
import static org.junit.Assert.*;

public class UserSuppliedDependencyInputTypeTest {
	@Test
	public void testGetDependentJarOption_1()
		throws Exception {
		UserSuppliedDependencyInputType fixture = UserSuppliedDependencyInputType.FIVE;

		int result = fixture.getDependentJarOption();

		// TODO: add additional test code here
		assertNotNull(result);
	}

	//@Test
	public void testGetOptionValue_1()
		throws Exception {
		UserSuppliedDependencyInputType fixture = UserSuppliedDependencyInputType.FIVE;

		int result = fixture.getOptionValue();

		// TODO: add additional test code here
		assertEquals(5, result);
	}

	@Test
	public void testIsValid_1()
		throws Exception {
		int value = 1;

		Boolean result = UserSuppliedDependencyInputType.isValid(value);

		// TODO: add additional test code here
		assertNotNull(result);
		assertEquals("true", result.toString());
		assertEquals(true, result.booleanValue());
	}

	@Test
	public void testIsValid_2()
		throws Exception {
		int value = 1;

		Boolean result = UserSuppliedDependencyInputType.isValid(value);

		// TODO: add additional test code here
		assertNotNull(result);
		assertEquals("true", result.toString());
		assertEquals(true, result.booleanValue());
	}

	@Test
	public void testIsValid_3()
		throws Exception {
		int value = 1;

		Boolean result = UserSuppliedDependencyInputType.isValid(value);

		// TODO: add additional test code here
		assertNotNull(result);
		assertEquals("true", result.toString());
		assertEquals(true, result.booleanValue());
	}

	@Test
	public void testSetDependentJarOption_1()
		throws Exception {
		UserSuppliedDependencyInputType fixture = UserSuppliedDependencyInputType.FIVE;
		int dependentJarOption = 1;

		fixture.setDependentJarOption(dependentJarOption);

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