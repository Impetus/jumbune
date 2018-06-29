package org.jumbune.debugger.instrumentation.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.debugger.log.processing.InstructionsBean;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;


/**
 * This class provide utility methods for conversion of methods to byte code.
 */
public final class MethodByteCodeUtil {
	private static final int CURRENT_CLASS_OBJECT_VAR_INDEX = 0;
	private static final Logger LOGGER = LogManager.getLogger(MethodByteCodeUtil.class);
	private MethodByteCodeUtil(){};	

	/**
	 * This API reads a method and copies the parameters to temporary variables
	 * @param node
	 * @param variableIndex
	 * @param insnList
	 * @param locaVariables
	 * @return
	 */
	public static InstructionsBean readMethodAndCopyParamToTemporaryVariables(AbstractInsnNode node, final int variableIndex, InsnList insnList,
			List<LocalVariableNode> locaVariables) {
		int tempVariableIndex=variableIndex;

		InstructionsBean insBean = new InstructionsBean();
		int indexKeyTempVar = InstrumentConstants.PARAMETER_NULL_INDEX;
		int indexValTempVar = InstrumentConstants.PARAMETER_NULL_INDEX;
		boolean isValueParameterNull = false;
		boolean isKeyParameterNull = false;

		AbstractInsnNode paramValueStart = null;
		try {
			paramValueStart = getParamStartNode(node, locaVariables);
		} catch (IllegalArgumentException pne) {
			paramValueStart = node;
			isValueParameterNull = true;
		}
		try {
			isParameterSetToNull(paramValueStart.getPrevious());
		} catch (IllegalArgumentException pnE) {
			isKeyParameterNull = true;
		}

		// Creating instructions to copy key and insert it just before value
		// parameter
		// starts
		if (!isKeyParameterNull) {
			indexKeyTempVar = tempVariableIndex;
			InsnList copyKeyParamList = createTempVariableAndCopyValue(indexKeyTempVar);
			insnList.insertBefore(paramValueStart, copyKeyParamList);
		}

		tempVariableIndex++;

		// Add copy statement of value if it is not null and so its variable
		// index
		if (!isValueParameterNull) {
			indexValTempVar = tempVariableIndex;
			InsnList copyValParamList = createTempVariableAndCopyValue(indexValTempVar);
			insnList.insert(node, copyValParamList);
		}

		// No matter if value is null or not variable index should be
		// incremented else it
		// will spoil the order of objects created. It is required in case if
		// there are multiple
		// context.write in Map/reduce and one takes null value but others have
		// value, so leaving appropriate
		// space for the value parameter
		tempVariableIndex++;

		// Add the variable index key and value for further use
		insBean.addIndexToTemporaryVariablesList(indexKeyTempVar);
		insBean.addIndexToTemporaryVariablesList(indexValTempVar);

		insBean.setVariableIndex(tempVariableIndex);
		return insBean;
	}

