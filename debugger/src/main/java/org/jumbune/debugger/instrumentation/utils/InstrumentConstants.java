package org.jumbune.debugger.instrumentation.utils;


import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapred.lib.ChainReducer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskInputOutputContext;
import org.jumbune.utils.ClassLoaderUtil;
import org.jumbune.utils.Instrumented;
import org.jumbune.utils.JobUtil;
import org.jumbune.utils.LogUtil;
import org.jumbune.utils.MapReduceExecutionUtil;
import org.jumbune.utils.PatternMatcher;
import org.jumbune.utils.PatternValidator;
import org.objectweb.asm.Type;


/**
 * This is interface for constants used in instrument module.
 */
@SuppressWarnings("deprecation")
public interface InstrumentConstants {
	String CLASSNAME_CLASS_HAS_BEEN_INSTRUMENTED = Type.getInternalName(Instrumented.class);
	String CLASSNAME_MAPPER_CONTEXT = Type.getInternalName(Mapper.Context.class);
	String CLASSNAME_REDUCER_CONTEXT = Type.getInternalName(Reducer.Context.class);
	String CLASSNAME_MAPPER = Type.getInternalName(Mapper.class);
	String CLASSNAME_REDUCER = Type.getInternalName(Reducer.class);
	String CLASSNAME_MAPREDUCEBASE = Type.getInternalName(MapReduceBase.class);
	String CLASSNAME_IOEXCEPTION = Type.getInternalName(IOException.class);
	String CLASSNAME_INTERRUPTEDEXCEPTION = Type.getInternalName(InterruptedException.class);
	String INTERFACENAME_MAPPER = Type.getInternalName(org.apache.hadoop.mapred.Mapper.class);
	String INTERFACENAME_REDUCER = Type.getInternalName(org.apache.hadoop.mapred.Reducer.class);
	String CLASSNAME_LOGUTIL = Type.getInternalName(LogUtil.class);
	String CLASSNAME_MAPREDUCEEXECUTIL = Type.getInternalName(MapReduceExecutionUtil.class);
	String CLASSNAME_MR_JOB = Type.getInternalName(Job.class);
	String CLASSNAME_JOB_UTIL = Type.getInternalName(JobUtil.class);
	String CLASSNAME_CLASSLOADER_UTIL = Type.getInternalName(ClassLoaderUtil.class);
	String CLASSNAME_HADOOP_CONFIGURATION = Type.getInternalName(Configuration.class);
	String CLASSNAME_PATTERNMATCHER = Type.getInternalName(PatternMatcher.class);
	String CLASSNAME_PATTERN = Type.getInternalName(Pattern.class);
	String CLASSNAME_SYSTEM = Type.getInternalName(System.class);
	// PM
	String CLASSNAME_MR_JOBCLIENT = Type.getInternalName(JobClient.class);
	String CLASSNAME_OUTPUTCOLLECTOR = Type.getInternalName(OutputCollector.class);
	String CLASSNAME_JOB_CONF = Type.getInternalName(JobConf.class);
	String CLASSNAME_TASKINPUTOUTPUTCONTEXT = Type.getInternalName(TaskInputOutputContext.class);
	String CLASSNAME_TASKATTEMPTID = Type.getInternalName(TaskAttemptID.class);
	String CLASSNAME_PATTERNVALIDATOR = Type.getInternalName(PatternValidator.class);
	String CLASSNAME_LONG = Type.getInternalName(Long.class);

	String CLASSNAME_CLASS = Type.getInternalName(Class.class);
	String CLASSNAME_FIELD = Type.getInternalName(Field.class);
	String CLASSNAME_RUNNINGJOB = Type.getInternalName(RunningJob.class);
	String CLASSNAME_CHAINMAPPPER = Type.getInternalName(ChainMapper.class);
	String CLASSNAME_CHAINREDUCER = Type.getInternalName(ChainReducer.class);
	String CLASSNAME_STRINGBUILDER = Type.getInternalName(StringBuilder.class);
	String CLASSNAME_OBJECT = Type.getInternalName(Object.class);
	String CLASSNAME_TYPE = Type.getInternalName(Type.class);

