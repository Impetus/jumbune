package org.jumbune.common.beans.cluster;

import java.util.Set;

import org.jumbune.common.beans.cluster.Cluster;

public interface EnterpriseCluster extends Cluster {

	Set<ZK> getZks();
}
