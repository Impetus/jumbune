package org.jumbune.debugger.instrumentation.utils;


/**
 * The Interface MessageConstants contains constants for debugger.
 */
public interface MessageConstants {
	
	/** The interface not instrument. */
	int INTERFACE_NOT_INSTRUMENT = 4000;
	
	/** The class already instrumented. */
	int CLASS_ALREADY_INSTRUMENTED = 4001;
	
	/** The class being instrumented. */
	int CLASS_BEING_INSTRUMENTED = 4002;
	
	/** The not marked as instrumented. */
	int NOT_MARKED_AS_INSTRUMENTED = 4003;
	
	/** The marked as instrumented. */
	int MARKED_AS_INSTRUMENTED = 4004;
	
	/** The archive being instrumented. */
	int ARCHIVE_BEING_INSTRUMENTED = 4005;
	
	/** The archive could not be instrumented. */
	int ARCHIVE_COULD_NOT_BE_INSTRUMENTED = 4006;
	
	/** The archive being instrumented to. */
	int ARCHIVE_BEING_INSTRUMENTED_TO = 4007;
	
	/** The input strem not closed. */
	int INPUT_STREM_NOT_CLOSED = 4008;
	
	/** The output stream not closed. */
	int OUTPUT_STREAM_NOT_CLOSED = 4009;
	
	/** The instrumentation time taken. */
	int INSTRUMENTATION_TIME_TAKEN = 4010;
	
	/** The archive will be instrumented to. */
	int ARCHIVE_WILL_BE_INSTRUMENTED_TO = 4011;
	
	/** The adding class to archive. */
	int ADDING_CLASS_TO_ARCHIVE = 4012;
	
	/** The instrument using. */
	int INSTRUMENT_USING = 4013;
	
	/** The instrumented to class. */
	int INSTRUMENTED_TO_CLASS = 4014;
	
	/** The adding class member. */
	int ADDING_CLASS_MEMBER = 4015;
	
	/** The log instrumenting method. */
	int LOG_INSTRUMENTING_METHOD = 4016;
	
	/** The not implementing patternvalidator. */
	int NOT_IMPLEMENTING_PATTERNVALIDATOR = 4017;

	// Libraries
	/** The adding file to archive. */
	int ADDING_FILE_TO_ARCHIVE = 4100;
	
	/** The excluding file from archive. */
	int EXCLUDING_FILE_FROM_ARCHIVE = 4101;
	
	/** The file folder doesnt exist. */
	int FILE_FOLDER_DOESNT_EXIST = 4102;

	// Adapter for if blocks, loops
	/** The log multiple if condition or. */
	int LOG_MULTIPLE_IF_CONDITION_OR = 4200;
	
	/** The log multiple if condition and. */
	int LOG_MULTIPLE_IF_CONDITION_AND = 4201;
	
	/** The log multiple if condition not found. */
	int LOG_MULTIPLE_IF_CONDITION_NOT_FOUND = 4202;
	
	/** The log goto found. */
	int LOG_GOTO_FOUND = 4203;
	
	/** The log goto not found. */
	int LOG_GOTO_NOT_FOUND = 4204;
	
	/** The log before if. */
	int LOG_BEFORE_IF = 4205;
	
	/** The log after if. */
	int LOG_AFTER_IF = 4206;
	
	/** The log in if. */
	int LOG_IN_IF = 4207;
	
	/** The log loop found. */
	int LOG_LOOP_FOUND = 4208;
	
	/** The log in else. */
	int LOG_IN_ELSE = 4209;
	
	/** The log in elseif. */
	int LOG_IN_ELSEIF = 4210;

	// cleanup setup
	/** The log cleanup method found. */
	int LOG_CLEANUP_METHOD_FOUND = 4250;
	
	/** The log setup method found. */
	int LOG_SETUP_METHOD_FOUND = 4251;
	
	/** The log cleanup method not found. */
	int LOG_CLEANUP_METHOD_NOT_FOUND = 4252;
	
	/** The log setup method not found. */
	int LOG_SETUP_METHOD_NOT_FOUND = 4253;
	
