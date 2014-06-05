package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import org.apache.hadoop.io.MapWritable;
import org.easymock.EasyMock;
import org.jumbune.datavalidation.DataViolationArrayWritable;
import org.jumbune.datavalidation.DataViolationWritable;
import org.jumbune.datavalidation.DataViolationWritableBean;
import org.junit.*;
import static org.junit.Assert.*;

public class DataViolationWritableTest {
	private DataViolationWritable fixture1 ;


	private DataViolationWritable fixture2;

	{
		fixture2 = new DataViolationWritable();
		fixture2.setDataViolationArrayWritable(new DataViolationArrayWritable());
		fixture2.setFieldMap(new MapWritable());
		fixture2.setTotalViolations(0);
	}

	private DataViolationWritable fixture3;

	{
		fixture3 = new DataViolationWritable();
		fixture3.setDataViolationArrayWritable(new DataViolationArrayWritable());
		fixture3.setFieldMap(new MapWritable());
		fixture3.setTotalViolations(1);
	}

	private DataViolationWritable fixture4;

	{
		fixture4 = new DataViolationWritable();
		fixture4.setDataViolationArrayWritable(new DataViolationArrayWritable());
		fixture4.setFieldMap(new MapWritable());
		fixture4.setTotalViolations(7);
	}

	public DataViolationWritable getFixture1()
		throws Exception {
		return fixture1;
	}

	public DataViolationWritable getFixture2()
		throws Exception {
		if (fixture2 == null) {
			fixture2 = new DataViolationWritable();
			fixture2.setDataViolationArrayWritable(new DataViolationArrayWritable());
			fixture2.setFieldMap(new MapWritable());
			fixture2.setTotalViolations(0);
		}
		return fixture2;
	}

	public DataViolationWritable getFixture3()
		throws Exception {
		if (fixture3 == null) {
			fixture3 = new DataViolationWritable();
			fixture3.setDataViolationArrayWritable(new DataViolationArrayWritable());
			fixture3.setFieldMap(new MapWritable());
			fixture3.setTotalViolations(1);
		}
		return fixture3;
	}

	public DataViolationWritable getFixture4()
		throws Exception {
		if (fixture4 == null) {
			fixture4 = new DataViolationWritable();
			fixture4.setDataViolationArrayWritable(new DataViolationArrayWritable());
			fixture4.setFieldMap(new MapWritable());
			fixture4.setTotalViolations(7);
		}
		return fixture4;
	}

	@Test
	public void testDataViolationWritable_1()
		throws Exception {

		DataViolationWritable result = new DataViolationWritable();

		assertNotNull(result);
		assertEquals(0, result.getTotalViolations());
	}

	@Test
	public void testCompareTo_fixture1_1()
		throws Exception {
		DataViolationWritable fixture = getFixture1();
		DataViolationWritableBean arg0 = new DataViolationWritableBean();

		int result = fixture.compareTo(arg0);

		assertEquals(0, result);
	}


	

	@Test
	public void testCompareTo_fixture2_2()
		throws Exception {
		DataViolationWritable fixture = getFixture2();
		DataViolationWritableBean arg0 = new DataViolationWritableBean();

		int result = fixture.compareTo(arg0);

		assertEquals(0, result);
	}

	@Test
	public void testCompareTo_fixture3_2()
		throws Exception {
		DataViolationWritable fixture = getFixture3();
		DataViolationWritableBean arg0 = new DataViolationWritableBean();

		int result = fixture.compareTo(arg0);

		assertEquals(0, result);
	}

	@Test
	public void testCompareTo_fixture4_2()
		throws Exception {
		DataViolationWritable fixture = getFixture4();
		DataViolationWritableBean arg0 = new DataViolationWritableBean();

		int result = fixture.compareTo(arg0);

		assertEquals(0, result);
	}

