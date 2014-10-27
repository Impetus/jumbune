package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.apache.hadoop.mapred.JobConf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.utils.Environment;
import org.jumbune.debugger.instrumentation.utils.InstrumentConstants;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.jumbune.utils.MapReduceExecutionUtil;
import org.jumbune.utils.beans.LogInfoBean;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;



/**
 * <p>
 * This adapter injects code related to map reduce execution.
 * </p>
 * 

 */
@SuppressWarnings("deprecation")
public class ConfigureMapReduceAdapter extends BaseAdapter {
	
	/** The Constant SET_LOG_NUM_METHOD. */
	private static final String SET_LOG_NUM_METHOD = "setLoggerNumber";

	/** The Constant UNCHECKED_WARNING. */
	private static final String UNCHECKED_WARNING = "unchecked";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(ConfigureMapReduceAdapter.class);

	/** The env. */
	private Environment env;
	
	/**
	 * <p>
	 * Create a new instance of MapreduceCounterAdapter.
	 * </p>
	 *
	 * @param loader the loader
	 * @param cv Class visitor
	 */
	public ConfigureMapReduceAdapter(Loader loader, ClassVisitor cv) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
	}

	/**
	 * Instantiates a new configure map reduce adapter.
	 *
	 * @param loader the loader
	 * @param cv the cv
	 * @param env the env
	 */
	public ConfigureMapReduceAdapter(Loader loader, ClassVisitor cv,Environment env) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
		this.env = env;
	}

	/* (non-Javadoc)
	 * @see org.jumbune.debugger.instrumentation.adapter.BaseAdapter#visitEnd()
	 */
	@SuppressWarnings({ UNCHECKED_WARNING })
	@Override
	public void visitEnd() {
		if (isMapperClass() || isReducerClass()) {
			InstrumentUtil.addClassMember(fields, Opcodes.ACC_PRIVATE,
					KEY_PATTERN, DESCRIPTOR_PATTERN, null, null);
			InstrumentUtil.addClassMember(fields, Opcodes.ACC_PRIVATE,
					VALUE_PATTERN, DESCRIPTOR_PATTERN, null, null);
			InstrumentUtil.addClassMember(fields, Opcodes.ACC_PRIVATE,
					InstrumentConstants.FIELD_LOADLOGGER,
					Type.BOOLEAN_TYPE.getDescriptor(), null, null);
			InstrumentUtil.addClassMember(fields, Opcodes.ACC_PRIVATE,
					InstrumentConstants.FIELD_LOGGERNUMBER,
					Type.INT_TYPE.getDescriptor(), null, null);
			InstrumentUtil.addClassMember(fields, Opcodes.ACC_PRIVATE,
					FIELD_UNLOADLOGGER, Type.BOOLEAN_TYPE.getDescriptor(),
					null, true);

			// Add counter for counting the number of keys processed by this map
			// instance. Also add a boolean variable for checking if partitioner
			// should be profiled or not
			LOGGER.info("Is this a mapper class " + isMapperClass()
					+ " className " + getClassName());
			if (isMapperClass()) {
				InstrumentUtil.addClassMember(fields, Opcodes.ACC_PRIVATE,
						MAP_REDUCE_COUNTER, Type.INT_TYPE.getDescriptor(),
						null, null);
				InstrumentUtil.addClassMember(fields, Opcodes.ACC_PRIVATE,
						PROFILE_PARTITIONER, Type.BOOLEAN_TYPE.getDescriptor(),
						null, true);
			}

			addKeyValueValidators();

			boolean cleanupMethodExists = false;
			boolean setupMethodExists = false;

			for (Object o : methods) {
				MethodNode mn = (MethodNode) o;

				// validating if the method is map/reduce
				if (InstrumentUtil.isExitMethod(mn)) {
					LOGGER.info(MessageFormat.format(
							InstrumentationMessageLoader
									.getMessage(MessageConstants.LOG_CLEANUP_METHOD_FOUND),
							getClassName()));
					cleanupMethodExists = true;
					// modifying cleanup method
					modifyConfigureMethods(mn, false);
				} else if (InstrumentUtil.isInitMethod(mn)) {
					LOGGER.info(MessageFormat.format(
							InstrumentationMessageLoader
									.getMessage(MessageConstants.LOG_SETUP_METHOD_FOUND),
							getClassName()));
					setupMethodExists = true;
					// modifying setup method
					modifyConfigureMethods(mn, true);
				}
				mn.visitMaxs(0, 0);
			}

			// adding setup/configure method
			if (!setupMethodExists) {
				LOGGER.info(MessageFormat.format(
						InstrumentationMessageLoader
								.getMessage(MessageConstants.LOG_SETUP_METHOD_NOT_FOUND),
						getClassName()));
				addConfigureMethods(true);
			}

			// adding cleanup/close method
			if (!cleanupMethodExists) {
				LOGGER.info(MessageFormat.format(
						InstrumentationMessageLoader
								.getMessage(MessageConstants.LOG_CLEANUP_METHOD_NOT_FOUND),
						getClassName()));
				addConfigureMethods(false);
			}
		}
		accept(cv);
	}

	
	/**
	 * It adds setup/configure , cleanup/close methods having jumbune logging
	 * related instructions
	 * 
	 * @param isSetup
	 *            - true if its setup method, false if its cleanup method
	 */
	private void addConfigureMethods(boolean isSetup) {
		if (isOldApiClazz()) {
			addCleanupSetupMethodOldApi(isSetup);
		} else {
			addCleanupSetupMethod(isSetup);
		}
	}

	/**
	 * This method removes the jumbune mumber from threadLocal signifying that
	 * jumbune doesn't need to log further information.
	 *
	 * @return InsnList list of instructions to be injected
	 */
	private InsnList stopJumbuneLogging() {
		LOGGER.debug("Removing jumbune from ThreadLocal");
		InsnList il = new InsnList();
		il.add(new LabelNode());

		String methodDesc = null;

		if (isOldApiClazz()) {
			il.add(new VarInsnNode(Opcodes.ALOAD, 0));
			il.add(new FieldInsnNode(Opcodes.GETFIELD, ConfigurationUtil
					.convertQualifiedClassNameToInternalName(getClassName()),
					InstrumentConstants.FIELD_UNLOADLOGGER, "Z"));

			methodDesc = Type.getMethodDescriptor(Type.VOID_TYPE,
					Type.BOOLEAN_TYPE);
		} else {
			methodDesc = EMPTY_PARAMETER_VOID_RETURN;
		}

		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				CLASSNAME_MAPREDUCEEXECUTIL, "stopJumbuneLogging", methodDesc));
		return il;
	}

	/**
	 * It removes variables related to Partitioner logging set in threadLocal
	 * like totalNumberOfReducerTasks.
	 *
	 * @param isSetup - true if setup method, false if cleanup method
	 * @return the insn list
	 */
	private InsnList addRemoveTotalReducerTasksFromThreadLocal(boolean isSetup) {
		InsnList il = new InsnList();
		il.add(new LabelNode());

		String methodName = SET_NUM_REDUCER_TASKS;
		String methodDesc = DESC_INT_PARAM_RETURN_VOID;

		if (isSetup) {
			// The method MapReduceExecutionUtil.setNumReducerTasks require a
			// int
			// parameter which is fetched from context.getNumReduceTasks()
			String className = CLASSNAME_TASKINPUTOUTPUTCONTEXT;
			if (isOldApiClazz()) {
				className = CLASSNAME_JOB_CONF;
			}

			il.add(new VarInsnNode(Opcodes.ALOAD, 1));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, className,
					GET_NUMBER_OF_REDUCERS, DESC_EMPTY_PARAM_RETURN_INT));

		} else {
			LOGGER.debug("Removing number of reduce tasks from threadLocal");
			methodName = REMOVE_NUM_REDUCER_TASKS;
			methodDesc = EMPTY_PARAMETER_VOID_RETURN;
		}

		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				CLASSNAME_MAPREDUCEEXECUTIL, methodName, methodDesc));
		return il;
	}

	/**
	 * Adds the partitioner logging.
	 *
	 * @param methodName the method name
	 * @param isSetup the is setup
	 * @return the insn list
	 */
	private InsnList addPartitionerLogging(String methodName, boolean isSetup) {

		LOGGER.info("Add logging of partitioner !!! ");

		InsnList il = new InsnList();

		if (!isSetup) {
			il.add(new LabelNode());
			String cSymbol = env.getClassSymbol(getClassName());
			String mSymbol = env.getMethodSymbol(getClassName(), cSymbol, methodName);

			LogInfoBean logBean = new LogInfoBean(cSymbol,
					mSymbol,InstrumentationMessageLoader
							.getMessage(MessageConstants.MSG_PARTITION_INFO),
					null);

			il.add(InstrumentUtil.addLoggingForPartitioner(logBean));

		}

		return il;
	}

	/**
	 * This method will add code for setting Partitioner in threadLocal so that
	 * it can be accessed inside map method. If its cleanup method then code is
	 * added to remove partitioner from thread local
	 * 
	 * @param isSetup
	 *            - set true if its setup method
	 * @return - Instruction list containing code for setting/removing
	 *         partitioner
	 */
	private InsnList addRemovePartitionerFromThreadLocal(boolean isSetup) {
		InsnList il = new InsnList();
		il.add(new LabelNode());

		String methodDesc = null;

		if (isSetup) {
			if (isOldApiClazz()) {
				methodDesc = InstrumentConstants.METHOD_DESC_PARAM_CONF_RETURN_VOID;
			} else {
				methodDesc = METHOD_DESC_PARAM_CONTEXT_RETURN_VOID;
			}
			// Load context variable
			il.add(new VarInsnNode(Opcodes.ALOAD, 1));

			// adding partitioner to ThreadLocal
			il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
					CLASSNAME_MAPREDUCEEXECUTIL, SET_PARTITIONER_METHOD,
					methodDesc));
		} else {
			LOGGER.info("Remvoing partitioner from ThreadLocal in cleanup method !!");

			String methodName = null;

			if (isOldApiClazz()) {
				methodName = REMOVE_OLD_PARTITIONER_METHOD;
			} else {
				methodName = REMOVE_PARTITIONER_METHOD;
			}

			// removing partitioner from ThreadLocal
			il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
					CLASSNAME_MAPREDUCEEXECUTIL, methodName,
					InstrumentConstants.EMPTY_PARAMETER_VOID_RETURN));

			// Also remove PartitonerTime and partitionerSample count
			il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
					CLASSNAME_MAPREDUCEEXECUTIL,
					InstrumentConstants.REMOVE_PARTITONER_TIME_SAMPLE_COUNT,
					InstrumentConstants.EMPTY_PARAMETER_VOID_RETURN));
		}

		return il;
	}

	/**
	 * <p>
	 * This method provides instructions to add Pattern.compile() call for regex
	 * values
	 * </p>
	 *
	 * @return Instructions
	 */
	private InsnList addPatternCompiler() {
		YamlLoader yamlLoader = (YamlLoader)getLoader();
		String keyRegex = yamlLoader.getMapReduceKeyRegex(getClassName());
		String valueRegex = yamlLoader.getMapReduceValueRegex(getClassName());

		InsnList il = new InsnList();

		LOGGER.info(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.LOG_ADD_PATTERN_COMPILE),
				getClassName()));

		// regex values
		String[] regexes = new String[] { keyRegex, valueRegex };

		// class fields
		String[] filedNames = new String[] { KEY_PATTERN, VALUE_PATTERN };

		for (int i = 0; i < regexes.length; i++) {
			il.add(new LabelNode());
			il.add(new VarInsnNode(Opcodes.ALOAD, 0));
			// if regex is not null
			if (regexes[i] != null) {
				il.add(new LdcInsnNode(regexes[i]));
				il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
						CLASSNAME_PATTERN, "compile", Type.getMethodDescriptor(
								Type.getType(Pattern.class), TYPE_STRING)));
				il.add(new FieldInsnNode(
						Opcodes.PUTFIELD,
						ConfigurationUtil.convertQualifiedClassNameToInternalName(getClassName()),
						filedNames[i], DESCRIPTOR_PATTERN));
			}
		}

		return il;
	}

	/**
	 * This method add statements to initialize the given PatternValidator
	 * objects in setup method.
	 *
	 * @return the insn list
	 */
	private InsnList addValidatorInitializationInstructions() {
		YamlLoader yamlLoader = (YamlLoader)getLoader();
		String keyValidationClass = yamlLoader
				.getMapReduceKeyValidator(getClassName());
		String valueValidationClass = yamlLoader
				.getMapReduceValueValidator(getClassName());

		InsnList il = null;
		if (keyValidationClass != null || valueValidationClass != null) {
			il = new InsnList();
			LOGGER.info(MessageFormat.format(
					InstrumentationMessageLoader
							.getMessage(MessageConstants.LOG_INITIALIZE_PATTERN_VALIDATOR),
					getClassName()));

			// Validation classes
			String[] validators = new String[] { keyValidationClass,
					valueValidationClass };

			// class fields
			String[] fieldNames = new String[] { KEY_VALIDATOR, VALUE_VALIDATOR };
			int index = 0;
			for (String validator : validators) {
				if (!CollectionUtil.isNullOrEmpty(validator)) {
					String internalClassName = ConfigurationUtil
							.convertQualifiedClassNameToInternalName(validator);

					il.add(new LabelNode());
					il.add(new VarInsnNode(Opcodes.ALOAD, 0));
					il.add(new TypeInsnNode(Opcodes.NEW, internalClassName));
					il.add(new InsnNode(Opcodes.DUP));
					il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
							internalClassName, InstrumentConstants.INIT_METHOD,
							InstrumentConstants.EMPTY_PARAMETER_VOID_RETURN));

					il.add(new FieldInsnNode(
							Opcodes.PUTFIELD,
							ConfigurationUtil.convertQualifiedClassNameToInternalName(getClassName()),
							fieldNames[index],
							InstrumentConstants.DESCRIPTOR_PATTERNVALIDATOR));

				}
				index++;
			}
		}

		return il;
	}

	/**
	 * It modifies existing setup/configure , cleanup/close methods by adding
	 * jumbune logging related instructions.
	 *
	 * @param mn - current method node
	 * @param isSetup - true if its setup method, false if its cleanup method
	 */
	private void modifyConfigureMethods(MethodNode mn, boolean isSetup) {
		if (isSetup) {
			LOGGER.info(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.LOG_MODIFY_SETUP_METHOD),
					getClassName()));
		} else {
			LOGGER.info(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.LOG_MODIFY_CLEANUP_METHODD),
					getClassName()));
		}
		InsnList insnList = mn.instructions;
		AbstractInsnNode[] insnArr = insnList.toArray();

		for (AbstractInsnNode abstractInsnNode : insnArr) {
			if (Opcodes.RETURN >= abstractInsnNode.getOpcode()
					&& Opcodes.IRETURN <= abstractInsnNode.getOpcode()) {
				InsnList il = new InsnList();

				il.add(prepareMapReduceForJumbuneInstructions(isSetup, mn));

				insnList.insertBefore(abstractInsnNode, il);
				break;
			}
		}
	}

	/**
	 * <p>
	 * This method adds a new method (setup or cleanup) to the class
	 * </p>.
	 *
	 * @param isSetup true if method is setup
	 */
	@SuppressWarnings(UNCHECKED_WARNING)
	private void addCleanupSetupMethod(boolean isSetup) {
		// new method
		MethodNode newMN = new MethodNode(Opcodes.ASM4, Opcodes.ACC_PROTECTED,
				null, null, null, new String[] { CLASSNAME_IOEXCEPTION,
						CLASSNAME_INTERRUPTEDEXCEPTION });

		// method name
		if (isSetup) {
			LOGGER.info(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.LOG_ADDING_SETUP_METHOD),
					getClassName()));
			newMN.name = SETUP_METHOD;
		} else {
			LOGGER.info(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.LOG_ADDING_CLEANUP_METHODD),
					getClassName()));
			newMN.name = CLEANUP_METHOD;
		}

		// method descriptor
		if (isMapperClass()) {
			newMN.desc = DESCRIPTOR_MAPPER_CLEANUP;
		} else {
			newMN.desc = DESCRIPTOR_REDUCER_CLEANUP;
		}

		InsnList il = new InsnList();

		// adding call to super method
		il.add(new LabelNode());
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new VarInsnNode(Opcodes.ALOAD, 1));
		il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, getSuperClassName(),
				isSetup ? SETUP_METHOD : CLEANUP_METHOD, newMN.desc));

		il.add(prepareMapReduceForJumbuneInstructions(isSetup, newMN));

		il.add(new InsnNode(Opcodes.RETURN));

		newMN.instructions = il;
		methods.add(0, newMN);
	}

	/**
	 * This method creates InstructionsList for adding jumbune related
	 * instructions in configure and close methods in old map and reduce
	 * classes. And setup, cleanup method in new map, reduce classes
	 * 
	 * @param isSetup
	 *            - true if the current method is setup
	 * @param methodNode
	 *            - method node on which instructions are operating can be
	 *            setup/cleanup
	 * @return - Instructions for
	 */
	private InsnList prepareMapReduceForJumbuneInstructions(boolean isSetup,
			MethodNode methodNode) {
		YamlLoader yamlLoader = (YamlLoader)getLoader();
		boolean instrumentMapreduceRegex =  yamlLoader
				.isInstrumentEnabled("instrumentRegex");
		boolean instrumentMapreduceUserDefinedValidation = yamlLoader
				.isInstrumentEnabled("instrumentUserDefValidate");
		boolean instrumentJobPartitioner = yamlLoader
				.isInstrumentEnabled("partitioner");

		InsnList il = new InsnList();

		// Enable jumbune logging in threadLocal then only logging will begin
		if (isSetup) {
			// setting class fields
			if (isOldApiClazz()) {
				il.add(setFields());
			}

			// load the logger
			il.add(loadLogger());

			if (isOldApiClazz()) {
				il.add(addChainInfo());
			}

			// add the log header
			il.add(addLogHeader());

			if (instrumentMapreduceRegex) {
				il.add(addPatternCompiler());
			}

			// Add Initialization of Pattern Validator
			if (instrumentMapreduceUserDefinedValidation) {
				InsnList insnList = addValidatorInitializationInstructions();
				if (insnList != null) {
					il.add(insnList);
				}
			}
			
		} else {
			il.add(setLoggerNumber());
		}

		il=logPartitionerData(isSetup, methodNode, instrumentJobPartitioner, il);


		// Remove the jumbune logging in cleanup method after all the required
		// logging is done
		if (!isSetup) {
			il.add(stopJumbuneLogging());
		}

		return il;
	}
	
	/**
	 * Log partitioner data.
	 *
	 * @param isSetup the is setup
	 * @param methodNode the method node
	 * @param instrumentJobPartitioner the instrument job partitioner
	 * @param il the il
	 * @return the insn list
	 */
	private InsnList logPartitionerData(boolean isSetup, MethodNode methodNode, boolean instrumentJobPartitioner, InsnList il) {
		if (instrumentJobPartitioner && isMapperClass()) {
			// Before partitioner and its related data is removed use it for
			// logging
			LabelNode loggingPartitionerLabel = new LabelNode();
			if (isOldApiClazz()) {
				il.add(new VarInsnNode(Opcodes.ALOAD, 0));
				il.add(new FieldInsnNode(
						Opcodes.GETFIELD,
						ConfigurationUtil.convertQualifiedClassNameToInternalName(getClassName()),
						InstrumentConstants.PROFILE_PARTITIONER, "Z"));
				il.add(new JumpInsnNode(Opcodes.IFEQ, loggingPartitionerLabel));

				il.add(new LabelNode());
			}
			// All the code of removing partitioner from threadLocal should be
			// done only if profilePartitioner is set to true
			il.add(addPartitionerLogging(methodNode.name, isSetup));
			il.add(addRemoveTotalReducerTasksFromThreadLocal(isSetup));
			il.add(addRemovePartitionerFromThreadLocal(isSetup));

			if (isOldApiClazz()) {
				il.add(loggingPartitionerLabel);
			}
		}
		return il;
	}
	/**
	 * This method add PatternValidator fields for both key and value.
	 */
	@SuppressWarnings(UNCHECKED_WARNING)
	private void addKeyValueValidators() {
		String[] fieldNames = new String[] { KEY_VALIDATOR, VALUE_VALIDATOR };

		for (String fieldName : fieldNames) {
			InstrumentUtil.addClassMember(fields, Opcodes.ACC_PRIVATE,
					fieldName, DESCRIPTOR_PATTERNVALIDATOR, null, null);
		}
	}

	/**
	 * <p>
	 * This method adds a new method (setup or cleanup) to the class
	 * </p>.
	 *
	 * @param isSetup true if method is setup
	 */
	@SuppressWarnings(UNCHECKED_WARNING)
	private void addCleanupSetupMethodOldApi(boolean isSetup) {
		// new method
		MethodNode newMN = new MethodNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC,
				null, null, null, new String[] { CLASSNAME_IOEXCEPTION });
		InsnList il = new InsnList();
		il.add(new LabelNode());
		// method name
		if (isSetup) {
			LOGGER.info(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.LOG_ADDING_SETUP_METHOD),
					getClassName()));
			newMN.name = CONFIGURE_METHOD;
			newMN.desc = DESCRIPTOR_MAPPER_CONFIGURE;
			il.add(new VarInsnNode(Opcodes.ALOAD, 0));
			il.add(new VarInsnNode(Opcodes.ALOAD, 1));
		} else {
			LOGGER.info(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.LOG_ADDING_CLEANUP_METHODD),
					getClassName()));
			newMN.name = CLOSE_METHOD;
			newMN.desc = DESCRIPTOR_MAPPER_CLOSE;
			il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		}

		// adding call to super method
		il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, getSuperClassName(),
				isSetup ? CONFIGURE_METHOD : CLOSE_METHOD, newMN.desc));

		il.add(prepareMapReduceForJumbuneInstructions(isSetup, newMN));

		il.add(new InsnNode(Opcodes.RETURN));

		newMN.instructions = il;
		methods.add(0, newMN);
	}

	/**
	 * <p>
	 * This method provides instruction to inject method call to load the logger
	 * </p>.
	 *
	 * @return InsnList Instructions
	 */
	private InsnList loadLogger() {
		LOGGER.info(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.LOG_LOAD_LOGGER), getClassName()));

		InsnList il = new InsnList();
		il.add(new LabelNode());
		YamlLoader yamlLoader = (YamlLoader)getLoader();
		String logFileDir = yamlLoader.getSlaveLogLocationWithPlaceHolder()
				.substring(
						0,
						yamlLoader.getSlaveLogLocationWithPlaceHolder()
								.lastIndexOf('/') + 1);

		// getting task attempt id
		il.add(new LdcInsnNode(logFileDir));
		il.add(new VarInsnNode(Opcodes.ALOAD, 1));
		il.add(new LdcInsnNode(isMapperClass()));

		String methodDesc = null;

		if (isOldApiClazz()) {
			il.add(new VarInsnNode(Opcodes.ALOAD, 0));
			il.add(new FieldInsnNode(Opcodes.GETFIELD, ConfigurationUtil
					.convertQualifiedClassNameToInternalName(getClassName()),
					InstrumentConstants.FIELD_LOADLOGGER, "Z"));
			il.add(new VarInsnNode(Opcodes.ALOAD, 0));
			il.add(new FieldInsnNode(Opcodes.GETFIELD, ConfigurationUtil
					.convertQualifiedClassNameToInternalName(getClassName()),
					InstrumentConstants.FIELD_LOGGERNUMBER, "I"));

			methodDesc = Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
					TYPE_JOBCONF, Type.BOOLEAN_TYPE, Type.BOOLEAN_TYPE,
					Type.INT_TYPE);
		} else {
			methodDesc = Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
					TYPE_TASKINPUTOUTPUTCONTEXT, Type.BOOLEAN_TYPE);
		}
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type
				.getInternalName(MapReduceExecutionUtil.class),
				"configureLogging", methodDesc));

		return il;
	}

	/**
	 * Sets the logger number.
	 *
	 * @return the insn list
	 */
	private InsnList setLoggerNumber() {

		InsnList il = new InsnList();

		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(getClassName()),
				InstrumentConstants.FIELD_LOGGERNUMBER, "I"));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type
				.getInternalName(MapReduceExecutionUtil.class),
				SET_LOG_NUM_METHOD, "(I)V"));

		return il;
	}

	/**
	 * <p>
	 * This method provides instructions to inject method call to log header row
	 * </p>.
	 *
	 * @return InsnList Instructions
	 */
	private InsnList addLogHeader() {
		InsnList il = new InsnList();
		String newName = "";
		if(name != null){
			newName = name.replaceAll("/", ".");
		}
		String symbol = env.getClassSymbol(newName);

		if (isOldApiClazz()) {
			il.add(InstrumentUtil.addLogHeaderOldApi(symbol));
		} else {
			il.add(InstrumentUtil.addLogHeader(symbol));
		}
		return il;
	}

	/**
	 * <p>
	 * This method provides instruction to get values from JobConf and set them
	 * to class fields
	 * </p>.
	 *
	 * @return InsnList Instructions
	 * @see InstrumentConstants#FIELD_LOGGERNUMBER
	 * @see InstrumentConstants#FIELD_LOADLOGGER
	 */
	private InsnList setFields() {
		InsnList list = new InsnList();
		list.add(new LabelNode());
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new LdcInsnNode(InstrumentConstants.FIELD_LOGGERNUMBER));
		list.add(new InsnNode(Opcodes.ICONST_0));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type
				.getInternalName(JobConf.class), "getInt", Type
				.getMethodDescriptor(Type.INT_TYPE, TYPE_STRING, Type.INT_TYPE)));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(getClassName()),
				InstrumentConstants.FIELD_LOGGERNUMBER, "I"));

		list.add(new LabelNode());
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new LdcInsnNode(InstrumentConstants.FIELD_LOADLOGGER));
		list.add(new InsnNode(Opcodes.ICONST_1));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type
				.getInternalName(JobConf.class), "getBoolean", Type
				.getMethodDescriptor(Type.BOOLEAN_TYPE, TYPE_STRING,
						Type.BOOLEAN_TYPE)));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(getClassName()),
				InstrumentConstants.FIELD_LOADLOGGER, "Z"));

		list.add(new LabelNode());
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new LdcInsnNode(InstrumentConstants.FIELD_UNLOADLOGGER));
		list.add(new InsnNode(Opcodes.ICONST_1));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type
				.getInternalName(JobConf.class), "getBoolean", Type
				.getMethodDescriptor(Type.BOOLEAN_TYPE, TYPE_STRING,
						Type.BOOLEAN_TYPE)));
		list.add(new FieldInsnNode(Opcodes.PUTFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(getClassName()),
				InstrumentConstants.FIELD_UNLOADLOGGER, "Z"));

		// Set the profilePartitioner in map configure method
		if (isMapperClass()) {
			list.add(new LabelNode());
			list.add(new VarInsnNode(Opcodes.ALOAD, 0));
			list.add(new VarInsnNode(Opcodes.ALOAD, 1));
			list.add(new LdcInsnNode(InstrumentConstants.PROFILE_PARTITIONER));
			list.add(new InsnNode(Opcodes.ICONST_1));
			list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type
					.getInternalName(JobConf.class), "getBoolean", Type
					.getMethodDescriptor(Type.BOOLEAN_TYPE, TYPE_STRING,
							Type.BOOLEAN_TYPE)));
			list.add(new FieldInsnNode(Opcodes.PUTFIELD, ConfigurationUtil
					.convertQualifiedClassNameToInternalName(getClassName()),
					InstrumentConstants.PROFILE_PARTITIONER, "Z"));
		}
		return list;
	}

	/**
	 * Adds the chain info.
	 *
	 * @return the insn list
	 */
	private InsnList addChainInfo() {
		InsnList list = new InsnList();
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				CLASSNAME_MAPREDUCEEXECUTIL, "addChainInfo", Type
						.getMethodDescriptor(Type.VOID_TYPE, TYPE_JOBCONF)));

		return list;
	}
}