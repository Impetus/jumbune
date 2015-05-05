package org.jumbune.common.beans;


public class FieldProfilingBean {

	/** The field number. */
	private int fieldNumber;
	
	
	/** The comparison value. */
	private double comparisonValue ;

	/** The data profiling operand. */
	private String dataProfilingOperand = null;

	/**
	 * @return the fieldNumber
	 */
	public int getFieldNumber() {
		return fieldNumber;
	}

	/**
	 * @param fieldNumber
	 *            the fieldNumber to set
	 */
	public void setFieldNumber(int fieldNumber) {
		this.fieldNumber = fieldNumber;
	}

	public void setDataProfilingOperand(String dataProfilingOperand) {
		this.dataProfilingOperand = dataProfilingOperand;
	}

	public String getDataProfilingOperand() {
		return dataProfilingOperand;
	}

	public void setComparisonValue(double comparisonValue) {
		this.comparisonValue = comparisonValue;
	}

	public double getComparisonValue() {
		return comparisonValue;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(comparisonValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((dataProfilingOperand == null) ? 0 : dataProfilingOperand.hashCode());
		result = prime * result + fieldNumber;
		return result;
	}
	
	@Override
	public String toString() {
		return "FieldProfilingBean [fieldNumber=" + fieldNumber
				+ ", comparisonValue=" + String.format("%.3f", comparisonValue)
				+ ", dataProfilingOperand=" + dataProfilingOperand + "]";
	}
	
}