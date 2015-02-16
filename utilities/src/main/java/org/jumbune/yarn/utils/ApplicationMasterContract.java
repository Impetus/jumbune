package org.jumbune.yarn.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.exceptions.YarnException;

public interface ApplicationMasterContract<T extends ContainerRequest> {

	RegisterApplicationMasterResponse 
    registerApplicationMaster(String appHostName,
                              int appHostPort,
                              String appTrackingUrl) 
    throws YarnException, IOException;
	
	AllocateResponse allocate(float progressIndicator) 
            throws YarnException, IOException;
	
	void unregisterApplicationMaster(FinalApplicationStatus appStatus,
            String appMessage,
            String appTrackingUrl) 
throws YarnException, IOException;
	
	void addContainerRequest(T req);
	
	void removeContainerRequest(T req);
	
	void releaseAssignedContainer(ContainerId containerId);
	
	Resource getAvailableResources();
	
	int getClusterNodeCount();
	
	List<? extends Collection<T>> getMatchingRequests(
            Priority priority, 
            String resourceName, 
            Resource capability);
	
	void updateBlacklist(List<String> blacklistAdditions,
		      List<String> blacklistRemovals);

}
