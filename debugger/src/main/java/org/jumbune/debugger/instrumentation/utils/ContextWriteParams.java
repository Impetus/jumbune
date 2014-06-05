/**
 * 
 */
package org.jumbune.debugger.instrumentation.utils;

/**
 * This is an interesting class as it holds only the indexes of temporary variables that store the value of key/value passed in context.write. i.e. we
 * are storing the index of temp1 and temp2 shown below.
 * 
 * context.write(temp1 = key, temp2 = value)
 * 
 * This class is required by both ContextWriteValidator and PartitionerAdapter as they both operate on values passed in context.write()
 * 
 * 
 */
public final class ContextWriteParams {

	private static ContextWriteParams contextWriteParams;

	/**
	 * Index of variable that copies the key in context.write() and stores its value
	 */
	private int tempKeyVariableIndex;

	/**
	 * Index of variable that copies the value in context.write() and stores its value
	 */
	private int tempValueVariableIndex;

	private ContextWriteParams() {
	}

	/**
	 * get the instance of contextWriteParams
	 * @return
	 */
	public static synchronized ContextWriteParams getInstance() {
		if (contextWriteParams == null) {
			contextWriteParams = new ContextWriteParams();
		}
		return contextWriteParams;
	}

	/**
	 * getter for tempKeyVariableIndex
	 * @return
	 */
	public int getTempKeyVariableIndex() {
		return tempKeyVariableIndex;
	}

	/**
	 * setter for tempKeyVariableIndex
	 * @param tempKeyIndex
	 */
	public void setTempKeyVariableIndex(int tempKeyIndex) {
		this.tempKeyVariableIndex = tempKeyIndex;
	}

	/**
	 * getter for tempValueVariableIndex
	 * @return
	 */
	public int getTempValueVariableIndex() {
		return tempValueVariableIndex;
	}

	/**
	 * setter for tempValueVariableIndex
	 * @param tempValueIndex
	 */
	public void setTempValueVariableIndex(int tempValueIndex) {
		this.tempValueVariableIndex = tempValueIndex;
	}
}
