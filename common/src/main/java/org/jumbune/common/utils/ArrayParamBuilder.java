package org.jumbune.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * The class ArrayParamBuilder.
 * Used for creating a list of String values.
 *
 */
public class ArrayParamBuilder {
	private List<String> paramList;
	
	/**
	 * Creates a new paramList
	 */
	public ArrayParamBuilder() {
	paramList=new ArrayList<String>();
	}
	
	/**
	 * Creates a new paramList with specified number of arguments
	 * @param noOfArgs
	 */
	public ArrayParamBuilder(int noOfArgs){
		paramList = new ArrayList<String>(noOfArgs);
	}
	
	/**
	 * Appends the specified String param to the list
	 * @param param
	 * @return
	 */
	public ArrayParamBuilder append(String param){
		paramList.add(param);
		return this;
	}
	
	/**
	 * Returns the param list 
	 * @return
	 */
	public List<String> toList(){
		return paramList;
	}
}