	@Test
	public void testGetDataViolationArrayWritable_fixture1_1()
		throws Exception {
		DataViolationWritable fixture = getFixture1();

		DataViolationArrayWritable result = fixture.getDataViolationArrayWritable();

		assertNotNull(result);
		assertEquals(null, result.get());
	}

	@Test
	public void testGetDataViolationArrayWritable_fixture2_1()
		throws Exception {
		DataViolationWritable fixture = getFixture2();

		DataViolationArrayWritable result = fixture.getDataViolationArrayWritable();

		assertNotNull(result);
		assertEquals(null, result.get());
	}

	@Test
	public void testGetDataViolationArrayWritable_fixture3_1()
		throws Exception {
		DataViolationWritable fixture = getFixture3();

		DataViolationArrayWritable result = fixture.getDataViolationArrayWritable();

		assertNotNull(result);
		assertEquals(null, result.get());
	}

	@Test
	public void testGetDataViolationArrayWritable_fixture4_1()
		throws Exception {
		DataViolationWritable fixture = getFixture4();

		DataViolationArrayWritable result = fixture.getDataViolationArrayWritable();

		assertNotNull(result);
		assertEquals(null, result.get());
	}

	@Test
	public void testGetFieldMap_fixture1_1()
		throws Exception {
		DataViolationWritable fixture = getFixture1();

		MapWritable result = fixture.getFieldMap();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetFieldMap_fixture2_1()
		throws Exception {
		DataViolationWritable fixture = getFixture2();

		MapWritable result = fixture.getFieldMap();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetFieldMap_fixture3_1()
		throws Exception {
		DataViolationWritable fixture = getFixture3();

		MapWritable result = fixture.getFieldMap();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetFieldMap_fixture4_1()
		throws Exception {
		DataViolationWritable fixture = getFixture4();

		MapWritable result = fixture.getFieldMap();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetTotalViolations_fixture1_1()
		throws Exception {
		DataViolationWritable fixture = getFixture1();

		int result = fixture.getTotalViolations();

		assertEquals(0, result);
	}

	@Test
	public void testGetTotalViolations_fixture2_1()
		throws Exception {
		DataViolationWritable fixture = getFixture2();

		int result = fixture.getTotalViolations();

		assertEquals(0, result);
	}

	@Test
	public void testGetTotalViolations_fixture3_1()
		throws Exception {
		DataViolationWritable fixture = getFixture3();

		int result = fixture.getTotalViolations();

		assertEquals(1, result);
	}

	@Test
	public void testGetTotalViolations_fixture4_1()
		throws Exception {
		DataViolationWritable fixture = getFixture4();

		int result = fixture.getTotalViolations();

		assertEquals(7, result);
	}





	
	@Test
	public void testSetDataViolationArrayWritable_fixture1_1()
		throws Exception {
		DataViolationWritable fixture = getFixture1();
		DataViolationArrayWritable dataViolationArrayWritable = new DataViolationArrayWritable();

		fixture.setDataViolationArrayWritable(dataViolationArrayWritable);

	}

	@Test
	public void testSetDataViolationArrayWritable_fixture2_1()
		throws Exception {
		DataViolationWritable fixture = getFixture2();
		DataViolationArrayWritable dataViolationArrayWritable = new DataViolationArrayWritable();

		fixture.setDataViolationArrayWritable(dataViolationArrayWritable);

	}

	@Test
	public void testSetDataViolationArrayWritable_fixture3_1()
		throws Exception {
		DataViolationWritable fixture = getFixture3();
		DataViolationArrayWritable dataViolationArrayWritable = new DataViolationArrayWritable();

		fixture.setDataViolationArrayWritable(dataViolationArrayWritable);

	}

	@Test
	public void testSetDataViolationArrayWritable_fixture4_1()
		throws Exception {
		DataViolationWritable fixture = getFixture4();
		DataViolationArrayWritable dataViolationArrayWritable = new DataViolationArrayWritable();

		fixture.setDataViolationArrayWritable(dataViolationArrayWritable);

	}

	@Test
	public void testSetFieldMap_fixture1_1()
		throws Exception {
		DataViolationWritable fixture = getFixture1();
		MapWritable fieldMap = new MapWritable();

		fixture.setFieldMap(fieldMap);

	}

	
	@Test
	public void testSetFieldMap_fixture2_2()
		throws Exception {
		DataViolationWritable fixture = getFixture2();
		MapWritable fieldMap = new MapWritable();

		fixture.setFieldMap(fieldMap);

	}

	@Test
	public void testSetFieldMap_fixture3_2()
		throws Exception {
		DataViolationWritable fixture = getFixture3();
		MapWritable fieldMap = new MapWritable();

		fixture.setFieldMap(fieldMap);

	}

	@Test
	public void testSetFieldMap_fixture4_2()
		throws Exception {
		DataViolationWritable fixture = getFixture4();
		MapWritable fieldMap = new MapWritable();

		fixture.setFieldMap(fieldMap);

	}



	@Test
	public void testSetTotalViolations_fixture1_1()
		throws Exception {
		DataViolationWritable fixture = getFixture1();
		int totalViolations = 0;

		fixture.setTotalViolations(totalViolations);

	}

	@Test
	public void testSetTotalViolations_fixture2_1()
		throws Exception {
		DataViolationWritable fixture = getFixture2();
		int totalViolations = 1;

		fixture.setTotalViolations(totalViolations);

	}

	@Test
	public void testSetTotalViolations_fixture3_1()
		throws Exception {
		DataViolationWritable fixture = getFixture3();
		int totalViolations = 7;

		fixture.setTotalViolations(totalViolations);

	}

	@Test
	public void testSetTotalViolations_fixture4_1()
		throws Exception {
		DataViolationWritable fixture = getFixture4();
		int totalViolations = 7;

		fixture.setTotalViolations(totalViolations);

	}

	@Test
	public void testSetTotalViolations_fixture2_2()
		throws Exception {
		DataViolationWritable fixture = getFixture2();
		int totalViolations = 0;

		fixture.setTotalViolations(totalViolations);

	}

	@Test
	public void testSetTotalViolations_fixture3_2()
		throws Exception {
		DataViolationWritable fixture = getFixture3();
		int totalViolations = 1;

		fixture.setTotalViolations(totalViolations);

	}

	@Test
	public void testSetTotalViolations_fixture4_2()
		throws Exception {
		DataViolationWritable fixture = getFixture4();
		int totalViolations = 1;

		fixture.setTotalViolations(totalViolations);

	}

	@Test
	public void testSetTotalViolations_fixture1_2()
		throws Exception {
		DataViolationWritable fixture = getFixture1();
		int totalViolations = 7;

		fixture.setTotalViolations(totalViolations);

	}

	@Test
	public void testSetTotalViolations_fixture3_3()
		throws Exception {
		DataViolationWritable fixture = getFixture3();
		int totalViolations = 0;

		fixture.setTotalViolations(totalViolations);

	}

	@Test
	public void testSetTotalViolations_fixture4_3()
		throws Exception {
		DataViolationWritable fixture = getFixture4();
		int totalViolations = 0;

		fixture.setTotalViolations(totalViolations);

	}

	@Test
	public void testSetTotalViolations_fixture1_3()
		throws Exception {
		DataViolationWritable fixture = getFixture1();
		int totalViolations = 1;

		fixture.setTotalViolations(totalViolations);

	}

	@Test
	public void testSetTotalViolations_fixture2_3()
		throws Exception {
		DataViolationWritable fixture = getFixture2();
		int totalViolations = 7;

		fixture.setTotalViolations(totalViolations);

	}


	@Before
	public void setUp()
		throws Exception {
		fixture1 = new DataViolationWritable();
	}

	@After
	public void tearDown()
		throws Exception {
	}
}