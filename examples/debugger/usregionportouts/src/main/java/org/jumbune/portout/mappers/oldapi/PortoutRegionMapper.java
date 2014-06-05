package org.jumbune.portout.mappers.oldapi;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Counter;

/**
 * PortoutRegionMapper class
 *
 */
@SuppressWarnings("deprecation")
public class PortoutRegionMapper
  extends MapReduceBase
  implements Mapper<Text, Text, Text, Text>
{
	/**
	 * mapper function which collects port out region information
	 */
  public void map( Text key, Text value, OutputCollector<Text, Text> output, Reporter reporter )
    throws IOException
  {
    String region = key.toString();
    if ( region.equals( "1" ) || region.equals( "2" ) || region.equals( "3" ) ) {
      String counterGroupRegion = "PortoutRegionCounter";
      Counter totalPortOutCounter = reporter.getCounter( counterGroupRegion, "TotalPortout" );
      totalPortOutCounter.increment( 1 );

      Counter counterPortoutRegion = reporter.getCounter( counterGroupRegion, "Region " + region );
      counterPortoutRegion.increment( 1 );
    }
    output.collect( value, new Text( "" ) );
  }
}