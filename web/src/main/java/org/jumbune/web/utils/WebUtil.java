/**
 * 
 */
package org.jumbune.web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.Enable;
import org.jumbune.common.beans.cluster.ClusterDefinition;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.utils.Constants;
import org.jumbune.utils.exception.ErrorCodesAndMessages;
import org.jumbune.utils.exception.JumbuneException;
import org.jumbune.utils.exception.JumbuneRuntimeException;

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
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(new FileInputStream(file));

			JobConfig jobConfig = Constants.gson.fromJson(inputStreamReader, JobConfig.class);

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
		return Constants.gson.fromJson(json, ClusterDefinition.class);
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
