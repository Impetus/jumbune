package com.impetus.debugging.defaulter.validation;

import org.apache.hadoop.io.WritableComparable;

import org.jumbune.utils.PatternValidator;

/**
 * Key validator for defaulter list example.
 * Validates the key to match a certain criteria. Returns false if invalid
 * 
 */
public class ListDefaulterMapKeyValidator  implements PatternValidator
{
	
	/**
	 * validates whether the given input key is valid
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
