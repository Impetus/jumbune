package org.jumbune.datavalidation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import org.easymock.EasyMock;
import org.jumbune.datavalidation.FileViolationsWritable;
import org.junit.*;
import static org.junit.Assert.*;

public class FileViolationsWritableTest {
	private FileViolationsWritable fixture1 = new FileViolationsWritable();


	private FileViolationsWritable fixture2;

	{
		fixture2 = new FileViolationsWritable();
		fixture2.setFileName("");
		fixture2.setNumOfViolations(new Integer(-1));
	}

	private FileViolationsWritable fixture3;

	{
		fixture3 = new FileViolationsWritable();
		fixture3.setFileName("");
		fixture3.setNumOfViolations(new Integer(0));
	}

	private FileViolationsWritable fixture4;

	{
		fixture4 = new FileViolationsWritable();
		fixture4.setFileName("");
		fixture4.setNumOfViolations(new Integer(1));
	}

	private FileViolationsWritable fixture5;

	{
		fixture5 = new FileViolationsWritable();
		fixture5.setFileName("0123456789");
		fixture5.setNumOfViolations(new Integer(-1));
	}

	private FileViolationsWritable fixture6;

	{
		fixture6 = new FileViolationsWritable();
		fixture6.setFileName("0123456789");
		fixture6.setNumOfViolations(new Integer(0));
	}

	private FileViolationsWritable fixture7;

	{
		fixture7 = new FileViolationsWritable();
		fixture7.setFileName("0123456789");
		fixture7.setNumOfViolations(new Integer(1));
	}

	private FileViolationsWritable fixture8;

	{
		fixture8 = new FileViolationsWritable();
		fixture8.setFileName("An��t-1.0.txt");
		fixture8.setNumOfViolations(new Integer(-1));
	}

	private FileViolationsWritable fixture9;

	{
		fixture9 = new FileViolationsWritable();
		fixture9.setFileName("An��t-1.0.txt");
		fixture9.setNumOfViolations(new Integer(0));
	}

	private FileViolationsWritable fixture10;

	{
		fixture10 = new FileViolationsWritable();
		fixture10.setFileName("An��t-1.0.txt");
		fixture10.setNumOfViolations(new Integer(1));
	}

	public FileViolationsWritable getFixture1()
		throws Exception {
		return fixture1;
	}

	public FileViolationsWritable getFixture2()
		throws Exception {
		if (fixture2 == null) {
			fixture2 = new FileViolationsWritable();
			fixture2.setFileName("");
			fixture2.setNumOfViolations(new Integer(-1));
		}
		return fixture2;
	}

	public FileViolationsWritable getFixture3()
		throws Exception {
		if (fixture3 == null) {
			fixture3 = new FileViolationsWritable();
			fixture3.setFileName("");
			fixture3.setNumOfViolations(new Integer(0));
		}
		return fixture3;
	}

	public FileViolationsWritable getFixture4()
		throws Exception {
		if (fixture4 == null) {
			fixture4 = new FileViolationsWritable();
			fixture4.setFileName("");
			fixture4.setNumOfViolations(new Integer(1));
		}
		return fixture4;
	}

	public FileViolationsWritable getFixture5()
		throws Exception {
		if (fixture5 == null) {
			fixture5 = new FileViolationsWritable();
			fixture5.setFileName("0123456789");
			fixture5.setNumOfViolations(new Integer(-1));
		}
		return fixture5;
	}

	public FileViolationsWritable getFixture6()
		throws Exception {
		if (fixture6 == null) {
			fixture6 = new FileViolationsWritable();
			fixture6.setFileName("0123456789");
			fixture6.setNumOfViolations(new Integer(0));
		}
		return fixture6;
	}

	public FileViolationsWritable getFixture7()
		throws Exception {
		if (fixture7 == null) {
			fixture7 = new FileViolationsWritable();
			fixture7.setFileName("0123456789");
			fixture7.setNumOfViolations(new Integer(1));
		}
		return fixture7;
	}

	public FileViolationsWritable getFixture8()
		throws Exception {
		if (fixture8 == null) {
			fixture8 = new FileViolationsWritable();
			fixture8.setFileName("An��t-1.0.txt");
			fixture8.setNumOfViolations(new Integer(-1));
		}
		return fixture8;
	}

	public FileViolationsWritable getFixture9()
		throws Exception {
		if (fixture9 == null) {
			fixture9 = new FileViolationsWritable();
			fixture9.setFileName("An��t-1.0.txt");
			fixture9.setNumOfViolations(new Integer(0));
		}
		return fixture9;
	}

