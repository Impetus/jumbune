package org.jumbune.yarn.utils.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Records;

public class ApplicationMaster {
public static void main(String[] args) throws Exception {
List<String> commandParams = new ArrayList<String>();
commandParams.addAll(Arrays.asList(args));
//final int n = Integer.valueOf(args[1]);
// Initialize clients to ResourceManager and NodeManagers
Configuration conf = new YarnConfiguration();
/*conf.set("yarn.resourcemanager.address", "192.168.49.52:8032");
conf.set("yarn.nodemanager.aux-services", "mapreduce_shuffle");
conf.set("yarn.resourcemanager.scheduler.address", "192.168.49.52:8030");
conf.set("yarn.resourcemanager.resource-tracker.address", "192.168.49.52:8031");
conf.set("yarn.resourcemanager.webapp.address", "192.168.49.52:8088");
conf.set("yarn.resourcemanager.admin.address", "192.168.49.52:8033");
*/AMRMClient<ContainerRequest> rmClient = AMRMClient.createAMRMClient();
rmClient.init(conf);
rmClient.start();
NMClient nmClient = NMClient.createNMClient();
nmClient.init(conf);
nmClient.start();
// Register with ResourceManager
System.out.println("registerApplicationMaster 0");
rmClient.registerApplicationMaster("192.168.49.52", 8032, "");
System.out.println("registerApplicationMaster 1");
// Priority for worker containers - priorities are intra-application
Priority priority = Records.newRecord(Priority.class);
priority.setPriority(0);
// Resource requirements for worker containers
//Resource.newInstance(1024,  2);
Resource capability = Records.newRecord(Resource.class);
capability.setMemory(1024);
capability.setVirtualCores(2);
// Make container requests to ResourceManager
//for (int i = 0; i < n; ++i) {
ContainerRequest containerAsk = new ContainerRequest(capability, null, null, priority);
//System.out.println("Making res-req " + i);
rmClient.addContainerRequest(containerAsk);
//}
// Obtain allocated containers, launch and check for responses
int responseId = 0;
int completedContainers = 0;
while (completedContainers < 1) {
AllocateResponse response = rmClient.allocate(responseId++);
for (Container container : response.getAllocatedContainers()) {
// Launch container by create ContainerLaunchContext
ContainerLaunchContext ctx =
Records.newRecord(ContainerLaunchContext.class);
ctx.setCommands(commandParams);
/*ctx.setCommands(
Collections.singletonList(
command +
" 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
" 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
));*/
System.out.println("Launching container " + container.getId());
nmClient.startContainer(container, ctx);
}
for (ContainerStatus status : response.getCompletedContainersStatuses()) {
++completedContainers;
System.out.println("Completed container " + status.getContainerId());
}
Thread.sleep(100);
}
// Un-register with ResourceManager
rmClient.unregisterApplicationMaster(
FinalApplicationStatus.SUCCEEDED, "", "");
}
}