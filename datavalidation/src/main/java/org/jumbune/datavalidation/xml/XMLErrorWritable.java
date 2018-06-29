/**
 * 
 */
package org.jumbune.datavalidation.xml;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

/**
 * @author vivek.shivhare
 *
 */
public class XMLErrorWritable implements Writable{
	
	/**
	 * lineNumber - the line number in the xml.
	 */
	private LongWritable lineNumber;
	/**
	 * fileName - name of the file .
	 */
	private Text fileName;
	/**
	 * errorDetail - Deatiled validation error.
	 */
	private Text errorDetail;
	/**
	 * @param lineNumber
	 * @param fileName
	 * @param errorType
	 * @param errorDetail
	 */
	public XMLErrorWritable() {
		lineNumber = new LongWritable();
		fileName = new Text();
		errorDetail = new Text();
	}
	/**
	 * @return the lineNumber
	 */
	public LongWritable getLineNumber() {
		return lineNumber;
	}
	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(LongWritable lineNumber) {
		this.lineNumber = lineNumber;
	}
	/**
	 * @return the fileName
	 */
	public Text getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(Text fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the errorDetail
	 */
	public Text getErrorDetail() {
		return errorDetail;
	}
	/**
	 * @param errorDetail the errorDetail to set
	 */
	public void setErrorDetail(Text errorDetail) {
		this.errorDetail = errorDetail;
	}
	@Override
	public void write(DataOutput out) throws IOException {
		lineNumber.write(out);
		fileName.write(out);
		errorDetail.write(out);
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		lineNumber.readFields(in);
		fileName.readFields(in);
		errorDetail.readFields(in);
	}
	
	@Override
	public String toString() {
		return "XMLErrorWritable [lineNumber=" + lineNumber + ", fileName="
				+ fileName + ", errorDetail="+ errorDetail + "]";
	}
	

}
