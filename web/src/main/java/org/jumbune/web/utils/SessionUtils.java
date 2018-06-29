package org.jumbune.web.utils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.utils.CommunicatorFactory;
import org.jumbune.utils.yarn.communicators.MRCommunicator;
import org.jumbune.utils.yarn.communicators.RMCommunicator;

public class SessionUtils {
	
	/**
	 * Helpful in case of High Availability, Map key = cluster name and Map value =
	 * history server's socket address
	 * 
	 * It saves the socket address that was fetched last time. If current
	 * fetched socket address and last time fetched (historyservers) socket
	 * address are different then it means we have to again create
	 * MRCommunicator object.
	 */
	private static Map<String, String> historyservers = new HashMap<String, String>(2);
	
	private CommunicatorFactory factory;
	
	private static volatile SessionUtils instance = null;

	public static SessionUtils getInstance() {
		if (instance == null) {
			synchronized (SessionUtils.class) {
				if (instance == null) {
					instance = new SessionUtils();
				}
			}
		}
		return instance;
	}
	
	private SessionUtils() {
		factory = CommunicatorFactory.getInstance();
	}

	/**
	 * @param cluster
	 * @param session
	 * @return RMCommunicator
	 * @throws Exception
	 */
	public RMCommunicator getRM(final Cluster cluster, HttpSession session)
			throws Exception {

		@SuppressWarnings("unchecked")
		Map<String, RMCommunicator> rmCommunicators = (Map<String, RMCommunicator>) session
				.getAttribute(WebConstants.SESSION_RM_COMMUNICATORS);

		if (rmCommunicators == null) {
			rmCommunicators = new HashMap<>(2);
			session.setAttribute(WebConstants.SESSION_RM_COMMUNICATORS, rmCommunicators);
		}

		RMCommunicator rmc = rmCommunicators.get(cluster.getClusterName());
		if (rmc == null || !rmc.getNodeIP().equals(cluster.getResourceManager())) {
			rmc = factory.createRMCommunicator(cluster);
			rmCommunicators.put(cluster.getClusterName(), rmc);
		}
		return rmc;
	}

	/**
	 * @param cluster
	 * @param session
	 * @return MRCommunicator
	 * @throws Exception
	 */
	public MRCommunicator getMR(final Cluster cluster, HttpSession session)
			throws Exception {

		@SuppressWarnings("unchecked")
		Map<String, MRCommunicator> mrCommunicators = (Map<String, MRCommunicator>) session
				.getAttribute(WebConstants.SESSION_MR_COMMUNICATORS);

		if (mrCommunicators == null) {
			mrCommunicators = new HashMap<>(2);
			session.setAttribute(WebConstants.SESSION_MR_COMMUNICATORS,
					mrCommunicators);
		}

		if (historyservers.get(cluster.getClusterName()) == null
				|| !historyservers.get(cluster.getClusterName())
						.equals(cluster.getMRSocketAddress())
				|| mrCommunicators.get(cluster.getClusterName()) == null) {

			historyservers.put(cluster.getClusterName(),
					cluster.getMRSocketAddress());
			MRCommunicator mrc = factory.createMRCommunicator(cluster);
			mrCommunicators.put(cluster.getClusterName(), mrc);
			return mrc;
		} else {
			return mrCommunicators.get(cluster.getClusterName());
		}

	}

}
