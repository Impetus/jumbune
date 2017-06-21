package org.jumbune.utils.exception;


import org.jumbune.utils.ErrorCodesMessageInterface;

public enum ExtendedErrorCodesAndMessages implements ErrorCodesMessageInterface{


	MESSAGE_REMOVE_CRON_ENTRY(200, "Could not remove task entry from cron file"), 
	COULD_NOT_WRITE_CRON_TO_FILE(201, "Could not write cron info to a file may be some file permissions issue"), 
	COULD_NOT_SET_FILE_TO_CRON(202,	"Could not set modified file to crontab. So the current job is not be scheduled"),
	COULD_NOT_READ_CRON_FILE(203, "Could not read original crontab file for modifying it "),
	SCHEDULING_JSON_PATH_NOT_EXIST(204, "Scheduling job destination path does not exist so could not copy the user's yaml file "), 
	COULD_NOT_COPY_SCHEDULING_YAML(205, "Could not copy the user's yaml file to destination scheduling folder"), 
	COULD_NOT_COPY_SCHEDULING_SCRIPT(206, "Could not copy the execution script for scheduling job"),
	COULD_WRITE_SCHEDULING_RESULT(207, "Could write result of scheduling job!"),
	COULD_NOT_COPY_SCHEDULING_JAR(208, "Could not copy jar files for scheduling!!"),
	COULD_NOT_READ_SCHEDULE_REPORT(209, "Could not read scheduled job reports"),

	// Web errors/exceptions
	UNABLE_TO_LOAD_JSON(410, "Could not save given json file on server"), 
	UNABLE_TO_LOAD_JAR(411, "Could not save given jar file on server"), 
	UNABLE_TO_PROCESS(412, "Could not process further as some other process is currently under process."), 
	UNABLE_TO_FIND_SCH_JSON(413, "Scheduled job json file could not be found"),
	UNABLE_TO_REDIRECT_TO_RESULT(414, "Unable to re-direct to result page for scheduling jobs"),
	UNABLE_TO_REDIRECT_TO_HOME(415, "Unable to re-direct to home page"), 
	INVALID_JSON(601, "provided yaml contains errors"),
	LOG_ANALYSER_FAILED(602,"ERROR : LogAnalyser Failed !!! Error occured during Debug Analysis"),
	IO_OPERATION_FAILED(603,"ERROR : I/O Operation failed !!!"),
	COULD_NOT_EXECUTE_PROGRAM(604,"Could not execute program currently !!! Please delete contents of tmp folder and try again.");
	// Scheduling related error messages

	private final int code;
	private final String description;

	/**
	 * Instantiates a new error codes and messages.
	 *
	 * @param code the code
	 * @param description the description
	 */
	private ExtendedErrorCodesAndMessages(int code, String description) {
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
