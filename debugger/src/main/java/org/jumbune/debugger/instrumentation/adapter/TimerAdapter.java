package org.jumbune.debugger.instrumentation.adapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.debugger.instrumentation.utils.Environment;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;


/**
 * <p>
 * This adapter injects code into map/reduce methods to log the execution time
 * </p>
 * 
 */
@Deprecated
public class TimerAdapter extends BaseAdapter {
	private static Logger logger = LogManager.getLogger(TimerAdapter.class);
	
	private Environment env;

	/**
	 * <p>
	 * Create a new instance of TimerAdapter.
	 * </p>
	 * 
	 * @param cv
	 *            Class Visitor
	 */
	//TODO: No ref found....
	public TimerAdapter(Loader loader, ClassVisitor cv) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
	}

	/**
	 * <p>
	 * Create a new instance of TimerAdapter.
	 * </p>
	 * @param loader
	 * @param cv
	 * @param env
	 */
	public TimerAdapter(Loader loader, ClassVisitor cv,Environment env) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
		this.env = env;
	}

	/**
	 * visit end method for intrumentation	
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void visitEnd() {
		if (isMapperClass() || isReducerClass()) {
			for (Object o : methods) {
				MethodNode mn = (MethodNode) o;
				if (InstrumentUtil.validateMapReduceMethod(mn)) {
					logger.debug("instrumenting " + getClassName() + "##"
							+ mn.name);
					InsnList list = mn.instructions;
					AbstractInsnNode[] insnArr = list.toArray();
					int variable = mn.maxLocals;

					// adding variable declaration
					InsnList il = new InsnList();
					LabelNode newLabel = new LabelNode();
					il.add(newLabel);
					il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
							"java/lang/System", "currentTimeMillis", Type
									.getMethodDescriptor(Type.LONG_TYPE)));
					il.add(new VarInsnNode(Opcodes.LSTORE, variable));
					list.insertBefore(list.getFirst(), il);

					// adding local variable
					LabelNode begin = new LabelNode();
					LabelNode end = new LabelNode();
					list.insertBefore(list.getFirst(), begin);
					list.insert(list.getLast(), end);
					Type type = Type.LONG_TYPE;
					LocalVariableNode lv = new LocalVariableNode("startMethod",
							type.getDescriptor(), null, begin, end, variable);
					mn.localVariables.add(lv);

					// finding the return statement
					for (AbstractInsnNode abstractInsnNode : insnArr) {
						if (abstractInsnNode.getOpcode() >= Opcodes.IRETURN
								&& abstractInsnNode.getOpcode() <= Opcodes.RETURN) {
							// adding logging statement
							String msg = new StringBuilder(
									"[Method executed] [time] ").toString();
							String cSymbol = env.getClassSymbol(getClassName());
							String mSymbol = env.getMethodSymbol(getClassName(), cSymbol, mn.name);
							InsnList il1 = InstrumentUtil.addTimerLogging(
									cSymbol,mSymbol, variable, msg);

							list.insertBefore(abstractInsnNode, il1);
						}
					}
				}
				mn.visitMaxs(0, 0);
			}
		}
		accept(cv);
	}
}
