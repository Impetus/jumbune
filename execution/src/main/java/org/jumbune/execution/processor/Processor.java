package org.jumbune.execution.processor;

import java.util.Map;

import org.jumbune.common.beans.ReportsBean;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.execution.beans.Parameters;
import org.jumbune.utils.exception.JumbuneException;


/**
 * PRocessor interface that identifies the basic method to be implemented by any processor
 * 
 * 
 */
public interface Processor {


	/**
	 * Method for running any processor
	 */
	void process(Loader loader, ReportsBean reports, Map<Parameters, String> parameters) throws JumbuneException;

	/**
	 * Method for chaining one processor with other
	 * 
	 * @param processor
	 */
	void chain(Processor processor);

	
}
