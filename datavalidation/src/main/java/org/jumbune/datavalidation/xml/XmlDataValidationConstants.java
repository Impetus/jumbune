package org.jumbune.datavalidation.xml;

/**
 * This is the interface for storing Constants related to data validation
 * module.
 * 
 * 
 * 
 * 
 */
public interface XmlDataValidationConstants {

	/** The minus one. */
	int MINUS_ONE = -1;
	
	/** The tokens for xml dv report. */
	int TOKENS_FOR_XML_DV_REPORT = 5;

	String PIPE_SEPARATOR = "|";

	String SEPARATOR_UNDERSCORE = "_";

	/** The is validation enabled. */
	String IS_VALIDATION_ENABLED = "isVa1lidationEnabled";

	/** User defined null check */
	String USER_DEFINED_NULL_CHECK = "NullCheck";

	/** User defined Data type check */
	String USER_DEFINED_DATA_TYPE = "DataType";

	/** User defined regular expression check */
	String USER_DEFINED_REGEX_CHECK = "Regex";

	/** Fatal Error */
	String FATAL_ERROR = "FatalError";

	/** Other XML Errors */
	String OTHER_XML_ERROR = "OtherXMLError";

	/** The skip check. */
	String SKIP_CHECK = " ";

	/** The not null. */
	String NOT_NULL = "notNull";

	/** The null. */
	String NULL = "null";

	/** The empty string. */
	String EMPTY_STRING = "";

	String XML_LITERAL = ".xml";

	String XSD_LITERAL = ".xsd";

	/** The hadoop log files. */
	String HADOOP_LOG_FILES = "_logs";

	String CACHED_SCHEMA = "jumbune.cache.schema";

	/** The hadoop success files. */
	String HADOOP_SUCCESS_FILES = "_SUCCESS";

	/** The job name. */
	String XML_JOB_NAME = "xml data validation";

	/** The data validation bean string. */
	String DATA_VALIDATION_BEAN_STRING = "dvBeanString";

	/** The data validation bean. */
	String DATA_VALIDATION_BEAN = "dataValidation";

	String HEADER_START_TAG = "headerstarttag";

	String HEADER_END_TAG = "headerendtag";

	String START_TAG = "starttag";

	String END_TAG = "endtag";

	String XML_START_ELEMENT_TAG = "<";

	String XML_END_ELEMENT_TAG = "</";

	String XML_ELEMENT_END_TAG = ">";

	String XML_HEADER_START_TAG = "<?xml";

	String XML_HEADER_END_TAG = "?>";

	/** The output dir path. */
	String OUTPUT_DIR_PATH = "/tmp/jumbune/dvreport/xml";

	/** The output dir path. */
	int MAX_VIOLATIONS = 1000;

	/** The hdfs url prefix. */
	String HDFS_URL_PREFIX = "hdfs://";

	/** The hdfs url suffix. */
	String HDFS_URL_SUFFIX = ":9000/";

	/** The dv report. */
	String XML_DV_REPORT = "xdvr:";

	/** The slave file loc. */
	String SLAVE_FILE_LOC = "slaveFileLoc";

	/** The hdfs input path. */
	String HDFS_INPUT_PATH = "hdfsInputPath";

	/** The file separator. */
	String DIR_SEPARATOR = "/";

	/** The file separator. */
	String PERIOD = ".";

	/** The space token. */
	String SPACE_TOKEN = " ";

	int ZERO_CROSS_FF = 0xFF;

	int EIGHT = 8;

	int TWENTY_FOUR = 24;

	int SIXTEEN = 16;

	int ONE = 1;

	int TWO = 2;

	int THREE = 3;

	int FOUR = 4;

	int FIVE = 5;

	int ONE_ZERO_TWO_FOUR = 1024;

	int TEN = 10;

	String VALIDATE_MATRIX = "validateMatrix";

	int NULL_MATRIX = 0;

	int DATA_TYPE_MATRIX = 1;

	int REGEX_MATRIX = 2;
	
	/** The XML Schema  Constants */
	
	String SCHEMA = "schema";
	String COMPLEX_TYPE = "complexType";
	String SEQUENCE = "sequence";
	String ATT_GROUP = "attGroup";
	String ATTRIBUTE = "attribute";
	String SIMPLE_TYPE = "simpleType";
	String LIST = "list";
	String UNION = "union";
	String RESTRICTION = "restriction";
	String NOTATION = "notation";
	String EXTENSION = "extension";
	String REDEFINE = "redefine";
	String SIMPLE_CONTENT = "simpleContent";
	String COMPLEX_CONTENT = "complexContent";
	String MIN_LENGTH= "minLength";
	String MAX_LENGTH = "maxLength";
	String ELEMENT = "element";
	String PATTERN = "pattern";
	String ENUMERATION = "enumeration";
	String MIN_INCLUSIVE = "minInclusive";
	String MIN_EXCLUSIVE = "minExclusive";
	String MAX_INCLUSIVE = "maxInclusive";
	String MAX_EXCLUSIVE = "maxExclusive";
	String ALL = "all";
	String GROUP = "group";
	String WHITE_SPACE = "whiteSpace";
	String TOTAL_DIGITS = "totalDigits";
	String FRACTION_DIGITS = "fractionDigits";
	String LENGTH = "length";
	
	/** The XML Schema  Constants */
}
