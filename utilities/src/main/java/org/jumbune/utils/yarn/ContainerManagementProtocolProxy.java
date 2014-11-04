package org.jumbune.utils.yarn;

import java.net.InetSocketAddress;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.SecretManager.InvalidToken;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.yarn.security.NMTokenIdentifier;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * Helper class to manage container manager proxies
 */
public class ContainerManagementProtocolProxy {
	private static final Logger LOGGER = LogManager.getLogger(ContainerManagementProtocolProxy.class);

  private final int maxConnectedNMs;
  private final LinkedHashMap<String, ContainerManagementProtocolProxyData> cmProxy;
  private final Configuration conf;
  private final YarnRPC rpc;
  private NMTokenCache nmTokenCache;
  
  public ContainerManagementProtocolProxy(Configuration conf) {
    this(conf, NMTokenCache.getSingleton());
  }

  public ContainerManagementProtocolProxy(Configuration conf,
      NMTokenCache nmTokenCache) {
    this.conf = conf;
    this.nmTokenCache = nmTokenCache;

    maxConnectedNMs =
        conf.getInt(YarnConfiguration.NM_CLIENT_MAX_NM_PROXIES,
            YarnConfiguration.DEFAULT_NM_CLIENT_MAX_NM_PROXIES);
    if (maxConnectedNMs < 1) {
      throw new YarnRuntimeException(
          YarnConfiguration.NM_CLIENT_MAX_NM_PROXIES
              + " (" + maxConnectedNMs + ") can not be less than 1.");
    }
    LOGGER.info(YarnConfiguration.NM_CLIENT_MAX_NM_PROXIES + " : "
        + maxConnectedNMs);

    cmProxy =
        new LinkedHashMap<String, ContainerManagementProtocolProxyData>();
    rpc = YarnRPC.create(conf);
  }
  
  public synchronized ContainerManagementProtocolProxyData getProxy(
      String containerManagerBindAddr, ContainerId containerId)
      throws InvalidToken {
    
    // This get call will update the map which is working as LRU cache.
    ContainerManagementProtocolProxyData proxy =
        cmProxy.get(containerManagerBindAddr);

    while (proxy != null
        && !proxy.token.getIdentifier().equals(
            nmTokenCache.getToken(containerManagerBindAddr).getIdentifier())) {
      LOGGER.info("Refreshing proxy as NMToken got updated for node : "
          + containerManagerBindAddr);
      // Token is updated. check if anyone has already tried closing it.
      if (!proxy.scheduledForClose) {
        // try closing the proxy. Here if someone is already using it
        // then we might not close it. In which case we will wait.
        removeProxy(proxy);
      } else {
        try {
          this.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      if (proxy.activeCallers < 0) {
        proxy = cmProxy.get(containerManagerBindAddr);
      }
    }
    
    if (proxy == null) {
      proxy =
          new ContainerManagementProtocolProxyData(rpc, containerManagerBindAddr,
              containerId, nmTokenCache.getToken(containerManagerBindAddr));
      if (cmProxy.size() > maxConnectedNMs) {
        // Number of existing proxy exceed the limit.
        String cmAddr = cmProxy.keySet().iterator().next();
        removeProxy(cmProxy.get(cmAddr));
      }
      
      cmProxy.put(containerManagerBindAddr, proxy);
    }
    // This is to track active users of this proxy.
    proxy.activeCallers++;
    updateLRUCache(containerManagerBindAddr);
    
    return proxy;
  }
  
  private void updateLRUCache(String containerManagerBindAddr) {
    ContainerManagementProtocolProxyData proxy =
        cmProxy.remove(containerManagerBindAddr);
    cmProxy.put(containerManagerBindAddr, proxy);
  }

  public synchronized void mayBeCloseProxy(
      ContainerManagementProtocolProxyData proxy) {
    proxy.activeCallers--;
    if (proxy.scheduledForClose && proxy.activeCallers < 0) {
      LOGGER.info("Closing proxy : " + proxy.containerManagerBindAddr);
      cmProxy.remove(proxy.containerManagerBindAddr);
      try {
        rpc.stopProxy(proxy.getContainerManagementProtocol(), conf);
      } finally {
        this.notifyAll();
      }
    }
  }

  private synchronized void removeProxy(
      ContainerManagementProtocolProxyData proxy) {
    if (!proxy.scheduledForClose) {
      proxy.scheduledForClose = true;
      mayBeCloseProxy(proxy);
    }
  }
  
  public synchronized void stopAllProxies() {
    List<String> nodeIds = new ArrayList<String>();
    nodeIds.addAll(this.cmProxy.keySet());
    for (String nodeId : nodeIds) {
      ContainerManagementProtocolProxyData proxy = cmProxy.get(nodeId);
      // Explicitly reducing the proxy count to allow stopping proxy.
      proxy.activeCallers = 0;
      try {
        removeProxy(proxy);
      } catch (Throwable t) {
        LOGGER.error("Error closing connection", t);
      }
    }
    cmProxy.clear();
  }
  
  public class ContainerManagementProtocolProxyData {
    private final String containerManagerBindAddr;
    private final ContainerManagementProtocol proxy;
    private int activeCallers;
    private boolean scheduledForClose;
    private final Token token;
    
    public ContainerManagementProtocolProxyData(YarnRPC rpc,
        String containerManagerBindAddr,
        ContainerId containerId, Token token) throws InvalidToken {
      this.containerManagerBindAddr = containerManagerBindAddr;
      ;
      this.activeCallers = 0;
      this.scheduledForClose = false;
      this.token = token;
      this.proxy = newProxy(rpc, containerManagerBindAddr, containerId, token);
    }

    protected ContainerManagementProtocol newProxy(final YarnRPC rpc,
        String containerManagerBindAddr, ContainerId containerId, Token token)
        throws InvalidToken {

      if (token == null) {
        throw new InvalidToken("No NMToken sent for "
            + containerManagerBindAddr);
      }
      
      final InetSocketAddress cmAddr =
          NetUtils.createSocketAddr(containerManagerBindAddr);
      LOGGER.info("Opening proxy : " + containerManagerBindAddr);
      // the user in createRemoteUser in this context has to be ContainerID
      UserGroupInformation user =
          UserGroupInformation.createRemoteUser(containerId
              .getApplicationAttemptId().toString());

      org.apache.hadoop.security.token.Token<NMTokenIdentifier> nmToken =
          ConverterUtils.convertFromYarn(token, cmAddr);
      user.addToken(nmToken);

      ContainerManagementProtocol proxy = user
          .doAs(new PrivilegedAction<ContainerManagementProtocol>() {

            @Override
            public ContainerManagementProtocol run() {
              return (ContainerManagementProtocol) rpc.getProxy(
                  ContainerManagementProtocol.class, cmAddr, conf);
            }
          });
      return proxy;
    }

    public ContainerManagementProtocol getContainerManagementProtocol() {
      return proxy;
    }
  }
  
}