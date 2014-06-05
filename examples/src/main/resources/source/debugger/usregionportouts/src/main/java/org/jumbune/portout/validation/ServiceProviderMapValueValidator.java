package org.jumbune.portout.validation;

import org.apache.hadoop.io.WritableComparable;

import org.jumbune.utils.PatternValidator;
import org.jumbune.portout.PortoutConstants;

/**
 * Value validator for mapper service provider.
 * 
 */
public class ServiceProviderMapValueValidator
  implements PatternValidator
{
	/***
	 * validates whether the given input pattern is valid
	 * @return boolean isValid
	 */
  @SuppressWarnings("rawtypes")
  @Override
  public boolean isPatternValid( WritableComparable value )
  {
    boolean isValid = false;
    String[] values = value.toString().split( "," );
    if ( values.length == PortoutConstants.SIX ) {
      String date = values[1];
      try {
        int year = Integer.valueOf( date.substring( 0, PortoutConstants.FOUR ) );
        if ( year != PortoutConstants.TWO_THOUSAND ) {
          isValid = true;
        }
      } catch( NumberFormatException nfe ) {
        isValid = false;
      }
    }
    return isValid;
  }
}
