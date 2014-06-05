package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataOutput;
import org.easymock.EasyMock;
import org.jumbune.datavalidation.DataViolationWritableBean;
import org.junit.*;
import static org.junit.Assert.*;

public class DataViolationWritableBeanTest {
	private DataViolationWritableBean fixture1;


	private DataViolationWritableBean fixture2;

	{
		fixture2 = new DataViolationWritableBean();
		fixture2.setActualValue("");
		fixture2.setExpectedValue("");
		fixture2.setFieldNumber(new Integer(-1));
		fixture2.setFileName("");
		fixture2.setLineNumber(new Integer(-1));
	}

	private DataViolationWritableBean fixture3;

	{
		fixture3 = new DataViolationWritableBean();
		fixture3.setActualValue("");
		fixture3.setExpectedValue("0123456789");
		fixture3.setFieldNumber(new Integer(-1));
		fixture3.setFileName("0123456789");
		fixture3.setLineNumber(new Integer(-1));
	}

	private DataViolationWritableBean fixture4;

	{
		fixture4 = new DataViolationWritableBean();
		fixture4.setActualValue("");
		fixture4.setExpectedValue("0123456789");
		fixture4.setFieldNumber(new Integer(-1));
		fixture4.setFileName("0123456789");
		fixture4.setLineNumber(new Integer(0));
	}

	private DataViolationWritableBean fixture5;

	{
		fixture5 = new DataViolationWritableBean();
		fixture5.setActualValue("");
		fixture5.setExpectedValue("0123456789");
		fixture5.setFieldNumber(new Integer(0));
		fixture5.setFileName("0123456789");
		fixture5.setLineNumber(new Integer(-1));
	}

	private DataViolationWritableBean fixture6;

	{
		fixture6 = new DataViolationWritableBean();
		fixture6.setActualValue("");
		fixture6.setExpectedValue("0123456789");
		fixture6.setFieldNumber(new Integer(0));
		fixture6.setFileName("0123456789");
		fixture6.setLineNumber(new Integer(0));
	}

	private DataViolationWritableBean fixture7;

	{
		fixture7 = new DataViolationWritableBean();
		fixture7.setActualValue("");
		fixture7.setExpectedValue("0123456789");
		fixture7.setFieldNumber(new Integer(1));
		fixture7.setFileName("0123456789");
		fixture7.setLineNumber(new Integer(-1));
	}

	private DataViolationWritableBean fixture8;

	{
		fixture8 = new DataViolationWritableBean();
		fixture8.setActualValue("");
		fixture8.setExpectedValue("0123456789");
		fixture8.setFieldNumber(new Integer(1));
		fixture8.setFileName("0123456789");
		fixture8.setLineNumber(new Integer(0));
	}

	private DataViolationWritableBean fixture9;

	{
		fixture9 = new DataViolationWritableBean();
		fixture9.setActualValue("");
		fixture9.setExpectedValue("An��t-1.0.txt");
		fixture9.setFieldNumber(new Integer(-1));
		fixture9.setFileName("An��t-1.0.txt");
		fixture9.setLineNumber(new Integer(1));
	}

	private DataViolationWritableBean fixture10;

	{
		fixture10 = new DataViolationWritableBean();
		fixture10.setActualValue("");
		fixture10.setExpectedValue("An��t-1.0.txt");
		fixture10.setFieldNumber(new Integer(0));
		fixture10.setFileName("An��t-1.0.txt");
		fixture10.setLineNumber(new Integer(1));
	}

	private DataViolationWritableBean fixture11;

	{
		fixture11 = new DataViolationWritableBean();
		fixture11.setActualValue("");
		fixture11.setExpectedValue("An��t-1.0.txt");
		fixture11.setFieldNumber(new Integer(1));
		fixture11.setFileName("An��t-1.0.txt");
		fixture11.setLineNumber(new Integer(1));
	}

	private DataViolationWritableBean fixture12;

	{
		fixture12 = new DataViolationWritableBean();
		fixture12.setActualValue("0123456789");
		fixture12.setExpectedValue("0123456789");
		fixture12.setFieldNumber(new Integer(-1));
		fixture12.setFileName("0123456789");
		fixture12.setLineNumber(new Integer(-1));
	}

	private DataViolationWritableBean fixture13;

	{
		fixture13 = new DataViolationWritableBean();
		fixture13.setActualValue("0123456789");
		fixture13.setExpectedValue("0123456789");
		fixture13.setFieldNumber(new Integer(-1));
		fixture13.setFileName("0123456789");
		fixture13.setLineNumber(new Integer(0));
	}

	private DataViolationWritableBean fixture14;

	{
		fixture14 = new DataViolationWritableBean();
		fixture14.setActualValue("0123456789");
		fixture14.setExpectedValue("0123456789");
		fixture14.setFieldNumber(new Integer(0));
		fixture14.setFileName("0123456789");
		fixture14.setLineNumber(new Integer(-1));
	}

	private DataViolationWritableBean fixture15;

	{
		fixture15 = new DataViolationWritableBean();
		fixture15.setActualValue("0123456789");
		fixture15.setExpectedValue("0123456789");
		fixture15.setFieldNumber(new Integer(0));
		fixture15.setFileName("0123456789");
		fixture15.setLineNumber(new Integer(0));
	}

