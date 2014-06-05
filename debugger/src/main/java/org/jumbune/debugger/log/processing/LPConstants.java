package org.jumbune.debugger.log.processing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;


/**
 * This is the interface for storing Constants.
 * 
 */
@SuppressWarnings("serial")
public interface LPConstants {
	int SEVEN = 7;
	int SIX = 6;
	int FIVE = 5;
	int FOUR = 4;
	int THREE = 3;
	int TWO = 2;
	int ONE = 1;
	String DEBUGGER_SUMMARY="debuggerSummary";
	float SAMPLING_FRACTION=0.1f;
	char DOT = '.';
	char HASH = '#';
	int MAPPER_REDUCER_INDEX = 1;
	int METHOD_INDEX = 2;
	int JOB_INDEX = 3;
	int INSTANCE_INDEX = 4;
	int MESSAGE_INDEX = 5;
	int KEY_TYPE_INDEX = 6;
	int KEY_VALUE_INDEX = 7;
	String UNDERSCORE = "_";
	String PIPE_SEPARATOR = "\\|";
	String FILE_SEPARATOR = "/";
	String COMMA = ",";
	String EMPTY_STRING = "";
	String LOG = "log";
	String ENTERED_MAP_MESSAGE = MessageFormat.format(InstrumentationMessageLoader.getMessage(MessageConstants.ENTERED_MAPREDUCE), "map");
	String EXITING_MAP_MESSAGE = MessageFormat.format(InstrumentationMessageLoader.getMessage(MessageConstants.EXITING_MAPREDUCE), "map");
	String ENTERING_IF_MESSAGE = InstrumentationMessageLoader.getMessage(MessageConstants.MSG_BEFORE_IF);
	String EXITED_IF_MESSAGE = InstrumentationMessageLoader.getMessage(MessageConstants.MSG_AFTER_IF);
	String INSIDE_IF_MESSAGE = InstrumentationMessageLoader.getMessage(MessageConstants.MSG_IN_IF);
	String ENTERED_REDUCE_MESSAGE = MessageFormat.format(InstrumentationMessageLoader.getMessage(MessageConstants.ENTERED_MAPREDUCE), "reduce");
	String EXITING_REDUCE_MESSAGE = MessageFormat.format(InstrumentationMessageLoader.getMessage(MessageConstants.EXITING_MAPREDUCE), "reduce");
	String MATCHING_REGEX_MESSAGE = "Matching against regular expression";
	String VALIDATING_MESSAGE = InstrumentationMessageLoader.getMessage(MessageConstants.VALIDATION_KEY_VALUE);
	String MAP_METHOD = "map";
	String REDUCE_METHOD = "reduce";
	String KEY = "K";
	String FALSE = "false";
	String NOT_AVAILABLE = "NOT AVAILABLE";
	String LOG_FORMAT_EXCEPTION = "Error Parsing Log File";
	String UNAVAILABLE = "Not Available";
	String MAP_REDUCE_LOG_FILE = "mapreduce";
	String INSIDE_ELSE_IF_MESSAGE = InstrumentationMessageLoader.getMessage(MessageConstants.MSG_IN_ELSEIF);
	String INSIDE_ELSE_MESSAGE = InstrumentationMessageLoader.getMessage(MessageConstants.MSG_IN_ELSE);
	String LOOP_EXECUTED_MESSAGE = InstrumentationMessageLoader.getMessage(MessageConstants.LOOP_EXECUTION);
	String SETUP_METHOD = "setup";
	String CLEANUP_METHOD = "cleanup";
	String IF = "If#";
	String ELSE_IF = "Else-if#";
	String ELSE = "Else";
	String SWITCH = "Switch#";
	String SWITCH_CASE = "Case#";
	String CONTEXT_WRITE = "ctxWrt";
	String ENTERING_SWITCH_BLOCK_MESSAGE = InstrumentationMessageLoader.getMessage(MessageConstants.MSG_BEFORE_SWITCH);
	String EXITED_SWITCH_BLOCK_MESSAGE = InstrumentationMessageLoader.getMessage(MessageConstants.MSG_AFTER_SWITCH);
	String ENTERED_SWITCH_CASE_MESSAGE = InstrumentationMessageLoader.getMessage(MessageConstants.MSG_IN_SWITCHCASE);
	String EXITING_SWITCH_CASE_MESSAGE = InstrumentationMessageLoader.getMessage(MessageConstants.MSG_OUT_SWITCHCASE);
	String INFO_MESSAGE = "INFO";
	String METHOD_ENTRY = InstrumentationMessageLoader.getMessage(MessageConstants.ENTERED_METHOD);
	String METHOD_EXIT = InstrumentationMessageLoader.getMessage(MessageConstants.EXITING_METHOD);
	String RETURN = "return";
	String METHOD = "method";
	String MR_CHAIN = "mrChain";
	String JOB_CHAIN = "jobChain";
	String JOB_ATTRIBUTE = "job_";
	String DEBUG_ANALYSIS = "debugAnalysis";
	String IF_BLOCK = "IfBlock#";
	String LOOP_ENTRY = InstrumentationMessageLoader.getMessage(MessageConstants.ENTERED_LOOP);
	String LOOP_EXIT = InstrumentationMessageLoader.getMessage(MessageConstants.EXITING_LOOP);
	String LOOP = "Loop#";
	String PARTITIONER_ENTRY = "PI";
	String REDUCER_IDENTIFIER = "r";

