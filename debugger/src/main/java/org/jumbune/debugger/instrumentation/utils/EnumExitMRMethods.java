package org.jumbune.debugger.instrumentation.utils;

/**
 * This ENUM provides the constants for the output write methods.
 * 
 */

public enum EnumExitMRMethods {

	CLEANUP("cleanup"), CLOSE("close");

	private final String exitMethod;

	EnumExitMRMethods(String exitMethod) {
		this.exitMethod = exitMethod;
	}

	@Override
	public String toString() {
		return exitMethod;
	}

}