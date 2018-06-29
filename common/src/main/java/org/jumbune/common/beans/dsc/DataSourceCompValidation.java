package org.jumbune.common.beans.dsc;

public class DataSourceCompValidation {

	private String transformationNumber;

	private int sourcefieldNumber;

	private int destinationFieldNumber;

	/**
	 * Transformation method is in form of packagename.classname.methodname eg.
	 * org.jumbune.datavalidation.dsc.beans.DataSourceCompValidation.getTransformationNumber
	 */
	private String transformationMethod;

	public String getTransformationNumber() {
		return transformationNumber;
	}

	public void setTransformationNumber(String transformationNumber) {
		this.transformationNumber = transformationNumber;
	}
	
	public int getSourcefieldNumber() {
		return sourcefieldNumber;
	}

	public void setSourcefieldNumber(int sourcefieldNumber) {
		this.sourcefieldNumber = sourcefieldNumber;
	}

	public int getDestinationFieldNumber() {
		return destinationFieldNumber;
	}

	public void setDestinationFieldNumber(int destinationFieldNumber) {
		this.destinationFieldNumber = destinationFieldNumber;
	}

	public String getTransformationMethod() {
		return transformationMethod;
	}

	public void setTransformationMethod(String transformationMethod) {
		this.transformationMethod = transformationMethod;
	}

}
