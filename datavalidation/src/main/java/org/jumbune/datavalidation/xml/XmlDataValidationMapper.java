package org.jumbune.datavalidation.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.datavalidation.xml.helper.GrammarPreParser;
import org.jumbune.datavalidation.xml.helper.XMLParserImpl;
import org.jumbune.utils.JobUtil;
import org.jumbune.datavalidation.ArrayListWritable;

import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;


/**
 * The Mapper takes<line number, record value> as input and writes <data violation type,data violation bean> as output.
 * 

 * 
 */
@SuppressWarnings({ "deprecation" })
public class XmlDataValidationMapper extends Mapper<Object, Text, Text, XmlDVWB> {
	
	private long totalTuple;
	
	private long cleanTuple;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger(XmlDataValidationMapper.class);
	
	/** The file name. */
	private String fileName = null;
	
	/** The field validation list. */
	private Set<XMLValidationBean>  errorSet;
	
	private XMLGrammarPoolImpl grammarPool = null;
	
	//private int voilationCounter;
	
	private MapWritable mapErrorType;
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
	 */
	protected void setup(Mapper.Context context) throws IOException, InterruptedException {
		
		super.setup(context);
		
		totalTuple=0;
		cleanTuple=0;
		
		mapErrorType = new MapWritable();
		
		URI[] cachedSchema = JumbuneDistributedCache.getCacheFiles(context.getConfiguration());
		
		LOGGER.debug("Loading Grammar from Cache");
		
		for(int i=0; i<cachedSchema.length;i++){
			grammarPool = new GrammarPreParser().loadCache(cachedSchema[i].getPath());
		}
		
		FileSplit split = (FileSplit) context.getInputSplit();		
		
		fileName = split.getPath().toUri().getPath();
		fileName = fileName.replace(XmlDataValidationConstants.XML_LITERAL, XmlDataValidationConstants.EMPTY_STRING);
		fileName = fileName.replaceAll(XmlDataValidationConstants.DIR_SEPARATOR, XmlDataValidationConstants.PERIOD)
				   .substring(1, fileName.length());
		
	}

	/**
	 * Map function that takes<record number, record value> as input
	 * and writes <data violation type,data violation bean> as output.
	 */
	@SuppressWarnings("unused")
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		
		int voilationCounter =0;
		
		totalTuple++;
		
		String recordValue = value.toString();
		
		XMLParserImpl parserImpl = new XMLParserImpl();
		
		errorSet = parserImpl.parseXMLWithGrammar(value, grammarPool,fileName);
		
		for (XMLValidationBean fieldValidationBean :errorSet) {
			
			addFailedValidationToOutput(mapErrorType, fieldValidationBean);
			
			voilationCounter++;
			
			if(voilationCounter >=XmlDataValidationConstants.MAX_VIOLATIONS){
				LOGGER.debug("Max Violations reached,writing to context");
				writeViolations(context, mapErrorType);
				voilationCounter=0;
				mapErrorType = new MapWritable();
			}
		}
		
		if(errorSet == null || errorSet.isEmpty()){
			cleanTuple++;
		}
		
	}
	
	/**
	 * Write failed validation to output.
	 */
	private void addFailedValidationToOutput(MapWritable mapErrorType,XMLValidationBean fieldValidationBean){
		
			
		if(mapErrorType.get(new Text(fieldValidationBean.getErrorType())) == null){
				
				
				ArrayList<XMLErrorWritable> arrayList = new ArrayList<XMLErrorWritable>();
				
				XMLErrorWritable xmlErrorWritable = new XMLErrorWritable();
				xmlErrorWritable.setFileName(new Text(fieldValidationBean.getFileName()));
				xmlErrorWritable.setLineNumber(new LongWritable(fieldValidationBean.getLineNumber()));
				xmlErrorWritable.setErrorDetail(new Text(fieldValidationBean.getErrorDetail()));
				arrayList.add(xmlErrorWritable);
				
				ArrayListWritable<XMLErrorWritable> arrayListWritable = new ArrayListWritable<>(arrayList);
				
				mapErrorType.put(new Text(fieldValidationBean.getErrorType()), arrayListWritable);
				
			}else{
				
			ArrayListWritable<XMLErrorWritable> arrayListWritable =  ((ArrayListWritable)mapErrorType.get(new Text(fieldValidationBean.getErrorType())));

			XMLErrorWritable xmlErrorWritable = new XMLErrorWritable();
			xmlErrorWritable.setFileName(new Text(fieldValidationBean.getFileName()));
			xmlErrorWritable.setLineNumber(new LongWritable(fieldValidationBean
					.getLineNumber()));
			xmlErrorWritable.setErrorDetail(new Text(fieldValidationBean
					.getErrorDetail()));
			arrayListWritable.add(xmlErrorWritable);
				}
	}
	
	private void writeViolations(Context context,MapWritable mapErrorType) throws IOException, InterruptedException{
		
		Set<Writable> keys = mapErrorType.keySet();
		for(Writable key : keys){
			
			XmlDVWB xmlDVWB = new XmlDVWB();
			xmlDVWB.setFileName(new Text(fileName));
			xmlDVWB.setViolationList((ArrayListWritable<XMLErrorWritable>) mapErrorType.get((Text)key));
			
			context.write((Text)key,xmlDVWB);
			
		}
	}
	
	private void cleanOutput(MapWritable mapErrorType) {

				Set<Map.Entry<Writable, Writable>> errorTypeEntrySet = mapErrorType.entrySet();

				for (Map.Entry<Writable, Writable> errorTypeMap : errorTypeEntrySet) {
					ArrayListWritable<XMLErrorWritable> errorList = (ArrayListWritable<XMLErrorWritable>) errorTypeMap.getValue();

					for (XMLErrorWritable error : errorList) {
						error = null;
					}
					errorList.clear();
				}
				errorTypeEntrySet.clear();
	}
	
	@Override
	protected void cleanup(
			Mapper<Object, Text, Text, XmlDVWB>.Context context)
			throws IOException, InterruptedException {
		
		writeViolations(context, mapErrorType);
		mapErrorType = null;
		
		String dir = context.getConfiguration().get(XmlDataValidationConstants.SLAVE_FILE_LOC);
		String dirPath = JobUtil.getAndReplaceHolders(dir);
		dirPath = dirPath +File.separator+"tuple"+File.separator;
		File f = new File(dirPath);
		if(!f.exists()){
			f.mkdirs();
		}
		f.setReadable(true, false);
		f.setWritable(true, false);
		BufferedWriter bufferedWriter =null;
		try{
			File attemptFile = new File(dirPath, context.getTaskAttemptID().getTaskID().toString());
			attemptFile.createNewFile();
			attemptFile.setReadable(true, false);
			attemptFile.setWritable(true, false);
			bufferedWriter = new BufferedWriter(new FileWriter(attemptFile));
			bufferedWriter.write(Long.toString(totalTuple)+"\n"+Long.toString(cleanTuple));
			
		}finally{
			if(bufferedWriter!= null){
				bufferedWriter.close();
			}
		}
		
		super.cleanup(context);				
	}
	

}