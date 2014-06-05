package org.jumbune.profiling.hprof;

import java.util.LinkedList;
import java.util.List;

import org.jumbune.profiling.utils.ProfilerConstants;


/**
 * The Class HeapAllocSitesBean is a bean class stores the total bytes,total instances,total stack trace of the particular class.
 */
public class HeapAllocSitesBean {
	private float cutOffRatio;
	private int totalLiveBytes;
	private int totalLiveInstances;
	private Long totalByteAllocated;
	private Long totalInstancesAllocated;
	private int noOfSites;
	private List<SiteDescriptor> siteDetails;

	/**
	 * Instantiates a new heap alloc sites bean.
	 */
	HeapAllocSitesBean() {
		siteDetails = new LinkedList<SiteDescriptor>();
	}

	/**
	 * The Class SiteDescriptor is used for allocating bytes,instances and stack trace of the particular class.
	 */
	public static final class SiteDescriptor {
		private float liveBytes;
		private float bytesAllocated;
		private int liveInstances;
		private int instanceAllocated;
		private String className;
		private int stackTraceId;

		/**
		 * @return the liveBytes
		 */
		public float getLiveBytes() {
			return liveBytes;
		}

		/**
		 * @param liveBytes
		 *            the liveBytes to set is converted in KiloBytes
		 */
		public void setLiveBytes(float liveBytes) {
			float liveBytesTmp = liveBytes;
			liveBytesTmp = liveBytesTmp / (float) ProfilerConstants.ONE_ZERO_TWO_FOUR;
			this.liveBytes = liveBytesTmp;
		}

		/**
		 * @return the liveInstances
		 */
		public int getLiveInstances() {
			return liveInstances;
		}

		/**
		 * @param liveInstances
		 *            the liveInstances to set
		 */
		public void setLiveInstances(int liveInstances) {
			this.liveInstances = liveInstances;
		}

		/**
		 * @return the bytesAllocated
		 */
		public float getBytesAllocated() {
			return bytesAllocated;
		}

		/**
		 * @param bytesAllocated
		 *            the bytesAllocated to set is converted in KiloBytes
		 */
		public void setBytesAllocated(float bytesAllocated) {
			float bytesAllocatedTmp = bytesAllocated;
			bytesAllocatedTmp = bytesAllocatedTmp / (float) ProfilerConstants.ONE_ZERO_TWO_FOUR;
			this.bytesAllocated = bytesAllocatedTmp;
		}

		/**
		 * @return the instanceAllocated
		 */
		public int getInstanceAllocated() {
			return instanceAllocated;
		}

		/**
		 * @param instanceAllocated
		 *            the instanceAllocated to set
		 */
		public void setInstanceAllocated(int instanceAllocated) {
			this.instanceAllocated = instanceAllocated;
		}

		/**
		 * @return the className
		 */
		public String getClassName() {
			return className;
		}

		/**
		 * @param className
		 *            the className to set
		 */
		public void setClassName(String className) {
			this.className = className;
		}

		/**
		 * @return the stackTrace
		 */
		public int getStackTraceId() {
			return stackTraceId;
		}

		/**
		 * @param stackTraceId
		 *            the stackTrace to set
		 */
		public void setStackTraceId(int stackTraceId) {
			this.stackTraceId = stackTraceId;
		}

	}

	/**
	 * @return the cutOffRatio
	 */
	public float getCutOffRatio() {
		return cutOffRatio;
	}

	/**
	 * @param cutOffRatio
	 *            the cutOffRatio to set
	 */
	public void setCutOffRatio(float cutOffRatio) {
		this.cutOffRatio = cutOffRatio;
	}

	/**
	 * @return the totalLiveBytes
	 */
	public int getTotalLiveBytes() {
		return totalLiveBytes;
	}

	/**
	 * @param totalLiveBytes
	 *            the totalLiveBytes to set
	 */
	public void setTotalLiveBytes(int totalLiveBytes) {
		this.totalLiveBytes = totalLiveBytes;
	}

	/**
	 * @return the totalLiveInstances
	 */
	public int getTotalLiveInstances() {
		return totalLiveInstances;
	}

	/**
	 * @param totalLiveInstances
	 *            the totalLiveInstances to set
	 */
	public void setTotalLiveInstances(int totalLiveInstances) {
		this.totalLiveInstances = totalLiveInstances;
	}

	/**
	 * @return the totalByteAllocated
	 */
	public Long getTotalByteAllocated() {
		return totalByteAllocated;
	}

	/**
	 * @param totalByteAllocated
	 *            the totalByteAllocated to set
	 */
	public void setTotalByteAllocated(Long totalByteAllocated) {
		this.totalByteAllocated = totalByteAllocated;
	}

	/**
	 * @return the totalInstancesAllocated
	 */
	public Long getTotalInstancesAllocated() {
		return totalInstancesAllocated;
	}

	/**
	 * @param totalInstancesAllocated
	 *            the totalInstancesAllocated to set
	 */
	public void setTotalInstancesAllocated(Long totalInstancesAllocated) {
		this.totalInstancesAllocated = totalInstancesAllocated;
	}

	/**
	 * @return the noOfSites
	 */
	public int getNoOfSites() {
		return noOfSites;
	}

	/**
	 * @param noOfSites
	 *            the noOfSites to set
	 */
	public void setNoOfSites(int noOfSites) {
		this.noOfSites = noOfSites;
	}

	/**
	 * @return the siteDetails
	 */
	public List<SiteDescriptor> getSiteDetails() {
		return siteDetails;
	}

	/**
	 * @param siteDetails
	 *            the siteDetails to set
	 */
	public void setSiteDetails(List<SiteDescriptor> siteDetails) {
		this.siteDetails = siteDetails;
	}

	/**
	 * Adds the site details to the SiteDescriptor.
	 *
	 * @param siteDescriptor the site descriptor
	 */
	public void addToSiteDetails(SiteDescriptor siteDescriptor) {
		this.siteDetails.add(siteDescriptor);
	}

	/**
	 * This method provides string implementation of the object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Cut Off Ratio:" + this.getCutOffRatio() + "\n").append("total live bytes:" + this.getTotalLiveBytes() + "\n")
				.append("total live instances:" + this.getTotalLiveInstances() + "\n")
				.append("total bytes allocated:" + this.getTotalByteAllocated() + "\n")
				.append("total instances allocated:" + this.getTotalInstancesAllocated() + "\n")
				.append("No. of sites to follow:" + this.getNoOfSites() + "\n\n").append("---Sites to follow---\n\n");
		List<SiteDescriptor> descriptorList = this.getSiteDetails();
		for (SiteDescriptor descriptor : descriptorList) {
			buffer.append("Class: " + descriptor.getClassName() + " Live Bytes:" + descriptor.getLiveBytes() + " Live Instances:"
					+ descriptor.getLiveInstances() + " Alloc Bytes:" + descriptor.getBytesAllocated() + " Alloc Instances:"
					+ descriptor.getInstanceAllocated() + "\n");
		}
		return new String(buffer);
	}

}