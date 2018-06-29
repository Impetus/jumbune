package org.jumbune.monitoring.beans;

/**
 * Pojo to store performance parameters (operator and value to be compared)
 * 
*/
public class PerformanceEval {

	private String operator;
	private String val;

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * @return the val
	 */
	public String getVal() {
		return val;
	}

	/**
	 * @param val
	 *            the val to set
	 */
	public void setVal(String val) {
		this.val = val;
	}

}
