package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.debugger.instrumentation.utils.Environment;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;


/**
 * <p>
 * Once a class is instrumented, this class adds a marker interface to class to
 * indicate that the class has been instrumented.
 * </p>
 * 

 */
public class InstrumentFinalizer extends BaseAdapter {
	private static final Logger LOGGER = LogManager
			.getLogger(InstrumentFinalizer.class);
	
	private Environment env;

	/**
	 * <p>
	 * Create a new instance of InstrumentFinalizer.
	 * </p>
	 * 
	 * @param cv
	 *            Class visitor
	 */
	public InstrumentFinalizer(Loader loader, ClassVisitor cv) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
	}

	/**
	 * <p>
	 * Create a new instance of InstrumentFinalizer.
	 * </p>
	 * @param loader
	 * @param cv
	 * @param env
	 */
	public InstrumentFinalizer(Loader loader, ClassVisitor cv,Environment env) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
		this.env = env;
	}

	
	/**
	 * This method is called when a class being visited
	 * 
	 * @param name
	 *            In the format "util/a/b/c"
	 * @see org.jumbune.debugger.instrumentation.adapter.BaseAdapter#visit(int,
	 *      int, java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String[])
	 */
	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		setClassName(name);

		// Do not attempt to instrument interfaces or classes that have already
		// been instrumented
		if (((access & Opcodes.ACC_INTERFACE) != 0)
				|| CollectionUtil.arrayContains(interfaces,
						CLASSNAME_CLASS_HAS_BEEN_INSTRUMENTED)) {
			LOGGER.info(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.NOT_MARKED_AS_INSTRUMENTED),
					getClassName()));

			super.visit(version, access, name, signature, superName, interfaces);
		} else {
			// Flag this class as having been instrumented
			String[] newInterfaces = new String[interfaces.length + 1];
			System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
			newInterfaces[newInterfaces.length - 1] = CLASSNAME_CLASS_HAS_BEEN_INSTRUMENTED;

			super.visit(version, access, name, signature, superName,
					newInterfaces);
		}
	}

	/**
	 * gets the env
	 * @return
	 */
	public Environment getEnv() {
		return env;
	}

	/**
	 * sets the env
	 * @param env
	 */
	public void setEnv(Environment env) {
		this.env = env;
	}
	
	
	
}


