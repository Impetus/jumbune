package org.jumbune.utils.yarn;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.net.CachedDNSToSwitchMapping;
import org.apache.hadoop.net.DNSToSwitchMapping;
import org.apache.hadoop.net.NetworkTopology;
import org.apache.hadoop.net.Node;
import org.apache.hadoop.net.NodeBase;
import org.apache.hadoop.net.ScriptBasedMapping;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RackResolver {
	  private static DNSToSwitchMapping dnsToSwitchMapping;
	  private static boolean initCalled = false;
	  public static final Logger LOGGER = LogManager.getLogger(RackResolver.class);

	  public synchronized static void init(Configuration conf) {
	    if (initCalled) {
	      return;
	    } else {
	      initCalled = true;
	    }
	    Class<? extends DNSToSwitchMapping> dnsToSwitchMappingClass =
	      conf.getClass(
	        CommonConfigurationKeysPublic.NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY, 
	        ScriptBasedMapping.class,
	        DNSToSwitchMapping.class);
	    try {
	      DNSToSwitchMapping newInstance = ReflectionUtils.newInstance(
	          dnsToSwitchMappingClass, conf);
	      // Wrap around the configured class with the Cached implementation so as
	      // to save on repetitive lookups.
	      // Check if the impl is already caching, to avoid double caching.
	      dnsToSwitchMapping =
	          ((newInstance instanceof CachedDNSToSwitchMapping) ? newInstance
	              : new CachedDNSToSwitchMapping(newInstance));
	    } catch (Exception e) {
	      throw new RuntimeException(e);
	    }
	  }
	  
	  /**
	   * Utility method for getting a hostname resolved to a node in the
	   * network topology. This method initializes the class with the 
	   * right resolver implementation.
	   * @param conf
	   * @param hostName
	   * @return node {@link Node} after resolving the hostname
	   */
	  public static Node resolve(Configuration conf, String hostName) {
	    init(conf);
	    return coreResolve(hostName);
	  }

	  /**
	   * Utility method for getting a hostname resolved to a node in the
	   * network topology. This method doesn't initialize the class.
	   * Call {@link #init(Configuration)} explicitly.
	   * @param hostName
	   * @return node {@link Node} after resolving the hostname
	   */
	  public static Node resolve(String hostName) {
	    if (!initCalled) {
	      throw new IllegalStateException("RackResolver class not yet initialized");
	    }
	    return coreResolve(hostName);
	  }
	  
	  private static Node coreResolve(String hostName) {
	    List <String> tmpList = new ArrayList<String>(1);
	    tmpList.add(hostName);
	    List <String> rNameList = dnsToSwitchMapping.resolve(tmpList);
	    String rName = null;
	    if (rNameList == null || rNameList.get(0) == null) {
	      rName = NetworkTopology.DEFAULT_RACK;
	      LOGGER.info("Couldn't resolve " + hostName + ". Falling back to "
	          + NetworkTopology.DEFAULT_RACK);
	    } else {
	      rName = rNameList.get(0);
	      LOGGER.info("Resolved " + hostName + " to " + rName);
	    }
	    return new NodeBase(hostName, rName);
	  }

	  /**
	   * Only used by tests
	   */
	  static DNSToSwitchMapping getDnsToSwitchMapping(){
	    return dnsToSwitchMapping;
	  }
	}
