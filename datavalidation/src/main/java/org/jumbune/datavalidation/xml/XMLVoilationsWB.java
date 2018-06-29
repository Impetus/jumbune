/**
 * 
 */
package org.jumbune.datavalidation.xml;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.jumbune.datavalidation.ArrayListWritable;
/**
 * @author vivek.shivhare
 *
 */
public class XMLVoilationsWB implements Writable{
	
	/**
	 * totalVoilations - the total voilations in the xml.
	 */
	private LongWritable totalVoilations;
	/**
	 * individualVoilations - the individual voilations in the xml.
	 */
	private LongWritable individualVoilations;
	/**
	 * violationList - the list of XmlFileViolationsWritable .
	 */
	private ArrayListWritable<XmlFileViolationsWritable> violationList;
	
	/**
	 * @param fileName
	 * @param errorType
	 * @param totalVoilations
	 * @param individualVoilations
	 */
	public XMLVoilationsWB() {
		//fileName = new Text();
		//errorType = new Text();
		totalVoilations = new LongWritable();
		individualVoilations = new LongWritable();
		violationList = new ArrayListWritable<XmlFileViolationsWritable>();
	}
	
	/**
	 * @return the totalVoilations
	 */
	public LongWritable getTotalVoilations() {
		return totalVoilations;
	}
	/**
	 * @param totalVoilations the totalVoilations to set
	 */
	public void setTotalVoilations(LongWritable totalVoilations) {
		this.totalVoilations = totalVoilations;
	}
	/**
	 * @return the individualVoilations
	 */
	public LongWritable getIndividualVoilations() {
		return individualVoilations;
	}
	/**
	 * @param individualVoilations the individualVoilations to set
	 */
	public void setIndividualVoilations(LongWritable individualVoilations) {
		this.individualVoilations = individualVoilations;
	}
	
	/**
	 * @return the violationList
	 */
	public ArrayListWritable<XmlFileViolationsWritable> getViolationList() {
		return violationList;
	}

	/**
	 * @param violationList the violationList to set
	 */
	public void setViolationList(
			ArrayListWritable<XmlFileViolationsWritable> violationList) {
		this.violationList = violationList;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		totalVoilations.write(out);
		individualVoilations.write(out);
		violationList.write(out);
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		totalVoilations.readFields(in);
		individualVoilations.readFields(in);
		violationList.readFields(in);
		
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "XMLVoilationsWB [totalVoilations=" + totalVoilations
				+ ", individualVoilations=" + individualVoilations
				+ ", violationList=" + violationList + "]";
	}
	
	

}
