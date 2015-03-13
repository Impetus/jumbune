
<div id="treeGridBox">
	<div class="pageTopPane">
		<h2 class="pageTitle">Cluster-wide MapReduce Job Execution Analysis(Debug) Report - </h2>
		<div class="pageTopPaneRight" id="debugRptViewOpt">
			<div class="tblChartOpts">
				<span class="optLbl">Table View -</span><br />
				<a href="javascript:void(0);" id="tblView" class="viewOpt" ui:target="debugreportTableView"><span class="selView"></span></a>
			</div>
			<div class="tblChartOptsSap"></div>
			<div class="tblChartOpts">
				<span class="optLbl">Chart View -</span><br />
				<a href="javascript:void(0);" id="pieChartView" class="viewOpt" ui:target="debugreportPieChartView"><span></span></a>
			</div>
		</div>
	</div>

	<div id="debugreportTableView" class="debugReportTblChartViewCnt">	

		<div class="commonBox breadcrumbwrap">
			<span class="fleft"><b>You are here:</b>&nbsp;</span><div id="breadcrumBox" class="fleft" style=""></div>
		</div>

		<div id="debugErrorLinks" class="errorLinkBox"></div>


		<div class="viewChangeLinks">
			<div>
			<a id="jobsViewLink" href="javascript:void(0);" class="active">J</a>
			<a id="mapsViewLink"  href="javascript:void(0);">M</a>
			<a id="instViewLink"  href="javascript:void(0);">I</a>
			</div>
			<div id="sampledHDFSPathBox" class="fright" style="display:none;">
			<span>Sampled HDFS Path:</span>
			<span id="sampledHDFSPathDataBox">Sampled HDFS Path:</span>
			</div>
		</div>
		
		<div id="debugMainGridBox" class="analyserReportWrap">
			<div class="widget-container jmritablewrap">
				<div class="widget-header"><div class="title">Jobs</div><a id="jobsChainSorting" href="javascript:void(0);" >chain</a><div id="jobActiveGrid" class="activegrid" style="display:block;"></div></div>
				<div class="widget-body">
					<div id="jobptreegrid"></div>
				</div>
			</div>

			<div class="widget-container jmritablewrap">
				<div class="widget-header"><div class="title">Faulty Mappers/Reducers</div><a id="mapsChainSorting" href="javascript:void(0);" >chain</a><div id="mapActiveGrid" class="activegrid"></div></div>
				<div class="widget-body">
					<div id="mapptreegrid"></div>
				</div>
			</div>

			<div class="widget-container jmritablewrap">
				<div class="widget-header"><div class="title">Instances</div><div id="insActiveGrid" class="activegrid"></div></div>
				<div class="widget-body">
					<div id="insptreegrid"></div>
				</div>
			</div>
		</div>	
		
		<div class="legendwrap">
			<div class="commonBox" style="display:none;">
				<ul id="classLegendBox">
					<li><span class="mapper_ident"></span>Mapper</li>
					<li><span class="reducer_ident"></span>Reducer</li>
				</ul>
			</div>	
			<div class="commonBox">
				<ul id="nodeLegendBox"></ul>
			</div>
		</div>

		<div class="widget-container" id="insSubCounter" style="display:none;">
			<div class="widget-header"></div>
			<div class="widget-body relative">
				<div class="taskcountersdivlegend">
					<span class="rootmap" id="treeRootName">Map</span>
					<span class="method">Method</span>
					<span class="counter">Counter</span>
				</div>
				<div class="clear taskcountersdiv" id="taskCountersDiv"><ul></ul></div>
				<div id="taskCountersTableWrap" class="taskcounterstablewrap"></div>
			</div>
		</div>	
	</div>

	<div class="clear"></div>
	<div id="debugreportPieChartView" class="debugReportTblChartViewCnt" style="display:none;">
		<div class="topBar widget-header"></div>
		<table width="100%" height="30px" cellpadding="0" cellspacing="0">
			<tr>
				<th width="40%">&nbsp;</th>
				<th width="15%" align="center">Input Keys</th>
				<th width="15%" align="center" class="selCol">Output Records</th>
				<th width="15%" align="center">Unmatched Keys</th>
				<th width="15%" align="center" class="last">Unmatched Values</th>
			</tr>
		</table>
		<div id="debugReportAccordion"></div>
	</div>

	<div class="widget-container" id="partitionMapDiv" style="display:none;">
		<div class="widget-header">Partition</div>
		<div class="widget-body">			
			<div id="partitionMapDivBox">			
				
			</div>
		</div>
	</div>

	
	
</div>

<script type="text/javascript">

var logAnalysisJSONStringObj;
var numOfJobs;
var numOfMapReduce;
var numOfNodes;
var numOfInstance;
var iPlot = [];
var mrPlot = [];
var instMapColorCode = new Array();
var mrColorCode = new Array();

