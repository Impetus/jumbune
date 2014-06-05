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
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/skins/leap_style2.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/skins/fonts-min.css">
	<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/css/jquery.treeview.css" />
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/slideshow.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/ui.jqgrid.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/jquery-ui-1.8.17.custom.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/htf_style.css">
	<!-- <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>  -->
	<script src="${pageContext.servletContext.contextPath}/js/jquery-1.7.1.min.js" type="text/javascript"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/grid.locale-en.js"></script>	
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/jquery.jqGrid.min.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/jquery-ui-1.8.17.custom.min.js"></script>
	<script>
	$(function() {
		$( "#tabs" ).tabs();
	});
	</script>
	<script type="text/javascript">  
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
        
		  
    </script>    
    <script type="text/javascript">  
        $(document).ready(function(){ 
        	var ctr=0;
        	$(".modal").click(function()        		
  				  {
  			  		var serializedData=$(this).find('form').serialize();			  		
  			  		var  tempName=serializedData.split('=');
  			  		var elementName=tempName[1];
  			  		
		  			if(ctr!=0){
		  			$("#taskCountersTable").jqGrid('clearGridData');
		  			$("#taskCountersTable").jqGrid('setCaption','Task Counter Statistics- '+elementName);
		  			}
  			  		

  			  		var taskCounters=allTaskCounterData[elementName];
  					  jQuery("#taskCountersTable").jqGrid({ 
							datastr: taskCounters,
						    datatype: "local",
						    colNames:['Counter Name','Filtered In','Context Writes','Unmatched Keys','Unmatched Values','Filtered Out'], 
						    colModel:[ 
										{name:'counterName',index:'counterName', width:400},
										{name:'filteredIn',index:'filteredIn', width:120, align:"right"}, 
										{name:'contextWrites',index:'contextWrites', width:120, align:"right"},
										{name:'unmatchedKeys',index:'unmatchedKeys', width:150, align:"right"},
										{name:'unmatchedValues',index:'unmatchedValues', width:150, align:"right"},
										{name:'filteredOut',index:'filteredOut', width:120, align:"right"},
							], 
							height: 'auto', 
							autowidth:true,
							rowNum:1000, 
							pager: '#taskCountersDiv', 
							viewrecords: true, 
							caption:'Task Counter Statistics- '+elementName
						}); 
						jQuery("#taskCountersTable").jqGrid('navGrid','#taskCountersDiv',{edit:false,add:false,del:false});	
						//jQuery("#taskCountersTable").setGridWidth(500);
						for(var b=0; b<taskCounters.length; b++){
		                    $("#taskCountersTable").jqGrid('addRowData', b+1, taskCounters[b]);
		                
		                }
						jQuery('#taskCountersTable').trigger("reloadGrid");
						//$('#dialog-confirm').html(elementName);
  					   $('#taskCountersTable').dialog('open');
  					    
  			  		
  			  		
  				 	/* traceListArray=traceList.split("[");
  				 	traceListArraytwo=traceListArray[1].split("]");
  				 	traceListArrayThree=traceListArraytwo[0].split(",");
  				 	*/	
  				 	ctr++;
  			});
  		  
  		   $("#taskCountersTable").dialog({
  	        	 resizable: true,
  	        	  autoOpen: false,
  	        	height:140,
  	        	width:1100,
  	        	modal: true,
  	        	buttons: {
  	        	'OK': function() {
  	        	 $(this).dialog('close');
  	        	}
  	        } }); 
        });
    </script>   
    
	
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
	         				  <div id="htf">
	         				  	<br/>			
	         					<div id="tabs">	
	         						<ul>
										<li><a href="#logChurningReport">Job Execution Analysis Report</a></li>
										<li><a href="#profilingReport">Profiling Report</a></li>
										<li><a href="#pureJarCountersReport">Un-Instrumented Jar Execution Stats</a></li>
									</ul>
	         			
	         						<!-- Display of log churning starts here --> 
									<div id="logChurningReport">
										
										<div class='field'>
											<h3>Cluster-wide MapReduce Job Execution Analysis(Debug) Report:<img src='images/info.png' class='info-image'/></h3>
											<div class='info-message' style='height:35px; width:160px;'>
												<div class='speech-icon'></div><p>This is the report which contains cluster-wide MapReduce job execution analysis.</p>
											</div>
										</div>
										
										
													
										<%  
											Map logMap= (Map)request.getAttribute("logMap");
											if(logMap!=null){
										%>
												<div id="ptreegrid"></div>
												<script type="text/javascript">
												 $('<table id="treegrid"></table>').appendTo('#ptreegrid');
													var logChurningData=[];		
													var allTaskCounterData={};
													
													
													var logAnalysisJSONString=<%=request.getAttribute("logAnalysisJSONString") %>;
													var numOfJobs=0;
													 $.each(logAnalysisJSONString, function(jobId, jobBean){
														 
														 	numOfJobs++;
															var totalInputKeys=jobBean["totalInputKeys"];
															var totalContextWrites=jobBean["totalContextWrites"];
															var totalUnmatchedKeys=jobBean["totalUnmatchedKeys"];
															var totalUnmatchedValues=jobBean["totalUnmatchedValues"];
															var jobMap=jobBean["jobMap"];
															var logChurningJsonObj = { "id":numOfJobs,"elementName":"<div class='jobDetails' style='color:blue'>"+jobId+"<form></form></div>","totalInputKeys":totalInputKeys,"totalContextWrites":totalContextWrites,"totalUnmatchedKeys":totalUnmatchedKeys,"totalUnmatchedValues":totalUnmatchedValues,level:"0", parent:"", isLeaf:false, expanded:false, loaded:true};
															logChurningData.push(logChurningJsonObj);
															var numOfMapReduce=0; 
														
														 
														 $.each(jobMap, function(mapReduceName, mapReduceBean){														 
															numOfMapReduce++;
															var totalMapReduceInputKeys=mapReduceBean["totalInputKeys"];
															var totalMapReduceContextWrites=mapReduceBean["totalContextWrites"];
															var totalMapReduceUnmatchedKeys=mapReduceBean["totalUnmatchedKeys"];
															var totalMapReduceUnmatchedValues=mapReduceBean["totalUnmatchedValues"];
															var mapReduceMap=mapReduceBean["mapReduceMap"];
															var mapReduceJsonObj = { "id":numOfJobs+'_'+numOfMapReduce,"elementName":"<div class='classDetails' style='color:Tomato'>"+mapReduceName+"<form></form></div>","totalInputKeys":totalMapReduceInputKeys,"totalContextWrites":totalMapReduceContextWrites,"totalUnmatchedKeys":totalMapReduceUnmatchedKeys,"totalUnmatchedValues":totalMapReduceUnmatchedValues,level:"1", parent:numOfJobs, isLeaf:false, expanded:false, loaded:true};
															logChurningData.push(mapReduceJsonObj);  
															var numOfNodes=0;
															$.each(mapReduceMap, function(nodeName, nodeBean){
																
																numOfNodes++;
																var totalNodeInputKeys=nodeBean["totalInputKeys"];
																var totalNodeContextWrites=nodeBean["totalContextWrites"];
																var totalNodeUnmatchedKeys=nodeBean["totalUnmatchedKeys"];
																var totalNodeUnmatchedValues=nodeBean["totalUnmatchedValues"];
																var nodeMap=nodeBean["nodeMap"];
																var numOfInstance=0;
																var nodeJsonObj = { "id":numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes,"elementName":"<div class='nodeDetails' style='color:green'>"+nodeName+"<form></form></div>","totalInputKeys":totalNodeInputKeys,"totalContextWrites":totalNodeContextWrites,"totalUnmatchedKeys":totalNodeUnmatchedKeys,"totalUnmatchedValues":totalNodeUnmatchedValues,level:"2", parent:numOfJobs+'_'+numOfMapReduce, isLeaf:false, expanded:false, loaded:true};
																logChurningData.push(nodeJsonObj); 
																$.each(nodeMap, function(instanceName, instanceBean){
																	
																	numOfInstance++;
																	var totalInstanceInputKeys=instanceBean["totalInputKeys"];
																	var totalInstanceContextWrites=instanceBean["totalContextWrites"];
																	var totalInstanceUnmatchedKeys=instanceBean["totalUnmatchedKeys"];
																	var totalInstanceUnmatchedValues=instanceBean["totalUnmatchedValues"];
																	var taskName=instanceName+" (Click here for details)";
																	var instanceMap=instanceBean["instanceMap"];
																	var instanceJsonObj = { "id":numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes+'_'+numOfInstance,"elementName":"<div class='modal' style='cursor:pointer'>"+taskName+" <form><input type='hidden' name='elementName' value="+instanceName+" id='elementName' /></form></div>","totalInputKeys":totalInstanceInputKeys,"totalContextWrites":totalInstanceContextWrites,"totalUnmatchedKeys":totalInstanceUnmatchedKeys,"totalUnmatchedValues":totalInstanceUnmatchedValues,level:"3", parent:numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes, isLeaf:true, expanded:false, loaded:true};
																	logChurningData.push(instanceJsonObj); 
																	var taskCounterData=[];
																	
																	$.each(instanceMap, function(counterName, counterBean){
																	
																		if(counterName.indexOf("contextWrite")!=-1){
																			var totalFilteredIn=counterBean["totalFilteredIn"];
																			var totalCounterContextWrites=counterBean["totalContextWrites"];
																			var totalCounterUnmatchedKeys=counterBean["totalUnmatchedKeys"];
																			var totalCounterUnmatchedValues=counterBean["totalUnmatchedValues"];
																			var totalFilteredOut=counterBean["totalFilteredOut"];
																			var counterJsonObj = {"counterName":counterName,"filteredIn":'-',"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":'-'};
																			taskCounterData.push(counterJsonObj); 
																		}else{													
																			var totalFilteredIn=counterBean["totalFilteredIn"];
																			var totalCounterContextWrites=counterBean["totalContextWrites"];
																			var totalCounterUnmatchedKeys=counterBean["totalUnmatchedKeys"];
																			var totalCounterUnmatchedValues=counterBean["totalUnmatchedValues"];
																			var totalFilteredOut=counterBean["totalFilteredOut"];
																			var counterJsonObj = {"counterName":counterName,"filteredIn":totalFilteredIn,"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":totalFilteredOut};
																			taskCounterData.push(counterJsonObj); 
																		}
																	});
																	allTaskCounterData[instanceName]=taskCounterData;
																});
															});
														 });	
													 });
													 
													 var logAnalysisJson={
					         								    "response": logChurningData},
					         								    grid;    								    
					         								grid = jQuery("#treegrid");
					         								grid.jqGrid({
					         								    datastr: logAnalysisJson,
					         								    datatype: "jsonstring",
					         								    height: "auto",
					         								    loadui: "disable",
					         								  	colNames:["Name","Input Keys","Context Writes","Unmatched Keys", "Unmatched Values"], 
																colModel:[ 
																			{name:'elementName',index:'elementName', width:500},
																			{name:'totalInputKeys',index:'totalInputKeys', width:120,align:"right"}, 
																			{name:'totalContextWrites',index:'totalContextWrites', width:120,align:"right"},
																			{name:'totalUnmatchedKeys',index:'totalUnmatchedKeys', width:130,align:"right"},																	
																			{name:'totalUnmatchedValues',index:'totalUnmatchedValues', width:140,align:"right"},
																			
																],												
					         								    treeGrid: true,
					         								    treeGridModel: "adjacency",
					         								    caption: "Cluster-wide MapReduce Job Execution Analysis(Debug) Report",
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
										<br/>		
										<table id="taskCountersTable"></table><div id="taskCountersDiv"></div>
									</div>	
									<!-- Display of log churning ends here -->
         							
			         							
			         				<!-- Display of profiling counters starts here -->	
			         					<div id="profilingReport">         								         												
			         							<div class='field'>
													<h3>CPU Samples and Heap Sites Allocation Stats Report (by JVMTI HProof) :<img src='images/info.png' class='info-image'/></h3>
													<div class='info-message' style='height:30px; width:160px;'>
														<div class='speech-icon'></div><p>This report shows CPU samples and Heap Allocation stats for various tasks.</p>
													</div>
												</div>					
			         												
			         												
			         							<br/>
												<%  
													Map profilingCountersMap=(Map)request.getAttribute("profilingMap");
													if(profilingCountersMap!=null){
												%>	
														<table id="cpuSample"></table> <div id="cpuSamplePager"></div>
														<br/>
														<table id="heapAllocation"></table> <div id="heapAllocationPager"></div>
														<br/>
														<br/>
													<script type="text/javascript"> 
															var cpuSampleData=[];
															var heapAllocationData=[];
															var taskNameString=null;
															var profilingJSONString=<%=request.getAttribute("profilingJSONString")%>;
															$.each(profilingJSONString, function(taskName, taskCounterMap){																
																 $.each(taskCounterMap, function(tableName, sampleMap){
																	 if(tableName=="cpuSample"){
																		 $.each(sampleMap, function(rankId, rankMap){
																				var tempName=taskName.split('_');
															  			  		var taskIdentifier=tempName[3];
															  			  		if(taskIdentifier=="m"){
															  			  		taskNameString="Mapper ("+taskName+")";
															  			  		}
															  			  		else{
															  			  		taskNameString="Reducer ("+taskName+")";
															  			  		}
												            					var percentageValue=rankMap["selfPercentage"];
												            					percentageValue=percentageValue+"%";
												            					var countValue=rankMap["count"];
												            					var qualifiedMethod=rankMap["qualifiedMethod"];
												            					var cpuSampleJsonObj = {"taskName":taskNameString,"rank":rankId,"percentage":percentageValue,"count":countValue,"methodName":qualifiedMethod};
												            					cpuSampleData.push(cpuSampleJsonObj);
																			 
																		 });
																	 }
																	 else{
																		 $.each(sampleMap, function(rankId, rankMap){
																			 
												            					var heapAllocSiteBeanMap=rankMap["heapAllocSiteBean"];
												            					var stackTraceList=rankMap["stackTraceList"];
												            					if(heapAllocSiteBeanMap!=null){
																					var tempName=taskName.split('_');
																  			  		var taskIdentifier=tempName[3];
																  			  		if(taskIdentifier=="m"){
																  			  		taskNameString="Mapper ("+taskName+")";
																  			  		}
																  			  		else{
																  			  		taskNameString="Reducer ("+taskName+")";
																  			  		}
														            					var className=heapAllocSiteBeanMap["className"];
														            					var bytesAllocated=heapAllocSiteBeanMap["bytesAllocated"];
														            					var liveBytes=heapAllocSiteBeanMap["liveBytes"];
														            					var liveInstances=heapAllocSiteBeanMap["liveInstances"];
														            					var instanceAllocated=heapAllocSiteBeanMap["instanceAllocated"];
														            					var heapAllocationJsonObj = {"taskName":taskNameString,"rank":rankId,"liveBytes":liveBytes,"liveInstances":liveInstances,"instanceAllocated":instanceAllocated,"bytesAllocated":bytesAllocated,"className":className,"stackTraceList":stackTraceList};
														            					heapAllocationData.push(heapAllocationJsonObj);
												            					}
																			 
																		 });
																	 }
																	 
																	
																 });	
																 
															 }); 
															
															 jQuery("#cpuSample").jqGrid({ 
																datastr: cpuSampleData,
															    datatype: "local",
															    colNames:['Task Name','Rank','Percentage','Count','Method Name'], 
															    colModel:[ 
																			{name:'taskName',index:'taskName', width:100, align:"right"},
																			{name:'rank',index:'rank', width:100}, 
																			{name:'percentage',index:'percentage', width:170, align:"right"},
																			{name:'count',index:'count', width:100, align:"right"},
																			{name:'methodName',index:'methodName', width:650}
																], 
																rowNum:1000,
																height: 'auto', 
																pager: '#cpuSamplePager', 
																viewrecords: true,
																sortname: 'taskName', 
																grouping:true,
																groupingView : {
																	groupField : ['taskName'],
																	groupColumnShow : [false], 
																	groupText : ['<b>{0}</b>'],
																	groupCollapse : true,
																	groupOrder: ['desc'] 
																},
																caption:'CPU Samples Stats (by JVMTI HProof)'
															}); 
															jQuery("#cpuSample").jqGrid('navGrid','#cpuSamplePager',{edit:false,add:false,del:false}); 
															
															jQuery("#heapAllocation").jqGrid({ 
																datastr: heapAllocationData,
															    datatype: "local",
															    colNames:['Task Name','Rank','Bytes Allocated (in kB)','Live Bytes','Live Instances','Instances Allowed','Class Name','StackTraceList'], 
															    colModel:[ 
																			{name:'taskName',index:'taskName', width:100},
																			{name:'rank',index:'rank', width:100, align:"right"},																					
																			{name:'bytesAllocated',index:'bytesAllocated', width:160, align:"right"},
																			{name:'liveBytes',index:'liveBytes', width:120, align:"right"},
																			{name:'liveInstances',index:'liveInstances', width:120, align:"right"},
																			{name:'instanceAllocated',index:'instanceAllocated', width:140, align:"right"},
																			{name:'className',index:'className', width:120, align:"right"},
																			{name:'stackTraceList',index:'stackTraceList', width:650,formatter:function(cell,options,row){
																				 var traceList="";
																				 traceList =row.stackTraceList.toString();
																				 traceListArray=traceList.split(",");
																				 var value;
																				 var tarceListStringElement="";
																				 for(var traceString in traceListArray ){
																					 value= traceListArray[traceString];
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
																rowNum:1000, 
																height: 'auto', 
																pager: '#heapAllocationPager', 
																viewrecords: true, 
																sortname: 'taskName', 
																grouping:true,
																groupingView : {
																	groupField : ['taskName'],
																	groupColumnShow : [false], 
																	groupText : ['<b>{0}</b>'],
																	groupCollapse : true,
																	groupOrder: ['desc'] 
																},
																caption:'Heap Sites Allocation Stats (by JVMTI HProof) '
															}); 
															jQuery("#heapAllocation").jqGrid('navGrid','#heapAllocationPager',{edit:false,add:false,del:false});
															
															 for(var x=0; x<cpuSampleData.length; x++){
											                    $("#cpuSample").jqGrid('addRowData', x+1, cpuSampleData[x]);
											                
											                }
															jQuery('#cpuSample').trigger("reloadGrid");
															
															for(var y=0; y<cpuSampleData.length; y++){
											                    $("#heapAllocation").jqGrid('addRowData', y+1, heapAllocationData[y]);
											                
											                }
															jQuery('#heapAllocation').trigger("reloadGrid");
															
															
														</script>
														
														
													<%}else{
															String profilingEnabled=(String)request.getAttribute("profilingEnabled");	
															if(profilingEnabled.equalsIgnoreCase("true")){
															%>	
																<div style= "color:red">
					         										<h4>Data Not Found!!</h4>
					         									</div>		
															<%}
															else{
																%>	
																<div style= "color:red">
					         										<h4>Profiling not Enabled</h4>
					         									</div>	
															<%}
																									
														
													}%>
			         								
												       
												     		
			         												
			         						</div>	
			         				<!-- Display of profiling counters ends here -->    							
			         							
	         					<!-- Display of pure jar counters starts here -->
			         				<div id="pureJarCountersReport">	
			         					<div class='field'>
											<h3>Un-Instrumented Jar Execution Statistics Report:<img src='images/info.png' class='info-image'/></h3>
											<div class='info-message' style='height:40px; width:160px;'>
												<div class='speech-icon'></div><p>This report shows all the counters genreated from the jar without any intsumentation process on it.</p>
											</div>
										</div>
			         					
			         					<div >					
	         							<br/>
										
										<%  
											String pureJarCountersJSON2=(String)request.getAttribute("pureJarCountersJSON");
											if(pureJarCountersJSON2!=null){
												Map pureJarCountersMap=(Map)request.getAttribute("pureJarCountersMap");
												%>	
													<table id="list"></table> <div id="pager"></div>
													<br/>
												
												<script type="text/javascript"> 
													
													var myData=[];
													var pureJarJSONString=<%=request.getAttribute("pureJarCountersJSON") %>;
													 $.each(pureJarJSONString, function(j, level0){
														
														 
														 $.each(level0, function(i, level1){														 
															 var jsonObj = {"jobName":j,"counterName":i,"counterValue":level1};
															 myData.push(jsonObj);
														 });	
													 });
																											
														jQuery("#list").jqGrid({ 
															datastr: myData,
														    datatype: "local",
														    colNames:['Job Name','Counter Name','Counter Value'], 
														    colModel:[ 
																		{name:'jobName',index:'jobName', width:100, editable:true},
																		{name:'counterName',index:'counterName', width:150},
																		{name:'counterValue',index:'counterValue', width:150, align:"right"},
															],
															rowNum:1000,
															height: 'auto',															
															pager: '#pager', 
															viewrecords: true, 
															sortname: 'jobName', 
															grouping:true,
															groupingView : {
																groupField : ['jobName'],
																groupColumnShow : [false], 
																groupText : ['<b>Job- {0} </b>'],
																groupCollapse : true,
																groupOrder: ['desc'] 
															},
															caption:'Un-Instrumented Jar Execution Statistics'
														}); 
														jQuery("#list").jqGrid('navGrid','#pager',{edit:false,add:false,del:false});	
														jQuery("#list").setGridWidth(500);
														for(var i=0; i<myData.length; i++){
										                    $("#list").jqGrid('addRowData', i+1, myData[i]);
										                
										                }
														jQuery('#list').trigger("reloadGrid");

												</script>
											<%}else{%>	
												<div style= "color:red">
	         										<h4>Data Not Found!!</h4>
	         									</div>												
												
											<%}%>
										       										     		
	         											
	         							</div>	 
	         						</div>        							
	         					<!-- Display of pure jar counters ends here -->    
	         						
																						
											<br/><br/>
											<%  
											long executionTime=(Long)request.getAttribute("totalExecutionTime");
										%>
										<div style="color:#009900">
	         								<h4>Total time taken to complete the process: <%=executionTime%> ms</h4>
	         							</div>	
																					
										 <br>
								 		<a href="${pageContext.servletContext.contextPath}">Go to Home Page</a>
							
							
								</div>
							  </div>	
							<!-- End of HTF code -->
	         					
	         					
							</td>
						</tr>						
					
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>