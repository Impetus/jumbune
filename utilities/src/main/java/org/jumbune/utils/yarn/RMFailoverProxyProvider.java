package org.jumbune.utils.yarn;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.retry.FailoverProxyProvider;

public interface RMFailoverProxyProvider<T> extends FailoverProxyProvider <T> {
	  /**
	   * Initialize internal data structures, invoked right after instantiation.
	   *
	   * @param conf Configuration to use
	   * @param proxy The {@link RMProxy} instance to use
	   * @param protocol The communication protocol to use
	   */
	  public void init(Configuration conf, RMProxy<T> proxy, Class<T> protocol);
	}
