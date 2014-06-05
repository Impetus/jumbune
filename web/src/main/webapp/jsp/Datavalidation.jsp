<style>
.dvrightdatabox {float:right;border-left:solid 1px #ccc;width:60%}
#dvReportTablePager_left {width: 20px !important;}
#dvReportTablePager_center {width: 250px !important;}
</style>
<div class="pageTopPane">
	<h2 class="pageTitle">Data Violations</h2>
	<div id="dataValidationErrorLinks" class="errorLinkBox"></div>
</div>

<div class="widget-container" id="dataValidationWrap">
	<div class="widget-header">Data Violations Chart</div>
	<div class="widget-body">
		<div id="dvDataChart" class="dvdatabox"></div>
		<div id="dvDataViolations" class="dvdatabox"></div>
	</div>
</div>

<div style="clear:both"></div>

<div class="widget-container" id="dvFieldDataWrap">
	<div class="widget-header">Data Violations Table (<span id="voilationHeader"></span>) <a href="javascript:void(0);" id="resetDVFieldData" ui:target="">Reset</a></div>
	<div class="widget-body">
		<div class="dvdatabox">
			<table id="dvTable"></table>
		</div>
		<div class="dvrightdatabox">
			<table id="dvReportTable"></table>
			<div id="dvReportTablePager" ></div>
		</div>
		<div id="dvFieldChart" class="fright"></div>
	</div>
</div>
	

<script type="text/javascript">

	//var chart;
	var dataSum = 0;
	var fieldSum = 0;
	var dvInnerObjs = new Array();
	var dvInnerObjsTmp = new Array();
	var dvInnerObjsData = new Array();
	var dvInnerObjsDataTmp = new Array();
	var newDVArr = new Array();
	var DVCData = new Array();
	var fieldData = new Array();
	var selFieldData = new Array();	
	var DVCDataColor = new Array();
	var fieldDataColor = new Array();
	var selFieldDataColor = new Array();
	var violationsTblObj = '';
	var colors;
	var dataValidationJSON;
	var dataValidationErrorGridData=[];
	var errorCount = 0;
	var DVCDataLabel = new Array();
	var fieldDataLabel = new Array();

function enableDataValidationTab(dvJSON) {
	
	

	dataValidationJSON = jQuery.parseJSON(dvJSON);	
	console.log(dataValidationJSON);

if(dataValidationJSON["ErrorAndException"])
			{			
			$.each(dataValidationJSON, function(key, val) {	
				$.each(val, function(key, val){
					errorCount++;				
					var eachDataValidationErrorJobsJsonObj = { "id":errorCount,"jobName":key,"errorMsg":val};
					dataValidationErrorGridData.push(eachDataValidationErrorJobsJsonObj);
				
				});	
				$('#dataValidationErrorsBox').show();
				$('#dataValidationErrorLinks').html('<button id="errorBtn"><span>Failed Jobs</span></button>');
				
				//$('#summary-data-validation').find('.summary-debug-main').addClass("loaderMainBox");
				$('#summary-data-validation').find('.loaderMainBox').html('<div class="status error"><span>Information: </span>An error has been occurred while processing module, Please refer same module for more details.</div>').removeClass('loaderMainBox');
				return;
});			
}	
else if(jQuery.isEmptyObject(dataValidationJSON["DVSUMMARY"]) == true || dataValidationJSON["DVSUMMARY"] == '' || dataValidationJSON["DVSUMMARY"] == 'undefined' || dataValidationJSON["DVSUMMARY"] == null) {
		$("#dataValidationWrap .widget-header, #dataValidationWrap .widget-body").hide();
		$("#dataValidationWrap").append('<div class="status success"><span>No Violations Found</span></div>');//.removeClass("widget-container");
		$('#summary-data-validation').find('.loaderMainBox').html('<div class="status success"><span>No Violations Found</span></div>');
		$('#summary-data-validation').find('.loaderMainBox').children('.status').parent().removeClass("loaderMainBox");
		
	}else {
		$.each(dataValidationJSON, function(key, val) {		
			if(key == 'DVSUMMARY')
			{
				summaryHtml ='<table class="summary-datavalidation-table" width="420px" cellspacing="2" cellpadding="0">'
				$.each(val, function(summaryKey, summaryVal){
					summaryHtml += '<tr><td class="yellow">'+summaryKey+'</td><td class="pink">'+summaryVal+'</td></tr>';					
				});
				
				if(Object.keys(val).length==0){
					summaryHtml += '<tr><td class="yellow">No Data Violations Found</td></tr>';					
				}

				summaryHtml +='</table>'; 
				$('#summary-data-validation').find('.summary-debug-main').html(summaryHtml);
				$('#summary-data-validation').find('.loaderMainBox').removeClass('loaderMainBox');
				return;
			}


			$.each(val["fieldMap"], function(fldKey, fldVal){
				if(jQuery.inArray(("Field"+fldKey), dvInnerObjsTmp) == -1){
					dvInnerObjsTmp.push("Field"+fldKey);
					dvInnerObjsDataTmp.push(fldVal);
				}
			});
			
			fieldSum = val["totalViolations"];
			dataSum += val["totalViolations"];

			dvInnerObjs = dvInnerObjsTmp;
			dvInnerObjsTmp = new Array();
			dvInnerObjsData = dvInnerObjsDataTmp;
			dvInnerObjsDataTmp = new Array();
			
			newDVArr.push(
				[key, dvInnerObjs, dvInnerObjsData, fieldSum]
			);
			
		});
		
		//monocolors = ["#003354", "#006eb6", "#0091f0", "#19a4ff", "#54bbff", "#8ed3ff", "#7a64c1", "#8a76c8", "#302359"];
		//colors = ["#4572A7", "#AA4643", "#89A54E", "#80699B", "#3D96AE", "#DB843D", "#92A8CD", "#A47D7C", "#B5CA92"];
		colors = ["#DB843D", "#AA4643","#A47D7C","#80699B","#BA683F","#92A8CD","#A47D7C","#B5CA92"];
		
		buildDataArray();
		//console.log(newDVArr);
		violationsTblObj += "<table cellspacing='0'>";
		if(newDVArr.length>0){
			violationsTblObj += "<tr><th>&nbsp;</th><th>Type</th><th class='lastcol'>Number</th></tr>";
		}
		for(var k=0; k<newDVArr.length; k++){
			if(k==newDVArr.length-1){
				violationsTblObj += "<tr class='lastrow'>";
			}else{
				violationsTblObj += "<tr>";
			}
			violationsTblObj += "<td>";
			violationsTblObj += "<span style='background-color:"+colors[k]+"; width:20px; height:12px;float:left;'></span>";
			violationsTblObj += "</td>";
			violationsTblObj += "<td><a href='javascript:void(0);' onclick='javascript:fieldCounters("+'"'+newDVArr[k][0]+'"'+");'>";
			violationsTblObj += newDVArr[k][0];
			violationsTblObj += "</a></td>";
			violationsTblObj += "<td align='center' class='lastcol'>";
			violationsTblObj += newDVArr[k][3];
			violationsTblObj += "</td>";
			violationsTblObj += "</tr>";
		}
		violationsTblObj += "</table>";
		$("#dvDataViolations").html(violationsTblObj);
		
		if(newDVArr.length==0){		
			$("#dataValidationWrap .widget-header, #dataValidationWrap .widget-body").hide();
			$("#dataValidationWrap").append('<div class="yellow" style="padding:5px;">No Data Violations Found</div>');
		}
		

		// Create the chart	
		$.jqplot.config.defaultWidth = 600;
		$.jqplot.config.defaultHeight = 300;

				
	        var chart = $.jqplot('dvDataChart', [fieldData, DVCData], {
		seriesColors: DVCDataColor,		
		grid: {
			drawBorder: false,
			shadow: false,
			background: 'transparent'
		},
		seriesDefaults: {
		  // make this a donut chart.
		  renderer:$.jqplot.DonutRenderer,
		  shadow: false,		
		  rendererOptions: {			
				diameter: undefined, // diameter of pie, auto computed by default.				
				innerDiameter: 0,				
				padding: 20,        // padding between pie and neighboring legend or plot margin.
				
				sliceMargin: 1,     // gap between slices.
				fill: true,         // render solid (filled) slices.				
				shadowOffset: 2,    // offset of the shadow from the chart.
				shadowDepth: 5,     // Number of strokes to make when drawing shadow.  Each stroke
									// offset by shadowOffset from the last.
				shadowAlpha: 0.07,   // Opacity of the shadow
				showDataLabels: true
			}	
		},
		 series:[
			{
				seriesColors:fieldDataColor,				
				rendererOptions: {					
					dataLabelPositionFactor: 1.4,
					dataLabels: fieldDataLabel,
					dataLabelFormatString:'%.2f %'
				}
			},
			{
				seriesColors:DVCDataColor,
				rendererOptions: {					
					dataLabels: DVCDataLabel,
					dataLabelFormatString:'<span style="color:#fff;">%.2f %</span>'
				}
			}
		]
	  });

	  customJQPlotTooltip('donut','dvDataChart', chart, '%');
		
	  $('#dvDataChart').bind('jqplotDataClick',
			function (ev, seriesIndex, pointIndex, data) {	
				
				if(data[0].indexOf('::') > 0)	
				{	
					var newSplitData = data[0].split('::');
					//createFieldChart(newSplitData[0]);
					createTable(newSplitData[0]);					
				}
				else
				{
					//createFieldChart(data[0]);
					createTable(data[0]);
				}
			}
		);

	

	// error grid table start
	if(dataValidationErrorGridData)
	{
		$('<table id="dataValidationErrortable"></table>').appendTo('#dataValidationErrorDiv');		
		var dataValidationErrorJobsGridDataJson={"response": dataValidationErrorGridData};							    
		dataValidationErrorJobsGrid = jQuery("#dataValidationErrortable");
		dataValidationErrorJobsGrid.jqGrid({
			datastr: dataValidationErrorJobsGridDataJson,
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
	}
	}

$('#dataValidationErrortable tr:nth-child(even)').addClass("evenTableRow");

$("#resetDVFieldData").live("click", function() {
	//createFieldChart($(this).attr("ui:target"));
	createTable($(this).attr("ui:target"));
});

function fieldCounters(parentField)
{
	//createFieldChart(parentField);
	createTable(parentField);
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

function buildDataArray(dvKey) {
	DVCData = new Array();
	fieldData = new Array();
	selFieldData = new Array();
	DVCDataColor = new Array();
	fieldDataColor = new Array();
	selFieldDataColor = new Array();

	for(var m=0; m<newDVArr.length; m++) {
		fieldSum = 0;
		var DataPerc = Math.round((((newDVArr[m][3]*100)/dataSum*100)))/100;
		DVCDataLabel.push(DataPerc);

		DVCData.push([
			newDVArr[m][0],
			DataPerc
		]);
		DVCDataColor.push(colors[m]);
		for(var n=0; n<newDVArr[m][1].length; n++) {
			fieldSum += newDVArr[m][2][n];
		}
		for(var n=0; n<newDVArr[m][1].length; n++) {
			var brightness = 0.2 - (n / newDVArr[m][1].length) / 5 ;
			if(newDVArr[m][1][n] == "Field-1") {
				fieldData.push([
					newDVArr[m][0]+"::"+"",
					Math.round(((newDVArr[m][2][n]*DataPerc)/fieldSum)*100)/100
				]);
				fieldDataColor.push(colors[m]);
				
			}else {
				fieldData.push([
					newDVArr[m][0]+"::"+newDVArr[m][1][n],
					Math.round(((newDVArr[m][2][n]*DataPerc)/fieldSum)*100)/100
				]);
				fieldDataColor.push(colors[m]);
				
			}
			
			if(dvKey == newDVArr[m][0]) {
				if(newDVArr[m][1][n] == "Field-1") {
					selFieldData.push([
						"",
						Math.round(((newDVArr[m][2][n]*100)/fieldSum)*100)/100						
					]);
					selFieldDataColor.push(colors[m]);
				}else {
					selFieldData.push([
						newDVArr[m][1][n],
						Math.round(((newDVArr[m][2][n]*100)/fieldSum)*100)/100						
					]);					
					selFieldDataColor.push(colors[m]);
				}				
			}
			fieldDataLabel.push(Math.round(((newDVArr[m][2][n]*100)/fieldSum)*100)/100);
		}
	}
}

/*function createFieldChart(dvKey) {
	$("#dvFieldDataWrap").css({"display":"block"});
	$("#resetDVFieldData").attr("ui:target", dvKey);
	$("#resetDVFieldData").css({"display":"none"});
	buildDataArray(dvKey);
	
	// Create the chart		
	$.jqplot.config.defaultWidth = 400;
	$.jqplot.config.defaultHeight = 250;
	$('#dvFieldChart').html("");
	var chart2 = $.jqplot ('dvFieldChart', [selFieldData], { 
		  seriesColors: selFieldDataColor,
		  animate: true,       
		  animateReplot: true,
		  textColor:"#ffffff",
		  title: {
			text: dvKey
		},
		  grid: {
			drawBorder: false,
			shadow: false,
			background: 'transparent'
		},		 
		  seriesDefaults: {						
			// Make this a pie chart.
			renderer: jQuery.jqplot.PieRenderer,		
			shadow: false,		
			rendererOptions: {
				dataLabels: 'label',
				animation: {
					speed: 2000
				},
				diameter: undefined, // diameter of pie, auto computed by default.
				padding: 20,        // padding between pie and neighboring legend or plot margin.
				sliceMargin: 1,     // gap between slices.
				fill: true,         // render solid (filled) slices.				
				shadowOffset: 2,    // offset of the shadow from the chart.
				shadowDepth: 5,     // Number of strokes to make when drawing shadow.  Each stroke
									// offset by shadowOffset from the last.
				shadowAlpha: 0.07,   // Opacity of the shadow.
				startAngle: -90,
				showDataLabels: true,
				dataLabelPositionFactor: 1.3
			}
		},
		cursor: {
			style: 'pointer',     // A CSS spec for the cursor type to change the									
			show: true
		},
		 legend: {
			show: false					
			}		
		}
	  );
	
	customJQPlotTooltip('pie', 'dvFieldChart', chart2, '%');

	$('#dvFieldChart').bind('jqplotDataClick',
		function (ev, seriesIndex, pointIndex, data) {		
			$("#resetDVFieldData").css({"display":"block"});				
			filterFieldData(data[0]);		
		}
	);

}*/

var searchColumn;
var dvSelectedKey;
function createTable(dvKey) {
	dvSelectedKey = dvKey;	
	$("#voilationHeader").html(dvSelectedKey);
	$("#dvFieldDataWrap").css({"display":"block"});
	$("#resetDVFieldData").attr("ui:target", dvSelectedKey);
	$("#resetDVFieldData").css({"display":"none"});
	jQuery("#dvTable").jqGrid({
		datatype: "local",
		viewrecords: true,
		sortorder: "asc",
		sortable: true,
		sortname: 'fileName',
		width: 350,
		rowNum: 500000,
		height: 200,
		colNames: ["File Name", "Violations"],
		colModel: [			
			 
			{name:'fileName',index:'fileName', width:150, align:"center", formatter:function (cellvalue, options, rowObject) 	{
				return "<div class='fileNameBox' rel='"+dvSelectedKey+"'>"+cellvalue+"</div>";
			}}, 
			{name:'numOfViolations',index:'numOfViolations', width:150, align:"center", sorttype: "integer", formatter:function (cellvalue, options, rowObject) 	{
				return cellvalue == "-1" ? "-" : cellvalue;
			}}
		],
		gridComplete: function() {
			searchColumn = jQuery("#dvTable").jqGrid('getCol','numOfViolations',true) //needed for live filtering search
		}
	});
	jQuery("#dvTable").jqGrid().clearGridData();
	//console.log(dataValidationJSON[dvKey]["violationList"]);
	jQuery("#dvTable").jqGrid("setGridParam", {
			'data': dataValidationJSON[dvSelectedKey]["violationList"]
		}).trigger("reloadGrid");
}

function filterFieldData(fieldName) {
	var searchString = "-";
	if(fieldName != '')
	{
		searchString = fieldName.split("Field")[1];	
	}
	$.each(searchColumn,function() {
		
		if(this.value.toLowerCase().indexOf(searchString) == -1) {
			$("#dvTable").find('#'+this.id).hide();
		} else {
			$("#dvTable").find('#'+this.id).show();
		}
	})
}

$.jgrid.formatter.integer.thousandsSeparator='';

$(".fileNameBox").live('click', function () {
	
	var filename = $(this).text();
	var dvType =  $(this).attr('rel');	
	var yamlLocation=$('#var_yamlLocation').val();
	
	$("#dvReportTable").jqGrid({
		url:'DVReportServlet?fileName='+filename+"&dvType="+dvType+"&yamlLocation="+yamlLocation,		
		datatype:"json",				
		sortorder: "asc", sortable: true, sortname: 'lineNumber', width: 500, height: 200,
		colNames: ["Line Number", "Field Number", "Expected Value", "Actual Value"],
		colModel: [
			{name:'lineNumber',index:'lineNumber', width:120, align:"center", sorttype: "integer"}, 
			{name:'fieldNumber',index:'fieldNumber', width:120, align:"center", sorttype: "integer"},			 
			{name:'expectedValue',index:'expectedValue', width:120, align:"center"}, 
			{name:'actualValue',index:'actualValue', width:120, align:"center"}
		],
		rowNum:200, rowList: [100,200,300], jsonReader: { repeatitems : false, cell:""}, loadonce:false, mtype: "POST", 		
		pager: '#dvReportTablePager',
		viewrecords: true
	});
	
	 $("#dvReportTable").jqGrid("setGridParam", {url:'DVReportServlet?fileName='+filename+"&dvType="+dvType+"&yamlLocation="+yamlLocation}).trigger("reloadGrid",[{page:1}]);
});
	
	
	




</script>