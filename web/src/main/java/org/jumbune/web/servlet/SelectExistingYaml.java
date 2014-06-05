package org.jumbune.web.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.ClasspathElement;
import org.jumbune.common.utils.Constants;
import org.jumbune.common.utils.RemoteFileUtil;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;




/**
 * The Class SelectExistingYaml is used to fetch the already configured yamls present in the yaml repository.
 */
public class SelectExistingYaml extends HttpServlet {
	
	/** The Constant YAML_FILE_LOCATION. */
	private static final String YAML_FILE_LOCATION = "yamlrepo/";
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5670494587459539993L;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(SelectExistingYaml.class);
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.service(request, response);
		String actionResult = request.getParameter("selectYamlList");
		Gson gson = new Gson();
		if (actionResult.equals("TRUE")) {
			
			String yamlFileLocation = YamlLoader.getjHome() + YAML_FILE_LOCATION;
				
			List<String> responseList = RemoteFileUtil.executeResponseList(Constants.SORT_COMMAND.split(" "), yamlFileLocation);
			String returnJsonString = gson.toJson(responseList);
			
			returnJsonString = returnJsonString.replaceAll("\\[", "");
			returnJsonString = returnJsonString.replaceAll("\\]", "");
			returnJsonString = returnJsonString.replaceAll("\\\"", "");
			
			if (returnJsonString == null || returnJsonString.trim().equals("")) {
				returnJsonString = "";
			}
			PrintWriter out = null;
			

			try {
				
				response.setContentType("text/html");
				out = response.getWriter();
				out.println(returnJsonString);

			} finally {
				out.close();
				out.flush();
			}
		} else {
			String yamlFileName = request.getParameter("selectedYamlFileName");
			StringBuilder sb = new StringBuilder().append(YamlLoader.getjHome()).append(YAML_FILE_LOCATION).append(yamlFileName);
			yamlFileName = sb.toString();
			InputStream input = new FileInputStream(yamlFileName);
			Constructor constructor = new Constructor(YamlConfig.class);
			TypeDescription desc = new TypeDescription(YamlConfig.class);
			constructor.addTypeDescription(desc);
			Yaml yaml = new Yaml(new Loader(constructor));
			YamlConfig conf = (YamlConfig) yaml.load(input);
			ClasspathElement classpathElement = conf.getClasspath().getUserSupplied();
			JsonObject jsonObject = gson.toJsonTree(conf).getAsJsonObject();
			checkUserSuppliedJar(conf, classpathElement, jsonObject);
			String jsonString = new Gson().toJson(jsonObject);
			
			PrintWriter out = null;
			try {
				response.setContentType("text/html");
				out = response.getWriter();
				out.println(jsonString);
				LOGGER.info("Yaml Wizard configuration loaded");
			} finally {
				if (out != null) {
					out.flush();
				}
				out.close();
			}
			

		}
	}

	/**
	 * Check user supplied jar.
	 *
	 * @param conf bean for the yaml file
	 * @param classpathElement bean for the classpath elements
	 * @param jsonObject the json object
	 */
	private void checkUserSuppliedJar(YamlConfig conf,
			ClasspathElement classpathElement, JsonObject jsonObject) {
		String[] resources;
		if (conf.getClasspath().getUserSupplied().getSource() == WebConstants.MASTER_MACHINE_PATH_OPTION) {
			JsonObject tempObject = null;
			tempObject = ((JsonObject) jsonObject.get("classpath")).get("userSupplied").getAsJsonObject();
			resources = classpathElement.getExcludes();
			if (resources != null) {
				WebUtil.removeAndAddJsonAttribute(tempObject, WebConstants.DEPENDENT_JAR_EXCLUDE_RESOURCE, WebConstants.DEPENDENT_JAR_EXCLUDE,
						resources);
			}
			resources = classpathElement.getFiles();
			if (resources != null) {
				WebUtil.removeAndAddJsonAttribute(tempObject, WebConstants.DEPENDENT_JAR_INCLUDE_RESOURCE, WebConstants.DEPENDENT_JAR_INCLUDE,
						resources);
			}
			resources = classpathElement.getFolders();
			if (resources != null) {
				WebUtil.removeAndAddJsonAttribute(tempObject, WebConstants.DEPENDENT_JAR_FOLDER_RESOURCE,
						WebConstants.DEPENDENT_JAR_MASTER_MACHINE_PATH, resources);
			}
		}
	}

	

}
