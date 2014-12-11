package org.jumbune.datavalidation;

import java.util.HashMap;
import java.util.Map;



/**
 * This is the interface for storing Constants related to data validation module.
 * 


 * 
 */
public interface DataValidationConstants {

	/** The minus one. */
	int MINUS_ONE = -1;
	
	/** The tokens for dv report. */
	int TOKENS_FOR_DV_REPORT = 4;
	
	/** The null check. */
	String NULL_CHECK = "nullCheck";
	
	/** The data type check. */
	String DATA_TYPE_CHECK = "fieldType";
	
	/** The regex expression check. */
	String REGEX_EXPRESSION_CHECK = "regexExpression";
	
	/** The num of occurrences check. */
	String NUM_OF_OCCURRENCES_CHECK = "numOfOccurrences";
	
	/** The is validation enabled. */
	String IS_VALIDATION_ENABLED = "isVa1lidationEnabled";
	
	/** The num of fields check. */
	String NUM_OF_FIELDS_CHECK = "Number of Fields";
	
	/** The skip check. */
	String SKIP_CHECK = " ";
	
	/** The not null. */
	String NOT_NULL = "notNull";
	
	/** The null. */
	String NULL = "null";
	
	/** The empty string. */
	String EMPTY_STRING = "";
	
	/** The hadoop log files. */
	String HADOOP_LOG_FILES = "_logs";
	
	/** The hadoop success files. */
	String HADOOP_SUCCESS_FILES = "_SUCCESS";
	
	/** The job name. */
	String JOB_NAME = "data validation";
	
	/** The data validation bean string. */
	String DATA_VALIDATION_BEAN_STRING = "dvBeanString";
	
	/** The data validation bean. */
	String DATA_VALIDATION_BEAN = "dataValidation";
	
	/** The record separator. */
	String RECORD_SEPARATOR = "recordSeparator";
	
	/** The output dir path. */
	String OUTPUT_DIR_PATH = "/tmp/jumbune/dvreport";
	
	/** The hdfs url prefix. */
	String HDFS_URL_PREFIX = "hdfs://";
	
	/** The hdfs url suffix. */
	String HDFS_URL_SUFFIX = ":9000/";
	
	/** The dv report. */
	String DV_REPORT = "dvr:";
	
	/** The slave file loc. */
	String SLAVE_FILE_LOC = "slaveFileLoc";

	/**
	 * The Enum DataTypes.
	 */
	enum DataTypes {
		
		/** The int_type. */
		int_type, 
 /** The long_type. */
 long_type, 
 /** The float_type. */
 float_type, 
 /** The double_type. */
 double_type
	}

	/** VALIDATION_CHECK_MAP - the map containing various checks to be applied. */
	@SuppressWarnings("serial")
	Map<Integer, String> VALIDATION_CHECK_MAP = new HashMap<Integer, String>() {

		{
			put(1, "Null Check");
			put(2, "Data Type");
			put(THREE, "Regex");

		}
	};

	
	/** The user name property. */
	String USER_NAME_PROPERTY = "user.name";
	
	/** The field separator. */
	String FIELD_SEPARATOR = "fieldSeparator";
	
	/** The user. */
	String USER = "user";
	
	/** The default record separator. */
	String DEFAULT_RECORD_SEPARATOR = "\n";
	
	/** The default field separator. */
	String DEFAULT_FIELD_SEPARATOR = ",";
	
	/** The hdfs input path. */
	String HDFS_INPUT_PATH = "hdfsInputPath";
	
	/** The file separator. */
	String FILE_SEPARATOR = "/";
	
	/** The space token. */
	String SPACE_TOKEN = " ";
	
	/** The last index value. */
	String LAST_INDEX_VALUE = "lastIndexValue";
	
	/** The table name attribute. */
	String TABLE_NAME_ATTRIBUTE = "--table";
	
	/** The table name. */
	String TABLE_NAME = "tableName";
	
	/** The last value attribute. */
	String LAST_VALUE_ATTRIBUTE = "--last-value ";
	
	/** The last modified attribute. */
	String LAST_MODIFIED_ATTRIBUTE = "lastmodified";
	
	/** The append mode attribute. */
	String APPEND_MODE_ATTRIBUTE = "append";
	
	/** The check column. */
	String CHECK_COLUMN = " --check-column ";
	
	/** The incremental. */
	String INCREMENTAL = " --incremental ";
	
	/** The last value. */
	String LAST_VALUE = " --last-value ";
	
	
	/** The java file already exists exception. */
	String JAVA_FILE_ALREADY_EXISTS_EXCEPTION = ".java' already exists";
	
	/** The import. */
	String IMPORT = "import";
	
	/** The no new records. */
	String NO_NEW_RECORDS = "No new rows detected since last import.";
	
	/** The zero records fetched. */
	String ZERO_RECORDS_FETCHED = "zeroRecordsFetched";
	
	/** The null value. */
	String NULL_VALUE = "null";
	
	/** The target dir attribute. */
	String TARGET_DIR_ATTRIBUTE = " --target-dir ";
	
	int ZERO_CROSS_FF = 0xFF;
 	
	int EIGHT = 8 ;
	
	int TWENTY_FOUR= 24;

	int SIXTEEN = 16;

	int THREE = 3;

	int FOUR = 4;

	int ONE_ZERO_TWO_FOUR = 1024;

	int ONE = 1;

	int TEN = 10;
	
}
