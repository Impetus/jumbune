/**
 * 
 */
package org.jumbune.datavalidation.xml.helper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author vivek.shivhare
 *
 */
public class RootProcessorHandler extends DefaultHandler {
	
	private static boolean isRoot = false;
	
	private String root;
	
    /**
	 * @return the root
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * @param root the root to set
	 */
	public void setRoot(String root) {
		this.root = root;
	}

	/**
     * Receive notification of the start of an element.
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    	if(!isRoot){
    		root = qName;
    		isRoot = true ;
    	}
    }

    }
