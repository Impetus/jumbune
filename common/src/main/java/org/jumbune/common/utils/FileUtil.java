package org.jumbune.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.job.Config;
import org.jumbune.common.job.JobConfig;
import org.jumbune.common.job.JumbuneRequest;
import org.jumbune.utils.exception.JumbuneRuntimeException;

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
	public static void copyLibFilesToMaster(
			JumbuneRequest jumbuneRequest) throws InterruptedException, IOException {
		RemoteFileUtil remoteFileUtil = new RemoteFileUtil();
	
		remoteFileUtil.copyRemoteLibFilesToMaster(jumbuneRequest);
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
				LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
			}finally{
				if(fileReader != null){
					try {
						fileReader.close();
					} catch (IOException e) {
						LOGGER.error(JumbuneRuntimeException.throwUnresponsiveIOException(e.getStackTrace()));
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
	
	/**
	 * Gets the all nested file paths. 
	 * This method fetches all the nested files(not directories) recursively inside a particular directory passed as {@code inputPath}.
	 *
	 * @param job the job
	 * @param inputPath the input path
	 * @return the all nested file path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Path[] getAllNestedFilePath(Job job, String inputPath) throws IOException {
		Configuration c = job.getConfiguration();		
		Path parentInputPath = new Path(inputPath);
		FileSystem fs = parentInputPath.getFileSystem(c);
		Object [] arr = getAllFilePath(parentInputPath, fs).toArray();		
		return Arrays.copyOf(arr, arr.length, Path[].class);
	}
	
	/**
	 * Gets the all nested file paths. 
	 * This method fetches all the nested files(not directories) recursively inside a particular directory passed as {@code inputPath}.
	 *
	 * @param job the job
	 * @param inputPath the input path
	 * @return the all nested file path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Path[] getAllJsonNestedFilePath(Job job, String inputPath) throws IOException {
		Configuration c = job.getConfiguration();		
		Path parentInputPath = new Path(inputPath);
		FileSystem fs = parentInputPath.getFileSystem(c);
		Object [] arr = getAllJsonFilePath(parentInputPath, fs).toArray();		
		return Arrays.copyOf(arr, arr.length, Path[].class);
	}
	
	/**
	 * Gets the all nested xml file path.
	 *
	 * @param job
	 *            the job
	 * @param inputPath
	 *            the input path
	 * @return the all nested xml file path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Path[] getAllNestedXMLFilePath(Job job, String inputPath) throws IOException {
		Configuration c = job.getConfiguration();
		Path parentInputPath = new Path(inputPath);
		FileSystem fs = parentInputPath.getFileSystem(c);
		Object[] arr = getAllXMLFilePath(parentInputPath, fs).toArray();
		return Arrays.copyOf(arr, arr.length, Path[].class);
	}

	
	/**
	 * Gets the all file path. A helper method of method {@code getAllNestedFilePath(Job job, String inputPath)}
	 *
	 * @param filePath the file path
	 * @param fs the fs
	 * @return the all file path
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static List<Path> getAllFilePath(Path filePath, FileSystem fs) throws FileNotFoundException, IOException {
		 List<Path> fileList = new ArrayList<>();	    
	    FileStatus[] fileStatus = fs.listStatus(filePath);
	    
	    for (FileStatus fileStat : fileStatus) {	    	
	        if (fileStat.isDirectory()) {
	            fileList.addAll(getAllFilePath(fileStat.getPath(), fs));
	        } else {	        	
	        	 	
	        	if(!(fileStat.getLen() == 0)){
	        		String filePat  = fileStat.getPath().toString() ;
	        		if((getFileSystemAbsolutePath(filePat).endsWith(".txt"))||(getFileSystemAbsolutePath(filePat).endsWith(".csv"))
	        				||!(getFileSystemAbsolutePath(filePat).contains("."))){
	        			fileList.add(fileStat.getPath());	
	        		}	        			        		
	        	}
	        }
	    }
	    
	    return fileList;
	}
	
	private static String getFileSystemAbsolutePath(String prefixedHdfsPath){		
		 int startLocOfString = prefixedHdfsPath.indexOf(":",(prefixedHdfsPath.indexOf(":"))+1);
		 String newHdfsPath = prefixedHdfsPath.substring(startLocOfString+1,prefixedHdfsPath.length());
		 return newHdfsPath.substring(newHdfsPath.indexOf("/"), newHdfsPath.length());
	}
	
	
	/**
	 * Gets the all file path. A helper method of method {@code getAllNestedFilePath(Job job, String inputPath)}
	 *
	 * @param filePath the file path
	 * @param fs the fs
	 * @return the all file path
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static List<Path> getAllJsonFilePath(Path filePath, FileSystem fs) throws FileNotFoundException, IOException {
	    List<Path> fileList = new ArrayList<>();
	    FileStatus[] fileStatus = fs.listStatus(filePath);
	    for (FileStatus fileStat : fileStatus) {
	        if (fileStat.isDirectory()) {
	            fileList.addAll(getAllJsonFilePath(fileStat.getPath(), fs));
	        } else {
	        	if(!(fileStat.getLen() == 0)){
	        		String filePat  = fileStat.getPath().toString() ;
	        		if(((getFileSystemAbsolutePath(filePat).endsWith(".json"))||!(getFileSystemAbsolutePath(filePat).contains("."))))
		        		fileList.add(fileStat.getPath());	     		
	        	}
	        }
	    }
	    return fileList;
	}
	
	/**
	 * Gets the all xml file path.
	 *
	 * @param filePath
	 *            the file path
	 * @param fs
	 *            the fs
	 * @return the all xml file path
	 * @throws IOException
	 */
	private static List<Path> getAllXMLFilePath(Path filePath, FileSystem fs) throws IOException {
		List<Path> fileList = new ArrayList<>();
		FileStatus[] fileStatus = fs.listStatus(filePath);

		for (FileStatus fileStat : fileStatus) {
			if (fileStat.isDirectory()) {
				fileList.addAll(getAllXMLFilePath(fileStat.getPath(), fs));
			} else {
				if (!(fileStat.getLen() == 0)) {
					String filePat  = fileStat.getPath().toString() ;
					if (((getFileSystemAbsolutePath(filePat).endsWith(".xml")) || !(getFileSystemAbsolutePath(filePat).contains(".")))) {
						fileList.add(fileStat.getPath());
					}
				}
			}
		}
		return fileList;
	}
}
