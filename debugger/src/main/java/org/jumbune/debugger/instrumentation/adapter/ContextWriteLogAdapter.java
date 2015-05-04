package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.job.Config;
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
 * This adapter is used to add logging statements for context.write method calls
 * in map/reduce methods.
 * <p>
 * </p>
 * 
 */
public class ContextWriteLogAdapter extends BaseAdapter {
	private static final Logger LOGGER = LogManager
			.getLogger(ContextWriteLogAdapter.class);

	/**
	 * <p>
	 * Create a new instance of ContextWriteLogAdapter.
	 * </p>
	 * 
	 * @param cv
	 *            Class visitor
	 */
	public ContextWriteLogAdapter(Config config, ClassVisitor cv) {
		super(config, Opcodes.ASM4);
		this.cv = cv;
	}

	/* (non-Javadoc)
	 * @see org.jumbune.debugger.instrumentation.adapter.BaseAdapter#visitEnd()
	 */
	@Override
	public void visitEnd() {
		if (isMapperClass() || isReducerClass()) {
			for (Object o : methods) {
				MethodNode mn = (MethodNode) o;
				InsnList insnList = mn.instructions;
				AbstractInsnNode[] insnArr = insnList.toArray();
				int writeCount = 0;

				// traversing the instructions
				for (AbstractInsnNode abstractInsnNode : insnArr) {
					if (abstractInsnNode instanceof MethodInsnNode) {
						MethodInsnNode min = (MethodInsnNode) abstractInsnNode;

						// write method
						if (InstrumentUtil.isOutputMethod(min)) {
							writeCount++;
							LOGGER.info(MessageFormat.format(
									InstrumentationMessageLoader
											.getMessage(MessageConstants.LOG_MAPREDUCE_CTXWRITE_CALL),
									getClassName() + "##" + mn.name, writeCount));
							String logMsg = null;

							// mapper
							if (isMapperClass()) {
								logMsg = InstrumentationMessageLoader
										.getMessage(MessageConstants.MAPPER_CONTEXT_WRITE);
							}
							// combiner or reducer
							else if (isReducerClass()) {
								logMsg = InstrumentationMessageLoader
										.getMessage(MessageConstants.REDUCER_CONTEXT_WRITE);
							}

							InsnList il = InstrumentUtil
									.addMapReduceContextWriteLogging(
											getLogClazzName(), mn.name, logMsg);

							insnList.insertBefore(abstractInsnNode, il);

							// setting loggernumber to ThreadLocal
							InsnList lll = new InsnList();
							lll.add(new LabelNode());
							lll.add(new VarInsnNode(Opcodes.ALOAD, 0));
							lll.add(new FieldInsnNode(
									Opcodes.GETFIELD,
									ConfigurationUtil.convertQualifiedClassNameToInternalName(getClassName()),
									InstrumentConstants.FIELD_LOGGERNUMBER, "I"));
							lll.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
									CLASSNAME_MAPREDUCEEXECUTIL,
									"setLoggerNumber", Type
											.getMethodDescriptor(
													Type.VOID_TYPE,
													Type.INT_TYPE)));

							insnList.insert(abstractInsnNode, lll);
						}
					}
				}
				mn.visitMaxs(0, 0);
			}
		}
		accept(cv);
	}

}
