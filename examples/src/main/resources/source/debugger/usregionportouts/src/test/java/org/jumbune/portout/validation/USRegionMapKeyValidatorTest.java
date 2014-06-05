package org.jumbune.portout.validation;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.easymock.EasyMock;
import org.jumbune.portout.validation.USRegionMapKeyValidator;
import org.junit.*;
import static org.junit.Assert.*;

public class USRegionMapKeyValidatorTest {
	private USRegionMapKeyValidator fixture = new USRegionMapKeyValidator();


	public USRegionMapKeyValidator getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testIsPatternValid_fixture_1()
		throws Exception {
		USRegionMapKeyValidator fixture2 = getFixture();
		WritableComparable value = EasyMock.createMock(WritableComparable.class);
		// add mock object expectations here

		EasyMock.replay(value);

		boolean result = fixture2.isPatternValid(value);

		EasyMock.verify(value);
		assertEquals(false, result);
	}

	@Test
	public void testIsPatternValid_fixture_2()
		throws Exception {
		USRegionMapKeyValidator fixture2 = getFixture();
		WritableComparable value = new Text("");

		boolean result = fixture2.isPatternValid(value);

		assertEquals(false, result);
	}

	@Test
	public void testIsPatternValid_fixture_3()
		throws Exception {
		USRegionMapKeyValidator fixture2 = getFixture();
		WritableComparable value = new Text();

		boolean result = fixture2.isPatternValid(value);

		assertEquals(false, result);
	}

	@Test
	public void testIsPatternValid_fixture_4()
		throws Exception {
		USRegionMapKeyValidator fixture2 = getFixture();
		WritableComparable value = new Text(new byte[] {(byte) -1, (byte) 0, (byte) 1, Byte.MAX_VALUE, Byte.MIN_VALUE});

		boolean result = fixture2.isPatternValid(value);

		assertEquals(false, result);
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