package org.jumbune.portout.validation;

import org.apache.hadoop.io.WritableComparable;

import org.jumbune.utils.PatternValidator;

/**
 * Key validator for reducer of US portout region example.
 * 
 */
public class PortoutReduceKeyValidator
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

    if ( !value.toString().equals( "" ) ) {
      isValid = true;
    }
    return isValid;
  }
}
