package org.jumbune.web.services;

import java.util.HashSet;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;


/**
 * The Class WebServicesBootstrapper. This class registers all the resources/providers({@code org.jumbune.web.services}) when the web container is started.
 */
@ApplicationPath("/apis")
public class WebServicesBootstrapper extends Application {


	@Override
	public Set<Class<?>> getClasses() {
		 final Set<Class<?>> classes = new HashSet<Class<?>>();

		 //for multipart functionality 
		 classes.add(MultiPartFeature.class);
		 
		 //jumbune resources/providers
		 classes.add(ClusterOperationsService.class);
		 classes.add(DVReportService.class);
		 classes.add(GatherScheduledJobResultService.class);
		 classes.add(HomeService.class);
		 classes.add(JobAnalysisService.class);
		 classes.add(ResultService.class);
		 classes.add(SchedulerInfoService.class);
		 classes.add(ValidateService.class);
		 classes.add(XmlDVReportService.class);
		 classes.add(AdminConfigurationService.class);
		 return classes;
	}


	@Override
	public Set<Object> getSingletons() {
		Set<Object> objs = new HashSet<>(1);
		objs.add(new ClusterAnalysisService());
		return objs;
	}

}
