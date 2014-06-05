
<div class="pageTopPane">
	<h2 class="pageTitle">Pure & Instrumented Jobs </h2>
	<div id="jobsErrorLinks" class="errorLinkBox"></div>
</div>

	

<div class="fleft" id="pureJobsMainContainer" style="display:none;">
	<div class="widget-container">
		<div class="widget-header">Pure Jobs</div>
		<div class="widget-body">
			<div id="pureJobsDiv" class="dvdatabox"></div>		
		</div>
	</div>
</div>
<div id="instrumentalJobsMainContainer" class="fleft" style="padding-left:75px;"  style="display:none;">
	<div class="widget-container">
		<div class="widget-header">Instrumented Jobs</div>
		<div class="widget-body">		
			<div id="instrumentalJobsDiv" class="dvdatabox"></div>
		</div>
	</div>
</div>


<script type="text/javascript">

var errorCount = 0;

function enablePureJobGrid(purJobsJson) { 	
	
	var pureJobsGridData=[];
	var pureJobsErrorGridData=[];	

	var numOfJobs=0;

	var purJobsJsonStringObj = $.parseJSON(purJobsJson);


	$.each(purJobsJsonStringObj, function(key, val){
		
		if(key == 'ErrorAndException')
		{			
			$.each(val, function(key, val){
				errorCount++;				
				var eachPureErrorJobsJsonObj = { "id":errorCount,"jobName":key,"errorMsg":val};
				pureJobsErrorGridData.push(eachPureErrorJobsJsonObj);				
			});				
			$('#pureJobsErrorsBox').show();
			$('#jobsErrorLinks').html('<button id="errorBtn"><span>Failed Jobs</span></button>');
			return;
		}
		var mainKey = key;
		$.each(val, function(key, val){

		numOfJobs++;
		
		var eachPureJobsJsonObj = { "id":numOfJobs,"jobs":mainKey,"counterName":key,"counterValue":val};

		pureJobsGridData.push(eachPureJobsJsonObj);

		});

	});

	if (pureJobsGridData) {
	$('#pureJobsMainContainer').show();
	$('<table id="pureJobstable"></table>').appendTo('#pureJobsDiv');
	var pureJobsGridDataJson={"response": pureJobsGridData};    								    
	pureJobsGrid = jQuery("#pureJobstable");
	pureJobsGrid.jqGrid({
		datastr: pureJobsGridDataJson,
		datatype: "jsonstring",
		height: 400,
		scroll: true,
		hidegrid:false,
		loadui: "disable",
		colNames:["ID","Job Name","Counter Name","Counter Value"], 
		colModel:
			[ 
				{name:'id',index:'elementName', width:30, align:"center"},
				{name:'jobs',index:'jobs', width:40},
				{name:'counterName',index:'counterName', width:320,align:"left", formatter:addPaddInVal}, 
				{name:'counterValue',index:'counterValue', width:80,align:"left", formatter:addPaddInVal}			
			],												
		rowNum: 10000,	
		grouping:true, 
		groupingView : 
			{ 
				groupField : ['jobs'], 
				groupColumnShow : [false] 
			},	
		jsonReader: {
		repeatitems: false,
		root: "response"
		}
	});
	
	$('#pureJobstable tr:nth-child(even)').addClass("evenTableRow");	
	
	}
	
	if(pureJobsErrorGridData){
		
	$('<table id="pureJobsErrortable"></table>').appendTo('#pureJobsErrorDiv');
	
	var pureErrorJobsGridDataJson={"response": pureJobsErrorGridData};    								    
	pureErrorJobsGrid = jQuery("#pureJobsErrortable");
	pureErrorJobsGrid.jqGrid({
		datastr: pureErrorJobsGridDataJson,
		datatype: "jsonstring",
		height: 'auto',
		hidegrid:false,
		loadui: "disable",
		colNames:["ID","Job Name","Error"], 
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

}

function enableInstrumentJobGrid(instrumentJobsJson){
	
	var insJobsGridData=[];	
	var insJobsErrorGridData=[];
	var instrumentJobsJsonStringObj = $.parseJSON(instrumentJobsJson);
	var instrumentNumOfJobs=0;	

	$('#errorBtn').live('click', function () { 

		$('#tabs').tabs("option","disabled", []);
		var tabsLength = $('#tabs').tabs("length");
		$('#tabs').tabs('select', parseInt(tabsLength - 1)); 
		$( "#tabs" ).tabs( "refresh" );
		

	});

	$.each(instrumentJobsJsonStringObj, function(key, val){
		
		if(key == 'ErrorAndException')
		{	var insErrorCount = 0;
			$.each(val, function(key, val){
				errorCount++;
				insErrorCount++;
				var eachInsErrorJobsJsonObj = { "id":insErrorCount,"jobName":key,"errorMsg":val};
				insJobsErrorGridData.push(eachInsErrorJobsJsonObj);
			});
			
			$('#instrumentalJobsErrorsBox').show();
			$('#jobsErrorLinks').append('<button id="errorBtn"><span>'+errorCount+' Errors</span></button>');
			
			return;
		}
		var mainKey = key;
		$.each(val, function(key, val){

		instrumentNumOfJobs++;
		
		var eachInsJobsJsonObj = { "id":instrumentNumOfJobs,"jobs":mainKey,"counterName":key,"counterValue":val};

		insJobsGridData.push(eachInsJobsJsonObj);

		});

	});
	
	if(insJobsGridData){
	$('#instrumentalJobsMainContainer').show();
	$('<table id="instrumentalJobstable"></table>').appendTo('#instrumentalJobsDiv');
	var insJobsGridDataJson={"response": insJobsGridData};    								    
	insJobsGrid = jQuery("#instrumentalJobstable");
	insJobsGrid.jqGrid({
		datastr: insJobsGridDataJson,
		datatype: "jsonstring",
		height: 400,
		scroll: true,
		hidegrid:false,
		loadui: "disable",
		colNames:["ID","Job Name","Counter Name","Counter Value"], 
		colModel:
			[ 
				{name:'id',index:'elementName', width:30, align:"center"},
				{name:'jobs',index:'jobs', width:40},
				{name:'counterName',index:'counterName', width:320,align:"left", formatter:addPaddInVal}, 
				{name:'counterValue',index:'counterValue', width:80,align:"left", formatter:addPaddInVal}			
			],												
		rowNum: 10000,
		grouping:true, 
		groupingView : 
			{ 
				groupField : ['jobs'], 
				groupColumnShow : [false] 
			},	
		jsonReader: {
		repeatitems: false,
		root: "response"
		}
	});
	//instrumental jobs End Here
	$('#instrumentalJobstable tr:nth-child(even)').addClass("evenTableRow");
	}
	
	if(insJobsErrorGridData){		
	
	$('<table id="insJobsErrortable"></table>').appendTo('#insJobsErrorDiv');	
	
	var insErrorJobsGridDataJson={"response": insJobsErrorGridData};    								    
	insErrorJobsGrid = jQuery("#insJobsErrortable");
	insErrorJobsGrid.jqGrid({
		datastr: insErrorJobsGridDataJson,
		datatype: "jsonstring",
		height: 'auto',
		hidegrid:false,
		loadui: "disable",
		colNames:["ID","Job Name","Error"], 
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

	
}

function addPaddInVal(cellvalue, options, rowObject)
{
	var paddVal = '<div class="paddLeftWithTextWrap">'+cellvalue+'</div>';
	return paddVal;
}	










</script>
