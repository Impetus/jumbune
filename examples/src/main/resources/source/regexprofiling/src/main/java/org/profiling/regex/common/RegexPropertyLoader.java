package org.profiling.regex.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RegexPropertyLoader {
	

	  private static final Log LOG = LogFactory.getLog( RegexPropertyLoader.class );

	  private static Properties prop;

	  private static final String ERROR_MESSAGE = "Could not load properties file!!";

	  private static void loadProperties()
	    throws IOException
	  {

	    InputStream inputStream = RegexPropertyLoader.class.getClassLoader().getResourceAsStream( "moviedemo.properties" );

	    prop = new Properties();

	    if ( inputStream != null )
	      prop.load( inputStream );
	  }

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