	/** The log adding cleanup methodd. */
	int LOG_ADDING_CLEANUP_METHODD = 4254;
	
	/** The log adding setup method. */
	int LOG_ADDING_SETUP_METHOD = 4255;
	
	/** The log modify cleanup methodd. */
	int LOG_MODIFY_CLEANUP_METHODD = 4256;
	
	/** The log modify setup method. */
	int LOG_MODIFY_SETUP_METHOD = 4257;
	
	/** The log adding ctx threadlocal. */
	int LOG_ADDING_CTX_THREADLOCAL = 4258;
	
	/** The log removing ctx threadlocal. */
	int LOG_REMOVING_CTX_THREADLOCAL = 4259;
	
	/** The log add pattern compile. */
	int LOG_ADD_PATTERN_COMPILE = 4260;
	
	/** The log initialize pattern validator. */
	int LOG_INITIALIZE_PATTERN_VALIDATOR = 4261;
	
	/** The log load logger. */
	int LOG_LOAD_LOGGER = 4262;

	// job related
	/** The job submission found. */
	int JOB_SUBMISSION_FOUND = 4301;
	
	/** The main method found. */
	int MAIN_METHOD_FOUND = 4302;
	
	/** The class instrumented for profiling. */
	int CLASS_INSTRUMENTED_FOR_PROFILING = 4303;
	
	/** The log owner is job. */
	int LOG_OWNER_IS_JOB = 4304;

	// inside map/reduce method
	/** The log mapreduce method entry. */
	int LOG_MAPREDUCE_METHOD_ENTRY = 4400;
	
	/** The log mapreduce method exit. */
	int LOG_MAPREDUCE_METHOD_EXIT = 4401;
	
	/** The log mapreduce ctxwrite call. */
	int LOG_MAPREDUCE_CTXWRITE_CALL = 4402;
	
	/** The log adding regex validation call. */
	int LOG_ADDING_REGEX_VALIDATION_CALL = 4403;
	
	/** The log method entry. */
	int LOG_METHOD_ENTRY = 4404;
	
	/** The log method exit. */
	int LOG_METHOD_EXIT = 4405;

	// Log messages to be injected
	/** The entered mapreduce. */
	int ENTERED_MAPREDUCE = 4800;
	
	/** The exiting mapreduce. */
	int EXITING_MAPREDUCE = 4801;
	
	/** The mapper context write. */
	int MAPPER_CONTEXT_WRITE = 4802;
	
	/** The reducer context write. */
	int REDUCER_CONTEXT_WRITE = 4803;
	
	/** The msg in if. */
	int MSG_IN_IF = 4804;
	
	/** The msg in elseif. */
	int MSG_IN_ELSEIF = 4805;
	
	/** The msg in else. */
	int MSG_IN_ELSE = 4806;
	
	/** The msg before if. */
	int MSG_BEFORE_IF = 4807;
	
	/** The msg after if. */
	int MSG_AFTER_IF = 4808;
	
	/** The loop execution. */
	int LOOP_EXECUTION = 4809;
	
	/** The validation key value. */
	int VALIDATION_KEY_VALUE = 4810;
	
	/** The msg before switch. */
	int MSG_BEFORE_SWITCH = 4812;
	
	/** The msg after switch. */
	int MSG_AFTER_SWITCH = 4813;
	
	/** The msg in switchcase. */
	int MSG_IN_SWITCHCASE = 4814;
	
	/** The msg out switchcase. */
	int MSG_OUT_SWITCHCASE = 4815;
	
	/** The msg partition info. */
	int MSG_PARTITION_INFO = 4816;
	
	/** The msg before ternary. */
	int MSG_BEFORE_TERNARY = 4817;
	
	/** The msg after ternary. */
	int MSG_AFTER_TERNARY = 4818;
	
	/** The msg return if. */
	int MSG_RETURN_IF = 4819;
	
	/** The entered method. */
	int ENTERED_METHOD = 4820;
	
	/** The exiting method. */
	int EXITING_METHOD = 4821;
	
	/** The entered loop. */
	int ENTERED_LOOP = 4822;
	
	/** The exiting loop. */
	int EXITING_LOOP = 4823;
}