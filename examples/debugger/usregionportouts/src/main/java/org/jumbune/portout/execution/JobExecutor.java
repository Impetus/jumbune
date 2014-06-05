package org.jumbune.portout.execution;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;
import org.jumbune.portout.PortoutHelper;
import org.jumbune.portout.common.PropertyLoader;


/** 
 *Base Executor for MR Jobs.
 *Sets hadoop configuration details
 *
 */
public class JobExecutor
{
  private String hdfsOutputPath;
  private Configuration conf;
  private GenericOptionsParser parser;

  /**
   * constructor thats sets coreSiteLocation & hdfsSiteLocation in configuration
   */
  public JobExecutor()
  {
    conf = new Configuration();

    String coreSiteLocation = PropertyLoader.getProperty( "hadoop.home" ) + PropertyLoader.getProperty( "location.coreSite" );
    String hdfsSiteLocation = PropertyLoader.getProperty( "hadoop.home" ) + PropertyLoader.getProperty( "location.hdfsSite" );
    if ( (!PortoutHelper.isEmptyOrNull( coreSiteLocation ))  && (!PortoutHelper.isEmptyOrNull( hdfsSiteLocation ))) {
      
        conf.addResource( new Path( "/home/impadmin/hadoop_setups/hadoop-1.0.4/conf/core-site.xml" ) );
        conf.addResource( new Path( "/home/impadmin/hadoop_setups/hadoop-1.0.4/conf/hdfs-site.xml" ) );
      
    }
     
    
  }

  public Configuration getConf() {
	return conf;
}



public void setConf(Configuration conf) {
	this.conf = conf;
}



public String getHdfsOutputPath() {
	return hdfsOutputPath;
}



public void setHdfsOutputPath(String hdfsOutputPath) {
	this.hdfsOutputPath = hdfsOutputPath;
}

public GenericOptionsParser getParser() {
	return parser;
}



public void setParser(GenericOptionsParser parser) {
	this.parser = parser;
}



/**
   * Sets output path in HDFS
   * @return Output path
   */
  protected String getOutputPath()
  {
    hdfsOutputPath = PropertyLoader.getProperty( "hdfs.path.Output" );
    hdfsOutputPath += "_" + System.currentTimeMillis();
    return hdfsOutputPath;
  }

  protected Path[] getListedPaths()
    throws IOException
  {
    FileSystem fs = FileSystem.get( conf );

    StringBuilder preprocessedSBuilder = new StringBuilder( PropertyLoader.getProperty( "hdfs.path.preProcessingOutput" ) );
    
     FileStatus fileStatus[] = fs.globStatus( new Path( preprocessedSBuilder.toString() ) );

    return FileUtil.stat2Paths( fileStatus );
  }
}
