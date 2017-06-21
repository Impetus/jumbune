/**
 * 
 */
package org.jumbune.datavalidation.xml.helper;

import java.util.Map;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author vivek.shivhare
 *
 */
public class LocalContentHandler extends DefaultHandler {
	
	Map<String,String> uriMapping ;


    /**
	 * @param uriMapping
	 */
	public LocalContentHandler(Map<String, String> uriMapping) {
		this.uriMapping = uriMapping;
	}

	
    
    public void startPrefixMapping (String prefix, String uri)
            throws SAXException
        {
    	uriMapping.put(prefix, uri);
        }

}
