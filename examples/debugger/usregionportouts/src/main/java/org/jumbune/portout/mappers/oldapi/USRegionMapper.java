package org.jumbune.portout.mappers.oldapi;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.jumbune.portout.PortoutConstants;
import org.jumbune.portout.PortoutHelper;


/**
 * USRegion mapper class
 *
 */
@SuppressWarnings("deprecation")
public class USRegionMapper
  extends MapReduceBase
  implements Mapper<Text, Text, Text, Text>
{
	/** 
	 * mapper function for USRegionMapper that collects results for regions 1,2 & 3
	 */
  public void map( Text key, Text value, OutputCollector<Text, Text> output, Reporter reporter )
    throws IOException
  {
    List<String> keyString = PortoutHelper.tokenize( value.toString(), "," );

    String region = keyString.get( PortoutConstants.THREE );

    int regionId = Integer.valueOf( region );

    if ( regionId == 1 || regionId == 2 || regionId == PortoutConstants.THREE ) {
      output.collect( key, value );
    }
    
  }
}