package org.jumbune.datavalidation;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.db.DataDrivenDBInputFormat;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;

import org.jumbune.datavalidation.DataValidationFileSplit;
import org.jumbune.datavalidation.DataValidationInputFormat;
import org.junit.*;
import static org.junit.Assert.*;

public class DataValidationInputFormatTest {
	private DataValidationInputFormat fixture = new DataValidationInputFormat();


	public DataValidationInputFormat getFixture()
		throws Exception {
		return fixture;
	}

	@Test
	public void testCreateRecordReader_fixture_1()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new DataValidationFileSplit();
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(), new TaskAttemptID("", 0, false, 0, 0));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test
	public void testCreateRecordReader_fixture_2()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new org.apache.hadoop.mapreduce.lib.db.DBInputFormat.DBInputSplit();
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(false), new TaskAttemptID("0123456789", 0, false, Integer.MAX_VALUE, Integer.MAX_VALUE));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test
	public void testCreateRecordReader_fixture_3()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new org.apache.hadoop.mapreduce.lib.db.DataDrivenDBInputFormat.DataDrivenDBInputSplit("", "");
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(false), new TaskAttemptID("0123456789", 0, false, Integer.MAX_VALUE, Integer.MAX_VALUE));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test
	public void testCreateRecordReader_fixture_4()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new org.apache.hadoop.mapreduce.lib.db.DataDrivenDBInputFormat.DataDrivenDBInputSplit("0123456789", "0123456789");
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(false), new TaskAttemptID("0123456789", 0, false, Integer.MAX_VALUE, Integer.MAX_VALUE));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test
	public void testCreateRecordReader_fixture_5()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new org.apache.hadoop.mapreduce.lib.db.DataDrivenDBInputFormat.DataDrivenDBInputSplit();
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(false), new TaskAttemptID("0123456789", 0, false, Integer.MAX_VALUE, Integer.MAX_VALUE));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test
	public void testCreateRecordReader_fixture_6()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new CombineFileSplit();
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(false), new TaskAttemptID("0123456789", 0, false, Integer.MAX_VALUE, Integer.MAX_VALUE));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test
	public void testCreateRecordReader_fixture_10()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new org.apache.hadoop.mapreduce.lib.db.DBInputFormat.DBInputSplit();
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(), new TaskAttemptID("", 0, false, 0, 0));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test
	public void testCreateRecordReader_fixture_11()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new org.apache.hadoop.mapreduce.lib.db.DataDrivenDBInputFormat.DataDrivenDBInputSplit("", "");
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(), new TaskAttemptID("", 0, false, 0, 0));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test
	public void testCreateRecordReader_fixture_12()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new org.apache.hadoop.mapreduce.lib.db.DataDrivenDBInputFormat.DataDrivenDBInputSplit("0123456789", "0123456789");
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(), new TaskAttemptID("", 0, false, 0, 0));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test
	public void testCreateRecordReader_fixture_13()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new org.apache.hadoop.mapreduce.lib.db.DataDrivenDBInputFormat.DataDrivenDBInputSplit();
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(), new TaskAttemptID("", 0, false, 0, 0));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test
	public void testCreateRecordReader_fixture_14()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new CombineFileSplit();
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(), new TaskAttemptID("", 0, false, 0, 0));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test
	public void testCreateRecordReader_fixture_18()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		InputSplit split = new DataValidationFileSplit();
		TaskAttemptContext context = new TaskAttemptContext(new Configuration(false), new TaskAttemptID("0123456789", 0, false, Integer.MAX_VALUE, Integer.MAX_VALUE));

		RecordReader<LongWritable, Text> result = fixture2.createRecordReader(split, context);

		assertNotNull(result);
		assertEquals(0.0f, result.getProgress(), 1.0f);
		assertEquals(null, result.getCurrentValue());
	}

	@Test(expected = java.io.IOException.class)
	public void testGetSplits_fixture_1()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		JobContext job = new JobContext(new Configuration(), new JobID("", 0));

		List<InputSplit> result = fixture2.getSplits(job);

		assertNotNull(result);
	}

	@Test(expected = java.io.IOException.class)
	public void testGetSplits_fixture_2()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		JobContext job = new JobContext(new Configuration(false), new JobID("0123456789", 1));

		List<InputSplit> result = fixture2.getSplits(job);

		assertNotNull(result);
	}


	@Test
	public void testIsSplitable_fixture_5()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		JobContext context = new JobContext(new Configuration(), new JobID("", 0));
		Path file = new Path("0123456789", "0123456789");

		boolean result = fixture2.isSplitable(context, file);

		assertEquals(true, result);
	}

	
	

	@Test
	public void testIsSplitable_fixture_9()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		JobContext context = new JobContext(new Configuration(), new JobID("", 0));
		Path file = new Path(URI.create(""));

		boolean result = fixture2.isSplitable(context, file);

		assertEquals(true, result);
	}

	
	
	

	@Test
	public void testIsSplitable_fixture_17()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		JobContext context = new JobContext(new Configuration(false), new JobID("0123456789", 1));
		Path file = new Path("0123456789", "0123456789");

		boolean result = fixture2.isSplitable(context, file);

		assertEquals(true, result);
	}



	@Test
	public void testIsSplitable_fixture_21()
		throws Exception {
		DataValidationInputFormat fixture2 = getFixture();
		JobContext context = new JobContext(new Configuration(false), new JobID("0123456789", 1));
		Path file = new Path(URI.create(""));

		boolean result = fixture2.isSplitable(context, file);

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