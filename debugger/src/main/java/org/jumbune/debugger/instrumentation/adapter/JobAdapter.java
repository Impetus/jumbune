package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.utils.EnumJobSubmitMethods;
import org.jumbune.debugger.instrumentation.utils.FileUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;



/**
 * This adapter is used to add job configuration settings
 * <p>
 * </p>.
 */
public class JobAdapter extends BaseAdapter {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(JobAdapter.class);

	/**
	 * <p>
	 * Create a new instance of JobAdapter.
	 * </p>
	 *
	 * @param loader the loader
	 * @param cv Class visitor
	 */
	public JobAdapter(YamlLoader loader, ClassVisitor cv) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
	}

	/**
	 * Visit end.
	 *
	 * @see org.jumbune.debugger.instrumentation.adapter.BaseAdapter#visitEnd()
	 */
	@Override
	public void visitEnd() {
		for (Object o : methods) {
			MethodNode mn = (MethodNode) o;

			InsnList insnList = mn.instructions;
			AbstractInsnNode[] insnArr = insnList.toArray();

			LOGGER.info(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.LOG_INSTRUMENTING_METHOD),
					getClassName() + "##" + mn.name));
			// traversing in reverse order as job submission comes at the end of
			// the method
			for (AbstractInsnNode abstractInsnNode : insnArr) {
				if (abstractInsnNode instanceof MethodInsnNode) {
					MethodInsnNode min = (MethodInsnNode) abstractInsnNode;

					// finding job submission
					if (InstrumentUtil.isJobSubmissionMethod(min)) {

						LOGGER.info(MessageFormat.format(
								InstrumentationMessageLoader
										.getMessage(MessageConstants.JOB_SUBMISSION_FOUND),
								getClassName()));

						// validating if the owner is mapreduce.Job or JobClient
						if (InstrumentUtil.isOwnerJob(min)) {

							LOGGER.info(MessageFormat.format(
									InstrumentationMessageLoader
											.getMessage(MessageConstants.LOG_OWNER_IS_JOB),
									getClassName()));
							// finding index of job variable
							AbstractInsnNode ain = min.getPrevious();
							while (!(ain instanceof VarInsnNode)) {
								ain = ain.getPrevious();
							}
							VarInsnNode vin = (VarInsnNode) ain;
							int jobVariableIndex = vin.var;

							InsnList il = new InsnList();

							// classpath is to be passed as libjars
							il.add(addClasspath(jobVariableIndex,
									getOwnerType(min)));
							// // output path modification
							il.add(modifyOutputPath(jobVariableIndex,
									getOwnerType(min)));
							insnList.insertBefore(vin, il);

							// Disabling the profiling for the pure jar - old
							// api
							if (getOwnerType(min).getInternalName().equals(
									CLASSNAME_JOB_CONF)) {

								il.add(new LabelNode());
								il.add(new VarInsnNode(Opcodes.ALOAD,
										jobVariableIndex));
								il.add(new LdcInsnNode(false));
								il.add(new MethodInsnNode(
										Opcodes.INVOKEVIRTUAL,
										CLASSNAME_JOB_CONF,
										"setProfileEnabled", Type
												.getMethodDescriptor(
														Type.VOID_TYPE,
														Type.BOOLEAN_TYPE)));
							} else {
								il.add(new LabelNode());
								il.add(new VarInsnNode(Opcodes.ALOAD,
										jobVariableIndex));
								il.add(new MethodInsnNode(
										Opcodes.INVOKEVIRTUAL,
										CLASSNAME_MR_JOB, "getConfiguration",
										Type.getMethodDescriptor(Type
												.getType(Configuration.class))));

								il.add(new LdcInsnNode(PROFILE_TASK));
								il.add(new LdcInsnNode(Boolean.toString(false)));
								il.add(new MethodInsnNode(
										Opcodes.INVOKEVIRTUAL,
										CLASSNAME_HADOOP_CONFIGURATION, "set",
										Type.getMethodDescriptor(
												Type.VOID_TYPE, TYPE_STRING,
												TYPE_STRING)));
							}

							insnList.insertBefore(vin, il);
						}
					}
				}
			}
			mn.visitMaxs(0, 0);
		}
		accept(cv);
	}

	/**
	 * <p>
	 * This method finds the owner class of the job
	 * </p>.
	 *
	 * @param min MethodInsnNode method being visited
	 * @return class owner Job or JobClient
	 */
	private Type getOwnerType(MethodInsnNode min) {

		Type type = null;

		for (EnumJobSubmitMethods js : EnumJobSubmitMethods.values()) {
			if (min.owner.equals(js.getOwner().getInternalName())) {
				type = js.getOwner();
				break;
			}
		}
		// Since old api needs jobconf type and not jobclient.
		if (type.getInternalName().equals(CLASSNAME_MR_JOBCLIENT)) {
			type = TYPE_JOBCONF;
		}
		return type;
	}

	/**
	 * <p>
	 * This method provides instructions to add method call to add classpath
	 * </p>.
	 *
	 * @param jobVariableIndex Index of job variable
	 * @param type the type
	 * @return Instructions
	 */
	private InsnList addClasspath(int jobVariableIndex, Type type) {
		LOGGER.info("passed class name in addClasspath is "
				+ type.getInternalName());

		List<List<String>> jarsAndResources = FileUtil
				.getJarsAndResources(FileUtil
						.getClassPathFilesForThinJar(getLoader()));

		InsnList il = new InsnList();
		il.add(new LabelNode());
		if (jarsAndResources != null) {
			il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
			il.add(new LdcInsnNode(CollectionUtil.createStringFromList(jarsAndResources
					.get(0))));
			il.add(new LdcInsnNode(CollectionUtil.createStringFromList(jarsAndResources
					.get(1))));
			il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_JOB_UTIL,
					"addClassPath", Type.getMethodDescriptor(Type.VOID_TYPE,
							type, TYPE_STRING, TYPE_STRING)));
		}

		return il;
	}

	/**
	 * <p>
	 * This method provides instructions to add method call to modify output
	 * path
	 * </p>.
	 *
	 * @param jobVariableIndex Index of job variable
	 * @param type the type
	 * @return Instructions
	 */
	private InsnList modifyOutputPath(int jobVariableIndex, Type type) {

		LOGGER.info("passed class name in modifyOutputPath is "
				+ type.getInternalName());
		InsnList il = new InsnList();
		il.add(new LabelNode());
		il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_JOB_UTIL,
				"modifyOutputPath", Type.getMethodDescriptor(Type.VOID_TYPE,
						type)));
		return il;
	}

	/**
	 * Handle submit case.
	 *
	 * @param jobVariableIndex the job variable index
	 * @param type the type
	 * @return the insn list
	 */
	public static InsnList handleSubmitCase(int jobVariableIndex, Type type) {
		InsnList il = new InsnList();
		il.add(new LabelNode());
		il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLASSNAME_JOB_UTIL,
				"handleSubmitCase", Type.getMethodDescriptor(Type.VOID_TYPE,
						type)));

		return il;
	}

}
