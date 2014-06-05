package org.jumbune.clickstream.validation;

import org.apache.hadoop.io.WritableComparable;
import org.jumbune.utils.PatternValidator;

public class ClickstreamReduceKeyValidator implements PatternValidator{
	

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
