/**
 * 
 */
package org.jumbune.datavalidation.xml.helper;

import java.util.Set;

import org.jumbune.datavalidation.xml.XMLValidationBean;
import org.jumbune.datavalidation.xml.XmlDataValidationConstants;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * @author vivek.shivhare
 *
 */
public class LocalErrorHandler implements ErrorHandler {

	 private Set<XMLValidationBean> errorSet  = null;
	 private String fileName = null;

    public LocalErrorHandler(Set<XMLValidationBean> errorSet,String fileName) {
		super();
		this.errorSet = errorSet;
		this.fileName = fileName;
	}

    /**
     * Report a non-fatal error
     * @param ex the error condition
     */
    public void error(SAXParseException ex) {
        
    	String errorMessage = ex.getMessage();
    	if(errorMessage != null){
        XMLValidationBean fieldValidationBean = new XMLValidationBean();
        String errorType = parseKey(errorMessage);
        fieldValidationBean.setErrorType(errorType);
        fieldValidationBean.setFileName(fileName);
        fieldValidationBean.setLineNumber(ex.getLineNumber());
        
        String[] splits = errorMessage.split(":");
        if(splits.length ==2 ){
        	 errorMessage = splits[1].trim();
        }
       
        fieldValidationBean.setErrorDetail(errorMessage);
        errorSet.add(fieldValidationBean);
    	}
    }

    /**
     * Report a fatal error
     * @param ex the error condition
     */

    public void fatalError(SAXParseException ex) {
    	
    	String errorMessage = ex.getMessage();
    	if(errorMessage != null){
    	XMLValidationBean fieldValidationBean = new XMLValidationBean();
    	fieldValidationBean.setErrorType(XmlDataValidationConstants.FATAL_ERROR);
        fieldValidationBean.setFileName(fileName);
        fieldValidationBean.setLineNumber(ex.getLineNumber());

        String[] splits = errorMessage.split(":");
        if(splits.length ==2 ){
        	 errorMessage = splits[1].trim();
        }
       
        fieldValidationBean.setErrorDetail(errorMessage);
        errorSet.add(fieldValidationBean);
    	}
    }
    

    /**
     * Report a warning
     * @param ex the warning condition
     */
    public void warning(SAXParseException ex) {
    	
    	String errorMessage = ex.getMessage();
    	if(errorMessage != null){
    	XMLValidationBean fieldValidationBean = new XMLValidationBean();
        String errorType = parseKey(errorMessage);
        fieldValidationBean.setErrorType(errorType);
        fieldValidationBean.setFileName(fileName);
        fieldValidationBean.setLineNumber(ex.getLineNumber());

        String[] splits = errorMessage.split(":");
        if(splits.length ==2 ){
        	 errorMessage = splits[1].trim();
        }
       
        fieldValidationBean.setErrorDetail(errorMessage);
        errorSet.add(fieldValidationBean);
    	}
    }

    public String parseKey(String message){
    	
    		if(message.contains("cvc-pattern") || message.contains("cvc-totalDigits") || message.contains("cvc-wildcard")){
    			
        		return XmlDataValidationConstants.USER_DEFINED_REGEX_CHECK;
        		
        	}else if(message.contains("cvc-au") || message.contains("cvc-datatype") || message.contains("cvc-enumeration")
        			|| message.contains("cvc-facet") || message.contains("cvc-fractionDigits") || message.contains("cvc-simple-type")
        					|| message.contains("cvc-type")){
        		
        		return XmlDataValidationConstants.USER_DEFINED_DATA_TYPE;
        		
        	}else if(message.contains("cvc-minLength")){
        		
        		return XmlDataValidationConstants.USER_DEFINED_NULL_CHECK;
        		
        	}else if(message.contains("cvc-assess-attr") || message.contains("cvc-assess-elt") || message.contains("cvc-attribute")
        			|| message.contains("cvc-complex-type") || message.contains("cvc-elt") || message.contains("cvc-resolve-instance")){
        		
        		return XmlDataValidationConstants.FATAL_ERROR;
        		
        	}else{
        		
        		return XmlDataValidationConstants.OTHER_XML_ERROR;
        	}
    		
    }

}
