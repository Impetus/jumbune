package org.jumbune.portout.validation;

import org.apache.hadoop.io.WritableComparable;
import org.easymock.EasyMock;
import org.jumbune.portout.validation.PortoutReduceKeyValidator;
import org.junit.*;
import static org.junit.Assert.*;

public class PortoutReduceKeyValidatorTest {
	private PortoutReduceKeyValidator fixture = new PortoutReduceKeyValidator();


	public PortoutReduceKeyValidator getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testIsPatternValid_fixture_1()
		throws Exception {
		PortoutReduceKeyValidator fixture2 = getFixture();
		WritableComparable value = EasyMock.createMock(WritableComparable.class);
		// add mock object expectations here

		EasyMock.replay(value);

		boolean result = fixture2.isPatternValid(value);

		EasyMock.verify(value);
		assertEquals(true, result);
	}

	@Before
	public void setUp()
		throws Exception {
	}

	@After
	public void tearDown()
		throws Exception {
	}
}