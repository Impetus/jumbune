/**
 * 
 */
package org.jumbune.debugger.log.processing;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.InsnList;


/**
 * The Class InstructionsBean.
 */
public class InstructionsBean {
	
	/** The insn list. */
	private InsnList insnList;
	
	/** The last variable index. */
	private int lastVariableIndex;

	/** The temporary variables index list. */
	private List<Integer> temporaryVariablesIndexList;
	
	/** The temporary variables index and type. */
	private Map<Integer, String> temporaryVariablesIndexAndType;

	/**
	 * Instantiates a new instructions bean.
	 *
	 * @param insnList the insn list
	 * @param variableIndex the variable index
	 */
	public InstructionsBean(InsnList insnList, int variableIndex) {
		this.insnList = insnList;
		this.lastVariableIndex = variableIndex;
	}

	/**
	 * Instantiates a new instructions bean.
	 */
	public InstructionsBean() {
		temporaryVariablesIndexList = new LinkedList<Integer>();
		temporaryVariablesIndexAndType = new LinkedHashMap<Integer, String>();
	}

	/**
	 * Gets the insn list.
	 *
	 * @return the insn list
	 */
	public InsnList getInsnList() {
		return insnList;
	}

	/**
	 * Sets the insn list.
	 *
	 * @param insnList the new insn list
	 */
	public void setInsnList(InsnList insnList) {
		this.insnList = insnList;
	}

	/**
	 * Gets the variable index.
	 *
	 * @return the variable index
	 */
	public int getVariableIndex() {
		return lastVariableIndex;
	}

	/**
	 * Sets the variable index.
	 *
	 * @param variableIndex the new variable index
	 */
	public void setVariableIndex(int variableIndex) {
		this.lastVariableIndex = variableIndex;
	}

	/**
	 * Adds the index to temporary variables list.
	 *
	 * @param index the index
	 */
	public void addIndexToTemporaryVariablesList(int index) {
		temporaryVariablesIndexList.add(index);
	}

	/**
	 * Put index and type in temp variable map.
	 *
	 * @param index the index
	 * @param type the type
	 */
	public void putIndexAndTypeInTempVariableMap(int index, String type) {
		temporaryVariablesIndexAndType.put(index, type);
	}

	/**
	 * Gets the temporary variables index list.
	 *
	 * @return the temporary variables index list
	 */
	public List<Integer> getTemporaryVariablesIndexList() {
		return temporaryVariablesIndexList;
	}
}
