package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.debugger.instrumentation.utils.Environment;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 * <p>
 * This adapter traverses a class and passes to the method adapter to add logging statements before and after all the if blocks in method.
 * </p>
 * 
 */
public class BlockLogAdapter extends BaseAdapter {
	private static Logger logger = LogManager.getLogger(BlockLogAdapter.class);

	private Environment env;

	/**
	 * <p>
	 * Create a new instance of BlockLogAdapter.
	 * </p>
	 * 
	 * @param cv
	 *            class visitor
	 */
	public BlockLogAdapter(Loader loader, ClassVisitor cv) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
	}

	/**
	 * <p>
	 * Create a new instance of BlockLogAdapter.
	 * </p>
	 * @param loader
	 * @param cv
	 * @param env
	 */
	public BlockLogAdapter(Loader loader, ClassVisitor cv, Environment env) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
		this.env = env;
	}

	/**
	 * Called when a method is visited
	 * 
	 * @see org.objectweb.asm.tree.ClassNode#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		logger.info(MessageFormat.format(InstrumentationMessageLoader.getMessage(MessageConstants.LOG_INSTRUMENTING_METHOD), getClassName() + "##"
				+ name));
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		// no need to instrument synthetic method
		if ((access & Opcodes.ACC_SYNTHETIC) == 0) {
			return new BlockLogMethodAdapter(getLoader(), access, name, desc, signature, exceptions, mv, getClassName(), getLogClazzName(), env);
		} else {
			return mv;
		}
	}
}