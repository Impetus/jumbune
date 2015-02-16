package org.jumbune.yarn.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.service.AbstractService;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NMToken;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceBlacklistRequest;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.util.Records;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationMasterClient<T extends ContainerRequest> extends AbstractService implements ApplicationMasterContract<T>{
	
	public ApplicationMasterClient() {
		this(ApplicationMasterClient.class.getName());
	}

	
	public ApplicationMasterClient(String name) {
		super(name);
	}

	public static final Logger LOGGER = LogManager.getLogger(ApplicationMasterClient.class);
	
	  private NMTokenCache nmTokenCache;
	  
	  private static final List<String> ANY_LIST =
		      Collections.singletonList(ResourceRequest.ANY);	  

	private int lastResponseId = 0;
	  protected final Set<ContainerId> release = new TreeSet<ContainerId>();
	  protected final Set<ResourceRequest> ask = new TreeSet<ResourceRequest>(
		      new org.apache.hadoop.yarn.api.records.ResourceRequest.ResourceRequestComparator());
	  protected final Set<String> blacklistAdditions = new HashSet<String>();
	  protected final Set<String> blacklistRemovals = new HashSet<String>();
	  protected ApplicationMasterProtocol rmClient;
	  protected Resource clusterAvailableResources;
	  protected int clusterNodeCount;
	  
	  protected final 
	  Map<Priority, Map<String, TreeMap<Resource, ResourceRequestInfo>>>
	    remoteRequestsTable =
	    new TreeMap<Priority, Map<String, TreeMap<Resource, ResourceRequestInfo>>>();
	  
	@Override
	public Configuration getConfig(){
		return new YarnConfiguration();
	}

	  protected void serviceInit(Configuration conf) throws Exception {
		    RackResolver.init(conf);
		    super.serviceInit(conf);
		  }
	  
	  protected void serviceStart() throws Exception {
		    final YarnConfiguration conf = new YarnConfiguration(getConfig());
		    try {
		      this.rmClient =
		          ClientRMProxy.createRMProxy(conf, ApplicationMasterProtocol.class);
		    } catch (IOException e) {
		      throw new YarnRuntimeException(e);
		    }
		    super.serviceStart();
		  }
	
	  class ResourceRequestInfo {
		    ResourceRequest remoteRequest;
		    LinkedHashSet<T> containerRequests;
		    
		    ResourceRequestInfo(Priority priority, String resourceName,
		        Resource capability, boolean relaxLocality) {
		      remoteRequest = ResourceRequest.newInstance(priority, resourceName,
		          capability, 0);
		      remoteRequest.setRelaxLocality(relaxLocality);
		      containerRequests = new LinkedHashSet<T>();
		    }
		  }
	
	
	  /**
	   * Class compares Resource by memory then cpu in reverse order
	   */
	  class ResourceReverseMemoryThenCpuComparator implements Comparator<Resource> {
	    @Override
	    public int compare(Resource arg0, Resource arg1) {
	      int mem0 = arg0.getMemory();
	      int mem1 = arg1.getMemory();
	      int cpu0 = arg0.getVirtualCores();
	      int cpu1 = arg1.getVirtualCores();
	      if(mem0 == mem1) {
	        if(cpu0 == cpu1) {
	          return 0;
	        }
	        if(cpu0 < cpu1) {
	          return 1;
	        }
	        return -1;
	      }
	      if(mem0 < mem1) { 
	        return 1;
	      }
	      return -1;
	    }    
	  }	
	  
	  public void setNMTokenCache(NMTokenCache nmTokenCache) {
		    this.nmTokenCache = nmTokenCache;
		  }
	  
	  public NMTokenCache getNMTokenCache() {
		    return nmTokenCache;
		  }
	  
	  protected void populateNMTokens(List<NMToken> nmTokens) {
		    for (NMToken token : nmTokens) {
		      String nodeId = token.getNodeId().toString();
		      if (getNMTokenCache().containsToken(nodeId)) {
		        LOGGER.info("Replacing token for : " + nodeId);
		      } else {
		        LOGGER.info("Received new token for : " + nodeId);
		      }
		      getNMTokenCache().setToken(nodeId, token.getToken());
		    }
		  }
	  

	@Override
	public RegisterApplicationMasterResponse registerApplicationMaster(
			String appHostName, int appHostPort, String appTrackingUrl)
			throws YarnException, IOException {
	    RegisterApplicationMasterRequest request =
	            RegisterApplicationMasterRequest.newInstance(appHostName, appHostPort,
	              appTrackingUrl);
	        RegisterApplicationMasterResponse response =
	            rmClient.registerApplicationMaster(request);

	        synchronized (this) {
	          if(!response.getNMTokensFromPreviousAttempts().isEmpty()) {
	            populateNMTokens(response.getNMTokensFromPreviousAttempts());
	          }
	        }
	        return response;
	}

	@Override
	public AllocateResponse allocate(float progressIndicator)
			throws YarnException, IOException {
	    AllocateResponse allocateResponse = null;
	    List<ResourceRequest> askList = null;
	    List<ContainerId> releaseList = null;
	    AllocateRequest allocateRequest = null;
	    List<String> blacklistToAdd = new ArrayList<String>();
	    List<String> blacklistToRemove = new ArrayList<String>();
	    
	    try {
	      synchronized (this) {
	        askList = new ArrayList<ResourceRequest>(ask.size());
	        for(ResourceRequest r : ask) {
	          // create a copy of ResourceRequest as we might change it while the 
	          // RPC layer is using it to send info across
	          askList.add(ResourceRequest.newInstance(r.getPriority(),
	              r.getResourceName(), r.getCapability(), r.getNumContainers(),
	              r.getRelaxLocality()));
	        }
	        releaseList = new ArrayList<ContainerId>(release);
	        // optimistically clear this collection assuming no RPC failure
	        ask.clear();
	        release.clear();

	        blacklistToAdd.addAll(blacklistAdditions);
	        blacklistToRemove.addAll(blacklistRemovals);
	        
	        ResourceBlacklistRequest blacklistRequest = 
	            (blacklistToAdd != null) || (blacklistToRemove != null) ? 
	            ResourceBlacklistRequest.newInstance(blacklistToAdd,
	                blacklistToRemove) : null;
	        
	        allocateRequest =
	            AllocateRequest.newInstance(lastResponseId, progressIndicator,
	              askList, releaseList, blacklistRequest);
	        // clear blacklistAdditions and blacklistRemovals before 
	        // unsynchronized part
	        blacklistAdditions.clear();
	        blacklistRemovals.clear();
	      }

	      allocateResponse = rmClient.allocate(allocateRequest);

	      synchronized (this) {
	        // update these on successful RPC
	        clusterNodeCount = allocateResponse.getNumClusterNodes();
	        lastResponseId = allocateResponse.getResponseId();
	        clusterAvailableResources = allocateResponse.getAvailableResources();
	        if (!allocateResponse.getNMTokens().isEmpty()) {
	          populateNMTokens(allocateResponse.getNMTokens());
	        }
	      }
	    } finally {
	      // TODO how to differentiate remote yarn exception vs error in rpc
	      if(allocateResponse == null) {
	        // we hit an exception in allocate()
	        // preserve ask and release for next call to allocate()
	        synchronized (this) {
	          release.addAll(releaseList);
	          // requests could have been added or deleted during call to allocate
	          // If requests were added/removed then there is nothing to do since
	          // the ResourceRequest object in ask would have the actual new value.
	          // If ask does not have this ResourceRequest then it was unchanged and
	          // so we can add the value back safely.
	          // This assumes that there will no concurrent calls to allocate() and
	          // so we dont have to worry about ask being changed in the
	          // synchronized block at the beginning of this method.
	          for(ResourceRequest oldAsk : askList) {
	            if(!ask.contains(oldAsk)) {
	              ask.add(oldAsk);
	            }
	          }
	          
	          blacklistAdditions.addAll(blacklistToAdd);
	          blacklistRemovals.addAll(blacklistToRemove);
	        }
	      }
	    }
	    return allocateResponse;
	    }

	@Override
	public void unregisterApplicationMaster(FinalApplicationStatus appStatus,
			String appMessage, String appTrackingUrl) throws YarnException,
			IOException {
	    FinishApplicationMasterRequest request =
	            FinishApplicationMasterRequest.newInstance(appStatus, appMessage,
	              appTrackingUrl);
	        try {
	          while (true) {
	            FinishApplicationMasterResponse response =
	                rmClient.finishApplicationMaster(request);
	            if (response.getIsUnregistered()) {
	              break;
	            }
	            LOGGER.info("Waiting for application to be successfully unregistered.");
	            Thread.sleep(100);
	          }
	        } catch (InterruptedException e) {
	          LOGGER.info("Interrupted while waiting for application"
	              + " to be removed from RMStateStore");
	        }
	        }

	@Override
	public void addContainerRequest(T req) {
	    Set<String> dedupedRacks = new HashSet<String>();
	    if (req.getRacks() != null) {
	      dedupedRacks.addAll(req.getRacks());
	      if(req.getRacks().size() != dedupedRacks.size()) {
	        LOGGER.warn("ContainerRequest has duplicate racks: " +req.getRacks());
	      }
	    }
	    Set<String> inferredRacks = resolveRacks(req.getNodes());
	    inferredRacks.removeAll(dedupedRacks);

	    // check that specific and non-specific requests cannot be mixed within a
	    // priority
	    checkLocalityRelaxationConflict(req.getPriority(), ANY_LIST,
	        req.getRelaxLocality());
	    // check that specific rack cannot be mixed with specific node within a 
	    // priority. If node and its rack are both specified then they must be 
	    // in the same request.
	    // For explicitly requested racks, we set locality relaxation to true
	    checkLocalityRelaxationConflict(req.getPriority(), dedupedRacks, true);
	    checkLocalityRelaxationConflict(req.getPriority(), inferredRacks,
	        req.getRelaxLocality());

	    if (req.getNodes() != null) {
	      HashSet<String> dedupedNodes = new HashSet<String>(req.getNodes());
	      if(dedupedNodes.size() != req.getNodes().size()) {
	        LOGGER.warn("ContainerRequest has duplicate nodes: "
	            + req.getNodes());        
	      }
	      for (String node : dedupedNodes) {
	        addResourceRequest(req.getPriority(), node, req.getCapability(), req,
	            true);
	      }
	    }

	    for (String rack : dedupedRacks) {
	      addResourceRequest(req.getPriority(), rack, req.getCapability(), req,
	          true);
	    }

	    // Ensure node requests are accompanied by requests for
	    // corresponding rack
	    for (String rack : inferredRacks) {
	      addResourceRequest(req.getPriority(), rack, req.getCapability(), req,
	          req.getRelaxLocality());
	    }

	    // Off-switch
	    addResourceRequest(req.getPriority(), ResourceRequest.ANY, 
	                    req.getCapability(), req, req.getRelaxLocality());
	  	      
	}

	@Override
	public void removeContainerRequest(T req) {
	    Set<String> allRacks = new HashSet<String>();
	    if (req.getRacks() != null) {
	      allRacks.addAll(req.getRacks());
	    }
	    allRacks.addAll(resolveRacks(req.getNodes()));

	    // Update resource requests
	    if (req.getNodes() != null) {
	      for (String node : new HashSet<String>(req.getNodes())) {
	        decResourceRequest(req.getPriority(), node, req.getCapability(), req);
	      }
	    }

	    for (String rack : allRacks) {
	      decResourceRequest(req.getPriority(), rack, req.getCapability(), req);
	    }

	    decResourceRequest(req.getPriority(), ResourceRequest.ANY,
	        req.getCapability(), req);
	}

	@Override
	public void releaseAssignedContainer(ContainerId containerId) {
	    release.add(containerId);
	    }

	@Override
	public Resource getAvailableResources() {
		 return clusterAvailableResources;
		 }

	@Override
	public int getClusterNodeCount() {
		return clusterNodeCount;
		}

	@Override
	public List<? extends Collection<T>> getMatchingRequests(Priority priority,
			String resourceName, Resource capability) {
	    List<LinkedHashSet<T>> list = new LinkedList<LinkedHashSet<T>>();
	    Map<String, TreeMap<Resource, ResourceRequestInfo>> remoteRequests = 
	        this.remoteRequestsTable.get(priority);
	    if (remoteRequests == null) {
	      return list;
	    }
	    TreeMap<Resource, ResourceRequestInfo> reqMap = remoteRequests
	        .get(resourceName);
	    if (reqMap == null) {
	      return list;
	    }

	    ResourceRequestInfo resourceRequestInfo = reqMap.get(capability);
	    if (resourceRequestInfo != null &&
	        !resourceRequestInfo.containerRequests.isEmpty()) {
	      list.add(resourceRequestInfo.containerRequests);
	      return list;
	    }
	    
	    // no exact match. Container may be larger than what was requested.
	    // get all resources <= capability. map is reverse sorted. 
	    SortedMap<Resource, ResourceRequestInfo> tailMap = 
	                                                  reqMap.tailMap(capability);
	    for(Map.Entry<Resource, ResourceRequestInfo> entry : tailMap.entrySet()) {
	      if (canFit(entry.getKey(), capability) &&
	          !entry.getValue().containerRequests.isEmpty()) {
	        // match found that fits in the larger resource
	        list.add(entry.getValue().containerRequests);
	      }
	    }
	    
	    // no match found
	    return list;          
	}

	@Override
	public void updateBlacklist(List<String> blacklistAdditions,
			List<String> blacklistRemovals) {
	    if (blacklistAdditions != null) {
	        this.blacklistAdditions.addAll(blacklistAdditions);
	        // if some resources are also in blacklistRemovals updated before, we 
	        // should remove them here.
	        this.blacklistRemovals.removeAll(blacklistAdditions);
	      }
	      
	      if (blacklistRemovals != null) {
	        this.blacklistRemovals.addAll(blacklistRemovals);
	        // if some resources are in blacklistAdditions before, we should remove
	        // them here.
	        this.blacklistAdditions.removeAll(blacklistRemovals);
	      }
	      
	      if (blacklistAdditions != null && blacklistRemovals != null
	          && blacklistAdditions.removeAll(blacklistRemovals)) {
	        // we allow resources to appear in addition list and removal list in the
	        // same invocation of updateBlacklist(), but should get a warn here.
	        LOGGER.warn("The same resources appear in both blacklistAdditions and " +
	            "blacklistRemovals in updateBlacklist.");
	      }
	    }
	 
	  private Set<String> resolveRacks(List<String> nodes) {
		    Set<String> racks = new HashSet<String>();    
		    if (nodes != null) {
		      for (String node : nodes) {
		        // Ensure node requests are accompanied by requests for
		        // corresponding rack
		        String rack = RackResolver.resolve(node).getNetworkLocation();
		        if (rack == null) {
		          LOGGER.warn("Failed to resolve rack for node " + node + ".");
		        } else {
		          racks.add(rack);
		        }
		      }
		    }
		    
		    return racks;
		  }
	
	  private void addResourceRequest(Priority priority, String resourceName,
		      Resource capability, T req, boolean relaxLocality) {
		    Map<String, TreeMap<Resource, ResourceRequestInfo>> remoteRequests =
		      this.remoteRequestsTable.get(priority);
		    if (remoteRequests == null) {
		      remoteRequests = 
		          new HashMap<String, TreeMap<Resource, ResourceRequestInfo>>();
		      this.remoteRequestsTable.put(priority, remoteRequests);
		      if (LOGGER.isDebugEnabled()) {
		        LOGGER.debug("Added priority=" + priority);
		      }
		    }
		    TreeMap<Resource, ResourceRequestInfo> reqMap = 
		                                          remoteRequests.get(resourceName);
		    if (reqMap == null) {
		      // capabilities are stored in reverse sorted order. smallest last.
		      reqMap = new TreeMap<Resource, ResourceRequestInfo>(
		          new ResourceReverseMemoryThenCpuComparator());
		      remoteRequests.put(resourceName, reqMap);
		    }
		    ResourceRequestInfo resourceRequestInfo = reqMap.get(capability);
		    if (resourceRequestInfo == null) {
		      resourceRequestInfo =
		          new ResourceRequestInfo(priority, resourceName, capability,
		              relaxLocality);
		      reqMap.put(capability, resourceRequestInfo);
		    }
		    
		    resourceRequestInfo.remoteRequest.setNumContainers(
		         resourceRequestInfo.remoteRequest.getNumContainers() + 1);

		    if (relaxLocality) {
		      resourceRequestInfo.containerRequests.add(req);
		    }

		    // Note this down for next interaction with ResourceManager
		    addResourceRequestToAsk(resourceRequestInfo.remoteRequest);

		    if (LOGGER.isDebugEnabled()) {
		      LOGGER.debug("addResourceRequest:" + " applicationId="
		          + " priority=" + priority.getPriority()
		          + " resourceName=" + resourceName + " numContainers="
		          + resourceRequestInfo.remoteRequest.getNumContainers() 
		          + " #asks=" + ask.size());
		    }
		  }

		  private void decResourceRequest(Priority priority, 
		                                   String resourceName,
		                                   Resource capability, 
		                                   T req) {
		    Map<String, TreeMap<Resource, ResourceRequestInfo>> remoteRequests =
		      this.remoteRequestsTable.get(priority);
		    
		    if(remoteRequests == null) {
		      if (LOGGER.isDebugEnabled()) {
		        LOGGER.debug("Not decrementing resource as priority " + priority 
		            + " is not present in request table");
		      }
		      return;
		    }
		    
		    Map<Resource, ResourceRequestInfo> reqMap = remoteRequests.get(resourceName);
		    if (reqMap == null) {
		      if (LOGGER.isDebugEnabled()) {
		        LOGGER.debug("Not decrementing resource as " + resourceName
		            + " is not present in request table");
		      }
		      return;
		    }
		    ResourceRequestInfo resourceRequestInfo = reqMap.get(capability);

		    if (LOGGER.isDebugEnabled()) {
		      LOGGER.debug("BEFORE decResourceRequest:" + " applicationId="
		          + " priority=" + priority.getPriority()
		          + " resourceName=" + resourceName + " numContainers="
		          + resourceRequestInfo.remoteRequest.getNumContainers() 
		          + " #asks=" + ask.size());
		    }

		    resourceRequestInfo.remoteRequest.setNumContainers(
		        resourceRequestInfo.remoteRequest.getNumContainers() - 1);

		    resourceRequestInfo.containerRequests.remove(req);
		    
		    if(resourceRequestInfo.remoteRequest.getNumContainers() < 0) {
		      // guard against spurious removals
		      resourceRequestInfo.remoteRequest.setNumContainers(0);
		    }
		    // send the ResourceRequest to RM even if is 0 because it needs to override
		    // a previously sent value. If ResourceRequest was not sent previously then
		    // sending 0 aught to be a no-op on RM
		    addResourceRequestToAsk(resourceRequestInfo.remoteRequest);

		    // delete entries from map if no longer needed
		    if (resourceRequestInfo.remoteRequest.getNumContainers() == 0) {
		      reqMap.remove(capability);
		      if (reqMap.size() == 0) {
		        remoteRequests.remove(resourceName);
		      }
		      if (remoteRequests.size() == 0) {
		        remoteRequestsTable.remove(priority);
		      }
		    }

		    if (LOGGER.isInfoEnabled()) {
		      LOGGER.debug("AFTER decResourceRequest:" + " applicationId="
		          + " priority=" + priority.getPriority()
		          + " resourceName=" + resourceName + " numContainers="
		          + resourceRequestInfo.remoteRequest.getNumContainers() 
		          + " #asks=" + ask.size());
		    }
		  }
		  
		  private void checkLocalityRelaxationConflict(Priority priority,
			      Collection<String> locations, boolean relaxLocality) {
			    Map<String, TreeMap<Resource, ResourceRequestInfo>> remoteRequests =
			        this.remoteRequestsTable.get(priority);
			    if (remoteRequests == null) {
			      return;
			    }
			    // Locality relaxation will be set to relaxLocality for all implicitly
			    // requested racks. Make sure that existing rack requests match this.
			    for (String location : locations) {
			        TreeMap<Resource, ResourceRequestInfo> reqs =
			            remoteRequests.get(location);
			        if (reqs != null && !reqs.isEmpty()) {
			          boolean existingRelaxLocality =
			              reqs.values().iterator().next().remoteRequest.getRelaxLocality();
			          if (relaxLocality != existingRelaxLocality) {
			            throw new YarnRuntimeException("Cannot submit a "
			                + "ContainerRequest asking for location " + location
			                + " with locality relaxation " + relaxLocality + " when it has "
			                + "already been requested with locality relaxation " + existingRelaxLocality);
			          }
			        }
			      }
			  }

		  private void addResourceRequestToAsk(ResourceRequest remoteRequest) {
			    // This code looks weird but is needed because of the following scenario.
			    // A ResourceRequest is removed from the remoteRequestTable. A 0 container 
			    // request is added to 'ask' to notify the RM about not needing it any more.
			    // Before the call to allocate, the user now requests more containers. If 
			    // the locations of the 0 size request and the new request are the same
			    // (with the difference being only container count), then the set comparator
			    // will consider both to be the same and not add the new request to ask. So 
			    // we need to check for the "same" request being present and remove it and 
			    // then add it back. The comparator is container count agnostic.
			    // This should happen only rarely but we do need to guard against it.
			    if(ask.contains(remoteRequest)) {
			      ask.remove(remoteRequest);
			    }
			    ask.add(remoteRequest);
			  }
		  
		  static boolean canFit(Resource arg0, Resource arg1) {
			    int mem0 = arg0.getMemory();
			    int mem1 = arg1.getMemory();
			    int cpu0 = arg0.getVirtualCores();
			    int cpu1 = arg1.getVirtualCores();
			    
			    if(mem0 <= mem1 && cpu0 <= cpu1) { 
			      return true;
			    }
			    return false; 
			  }
		  
		  public static void main(String args[]) throws Exception{
			  if(args.length!=4){
				  System.out.println("Usage: Command, number_of_containers, hostname, port");
			  }
			  final String command = args[0];
			  final int n = Integer.valueOf(args[1]);
			  final String appHostName = args[2];
			  final int appHostPort = Integer.valueOf(args[3]);
			  final String appTrackingUrl = "";
			  
			  ApplicationMasterClient applicationMasterClient = new ApplicationMasterClient();
			  applicationMasterClient.serviceInit(applicationMasterClient.getConfig());
			  applicationMasterClient.serviceStart();

			  NMClient nmClient = new NMClient();
			  nmClient.serviceInit(nmClient.getConfig());
			  nmClient.start();
			  
			  // Register with ResourceManager
			  applicationMasterClient.registerApplicationMaster(appHostName, appHostPort, appTrackingUrl);
			  // Priority for worker containers - priorities are intra-application
			  Priority priority = Records.newRecord(Priority.class);
			  priority.setPriority(0);
			  // Resource requirements for worker containers
			  Resource capability = Records.newRecord(Resource.class);
			  capability.setMemory(512);
			  capability.setVirtualCores(1);
			  // Make container requests to ResourceManager
			  for (int i = 0; i < n; ++i) {
			  ContainerRequest containerAsk = new ContainerRequest(capability, null, null, priority, true);
			  System.out.println("Making res-req " + i);
			  applicationMasterClient.addContainerRequest(containerAsk);
			  // Obtain allocated containers, launch and check for responses
			  int responseId = 0;
			  int completedContainers = 0;
			while (completedContainers < n) {
				AllocateResponse response = applicationMasterClient
						.allocate(responseId++);
				for (Container container : response.getAllocatedContainers()) {
					// Launch container by create ContainerLaunchContext
					ContainerLaunchContext ctx = Records
							.newRecord(ContainerLaunchContext.class);
					ctx.setCommands(Collections.singletonList(command));
					System.out.println("Launching container "
							+ container.getId());
					nmClient.startContainer(container, ctx);
				}
				for (ContainerStatus status : response
						.getCompletedContainersStatuses()) {
					++completedContainers;
					System.out.println("Completed container "
							+ status.getContainerId());
				}
				Thread.sleep(100);
			}
			  // Un-register with ResourceManager
			  applicationMasterClient.unregisterApplicationMaster(
			  FinalApplicationStatus.SUCCEEDED, "", "");
			  }			  
			  }
		  }
