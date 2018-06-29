package org.debugging.defaulter.validation;

import org.apache.hadoop.io.BooleanWritable;

import org.apache.hadoop.io.WritableComparable;

import org.jumbune.utils.PatternValidator;


/**
 * Value validator for defaulter list example.
 * Validates the value to check if it is a valid defaulter
 * 
 */
public class ListDefaulterMapValueValidator  implements PatternValidator{
	
	/***
	 * validates whether the given input value is valid
	 * @return boolean isValid
	 */
	@SuppressWarnings("rawtypes")
	  @Override
	  public boolean isPatternValid( WritableComparable value )
	  {
		boolean isValid = false;
		BooleanWritable booleanWritable=(BooleanWritable) value;
		boolean isDefaulter = booleanWritable.get();
		
	   // if is defaulter is true then only valid input
		isValid = isDefaulter;
		
		return isValid;
	  }	
}

