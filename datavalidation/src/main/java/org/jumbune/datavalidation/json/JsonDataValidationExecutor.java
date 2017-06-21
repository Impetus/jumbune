package org.jumbune.datavalidation.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import org.jumbune.common.utils.FileUtil;

import com.google.gson.Gson;



public class JsonDataValidationExecutor 
{
	private static final Logger LOGGER = LogManager.getLogger(JsonDataValidationExecutor.class);
	public JsonDataValidationExecutor(){
		
	}
	
    public static void main( String[] args ) throws IOException, ClassNotFoundException, InterruptedException
    {
    	Configuration conf = new Configuration();	
    	String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		StringBuilder sb = new StringBuilder();
    	for (int j = 2; j < otherArgs.length; j++) {
			
    		sb.append(otherArgs[j]);
		}
    	
    	LOGGER.debug("Arguments[ " + otherArgs.length+"]"+"and values respectively ["+otherArgs[0]+"], "+
				otherArgs[1]+", ["+otherArgs[2]+"]"+", ["+otherArgs[3]+"],"+
				otherArgs[4]);
		
		String inputpath = otherArgs[0];
		String outputpath = "/tmp/jumbune/dvjsonreport"+  new Date().getTime();
		
		String json = otherArgs[1];
		String nullCondition = otherArgs[2];
		String regex = otherArgs[3];
		String dvDir = otherArgs[4];
		
		

		if(regex.isEmpty()){
			conf.set(JsonDataVaildationConstants.REGEX_ARGUMENT, "");
		}else{
			conf.set(JsonDataVaildationConstants.REGEX_ARGUMENT, regex);
		}
		
		if(nullCondition.isEmpty()){
			conf.set(JsonDataVaildationConstants.NULL_ARGUMENT, "");
		}else{
			conf.set(JsonDataVaildationConstants.NULL_ARGUMENT, nullCondition);
		}
		
		
		conf.set(JsonDataVaildationConstants.SLAVE_DIR, dvDir);
		conf.set(JsonDataVaildationConstants.JSON_ARGUMENT, json);
		FileSystem fs = FileSystem.get(conf);

		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "JSONDataValidation");
		job.setJarByClass(JsonDataValidationExecutor.class);
		
		job.setInputFormatClass(JsonFileInputFormat.class);
		