	/**
	 * API to fetch the start node
	 * @param node
	 * @param locaVariables
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static AbstractInsnNode getParamStartNode(AbstractInsnNode node,
			List<LocalVariableNode> locaVariables)
			throws IllegalArgumentException {
		AbstractInsnNode paramStartNode = null;

		if (node instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) node;

			if ("<init>".equals(min.name)) {
				paramStartNode = getInitializationInstructionsSet(node);
			} else {
				AbstractInsnNode traversalNode = node;
				while (!isMethodStartNode(traversalNode)
						&& traversalNode != null) {
					traversalNode = traversalNode.getPrevious();
				}

				// Since we could not fetch startNode of parameter, this means
				// there must be another class's method call
				if (paramStartNode == null) {
					traversalNode = getParamStartNodeOfUDClassMethodCall(min,
							locaVariables);
				}
				paramStartNode = traversalNode;
			}
		} else if (node instanceof VarInsnNode) {
			VarInsnNode varNode = (VarInsnNode) node;
			if (!(varNode.var == InstrumentConstants.THREE || varNode.var == 0)) {
				paramStartNode = node;
			}
		} else if (node instanceof FieldInsnNode) {
			paramStartNode = node;
		} else{isParameterSetToNull(node);
			// If parameter is set to null then don't do anything as an
			// exception will be thrown
		}
		return paramStartNode;
	}
	
	private static boolean isParameterSetToNull(AbstractInsnNode node) throws IllegalArgumentException {
		if (node instanceof InsnNode && Opcodes.ACONST_NULL == node.getOpcode()) {
			throw new IllegalArgumentException("Parameter set as Null");
		}
		return false;
	}

	private static AbstractInsnNode getParamStartNodeOfUDClassMethodCall(MethodInsnNode node, List<LocalVariableNode> locaVariables) {
		String owner = node.owner;

		List<Integer> descriptionMatchingLocalVariables = new ArrayList<Integer>();
		for (LocalVariableNode localVar : locaVariables) {
			if (localVar.desc.contains(owner)) {
				descriptionMatchingLocalVariables.add(localVar.index);
			}
		}
		boolean isOtherClassVariableFound = false;
		AbstractInsnNode previousNode = node.getPrevious();
		while (previousNode != null && !isOtherClassVariableFound) {
			if (previousNode instanceof VarInsnNode) {
				VarInsnNode varInsnNode = (VarInsnNode) previousNode;
				Integer indexOfUDC = descriptionMatchingLocalVariables.indexOf(varInsnNode.var);
				if (indexOfUDC != -1) {
					return previousNode;
				}
				previousNode = previousNode.getPrevious();
				return previousNode;
			}
		}
		return null;
	}

	

	private static boolean isMethodStartNode(AbstractInsnNode node) {
		if (node instanceof VarInsnNode) {
			VarInsnNode vi = (VarInsnNode) node;
			if (vi.var == CURRENT_CLASS_OBJECT_VAR_INDEX) {
				AbstractInsnNode previouNode = vi.getPrevious();
				if (previouNode instanceof VarInsnNode) {
					VarInsnNode viPrevious = (VarInsnNode) node;
					if (viPrevious.var == CURRENT_CLASS_OBJECT_VAR_INDEX) {
						return false;
					}
				} else{
					return true;
				}
			}
		}
		return false;
	}

	private static AbstractInsnNode getInitializationInstructionsSet(AbstractInsnNode node) {
		AbstractInsnNode paramStartNode = null;
		AbstractInsnNode traversalInsnNode = node;

		if (node instanceof MethodInsnNode) {
			MethodInsnNode min = (MethodInsnNode) node;

			String initializedObjectType = min.owner;
			traversalInsnNode = node.getPrevious();
			while (!isInitializationInstructionStartReached(traversalInsnNode, initializedObjectType)) {
				traversalInsnNode = traversalInsnNode.getPrevious();
			}
			paramStartNode = traversalInsnNode;
		}
		return paramStartNode;
	}
	/***
	 * this method checks whether start of initialization instruction has been reached if it is reached then it return true otherwise false.   
	 * @param node 
	 * @param initializedObjectType
	 * @return boolean true if initialization instrutction has been reached.
	 */
	private static boolean isInitializationInstructionStartReached(AbstractInsnNode node, String initializedObjectType) {
		if (node instanceof TypeInsnNode) {
			TypeInsnNode typeNode = (TypeInsnNode) node;
			if (typeNode.desc.equals(initializedObjectType)){
				return true;
			}
		}
		return false;
	}

	/**
	 * API that returns the list of parameter types
	 * @param min
	 * @return
	 */
	public static List<String> getParamType(MethodInsnNode min) {
		List<String> paramTypeList = new LinkedList<String>();

		String desc = min.desc;
		desc = desc.substring(1, desc.indexOf(')'));
		String[] type = desc.split(";");

		for (String t : type) {
			// Removing the L that starts the class name and then adding type in
			// a list
			paramTypeList.add(t.substring(1));
		}

		return paramTypeList;
	}

