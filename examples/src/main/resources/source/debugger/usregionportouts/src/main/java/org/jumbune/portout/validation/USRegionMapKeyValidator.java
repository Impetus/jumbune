package org.jumbune.portout.validation;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import org.jumbune.utils.PatternValidator;
import org.jumbune.portout.PortoutConstants;

/**
 * Key validator for mapper of USRegion
 *
 */
public class USRegionMapKeyValidator
  implements PatternValidator
{
	
	/***
	 * validates whether the given input value is valid
	 * @return boolean isValid
	 */
  @SuppressWarnings("rawtypes")
  @Override
  public boolean isPatternValid( WritableComparable value )
  {
    boolean isValid = false;
    if ( value instanceof Text && value.toString().length() == PortoutConstants.FOUR ) {
      isValid = true;
      try {
    	  Integer.valueOf( value.toString() );
      } catch( NumberFormatException nfe ) {
        isValid = false;
      }
    }
    return isValid;
  }
}