function enableDebugAnalysis(logAnalysisJSONString)
{
	$('<table id="jobtreegrid"></table>').appendTo('#jobptreegrid');
	$('<table id="maptreegrid"></table>').appendTo('#mapptreegrid');
	$('<table id="instreegrid"></table>').appendTo('#insptreegrid');
	var logJobChurningData=[];	
	var logMapChurningData=[];
	var logInsChurningData=[];
	var debugAnalyzerErrorGridData=[];
	allTaskCounterData=[];
    var errorCount = 0;
	
	logAnalysisJSONStringObj =  $.parseJSON(logAnalysisJSONString);
	numOfJobs=0;

	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	console.log(logName);
	console.log(logBean);
	var logMap=logBean["logMap"];
	var jobChain=logBean["jobChain"];
	var mrChain=logBean["mrChain"];
	var partitionerMap = logBean["partitionerMap"];
	
	if(typeof logBean["sampledHDFSPath"] != 'undefined')
	{
		$('#sampledHDFSPathDataBox').html(logBean["sampledHDFSPath"]);
		$('#sampledHDFSPathBox').show();
	}
	if(logName == 'ErrorAndException')
	{			
		$.each(logBean, function(key, val){
			errorCount++;				
			var eachDebugAnalyzerErrorJobsJsonObj = { "id":errorCount,"jobName":key,"errorMsg":val};
			debugAnalyzerErrorGridData.push(eachDebugAnalyzerErrorJobsJsonObj);
			
		});	
		$('#debugAnalyzerErrorsBox').show();
		$('#debugErrorLinks').html('<button id="errorBtn"><span>Failed Jobs</span></button>');
		$('#summary-debugger').find('.loaderMainBox').html('<div class="status error"><span>Information: </span>An error has been occurred while processing module, Please refer same module for more details.</div>').removeClass('loaderMainBox');
		return;
	}
	else if(logName == 'debuggerSummary')
	{
		$.each(logBean, function(summaryMapperKey, summaryMapperVal){
			
			if(summaryMapperKey == 'mapperReducerNames')
			{		
				summaryJobHTml ='<div class="summary-debug-half-box"><table class="summary-debugger-table" width="100%" cellspacing="1" cellpadding="1"><tr class="summary-debug-heading"><td>Jobs/Mapper/Reducer</td><td>Unmatched Keys</td><td>Unmatched Values</td></tr>';								
				$.each(summaryMapperVal, function(summaryJobMapKey, summaryJobMapVal){
					if(summaryJobMapVal['totalUnmatchedKeys']==-1)
					{
						summaryJobMapVal['totalUnmatchedKeys'] = '-';
					}
					if(summaryJobMapVal['totalUnmatchedValues']==-1)
					{
						summaryJobMapVal['totalUnmatchedValues'] = '-';
					}	

					if(summaryJobMapVal['jobMapReduceName'].indexOf('.') > 0) 
					{
						var summaryJobMapValArr = summaryJobMapVal['jobMapReduceName'].split('.'); 
						summaryJobMapSplitVal = summaryJobMapValArr[summaryJobMapValArr.length-1];		
						summaryJobHTml +="<tr><td title='"+summaryJobMapVal['jobMapReduceName']+"'>"+summaryJobMapSplitVal+"</td><td>"+summaryJobMapVal['totalUnmatchedKeys']+"</td><td>"+summaryJobMapVal['totalUnmatchedValues']+"</td></tr>";
					}
					else
					{		
					summaryJobHTml +="<tr><td title='"+summaryJobMapVal['jobMapReduceName']+"'>"+summaryJobMapVal['jobMapReduceName']+"</td><td>"+summaryJobMapVal['totalUnmatchedKeys']+"</td><td>"+summaryJobMapVal['totalUnmatchedValues']+"</td></tr>";
					}
				});
				summaryJobHTml +='</table></div>';
				$('#summary-debugger').find('.summary-debug-main').html(summaryJobHTml);
				
			}
					
		});	
		$('#summary-debugger').find('.loaderMainBox').removeClass('loaderMainBox');		
		return;
	}

	$.each(logMap, function(jobId, jobBean){
		numOfJobs++;
		var totalInputKeys=jobBean["totalInputKeys"];
		var totalContextWrites=jobBean["totalContextWrites"];	
		var totalUnmatchedKeys=jobBean["totalUnmatchedKeys"];
		var totalUnmatchedValues=jobBean["totalUnmatchedValues"];	
		var jobMap=jobBean["jobMap"];
		var jobChainName="";
		var jobChainCounter=numOfJobs;
		var jobChainCount=1;

		if(jobChain)
		{
		$.each(jobChain, function(jobChainId, jobChainBean){	
			if(jobId == jobChainBean["name"])
			{
				jobChainName='<div class="jobChainIco"></div>';
				jobChainCounter = jobChainCount;
				//var jobChainInputKeys=jobChainBean["inputKeys"];
				//var jobChainContextWrites=jobChainBean["contextWrites"];
			}		
			jobChainCount++;

		});
		if(jobChainCounter != 1)
		{
			jobChainCounter = 9999;
			jobChainName = '<div class="jobChainFkIco"></div>';
		}
		}
		
		// partitionerMap
		if(partitionerMap)
		{
			$.each(partitionerMap, function(partitionerMapId, partitionerMapBean){	
				if(jobId == partitionerMapId)
				{
					partitionerMapName = '<span class="partitionHighlight"><span class="partitionerMapName" rel="'+partitionerMapId+'" href="javascript:void(0);">P</span>&nbsp;</span>';	
					jobChainName += partitionerMapName;
										
					//var partitionerMapInputKeys=jobChainBean["inputKeys"];
					//var partitionerMapContextWrites=jobChainBean["contextWrites"];
				}
			});		
		}
                if(totalUnmatchedKeys==-1)
                    totalUnmatchedKeys="-";
                if(totalUnmatchedValues==-1)
                    totalUnmatchedValues="-";
                
		var logChurningJsonObj = { "chain":"-", "id":jobChainCounter,"elementName":jobChainName+"<div class='jobDetails'>"+jobId+"<form></form></div>","totalInputKeys":totalInputKeys,"totalContextWrites":totalContextWrites,"totalUnmatchedKeys":totalUnmatchedKeys,"totalUnmatchedValues":totalUnmatchedValues,level:"0", parent:"", isLeaf:false, expanded:false, loaded:true};
		logJobChurningData.push(logChurningJsonObj);
		

			numOfMapReduce=0;
			$.each(jobMap, function(mapReduceName, mapReduceBean){																 
				numOfMapReduce++;
				var totalMapReduceInputKeys=mapReduceBean["totalInputKeys"];
				var totalMapReduceContextWrites=mapReduceBean["totalContextWrites"];
				var totalMapReduceUnmatchedKeys=mapReduceBean["totalUnmatchedKeys"];
				var totalMapReduceUnmatchedValues=mapReduceBean["totalUnmatchedValues"];
				var mapReduceMap=mapReduceBean["mapReduceMap"];
				var mapChainName="";
				var mapChainCounter="";
				var mapChainCount=1;
			
				if(mrChain)
				{
				$.each(mrChain, function(mrJobChainId, mrJobChainBean){
								
					if(jobId == mrJobChainId)
					{	
						//var marJobChain=mrJobChainBean[jobId];
										
						$.each(mrJobChainBean, function(mrMapChainId, mrMapChainBean){
						
						if(mapReduceName == mrMapChainBean["name"])
						{
							mapChainName='<div class="mapChainIco"></div>';
							mapChainCounter = mapChainCount;
							//var mapChainInputKeys=mrMapChainBean["inputKeys"];
							//var mapChainContextWrites=mrMapChainBean["contextWrites"];
						}	
						mapChainCount++;

						});
					}

				});
				if(!mapChainCounter)
				{
					mapChainCounter = 9999999;
					mapChainName = '<div class="mapChainFkIco"></div>';
				}
				}
			
				var mapReduceJsonObj = { "chain":"-", "id":mapChainCounter,"elementName":mapChainName+"xzxx<div class='classDetails'>"+mapReduceName+"<form></form></div>","totalInputKeys":totalMapReduceInputKeys,"totalContextWrites":totalMapReduceContextWrites,"totalUnmatchedKeys":totalMapReduceUnmatchedKeys,"totalUnmatchedValues":totalMapReduceUnmatchedValues,level:"1", parent:numOfJobs, isLeaf:false, expanded:false, loaded:true};
			
				logMapChurningData.push(mapReduceJsonObj);  
				

			numOfNodes=0;
			$.each(mapReduceMap, function(nodeName, nodeBean){
				numOfNodes++;
				var totalNodeInputKeys=nodeBean["totalInputKeys"];
				var totalNodeContextWrites=nodeBean["totalContextWrites"];
				var totalNodeUnmatchedKeys=nodeBean["totalUnmatchedKeys"];
				var totalNodeUnmatchedValues=nodeBean["totalUnmatchedValues"];
				var nodeMap=nodeBean["nodeMap"];
				numOfInstance=0;
				var nodeJsonObj = { "id":numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes,"elementName":"<div class='nodeDetails'>"+nodeName+"<form></form></div>","totalInputKeys":totalNodeInputKeys,"totalContextWrites":totalNodeContextWrites,"totalUnmatchedKeys":totalNodeUnmatchedKeys,"totalUnmatchedValues":totalNodeUnmatchedValues,level:"2", parent:numOfJobs+'_'+numOfMapReduce, isLeaf:false, expanded:false, loaded:true};
				var nodeColor = colorCodes[numOfNodes];

				$.each(nodeMap, function(instanceName, instanceBean){
					numOfInstance++;
					var totalInstanceInputKeys=instanceBean["totalInputKeys"];
					var totalInstanceContextWrites=instanceBean["totalContextWrites"];
					var totalInstanceUnmatchedKeys=instanceBean["totalUnmatchedKeys"];
					var totalInstanceUnmatchedValues=instanceBean["totalUnmatchedValues"];
					var taskName=instanceName;
					var instanceMap=instanceBean["instanceMap"];
					var instanceJsonObj = { "id":numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes+'_'+numOfInstance,"elementName":"<span class='legendBullets' style='background-color:"+nodeColor+"'></span><div class='modal' style='cursor:pointer'>"+instanceName+" <form><input type='hidden' name='elementName' value="+taskName+" id='elementName' /></form></div>","totalInputKeys":totalInstanceInputKeys,"totalContextWrites":totalInstanceContextWrites,"totalUnmatchedKeys":totalInstanceUnmatchedKeys,"totalUnmatchedValues":totalInstanceUnmatchedValues,level:"3", parent:numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes, isLeaf:true, expanded:false, loaded:true};
					logInsChurningData.push(instanceJsonObj); 
					if(typeof instanceMap == 'undefined')
					{ return; }
					var taskCounterData=[];
					$.each(instanceMap, function(counterName, counterBean){
						if(counterName.indexOf("contextWrite")!=-1)
						{
							var totalFilteredIn=counterBean["totalFilteredIn"];
							var totalCounterContextWrites=counterBean["totalContextWrites"];
							var totalCounterUnmatchedKeys=counterBean["totalUnmatchedKeys"];
							var totalCounterUnmatchedValues=counterBean["totalUnmatchedValues"];
							var totalFilteredOut=counterBean["totalFilteredOut"];
							var counterMap2=counterBean["counterMap"];
							var counterJsonObj = {"counterName":counterName,"filteredIn":'-',"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":'-',level:"0", parent:counterName, isLeaf:true, expanded:false, loaded:true};
							taskCounterData.push(counterJsonObj); 
						}
						else
						{													
							var totalFilteredIn=counterBean["totalFilteredIn"];
							var totalCounterContextWrites=counterBean["totalContextWrites"];
							var totalCounterUnmatchedKeys=counterBean["totalUnmatchedKeys"];
							var totalCounterUnmatchedValues=counterBean["totalUnmatchedValues"];
							var totalFilteredOut=counterBean["totalFilteredOut"];
							var counterMap2=counterBean["counterMap"];
							var counterJsonObj = {"counterName":counterName,"filteredIn":totalFilteredIn,"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":totalFilteredOut,level:"0", parent:counterName, isLeaf:true, expanded:false, loaded:true};
							taskCounterData.push(counterJsonObj); 
						}
					
						if(typeof counterMap2 != 'undefined')
						{
							$.each(counterMap2, function(counterName2, counterBean2){
							
								var totalFilteredIn=counterBean2["totalFilteredIn"];
								var totalCounterContextWrites=counterBean2["totalContextWrites"];
								var totalCounterUnmatchedKeys=counterBean2["totalUnmatchedKeys"];
								var totalCounterUnmatchedValues=counterBean2["totalUnmatchedValues"];
								var totalFilteredOut=counterBean2["totalFilteredOut"];
								var counterMap3=counterBean2["counterMap"];	
							
								var counterJsonObj = {"counterName":counterName2,"filteredIn":'-',"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":'-',level:"1", parent:counterName, isLeaf:true, expanded:false, loaded:true};
								taskCounterData.push(counterJsonObj);

								if(typeof counterMap3 != 'undefined')
								{
									$.each(counterMap3, function(counterName3, counterBean3){
								
									var totalFilteredIn=counterBean3["totalFilteredIn"];
									var totalCounterContextWrites=counterBean3["totalContextWrites"];
									var totalCounterUnmatchedKeys=counterBean3["totalUnmatchedKeys"];
									var totalCounterUnmatchedValues=counterBean3["totalUnmatchedValues"];
									var totalFilteredOut=counterBean3["totalFilteredOut"];
									var counterMap4=counterBean3["counterMap"];	
									var counterJsonObj = {"counterName":counterName3,"filteredIn":'-',"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":'-',level:"2", parent:counterName, isLeaf:true, expanded:false, loaded:true};
									taskCounterData.push(counterJsonObj);	
								
										if(typeof counterMap4 != 'undefined')
										{
											$.each(counterMap4, function(counterName4, counterBean4){
										
											var totalFilteredIn=counterBean4["totalFilteredIn"];
											var totalCounterContextWrites=counterBean4["totalContextWrites"];
											var totalCounterUnmatchedKeys=counterBean4["totalUnmatchedKeys"];
											var totalCounterUnmatchedValues=counterBean4["totalUnmatchedValues"];
											var totalFilteredOut=counterBean4["totalFilteredOut"];
											var counterMap5=counterBean4["counterMap"];	
											var counterJsonObj = {"counterName":counterName4,"filteredIn":'-',"contextWrites":totalCounterContextWrites,"unmatchedKeys":totalCounterUnmatchedKeys,"unmatchedValues":totalCounterUnmatchedValues,"filteredOut":'-',level:"3", parent:counterName, isLeaf:true, expanded:false, loaded:true};
											taskCounterData.push(counterJsonObj);							

											});					
										}

									});					
								}

							});

						}

					});

					allTaskCounterData[instanceName]=taskCounterData;
				});
			});
		});	
	});
	});


	//job Grid
	var logJobAnalysisJson={"response": logJobChurningData};    								    
	jobGrid = jQuery("#jobtreegrid");
	jobGrid.jqGrid({
		datastr: logJobAnalysisJson,
		datatype: "jsonstring",
		height: 199,
		sortable:true,
		sortname:"id",
		sortorder:"desc",	
		hidegrid:false,
		loadui: "disable",
		scrollOffset: 0,
		colNames:["Chain", "ID", "Name","Input Keys","Output Records","Unmatched Keys", "Unmatched Values"], 
		colModel:
			[ 			
				{name:'chain',index:'chain', width:20, align:"center", hidden:true},
				{name:'id',index:'id', width:10, align:"center", hidden:true},
				{name:'elementName',index:'elementName', width:210, align:"left", classes:"jobfirstcol"},
				{name:'totalInputKeys',index:'totalInputKeys', width:70,align:"center"}, 
				{name:'totalContextWrites',index:'totalContextWrites', width:70,align:"center"},
				{name:'totalUnmatchedKeys',index:'totalUnmatchedKeys', width:70,align:"center"},
				{name:'totalUnmatchedValues',index:'totalUnmatchedValues', width:70,align:"center"}
			],												
		rowNum: 10000,
		jsonReader: {
			repeatitems: false,
			root: "response"
		}
	});


	//job map/reduce grid
	var logMapAnalysisJson={"response": logMapChurningData};    								    
	mapGrid = jQuery("#maptreegrid");
	mapGrid.jqGrid({
		datastr: logMapAnalysisJson,
		datatype: "jsonstring",
		height: 178,
		hidegrid:false,
		loadui: "disable",
		scrollOffset: 0,
		colNames:["Chain", "ID", "Name","Input Keys","Output Records","Unmatched Keys", "Unmatched Values"], 
		colModel:
			[ 
				{name:'chain',index:'chain', width:20, align:"center", hidden:true},
				{name:'id',index:'id', width:10, align:"center", hidden:true},
				{name:'elementName',index:'elementName', width:200, align:"left", formatter:classTitleShort, classes:"mrfirstcol"},
				{name:'totalInputKeys',index:'totalInputKeys', width:70,align:"center", hidden: true}, 
				{name:'totalContextWrites',index:'totalContextWrites', width:70, align:"center", hidden: true},
				{name:'totalUnmatchedKeys',index:'totalUnmatchedKeys', width:70, align:"center", hidden: true},
				{name:'totalUnmatchedValues',index:'totalUnmatchedValues', width:70, align:"center", hidden: true}
			],												
		rowNum: 10000,
		footerrow:true,	
		userDataOnFooter: true, 
		altRows: true, 
		jsonReader: {
			repeatitems: false,
			root: "response"
		}
	});


	//job intance
	var logInsAnalysisJson={"response": logInsChurningData};    								    
	insGrid = jQuery("#instreegrid");
	insGrid.jqGrid({
		datastr: logInsAnalysisJson,
		datatype: "jsonstring",
		height: 157,
		hidegrid:false,
		loadui: "disable",
		scrollOffset: 0,
		colNames:["chain", "ID", "Name","Input Keys","Output Records","Unmatched Keys", "Unmatched Values"], 
		colModel:
			[ 
				{name:'chain',index:'chain', width:20,align:"center", hidden: true},
				{name:'id',index:'id', width:20,align:"center", hidden: true},
				{name:'elementName',index:'elementName', width:200, align:"left", formatter:classTitleShortFromInstanceTable, classes:"insfirstcol"},
				{name:'totalInputKeys',index:'totalInputKeys', width:70,align:"center", hidden: true}, 
				{name:'totalContextWrites',index:'totalContextWrites', width:70,align:"center", hidden: true},
				{name:'totalUnmatchedKeys',index:'totalUnmatchedKeys', width:70,align:"center", hidden: true},
				{name:'totalUnmatchedValues',index:'totalUnmatchedValues', width:70,align:"center", hidden: true}
			],												
		rowNum: 10000,
		footerrow:true,
		userDataOnFooter: true, 
		altRows: true, 
		jsonReader: {
		repeatitems: false,
		root: "response"
		}
	});
	
	// error grid table start
	if(debugAnalyzerErrorGridData)
	{
		$('<table id="debugAnalyzerErrortable"></table>').appendTo('#debugAnalyzerErrorDiv');		
		var debugAnalyzerErrorJobsGridDataJson={"response": debugAnalyzerErrorGridData};							    
		debugAnalyzerErrorJobsGrid = jQuery("#debugAnalyzerErrortable");
		debugAnalyzerErrorJobsGrid.jqGrid({
			datastr: debugAnalyzerErrorJobsGridDataJson,
			datatype: "jsonstring",
			height: 'auto',
			hidegrid:false,
			loadui: "disable",
			colNames:["ID","Name","Error"], 
			colModel:
				[ 
					{name:'id', index:'id', width:30, align:"center"},
					{name:'jobName', index:'jobName', width:150, align:"center"},
					{name:'errorMsg', index:'errorMsg', width:620, align:"left" , formatter:addPaddInVal} 
	
				],												
			rowNum: 10000,	
			jsonReader: {
			repeatitems: false,
			root: "response"
			}
		});
	}
	// error grid table end

	$('#debugAnalyzerErrortable tr:nth-child(even)').addClass("evenTableRow");
	
	
	$("#jobtreegrid tr:eq(1)").trigger('click');
	$('#mapptreegrid').find('div.ui-jqgrid-sdiv').insertBefore($('#mapptreegrid').find('div.ui-jqgrid-bdiv'));
	$('#insptreegrid').find('div.ui-jqgrid-sdiv').insertBefore($('#insptreegrid').find('div.ui-jqgrid-bdiv'));

	createDebugReportPieCharts();
}

