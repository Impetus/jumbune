package org.jumbune.web.sockets;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import org.jumbune.web.utils.WebConstants;


public class ModuleBasedWebSocketCreator implements WebSocketCreator{

	private JobAnalysisSocket jobAnalysisSocket;
	private DataAnalysisSocket dataAnalysisSocket;
	
	@Override
	public Object createWebSocket(ServletUpgradeRequest req,
			ServletUpgradeResponse resp) {
		String requestPath = req.getRequestPath();
	    Map<String, List<String>> paramsMap = req.getParameterMap();
	    String jobName = paramsMap.get("jobName").get(0);
		switch(requestPath){
		case WebConstants.JOB_ANALYSIS_SOCKET_URL:{
			jobAnalysisSocket = new JobAnalysisSocket(jobName);
			return jobAnalysisSocket;
		}
		case WebConstants.DATA_ANALYSIS_SOCKET_URL:{
			dataAnalysisSocket = new DataAnalysisSocket();
			return dataAnalysisSocket;
		}
		}
		return null;
	}

}
