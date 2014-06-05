package org.jumbune.clickstream.validation;

import org.jumbune.utils.PatternValidator;
import org.apache.hadoop.io.WritableComparable;

public class ClickstreamReduceValueValidator implements PatternValidator{

	/**
	 * validates whether the given input key is valid
	 * @return boolean isValid
	 */
	  @SuppressWarnings("rawtypes")
	  @Override
	  public boolean isPatternValid( WritableComparable value )
	  {
	    boolean isValid = false;

	    if (!value.toString().equals( "" )) {
	      isValid = true;
	    }
	    return isValid;
	  }

}
