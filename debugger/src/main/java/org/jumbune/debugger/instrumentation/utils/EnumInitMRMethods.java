package org.jumbune.debugger.instrumentation.utils;

/**
 * This ENUM provides the constants for the output write methods.
 */

public enum EnumInitMRMethods {

	SETUP("setup"), CONFIGURE("configure");

	private final String initMethod;

	EnumInitMRMethods(String initMethod) {
		this.initMethod = initMethod;
	}

	@Override
	public String toString() {
		return initMethod;
	}

}