package org.jumbune.portout.validation;

import org.apache.hadoop.io.WritableComparable;

import org.jumbune.utils.PatternValidator;
import org.jumbune.portout.PortoutConstants;

/**
 * Value validator for reducer of US portout region example.
 * 
 */
public class PortoutReduceValueValidator
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

    if ( values.length == PortoutConstants.SEVEN ) {
      isValid = true;
      String tn = values[PortoutConstants.FIVE];
      if ( tn.indexOf( "2345" ) != -1 ) {
        isValid = false;
      }
    }
    return isValid;
  }
}