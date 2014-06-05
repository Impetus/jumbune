package org.jumbune.datavalidation;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;


/**
 * The Class FileViolationsWritable is responsible for writing the violations to data output stream.
 */
public class FileViolationsWritable implements WritableComparable<FileViolationsWritable> {

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
	public FileViolationsWritable() {
	}

	/**
	 * Instantiates a new file violations writable.
	 *
	 * @param fileViolationsWritable the file violations writable
	 */
	public FileViolationsWritable(FileViolationsWritable fileViolationsWritable) {
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
		return (b[0] & DataValidationConstants.ZERO_CROSS_FF) << DataValidationConstants.TWENTY_FOUR;
	}

	/**
	 * Expression two.
	 *
	 * @param b the b
	 * @return the int
	 */
	private int expressionTwo(byte[] b) {
		return (b[1] & DataValidationConstants.ZERO_CROSS_FF) << DataValidationConstants.SIXTEEN;
	}

	/**
	 * Expression one.
	 *
	 * @param b the b
	 * @return the int
	 */
	private int expressionOne(byte[] b) {
		return b[DataValidationConstants.THREE] & DataValidationConstants.ZERO_CROSS_FF | (b[2] & DataValidationConstants.ZERO_CROSS_FF) << DataValidationConstants.EIGHT;
	}

	/**
	 * To bytes.
	 *
	 * @param i the i
	 * @return the byte[]
	 */
	private byte[] toBytes(int i) {
		byte[] result = new byte[DataValidationConstants.FOUR];
		result[0] = (byte) (i >> DataValidationConstants.TWENTY_FOUR);
		result[1] = (byte) (i >> DataValidationConstants.SIXTEEN);
		result[2] = (byte) (i >> DataValidationConstants.EIGHT);
		result[DataValidationConstants.THREE] = (byte) (i);

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
		byte[] b = new byte[DataValidationConstants.FOUR];
		b[0] = dis.readByte();
		b[1] = dis.readByte();
		b[2] = dis.readByte();
		b[DataValidationConstants.THREE] = dis.readByte();
		int lengthOfByteArray = byteArrayToInt(b);
		byte[] readBytes;
		if (lengthOfByteArray > DataValidationConstants.ONE_ZERO_TWO_FOUR || lengthOfByteArray < DataValidationConstants.ONE) {
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
	public int compareTo(FileViolationsWritable arg0) {
		if (this.fileName.equals(arg0.fileName)) {
			return 0;
		} else {
			return -1;
		}
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