function addPaddInVal(cellvalue, options, rowObject)
{
	var paddVal = '<div class="paddLeftWithTextWrap">'+cellvalue+'</div>';
	return paddVal;
}

$('#errorBtn').live('click', function () { 

	$('#tabs').tabs("option","disabled", []);
	var tabsLength = $('#tabs').tabs("length");
	$('#tabs').tabs('select', parseInt(tabsLength - 1)); 
	$( "#tabs" ).tabs( "refresh" );
	

});



function classTitleShort (cellvalue, options, rowObject) {
	// do something here
	if(cellvalue.indexOf(".") != -1 && cellvalue.indexOf("mapChainIco") != -1) {
		var valueArray = cellvalue.split('.'); 
		valueArray = "<div class='mapChainIco'></div><span class='legendBullets mrlegendBullets mapper_ident'></span><div class='classDetails'>"+valueArray[valueArray.length-1]+"</div>";
		return valueArray;
	}else if(cellvalue.indexOf(".") != -1 && cellvalue.indexOf("redChainIco") != -1) {
		var valueArray = cellvalue.split('.'); 
		valueArray = "<div class='redChainIco'></div><span class='legendBullets mrlegendBullets mapper_ident'></span><div class='classDetails'>"+valueArray[valueArray.length-1]+"</div>";
		return valueArray;
	}else if(cellvalue.indexOf(".") != -1) {
		var valueArray = cellvalue.split('.'); 
		valueArray = "<div class='mapChainFkIco'></div><span class='legendBullets mrlegendBullets mapper_ident'></span><div class='classDetails'>"+valueArray[valueArray.length-1]+"</div>";
		return valueArray;
	}else {
		return cellvalue;
	}
}