	private DataViolationWritableBean fixture16;

	{
		fixture16 = new DataViolationWritableBean();
		fixture16.setActualValue("0123456789");
		fixture16.setExpectedValue("0123456789");
		fixture16.setFieldNumber(new Integer(1));
		fixture16.setFileName("0123456789");
		fixture16.setLineNumber(new Integer(-1));
	}

	private DataViolationWritableBean fixture17;

	{
		fixture17 = new DataViolationWritableBean();
		fixture17.setActualValue("0123456789");
		fixture17.setExpectedValue("An��t-1.0.txt");
		fixture17.setFieldNumber(new Integer(-1));
		fixture17.setFileName("An��t-1.0.txt");
		fixture17.setLineNumber(new Integer(1));
	}

	private DataViolationWritableBean fixture18;

	{
		fixture18 = new DataViolationWritableBean();
		fixture18.setActualValue("0123456789");
		fixture18.setExpectedValue("An��t-1.0.txt");
		fixture18.setFieldNumber(new Integer(0));
		fixture18.setFileName("An��t-1.0.txt");
		fixture18.setLineNumber(new Integer(0));
	}

	private DataViolationWritableBean fixture19;

	{
		fixture19 = new DataViolationWritableBean();
		fixture19.setActualValue("0123456789");
		fixture19.setExpectedValue("An��t-1.0.txt");
		fixture19.setFieldNumber(new Integer(0));
		fixture19.setFileName("An��t-1.0.txt");
		fixture19.setLineNumber(new Integer(1));
	}

	private DataViolationWritableBean fixture20;

	{
		fixture20 = new DataViolationWritableBean();
		fixture20.setActualValue("0123456789");
		fixture20.setExpectedValue("An��t-1.0.txt");
		fixture20.setFieldNumber(new Integer(1));
		fixture20.setFileName("An��t-1.0.txt");
		fixture20.setLineNumber(new Integer(0));
	}

	private DataViolationWritableBean fixture21;

	{
		fixture21 = new DataViolationWritableBean();
		fixture21.setActualValue("0123456789");
		fixture21.setExpectedValue("An��t-1.0.txt");
		fixture21.setFieldNumber(new Integer(1));
		fixture21.setFileName("An��t-1.0.txt");
		fixture21.setLineNumber(new Integer(1));
	}

	private DataViolationWritableBean fixture22;

	{
		fixture22 = new DataViolationWritableBean();
		fixture22.setActualValue("An��t-1.0.txt");
		fixture22.setExpectedValue("0123456789");
		fixture22.setFieldNumber(new Integer(-1));
		fixture22.setFileName("0123456789");
		fixture22.setLineNumber(new Integer(-1));
	}

	private DataViolationWritableBean fixture23;

	{
		fixture23 = new DataViolationWritableBean();
		fixture23.setActualValue("An��t-1.0.txt");
		fixture23.setExpectedValue("0123456789");
		fixture23.setFieldNumber(new Integer(0));
		fixture23.setFileName("0123456789");
		fixture23.setLineNumber(new Integer(-1));
	}

	private DataViolationWritableBean fixture24;

	{
		fixture24 = new DataViolationWritableBean();
		fixture24.setActualValue("An��t-1.0.txt");
		fixture24.setExpectedValue("0123456789");
		fixture24.setFieldNumber(new Integer(1));
		fixture24.setFileName("0123456789");
		fixture24.setLineNumber(new Integer(-1));
	}

	private DataViolationWritableBean fixture25;

	{
		fixture25 = new DataViolationWritableBean();
		fixture25.setActualValue("An��t-1.0.txt");
		fixture25.setExpectedValue("An��t-1.0.txt");
		fixture25.setFieldNumber(new Integer(-1));
		fixture25.setFileName("An��t-1.0.txt");
		fixture25.setLineNumber(new Integer(0));
	}

	private DataViolationWritableBean fixture26;

	{
		fixture26 = new DataViolationWritableBean();
		fixture26.setActualValue("An��t-1.0.txt");
		fixture26.setExpectedValue("An��t-1.0.txt");
		fixture26.setFieldNumber(new Integer(-1));
		fixture26.setFileName("An��t-1.0.txt");
		fixture26.setLineNumber(new Integer(1));
	}

	private DataViolationWritableBean fixture27;

	{
		fixture27 = new DataViolationWritableBean();
		fixture27.setActualValue("An��t-1.0.txt");
		fixture27.setExpectedValue("An��t-1.0.txt");
		fixture27.setFieldNumber(new Integer(0));
		fixture27.setFileName("An��t-1.0.txt");
		fixture27.setLineNumber(new Integer(0));
	}

	private DataViolationWritableBean fixture28;

	{
		fixture28 = new DataViolationWritableBean();
		fixture28.setActualValue("An��t-1.0.txt");
		fixture28.setExpectedValue("An��t-1.0.txt");
		fixture28.setFieldNumber(new Integer(0));
		fixture28.setFileName("An��t-1.0.txt");
		fixture28.setLineNumber(new Integer(1));
	}

	private DataViolationWritableBean fixture29;

	{
		fixture29 = new DataViolationWritableBean();
		fixture29.setActualValue("An��t-1.0.txt");
		fixture29.setExpectedValue("An��t-1.0.txt");
		fixture29.setFieldNumber(new Integer(1));
		fixture29.setFileName("An��t-1.0.txt");
		fixture29.setLineNumber(new Integer(0));
	}

