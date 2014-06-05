package org.jumbune.debugger.instrumentation.utils;

import static org.jumbune.debugger.instrumentation.utils.InstrumentConstants.CLASSNAME_LOGUTIL;
import static org.jumbune.debugger.instrumentation.utils.InstrumentConstants.CLASSNAME_SYSTEM;
import static org.jumbune.debugger.instrumentation.utils.InstrumentConstants.CURRENT_TIME_MILLIS;
import static org.jumbune.debugger.instrumentation.utils.InstrumentConstants.TYPE_JOBCONF;
import static org.jumbune.debugger.instrumentation.utils.InstrumentConstants.TYPE_OBJECT;
import static org.jumbune.debugger.instrumentation.utils.InstrumentConstants.TYPE_STRING;
import static org.jumbune.debugger.instrumentation.utils.InstrumentConstants.TYPE_TASKINPUTOUTPUTCONTEXT;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.utils.beans.LogInfoBean;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;


/**
 * <p>
 * This class provides api to be used to inject logging
 * </p>
 * 
 
 */
public final class InstrumentUtil {
	private static final Logger LOGGER = LogManager
			.getLogger(InstrumentUtil.class);
	private static final int CONTEXT_VARIABLE_IN_CLEANUP_SETUP = 1;
	private InstrumentUtil(){};

	private static final char DEFAULT_MESSAGE_SEPARATOR = '|';

	private static Map<Integer, String> jumpInsnOpcodeMap = new HashMap<Integer, String>();

	static {
		jumpInsnOpcodeMap.put(Opcodes.IFNE, "IFNE");
		jumpInsnOpcodeMap.put(Opcodes.IFEQ, "IFEQ");
		jumpInsnOpcodeMap.put(Opcodes.IF_ACMPEQ, "IF_ACMPEQ");
		jumpInsnOpcodeMap.put(Opcodes.IF_ACMPNE, "IF_ACMPNE");
		jumpInsnOpcodeMap.put(Opcodes.IF_ICMPEQ, "IF_ICMPEQ");
		jumpInsnOpcodeMap.put(Opcodes.IF_ICMPGE, "IF_ICMPGE");
		jumpInsnOpcodeMap.put(Opcodes.IF_ICMPLE, "IF_ICMPLE");
		jumpInsnOpcodeMap.put(Opcodes.IF_ICMPLT, "IF_ICMPLT");
		jumpInsnOpcodeMap.put(Opcodes.IF_ICMPNE, "IF_ICMPNE");
		jumpInsnOpcodeMap.put(Opcodes.IFGE, "IFGE");
		jumpInsnOpcodeMap.put(Opcodes.IFGT, "IFGT");
		jumpInsnOpcodeMap.put(Opcodes.IFLE, "IFLE");
		jumpInsnOpcodeMap.put(Opcodes.IFLT, "IFLT");
		jumpInsnOpcodeMap.put(Opcodes.IFNONNULL, "IFNONNULL");
		jumpInsnOpcodeMap.put(Opcodes.IFNULL, "IFNULL");
	}

	/**
	 * gets instrumentation code map
	 * @return
	 */
	public static Map<Integer, String> getJumpInsnOpcodesMap() {
		return jumpInsnOpcodeMap;
	}

	/**
	 * <p>
	 * This method provides instructions to be included before various logging
	 * </p>
	 * 
	 * @param String
	 *            [] values values to be passed as parameters in logging method
	 * @return Instructions
	 */
	private static InsnList getBasicInstructions(Object... values) {
		InsnList il = new InsnList();
		il.add(new LabelNode());
		for (Object value : values) {
			il.add(new LdcInsnNode(value));
		}

		return il;
	}

