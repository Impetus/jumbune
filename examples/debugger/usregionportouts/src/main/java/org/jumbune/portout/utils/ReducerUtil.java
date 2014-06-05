package org.jumbune.portout.utils;

import java.util.Map;

import org.apache.hadoop.io.Text;
import org.jumbune.portout.PortoutConstants;
import org.jumbune.portout.common.PropertyLoader;
import org.jumbune.portout.utils.ReducerUtil;



/**
 * Utility class for US region port out reducer
 *
 */
public final class ReducerUtil
{
  private static final char DEMO_REPORT_SEPARATOR = PropertyLoader.getProperty( "separator.demoReport" ).charAt( 0 );
  private static final String INPUT_DATA_SEPARATOR = PropertyLoader.getProperty( "separator.inputData" );
  private static final Integer ONE = Integer.valueOf( 1 );
  
  /**
   * private constructor for utility
   */
  private ReducerUtil()
  {
	  
  }

  /**
   * gets the report data
   * @param key
   * @param filterData
   * @return
   */
  private static String getTNReportData( String key, String[] filterData )
  {
    StringBuffer portOutDetailBuffer = new StringBuffer();

    portOutDetailBuffer.append( filterData[PortoutConstants.FOUR] );
    portOutDetailBuffer.append( DEMO_REPORT_SEPARATOR );

    portOutDetailBuffer.append( filterData[PortoutConstants.THREE] );
    portOutDetailBuffer.append( DEMO_REPORT_SEPARATOR );

    portOutDetailBuffer.append( "DemoReport" );
    portOutDetailBuffer.append( DEMO_REPORT_SEPARATOR );

    portOutDetailBuffer.append( filterData[1] );
    portOutDetailBuffer.append( DEMO_REPORT_SEPARATOR );

    portOutDetailBuffer.append( filterData[1] );
    portOutDetailBuffer.append( DEMO_REPORT_SEPARATOR );

    portOutDetailBuffer.append(key);
    portOutDetailBuffer.append( DEMO_REPORT_SEPARATOR );

    portOutDetailBuffer.append( filterData[PortoutConstants.FIVE] );

    return portOutDetailBuffer.toString();
  }

  /**
   * populates the sub total map comprising of report details
   * @param key
   * @param row
   * @param subTotalMap
   * @return
   */
  public static String populateSubtotalMap( Text key, Text row, Map<String, Integer> subTotalMap )
  {
    String[] filterData = row.toString().split( INPUT_DATA_SEPARATOR );

    String tnDetail = ReducerUtil.getTNReportData( key.toString(), filterData );
    Integer previosCount = subTotalMap.get( filterData[PortoutConstants.THREE] );

    if ( previosCount != null ){
    	previosCount = Integer.valueOf( previosCount.intValue() + 1 );
      subTotalMap.put( filterData[PortoutConstants.THREE], previosCount);
      }
    else {
      subTotalMap.put( filterData[PortoutConstants.THREE], ONE );
    }
    return tnDetail;
  }
}
