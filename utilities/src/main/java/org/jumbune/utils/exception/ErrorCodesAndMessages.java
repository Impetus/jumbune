package org.jumbune.utils.exception;

/**
 * The Enum ErrorCodesAndMessages is used to provide the error messages.
 */
public enum ErrorCodesAndMessages {
	YAML_NOT_FOUND(0, "Yaml file is not found."), YAMLPROPERTY_NOT_CREATED(1, "Yaml property can not be created."), EXECUTIONMESSAGEF_FILE_NOT_FOUND(
			2, "Execution message file is not found"), INPUTJAR_FILE_NOT_FOUND(3, "Input Jar file is not found"), YAMLPROPERTY_INTRUMENTATION_NOT_FOUND(
			4, "Yaml property instrumentaion is not defined in given FrameworkProperty file."), YAMLPROPERTY_LOGLEVEL_NOT_FOUND(5,
			"Log level are not defined under Yaml property intrumentation in given FrameworkProperty file."), JOBS_NOT_MATCH(6,
			"jobs configured from yaml or console are not in given jar file."), FILE_PATH_FORMAT_NOT_CORRECT(7,
			"Specified file path not in correct format"), PROPERTY_FILE_NOT_FOUND(8, "Property file could not be loaded"), PROFILING_FORMAT_NOT_CORRECT(
			9, "Profiling format should only be byte format, use format=b for setting it"), MESSAGEfILE_NOT_FOUND(10,
			"Message file is not found: instrumentationMessages.en"), METHOD_BYTE_CODE(11, "Adding a parameter in method requires 3 values"
			+ "opCode, owner, desc in given order only"), LOG_FORMAT_ERROR_MESSAGE(12, "Error Parsing Log File"), DIRECTORY_PATH_NOT_CORRECT(12,
			"directory path is not correct."), MESSAGE_BEAN_UNREGISTERED(13, "Could not register jmx bean: "), MESSAGE_FILE_PATH_FORMAT_NOT_CORRECT(
			14, "Specified file path not in correct format"), MESSAGE_JOBS_NOT_MATCH(15, "Specified jobs in yaml are not present in given jar"), MESSAGE_PARTITIONER_NOT_SET(
			16, "Could not set partitioner for debugging its performance"), MESSAGE_JOB_ALREADY_RUNNING(99,
			"Application is already running, there can only be one instance of application running at a time."),

	// Data validation related messages
	INVALID_NUMBERS_OF_ARGUMENTS(101, "Invalid number of arguments while executing DataValidationJobExecutor"), ERROR_EXECUTING_DV(102,
			"Exception ocurred during executing data validation"), 

	COULD_NOT_CREATE_REPORTS(300, "Could not copy reports"),

	COULD_NOT_CREATE_DIRECTORY(350, "Could not create directory"),
	

	// Web errors/exceptions
	UNABLE_TO_LOAD_YAML(410, "Could not save given yaml file on server"), UNABLE_TO_LOAD_JAR(411, "Could not save given jar file on server"), UNABLE_TO_PROCESS(
			412, "Could not process further as some other process is currently under process."),  UNABLE_TO_REDIRECT_TO_HOME(
			415, "Unable to re-direct to home page"), INVALID_YAML(601, "provided yaml contains errors"),

	LOG_ANALYSER_FAILED(602,"ERROR : LogAnalyser Failed !!! Error occured during Debug Analysis"),
	
	IO_OPERATION_FAILED(603,"ERROR : I/o Operation failed !!!"), 
	COULD_NOT_EXECUTE_PROGRAM(604,"Could not execute program currently !!! Please delete contents of tmp folder and try again.");
	
	
	private final int code;
	private final String description;

	/**
	 * Instantiates a new error codes and messages.
	 *
	 * @param code the code
	 * @param description the description
	 */
	private ErrorCodesAndMessages(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * This method provides string implementation of the object
	 */
	public String toString() {
		return code + ": " + description;
	}

}
