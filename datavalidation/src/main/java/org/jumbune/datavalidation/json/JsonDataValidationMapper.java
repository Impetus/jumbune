package org.jumbune.datavalidation.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


/**
 * The Class JsonDataValidationMapper.
 */
@SuppressWarnings("rawtypes")
public class JsonDataValidationMapper extends Mapper<LongWritable, Text, Text, FileKeyViolationBean> {
	
	/** The schema. */
	private Map <String, Datatype> schema = new HashMap <String, Datatype>();
	
	/** The null map. */
	private Map <String, String> nullMap = new HashMap <String,String>();
	
	/** The regex. */
	private Map <String, String> regex = new HashMap<String, String>();
 
	/** The Constant ARRAY_SUFFIX. */
	private static final String ARRAY_SUFFIX = "Array";
	
	/** The key list. */
	private List <String> keylist = new ArrayList<String>(); 
	
	/** The line number. */
	private int lineNumber = 0;
	
	/** The Tuple counter. */
	private long tupleCounter;
	
	/** The Clean tuple counter. */
	private long cleanTupleCounter;
	
	/** The split start offset. */
	private long splitStartOffset;
	
	/** The split end offset. */
	private long splitEndOffset;
	
	/** The records emitted by map. */
	private long recordsEmittByMap ;
	
	/** The Constant CONSOLE_LOGGER. */
	public static final Logger CONSOLE_LOGGER = LogManager.getLogger("EventLogger");
	
	/** The filename. */
	String filename = null;
	
	/** The missing json line violation bean list. */
	ArrayList <JsonLineViolationBean> missingJsonLineViolationBeanList = new ArrayList <JsonLineViolationBean>();
	
	/** The regex json line violation bean list. */
	ArrayList <JsonLineViolationBean> regexJsonLineViolationBeanList = new ArrayList <JsonLineViolationBean>();
	
	/** The data json line violation bean list. */
	ArrayList <JsonLineViolationBean> dataJsonLineViolationBeanList = new ArrayList <JsonLineViolationBean>();
	
	/** The schema json line violation bean list. */
	ArrayList <JsonLineViolationBean> schemaJsonLineViolationBeanList = new ArrayList <JsonLineViolationBean>();
	
	/** The ok key json line violation bean list. */
	ArrayList <JsonLineViolationBean> okKeyJsonLineViolationBeanList = new ArrayList <JsonLineViolationBean>();
	
