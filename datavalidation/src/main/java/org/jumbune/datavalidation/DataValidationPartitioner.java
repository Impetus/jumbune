package org.jumbune.datavalidation;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * The Class DataValidationPartitioner divides the data to be processed by
 * reducer into four parts.
 */
public class DataValidationPartitioner extends Partitioner<Text, DataDiscrepanciesArrayWritable>
		implements Configurable {

	private Configuration configuration;

	List<Integer> partitionAllocatedTo = new ArrayList<Integer>();

	RoundRobinIterator rrIterForType = null;

	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Partitioner#getPartition(java.lang.Object, java.lang.Object, int)
	 */
	@Override
	public int getPartition(Text key, DataDiscrepanciesArrayWritable value, int numPartitions) {

		int partitionNumber = Integer.MAX_VALUE;

		boolean configureRoundRobin = configuration.getBoolean(DataValidationConstants.CONFIGURE_RR_FOR_REDUCERS,
				false);

		if (configureRoundRobin) {

			for (int i = 0; i < numPartitions; i++) {
				partitionAllocatedTo.add(i);
			}

			configuration.setBoolean(DataValidationConstants.CONFIGURE_RR_FOR_REDUCERS, false);
		}

		partitionNumber = getPartitionNoRoundRobinbased();

		return partitionNumber;
	}

	/**
	 * Gets the partition no round robinbased. 
	 * list of partition numbers are passed as an input to instantiate RR
	 * Returns the partition number based upon the round robin fashion 
	 *
	 * @return the partition no round robinbased
	 */
	private int getPartitionNoRoundRobinbased() {

		int candidateReducerNo = Integer.MAX_VALUE;

		if (configuration.getBoolean(DataValidationConstants.CONFIGURE_RR_FOR_PARTITION_NO, false)) {
			rrIterForType = new RoundRobinIterator(partitionAllocatedTo);
			configuration.setBoolean(DataValidationConstants.CONFIGURE_RR_FOR_PARTITION_NO, false);
		}

		if (rrIterForType.hasNext()) {
			candidateReducerNo = rrIterForType.next();
		}

		return candidateReducerNo;
	}

	@Override
	public Configuration getConf() {
		return configuration;
	}

	@Override
	public void setConf(Configuration configuration) {
		this.configuration = configuration;
	}
}