		job.setMapperClass(JsonDataValidationMapper.class);
		job.setPartitionerClass(JsonDataValidationPartitioner.class);
		job.setReducerClass(JsonDataValidationReducer.class);
		job.setNumReduceTasks(5);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(FileKeyViolationBean.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(TotalReducerViolationBean.class);
	
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		
    	Path[] inputPaths = FileUtil.getAllJsonNestedFilePath(job, inputpath);

		FileInputFormat.setInputPaths(job, inputPaths);
		FileOutputFormat.setOutputPath(job, new Path(outputpath));
				
		if(fs.exists(new Path(outputpath)))
		{
			fs.delete(new Path(outputpath), true);
		}
		
		job.waitForCompletion(true);	
		
		 Map<String, JsonViolationReport> jsonMap = readDataFromHdfs(conf,outputpath);
		 final Gson gson= new Gson();
		 final String jsonReport = gson.toJson(jsonMap);

		 LOGGER.info("Completed DataValidation");
		 LOGGER.info(JsonDataVaildationConstants.JSON_DV_REPORT + jsonReport);
    }
    
@SuppressWarnings("deprecation")
private static Map<String, JsonViolationReport> readDataFromHdfs(Configuration conf, String outputpath) throws IOException{
	  Map<String, JsonViolationReport> jsonMap = new HashMap<String, JsonViolationReport>();
	  
	  FileSystem fs = FileSystem.get(conf);
	  Path inFile = new Path(outputpath);
	  FileStatus[] ffs = fs.listStatus(inFile);
	  Path path = null;
	  Text key = null;  

	  JsonViolationReport report =null;
	  SequenceFile.Reader reader = null;
	  TotalReducerViolationBean value = null;
	  
	  List<FileViolationReport> missingViolationReport = new ArrayList<FileViolationReport>();
	  List<FileViolationReport> regexViolationReport = new ArrayList<FileViolationReport>();
	  List<FileViolationReport> dataKeyViolationReport = new ArrayList<FileViolationReport>();
	  List<FileViolationReport> jsonSchemaViolationReport = new ArrayList<FileViolationReport>();
	  List<FileViolationReport> nullViolationReport = new ArrayList<FileViolationReport>();
	  
	  Map<String,Long> missingViolation = new TreeMap<String, Long>();
	  Map<String,Long> regexViolation = new TreeMap<String, Long>();
	  Map<String,Long> dataKeyViolation = new TreeMap<String, Long>();
	  Map<String,Long> jsonSchemaViolation = new TreeMap<String, Long>();
	  Map<String,Long> nullViolation = new TreeMap<String, Long>();
	  
	  
	  	long totalViolation = 0;

		long totalDataTypeViolations = 0;
		long totalRegexCheckViolations = 0;
		long totalMissingViolations = 0;
		long totalSchemaViolations = 0;
		long totalNullViolations = 0;
	  
	  
	  for(FileStatus status:ffs){
		  path = status.getPath();
		  
		  if(!((path.getName().equals(JsonDataVaildationConstants.HADOOP_SUCCESS_FILES)) || path.getName().equals(JsonDataVaildationConstants.HADOOP_LOG_FILES))) { 		  
			  reader = new SequenceFile.Reader(fs, path, conf);	
			  key = new Text();
			  value = new TotalReducerViolationBean();
			  List <ReducerViolationBean > list =null;
			  while(reader.next(key, value)){
				  switch(key.toString()){
				  case JsonDataVaildationConstants.MISSING_KEY:
					  totalViolation = totalViolation + value.getTotalKeyViolation().get();
					  totalMissingViolations = totalMissingViolations +  value.getTotalKeyViolation().get();
					  list = value.getReducerViolationBeanList();
					  
					  for(ReducerViolationBean rvb:list){				
						  long size = rvb.getSize().get();
						  if(missingViolation.containsKey(rvb.getFileName().toString())){
							 long total =  missingViolation.get(rvb.getFileName().toString())+size;
							 missingViolation.put(rvb.getFileName().toString(), total);
						  }else{
							  missingViolation.put(rvb.getFileName().toString(), size);
						  }
						 
					  }
					  
					  for(Map.Entry<String, Long> missedViolation :missingViolation.entrySet()){
						  FileViolationReport fvr =  new FileViolationReport();
						  fvr.setFileName(missedViolation.getKey());  
						  fvr.setViolatedTupleinFile(missedViolation.getValue());
						  missingViolationReport.add(fvr);
					  }
					  
					  
					  break;
				  case JsonDataVaildationConstants.REGEX_KEY:
					  totalViolation = totalViolation + value.getTotalKeyViolation().get();
					  totalRegexCheckViolations = totalRegexCheckViolations +  value.getTotalKeyViolation().get();
					  list = value.getReducerViolationBeanList();
					  
					  for(ReducerViolationBean rvb:list){					 
						  long size = rvb.getSize().get();
						  if(regexViolation.containsKey(rvb.getFileName().toString())){
							 long total =  regexViolation.get(rvb.getFileName().toString())+size;
							 regexViolation.put(rvb.getFileName().toString(), total);
							}else{
							   regexViolation.put(rvb.getFileName().toString(), size);
							}
					  }
					  for(Map.Entry<String, Long> regexedViolation :regexViolation.entrySet()){
						  FileViolationReport fvr =  new FileViolationReport();
						  fvr.setFileName(regexedViolation.getKey());
						  fvr.setViolatedTupleinFile(regexedViolation.getValue());
						  regexViolationReport.add(fvr);
					  }
					  break;
				  case JsonDataVaildationConstants.DATA_KEY:
					  totalViolation = totalViolation + value.getTotalKeyViolation().get();
					  totalDataTypeViolations = totalDataTypeViolations +  value.getTotalKeyViolation().get();
					  list = value.getReducerViolationBeanList();
					  
					  for(ReducerViolationBean rvb:list){					 
						  long size = rvb.getSize().get();
						  if(dataKeyViolation.containsKey(rvb.getFileName().toString())){
							long total =  dataKeyViolation.get(rvb.getFileName().toString())+size;
							dataKeyViolation.put(rvb.getFileName().toString(), total);
						   }else{
							  dataKeyViolation.put(rvb.getFileName().toString(), size);
						   }	 
					  }
					  for(Map.Entry<String, Long> dataViolation :dataKeyViolation.entrySet()){
						  FileViolationReport fvr =  new FileViolationReport();
						  fvr.setFileName(dataViolation.getKey());
						  fvr.setViolatedTupleinFile(dataViolation.getValue());
						  dataKeyViolationReport.add(fvr);
					  }
					  break;
				  case JsonDataVaildationConstants.JSON_SCHEMA_KEY:
					  totalViolation = totalViolation + value.getTotalKeyViolation().get();
					  totalSchemaViolations = totalSchemaViolations +  value.getTotalKeyViolation().get();
					  list = value.getReducerViolationBeanList();
					  
					  for(ReducerViolationBean rvb:list){
						  long size = rvb.getSize().get();
						  if(jsonSchemaViolation.containsKey(rvb.getFileName().toString())){
							long total =  jsonSchemaViolation.get(rvb.getFileName().toString())+size;
							jsonSchemaViolation.put(rvb.getFileName().toString(), total);
						  }else{
							 jsonSchemaViolation.put(rvb.getFileName().toString(), size);
						  } 
					  }
					  for(Map.Entry<String, Long> schemaViolation :jsonSchemaViolation.entrySet()){
						  FileViolationReport fvr =  new FileViolationReport();
						  fvr.setFileName(schemaViolation.getKey());
						  fvr.setViolatedTupleinFile(schemaViolation.getValue());
						  jsonSchemaViolationReport.add(fvr);
					  }
					  break;
				  case JsonDataVaildationConstants.NULL_KEY:
					  totalViolation = totalViolation + value.getTotalKeyViolation().get();
					  totalNullViolations = totalNullViolations +  value.getTotalKeyViolation().get();
					  list = value.getReducerViolationBeanList();
 
					  for(ReducerViolationBean rvb:list){
						  long size = rvb.getSize().get();
						  if(nullViolation.containsKey(rvb.getFileName().toString())){
							long total =  nullViolation.get(rvb.getFileName().toString())+size;
							nullViolation.put(rvb.getFileName().toString(), total);
						  }else{
							  nullViolation.put(rvb.getFileName().toString(), size);
						  } 
					  }
					  for(Map.Entry<String, Long> nullViolated :nullViolation.entrySet()){
						  FileViolationReport fvr =  new FileViolationReport();
						  fvr.setFileName(nullViolated.getKey());
						  fvr.setViolatedTupleinFile(nullViolated.getValue());
						  nullViolationReport.add(fvr);
					  }
					  break;
				default:
					break;
				  }
			  }
		  	reader.close();
		  	}
		  }
	  
	  if(!missingViolationReport.isEmpty()){
		  report = new JsonViolationReport();
		  report.setTotalKeyViolation(totalMissingViolations);
		  report.setTotalViolation(totalViolation);
		  report.setFileViolationReport(missingViolationReport);
		  jsonMap.put(JsonDataVaildationConstants.MISSING_KEY, report);
	  } if (!regexViolationReport.isEmpty()){
		  report = new JsonViolationReport();
		  report.setTotalKeyViolation(totalRegexCheckViolations);
		  report.setTotalViolation(totalViolation);
		  report.setFileViolationReport(regexViolationReport);
		  jsonMap.put(JsonDataVaildationConstants.REGEX_KEY, report);
	  } if (!dataKeyViolationReport.isEmpty()){
		  report = new JsonViolationReport();
		  report.setTotalKeyViolation(totalDataTypeViolations);
		  report.setTotalViolation(totalViolation);
		  report.setFileViolationReport(dataKeyViolationReport);
		  jsonMap.put(JsonDataVaildationConstants.DATA_KEY, report);
	  }if (!jsonSchemaViolationReport.isEmpty()){
		  report = new JsonViolationReport();
		  report.setTotalKeyViolation(totalSchemaViolations);
		  report.setTotalViolation(totalViolation);
		  report.setFileViolationReport(jsonSchemaViolationReport);
		  jsonMap.put(JsonDataVaildationConstants.JSON_SCHEMA_KEY, report);
	  } if (!nullViolationReport.isEmpty()){
		  report = new JsonViolationReport();
		  report.setTotalKeyViolation(totalNullViolations);
		  report.setTotalViolation(totalViolation);
		  report.setFileViolationReport(nullViolationReport);
		  jsonMap.put(JsonDataVaildationConstants.NULL_KEY, report);
	  }

	  return jsonMap;
  }
}
