package org.jumbune.portout.execution;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileAlreadyExistsException;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.jumbune.portout.execution.JobExecutor;
import org.jumbune.portout.execution.MapRedJobExecutor;

/**
 * 
 * The executor class for MR Jobs
 *
 */
@SuppressWarnings("deprecation")
public abstract class MapRedJobExecutor
  extends JobExecutor
{
  private static final Log LOG = LogFactory.getLog( MapRedJobExecutor.class );

  /**
   * constructor for MapRedJobExecutor
   */
  public MapRedJobExecutor()
  {
    super();
  }

  /**
   * Executes job.
   * @throws IOException
   * @throws InterruptedException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("unused")
  public int execute( JobConf job )
    throws IOException
  {
    boolean isFileAlreadyExists = false;
    
    try {
      Path[] listedPaths = getListedPaths( );
      if ( listedPaths == null || listedPaths.length == 0 ) {
        return 1;
      } else {
        for( int i = 0; i < listedPaths.length; i++ ) {
          FileInputFormat.addInputPath( job, listedPaths[i] );
        }
      }
      JobClient.runJob( job );
      LOG.info( "Job executed" );
      return 0;
    } catch( FileAlreadyExistsException fae ) {
      // If the output directory already exists in Hadoop, no need to process data again simply return the path of existing directory
      isFileAlreadyExists = true;
      return 1;
    }
  }
}