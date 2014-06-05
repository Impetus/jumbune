package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.utils.FileUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;


/**
 * This adapter is used to load classes at runtime as soon as main method is invoked
 * <p>
 * </p>
 */
public class MainAdapter extends BaseAdapter {
	private static final Logger LOGGER = LogManager
			.getLogger(MainAdapter.class);

	/**
	 * <p>
	 * Create a new instance of MainAdapter.
	 * </p>
	 * 
	 * @param cv
	 *            Class visitor
	 */
	public MainAdapter(YamlLoader loader, ClassVisitor cv) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
	}

	/**
	 * @see org.jumbune.debugger.instrumentation.adapter.BaseAdapter#visitEnd()
	 */
	@Override
	public void visitEnd() {
		for (Object o : methods) {
			MethodNode mn = (MethodNode) o;

			// finding main method
			if (InstrumentUtil.validateMethodName(mn.name, MAIN_METHOD)
					&& Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC == mn.access
					&& Type.getMethodDescriptor(Type.VOID_TYPE,
							TYPE_STRING_ARRAY).equals(mn.desc)) {
				LOGGER.info(MessageFormat.format(InstrumentationMessageLoader
						.getMessage(MessageConstants.MAIN_METHOD_FOUND),
						getClassName()));

				InsnList insnList = mn.instructions;

				InsnList il = new InsnList();
				il.add(loadClasses());

				// insert as first instruction
				insnList.insertBefore(insnList.getFirst(), il);
				break;
			}
			mn.visitMaxs(0, 0);
		}
		accept(cv);
	}

	/**
	 * <p>
	 * This method provides instructions to inject method call to load classes
	 * </p>
	 * 
	 * @return Instructions
	 */
	private InsnList loadClasses() {
		InsnList il = new InsnList();
		il.add(new LabelNode());
		il.add(new LdcInsnNode(CollectionUtil.createStringFromList(FileUtil
				.getClassPathFileList(getLoader()))));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				CLASSNAME_CLASSLOADER_UTIL, "loadClasses", Type
						.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING)));

		return il;
	}
}
