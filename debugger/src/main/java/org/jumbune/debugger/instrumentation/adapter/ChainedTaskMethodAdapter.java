package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.mapred.JobConf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentConstants;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;



/**
 * This adapter traverses a method, collects necessary information about Chained
 * tasks and process them.
 * 

 */
public class ChainedTaskMethodAdapter extends BaseMethodAdpater {

	/** The logger. */
	private static Logger logger = LogManager
			.getLogger(ChainedTaskMethodAdapter.class);

	/** The chained mapper list. */
	private Map<Integer, List<Integer>> chainedMapperList = null;
	
	/** The chained reducer list. */
	private Map<Integer, List<Integer>> chainedReducerList = null;
	
	/** The local job conf added. */
	private int localJobConfAdded = 0;
	
	/** The insn arr. */
	private AbstractInsnNode[] insnArr;
	
	/** The map jobs. */
	private Map<Integer, Integer> mapJobs = null;
	
	/** The reduce jobs. */
	private Map<Integer, Integer> reduceJobs = null;
	
	/** The Constant LOCAL_CONF_VARIABLE_NAME. */
	private static final String LOCAL_CONF_VARIABLE_NAME = "jumbuneTempJobConf";
	
	/** The chain mapper info list. */
	private Map<Integer, List<Object>> chainMapperInfoList = null;
	
	/** The chain reducer info list. */
	private Map<Integer, List<Object>> chainReducerInfoList = null;

	/**
	 * Instantiates a new chained task method adapter.
	 *
	 * @param access the access
	 * @param name the name
	 * @param desc the desc
	 * @param signature the signature
	 * @param exceptions the exceptions
	 * @param mv the mv
	 * @param className the class name
	 */
	public ChainedTaskMethodAdapter(int access, String name, String desc,
			String signature, String[] exceptions, MethodVisitor mv,
			String className) {
		super(access, name, desc, signature, exceptions, mv, className);
		this.mv = mv;
	}