function classTitleShortFromInstanceTable (cellvalue, options, rowObject)
{

	if(cellvalue.indexOf(".") != -1 )  
	{
	   var valueArray = cellvalue.split('.'); 
	   valueArray = "<div class='classDetails'>"+valueArray[valueArray.length-1]+"</div>";
	   return valueArray;
	}
	else
	{
		return cellvalue;
	}
	
}


$(".partitionerMapName").live('click', function(){
	var partitionerMapName = $(this).attr('rel');
	
	var numOfJobs = 0;
	var partitionerMapData = [];
	
	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var partitionerMap=logBean["partitionerMap"];
	if(logName == 'ErrorAndException' || logName == 'debuggerSummary')
	{
		return;
	}
		if(partitionerMapName)
		{
			$.each(partitionerMap[partitionerMapName], function(partitionerMapId, partitionerMapBean){			
				
					numOfJobs++;
					var partitionName = partitionerMapBean["name"];
					var partitionInputKeys = partitionerMapBean["inputKeys"];
					var partitionIdealDistribution = partitionerMapBean["idealDistribution"];
					var partitionVariance = partitionerMapBean["variance"]+"%";			

					var partitionerMapJsonObj = { "id":numOfJobs,"name":partitionName,"inputKeys":partitionInputKeys,"idealDistribution":partitionIdealDistribution,"variance":partitionVariance};
					partitionerMapData.push(partitionerMapJsonObj);
					
				
			});
		}
	});	 

		
		var partitionerMapDataJson={"response": partitionerMapData};	
		
		jQuery("#partitionMapDivBox").html('<table id="partitionMapTable"></table>');

		jQuery("#partitionMapTable").jqGrid({
			datastr: partitionerMapDataJson,
			datatype: "jsonstring",
			height: 'auto',
			hidegrid:false,
			loadui: "disable",
			colNames:["ID", "Name", "Input Keys", "Ideal Distribution", "Variance"], 
			colModel:
				[ 
					{name:'id', index:'id', width:30, align:"center"},
					{name:'name', index:'name', width:260, align:"center"},
					{name:'inputKeys', index:'inputKeys', width:60, align:"center"},
					{name:'idealDistribution', index:'idealDistribution', width:100, align:"center"},
					{name:'variance', index:'variance', width:100, align:"center"} 
					
				],												
			rowNum: 100,	
			jsonReader: {
				repeatitems: false,
				root: "response"
			}
		}).trigger("reloadGrid");
		
		
		$("#partitionMapDiv").dialog({
			dialogClass: 'modalSelectLocation',
			height:'auto',
			width:600,
			resizable:false,
			modal: true
		});

});

$("#jobsViewLink").click(function(){
	$(".viewChangeLinks").find("a").each(function() {
		$(this).removeClass("active");
	});
	$(this).addClass("active");

	$("#debugMainGridBox").find(".activegrid").each(function() {
		$(this).fadeOut(300);
	});
	$("#jobActiveGrid").fadeIn(1500);
	
	$("#jobptreegrid").animate({width: "100%"}, 500);
	$("#mapptreegrid, #insptreegrid").animate({width: "100%"}, 500);

	$('#jobtreegrid').jqGrid("showCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	$('#maptreegrid, #instreegrid').jqGrid("hideCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv td').hide();
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0) td:eq(0)').show();
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1) td:eq(0)').show();

	// Set all grid width
	/*$('#jobtreegrid').jqGrid().setGridWidth(522);
	$('#maptreegrid, #instreegrid').jqGrid().setGridWidth(221);*/
});

$("#mapsViewLink").click(function(){
	$(".viewChangeLinks").find("a").each(function() {
		$(this).removeClass("active");
	});
	$(this).addClass("active");

	$("#debugMainGridBox").find(".activegrid").each(function() {
		$(this).fadeOut(300);
	});
	$("#mapActiveGrid").fadeIn(1500);

	$("#jobptreegrid").animate({width: "100%"}, 500);
	$("#mapptreegrid").animate({width: "100%"}, 500);
	$("#insptreegrid").animate({width: "100%"}, 500);

	$('#maptreegrid').jqGrid("showCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	$('#jobtreegrid, #instreegrid').jqGrid("hideCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv td').hide();
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0) td:eq(0)').show();
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1) td:eq(0)').show();
	
	//autoAdjustGridHeight();
	// Set all grid width
	/*$('#jobtreegrid').jqGrid().setGridWidth(222);
	$('#maptreegrid').jqGrid().setGridWidth(521);
	$('#instreegrid').jqGrid().setGridWidth(221);*/
});

