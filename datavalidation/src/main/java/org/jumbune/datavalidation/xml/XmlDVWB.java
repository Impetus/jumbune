/**
 * 
 */
package org.jumbune.datavalidation.xml;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.jumbune.datavalidation.ArrayListWritable;
/**
 * @author vivek.shivhare
 *
 */
public class XmlDVWB implements Writable{
	
	/**
	 * fileName - the name of the file where the violation occurred.
	 */
	private Text fileName;
	
	/**
	 * violationList - the list of XmlFileViolationsWritable .
	 */
	private ArrayListWritable<XMLErrorWritable> violationList;

	/**
	 * @param fileName
	 * @param violationList
	 */
	public XmlDVWB() {
		this.fileName = new Text();
		this.violationList = new ArrayListWritable<XMLErrorWritable>();
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
	 * @return the violationList
	 */
	public ArrayListWritable<XMLErrorWritable> getViolationList() {
		return violationList;
	}

	/**
	 * @param violationList the violationList to set
	 */
	public void setViolationList(ArrayListWritable<XMLErrorWritable> violationList) {
		this.violationList = violationList;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		fileName.write(out);
		violationList.write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		fileName.readFields(in);
		violationList.readFields(in);
	}

}
