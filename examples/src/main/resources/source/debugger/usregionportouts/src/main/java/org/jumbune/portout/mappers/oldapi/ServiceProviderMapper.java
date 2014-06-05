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
 * ServiceProviderMapper class
 *
 */
@SuppressWarnings("deprecation")
public class ServiceProviderMapper
  extends MapReduceBase
  implements Mapper<Text, Text, Text, Text>
{
	/**
	 * mapper function which collects data where service provider has been changed
	 */
  public void map( Text key, Text value, OutputCollector<Text, Text> output, Reporter reporter )
    throws IOException
  {
    List<String> keyString = PortoutHelper.tokenize( value.toString(), "," );

    String spId = keyString.get( 2 );
    String olDSpId = keyString.get( PortoutConstants.FIVE );

    if ( !spId.equals( olDSpId ) ) {
      String tn = keyString.get( PortoutConstants.FOUR );
      output.collect( new Text(tn), value );
    }

  }
}
