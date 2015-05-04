package org.jumbune.web.servlet;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.common.job.JobConfig;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * For allowing the user to save the populated yaml form.
 */
@SuppressWarnings("serial")
public class SaveJSONServlet extends HttpServlet {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager
            .getLogger(SaveJSONServlet.class);

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    
    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        super.service(request, response);

        String jsonFolder = System.getenv("JUMBUNE_HOME")
                + WebConstants.TMP_DIR_PATH;

        String fileName = WebConstants.SAVED_JSON_DIR_NAME
                + System.currentTimeMillis() + WebConstants.JSON_EXTENTION;

        File jsonDirectory = new File(jsonFolder);
        if (!jsonDirectory.exists()) {
            jsonDirectory.mkdir();
        }

        String json = (String) request.getParameter("saveJsonData");
        LOGGER.debug("Received JSON [" + json+"]");
        Gson gson = new Gson();
        JobConfig config = gson.fromJson(json, JobConfig.class);
        ClasspathElement classpathElement = config.getClasspath()
                .getUserSupplied();
        addUserSuppliedJars(json, classpathElement);
        
        String jsonString = gson.toJson(config,JobConfig.class);
        

        File file = new File(jsonFolder + fileName);

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(jsonString);
        bw.close();

        String originalFilename = config.getJumbuneJobName()
                + "_properties.json";

        int length = 0;
        ServletOutputStream op = response.getOutputStream();
        ServletContext context = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(fileName);

        response.setContentType((mimetype != null) ? mimetype
                : "application/octet-stream");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + originalFilename + "\"");
        byte[] bbuf = new byte[Constants.ONE_ZERO_TWO_FOUR];
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        length = in.read(bbuf);
        while ((in != null) && ((length ) != -1)) {
            op.write(bbuf, 0, length);
            length = in.read(bbuf);
        }

        in.close();
        op.flush();
        op.close();
    }

    /**
     * Adds the user supplied jars.
     *
     * @param json the json
     * @param classpathElement the classpath element
     */
    private void addUserSuppliedJars(String json,
            ClasspathElement classpathElement) {
        if (WebConstants.MASTER_MACHINE_PATH_OPTION == classpathElement
                .getSource()) {
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