	private DataViolationWritableBean fixture30;

	{
		fixture30 = new DataViolationWritableBean();
		fixture30.setActualValue("An��t-1.0.txt");
		fixture30.setExpectedValue("An��t-1.0.txt");
		fixture30.setFieldNumber(new Integer(1));
		fixture30.setFileName("An��t-1.0.txt");
		fixture30.setLineNumber(new Integer(1));
	}

	public DataViolationWritableBean getFixture1()
		throws Exception {
		return fixture1;
	}

	public DataViolationWritableBean getFixture2()
		throws Exception {
		if (fixture2 == null) {
			fixture2 = new DataViolationWritableBean();
			fixture2.setActualValue("");
			fixture2.setExpectedValue("");
			fixture2.setFieldNumber(new Integer(-1));
			fixture2.setFileName("");
			fixture2.setLineNumber(new Integer(-1));
		}
		return fixture2;
	}

	public DataViolationWritableBean getFixture3()
		throws Exception {
		if (fixture3 == null) {
			fixture3 = new DataViolationWritableBean();
			fixture3.setActualValue("");
			fixture3.setExpectedValue("0123456789");
			fixture3.setFieldNumber(new Integer(-1));
			fixture3.setFileName("0123456789");
			fixture3.setLineNumber(new Integer(-1));
		}
		return fixture3;
	}

	public DataViolationWritableBean getFixture4()
		throws Exception {
		if (fixture4 == null) {
			fixture4 = new DataViolationWritableBean();
			fixture4.setActualValue("");
			fixture4.setExpectedValue("0123456789");
			fixture4.setFieldNumber(new Integer(-1));
			fixture4.setFileName("0123456789");
			fixture4.setLineNumber(new Integer(0));
		}
		return fixture4;
	}

	public DataViolationWritableBean getFixture5()
		throws Exception {
		if (fixture5 == null) {
			fixture5 = new DataViolationWritableBean();
			fixture5.setActualValue("");
			fixture5.setExpectedValue("0123456789");
			fixture5.setFieldNumber(new Integer(0));
			fixture5.setFileName("0123456789");
			fixture5.setLineNumber(new Integer(-1));
		}
		return fixture5;
	}

	public DataViolationWritableBean getFixture6()
		throws Exception {
		if (fixture6 == null) {
			fixture6 = new DataViolationWritableBean();
			fixture6.setActualValue("");
			fixture6.setExpectedValue("0123456789");
			fixture6.setFieldNumber(new Integer(0));
			fixture6.setFileName("0123456789");
			fixture6.setLineNumber(new Integer(0));
		}
		return fixture6;
	}

	public DataViolationWritableBean getFixture7()
		throws Exception {
		if (fixture7 == null) {
			fixture7 = new DataViolationWritableBean();
			fixture7.setActualValue("");
			fixture7.setExpectedValue("0123456789");
			fixture7.setFieldNumber(new Integer(1));
			fixture7.setFileName("0123456789");
			fixture7.setLineNumber(new Integer(-1));
		}
		return fixture7;
	}

	public DataViolationWritableBean getFixture8()
		throws Exception {
		if (fixture8 == null) {
			fixture8 = new DataViolationWritableBean();
			fixture8.setActualValue("");
			fixture8.setExpectedValue("0123456789");
			fixture8.setFieldNumber(new Integer(1));
			fixture8.setFileName("0123456789");
			fixture8.setLineNumber(new Integer(0));
		}
		return fixture8;
	}

	public DataViolationWritableBean getFixture9()
		throws Exception {
		if (fixture9 == null) {
			fixture9 = new DataViolationWritableBean();
			fixture9.setActualValue("");
			fixture9.setExpectedValue("An��t-1.0.txt");
			fixture9.setFieldNumber(new Integer(-1));
			fixture9.setFileName("An��t-1.0.txt");
			fixture9.setLineNumber(new Integer(1));
		}
		return fixture9;
	}

	public DataViolationWritableBean getFixture10()
		throws Exception {
		if (fixture10 == null) {
			fixture10 = new DataViolationWritableBean();
			fixture10.setActualValue("");
			fixture10.setExpectedValue("An��t-1.0.txt");
			fixture10.setFieldNumber(new Integer(0));
			fixture10.setFileName("An��t-1.0.txt");
			fixture10.setLineNumber(new Integer(1));
		}
		return fixture10;
	}

	public DataViolationWritableBean getFixture11()
		throws Exception {
		if (fixture11 == null) {
			fixture11 = new DataViolationWritableBean();
			fixture11.setActualValue("");
			fixture11.setExpectedValue("An��t-1.0.txt");
			fixture11.setFieldNumber(new Integer(1));
			fixture11.setFileName("An��t-1.0.txt");
			fixture11.setLineNumber(new Integer(1));
		}
		return fixture11;
	}

	public DataViolationWritableBean getFixture12()
		throws Exception {
		if (fixture12 == null) {
			fixture12 = new DataViolationWritableBean();
			fixture12.setActualValue("0123456789");
			fixture12.setExpectedValue("0123456789");
			fixture12.setFieldNumber(new Integer(-1));
			fixture12.setFileName("0123456789");
			fixture12.setLineNumber(new Integer(-1));
		}
		return fixture12;
	}

