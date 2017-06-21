package org.jumbune.dataprofiling;


import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DataProfNoCriteriaReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
	
	DataProfNoCritBean sorted[] = null; 
 
	private static final String INITIAL_KEY = "initial";
	
	@Override
	protected void setup(Reducer<Text, IntWritable, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
     sorted =  new DataProfNoCritBean[1000];
     DataProfNoCritBean dataProfNoCritBean = new DataProfNoCritBean();
     dataProfNoCritBean.setKey(INITIAL_KEY);
     dataProfNoCritBean.setValue(Integer.MIN_VALUE);
     sorted[0] = dataProfNoCritBean;
	}
	
	
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
		
		int sum = 0;
		for (IntWritable val : values) {
			sum += val.get();
		}
		DataProfNoCritBean dataProfNoCritBean = new DataProfNoCritBean();
		dataProfNoCritBean.setKey(key.toString());
		dataProfNoCritBean.setValue(sum);
		int pos = getPos(sorted, dataProfNoCritBean);
		insert(pos, sorted, dataProfNoCritBean);
	}
	
	/**
	 * Gets the pos of the element to be inserted into the array after comparing the largest of the two element.
	 *
	 * @param arr the arr
	 * @param element the element
	 * @return the pos
	 */
	private static int getPos(DataProfNoCritBean arr[], DataProfNoCritBean element) {
		int indexToInsert = -1;
		for (int i = 0; i < arr.length ; i++) {
			if (element != null && arr[i] != null && element.getValue() > arr[i].getValue()) {
				indexToInsert = i;
				break;
			}
		}
		return indexToInsert;
	}

	/**
	 * This method inserts the element into the array at the specific position.
	 *
	 * @param index the index
	 * @param arr the arr
	 * @param element the element
	 */
	private static void insert(final int index, DataProfNoCritBean arr[], DataProfNoCritBean element) {
		if (index == -1)
			return;
		for (int i = arr.length -1 ; i > index; i--) {
			arr[i] = arr[i - 1];
		}
		arr[index] = element;
	}

	
	

	@Override
	protected void cleanup(Reducer<Text, IntWritable, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {

		for (int i = 0; i < sorted.length; i++) {
			if (sorted[i] != null && !sorted[i].getKey().equals(INITIAL_KEY)) {
				context.write(new Text(sorted[i].getKey()), new IntWritable(sorted[i].getValue()));
			}
		}

	}
	
	/**
	 * The Class DataProfNoCritBean is resposible for storing the output key and the sorted output value.
	 */
	private class DataProfNoCritBean {
		
		/** The key. */
		private String key ;
		
		/** The value. */
		private Integer value  ;
		
		/**
		 * Gets the key.
		 *
		 * @return the key
		 */
		public String getKey() {
			return key;
		}
		
		/**
		 * Sets the key.
		 *
		 * @param key the new key
		 */
		public void setKey(String key) {
			this.key = key;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public Integer getValue() {
			return value;
		}

		/**
		 * Sets the value.
		 *
		 * @param value the new value
		 */
		public void setValue(Integer value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "[" + key + ", " + value + "]";
		}
		
		
		
	}

	
}
