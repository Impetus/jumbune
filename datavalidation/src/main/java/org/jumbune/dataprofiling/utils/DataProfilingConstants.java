package org.jumbune.dataprofiling.utils;


/**
 * The Interface DataProfilingConstants .
 */
public interface DataProfilingConstants {
	
	String OUTPUT_DIR_PATH = "/tmp/jumbune/dpreport";
	
	String RECORD_SEPARATOR = "recordSeparator";
	
	String JOB_NAME = "Data Profiling" ;
	
	String DATA_PROFILING = "DATA_PROFILING" ;
	
	String DP_MAIN_CLASS = "org.jumbune.dataprofiling.DataProfilingJobExecutor";
	
	String DP_NO_CRITERIA_MAIN_CLASS = "org.jumbune.dataprofiling.DataProfNoCriteriaJobExecutor";
	
	String HADOOP_LOG_FILES = "_logs";

	String HADOOP_SUCCESS_FILES = "_SUCCESS";
	
	String DATA_PROFILING_BEAN = "dpBeanString" ;
	
	String DATA_PROFILING_REPORT = "dataprofilingreport:";

	String GREATER_THAN_EQUAL_TO = "GREATER_THAN_EQUAL_TO";

	String LESS_THAN_EQUAL_TO = "LESS_THAN_EQUAL_TO";
	
	String MATCHED = "MATCHED-";
	
	String UNMATCHED = "UNMATCHED-";
	
	String ZERO = "0" ;
	
	String GREATERTHANEQUALTO = ">=" ;
	
	String LESSTHANEQUALTO = "<=" ;
	
	String RULE  = "Rule";
	
	String DATA_PROFILES = "dataProfiles";
	
	String PROFILE = "Profile_";
	
	String JSON = ".json";

	String CB = "CB";
	
	String  TEXTINPUTFORMAT_RECORD_DELIMITER = "textinputformat.record.delimiter";
	

}