	public DataViolationWritableBean getFixture13()
		throws Exception {
		if (fixture13 == null) {
			fixture13 = new DataViolationWritableBean();
			fixture13.setActualValue("0123456789");
			fixture13.setExpectedValue("0123456789");
			fixture13.setFieldNumber(new Integer(-1));
			fixture13.setFileName("0123456789");
			fixture13.setLineNumber(new Integer(0));
		}
		return fixture13;
	}

	public DataViolationWritableBean getFixture14()
		throws Exception {
		if (fixture14 == null) {
			fixture14 = new DataViolationWritableBean();
			fixture14.setActualValue("0123456789");
			fixture14.setExpectedValue("0123456789");
			fixture14.setFieldNumber(new Integer(0));
			fixture14.setFileName("0123456789");
			fixture14.setLineNumber(new Integer(-1));
		}
		return fixture14;
	}

	public DataViolationWritableBean getFixture15()
		throws Exception {
		if (fixture15 == null) {
			fixture15 = new DataViolationWritableBean();
			fixture15.setActualValue("0123456789");
			fixture15.setExpectedValue("0123456789");
			fixture15.setFieldNumber(new Integer(0));
			fixture15.setFileName("0123456789");
			fixture15.setLineNumber(new Integer(0));
		}
		return fixture15;
	}

	public DataViolationWritableBean getFixture16()
		throws Exception {
		if (fixture16 == null) {
			fixture16 = new DataViolationWritableBean();
			fixture16.setActualValue("0123456789");
			fixture16.setExpectedValue("0123456789");
			fixture16.setFieldNumber(new Integer(1));
			fixture16.setFileName("0123456789");
			fixture16.setLineNumber(new Integer(-1));
		}
		return fixture16;
	}

	public DataViolationWritableBean getFixture17()
		throws Exception {
		if (fixture17 == null) {
			fixture17 = new DataViolationWritableBean();
			fixture17.setActualValue("0123456789");
			fixture17.setExpectedValue("An��t-1.0.txt");
			fixture17.setFieldNumber(new Integer(-1));
			fixture17.setFileName("An��t-1.0.txt");
			fixture17.setLineNumber(new Integer(1));
		}
		return fixture17;
	}

	public DataViolationWritableBean getFixture18()
		throws Exception {
		if (fixture18 == null) {
			fixture18 = new DataViolationWritableBean();
			fixture18.setActualValue("0123456789");
			fixture18.setExpectedValue("An��t-1.0.txt");
			fixture18.setFieldNumber(new Integer(0));
			fixture18.setFileName("An��t-1.0.txt");
			fixture18.setLineNumber(new Integer(0));
		}
		return fixture18;
	}

	public DataViolationWritableBean getFixture19()
		throws Exception {
		if (fixture19 == null) {
			fixture19 = new DataViolationWritableBean();
			fixture19.setActualValue("0123456789");
			fixture19.setExpectedValue("An��t-1.0.txt");
			fixture19.setFieldNumber(new Integer(0));
			fixture19.setFileName("An��t-1.0.txt");
			fixture19.setLineNumber(new Integer(1));
		}
		return fixture19;
	}

	public DataViolationWritableBean getFixture20()
		throws Exception {
		if (fixture20 == null) {
			fixture20 = new DataViolationWritableBean();
			fixture20.setActualValue("0123456789");
			fixture20.setExpectedValue("An��t-1.0.txt");
			fixture20.setFieldNumber(new Integer(1));
			fixture20.setFileName("An��t-1.0.txt");
			fixture20.setLineNumber(new Integer(0));
		}
		return fixture20;
	}

	public DataViolationWritableBean getFixture21()
		throws Exception {
		if (fixture21 == null) {
			fixture21 = new DataViolationWritableBean();
			fixture21.setActualValue("0123456789");
			fixture21.setExpectedValue("An��t-1.0.txt");
			fixture21.setFieldNumber(new Integer(1));
			fixture21.setFileName("An��t-1.0.txt");
			fixture21.setLineNumber(new Integer(1));
		}
		return fixture21;
	}

	public DataViolationWritableBean getFixture22()
		throws Exception {
		if (fixture22 == null) {
			fixture22 = new DataViolationWritableBean();
			fixture22.setActualValue("An��t-1.0.txt");
			fixture22.setExpectedValue("0123456789");
			fixture22.setFieldNumber(new Integer(-1));
			fixture22.setFileName("0123456789");
			fixture22.setLineNumber(new Integer(-1));
		}
		return fixture22;
	}

	public DataViolationWritableBean getFixture23()
		throws Exception {
		if (fixture23 == null) {
			fixture23 = new DataViolationWritableBean();
			fixture23.setActualValue("An��t-1.0.txt");
			fixture23.setExpectedValue("0123456789");
			fixture23.setFieldNumber(new Integer(0));
			fixture23.setFileName("0123456789");
			fixture23.setLineNumber(new Integer(-1));
		}
		return fixture23;
	}

	public DataViolationWritableBean getFixture24()
		throws Exception {
		if (fixture24 == null) {
			fixture24 = new DataViolationWritableBean();
			fixture24.setActualValue("An��t-1.0.txt");
			fixture24.setExpectedValue("0123456789");
			fixture24.setFieldNumber(new Integer(1));
			fixture24.setFileName("0123456789");
			fixture24.setLineNumber(new Integer(-1));
		}
		return fixture24;
	}

