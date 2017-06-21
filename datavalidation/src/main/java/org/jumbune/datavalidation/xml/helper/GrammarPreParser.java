/**
 * 
 */
package org.jumbune.datavalidation.xml.helper;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.XMLReader;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.parsers.XMLGrammarPreparser;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;

/**
 * @author vivek.shivhare
 *
 */
public class GrammarPreParser {
	
	private static final Logger LOGGER = LogManager
			.getLogger(GrammarPreParser.class);
	
	static final String SYMBOL_TABLE = Constants.XERCES_PROPERTY_PREFIX+ Constants.SYMBOL_TABLE_PROPERTY;

	static final String GRAMMAR_POOL = Constants.XERCES_PROPERTY_PREFIX+Constants.XMLGRAMMAR_POOL_PROPERTY;
	
	SymbolTable sym = null;
	XMLGrammarPoolImpl grammarPool = null;
	XMLReader reader = null;
	
	 

	public synchronized XMLGrammarPoolImpl loadCache(String schemaURI) {
		grammarPool = new XMLGrammarPoolImpl();
		
		XMLGrammarPreparser preparser = new XMLGrammarPreparser();
		preparser.registerPreparser(XMLGrammarDescription.XML_SCHEMA, null);
		preparser.setProperty(GRAMMAR_POOL, grammarPool);
		preparser.setFeature("http://xml.org/sax/features/validation", true);
		preparser.setFeature("http://apache.org/xml/features/validation/schema", true);
		// parse the grammar...

		try {
			preparser.preparseGrammar(XMLGrammarDescription.XML_SCHEMA, new XMLInputSource(null,schemaURI, null));
		} catch (XNIException xe) {
			LOGGER.error(xe);
		} catch (IOException ioe) {
			LOGGER.error(ioe);
		}
		return grammarPool;

	}
}
