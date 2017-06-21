/**
 * 
 */
package org.jumbune.datavalidation.xml;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.StringUtils;

/**
 * @author vivek.shivhare
 *
 */
public class JumbuneDistributedCache {
	
	 public static void addCacheFile(URI uri, Configuration conf) {
		    String files = conf.get(XmlDataValidationConstants.CACHED_SCHEMA);
		    conf.set(XmlDataValidationConstants.CACHED_SCHEMA, files == null ? uri.toString() : files + ","
		             + uri.toString());
		  }
	 
	 public static URI[] getCacheFiles(Configuration conf) throws IOException {
		    return StringUtils.stringToURI(conf.getStrings(XmlDataValidationConstants.CACHED_SCHEMA));
		  }

}
