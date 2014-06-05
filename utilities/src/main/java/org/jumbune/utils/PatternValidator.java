/**
 * 
 */
package org.jumbune.utils;

import org.apache.hadoop.io.WritableComparable;


/**
 * An interface which must be implemented by user if he wants his class to be called for validating context.write key/values
 * 

 */
public interface PatternValidator {
	 
 	/**
 	 * Checks if is pattern valid.
 	 *
 	 * @param value the value
 	 * @return true, if is pattern valid
 	 */
 	boolean isPatternValid(WritableComparable value);
}
