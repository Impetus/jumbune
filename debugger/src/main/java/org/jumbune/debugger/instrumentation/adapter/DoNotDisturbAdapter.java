package org.jumbune.debugger.instrumentation.adapter;

import org.jumbune.common.yaml.config.Loader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;


/**
 * <p>
 * This adapter does nothing. It should be used if a class needs to be used as it is.
 * </p>
 */
public class DoNotDisturbAdapter extends BaseAdapter {
	/**
	 * <p>
	 * Create a new instance of DoNotDisturbAdapter.
	 * </p>
	 * 
	 * @param cv
	 *            Class Visitor
	 */
	public DoNotDisturbAdapter(Loader loader, ClassVisitor cv) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
	}
}
