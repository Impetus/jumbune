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
import org.jumbune.common.utils.YamlConfigUtil;
import org.jumbune.common.yaml.config.YamlConfig;
import org.jumbune.web.utils.WebConstants;
import org.jumbune.web.utils.WebUtil;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * For allowing the user to save the populated yaml form.
 */
@SuppressWarnings("serial")
public class SaveYamlServlet extends HttpServlet {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(SaveYamlServlet.class);

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

		String yamlFolder = System.getenv("JUMBUNE_HOME")
				+ WebConstants.TMP_DIR_PATH;

		String fileName = WebConstants.SAVED_YAML_DIR_NAME
				+ System.currentTimeMillis() + WebConstants.YAML_EXTENSION;

		File yamlDirectory = new File(yamlFolder);
		if (!yamlDirectory.exists()) {
			yamlDirectory.mkdir();
		}

		String json = (String) request.getParameter("saveYamlJsonData");
		LOGGER.debug("Received JSON [" + json+"]");
		Gson gson = new Gson();
		YamlConfig config = gson.fromJson(json, YamlConfig.class);
		ClasspathElement classpathElement = config.getClasspath()
				.getUserSupplied();
		addUserSuppliedJars(json, classpathElement);
		Constructor constructor = new Constructor(YamlConfig.class);

		TypeDescription desc = new TypeDescription(YamlConfig.class);
		constructor.addTypeDescription(desc);

		Yaml yaml = new Yaml(constructor);

		String yamlData = yaml.dump(config);

		File file = new File(yamlFolder + fileName);

		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(yamlData);
		bw.close();

		String originalFilename = config.getJumbuneJobName()
				+ "_properties.yaml";

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
				String[] path = YamlConfigUtil.replaceJumbuneHome(resources);
				classpathElement.setFolders(path);
			}
			resources = WebUtil.jsonValueOfMasterMachineField(
					WebConstants.DEPENDENT_JAR_INCLUDE, jsonObject);
			if (resources != null) {
				String[] path = YamlConfigUtil.replaceJumbuneHome(resources);
				classpathElement.setFiles(path);
			}
			resources = WebUtil.jsonValueOfMasterMachineField(
					WebConstants.DEPENDENT_JAR_EXCLUDE, jsonObject);
			if (resources != null) {
				String[] path = YamlConfigUtil.replaceJumbuneHome(resources);
				classpathElement.setExcludes(path);
			}
		}
	}
}
