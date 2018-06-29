package org.jumbune.datavalidation.xml.helper;


import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;

/**
 * @author vivek.shivhare
 * 
 */

public class XMLCustomComponent implements XMLComponent {

	public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
	
	/**
     * The domain of messages concerning the XML 1.0 specification.
     */
	public static final String DOMAIN = "http://www.w3.org/TR/xml-schema-1";
	public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
	public static final String XMLNS_DOMAIN = "http://www.w3.org/TR/1999/REC-xml-names-19990114";

	public XMLCustomComponent() {
		super();
	}

	// XMLComponent methods

	public void reset(XMLComponentManager manager)
			throws XMLConfigurationException {
		XMLErrorReporter reporter = (XMLErrorReporter) manager
				.getProperty(ERROR_REPORTER);

		reporter.setFeature(
				"http://apache.org/xml/features/continue-after-fatal-error",
				true);
	}

	@Override
	public String[] getRecognizedFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFeature(String featureId, boolean state)
			throws XMLConfigurationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getRecognizedProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(String propertyId, Object value)
			throws XMLConfigurationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Boolean getFeatureDefault(String featureId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPropertyDefault(String propertyId) {
		// TODO Auto-generated method stub
		return null;
	}


}
