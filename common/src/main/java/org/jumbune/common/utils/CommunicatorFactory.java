package org.jumbune.common.utils;

import java.net.InetSocketAddress;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.v2.api.HSClientProtocol;
import org.apache.hadoop.mapreduce.v2.api.MRClientProtocol;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.JumbuneInfo;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.utils.yarn.communicators.MRCommunicator;
import org.jumbune.utils.yarn.communicators.RMCommunicator;

/**
 *
 */
public class CommunicatorFactory {
	
	private static volatile CommunicatorFactory instance = null;
	
	private final static Logger LOGGER = LogManager.getLogger(CommunicatorFactory.class);

	public static CommunicatorFactory getInstance() {
		if (instance == null) {
			synchronized (CommunicatorFactory.class) {
				if (instance == null) {
					instance = new CommunicatorFactory();
				}
			}
		}
		return instance;
	}
	
	private CommunicatorFactory() {}
	
	public RMCommunicator createRMCommunicator(final Cluster cluster) throws Exception {
		LOGGER.debug("Creating RMCommunicator for cluster [ "
				+ cluster.getClusterName() + " ], RMAddress [ " + cluster.getRMSocketAddress() + " ]");
		UserGroupInformation userUGI = UserGroupInformation
				.createRemoteUser(cluster.getHadoopUsers().getFsUser());
		
		final Configuration conf = new Configuration();
		
		ApplicationClientProtocol proxy = userUGI.doAs(
				new PrivilegedExceptionAction<ApplicationClientProtocol>() {
					@Override
					public ApplicationClientProtocol run() throws Exception {

						String hadoopConfDir = RemotingUtil
								.getHadoopConfigurationDirPath(cluster);
						RemotingUtil.addHadoopResource(conf, cluster,
								hadoopConfDir, "core-site.xml");
						RemotingUtil.addHadoopResource(conf, cluster,
								hadoopConfDir, "hdfs-site.xml");
						RemotingUtil.addHadoopResource(conf, cluster,
								hadoopConfDir, "yarn-site.xml");
						RemotingUtil.addHadoopResource(conf, cluster,
								hadoopConfDir, "mapred-site.xml");
						
						YarnRPC rpc = YarnRPC.create(conf);

						InetSocketAddress rmSocketAddress = NetUtils
								.createSocketAddr(cluster.getRMSocketAddress());

						return (ApplicationClientProtocol) rpc.getProxy(
								ApplicationClientProtocol.class,
								rmSocketAddress, conf);
					}
				});

		RMCommunicator rmCommunicator = new RMCommunicator(cluster.getResourceManager(), proxy);
		return rmCommunicator;
	}

	public RMCommunicator createRMCommunicator(final Cluster cluster, Subject subject)
			throws Exception {
		
			return createRMCommunicator(cluster);
	}
	
	public MRCommunicator createMRCommunicator(final Cluster cluster) throws Exception {

		LOGGER.debug("Creating MRCommunicator for Cluster [ "
				+ cluster.getClusterName() + " ], MRAddress [ " + cluster.getMRSocketAddress() + " ]");
		
		final Configuration conf = new Configuration();
		
		UserGroupInformation userUGI = UserGroupInformation
				.createRemoteUser(cluster.getHadoopUsers().getFsUser());

		MRClientProtocol proxy = userUGI
				.doAs(new PrivilegedExceptionAction<MRClientProtocol>() {
					@Override
					public MRClientProtocol run() throws Exception {

						String hadoopConfDir = RemotingUtil
								.getHadoopConfigurationDirPath(cluster);
						RemotingUtil.addHadoopResource(conf, cluster,
								hadoopConfDir, "yarn-site.xml");
						RemotingUtil.addHadoopResource(conf, cluster,
								hadoopConfDir, "mapred-site.xml");
						YarnRPC rpc = YarnRPC.create(conf);

						InetSocketAddress rmSocketAddress = NetUtils
								.createSocketAddr(cluster.getMRSocketAddress());

						return (MRClientProtocol) rpc.getProxy(
								HSClientProtocol.class, rmSocketAddress, conf);
					}
				});

		MRCommunicator mrCommunicator = new MRCommunicator(proxy);
		return mrCommunicator;
	}

	public MRCommunicator createMRCommunicator(final Cluster cluster, Subject subject)
			throws Exception {
			return createMRCommunicator(cluster);		
	}


}
