<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta http-equiv="Cache-control" content="no-cache">
	<meta http-equiv="Expires" content="-1">
	<title>Result Analysis</title>

	<!-- Include the meta JSP here. -->
	<jsp:include page="Meta.jsp" />	

	<!-- <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/skins/leap_style2.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/skins/fonts-min.css">
	<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/jquery.treeview.css" />
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/slideshow.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/ui.jqgrid.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/jquery-ui-1.8.17.custom.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/htf_style.css">
	
	<script src="${pageContext.servletContext.contextPath}/js/jquery-1.7.1.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/grid.locale-en.js"></script>	
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/jquery.jqGrid.min.js"></script> -->
	<!-- <script type="text/javascript">  
        $(document).ready(function(){      

		  $(".content").hide();

		  $(".heading").click(function()
		  {
		    $(this).next(".content").slideToggle(500);
		  });



        });
        $(function() {
    		
    		$('.info-image').hover(
    			function() {				
    				var tooltip = $(this).closest('.field').find('.info-message');
    				var pos = $(this).position(); 
    				tooltip.css({ 'top':(pos.top+27)+'px', 'left':(pos.left-90)+'px' });
    				tooltip.show();
    			}, 
    			function() {
    				$(this).closest('.field').find('.info-message').hide();
    			}
    		);
    	});
    </script>   -->    
	
</head>

