package org.jumbune.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.jumbune.common.yaml.config.Loader;



/**
 * Utility apis related to a file.
 * 
 * 
 */
public final class FileUtil {
	
	
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
			FileChannel fc = stream.getChannel();
			MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			return Charset.defaultCharset().decode(mbb).toString();
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
	 * @param loader Yaml loader
	 * @throws InterruptedException If an error occurs
	 * @throws IOException If an IO error occurs during the operation
	 * @see Constants#USER_LIB_LOC
	 * @see Loader#getUserLibLocatinAtMaster()
	 */
	public static void copyLibFilesToMaster(Loader loader) throws InterruptedException, IOException {
		RemoteFileUtil cu = new RemoteFileUtil();
	
		cu.copyRemoteLibFilesToMaster(loader);
	}

}
