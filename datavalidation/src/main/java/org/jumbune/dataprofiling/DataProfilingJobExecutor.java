/*
 * 
 */
package org.jumbune.dataprofiling;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.jumbune.common.beans.FieldProfilingBean;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.dataprofiling.utils.DataProfilingConstants;

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
		String inputPath = otherArgs[0];
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
		
		Job job = new Job(conf,DataProfilingConstants.JOB_NAME);
		
		job.setJarByClass(DataProfilingJobExecutor.class);
		job.setMapperClass(DataProfilingMapper.class);
		
		job.setCombinerClass(DataProfilingReducer.class);
		job.setReducerClass(DataProfilingReducer.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
    	Path[] inputPaths = FileUtil.getAllNestedFilePath(job, inputPath);		
		
		TextInputFormat.setInputPaths(job, inputPaths);
		SequenceFileOutputFormat.setOutputPath(job, new Path(outputPath));
		
		job.waitForCompletion(true);
		LOGGER.debug("Job completed , now going to read the result from hdfs");
		Set<CriteriaBasedDataProfiling> criteriaBasedDataProfilings = readJobOutputFromHdfs(conf,outputPath,dataProfilingBean);
		final Gson dpReportGson = new GsonBuilder().disableHtmlEscaping().create();

		final String jsonString = dpReportGson.toJson(criteriaBasedDataProfilings);
		LOGGER.info(DataProfilingConstants.DATA_PROFILING_REPORT + jsonString);
	}
	
	private static Set<CriteriaBasedDataProfiling> readJobOutputFromHdfs(Configuration configuration , String outputPath,DataProfilingBean dataProfilingBean) throws IOException{
		
		LOGGER.debug("Inside read the result from hdfs method");
		FileSystem fs = FileSystem.get(configuration);
		Path inFile = new Path(outputPath);
		FileStatus[] fss = fs.listStatus(inFile);
		Path path = null;
		Text key = null;
		IntWritable value = null;
		String unMatchedValue = null;
		SequenceFile.Reader reader = null;
		CriteriaBasedDataProfiling criteriaBasedDataProfiling = null ;
		Set<CriteriaBasedDataProfiling> matchedList = new HashSet<CriteriaBasedDataProfiling>();
		Set<CriteriaBasedDataProfiling> unMatchedList = new HashSet<CriteriaBasedDataProfiling>();
		List<FieldProfilingBean> fieldProfilingBeansList = dataProfilingBean.getFieldProfilingRules();
		for (FileStatus status : fss) {
			path = status.getPath();
			if (!((path.getName().equals(DataProfilingConstants.HADOOP_SUCCESS_FILES)) || (path.getName()
					.equals(DataProfilingConstants.HADOOP_LOG_FILES)))) {
				LOGGER.debug("Going to read the file : [" +path.getName()+"] at path ["+path+"]");
				reader = new SequenceFile.Reader(fs, path, configuration);
				key = new Text();
				value = new IntWritable();
				
			
			while (reader.next(key, value)) {
				String[] keyArray = key.toString().split("-");
				int fieldNumber = Integer.parseInt(keyArray[1]);
				criteriaBasedDataProfiling = new CriteriaBasedDataProfiling();
				criteriaBasedDataProfiling.setFieldNo(fieldNumber);
				if (keyArray[0].equalsIgnoreCase("matched")) {
					criteriaBasedDataProfiling.setMatched(Integer.toString(value.get()));
					matchedList.add(criteriaBasedDataProfiling);		
				} else {
					criteriaBasedDataProfiling.setUnMatched(Integer.toString(value.get()));
					unMatchedList.add(criteriaBasedDataProfiling);
				}

				for (FieldProfilingBean fieldProfilingBean : fieldProfilingBeansList) {

					String operand = fieldProfilingBean.getDataProfilingOperand();
					if (operand != null && !operand.isEmpty()) {
						if (operand.equalsIgnoreCase(DataProfilingConstants.GREATER_THAN_EQUAL_TO)) {
							operand = DataProfilingConstants.GREATERTHANEQUALTO;
						} else {
							operand = DataProfilingConstants.LESSTHANEQUALTO;
						}
						if (Integer.toString(criteriaBasedDataProfiling.getFieldNo())
								.equalsIgnoreCase(Integer.toString(fieldProfilingBean.getFieldNumber()))) {
							String definedRule = operand
									+ String.format("%.3f", fieldProfilingBean.getComparisonValue());
							criteriaBasedDataProfiling.setRule(definedRule);
						}
					}
				}
			}
			reader.close();
		}
		}
		Set<CriteriaBasedDataProfiling> criteriaBasedDataProfilings = merge(matchedList, unMatchedList);
		return criteriaBasedDataProfilings;
	}
	
	
	
	/**
	 * Merges the match and unmatch list and returns the final list containing match ,unmatch corresponding to field number.
	 *
	 * @param matchedList the matched list
	 * @param unMatchedList the un matched list
	 * @return the list containing both matched and unmatched values
	 */
	private static Set<CriteriaBasedDataProfiling> merge(Set<CriteriaBasedDataProfiling> matchedList, Set<CriteriaBasedDataProfiling> unMatchedList) {
		Set<CriteriaBasedDataProfiling> result = null;
		
	       if(matchedList.size() > unMatchedList.size()) {
	    	  result = intersectAndMerge(matchedList, unMatchedList);
	       } else {
	    	   result = intersectAndMerge(unMatchedList, matchedList);
	       }       
	       
	       matchedList.removeAll(result); 
	       unMatchedList.removeAll(result);
	       result.addAll(matchedList);
	       result.addAll(unMatchedList);  
	       return result;		
		}
	
	/**
	 * This method removes the common elements in both sets and merge them into one.
	 *
	 * @param bigger the bigger
	 * @param smaller the smaller
	 * @return the sets the
	 */
	private static Set<CriteriaBasedDataProfiling> intersectAndMerge(Set<CriteriaBasedDataProfiling> bigger, Set<CriteriaBasedDataProfiling> smaller) {
		Set<CriteriaBasedDataProfiling> intersection = new HashSet<CriteriaBasedDataProfiling>();
		CriteriaBasedDataProfiling containedJson;
	for (CriteriaBasedDataProfiling json : bigger) {
		  for(CriteriaBasedDataProfiling smallJson : smaller){
				if (smallJson.equals(json)) {
					containedJson = getElementFromSet(smaller, json);
					if (containedJson.getMatched() == null) {
						containedJson.setMatched(json.getMatched());
					} else {
						containedJson.setUnMatched(json.getUnMatched());
					}				
					intersection.add(containedJson);
				}
			
		  }
		}
		return intersection;
	}
	
	/**
	 * Gets the element from set.
	 *
	 * @param jsons the jsons
	 * @param element the element
	 * @return the element from set
	 */
	private static CriteriaBasedDataProfiling getElementFromSet(Set<CriteriaBasedDataProfiling> jsons, CriteriaBasedDataProfiling element) {
		for(CriteriaBasedDataProfiling json : jsons) {
			if (element.equals(json)) {
				return json;
			}
		}
		return null;		
	}
	
		
	
	
	/**
	 * The Class CriteriaBasedDataProfiling is a bean containing the result details of criteria based data profiling.
	 */
	public static class CriteriaBasedDataProfiling {
		 
		/** The field no. */
		private int fieldNo;
		
		/** The matched. */
		private String matched = "0";
		
		/** The un matched. */
		private String unMatched = "0" ;
		
		/** The rule. */
		private String rule ;
		
		/**
		 * Gets the field no.
		 *
		 * @return the field no
		 */
		public int getFieldNo() {
			return fieldNo;
		}

		/**
		 * Sets the field no.
		 *
		 * @param fieldNo the new field no
		 */
		public void setFieldNo(int fieldNo) {
			this.fieldNo = fieldNo;
		}

		/**
		 * Gets the matched.
		 *
		 * @return the matched
		 */
		public String getMatched() {
			return matched;
		}

		/**
		 * Sets the matched.
		 *
		 * @param matched the new matched
		 */
		public void setMatched(String matched) {
			this.matched = matched;
		}

		/**
		 * Gets the un matched.
		 *
		 * @return the un matched
		 */
		public String getUnMatched() {
			return unMatched;
		}

		/**
		 * Sets the un matched.
		 *
		 * @param unMatched the new un matched
		 */
		public void setUnMatched(String unMatched) {
			this.unMatched = unMatched;
		}

		/**
		 * Gets the rule.
		 *
		 * @return the rule
		 */
		public String getRule() {
			return rule;
		}

		/**
		 * Sets the rule.
		 *
		 * @param rule the new rule
		 */
		public void setRule(String rule) {
			this.rule = rule;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + fieldNo;
			result = prime * result + ((rule == null) ? 0 : rule.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CriteriaBasedDataProfiling other = (CriteriaBasedDataProfiling) obj;
			if (fieldNo != other.fieldNo)
				return false;
			if (rule == null) {
				if (other.rule != null)
					return false;
			} else if (!rule.equals(other.rule))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "CriteriaBasedDataProfiling [fieldNo=" + fieldNo + ", matched=" + matched + ", unMatched="
					+ unMatched + ", rule=" + rule + "]";
		}
	}
}