$("#instViewLink").click(function(){
	$(".viewChangeLinks").find("a").each(function() {
		$(this).removeClass("active");
	});
	$(this).addClass("active");

	$("#debugMainGridBox").find(".activegrid").each(function() {
		$(this).fadeOut(300);
	});
	$("#insActiveGrid").fadeIn(1500);

	$("#jobptreegrid").animate({width: "100%"}, 500);
	$("#mapptreegrid").animate({width: "100%"}, 500);
	$("#insptreegrid").animate({width: "100%"}, 500);

	$('#instreegrid').jqGrid("showCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	$('#jobtreegrid, #maptreegrid').jqGrid("hideCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
	
	//$('#insptreegrid').find('div.ui-jqgrid-sdiv td').show();

	// Set all grid width
	/*$('#jobtreegrid').jqGrid().setGridWidth(222);
	$('#maptreegrid').jqGrid().setGridWidth(221);
	$('#instreegrid').jqGrid().setGridWidth(521);*/
});

//Click on jobs 
$("#jobtreegrid tr").live('click', function(){
	var jobTitle = $(this).find('.jobDetails').text();
		
	numOfMapReduce=0;
	var logJobChurningData=[];	
	var logMapChurningData=[];
	var logMapFooterChurningData=[];
	var logInsChurningData=[];
	
	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];
	if(logName == 'ErrorAndException' || logName == 'debuggerSummary')
	{
		return;
	}	
		$.each(logMap, function(jobId, jobBean){
			if(jobId == jobTitle)
			{
				numOfJobs++;
				var totalInputKeys=jobBean["totalInputKeys"];
				var totalContextWrites=jobBean["totalContextWrites"];
				var totalUnmatchedKeys=jobBean["totalUnmatchedKeys"];
				if(totalUnmatchedKeys == '-1')
				{
					totalUnmatchedKeys = '-';
				}
				var totalUnmatchedValues=jobBean["totalUnmatchedValues"];
				if(totalUnmatchedValues == '-1')
				{
					totalUnmatchedValues = '-';
				}
				var jobMap=jobBean["jobMap"];
				var logChurningJsonObj = { "chain":" ", "id":numOfJobs,"elementName":"<div class='jobDetails'>"+jobId+"<form></form></div>","totalInputKeys":totalInputKeys,"totalContextWrites":totalContextWrites,"totalUnmatchedKeys":totalUnmatchedKeys,"totalUnmatchedValues":totalUnmatchedValues,level:"0", parent:"", isLeaf:false, expanded:false, loaded:true};
				logMapFooterChurningData.push(logChurningJsonObj);
			}
		});
	});

	// push maps/reduces
	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];
	var mrChain=logBean["mrChain"];
	if(logName == 'ErrorAndException' || logName == 'debuggerSummary')
	{
		return;
	}
	

		$.each(logMap[jobTitle].jobMap, function(mapReduceName, mapReduceBean){
			numOfMapReduce++;
			var totalMapReduceInputKeys=mapReduceBean["totalInputKeys"];
			var totalMapReduceContextWrites=mapReduceBean["totalContextWrites"];
			var totalMapReduceUnmatchedKeys=mapReduceBean["totalUnmatchedKeys"];
			if(totalMapReduceUnmatchedKeys == '-1')
			{
				totalMapReduceUnmatchedKeys = '-';
			}
			var totalMapReduceUnmatchedValues=mapReduceBean["totalUnmatchedValues"];
			if(totalMapReduceUnmatchedValues == '-1')
			{
				totalMapReduceUnmatchedValues = '-';
			}			
			var mapReduceMap=mapReduceBean["mapReduceMap"];
			var mapChainName="";
			var mapChainCounter="";
			var mapChainCount=1;
			
			$.each(mrChain, function(mrJobChainId, mrJobChainBean){
								
				if(jobTitle == mrJobChainId)
				{	
					//var marJobChain=mrJobChainBean[jobId];					
					
					if(mrJobChainBean["mapChainList"])
					{
						$.each(mrJobChainBean["mapChainList"], function(mrMapChainId, mrMapChainBean){				
							
							if(mapReduceName == mrMapChainBean["name"])
							{
								
								mapChainName='<div class="mapChainIco"></div>';
								mapChainCounter = mapChainCount;
								//var mapChainInputKeys=mrMapChainBean["inputKeys"];
								//var mapChainContextWrites=mrMapChainBean["contextWrites"];
							}	
							mapChainCount++;

						});
					}

					if(mrJobChainBean["reduceChainList"])
					{
						$.each(mrJobChainBean["reduceChainList"], function(mrMapChainId, mrMapChainBean){				
							
							if(mapReduceName == mrMapChainBean["name"])
							{
								
								mapChainName='<div class="redChainIco"></div>';
								mapChainCounter = mapChainCount;
								//var mapChainInputKeys=mrMapChainBean["inputKeys"];
								//var mapChainContextWrites=mrMapChainBean["contextWrites"];
							}	
							mapChainCount++;

						});
					}

					
				}

			});
			if(!mapChainCounter)
			{
				mapChainCounter = 9999999;
				mapChainName = '<div class="mapChainFkIco"></div>';
			}

			var mapReduceJsonObj = { "chain":"-", "id":mapChainCounter, "elementName":mapChainName+"asas<div class='classDetails'>"+mapReduceName+"<form></form></div>","totalInputKeys":totalMapReduceInputKeys,"totalContextWrites":totalMapReduceContextWrites,"totalUnmatchedKeys":totalMapReduceUnmatchedKeys,"totalUnmatchedValues":totalMapReduceUnmatchedValues,level:"1", parent:numOfJobs, isLeaf:false, expanded:false, loaded:true};		
			logMapChurningData.push(mapReduceJsonObj);	
		});
	});
	mapGrid.clearGridData();
	mapGrid.jqGrid('footerData','set',logMapFooterChurningData[0]);
	mapGrid.jqGrid("setGridParam", { 'data': logMapChurningData }).trigger("reloadGrid");	
	
	trIndex =$(this).index();
	
	$("#maptreegrid tr:nth-child(2)").trigger('click');

});


