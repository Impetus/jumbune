package org.jumbune.datavalidation.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.jumbune.datavalidation.DVLRUCache;
import org.jumbune.utils.JobUtil;
import org.jumbune.datavalidation.ArrayListWritable;

/**
 * The Reducer takes <dataviolation type, Iterable<XmlDVWB>>
 * as input and writes a list of all data violation beans corresponding to
 * different data violation types.
 * 
 * 
 * 
 */
public class XmlDataValidationReducer
extends
Reducer<Text, XmlDVWB, Text, XMLVoilationsWB> {
	
	/** The dir path. */
	private String dirPath;

	/** The file handler map. */
	private Map<String, BufferedWriter> fileHandlerMap;
	
	ArrayList<XmlFileViolationsWritable> dataTypeFileViolationsList = null;
	ArrayList<XmlFileViolationsWritable> regexCheckFileViolationsList = null;
	ArrayList<XmlFileViolationsWritable> nullCheckFileViolationsList = null;
	ArrayList<XmlFileViolationsWritable> otherXmlViolationsList = null;
	ArrayList<XmlFileViolationsWritable> fatalViolationsList = null;


	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setup(Reducer.Context context) throws IOException,
	InterruptedException {
		super.setup(context);
		
		String dir = context.getConfiguration().get(XmlDataValidationConstants.SLAVE_FILE_LOC);
		
		dirPath = JobUtil.getAndReplaceHolders(dir);
		
		fileHandlerMap = new DVLRUCache(XmlDataValidationConstants.TEN);
		
	}

	
	/**
	 * reduce method takes <dataviolation type,
	 * Iterable<XmlDVWB>> as input and writes a list of all
	 * data violation beans corresponding to different data violation types.
	 */
	@SuppressWarnings("unused")
	public void reduce(Text key,Iterable<XmlDVWB> values, Context context)
					throws IOException, InterruptedException {
		
		dataTypeFileViolationsList = new ArrayList<XmlFileViolationsWritable>(XmlDataValidationConstants.MAX_VIOLATIONS);
		regexCheckFileViolationsList = new ArrayList<XmlFileViolationsWritable>(XmlDataValidationConstants.MAX_VIOLATIONS);
		nullCheckFileViolationsList = new ArrayList<XmlFileViolationsWritable>(XmlDataValidationConstants.MAX_VIOLATIONS);
		otherXmlViolationsList = new ArrayList<XmlFileViolationsWritable>(XmlDataValidationConstants.MAX_VIOLATIONS);
		fatalViolationsList = new ArrayList<XmlFileViolationsWritable>(XmlDataValidationConstants.MAX_VIOLATIONS);
		
		long totalDirtyTuple = 0;

		long totalDataTypeViolations = 0;
		long totalRegexCheckViolations = 0;
		long totalnullCheckViolations = 0;
		long totalOtherXmlViolations = 0;
		long totalFatalViolations = 0;
		
//		String[] falseSplits = key.toString().split("DDAW");
//		
//		if (falseSplits.length > 1) {
//			key = new Text(falseSplits[0]);
//		}

		createDirectory(key);
		
	      for (XmlDVWB value : values) {
	    	  
	    	String fileName = value.getFileName().toString();
		    String errorType = key.toString();
	    			  
	    			  ArrayListWritable<XMLErrorWritable> errorList = (ArrayListWritable<XMLErrorWritable>)value.getViolationList();
	    			  
	    			  XmlFileViolationsWritable xmlFileViolationsWritable ;
	    			  
	    			  switch (errorType) {
	    			  
	    			  case XmlDataValidationConstants.USER_DEFINED_DATA_TYPE:
	    				  
	    				  totalDataTypeViolations = totalDataTypeViolations+ errorList.size();
	    				  totalDirtyTuple = totalDirtyTuple+errorList.size();
	    				  
	    				  xmlFileViolationsWritable = new XmlFileViolationsWritable();
	    				  xmlFileViolationsWritable.setFileName(fileName);
	    				  xmlFileViolationsWritable.setNumOfViolations(errorList.size());
	    				  dataTypeFileViolationsList.add(xmlFileViolationsWritable);
	    				  
	    				  break;
	    			  
	    			  case XmlDataValidationConstants.USER_DEFINED_REGEX_CHECK:
	    				  
	    				  totalRegexCheckViolations = totalRegexCheckViolations+ errorList.size();
	    				  totalDirtyTuple = totalDirtyTuple+errorList.size();
	    				  
	    				  xmlFileViolationsWritable = new XmlFileViolationsWritable();
	    				  xmlFileViolationsWritable.setFileName(fileName);
	    				  xmlFileViolationsWritable.setNumOfViolations(errorList.size());
	    				  regexCheckFileViolationsList.add(xmlFileViolationsWritable);
	    				  
	    				  break;
	    				  
	    			  case XmlDataValidationConstants.USER_DEFINED_NULL_CHECK:
	    				  
	    				  totalnullCheckViolations = totalnullCheckViolations+ errorList.size();
	    				  totalDirtyTuple = totalDirtyTuple+errorList.size();
	    				  
	    				  xmlFileViolationsWritable = new XmlFileViolationsWritable();
	    				  xmlFileViolationsWritable.setFileName(fileName);
	    				  xmlFileViolationsWritable.setNumOfViolations(errorList.size());
	    				  nullCheckFileViolationsList.add(xmlFileViolationsWritable);
	    				  
	    				  break;
	    				  
	    			  case XmlDataValidationConstants.FATAL_ERROR:

	    				  totalFatalViolations = totalFatalViolations+ errorList.size();
	    				  totalDirtyTuple = totalDirtyTuple+errorList.size();
	    				  
	    				  xmlFileViolationsWritable = new XmlFileViolationsWritable();
	    				  xmlFileViolationsWritable.setFileName(fileName);
	    				  xmlFileViolationsWritable.setNumOfViolations(errorList.size());
	    				  fatalViolationsList.add(xmlFileViolationsWritable);
	    				  
	    				  break;
	    				  
	    			  case XmlDataValidationConstants.OTHER_XML_ERROR:

	    				  totalOtherXmlViolations = totalOtherXmlViolations+ errorList.size();
	    				  totalDirtyTuple = totalDirtyTuple+errorList.size();
	    				  
	    				  xmlFileViolationsWritable = new XmlFileViolationsWritable();
	    				  xmlFileViolationsWritable.setFileName(fileName);
	    				  xmlFileViolationsWritable.setNumOfViolations(errorList.size());
	    				  otherXmlViolationsList.add(xmlFileViolationsWritable);
	    				  
	    				  break;
	    				  
	    			  default:
	    					break;
	    				  
	    			  }
		    		  
		    		  for(XMLErrorWritable error : errorList){
		    			  
		    			  writeViolationsToFile(fileName, error.getLineNumber().get(), errorType, error.getErrorDetail().toString());
		    			  
		    		  }
	    		  }
	 
		for (BufferedWriter bw : fileHandlerMap.values()) {
			bw.close();
		}
		fileHandlerMap.clear();
		
		long dirtyTuple = 0;
		
		if (!dataTypeFileViolationsList.isEmpty()) {
			writeViolations(XmlDataValidationConstants.USER_DEFINED_DATA_TYPE, context, totalDirtyTuple, 
					totalDataTypeViolations, dataTypeFileViolationsList);
		}
		if (!regexCheckFileViolationsList.isEmpty()) {
			writeViolations(XmlDataValidationConstants.USER_DEFINED_REGEX_CHECK, context, totalDirtyTuple, 
					totalRegexCheckViolations, regexCheckFileViolationsList);
		}
		if (!nullCheckFileViolationsList.isEmpty()) {
			writeViolations(XmlDataValidationConstants.USER_DEFINED_NULL_CHECK, context, totalDirtyTuple, 
					totalnullCheckViolations, nullCheckFileViolationsList);
		}
		if (!fatalViolationsList.isEmpty()) {
			writeViolations(XmlDataValidationConstants.FATAL_ERROR, context, totalDirtyTuple, 
					totalFatalViolations, fatalViolationsList);
		}
		if (!otherXmlViolationsList.isEmpty()) {
			writeViolations(XmlDataValidationConstants.OTHER_XML_ERROR, context, totalDirtyTuple, 
					totalOtherXmlViolations, otherXmlViolationsList);
		}
	}
	

	private void writeViolations(String violatoinType, Context context,
			long totalViolations,long individualViolations,ArrayList<XmlFileViolationsWritable> voilationList) throws IOException, InterruptedException {
		
		XMLVoilationsWB xmlVoilationsWB =  new XMLVoilationsWB();
		xmlVoilationsWB.setTotalVoilations(new LongWritable(totalViolations));
		xmlVoilationsWB.setIndividualVoilations(new LongWritable(individualViolations));
		
		ArrayListWritable<XmlFileViolationsWritable> list = new ArrayListWritable<XmlFileViolationsWritable>(voilationList);
		
		xmlVoilationsWB.setViolationList(list);
		
		context.write(new Text(violatoinType), xmlVoilationsWB);
	}

	private void createDirectory(Text key) {
			File f = new File(dirPath + File.separator + key.toString());
			f.mkdirs();
			f.setReadable(true, false);
			f.setWritable(true, false);
	}

	/**
	 * Write violations to file. This method writes violations to respective
	 * files in corresponding directories(null, data type, regex, no. of field).
	 * 
	 * @param fileName
	 *            the file name
	 * @param lineNumber
	 *            the line number
	 * @param errorType
	 *            the error type
	 * @param errorDetail
	 *            the error detail
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void writeViolationsToFile(String fileName, long lineNumber,
			String errorType,String errorDetail) throws IOException {

		StringBuffer stringBuffer = new StringBuffer();
		BufferedWriter out = null;

		if (lineNumber == -1) {
			stringBuffer.append("-");
		} else {
			stringBuffer.append(lineNumber);
		}

		stringBuffer.append(XmlDataValidationConstants.PIPE_SEPARATOR).append(errorDetail)
		.append(System.lineSeparator());
		out = getFileHandler(fileName, errorType);
		out.write(stringBuffer.toString());
		out.flush();
	}

	/**
	 * Gets the file handler.
	 * 
	 * @param fileName
	 *            the file name
	 * @param violatino
	 *            type
	 * @return the file handler
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private BufferedWriter getFileHandler(String fileName, String violationType)
			throws IOException {
		
		String absoluteFilePath = dirPath + File.separator + violationType
				+ File.separator + fileName;
		BufferedWriter out = fileHandlerMap.get(absoluteFilePath);
		if (out == null) {
			File f = new File(absoluteFilePath);
			f.setReadable(true, false);
			f.setWritable(true, false);
			out = new BufferedWriter(new FileWriter(f));
			fileHandlerMap.put(absoluteFilePath, out);
		}
		return out;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void cleanup(Reducer.Context context) throws IOException,
	InterruptedException {

		for (BufferedWriter bw : fileHandlerMap.values()) {
			bw.close();
		}
		super.cleanup(context);
	}

	
}