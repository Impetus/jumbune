/**
 * 
 */
package org.jumbune.common.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds list of packages, classes which should not be instrumented.
 * 

 */
public class DoNotInstrument {

	/** List of packages that should not be instrumented. */
	private List<String> packages;

	/** List of classes that should not be instrumented. */
	private List<String> classes;

	/** List of classes which should be instrumented even if there packages are not instrumented. */
	private List<String> includeAnyways;

	/** The Constant DOT. */
	private static final char DOT = '.';
	
	/** The Constant SLASH. */
	private static final char SLASH = '/';
	

	/**
	 * Gets the packages.
	 *
	 * @return the packages
	 */
	public final List<String> getPackages() {
		return packages;
	}

	/**
	 * Sets the packages.
	 *
	 * @param packages the new packages
	 */
	public final void setPackages(List<String> packages) {
		if (packages != null) {
			this.packages = new ArrayList<String>(packages.size());
			for (String packageName : packages) {
				this.packages.add(convertQualifiedClassNameToInternalName(packageName));
			}
		}
	}

	/**
	 * Gets the classes.
	 *
	 * @return the classes
	 */
	public final List<String> getClasses() {
		return classes;
	}

	/**
	 * Sets the classes.
	 *
	 * @param classes the new classes
	 */
	public final void setClasses(List<String> classes) {
		if (classes != null) {
			this.classes = new ArrayList<String>(classes.size());
			for (String classesName : classes) {
				this.classes.add(convertQualifiedClassNameToInternalName(classesName));
			}
		}
	}

	/**
	 * Gets the include anyways.
	 *
	 * @return the include anyways
	 */
	public final List<String> getIncludeAnyways() {
		return includeAnyways;
	}

	/**
	 * Sets the include anyways.
	 *
	 * @param includeAnyways the new include anyways
	 */
	public final void setIncludeAnyways(List<String> includeAnyways) {
		if (includeAnyways != null) {
			this.includeAnyways = new ArrayList<String>(includeAnyways.size());
			for (String classesName : includeAnyways) {
				this.includeAnyways.add(convertQualifiedClassNameToInternalName(classesName));
			}
		}
	}

	
	/**
	 * This method converts given qualified class name to internal name by replacing . with /
	 *
	 * @param className the class name
	 * @return the string
	 */
	private String convertQualifiedClassNameToInternalName(String className) {
		if (className == null) {
			return className;
		}
		return className.replace(DOT, SLASH);
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DoNotInstrument [packages=" + packages + ", classes=" + classes + ", includeAnyways=" + includeAnyways + "]";
	}
}