//Click on maps/reduces
$("#maptreegrid tr").live('click',function(){	
	var jobTitle = $("#jobtreegrid tr:nth-child("+parseInt(trIndex+1)+")").find('.jobDetails').text();	
	var mapTitle = $(this).find('.classDetails').text();	
	var insTitle = $(this).find('.modal').text();
		
	// push instances
	var logJobChurningData=[];	
	var logMapChurningData=[];
	var logNodChurningData=[];
	var logInsChurningData=[];
	var logInsFooterChurningData=[];
	numOfInstance=0;
	numOfNodes=0;
	numOfMapReduce=0;

	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];
	if(logName == 'ErrorAndException' || logName == 'debuggerSummary')
	{
		return;
	}
	
		$.each(logMap, function(jobId, jobBean){
			if(jobId == jobTitle)
			{
				//Insert job name in breadcrum
				$('#breadcrumBox').html('<span id="jobBc">'+jobId+'<span class="raquo">&raquo;</span></span>');

				numOfJobs++;
				var totalInputKeys=jobBean["totalInputKeys"];
				var totalContextWrites=jobBean["totalContextWrites"];
				var totalUnmatchedKeys=jobBean["totalUnmatchedKeys"];
				if(totalUnmatchedKeys == '-1')
				{
					totalUnmatchedKeys = '-';
				}
				var totalUnmatchedValues=jobBean["totalUnmatchedValues"];
				if(totalUnmatchedValues == '-1')
				{
					totalUnmatchedValues = '-';
				}
				var jobMap=jobBean["jobMap"];
				

				var logChurningJsonObj = { "chain":" ", "id":99999,"elementName":"<div class='jobDetails'>"+jobId+"<form></form></div>","totalInputKeys":totalInputKeys,"totalContextWrites":totalContextWrites,"totalUnmatchedKeys":totalUnmatchedKeys,"totalUnmatchedValues":totalUnmatchedValues,level:"0", parent:"", isLeaf:false, expanded:false, loaded:true};			
				logInsFooterChurningData.push(logChurningJsonObj);
			}
		});
	});

	
	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];
	if(logName == 'ErrorAndException' || logName == 'debuggerSummary')
	{
		return;
	}

		$.each(logMap[jobTitle].jobMap, function(mapReduceName, mapReduceBean){
			numOfMapReduce++;	
			var valueArray = mapReduceName.split('.');
					
			if(mapTitle == valueArray[valueArray.length-1])
			{
				//Insert mapper/reducer name in breadcrum			
				$('#breadcrumBox').append('<span id="mapBc">'+mapTitle+'<span class="raquo">&raquo;</span></span>');

				var totalMapReduceInputKeys=mapReduceBean["totalInputKeys"];
				var totalMapReduceContextWrites=mapReduceBean["totalContextWrites"];
				var totalMapReduceUnmatchedKeys=mapReduceBean["totalUnmatchedKeys"];
				if(totalMapReduceUnmatchedKeys == '-1')
				{
					totalMapReduceUnmatchedKeys = '-';
				}
				var totalMapReduceUnmatchedValues=mapReduceBean["totalUnmatchedValues"];
				if(totalMapReduceUnmatchedValues == '-1')
				{
					totalMapReduceUnmatchedValues = '-';
				}
				var mapReduceMap=mapReduceBean["mapReduceMap"];
				var mapReduceJsonObj = { "chain":" ", "id":9999999, "id":numOfJobs+'_'+numOfMapReduce,"elementName":"<div class='classDetails'>"+mapReduceName+"<form></form></div>","totalInputKeys":totalMapReduceInputKeys,"totalContextWrites":totalMapReduceContextWrites,"totalUnmatchedKeys":totalMapReduceUnmatchedKeys,"totalUnmatchedValues":totalMapReduceUnmatchedValues,level:"1", parent:numOfJobs, isLeaf:false, expanded:false, loaded:true};	
				logInsFooterChurningData.push(mapReduceJsonObj);	
			

				$.each(mapReduceMap, function(nodeName, nodeBean){
					numOfNodes++;
					var nodeMap=nodeBean["nodeMap"];				
					logNodChurningData.push(nodeName);				
					var nodeColor = colorCodes[numOfNodes];

					$.each(nodeMap, function(instanceName, instanceBean){				
						numOfInstance++;
						var totalInstanceInputKeys=instanceBean["totalInputKeys"];
						var totalInstanceContextWrites=instanceBean["totalContextWrites"];
						var totalInstanceUnmatchedKeys=instanceBean["totalUnmatchedKeys"];
						if(totalInstanceUnmatchedKeys == '-1')
						{
							totalInstanceUnmatchedKeys = '-';
						}
						var totalInstanceUnmatchedValues=instanceBean["totalUnmatchedValues"];
						if(totalInstanceUnmatchedValues == '-1')
						{
							totalInstanceUnmatchedValues = '-';
						}
						var mapValueArray = mapReduceName.split('.');						
						var taskName=mapValueArray[mapValueArray.length-1]+"__"+instanceName;
						
						var instanceMap=instanceBean["instanceMap"];
						
						var instanceJsonObj = { "id":numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes+'_'+numOfInstance,"elementName":"<span class='legendBullets' style='background-color:"+nodeColor+"'></span><div class='modal' style='cursor:pointer'>"+instanceName+" <form><input type='hidden' name='elementName' value="+taskName+" id='elementName' /></form></div>","totalInputKeys":totalInstanceInputKeys,"totalContextWrites":totalInstanceContextWrites,"totalUnmatchedKeys":totalInstanceUnmatchedKeys,"totalUnmatchedValues":totalInstanceUnmatchedValues,level:"3", parent:numOfJobs+'_'+numOfMapReduce+'_'+numOfNodes, isLeaf:true, expanded:false, loaded:true};
						
						logInsChurningData.push(instanceJsonObj);		
					});
				});
			
			}
			else
				var mapReduceMap=mapReduceBean["mapReduceMap"];

		});
	});
	
	insGrid.clearGridData();	
	
	$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1)').remove();
	
	insGrid.jqGrid("setGridParam", { 'data': logInsChurningData }).trigger("reloadGrid");
	insGrid.jqGrid('footerData','set',logInsFooterChurningData[0]);
	var copyMapHtml = $(this).clone().html().replace(/maptreegrid/g, "instreegrid");	
	$(this).clone().insertAfter($('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0)'));
	$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1)').html().replace(/maptreegrid/g, "instreegrid");
	$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1)').removeClass().addClass('ui-widget-content footrow footrow-ltr');
	
	

	$('#nodeLegendBox').html('');
	for(var i=0;i<logNodChurningData.length;i++)
	{		
		$('#nodeLegendBox').append("<li><span class='legendBullets' style='background-color:"+colorCodes[i+1]+"'></span>"+logNodChurningData[i]+"</li>");
	}

	$('#insptreegrid').find('div.ui-jqgrid-sdiv td').show();
	$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0) td:eq(0), div.ui-jqgrid-sdiv tr:eq(0) td:eq(1)').hide();
	$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1) td:eq(0), div.ui-jqgrid-sdiv tr:eq(1) td:eq(1)').hide();

	// Set vertical scroller to Job grid
	var jobBDivHeight = $("#jobptreegrid .ui-jqgrid-bdiv").height();
	var jobBDivTableHeight = $("#jobptreegrid .ui-jqgrid-bdiv div:first").height();
	if(jobBDivTableHeight >= jobBDivHeight) {
		jobGrid.jqGrid("setGridParam", { 'scrollOffset': 18 });
	}

	// Set vertical scroller to Mappers/Reducers grid
	var mapBDivHeight = $("#mapptreegrid .ui-jqgrid-bdiv").height();
	var mapBDivTableHeight = $("#mapptreegrid .ui-jqgrid-bdiv div:first").height();
	if(mapBDivTableHeight >= mapBDivHeight) {
		mapGrid.jqGrid("setGridParam", { 'scrollOffset': 18 });
	}
	
	// Set vertical to Instance grid
	var insBDivHeight = $("#insptreegrid .ui-jqgrid-bdiv").height();
	var insBDivTableHeight = $("#insptreegrid .ui-jqgrid-bdiv div:first").height();
	if(insBDivTableHeight >= insBDivHeight) {
		insGrid.jqGrid("setGridParam", { 'scrollOffset': 18 });
	}
	
	
});

// Instance counter sub table 
var mapCounterData = new Array();
$(document).ready(function() {
	$("#jobtreegrid tr:nth-child(2)").trigger('click'); // default trigger first row click of jobs table

	$(".modal").live('click', function(){
		var serializedData=$(this).find('form').serialize();		
		var  tempName=serializedData.split('=');
		var mapElementName	=tempName[1].split('__')[0];
		var elementName	=tempName[1].split('__')[1];
		var insKeyValArr = [];
		
		
		//Insert instance name in breadcrum		
		if($('#breadcrumBox:contains('+elementName+')').length==0 && $('#breadcrumBox').find('#insBc').length==0)
		$('#breadcrumBox').append('<span id="insBc">'+elementName+'</span>');
		else
		$('#breadcrumBox').find('#insBc').html(elementName);
		
		$("#insSubCounter").show().find(".widget-header").html(elementName);
		

		$.each(logAnalysisJSONStringObj, function(logName, logBean){
		var logMap=logBean["logMap"];
		if(logName == 'ErrorAndException' || logName == 'debuggerSummary')
		{
			return;
		}		
			$.each(logMap, function(jobId, jobBean){
				var jobMap=jobBean["jobMap"];
				$.each(jobMap, function(mapReduceName, mapReduceBean){
					var mapReduceMap=mapReduceBean["mapReduceMap"];
					$.each(mapReduceMap, function(nodeName, nodeBean){
						var nodeMap=nodeBean["nodeMap"];
						$.each(nodeMap, function(instanceName, instanceBean){
							var mapValueArray = mapReduceName.split('.');

							insKeyValArr["totalUnmatchedKeys"]=instanceBean["totalUnmatchedKeys"];						
							insKeyValArr["totalUnmatchedValues"]=instanceBean["totalUnmatchedValues"];	
							
							if(mapElementName == mapValueArray[mapValueArray.length-1] && elementName == instanceName) {
								var instanceMap=instanceBean["instanceMap"];
								if(typeof instanceMap == 'undefined')
								{
									$('#taskCountersDiv ul:first').html('');
									$("#taskCountersTableWrap").html('<div class="status info"><span>Information: </span>No data available.</div>');
									if(instanceName.indexOf('_m_') > 0 ) {
										$("#treeRootName").html("Map");
									}else {
										$("#treeRootName").html("Reduce");
									}									
								 	return; 
								}
								var taskCounterData='';
								$.each(instanceMap, function(counterName, counterBean){
									var counterMap2=counterBean["counterMap"];
									mapCounterData[0] = counterMap2;									
									//console.log(counterName);
									if(counterName == "map") {
										$("#treeRootName").html("Map");
									}else {
										$("#treeRootName").html("Reduce");
									}
									
									taskCounterData += '<li id="phtml_1"><a class="rootmap" title="'+counterName+'" href="javascript:void(0);" onclick="javascript:createTreeTable(0, '+instanceBean["totalUnmatchedKeys"]+', '+instanceBean["totalUnmatchedValues"]+');"></a>';
									if(typeof counterMap2 != 'undefined'){
										taskCounterData +='<ul>';
										taskCounterData = createTreeNode(counterMap2, taskCounterData, instanceBean["totalUnmatchedKeys"], instanceBean["totalUnmatchedValues"]);
										taskCounterData +='</ul></li>';
									}
								});
								if(taskCounterData.length > 0) {
									$('#taskCountersDiv ul:first').html(taskCounterData);
								}
							}
						});
					});
				});	
			});
		});

		$("#taskCountersDiv").jstree({
			"plugins" : ["themes","html_data", "ui"],
			"core" : { "initially_open" : [ "phtml_1" ] }
		}).bind("before.jstree", function (e, data) {
			if(data.func === "close_node") {
				e.stopImmediatePropagation();
				return false;
			}
		});
		setTimeout(function () { $("#taskCountersDiv").jstree("open_all"); }, 500);
		$("#taskCountersDiv").find("a.rootmap").trigger("click");
	});
});

