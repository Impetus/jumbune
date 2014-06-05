package org.jumbune.portout.execution;

import java.io.IOException;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapred.lib.ChainReducer;
import org.apache.hadoop.util.GenericOptionsParser;
import org.jumbune.portout.PortoutConstants;
import org.jumbune.portout.execution.ChainMRMJobExecutor;
import org.jumbune.portout.execution.JobExecutor;
import org.jumbune.portout.execution.MapRedJobExecutor;
import org.jumbune.portout.mappers.oldapi.PortoutReducer;
import org.jumbune.portout.mappers.oldapi.PortoutRegionMapper;
import org.jumbune.portout.mappers.oldapi.ServiceProviderMapper;
import org.jumbune.portout.mappers.oldapi.TupleValidateMapper;
import org.jumbune.portout.mappers.oldapi.USRegionMapper;


/**
 * Executor class for US Region portout example
 *
 */
@SuppressWarnings("deprecation")
public class ChainMRMJobExecutor extends MapRedJobExecutor {
	
	/**
	 * public constructor for ChainMRMJobExecutor
	 */
	public ChainMRMJobExecutor() {
		super();
	}

	/**
	 * Create Job with required configuration
	 * 
	 * @param outputPath
	 *            - output path directory on HDFS
	 * @return job
	 * @throws IOException
	 */
	public JobConf createPortPSJob(int numReducers) throws IOException {
		JobExecutor jExec = new JobExecutor();
		JobConf job = new JobConf(jExec.getConf());
		job.setJobName(PortoutConstants.JOB_NAME);
		job.setJarByClass(USRegionMapper.class);

		FileOutputFormat.setOutputPath(job, new Path(getOutputPath()));
		job.setInputFormat(TextInputFormat.class);
		job.setOutputFormat(TextOutputFormat.class);
		JobConf reportMapConf = new JobConf(false);
		ChainMapper.addMapper(job, TupleValidateMapper.class, LongWritable.class, Text.class, Text.class, Text.class, true, reportMapConf);

		JobConf map1Conf = new JobConf(false);
		ChainMapper.addMapper(job, USRegionMapper.class, Text.class, Text.class, Text.class, Text.class, true, map1Conf);

		ChainMapper.addMapper(job, ServiceProviderMapper.class, Text.class, Text.class, Text.class, Text.class, true, new JobConf(false));

		JobConf reduceConf = new JobConf(false);
		ChainReducer.setReducer(job, PortoutReducer.class, Text.class, Text.class, Text.class, Text.class, true, reduceConf);

		JobConf map11Conf = new JobConf(false);
		ChainReducer.addMapper(job, PortoutRegionMapper.class, Text.class, Text.class, Text.class, Text.class, true, map11Conf);

		job.setNumReduceTasks(numReducers);
		return job;
	}

	/**
	 * Main method for executing US region port out example
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ClassNotFoundException, InterruptedException, IOException {
		ChainMRMJobExecutor excecutor = new ChainMRMJobExecutor();
		Configuration confg=new Configuration();
		confg.addResource( new Path( "/home/impadmin/hadoop_setups/hadoop-1.0.4/conf/core-site.xml" ) );
		confg.addResource( new Path( "/home/impadmin/hadoop_setups/hadoop-1.0.4/conf/hdfs-site.xml" ) );
		
		GenericOptionsParser genericOptionsParser = new GenericOptionsParser( args);
		String [] arrays= genericOptionsParser.getRemainingArgs();
		
		int numReducers = Integer.parseInt(arrays[0]);
		JobConf job1 = excecutor.createPortPSJob(numReducers);
		excecutor.execute(job1);
	}
}