package org.jumbune.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;

/**
 * Utility apis related to a file.
 */
public final class FileUtil {
	
	private static final Logger LOGGER = LogManager
			.getLogger(FileUtil.class);
	/**
	 * Instantiates a new file util.
	 */
	private FileUtil(){
		
	}

	/**
	 * Read the contents of a file into String.
	 *
	 * @param path the path
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String readFileIntoString(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fileChannel = stream.getChannel();
			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());		
			return Charset.defaultCharset().decode(mappedByteBuffer).toString();
		} finally {
			if(stream != null){
			stream.close();
			}
		}
	}

	/**
	 * <p>
	 * This method copies all the user dependencies (jars, resources) from first slave to master node. The files will be copied to the UserLib folder
	 * on master.
	 * </p>
	 *
	 * @param Config config 
	 * @throws InterruptedException If an error occurs
	 * @throws IOException If an IO error occurs during the operation
	 * @see Constants#USER_LIB_LOC
	 * @see Loader#getUserLibLocationAtMaster()
	 */
	public static void copyLibFilesToMaster(Config config) throws InterruptedException, IOException {
		RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
	
		remoteFileUtil.copyRemoteLibFilesToMaster(config);
	}


	/**
	 * Extracts the property value from Properties instance from the given file instance
	 * @param file
	 * @param propertyKey for which property value to be returned
	 * @return the populated property value as String
	 */
	public static String getPropertyFromFile(String file, String propertyKey){
		Properties properties = new Properties();
			
			File filePath = new File(file);
			FileReader fileReader = null;
			try{
				fileReader = new FileReader(filePath);
				properties.load(fileReader);
			} catch (IOException e) {
				LOGGER.error("This is unexpected! Configuration file ["+file+"] doesn't exist", e);
			}finally{
				if(fileReader != null){
					try {
						fileReader.close();
					} catch (IOException e) {
						LOGGER.error("Unable to close File Reader instance.", e);
					}
				}
			}
			return properties.getProperty(propertyKey);
	}
	
	/**
	 * Utility method, this method extracts out the given property from the <Jumbune Home>/jobjars/jobname/cluster-configuration.properties file.
	 * @param config
	 * @param propertyKey
	 * @return
	 */
	public static String getClusterDetail(Config config, String propertyKey){
		JobConfig jobConfig = (JobConfig)config;
		String expectedLocation = new StringBuilder().append(JobConfig.getJumbuneHome()).append(File.separator).append(Constants.JOB_JARS_LOC).append(jobConfig.getFormattedJumbuneJobName()).append("cluster-configuration.properties").toString();
		return getPropertyFromFile(expectedLocation, propertyKey);
	}

	/**
	 * Utility method, this method extracts out the given property from the <Jumbune Home>/configuration/clusterinfo properties.
	 * @param propertyKey
	 * @return
	 */
	public static String getClusterInfoDetail(String propertyKey){
		String expectedLocation = new StringBuilder().append(JobConfig.getJumbuneHome()).append(File.separator).append(Constants.CONFIGURATION).append(File.separator).append(Constants.CLUSTER_INFO).toString();
		return getPropertyFromFile(expectedLocation, propertyKey);
	}
}