<body onload="clearForm()">
	<div align="center">
			<div id="wrap">
				<table height="100%" width="100%" border="1" cellpadding="0" cellspacing="0">
					<tbody>
						<tr height="4%">
	   						<td colspan="2" align="left">
	   							<div id="header-inside">
									<div id="logo" style="font-size:45px; font-family:tahoma; color:white; padding-top:25px; padding-left:20px;">
											Hadoop Testing Framework 
									</div>
								</div>	
								
						  </td>
				       </tr>
				       <tr height="94%">
	    					<td valign="top" width="100%">    
	         					
	         					<!-- Start of HTF code --> 
	         					
	         			
	         							<!-- Display of log churning starts here --> 
										<br/>
										<div class='field'>
											<h3>Cluster-wide MapReduce Job Execution Analysis(Debug) Report:<img src='skins/images/info.png' class='info-image'/></h3>
											<div class='info-message' style='height:35px; width:160px;'>
												<div class='speech-icon'></div><p>This is the report which contains cluster-wide MapReduce job execution analysis.</p>
											</div>
										</div>
										
										
													
										<%  
											Map logMap=(Map)request.getAttribute("logMap");
											if(logMap!=null){
										%>
												<div id="ptreegrid"></div>
												<script type="text/javascript">
												 $('<table id="treegrid"></table>').appendTo('#ptreegrid');
													var logChurningData=[];					
												</script>
											
		         									
												
												<% 	
													Iterator it = logMap.entrySet().iterator();
													Map jobMap = null;
													String jobId=null;
													int totalInputKeys = 0;
													int totalContextWrites = 0;
													int totalUnmatchedKeys = 0;
													int totalUnmatchedValues = 0;
													Map childJobMap=null;
													int numOfJobs=0;
													while (it.hasNext()) {
														numOfJobs++;
														Map.Entry mapPairs = (Map.Entry) it.next();
														jobId = (String) mapPairs.getKey();
														jobMap = (Map) mapPairs.getValue();
														totalInputKeys = ((Double) jobMap.get("totalInputKeys")).intValue();
														totalContextWrites =((Double) jobMap.get("totalContextWrites")).intValue();
														totalUnmatchedKeys = ((Double) jobMap.get("totalUnmatchedKeys")).intValue();
														totalUnmatchedValues = ((Double) jobMap.get("totalUnmatchedValues")).intValue();
														childJobMap = (Map) jobMap.get("childJobMap");
												%>
													
														<script type="text/javascript">
															var numOfJobs=<%=numOfJobs%>;
															var jobIdvar="<%=jobId%>";
															var totalInputKeys=<%=totalInputKeys%>;
															var totalContextWrites=<%=totalContextWrites%>;
															var totalUnmatchedKeys=<%=totalUnmatchedKeys%>;
															var totalUnmatchedValues=<%=totalUnmatchedValues%>;
															
															var logChurningJsonObj = { "id":numOfJobs,"elementName":jobIdvar,"totalInputKeys":totalInputKeys,"totalContextWrites":totalContextWrites,"totalUnmatchedKeys":totalUnmatchedKeys,"totalUnmatchedValues":totalUnmatchedValues,level:"0", parent:"", isLeaf:false, expanded:false, loaded:true};
															logChurningData.push(logChurningJsonObj); 
														</script>
														
														
																<%
																	if(childJobMap!=null){
																		Iterator it2 = childJobMap.entrySet().iterator();
																		Map childClassMap = null;
																		Map classMap=null;
																		String className=null;
																		int totalClassInputKeys = 0;
																		int totalClassContextWrites = 0;
																		int totalClassUnmatchedKeys = 0;
																		int totalClassUnmatchedValues = 0;
																		int numOfClass=0;
																		while (it2.hasNext()) {
																			numOfClass++;
																			Map.Entry classMapPairs = (Map.Entry) it2.next();
																			className = (String) classMapPairs.getKey();
																			classMap = (Map) classMapPairs.getValue();
																			if(classMap!=null){									
																			
																				totalClassInputKeys = ((Double) classMap.get("totalInputKeys")).intValue();
																				totalClassContextWrites =((Double) classMap.get("totalContextWrites")).intValue();
																				totalClassUnmatchedKeys = ((Double) classMap.get("totalUnmatchedKeys")).intValue();
																				totalClassUnmatchedValues = ((Double) classMap.get("totalUnmatchedValues")).intValue();
																				childClassMap = (Map) classMap.get("childClassMap");
																		%>
																			<script type="text/javascript">
																				var numOfClass=<%=numOfClass%>;
																				var classNamevar="<%=className%>";
																				var totalClassInputKeys=<%=totalClassInputKeys%>;
																				var totalClassContextWrites=<%=totalClassContextWrites%>;
																				var totalClassUnmatchedKeys=<%=totalClassUnmatchedKeys%>;
																				var totalClassUnmatchedValues=<%=totalClassUnmatchedValues%>;
																				
																				var classJsonObj = { "id":numOfJobs+'_'+numOfClass,"elementName":classNamevar,"totalInputKeys":totalClassInputKeys,"totalContextWrites":totalClassContextWrites,"totalUnmatchedKeys":totalClassUnmatchedKeys,"totalUnmatchedValues":totalClassUnmatchedValues,level:"1", parent:numOfJobs, isLeaf:false, expanded:false, loaded:true};
																				logChurningData.push(classJsonObj); 
																			</script>
																				
																				
																						<%
																							if(childClassMap!=null){
																								Iterator it3 = childClassMap.entrySet().iterator();
																								Map childNodeMap = null;
																								Map nodeMap=null;
																								String nodeName=null;
																								int totalNodeInputKeys = 0;
																								int totalNodeContextWrites = 0;
																								int totalNodeUnmatchedKeys = 0;
																								int totalNodeUnmatchedValues = 0;
																								int numOfNodes=0;
																								while (it3.hasNext()) {
																									numOfNodes++;
																									Map.Entry nodeMapPairs = (Map.Entry) it3.next();
																									nodeName = (String) nodeMapPairs.getKey();
																									nodeMap = (Map) nodeMapPairs.getValue();
																									if(nodeMap!=null){									
																									
																										totalNodeInputKeys = ((Double) nodeMap.get("totalInputKeys")).intValue();
																										totalNodeContextWrites =((Double) nodeMap.get("totalContextWrites")).intValue();
																										totalNodeUnmatchedKeys = ((Double) nodeMap.get("totalUnmatchedKeys")).intValue();
																										totalNodeUnmatchedValues = ((Double) nodeMap.get("totalUnmatchedValues")).intValue();
																										childNodeMap = (Map) nodeMap.get("childNodeMap");
																								%>
																											<script type="text/javascript">
																												var numOfNodes=<%=numOfNodes%>;
																												var nodeNamevar="<%=nodeName%>";
																												var totalNodeInputKeys=<%=totalNodeInputKeys%>;
																												var totalNodeContextWrites=<%=totalNodeContextWrites%>;
																												var totalNodeUnmatchedKeys=<%=totalNodeUnmatchedKeys%>;
																												var totalNodeUnmatchedValues=<%=totalNodeUnmatchedValues%>;
																												
																												var nodeJsonObj = { "id":numOfJobs+'_'+numOfClass+'_'+numOfNodes,"elementName":nodeNamevar,"totalInputKeys":totalNodeInputKeys,"totalContextWrites":totalNodeContextWrites,"totalUnmatchedKeys":totalNodeUnmatchedKeys,"totalUnmatchedValues":totalNodeUnmatchedValues,level:"2", parent:numOfJobs+'_'+numOfClass, isLeaf:false, expanded:false, loaded:true};
																												logChurningData.push(nodeJsonObj); 
																											</script>
																										
																										
																												<%
																													if(childNodeMap!=null){
																														Iterator it4 = childNodeMap.entrySet().iterator();
																														Map child2InstanceMap = null;
																														Map instanceMap=null;
																														String instanceName=null;
																														int totalInstanceInputKeys = 0;
																														int totalInstanceContextWrites = 0;
																														int totalInstanceUnmatchedKeys = 0;
																														int totalInstanceUnmatchedValues = 0;
																														int numOfInstance=0;
																														while (it4.hasNext()) {
																															numOfInstance++;
																															Map.Entry instanceMapPairs = (Map.Entry) it4.next();
																															instanceName = (String) instanceMapPairs.getKey();
																															instanceMap = (Map) instanceMapPairs.getValue();
																															if(instanceMap!=null){									
																															
																																totalInstanceInputKeys = ((Double) instanceMap.get("totalInputKeys")).intValue();
																																totalInstanceContextWrites =((Double) instanceMap.get("totalContextWrites")).intValue();
																																totalInstanceUnmatchedKeys = ((Double) instanceMap.get("totalUnmatchedKeys")).intValue();
																																totalInstanceUnmatchedValues = ((Double) instanceMap.get("totalUnmatchedValues")).intValue();
																																child2InstanceMap = (Map) instanceMap.get("child2InstanceMap");
																														%>
																															
																																		<script type="text/javascript">
																																			var numOfInstance=<%=numOfInstance%>;
																																			var instanceNamevar="<%=instanceName%>";
																																			var totalInstanceInputKeys=<%=totalInstanceInputKeys%>;
																																			var totalInstanceContextWrites=<%=totalInstanceContextWrites%>;
																																			var totalInstanceUnmatchedKeys=<%=totalInstanceUnmatchedKeys%>;
																																			var totalInstanceUnmatchedValues=<%=totalInstanceUnmatchedValues%>;
																																			
																																			var instanceJsonObj = { "id":numOfJobs+'_'+numOfClass+'_'+numOfNodes+'_'+numOfInstance,"elementName":instanceNamevar,"totalInputKeys":totalInstanceInputKeys,"totalContextWrites":totalInstanceContextWrites,"totalUnmatchedKeys":totalInstanceUnmatchedKeys,"totalUnmatchedValues":totalInstanceUnmatchedValues,level:"3", parent:numOfJobs+'_'+numOfClass+'_'+numOfNodes, isLeaf:false, expanded:false, loaded:true};
																																			logChurningData.push(instanceJsonObj); 
																																		</script>
																																
																																		<%
																																			if(child2InstanceMap!=null){
																																				Iterator it5 = child2InstanceMap.entrySet().iterator();
																																				Map counterMap=null;
																																				String counterName=null;
																																				int totalCounterInputKeys = 0;
																																				int totalCounterContextWrites = 0;
																																				int totalCounterUnmatchedKeys = 0;
																																				int totalCounterUnmatchedValues = 0;
																																				int totalFilteredIn = 0;
																																				int totalFilteredOut=0;
																																				int numOfCounters=0;
																																				while (it5.hasNext()) {
																																					numOfCounters++;
																																					Map.Entry counterMapPairs = (Map.Entry) it5.next();
																																					counterName = (String) counterMapPairs.getKey();
																																					counterMap = (Map) counterMapPairs.getValue();
																																					if(counterMap!=null){									
																																					
																																						totalCounterInputKeys = ((Double) counterMap.get("totalInputkeys")).intValue();
																																						totalCounterContextWrites =((Double) counterMap.get("totalContextWrites")).intValue();
																																						totalCounterUnmatchedKeys = ((Double) counterMap.get("totalUnmatchedKeys")).intValue();
																																						totalCounterUnmatchedValues = ((Double) counterMap.get("totalUnmatchedValues")).intValue();
																																						totalFilteredIn = ((Double) counterMap.get("totalFilteredIn")).intValue();
																																						totalFilteredOut = ((Double) counterMap.get("totalFilteredOut")).intValue();
																																						if(!counterName.contains("contextWrite")){
																																				%>																																			
																																						
																																							
																																					<%}else{
																																						
																																					%>
																																							
																																							
																																					<% }
																																					%>																																													
																																																											
																																																														
																																																																
																																																																	
																																																																		
																																						
																																							
																																			
																																		<%
																																					}
																																				}
																																			}%>		
																																			
																												
																													
																												<%
																															}
																														}
																													}%>		
																													
																												
																						<%
																									}
																								}
																							}
																						%>		
																							
																<%
																				
																			}
																		}
																	}
																%>													
																	
													<%}%>
												<script type="text/javascript">
												var logAnalysisJson={
			         								    "response": logChurningData},
			         								    grid;    								    
			         								grid = jQuery("#treegrid");
			         								grid.jqGrid({
			         								    datastr: logAnalysisJson,
			         								    datatype: "jsonstring",
			         								    height: "auto",
			         								    loadui: "disable",
			         								  	colNames:["Name","Total Input keys","Total Context Writes","Total Unmatched keys", "Total Unmatched Values"], 
														colModel:[ 
																	{name:'elementName',index:'elementName', width:450},
																	{name:'totalInputKeys',index:'totalInputKeys', width:180,align:"right"}, 
																	{name:'totalContextWrites',index:'totalContextWrites', width:180,align:"right"},
																	{name:'totalUnmatchedKeys',index:'totalUnmatchedKeys', width:180,align:"right"},																	
																	{name:'totalUnmatchedValues',index:'totalUnmatchedValues', width:180,align:"right"},
																	
														],												
			         								    treeGrid: true,
			         								    treeGridModel: "adjacency",
			         								    caption: "Log Analysis Report",
			         								    ExpandColumn: "elementName",
			         								    //autowidth: true,
			         								    rowNum: 10000,
			         								    //ExpandColClick: true,
			         								    treeIcons: {leaf:'ui-icon-document-b'},
			         								    jsonReader: {
			         								        repeatitems: false,
			         								        root: "response"
			         								    }
			         								});

								                
												 </script>
												
	         							
	         												
	    
											
												
												
											<%}else{%>	
												<div style="color:'red'">
	         										<h4>Data Not Found!!</h4>
	         									</div>												
												
											<%}
											%>
												
						
										
										<!-- Display of log churning ends here -->
	         							<br/><br/>
	         					
	         								         							
	         					 
			         									         							
			         							
			         							
			         							<!-- Display of profiling counters starts here -->	         								         												
			         							<div class='field'>
													<h3>CPU Samples and Heap Sites Allocation Stats Report (by JVMTI HProof) :<img src='skins/images/info.png' class='info-image'/></h3>
													<div class='info-message' style='height:30px; width:160px;'>
														<div class='speech-icon'></div><p>This report shows CPU samples and Heap Allocation stats for various tasks.</p>
													</div>
												</div>					
			         												
			         												
			         							<br/>
												<%  
													Map profilingCountersMap=(Map)request.getAttribute("profilingMap");
													if(profilingCountersMap!=null){
												%>	
														
														<% 	
															Iterator iter = profilingCountersMap.entrySet().iterator();
															Map taskCounterMap = null;
															String taskName=null;	
															int rowCounter=0;
															while (iter.hasNext()) {
																Map.Entry profilingCounterMapPairs = (Map.Entry) iter.next();
																taskName = (String) profilingCounterMapPairs.getKey();
																taskCounterMap = (Map) profilingCounterMapPairs.getValue();
																String cpuSampleTableId="cpuSample"+rowCounter;
																String cpuSampleDivId="cpuSamplePager"+rowCounter;
																String heapAllocationTableId="heapAllocation"+rowCounter;
																String heapSampleDivId="heapAllocationPager"+rowCounter;
														%>													
														
																<table id=<%=cpuSampleTableId %>></table> <div id=<%=cpuSampleDivId %>></div>
																<br/>
																<table id=<%=heapAllocationTableId %>></table> <div id=<%=heapSampleDivId %>></div>
																<br/>
																<br/>
															        
												            	<% 	
												            		if(taskCounterMap!=null){						            			
													            	Iterator iter2 = taskCounterMap.entrySet().iterator();
																	String tableName=null;													
																	Map cpuSampleMap =null;
																	while (iter2.hasNext()) {
																		Map.Entry taskCounterMapPairs = (Map.Entry) iter2.next();
																		tableName = (String) taskCounterMapPairs.getKey();
																		cpuSampleMap = (Map) taskCounterMapPairs.getValue();			
																		if(tableName.equalsIgnoreCase("cpuSample")){
																			%>
																			
																			<script type="text/javascript"> 
																				var rowCounter=<%=rowCounter%>;
																				var taskName="<%=taskName%>";
																				var cpuSampleData=[];
																					jQuery("#cpuSample"+rowCounter).jqGrid({ 
																						datastr: cpuSampleData,
																					    datatype: "local",
																					    colNames:['Rank','Percentage','Count','Method Name'], 
																					    colModel:[ 
																									{name:'rank',index:'rank', width:100}, 
																									{name:'percentage',index:'percentage', width:170, align:"right"},
																									{name:'count',index:'count', width:100, align:"right"},
																									{name:'methodName',index:'methodName', width:650}
																						], 
																						rowNum:10,
																						hiddengrid:true,
																						pager: '#cpuSamplePager'+rowCounter, 
																						viewrecords: true, 
																						caption:'CPU Samples Stats (by JVMTI HProof) - '+taskName
																					}); 
																					jQuery("#cpuSample"+rowCounter).jqGrid('navGrid','#cpuSamplePager'+rowCounter,{edit:false,add:false,del:false});	
																					</script>
		
																				<%
																						Iterator iter3 = cpuSampleMap.entrySet().iterator();
																						String rankId=null;													
																						Map rankMap =null;
																						while (iter3.hasNext()) {
																							Map.Entry rankMapPairs = (Map.Entry) iter3.next();
																							rankId = (String) rankMapPairs.getKey();
																							rankMap = (Map) rankMapPairs.getValue();																	
															            	
															            				if(rankMap!=null){
															            					double percentageValue=(Double)rankMap.get("selfPercentage");
															            					int countValue=((Double)rankMap.get("count")).intValue();
															            					String qualifiedMethod=(String)rankMap.get("qualifiedMethod");
															            													            			
															            			%>
															            				<script type="text/javascript">
															            					var rankId=<%=rankId%>;
															            					var percentageValue=<%=percentageValue%>;
															            					percentageValue=percentageValue+"%";
															            					var countValue=<%=countValue%>;
															            					var qualifiedMethod="<%=qualifiedMethod%>";
															            					var cpuSampleJsonObj = {"rank":rankId,"percentage":percentageValue,"count":countValue,"methodName":qualifiedMethod};
															            					cpuSampleData.push(cpuSampleJsonObj);
															            				</script>
															            			
															            			<%}
																					}
															            		  %>
																					<script type="text/javascript">
												            						//alert("row counter value before for loop:"+rowCounter);
												            						//alert("cpuSampleData.length:"+cpuSampleData.length);
																						for(var x=0; x<cpuSampleData.length; x++){
																		                    $("#cpuSample"+rowCounter).jqGrid('addRowData', x+1, cpuSampleData[x]);
																		                
																		                }
																					</script>
																					
																			
																
															<%}else if(tableName.equalsIgnoreCase("heapAllocation")){
																
																%>
												            	<script type="text/javascript"> 
																var rowCounter=<%=rowCounter%>;
																var taskName="<%=taskName%>";
																var heapAllocationData=[];
																	jQuery("#heapAllocation"+rowCounter).jqGrid({ 
																		datastr: heapAllocationData,
																	    datatype: "local",
																	    colNames:['Rank','Bytes Allocated','Live Bytes','Live Instances','Instances Allowed','Class Name','StackTraceList'], 
																	    colModel:[ 
																					{name:'rank',index:'rank', width:100},																					
																					{name:'bytesAllocated',index:'bytesAllocated', width:120, align:"right"},
																					{name:'liveBytes',index:'liveBytes', width:120, align:"right"},
																					{name:'liveInstances',index:'liveInstances', width:120, align:"right"},
																					{name:'instanceAllocated',index:'instanceAllocated', width:140, align:"right"},
																					{name:'className',index:'className', width:120, align:"right"},
																					{name:'stackTraceList',index:'stackTraceList', width:650,formatter:function(cell,options,row){
																						 var traceList="";
																						 traceList =row.stackTraceList.toString();
																						 traceListArray=traceList.split("[");
																						 traceListArraytwo=traceListArray[1].split("]");
																						 traceListArrayThree=traceListArraytwo[0].split(",");
																						 var value;
																						 var tarceListStringElement="";
																						 for(var traceString in traceListArrayThree ){
																							 value= traceListArrayThree[traceString];
																							 	if(typeof value == "undefined"){
																							 		value = " ";
																								}
																								tarceListStringElement+="<div>&nbsp;"+value+"</div>";
																							}
																						 if(typeof traceList == "undefined"){
																							 traceList = " ";
																							}
																						 	
																							return tarceListStringElement;
		
																						
																					}}
																		], 
																		rowNum:10, 
																		hiddengrid:true,
																		pager: '#heapAllocationPager'+rowCounter, 
																		viewrecords: true, 
																		caption:'Heap Sites Allocation Stats (by JVMTI HProof) - '+taskName
																	}); 
																	jQuery("#heapAllocation"+rowCounter).jqGrid('navGrid','#heapAllocationPager'+rowCounter,{edit:false,add:false,del:false});	
																</script>
										            			<%	
																
																Iterator iter3 = cpuSampleMap.entrySet().iterator();
																String rankId=null;													
																Map rankMap =null;
																while (iter3.hasNext()) {
																	Map.Entry rankMapPairs = (Map.Entry) iter3.next();
																	rankId = (String) rankMapPairs.getKey();
																	rankMap = (Map) rankMapPairs.getValue();																	
									            	
									            				if(rankMap!=null){
									            					Map heapAllocSiteBeanMap=(Map)rankMap.get("heapAllocSiteBean");
									            					List stackTraceList=(List)rankMap.get("stackTraceList");
									            					String stackTraceListString=stackTraceList.toString();
									            					if(heapAllocSiteBeanMap!=null){
									            						String className=(String)heapAllocSiteBeanMap.get("className");
									            						int bytesAllocated=((Double)heapAllocSiteBeanMap.get("bytesAllocated")).intValue();
									            						int liveBytes=((Double)heapAllocSiteBeanMap.get("liveBytes")).intValue();
									            						int liveInstances=((Double)heapAllocSiteBeanMap.get("liveInstances")).intValue();
									            						int instanceAllocated=((Double)heapAllocSiteBeanMap.get("instanceAllocated")).intValue();
									            													            			
									            			%>
																	<script type="text/javascript">
										            					var rankId=<%=rankId%>;
										            					var className="<%=className%>";
										            					var bytesAllocated=<%=bytesAllocated%>;
										            					var liveBytes="<%=liveBytes%>";
										            					var liveInstances="<%=liveInstances%>";
										            					var instanceAllocated="<%=instanceAllocated%>";
										            					var stackTraceListString="<%=stackTraceListString%>";								            					
										            					var heapAllocationJsonObj = {"rank":rankId,"liveBytes":liveBytes,"liveInstances":liveInstances,"instanceAllocated":instanceAllocated,"bytesAllocated":bytesAllocated,"className":className,"stackTraceList":stackTraceListString};
										            					heapAllocationData.push(heapAllocationJsonObj);
										            				</script>							            			
									            			
									            			
									            			<%}
									            			}
														  }
									            		  %>
									            		       	
									            				<script type="text/javascript">
								            						//alert("row counter value before for loop:"+rowCounter);
								            						//alert("heapAllocationData.length:"+heapAllocationData.length);
																		for(var y=0; y<cpuSampleData.length; y++){
														                    $("#heapAllocation"+rowCounter).jqGrid('addRowData', y+1, heapAllocationData[y]);
														                
														                }
																</script>
									            	
													        
																
															<%}
																		
															}%>
															
															
														<%}
												           rowCounter++;
														}%>
														
														
													<%}else{
															String profilingEnabled=(String)request.getAttribute("profilingEnabled");	
															if(profilingEnabled.equalsIgnoreCase("true")){
															%>	
																<div style= "color:'red'">
					         										<h4>Data Not Found!!</h4>
					         									</div>		
															<%}
															else{
																%>	
																<div style= "color:'red'">
					         										<h4>Profiling not Enabled</h4>
					         									</div>	
															<%}
																									
														
													}%>
			         								
												       
												     		
			         												
			         							
			         							<!-- Display of profiling counters ends here -->    							
			         							
	         								<!-- Display of pure jar counters starts here -->
			         					
			         					<div class='field'>
											<h3>Un-Instrumented Jar Execution Statistics Report:<img src='skins/images/info.png' class='info-image'/></h3>
											<div class='info-message' style='height:40px; width:160px;'>
												<div class='speech-icon'></div><p>This report shows all the counters genreated from the jar without any intsumentation process on it.</p>
											</div>
										</div>
			         					
			         					<div >					
	         							<br/>
										
										<%  
											String pureJarCountersJSON=(String)request.getAttribute("pureJarCountersJSON");
											if(pureJarCountersJSON!=null){
												Map pureJarCountersMap=(Map)request.getAttribute("pureJarCountersMap");
												int totalJobs=pureJarCountersMap.size();
												for(int ctr=0;ctr<totalJobs;ctr++){
													String tableId="list"+ctr;
													String divId="pager"+ctr;
												%>	
													<table id=<%=tableId %>></table> <div id=<%=divId %>></div>
													<br/>
												<%}
										%>	
												<script type="text/javascript"> 
													
													var rowCount=0;
													var pureJarJSONString=<%=pureJarCountersJSON %>;
													 $.each(pureJarJSONString, function(j, level0){
														 var myData=[];
														 
														 $.each(level0, function(i, level1){														 
															 var jsonObj = {"counterName":i,"counterValue":level1};
															 myData.push(jsonObj);
														 });								 
																											
														jQuery("#list"+rowCount).jqGrid({ 
															datastr: myData,
														    datatype: "local",
														    colNames:['Counter Name','Counter Value'], 
														    colModel:[ 
																		{name:'counterName',index:'counterName', width:150}, 
																		{name:'counterValue',index:'counterValue', width:150, align:"right"},
															], 
															rowNum:10, 
															pager: '#pager'+rowCount, 
															viewrecords: true, 
															caption:'Un-Instrumented Jar Execution Statistics - '+j
														}); 
														jQuery("#list"+rowCount).jqGrid('navGrid','#pager'+rowCount,{edit:false,add:false,del:false});	
														jQuery("#list"+rowCount).setGridWidth(500);
														for(var i=0; i<myData.length; i++){
															//alert("rowdata value: "+myData[i]);
										                    $("#list"+rowCount).jqGrid('addRowData', i+1, myData[i]);
										                
										                }
														rowCount++;
													 });						
													 



												</script>
												
												
												
											<%}else{%>	
												<div style= "color:red">
	         										<h4>Data Not Found!!</h4>
	         									</div>												
												
											<%}%>
										       										     		
	         											
	         							</div>	         							
	         							<!-- Display of pure jar counters ends here -->    
	         						
	         								
	         								
	         								
	         								
	         								
	         									         								
																						
											<br/><br/>
											<%  
											long executionTime=(Long)request.getAttribute("totalExecutionTime");
										%>
										<div style="color:#009900">
	         								<h4>Total time taken to complete the process: <%=executionTime%> ms</h4>
	         							</div>	
																					
										
							
							
							
									<!-- Start of HTF code -->
	         					
	         					
							</td>
						</tr>						
					
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>