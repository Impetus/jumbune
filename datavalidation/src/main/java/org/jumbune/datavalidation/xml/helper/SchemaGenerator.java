/**
 * 
 */
package org.jumbune.datavalidation.xml.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.inst2xsd.Inst2Xsd;
import org.apache.xmlbeans.impl.inst2xsd.Inst2XsdOptions;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.jumbune.common.beans.XmlElementBean;
import org.jumbune.datavalidation.xml.JumbuneSchemaWriter;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * @author vivek.shivhare
 *
 */
public class SchemaGenerator {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(SchemaGenerator.class);
	  
	    public SchemaDocument generateSchema(File inputFile) throws XmlException, IOException {
	    	 Inst2XsdOptions inst2XsdOptions = new Inst2XsdOptions();
		     inst2XsdOptions.setDesign(Inst2XsdOptions.DESIGN_VENETIAN_BLIND);
		     
		     XmlObject[] xmlInstances = new XmlObject[1];
		     xmlInstances[0] = XmlObject.Factory.parse(inputFile);
		     
		     SchemaDocument[] schemaDocuments = Inst2Xsd.inst2xsd(xmlInstances, inst2XsdOptions);
		        if (schemaDocuments != null && schemaDocuments.length > 0) {
		            return schemaDocuments[0];
		        }
		  
		        return null;
	    	
	    }
	  
	 public boolean updateSchema(String schemaPath, Map<String, XmlElementBean> elementMap) throws SAXException, ParserConfigurationException{
		 
		 File file = new File(schemaPath);
		 try {
			 
			 Map<String,String> uriMapping = new HashMap<String,String>();
			 
			 SAXParserFactory spf = SAXParserFactory.newInstance();
				spf.setNamespaceAware(true);
				XMLReader xr = spf.newSAXParser().getXMLReader();
				xr.setContentHandler(new LocalContentHandler(uriMapping));
			 
				xr.parse(file.getPath());	
			
			 XSOMParser parser = new XSOMParser();
			 
			 parser.parse(file);
			 XSSchemaSet schemaSet = parser.getResult();
			 
			 XSSchema xsSchema = schemaSet.getSchema(1);
			 
			 StringWriter string = new StringWriter();
			 
			//TODO - list to map 
			 
			 JumbuneSchemaWriter schemaWriter = new JumbuneSchemaWriter(string,elementMap,uriMapping);
			 
			 schemaWriter.schema(xsSchema);
			
			 FileWriter fw = new FileWriter(schemaPath);
			 fw.write(string.toString());
			 fw.close();
		     return true ;
		 }
		 catch (FactoryConfigurationError | IOException e) {
			 LOGGER.error(e.getMessage());
			 return false;
		 }
		
	 }

}