	/**
	 * Deciding factors for Anonymous object creation 1) The current method is init e.g. new MyClass() 2) The current method is valueOf and its
	 * previous node is LDC, IntInsn, or Insn e.g new Double(23.3), 123, false
	 */
	public static boolean isAnonymousInitialization(MethodInsnNode min) {
		AbstractInsnNode previousNode = min.getPrevious();

		if ("<init>".equals(min.name)){
			return true;
		}

		if ("valueOf".equals(min.name) && (previousNode instanceof LdcInsnNode) || (previousNode instanceof IntInsnNode)
				|| (previousNode instanceof InsnNode)){
			return true;
		}

		return false;
	}

	/**
	 * This method creates a temporary variable and copies value of specified variable in this newly created temp variable. When an InsnNode using
	 * Opcode.DUP is called it automatically creates a temporary variable of type whose value we want to copy. So in byte code we are not adding code
	 * to create a temp variable but it is automatically created.
	 * 
	 * @param variableIndex
	 *            - index of variable whose value is to be copied to a temp variable
	 * @return InstructionList for copying value of variable
	 */
	public static InsnList createTempVariableAndCopyValue(int variableIndex) {
		InsnList copyParamInsnList = new InsnList();
		copyParamInsnList.add(new LabelNode());
		copyParamInsnList.add(new InsnNode(Opcodes.DUP));
		copyParamInsnList.add(new VarInsnNode(Opcodes.ASTORE, variableIndex));

		return copyParamInsnList;
	}

	/**
	 * @param mn
	 * @param varName
	 * @param desc
	 * @param signature
	 * @param parameters
	 *            - Pass method parameters null if there are no paramters. This map should be strictly a LinkedHashMap so that the order of parameters
	 *            could be maintained. Else a wrong value will pass in wrong parameter
	 * @return
	 */
	public static InsnList addMethodCall(String owner, String methodName, String signature, Map<String, String[]> parameters, int[] variableIndexs,
			int variable) throws JumbuneException {
		InsnList il = new InsnList();
		il.add(new LabelNode());

		for (int i : variableIndexs) {
			il.add(new VarInsnNode(Opcodes.ALOAD, i));
		}

		if (parameters != null && parameters.size() > 0) {

			for (Map.Entry<String, String[]> param : parameters.entrySet()) {

				if (param.getValue() == null || param.getValue().length < InstrumentConstants.THREE) {
					throw new JumbuneException(ErrorCodesAndMessages.METHOD_BYTE_CODE);
				}
				String paramName = param.getKey();
				String[] paramDetails = param.getValue();

				FieldInsnNode tempFin = new FieldInsnNode(Integer.parseInt(paramDetails[InstrumentConstants.PARAMETER_DETAIL_OPCODE_INDEX]),
						paramDetails[InstrumentConstants.PARAMETER_DETAIL_OWNER_INDEX], paramName,
						paramDetails[InstrumentConstants.PARAMETER_DETAIL_DESC_INDEX]);
				il.add(tempFin);
			}
			il.add(new LabelNode());
			il.add(new InsnNode(Opcodes.DUP));
			il.add(new VarInsnNode(Opcodes.ASTORE, variable));
		}

		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, owner, methodName, signature));
		LOGGER.debug(" owner  " + owner);
		return il;
	}

	/**
	 * API to add local variable in method
	 * @param mn
	 * @param varName
	 * @param desc
	 * @param signature
	 * @param index
	 * @return
	 */
	public static LocalVariableNode addMethodLocalVariable(MethodNode mn, String varName, String desc, String signature, int index) {
		InsnList list = mn.instructions;

		LabelNode begin = new LabelNode();
		LabelNode end = new LabelNode();
		list.insert(list.getFirst(), begin);
		list.insert(list.getLast(), end);

		LocalVariableNode lv = new LocalVariableNode(varName, desc, signature, begin, end, index);

		return lv;

	}
}