package org.jumbune.debugger.instrumentation.adapter;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.utils.CollectionUtil;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.debugger.instrumentation.utils.Environment;
import org.jumbune.debugger.instrumentation.utils.InstrumentUtil;
import org.jumbune.debugger.instrumentation.utils.InstrumentationMessageLoader;
import org.jumbune.debugger.instrumentation.utils.MessageConstants;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;


/**
 * <p>
 * Adds logging statements before and after all the if blocks in method *
 * </p>
 * 
 * Logs are injected:
 * <ul>
 * <li>Before <i>if</i> statements
 * <li>In <i>if</i> statements
 * <li>In <i>else if</i> statements
 * <li>In <i>else</i> statements
 * <li>After <i>if</i> statements
 * </ul>
 * 
 * Nesting of <i>if</i> blocks is handled upto configurable level (
 * {@link Loader#getMaxIfBlockNestingLevel()}
 * 
 */
public class BlockLogMethodAdapter extends BaseMethodAdpater {
	private static final String NESTING_LEVEL = " Nesting Level: ";
	private static Logger logger = LogManager
			.getLogger(BlockLogMethodAdapter.class);
	private AbstractInsnNode[] insnArr;
	private int localVariableSize = 0;
	private AbstractInsnNode ifStmtNodeTemp = null;
	private AbstractInsnNode currentInsnNode = null;
	private boolean isVariableAdded = false;
	JobConfig jobConfig = (JobConfig)getConfig();
	private int maxNestingLevel = jobConfig.getMaxIfBlockNestingLevel();
	// variables to hold values for each nesting level
	private int[] currentScanIndexForIf = new int[maxNestingLevel];
	private int[] currentIfCount = new int[maxNestingLevel];
	private Integer[] currentLoopCount = new Integer[maxNestingLevel];
	private AbstractInsnNode[] currIfStmtNode = new AbstractInsnNode[maxNestingLevel];
	private boolean[] isGoToFound = new boolean[maxNestingLevel];
	private boolean[] isTraverseGoto = new boolean[maxNestingLevel];
	private boolean[] isOrOperatorFound = new boolean[maxNestingLevel];
	private boolean[] isAndOperatorFound = new boolean[maxNestingLevel];
	private JumpInsnNode[] currentJIN = new JumpInsnNode[maxNestingLevel];
	private LabelNode[] currentTarget = new LabelNode[maxNestingLevel];
	private Environment env;

	/**
	 * creates new instance of BlockLogMethodAdapter
	 * @param loader
	 * @param access
	 * @param name
	 * @param desc
	 * @param signature
	 * @param exceptions
	 * @param mv
	 * @param className
	 * @param logClassName
	 */
	public BlockLogMethodAdapter(Config config, int access, String name,
			String desc, String signature, String[] exceptions,
			MethodVisitor mv, String className, String logClassName) {
		super(config, access, name, desc, signature, exceptions, mv, className,
				logClassName);
		this.mv = mv;
}
	
	/**
	 * creates new instance of BlockLogMethodAdapter
	 * @param loader
	 * @param access
	 * @param name
	 * @param desc
	 * @param signature
	 * @param exceptions
	 * @param mv
	 * @param className
	 * @param logClassName
	 * @param env
	 */
	public BlockLogMethodAdapter(Config config, int access, String name,
			String desc, String signature, String[] exceptions,
			MethodVisitor mv, String className, String logClassName,Environment env) {
		super(config, access, name, desc, signature, exceptions, mv, className,
				logClassName);
		this.mv = mv;
		this.env = env;
	}