// Create tree
function createTreeNode(counterMap, taskCounterData, insTotalUnmatchedKeys, insTotalUnmatchedValues) {
	var dataType='';
	$.each(counterMap, function(counterName, counterBean){
		var counterMapObj = counterBean["counterMap"];
		dataType = counterBean["counterDetails"];
		var lngth = mapCounterData.length;
		mapCounterData[lngth] = counterMapObj;
		if(dataType == "method") {
			taskCounterData +='<li><a class="method" href="javascript:void(0);" title="'+counterName+'" onclick="javascript:createTreeTable('+lngth+', '+insTotalUnmatchedKeys+', '+insTotalUnmatchedValues+');"></a>';
		}else {
			taskCounterData +='<li><a class="counters" href="javascript:void(0);" title="'+counterName+' (Line no: '+counterBean["counterDetails"]+')" onclick="javascript:createTreeTable('+lngth+', '+insTotalUnmatchedKeys+', '+insTotalUnmatchedValues+');"></a>';
		}

		if(typeof counterMapObj != 'undefined') {
			taskCounterData +='<ul>';
			taskCounterData = createTreeNode(counterMapObj, taskCounterData, insTotalUnmatchedKeys, insTotalUnmatchedValues);
			taskCounterData +='</ul></li>';
		}
	});
	return taskCounterData;
}

// Create tree table
function createTreeTable(itemObj, insTotalUnmatchedKeys, insTotalUnmatchedValues) {
	var taskCounterData = new Array();
	if(mapCounterData[itemObj]) {
		$.each(mapCounterData[itemObj], function(counterKey, counterVal) {
			var totalFilteredIn = counterVal["totalFilteredIn"];
			var totalContextWrites = counterVal["totalContextWrites"];
			var totalUnmatchedKeys = counterVal["totalUnmatchedKeys"];
			if(totalUnmatchedKeys == '-1')
			{
				totalUnmatchedKeys = '-';
				if(insTotalUnmatchedKeys != '-1')
				{
					totalUnmatchedKeys = '0';
				}
			}
			var totalUnmatchedValues = counterVal["totalUnmatchedValues"];
			if(totalUnmatchedValues == '-1')
			{
				totalUnmatchedValues = '-';
				if(insTotalUnmatchedValues != '-1')
				{
					totalUnmatchedValues = '0';
				}
			}
			var totalFilteredOut = counterVal["totalFilteredOut"];
			var counterDetails = counterVal["counterDetails"];
				
			//if(counterDetails != "method") {
				var counterJsonObj = {"counterName":counterKey,"totalFilteredIn":totalFilteredIn,"totalContextWrites":totalContextWrites,"totalUnmatchedKeys":totalUnmatchedKeys,"totalUnmatchedValues":totalUnmatchedValues,"totalFilteredOut":totalFilteredOut};
				taskCounterData.push(counterJsonObj);
			//}
		});
	}else {
		taskCounterData = [];
	}

	var taskCounters = {"response": taskCounterData};
	if(jQuery.isEmptyObject(taskCounters["response"]) == true) {
		$("#taskCountersTableWrap").html('<div class="status info"><span>Information: </span>No data available.</div>');
		$("#taskCountersTableWrap .status").delay(300).fadeTo(150, 0.5).fadeTo(150, 1).fadeTo(150, 0.5).fadeTo(150, 1);
	}else {
		$("#taskCountersTableWrap").html('<table id="taskCountersTable"></table>');
	}

	$("#taskCountersTable").jqGrid({ 
		datastr: taskCounters,
		datatype: "jsonstring",
		height: "auto",
		hidegrid:false,			
		colNames:['Counter Name','Filtered In','Output Records','Unmatched Keys','Unmatched Values','Filtered From Inside'],
		colModel:[ 
					{name:'counterName',index:'counterName'},
					{name:'totalFilteredIn',index:'totalFilteredIn', width:70, align:'center'}, 
					{name:'totalContextWrites',index:'totalContextWrites', width:70, align:'center'},
					{name:'totalUnmatchedKeys',index:'totalUnmatchedKeys', width:70, align:'center'},
					{name:'totalUnmatchedValues',index:'totalUnmatchedValues', width:70, align:'center'},
					{name:'totalFilteredOut',index:'totalFilteredOut', width:70, align:'center'}
		],			
		rowNum:1000, 
		jsonReader: {
			repeatitems: false,
			root: "response"
		}
	});
	
}



function createDebugReportPieCharts() {
	// Chart View Start
	var chartViewContent='';
	var chartViewCounter=0;
	var mapReduceChartArr=new Array();
	var insChartArr=new Array();

	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];	
	if(logName == 'ErrorAndException' || logName == 'debuggerSummary')
	{
		return;
	}	
		$.each(logMap, function(jobId, jobBean) {
		
				var totalUnmatchedKeys=jobBean["totalUnmatchedKeys"];
				if(totalUnmatchedKeys == '-1')
				{
					totalUnmatchedKeys = '-';
				}
				var totalUnmatchedValues=jobBean["totalUnmatchedValues"];
				if(totalUnmatchedValues == '-1')
				{
					totalUnmatchedValues = '-';
				}
			
			chartViewContent = '<h3 class="accordion-heading"><table width="100%" cellpadding="0" cellspacing="0"><tr><td width="40%" align="left"><div class="seljob">'+jobId+'</div></td><td width="15%" align="center">'+jobBean["totalInputKeys"]+'</td><td width="15%" align="center" class="selCol">'+jobBean["totalContextWrites"]+'</td><td width="15%" align="center">'+totalUnmatchedKeys+'</td><td width="15%" align="center" class="last">'+totalUnmatchedValues+'</td></tr></table></h3><div class="accordion-content"><div class="debugreportchartholder" id="MapReduceChartHolder_'+chartViewCounter+'"></div><div class="debugreportchartholder" id="insChartHolder_'+chartViewCounter+'"></div><div class="charttitle">Mappers/Reducers</div><div class="charttitle">Instances</div></div>';
			$("#debugReportAccordion").append(chartViewContent);
			createMapReducePieChart("MapReduceChartHolder_"+chartViewCounter, jobId, jobBean, chartViewCounter);
			chartViewCounter++;
			chartViewContent = '';
		});
	});

	$("#debugReportAccordion").parent().append('<input id="makeReportAccordian" type="button" style="display:none;" value="make accordian" />');	
	$('#makeReportAccordian').click(function(){
		$("#debugReportAccordion").accordion();
		$('#makeReportAccordian').remove();
	});	
		setTimeout(function(){
			$('#makeReportAccordian').trigger("click");
			$('.accordion-heading').find('table').attr("height","100%");
		},4000);
}