	// constants for field names
	String MAP_REDUCE_COUNTER = "mapReduceCounter";
	String CONTEXT_WRITE_COUNTER = "contextWriteCounter";
	String LOOP_COUNTER = "loopCounter";
	String KEY_PATTERN = "keyPattern";
	String VALUE_PATTERN = "valuePattern";
	String KEY_REGEX_NULL = "isKeyRegexNull";
	String VALUE_REGEX_NULL = "isValueRegexNull";
	String KEY_VALIDATOR = "keyValidator";
	String VALUE_VALIDATOR = "valueValidator";
	String FIELD_LOADLOGGER = "jLoadLogger";
	String FIELD_LOGGERNUMBER = "jLoggerNumber";
	String FIELD_LOGGERCOUNT = "jLoggerKount";
	String PROFILE_PARTITIONER = "profilePartitioner";
	String FIELD_UNLOADLOGGER = "jUnloadLogger";

	// constant for method names
	String CLEANUP_METHOD = "cleanup";
	String SETUP_METHOD = "setup";
	String CONFIGURE_METHOD = "configure";
	String CLOSE_METHOD = "close";
	String CURRENT_TIME_MILLIS = "currentTimeMillis";
	String MAP_METHOD = "map";
	String REDUCE_METHOD = "reduce";
	String INIT_METHOD = "<init>";
	String JOB_SUBMIT = "submit";
	String JOB_WAIT_FOR_COMPLETION = "waitForCompletion";
	String MAIN_METHOD = "main";
	String REGEX_METHOD_NAME = "match";
	String REGEX_LOG_METHOD = "getRegExInfo";
	String USER_PATTERN_VALIDATOR_METHOD_NAME = "isPatternValid";
	String SET_PARTITIONER_METHOD = "setPartitioner";
	String REMOVE_PARTITIONER_METHOD = "removePartitioner";
	String REMOVE_OLD_PARTITIONER_METHOD = "removeOldPartitioner";
	String PARTITIONER_INFO = "getPartitionerInfo";
	String INITIALIZE_JUMBUNE_LOGGING = "initializeJumbuneLog";
	String STOP_JUMBUNE_LOGGING = "removeJumbuneLog";
	String GET_NUMBER_OF_REDUCERS = "getNumReduceTasks";
	String LOAD_LOGGER = "loadLogger";
	String GET_TASK_ATTEMPT_ID = "getTaskAttemptID";
	String TO_STRING = "toString";
	String REMOVE_NUM_REDUCER_TASKS = "removeNumReducetasks";
	String SET_NUM_REDUCER_TASKS = "setNumReducetasksthreadlocal";
	String ADD_MAPPER = "addMapper";
	String SET_REDUCER = "setReducer";
	String CLINIT_METHOD = "<clinit>";

	// PM
	String RUN_JOB = "runJob";
	String SUBMIT_JOB = "submitJob";
	String CALCULATE_PARTITIONING_TIME = "calculateParitioningTime";
	String GET_PARTITIONING_SAMPLE_COUNT = "getPartitioningSampleCount";
	String GET_PARTITIONING_TIME_TAKEN = "getPartitionerTotalTimeTaken";
	String REMOVE_PARTITONER_TIME_SAMPLE_COUNT = "removePartitionerTimeAndSampleCount";

	// Class descriptors
	String DESCRIPTOR_PATTERN = Type.getDescriptor(Pattern.class);
	String DESCRIPTOR_STRING = Type.getDescriptor(String.class);
	String DESCRIPTOR_PATTERNVALIDATOR = Type.getDescriptor(PatternValidator.class);
	String DESCRIPTOR_FIELD = Type.getDescriptor(Field.class);
	String DESCRIPTOR_JOBCLIENT = Type.getDescriptor(JobClient.class);
	String DESCRIPTOR_JOBCONF = Type.getDescriptor(JobConf.class);
	String DESCRIPTOR_RUNNINGJOB = Type.getDescriptor(RunningJob.class);
	String DESCRIPTOR_INTEGER = Type.getDescriptor(Integer.class);
	String DESCRIPTOR_LONG = Type.getDescriptor(Long.class);
	String DESCRIPTOR_OBJECT = Type.getDescriptor(Object.class);

	// Type descriptors
	Type TYPE_MAPPER_CONTEXT = Type.getType(Mapper.Context.class);
	Type TYPE_REDUCER_CONTEXT = Type.getType(Reducer.Context.class);
	Type TYPE_OBJECT = Type.getType(Object.class);
	Type TYPE_ITERABLE = Type.getType(Iterable.class);
	Type TYPE_STRING = Type.getType(String.class);
	Type TYPE_TASKINPUTOUTPUTCONTEXT = Type.getType(TaskInputOutputContext.class);
	Type TYPE_WRITABLE = Type.getType(Writable.class);
	Type TYPE_STRING_ARRAY = Type.getType(new StringBuilder().append('[').append(DESCRIPTOR_STRING).toString());
	Type TYPE_ITERATOR = Type.getType(Iterator.class);
	Type TYPE_OBJECT_ARRAY = Type.getType(new StringBuilder().append('[').append(DESCRIPTOR_OBJECT).toString());
	// PM
	Type TYPE_OUTPUT_COLLECTOR = Type.getType(OutputCollector.class);
	Type TYPE_REPORTER = Type.getType(Reporter.class);
	Type TYPE_JOBCONF = Type.getType(JobConf.class);
	Type TYPE_BOOLEAN = Type.getType(Boolean.class);

