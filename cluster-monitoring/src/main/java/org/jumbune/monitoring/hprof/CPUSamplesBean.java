package org.jumbune.monitoring.hprof;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class CPUSamplesBean is a bean class for storing the total cpu samples count and list of Sample Descriptor.
 */
public class CPUSamplesBean {

	private int totalSamplesCount;
	private List<SampleDescriptor> sampleDescriptorList;

	/**
	 * Instantiates a new CPU samples bean.
	 */
	public CPUSamplesBean() {
		sampleDescriptorList = new LinkedList<SampleDescriptor>();
	}

	/**
	 * @return the totalSamplesCount
	 */
	public int getTotalSamplesCount() {
		return totalSamplesCount;
	}

	/**
	 * Sets the total samples count.
	 *
	 * @param totalSamplesCount the totalSamplesCount to set
	 */
	public void setTotalSamplesCount(int totalSamplesCount) {
		this.totalSamplesCount = totalSamplesCount;
	}

	/**
	 * Gets the sample descriptor list.
	 *
	 * @return the sampleDescriptor
	 */
	public List<SampleDescriptor> getSampleDescriptorList() {
		return sampleDescriptorList;
	}

	/**
	 * Sets the sample descriptor list.
	 *
	 * @param sampleDescriptor the sampleDescriptor to set
	 */
	public void setSampleDescriptorList(List<SampleDescriptor> sampleDescriptor) {
		this.sampleDescriptorList = sampleDescriptor;
	}

	/**
	 * Adds the descriptor to list.
	 *
	 * @param sampleDescriptor the sample descriptor
	 */
	public void addDescriptorToList(SampleDescriptor sampleDescriptor) {
		this.sampleDescriptorList.add(sampleDescriptor);
	}

	/**
	 * The Class SampleDescriptor is a pojo class which stores the self percentage,count and qualified method of the Cpu.
	 */
	public static final class SampleDescriptor {

		private float selfPercentage;
		private int count;
		private String qualifiedMethod;

		/**
		 * @return the selfPercentage
		 * 
		 */
		public float getSelfPercentage() {
			return selfPercentage;
		}

		/**
		 * @param selfPercentage
		 *            the selfPercentage to set
		 */
		public void setSelfPercentage(float selfPercentage) {
			String percentagePattern = "###.##";
			DecimalFormat deciFormat = new DecimalFormat(percentagePattern);

			this.selfPercentage = Float.valueOf(deciFormat.format(selfPercentage));
		}

		/**
		 * @return the count
		 */
		public int getCount() {
			return count;
		}

		/**
		 * @param count
		 *            the count to set
		 */
		public void setCount(int count) {
			this.count = count;
		}

		/**
		 * @return the qualifiedMethod
		 */
		public String getQualifiedMethod() {
			return qualifiedMethod;
		}

		/**
		 * @param qualifiedMethod
		 *            the qualifiedMethod to set
		 */
		public void setQualifiedMethod(String qualifiedMethod) {
			this.qualifiedMethod = qualifiedMethod;
		}

	}

	/**
	 * This method provides string implementation of the object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Total Samples Count:" + this.getTotalSamplesCount() + "\n").append("---CPU Samples---\n");
		List<SampleDescriptor> descriptorList = this.getSampleDescriptorList();
		for (SampleDescriptor descriptor : descriptorList) {
			buffer.append("Count: " + descriptor.getCount() + "  Method:" + descriptor.getQualifiedMethod() + "\n");
		}
		return new String(buffer);
	}
}
