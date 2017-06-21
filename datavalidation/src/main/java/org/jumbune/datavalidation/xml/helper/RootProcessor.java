/**
 * 
 */
package org.jumbune.datavalidation.xml.helper;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author vivek.shivhare
 *
 */
public class RootProcessor {
	
	File input;
	
	/**
	 * @param input
	 */
	public RootProcessor(File input) {
		super();
		this.input = input;
	}
	
	public String processRoot(){
		RootProcessorHandler handler =  new RootProcessorHandler();
		try {	
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         saxParser.parse(this.input, handler); 
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		return handler.getRoot();
	}
	
	

}
