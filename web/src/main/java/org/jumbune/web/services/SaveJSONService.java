package org.jumbune.web.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jumbune.common.job.JobConfig;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;


/**
 * For allowing the user to save the populated json form.
 */
@Path(WebConstants.SAVE_JSON_SERVICE_URL)
public class SaveJSONService {

    private static final Logger LOGGER = LogManager.getLogger(SaveJSONService.class);

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response processPost(String jobConfigJSON){
		try {
			return service(jobConfigJSON);
		} catch (IOException e) {
			LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	

    private Response service(String configJSON) throws IOException {
        String tempDirectoryPath = System.getenv(Constants.JUMBUNE_ENV_VAR_NAME)
        		+ WebConstants.TMP_DIR_PATH;

        String fileName = WebConstants.SAVED_JSON_DIR_NAME
                + System.currentTimeMillis() + WebConstants.JSON_EXTENSION;

        File tempDirectory = new File(tempDirectoryPath);
        if (!tempDirectory.exists()) {
            tempDirectory.mkdir();
        }
        
        LOGGER.debug("Received JSON [" + configJSON + "]");
        Gson gson = new Gson();
        JobConfig config = gson.fromJson(configJSON, JobConfig.class);
        ClasspathElement classpathElement = config.getClasspath().getUserSupplied();
        addUserSuppliedJars(configJSON, classpathElement);
        
        String jsonString = gson.toJson(config, JobConfig.class);
        
        File file = new File(tempDirectoryPath + fileName);

        BufferedWriter bw = null;
        try {
        	bw = new BufferedWriter(new FileWriter(file));
        	bw.write(jsonString);
        } finally {
        	if (bw != null) {
        		bw.close();
        	}
        }
        String originalFilename = config.getJumbuneJobName() + "_properties.json";
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=\"" + originalFilename + "\"");
        return response.build();
    }

    /**
     * Adds the user supplied jars.
     *
     * @param json the json
     * @param classpathElement the classpath element
     */
    private void addUserSuppliedJars(String json,
            ClasspathElement classpathElement) {
        if (WebConstants.MASTER_MACHINE_PATH_OPTION == classpathElement.getSource()) {
            String[] resources = null;
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
            resources = WebUtil.jsonValueOfMasterMachineField(
                    WebConstants.DEPENDENT_JAR_MASTER_MACHINE_PATH, jsonObject);
            
            if (resources != null) {
                String[] path = JobConfigUtil.replaceJumbuneHome(resources);
                classpathElement.setFolders(path);
            }
            
            resources = WebUtil.jsonValueOfMasterMachineField(
                    WebConstants.DEPENDENT_JAR_INCLUDE, jsonObject);
            
            if (resources != null) {
                String[] path = JobConfigUtil.replaceJumbuneHome(resources);
                classpathElement.setFiles(path);
            }
            
            resources = WebUtil.jsonValueOfMasterMachineField(
                    WebConstants.DEPENDENT_JAR_EXCLUDE, jsonObject);
            
            if (resources != null) {
                String[] path = JobConfigUtil.replaceJumbuneHome(resources);
                classpathElement.setExcludes(path);
            }
        }
    }
    
}
