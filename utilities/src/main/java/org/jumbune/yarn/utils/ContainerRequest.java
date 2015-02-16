package org.jumbune.yarn.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;

public class ContainerRequest {
    final Resource capability;
    final List<String> nodes;
    final List<String> racks;
    final Priority priority;
    final boolean relaxLocality;
    
    /**
     * Instantiates a {@link ContainerRequest} with the given constraints and
     * locality relaxation enabled.
     * 
     * @param capability
     *          The {@link Resource} to be requested for each container.
     * @param nodes
     *          Any hosts to request that the containers are placed on.
     * @param racks
     *          Any racks to request that the containers are placed on. The
     *          racks corresponding to any hosts requested will be automatically
     *          added to this list.
     * @param priority
     *          The priority at which to request the containers. Higher
     *          priorities have lower numerical values.
     */
    public ContainerRequest(Resource capability, String[] nodes,
        String[] racks, Priority priority) {
      this(capability, nodes, racks, priority, true);
    }
          
    /**
     * Instantiates a {@link ContainerRequest} with the given constraints.
     * 
     * @param capability
     *          The {@link Resource} to be requested for each container.
     * @param nodes
     *          Any hosts to request that the containers are placed on.
     * @param racks
     *          Any racks to request that the containers are placed on. The
     *          racks corresponding to any hosts requested will be automatically
     *          added to this list.
     * @param priority
     *          The priority at which to request the containers. Higher
     *          priorities have lower numerical values.
     * @param relaxLocality
     *          If true, containers for this request may be assigned on hosts
     *          and racks other than the ones explicitly requested.
     */
    public ContainerRequest(Resource capability, String[] nodes,
        String[] racks, Priority priority, boolean relaxLocality) {
      this.capability = capability;
      this.nodes = (nodes != null ? Arrays.asList(nodes) : null);
      this.racks = (racks != null ? Arrays.asList(racks) : null);
      this.priority = priority;
      this.relaxLocality = relaxLocality;
    }
    
    public Resource getCapability() {
      return capability;
    }
    
    public List<String> getNodes() {
      return nodes;
    }
    
    public List<String> getRacks() {
      return racks;
    }
    
    public Priority getPriority() {
      return priority;
    }
    
    public boolean getRelaxLocality() {
      return relaxLocality;
    }
    
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Capability[").append(capability).append("]");
      sb.append("Priority[").append(priority).append("]");
      return sb.toString();
    }
  }
