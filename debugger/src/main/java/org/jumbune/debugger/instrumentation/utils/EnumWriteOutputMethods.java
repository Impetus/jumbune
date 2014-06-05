package org.jumbune.debugger.instrumentation.utils;

/**
 * This ENUM provides the constants for the output write methods.
 * 
 */

public enum EnumWriteOutputMethods {

	MAPPER_WRITE("write", InstrumentConstants.CLASSNAME_MAPPER_CONTEXT), REDUCER_WRITE("write", InstrumentConstants.CLASSNAME_REDUCER_CONTEXT), COLLECT(
			"collect", InstrumentConstants.CLASSNAME_OUTPUTCOLLECTOR);

	private final String name;
	private final String owner;

	EnumWriteOutputMethods(String name, String owner) {
		this.name = name;
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}
}