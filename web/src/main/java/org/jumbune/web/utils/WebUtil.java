/**
 * 
 */
package org.jumbune.web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.cluster.Cluster;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.common.utils.ConfigurationUtil;
import org.jumbune.common.utils.JobConfigUtil;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jumbune.common.beans.cluster.ClusterDefinition;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.ExtendedConstants;
import org.jumbune.utils.exception.ExtendedErrorCodesAndMessages;

/**
 * This class contains utility method required for web module.
 * 
 */
public class WebUtil {

	private static final String $JUMBUNE_HOME_CLUSTERS = "$JUMBUNE_HOME/clusters/";
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(WebUtil.class);

	/**
	 * Upload files.
	 *
	 * @param request
	 *            the request
	 * @param fileUploadLoc
	 *            the file upload loc
	 * @return the list
	 * @throws JumbuneException
	 *             the Jumbune exception
	 */
	@SuppressWarnings("unchecked")
	public List<File> uploadFiles(HttpServletRequest request, String fileUploadLoc) throws JumbuneException {

		File repository = new File(fileUploadLoc);

		if (!repository.exists()) {
			LOG.debug("Since the repository doesn't exists create its parent directories");
			repository.mkdirs();
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(repository);
		factory.setSizeThreshold(-1);
		ServletFileUpload upload = new ServletFileUpload(factory);

		List<FileItem> uploadedItems = null;

		String filePath = repository.getAbsolutePath();

		try {
			List<File> fileList = new ArrayList<File>();
			uploadedItems = upload.parseRequest(request);
			for (FileItem fileItem : uploadedItems) {
				if (!fileItem.isFormField() && fileItem.getSize() > 0) {
					File uploadedFile = null;
					String myFullFileName = fileItem.getName(), myFileName = "",
							slashType = (myFullFileName.lastIndexOf('\\') > 0) ? "\\" : "/";
					int startIndex = myFullFileName.lastIndexOf(slashType);
					myFileName = myFullFileName.substring(startIndex + 1, myFullFileName.length());
					uploadedFile = new File(filePath, myFileName);
					fileItem.write(uploadedFile);
					fileList.add(uploadedFile);

				}
			}
			return fileList;

		} catch (FileUploadException e) {
			LOG.error(e);
			throw JumbuneRuntimeException.throwFileNotLoadedException(e.getStackTrace());
		} catch (Exception e) {
			LOG.error(JumbuneRuntimeException.throwException(e.getStackTrace()));
			throw new JumbuneException(ErrorCodesAndMessages.COULD_NOT_CREATE_DIRECTORY, e);
		}
	}

	// TODO: this method should in some common utility class
	/**
	 * Gets the json conf from file.
	 *
	 * @param file
	 *            the file
	 * @return the json conf from file
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	public Config getJobConfFromFile(File file) throws FileNotFoundException {
		Gson gson = new Gson();
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(new FileInputStream(file));

			JobConfig jobConfig = gson.fromJson(inputStreamReader, JobConfig.class);

			LOG.debug("JSON loaded successfully from " + file.getAbsolutePath() + " conf " + jobConfig);
			return jobConfig;

		} catch (FileNotFoundException fne) {
			LOG.error("Could not find JSON file : " + file.getAbsolutePath());
			throw fne;
		} finally {
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException ioe) {
					LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(ioe.getStackTrace()));
				}
			}
		}
	}

	/**
	 * Gets the tabs information.
	 *
	 * @param config
	 *            bean for the yaml file
	 * @return it returns a list containing the tab information.
	 */
	public String getTabsInformation(Config config) {
		boolean isDashBoardNeeded = false;
		StringBuilder tabBuilder = new StringBuilder();

		final char separator = ',';
		JobConfig jobConfig = (JobConfig) config;
		if (jobConfig.getEnableDataValidation().equals(Enable.TRUE)) {
			tabBuilder.append("Data Validation");
			isDashBoardNeeded = true;
		}

		if (jobConfig.getHadoopJobProfile().equals(Enable.TRUE)) {
			tabBuilder.append(separator).append("Cluster Profiling");
		}
		if (jobConfig.getEnableStaticJobProfiling().equals(Enable.TRUE)) {
			tabBuilder.append(separator).append("Static Profiling");
		}

		if (jobConfig.getDebugAnalysis().equals(Enable.TRUE)) {
			tabBuilder.append(separator).append("Debug Analysis");
			isDashBoardNeeded = true;
		}

		if (isDashBoardNeeded) {
			tabBuilder.append(separator).append("Dashboard");
		}
		return tabBuilder.toString();
	}

	/**
	 * This method cut start and ends character of a string.
	 *
	 * @param value
	 *            string which is to cut
	 * @return string after derived new string from old string which passed from
	 *         argument
	 */
	public static String subString(String value) {
		return value.substring(1, value.length() - 1);
	}

	/**
	 * Json value of master machine field.
	 *
	 * @param dependentJarString
	 *            the dependent jar string
	 * @param json
	 *            the json
	 * @return the string[]
	 */
	public static String[] jsonValueOfMasterMachineField(String dependentJarString, JsonObject json) {
		String files[] = null;

		JsonElement element = ((JsonObject) ((JsonObject) json.get("classpath")).get("userSupplied"))
				.get(dependentJarString);
		if (element != null) {
			files = subString(element.toString()).split(WebConstants.DEPENDENT_JAR_SPLIT_REGEX_EXP);
		}
		return files;

	}

	/**
	 * This method checks that if at least one module is enabled which is
	 * required for checking dependent jar field in jobs section like if debug
	 * analysis, profiling or Self Tuning is enabled then it checks for
	 * dependent jar field otherwise no need to check this condition.
	 *
	 * @param config
	 *            the config
	 * @return true if at least one required module is enabled
	 */
	public static boolean isRequiredModuleEnable(Config config) {
		JobConfig jobConfig = (JobConfig) config;
		return (JobConfigUtil.isEnable(jobConfig.getHadoopJobProfile())
				|| JobConfigUtil.isEnable(jobConfig.getDebugAnalysis()));
	}

	/**
	 * This method split the Absolutepathname which is in string,and get the
	 * filename out of it.and save it to the folder which is specified in
	 * argument3
	 *
	 * @param resources
	 *            array of resources which should be copy to the specified
	 *            location
	 * @param dependentJarDir
	 *            the dependent jar dir
	 * @return true if all files copy to specifield location succesfully
	 *         otherwise return false
	 */
	public static boolean getLastIndexOfArray(String[] resources, String dependentJarDir) {

		String[] tempArray = null;
		String tempDestFilePath = null;
		for (String pathname : resources) {
			tempArray = pathname.split("/");
			tempDestFilePath = dependentJarDir + tempArray[tempArray.length - 1];
			try {
				FileUtils.copyFile(new File(pathname), new File(tempDestFilePath));
			} catch (IOException e) {
				return false;
			}
		}
		return true;

	}

	/**
	 * This method take master machine path fields of yaml config bean which is
	 * type of array and convert it into a string.while convert to String .it
	 * include \n in middle of two element .
	 *
	 * @param resourceArray
	 *            the resource array
	 * @return a string
	 */
	public static String convetResourceListToString(String[] resourceArray) {
		return convetResourceListToString(resourceArray, "\n");
	}

	/**
	 * This method take master machine path fields of yaml config bean which is
	 * type of array and convert it into a string.while convert to String .it
	 * include separator provided in second argument on this function in middle
	 * of two element .
	 * 
	 * @param resourceArray
	 *            array of resources which is to convert to string
	 * @param separator
	 *            each array element separate whith this value
	 * @return String
	 */
	public static String convetResourceListToString(String[] resourceArray, String separator) {
		StringBuilder builder = new StringBuilder();
		for (String string : resourceArray) {
			builder.append(separator).append(string);
		}
		return builder.substring(separator.length());
	}

	/**
	 * This method first remove a attribute in json provided in first argument
	 * and then replace with it new attribute .
	 * 
	 * @param jsonObject
	 *            json Object
	 * @param attributeValue
	 *            attribute which is to be remove from json attribute
	 * @param newAttributeValue
	 *            attribute which is to be add in json attribute
	 * @param resources
	 *            resource array .
	 */
	public static void removeAndAddJsonAttribute(JsonObject jsonObject, String attributeValue, String newAttributeValue,
			String[] resources) {
		jsonObject.remove(attributeValue);
		jsonObject.addProperty(newAttributeValue, WebUtil.convetResourceListToString(resources));
	}

	/**
	 * * This method reads the scheduled job report.
	 *
	 * @param scheduledJobLoc
	 *            the scheduled job loc
	 * @return the string
	 * @throws JumbuneException
	 *             the Jumbune exception
	 */
	public static String readScheduleJobReports(String scheduledJobLoc) throws JumbuneException {
		// File scheduleJobLoc
		String reportFolderPath = new StringBuilder(scheduledJobLoc).append(ExtendedConstants.SCHEDULING_REPORT_FOLDER)
				.toString();

		File reportFolder = new File(reportFolderPath);
		Map<String, String> reportMap = new LinkedHashMap<String, String>();

		if (reportFolder.exists()) {
			File[] listOfReports = reportFolder.listFiles();

			if (listOfReports != null) {
				for (File report : listOfReports) {
					try {
						reportMap.put(report.getName(), ConfigurationUtil.readFileData(report.getAbsolutePath()));
					} catch (FileNotFoundException e) {
						LOG.error(JumbuneRuntimeException.throwFileNotLoadedException(e.getStackTrace()));
						throw new JumbuneException(ExtendedErrorCodesAndMessages.COULD_NOT_READ_SCHEDULE_REPORT);
					} catch (IOException e) {
						LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
						throw new JumbuneException(ExtendedErrorCodesAndMessages.COULD_NOT_READ_SCHEDULE_REPORT);
					}
				}
			}

			Gson gson = new Gson();
			return gson.toJson(reportMap);
		}
		return WebConstants.AJAX_STOP_MSG;
	}

	/**
	 * * This method load properties from a file which is type of key value
	 * paired and return value against specific key.
	 *
	 * @param propertyFile
	 *            property file name
	 * @param propertyName
	 *            property name which value user wants to get.
	 * @return String property value
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String getPropertyFromResource(String propertyFile, String propertyName) throws IOException {
		final InputStream msgStream = WebUtil.class.getClassLoader().getResourceAsStream(propertyFile);
		Properties properties = new Properties();
		properties.load(msgStream);
		return (String) properties.get(propertyName);

	}

	/**
	 * Prepare Job config.
	 * 
	 * @param data
	 *            the data
	 * @return the Job config
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws FileUploadException
	 *             the file upload exception
	 */
	public static Config prepareJobConfig(String data) throws IOException, FileUploadException {
		Gson gson = new Gson();
		return gson.fromJson(data, JobConfig.class);

	}

	public static JumbuneRequest addJobConfigWithCluster(String jobConfigJSON) throws IOException, FileUploadException {
		JobConfig jobConfig = (JobConfig) prepareJobConfig(jobConfigJSON);
		String clusterName = jobConfig.getOperatingCluster();
		Cluster cluster = (Cluster) getClusterByName(clusterName);
		JumbuneRequest jumbuneRequest = new JumbuneRequest();
		jumbuneRequest.setCluster(cluster);
		jumbuneRequest.setConfig(jobConfig);
		return jumbuneRequest;
	}

	public static ClusterDefinition getClusterByName(String clusterName) {
		File file = new File($JUMBUNE_HOME_CLUSTERS + clusterName);
		if (!file.exists()) {
			return null;
		}
		String json;
		try {
			json = FileUtils.readFileToString(file);
		} catch (IOException e) {
			LOG.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			return null;
		}
		Gson gson = new Gson();
		return gson.fromJson(json, ClusterDefinition.class);
	}

	/**
	 * @param repository
	 *            creates the directories if they not exist
	 */
	public static void makeDirectories(File repository) {
		if (!repository.exists()) {
			repository.mkdirs();
		}
	}

	/**
	 * @param repository
	 */
	public static void deleteTempFiles(File repository) {
		File folder = new File(repository.getAbsolutePath());

		// Delete all the temp files that created because of this
		// parseRequest!!
		File[] downloadedFiles = folder.listFiles();
		if (downloadedFiles != null) {
			for (File f : downloadedFiles) {
				if (f.getName().endsWith(".tmp")) {
					f.delete();
				}
			}
		}
	}

}
