package org.jumbune.datavalidation.xml;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.Writable;


/**
 * The Class XmlFileViolationsWritable is responsible for writing the violations to data output stream.
 */
public class XmlFileViolationsWritable implements Writable{

	/**
	 * numOfViolations - the number of violations in the file.
	 */
	private Integer numOfViolations;
	/**
	 * fileName - the name of the file where the violation occurred.
	 */
	private String fileName;

	/**
	 * Instantiates a new file violations writable.
	 */
	public XmlFileViolationsWritable() {
	}

	/**
	 * Instantiates a new file violations writable.
	 *
	 * @param fileViolationsWritable the file violations writable
	 */
	public XmlFileViolationsWritable(XmlFileViolationsWritable fileViolationsWritable) {
		this.numOfViolations = fileViolationsWritable.getNumOfViolations();
		this.fileName = fileViolationsWritable.getFileName();
	}

	/**
	 * writes to output stream
	 */
	public void write(DataOutput out) throws IOException {
		DataOutputStream dos = (DataOutputStream) out;
		dos.writeInt(numOfViolations);
		byte[] fileNameBytes = fileName.getBytes();
		dos.write(toBytes(fileNameBytes.length));
		dos.write(fileNameBytes);

	}

	/**
	 * Byte array to int.
	 *
	 * @param b the b
	 * @return the int
	 */
	private int byteArrayToInt(byte[] b) {
		return expressionOne(b) | expressionTwo(b) | expressionThree(b);
	}

	/**
	 * Expression three.
	 *
	 * @param b the b
	 * @return the int
	 */
	private int expressionThree(byte[] b) {
		return (b[0] & XmlDataValidationConstants.ZERO_CROSS_FF) << XmlDataValidationConstants.TWENTY_FOUR;
	}

	/**
	 * Expression two.
	 *
	 * @param b the b
	 * @return the int
	 */
	private int expressionTwo(byte[] b) {
		return (b[1] & XmlDataValidationConstants.ZERO_CROSS_FF) << XmlDataValidationConstants.SIXTEEN;
	}

	/**
	 * Expression one.
	 *
	 * @param b the b
	 * @return the int
	 */
	private int expressionOne(byte[] b) {
		return b[XmlDataValidationConstants.THREE] & XmlDataValidationConstants.ZERO_CROSS_FF | (b[2] & XmlDataValidationConstants.ZERO_CROSS_FF) << XmlDataValidationConstants.EIGHT;
	}

	/**
	 * To bytes.
	 *
	 * @param i the i
	 * @return the byte[]
	 */
	private byte[] toBytes(int i) {
		byte[] result = new byte[XmlDataValidationConstants.FOUR];
		result[0] = (byte) (i >> XmlDataValidationConstants.TWENTY_FOUR);
		result[1] = (byte) (i >> XmlDataValidationConstants.SIXTEEN);
		result[2] = (byte) (i >> XmlDataValidationConstants.EIGHT);
		result[XmlDataValidationConstants.THREE] = (byte) (i);

		return result;
	}

	/**
	 * reads from input stream
	 * 
	 */
	public void readFields(DataInput in) throws IOException {
		DataInputStream dis = (DataInputStream) in;
		numOfViolations = dis.readInt();
		// read string values byte by byte
		byte[] b = new byte[XmlDataValidationConstants.FOUR];
		b[0] = dis.readByte();
		b[1] = dis.readByte();
		b[2] = dis.readByte();
		b[XmlDataValidationConstants.THREE] = dis.readByte();
		int lengthOfByteArray = byteArrayToInt(b);
		byte[] readBytes;
		if (lengthOfByteArray > XmlDataValidationConstants.ONE_ZERO_TWO_FOUR || lengthOfByteArray < XmlDataValidationConstants.ONE) {
			fileName = "skipped";
		} else {
			readBytes = new byte[lengthOfByteArray];
			dis.read(readBytes);
			fileName = new String(readBytes);
		}
	}

	/**
	 * checks filename
	 */
	public int compareTo(XmlFileViolationsWritable arg0) {
		if (this.fileName.equals(arg0.fileName)) {
			return 0;
		} else {
			return -1;
		}
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XmlFileViolationsWritable other = (XmlFileViolationsWritable) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}

	/**
	 * converts to string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"numOfViolations\":").append(numOfViolations).append(",\"fileName\":").append(fileName).append("}");
		return sb.toString();
	}

	/**
	 * Gets the num of violations.
	 *
	 * @return the numOfViolations
	 */
	public Integer getNumOfViolations() {
		return numOfViolations;
	}

	/**
	 * Sets the num of violations.
	 *
	 * @param numOfViolations the numOfViolations to set
	 */
	public void setNumOfViolations(Integer numOfViolations) {
		this.numOfViolations = numOfViolations;
	}

	/**
	 * Gets the file name.
	 *
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 *
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
