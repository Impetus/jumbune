package org.jumbune.datavalidation.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


/**
 * The Class JsonDataValidationReducer.
 */
public class JsonDataValidationReducer extends
		Reducer<Text, FileKeyViolationBean, Text, TotalReducerViolationBean> {
	
	/** The true count. */
	int trueCount = 0;

	/** The file handler map. */
	private Map <String, BufferedWriter> fileHandlerMap;
	
	/** The max violations in report. */
	private int maxViolationsInReport;
	
	/** The offset lines map. 
	 * This map is used to keep track total number of lines processed against an offset which is the end offset of split
	 * A TreeMap implementation is used further so as to keep the records sorted by end offset of split. 
	 * 
	 **/
	private Map<FileOffsetKey, Long> offsetLinesMap;
	
	/** The regex array. */
	private ArrayList<ViolationPersistenceBean> regexArray;
	
	/** The data array. */
	private ArrayList<ViolationPersistenceBean>  dataArray;
	
	/** The null type array. */
	private ArrayList<ViolationPersistenceBean>  nullTypeArray;
	
	/** The missing array. */
	private ArrayList<ViolationPersistenceBean>  missingArray;
	
	/** The schema array. */
	private ArrayList<ViolationPersistenceBean>  schemaArray;
	
	/** The dir path. */
	String dirPath;
	
	/** The count. */
	Map <String, Integer> count =  new HashMap<String, Integer>();
	
	/** The ok. */
	Text ok = new Text("OkKey");
	
	/** The json schema key. */
	Text jsonSchemaKey = new Text(JsonDataVaildationConstants.JSON_SCHEMA_KEY);
	
	/** The data key. */
	Text dataKey = new Text(JsonDataVaildationConstants.DATA_KEY);
	
	/** The Regex key. */
	Text regexKey = new Text(JsonDataVaildationConstants.REGEX_KEY);
	
	/** The Missing key. */
	Text missingKey = new Text(JsonDataVaildationConstants.MISSING_KEY);
	
	/** The Null key. */
	Text nullKey = new Text(JsonDataVaildationConstants.NULL_KEY);

	/** The tuple counter. */
	Text tupleCounter = new Text("Tuple");
	
	
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#setup(org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setup(Reducer.Context context) throws IOException, InterruptedException {
		super.setup(context);
		maxViolationsInReport = 1000;
		dirPath = context.getConfiguration().get(JsonDataVaildationConstants.SLAVE_DIR);
		ViolationPersistenceBean bean = new ViolationPersistenceBean();
		bean.setLineNum(Integer.MAX_VALUE);
		fileHandlerMap = new DVLRUCache(10);
		offsetLinesMap = new TreeMap<>();

		regexArray = new ArrayList <ViolationPersistenceBean> (maxViolationsInReport);

		dataArray = new ArrayList <ViolationPersistenceBean> (maxViolationsInReport);
		
		nullTypeArray = new ArrayList <ViolationPersistenceBean> (maxViolationsInReport);

		missingArray = new ArrayList <ViolationPersistenceBean> (maxViolationsInReport);

		schemaArray = new ArrayList <ViolationPersistenceBean> (maxViolationsInReport);
		
	}
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#reduce(KEYIN, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	public void reduce(Text key, Iterable<FileKeyViolationBean> values, Context context)
			throws IOException, InterruptedException {
		 
		if(!key.equals(ok)){
			 	createDirectory(key);
		 }
		ArrayList <ReducerViolationBean>  list = new ArrayList<ReducerViolationBean>();
		Integer totalKey = 0, totalLine = 0;
		
		for(FileKeyViolationBean value:values){
			Integer size = value.getViolationList().size();
			Long splitEndOffset = value.getSplitEndOffset().get();
			Long totalRecEmiByMap = value.getTotalRecordsEmittByMap().get();
			String fileName = value.getFileName().toString();
			FileOffsetKey fileOffsetKey = new FileOffsetKey(fileName,splitEndOffset);
			
			offsetLinesMap.put(fileOffsetKey, totalRecEmiByMap);
			ArrayListWritable <JsonLineViolationBean> alw = value.getViolationList();
			Integer keyViolationSize = 0;
			for(JsonLineViolationBean jslb :alw){
			 Integer ViolationSize = jslb.getJsonKeyViolationList().size();
			 List<JsonKeyViolationBean> jsonKVB=jslb.getJsonKeyViolationList();
			 for (JsonKeyViolationBean jskvb :jsonKVB){
				 if(!key.equals(ok)){
					 ViolationPersistenceBean bean =null;
					 if (key.equals(regexKey)){
						 bean = new ViolationPersistenceBean(Integer.parseInt(jskvb.getLineNumber().toString()),jskvb.getJsonNode().toString(),jskvb.getExpectedValue().toString(),
								 jskvb.getActualValue().toString(),regexKey.toString() ,value.getFileName().toString(),splitEndOffset);
						 regexArray.add(bean);
					 }else if (key.equals(missingKey)){
						 bean = new ViolationPersistenceBean(Integer.parseInt(jskvb.getLineNumber().toString()),jskvb.getJsonNode().toString(),jskvb.getExpectedValue().toString(),
								 jskvb.getActualValue().toString(),missingKey.toString() ,value.getFileName().toString(),splitEndOffset);
					 	 missingArray.add(bean);
					 }else if (key.equals(dataKey)){
						 bean = new ViolationPersistenceBean(Integer.parseInt(jskvb.getLineNumber().toString()),jskvb.getJsonNode().toString(),jskvb.getExpectedValue().toString(),
								 jskvb.getActualValue().toString(),dataKey.toString() ,value.getFileName().toString(),splitEndOffset);
						 dataArray.add(bean);
					 }else if (key.equals(jsonSchemaKey)){
						 bean = new ViolationPersistenceBean(Integer.parseInt(jskvb.getLineNumber().toString()),jskvb.getJsonNode().toString(),jskvb.getExpectedValue().toString(),
								 jskvb.getActualValue().toString(),jsonSchemaKey.toString() ,value.getFileName().toString(),splitEndOffset);
						schemaArray.add(bean);
					 
					}else if (key.equals(nullKey)){
						 bean = new ViolationPersistenceBean(Integer.parseInt(jskvb.getLineNumber().toString()),jskvb.getJsonNode().toString(),jskvb.getExpectedValue().toString(),
								 jskvb.getActualValue().toString(),nullKey.toString() ,value.getFileName().toString(),splitEndOffset);
						 nullTypeArray.add(bean);
					 
					}
				 }
				
			 }
			 keyViolationSize = keyViolationSize + ViolationSize;
			}

			totalKey = totalKey + keyViolationSize;
			totalLine = totalLine + size;	
			
			Text text = new Text(value.getFileName());
			ReducerViolationBean reducerViolationBean = new ReducerViolationBean();
			reducerViolationBean.setFileName(text);
			reducerViolationBean.setSize(new IntWritable(keyViolationSize));
			list.add(reducerViolationBean);
		}
		ArrayListWritable<ReducerViolationBean> awb = new ArrayListWritable<ReducerViolationBean>(list);
		TotalReducerViolationBean totalReducerViolationBean= new TotalReducerViolationBean();
		totalReducerViolationBean.setReducerViolationBeanList(awb);
		totalReducerViolationBean.setTotalLineViolation(new IntWritable(totalLine));
		totalReducerViolationBean.setTotalKeyViolation(new IntWritable(totalKey));
		
		context.write(key, totalReducerViolationBean);
	}

	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Reducer#cleanup(org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void cleanup(Reducer.Context context) throws IOException, InterruptedException {		
		
		
		for(ViolationPersistenceBean bean: missingArray){
			if(bean!= null  && bean.getFileName()!= null){
			writeViolationsToFile(bean.getLineNum(),bean.getJsonNode(),bean.getExpectedValue(),
					bean.getActualValue(),bean.getViolationType(),bean.getFileName());
			}
		}
		
		for(ViolationPersistenceBean bean: dataArray){
			if(bean!= null && bean.getFileName()!= null){
			writeViolationsToFile(bean.getLineNum(),bean.getJsonNode(),bean.getExpectedValue(),
					bean.getActualValue(),bean.getViolationType(),bean.getFileName());
			}
		}
		
		for(ViolationPersistenceBean bean: schemaArray){
			if(bean!= null && bean.getFileName()!= null){
			writeViolationsToFile(bean.getLineNum(),bean.getJsonNode(),bean.getExpectedValue(),
					bean.getActualValue(),bean.getViolationType(),bean.getFileName());
			}
		}
		
		for(ViolationPersistenceBean bean: regexArray){
			if(bean!= null && bean.getFileName()!= null){
			writeViolationsToFile(bean.getLineNum(),bean.getJsonNode(),bean.getExpectedValue(),
					bean.getActualValue(),bean.getViolationType(),bean.getFileName());
			}
		}
		for(ViolationPersistenceBean bean: nullTypeArray){
			if(bean!= null && bean.getFileName()!= null){
			writeViolationsToFile(bean.getLineNum(),bean.getJsonNode(),bean.getExpectedValue(),
					bean.getActualValue(),bean.getViolationType(),bean.getFileName());
			}
		}
		for (BufferedWriter bw : fileHandlerMap.values()) {
			bw.close();
		}
		
		super.cleanup(context);
	}

	/**
	 * Creates the directory.
	 *
	 * @param key the key
	 */
	private void createDirectory(Text key) {
		File f = new File(dirPath + File.separator + key.toString());
		f.mkdirs();
		f.setReadable(true, false);
		f.setWritable(true, false);	
}
	
	/**
	 * Gets the file handler.
	 *
	 * @param fileName the file name
	 * @param violationType the violation type
	 * @return the file handler
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private BufferedWriter getFileHandler(String fileName, String violationType) throws IOException {
		String absoluteFilePath = dirPath +File.separator+ violationType + File.separator+ fileName;
		BufferedWriter out = fileHandlerMap.get(absoluteFilePath);
		if (out == null) {
			File f = new File(absoluteFilePath);
			f.setReadable(true, false);
	        f.setWritable(true, false);		
			out = new BufferedWriter(new FileWriter(f));
			fileHandlerMap.put( absoluteFilePath, out);
		}
		return out;
	}
	
	
	/**
	 * Write violations to file.
	 *
	 * @param lineNumber the line number
	 * @param jsonNode the json node
	 * @param expectedValue the expected value
	 * @param actualValue the actual value
	 * @param violType the viol type
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void writeViolationsToFile(int lineNumber, String jsonNode, String expectedValue, String actualValue,
			String violType, String fileName) throws IOException {

		StringBuffer stringBuffer = new StringBuffer();
		BufferedWriter out = null;

		stringBuffer.append(lineNumber);
		stringBuffer.append("|").append(jsonNode);
		stringBuffer.append("|").append(expectedValue).append("|")
				.append(actualValue).append(System.lineSeparator());
		out = getFileHandler(fileName, violType);
		out.write(stringBuffer.toString());
		out.flush();
	}
	
	
	/**
	 * Sets the actual line no.
	 *
	 * @param bean the new actual line no
	 */
	private void setActualLineNo(ViolationPersistenceBean bean) {	 
		 bean.setLineNum(calculateActualLineNo(bean));    	
	}
	
	/**
	 * Calculate actual line no.
	 *
	 * @param bean the bean
	 * @return the integer
	 */
	private int calculateActualLineNo(ViolationPersistenceBean bean) {
		long splitEndOff = bean.getSplitEndOffset();
		long sum = 0; 	   
	    //this fragment of code calculates the sum of all the values in the map
	    // till the splitEndOff is encountered in the keys.  
	    for(Entry<FileOffsetKey, Long> entry: offsetLinesMap.entrySet()) {	    				 
				//verifying that the split belong to the same file
				//entry.getValue() == offsetLinesMap.get(bean.getSplitEndOffset())		
	    	
				//if(offsetFilesMap.get(entry.getKey()).equals(bean.getFileName())){
	    	if(entry.getKey().getFileName().equals(bean.getFileName())){
					if(entry.getKey().getOffset() == splitEndOff) {
						break;
					} else {
						sum += entry.getValue();					
					}
				}
			}
		return (int) (bean.getLineNum() + sum);
	}
	
	/**
	 * The Class FileOffsetKey contains filename and the offset.
	 */
	private static class FileOffsetKey implements Comparable<FileOffsetKey> {

		/** The file name. */
		private String fileName;
		
		/** The offset. */
		private long offset;

		/**
		 * Instantiates a new file offset key.
		 *
		 * @param fileName the file name
		 * @param offset the offset
		 */
		FileOffsetKey(String fileName, long offset) {
			this.fileName = fileName;
			this.offset = offset;
		}

		/**
		 * Gets the file name.
		 *
		 * @return the file name
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * Gets the offset.
		 *
		 * @return the offset
		 */
		public long getOffset() {
			return offset;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(FileOffsetKey o) {
			int i = this.fileName.compareTo(o.getFileName());
			if (i == 0) {
				if (this.offset == o.getOffset()) {
					i = 0;
				} else if (this.offset < o.getOffset()) {
					i = -1;
				} else if (this.offset > o.getOffset()) {
					i = 1;
				}
			}
			return i;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Com [" + fileName + "," + offset + "]";
		}

	}

	/**
	 * The Class ViolationPersistenceBean.
	 */
	private static class ViolationPersistenceBean {
		

		/** The line num. */
		private int lineNum;
		
		/** The expected value. */
		private String expectedValue;
		
		/** The actual value. */
		private String actualValue;
		
		/** The violation type. */
		private String violationType;
		
		/** The file name. */
		private String fileName;
		
		/** The json node. */
		private String jsonNode;
		
		/** The split end offset. */
		private long splitEndOffset;
		
		/**
		 * Instantiates a new violation persistence bean.
		 */
		public ViolationPersistenceBean() {
		
		}
		
  	/**
	   * Instantiates a new violation persistence bean.
	   *
	   * @param lineNum the line num
	   * @param jsonNode the json node
	   * @param expectedValue the expected value
	   * @param actualValue the actual value
	   * @param violationType the violation type
	   * @param fileName the file name
	   * @param splitEndOffset the split end offset
	   */
	  public ViolationPersistenceBean (int lineNum,String jsonNode ,String expectedValue, String actualValue,
			String violationType, String fileName, long splitEndOffset) {

		this.lineNum = lineNum;
		this.jsonNode = jsonNode;
		this.expectedValue = expectedValue;
		this.actualValue = actualValue;
		this.violationType = violationType;
		this.fileName = fileName;
		this.splitEndOffset = splitEndOffset;
	}

		/**
		 * Gets the line num.
		 *
		 * @return the line num
		 */
		public int getLineNum() {
			return lineNum;
		}

		/**
		 * Sets the line num.
		 *
		 * @param lineNum the new line num
		 */
		public void setLineNum(int lineNum) {
			this.lineNum = lineNum;
		}
		
		

		/**
		 * Gets the expected value.
		 *
		 * @return the expected value
		 */
		public String getExpectedValue() {
			return expectedValue;
		}



		/**
		 * Gets the actual value.
		 *
		 * @return the actual value
		 */
		public String getActualValue() {
			return actualValue;
		}



		/**
		 * Gets the violation type.
		 *
		 * @return the violation type
		 */
		public String getViolationType() {
			return violationType;
		}



		/**
		 * Gets the file name.
		 *
		 * @return the file name
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * Gets the json node.
		 *
		 * @return the json node
		 */
		public String getJsonNode() {
			return jsonNode;
		}

		/**
		 * Gets the split end offset.
		 *
		 * @return the split end offset
		 */
		public long getSplitEndOffset() {
			return splitEndOffset;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ViolationPersistenceBean [lineNum=" + lineNum
					+ ", expectedValue=" + expectedValue + ", actualValue="
					+ actualValue + ", violationType=" + violationType
					+ ", fileName=" + fileName + ", jsonNode=" + jsonNode
					+ ", splitEndOffset=" + splitEndOffset + "]";
		}

	}
		
}
