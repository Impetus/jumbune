package org.jumbune.portout;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jumbune.portout.PortoutHelper;

/**
 * Helper class contains helper methods used be different classes.
 */
public final class PortoutHelper
{
	
	private PortoutHelper(){
		
	}
  /**
   * @param s
   *          string to be checked for null/empty.
   * @return true if the string is null or empty
   */
  public static boolean isEmptyOrNull( String s )
  {
    return ( s == null || s.length() <= 0 || s.equals( "null" ) );
  }

  /**
   * Helper method to tokenize a string on passed delimiter and return a list of tokens.
   * @param source
   * @param delim
   * @return
   */
  public static List<String> tokenize( String source, String delim )
  {
    StringTokenizer stringTokenizer = new StringTokenizer( source, delim );

    List<String> result = new ArrayList<String>( stringTokenizer.countTokens() );

    while ( stringTokenizer.hasMoreTokens() ) {
      result.add( stringTokenizer.nextToken() );
    }

    return result;
  }

  /**
   * This method is used to check for valid records based on the filter criteria passed into the report generator.
   * @param value
   * @param requiredValue
   * @param pType
   * @return
   */
  public static boolean isValid( String value, String requiredValue )
  {
    if ( !PortoutHelper.isEmptyOrNull( requiredValue ) ) {
      return value.equals( requiredValue );
    }
    return true;
  }
}
