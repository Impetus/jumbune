package org.jumbune.remoting.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HadoopConfigurationPropertyLoader {
	
	/*** Configuration file property variable */
	private static final String DISTRIBUTION_TYPE = "distributionType";
	private static final String HADOOP_TYPE = "hadoopType";
	private static final String HADOOP_HOME = "hadoopHome";
	
	private static final String HDFS_USER = "hdfsUser";
	private static final String YARN_USER = "yarnUser";
	
	private static final String HDFS_PASSWD = "hdfsPasswd";
	private static final String YARN__PASSWD = "yarnPasswd";
	
	private static final String HDFS_USER_PRIVATE_KEY_PATH = "hdfsUserPrivateKeyPath";
	private static final String YARN_USER_PRIVATE_KEY_PATH = "yarnUserPrivateKeyPath";

	/** Logger instance. */
	private static final Logger LOGGER = LogManager.getLogger(HadoopConfigurationPropertyLoader.class);
	
	/** configuration file name */
	private static final String CONFIGURATION_FILE_NAME = "cluster-configuration.properties";
	
	private String configurationFilePath = null;

	
	/** The instance. */
	private static HadoopConfigurationPropertyLoader instance;
	
	/** The properties. */
	private Properties properties;
	
	/** shows status of loading of property in configuration file */
	private boolean propertyLoaded = false;
	
	private HadoopConfigurationPropertyLoader(File filePath) throws IOException{
		configurationFilePath = filePath.getAbsolutePath();
		properties = new Properties();
		FileReader fileReader = null;
		try{
			if(filePath.exists()){
				fileReader = new FileReader(new File(configurationFilePath));
				properties.load(fileReader);
				propertyLoaded = true;
			}else {
			//	properties.store( new FileWriter(filePath), "File contains Hadoop cluster configurations");
				LOGGER.warn("This is unexpected! Configuration file doesn't exist");
			}
		}finally{
			if(fileReader != null){
				fileReader.close();
			}
		}
	}
	
	public static synchronized HadoopConfigurationPropertyLoader getInstance(){
		String configurationFilePath;
		try{
			if(instance == null){
				String agentHome = System.getenv("AGENT_HOME");
				configurationFilePath = agentHome + File.separator + CONFIGURATION_FILE_NAME;
				instance = new HadoopConfigurationPropertyLoader(new File(configurationFilePath));
				return instance;
			}
		}catch(IOException e){
			LOGGER.error("Could not create configuration property loader object", e);
		}
		return instance;
	}
	
	/**
	 * Update and writes in configuration file which stored in cluster configuration cache.
	 */
	public void persistPropertiesToDisk(){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(configurationFilePath),true);
			properties.store(fos,"This file contains cluster configuration properties.");
			propertyLoaded = true;
		}catch(IOException e){
			LOGGER.error("Could not write in configuration file", e);
		}finally{
			if(fos!= null){
				try {
					fos.close();
				} catch (IOException e) {
					LOGGER.error("Could not close the connection", e);
				}	
			}
		}
	}
	
	/**
	 * Checks whether properties has been loaded in configuration file
	 * @return
	 */
	public boolean isPropertyLoaded(){
		return propertyLoaded;
	}

	/**
	 * @return the hadoopHome
	 */
	public String getHadoopHome() {
		return properties.getProperty(HADOOP_HOME);
	}

	/**
	 * @param hadoopHome the hadoopHome to set
	 */
	public void setHadoopHome(String hadoopHome) {
		properties.setProperty(HADOOP_HOME, hadoopHome);
	}

	/**
	 * @return the hadoopType
	 */
	public String getHadoopType() {
		return properties.getProperty(HADOOP_TYPE);
	}

	/**
	 * @param hadoopType the hadoopType to set
	 */
	public void setHadoopType(String hadoopType) {
		properties.setProperty(HADOOP_TYPE, hadoopType);
	}

	/**
	 * @return the distributionType
	 */
	public String getDistributionType() {
		return properties.getProperty(DISTRIBUTION_TYPE);
	}

	/**
	 * @param distributionType the distributionType to set
	 */
	public void setDistributionType(String distributionType) {
		properties.setProperty(DISTRIBUTION_TYPE, distributionType);
	}

	/**
	 * @return, gets the hdfs user
	 */
	public String getHdfsUser() {
		return properties.getProperty(HDFS_USER);
	}

	/**
	 * @param hdfsUser, sets the hdfs user
	 */
	public void setHdfsUser(String hdfsUser) {
		properties.setProperty(HDFS_USER, hdfsUser);
	}

	/**
	 * @return, get's the yarn user
	 */
	public String getYarnUser() {
		return properties.getProperty(YARN_USER);
	}

	/**
	 * @param yarnUser, set's the yarn user
	 */
	public void setYarnUser(String yarnUser) {
		properties.setProperty(YARN_USER, yarnUser);
	}
	/**
	 * @return the hdfsUserPrivateKeyPath
	 */
	public String getHdfsUserPrivateKeyPath() {
		return properties.getProperty(HDFS_USER_PRIVATE_KEY_PATH);
	}

	/**
	 * @param hdfsUserPrivateKeyPath the hdfsUserPrivateKeyPath to set
	 */
	public void setHdfsUserPrivateKeyPath(String hdfsUserPrivateKeyPath) {
		properties.setProperty(HDFS_USER_PRIVATE_KEY_PATH, hdfsUserPrivateKeyPath);
	}

	/**
	 * @return the yarnUserPrivateKeyPath
	 */
	public String getYarnUserPrivateKeyPath() {
		return properties.getProperty(YARN_USER_PRIVATE_KEY_PATH);
	}

	/**
	 * @param yarnUserPrivateKeyPath the yarnUserPrivateKeyPath to set
	 */
	public void setYarnUserPrivateKeyPath(String yarnUserPrivateKeyPath) {
		properties.setProperty(YARN_USER_PRIVATE_KEY_PATH, yarnUserPrivateKeyPath);
	}

	/**
	 * @return the hdfsPasswd
	 */
	public String getHdfsPasswd() {
		return properties.getProperty(HDFS_PASSWD);
	}

	/**
	 * @param hdfsPasswd the hdfsPasswd to set
	 */
	public void setHdfsPasswd(String hdfsPasswd) {
		properties.setProperty(HDFS_PASSWD, hdfsPasswd);
	}

	/**
	 * @return the yarnPasswd
	 */
	public String getYarnPasswd() {
		return properties.getProperty(YARN__PASSWD);
	}

	/**
	 * @param yarnPasswd the yarnPasswd to set
	 */
	public void setYarnPasswd(String yarnPasswd) {
		properties.setProperty(YARN__PASSWD, yarnPasswd);
	}
}