	public FileViolationsWritable getFixture10()
		throws Exception {
		if (fixture10 == null) {
			fixture10 = new FileViolationsWritable();
			fixture10.setFileName("An��t-1.0.txt");
			fixture10.setNumOfViolations(new Integer(1));
		}
		return fixture10;
	}

	@Test
	public void testFileViolationsWritable_1()
		throws Exception {

		FileViolationsWritable result = new FileViolationsWritable();

		assertNotNull(result);
		assertEquals("{\"numOfViolations\":null,\"fileName\":null}", result.toString());
		assertEquals(null, result.getFileName());
		assertEquals(null, result.getNumOfViolations());
	}

	@Test
	public void testFileViolationsWritable_2()
		throws Exception {
		FileViolationsWritable fileViolationsWritable = new FileViolationsWritable();

		FileViolationsWritable result = new FileViolationsWritable(fileViolationsWritable);

		assertNotNull(result);
		assertEquals("{\"numOfViolations\":null,\"fileName\":null}", result.toString());
		assertEquals(null, result.getFileName());
		assertEquals(null, result.getNumOfViolations());
	}


	

	@Test
	public void testCompareTo_fixture2_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture2();
		FileViolationsWritable arg0 = new FileViolationsWritable();

		int result = fixture.compareTo(arg0);

