package org.jumbune.dataprofiling;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataProfilingBean;
import org.jumbune.common.beans.FieldProfilingBean;
import org.jumbune.dataprofiling.utils.DataProfilingConstants;
import org.jumbune.datavalidation.DataValidationInputFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class DataProfilingJobExecutor {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(DataProfilingJobExecutor.class);
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, InterruptedException , ClassNotFoundException{
		
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		LOGGER.info("Data Profiling job values respectively ["+otherArgs[0]+"], "+
				 otherArgs[1]);
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < otherArgs.length; i++) {
			LOGGER.info("other arguments" + otherArgs[i]);
			sb.append(otherArgs[i]);
		}
		String outputPath = DataProfilingConstants.OUTPUT_DIR_PATH + new Date().getTime();
		String inputPath = otherArgs[0];
		String dpBeanString = sb.toString();
		LOGGER.info("Received dpBean value [" + dpBeanString+"]");
		Gson gson = new Gson();
		Type type = new TypeToken<DataProfilingBean>() {
		}.getType();
		
		DataProfilingBean dataProfilingBean = gson.fromJson(dpBeanString, type);
		String recordSeparator = dataProfilingBean.getRecordSeparator();
		conf.set(DataProfilingConstants.DATA_PROFILING_BEAN, dpBeanString);
		conf.set(DataProfilingConstants.RECORD_SEPARATOR, recordSeparator);
		Job job = new Job(conf,DataProfilingConstants.JOB_NAME);
		job.setJarByClass(DataProfilingJobExecutor.class);
		job.setMapperClass(DataProfilingMapper.class);
		job.setReducerClass(DataProfilingReducer.class);
		job.setInputFormatClass(DataValidationInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		DataValidationInputFormat.addInputPath(job, new Path(inputPath));
		SequenceFileOutputFormat.setOutputPath(job, new Path(outputPath));
		job.waitForCompletion(true);
		LOGGER.debug("Job completed , now going to read the result from hdfs");
		Map<String,Map<String, String>> dataMap = readJobOutputFromHdfs(conf,outputPath,dataProfilingBean);
		final Gson dpReportGson = new GsonBuilder().disableHtmlEscaping().create();

		final String jsonString = dpReportGson.toJson(dataMap);
		LOGGER.info(DataProfilingConstants.DATA_PROFILING_REPORT + jsonString);
	}
	
	private static Map<String, Map<String, String>> readJobOutputFromHdfs(Configuration configuration , String outputPath,DataProfilingBean dataProfilingBean) throws IOException{
		
		
		FileSystem fs = FileSystem.get(configuration);
		Path inFile = new Path(outputPath);
		FileStatus[] fss = fs.listStatus(inFile);
		Path path = null;
		Text key = null;
		IntWritable value = null;
		String unMatchedValue = null;
		SequenceFile.Reader reader = null;
		Map<String, Map<String, String>> fieldResultMap = new HashMap<String, Map<String,String>>();
		List<FieldProfilingBean> fieldProfilingBeansList = dataProfilingBean.getFieldProfilingRules();
		for (FileStatus status : fss) {
			path = status.getPath();
			if (!((path.getName().equals(DataProfilingConstants.HADOOP_SUCCESS_FILES)) || (path.getName()
					.equals(DataProfilingConstants.HADOOP_LOG_FILES)))) {
				reader = new SequenceFile.Reader(fs, path, configuration);
				key = new Text();
				value = new IntWritable();
				
			Map<String, String> matchedUnmatchedKeys = null;	
			while (reader.next(key, value)) {
				
				String [] keyArray = key.toString().split("-");
				int fieldNumber = Integer.parseInt(keyArray[1]);
				if(keyArray[0].equalsIgnoreCase("matched")){
						if(fieldResultMap.isEmpty()){
						matchedUnmatchedKeys = new HashMap<String, String>();
						matchedUnmatchedKeys.put(Integer.toString(value.get()),DataProfilingConstants.ZERO);
						fieldResultMap.put(Integer.toString(fieldNumber), matchedUnmatchedKeys);
					}else{
						LOGGER.info("unmachedcase");
						if(fieldResultMap.containsKey(Integer.toString(fieldNumber))){
							for(Map.Entry<String, String> map : fieldResultMap.get(Integer.toString(fieldNumber)).entrySet()){
								unMatchedValue = map.getValue();
							}
							fieldResultMap.get(Integer.toString(fieldNumber)).remove(DataProfilingConstants.ZERO);
							fieldResultMap.get(Integer.toString(fieldNumber)).put(Integer.toString(value.get()),unMatchedValue);
						}else{
							matchedUnmatchedKeys = new HashMap<String, String>();
							matchedUnmatchedKeys.put(Integer.toString(value.get()),DataProfilingConstants.ZERO);
							fieldResultMap.put(Integer.toString(fieldNumber), matchedUnmatchedKeys);
						}
					}
				}else{
					if( !fieldResultMap.isEmpty()){
						if(fieldResultMap.containsKey(Integer.toString(fieldNumber))){
							// if after match unmatched comes 
							String matchedKey = null;
							for(Map.Entry<String, String> map : fieldResultMap.get(Integer.toString(fieldNumber)).entrySet()){
								matchedKey = map.getKey();
							}
							matchedUnmatchedKeys = fieldResultMap.get(Integer.toString(fieldNumber));
							matchedUnmatchedKeys.put(matchedKey, Integer.toString(value.get()));
						}else{
							matchedUnmatchedKeys = new HashMap<String, String>();
							matchedUnmatchedKeys.put(DataProfilingConstants.ZERO,Integer.toString(value.get()));
							fieldResultMap.put(Integer.toString(fieldNumber), matchedUnmatchedKeys);
					}
					}else{
						matchedUnmatchedKeys = new HashMap<String, String>();
						matchedUnmatchedKeys.put(DataProfilingConstants.ZERO, Integer.toString(value.get()));
						fieldResultMap.put(Integer.toString(fieldNumber), matchedUnmatchedKeys);
					}
					
				}
			}
			reader.close();
		}
		}
		for (FieldProfilingBean fieldProfilingBean : fieldProfilingBeansList) {
			
			String operand = fieldProfilingBean.getDataProfilingOperand();
			if(operand!=null && !operand.isEmpty()){
			if(operand.equalsIgnoreCase(DataProfilingConstants.GREATER_THAN_EQUAL_TO)){
				operand = DataProfilingConstants.GREATERTHANEQUALTO;
			}else
			{
				operand = DataProfilingConstants.LESSTHANEQUALTO;
			}
			Map<String,String> fieldMap = fieldResultMap.get(Integer.toString(fieldProfilingBean.getFieldNumber()));
			if(fieldMap!=null){
				String definedRule = operand+String.format("%.3f", fieldProfilingBean.getComparisonValue());
				fieldMap.put(DataProfilingConstants.RULE, definedRule);
			}
		}}
		
		return fieldResultMap;
	}
	
	

}
