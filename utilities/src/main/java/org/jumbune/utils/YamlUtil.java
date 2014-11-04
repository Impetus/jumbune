package org.jumbune.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides api to validate yaml entries and other util methods
 * 
 */
public final class YamlUtil {
  
	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private static final String FILESYSTEM_LOC_PATTERN = "(/[a-zA-Z0-9_ -]+\\s*)+(/)?(\\*)?(\\*\\.(log)?\\*)?(\\.\\*)?(\\.json)?";

	private static final char STARTER = '<';
	private static final char CLOSER = '>';
	private static final int INVALID_INDEX = -1;
	private static Pattern ipPattern = Pattern.compile(IPADDRESS_PATTERN);
	private static Pattern filePattern = Pattern.compile(FILESYSTEM_LOC_PATTERN);
	/**
	 * Instantiates a new yaml util.
	 */
	private YamlUtil(){
		
	}

	/**
	 * This method gets the place holders for the passed string.
	 *
	 * @param str the str
	 * @return the place holders
	 */
	public static String[] getPlaceHolders(String str) {
		ArrayList<String> list = new ArrayList<String>();
		int[] array = null;

		boolean scan = true;
		int scanIndex = 0;

		while (scan) {
			array = getPlaceHolder(str, scanIndex);
			if (array != null) {
				String prop = str.substring(array[0] + 1, array[1]);
				list.add(prop);
				scanIndex = array[1] + 1;
				array = null;
			} else {
				break;
			}
		}

		String[] retArray = null;
		if (list.size() > 0) {
			retArray = new String[list.size()];
			list.toArray(retArray);
		}

		return retArray;
	}

	/**
	 * This api gets the place holder according to the starting index of the string.
	 *
	 * @param str the str
	 * @param start the start
	 * @return the place holder
	 */
	public static int[] getPlaceHolder(String str, int start) {
		String scanString = str.substring(start);

		int index = scanString.indexOf(STARTER);
		if (index != INVALID_INDEX) {
			int endIndex = scanString.indexOf(CLOSER);
			if (endIndex != INVALID_INDEX) {
				return new int[] { start + index, start + endIndex };
			}
		}
		return null;
	}

	/**
	 * This method replaces the holder according to the property.
	 *
	 * @param str the str
	 * @param prop the prop
	 * @return the string
	 */
	public static String replaceHolder(String str, String prop) {
		return str
				.replaceAll(STARTER + prop + CLOSER, System.getProperty(prop));
	}

	/**
	 * This method gets and replace holders for the string passed.
	 *
	 * @param str the str
	 * @return the and replace holders
	 */
	public static String getAndReplaceHolders(String str) {
		if (str != null) {
			String[] props = YamlUtil.getPlaceHolders(str);
			return replaceHolders(str, props);
		}
		return null;
	}

	/**
	 * This method is used to replace holders.
	 *
	 * @param str the str
	 * @param props the props
	 * @return the string
	 */
	public static String replaceHolders(String str, String[] props) {
		String strTmp = str;
		if (strTmp != null && props != null && props.length > 0) {
					for (String prop : props) {
					strTmp = replaceHolder(strTmp, prop);
				}
	
		}
		return strTmp;
	}

	/**
	 * This method gets and replace holders for an array of strings.
	 *
	 * @param strArray the str array
	 * @return the and replace holders
	 */
	public static String[] getAndReplaceHolders(String[] strArray) {
		if (strArray != null) {
			for (int i = 0; i < strArray.length; i++) {
				String[] props = YamlUtil.getPlaceHolders(strArray[i]);
				strArray[i] = replaceHolders(strArray[i], props);
			}
		}
		return strArray;
	}

	/**
	 * This method is used to validate IP address pattern.
	 *
	 * @param address the address
	 * @return true, if successful
	 */
	public static boolean validateIPAddress(String address) {
		Matcher matcher = ipPattern.matcher(address);

		return matcher.matches();
	}

	/**
	 * This method is used to validate file system location.
	 *
	 * @param address the address
	 * @return true, if successful
	 */
	public static boolean validateFileSystemLocation(String address) {
		Matcher matcher = filePattern.matcher(address);
		return matcher.matches();
	}
}
