package org.jumbune.web.services;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.beans.cluster.HadoopUsers;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.FileUtil;
import org.jumbune.remoting.common.StringUtil;
import org.jumbune.utils.conf.AdminConfigurationUtil;
import org.jumbune.utils.conf.beans.InfluxDBConf;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.Gson;
import org.jumbune.common.beans.cluster.EnterpriseCluster;
import org.jumbune.common.beans.cluster.EnterpriseClusterDefinition;
import org.jumbune.common.influxdb.InfluxDBUtil;
import org.jumbune.common.utils.JMXUtility;
import org.jumbune.common.utils.JobRequestUtil;
import org.jumbune.web.utils.WebConstants;


/**
 * The Class ClusterOperationsService. This class exposes APIs for performing
 * CRUD operations on cluster. It serves all the CRUD requests starting with
 * path /apis/cluster (i.e. the class is bound to the context path
 * /apis/cluster)
 */
@Path(WebConstants.CLUSTER_SERVICE_URL)
public class ClusterOperationsService{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(ClusterOperationsService.class);

	private static final String SIGNATUREFAILURE = "Verify Signature on license key....[FAILED]";

	/**
	 * Persist cluster.
	 *
	 * @param cluster the cluster
	 * @return the response
	 * @throws Exception 
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.TEXT_PLAIN)
	public Response persistCluster(EnterpriseClusterDefinition cluster) throws Exception {
    	LOGGER.debug("Cluster(deserialized based on info from UI): " + cluster);
    	//Appending '/' at the end of worker directory if not present
    	String workDirectory = cluster.getWorkers().getWorkDirectory();
		if (!(workDirectory.endsWith(File.separator))) {
			workDirectory = workDirectory + File.separator;
			cluster.getWorkers().setWorkDirectory(workDirectory);
		}
		
		String password = cluster.getAgents().getPassword();
		if (password != null && !password.isEmpty()) {
			try {
			cluster.getAgents().setPassword(StringUtil.getEncrypted(password));
			} catch (Exception e) {
				LOGGER.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			}
		}
    	saveClusterToJsonFile(cluster);
    	if(cluster.isJmxPluginEnabled()){
    		new JMXUtility().sendJmxAgentToAllDaemons(cluster);    		
    	}
    	
    	String clusterName = cluster.getClusterName();
    	AdminConfigurationService.checkAndCreateConfAndInfluxDatabase(clusterName);
    	AdminConfigurationService.enableWorkersNodeUpdater(cluster);
    	return Response.ok("Cluster persisted SUCCESSFULLY").build();
	}

	/**
	 * Gets the cluster by name.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the cluster by name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/{clusterName}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getClusterByName(@PathParam("clusterName") final String clusterName)
			throws IOException {
		
		String response;
		try{
           response = new Gson().toJson(JobRequestUtil.getClusterByName(clusterName));
		} catch (Exception e) {
			response = "No cluster with name " + clusterName + " exists. Please provide a valid cluster name";
		}
		return Response.ok(response).build();
		
	}
	
	@GET
	@Path("/total-cluster-nodes-added")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTotalClusterNodesAdded() {
		try {
			int totalNodesAdded = 0;
			List<String> list = getAllClusterNames();
			for (String clusterName : list) {
				Cluster cluster = JobRequestUtil.getClusterByName(clusterName);
				totalNodesAdded += cluster.getWorkers().getHosts().size();
			}
			return Response.ok(totalNodesAdded).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	private List<String> getAllClusterNames() {
		List<String> clusters = new ArrayList<String>();
		File clusterJsonFile = new File(getClusterJsonDir());
		for (File file : clusterJsonFile.listFiles()) {
			if(file.getName().endsWith(WebConstants.JSON_EXTENSION)){
				clusters.add(file.getName().replace(WebConstants.JSON_EXTENSION, ""));				
			}
		}
		return clusters;
	}

	/**
	 * Update cluster.
	 *
	 * @param modifiedCluster the cluster
	 * @return true, if successful
	 * @throws Exception 
	 */
	@PUT
	@Path("/{clusterName}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateCluster(final EnterpriseClusterDefinition modifiedCluster)
			throws Exception {
		boolean updated = false;
		String clusterName = modifiedCluster.getClusterName();
		EnterpriseCluster oldCluster = JobRequestUtil.getClusterByName(clusterName);
		String newPassword = modifiedCluster.getAgents().getPassword();
		String oldPassword = oldCluster.getAgents().getPassword();
		if ( newPassword!= null && (oldPassword == null || !newPassword.equals(oldPassword))) {
			modifiedCluster.getAgents().setPassword(StringUtil.getEncrypted(newPassword));
		}
		
		//Appending '/' at the end of worker directory if not present
    	String workDirectory = modifiedCluster.getWorkers().getWorkDirectory();
		if (!(workDirectory.endsWith(File.separator))) {
			workDirectory = workDirectory + File.separator;
			modifiedCluster.getWorkers().setWorkDirectory(workDirectory);
		}
		
		updated = deleteClusterJsonFile(clusterName);
		updated = saveClusterToJsonFile(modifiedCluster);
		ClusterAnalysisService.updateClusterCache(clusterName, modifiedCluster);
		if(modifiedCluster.isJmxPluginEnabled()){
			new JMXUtility().sendJmxAgentToAllDaemons(modifiedCluster);			
		}
		AdminConfigurationService.checkAndCreateConfAndInfluxDatabase(clusterName);
		AdminConfigurationService.enableWorkersNodeUpdater(modifiedCluster);
		return Response.ok(new Gson().toJson(updated)).build();
	}

	/**
	 * Delete cluster.
	 *
	 * @param clusterName, the cluster name
	 * @return true, if successful
	 * @throws Exception 
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{clusterName}")
	public Response deleteCluster(@PathParam("clusterName") final String clusterName) {
		InfluxDBConf influxDBConf = null;
		try {
			influxDBConf = AdminConfigurationUtil.getInfluxdbConfiguration(clusterName);
		} catch (Exception e) {
			LOGGER.error("Unable to get influxdb configuration of cluster [" + clusterName + "]. " + e.getMessage());
		}
		try {	
			if (influxDBConf != null) {
				InfluxDBUtil.dropDatabase(influxDBConf);
			}
		} catch(Exception e) {
			if (influxDBConf != null) {
				LOGGER.warn("It seems that influx DB is not installed. Not dropping database [" + influxDBConf.getDatabase() + "]");
			} else {
				LOGGER.warn("It seems that influx DB is not installed. Not dropping database");
			}
		}
		try {
			AdminConfigurationUtil.deleteConfigurations(clusterName);
		} catch (Exception e) {
			LOGGER.error("Unable to remove configurations of cluster [" + clusterName + "]. " + e.getMessage());
		}
		try {
			deleteClusterJsonFile(clusterName);
			return Response.ok(true).build();
		} catch (IOException e) {
			LOGGER.error("Unable to delete cluster json file for [" + clusterName + "]. " + e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Save cluster to json file.
	 *
	 * @param cluster
	 *            the cluster
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private boolean saveClusterToJsonFile(final EnterpriseClusterDefinition cluster) throws IOException {
		HadoopUsers hadoopUsers = new HadoopUsers();
		hadoopUsers.setFsUser(cluster.getAgents().getUser());
		hadoopUsers.setFsPrivateKeyPath(cluster.getAgents().getSshAuthKeysFile());
		hadoopUsers.setFsUserPassword(cluster.getAgents().getPassword());
		hadoopUsers.setHasSingleUser(cluster.getHadoopUsers().isHasSingleUser());
		cluster.setHadoopUsers(hadoopUsers);
		String jsonFilePath = getClusterJsonDir() + File.separator + cluster.getClusterName() + WebConstants.JSON_EXTENSION;
		PrintWriter out = new PrintWriter(jsonFilePath);
		Gson gson = new Gson();
		out.print(gson.toJson(cluster));
		out.flush();
		out.close();
		LOGGER.debug("Cluster "+ cluster.getClusterName() + " persisted successfully");
		return true;
	}

	/**
	 * Delete cluster json file.
	 *
	 * @param clusterName the cluster name
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private boolean deleteClusterJsonFile(final String clusterName)
			throws IOException {
		return Files.deleteIfExists(Paths.get(getClusterJsonDir()
				+ File.separator + clusterName + WebConstants.JSON_EXTENSION));
	}
	
	/**
	 * Gets the cluster json dir.
	 *
	 * @return the cluster json dir
	 */
	private String getClusterJsonDir() {
		String clusterJsonDir = System.getenv(WebConstants.JUMBUNE_HOME)
				+ WebConstants.CLUSTER_DIR;
		File jsonDirectory = new File(clusterJsonDir);
		if (!jsonDirectory.exists()) {
			jsonDirectory.mkdir();
		}
		return jsonDirectory.getAbsolutePath();
	}
	
	
	/**
	 * Gets the hadoop type by cluster name.
	 *
	 * @param clusterName
	 *            the cluster name
	 * @return the cluster by name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@GET
	@Path(WebConstants.HADOOP_TYPE)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getHadoopType()
			throws IOException {
		Gson gson = new Gson();
		Map<String, String> hadoopTypeMap  = new HashMap<String, String>();
		hadoopTypeMap.put("hadoopType", FileUtil.getClusterInfoDetail(Constants.HADOOP_TYPE));
		String hadoopType = gson.toJson(hadoopTypeMap);
		return Response.ok(hadoopType).build();
	
	}
	
}
