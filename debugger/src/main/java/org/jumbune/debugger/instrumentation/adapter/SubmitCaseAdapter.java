package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.job.Config;
import org.jumbune.debugger.instrumentation.utils.EnumJobSubmitMethods;
import org.jumbune.debugger.instrumentation.utils.InstrumentConstants;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;



/*
 * This class is used to handle job.submit case for new API. 
 *    
 */
/**
 * The Class SubmitCaseAdapter.
 */
public class SubmitCaseAdapter extends BaseAdapter {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(ProfileAdapter.class);

	/**
	 * <p>
	 * Create a new instance of ProfileAdapter.
	 * </p>
	 *
	 * @param loader the loader
	 * @param cv Class visitor
	 */
	public SubmitCaseAdapter(Config config, ClassVisitor cv) {
		super(config, Opcodes.ASM4);
		this.cv = cv;
	}

	/* (non-Javadoc)
	 * @see org.jumbune.debugger.instrumentation.adapter.BaseAdapter#visitEnd()
	 */
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
					if (min.name.equals(EnumJobSubmitMethods.JOB_SUBMIT
							.toString())) {

						LOGGER.info(MessageFormat.format(
								InstrumentationMessageLoader
										.getMessage(MessageConstants.JOB_SUBMISSION_FOUND),
								getClassName() + "##" + mn.name));

						// validating that the owner of the method call is

						if (min.owner.equals(EnumJobSubmitMethods.JOB_SUBMIT
								.getOwner().getInternalName())) {
							LOGGER.info(MessageFormat.format(
									InstrumentationMessageLoader
											.getMessage(MessageConstants.LOG_OWNER_IS_JOB),
									getClassName() + "##" + mn.name));
							AbstractInsnNode ain = min.getPrevious();
							while (!(ain instanceof VarInsnNode)) {
								ain = ain.getPrevious();
							}
							VarInsnNode vin = (VarInsnNode) ain;
							int jobVariableIndex = vin.var;
							InsnList il = null;
							il = handleJobSubmitcase(mn, jobVariableIndex);
							insnList.insert(min, il);
						}
					}
				}
			}
			mn.visitMaxs(0, 0);
		}
		accept(cv);
	}

	/**
	 * This method is for handling Job.submit case for new API
	 *
	 * @param mn the mn
	 * @param jobVariableIndex the job variable index
	 * @return InsnList
	 */

	private InsnList handleJobSubmitcase(MethodNode mn, int jobVariableIndex) {

		InsnList il = new InsnList();
		LabelNode l0 = new LabelNode(),l1 = new LabelNode(),l2 = new LabelNode(),l3 = new LabelNode(),l4 = new LabelNode(),
		l5 = new LabelNode(),l6 = new LabelNode(),l7 = new LabelNode(),l8 = new LabelNode(),l9 = new LabelNode(),
		l10 = new LabelNode();

		int index = mn.localVariables.size();
		il.add(l0);
		addJobClient(il);
		il.add(new VarInsnNode(Opcodes.ASTORE, index));
		il.add(l1);
		il.add(new VarInsnNode(Opcodes.ASTORE, index + 1));
		addJobInfo(il);
		il.add(new VarInsnNode(Opcodes.ASTORE, index + 2));
		il.add(l2);
		addJobConf(il);
		il.add(l3);
		il.add(new VarInsnNode(Opcodes.ALOAD, index));
		addConstantBooleanIns(il);
		il.add(l4);
		il.add(new VarInsnNode(Opcodes.ALOAD, index + 1));
		addConstantBooleanIns(il);
		il.add(l5);
		il.add(new VarInsnNode(Opcodes.ALOAD, index + 2));
		addConstantBooleanIns(il);
		il.add(l6);
		il.add(new VarInsnNode(Opcodes.ALOAD, index));
		il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
		addMethodAndTypeInsnNode(il);
		il.add(new VarInsnNode(Opcodes.ASTORE, index + InstrumentConstants.THREE));
		il.add(l7);
		il.add(new VarInsnNode(Opcodes.ALOAD, index + 1));
		il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_FIELD,
				"get", Type.getMethodDescriptor(TYPE_OBJECT, TYPE_OBJECT)));
		il.add(new TypeInsnNode(Opcodes.CHECKCAST, CLASSNAME_RUNNINGJOB));
		il.add(new VarInsnNode(Opcodes.ASTORE, index + InstrumentConstants.FIVE));
		il.add(l8);
		il.add(new VarInsnNode(Opcodes.ALOAD, index + 2));
		il.add(new VarInsnNode(Opcodes.ALOAD, jobVariableIndex));
		addObjectToMethodAndTypeInsn(il);
		il.add(new VarInsnNode(Opcodes.ASTORE, index + InstrumentConstants.FOUR));
		il.add(l9);
		il.add(new VarInsnNode(Opcodes.ALOAD, index + InstrumentConstants.THREE));
		il.add(new VarInsnNode(Opcodes.ALOAD, index + InstrumentConstants.FOUR));
		il.add(new VarInsnNode(Opcodes.ALOAD, index + InstrumentConstants.FIVE));
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
				CLASSNAME_MR_JOBCLIENT, "monitorAndPrintJob", Type
						.getMethodDescriptor(Type.BOOLEAN_TYPE, TYPE_JOBCONF,
								TYPE_RUNNINGJOB)));
		il.add(new InsnNode(Opcodes.POP));
		il.add(l10);
		LocalVariableNode lvn1 = new LocalVariableNode(FIELD_JOBCLIENT,
				DESCRIPTOR_FIELD, null, l0, l1, index);
		LocalVariableNode lvn2 = new LocalVariableNode(FIELD_INFO,
				DESCRIPTOR_FIELD, null, l1, l2, index + 1);
		LocalVariableNode lvn3 = new LocalVariableNode(FIELD_CONF,
				DESCRIPTOR_FIELD, null, l2, l3, index + 2);
		LocalVariableNode lvn4 = new LocalVariableNode(JOB_FIELDCLIENT,
				DESCRIPTOR_JOBCLIENT, null, l6, l7, index + InstrumentConstants.THREE);
		LocalVariableNode lvn5 = new LocalVariableNode(JOB_FIELDCONFF,
				DESCRIPTOR_JOBCONF, null, l8, l9, index + InstrumentConstants.FOUR);
		LocalVariableNode lvn6 = new LocalVariableNode(JOB_FIELDRUNNINGJOB,
				DESCRIPTOR_RUNNINGJOB, null, l7, l8, index + InstrumentConstants.FIVE);
		mn.localVariables.add(lvn1);
		mn.localVariables.add(lvn2);
		mn.localVariables.add(lvn3);
		mn.localVariables.add(lvn4);
		mn.localVariables.add(lvn5);
		mn.localVariables.add(lvn6);
		return il;
	}

	/**
	 * Adds the object to method and type insn.
	 *
	 * @param il the il
	 */
	private void addObjectToMethodAndTypeInsn(InsnList il) {
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_FIELD,
				"get", Type.getMethodDescriptor(TYPE_OBJECT, TYPE_OBJECT)));
		il.add(new TypeInsnNode(Opcodes.CHECKCAST, CLASSNAME_JOB_CONF));
	}

	/**
	 * Adds the method and type insn node.
	 *
	 * @param il the il
	 */
	private void addMethodAndTypeInsnNode(InsnList il) {
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_FIELD,
				"get", Type.getMethodDescriptor(TYPE_OBJECT, TYPE_OBJECT)));
		il.add(new TypeInsnNode(Opcodes.CHECKCAST, CLASSNAME_MR_JOBCLIENT));
	}

	/**
	 * Adds the constant boolean ins.
	 *
	 * @param il the il
	 */
	private void addConstantBooleanIns(InsnList il) {
		il.add(new InsnNode(Opcodes.ICONST_1));
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_FIELD,
				"setAccessible", Type.getMethodDescriptor(Type.VOID_TYPE,
						Type.BOOLEAN_TYPE)));
	}

	/**
	 * Adds the job conf.
	 *
	 * @param il the il
	 */
	private void addJobConf(InsnList il) {
		il.add(new LdcInsnNode(TYPE_JOB_CONTEXT));
		il.add(new LdcInsnNode(JOB_CONF));
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_CLASS,
				"getDeclaredField", Type.getMethodDescriptor(TYPE_FIELD,
						TYPE_STRING)));
	}

	/**
	 * Adds the job info.
	 *
	 * @param il the il
	 */
	private void addJobInfo(InsnList il) {
		il.add(new LdcInsnNode(Type
				.getType("Lorg/apache/hadoop/mapreduce/Job;")));
		il.add(new LdcInsnNode(JOB_INFO));
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_CLASS,
				"getDeclaredField", Type.getMethodDescriptor(TYPE_FIELD,
						TYPE_STRING)));
	}

	/**
	 * Adds the job client.
	 *
	 * @param il the il
	 */
	private void addJobClient(InsnList il) {
		il.add(new LdcInsnNode(Type
				.getType("Lorg/apache/hadoop/mapreduce/Job;")));
		il.add(new LdcInsnNode(JOB_CLIENT));
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASSNAME_CLASS,
				"getDeclaredField", Type.getMethodDescriptor(TYPE_FIELD,
						TYPE_STRING)));
	}

}