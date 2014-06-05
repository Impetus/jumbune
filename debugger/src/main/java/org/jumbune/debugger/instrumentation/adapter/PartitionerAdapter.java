/**
 * 
 */
package org.jumbune.debugger.instrumentation.adapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.InstructionsBean;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.debugger.instrumentation.utils.ContextWriteParams;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.debugger.instrumentation.utils.MethodByteCodeUtil;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;


/**
 * This class helps in debugging Partitioner's performance. The time taken in
 * partitioning various keys
 * 
 */
public class PartitionerAdapter extends BaseAdapter {

	private static final Logger LOG = LogManager
			.getLogger(PartitionerAdapter.class);
	private static final int CURRENT_CLASS_VAR_INDEX=0;

	/**
	 * Creates new intance of PartitionerAdapter
	 * @param loader
	 * @param cv
	 */
	public PartitionerAdapter(YamlLoader loader, ClassVisitor cv) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
	}

	/**
	 * method for instrumenting jar for debugging partitioner
	 */
	@Override
	public void visitEnd() {
		if (isMapperClass()) {
			LOG.info("Instrumenting jar for debugging partitioner !!");
			for (int i = 0; i < methods.size(); i++) {
				MethodNode mn = (MethodNode) methods.get(i);
				final int variableIndex = mn.maxLocals;

				if (InstrumentUtil.validateMapMethod(mn)) {
					InsnList insnList = mn.instructions;
					AbstractInsnNode[] insnArr = insnList.toArray();

					// Get the index of temporary variables of key and value
					int keyVariableIndex = ContextWriteParams.getInstance()
							.getTempKeyVariableIndex();
					int valueVariableIndex = ContextWriteParams.getInstance()
							.getTempValueVariableIndex();

					LOG.info("Index set in ContextWriteParams  "
							+ keyVariableIndex + " Index of Value  "
							+ valueVariableIndex + " for class "
							+ getClassName());

					for (AbstractInsnNode abstractInsnNode : insnArr) {
						if (abstractInsnNode instanceof MethodInsnNode) {
							MethodInsnNode min = (MethodInsnNode) abstractInsnNode;

							if (InstrumentUtil.isOutputMethod(min)) {
								// If both the key and value index are 0 that
								// means its yet not copied to temporary
								// variables just copy these params to temporary
								// variables
								if (keyVariableIndex == 0
										&& valueVariableIndex == 0) {
									LOG.info("Since params are not copied to temporary variables do it now !!!!! "
											+ " \n previous to context.write  "
											+ min.getPrevious()
											+ " variableIndex "
											+ variableIndex
											+ " insnList " + insnList.size());
									InstructionsBean insBean = MethodByteCodeUtil
											.readMethodAndCopyParamToTemporaryVariables(
													min.getPrevious(),
													variableIndex, insnList,
													mn.localVariables);
									LOG.info("After the instructions were added size of insnList :: "
											+ insnList.size());
									// Add the instance of temporary key/value
									// index to ContextWriteParams
									keyVariableIndex = insBean
											.getTemporaryVariablesIndexList()
											.get(KEY_INDEX);
									valueVariableIndex = insBean
											.getTemporaryVariablesIndexList()
											.get(VALUE_INDEX);

									ContextWriteParams.getInstance()
											.setTempKeyVariableIndex(
													keyVariableIndex);
									ContextWriteParams.getInstance()
											.setTempValueVariableIndex(
													valueVariableIndex);
								}
								// Add instructions for detecting the time taken
								// in partitioning
								insnList.insert(
										abstractInsnNode,
										getInsnListForCalculatePartitioningTime(
												keyVariableIndex,
												valueVariableIndex));
							}
						}
					}
				}
				mn.visitMaxs(0, 0);
			}
		}
		accept(cv);
	}

	/**
	 * This method writes instructions for calling
	 * MRContextUtil.calculatePartitioningTime method based on current key
	 * count. The following instructions will be added
	 * 
	 * if((mrCounter % k) == 0){
	 * MRContextUtil.calculateParitioningTime(key,value); }
	 * 
	 * @return - instruction list adding the above statements
	 */
	private InsnList getInsnListForCalculatePartitioningTime(
			int tempkeyVarIndex, int tempValueVarIndex) {
		LOG.debug("Adding statements for partitioning temKeyIndex "
				+ tempkeyVarIndex + " ValueIndex " + tempValueVarIndex);

		LabelNode ifLabelNode = new LabelNode();
		LabelNode counterCheckNode = new LabelNode();
		InsnList il = new InsnList();

		// Creating statement isProfilePartitioner
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(getClassName()),
				PROFILE_PARTITIONER, Type.BOOLEAN_TYPE.getDescriptor()));

		il.add(counterCheckNode);
		il.add(new JumpInsnNode(Opcodes.IFEQ, ifLabelNode));

		// Creating statement mrCounter % k == 0
		il.add(new VarInsnNode(Opcodes.ALOAD, CURRENT_CLASS_VAR_INDEX));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, ConfigurationUtil
				.convertQualifiedClassNameToInternalName(getClassName()),
				MAP_REDUCE_COUNTER, Type.INT_TYPE.getDescriptor()));
		il.add(new IntInsnNode(Opcodes.BIPUSH, getLoader()
				.getPartitionerSampleInterval()));
		il.add(new InsnNode(Opcodes.IREM));
		il.add(new JumpInsnNode(Opcodes.IFNE, ifLabelNode));
		il.add(new LabelNode());
		il.add(new VarInsnNode(Opcodes.ALOAD, tempkeyVarIndex));
		il.add(new VarInsnNode(Opcodes.ALOAD, tempValueVarIndex));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
				CLASSNAME_MAPREDUCEEXECUTIL, CALCULATE_PARTITIONING_TIME,
				DESC_CALCULATE_PARTITIONING_TIME));
		il.add(ifLabelNode);
		il.add(new LabelNode());

		return il;
	}

}
