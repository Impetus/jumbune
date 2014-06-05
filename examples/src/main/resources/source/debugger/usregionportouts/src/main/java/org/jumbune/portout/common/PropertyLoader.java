package org.jumbune.portout.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jumbune.portout.PortoutConstants;
import org.jumbune.portout.common.PropertyLoader;


/**
 * Load Properties reading from file
 */
public final class PropertyLoader
{

  /**
   * Logger instance
   */
  private static final Log LOG = LogFactory.getLog( PropertyLoader.class );

  private static Properties prop;

  private static Map<String, String> serviceProviderIdNameMap;

  private static final String ERROR_MESSAGE = "Could not load properties file!!";

  private static final String SPID_PROPERTY_KEY_PREFIX = "spidName.";
  
  private PropertyLoader(){
	  
  }

  private static void loadProperties()
    throws IOException
  {

    InputStream inputStream = PropertyLoader.class.getClassLoader().getResourceAsStream( "demo.properties" );

    prop = new Properties();

    if ( inputStream != null ){
      prop.load( inputStream );
    }
  }

  /**
   * fetch the value from property file based on given key
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

  /**
   * fetch the integer value from property file based on given key
   * @param key
   * @return
   */
  public static int getIntProperty( String key )
  {
    int errorReturnValue = PortoutConstants.MINUS_HUNDRED;
    try {
      if ( prop == null ) {
        loadProperties();
      }
      return Integer.parseInt( prop.getProperty( key ) );

    } catch( NumberFormatException nfe ) {
      LOG.error( "Key " + key + " returned a value  " + prop.getProperty( key ) + " which could not be parsed" );
    } catch( IOException ie ) {
      LOG.error( ERROR_MESSAGE );
    }

    return errorReturnValue;
  }

  /**
   * get all service provider name information populated in form of a map
   * @return
   */
  public static Map<String, String> getAllSPIDNameMap()
  {
    try {
      if ( prop == null ) {
        loadProperties();
      }

      if ( serviceProviderIdNameMap == null ) {
        serviceProviderIdNameMap = new HashMap<String, String>();

        Set<String> propertyNames = prop.stringPropertyNames();
        for( String key : propertyNames ) {

          if ( key.startsWith( SPID_PROPERTY_KEY_PREFIX ) ) {
            String value = getProperty( key );
            // Get only the id of SPID, so removing its prefix
            key = key.replace( SPID_PROPERTY_KEY_PREFIX, "" );
            serviceProviderIdNameMap.put( key, value );
          }
        }
      }
    } catch( IOException e ) {
      LOG.error( ERROR_MESSAGE );
      return null;
    }
    return serviceProviderIdNameMap;
  }
}
