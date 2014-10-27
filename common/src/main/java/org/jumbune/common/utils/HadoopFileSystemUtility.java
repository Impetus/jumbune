package org.jumbune.common.utils;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.SupportedApacheHadoopVersions;
import org.jumbune.common.yaml.config.Loader;
import org.jumbune.common.yaml.config.YamlLoader;
import org.jumbune.hadoop.distro.ApacheDistributionFileSystem;
import org.jumbune.hadoop.distro.ClouderaDistributionFileSystem;
import org.jumbune.hadoop.distro.HortonDistributionFileSystem;
import org.jumbune.utils.beans.VirtualFileSystem;


/***
 * This class is access point for using Hadoop file system APIs.For specific version of hadoop it provides those specific Hadoop APIs calls.
 * 
 * 
 * 
 */
public class HadoopFileSystemUtility {
	
	/** The loader. */
	private Loader loader = null;
	
	/** The hadoop versions. */
	private SupportedApacheHadoopVersions hadoopVersions = null;
	
	/** The Constant LOG. */
	private static final Logger LOG = LogManager.getLogger(HadoopFileSystemUtility.class);

	/**
	 * Instantiates a new hadoop file system utility.
	 *
	 * @param loader the loader
	 */
	public HadoopFileSystemUtility(Loader loader) {
		this.loader = loader;
		YamlLoader yamlLoader = (YamlLoader)loader;
		this.hadoopVersions = RemotingUtil.getHadoopVersion(yamlLoader.getYamlConfiguration());
	}

	/**
	 * Gets the loader.
	 *
	 * @return the loader
	 */
	public Loader getLoader() {
		return loader;
	}

	/**
	 * Sets the loader.
	 *
	 * @param loader the new loader
	 */
	public void setLoader(Loader loader) {
		this.loader = loader;
	}

	/**
	 * *
	 * return Virtual File System Object of specific version of Hadoop.
	 *
	 * @param nameNodeURI name node URI, Same as that of written in core-site.xml in hadoop configuration directory.
	 * @param username Username of Machine.
	 * @return Hadoop File System Object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public VirtualFileSystem getVirtualFileSystem(String nameNodeURI, String username) throws IOException {
		VirtualFileSystem fileSystem = null;
		switch (hadoopVersions) {
		case HADOOP_0_20_2:
			fileSystem = new ClouderaDistributionFileSystem(nameNodeURI);
			LOG.debug("Detected filesystem [ApacheFileSystem 0.20.2 version]");
			break;
		case Hadoop_1_0_4:
			fileSystem = new ApacheDistributionFileSystem(nameNodeURI, username);
			LOG.debug("Detected filesystem [ApacheFileSystem 1.0.4 version]");
			break;
		case HADOOP_1_0_3:
			fileSystem = new HortonDistributionFileSystem(nameNodeURI, username);
			LOG.debug("Detected filesystem [HortonworksFileSystem]");
			break;
		case HADOOP_2_0_CDH:
			fileSystem = new ClouderaDistributionFileSystem(nameNodeURI);
			LOG.debug("Detected filesystem [ClouderaFileSystem]");
			break;
		default:
			fileSystem = new ApacheDistributionFileSystem(nameNodeURI, username);
			LOG.debug("Detected filesystem [ApacheFileSystem 1.0.4 version]");
			break;
		}
		
		return fileSystem;
	}
}
