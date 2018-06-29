package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.job.Config;
import org.jumbune.debugger.instrumentation.utils.Environment;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;


/**
 * <p>
 * This adapter is used to add logging statements at method entry and exit.
 * </p>
 * Following methods are excluded:
 * <ul>
 * <li>map method (handled in {@link MREntryExitAdapter})
 * <li>reduce method (handled in {@link MREntryExitAdapter})
 * <li>default constructor
 * <li>static block
 * <li>synthetic methods
 * </ul>
 * 
 */
public class MethodEntryExitAdapter extends BaseAdapter {
	private static Logger logger = LogManager
			.getLogger(MethodEntryExitAdapter.class);

	private Environment env;
	/**
	 * <p>
	 * Create a new instance of ReturnAdapter.
	 * </p>
	 * 
	 * @param cv
	 *            Class visitor
	 */
	public MethodEntryExitAdapter(Config config, ClassVisitor cv) {
		super(config, Opcodes.ASM4);
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
	public MethodEntryExitAdapter(Config config, ClassVisitor cv,Environment env) {
		super(config, Opcodes.ASM4);
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

			// filtering the methods
			if (!(validateMapReduceClinitMethod(mn.name,MAP_METHOD , REDUCE_METHOD,CLINIT_METHOD)
					|| checkMethodNameAndArgumentLength(mn)
					|| (mn.access & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC)) {

				InsnList insnList = mn.instructions;
				AbstractInsnNode[] insnArr = insnList.toArray();

				// adding entry logging
				logger.debug(MessageFormat.format(InstrumentationMessageLoader
						.getMessage(MessageConstants.LOG_METHOD_ENTRY),
						getClassName() + "##" + mn.name + "##" + mn.desc));

				String logMsg = InstrumentationMessageLoader
						.getMessage(MessageConstants.ENTERED_METHOD);
				
				String cSymbol = env.getClassSymbol(getClassName());
				String mSymbol = env.getMethodSymbol(getClassName(),cSymbol, mn.name);
				
				InsnList il = InstrumentUtil.addLogMessage(cSymbol,
						mSymbol, logMsg);
				insnList.insertBefore(insnList.getFirst(), il);

				for (AbstractInsnNode abstractInsnNode : insnArr) {
					if (Opcodes.RETURN >= abstractInsnNode.getOpcode()
							&& Opcodes.IRETURN <= abstractInsnNode.getOpcode()) {
						// adding exit logging
						logger.debug(MessageFormat.format(
								InstrumentationMessageLoader
										.getMessage(MessageConstants.LOG_METHOD_EXIT),
								getClassName() + "##" + mn.name));

						logMsg = InstrumentationMessageLoader
								.getMessage(MessageConstants.EXITING_METHOD);
						cSymbol = env.getClassSymbol(getClassName());
						mSymbol = env.getMethodSymbol(getClassName(),cSymbol,mn.name);
						il = InstrumentUtil.addLogMessage(cSymbol,
								mSymbol, logMsg);

						// inserting the list at the associated label node
						AbstractInsnNode prevNode = abstractInsnNode
								.getPrevious();
						while (!(prevNode instanceof LabelNode)) {
							prevNode = prevNode.getPrevious();
						}
						insnList.insert(prevNode, il);
					}
				}
			}
			mn.visitMaxs(0, 0);
		}
		accept(cv);
	}
	/***
	 * it checks for main,reduce,clinit methods in instructions. 
	 * @param name,
	 * name of the method to check
	 * @param mainMethod,
	 *  main Method instruction in ASM
	 * @param reduceMethod,
	 *  reduce method instruction in ASM
	 * @param clinitMethod,
	 *  clinit method instruction in ASM
	 * @return
	 */
	private boolean validateMapReduceClinitMethod(String name, String mainMethod, String reduceMethod,String clinitMethod) {
		return InstrumentUtil.validateMethodName(name,mainMethod)||InstrumentUtil.validateMethodName(name, reduceMethod)
		|| InstrumentUtil.validateMethodName(name, clinitMethod);
	}
	/***
	 * checks whether a method is init or not and if its init then its argument length should be zero
	 * @param mn methodnode for obtaining name from a methodnode
	 * @return true 
	 */
	private boolean checkMethodNameAndArgumentLength(MethodNode mn) {
		return (InstrumentUtil.validateMethodName(mn.name, INIT_METHOD) && Type
				.getArgumentTypes(mn.desc).length == 0);
	}
}