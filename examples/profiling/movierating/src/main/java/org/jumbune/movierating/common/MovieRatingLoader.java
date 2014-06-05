package org.jumbune.movierating.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jumbune.movierating.common.MovieRatingLoader;


/**
 * Property loader for regex validator example
 *
 */
public final class MovieRatingLoader {
	

	  private static final Log LOG = LogFactory.getLog( MovieRatingLoader.class );

	  private static Properties prop;

	  private static final String ERROR_MESSAGE = "Could not load properties file!!";
	  
	  private MovieRatingLoader(){
		  
	  }

	  private static void loadProperties()
	    throws IOException
	  {

	    InputStream inputStream = MovieRatingLoader.class.getClassLoader().getResourceAsStream( "moviedemo.properties" );

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