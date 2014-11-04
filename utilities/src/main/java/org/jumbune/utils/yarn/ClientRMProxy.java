package org.jumbune.utils.yarn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientRMProxy<T> extends RMProxy<T>  {
 
  public static final Logger LOGGER = LogManager.getLogger(ClientRMProxy.class);  
  private static final ClientRMProxy INSTANCE = new ClientRMProxy();

  private interface ClientRMProtocols extends ApplicationClientProtocol,
      ApplicationMasterProtocol, ResourceManagerAdministrationProtocol {
    // Add nothing
  }

  private ClientRMProxy(){
    super();
  }

  /**
   * Create a proxy to the ResourceManager for the specified protocol.
   * @param configuration Configuration with all the required information.
   * @param protocol Client protocol for which proxy is being requested.
   * @param <T> Type of proxy.
   * @return Proxy to the ResourceManager for the specified client protocol.
   * @throws IOException
   */
  public static <T> T createRMProxy(final Configuration configuration,
      final Class<T> protocol) throws IOException {
    return createRMProxy(configuration, protocol, INSTANCE);
  }

  private static void setupTokens(InetSocketAddress resourceManagerAddress)
      throws IOException {
    // It is assumed for now that the only AMRMToken in AM's UGI is for this
    // cluster/RM. TODO: Fix later when we have some kind of cluster-ID as
    // default service-address, see YARN-1779.
    for (Token<? extends TokenIdentifier> token : UserGroupInformation
      .getCurrentUser().getTokens()) {
      if (token.getKind().equals(AMRMTokenIdentifier.KIND_NAME)) {
        // This token needs to be directly provided to the AMs, so set the
        // appropriate service-name. We'll need more infrastructure when we
        // need to set it in HA case.
        SecurityUtil.setTokenService(token, resourceManagerAddress);
      }
    }
  }

  @Override
  protected InetSocketAddress getRMAddress(YarnConfiguration conf,
      Class<?> protocol) throws IOException {
    if (protocol == ApplicationClientProtocol.class) {
      return conf.getSocketAddr(YarnConfiguration.RM_ADDRESS,
          YarnConfiguration.DEFAULT_RM_ADDRESS,
          YarnConfiguration.DEFAULT_RM_PORT);
    } else if (protocol == ResourceManagerAdministrationProtocol.class) {
      return conf.getSocketAddr(
          YarnConfiguration.RM_ADMIN_ADDRESS,
          YarnConfiguration.DEFAULT_RM_ADMIN_ADDRESS,
          YarnConfiguration.DEFAULT_RM_ADMIN_PORT);
    } else if (protocol == ApplicationMasterProtocol.class) {
      InetSocketAddress serviceAddr =
          conf.getSocketAddr(YarnConfiguration.RM_SCHEDULER_ADDRESS,
            YarnConfiguration.DEFAULT_RM_SCHEDULER_ADDRESS,
            YarnConfiguration.DEFAULT_RM_SCHEDULER_PORT);
      setupTokens(serviceAddr);
      return serviceAddr;
    } else {
      String message = "Unsupported protocol found when creating the proxy " +
          "connection to ResourceManager: " +
          ((protocol != null) ? protocol.getClass().getName() : "null");
      LOGGER.error(message);
      throw new IllegalStateException(message);
    }
  }

/*  @Override
  protected void checkAllowedProtocols(Class<?> protocol) {
    Preconditions.checkArgument(
        protocol.isAssignableFrom(ClientRMProtocols.class),
        "RM does not support this client protocol");
  }
*/
  /**
   * Get the token service name to be used for RMDelegationToken. Depending
   * on whether HA is enabled or not, this method generates the appropriate
   * service name as a comma-separated list of service addresses.
   *
   * @param conf Configuration corresponding to the cluster we need the
   *             RMDelegationToken for
   * @return - Service name for RMDelegationToken
   */
  public static Text getRMDelegationTokenService(Configuration conf) {
    if (HAUtil.isHAEnabled(conf)) {
      // Build a list of service addresses to form the service name
      ArrayList<String> services = new ArrayList<String>();
      YarnConfiguration yarnConf = new YarnConfiguration(conf);
      for (String rmId : HAUtil.getRMHAIds(conf)) {
        // Set RM_ID to get the corresponding RM_ADDRESS
        yarnConf.set(YarnConfiguration.RM_HA_ID, rmId);
        services.add(SecurityUtil.buildTokenService(
            yarnConf.getSocketAddr(YarnConfiguration.RM_ADDRESS,
                YarnConfiguration.DEFAULT_RM_ADDRESS,
                YarnConfiguration.DEFAULT_RM_PORT)).toString());
      }
      return new Text(services.toString());
    }

    // Non-HA case - no need to set RM_ID
    return SecurityUtil.buildTokenService(
        conf.getSocketAddr(YarnConfiguration.RM_ADDRESS,
            YarnConfiguration.DEFAULT_RM_ADDRESS,
            YarnConfiguration.DEFAULT_RM_PORT));
  }
}
