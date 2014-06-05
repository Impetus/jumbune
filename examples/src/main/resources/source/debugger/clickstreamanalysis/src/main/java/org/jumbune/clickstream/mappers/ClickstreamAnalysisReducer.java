package org.jumbune.clickstream.mappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.jumbune.clickstream.common.ProductDetailsPropertyLoader;


/**
 * 
 *
 */
public class ClickstreamAnalysisReducer extends Reducer<Text, Text, Text, Text>{

public void reduce(Text key, Iterable<Text> productData, Context context) throws IOException, InterruptedException {
		
		int defaulterCount = 0;
		List<String> productList = new ArrayList<String>();
			for (Text productId : productData) {
				String productCategory = ProductDetailsPropertyLoader.getProperty(productId.toString());
				productList.add(productCategory);
			}
			
			//Logic to suggest product recommendations to user based on clickstream information
			Text productRecommendation = new Text();
			if(productList.contains("clothing") && productList.contains("shoes")){
				productRecommendation.set("handbags");
				context.write(key, productRecommendation);
			}else if(productList.contains("grocery") && productList.contains("tools")){
				productRecommendation.set("home&garden");
				context.write(key, productRecommendation);
			}else if(productList.contains("computers") && productList.contains("movies")){
				productRecommendation.set("electronics");
				context.write(key, productRecommendation);
			}else{
				context.write(key, productRecommendation);
			}
	}
}
