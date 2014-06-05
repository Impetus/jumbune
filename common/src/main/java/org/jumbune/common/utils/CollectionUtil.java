package org.jumbune.common.utils;

import java.util.List;

import org.jumbune.utils.UtilitiesConstants;



/**
 * The Class CollectionUtil contains all the utility methods for collections.
 */
public final class CollectionUtil {
	
	
	
	/**
	 * Instantiates a new collection util.
	 */
	private CollectionUtil(){
	}

	/**
	 * This method finds if an array contains the given object.
	 *
	 * @param array Array to be scanned
	 * @param key given object
	 * @return boolean true if given object is found
	 */
	public static boolean arrayContains(Object[] array, Object key) {
		return CollectionUtil.getObjectIndexInArray(array, key) >= 0;
	}

	/**
	 * Method to find if the given key is part of any of the array values
	 * <p>
	 * e.g. if array contains <i>"abc"</i> and given key is <i>"abcdef"</i>; true is returned
	 * </p>
	 * 
	 * @param array
	 *            Array to be scanned
	 * @param key
	 *            key
	 * @return boolean true if partial key is found
	 */
	public static boolean arrayContainsPartKey(String[] array, String key) {
		for (String string : array) {
			if (key.indexOf(string) != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to find the index of a given key in an array.
	 * 
	 * @param array
	 *            Array to be scanned
	 * @param key
	 *            Key to be searched
	 * @return int index of the key in the array. -1 if key is not found.
	 */
	public static int getObjectIndexInArray(Object[] array, Object key) {
		int index = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(key)) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * This method checks whether a given string is null or empty.
	 *
	 * @param str given string
	 * @return true if the given string is null or empty
	 */
	public static boolean isNullOrEmpty(String str) {
		if (str == null) {
			return true;
		}
	
		if (str.trim().length() == 0) {
			return true;
		}
	
		return false;
	}

	/**
	 * This method creates a character separated list of all the strings.
	 *
	 * @param list List of strings
	 * @param separator character to be used as separator
	 * @return String List separated by the specified character
	 */
	public static String createStringFromList(List<String> list, String separator) {
		if (list != null && list.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (String string : list) {
				builder.append(separator).append(string);
			}
			return builder.substring(1);
		}
		return UtilitiesConstants.EMPTY_STRING;
	}

	/**
	 * This method creates a character separated list of all the strings.
	 *
	 * @param list List of strings
	 * @return String List separated by the default comma character
	 */
	public static String createStringFromList(List<String> list) {
		return createStringFromList(list, UtilitiesConstants.SEPARATOR_COMMA);
	}

	/**
	 * *
	 * This method convert array to a string which is seperated by a seprator passed in argument.
	 *
	 * @param array array which is to be converted into string
	 * @param separator separator between two element of array.
	 * @return String string data
	 */
	public static String convertArrayToString(String[] array, String separator) {
		StringBuilder builder = new StringBuilder();
		for (String string : array) {
			if (string != null) {
				builder.append(separator).append(string);
			}
		}
		return builder.substring(separator.length());
	}
	
	

}
