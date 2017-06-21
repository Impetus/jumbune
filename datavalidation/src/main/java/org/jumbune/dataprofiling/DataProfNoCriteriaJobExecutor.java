package org.jumbune.dataprofiling;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataProfilingBean;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.dataprofiling.utils.DataProfilingConstants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class DataProfNoCriteriaJobExecutor {	
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(DataProfNoCriteriaJobExecutor.class);	
	
	/***
	 * main method for job execution
	 * @param args
	 */
	public static void main(String[] args) throws IOException, InterruptedException , ClassNotFoundException{

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		LOGGER.debug("Data Profiling job values respectively ["+otherArgs[0]+"], "+
				 otherArgs[1]);
		StringBuilder sb = new StringBuilder();
		
		int dynamicArgs = 0;		
		dynamicArgs = ((otherArgs.length)-1);
		
		for (int i = dynamicArgs; i < otherArgs.length; i++) {
			LOGGER.debug("other arguments" + otherArgs[i]);
			sb.append(otherArgs[i]);
		}
		
		String outputPath = DataProfilingConstants.OUTPUT_DIR_PATH + new Date().getTime();
		String inputPath = otherArgs[0].replace(" ", "");
		String dpBeanString = sb.toString();
		LOGGER.debug("Received dpBean value [" + dpBeanString+"]");
		Gson gson = new Gson();
		Type type = new TypeToken<DataProfilingBean>() {
		}.getType();
		
		DataProfilingBean dataProfilingBean = gson.fromJson(dpBeanString, type);
		String recordSeparator = dataProfilingBean.getRecordSeparator();
		conf.set(DataProfilingConstants.DATA_PROFILING_BEAN, dpBeanString);
		conf.set(DataProfilingConstants.RECORD_SEPARATOR, recordSeparator);
		
		conf.set(DataProfilingConstants.TEXTINPUTFORMAT_RECORD_DELIMITER, recordSeparator);

		Job job = new Job(conf, DataProfilingConstants.JOB_NAME);

		job.setJarByClass(DataProfNoCriteriaJobExecutor.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setMapperClass(DataProfNoCriteriaMapper.class);
		job.setCombinerClass(DataProfNoCriteriaReducer.class);
		job.setReducerClass(DataProfNoCriteriaReducer.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		
    	Path[] inputPaths = FileUtil.getAllNestedFilePath(job, inputPath);		
		
		TextInputFormat.setInputPaths(job, inputPaths);
		SequenceFileOutputFormat.setOutputPath(job, new Path(outputPath));
		
		job.waitForCompletion(true);
		
		Map<String, Integer> sortedMap = readJobOutputFromHdfs(conf,outputPath);
		final Gson dpReportGson = new GsonBuilder().disableHtmlEscaping().create();

		final String jsonString = dpReportGson.toJson(sortedMap);
		LOGGER.info(DataProfilingConstants.DATA_PROFILING_REPORT + jsonString);
	}

	private static  Map<String, Integer> readJobOutputFromHdfs(Configuration configuration,
			String outputPath) throws IOException {		
		
		FileSystem fs = FileSystem.get(configuration);
		Path inFile = new Path(outputPath);
		FileStatus[] fss = fs.listStatus(inFile);
		Path path = null;
		Text key = null;
		IntWritable value = null;
		SequenceFile.Reader reader = null;
		Map<String, Integer> dataValueMap = new LinkedHashMap<String, Integer>();
		for (FileStatus status : fss) {
			path = status.getPath();
			if (!((path.getName().equals("_SUCCESS")) || (path.getName()
					.equals("_logs")))) {
			reader = new SequenceFile.Reader(fs, path, configuration);
			key = new Text();
			value = new IntWritable();
			while (reader.next(key, value)) {
				dataValueMap.put(key.toString(), value.get());
			}
			reader.close();
			}
		}
		return dataValueMap;	
	}
}