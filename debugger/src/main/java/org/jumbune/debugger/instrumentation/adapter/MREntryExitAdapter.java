package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.job.Config;
import org.jumbune.debugger.instrumentation.utils.Environment;
import org.jumbune.debugger.instrumentation.utils.InstrumentConstants;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;


/**
 * This adapter is used to add logging statements for entry/exit of map/reduce
 * methods.
 * 
 */
public class MREntryExitAdapter extends BaseAdapter {
	private static final Logger LOGGER = LogManager
			.getLogger(MREntryExitAdapter.class);

	private Environment env;
	/**
	 * <p>
	 * Create a new instance of MREntryExitAdapter.
	 * </p>
	 * 
	 * @param cv
	 *            Class visitor
	 */
	public MREntryExitAdapter(Config config, ClassVisitor cv) {
		super(config, Opcodes.ASM4);
		this.cv = cv;
	}
	

	/**
	 *  <p>
	 * Create a new instance of MREntryExitAdapter.
	 * </p>
	 * @param loader
	 * @param cv
	 * @param env
	 */
	public MREntryExitAdapter(Config config, ClassVisitor cv,Environment env) {
		super(config, Opcodes.ASM4);
		this.cv = cv;
		this.env = env;
	}


	/**
	 * visit end for instrumentation of map-reduce methods
	 */
	@Override
	public void visitEnd() {
		if (isMapperClass() || isReducerClass()) {
			for (Object o : methods) {
				MethodNode mn = (MethodNode) o;
				/**
				 * Valid map/reduce method
				 */
				if (InstrumentUtil.validateMapReduceMethod(mn)) {
					InsnList insnList = mn.instructions;
					AbstractInsnNode[] insnArr = insnList.toArray();

					// adding entry logging
					LOGGER.debug(MessageFormat.format(
							InstrumentationMessageLoader
									.getMessage(MessageConstants.LOG_MAPREDUCE_METHOD_ENTRY),
							getClassName() + "##" + mn.name + "##" + mn.desc));
					String logMsg = new StringBuilder(
							MessageFormat.format(
									InstrumentationMessageLoader
											.getMessage(MessageConstants.ENTERED_MAPREDUCE),
									mn.name)).toString();

					// setting the logger number in ThreadLocal
					InsnList il1 = new InsnList();
					il1.add(new LabelNode());
					il1.add(new VarInsnNode(Opcodes.ALOAD, 0));
					il1.add(new FieldInsnNode(
							Opcodes.GETFIELD,
							ConfigurationUtil.convertQualifiedClassNameToInternalName(getClassName()),
							InstrumentConstants.FIELD_LOGGERNUMBER, "I"));
					il1.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
							CLASSNAME_MAPREDUCEEXECUTIL, "setLoggerNumber",
							Type.getMethodDescriptor(Type.VOID_TYPE,
									Type.INT_TYPE)));
					
					String symbol = env.getClassSymbol(getClassName());
					il1.add(InstrumentUtil.addLogMessage(symbol,
							mn.name, logMsg));

					il1.add(addMapCounter(mn));
					insnList.insertBefore(insnList.getFirst(), il1);

					// traversing the instructions for exit logging
					for (AbstractInsnNode abstractInsnNode : insnArr) {
						// return statement
						if (abstractInsnNode.getOpcode() >= Opcodes.IRETURN
								&& abstractInsnNode.getOpcode() <= Opcodes.RETURN) {
							LOGGER.debug(MessageFormat.format(
									InstrumentationMessageLoader
											.getMessage(MessageConstants.LOG_MAPREDUCE_METHOD_EXIT),
									getClassName() + "##" + mn.name));
							String logMsg2 = new StringBuilder(
									MessageFormat.format(
											InstrumentationMessageLoader
													.getMessage(MessageConstants.EXITING_MAPREDUCE),
											mn.name)).toString();
							
							symbol = getLogClazzName(); 
							InsnList il = InstrumentUtil.addLogMessage(
									symbol, mn.name, logMsg2);
							insnList.insert(abstractInsnNode.getPrevious(), il);
						}
					}
				}
				mn.visitMaxs(0, 0);
			}
		}
		accept(cv);
	}

	/***
	 * It gives instructions for incrementing counter for map method only.
	 * 
	 * @param mn
	 *            - current methodnode
	 * @return - Instructions for incrementing mapCounter
	 */
	private InsnList addMapCounter(MethodNode mn) {
		InsnList il1 = new InsnList();
		if (InstrumentUtil.validateMapMethod(mn)) {
			il1.add(InstrumentUtil.incrementIntBy1(getClassName(),
					MAP_REDUCE_COUNTER));
		}
		return il1;
	}
}
