package org.jumbune.common.alerts;

import java.io.IOException;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.jumbune.common.beans.cluster.Cluster;

import org.jumbune.common.beans.AlertInfo;

public interface HAAlert {

	List<AlertInfo> getNameNodeStatus(Cluster cluster) throws AttributeNotFoundException, InstanceNotFoundException, IntrospectionException, MBeanException, ReflectionException, IOException;
	
}
