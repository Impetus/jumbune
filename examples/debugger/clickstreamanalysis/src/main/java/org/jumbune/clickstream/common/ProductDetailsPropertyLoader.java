package org.jumbune.clickstream.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jumbune.clickstream.common.ProductDetailsPropertyLoader;


public class ProductDetailsPropertyLoader {
	
	private static final Log LOG = LogFactory.getLog( ProductDetailsPropertyLoader.class );

	  private static Properties prop;

	  private static final String ERROR_MESSAGE = "Could not load properties file!!";
	  
	  private ProductDetailsPropertyLoader(){
		  
	  }

	  private static void loadProperties()
	    throws IOException
	  {

	    InputStream inputStream = ProductDetailsPropertyLoader.class.getClassLoader().getResourceAsStream( "productdata.properties" );

	    prop = new Properties();

	    if ( inputStream != null ){
	      prop.load( inputStream );
	    }
	  }

	  /**
	   * fetch value from property file based on supplied key
	   * @param key
	   * @return
	   */
	  public static String getProperty( String key )
	  {
	    try {
	      if ( prop == null ) {
	        loadProperties();
	      }
	      return prop.getProperty( key );
	    } catch( IOException ie ) {
	      LOG.error( ERROR_MESSAGE );
	      return null;
	    }
	  }

}