	public DataViolationWritableBean getFixture25()
		throws Exception {
		if (fixture25 == null) {
			fixture25 = new DataViolationWritableBean();
			fixture25.setActualValue("An��t-1.0.txt");
			fixture25.setExpectedValue("An��t-1.0.txt");
			fixture25.setFieldNumber(new Integer(-1));
			fixture25.setFileName("An��t-1.0.txt");
			fixture25.setLineNumber(new Integer(0));
		}
		return fixture25;
	}

	public DataViolationWritableBean getFixture26()
		throws Exception {
		if (fixture26 == null) {
			fixture26 = new DataViolationWritableBean();
			fixture26.setActualValue("An��t-1.0.txt");
			fixture26.setExpectedValue("An��t-1.0.txt");
			fixture26.setFieldNumber(new Integer(-1));
			fixture26.setFileName("An��t-1.0.txt");
			fixture26.setLineNumber(new Integer(1));
		}
		return fixture26;
	}

	public DataViolationWritableBean getFixture27()
		throws Exception {
		if (fixture27 == null) {
			fixture27 = new DataViolationWritableBean();
			fixture27.setActualValue("An��t-1.0.txt");
			fixture27.setExpectedValue("An��t-1.0.txt");
			fixture27.setFieldNumber(new Integer(0));
			fixture27.setFileName("An��t-1.0.txt");
			fixture27.setLineNumber(new Integer(0));
		}
		return fixture27;
	}

	public DataViolationWritableBean getFixture28()
		throws Exception {
		if (fixture28 == null) {
			fixture28 = new DataViolationWritableBean();
			fixture28.setActualValue("An��t-1.0.txt");
			fixture28.setExpectedValue("An��t-1.0.txt");
			fixture28.setFieldNumber(new Integer(0));
			fixture28.setFileName("An��t-1.0.txt");
			fixture28.setLineNumber(new Integer(1));
		}
		return fixture28;
	}

	public DataViolationWritableBean getFixture29()
		throws Exception {
		if (fixture29 == null) {
			fixture29 = new DataViolationWritableBean();
			fixture29.setActualValue("An��t-1.0.txt");
			fixture29.setExpectedValue("An��t-1.0.txt");
			fixture29.setFieldNumber(new Integer(1));
			fixture29.setFileName("An��t-1.0.txt");
			fixture29.setLineNumber(new Integer(0));
		}
		return fixture29;
	}

	public DataViolationWritableBean getFixture30()
		throws Exception {
		if (fixture30 == null) {
			fixture30 = new DataViolationWritableBean();
			fixture30.setActualValue("An��t-1.0.txt");
			fixture30.setExpectedValue("An��t-1.0.txt");
			fixture30.setFieldNumber(new Integer(1));
			fixture30.setFileName("An��t-1.0.txt");
			fixture30.setLineNumber(new Integer(1));
		}
		return fixture30;
	}

	@Test
	public void testDataViolationWritableBean_1()
		throws Exception {

		DataViolationWritableBean result = new DataViolationWritableBean();

		assertNotNull(result);
		assertEquals("{\"lineNumber\":null,\"fieldNumber\":null,\"fileName\":null,\"expectedValue\":null,\"actualValue\":null}", result.toString());
		assertEquals(null, result.getFileName());
		assertEquals(null, result.getLineNumber());
		assertEquals(null, result.getFieldNumber());
		assertEquals(null, result.getExpectedValue());
		assertEquals(null, result.getActualValue());
	}

	@Test
	public void testDataViolationWritableBean_2()
		throws Exception {
		DataViolationWritableBean dataViolationWritableBean = new DataViolationWritableBean();

		DataViolationWritableBean result = new DataViolationWritableBean(dataViolationWritableBean);

		assertNotNull(result);
		assertEquals("{\"lineNumber\":null,\"fieldNumber\":null,\"fileName\":null,\"expectedValue\":null,\"actualValue\":null}", result.toString());
		assertEquals(null, result.getFileName());
		assertEquals(null, result.getLineNumber());
		assertEquals(null, result.getFieldNumber());
		assertEquals(null, result.getExpectedValue());
		assertEquals(null, result.getActualValue());
	}


	@Test
	public void testGetActualValue_fixture1_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture1();

		String result = fixture.getActualValue();