		assertEquals(-1, result);
	}

	@Test
	public void testCompareTo_fixture3_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture3();
		FileViolationsWritable arg0 = new FileViolationsWritable();

		int result = fixture.compareTo(arg0);

		assertEquals(-1, result);
	}

	@Test
	public void testCompareTo_fixture4_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture4();
		FileViolationsWritable arg0 = new FileViolationsWritable();

		int result = fixture.compareTo(arg0);

		assertEquals(-1, result);
	}

	@Test
	public void testCompareTo_fixture5_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture5();
		FileViolationsWritable arg0 = new FileViolationsWritable();

		int result = fixture.compareTo(arg0);

		assertEquals(-1, result);
	}

	@Test
	public void testCompareTo_fixture6_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture6();
		FileViolationsWritable arg0 = new FileViolationsWritable();

		int result = fixture.compareTo(arg0);

		assertEquals(-1, result);
	}

	@Test
	public void testCompareTo_fixture7_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture7();
		FileViolationsWritable arg0 = new FileViolationsWritable();

		int result = fixture.compareTo(arg0);

		assertEquals(-1, result);
	}

	@Test
	public void testCompareTo_fixture8_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture8();
		FileViolationsWritable arg0 = new FileViolationsWritable();

		int result = fixture.compareTo(arg0);

		assertEquals(-1, result);
	}

	@Test
	public void testCompareTo_fixture9_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture9();
		FileViolationsWritable arg0 = new FileViolationsWritable();

		int result = fixture.compareTo(arg0);

		assertEquals(-1, result);
	}

	@Test
	public void testCompareTo_fixture10_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture10();
		FileViolationsWritable arg0 = new FileViolationsWritable();

		int result = fixture.compareTo(arg0);

		assertEquals(-1, result);
	}


	@Test
	public void testGetFileName_fixture1_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture1();

		String result = fixture.getFileName();

		assertEquals(null, result);
	}

	@Test
	public void testGetFileName_fixture2_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture2();

		String result = fixture.getFileName();

		assertEquals("", result);
	}

	@Test
	public void testGetFileName_fixture3_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture3();

		String result = fixture.getFileName();

		assertEquals("", result);
	}

	@Test
	public void testGetFileName_fixture4_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture4();

		String result = fixture.getFileName();

		assertEquals("", result);
	}

	@Test
	public void testGetFileName_fixture5_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture5();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture6_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture6();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture7_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture7();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture8_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture8();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture9_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture9();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture10_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture10();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetNumOfViolations_fixture1_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture1();

		Integer result = fixture.getNumOfViolations();

		assertEquals(null, result);
	}

	@Test
	public void testGetNumOfViolations_fixture2_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture2();

		Integer result = fixture.getNumOfViolations();

		assertNotNull(result);
		assertEquals("-1", result.toString());
		assertEquals((byte) -1, result.byteValue());
		assertEquals((short) -1, result.shortValue());
		assertEquals(-1, result.intValue());
		assertEquals(-1L, result.longValue());
		assertEquals(-1.0f, result.floatValue(), 1.0f);
		assertEquals(-1.0, result.doubleValue(), 1.0);
	}

	@Test
	public void testGetNumOfViolations_fixture3_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture3();

		Integer result = fixture.getNumOfViolations();

		assertNotNull(result);
		assertEquals("0", result.toString());
		assertEquals((byte) 0, result.byteValue());
		assertEquals((short) 0, result.shortValue());
		assertEquals(0, result.intValue());
		assertEquals(0L, result.longValue());
		assertEquals(0.0f, result.floatValue(), 1.0f);
		assertEquals(0.0, result.doubleValue(), 1.0);
	}

	@Test
	public void testGetNumOfViolations_fixture4_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture4();

		Integer result = fixture.getNumOfViolations();

		assertNotNull(result);
		assertEquals("1", result.toString());
		assertEquals((byte) 1, result.byteValue());
		assertEquals((short) 1, result.shortValue());
		assertEquals(1, result.intValue());
		assertEquals(1L, result.longValue());
		assertEquals(1.0f, result.floatValue(), 1.0f);
		assertEquals(1.0, result.doubleValue(), 1.0);
	}

	@Test
	public void testGetNumOfViolations_fixture5_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture5();

		Integer result = fixture.getNumOfViolations();

		assertNotNull(result);
		assertEquals("-1", result.toString());
		assertEquals((byte) -1, result.byteValue());
		assertEquals((short) -1, result.shortValue());
		assertEquals(-1, result.intValue());
		assertEquals(-1L, result.longValue());
		assertEquals(-1.0f, result.floatValue(), 1.0f);
		assertEquals(-1.0, result.doubleValue(), 1.0);
	}

	@Test
	public void testGetNumOfViolations_fixture6_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture6();

		Integer result = fixture.getNumOfViolations();

		assertNotNull(result);
		assertEquals("0", result.toString());
		assertEquals((byte) 0, result.byteValue());
		assertEquals((short) 0, result.shortValue());
		assertEquals(0, result.intValue());
		assertEquals(0L, result.longValue());
		assertEquals(0.0f, result.floatValue(), 1.0f);
		assertEquals(0.0, result.doubleValue(), 1.0);
	}

	@Test
	public void testGetNumOfViolations_fixture7_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture7();

		Integer result = fixture.getNumOfViolations();

		assertNotNull(result);
		assertEquals("1", result.toString());
		assertEquals((byte) 1, result.byteValue());
		assertEquals((short) 1, result.shortValue());
		assertEquals(1, result.intValue());
		assertEquals(1L, result.longValue());
		assertEquals(1.0f, result.floatValue(), 1.0f);
		assertEquals(1.0, result.doubleValue(), 1.0);
	}

	@Test
	public void testGetNumOfViolations_fixture8_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture8();

		Integer result = fixture.getNumOfViolations();

		assertNotNull(result);
		assertEquals("-1", result.toString());
		assertEquals((byte) -1, result.byteValue());
		assertEquals((short) -1, result.shortValue());
		assertEquals(-1, result.intValue());
		assertEquals(-1L, result.longValue());
		assertEquals(-1.0f, result.floatValue(), 1.0f);
		assertEquals(-1.0, result.doubleValue(), 1.0);
	}

	@Test
	public void testGetNumOfViolations_fixture9_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture9();

		Integer result = fixture.getNumOfViolations();

		assertNotNull(result);
		assertEquals("0", result.toString());
		assertEquals((byte) 0, result.byteValue());
		assertEquals((short) 0, result.shortValue());
		assertEquals(0, result.intValue());
		assertEquals(0L, result.longValue());
		assertEquals(0.0f, result.floatValue(), 1.0f);
		assertEquals(0.0, result.doubleValue(), 1.0);
	}

	@Test
	public void testGetNumOfViolations_fixture10_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture10();

		Integer result = fixture.getNumOfViolations();

		assertNotNull(result);
		assertEquals("1", result.toString());
		assertEquals((byte) 1, result.byteValue());
		assertEquals((short) 1, result.shortValue());
		assertEquals(1, result.intValue());
		assertEquals(1L, result.longValue());
		assertEquals(1.0f, result.floatValue(), 1.0f);
		assertEquals(1.0, result.doubleValue(), 1.0);
	}


	@Test(expected = java.io.EOFException.class)
	public void testReadFields_fixture2_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture2();
		DataInput in = new DataInputStream(new ByteArrayInputStream("".getBytes()));

		fixture.readFields(in);

	}

	@Test(expected = java.io.EOFException.class)
	public void testReadFields_fixture3_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture3();
		DataInput in = new DataInputStream(new ByteArrayInputStream("".getBytes()));

		fixture.readFields(in);

	}

	@Test(expected = java.io.EOFException.class)
	public void testReadFields_fixture4_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture4();
		DataInput in = new DataInputStream(new ByteArrayInputStream("".getBytes()));

		fixture.readFields(in);

	}

	@Test(expected = java.io.EOFException.class)
	public void testReadFields_fixture5_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture5();
		DataInput in = new DataInputStream(new ByteArrayInputStream("".getBytes()));

		fixture.readFields(in);

	}

	@Test(expected = java.io.EOFException.class)
	public void testReadFields_fixture6_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture6();
		DataInput in = new DataInputStream(new ByteArrayInputStream("".getBytes()));

		fixture.readFields(in);

	}

	@Test(expected = java.io.EOFException.class)
	public void testReadFields_fixture7_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture7();
		DataInput in = new DataInputStream(new ByteArrayInputStream("".getBytes()));

		fixture.readFields(in);

	}

	@Test(expected = java.io.EOFException.class)
	public void testReadFields_fixture8_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture8();
		DataInput in = new DataInputStream(new ByteArrayInputStream("".getBytes()));

		fixture.readFields(in);

	}

	@Test(expected = java.io.EOFException.class)
	public void testReadFields_fixture9_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture9();
		DataInput in = new DataInputStream(new ByteArrayInputStream("".getBytes()));

		fixture.readFields(in);

	}

	@Test(expected = java.io.EOFException.class)
	public void testReadFields_fixture10_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture10();
		DataInput in = new DataInputStream(new ByteArrayInputStream("".getBytes()));

		fixture.readFields(in);

	}

	@Test(expected = java.io.EOFException.class)
	public void testReadFields_fixture1_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture1();
		DataInput in = new DataInputStream(new ByteArrayInputStream("".getBytes()));

		fixture.readFields(in);

	}

	@Test
	public void testSetFileName_fixture1_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture1();
		String fileName = "";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture2_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture2();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture3_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture3();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture4_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture4();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture5_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture5();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture6_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture6();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture7_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture7();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture8_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture8();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture9_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture9();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture10_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture10();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture2_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture2();
		String fileName = "";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture3_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture3();
		String fileName = "";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture4_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture4();
		String fileName = "";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture5_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture5();
		String fileName = "";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture6_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture6();
		String fileName = "";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture7_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture7();
		String fileName = "";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture8_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture8();
		String fileName = "";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture9_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture9();
		String fileName = "";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture10_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture10();
		String fileName = "";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture1_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture1();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetNumOfViolations_fixture1_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture1();
		Integer numOfViolations = new Integer(-1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture2_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture2();
		Integer numOfViolations = new Integer(0);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture3_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture3();
		Integer numOfViolations = new Integer(1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture4_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture4();
		Integer numOfViolations = new Integer(1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture5_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture5();
		Integer numOfViolations = new Integer(1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture6_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture6();
		Integer numOfViolations = new Integer(1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture7_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture7();
		Integer numOfViolations = new Integer(1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture8_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture8();
		Integer numOfViolations = new Integer(1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture9_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture9();
		Integer numOfViolations = new Integer(1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture10_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture10();
		Integer numOfViolations = new Integer(1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture2_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture2();
		Integer numOfViolations = new Integer(-1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture3_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture3();
		Integer numOfViolations = new Integer(0);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture4_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture4();
		Integer numOfViolations = new Integer(0);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture5_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture5();
		Integer numOfViolations = new Integer(0);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture6_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture6();
		Integer numOfViolations = new Integer(0);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture7_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture7();
		Integer numOfViolations = new Integer(0);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture8_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture8();
		Integer numOfViolations = new Integer(0);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture9_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture9();
		Integer numOfViolations = new Integer(0);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture10_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture10();
		Integer numOfViolations = new Integer(0);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture1_2()
		throws Exception {
		FileViolationsWritable fixture = getFixture1();
		Integer numOfViolations = new Integer(1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture3_3()
		throws Exception {
		FileViolationsWritable fixture = getFixture3();
		Integer numOfViolations = new Integer(-1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture4_3()
		throws Exception {
		FileViolationsWritable fixture = getFixture4();
		Integer numOfViolations = new Integer(-1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture5_3()
		throws Exception {
		FileViolationsWritable fixture = getFixture5();
		Integer numOfViolations = new Integer(-1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture6_3()
		throws Exception {
		FileViolationsWritable fixture = getFixture6();
		Integer numOfViolations = new Integer(-1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture7_3()
		throws Exception {
		FileViolationsWritable fixture = getFixture7();
		Integer numOfViolations = new Integer(-1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture8_3()
		throws Exception {
		FileViolationsWritable fixture = getFixture8();
		Integer numOfViolations = new Integer(-1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture9_3()
		throws Exception {
		FileViolationsWritable fixture = getFixture9();
		Integer numOfViolations = new Integer(-1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture10_3()
		throws Exception {
		FileViolationsWritable fixture = getFixture10();
		Integer numOfViolations = new Integer(-1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture1_3()
		throws Exception {
		FileViolationsWritable fixture = getFixture1();
		Integer numOfViolations = new Integer(0);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testSetNumOfViolations_fixture2_3()
		throws Exception {
		FileViolationsWritable fixture = getFixture2();
		Integer numOfViolations = new Integer(1);

		fixture.setNumOfViolations(numOfViolations);

	}

	@Test
	public void testToString_fixture1_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture1();

		String result = fixture.toString();

		assertEquals("{\"numOfViolations\":null,\"fileName\":null}", result);
	}

	@Test
	public void testToString_fixture2_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture2();

		String result = fixture.toString();

		assertEquals("{\"numOfViolations\":-1,\"fileName\":}", result);
	}

	@Test
	public void testToString_fixture3_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture3();

		String result = fixture.toString();

		assertEquals("{\"numOfViolations\":0,\"fileName\":}", result);
	}

	@Test
	public void testToString_fixture4_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture4();

		String result = fixture.toString();

		assertEquals("{\"numOfViolations\":1,\"fileName\":}", result);
	}

	@Test
	public void testToString_fixture5_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture5();

		String result = fixture.toString();

		assertEquals("{\"numOfViolations\":-1,\"fileName\":0123456789}", result);
	}

	@Test
	public void testToString_fixture6_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture6();

		String result = fixture.toString();

		assertEquals("{\"numOfViolations\":0,\"fileName\":0123456789}", result);
	}

	@Test
	public void testToString_fixture7_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture7();

		String result = fixture.toString();

		assertEquals("{\"numOfViolations\":1,\"fileName\":0123456789}", result);
	}

	@Test
	public void testToString_fixture8_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture8();

		String result = fixture.toString();

		assertEquals("{\"numOfViolations\":-1,\"fileName\":An��t-1.0.txt}", result);
	}

	@Test
	public void testToString_fixture9_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture9();

		String result = fixture.toString();

		assertEquals("{\"numOfViolations\":0,\"fileName\":An��t-1.0.txt}", result);
	}

	@Test
	public void testToString_fixture10_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture10();

		String result = fixture.toString();

		assertEquals("{\"numOfViolations\":1,\"fileName\":An��t-1.0.txt}", result);
	}

	

	@Test
	public void testWrite_fixture2_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture2();
		DataOutput out = new DataOutputStream(new ByteArrayOutputStream());

		fixture.write(out);

	}

	@Test
	public void testWrite_fixture3_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture3();
		DataOutput out = new DataOutputStream(new ByteArrayOutputStream());

		fixture.write(out);

	}

	@Test
	public void testWrite_fixture4_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture4();
		DataOutput out = new DataOutputStream(new ByteArrayOutputStream());

		fixture.write(out);

	}

	@Test
	public void testWrite_fixture5_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture5();
		DataOutput out = new DataOutputStream(new ByteArrayOutputStream());

		fixture.write(out);

	}

	@Test
	public void testWrite_fixture6_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture6();
		DataOutput out = new DataOutputStream(new ByteArrayOutputStream());

		fixture.write(out);

	}

	@Test
	public void testWrite_fixture7_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture7();
		DataOutput out = new DataOutputStream(new ByteArrayOutputStream());

		fixture.write(out);

	}

	@Test
	public void testWrite_fixture8_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture8();
		DataOutput out = new DataOutputStream(new ByteArrayOutputStream());

		fixture.write(out);

	}

	@Test
	public void testWrite_fixture9_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture9();
		DataOutput out = new DataOutputStream(new ByteArrayOutputStream());

		fixture.write(out);

	}

	@Test
	public void testWrite_fixture10_1()
		throws Exception {
		FileViolationsWritable fixture = getFixture10();
		DataOutput out = new DataOutputStream(new ByteArrayOutputStream());

		fixture.write(out);

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