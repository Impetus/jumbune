/**
 * 
 */
package org.jumbune.datavalidation.xml.helper;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.jumbune.datavalidation.xml.XMLValidationBean;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.parsers.StandardParserConfiguration;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;

/**
 * @author vivek.shivhare
 * 
 */
public class XMLParserImpl {

	XMLReader reader = null;

	public synchronized Set<XMLValidationBean> parseXMLWithGrammar(Text value,
			XMLGrammarPoolImpl grammarPool,String fileName) {
		
		Set<XMLValidationBean> errorSet = new HashSet<XMLValidationBean>();
		XMLParserConfiguration parserConfiguration = new StandardParserConfiguration();

		String grammarPoolProperty = "http://apache.org/xml/properties/internal/grammar-pool";
		try {
			parserConfiguration.setProperty(grammarPoolProperty, grammarPool);

			XMLCustomComponent xmlComponent = new XMLCustomComponent();
			xmlComponent.reset(parserConfiguration);

			parserConfiguration.setFeature(
					"http://xml.org/sax/features/validation", true);
			parserConfiguration.setFeature(
					"http://apache.org/xml/features/validation/schema", true);

		} catch (XMLConfigurationException xce) {
			xce.printStackTrace();
		}
		try {
			if (reader == null){
				reader = new SAXParser(parserConfiguration);
			}
			
			LocalErrorHandler errorHandler= new LocalErrorHandler(errorSet,fileName);
			reader.setErrorHandler(errorHandler);
			reader.parse(new InputSource(new StringReader(value.toString())));
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		}
		return errorSet;
	}

}
