package org.jumbune.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Writable;

/**
 * This class will be validating context.write Key/Values against user provided regularExpression
 * 
  */
public final class PatternMatcher {
	
	private PatternMatcher(){
		
	}

	/**
	 * It matches the value against given regular Expression. If value is null no need to match it with regEx
	 * 
	 * @param value
	 *            - The value to be matched
	 * @param regex
	 *            - RegularExpression
	 * @return true if value matches with RegEx false otherwise. If value is null false will be returned
	 */
	public static boolean match(Writable value, String regex) {
		if (value == null) {
			return false;
		}

		String valueStr = value.toString();
		if (valueStr == null || valueStr.length() == 0) {
			return false;
		}
		// TODO: Remove Pattern.compile
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(valueStr);
		return m.matches();
	}

	/**
	 * It matches the key/value with given pattern.
	 * 
	 * @param value
	 *            - The value to be matched
	 * @param pattern
	 *            - Pattern against which it should be matched
	 * @return true if value matches with RegEx false otherwise. If value is null false will be returned
	 */
	public static boolean match(Writable value, Pattern pattern) {
		if (value == null) {
			return false;
		}

		String valueStr = value.toString();
		if (valueStr == null || valueStr.length() == 0) {
			return false;
		}

		Matcher m = pattern.matcher(valueStr);
		return m.matches();
	}

	/**
	 * This method is used to match a key/value against null. This method will be called when the regEx given by user is 'null'. This null is
	 * considered as null object and not as null string.
	 * 
	 * @param value
	 *            - Key/Value to be matched against null
	 * @return true if key/Value is null, false otherwise
	 */
	public static boolean match(Writable value) {
		if (value == null) {
			return true;
		}
		return false;
	}
}