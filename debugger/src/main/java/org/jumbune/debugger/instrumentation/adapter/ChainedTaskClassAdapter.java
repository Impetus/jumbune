package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.job.Config;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 * This adapter traverses the classes and passes to the method adapter for
 * finding and processing chained tasks at method level.
 * 
 
 */

public class ChainedTaskClassAdapter extends BaseAdapter {

	private static Logger logger = LogManager
			.getLogger(ChainedTaskMethodAdapter.class);

	/**
	 * <p>
	 * Create a new instance of BaseAdapter.
	 * </p>
	 * 
	 * @param cv
	 */
	public ChainedTaskClassAdapter(Config config, ClassVisitor cv) {
		super(config, Opcodes.ASM4);
		this.cv = cv;
	}

	/**
	 * Called when a method is visited
	 * 
	 * @see org.objectweb.asm.tree.ClassNode#visitMethod(int, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String[])
	 */
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		logger.debug(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.LOG_INSTRUMENTING_METHOD),
				getClassName() + "##" + name));
		MethodVisitor mv = super.visitMethod(access, name, desc, signature,
				exceptions);
		// no need to instrument synthetic method
		if ((access & Opcodes.ACC_SYNTHETIC) == 0) {
			return new ChainedTaskMethodAdapter(access, name, desc, signature,
					exceptions, mv, getClassName());
		} else {
			return mv;
		}
	}

	@Override
	public void visitEnd() {
		accept(cv);
	}
}