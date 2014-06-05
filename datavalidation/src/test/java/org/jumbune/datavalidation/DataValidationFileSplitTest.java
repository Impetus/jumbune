package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.URI;

import org.apache.hadoop.fs.Path;
import org.easymock.EasyMock;
import org.jumbune.datavalidation.DataValidationFileSplit;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class DataValidationFileSplitTest {
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	private DataValidationFileSplit fixture1 = new DataValidationFileSplit();

	public Path path;

	private DataValidationFileSplit fixture2;

	public DataValidationFileSplit getFixture1() throws Exception {
		return fixture1;
	}

	public DataValidationFileSplit getFixture2() throws Exception {
		return fixture2;
	}

	@Before
	public void setUp() throws Exception {
		fixture1 = new DataValidationFileSplit();
		path = Mockito.mock(Path.class);
		fixture2 = new DataValidationFileSplit(path, Long.MIN_VALUE,
				Long.MIN_VALUE, Integer.MAX_VALUE, new String[] { null });
	}

	@Test
	public void testDataValidationFileSplit_1() throws Exception {

		DataValidationFileSplit result = new DataValidationFileSplit();

		assertNotNull(result);
		assertEquals("null:0+0", result.toString());
		assertEquals(0L, result.getLength());
		assertEquals(null, result.getPath());
		assertEquals(0L, result.getStart());
		assertEquals(0, result.getRecordNumber());
	}

	@Test
	public void testDataValidationFileSplit_2() throws Exception {
		Path file = path;
		long start = -1L;
		long length = -1L;
		int recordNumber = 0;
		String[] hosts = new String[] { "", "0123456789", "An��t-1.0.txt", null };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_3() throws Exception {
		Path file = path;

		long start = 0L;
		long length = 0L;
		int recordNumber = 1;
		String[] hosts = new String[] { "" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_4() throws Exception {
		Path file = path;

		long start = 1L;
		long length = 1L;
		int recordNumber = 7;
		String[] hosts = new String[] { "0123456789" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_5() throws Exception {
		Path file = new Path("0123456789", "0123456789");
		long start = 0L;
		long length = 0L;
		int recordNumber = 0;
		String[] hosts = new String[] { "0123456789" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
		assertEquals("0123456789/0123456789:0+0", result.toString());
		assertEquals(0L, result.getLength());
		assertEquals(0L, result.getStart());
		assertEquals(0, result.getRecordNumber());
	}

	@Test
	public void testDataValidationFileSplit_6() throws Exception {
		Path file = path;

		long start = 1L;
		long length = 1L;
		int recordNumber = 1;
		String[] hosts = new String[] { "0123456789" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_7() throws Exception {
		Path file = path;

		long start = 0L;
		long length = -1L;
		int recordNumber = 1;
		String[] hosts = new String[] { "0123456789" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_8() throws Exception {
		Path file = new Path(URI.create(""));
		long start = 1L;
		long length = 0L;
		int recordNumber = 1;
		String[] hosts = new String[] { "0123456789" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
		assertEquals(":1+0", result.toString());
		assertEquals(0L, result.getLength());
		assertEquals(1L, result.getStart());
		assertEquals(1, result.getRecordNumber());
	}

	@Test
	public void testDataValidationFileSplit_11() throws Exception {
		Path file = path;
		long start = 0L;
		long length = 0L;
		int recordNumber = 1;
		String[] hosts = new String[] { "0123456789" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_12() throws Exception {
		Path file = path;
		long start = 0L;
		long length = 0L;
		int recordNumber = 1;
		String[] hosts = new String[] { "0123456789" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_13() throws Exception {
		Path file = path;
		long start = 0L;
		long length = 0L;
		int recordNumber = 1;
		String[] hosts = new String[] { "0123456789" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IllegalArgumentException: Can not create a Path from an
		// empty string
		// at org.apache.hadoop.fs.Path.checkPathArg(Path.java:82)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:131)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_14() throws Exception {
		Path file = path;
		long start = -1L;
		long length = -1L;
		int recordNumber = 0;
		String[] hosts = new String[] { "", "0123456789", "An��t-1.0.txt", null };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
		assertEquals(-1L, result.getLength());
		assertEquals(-1L, result.getStart());
		assertEquals(0, result.getRecordNumber());
	}

	@Test
	public void testDataValidationFileSplit_15() throws Exception {
		Path file = path;
		long start = 0L;
		long length = 0L;
		int recordNumber = 1;
		String[] hosts = new String[] { "" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IllegalArgumentException: java.net.URISyntaxException:
		// Relative path in absolute URI: 0123456789://01234567890123456789
		// at org.apache.hadoop.fs.Path.initialize(Path.java:148)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:132)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_16() throws Exception {
		Path file = path;
		long start = 1L;
		long length = 1L;
		int recordNumber = 7;
		String[] hosts = new String[] { "" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at org.apache.hadoop.fs.Path.<init>(Path.java:70)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:55)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_17() throws Exception {
		Path file = path;
		long start = 0L;
		long length = 0L;
		int recordNumber = 0;
		String[] hosts = new String[] { "" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
		assertEquals(0L, result.getLength());
		assertEquals(0L, result.getStart());
		assertEquals(0, result.getRecordNumber());
	}

	@Test
	public void testDataValidationFileSplit_18() throws Exception {
		Path file = path;
		long start = 1L;
		long length = 1L;
		int recordNumber = 0;
		String[] hosts = new String[] { "" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IllegalArgumentException: Can not create a Path from an
		// empty string
		// at org.apache.hadoop.fs.Path.checkPathArg(Path.java:82)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:90)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:50)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_19() throws Exception {
		Path file = path;
		long start = 0L;
		long length = -1L;
		int recordNumber = 0;
		String[] hosts = new String[] { "" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at org.apache.hadoop.fs.Path.<init>(Path.java:61)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_20() throws Exception {
		Path file = path;
		long start = 1L;
		long length = -1L;
		int recordNumber = 0;
		String[] hosts = new String[] { "" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IllegalArgumentException: Can not create a Path from an
		// empty string
		// at org.apache.hadoop.fs.Path.checkPathArg(Path.java:82)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:90)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_21() throws Exception {
		Path file = path;
		long start = -1L;
		long length = -1L;
		int recordNumber = 0;
		String[] hosts = new String[] { "" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IllegalArgumentException: Can not create a Path from an
		// empty string
		// at org.apache.hadoop.fs.Path.checkPathArg(Path.java:82)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:131)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_22() throws Exception {
		Path file = path;
		long start = -1L;
		long length = -1L;
		int recordNumber = 0;
		String[] hosts = new String[] { "" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IllegalArgumentException: Can not create a Path from an
		// empty string
		// at org.apache.hadoop.fs.Path.checkPathArg(Path.java:82)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:90)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:55)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_23() throws Exception {
		Path file = path;
		long start = -1L;
		long length = -1L;
		int recordNumber = 0;
		String[] hosts = new String[] { "" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
		assertEquals(-1L, result.getLength());
		assertEquals(-1L, result.getStart());
		assertEquals(0, result.getRecordNumber());
	}

	@Test
	public void testDataValidationFileSplit_24() throws Exception {
		Path file = path;
		long start = -1L;
		long length = -1L;
		int recordNumber = 0;
		String[] hosts = new String[] { "" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IllegalArgumentException: java.net.URISyntaxException:
		// Relative path in absolute URI: 0123456789://01234567890123456789
		// at org.apache.hadoop.fs.Path.initialize(Path.java:148)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:132)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_25() throws Exception {
		Path file = path;
		long start = 0L;
		long length = 0L;
		int recordNumber = 1;
		String[] hosts = new String[] { "0123456789" };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at org.apache.hadoop.fs.Path.<init>(Path.java:70)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:55)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_26() throws Exception {
		Path file = path;
		long start = -1L;
		long length = -1L;
		int recordNumber = 0;
		String[] hosts = new String[] { "", "0123456789", "An��t-1.0.txt", null };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		assertNotNull(result);
		assertEquals(-1L, result.getLength());
		assertEquals(-1L, result.getStart());
		assertEquals(0, result.getRecordNumber());
	}

	@Test
	public void testDataValidationFileSplit_27() throws Exception {
		Path file = path;
		long start = 0L;
		long length = 0L;
		int recordNumber = 1;
		String[] hosts = new String[] { "", "0123456789", "An��t-1.0.txt", null };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IllegalArgumentException: Can not create a Path from an
		// empty string
		// at org.apache.hadoop.fs.Path.checkPathArg(Path.java:82)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:90)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:50)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_28() throws Exception {
		Path file = path;
		long start = 1L;
		long length = 1L;
		int recordNumber = 7;
		String[] hosts = new String[] { "", "0123456789", "An��t-1.0.txt", null };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.NullPointerException
		// at org.apache.hadoop.fs.Path.<init>(Path.java:61)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:50)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_29() throws Exception {
		Path file = path;
		long start = 0L;
		long length = -1L;
		int recordNumber = 7;
		String[] hosts = new String[] { "", "0123456789", "An��t-1.0.txt", null };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IllegalArgumentException: Can not create a Path from an
		// empty string
		// at org.apache.hadoop.fs.Path.checkPathArg(Path.java:82)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:90)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_30() throws Exception {
		Path file = path;
		long start = 1L;
		long length = 0L;
		int recordNumber = 7;
		String[] hosts = new String[] { "", "0123456789", "An��t-1.0.txt", null };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IllegalArgumentException: Can not create a Path from an
		// empty string
		// at org.apache.hadoop.fs.Path.checkPathArg(Path.java:82)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:90)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:45)
		assertNotNull(result);
	}

	@Test
	public void testDataValidationFileSplit_31() throws Exception {
		Path file = path;
		long start = -1L;
		long length = 0L;
		int recordNumber = 7;
		String[] hosts = new String[] { "", "0123456789", "An��t-1.0.txt", null };

		DataValidationFileSplit result = new DataValidationFileSplit(file,
				start, length, recordNumber, hosts);

		// An unexpected exception was thrown in user code while executing this
		// test:
		// java.lang.IllegalArgumentException: Can not create a Path from an
		// empty string
		// at org.apache.hadoop.fs.Path.checkPathArg(Path.java:82)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:90)
		// at org.apache.hadoop.fs.Path.<init>(Path.java:55)
		assertNotNull(result);
	}

	@Test
	public void testGetLength_fixture1_1() throws Exception {
		DataValidationFileSplit fixture = getFixture1();

		long result = fixture.getLength();

		assertEquals(0L, result);
	}

	@Test
	public void testGetLength_fixture2_1() throws Exception {
		DataValidationFileSplit fixture = getFixture2();

		long result = fixture.getLength();

		assertEquals(Long.MIN_VALUE, result);
	}

	@Test
	public void testGetLocations_fixture1_1() throws Exception {
		DataValidationFileSplit fixture = getFixture1();

		String[] result = fixture.getLocations();

		assertNotNull(result);
		assertEquals(0, result.length);
	}

	@Test
	public void testGetLocations_fixture2_1() throws Exception {
		DataValidationFileSplit fixture = getFixture2();

		String[] result = fixture.getLocations();

		assertNotNull(result);
		assertEquals(1, result.length);
		assertEquals(null, result[0]);
	}

	@Test
	public void testGetPath_fixture1_1() throws Exception {
		DataValidationFileSplit fixture = getFixture1();

		Path result = fixture.getPath();

		assertEquals(null, result);
	}

	@Test
	public void testGetPath_fixture2_1() throws Exception {
		DataValidationFileSplit fixture = getFixture2();

		Path result = fixture.getPath();

		assertNotNull(result);
		assertEquals(false, result.isAbsolute());
	}

	@Test
	public void testGetRecordNumber_fixture1_1() throws Exception {
		DataValidationFileSplit fixture = getFixture1();

		int result = fixture.getRecordNumber();

		assertEquals(0, result);
	}

	@Test
	public void testGetRecordNumber_fixture2_1() throws Exception {
		DataValidationFileSplit fixture = getFixture2();

		int result = fixture.getRecordNumber();

		assertEquals(Integer.MAX_VALUE, result);
	}

	@Test
	public void testGetStart_fixture1_1() throws Exception {
		DataValidationFileSplit fixture = getFixture1();

		long result = fixture.getStart();

		assertEquals(0L, result);
	}

	@Test
	public void testGetStart_fixture2_1() throws Exception {
		DataValidationFileSplit fixture = getFixture2();

		long result = fixture.getStart();

		assertEquals(Long.MIN_VALUE, result);
	}

	//@Test
	public void testReadFields_fixture1_1() throws Exception {
		DataValidationFileSplit fixture = getFixture1();
		DataInput in = Mockito.mock(DataInput.class);
		// add mock object expectations here

		Mockito.when(in.readLong()).thenReturn(new Long(1));
		Mockito.when(in.readInt()).thenReturn(new Integer(1));


		fixture.readFields(in);

		
	}



	@Test
	public void testToString_fixture1_1() throws Exception {
		DataValidationFileSplit fixture = getFixture1();

		String result = fixture.toString();

		assertEquals("null:0+0", result);
	}

	@Test
	public void testToString_fixture2_1() throws Exception {
		DataValidationFileSplit fixture = getFixture2();

		String result = fixture.toString();

		assertNotNull(result);
	}

	


	@After
	public void tearDown() throws Exception {
	}
}