	/* (non-Javadoc)
	 * @see org.objectweb.asm.tree.MethodNode#visitEnd()
	 */
	@Override
	public void visitEnd() {
		logger.debug(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.LOG_INSTRUMENTING_METHOD),
				getClassName() + "##" + name + "##" + desc));

		this.insnArr = instructions.toArray();

		// 1st pass: finding the chained tasks, collecting information and
		// perform basic operations
		processChinedTaskFromInstructionsArray(true);

		if (chainedMapperList != null || chainedReducerList != null) {
			if (chainedMapperList != null && chainedMapperList.size() > 0) {
				mapJobs = new HashMap<Integer, Integer>();
			}
			if (chainedReducerList != null && chainedReducerList.size() > 0) {
				reduceJobs = new HashMap<Integer, Integer>();
			}
			this.insnArr = instructions.toArray();
			// 2nd pass: Process the chained tasks based on information
			// available from 1st pass
			processChinedTaskFromInstructionsArray(false);
		}
		visitMaxs(0, 0);
		accept(mv);
	}
	
	/**
	 * *
	 * This method process chained task instruction by collecting information and perform basic operations.
	 *
	 * @param isFirstPass boolean
	 */ 
	private void processChinedTaskFromInstructionsArray(boolean isFirstPass) {
		for (AbstractInsnNode abstractInsnNode : insnArr) {
			if (abstractInsnNode instanceof MethodInsnNode) {
				MethodInsnNode min = (MethodInsnNode) abstractInsnNode;

				String minOwner = min.owner;
				String minName = min.name;

				if (minOwner.equals(CLASSNAME_CHAINMAPPPER)) {
					if (minName.equals(ADD_MAPPER)) {
						if(isFirstPass){
							preProcessChainedTask(min, true);

						}else{
							processChainedTask(min, true);
						}
					}
				} else if (minOwner.equals(CLASSNAME_CHAINREDUCER) && (minName.equals(ADD_MAPPER)
						|| minName.equals(SET_REDUCER))) {
					
						if(isFirstPass){
							preProcessChainedTask(min, false);
						}else{
							processChainedTask(min, false);
						}
					
				}
			}
		}
	}

	/**
	 * <p>
	 * Finds information about the ChainReducer.setReducer() &
	 * ChainReducer.addMapper() methods
	 * </p>
	 * 
	 * @param min
	 *            Method instruction node which represents the above methods
	 * @param isMapTask
	 *            Whether the task is map or reduce
	 * @see ChainReducer#setReducer(JobConf, Class, Class, Class, Class, Class,
	 *      boolean, JobConf)
	 * @see ChainReducer#addMapper(JobConf, Class, Class, Class, Class, Class,
	 *      boolean, JobConf)
	 */
	private void preProcessChainedTask(MethodInsnNode min, boolean isMapTask) {
		AbstractInsnNode ain = null;

		// getting variable index of the task conf object
		ain = min.getPrevious();
		int taskConfIndex = getTaskJobConfIndex(ain);

		InsnList insnToMove = null;
		// if the task conf is passed as new JobConf() or using a method call,
		// we need to move these instructions above, add a new local variable
		// for
		// the conf & pass this local variable to the method call
		if (ain instanceof MethodInsnNode) {
			addLocalJobConf(taskConfIndex);
			// add a new VarInsnNode to load the new conf
			AbstractInsnNode vin = new VarInsnNode(Opcodes.ALOAD, taskConfIndex);
			instructions.insert(ain, vin);

			insnToMove = deleteInsnsAndMove(ain);

			// storing the value returned by the moved insn to the conf variable
			insnToMove.add(new VarInsnNode(Opcodes.ASTORE, taskConfIndex));

			// setting the newly added vin as the current node for further
			// traversal, as we have removed the above ain node.
			ain = vin;
		}

		// getting node which represents parameter for Mapper/Reducer class
		AbstractInsnNode mrClassParamNode = getMRClassParamNode(ain);
		Object mrClassParam = getMRClassParam(mrClassParamNode);

		// getting variable index for Job Conf object
		AbstractInsnNode jobNode = getJobConfNode(mrClassParamNode);
		int jobVariableIndex = ((VarInsnNode) jobNode).var;

		// insert the instructions to be moved
		if (insnToMove != null) {
			instructions.insertBefore(jobNode, insnToMove);
		}

		if (isMapTask) {
			addMapJobAndTaskConfToIndex(taskConfIndex, mrClassParam, jobVariableIndex);
		} else {
			addReducerJobAndTaskConfToIndex(taskConfIndex, mrClassParam, jobVariableIndex);
		}
	}

	/**
	 * Adds the reducer job and task conf to index.
	 *
	 * @param taskConfIndex the task conf index
	 * @param mrClassParam the mr class param
	 * @param jobVariableIndex the job variable index
	 */
	private void addReducerJobAndTaskConfToIndex(int taskConfIndex, Object mrClassParam, int jobVariableIndex) {
		if (chainedReducerList == null) {
			chainedReducerList = new HashMap<Integer, List<Integer>>();
		}
		// adding the job conf variable and the task conf variable indexes
		// to a map
		List<Integer> list = chainedReducerList.get(jobVariableIndex);
		if (list == null) {
			list = new ArrayList<Integer>();
		}
		list.add(taskConfIndex);
		chainedReducerList.put(jobVariableIndex, list);

		if (chainReducerInfoList == null) {
			chainReducerInfoList = new HashMap<Integer, List<Object>>();
		}
		List<Object> infoList = chainReducerInfoList.get(jobVariableIndex);
		if (infoList == null) {
			infoList = new ArrayList<Object>();
		}
		infoList.add(mrClassParam);
		chainReducerInfoList.put(jobVariableIndex, infoList);
	}

	/**
	 * Adds the map job and task conf to index.
	 *
	 * @param taskConfIndex the task conf index
	 * @param mrClassParam the mr class param
	 * @param jobVariableIndex the job variable index
	 */
	private void addMapJobAndTaskConfToIndex(int taskConfIndex, Object mrClassParam, int jobVariableIndex) {
		if (chainedMapperList == null) {
			chainedMapperList = new HashMap<Integer, List<Integer>>();
		}
		// adding the job conf variable and the task conf variable indexes
		// to a map
		List<Integer> list = chainedMapperList.get(jobVariableIndex);
		if (list == null) {
			list = new ArrayList<Integer>();
		}
		list.add(taskConfIndex);
		chainedMapperList.put(jobVariableIndex, list);

		if (chainMapperInfoList == null) {
			chainMapperInfoList = new HashMap<Integer, List<Object>>();
		}
		List<Object> infoList = chainMapperInfoList.get(jobVariableIndex);
		if (infoList == null) {
			infoList = new ArrayList<Object>();
		}
		infoList.add(mrClassParam);
		
		chainMapperInfoList.put(jobVariableIndex, infoList);
	}

	/**
	 * <p>
	 * Finds the variable index of JobConf object for the chained task
	 * </p>.
	 *
	 * @param ain Node representing the addition of chained task
	 * @return int index
	 */
	private int getTaskJobConfIndex(AbstractInsnNode ain) {
		int taskIndex = -1;
		if (ain instanceof VarInsnNode) {
			taskIndex = ((VarInsnNode) ain).var;
		} else if (ain instanceof MethodInsnNode) {
			taskIndex = localVariables.size();
		}

		return taskIndex;
	}

	/**
	 * <p>
	 * Finds the variable index of JobConf object for the job
	 * </p>.
	 *
	 * @param ain Node representing the addition of chained task
	 * @return int index of the JobConf variable
	 */
	private AbstractInsnNode getJobConfNode(AbstractInsnNode ain) {
		// 1st parameter: JobConf object
		return ain.getPrevious();
	}

	/**
	 * <p>
	 * If 8th parameter is passed as a method call, the instructions need to be
	 * replaced with a variable. And the instructions need to moved above the
	 * method call.
	 * </p>
	 * <p>
	 * It also removes the instructions to be moved, from the original
	 * instructions.
	 * </p>
	 * <ul>
	 * <li>new JobConf(false)
	 * <li>getJobConf(). This is just an example.
	 * </ul>
	 *
	 * @param ain Method representing the method call
	 * @return InsnList Instructions to move
	 */
	private InsnList deleteInsnsAndMove(AbstractInsnNode ain) {
		InsnList insnToMove = new InsnList();
		LabelNode ln = new LabelNode();
		insnToMove.add(ln);

		int initIndex = CollectionUtil.getObjectIndexInArray(this.insnArr, ain);
		MethodInsnNode tempMIN = null;

		/**
		 * Number of instructions to be moved = number of method arguments.
		 * 
		 * If an argument happens to be a method call, add its argument size to
		 * the number of instruction to move.
		 * 
		 * If a method call is creation of new object (e.g. new JobConf()), add
		 * 2 to the total count
		 */
		int numInsmToMove = 0;
		AbstractInsnNode tempAIN = null;
		for (int k = initIndex; k >= initIndex - numInsmToMove; k--) {
			tempAIN = insnArr[k];
			/**
			 * add the clone to the move list. We are traversing in reverse
			 * order, hence adding the instruction to be moved at the beginning.
			 */
			insnToMove.insert(tempAIN.clone(null));

			// removing the original instruction
			instructions.remove(insnArr[k]);

			// recalculating the number of instructions to be moved
			if (tempAIN instanceof MethodInsnNode) {
				tempMIN = (MethodInsnNode) tempAIN;
				numInsmToMove += Type.getArgumentTypes(tempMIN.desc).length;
				if (tempMIN.name.equals(INIT_METHOD)) {
					numInsmToMove += 2;
				}
			}
		}
		return insnToMove;
	}

	/**
	 * <p>
	 * Processes a task found in the ChainMapper
	 * </p>.
	 *
	 * @param min method representing ChainMapper.addMapper method
	 * @param isMapTask Whether the task is map or reduce
	 */
	private void processChainedTask(MethodInsnNode min, boolean isMapTask) {
		AbstractInsnNode ain = min.getPrevious();
		int taskConfIndex = getTaskJobConfIndex(ain);
		AbstractInsnNode jobNode = getJobConfNode(getMRClassParamNode(ain));
		int jobVariableIndex = ((VarInsnNode) jobNode).var;

		InsnList insn = null;

		if (isMapTask) {
			// log configuration settings
			boolean loadLogger = true;
			if (mapJobs.containsKey(jobVariableIndex)) {
				loadLogger = false;
				int loggerKount = mapJobs.get(jobVariableIndex);
				mapJobs.put(jobVariableIndex, loggerKount + 1);
			} else {
				loadLogger = true;
				mapJobs.put(jobVariableIndex, 0);
			}
			int loggerNumber = mapJobs.get(jobVariableIndex);

			// inserting the logger configuration settings
			insn = configureJobConf(taskConfIndex, loadLogger, loggerNumber,
					(chainedMapperList.get(jobVariableIndex)).size(), isMapTask);
			if (loadLogger) {
				insn.add(createChainInfoList(taskConfIndex, 
						chainMapperInfoList.get(jobVariableIndex)));
			}
		} else {
			// log configuration settings
			boolean loadLogger = true;
			if (reduceJobs.containsKey(jobVariableIndex)) {
				loadLogger = false;
				int loggerKount = reduceJobs.get(jobVariableIndex);
				reduceJobs.put(jobVariableIndex, loggerKount + 1);
			} else {
				loadLogger = true;
				reduceJobs.put(jobVariableIndex, 0);
			}
			int loggerNumber = reduceJobs.get(jobVariableIndex);

			// inserting the logger configuration settings
			insn = configureJobConf(taskConfIndex, loadLogger, loggerNumber,
					(chainedReducerList.get(jobVariableIndex)).size(),
					isMapTask);
			if (loadLogger) {
				insn.add(createChainInfoList(taskConfIndex,
						chainReducerInfoList.get(jobVariableIndex)));
			}
		}
		instructions.insertBefore(jobNode, insn);
	}

	/**
	 * <p>
	 * Configures the JobConf object for the task.
	 * </p>
	 *
	 * @param confVariableIndex variable index for the task JobConf
	 * @param loadLogger Whether logger to be loaded or not
	 * @param loggerNumber Logger number for this task
	 * @param loggerKount Total number of loggers
	 * @param isMapTask the is map task
	 * @return the insn list
	 */
	private InsnList configureJobConf(int confVariableIndex,
			boolean loadLogger, int loggerNumber, int loggerKount,
			boolean isMapTask) {
		InsnList list = new InsnList();
		list.add(new LabelNode());
		list.add(new VarInsnNode(Opcodes.ALOAD, confVariableIndex));
		list.add(new LdcInsnNode(InstrumentConstants.FIELD_LOADLOGGER));
		list.add(new InsnNode(loadLogger ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_JOB_CONF,
				"setBoolean", Type.getMethodDescriptor(Type.VOID_TYPE,
						TYPE_STRING, Type.BOOLEAN_TYPE)));

		list.add(new LabelNode());
		list.add(new VarInsnNode(Opcodes.ALOAD, confVariableIndex));
		list.add(new LdcInsnNode(InstrumentConstants.FIELD_LOGGERNUMBER));
		list.add(new LdcInsnNode(loggerNumber));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_JOB_CONF,
				"setInt", Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
						Type.INT_TYPE)));

		list.add(new LabelNode());
		list.add(new VarInsnNode(Opcodes.ALOAD, confVariableIndex));
		list.add(new LdcInsnNode(InstrumentConstants.FIELD_LOGGERCOUNT));
		list.add(new LdcInsnNode(loggerKount));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_JOB_CONF,
				"setInt", Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
						Type.INT_TYPE)));

		// Adding profilePartitioner boolean variable. If this is the last
		// mapper set this variable to true else for every
		// other mapper this variable should be false
		list.add(new LabelNode());
		list.add(new VarInsnNode(Opcodes.ALOAD, confVariableIndex));
		list.add(new LdcInsnNode(InstrumentConstants.PROFILE_PARTITIONER));
		if (isMapTask) {
			list.add(new InsnNode(
					(loggerNumber == (loggerKount - 1)) ? Opcodes.ICONST_1
							: Opcodes.ICONST_0));
		} else {
			list.add(new InsnNode(Opcodes.ICONST_0));
		}
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_JOB_CONF,
				"setBoolean", Type.getMethodDescriptor(Type.VOID_TYPE,
						TYPE_STRING, Type.BOOLEAN_TYPE)));

		// Unload logger in the last mapper only
		list.add(new LabelNode());
		list.add(new VarInsnNode(Opcodes.ALOAD, confVariableIndex));
		list.add(new LdcInsnNode(InstrumentConstants.FIELD_UNLOADLOGGER));
		list.add(new InsnNode(
				(loggerNumber == (loggerKount - 1)) ? Opcodes.ICONST_1
						: Opcodes.ICONST_0));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_JOB_CONF,
				"setBoolean", Type.getMethodDescriptor(Type.VOID_TYPE,
						TYPE_STRING, Type.BOOLEAN_TYPE)));
		return list;
	}

	/**
	 * <p>
	 * Adds a local variable for chained mapper task
	 * </p>.
	 *
	 * @param varIndex Variable index
	 */
	@SuppressWarnings({ "unchecked" })
	private void addLocalJobConf(int varIndex) {
		LabelNode begin = new LabelNode();
		LabelNode end = new LabelNode();
		instructions.insertBefore(instructions.getFirst(), begin);
		instructions.insert(instructions.getLast(), end);
		LocalVariableNode lv = new LocalVariableNode(LOCAL_CONF_VARIABLE_NAME
				+ ++localJobConfAdded, Type.getDescriptor(JobConf.class), null,
				begin, end, varIndex);
		localVariables.add(lv);
	}

	/**
	 * <p>
	 * Finds the node for Mapper/Reducer class param (2nd parameter)
	 * </p>.
	 *
	 * @param ain Node representing the addition of chained task
	 * @return AbstractInsnNode mapper/reducer class param node
	 */
	private AbstractInsnNode getMRClassParamNode(final AbstractInsnNode ain) {
		// 7th parameter
		AbstractInsnNode secondParameterInstNode=ain;
		secondParameterInstNode = secondParameterInstNode.getPrevious();

		// 3rd to 6th parameters
		secondParameterInstNode = secondParameterInstNode.getPrevious().getPrevious().getPrevious().getPrevious();

		// 2nd parameter
		return secondParameterInstNode.getPrevious();
	}

	/**
	 * <p>
	 * Value of the mapper/reducer class
	 * </p>
	 * <ul>
	 * <li>If value is passed as a variable, the variable index is returned
	 * <li>If value is passed as constant, the value is returned
	 * </ul>.
	 *
	 * @param ain mapper/reducer class param node
	 * @return Object
	 */
	private Object getMRClassParam(AbstractInsnNode ain) {
		Object obj = null;
		if (ain instanceof VarInsnNode) {
			VarInsnNode vin = (VarInsnNode) ain;
			obj = vin.var;
		} else if (ain instanceof LdcInsnNode) {
			LdcInsnNode ldcNode = (LdcInsnNode) ain;
			obj = ldcNode.cst;
		}

		return obj;
	}

	/**
	 * <p>
	 * Provides list of instructions to add comma separator list of chained
	 * classes to the JobConf
	 * </p>.
	 *
	 * @param confVariableIndex Variable index at which JobConf resides
	 * @param list List of mapper/reducer class values in the chain
	 * @return InsnList Instructions
	 */
	private InsnList createChainInfoList(int confVariableIndex,List<Object> list) {
		InsnList il = new InsnList();

		il.add(new VarInsnNode(Opcodes.ALOAD, confVariableIndex));
		il.add(new LdcInsnNode("jChainedClasses"));
		il.add(new TypeInsnNode(NEW, CLASSNAME_STRINGBUILDER));
		il.add(new InsnNode(DUP));
		il.add(new MethodInsnNode(INVOKESPECIAL, CLASSNAME_STRINGBUILDER,
				INIT_METHOD, Type.getMethodDescriptor(Type.VOID_TYPE)));

		for (Object object : list) {
			il.add(new LdcInsnNode(","));
			il.add(new MethodInsnNode(INVOKEVIRTUAL, CLASSNAME_STRINGBUILDER,
					"append", Type.getMethodDescriptor(TYPE_STRINGBUILDER,
							TYPE_STRING)));
			if (object instanceof Integer) {
				il.add(new VarInsnNode(ALOAD, ((Integer) object)));
				il.add(new MethodInsnNode(INVOKEVIRTUAL, CLASSNAME_OBJECT,
						"toString", Type.getMethodDescriptor(TYPE_STRING)));
				il.add(new MethodInsnNode(INVOKESTATIC, CLASSNAME_TYPE,
						"getType", Type.getMethodDescriptor(TYPE_TYPE,
								TYPE_STRING)));
				il.add(new MethodInsnNode(INVOKEVIRTUAL, CLASSNAME_TYPE,
						"getClassName", Type.getMethodDescriptor(TYPE_STRING)));
			} else if (object instanceof Type) {
				il.add(new LdcInsnNode(Type.getType(object.toString())
						.getClassName()));
			}
			il.add(new MethodInsnNode(INVOKEVIRTUAL, CLASSNAME_STRINGBUILDER,
					"append", Type.getMethodDescriptor(TYPE_STRINGBUILDER,
							TYPE_STRING)));
		}
		il.add(new InsnNode(ICONST_1));
		il.add(new MethodInsnNode(INVOKEVIRTUAL, CLASSNAME_STRINGBUILDER,
				"substring", Type.getMethodDescriptor(TYPE_STRING,
						Type.INT_TYPE)));
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_JOB_CONF,
				"set", Type.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
						TYPE_STRING)));

		return il;
	}
}