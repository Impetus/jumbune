package org.jumbune.debugger.instrumentation.adapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.debugger.instrumentation.utils.Environment;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;


/**
 * <p>
 * This adapter is used to add logging statements before all the return
 * statements in a class.
 * </p>
 * 
 */
@Deprecated
public class ReturnAdapter extends BaseAdapter {
	private static Logger logger = LogManager.getLogger(ReturnAdapter.class);
	private Environment env;
	
	/**
	 * <p>
	 * Create a new instance of ReturnAdapter.
	 * </p>
	 * 
	 * @param cv
	 *            Class visitor
	 */
	public ReturnAdapter(Loader loader, ClassVisitor cv) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
	}
	
	/**
	 * <p>
	 * Create a new instance of ReturnAdapter.
	 * </p>
	 * @param loader
	 * @param cv
	 * @param env
	 */
	public ReturnAdapter(Loader loader, ClassVisitor cv,Environment env) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
		this.env = env;
	}

	/**
	 * visit end method for intrumentation	
	 */
	@Override
	public void visitEnd() {
		for (Object o : methods) {
			MethodNode mn = (MethodNode) o;

			if (validateMethods(mn)
					&& InstrumentUtil.validateMethodName(mn.name, "<clinit>")
					&& mn.name.indexOf("access$") < 0) {

				logger.debug("instrumenting " + getClassName() + "##" + mn.name);

				InsnList insnList = mn.instructions;
				AbstractInsnNode[] insnArr = insnList.toArray();
				for (AbstractInsnNode abstractInsnNode : insnArr) {
					if (Opcodes.RETURN >= abstractInsnNode.getOpcode()
							&& Opcodes.IRETURN <= abstractInsnNode.getOpcode()) {
						String msg = new StringBuilder("[Return] [method] [] ")
								.toString();

						String cSymbol = env.getClassSymbol(getClassName());
						String mSymbol = env.getMethodSymbol(getClassName(), cSymbol, mn.name);

						InsnList il = InstrumentUtil.addReturnLogging(
								cSymbol, mSymbol, msg);
						insnList.insertBefore(abstractInsnNode, il);
					}
				}
			}
			mn.visitMaxs(0, 0);
		}
		accept(cv);
	}

	private boolean validateMethods(MethodNode mn) {
		return InstrumentUtil.validateMethodName(mn.name, "map")
		&& InstrumentUtil.validateMethodName(mn.name, "reduce")
		&& InstrumentUtil.validateMethodName(mn.name, "<init>");
	}
}
