package org.jumbune.dataprofiling;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.common.beans.DataProfilingBean;
import org.jumbune.common.beans.FieldProfilingBean;
import org.jumbune.dataprofiling.utils.DataProfilingConstants;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataProfilingMapper extends Mapper<Object, Text,Text, IntWritable>{
	
	
	private static final Logger LOGGER = LogManager.getLogger(DataProfilingMapper.class);
	
	/** The field separator. */
	private String fieldSeparator;
	
	/** The field profiling beans. */
	private List<FieldProfilingBean> fieldProfilingBeans;
	
	private Text category = new Text();
	
	private  static final IntWritable ONE = new IntWritable(1);
	
	
	
	
	
	@SuppressWarnings("rawtypes")
	protected void setup(Mapper.Context context) throws IOException, InterruptedException {
	
		
		String dpBeanString = context.getConfiguration().get(DataProfilingConstants.DATA_PROFILING_BEAN);
		
		LOGGER.debug("Inside Mapper set up,data profiling bean received: "+ dpBeanString);
		Gson gson = new Gson();
		Type type = new TypeToken<DataProfilingBean>() {
		}.getType();
		DataProfilingBean dataProfilingBean= gson.fromJson(dpBeanString, type);

		fieldSeparator = dataProfilingBean.getFieldSeparator();
		fieldProfilingBeans = dataProfilingBean.getFieldProfilingRules();
	}
	
	
	/**
	 * Map function that takes<record number, record value> as input
	 * and writes <DataProfilingWritableBean,Intwritable> as output.
	 */
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		
		
		String recordValue = value.toString();
		String[] fields = recordValue.split(fieldSeparator);
		double actualFieldValue = 0 ;
		double comparisonValue = 0 ;
		int fieldNumber = 0;
		boolean isLessThan = false ;
		boolean isGreaterThan = false ;
		
		for (FieldProfilingBean fieldProfilingBean : fieldProfilingBeans) {
			fieldNumber = fieldProfilingBean.getFieldNumber();
			if(fieldProfilingBean.getDataProfilingOperand()!=null && !fieldProfilingBean.getDataProfilingOperand().isEmpty() ){
			try{
			actualFieldValue =  Double.parseDouble(fields[fieldNumber-1]);
			} catch(NumberFormatException e){
				String categoryName = DataProfilingConstants.UNMATCHED + fieldNumber;
		    	category.set(categoryName);
		    	writeCategoryToOutput(context,category);
				continue;
			} catch(ArrayIndexOutOfBoundsException e) {
				continue;
			}
			comparisonValue = fieldProfilingBean.getComparisonValue();
				if(fieldProfilingBean.getDataProfilingOperand().equalsIgnoreCase(DataProfilingConstants.GREATER_THAN_EQUAL_TO)){
					  isGreaterThan = checkGreaterThanValue(actualFieldValue,comparisonValue);
					    if(isGreaterThan){
					    	String categoryName = DataProfilingConstants.MATCHED + fieldNumber;
					    	category.set(categoryName);
					    	writeCategoryToOutput(context,category);
					     }else{
					    	 String categoryName = DataProfilingConstants.UNMATCHED + fieldNumber;
					    	category.set(categoryName);
					    	writeCategoryToOutput(context,category);
					  	}
					}else if (fieldProfilingBean.getDataProfilingOperand().equalsIgnoreCase(DataProfilingConstants.LESS_THAN_EQUAL_TO)){
					  isLessThan = checkLessThanValue(actualFieldValue,comparisonValue);
					  if(isLessThan){
							String categoryName = DataProfilingConstants.MATCHED + fieldNumber;
					    	category.set(categoryName);
					    	writeCategoryToOutput(context,category);
					   }else{
							String categoryName = DataProfilingConstants.UNMATCHED + fieldNumber;
					    	category.set(categoryName);
					    	writeCategoryToOutput(context,category);
					  	}
				 	}
			}
		}
	}


	private void writeCategoryToOutput(Context context, Text categoryName) throws IOException, InterruptedException {
			context.write(categoryName, ONE);
			System.out.println("CategoryName: " + categoryName);
		}


	/**
	 * Check less than value.
	 *
	 * @param actualFieldValue the actual field value
	 * @param comparisonValue the comparison value
	 */
	private boolean checkLessThanValue(double actualFieldValue, double comparisonValue) {
			
			if(actualFieldValue <= comparisonValue){
				return true;
			}
			return false;
			
	}


	/**
	 * Check greater than value.
	 *
	 * @param actualFieldValue the actual field value
	 * @param comparisonValue the comparison value
	 */
	private boolean checkGreaterThanValue(double actualFieldValue,
			double comparisonValue) {
		
			if(actualFieldValue >= comparisonValue) {
				return true;
			}
			return false;
	}

}