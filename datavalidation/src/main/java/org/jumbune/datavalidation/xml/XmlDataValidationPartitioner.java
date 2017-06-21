package org.jumbune.datavalidation.xml;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * The Class XmlDataValidationPartitioner divides the data to be processed by reducer into five parts.
 */
public class XmlDataValidationPartitioner extends Partitioner<Text, XmlDVWB>{

		@Override
		public int getPartition(Text key, XmlDVWB value, int numPartitions) {
			
			String mapperKey = key.toString();
			
			//String[] keys = mapperKey.split("DDAW");
			
			int partitionNumber = 4 ;
			
				switch (mapperKey) {
				
				case XmlDataValidationConstants.USER_DEFINED_DATA_TYPE: 
					
					partitionNumber = 0;
				
					break;
					
				case XmlDataValidationConstants.USER_DEFINED_NULL_CHECK : 
					
					partitionNumber = 1; 
				
					break ; 
					
					
				case XmlDataValidationConstants.USER_DEFINED_REGEX_CHECK :
					
					partitionNumber = 2; 
					
					break;
					
					
				case XmlDataValidationConstants.FATAL_ERROR : 
					
					partitionNumber = 3; 
				
					break;
					
					
				case XmlDataValidationConstants.OTHER_XML_ERROR : 
					
					partitionNumber = 4; 
				
					break ; 
				
				}
			
			return partitionNumber;
		}

}