	/**
	 * visitEnd method for instrumentation
	 */
	@Override
	public void visitEnd() {
		logger.info(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.LOG_INSTRUMENTING_METHOD),
				getClassName() + "##" + name + "##" + desc));

		// setting current count of if block to zero
		for (int index = 0; index < this.maxNestingLevel; index++) {
			this.currentIfCount[index] = 0;
			this.currentLoopCount[index] = 0;
		}

		this.localVariableSize = maxLocals;
		this.insnArr = instructions.toArray();
		int scanStartIndex = 0;
		int scanEndIndex = insnArr.length - 1;
		this.instrumentIfBlock(scanStartIndex, scanEndIndex, 0);
		visitMaxs(0, 0);
		accept(mv);
	}

	/**
	 * <p>
	 * This method scan for '&&' and '||' operator within if condition
	 * </p>
	 * Note: This is applicable for if statements, not for loops.
	 * 
	 * @param ifStartIndex
	 *            Index at which the if starts
	 * @param ifTargetIndex
	 *            Index of the label associated with this if block
	 * @param level
	 *            nesting level
	 */
	private void scanAndOrOperator(int ifStartIndex, int ifTargetIndex,
			int level) {
		this.isOrOperatorFound[level] = false;
		this.isAndOperatorFound[level] = false;

		int scanForAndIndex = ifStartIndex;
		boolean scanFurther = true;

		while (scanFurther) {
			if ((this.currentTarget[level].getPrevious() instanceof JumpInsnNode && InstrumentUtil
					.getJumpInsnOpcodesMap()
					.containsKey(
							this.currentTarget[level].getPrevious().getOpcode()))) {
				logger.debug(InstrumentationMessageLoader
						.getMessage(MessageConstants.LOG_MULTIPLE_IF_CONDITION_OR));
				this.isOrOperatorFound[level] = true;
				this.currIfStmtNode[level] = this.currentTarget[level];
				this.currentJIN[level] = (JumpInsnNode) currentTarget[level]
						.getPrevious();
				this.currentTarget[level] = this.currentJIN[level].label;

				if (ifTargetIndex == CollectionUtil.getObjectIndexInArray(this.insnArr,
						this.currentTarget[level])) {
					logger.debug("ifTargetIndex == Util.getArrayIndex( insnArr, currTarget[nestingLevel] )");
					scanFurther = false;
				} else if (ifTargetIndex > CollectionUtil.getObjectIndexInArray(
						this.insnArr, this.currentTarget[level])) {
					logger.debug("ifTargetIndex > Util.getArrayIndex( insnArr, currTarget[nestingLevel] ) )");
					this.isTraverseGoto[level] = false;
					this.currentJIN[level] = (JumpInsnNode) this.insnArr[ifStartIndex];
					this.currentTarget[level] = (LabelNode) this.insnArr[ifTargetIndex];
					scanFurther = false;
				}
			} else{
				this.ifStmtNodeTemp = this.scanForAnd(scanForAndIndex);
					if (this.ifStmtNodeTemp != null) {
					logger.debug(InstrumentationMessageLoader
							.getMessage(MessageConstants.LOG_MULTIPLE_IF_CONDITION_AND));
					this.isAndOperatorFound[level] = true;
					this.currIfStmtNode[level] = this.ifStmtNodeTemp;
					this.currentJIN[level] = (JumpInsnNode) this.currIfStmtNode[level];
					this.currentTarget[level] = this.currentJIN[level].label;
					scanForAndIndex = CollectionUtil.getObjectIndexInArray(this.insnArr,
							this.currentJIN[level]);
					this.ifStmtNodeTemp = null;
				} else {
					logger.debug(InstrumentationMessageLoader
							.getMessage(MessageConstants.LOG_MULTIPLE_IF_CONDITION_NOT_FOUND));
					scanFurther = false;
				}
			}
		}
	}

	/**
	 * <p>
	 * This method scans to find GoTo instruction
	 * </p>
	 * 
	 * @param nestingLevel
	 *            nesting level
	 */
	private void scanGoTo(int nestingLevel) {
		if (this.currentTarget[nestingLevel].getPrevious() instanceof JumpInsnNode
				&& Opcodes.GOTO == this.currentTarget[nestingLevel]
						.getPrevious().getOpcode()) {
			logger.debug(InstrumentationMessageLoader
					.getMessage(MessageConstants.LOG_GOTO_FOUND));
			this.isGoToFound[nestingLevel] = true;
			this.currentJIN[nestingLevel] = (JumpInsnNode) this.currentTarget[nestingLevel]
					.getPrevious();
			this.currentTarget[nestingLevel] = this.currentJIN[nestingLevel].label;
		} else {
			logger.debug(InstrumentationMessageLoader
					.getMessage(MessageConstants.LOG_GOTO_NOT_FOUND));
			this.isGoToFound[nestingLevel] = false;
		}
	}

	/**
	 * <p>
	 * This method finds if block in a method and processes it
	 * </p>
	 * 
	 * @param scanStartIndex
	 *            Start index for the scan
	 * @param scanEndIndex
	 *            End index for the scan
	 * @param level
	 *            nesting level
	 */
	private void instrumentIfBlock(int scanStartIndex, int scanEndIndex,
			int level) {
		if (level < this.maxNestingLevel && scanEndIndex > scanStartIndex) {

			
				// resetting the values for this scan
				this.isOrOperatorFound[level] = false;
				this.isAndOperatorFound[level] = false;
				this.currIfStmtNode[level] = null;
				this.ifStmtNodeTemp = null;
				this.currentJIN[level] = null;
				this.currentTarget[level] = null;
				this.isTraverseGoto[level] = true;
				this.isGoToFound[level] = false;

				for (this.currentScanIndexForIf[level] = scanStartIndex; this.currentScanIndexForIf[level] <= scanEndIndex; this.currentScanIndexForIf[level]++) {
					this.currentInsnNode = this.insnArr[this.currentScanIndexForIf[level]];
				
					if (this.currentInsnNode instanceof JumpInsnNode && (InstrumentUtil.getJumpInsnOpcodesMap().containsKey(
							this.currentInsnNode.getOpcode()))) {
							// an if statement is found
							this.currentJIN[level] = (JumpInsnNode) this.currentInsnNode;
							this.processIfBlock(this.currentJIN[level], level);
						}
					}
				
		}
	}

	/**
	 * <p>
	 * This method injects instructions for add logging for if blocks
	 * </p>
	 * 
	 * @param il
	 *            list of instruction lists
	 * @param nodes
	 *            list of nodes where these instructions needed to be injected
	 * @param nestingLevel
	 *            nesting level
	 */
	private void addInsnForIfBlock(InsnList[] il, AbstractInsnNode[] nodes,
			int nestingLevel) {
		// messages for before, after and within if blocks
		int[] msgs = new int[] { MessageConstants.LOG_BEFORE_IF,
				MessageConstants.LOG_AFTER_IF, MessageConstants.LOG_IN_IF };

		int index = 0;
		for (int msg : msgs) {
			logger.debug(MessageFormat.format(
					InstrumentationMessageLoader.getMessage(msg),
					getClassName() + "##" + name, currentIfCount[nestingLevel]));

			// for BEFORE_IF, the logging to be injected at the respective label
			// node
			AbstractInsnNode prevNode = nodes[index];
			if (index == 0) {
				while (!(prevNode instanceof LabelNode)) {
					prevNode = prevNode.getPrevious();
				}
			}
			instructions.insert(prevNode, il[index]);
			index++;
		}
	}

	/**
	 * <p>
	 * This method processes 'else if' block
	 * </p>
	 * 
	 * @param elseNode
	 * @param ifCount
	 *            Number of this if block in the method
	 * @param ifTargetIndex
	 *            Index of label associated with this if block
	 * @param nestingLevel
	 *            nesting level
	 */
	private void processElseIf(final AbstractInsnNode elseNode,int nestingLevel) {
		AbstractInsnNode tempElseNode=elseNode;
		this.isOrOperatorFound[nestingLevel] = false;
		this.isAndOperatorFound[nestingLevel] = false;
		Integer elseIfCount = 0;
		int tempIfTargetIndex = 0;
		int endIndex = 0;

		while (this.isGoToFound[nestingLevel]) {
			elseIfCount++;

			if (tempElseNode != null) {
				// this is else if
				int tempIfStartIndex = CollectionUtil.getObjectIndexInArray(this.insnArr,
						tempElseNode);
				this.currentTarget[nestingLevel] = ((JumpInsnNode) tempElseNode).label;
				tempIfTargetIndex = CollectionUtil.getObjectIndexInArray(this.insnArr,
						this.currentTarget[nestingLevel]);

				this.scanAndOrOperator(tempIfStartIndex, tempIfTargetIndex,
						nestingLevel);

				if (!this.isOrOperatorFound[nestingLevel]
						&& !this.isAndOperatorFound[nestingLevel]) {
					this.currIfStmtNode[nestingLevel] = tempElseNode;
				}

				if (this.isTraverseGoto[nestingLevel]) {
					this.scanGoTo(nestingLevel);
				} else {
					this.isGoToFound[nestingLevel] = false;
				}

				endIndex = CollectionUtil.getObjectIndexInArray(this.insnArr,
						this.currentTarget[nestingLevel]);

				String logMsg = InstrumentationMessageLoader
						.getMessage(MessageConstants.MSG_IN_ELSEIF);

				logger.debug(MessageFormat.format(InstrumentationMessageLoader
						.getMessage(MessageConstants.LOG_IN_ELSEIF),
						getClassName() + "##" + name,
						currentIfCount[nestingLevel]));

				Integer lineNumber = getPreviousLineNumber(this.currIfStmtNode[nestingLevel]);

				// There can be multiple 'else if' in a if block, hence its
				// number is logged
				String cSymbol = env.getClassSymbol(getLogClazzName());
				String mSymbol = env.getMethodSymbol(getLogClazzName(), cSymbol, name);
				InsnList il = InstrumentUtil.addLogMessage(cSymbol,
						mSymbol, logMsg, lineNumber.toString(),
						elseIfCount.toString());
				instructions.insert(this.currIfStmtNode[nestingLevel], il);

				// handling for next level
				this.instrumentIfBlock(this.currIfStmtNode[nestingLevel],
						nestingLevel + 1);

				if (this.isGoToFound[nestingLevel] && tempIfTargetIndex < endIndex) {
					
						this.isOrOperatorFound[nestingLevel] = false;
						this.isAndOperatorFound[nestingLevel] = false;

						// find next 'else if' or 'else'
						tempElseNode = this.scanForElse(tempIfTargetIndex,
								endIndex);
					
				}
			} else {
				// this is else
				this.isGoToFound[nestingLevel] = false;
				this.processElse(tempIfTargetIndex, endIndex, nestingLevel);
			}
		}
	}

	/**
	 * <p>
	 * This method processes else block
	 * </p>
	 * 
	 * @param indexElse
	 *            Index at which log instructions to be injected
	 * @param indexElseEnd
	 *            end index of the else block
	 * @param nestingLevel
	 *            nesting level
	 */
	private void processElse(int indexElse, int indexElseEnd, int nestingLevel) {
		String logMsg = InstrumentationMessageLoader
				.getMessage(MessageConstants.MSG_IN_ELSE);

		logger.debug(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.LOG_IN_ELSE), getClassName()
				+ "##" + name, currentIfCount[nestingLevel]));

		Integer lineNumber = this.getNextLineNumber(this.insnArr[indexElse]);

		// There can be only one else in a if block, hence its number is not
		// logged
		String cSymbol = env.getClassSymbol(getLogClazzName());
		String mSymbol = env.getMethodSymbol(getLogClazzName(), cSymbol, name);
		InsnList il = InstrumentUtil.addLogMessage(cSymbol, mSymbol,
				logMsg, lineNumber.toString());
		instructions.insert(this.insnArr[indexElse], il);

		// handling for next level
		this.instrumentIfBlock(indexElse, indexElseEnd, nestingLevel + 1);
	}

	/**
	 * <p>
	 * This method finds whether the if block represents a ternary operator or
	 * not. It also injects necessary instructions to inject logging before and
	 * after ternary operator usage.
	 * </p>
	 * 
	 * @param ifStartIndex
	 *            index of if block start
	 * @param endIndex
	 *            index of else end
	 * @param ainIfBlock
	 *            Instructions where logging to be injected
	 * @param nestingLevel
	 *            nesting level
	 */
	private boolean processTernaryOperator(int ifStartIndex, int endIndex,
			int nestingLevel) {
		boolean ternaryOperatorFound = true;
		for (int g = ifStartIndex; g < endIndex; g++) {
			if (this.insnArr[g] instanceof LineNumberNode) {
				ternaryOperatorFound = false;
				break;
			}
		}

		if (ternaryOperatorFound) {
			InsnList[] il = new InsnList[2];
			String cSymbol = env.getClassSymbol(getLogClazzName());
			String mSymbol = env.getMethodSymbol(getLogClazzName(), cSymbol, name);
			il[0] = InstrumentUtil.addLogMessage(cSymbol, mSymbol,
					InstrumentationMessageLoader
							.getMessage(MessageConstants.MSG_BEFORE_TERNARY),
					"", "" + this.currentIfCount[nestingLevel]);
			il[1] = InstrumentUtil.addLogMessage(cSymbol, mSymbol,
					InstrumentationMessageLoader
							.getMessage(MessageConstants.MSG_AFTER_TERNARY),
					"", "" + this.currentIfCount[nestingLevel]);


		}
		return ternaryOperatorFound;
	}

	/**
	 * <p>
	 * This method processes if block found
	 * </p>
	 * 
	 * @param jinNode
	 * @param nestingLevel
	 *            nesting level
	 */
	private void processIfBlock(JumpInsnNode jinNode, int nestingLevel) {
		int ifStartIndex = CollectionUtil.getObjectIndexInArray(this.insnArr, jinNode);
			logger.debug("If block Start at: " + ifStartIndex + NESTING_LEVEL
				+ nestingLevel);
		// target of the current if statement
		LabelNode ifTarget = jinNode.label;
		this.currentTarget[nestingLevel] = jinNode.label;
		int ifTargetIndex = CollectionUtil.getObjectIndexInArray(this.insnArr, ifTarget);
		logger.debug("If block target at: " + ifTargetIndex+ NESTING_LEVEL + nestingLevel);
		this.scanAndOrOperator(ifStartIndex, ifTargetIndex, nestingLevel);

		if (!this.isOrOperatorFound[nestingLevel]
				&& !this.isAndOperatorFound[nestingLevel]) {
			this.currIfStmtNode[nestingLevel] = jinNode;
		}

		if (this.isTraverseGoto[nestingLevel]) {
			this.scanGoTo(nestingLevel);
		}

		int gotoTargetIndex = CollectionUtil.getObjectIndexInArray(this.insnArr,
				this.currentTarget[nestingLevel]);
		int endIndex = CollectionUtil.getObjectIndexInArray(this.insnArr,
				this.currentTarget[nestingLevel]);
		if (endIndex > ifStartIndex) {
			boolean lineFound = scanForLine(ifStartIndex, endIndex);
			if (lineFound) {
				if (endIndex >= ifTargetIndex) {	
					logIfElseAndTernaryOperator(nestingLevel, ifStartIndex, ifTargetIndex, gotoTargetIndex, endIndex);
				} else {
					logger.debug("If block ended at: " + ifTargetIndex+ NESTING_LEVEL + nestingLevel);
					this.currentIfCount[nestingLevel]++;
					// nodes where logging statements to be injected
					AbstractInsnNode[] ainIfBlock = new AbstractInsnNode[] {
							this.currentInsnNode.getPrevious(), ifTarget,
							this.currIfStmtNode[nestingLevel] };

					InsnList[] il = this.getInsnForIfBlock(ainIfBlock,
							this.currentIfCount[nestingLevel]);
					this.addInsnForIfBlock(il, ainIfBlock, nestingLevel);
					// handling for nested if
					this.instrumentIfBlock(this.currIfStmtNode[nestingLevel],
							nestingLevel + 1);
					this.currentScanIndexForIf[nestingLevel] = ifTargetIndex;
				}
			} else {
				this.currentScanIndexForIf[nestingLevel] = endIndex;
			}
		} else {
			processLoopInstructions(nestingLevel, ifStartIndex, ifTargetIndex);

		}
	}
	

	private void processLoopInstructions(int nestingLevel, int ifStartIndex, int ifTargetIndex) {
		logger.info(MessageFormat.format(InstrumentationMessageLoader
				.getMessage(MessageConstants.LOG_LOOP_FOUND),
				getClassName() + "##" + name));
		if (ifTargetIndex < ifStartIndex) {
			logger.debug("ifTargetIndex < ifStartIndex");
			this.processLoop(ifTargetIndex, ifStartIndex, nestingLevel);
			this.currentScanIndexForIf[nestingLevel] = ifStartIndex;
		} else if (ifTargetIndex > ifStartIndex) {
			logger.debug("ifTargetIndex > ifStartIndex");
			this.processLoop(ifStartIndex, ifTargetIndex, nestingLevel);
			this.currentScanIndexForIf[nestingLevel] = ifTargetIndex;
		}
	}
	
	private void logIfElseAndTernaryOperator(int nestingLevel, int ifStartIndex, int ifTargetIndex, int gotoTargetIndex, int endIndex) {
		int tempNestingLevel=nestingLevel;
		logger.debug("If block ended at: " + endIndex+ NESTING_LEVEL + tempNestingLevel);
		this.currentIfCount[tempNestingLevel]++;
		// nodes where logging statements to be injected
		AbstractInsnNode[] ainIfBlock = new AbstractInsnNode[] {
				this.currentInsnNode.getPrevious(),
				this.currentTarget[tempNestingLevel],
				this.currIfStmtNode[tempNestingLevel] };

		boolean ternaryOperatorFound = false;
		AbstractInsnNode elseNode = null;
		if (this.isGoToFound[tempNestingLevel] && ifTargetIndex < endIndex) {
		
			
				elseNode = this.scanForElse(ifTargetIndex,
						endIndex);
				if (elseNode != null) {
					this.processElseIf(elseNode,
							 tempNestingLevel);
				} else {
					ternaryOperatorFound = this
							.processTernaryOperator(ifStartIndex,
									endIndex,tempNestingLevel);
					if (!ternaryOperatorFound) {
						this.processElse(ifTargetIndex, endIndex,
								tempNestingLevel);
					}
				}
			
		}
		// logging for before if, after if, in if
		if (!ternaryOperatorFound) {
			InsnList[] il = this
					.getInsnForIfBlock(ainIfBlock,
							this.currentIfCount[tempNestingLevel]);
			this.addInsnForIfBlock(il, ainIfBlock, tempNestingLevel);
		}

		// handling for nested if
		this.instrumentIfBlock(ainIfBlock[2], tempNestingLevel + 1);

		this.currentScanIndexForIf[tempNestingLevel] = gotoTargetIndex;
	}
	/**
	 * <p>
	 * This method processes loops and add logging
	 * </p>
	 * 
	 * @param firstStmtInLoopIndex
	 *            Index of the first statement in the loop.
	 * @param loopEndIndex
	 *            Index where loop ends.
	 * @param nestingLevel
	 *            nesting level
	 */
	private void processLoop(int firstStmtInLoopIndex, int loopEndIndex,
			int nestingLevel) {
		logger.debug(firstStmtInLoopIndex + "   " + loopEndIndex);
		this.currentLoopCount[nestingLevel]++;
		AbstractInsnNode abstractInsnNode;
		// adding loop entry
		abstractInsnNode = this.insnArr[firstStmtInLoopIndex];
		AbstractInsnNode gotoNode = abstractInsnNode.getPrevious();
		AbstractInsnNode lineNode = abstractInsnNode.getNext();
		if ((gotoNode instanceof JumpInsnNode && Opcodes.GOTO == gotoNode
				.getOpcode()) || (!(lineNode instanceof LineNumberNode))) {
			lineNode = getPreviousLineNode(abstractInsnNode);
		}
		Integer lineNumber = ((LineNumberNode) lineNode).line;
		String cSymbol = env.getClassSymbol(getClassName());
		String mSymbol = env.getMethodSymbol(getClassName(), cSymbol, name);
		InsnList il1 = InstrumentUtil.addLogMessage(cSymbol,mSymbol,
				InstrumentationMessageLoader
						.getMessage(MessageConstants.ENTERED_LOOP), lineNumber
						.toString(), this.currentLoopCount[nestingLevel]
						.toString());
		instructions.insertBefore(lineNode.getPrevious(), il1);
		// handling map reduce output in the loop
		handleCtxWrite(firstStmtInLoopIndex, loopEndIndex);
		// adding loop exit
		abstractInsnNode = this.insnArr[loopEndIndex];
		InsnList il2 = InstrumentUtil.addloopCounterLogging(cSymbol,
				mSymbol, InstrumentationMessageLoader
						.getMessage(MessageConstants.EXITING_LOOP),
				this.localVariableSize, this.currentLoopCount[nestingLevel]
						.toString());
		// resetting the counter to ZERO
		il2.add(new LabelNode());
		il2.add(new InsnNode(Opcodes.ICONST_0));
		il2.add(new VarInsnNode(Opcodes.ISTORE, this.localVariableSize));
		instructions.insert(abstractInsnNode.getNext(), il2);
		this.addLocalVariable(this.localVariableSize);
	}

	/**
	 * <p>
	 * This method provides instructions to add a local variable for loop
	 * counter
	 * </p>
	 * 
	 * @param varIndex
	 *            Index at which the variable will be added
	 */
	@SuppressWarnings("unchecked")
	private void addLocalVariable(int varIndex) {
		if (!this.isVariableAdded) {
			for (int j = 0; j < this.insnArr.length; j++) {
				AbstractInsnNode abstractInsnNode = this.insnArr[j];
				if (abstractInsnNode instanceof LabelNode) {
					// adding variable declaration and initializing with ZERO
					LabelNode ln = (LabelNode) abstractInsnNode;
					InsnList il = new InsnList();
					il.add(new LabelNode());
					il.add(new InsnNode(Opcodes.ICONST_0));
					il.add(new VarInsnNode(Opcodes.ISTORE, varIndex));
					instructions.insertBefore(ln, il);
					// adding local variable
					LabelNode begin = new LabelNode();
					LabelNode end = new LabelNode();
					instructions.insertBefore(instructions.getFirst(), begin);
					instructions.insert(instructions.getLast(), end);
					LocalVariableNode lv = new LocalVariableNode(LOOP_COUNTER,
							Type.INT_TYPE.getDescriptor(), null, begin, end,
							varIndex);
					localVariables.add(lv);
					this.isVariableAdded = true;
					break;
				}
			}
		}
	}

	/**
	 * <p>
	 * This method scans for '&&' operator within if condition and returns the
	 * node
	 * </p>
	 * 
	 * @param ifStartIndex
	 *            Index of start instruction
	 * @return
	 */
	private JumpInsnNode scanForAnd(int ifStartIndex) {
		JumpInsnNode ifNodeStmt = null;
		JumpInsnNode startJIN = (JumpInsnNode) this.insnArr[ifStartIndex];
		for (int i = ifStartIndex + 1; i < this.insnArr.length; i++) {
			AbstractInsnNode ain = this.insnArr[i];
			// Jump Instruction Node
			if (ain instanceof JumpInsnNode
					&& InstrumentUtil.getJumpInsnOpcodesMap().containsKey(
							ain.getOpcode())) {
				JumpInsnNode nextJIN = (JumpInsnNode) ain;
				// if the targets are same
				if (nextJIN.label == startJIN.label) {
					ifNodeStmt = nextJIN;
				}
				break;
			}
			// Method Instruction Node, then Jump Instruction Node
			else if (ain instanceof MethodInsnNode) {
				if (ain.getNext() instanceof JumpInsnNode
						&& InstrumentUtil.getJumpInsnOpcodesMap().containsKey(
								ain.getNext().getOpcode())) {
					JumpInsnNode nextJIN = (JumpInsnNode) ain.getNext();
					// if the targets are same
					if (nextJIN.label == startJIN.label) {
						ifNodeStmt = nextJIN;
					}
					break;
				} else {
					return null;
				}
			}
		}

		return ifNodeStmt;
	}

	/**
	 * <p>
	 * This method provides the instructions for injecting logging for if blocks
	 * </p>
	 * 
	 * @param nodes
	 *            Nodes at which instructions to be injected. They are used here
	 *            to find the line number in the code.
	 * @param ifCount
	 *            if block number
	 * @param nestingLevel
	 *            nesting level
	 * 
	 * @return Array of instructions
	 */
	private InsnList[] getInsnForIfBlock(AbstractInsnNode[] nodes,
			Integer ifCount) {
		int[] logMsgs = new int[] { MessageConstants.MSG_BEFORE_IF,
				MessageConstants.MSG_AFTER_IF, MessageConstants.MSG_IN_IF };
		InsnList[] ilArr = new InsnList[logMsgs.length];
		int i = 0;

		/**
		 * The if block number is provided in the log to separate out the
		 * various if blocks in a method
		 */
		// BEFORE_IF
		Integer lineNumber = this.getPreviousLineNumber(nodes[i]);
		String cSymbol = env.getClassSymbol(getClassName()); 
		String mSymbol = env.getMethodSymbol(getClassName(), cSymbol, name);

		ilArr[i] = InstrumentUtil.addLogMessage(cSymbol, mSymbol,
				InstrumentationMessageLoader.getMessage(logMsgs[i]),
				lineNumber.toString(), ifCount.toString());
		i++;

		// AFTER_IF
		lineNumber = this.getNextLineNumber(nodes[i]) - 1;
		ilArr[i] = InstrumentUtil.addLogMessage(cSymbol,mSymbol,
				InstrumentationMessageLoader.getMessage(logMsgs[i]),
				lineNumber.toString(), ifCount.toString());
		i++;

		// IN_IF
		lineNumber = this.getNextLineNumber(nodes[i]);
		ilArr[i] = InstrumentUtil.addLogMessage(cSymbol,mSymbol,
				InstrumentationMessageLoader.getMessage(logMsgs[i]),
				lineNumber.toString(), ifCount.toString());

		return ilArr;
	}

	/**
	 * <p>
	 * This method scans the instructions for 'else' and returns the node
	 * </p>
	 * 
	 * @param ifTargetIndex
	 *            Index of the target instruction of 'if'
	 * @param endIndex
	 *            Index of the end instruction upto which scanner will work
	 * @param nestingLevel
	 *            nesting level
	 * @return Node
	 */
	private AbstractInsnNode scanForElse(int ifTargetIndex, int endIndex) {
		boolean lineNumberFound = false;
		LabelNode ln = (LabelNode) this.insnArr[ifTargetIndex];
		for (int i = ifTargetIndex + 1; i <= endIndex; i++) {
			AbstractInsnNode ain = this.insnArr[i];
			if (ain instanceof JumpInsnNode
					&& InstrumentUtil.getJumpInsnOpcodesMap().containsKey(
							ain.getOpcode())) {
				if (!lineNumberFound) {
					return ain;
				}
			} else if (ain instanceof LineNumberNode) {
				LineNumberNode lnn = (LineNumberNode) ain;
				// if the line does not belong to the label
				if (lnn.start != ln) {
					lineNumberFound = true;
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * <p>
	 * This method gets the end index for the provided Jump instruction
	 * </p>
	 * 
	 * @param ain
	 *            The given jump node
	 * @return int end index
	 */
	private int getEndIndexForBlock(AbstractInsnNode ain) {
		int retIndex = 0;
		if (ain instanceof JumpInsnNode) {
			JumpInsnNode jin = (JumpInsnNode) ain;
			LabelNode targetAIN = jin.label;
			if (targetAIN.getPrevious() instanceof JumpInsnNode
					&& Opcodes.GOTO == targetAIN.getPrevious().getOpcode()) {
				retIndex = CollectionUtil.getObjectIndexInArray(this.insnArr, targetAIN
						.getPrevious().getPrevious());
			} else {
				retIndex = CollectionUtil.getObjectIndexInArray(this.insnArr,
						targetAIN.getPrevious());
			}
		}
		return retIndex;
	}

	/**
	 * <p>
	 * This method instruments the if/else/elseif block for the given nesting
	 * level
	 * </p>
	 * 
	 * @param ain
	 *            The jump instruction node which needs to be handled for the
	 *            nesting. The node which represents the if, else or elseif
	 *            block
	 * 
	 * @param nestingLevel
	 *            nesting level
	 */
	private void instrumentIfBlock(AbstractInsnNode ain, int nestingLevel) {
		this.instrumentIfBlock(
				CollectionUtil.getObjectIndexInArray(this.insnArr, ain) + 1,
				getEndIndexForBlock(ain), nestingLevel);
	}

	/**
	 * <p>
	 * This method tracks for mapreduce output method. If found, a counter is
	 * incremented.
	 * </p>
	 * 
	 * @param startIndex
	 *            Start index of the instructions
	 * @param endIndex
	 *            End index of the instructions
	 */
	private void handleCtxWrite(int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			AbstractInsnNode ain = this.insnArr[i];
			if (ain instanceof MethodInsnNode) {
				MethodInsnNode min = (MethodInsnNode) ain;
				if (InstrumentUtil.isOutputMethod(min)) {
					// adding incremental statement
					InsnList il = new InsnList();
					il.add(new IincInsnNode(this.localVariableSize, 1));
					instructions.insertBefore(ain, il);
				}
			}
		}
	}

	/**
	 * <p>
	 * This method scan for '&&' and '||' operator within if condition
	 * </p>
	 * Note: This is applicable for loops, not for if statements.
	 * 
	 * @param ifNode
	 *            If node
	 * @return JumpInsnNode The last associated instruction with &&/|| operators
	 * 
	 * @see #scanAndOrOperator(int, int, int)
	 */
	private JumpInsnNode scanAndOrOperatorForLoop(AbstractInsnNode ifNode) {
		JumpInsnNode currentJumpInstruction = null;
		AbstractInsnNode currentIfStatementNode = null;
		AbstractInsnNode target = ((JumpInsnNode) ifNode).label;

		boolean scanFurther = true;

		int ifStartIndex = CollectionUtil.getObjectIndexInArray(insnArr, ifNode);
		int ifTargetIndex = CollectionUtil.getObjectIndexInArray(insnArr, target);
		int scanForAndIndex = ifStartIndex;

		while (scanFurther) {
			if ((target.getPrevious() instanceof JumpInsnNode && InstrumentUtil
					.getJumpInsnOpcodesMap().containsKey(
							target.getPrevious().getOpcode()))) {
				/**
				 * && operator found
				 * 
				 * Note: This is valid for loops. For if statements, this case
				 * means an || is found.
				 */
				logger.debug(InstrumentationMessageLoader
						.getMessage(MessageConstants.LOG_MULTIPLE_IF_CONDITION_AND));
				currentIfStatementNode = target;
				currentJumpInstruction = (JumpInsnNode) target.getPrevious();
				target = currentJumpInstruction.label;

				if (ifTargetIndex == CollectionUtil.getObjectIndexInArray(this.insnArr,
						target)) {
						logger.debug("ifTargetIndex == Util.getObjectIndexInArray(this.insnArr, target)");
					scanFurther = false;
				} else if (ifTargetIndex > CollectionUtil.getObjectIndexInArray(
						this.insnArr, target)) {
						logger.debug("ifTargetIndex > Util.getObjectIndexInArray(this.insnArr, target)");
					currentJumpInstruction = (JumpInsnNode) this.insnArr[ifStartIndex];
					target = (LabelNode) this.insnArr[ifTargetIndex];
					scanFurther = false;
				}
			} else{
				this.ifStmtNodeTemp = this.scanForAnd(scanForAndIndex);
					if (this.ifStmtNodeTemp != null) {
					/**
					 * || operator found
					 * 
					 * Note: This is valid for loops. For if statements, this case
					 * means an && is found.
					 */
					logger.debug(InstrumentationMessageLoader
							.getMessage(MessageConstants.LOG_MULTIPLE_IF_CONDITION_OR));
					currentIfStatementNode = this.ifStmtNodeTemp;
					currentJumpInstruction = (JumpInsnNode) currentIfStatementNode;
					target = currentJumpInstruction.label;
					scanForAndIndex = CollectionUtil.getObjectIndexInArray(this.insnArr,
							currentJumpInstruction);
					this.ifStmtNodeTemp = null;
					} else {
						logger.debug(InstrumentationMessageLoader
								.getMessage(MessageConstants.LOG_MULTIPLE_IF_CONDITION_NOT_FOUND));
						scanFurther = false;
				}
			}
			
		}
		return currentJumpInstruction;
	}

	/**
	 * <p>
	 * Process if GoTo instruction is found.
	 * </p>
	 * A GoTo is found before/without an if statement, if the block is:
	 * <ul>
	 * <li>a loop (if is associated with it)
	 * <li>try/catch block (no if is associated)
	 * </ul>
	 * 
	 * @param gotoNode
	 *            GoTo node
	 * @param nestingLevel
	 *            Nesting level
	 * @return boolean Whether is processed or not for a loop
	 */
	@SuppressWarnings("unused")
	private boolean processGoTo(AbstractInsnNode gotoNode, int nestingLevel) {
		// get the target of go to node.
		AbstractInsnNode target = ((JumpInsnNode) gotoNode).label;

		// get the next if node (if any)
		AbstractInsnNode ifNode = target;
		boolean process = false;
		while ((ifNode = ifNode.getNext()) != null) {
			if (ifNode instanceof JumpInsnNode
					&& InstrumentUtil.getJumpInsnOpcodesMap().containsKey(
							ifNode.getOpcode())) {
				// an if statement is found... i.e. a loop is found
				process = true;
				break;
			} else if (Opcodes.RETURN >= ifNode.getOpcode()
					&& Opcodes.IRETURN <= ifNode.getOpcode()) {
				// return is found... i.e. no loop. It may be a catch block
				process = false;
				break;
			}
		}

		// process the loop
		if (process) {
			logger.debug(MessageFormat.format(InstrumentationMessageLoader
					.getMessage(MessageConstants.LOG_LOOP_FOUND),
					getClassName() + "##" + name));

			JumpInsnNode lastIfNode = scanAndOrOperatorForLoop(ifNode);
			if (lastIfNode == null) {
				lastIfNode = (JumpInsnNode) ifNode;
			}
			// index associated with loop
			int firstStmtInLoopIndex = CollectionUtil.getObjectIndexInArray(insnArr,
					lastIfNode.label);
			int loopEndIndex = CollectionUtil.getObjectIndexInArray(insnArr,
					lastIfNode.getNext());

			processLoop(firstStmtInLoopIndex, loopEndIndex, nestingLevel);
			this.currentScanIndexForIf[nestingLevel] = loopEndIndex;
		}
		return process;
	}

	/**
	 * <p>
	 * Finds whether a line node is found in the given range
	 * </p>
	 * 
	 * @param startIndex
	 *            start index for the scan
	 * @param endIndex
	 *            end index for the line
	 * @return boolean true if line node is found
	 */
	private boolean scanForLine(int startIndex, int endIndex) {
		boolean lineFound = false;
		for (int k = startIndex; k <= endIndex; k++) {
			AbstractInsnNode ain = insnArr[k];
			if (ain instanceof LineNumberNode) {
				lineFound = true;
				break;
			}
		}
		return lineFound;
	}
}