package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.debugger.instrumentation.utils.Environment;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;



/**
 * <p>
 * This adapter is used to add logging statements before and after all the
 * switch blocks and cases in a class.
 * </p>
 * 

 */
public class CaseAdapter extends BaseAdapter {
	
	/** The current method node. */
	private MethodNode currentMethodNode = null;
	
	/** The insn list. */
	private InsnList insnList = null;
	
	/** The insn arr. */
	private AbstractInsnNode[] insnArr = null;
	
	/** The scan index forswitch. */
	private int scanIndexForswitch = 0;
	
	/** The switch count. */
	private int switchCount = 0;
	
	/** The env. */
	private Environment env;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(CaseAdapter.class);

	/**
	 * <p>
	 * Create a new instance of CaseAdapter.
	 * </p>
	 *
	 * @param loader the loader
	 * @param cv class visitor
	 */
	public CaseAdapter(Loader loader, ClassVisitor cv) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
	}

	/**
	 * Instantiates a new case adapter.
	 *
	 * @param loader the loader
	 * @param cv the cv
	 * @param env the env
	 */
	public CaseAdapter(Loader loader, ClassVisitor cv,Environment env) {
		super(loader, Opcodes.ASM4);
		this.cv = cv;
		this.env = env;
	}

	
	/* (non-Javadoc)
	 * @see org.jumbune.debugger.instrumentation.adapter.BaseAdapter#visitEnd()
	 */
	@Override
	public void visitEnd() {
		for (Object o : methods) {
			MethodNode mn = (MethodNode) o;
			currentMethodNode = (MethodNode) o;

			LOGGER.debug(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.LOG_INSTRUMENTING_METHOD),
					getClassName() + "##" + mn.name));

			if ((currentMethodNode.access & Opcodes.ACC_SYNTHETIC) == 0) {
				instrumentswitchBlocks(mn);
			}
			mn.visitMaxs(0, 0);
		}

		accept(cv);
	}

	/**
	 * <p>
	 * This method instruments Switch blocks within a method
	 * </p>.
	 *
	 * @param mn Method being scanned
	 */
	private void instrumentswitchBlocks(MethodNode mn) {
		insnList = mn.instructions;
		insnArr = insnList.toArray();

		int scanStartIndex = 0;
		int scanEndIndex = insnArr.length - 1;

		instrumentswitchBlock(scanStartIndex, scanEndIndex);
	}

	/**
	 * <p>
	 * This method finds Switch block in a method and processes it
	 * </p>.
	 *
	 * @param scanStartIndex Start index for the scan
	 * @param scanEndIndex End index for the scan
	 */
	private void instrumentswitchBlock(int scanStartIndex,
			int scanEndIndex) {
		for (scanIndexForswitch = scanStartIndex; scanIndexForswitch <= scanEndIndex; scanIndexForswitch++) {
			AbstractInsnNode currentInsnNode = insnArr[scanIndexForswitch];

			if (currentInsnNode instanceof TableSwitchInsnNode
					&& Opcodes.TABLESWITCH == currentInsnNode.getOpcode()) {
				processTableSwitchBlock((TableSwitchInsnNode) currentInsnNode);
			} else if (currentInsnNode instanceof LookupSwitchInsnNode
					&& Opcodes.LOOKUPSWITCH == currentInsnNode.getOpcode()) {
				processLookupSwitchBlock((LookupSwitchInsnNode) currentInsnNode);
			}

		}
	}

	/**
	 * <p>
	 * This method Handled the Switch block of TableSwitchInsnNode type in a
	 * method
	 * </p>.
	 *
	 * @param currentTableSwithInsn Type of switch block
	 */
	private void processTableSwitchBlock(
			TableSwitchInsnNode currentTableSwithInsn) {
		LabelNode currentLabel = currentTableSwithInsn.dflt;

		int switchStartIndex = CollectionUtil.getObjectIndexInArray(insnArr,
				currentTableSwithInsn);
		int switchTargetIndex = CollectionUtil.getObjectIndexInArray(insnArr,
				currentLabel);

		if (switchTargetIndex > switchStartIndex) {
			LOGGER.debug("switch block ended at: " + switchTargetIndex);
			switchCount++;

			AbstractInsnNode[] ainSwitchBlock = new AbstractInsnNode[] {
					currentTableSwithInsn.getPrevious(), currentLabel };
			Integer[] lineNumbers = getLineNumbersForSwitchBlock(ainSwitchBlock);
			InsnList[] il = getInsnForswitchBlock(switchCount, lineNumbers);
			addInsnForswitchBlock(il, ainSwitchBlock);

			scanIndexForswitch = switchTargetIndex;

			handleTableSwitchCases(currentTableSwithInsn);
		}
	}

	/**
	 * <p>
	 * This method provides the instructions for injecting logging for Switch
	 * blocks
	 * </p>.
	 *
	 * @param switchCount Switch block number
	 * @param lineNumbers line numbers at which the switch block starts and ends
	 * @return Array of instructions
	 */

	private InsnList[] getInsnForswitchBlock(int switchCount,
			Integer[] lineNumbers) {

		int[] logMsgs = new int[] { MessageConstants.MSG_BEFORE_SWITCH,
				MessageConstants.MSG_AFTER_SWITCH };

		InsnList[] il = new InsnList[logMsgs.length];
		for (int i = 0; i < il.length; i++) {
			LOGGER.info(i + "   " + lineNumbers[i]);
			String cSymbol = env.getClassSymbol(getClassName()); 
			String mSymbol = env.getMethodSymbol(getClassName(), cSymbol, currentMethodNode.name);
			il[i] = InstrumentUtil.addLogMessage(cSymbol,mSymbol,
					InstrumentationMessageLoader.getMessage(logMsgs[i]),
					lineNumbers[i].toString(),
					String.valueOf(switchCount));
		}

		return il;
	}

	/**
	 * <p>
	 * This method injects instructions for add logging for Switch blocks and
	 * Switch Cases
	 * </p>.
	 *
	 * @param il list of instruction lists
	 * @param nodes list of nodes where these instructions needed to be injected
	 */
	private void addInsnForswitchBlock(InsnList[] il, AbstractInsnNode[] nodes) {
		for (int i = 0; i < nodes.length; i++) {
			insnList.insert(nodes[i], il[i]);
		}
	}

	/**
	 * <p>
	 * This method Handled the Switch cases of TableSwitchInsnNode type in a
	 * method
	 * </p>.
	 *
	 * @param currentTableSwithInsn Type of switch block
	 */
	@SuppressWarnings("rawtypes")
	private void handleTableSwitchCases(
			TableSwitchInsnNode currentTableSwithInsn) {
		List caseList = currentTableSwithInsn.labels;
		LabelNode currentLabel = currentTableSwithInsn.dflt;
		int min = currentTableSwithInsn.min;
		int max = currentTableSwithInsn.max;

		List<Integer> caseIndex = new ArrayList<Integer>();
		for (int cases = min, i = 0; cases <= max; cases++, i++) {
			if (caseList.get(i) != currentLabel) {
				caseIndex.add(cases);
			}
		}

		int totalcaselogs = caseIndex.size() * 2;

		String[] logMsgs = new String[totalcaselogs];
		int[] caseValues = new int[totalcaselogs];
		AbstractInsnNode abstractCaseInsnNode[] = new AbstractInsnNode[totalcaselogs];
		int index = 0;
		for (int i = 0; i < caseIndex.size(); i++) {

			int caseValue = caseIndex.get(i);


			AbstractInsnNode currentNode = (AbstractInsnNode) caseList.get(i);
			int j = i;
			while (currentNode.equals(currentLabel)) {
				currentNode = (LabelNode) caseList.get((j++));
			}

			abstractCaseInsnNode[index] = currentNode;

			LabelNode nextNode = null;

			if (caseIndex.size() != (i + 1)) {
				caseIndex.get(i + 1);
				int k = i;
				LabelNode tempNextNode = (LabelNode) caseList.get(i + 1);
				while (tempNextNode.equals(currentLabel)) {
					tempNextNode = (LabelNode) caseList.get((k++) + 1);
				}
				nextNode = tempNextNode;
			} else {
				nextNode = currentLabel;
			}

			caseValues[index] = caseValue;
			caseValues[index + 1] = caseValue;

			if (nextNode.getPrevious() instanceof JumpInsnNode) {
				abstractCaseInsnNode[index + 1] = nextNode.getPrevious()
						.getPrevious();
			} else {
				abstractCaseInsnNode[index + 1] = nextNode.getPrevious();
			}

			logMsgs[index] = InstrumentationMessageLoader
					.getMessage(MessageConstants.MSG_IN_SWITCHCASE);
			logMsgs[index + 1] = InstrumentationMessageLoader
					.getMessage(MessageConstants.MSG_OUT_SWITCHCASE);

			index += 2;
		}
		Integer[] lineNumbers = getLineNumbersForSwitchCase(abstractCaseInsnNode);

		InsnList[] il = getInsnForswitchCaseBlock(logMsgs, caseValues,
				lineNumbers);
		addInsnForswitchBlock(il, abstractCaseInsnNode);
	}

	/**
	 * <p>
	 * This method find the last node of every switch cases in a method
	 * </p>.
	 *
	 * @param caseList the case list
	 * @param currentInsnNode current switch case Label node
	 * @param currentLabel default of switch case Label node
	 * @return AbstractInsnNode
	 */
	private AbstractInsnNode lookNode(List<AbstractInsnNode> caseList,
			final AbstractInsnNode currentInsnNode, LabelNode currentLabel) {
		AbstractInsnNode tempCurrentInsnNode=currentInsnNode,insnNode = null;
		boolean scanFurther = true;
		while (scanFurther) {
			if (caseList.contains(tempCurrentInsnNode.getNext())) {
				scanFurther = false;
				if (tempCurrentInsnNode instanceof JumpInsnNode){
					insnNode = tempCurrentInsnNode.getPrevious();
				}
				else{
					insnNode = tempCurrentInsnNode.getNext();
				}

			} else {

				tempCurrentInsnNode = tempCurrentInsnNode.getNext();
			}

			if (currentLabel.equals(tempCurrentInsnNode)) {
				scanFurther = false;
				insnNode = currentLabel.getPrevious();
			}

		}
		return insnNode;
	}

	/**
	 * <p>
	 * This method Handled the Switch cases of LookupSwitchInsnNode type in a
	 * method
	 * </p>.
	 *
	 * @param currentTableSwithInsn Type of switch block
	 */
	@SuppressWarnings("unchecked")
	private void handleLookupSwitchCases(
			LookupSwitchInsnNode currentTableSwithInsn) {
		List<AbstractInsnNode> caseList = currentTableSwithInsn.labels;
		LabelNode currentLabel = currentTableSwithInsn.dflt;
		List<Integer> casekeys = currentTableSwithInsn.keys;

		int totalcaselogs = casekeys.size() * 2;

		String[] logMsgs = new String[totalcaselogs];
		int[] caseValues = new int[totalcaselogs];
		AbstractInsnNode abstractCaseInsnNode[] = new AbstractInsnNode[totalcaselogs];

		int index = 0;
		for (int i = 0; i < casekeys.size(); i++) {
			abstractCaseInsnNode[index] = caseList.get(i);
			caseValues[index] = casekeys.get(i);
			caseValues[index + 1] = casekeys.get(i);

			AbstractInsnNode nextNode = lookNode(caseList,
					abstractCaseInsnNode[index], currentLabel);

			abstractCaseInsnNode[index + 1] = nextNode;

			logMsgs[index] = InstrumentationMessageLoader
					.getMessage(MessageConstants.MSG_IN_SWITCHCASE);
			logMsgs[index + 1] = InstrumentationMessageLoader
					.getMessage(MessageConstants.MSG_OUT_SWITCHCASE);

			index += 2;
		}

		Integer[] lineNumbers = getLineNumbersForSwitchCase(abstractCaseInsnNode);

		InsnList[] il = getInsnForswitchCaseBlock(logMsgs, caseValues,
				lineNumbers);
		addInsnForswitchBlock(il, abstractCaseInsnNode);
	}

	/**
	 * Gets the line numbers for switch case.
	 *
	 * @param abstractCaseInsnNode the abstract case insn node
	 * @return the line numbers for switch case
	 */
	private Integer[] getLineNumbersForSwitchCase(
			AbstractInsnNode[] abstractCaseInsnNode) {
		Integer[] lineNumbers = new Integer[abstractCaseInsnNode.length];
		int i = 0;
		for (AbstractInsnNode abstractInsnNode : abstractCaseInsnNode) {
			lineNumbers[i++] = getPreviousLineNumber(abstractInsnNode) + 1;
		}

		return lineNumbers;
	}

	/**
	 * Gets the line numbers for switch block.
	 *
	 * @param abstractCaseInsnNode the abstract case insn node
	 * @return the line numbers for switch block
	 */
	private Integer[] getLineNumbersForSwitchBlock(
			AbstractInsnNode[] abstractCaseInsnNode) {
		Integer[] lineNumbers = new Integer[abstractCaseInsnNode.length];

		int i = 0;
		lineNumbers[i] = getPreviousLineNumber(abstractCaseInsnNode[i++]);
		lineNumbers[i] = getNextLineNumber(abstractCaseInsnNode[i]) - 1;

		return lineNumbers;
	}

	/**
	 * <p>
	 * This method provides the instructions for injecting logging for Switch
	 * cases
	 * </p>.
	 *
	 * @param logMsgs List of switch instrumentation logs
	 * @param caseValues List of cases in switch block
	 * @param lineNumbers line numbers at which the switch cases starts and ends
	 * @return Array of instructions
	 */
	private InsnList[] getInsnForswitchCaseBlock(String[] logMsgs,
			int[] caseValues, Integer[] lineNumbers) {
		InsnList[] il = new InsnList[logMsgs.length];
		for (int i = 0; i < il.length; i++) {
			String cSymbol = env.getClassSymbol(getClassName());
			String mSymbol = env.getMethodSymbol(getClassName(), cSymbol, currentMethodNode.name);
			il[i] = InstrumentUtil.addLogMessage(cSymbol,mSymbol,
					logMsgs[i],lineNumbers[i].toString(),
					String.valueOf(caseValues[i]));
		}
		return il;
	}

	/**
	 * <p>
	 * This method Handled the Switch block of LookupSwitchInsnNode type in a
	 * method
	 * </p>.
	 *
	 * @param currentTableSwithInsn Type of switch block
	 */
	private void processLookupSwitchBlock(
			LookupSwitchInsnNode currentTableSwithInsn) {
		LabelNode currentLabel = currentTableSwithInsn.dflt;

		int switchStartIndex = CollectionUtil.getObjectIndexInArray(insnArr,
				currentTableSwithInsn);
		int switchTargetIndex = CollectionUtil.getObjectIndexInArray(insnArr,
				currentLabel);

		if (switchTargetIndex > switchStartIndex) {
			LOGGER.debug("switch block ended at: " + switchTargetIndex);
			switchCount++;

			AbstractInsnNode[] ainSwitchBlock = new AbstractInsnNode[] {
					currentTableSwithInsn.getPrevious(), currentLabel };

			Integer[] lineNumbers = getLineNumbersForSwitchBlock(ainSwitchBlock);

			InsnList[] il = getInsnForswitchBlock(switchCount, lineNumbers);
			addInsnForswitchBlock(il, ainSwitchBlock);

			scanIndexForswitch = switchTargetIndex;

			handleLookupSwitchCases(currentTableSwithInsn);
		}
	}
}