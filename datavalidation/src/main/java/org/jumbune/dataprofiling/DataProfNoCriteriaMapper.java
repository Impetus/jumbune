package org.jumbune.dataprofiling;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataProfilingBean;
import org.jumbune.dataprofiling.utils.DataProfilingConstants;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class DataProfNoCriteriaMapper extends Mapper<Object, Text, Text, IntWritable> {
	
	private static final Logger LOGGER = LogManager.getLogger(DataProfNoCriteriaMapper.class);

	private  static final IntWritable ONE = new IntWritable(1);
	
	private Text category = new Text();
	
	
	/** The field separator. */
	private String fieldSeparator;
	
	@SuppressWarnings("rawtypes")
	protected void setup(Mapper.Context context) throws IOException, InterruptedException {
	
		
		String dpBeanString = context.getConfiguration().get(DataProfilingConstants.DATA_PROFILING_BEAN);
		
		LOGGER.info("Inside Mapper set up,data profiling bean received: "+ dpBeanString);
		Gson gson = new Gson();
		Type type = new TypeToken<DataProfilingBean>() {
		}.getType();
		DataProfilingBean dataProfilingBean= gson.fromJson(dpBeanString, type);

		fieldSeparator = dataProfilingBean.getFieldSeparator();
		

	}
	
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String recordValue = value.toString();
		String[] fields = recordValue.split(fieldSeparator);
		for (String field : fields) {
			category.set(field);
			context.write(category, ONE);
		}
		
	}


	
	
	
}