	Type TYPE_FIELD = Type.getType(Field.class);
	Type TYPE_JOB_CONTEXT= Type.getType(JobContext.class);
	Type TYPE_RUNNINGJOB = Type.getType(RunningJob.class);
	Type TYPE_TYPE = Type.getType(Type.class);
	Type TYPE_STRINGBUILDER = Type.getType(StringBuilder.class);

	// Method descriptors
	String DESCRIPTOR_REDUCER_CLEANUP = Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_REDUCER_CONTEXT);
	String DESCRIPTOR_MAPPER_CLEANUP = Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_MAPPER_CONTEXT);
	String DESCRIPTOR_PATTERNVALIDATOR_ISPATTERNVALID = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(WritableComparable.class));
	String REGEX_METHOD_DESC = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, TYPE_WRITABLE, Type.getType(Pattern.class));
	String REGEX_NULL_METHOD_DESC = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(Writable.class));
	String EMPTY_PARAMETER_VOID_RETURN = Type.getMethodDescriptor(Type.VOID_TYPE);
	String METHOD_DESC_PARAM_CONTEXT_RETURN_VOID = Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_TASKINPUTOUTPUTCONTEXT);
	String DESC_INT_PARAM_RETURN_VOID = Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE);
	String DESC_EMPTY_PARAM_RETURN_INT = Type.getMethodDescriptor(Type.INT_TYPE);
	String METHOD_DESC_RETURN_LONG = Type.getMethodDescriptor(Type.getType(Long.class));
	String DESC_CALCULATE_PARTITIONING_TIME = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Writable.class), Type.getType(Writable.class));
	String METHOD_DESC_PARAM_CONF_RETURN_VOID = Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_JOBCONF);
	String DESC_TO_STRING = Type.getMethodDescriptor(TYPE_STRING);

	// PM
	String DESCRIPTOR_CONFIGURE = Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_JOBCONF);
	String DESCRIPTOR_MAPPER_CONFIGURE = Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_JOBCONF);
	String DESCRIPTOR_MAPPER_CLOSE = Type.getMethodDescriptor(Type.VOID_TYPE);

	// Indexes
	int PARAMETER_DETAIL_OPCODE_INDEX = 0;
	int PARAMETER_DETAIL_OWNER_INDEX = 1;
	int PARAMETER_DETAIL_DESC_INDEX = 2;
	int PARAMETER_DETAIL_VAR_INDEX = 3;

	// Other constants
	String REGEX_MESSAGE = "Validating key Expresssion";
	String VALIDATION_MESSAGE = "Validating";
	String CLASS_FILE_EXTENSION = ".class";
	String PROFILE_TASK = "mapred.task.profile";
	String PROFILE_PARAMS = "mapred.task.profile.params";
	String PROFILE_MAPS = "mapred.task.profile.maps";
	String PROFILE_REDUCES = "mapred.task.profile.reduces";

	String JOB_CLIENT = "jobClient";
	String JOB_INFO = "info";
	String JOB_CONF = "conf";
	String FIELD_JOBCLIENT = "fieldJobClient";
	String FIELD_INFO = "fieldInfo";
	String FIELD_CONF = "fieldConf";
	String JOB_FIELDCLIENT = "client";
	String JOB_FIELDCONFF = "conff";
	String JOB_FIELDRUNNINGJOB = "runningJob";

	int PARAMETER_NULL_INDEX = -1;
	int KEY_INDEX = 0;
	int VALUE_INDEX = 1;
	String PROFILING_REDUCER_INSTANCES="0-1";
	String PROFILING_MAPPERS_INSTANCES="0-1";
	int FIVE = 5;
	int THREE = 3;
	int FOUR = 4;
	int SIX = 6;
	int SEVEN = 7;
	int FIFTEEN = 15;
	int TEN = 10 ;
	int HUNDRED = 100 ;
	int FOUR_ZERO_NINE_SIX = 4096;
	int LAKH = 1000000;
}
