package org.jumbune.monitoring.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jumbune.monitoring.hprof.HeapAllocSitesBean;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * This class is used to filter out fields while creating json string
 */
public class HeapAllocStackTraceExclStrat implements ExclusionStrategy {
	/**
	 * This is the list of fields that should be excluded for creating a Json
	 * string of a particular class. Add other non required fields in this list.
	 */
	private static final String EXCLUDED_FIELD_LIST = "stackTraceId";

	/** The LOGGER. */
	private static final Logger LOGGER = LogManager
			.getLogger(HeapAllocStackTraceExclStrat.class);

	/**
	 * This method will not filter out any class.
	 */
	@Override
	public boolean shouldSkipClass(Class<?> arg0) {
		return false;
	}

	/**
	 * This API will filter out field "stackTraceId" of HeapAllocSitesBean class
	 * so that it doesn't get printed in json string.
	 */
	@Override
	public boolean shouldSkipField(final FieldAttributes fieldAttrib) {

		if (fieldAttrib.getDeclaringClass().equals(
				HeapAllocSitesBean.SiteDescriptor.class)) {
			try {
				if (EXCLUDED_FIELD_LIST.equals(fieldAttrib.getName())) {
					return true;
				}
			} catch (final SecurityException e) {
				LOGGER.error(e.getMessage());
				return false;
			}
		}
		return false;
	}

}
