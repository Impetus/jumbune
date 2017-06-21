package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.debugger.instrumentation.utils.EnumJobSubmitMethods;
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
 * This adapter is used to add job configuration setting to enable/disable
 * profiling
 */
public class ProfileAdapter extends BaseAdapter {
	private static final String SETTER_METHOD_PREFIX = "set";
	private static final String GET_CONFIGURATION = "getConfiguration";
	private static final Logger LOGGER = LogManager
			.getLogger(ProfileAdapter.class);

	/**
	 * <p>
	 * Create a new instance of ProfileAdapter.
	 * </p>
	 * 
	 * @param cv
	 *            Class visitor
	 */
	public ProfileAdapter(Config config, ClassVisitor cv) {
		super(config, Opcodes.ASM4);
		this.cv = cv;
	}

	@Override
	public void visitEnd() {
		for (Object o : methods) {
			MethodNode mn = (MethodNode) o;
			InsnList insnList = mn.instructions;
			AbstractInsnNode[] insnArr = insnList.toArray();

			/**
			 * Finding the method instruction nodes whose owner is mapreduce.Job
			 * and it submits the job
			 */
			for (AbstractInsnNode abstractInsnNode : insnArr) {
				if (abstractInsnNode instanceof MethodInsnNode) {
					MethodInsnNode min = (MethodInsnNode) abstractInsnNode;

					// finding job submission
					if (InstrumentUtil.isJobSubmissionMethod(min)) {

						LOGGER.debug(MessageFormat.format(
								InstrumentationMessageLoader
										.getMessage(MessageConstants.JOB_SUBMISSION_FOUND),
								getClassName() + "##" + mn.name));

						// validating that the owner of the method call is
						if (InstrumentUtil.isOwnerJob(min)) {
							LOGGER.debug(MessageFormat.format(
									InstrumentationMessageLoader
											.getMessage(MessageConstants.LOG_OWNER_IS_JOB),
									getClassName() + "##" + mn.name));
							AbstractInsnNode ain = min.getPrevious();

							while (!(ain instanceof VarInsnNode)) {
								ain = ain.getPrevious();
							}

/*							VarInsnNode vin = (VarInsnNode) ain;
							int jobVariableIndex = vin.var;
							InsnList il = null;

							// old api check
							if (getOwnerType(min).getInternalName().equals(
									CLASSNAME_JOB_CONF)) {
								il = addProfilingForOldAPI(jobVariableIndex);
							} else {
								il = addProfiling(jobVariableIndex);
							}

							insnList.insertBefore(vin, il);
*/						}
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
	 * </p>
	 * 
	 * @param min
	 *            MethodInsnNode method being visited
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
	 * This method provides instructions to enable/disable profiling for the job
	 * </p>
	 * 
	 * @param jobVariableIndex
	 *            Index of variable which stores the job
	 * @return Instructions
	 */
/*	private InsnList addProfiling(int jobVariableIndex) {
		JobConfig jobConfig = (JobConfig)getConfig();
		boolean iSHadoopJobProfiling = jobConfig.isHadoopJobProfileEnabled();
//		String hadoopJobProfilingParams = jobConfig.getHadoopJobProfileParams();
		String hadoopJobProfilingMaps = PROFILING_MAPPERS_INSTANCES;
		String hadoopJobProfilingReduces = PROFILING_REDUCER_INSTANCES;

		InsnList il = new InsnList();
		if (iSHadoopJobProfiling) {
			il.add(new LabelNode());
			il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_MR_JOB,
					GET_CONFIGURATION, Type.getMethodDescriptor(Type
							.getType(Configuration.class))));

			il.add(new LdcInsnNode(PROFILE_TASK));
			il.add(new LdcInsnNode(Boolean.toString(iSHadoopJobProfiling)));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
					CLASSNAME_HADOOP_CONFIGURATION, SETTER_METHOD_PREFIX, Type
							.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
									TYPE_STRING)));

			// add profile parameters only if it is enabled
			il.add(new LabelNode());
			il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_MR_JOB,
					GET_CONFIGURATION, Type.getMethodDescriptor(Type
							.getType(Configuration.class))));

			il.add(new LdcInsnNode(PROFILE_PARAMS));
			il.add(new LdcInsnNode(hadoopJobProfilingParams));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
					CLASSNAME_HADOOP_CONFIGURATION, SETTER_METHOD_PREFIX, Type
							.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
									TYPE_STRING)));

			il.add(new LabelNode());
			il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_MR_JOB,
					GET_CONFIGURATION, Type.getMethodDescriptor(Type
							.getType(Configuration.class))));

			il.add(new LdcInsnNode(PROFILE_MAPS));
			il.add(new LdcInsnNode(hadoopJobProfilingMaps));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
					CLASSNAME_HADOOP_CONFIGURATION, SETTER_METHOD_PREFIX, Type
							.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
									TYPE_STRING)));

			il.add(new LabelNode());
			il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_MR_JOB,
					GET_CONFIGURATION, Type.getMethodDescriptor(Type
							.getType(Configuration.class))));

			il.add(new LdcInsnNode(PROFILE_REDUCES));
			il.add(new LdcInsnNode(hadoopJobProfilingReduces));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
					CLASSNAME_HADOOP_CONFIGURATION, SETTER_METHOD_PREFIX, Type
							.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING,
									TYPE_STRING)));

			LOGGER.info(MessageFormat.format(
					InstrumentationMessageLoader
							.getMessage(MessageConstants.CLASS_INSTRUMENTED_FOR_PROFILING),
					getClassName()));
		}

		return il;
	}
*/

/*	*//**
	 * <p>
	 * This method provides instructions to enable/disable profiling for the job
	 * for old hadoop api
	 * </p>
	 * 
	 * @param jobVariableIndex
	 *            Index of variable which stores the job
	 * @return Instructions
	 *//*
	private InsnList addProfilingForOldAPI(int jobVariableIndex) {
		JobConfig jobConfig = (JobConfig)getConfig();
		boolean isHadoopJobProfiling = jobConfig.isHadoopJobProfileEnabled();
//		String hadoopJobProfilingParams = jobConfig.getHadoopJobProfileParams();
		String hadoopJobProfilingMaps = PROFILING_MAPPERS_INSTANCES;
		String hadoopJobProfilingReduces = PROFILING_REDUCER_INSTANCES ;

		InsnList il = new InsnList();
		if (isHadoopJobProfiling) {
			il.add(new LabelNode());
			il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
			il.add(new LdcInsnNode(isHadoopJobProfiling));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
					CLASSNAME_JOB_CONF, "setProfileEnabled", Type
							.getMethodDescriptor(Type.VOID_TYPE,
									Type.BOOLEAN_TYPE)));

			// add profile parameters only if it is enabled
			il.add(new LabelNode());
			il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
			il.add(new LdcInsnNode(hadoopJobProfilingParams));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
					CLASSNAME_JOB_CONF, "setProfileParams", Type
							.getMethodDescriptor(Type.VOID_TYPE, TYPE_STRING)));

			// for maps
			il.add(new LabelNode());
			il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
			il.add(new LdcInsnNode(Boolean.TRUE));
			il.add(new LdcInsnNode(hadoopJobProfilingMaps));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
					CLASSNAME_JOB_CONF, "setProfileTaskRange", Type
							.getMethodDescriptor(Type.VOID_TYPE,
									Type.BOOLEAN_TYPE, TYPE_STRING)));
			// for reduce
			il.add(new LabelNode());
			il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
			il.add(new LdcInsnNode(Boolean.FALSE));
			il.add(new LdcInsnNode(hadoopJobProfilingReduces));
			il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
					CLASSNAME_JOB_CONF, "setProfileTaskRange", Type
							.getMethodDescriptor(Type.VOID_TYPE,
									Type.BOOLEAN_TYPE, TYPE_STRING)));

			LOGGER.info(MessageFormat.format(
					InstrumentationMessageLoader
							.getMessage(MessageConstants.CLASS_INSTRUMENTED_FOR_PROFILING),
					getClassName()));

		}
		return il;
	}
*/}
