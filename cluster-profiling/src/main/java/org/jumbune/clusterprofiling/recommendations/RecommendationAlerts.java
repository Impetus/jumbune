package org.jumbune.clusterprofiling.recommendations;

import java.io.IOException;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.hadoop.yarn.exceptions.YarnException;
import org.jumbune.clusterprofiling.SchedulerService;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.utils.yarn.communicators.RMCommunicator;

public interface RecommendationAlerts {
	/**
	 * Check memory configuration.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the list
	 * @throws YarnException
	 *             the yarn exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	Set<Recommendations> checkMemoryConfiguration(Cluster cluster);

	/**
	 * Check yarn property.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the list
	 */
	Set<Recommendations> checkYarnProperty(Cluster cluster);

	/**
	 * Gets the recommended container configuration.
	 *
	 * @param cluster
	 *            the cluster
	 * @return the recommended container configuration
	 */
	Set<Recommendations> getRecommendedContainerConfiguration(Cluster cluster);
	
	
	/**
	 * Check transparent huge page status.
	 *
	 * @param cluster the cluster
	 * @return the sets the
	 */
	Set<Recommendations> checkTransparentHugePageStatus(Cluster cluster);	
	
	/**
	 * Check vm swappiness param.
	 *
	 * @param cluster the cluster
	 * @return the sets the
	 */
	Set<Recommendations> checkVMSwappinessParam(Cluster cluster);
	
	/**
	 * Check se linux status.
	 *
	 * @param cluster the cluster
	 * @return the sets the
	 */
	Set<Recommendations> checkSELinuxStatus(Cluster cluster);
	
	
	/**
	 * Gets the spark configurations recommendations.
	 *
	 * @param cluster the cluster
	 * @return the sets the
	 */
	Set<Recommendations> getSparkConfigurations(Cluster cluster,boolean fairSchedulerFlag, SchedulerService schedulerService, RMCommunicator rmCommunicator);
	
}