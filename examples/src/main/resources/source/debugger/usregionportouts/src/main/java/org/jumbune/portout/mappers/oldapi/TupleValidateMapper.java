package org.jumbune.portout.mappers.oldapi;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.jumbune.portout.PortoutHelper;
import org.jumbune.portout.common.PropertyLoader;


/**
 * Tuple validate mapper for US portout region example.
 * 
 */
@SuppressWarnings("deprecation")
public class TupleValidateMapper
  extends MapReduceBase
  implements Mapper<LongWritable, Text, Text, Text>
{
  private String requiredSpId;

  public String getRequiredSpId() {
	return requiredSpId;
}

public void setRequiredSpId(String requiredSpId) {
	this.requiredSpId = requiredSpId;
}

/**
 * gets required service provider id from property file
 */
public void configure( JobConf conf )
  {
    this.requiredSpId = conf.get( PropertyLoader.getProperty( "table.field.key.spId" ) );
  }

/**
 * map function emits service provider list as output 
 */
  public void map( LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter )
    throws IOException
  {
    List<String> keyString = PortoutHelper.tokenize( value.toString(), "," );

    String spId = keyString.get( 2 );

    if ( spId != null && !spId.equals( "null" ) ) {
      output.collect( new Text( spId ), value );
    }
  }
}
