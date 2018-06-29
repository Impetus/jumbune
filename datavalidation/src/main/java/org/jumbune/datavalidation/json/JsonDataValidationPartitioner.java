package org.jumbune.datavalidation.json;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;


/**
 * The Class JsonDataValidationPartitioner.
 */
public class JsonDataValidationPartitioner extends Partitioner <Text, FileKeyViolationBean> {

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Partitioner#getPartition(java.lang.Object, java.lang.Object, int)
	 */
	@Override
	public int getPartition(Text key, FileKeyViolationBean value,
			int numPartitions) {
		
		String mapperkey = key.toString();
		
		int partitionNumber = 0;
		
		switch(mapperkey){
		case "MissingKey":
			partitionNumber = 0;
			break;
		case "JsonSchemaKey":
			partitionNumber = 1;
			break;
		case "DataKey":
			partitionNumber = 2;
			break;
		case "RegexKey":
			partitionNumber = 3;
			break;
		case "NullKey":
			partitionNumber = 4;
			break;
		default:
			break;
		}
	
		return partitionNumber;
	}

}