		assertEquals(null, result);
	}

	@Test
	public void testGetActualValue_fixture2_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture2();

		String result = fixture.getActualValue();

		assertEquals("", result);
	}

	@Test
	public void testGetActualValue_fixture3_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture3();

		String result = fixture.getActualValue();

		assertEquals("", result);
	}

	@Test
	public void testGetActualValue_fixture4_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture4();

		String result = fixture.getActualValue();

		assertEquals("", result);
	}

	@Test
	public void testGetActualValue_fixture5_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture5();

		String result = fixture.getActualValue();

		assertEquals("", result);
	}

	@Test
	public void testGetActualValue_fixture6_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture6();

		String result = fixture.getActualValue();

		assertEquals("", result);
	}

	@Test
	public void testGetActualValue_fixture7_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture7();

		String result = fixture.getActualValue();

		assertEquals("", result);
	}

	@Test
	public void testGetActualValue_fixture8_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture8();

		String result = fixture.getActualValue();

		assertEquals("", result);
	}

	@Test
	public void testGetActualValue_fixture9_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture9();

		String result = fixture.getActualValue();

		assertEquals("", result);
	}

	@Test
	public void testGetActualValue_fixture10_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture10();

		String result = fixture.getActualValue();

		assertEquals("", result);
	}

	@Test
	public void testGetActualValue_fixture11_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture11();

		String result = fixture.getActualValue();

		assertEquals("", result);
	}

	@Test
	public void testGetActualValue_fixture12_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture12();

		String result = fixture.getActualValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetActualValue_fixture13_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture13();

		String result = fixture.getActualValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetActualValue_fixture14_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture14();

		String result = fixture.getActualValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetActualValue_fixture15_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture15();

		String result = fixture.getActualValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetActualValue_fixture16_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture16();

		String result = fixture.getActualValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetActualValue_fixture17_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture17();

		String result = fixture.getActualValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetActualValue_fixture18_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture18();

		String result = fixture.getActualValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetActualValue_fixture19_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture19();

		String result = fixture.getActualValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetActualValue_fixture20_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture20();

		String result = fixture.getActualValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetActualValue_fixture21_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture21();

		String result = fixture.getActualValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetActualValue_fixture22_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture22();

		String result = fixture.getActualValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetActualValue_fixture23_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture23();

		String result = fixture.getActualValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetActualValue_fixture24_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture24();

		String result = fixture.getActualValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetActualValue_fixture25_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture25();

		String result = fixture.getActualValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetActualValue_fixture26_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture26();

		String result = fixture.getActualValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetActualValue_fixture27_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture27();

		String result = fixture.getActualValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetActualValue_fixture28_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture28();

		String result = fixture.getActualValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetActualValue_fixture29_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture29();

		String result = fixture.getActualValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetActualValue_fixture30_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture30();

		String result = fixture.getActualValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture1_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture1();

		String result = fixture.getExpectedValue();

		assertEquals(null, result);
	}

	@Test
	public void testGetExpectedValue_fixture2_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture2();

		String result = fixture.getExpectedValue();

		assertEquals("", result);
	}

	@Test
	public void testGetExpectedValue_fixture3_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture3();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture4_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture4();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture5_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture5();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture6_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture6();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture7_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture7();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture8_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture8();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture9_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture9();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture10_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture10();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture11_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture11();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture12_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture12();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture13_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture13();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture14_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture14();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture15_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture15();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture16_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture16();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture17_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture17();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture18_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture18();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture19_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture19();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture20_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture20();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture21_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture21();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture22_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture22();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture23_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture23();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture24_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture24();

		String result = fixture.getExpectedValue();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetExpectedValue_fixture25_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture25();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture26_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture26();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture27_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture27();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture28_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture28();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture29_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture29();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetExpectedValue_fixture30_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture30();

		String result = fixture.getExpectedValue();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFieldNumber_fixture1_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture1();

		Integer result = fixture.getFieldNumber();

		assertEquals(null, result);
	}

	@Test
	public void testGetFieldNumber_fixture2_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture2();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture3_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture3();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture4_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture4();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture5_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture5();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture6_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture6();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture7_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture7();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture8_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture8();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture9_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture9();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture10_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture10();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture11_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture11();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture12_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture12();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture13_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture13();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture14_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture14();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture15_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture15();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture16_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture16();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture17_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture17();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture18_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture18();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture19_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture19();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture20_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture20();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture21_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture21();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture22_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture22();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture23_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture23();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture24_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture24();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture25_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture25();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture26_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture26();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture27_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture27();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture28_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture28();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture29_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture29();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFieldNumber_fixture30_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture30();

		Integer result = fixture.getFieldNumber();

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
	public void testGetFileName_fixture1_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture1();

		String result = fixture.getFileName();

		assertEquals(null, result);
	}

	@Test
	public void testGetFileName_fixture2_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture2();

		String result = fixture.getFileName();

		assertEquals("", result);
	}

	@Test
	public void testGetFileName_fixture3_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture3();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture4_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture4();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture5_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture5();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture6_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture6();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture7_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture7();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture8_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture8();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture9_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture9();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture10_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture10();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture11_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture11();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture12_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture12();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture13_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture13();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture14_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture14();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture15_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture15();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture16_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture16();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture17_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture17();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture18_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture18();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture19_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture19();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture20_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture20();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture21_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture21();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture22_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture22();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture23_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture23();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture24_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture24();

		String result = fixture.getFileName();

		assertEquals("0123456789", result);
	}

	@Test
	public void testGetFileName_fixture25_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture25();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture26_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture26();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture27_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture27();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture28_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture28();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture29_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture29();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetFileName_fixture30_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture30();

		String result = fixture.getFileName();

		assertEquals("An��t-1.0.txt", result);
	}

	@Test
	public void testGetLineNumber_fixture1_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture1();

		Integer result = fixture.getLineNumber();

		assertEquals(null, result);
	}

	@Test
	public void testGetLineNumber_fixture2_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture2();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture3_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture3();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture4_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture4();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture5_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture5();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture6_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture6();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture7_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture7();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture8_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture8();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture9_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture9();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture10_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture10();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture11_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture11();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture12_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture12();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture13_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture13();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture14_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture14();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture15_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture15();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture16_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture16();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture17_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture17();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture18_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture18();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture19_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture19();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture20_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture20();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture21_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture21();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture22_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture22();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture23_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture23();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture24_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture24();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture25_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture25();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture26_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture26();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture27_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture27();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture28_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture28();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture29_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture29();

		Integer result = fixture.getLineNumber();

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
	public void testGetLineNumber_fixture30_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture30();

		Integer result = fixture.getLineNumber();

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
	public void testSetActualValue_fixture1_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture1();
		String actualValue = "";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture2_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture2();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture3_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture3();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture4_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture4();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture5_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture5();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture6_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture6();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture7_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture7();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture8_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture8();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture9_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture9();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture10_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture10();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture11_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture11();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture12_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture12();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture13_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture13();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture14_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture14();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture15_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture15();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture16_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture16();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture17_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture17();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture18_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture18();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture19_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture19();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture20_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture20();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture21_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture21();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture22_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture22();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture23_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture23();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture24_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture24();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture25_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture25();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture26_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture26();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture27_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture27();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture28_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture28();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture29_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture29();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetActualValue_fixture30_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture30();
		String actualValue = "0123456789";

		fixture.setActualValue(actualValue);

	}

	@Test
	public void testSetExpectedValue_fixture1_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture1();
		String expectedValue = "";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture2_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture2();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture3_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture3();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture4_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture4();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture5_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture5();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture6_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture6();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture7_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture7();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture8_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture8();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture9_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture9();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture10_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture10();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture11_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture11();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture12_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture12();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture13_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture13();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture14_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture14();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture15_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture15();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture16_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture16();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture17_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture17();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture18_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture18();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture19_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture19();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture20_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture20();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture21_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture21();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture22_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture22();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture23_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture23();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture24_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture24();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture25_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture25();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture26_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture26();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture27_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture27();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture28_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture28();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture29_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture29();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetExpectedValue_fixture30_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture30();
		String expectedValue = "0123456789";

		fixture.setExpectedValue(expectedValue);

	}

	@Test
	public void testSetFieldNumber_fixture1_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture1();
		Integer fieldNumber = new Integer(-1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture2_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture2();
		Integer fieldNumber = new Integer(0);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture3_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture3();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture4_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture4();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture5_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture5();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture6_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture6();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture7_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture7();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture8_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture8();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture9_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture9();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture10_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture10();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture11_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture11();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture12_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture12();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture13_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture13();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture14_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture14();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture15_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture15();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture16_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture16();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture17_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture17();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture18_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture18();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture19_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture19();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture20_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture20();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture21_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture21();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture22_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture22();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture23_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture23();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture24_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture24();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture25_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture25();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture26_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture26();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture27_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture27();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture28_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture28();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture29_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture29();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFieldNumber_fixture30_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture30();
		Integer fieldNumber = new Integer(1);

		fixture.setFieldNumber(fieldNumber);

	}

	@Test
	public void testSetFileName_fixture1_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture1();
		String fileName = "";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture2_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture2();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture3_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture3();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture4_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture4();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture5_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture5();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture6_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture6();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture7_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture7();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture8_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture8();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture9_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture9();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture10_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture10();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture11_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture11();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture12_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture12();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture13_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture13();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture14_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture14();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture15_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture15();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture16_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture16();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture17_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture17();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture18_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture18();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture19_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture19();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture20_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture20();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture21_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture21();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture22_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture22();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture23_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture23();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture24_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture24();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture25_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture25();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture26_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture26();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture27_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture27();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture28_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture28();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture29_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture29();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetFileName_fixture30_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture30();
		String fileName = "0123456789";

		fixture.setFileName(fileName);

	}

	@Test
	public void testSetLineNumber_fixture1_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture1();
		Integer lineNumber = new Integer(-1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture2_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture2();
		Integer lineNumber = new Integer(0);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture3_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture3();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture4_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture4();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture5_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture5();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture6_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture6();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture7_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture7();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture8_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture8();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture9_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture9();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture10_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture10();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture11_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture11();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture12_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture12();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture13_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture13();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture14_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture14();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture15_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture15();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture16_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture16();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture17_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture17();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture18_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture18();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture19_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture19();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture20_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture20();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture21_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture21();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture22_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture22();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture23_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture23();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture24_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture24();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture25_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture25();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture26_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture26();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture27_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture27();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture28_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture28();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture29_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture29();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testSetLineNumber_fixture30_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture30();
		Integer lineNumber = new Integer(1);

		fixture.setLineNumber(lineNumber);

	}

	@Test
	public void testToString_fixture1_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture1();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":null,\"fieldNumber\":null,\"fileName\":null,\"expectedValue\":null,\"actualValue\":null}", result);
	}

	@Test
	public void testToString_fixture2_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture2();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":-1,\"fieldNumber\":-1,\"fileName\":,\"expectedValue\":,\"actualValue\":}", result);
	}

	@Test
	public void testToString_fixture3_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture3();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":-1,\"fieldNumber\":-1,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":}", result);
	}

	@Test
	public void testToString_fixture4_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture4();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":0,\"fieldNumber\":-1,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":}", result);
	}

	@Test
	public void testToString_fixture5_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture5();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":-1,\"fieldNumber\":0,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":}", result);
	}

	@Test
	public void testToString_fixture6_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture6();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":0,\"fieldNumber\":0,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":}", result);
	}

	@Test
	public void testToString_fixture7_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture7();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":-1,\"fieldNumber\":1,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":}", result);
	}

	@Test
	public void testToString_fixture8_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture8();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":0,\"fieldNumber\":1,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":}", result);
	}

	@Test
	public void testToString_fixture9_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture9();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":1,\"fieldNumber\":-1,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":}", result);
	}

	@Test
	public void testToString_fixture10_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture10();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":1,\"fieldNumber\":0,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":}", result);
	}

	@Test
	public void testToString_fixture11_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture11();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":1,\"fieldNumber\":1,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":}", result);
	}

	@Test
	public void testToString_fixture12_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture12();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":-1,\"fieldNumber\":-1,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":0123456789}", result);
	}

	@Test
	public void testToString_fixture13_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture13();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":0,\"fieldNumber\":-1,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":0123456789}", result);
	}

	@Test
	public void testToString_fixture14_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture14();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":-1,\"fieldNumber\":0,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":0123456789}", result);
	}

	@Test
	public void testToString_fixture15_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture15();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":0,\"fieldNumber\":0,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":0123456789}", result);
	}

	@Test
	public void testToString_fixture16_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture16();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":-1,\"fieldNumber\":1,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":0123456789}", result);
	}

	@Test
	public void testToString_fixture17_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture17();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":1,\"fieldNumber\":-1,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":0123456789}", result);
	}

	@Test
	public void testToString_fixture18_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture18();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":0,\"fieldNumber\":0,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":0123456789}", result);
	}

	@Test
	public void testToString_fixture19_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture19();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":1,\"fieldNumber\":0,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":0123456789}", result);
	}

	@Test
	public void testToString_fixture20_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture20();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":0,\"fieldNumber\":1,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":0123456789}", result);
	}

	@Test
	public void testToString_fixture21_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture21();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":1,\"fieldNumber\":1,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":0123456789}", result);
	}

	@Test
	public void testToString_fixture22_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture22();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":-1,\"fieldNumber\":-1,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":An��t-1.0.txt}", result);
	}

	@Test
	public void testToString_fixture23_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture23();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":-1,\"fieldNumber\":0,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":An��t-1.0.txt}", result);
	}

	@Test
	public void testToString_fixture24_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture24();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":-1,\"fieldNumber\":1,\"fileName\":0123456789,\"expectedValue\":0123456789,\"actualValue\":An��t-1.0.txt}", result);
	}

	@Test
	public void testToString_fixture25_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture25();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":0,\"fieldNumber\":-1,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":An��t-1.0.txt}", result);
	}

	@Test
	public void testToString_fixture26_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture26();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":1,\"fieldNumber\":-1,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":An��t-1.0.txt}", result);
	}

	@Test
	public void testToString_fixture27_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture27();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":0,\"fieldNumber\":0,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":An��t-1.0.txt}", result);
	}

	@Test
	public void testToString_fixture28_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture28();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":1,\"fieldNumber\":0,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":An��t-1.0.txt}", result);
	}

	@Test
	public void testToString_fixture29_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture29();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":0,\"fieldNumber\":1,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":An��t-1.0.txt}", result);
	}

	@Test
	public void testToString_fixture30_1()
		throws Exception {
		DataViolationWritableBean fixture = getFixture30();

		String result = fixture.toString();

		assertEquals("{\"lineNumber\":1,\"fieldNumber\":1,\"fileName\":An��t-1.0.txt,\"expectedValue\":An��t-1.0.txt,\"actualValue\":An��t-1.0.txt}", result);
	}

	

	@Before
	public void setUp()
		throws Exception {
		fixture1 = new DataViolationWritableBean();
	}

	@After
	public void tearDown()
		throws Exception {
	}
}