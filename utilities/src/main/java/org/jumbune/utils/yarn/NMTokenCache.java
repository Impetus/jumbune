package org.jumbune.utils.yarn;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;
import org.apache.hadoop.yarn.api.records.Token;

/**
 * NMTokenCache manages NMTokens required for an Application Master
 * communicating with individual NodeManagers.
 * <p/>
 * By default Yarn client libraries {@link AMRMClient} and {@link NMClient} use
 * {@link #getSingleton()} instance of the cache.
 * <ul>
 * <li>Using the singleton instance of the cache is appropriate when running a
 * single ApplicationMaster in the same JVM.</li>
 * <li>When using the singleton, users don't need to do anything special,
 * {@link AMRMClient} and {@link NMClient} are already set up to use the default
 * singleton {@link NMTokenCache}</li>
 * </ul>
 * <p/>
 * If running multiple Application Masters in the same JVM, a different cache
 * instance should be used for each Application Master.
 * <p/>
 * <ul>
 * <li>
 * If using the {@link AMRMClient} and the {@link NMClient}, setting up and using
 * an instance cache is as follows:
 * <p/>
 * 
 * <pre>
 *   NMTokenCache nmTokenCache = new NMTokenCache();
 *   AMRMClient rmClient = AMRMClient.createAMRMClient();
 *   NMClient nmClient = NMClient.createNMClient();
 *   nmClient.setNMTokenCache(nmTokenCache);
 *   ...
 * </pre>
 * </li>
 * <li>
 * If using the {@link AMRMClientAsync} and the {@link NMClientAsync}, setting up
 * and using an instance cache is as follows:
 * <p/>
 * 
 * <pre>
 *   NMTokenCache nmTokenCache = new NMTokenCache();
 *   AMRMClient rmClient = AMRMClient.createAMRMClient();
 *   NMClient nmClient = NMClient.createNMClient();
 *   nmClient.setNMTokenCache(nmTokenCache);
 *   AMRMClientAsync rmClientAsync = new AMRMClientAsync(rmClient, 1000, [AMRM_CALLBACK]);
 *   NMClientAsync nmClientAsync = new NMClientAsync("nmClient", nmClient, [NM_CALLBACK]);
 *   ...
 * </pre>
 * </li>
 * <li>
 * If using {@link ApplicationMasterProtocol} and
 * {@link ContainerManagementProtocol} directly, setting up and using an
 * instance cache is as follows:
 * <p/>
 * 
 * <pre>
 *   NMTokenCache nmTokenCache = new NMTokenCache();
 *   ...
 *   ApplicationMasterProtocol amPro = ClientRMProxy.createRMProxy(conf, ApplicationMasterProtocol.class);
 *   ...
 *   AllocateRequest allocateRequest = ...
 *   ...
 *   AllocateResponse allocateResponse = rmClient.allocate(allocateRequest);
 *   for (NMToken token : allocateResponse.getNMTokens()) {
 *     nmTokenCache.setToken(token.getNodeId().toString(), token.getToken());
 *   }
 *   ...
 *   ContainerManagementProtocolProxy nmPro = ContainerManagementProtocolProxy(conf, nmTokenCache);
 *   ...
 *   nmPro.startContainer(container, containerContext);
 *   ...
 * </pre>
 * </li>
 * </ul>
 * It is also possible to mix the usage of a client (<code>AMRMClient</code> or
 * <code>NMClient</code>, or the async versions of them) with a protocol proxy (
 * <code>ContainerManagementProtocolProxy</code> or
 * <code>ApplicationMasterProtocol</code>).
 */
public class NMTokenCache {
  private static final NMTokenCache NM_TOKEN_CACHE = new NMTokenCache();
  
  /**
   * Returns the singleton NM token cache.
   *
   * @return the singleton NM token cache.
   */
  public static NMTokenCache getSingleton() {
    return NM_TOKEN_CACHE;
  }
  
  /**
   * Returns NMToken, null if absent. Only the singleton obtained from
   * {@link #getSingleton()} is looked at for the tokens. If you are using your
   * own NMTokenCache that is different from the singleton, use
   * {@link #getToken(String) }
   * 
   * @param nodeAddr
   * @return {@link Token} NMToken required for communicating with node manager
   */
  public static Token getNMToken(String nodeAddr) {
    return NM_TOKEN_CACHE.getToken(nodeAddr);
  }
  
  /**
   * Sets the NMToken for node address only in the singleton obtained from
   * {@link #getSingleton()}. If you are using your own NMTokenCache that is
   * different from the singleton, use {@link #setToken(String, Token) }
   * 
   * @param nodeAddr
   *          node address (host:port)
   * @param token
   *          NMToken
   */
 
  public static void setNMToken(String nodeAddr, Token token) {
    NM_TOKEN_CACHE.setToken(nodeAddr, token);
  }

  private ConcurrentHashMap<String, Token> nmTokens;

  /**
   * Creates a NM token cache instance.
   */
  public NMTokenCache() {
    nmTokens = new ConcurrentHashMap<String, Token>();
  }
  
  /**
   * Returns NMToken, null if absent
   * @param nodeAddr
   * @return {@link Token} NMToken required for communicating with node
   *         manager
   */
  public Token getToken(String nodeAddr) {
    return nmTokens.get(nodeAddr);
  }
  
  /**
   * Sets the NMToken for node address
   * @param nodeAddr node address (host:port)
   * @param token NMToken
   */
 
  public void setToken(String nodeAddr, Token token) {
    nmTokens.put(nodeAddr, token);
  }
  
  /**
   * Returns true if NMToken is present in cache.
   */
 
  public boolean containsToken(String nodeAddr) {
    return nmTokens.containsKey(nodeAddr);
  }
  
  /**
   * Returns the number of NMTokens present in cache.
   */
 
  public int numberOfTokensInCache() {
    return nmTokens.size();
  }
  
  /**
   * Removes NMToken for specified node manager
   * @param nodeAddr node address (host:port)
   */
 
  public void removeToken(String nodeAddr) {
    nmTokens.remove(nodeAddr);
  }
  
  /**
   * It will remove all the nm tokens from its cache
   */
 
  public void clearCache() {
    nmTokens.clear();
  }
}