	/** The null json line violation bean list. */
	ArrayList <JsonLineViolationBean> nullJsonLineViolationBeanList = new ArrayList <JsonLineViolationBean>();


	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	protected void setup(Mapper.Context context){		
		String jsonString = context.getConfiguration().get(JsonDataVaildationConstants.JSON_ARGUMENT);
		String regexString = context.getConfiguration().get(JsonDataVaildationConstants.REGEX_ARGUMENT);
		String nullString = context.getConfiguration().get(JsonDataVaildationConstants.NULL_ARGUMENT);
		tupleCounter = 0L;
		cleanTupleCounter =0L;
		recordsEmittByMap = 0L;
		//Populating JsonKey and Data type
		schema = getDatatypeExpression(jsonString);
		// Adding JsonKey given by user
		keylist = getKeyList(jsonString);

		if(!(regexString == null)){
			//Populating JsonKey and Regex
			regex = getExpression(regexString);
		}
		if(!(nullString == null)){
			//Populating JsonKey and NULLCONDITION
			nullMap = getExpression(nullString);
		}

		FileSplit fileSplit = (FileSplit)context.getInputSplit();
		splitStartOffset = fileSplit.getStart();
		//calculating end offset of current split
		splitEndOffset = splitStartOffset + fileSplit.getLength() - 1;
		filename = fileSplit.getPath().toUri().getPath();
		filename = filename.replaceAll(JsonDataVaildationConstants.FORWARD_SLASH, JsonDataVaildationConstants.JSON_DOT).substring(1, filename.length());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#map(KEYIN, VALUEIN, org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	public void map(LongWritable mkey, Text mvalue, Context context) throws IOException, InterruptedException
    {
		
		String line = mvalue.toString().trim();
		if(!line.isEmpty() && line.length() > 2)
			processJsonValidation(line);
		
    }
	
	
	private void processJsonValidation(String json){
		tupleCounter = tupleCounter + 1;
		try
          {  
			lineNumber++;
			recordsEmittByMap++;
  		    JsonElement jsonElement = (new JsonParser().parse(json.toString()));
  		    ArrayList <String> list = new ArrayList<String>(); 
  		    ArrayList <JsonKeyViolationBean> dataList = new ArrayList<JsonKeyViolationBean>(); 
  		    ArrayList <String> checkingKeyList = addMapperKey(jsonElement,"",list);
			//Checking Missing Jsonkey
  		    if(keylist.size() > checkingKeyList.size() && checkingKeyList.size()!=0){
  		    	
	    		ArrayList <JsonKeyViolationBean> missingJsonKeyViolationBeanList = new ArrayList <JsonKeyViolationBean>();
  		    	for(String key:keylist){
  		    		if(!checkingKeyList.contains(key)){
  		    			JsonKeyViolationBean keyViolationBean = setViolations(key, JsonDataVaildationConstants.JSON_NODE+key, JsonDataVaildationConstants.MISSING_MSG);
  		    			missingJsonKeyViolationBeanList.add(keyViolationBean);
  		    		}
  		    	}
  		    	
  		    	JsonLineViolationBean lineViolationBean = new JsonLineViolationBean();
  		    	lineViolationBean.setLineNumber(new IntWritable(lineNumber));
  		    	lineViolationBean.setJsonKeyViolationList(missingJsonKeyViolationBeanList);
  		    	missingJsonLineViolationBeanList.add(lineViolationBean);
  		    	
  		    } else{
  		    // Checking DataType Key 
  		    List <JsonKeyViolationBean> violatedKey = dataTypeKeyMatch(jsonElement,"",dataList);
 		    if(violatedKey.isEmpty()){
 		    		if(nullMap.size()>0){
 		    			dataList = new ArrayList<JsonKeyViolationBean>(); 
 		    			// Checking Null Key
 		    			violatedKey =nullTypeKeyMatch(jsonElement, "", dataList);
 		    		}
 		       if(violatedKey.isEmpty()){
 		    	  if(regex.size()>0 ){
 		    		  dataList = new ArrayList<JsonKeyViolationBean>(); 
 		    		  // Checking Regex Key
	    	   			violatedKey=regexKeyMatch(jsonElement, "", dataList);
	    	   		}
 		    	   	if(violatedKey.isEmpty()){
 		    	   				cleanTupleCounter = cleanTupleCounter + 1;
 		    	   			}else{
	  		    		
 		    	   				ArrayList <JsonKeyViolationBean> regexKeyJsonViolationBeanList = new ArrayList <JsonKeyViolationBean>();	
 		    	   				for (JsonKeyViolationBean data:violatedKey){
	  			    			regexKeyJsonViolationBeanList.add(data);  		    			
 		    	   				}
 		    	   				JsonLineViolationBean lineViolationBean = new JsonLineViolationBean();
 		    	   				lineViolationBean.setLineNumber(new IntWritable(lineNumber));
 		    	   				lineViolationBean.setJsonKeyViolationList(regexKeyJsonViolationBeanList);
 		    	   				regexJsonLineViolationBeanList.add(lineViolationBean);
	  		    		}
 		      		}else{
 		      				ArrayList <JsonKeyViolationBean> nullViolationKeyJsonViolationBeanList = new ArrayList <JsonKeyViolationBean>();	
	    	   				for (JsonKeyViolationBean data:violatedKey){
			    			nullViolationKeyJsonViolationBeanList.add(data);  		    			
	    	   				}
	    	   				JsonLineViolationBean lineViolationBean = new JsonLineViolationBean();
	    	   				lineViolationBean.setLineNumber(new IntWritable(lineNumber));
	    	   				lineViolationBean.setJsonKeyViolationList(nullViolationKeyJsonViolationBeanList);
	    	   				nullJsonLineViolationBeanList.add(lineViolationBean);
 		      			
 		      			}
 		    }else{
  		    	ArrayList <JsonKeyViolationBean> dataKeyJsonViolationBeanList = new ArrayList <JsonKeyViolationBean>();
  		    		for (JsonKeyViolationBean data:violatedKey){
  		    			dataKeyJsonViolationBeanList.add(data);
  		    		}
  		    	JsonLineViolationBean lineViolationBean = new JsonLineViolationBean();
	       		lineViolationBean.setLineNumber(new IntWritable(lineNumber));
	       		lineViolationBean.setJsonKeyViolationList(dataKeyJsonViolationBeanList);
	       		dataJsonLineViolationBeanList.add(lineViolationBean);
  		    			}
 		    		
  		    	}
  		    }
           catch (Exception e) {
        	   ArrayList <JsonKeyViolationBean> schemaKeyJsonViolationBeanList = new ArrayList <JsonKeyViolationBean>(1);
        	   JsonKeyViolationBean jsonKVB = setViolations("MalformedJson", "MalformedJson", "MalformedJson");
   				schemaKeyJsonViolationBeanList.add(jsonKVB);
        	   JsonLineViolationBean lineViolationBean = new JsonLineViolationBean();
		    	lineViolationBean.setLineNumber(new IntWritable(lineNumber));
	       		lineViolationBean.setJsonKeyViolationList(schemaKeyJsonViolationBeanList);
	       		schemaJsonLineViolationBeanList.add(lineViolationBean);
        	
           	}finally{
           		// Kept for writing If error list exceed certain size
           	}
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#cleanup(org.apache.hadoop.mapreduce.Mapper.Context)
	 * 
	 */
	protected void cleanup(Mapper<LongWritable, Text, Text, FileKeyViolationBean>.Context context) throws IOException, InterruptedException{
		
		if(missingJsonLineViolationBeanList.size()>0){
			Text text = new Text(JsonDataVaildationConstants.MISSING_KEY);
			writeViolations(context,missingJsonLineViolationBeanList,text);
    	
			missingJsonLineViolationBeanList.clear();
    		}
		if(regexJsonLineViolationBeanList.size()>0){
			Text text = new Text(JsonDataVaildationConstants.REGEX_KEY);
			writeViolations(context,regexJsonLineViolationBeanList,text);
    
			regexJsonLineViolationBeanList.clear();
			}
		if(nullJsonLineViolationBeanList.size()>0){
			Text text = new Text(JsonDataVaildationConstants.NULL_KEY);
			writeViolations(context,nullJsonLineViolationBeanList,text);
    	
			nullJsonLineViolationBeanList.clear();
			}
		if(dataJsonLineViolationBeanList.size()>0){

			Text text = new Text(JsonDataVaildationConstants.DATA_KEY);
			writeViolations(context,dataJsonLineViolationBeanList,text);
    	
			dataJsonLineViolationBeanList.clear();
		}
		if(schemaJsonLineViolationBeanList.size()>0){
	
			Text text = new Text(JsonDataVaildationConstants.JSON_SCHEMA_KEY);
			writeViolations(context,schemaJsonLineViolationBeanList,text);
    	
			schemaJsonLineViolationBeanList.clear();
		}
		
		String dir = context.getConfiguration().get(JsonDataVaildationConstants.SLAVE_DIR);
		String dirPath = dir+File.separator+JsonDataVaildationConstants.TUPLE+File.separator;
		
		File file = new File(dirPath);
		if(!file.exists()){
			file.mkdirs();
		}
		file.setReadable(true, false);
		file.setWritable(true, false);
		BufferedWriter bufferedWriter =null;
		try{
			File attemptFile = new File(dirPath, context.getTaskAttemptID().getTaskID().toString());
			attemptFile.createNewFile();
			attemptFile.setReadable(true, false);
			attemptFile.setWritable(true, false);
			bufferedWriter = new BufferedWriter(new FileWriter(attemptFile));
			bufferedWriter.write(Long.toString(tupleCounter)+JsonDataVaildationConstants.NEW_LINE+Long.toString(cleanTupleCounter));
		}finally{
			if(bufferedWriter!= null){
				bufferedWriter.close();
			}
		}
	}
	

	/**
	 * Write violations.
	 *
	 * @param context the context
	 * @param KeyViolationBeanList the key violation bean list
	 * @param key the key
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	private void writeViolations (Context context, ArrayList <JsonLineViolationBean> KeyViolationBeanList, Text key) throws IOException, InterruptedException{

		    FileKeyViolationBean fileKeyViolationBean = new FileKeyViolationBean();
		    ArrayListWritable<JsonLineViolationBean> arrayListWritable = new ArrayListWritable<JsonLineViolationBean>(KeyViolationBeanList);
			fileKeyViolationBean.setFileName(new Text(filename));
			fileKeyViolationBean.setViolationList(arrayListWritable);
			fileKeyViolationBean.setSplitEndOffset(new LongWritable(splitEndOffset));
			fileKeyViolationBean.setTotalRecordsEmittByMap(new LongWritable(recordsEmittByMap));
		    Text mapperKey = new Text(key.toString());
	    	context.write(mapperKey, fileKeyViolationBean);		
	}

	
	/**
	 * Gets the map<Key, Value> for Null and Regex check.
	 *
	 * @param json the regex json
	 * @return the expression
	 */
	private Map <String,String> getExpression(String json){
		json = json.trim();
		Map<String, String> regexMapper = new HashMap<String, String>();
		if(!json.equals("{}")){
		json = json.substring(1, json.lastIndexOf('}'));
		String [] elements =json.split(JsonDataVaildationConstants.COMMA);
		for (String element : elements) {
			String[] temp = element.split(JsonDataVaildationConstants.SEMI_COLON);
			String key=StringUtils.remove(temp [0], JsonDataVaildationConstants.REMOVE_QUOTE);
			String value = StringUtils.remove(temp [1], JsonDataVaildationConstants.REMOVE_QUOTE);
			regexMapper.put(key, value);
			}  	
		}	
		return regexMapper;
	}
	
	/**
	 * Gets the key list.
	 *
	 * @param json the json
	 * @return the key list
	 */
	private List<String> getKeyList(String json){
		json =json.trim();
		List <String> keylist = new ArrayList<String>();
		if(!json.isEmpty()){
			json =  json.substring(1, json.lastIndexOf('}'));
			String [] elements =json.split(JsonDataVaildationConstants.COMMA);
			for (String element : elements) {
				String[] temp = element.split(JsonDataVaildationConstants.SEMI_COLON);
				String key = StringUtils.remove(temp[0], JsonDataVaildationConstants.REMOVE_QUOTE);
				keylist.add(key);
				}  	
		}
		return keylist;
	}
	

	
	
	/**
	 * Map the user provided datatype json schema.
	 *
	 * @param dataTypeJson the data type json
	 * @return the datatype expression
	 */
	private Map <String, Datatype> getDatatypeExpression (String dataTypeJson){
	
		dataTypeJson =dataTypeJson.trim();
	
		Map<String, Datatype> dataTypeMapper = new HashMap<String, Datatype>();
		if(!dataTypeJson.isEmpty()){
			dataTypeJson = dataTypeJson.substring(1, dataTypeJson.lastIndexOf('}'));
		
		String [] elements =dataTypeJson.split(JsonDataVaildationConstants.COMMA);
		for (String element : elements) {
			
			String[] temp = element.split(JsonDataVaildationConstants.SEMI_COLON);
			String key = temp[0];
			key=StringUtils.remove(key, JsonDataVaildationConstants.REMOVE_QUOTE);
			if(temp[1].contains(JsonDataVaildationConstants.NUMERICTYPE)){
				dataTypeMapper.put(key, Datatype.NUMBER);
				}
			if(temp[1].contains(JsonDataVaildationConstants.NULLTYPE)){
				dataTypeMapper.put(key, Datatype.NULL);
				}
			if(temp[1].contains(JsonDataVaildationConstants.STRINGTYPE)){
				dataTypeMapper.put(key, Datatype.STRING);
				}
			if(temp[1].contains(JsonDataVaildationConstants.BOOLTYPE)){
				dataTypeMapper.put(key, Datatype.BOOLEAN);
				}
			if(temp[1].contains(JsonDataVaildationConstants.MAPTYPE)){
				dataTypeMapper.put(key, Datatype.MAP);
				}
			}  	
		}	
		return dataTypeMapper;
	}




	/**
	 * Adds the Json key in list for checking missing key.
	 *
	 * @param jsonObj the json obj
	 * @param prefix the prefix
	 * @param arrayList the array list
	 * @return the array list
	 */
	private ArrayList<String> addMapperKey(JsonElement jsonObj, String prefix, ArrayList<String> arrayList){
		if(jsonObj.isJsonObject()){
		for (Map.Entry<String, JsonElement> e : jsonObj.getAsJsonObject().entrySet()) {
			if(e.getValue().isJsonObject()){
				prefix = prefix + e.getKey() + JsonDataVaildationConstants.JSON_DOT;
				addMapperKey(e.getValue(), prefix, arrayList);
			}
			else{
				if(e.getValue().isJsonPrimitive()){		
					if(!prefix.isEmpty()){		
						arrayList.add(prefix+e.getKey());
					}else{
						arrayList.add(e.getKey());
					}
				}else if(e.getValue().isJsonArray()){
					for (JsonElement e1 : e.getValue().getAsJsonArray()) {
						if(e1.isJsonObject()){
							addMapperKey(e1,e.getKey()+JsonDataVaildationConstants.JSON_DOT,arrayList);
						}else{
							arrayList.add(prefix+e.getKey()+ARRAY_SUFFIX);						
						}
					}
				
				}else if(e.getValue().isJsonNull()){
					
					if(prefix.isEmpty()){
						arrayList.add(e.getKey());
					}else{
						arrayList.add(prefix+e.getKey());				
					}
					}else{
						arrayList.add(prefix+e.getKey());		
				}
			}
		}
	}
		else if (jsonObj.isJsonArray()){
			for (JsonElement e1 : jsonObj.getAsJsonArray()) {
				addMapperKey(e1,"",arrayList);
			}
		}	
		return arrayList;
	}

	/**
	 * Checking Data type Violation in Json.
	 *
	 * @param jsonObj the json obj
	 * @param prefix the prefix
	 * @param dataKeyViolationBean the data key violation bean
	 * @return the list
	 */
	private List <JsonKeyViolationBean> dataTypeKeyMatch(JsonElement jsonObj, String prefix, List <JsonKeyViolationBean> dataKeyViolationBean){
		if(jsonObj.isJsonObject()){
		for (Map.Entry<String, JsonElement> e : jsonObj.getAsJsonObject().entrySet()) {
			if(e.getValue().isJsonObject()){
				prefix = prefix + e.getKey() + JsonDataVaildationConstants.JSON_DOT;
				dataTypeKeyMatch(e.getValue(), prefix, dataKeyViolationBean);
			}else{
				if(e.getValue().isJsonPrimitive()){
					if(!prefix.isEmpty()){
						String key = prefix+e.getKey();
					if (e.getValue().getAsJsonPrimitive().isBoolean()) {
						if(!schema.get(key).equals(Datatype.BOOLEAN)){
							JsonKeyViolationBean jsonKeyViolationBean = setViolations(prefix+e.getKey(),
									schema.get(key).toString(),Datatype.BOOLEAN.toString());
							
							dataKeyViolationBean.add(jsonKeyViolationBean);
						}	
					} else if (e.getValue().getAsJsonPrimitive().isString()) {
						if(e.getValue().getAsJsonPrimitive().toString().isEmpty()){
							// By pass empty values, checked on Null Validation
						}else{		
							if(!schema.get(key).equals(Datatype.STRING))
							{
								JsonKeyViolationBean jsonKeyViolationBean = setViolations(prefix+e.getKey(),
										schema.get(key).toString(),Datatype.STRING.toString());
								
								dataKeyViolationBean.add(jsonKeyViolationBean);
							}
						}
					} else {
						if(!schema.get(key).equals(Datatype.NUMBER)){
							JsonKeyViolationBean jsonKeyViolationBean = setViolations(prefix+e.getKey(),
										schema.get(key).toString(),Datatype.NUMBER.toString());
									dataKeyViolationBean.add(jsonKeyViolationBean);
						}
					}
				}else{	String key = e.getKey();
						
						if (e.getValue().getAsJsonPrimitive().isBoolean()) {
							if(!schema.get(key).equals(Datatype.BOOLEAN)){
								JsonKeyViolationBean jsonKeyViolationBean = setViolations(e.getKey(),
										schema.get(key).toString(),Datatype.BOOLEAN.toString());
									dataKeyViolationBean.add(jsonKeyViolationBean);
							}
						} else if (e.getValue().getAsJsonPrimitive().isString()) {
							if(e.getValue().getAsJsonPrimitive().toString().isEmpty()){
								// By pass empty values, checked on Null Validation
							}else{
								if(!schema.get(key).equals(Datatype.STRING)){
									JsonKeyViolationBean jsonKeyViolationBean = setViolations(e.getKey(),
										schema.get(key).toString(),Datatype.STRING.toString());
									dataKeyViolationBean.add(jsonKeyViolationBean);
								}
							}
						} else {
							if(!schema.get(key).equals(Datatype.NUMBER)){
								JsonKeyViolationBean jsonKeyViolationBean = setViolations(e.getKey(),
										schema.get(key).toString(),Datatype.NUMBER.toString());
											dataKeyViolationBean.add(jsonKeyViolationBean);
							}
						}

					}
		
				}else if(e.getValue().isJsonArray()){
					for (JsonElement e1 : e.getValue().getAsJsonArray()) {
						if(e1.isJsonObject()){
							dataTypeKeyMatch(e1,e.getKey()+JsonDataVaildationConstants.JSON_DOT,dataKeyViolationBean);
						}else{
							String key = prefix+e.getKey()+ARRAY_SUFFIX;
							if(e1.getAsJsonPrimitive().isNumber()){
								if(!schema.get(key).equals(Datatype.NUMBER)){
									JsonKeyViolationBean jsonKeyViolationBean = setViolations(prefix+e.getKey(),
											schema.get(key).toString(),Datatype.NUMBER.toString());
									dataKeyViolationBean.add(jsonKeyViolationBean);
								}
							}
							else if(e1.getAsJsonPrimitive().isString()){
								if(!schema.get(prefix+e.getKey()+ARRAY_SUFFIX).equals(Datatype.STRING)){
									JsonKeyViolationBean jsonKeyViolationBean = setViolations(prefix+e.getKey(),
											schema.get(key).toString(),Datatype.STRING.toString());
									dataKeyViolationBean.add(jsonKeyViolationBean);
								}
							}
							else if(e1.getAsJsonPrimitive().isBoolean()){
								if(!schema.get(key).equals(Datatype.BOOLEAN)){
									JsonKeyViolationBean jsonKeyViolationBean = setViolations(prefix+e.getKey(),
											schema.get(key).toString(),Datatype.BOOLEAN.toString());
									dataKeyViolationBean.add(jsonKeyViolationBean);
								}
							}
						}
					}
			
				}
				/*else if(e.getValue().isJsonNull()){
					//
				}*/else if(!e.getValue().isJsonNull()) {
					if(!schema.get(prefix+e.getKey()).equals(Datatype.MAP)){
						JsonKeyViolationBean jsonKeyViolationBean = setViolations(prefix+e.getKey(),
								schema.get(prefix+e.getKey()).toString(),Datatype.MAP.toString());
							dataKeyViolationBean.add(jsonKeyViolationBean);
						}
					}
				}
			}
	
		}
		else if (jsonObj.isJsonArray()){
			for (JsonElement e1 : jsonObj.getAsJsonArray()) {
					dataTypeKeyMatch(e1,"",dataKeyViolationBean);
			}
		}
		return dataKeyViolationBean;
	}

	
	
	/**
	 * Checking Null Violation in Json.
	 *
	 * @param jsonObj the json obj
	 * @param prefix the prefix
	 * @param jsonNullViolationBean the json null violation bean
	 * @return the list
	 */
	private List<JsonKeyViolationBean> nullTypeKeyMatch(JsonElement jsonObj, String prefix, List <JsonKeyViolationBean> jsonNullViolationBean){
		if(jsonObj.isJsonObject()){
		for (Map.Entry<String, JsonElement> e : jsonObj.getAsJsonObject().entrySet()) {
			
			if(e.getValue().isJsonObject()){
				prefix = prefix + e.getKey() + JsonDataVaildationConstants.JSON_DOT;
				nullTypeKeyMatch(e.getValue(), prefix,jsonNullViolationBean);
			}else{
				
				if(e.getValue().isJsonPrimitive()){
					
					if(!prefix.isEmpty()){
					 if (e.getValue().getAsJsonPrimitive().isString()) {
						if(e.getValue().getAsJsonPrimitive().toString().isEmpty()){
							String key = prefix+e.getKey();
							if(nullMap.containsKey(key)){
								if(nullMap.get(key).equals(JsonDataVaildationConstants.REGEX_CHECK_CONDITION))
								{
								JsonKeyViolationBean jsonKeyViolationBean = setViolations(key,
										nullMap.get(key).toString(),Datatype.NULL.toString());
								
								jsonNullViolationBean.add(jsonKeyViolationBean);
								}
							}
						}
					 }
					
				}else{	
						 if (e.getValue().getAsJsonPrimitive().isString()) {
							if(e.getValue().getAsJsonPrimitive().toString().isEmpty()){
								String key = e.getKey();
								if(nullMap.containsKey(key)){
									if(nullMap.get(key).equals(JsonDataVaildationConstants.REGEX_CHECK_CONDITION)){
									JsonKeyViolationBean jsonKeyViolationBean = setViolations(key,
											nullMap.get(key).toString(),Datatype.NULL.toString());
									jsonNullViolationBean.add(jsonKeyViolationBean);
									}
								}
							}
						} 

					}
		
				}else if(e.getValue().isJsonArray()){
					for (JsonElement e1 : e.getValue().getAsJsonArray()) {
						if(e1.isJsonObject()){
							nullTypeKeyMatch(e1,e.getKey()+JsonDataVaildationConstants.JSON_DOT,jsonNullViolationBean);
						}else{
							if(e1.getAsJsonPrimitive().isJsonNull()){
								String key =prefix+e.getKey()+ARRAY_SUFFIX;
								if(nullMap.containsKey(key)){
									if(nullMap.get(key).equals(JsonDataVaildationConstants.REGEX_CHECK_CONDITION)){
									JsonKeyViolationBean jsonKeyViolationBean = setViolations(key,
											nullMap.get(key).toString(),Datatype.NUMBER.toString());
									jsonNullViolationBean.add(jsonKeyViolationBean);
									}
								}
							}
							
						}
					}
			
				}else if(e.getValue().isJsonNull()){
					if(prefix.isEmpty()){
						String key = e.getKey();
						if(nullMap.containsKey(key)){							
							if(nullMap.get(key).equals(JsonDataVaildationConstants.REGEX_CHECK_CONDITION)){
							JsonKeyViolationBean jsonKeyViolationBean = setViolations(key,
									nullMap.get(key).toString(),Datatype.NULL.toString());
							jsonNullViolationBean.add(jsonKeyViolationBean);
							}
						}
					}else{
						String key = prefix+e.getKey();
						if(nullMap.containsKey(key)){
							if(nullMap.get(key).equals(JsonDataVaildationConstants.REGEX_CHECK_CONDITION)){
							JsonKeyViolationBean jsonKeyViolationBean = setViolations(key,
									nullMap.get(key).toString(),Datatype.NULL.toString());
							jsonNullViolationBean.add(jsonKeyViolationBean);
								}
							}
						}
					}
				}
			}
	
		}
		else if (jsonObj.isJsonArray()){
			for (JsonElement e1 : jsonObj.getAsJsonArray()) {
				nullTypeKeyMatch(e1,"",jsonNullViolationBean);
			}
		}
		return jsonNullViolationBean;
		
	}	
	

	
	/**
	 * Regex key match.
	 *
	 * @param jsonObj the json obj
	 * @param prefix the prefix
	 * @param regexNullViolationBean the regex null violation bean
	 * @return the list
	 */
	private List<JsonKeyViolationBean> regexKeyMatch(JsonElement jsonObj, String prefix, List <JsonKeyViolationBean> regexNullViolationBean){
		if(jsonObj.isJsonObject()){
		
			for (Map.Entry<String, JsonElement> e : jsonObj.getAsJsonObject().entrySet()) {
			
			if(e.getValue().isJsonObject()){
				prefix = prefix + e.getKey() + JsonDataVaildationConstants.JSON_DOT;
				regexKeyMatch(e.getValue(), prefix,regexNullViolationBean);
			}else{
				if(e.getValue().isJsonPrimitive()){	
					if(!prefix.isEmpty()){
						String key = prefix+e.getKey();
						if(regex.containsKey(key)){	
							if(!(e.getValue().getAsJsonPrimitive().getAsString().matches(regex.get(key).toString())))
							{
								JsonKeyViolationBean jsonKeyViolationBean = setViolations(key,regex.get(key).toString(),
										e.getValue().getAsJsonPrimitive().getAsString()) ;

								regexNullViolationBean.add(jsonKeyViolationBean);
							}
						}	
					}else{	
						String key = e.getKey();
						if (regex.containsKey(key)) {
							if(!(e.getValue().getAsJsonPrimitive().toString().matches(regex.get(key).toString())))
							{
								JsonKeyViolationBean jsonKeyViolationBean = setViolations(e.getKey(),regex.get(key).toString(),
													e.getValue().getAsJsonPrimitive().getAsString());
								regexNullViolationBean.add(jsonKeyViolationBean);
							}
						}	
					}
				}else if(e.getValue().isJsonArray()){
					for (JsonElement e1 : e.getValue().getAsJsonArray()) {
						if(e1.isJsonObject()){
							regexKeyMatch(e1,e.getKey()+JsonDataVaildationConstants.JSON_DOT,regexNullViolationBean);
						}else{
							String key = prefix+e.getKey()+ARRAY_SUFFIX;
							if (regex.containsKey(key)) {
								if(!e1.toString().matches(regex.get(key).toString())){
									JsonKeyViolationBean jsonKeyViolationBean = setViolations(key,regex.get(key).
											toString(),e1.toString());
									regexNullViolationBean.add(jsonKeyViolationBean);
								}
							}	
						}
					}
				}else if(e.getValue().isJsonNull()){
					if(prefix.isEmpty()){
						String key = e.getKey();
						if (regex.containsKey(key)){
							if(!e.getValue().getAsJsonPrimitive().toString().matches(regex.get(key).toString())){
								JsonKeyViolationBean jsonKeyViolationBean = setViolations(e.getKey(),regex.get(key).toString(),
										e.getValue().getAsJsonPrimitive().toString());
								regexNullViolationBean.add(jsonKeyViolationBean);
								}
							}
						}else{
							String key = prefix+e.getKey();
						if (regex.containsKey(key)){
							if(!e.getValue().getAsJsonPrimitive().toString().matches(regex.get(key).toString())){
								JsonKeyViolationBean jsonKeyViolationBean = setViolations(e.getKey(),regex.get(key).toString(),
										e.getValue().getAsJsonPrimitive().toString());
								regexNullViolationBean.add(jsonKeyViolationBean);
									}
								}
							}
					}else{
						String key = prefix+e.getKey();
						if (regex.containsKey(key)){
							if(!e.getValue().getAsJsonPrimitive().toString().matches(regex.get(key).toString())){
								JsonKeyViolationBean jsonKeyViolationBean = setViolations (key,
										regex.get(key).toString(),e.getValue().getAsJsonPrimitive().toString());
								regexNullViolationBean.add(jsonKeyViolationBean);
							}
						}
					}
				}
			}
	
			
		}else if (jsonObj.isJsonArray()){
			for (JsonElement e1 : jsonObj.getAsJsonArray()) {
				regexKeyMatch(e1,"",regexNullViolationBean);
			}
		}
		return regexNullViolationBean;
		
	}

	/**
	 * Sets the violations.
	 *
	 * @param jsonNode the json node
	 * @param expectedValue the expected value
	 * @param actualValue the actual value
	 * @return the json key violation bean
	 */
	private JsonKeyViolationBean setViolations(String jsonNode, String expectedValue, String actualValue){
		JsonKeyViolationBean jsonKeyViolationBean = new JsonKeyViolationBean();
		jsonKeyViolationBean.setLineNumber(new IntWritable(lineNumber));
		jsonKeyViolationBean.setJsonNode(new Text(jsonNode));
		jsonKeyViolationBean.setExpectedValue(new Text(expectedValue));
		jsonKeyViolationBean.setActualValue(new Text(actualValue));
		return jsonKeyViolationBean;
	}
	
	
	
}


