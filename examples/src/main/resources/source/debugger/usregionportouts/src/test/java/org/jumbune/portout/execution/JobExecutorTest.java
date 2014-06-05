package org.jumbune.portout.execution;

import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;
import org.jumbune.portout.execution.JobExecutor;
import org.junit.*;
import static org.junit.Assert.*;

public class JobExecutorTest {
	private JobExecutor fixture = new JobExecutor();


	public JobExecutor getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testJobExecutor_1()
		throws Exception {

		JobExecutor result = new JobExecutor();

		assertNotNull(result);
		assertEquals(null, result.getParser());
		assertEquals(null, result.getHdfsOutputPath());
	}

	@Test
	public void testGetConf_fixture_1()
		throws Exception {
		JobExecutor fixture2 = getFixture();

		Configuration result = fixture2.getConf();

		assertNotNull(result);
		
	}

	@Test
	public void testGetHdfsOutputPath_fixture_1()
		throws Exception {
		JobExecutor fixture2 = getFixture();

		String result = fixture2.getHdfsOutputPath();

		assertEquals(null, result);
	}

	@Test
	public void testGetOutputPath_fixture_1()
		throws Exception {
		JobExecutor fixture2 = getFixture();

		String result = fixture2.getOutputPath();

		assertNotNull(result);
	}

	@Test
	public void testGetParser_fixture_1()
		throws Exception {
		JobExecutor fixture2 = getFixture();

		GenericOptionsParser result = fixture2.getParser();

		assertEquals(null, result);
	}

	@Test
	public void testSetConf_fixture_1()
		throws Exception {
		JobExecutor fixture2 = getFixture();
		Configuration conf = new Configuration();

		fixture2.setConf(conf);

	}

	@Test
	public void testSetConf_fixture_2()
		throws Exception {
		JobExecutor fixture2 = getFixture();
		Configuration conf = new Configuration(false);

		fixture2.setConf(conf);

	}


	@Test
	public void testSetHdfsOutputPath_fixture_1()
		throws Exception {
		JobExecutor fixture2 = getFixture();
		String hdfsOutputPath = "";

		fixture2.setHdfsOutputPath(hdfsOutputPath);

	}

	@Test
	public void testSetHdfsOutputPath_fixture_2()
		throws Exception {
		JobExecutor fixture2 = getFixture();
		String hdfsOutputPath = "0123456789";

		fixture2.setHdfsOutputPath(hdfsOutputPath);

	}



	

	@Test
	public void testSetParser_fixture_5()
		throws Exception {
		JobExecutor fixture2 = getFixture();
		GenericOptionsParser parser = new GenericOptionsParser(new Configuration(false), new String[] {""});

		fixture2.setParser(parser);

	}


	@Test
	public void testSetParser_fixture_7()
		throws Exception {
		JobExecutor fixture2 = getFixture();
		GenericOptionsParser parser = new GenericOptionsParser((Options) null, new String[] {""});

		fixture2.setParser(parser);

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