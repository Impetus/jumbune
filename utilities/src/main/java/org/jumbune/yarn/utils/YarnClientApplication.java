package org.jumbune.yarn.utils;

import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;

public  class YarnClientApplication {
	  private final GetNewApplicationResponse newAppResponse;
	  private final ApplicationSubmissionContext appSubmissionContext;

	  public YarnClientApplication(GetNewApplicationResponse newAppResponse,
	                               ApplicationSubmissionContext appContext) {
	    this.newAppResponse = newAppResponse;
	    this.appSubmissionContext = appContext;
	  }

	  public GetNewApplicationResponse getNewApplicationResponse() {
	    return newAppResponse;
	  }

	  public ApplicationSubmissionContext getApplicationSubmissionContext() {
	    return appSubmissionContext;
	  }
	}
