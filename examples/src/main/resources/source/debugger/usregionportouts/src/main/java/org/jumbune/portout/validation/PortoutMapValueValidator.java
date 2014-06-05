package org.jumbune.portout.validation;

import org.apache.hadoop.io.WritableComparable;

import org.jumbune.utils.PatternValidator;
import org.jumbune.portout.PortoutConstants;

/**
 * Value validator for mapper of US portout region example.
 * 
 */
public class PortoutMapValueValidator
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
    String input = value.toString();
    String[] values = input.split( "," );

    boolean isValid = false;
    
    //checks whether size of input is valid
    if ( values.length == PortoutConstants.SIX ) {
      isValid = true;
    }
    return isValid;
  }
}