	/**
	 * METHODS_CHECK_LIST - the list containing the methods to be processed when current counter is not available
	 */
	List<String> METHODS_CHECK_LIST = new ArrayList<String>() {

		{
			add(ENTERED_MAP_MESSAGE);
			add(ENTERED_REDUCE_MESSAGE);
			add(PARTITIONER_ENTRY);
			add(INFO_MESSAGE);

		}
	};

	/**
	 * ENTERING_COUNTER_MESSAGES_MAP - the map containing the messages indicating the entry of various expression counters
	 */
	Map<String, Integer> ENTERING_COUNTER_MESSAGES_MAP = new HashMap<String, Integer>() {

		{

			put(ENTERING_IF_MESSAGE, ONE);
			put(ENTERING_SWITCH_BLOCK_MESSAGE, TWO);
			put(ENTERED_SWITCH_CASE_MESSAGE, THREE);
			put(LOOP_ENTRY, FOUR);
			put(INSIDE_IF_MESSAGE, FIVE);
			put(INSIDE_ELSE_IF_MESSAGE, SIX);
			put(INSIDE_ELSE_MESSAGE, SEVEN);

		}
	};

	/**
	 * EXITING_COUNTER_MESSAGES_MAP - the map containing the messages indicating the exit of various expression counters
	 */
	Map<String, Integer> EXITING_COUNTER_MESSAGES_MAP = new HashMap<String, Integer>() {

		{
			put(EXITING_MAP_MESSAGE, ONE);
			put(EXITING_REDUCE_MESSAGE, ONE);
			put(METHOD_EXIT, TWO);
			put(EXITED_IF_MESSAGE, THREE);
			put(EXITED_SWITCH_BLOCK_MESSAGE, FOUR);
			put(EXITING_SWITCH_CASE_MESSAGE, FOUR);
			put(LOOP_EXIT,FOUR);
		}
	};

	/**
	 * EXITING_COUNTER_MESSAGES_MAP - the map containing the messages indicating the exit of various expression counters
	 */
	Map<String, Integer> OTHER_COUNTER_MESSAGES_MAP = new HashMap<String, Integer>() {

		{
			put(ENTERED_MAP_MESSAGE, ONE);
			put(ENTERED_REDUCE_MESSAGE, ONE);
			put(CONTEXT_WRITE, TWO);
			put(MATCHING_REGEX_MESSAGE, THREE);
			put(VALIDATING_MESSAGE,THREE);
			put(METHOD_ENTRY, FOUR);
		}
	};

}