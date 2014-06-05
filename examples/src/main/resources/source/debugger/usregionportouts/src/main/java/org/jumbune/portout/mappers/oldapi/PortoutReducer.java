package org.jumbune.portout.mappers.oldapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.jumbune.portout.PortoutConstants;
import org.jumbune.portout.PortoutHelper;
import org.jumbune.portout.utils.ReducerUtil;


/**
 * 
 *
 */
@SuppressWarnings("deprecation")
public class PortoutReducer
  extends MapReduceBase
  implements Reducer<Text, Text, Text, Text>
{
  private static final String REPORT_HEADER_KEY = "TN,Region,Port Activity Type,Port Date,Port Time,Old SPID,New SPID";

  private boolean isRowHeaderWritten = false;

  /**
   * reduce function for port outs. Collects region wise port out information
   */
  public void reduce( Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter )
    throws IOException
  {
    if ( !this.isRowHeaderWritten ) {
      addHeader( output, this.isRowHeaderWritten ? 0 : 1 );
      this.isRowHeaderWritten = true;
    }

    Map<String, Integer> subTotalMap = new HashMap<String, Integer>();

    while ( values.hasNext() ) {
      Text row = values.next();
      List<String> keyString = PortoutHelper.tokenize( row.toString(), "," );
      String region = keyString.get(PortoutConstants.THREE);
      String portOutTNDetail = ReducerUtil.populateSubtotalMap( key, row, subTotalMap );

      Text valueText = new Text( portOutTNDetail );
      output.collect( new Text( region ), valueText );
    }
  }

  private void addHeader( OutputCollector<Text, Text> output, int toWrite )
    throws IOException
  {
    switch( toWrite ) {
      case 0:
        break;
      case 1:
        output.collect( new Text( "" ), new Text( REPORT_HEADER_KEY ) );
        break;
    }
  }
}