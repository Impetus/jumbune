package org.jumbune.debugger.instrumentation.adapter;

import org.jumbune.common.job.Config;
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
	public DoNotDisturbAdapter(Config config, ClassVisitor cv) {
		super(config, Opcodes.ASM4);
		this.cv = cv;
	}
}
