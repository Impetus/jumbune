package org.jumbune.hadoop.distro;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.utils.beans.VirtualFileStatus;
import org.jumbune.utils.beans.VirtualFileSystem;



/**
 * The Class HortonFileSystem.
 */
public class HortonDistributionFileSystem implements VirtualFileSystem {
	
	/** The path. */
	private Path path = null;
	
	/** The file system. */
	private FileSystem fileSystem = null;
	
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(HortonDistributionFileSystem.class);

	/**
	 * Instantiates a new horton file system.
	 *
	 * @param nameNodeURI the name node uri
	 * @param user the user
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public HortonDistributionFileSystem(String nameNodeURI, String user) throws IOException {
		try {
			Configuration configuration = new Configuration();
			fileSystem = FileSystem.get(URI.create(nameNodeURI), configuration, user);
		} catch (InterruptedException exception) {
			LOG.error("interrupted while creating filesystem  object", exception);
		}

	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.beans.VirtualFileSystem#getFileBlockLocations(org.jumbune.utils.beans.VirtualFileStatus, long, long)
	 */
	@Override
	public Object getFileBlockLocations(VirtualFileStatus fileStatus, long start, long end) throws IOException {
		FileStatus fStatus = (FileStatus) fileStatus.get();
		return fileSystem.getFileBlockLocations(fStatus, start, end);

	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.beans.VirtualFileSystem#getFileStatus(java.lang.String)
	 */
	@Override
	public VirtualFileStatus getFileStatus(String hdfsFilePath) throws IOException {
		FileStatus fileStatus = null;
		path = new Path(hdfsFilePath);
		fileStatus = fileSystem.getFileStatus(path);
		return new HortonDistributionFileStatus(fileStatus);

	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.beans.VirtualFileSystem#getLengthOfFile(java.lang.String)
	 */
	@Override
	public long getLengthOfFile(String hdfsFilePath) throws IOException {
		path = new Path(hdfsFilePath);
		return fileSystem.getContentSummary(path).getLength();
	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.beans.VirtualFileSystem#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String hdfsPath) throws IOException {
		path = new Path(hdfsPath);
		return fileSystem.exists(path);
	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.beans.VirtualFileSystem#close()
	 */
	@Override
	public void close() throws IOException {
		fileSystem.close();
	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.beans.VirtualFileSystem#getSpaceConsumed(java.lang.String)
	 */
	@Override
	public long getSpaceConsumed(String hdfsPath) throws IOException {
		path = new Path(hdfsPath);
		return fileSystem.getContentSummary(path).getSpaceConsumed();
	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.beans.VirtualFileSystem#isFile(java.lang.String)
	 */
	@Override
	public boolean isFile(String filePath) throws IOException {
		return fileSystem.isFile(new Path(filePath));
	}

	/* (non-Javadoc)
	 * @see org.jumbune.utils.beans.VirtualFileSystem#delete(java.lang.String, boolean)
	 */
	@Override
	public void delete(String path, boolean recursive) throws IOException {
		fileSystem.delete(new Path(path), recursive);
	}

}