function createMapReducePieChart(mrChartHolder, jobId, jobBean, counter) {
	var colorCounter = 0;
	var newY;
	var totalContextWrite = 0;
	mapReduceChartArr = new Array();
	mrColorCode = new Array();
	var colors = ["#4572A7", "#AA4643", "#89A54E", "#80699B", "#3D96AE", "#DB843D", "#92A8CD", "#A47D7C", "#B5CA92"];
	
	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];
	if(logName == 'ErrorAndException' || logName == 'debuggerSummary')
	{
		return;
	}
		$.each(logMap[jobId].jobMap, function(mapReduceName, mapReduceBean){
			totalContextWrite += mapReduceBean["totalContextWrites"];			
		});

		$.each(logMap[jobId].jobMap, function(mapReduceName, mapReduceBean){
			if(colorCounter < 1){
				newY = Math.round(((mapReduceBean["totalContextWrites"]*100)/totalContextWrite)*100)/100;
				mapReduceChartArr.push([
					mapReduceName,
					newY
				]);
			}else {
				mapReduceChartArr.push([
					mapReduceName,
					Math.round(((mapReduceBean["totalContextWrites"]*100)/totalContextWrite)*100)/100
				]);
			}
			mrColorCode.push(colors[colorCounter]);
			colorCounter++;
		});
	});
	
	if(isNaN(newY) == false) {
		// Create the chart
		$.jqplot.config.defaultWidth = 420;
		$.jqplot.config.defaultHeight = 250;
		$.jqplot.config.enablePlugins = true;
		mrPlot[counter] = jQuery.jqplot (mrChartHolder, [mapReduceChartArr], {
			seriesColors: mrColorCode,
			grid: {
				drawBorder: false,
				shadow: false,
				background: 'transparent'
			},
			seriesDefaults: {
				// Make this a pie chart.
				renderer: jQuery.jqplot.PieRenderer,
				rendererOptions: {
					// Put data labels on the pie slices.
					// By default, labels show the percentage of the slice.
					showDataLabels: true,
					sliceMargin: 1,
					startAngle: -90,
					dataLabelPositionFactor: 1.3,
					dataLabelFormatString:'%.2f %'
				},
				shadow: false
			},
			legend: { show:false }
		});

		$('#'+mrChartHolder).bind('jqplotDataClick', 
			function (ev, seriesIndex, pointIndex, data ) {
				iChartHolder = mrChartHolder.split('_');						
				createInstancePieChart("insChartHolder_"+iChartHolder[1],counter, jobId, jobBean, data[0], mrPlot[counter].series[seriesIndex].seriesColors[pointIndex]);
			}
		);

		customJQPlotTooltip('pie', mrChartHolder, mrPlot[counter], "%"); // chartHolder, var in which you store your jqPlot

		createInstancePieChart("insChartHolder_"+counter,counter, jobId, jobBean, mapReduceChartArr[0][0], mrColorCode[0]);
	}else {
		$("#"+mrChartHolder).siblings(".charttitle").hide();
		$("#"+mrChartHolder).parent("div").html('<div class="status info" style="margin-bottom:0px;"><span>Information: </span>No data available.</div>');
	}
}

function createInstancePieChart(iChartHolder,graphCounter, jobId, jobBean, mapId, mapColor) {
	var colorCounter = 0;
	insChartArr = new Array();
	instMapColorCode = new Array();
	var colors = ["#4572A7", "#AA4643", "#89A54E", "#80699B", "#3D96AE", "#DB843D", "#92A8CD", "#A47D7C", "#B5CA92"];

	$("#"+iChartHolder).html("");
	$.each(logAnalysisJSONStringObj, function(logName, logBean){
	var logMap=logBean["logMap"];
	if(logName == 'ErrorAndException' || logName == 'debuggerSummary')
	{
		return;
	}	
		$.each(logMap[jobId].jobMap, function(mapReduceName, mapReduceBean){			
			var mapReduceMap = mapReduceBean["mapReduceMap"];
						
			if(mapId && mapReduceName != mapId)
			{				
				return true;
			}
						

			if(colorCounter < 1) {
				$.each(mapReduceMap, function(nodeName, nodeBean){
					var nodeMap=nodeBean["nodeMap"];
					$.each(nodeMap, function(instanceName, instanceBean){
						var newY = Math.round(((instanceBean["totalContextWrites"]*100)/mapReduceBean["totalContextWrites"])*100)/100;
						if(isNaN(newY) == true) {
							newY = 0;
						}
						insChartArr.push([
							instanceName,
							newY
						]);
						instMapColorCode.push(mapColor);
						colorCounter++;
					});
				});
			}
		});
	});

	// Create the chart
	var colors = ["#4572A7", "#AA4643", "#89A54E", "#80699B", "#3D96AE", "#DB843D", "#92A8CD", "#A47D7C", "#B5CA92"];
	$.jqplot.config.defaultWidth = 455;
	$.jqplot.config.defaultHeight = 250;
	$.jqplot.config.enablePlugins = true;
	iPlot[graphCounter] = jQuery.jqplot (iChartHolder, [insChartArr], {
		seriesColors: instMapColorCode,
		grid: {
			drawBorder: false,
			shadow: false,
			background: 'transparent'
		},
		seriesDefaults: {
			// Make this a pie chart.
			renderer: jQuery.jqplot.PieRenderer,
			rendererOptions: {
				// Put data labels on the pie slices.
				// By default, labels show the percentage of the slice.
				showDataLabels: true,
				sliceMargin: 1,
				startAngle: 90,
				dataLabelPositionFactor: 1.3,
				dataLabelFormatString:'%.2f %'
			},
			shadow: false
		},
		legend: { show:false }
	});
	
	
	customJQPlotTooltip('pie', iChartHolder, iPlot[graphCounter], "%"); // chartHolder, var in which you store your jqPlot
}

$("#debugRptViewOpt").find("a.viewOpt").click(function() {
	$("#debugRptViewOpt").find("a.viewOpt").each(function() {
		$(this).find("span").removeClass("selView");
	});
	$(this).find("span").addClass("selView");
	var targetView = $(this).attr("ui:target");
	$("div.debugReportTblChartViewCnt").each(function() {
		$(this).css({"display":"none"});
	});
	$("div#"+targetView).css({"display":"block"});
	if(targetView == "debugreportPieChartView") {
		$("#debugReportAccordion").find("div.ui-accordion-content").each(function() {
			$(this).css({"height":"auto"});
		});
	}
	
	for(var k=0; k<mrPlot.length; k++){
		mrPlot[k].replot();
		iPlot[k].replot();
	}
	//iPlot.replot();
});

$("#jobsChainSorting").live("click", function (){
	$("#jobtreegrid").jqGrid('setGridParam',{sortname: 'id', sortorder: 'asc'});
	//$('#jobtreegrid').jqGrid("showCol", ["chain"]);
	$('#jobtreegrid').trigger("reloadGrid");
	$("#jobptreegrid").find(".jobChainIco").each(function(){
		$(this).css({"visibility":"visible"});
	});
	$("#jobtreegrid tr:nth-child(2)").trigger('click'); // default trigger first row click of jobs table
});

$("#mapsChainSorting").live("click", function (){
	$("#maptreegrid").jqGrid('setGridParam',{sortname: 'id', sortorder: 'asc'});
	//$('#maptreegrid').jqGrid("showCol", ["chain"]);
	$('#maptreegrid').trigger("reloadGrid");
	$("#mapptreegrid").find(".mapChainIco, .redChainIco").each(function(){
		$(this).css({"visibility":"visible"});
	});
	$("#maptreegrid tr:nth-child(2)").trigger('click'); // default trigger first row click of map table
});


// Chart View End
</script>