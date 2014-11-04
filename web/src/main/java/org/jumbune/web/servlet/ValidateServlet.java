package org.jumbune.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.ValidateInput;
import org.jumbune.common.utils.YamlConfigUtil;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


/**
 * Class which validates the yaml that is submitted by client.
 */
@WebServlet("/ValidateYamlServlet")
public class ValidateServlet extends HttpServlet {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager
            .getLogger(ValidateServlet.class);

    /**
     * Instantiates a new validate yaml servlet.
     *
     * @see HttpServlet#HttpServlet()
     */
    public ValidateServlet() {
        super();
    }

    /**
     * Do post.
     *
     * @param request the request
     * @param response the response
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        Map<String, Map<String, Map<String, String>>> validatedData = null;
        StringBuilder sBuilder = new StringBuilder();
        String validateString = null;
        YamlConfig config = null;
        Gson gsonDV = new Gson();
        PrintWriter out = response.getWriter();
        BufferedReader br = request.getReader();
        String string;
        while ((string = br.readLine()) != null) {
            sBuilder.append(string);
        }
        try {
            config = (YamlConfig) WebUtil.prepareYamlConfig(sBuilder.toString());
            ClasspathElement classpathElement = config.getClasspath()
                    .getUserSupplied();

            setUserSuppliedJarsIntoClasspathElement(sBuilder, config,
                    classpathElement);
            validatedData = new ValidateInput().validateYaml(config);

        }catch (IOException e) {
            validatedData = new HashMap<String, Map<String, Map<String,String>>>();
            Map<String, Map<String,String>> exceptionMap = new HashMap<String, Map<String,String>>();
            Map<String,String> exceptions = new HashMap<String,String>();
            exceptions
                    .put("basic","Could not be parsed Yaml, fill the input Yaml Form again");
            exceptionMap.put(Constants.BASIC_VALIDATION, exceptions);
            validatedData.put(Constants.FAILURE_KEY, exceptionMap);

        } catch (JsonSyntaxException e) {
            validatedData = new HashMap<String, Map<String, Map<String,String>>>();
            Map<String, Map<String,String>> exceptionMap = new HashMap<String, Map<String,String>>();
            Map<String,String> exceptionList = new HashMap<String,String>();
            exceptionList.put("basic","Could not be parsed Yaml, fill the input Yaml Form again");
            exceptionMap.put(Constants.BASIC_VALIDATION, exceptionList);
            validatedData.put(Constants.FAILURE_KEY, exceptionMap);

        } catch (IllegalArgumentException e) {
            LOGGER.error("YamlWizard has wrong input",e);
        } catch (Exception e) {
            LOGGER.error("Internal error may cause issue",e);
        }

        validateString = gsonDV.toJson(validatedData);
        LOGGER.info("Completed YamlForm validation.");
        response.setContentType("application/json");
        out.print(validateString);
        out.flush();
        if(out!=null){
            out.close();
        }
    }

    /**
     * Sets the user supplied jars into classpath element.
     *
     * @param sBuilder the s builder
     * @param config class is the bean for the yaml file
     * @param classpathElement class is the bean for the classpath elements
     */
    private void setUserSuppliedJarsIntoClasspathElement(
            StringBuilder sBuilder, YamlConfig config,
            ClasspathElement classpathElement) {
        if (classpathElement.getSource() == Constants.MASTER_MAC_PATH
                && WebUtil.isRequiredModuleEnable(config)) {
            String[] resources = null;
            JsonObject json = (JsonObject) new JsonParser().parse(sBuilder
                    .toString());
            resources = WebUtil.jsonValueOfMasterMachineField(
                    WebConstants.DEPENDENT_JAR_MASTER_MACHINE_PATH, json);
            if (resources != null) {
                String [] path = YamlConfigUtil.replaceJumbuneHome(resources);
                classpathElement.setFolders(path);
            }
            resources = WebUtil.jsonValueOfMasterMachineField(
                    WebConstants.DEPENDENT_JAR_INCLUDE, json);
            if (resources != null) {
                String [] path = YamlConfigUtil.replaceJumbuneHome(resources);
                classpathElement.setFiles(path);
            }
            resources = WebUtil.jsonValueOfMasterMachineField(
                    WebConstants.DEPENDENT_JAR_EXCLUDE, json);
            if (resources != null) {
                String [] path = YamlConfigUtil.replaceJumbuneHome(resources);
                classpathElement.setExcludes(path);
            }

        }
    }
}