	/**
	 * <p>
	 * This method provides instruction to log timer information
	 * </p>
	 * 
	 * @param className
	 *            Class which has called the logger
	 * @param methodName
	 *            Method in which the logger has been called
	 * @param variable
	 *            index of the variable which contains the start time of the
	 *            method
	 * @param logMsg
	 *            log message
	 * @return Instructions
	 */
	public static InsnList addTimerLogging(String className, String methodName,
			int variable, String logMsg) {
		String method = "getMapReduceTimerInfo";

		InsnList il = getBasicInstructions(className, methodName, logMsg);
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_SYSTEM,
				CURRENT_TIME_MILLIS, Type.getMethodDescriptor(Type.LONG_TYPE)));
		il.add(new VarInsnNode(Opcodes.LLOAD, variable));
		il.add(new InsnNode(Opcodes.LSUB));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_LOGUTIL,
				method, Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
						TYPE_STRING, TYPE_STRING, Type.LONG_TYPE)));
		return il;
	}

	/**
	 * <p>
	 * This method provides instruction to log counter information
	 * </p>
	 * 
	 * @param className
	 *            Class which has called the logger
	 * @param methodName
	 *            Method in which the logger has been called
	 * @param logMsgPrefix
	 *            log message
	 * @param field
	 *            field which store the counter
	 * @return Instructions
	 */
	public static InsnList addCounterLogging(String className,
			String methodName, String logMsgPrefix, String field) {
		String method = "getMapReduceExecutionInfo";

		InsnList il = new InsnList();
		il.add(new LabelNode());
		il.add(new VarInsnNode(Opcodes.ALOAD, CONTEXT_VARIABLE_IN_CLEANUP_SETUP));
		il.add(new LdcInsnNode(className));
		il.add(new LdcInsnNode(methodName));
		il.add(new LdcInsnNode(logMsgPrefix));
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(className), field,
				Type.INT_TYPE.getDescriptor()));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_LOGUTIL,
				method, Type.getMethodDescriptor(Type.VOID_TYPE,
						TYPE_TASKINPUTOUTPUTCONTEXT, TYPE_STRING, TYPE_STRING,
						TYPE_STRING, Type.INT_TYPE)));
		return il;
	}

	/**
	 * <p>
	 * This method provides instruction to log method return information
	 * </p>
	 * 
	 * @param className
	 *            Class which has called the logger
	 * @param methodName
	 *            Method in which the logger has been called
	 * @param logMsg
	 *            log message
	 * @return Instructions
	 */
	public static InsnList addReturnLogging(String className,
			String methodName, String logMsg) {
		String method = "getMethodReturn";

		InsnList il = getBasicInstructions(className, methodName, logMsg);
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_LOGUTIL,
				method, Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
						TYPE_STRING, TYPE_STRING)));
		return il;
	}

	/**
	 * <p>
	 * This method provides instruction to log loop execution information
	 * </p>
	 * 
	 * @param className
	 *            Class which has called the logger
	 * @param methodName
	 *            Method in which the logger has been called
	 * @param logMsg
	 *            log message
	 * @param variable
	 *            index of the variable which stores the loop execution counter
	 * @return Instructions
	 */
	public static InsnList addloopCounterLogging(String className,
			String methodName, String logMsg, int variable, String info) {
		String method = "getLoopCounterInfo";

		InsnList il = getBasicInstructions(className, methodName, logMsg);
		il.add(new VarInsnNode(Opcodes.ILOAD, variable));
		il.add(new LdcInsnNode(info));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_LOGUTIL,
				method, Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
						TYPE_STRING, TYPE_STRING, Type.INT_TYPE, TYPE_STRING)));
		return il;
	}

	/**
	 * <p>
	 * This method provides instructions to increase value of an int field by 1
	 * </p>
	 * 
	 * @param className
	 *            class name
	 * @param field
	 *            field whose value need to be increased
	 * @return Instructions
	 */
	public static InsnList incrementIntBy1(String className, String field) {
		InsnList il = new InsnList();
		il.add(new LabelNode());
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new InsnNode(Opcodes.DUP));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(className), field,
				Type.INT_TYPE.getDescriptor()));
		il.add(new InsnNode(Opcodes.ICONST_1));
		il.add(new InsnNode(Opcodes.IADD));
		il.add(new FieldInsnNode(Opcodes.PUTFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(className), field,
				Type.INT_TYPE.getDescriptor()));
		return il;
	}

	/**
	 * <p>
	 * This method provides instructions to log information about context.write
	 * method calls
	 * </p>
	 * 
	 * @param className
	 *            Class which has called the logger
	 * @param methodName
	 *            Method in which the logger has been called
	 * @param logMessage
	 *            log message
	 * @return Instructions
	 */
	public static InsnList addMapReduceContextWriteLogging(String className,
			String methodName, String logMessage) {
		String method = "getMapReduceContextWriteInfo";

		InsnList il = getBasicInstructions(className, methodName, logMessage);
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_LOGUTIL,
				method, Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
						TYPE_STRING, TYPE_STRING)));
		return il;
	}

	/**
	 * <p>
	 * This method provides instructions to add a new class member
	 * </p>
	 * 
	 * @param fieldList
	 *            Existing fields
	 * @param access
	 *            access scope of new field
	 * @param name
	 *            name of new field
	 * @param desc
	 *            description of new field
	 * @param signature
	 *            signature of new field
	 * @param value
	 *            initial value of new field
	 */
	public static void addClassMember(List<FieldNode> fieldList, int access,
			String name, String desc, String signature, Object value) {
		LOGGER.debug(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.ADDING_CLASS_MEMBER), name));
		boolean isPresent = false;

		// find if the filed already exists
		for (int i = 0; i < fieldList.size(); i++) {
			FieldNode fn = fieldList.get(i);
			if (name.equals(fn.name)) {
				isPresent = true;
				break;
			}
		}

		// add if not exists
		if (!isPresent) {
			FieldNode newNode = new FieldNode(access, name, desc, signature,
					value);
			fieldList.add(newNode);
		}
	}
	/***
	 * This method  creates basic logger instructions 
	 * @param logBean 
	 * @return Instruction list which has been created.
	 */
	public static InsnList createBasicLoggerInsns(LogInfoBean logBean) {
		InsnList il = new InsnList();
		il.add(new LabelNode());

		il.add(new LdcInsnNode(logBean.getLogCallingClass()));
		il.add(new LdcInsnNode(logBean.getLogCallingMethod()));
		il.add(new LdcInsnNode(logBean.getMsgPrefix()));
		if (logBean.getMsgSuffix() != null) {
			il.add(new LdcInsnNode(logBean.getMsgSuffix()));
		}

		return il;
	}

	/**
	 * This method creates a LogUtil.getRegExInfo() method call which takes in
	 * parameter a boolean. To get this boolean user's validator class
	 * isPatternValid method is called which takes in input the the
	 * variableIndex defined. So below instruction is constructed:
	 * 
	 * LogUtil.getRegExInfo(callingClass, callingMethod, logLevel, message,
	 * keyValidator.isPatternValid(tempKey));
	 * 
	 * LogUtil.getRegExInfo(callingClass, callingMethod, logLevel, message,
	 * valueValidator.isPatternValid(tempValue));
	 * 
	 * @param logBean
	 *            - Bean containing information to be used for logging like
	 *            callingClass, methodName, message *
	 * @param variableIndex
	 *            - index of the temporary variable which stores value of either
	 *            key/value to be matched
	 * @param validatorFieldName
	 *            - name of the field which is of type Validator class and is to
	 *            be used to call the isPatternValidate
	 * @param validatorClass
	 *            - validator class
	 * @return InstructionList to add instructions for calling
	 *         LogUtil.getRegExinfo(..)
	 */
	public static InsnList addLoggerWithClassMethodCall(LogInfoBean logBean,
			int variableIndex, String validatorFieldName,
			String validatorClass, String classQualifiedName) {
		String logMethodDesc = Type.getMethodDescriptor(Type.VOID_TYPE,
				TYPE_STRING, TYPE_STRING, TYPE_STRING, TYPE_STRING,
				Type.BOOLEAN_TYPE);

		InsnList il = new InsnList();

		il.add(createBasicLoggerInsns(logBean));

		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		// Loading field of type of the class specified for validating key/value
		il.add(new FieldInsnNode(Opcodes.GETFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(classQualifiedName),
				validatorFieldName,
				InstrumentConstants.DESCRIPTOR_PATTERNVALIDATOR));

		il.add(new VarInsnNode(Opcodes.ALOAD, variableIndex));

		// Calling the method PatternMatcher.match
		il.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE,
				InstrumentConstants.CLASSNAME_PATTERNVALIDATOR,
				InstrumentConstants.USER_PATTERN_VALIDATOR_METHOD_NAME,
				InstrumentConstants.DESCRIPTOR_PATTERNVALIDATOR_ISPATTERNVALID));

		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_LOGUTIL,
				InstrumentConstants.REGEX_LOG_METHOD, logMethodDesc));

		return il;
	}

	/**
	 * This method adds statement for calling LogUtil method so it can log
	 * information about partitioner It will construct below statement:
	 * 
	 * LogUtil.getPartitionerInfo(className, methodName, logLevel, message,
	 * sampleCount, timeTaken );
	 * 
	 * @param logBean
	 *            - bean containing information to log
	 * @return InstructionList for calling LogUtil.getPartitionerInfo method
	 */
	public static InsnList addLoggingForPartitioner(LogInfoBean logBean) {

		String logMethodDesc = Type.getMethodDescriptor(Type.VOID_TYPE,
				TYPE_STRING, TYPE_STRING, TYPE_STRING,
				Type.getType(Long.class), Type.getType(Long.class));

		InsnList il = new InsnList();

		il.add(createBasicLoggerInsns(logBean));

		LOGGER.debug("logMethodDes " + logMethodDesc
				+ "  METHOD_DESC_RETURN_LONG "
				+ InstrumentConstants.METHOD_DESC_RETURN_LONG);
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				InstrumentConstants.CLASSNAME_MAPREDUCEEXECUTIL,
				InstrumentConstants.GET_PARTITIONING_SAMPLE_COUNT,
				InstrumentConstants.METHOD_DESC_RETURN_LONG));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				InstrumentConstants.CLASSNAME_MAPREDUCEEXECUTIL,
				InstrumentConstants.GET_PARTITIONING_TIME_TAKEN,
				InstrumentConstants.METHOD_DESC_RETURN_LONG));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_LOGUTIL,
				InstrumentConstants.PARTITIONER_INFO, logMethodDesc));

		return il;
	}

	/**
	 * This will return an InsnList which would be reading all instructions from
	 * beginning of statement till it reaches LineNumberNode
	 * 
	 * @param node
	 *            - the node from which instruction list is read till starting
	 * @return
	 */
	public static List<AbstractInsnNode> getInstructionListFromBeginingofStatement(
			AbstractInsnNode node, List<AbstractInsnNode> nodeList) {
		if (node instanceof LineNumberNode) {
			return nodeList;
		}
		getInstructionListFromBeginingofStatement(node.getPrevious(), nodeList);
		nodeList.add(node);
		return nodeList;
	}

	/**
	 * This method calls a method match of class PatternMatcher to match
	 * key/value against given pattern. The call to class method and printing
	 * the result is done in log statement. i.e. the resultant method call is
	 * Logger.info("Result  " + PatternMatcher.match(valueToBeCompared,
	 * regularExpression) + "  "); These strings are appended in StringBuilder
	 * and this StringBuilder is passed in Logger.
	 * 
	 * @param mn
	 * @param regEx
	 * @param variableIndex
	 * @param variableType
	 *            - The type of variable against which regular expression has to
	 *            be done. It should be a fully qualified name of class of
	 *            variable. E.g. if String value is to be matched then
	 *            variableType should be "java/lang/String;"
	 * @return
	 */
	public static InsnList addRegExMatcherClassCall(LogInfoBean logBean,
			int variableIndex, String pattern, String classQualifiedName) {
		// If using context use this else not
		String logMethodDesc = Type.getMethodDescriptor(Type.VOID_TYPE,
				TYPE_STRING, TYPE_STRING, TYPE_STRING, TYPE_STRING,
				Type.BOOLEAN_TYPE);

		InsnList il = createBasicLoggerInsns(logBean);
		il.add(new VarInsnNode(Opcodes.ALOAD, variableIndex));
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(classQualifiedName),
				pattern, InstrumentConstants.DESCRIPTOR_PATTERN));

		// Calling the method PatternMatcher.match
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				InstrumentConstants.CLASSNAME_PATTERNMATCHER,
				InstrumentConstants.REGEX_METHOD_NAME,
				InstrumentConstants.REGEX_METHOD_DESC));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				InstrumentConstants.CLASSNAME_LOGUTIL,
				InstrumentConstants.REGEX_LOG_METHOD, logMethodDesc));

		return il;
	}

	/**
	 * This method inserts a LogUtil method call in class. This LogUtil method
	 * calls PatternMatcher.match(Writable) method. The match method takes only
	 * writable object since the regEx given by user is null. So it doesn't
	 * requires Pattern object.
	 * 
	 * @param logBean
	 *            LogInfoBean object that contains information related to
	 *            logging
	 * @param variableIndex
	 *            index of writable object to be matched against null
	 * @return InstructionList containing instructions for inserting statement
	 *         LogUtil.getRegexInfo("msg" + PatternMatcher.match(writableVal));
	 */
	public static InsnList addRegExMatcherClassCall(LogInfoBean logBean,
			int variableIndex) {
		// If using context use this else not
		String logMethodDesc = Type.getMethodDescriptor(Type.VOID_TYPE,
				TYPE_STRING, TYPE_STRING, TYPE_STRING, TYPE_STRING,
				Type.BOOLEAN_TYPE);

		InsnList il = createBasicLoggerInsns(logBean);
		il.add(new VarInsnNode(Opcodes.ALOAD, variableIndex));

		// Calling the method PatternMatcher.match
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				InstrumentConstants.CLASSNAME_PATTERNMATCHER,
				InstrumentConstants.REGEX_METHOD_NAME,
				InstrumentConstants.REGEX_NULL_METHOD_DESC));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				InstrumentConstants.CLASSNAME_LOGUTIL,
				InstrumentConstants.REGEX_LOG_METHOD, logMethodDesc));

		return il;
	}

	/**
	 * <p>
	 * This method reads the current entry from a zip and returns the bytes
	 * </p>
	 * 
	 * @param inputStream
	 *            Zip Input stream
	 * @return bytes from the current entry in the zip file
	 * @throws FileNotFoundException
	 *             error occurred
	 * @throws IOException
	 *             error occurred
	 */
	public static byte[] getEntryBytesFromZip(ZipInputStream inputStream)
			throws IOException {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		int el;
		byte[] buffer = new byte[1 << InstrumentConstants.FIFTEEN];
		while ((el = inputStream.read(buffer)) != -1) {
			byteArray.write(buffer, 0, el);
		}
		return byteArray.toByteArray();
	}

	/**
	 * method to get all UDF array list
	 * @param list
	 * @return
	 */
	public static String[][] getAllUDFArray(List<MethodInsnNode> list) {
		String[][] retArray = new String[list.size()][InstrumentConstants.THREE];

		for (int i = 0; i < list.size(); i++) {
			MethodInsnNode min = list.get(i);
			retArray[i][0] = min.owner.replace('/', '.');
			retArray[i][1] = min.name;
			retArray[i][2] = min.desc;
		}

		return retArray;
	}

	/**
	 * <p>
	 * This method instruments the given bytes and return the modified bytes.
	 * </p>
	 * 
	 * @param inputBytes
	 *            bytes to be instrumented
	 * @param cv
	 *            Class Visitor
	 * @param cw
	 *            Class Writer
	 * @return Modified bytes
	 * @throws FileNotFoundException
	 *             An error occurred during the operation
	 * @throws IOException
	 *             An error occurred during the operation
	 */
	public static byte[] instrumentBytes(byte[] inputBytes, ClassVisitor cv,
			ClassVisitor cw) throws IOException {
		LOGGER.debug(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.INSTRUMENT_USING), cv.getClass()
				.getSimpleName()));

		ClassReader cr = new ClassReader(inputBytes);
		cr.accept(cv, ClassReader.SKIP_FRAMES);
		ClassWriter cw1 = (ClassWriter) cw;

		return cw1.toByteArray();
	}

	/**
	 * <p>
	 * This method instruments the given bytes and return the modified bytes.
	 * </p>
	 * 
	 * @param inputBytes
	 *            bytes to be instrumented
	 * @param cv
	 *            Class Visitor
	 * @param cw
	 *            Class Writer
	 * @return Modified bytes
	 * @throws FileNotFoundException
	 *             An error occurred during the operation
	 * @throws IOException
	 *             An error occurred during the operation
	 */
	public static byte[] instrumentBytesWithFrame(byte[] inputBytes,
			ClassVisitor cv, ClassVisitor cw) throws IOException {
		LOGGER.debug(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.INSTRUMENT_USING), cv.getClass()
				.getSimpleName()));
		ClassReader cr = new ClassReader(inputBytes);
		cr.accept(cv, 0);
		ClassWriter cw1 = (ClassWriter) cw;

		return cw1.toByteArray();
	}

	/**
	 * <p>
	 * This method finds the class with given name, instruments it and return
	 * the modified bytes.
	 * </p>
	 * 
	 * @param clazz
	 *            name of the class to be instrumented
	 * @param cv
	 *            Class visitor
	 * @param cw
	 *            Class Writer
	 * @return Modified bytes
	 * @throws FileNotFoundException
	 *             An error occurred during the operation
	 * @throws IOException
	 *             An error occurred during the operation
	 */
	public static byte[] instrumentBytes(String clazz, ClassVisitor cv,
			ClassVisitor cw) throws IOException {
		LOGGER.debug(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.INSTRUMENT_USING), cv.getClass()
				.getSimpleName()));
		InputStream is = InstrumentUtil.class.getClassLoader()
				.getResourceAsStream(
						ConfigurationUtil.convertQualifiedClassNameToInternalName(clazz)
								+ InstrumentConstants.CLASS_FILE_EXTENSION);

		ClassReader cr = new ClassReader(is);
		cr.accept(cv, 0);
		ClassWriter cw1 = (ClassWriter) cw;

		if (is != null) {
			is.close();
		}

		return cw1.toByteArray();
	}

	/**
	 * <p>
	 * This method reads a file and convert it into bytes
	 * </p>
	 * 
	 * @param inputFile
	 *            File to be read
	 * @return bytes read from the file
	 * @throws FileNotFoundException
	 *             If file is not found
	 * @throws IOException
	 *             Any error occurred during the operation
	 */
	public static byte[] getBytesFromFile(File inputFile)
			throws IOException {
		InputStream inputStream = new FileInputStream(inputFile);
		long length = inputFile.length();

		// Create the byte array to hold the data
		byte[] outputBytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		if(offset < outputBytes.length){
			numRead = inputStream.read(outputBytes, offset,
					outputBytes.length - offset);
			while(offset < outputBytes.length && numRead >= 0){
				offset += numRead;
				numRead = inputStream.read(outputBytes, offset,
						outputBytes.length - offset);
			}
		}

		// Ensure all the bytes have been read in
		if (offset < outputBytes.length) {
			throw new IOException("Could not completely read file "
					+ inputFile.getName());
		}

		// Close the input stream and return bytes
		inputStream.close();
		return outputBytes;
	}

	/**
	 * <p>
	 * Validates whether the method is map or reduce.
	 * </p>
	 * 
	 * @param mn
	 *            Method node being scanned
	 * @return true if the method is map or reduce
	 */
	public static boolean validateMapReduceMethod(MethodNode mn) {
		return (InstrumentConstants.MAP_METHOD.equals(mn.name) || InstrumentConstants.REDUCE_METHOD
				.equals(mn.name)) && !isSysntheticAccess(mn.access);
	}

	/**
	 * <p>
	 * Validates whether the method is map method.
	 * </p>
	 * 
	 * @param mn
	 *            Method node being scanned
	 * @return true if the method is map
	 */
	public static boolean validateMapMethod(MethodNode mn) {
		return InstrumentConstants.MAP_METHOD.equals(mn.name)
				&& !isSysntheticAccess(mn.access);
	}

	/**
	 * <p>
	 * Validates whether the given name and the compare name are same.
	 * </p>
	 * 
	 * @param srcName
	 *            Given name
	 * @param cmpName
	 *            Compare name
	 * @return true if the two names are same
	 */
	public static boolean validateMethodName(String srcName, String cmpName) {
		return cmpName.equals(srcName);
	}

	/**
	 * <p>
	 * This method appends a separator to the given message.
	 * </p>
	 * 
	 * @param msg
	 *            Given message
	 * @return Modified message
	 */
	public static String separateMessage(Object msg) {
		return separateMessage(msg, DEFAULT_MESSAGE_SEPARATOR);
	}

	/**
	 * <p>
	 * This method appends a separator to the given message.
	 * </p>
	 * 
	 * @param msg
	 *            Given message
	 * @param separator
	 *            Given separated to be appended
	 * @return Modified message
	 */
	public static String separateMessage(Object msg, char separator) {
		return new StringBuilder().append(separator).append(msg).toString();
	}

	/**
	 * <p>
	 * This method finds if the passed min is job submission
	 * </p>
	 * 
	 * @param min
	 *            MethodInsnNode method being visited
	 * @return true if the method being visited is submit job one
	 */
	public static boolean isJobSubmissionMethod(MethodInsnNode min) {
		for (EnumJobSubmitMethods js : EnumJobSubmitMethods.values()) {
			if (min.name.equals(js.toString())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * This method finds if the passed min owner is a job
	 * </p>
	 * 
	 * @param min
	 *            MethodInsnNode method being visited
	 * @return true if the method owner is Job or JobClient
	 */
	public static boolean isOwnerJob(MethodInsnNode min) {

		for (EnumJobSubmitMethods js : EnumJobSubmitMethods.values()) {
			if (min.owner.equals(js.getOwner().getInternalName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * This method checks if the method writes output from the mapper or reducer
	 * class.
	 * </p>
	 * 
	 * @param min
	 * @return boolean
	 */
	public static boolean isOutputMethod(MethodInsnNode min) {
		for (EnumWriteOutputMethods om : EnumWriteOutputMethods.values()) {
			if (min.name.equals(om.getName())
					&& min.owner.equals(om.getOwner())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * This method checks if the init method is mapper/reducer class is
	 * setup/configure.
	 * </p>
	 * 
	 * @param min
	 * @return boolean
	 */
	public static boolean isInitMethod(MethodNode min) {
		for (EnumInitMRMethods im : EnumInitMRMethods.values()) {
			if (min.name.equals(im.toString())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * This method checks if the init method is mapper/reducer class is
	 * cleanup/close.
	 * </p>
	 * 
	 * @param min
	 * @return boolean
	 */
	public static boolean isExitMethod(MethodNode min) {
		for (EnumExitMRMethods em : EnumExitMRMethods.values()) {
			if (min.name.equals(em.toString())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * This method provides instruction to log counter information
	 * </p>
	 * 
	 * @param className
	 *            Class which has called the logger
	 * @param methodName
	 *            Method in which the logger has been called
	 * @param logMethod
	 *            log method
	 * @param logMsgPrefix
	 *            log message
	 * @param field
	 *            field which store the counter
	 * @return Instructions
	 */
	public static InsnList addCounterLoggingOldApi(String className,
			String methodName, String logMethod, String logMsgPrefix,
			String field) {
		String method = "getMapReduceExecutionInfoOldApi";

		InsnList il = new InsnList();
		il.add(new LabelNode());
		il.add(new LdcInsnNode(className));
		il.add(new LdcInsnNode(methodName));
		il.add(new LdcInsnNode(logMethod));
		il.add(new LdcInsnNode(logMsgPrefix));
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(className), field,
				Type.INT_TYPE.getDescriptor()));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_LOGUTIL,
				method, Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
						TYPE_STRING, TYPE_STRING, TYPE_STRING, Type.INT_TYPE)));
		return il;
	}

	/**
	 * <p>
	 * This method provides instructions to log header row
	 * </p>
	 * 
	 * @param className
	 *            Class which has called the logger
	 * @return InsnList Instructions
	 */
	public static InsnList addLogHeader(String className) {
		return addLogHeader(className, TYPE_TASKINPUTOUTPUTCONTEXT);
	}

	/**
	 * <p>
	 * This method provides instructions to log header row
	 * </p>
	 * 
	 * @param className
	 *            Class which has called the logger
	 * @return InsnList Instructions
	 */
	public static InsnList addLogHeaderOldApi(String className) {
		return addLogHeader(className, TYPE_JOBCONF);
	}

	/**
	 * <p>
	 * This method provides instructions to log header row
	 * </p>
	 * 
	 * @param className
	 *            Class which has called the logger
	 * @return InsnList Instructions
	 */
	private static InsnList addLogHeader(String className, Type contextType) {
		String method = "addLogHeader";

		InsnList il = new InsnList();
		il.add(new LabelNode());
		il.add(new VarInsnNode(Opcodes.ALOAD, CONTEXT_VARIABLE_IN_CLEANUP_SETUP));
		il.add(new LdcInsnNode(className));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_LOGUTIL,
				method, Type.getMethodDescriptor(Type.VOID_TYPE, contextType,
						TYPE_STRING)));
		return il;
	}

	/**
	 * method for adding logger information
	 * @param sequence
	 * @param className
	 * @return
	 */
	public static InsnList addChainLoggerInfo(int sequence, String className) {
		String method = "addChainLoggerInfo";

		InsnList il = new InsnList();
		il.add(new LabelNode());
		il.add(new LdcInsnNode(sequence));
		il.add(new LdcInsnNode(className));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_LOGUTIL,
				method, Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE,
						TYPE_STRING)));
		return il;
	}

	/**
	 * <p>
	 * This method provides instruction to log method entry/exit information
	 * </p>
	 * 
	 * @param className
	 *            Class which has called the logger
	 * @param methodName
	 *            Method in which the logger has been called
	 * @param logMsg
	 *            log message
	 * @return InsnList Instructions
	 */
	public static InsnList addLogMessage(Object... objects) {
		String method = "addLogMsg";

		InsnList il = getBasicInstructions(objects);

		Type[] types = new Type[objects.length];
		for (int i = 0; i < objects.length; i++) {
			types[i] = TYPE_OBJECT;
		}

		String methodDesc = Type.getMethodDescriptor(Type.VOID_TYPE, types);

		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_LOGUTIL,
				method, methodDesc));
		return il;
	}

	/**
	 * <p>
	 * This method checks whether a method has Synthetic access or not.
	 * </p>
	 * 
	 * @param access
	 *            Access
	 * @return boolean true if synthetic access
	 */
	public static boolean isSysntheticAccess(int access) {
		return (access & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC;
	}
}
