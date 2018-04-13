package org.jumbune.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.Versioning;


public class JenkinsBuildDetails {
	
	private static String jenkinsBuildNumber = null;
	private static String jumbuneBuildNumber = null;
	private static List<String> listOfFilesToCreate = new ArrayList<String>();
	
	/** The LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(JenkinsBuildDetails.class);
	
	public static void main(String[] args) throws URISyntaxException, IOException, Exception{
		
		if (args.length > 0){
			jenkinsBuildNumber = args[0];						
		}else {			
			if (System.getProperty("jenkinsbuildno") != null){
				jenkinsBuildNumber = System.getProperty("jenkinsbuildno");				
			}
		}
		listOfFilesToCreate.add("common/src/main/resources/jumbune.version");
		listOfFilesToCreate.add("jumbune.version");
		writeJenkinsBuild();
	}
		
	public static String getJenkinsFromResources() {
		BufferedReader br = null;
		FileReader fr = null;
		String buildNumber = null;
		try {
			fr = new FileReader(System.getenv("JUMBUNE_HOME") + "/jumbune.version");			
			br = new BufferedReader(fr);
			String sCurrentLine;			
			while ((sCurrentLine = br.readLine()) != null) {
				buildNumber = sCurrentLine.substring(sCurrentLine.indexOf("#")+1).trim();				
			}			
		} catch (IOException e) {
			LOGGER.error("Exception while reading jenkins build number.");			
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				LOGGER.error("Exception while closing resources.");				
			}
		}
		return buildNumber;		
	}
	
	public static void writeJenkinsBuild() {
		FileOutputStream fop = null;
		File file;
		jumbuneBuildNumber = Versioning.COMMUNITY_BUILD_VERSION;
		String content = "Build # " + jumbuneBuildNumber + "_" +jenkinsBuildNumber;		
		for(String filePath : listOfFilesToCreate)
		{
			try {
				file = new File(filePath);
				fop = new FileOutputStream(file);
	
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
				
				// get the content in bytes
				byte[] contentInBytes = content.getBytes();
				fop.write(contentInBytes);
				fop.flush();
				fop.close();
			} catch (IOException e) {
				LOGGER.error("Exception while writing jenkins build number.");				
			} finally {
				try {
					if (fop != null) {
						fop.close();
					}
				} catch (IOException e) {
					LOGGER.error("Exception while closing resources.");					
				}
			}
		}
	}
}