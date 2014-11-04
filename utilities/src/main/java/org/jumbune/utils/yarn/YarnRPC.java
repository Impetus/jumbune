package org.jumbune.utils.yarn;

import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstraction to get the RPC implementation for Yarn.
 */
public abstract class YarnRPC {
  public static final Logger LOGGER = LogManager.getLogger(YarnRPC.class);
  public abstract Object getProxy(Class protocol, InetSocketAddress addr,
      Configuration conf);

  public abstract void stopProxy(Object proxy, Configuration conf);

  public abstract Server getServer(Class protocol, Object instance,
      InetSocketAddress addr, Configuration conf,
      SecretManager<? extends TokenIdentifier> secretManager,
      int numHandlers, String portRangeConfig);

  public Server getServer(Class protocol, Object instance,
      InetSocketAddress addr, Configuration conf,
      SecretManager<? extends TokenIdentifier> secretManager,
      int numHandlers) {
    return getServer(protocol, instance, addr, conf, secretManager, numHandlers,
        null);
  }
  
  public static YarnRPC create(Configuration conf) {
    LOGGER.debug("Creating YarnRPC for " + 
        conf.get(YarnConfiguration.IPC_RPC_IMPL));
    String clazzName = conf.get(YarnConfiguration.IPC_RPC_IMPL);
    if (clazzName == null) {
      clazzName = YarnConfiguration.DEFAULT_IPC_RPC_IMPL;
    }
    try {
      return (YarnRPC) Class.forName(clazzName).newInstance();
    } catch (Exception e) {
      throw new YarnRuntimeException(e);
    }
  }

}