<style>
.dvrightdatabox {float:right;border-left:solid 1px #ccc;width:60%}
#dvReportTablePager_left {width: 20px !important;}
#dvReportTablePager_center {width: 250px !important;}
</style>
<!--div class="pageTopPane">
	<h2 class="pageTitle">DataProfiling</h2>
	<div id="dataValidationErrorLinks" class="errorLinkBox"></div>
</div-->
<!--div style="clear:both"></div-->

<div class="widget-container" id="dataProfilingFieldDataWrap">
<table id="records_table" border='1'>
<!--div id="gauge" style="width:100px; height:80px"></div-->
</table>
</div>

	<!--div class="widget-header">Data Profiling Table  <a href="javascript:void(0);" id="resetDVFieldData" ui:target="">Reset</a></div>
	<div class="widget-body">
		<div class="dvdatabox">
			<table id="dvTable"></table>
		</div>
		<div class="dvrightdatabox">
			<table id="dvReportTable"></table>
			<div id="dvReportTablePager" ></div>
		</div>
		<div id="dvFieldChart" class="fright"></div>
	</div-->

<script type="text/javascript">
var unmatchedValue=0;
var matchedvalue=0;
var total=0;
var columnProcessed = "false";
var maximum =0;
var matchedFieldNumber=0;
var unmatchedFieldNUmber =0;
function enableDataProfilingTab(dpJson)
{
	makeChartTable(dpJson);
	fillChartTable(dpJson);
} 
function fillChartTable(dpJson){
	var obj = jQuery.parseJSON(dpJson);
	var fieldCount = 1;
	var tabledGaugdID = '';
	var minimumMatchedKey = 0;
	var maximumMatchedKey = 0;
	var actualMatchedKey = 0;
	var Title ='';
	var fieldNumber = 0;
	var ruleName = '';
	$.each(obj,function(jsonKeyFieldNumber,matcheUnmatchedValuePair){
		fieldNumber = parseInt(jsonKeyFieldNumber);
		tabledGaugdID = "fieldCount"+fieldCount;
		Title = "Field-"+fieldNumber+" >3";
		$.each(matcheUnmatchedValuePair,function(matchedKey,unmatchedValue){
			if(matchedKey=='Rule'){
				ruleName = unmatchedValue;
			}else{
				actualMatchedKey = parseInt(matchedKey);
				maximumMatchedKey = parseInt(matchedKey)+ parseInt(unmatchedValue);
			}
		});
		Title = "Field-"+fieldNumber+"  ["+ruleName+"]";
		var g = new JustGage({
				id: tabledGaugdID,
				value: actualMatchedKey,
				min: minimumMatchedKey,
				max: maximumMatchedKey,
				title: Title,
				gaugeWidthScale: 0.5,
				levelColorsGradient: true,
				levelColors: ["#FE2E2E","#F89955","#298A08"]
		});
		fieldCount= fieldCount+1;
	});
	
}

function makeChartTable(dpJson){
	var obj = jQuery.parseJSON(dpJson);
	var trHTML='';
	var fieldCount = 0;
	var tabledGaugdID = '';
	var minimumMatchedKey = 0;
	var maximumMatchedKey = 0;
	var actualMatchedKey = 0;
	var Title ='';
	var fieldNumber = 0;
	$.each(obj,function(jsonKeyFieldNumber,matcheUnmatchedValuePair){
		console.log("1trHTML"+trHTML);
			if(fieldCount%2==0){
				trHTML= trHTML+'<tr>';
			}
			fieldPassedNumberumber = parseInt(jsonKeyFieldNumber);
			tabledGaugdID = "fieldCount"+(fieldCount+1);
			console.log("1field NUmber while parsing in gauge chart"+jsonKeyFieldNumber+"trHTML"+trHTML);
			Title = "Field-"+fieldNumber+" Matched keys"
			tabledGaugdID = "fieldCount"+(fieldCount+1);
			trHTML=trHTML+'<td width="50%"><div id="'+tabledGaugdID+'" style="width:450px; height:260px"> </div></td>	';
			if(fieldCount%2!=0){
				trHTML=trHTML+'</tr>';
			}
		fieldCount= fieldCount+1;
	});
	if(fieldCount%2==0){
		trHTML=trHTML+'</tr>';
	}
	$('#records_table').append(trHTML);
}

function makedChartsIntoReality(dpJson){
	var obj = jQuery.parseJSON(dpJson);
	var trHTML='';
	var fieldCount = 0;
	var tabledGaugdID = '';
	var minimumMatchedKey = 0;
	var maximumMatchedKey = 0;
	var actualMatchedKey = 0;
	var Title ='';
	var fieldNumber = 0;
	$.each(obj,function(jsonKeyFieldNumber,matcheUnmatchedValuePair){
		trHTML= trHTML+'<tr>';
		fieldPassedNumberumber = parseInt(jsonKeyFieldNumber);
		tabledGaugdID = "fieldCount"+fieldCount;
		console.log("field NUmber while parsing in gauge chart"+jsonKeyFieldNumber+"trHTML"+trHTML);
		Title = "Field-"+fieldNumber+" Matched keys";
		$.each(matcheUnmatchedValuePair,function(matchedKey,unmatchedValue){
			maximumMatchedKey = parseInt(matchedKey)+ parseInt(unmatchedValue);
			actualMatchedKey = parseInt(matchedKey);
			trHTML=trHTML+'<td></td><td><div id="'+tabledGaugdID+'" style="width:220px; height:160px"> </div></td> <td></td>';
		});
		trHTML =trHTML+ '</tr>';
		var g = new JustGage({
				id: tabledGaugdID,
				value: actualMatchedKey,
				min: minimumMatchedKey,
				max: actualMatchedKey,
				title: Title
			});
		fieldCount= fieldCount+1;
	});
	$('#records_table').append(trHTML);
		
}

function makeTablesForGauge(valueJson){
	var fieldPassedNumber = 0;
	var tableRowID ='';
	var trHTML ='';
	var obj = jQuery.parseJSON(valueJson);
	$.each(obj,function(jsonKeyFieldNumber,matcheUnmatchedValuePair){
		fieldNumber = parseInt(jsonKeyFieldNumber);
		tableRowID = "row"+fieldPassedNumber;
		if(fieldPassedNumber==0 || fieldPassedNumber%3==0){
			trHTML=trHTML+'<tr><td><table><tr><td><div id="heading'+fieldNumber+'" class="gauge-fieldnumber-textField">Field-'+fieldNumber+'</div></td></tr><td><div id="'+tableRowID+'"></div></td></tr></table></td>'; 			
		}else if(fieldPassedNumber%3==2){

			trHTML=trHTML+'<td ><table><tr><div id="heading'+fieldNumber+'" class="gauge-fieldnumber-textField">Field-'+fieldNumber+'</div></td></tr><td><div id="'+tableRowID+'"></div> </td></tr></table></td></tr>';
		}else{
			trHTML=trHTML+'<td ><table><tr><td><div id="heading'+fieldNumber+'" class="gauge-fieldnumber-textField">Field-'+fieldNumber+'</div></td></tr><td><div id="'+tableRowID+'"></div></td></tr></table></td>';
		}
		fieldPassedNumber=fieldPassedNumber+1;
	});
	if(fieldPassedNumber%3!=2) {
	trHTML = trHTML+"</tr>";
	}
	$('#records_table').append(trHTML);
}

	var fieldPassed=0;
function fillFieldGauge(valueJson){
	console.log("obj has been processsed funcation has been called");
	var obj = jQuery.parseJSON(valueJson);
	var fieldNumber =0;
	var mKey =0;
	var uValue =0;
	var maximum = 0;
	var tableRowID="";
	var tableColumnID="";
	var tableContainer= "";
	var trHTML = '';
//	var tableRowID='';
	var fieldPassed=0;
	$.each(obj,function(jsonKeyFieldNumber,matcheUnmatchedValuePair){
		fieldNumber = parseInt(jsonKeyFieldNumber);
		tableRowID = "row"+fieldPassed;
		$.each(matcheUnmatchedValuePair,function(matchedKey,unmatchedValue){
			FillDataInGauge(matchedKey,parseInt(unmatchedValue), fieldNumber,tableRowID);
		});
		fieldPassed =fieldPassed+1;
	});
//	$('#records_table').append(trHTML);
} 

function FillDataInGauge(matched, unmatched, fieldNumber,tableRowID){
	var mKey =0;
	var uValue =0;
	var maximum = 0;
	var f = 0;
	mkey= parseInt(matched);
	uValue = parseInt(unmatched);
	f = parseInt(fieldNumber);
	maximum = uValue+mkey;
	var matchedID = "matchedID"+f;
	var canvasID = "matched"+f;
	var previewDIVID="preview"+f;
	console.log(tableRowID);
	var rowID =document.getElementById(tableRowID);
	console.log(rowID);
	
	 $("#"+tableRowID).append('<div class="gauge-super-canvas-preview" id="'+previewDIVID+'"><canvas class="gauge-canvas-preview" id="'+canvasID+'"> </canvas>  <div class="gauge-preview-textfield" id ="'+matchedID+'"> </div> </div>');
	var opts = {
		  	lines: 20, // The number of lines to draw
		  	angle: 0, // The length of each line
		  	lineWidth: 0.15, // The line thickness
		  	pointer: {
		 	   length: 0.9, // The radius of the inner circle
		 	   strokeWidth: 1.0, // The rotation offset
		 	   color: '#C0C0DB' // Fill color
		 	},
		  	limitMax: 'false',   // If true, the pointer will not go past the end of the gauge
		  	colorStart: '#1CB02B',   // Colors
		  	colorStop: '#1CB02B',    // just experiment with them
		  	//strokeColor: '#1234EE',   // to see which ones work best for you
		  	generateGradient: false,
		  	strokeColor: '#EEEEEE'
	};
	target = document.getElementById(canvasID);
	gauge = new Donut(target).setOptions(opts); // create sexy gauge!
	gauge.maxValue = maximum; // set max gauge value
	gauge.animationSpeed = 32; // set animation speed (32 is default value)
	gauge.setTextField(document.getElementById(matchedID));
	gauge.set(mkey); // set actual value
	
}


function addValuesToGaugeMeter(jsonKey,jsonValue){


	 $('#gaugeContainer').append('<canvas id="'+jsonKey+'"> </div>');
	 console.log("gauge container has been called");
	 
	 var opts = {
			  lines: 12, // The number of lines to draw
			  angle: 0.35, // The length of each line
			  lineWidth: 0.1, // The line thickness
			  pointer: {
			    length: 0.9, // The radius of the inner circle
			    strokeWidth: 0.035, // The rotation offset
			    color: '#EEEEEE' // Fill color
			  },
			  limitMax: 'false',   // If true, the pointer will not go past the end of the gauge
			  colorStart: '#6F6EA0',   // Colors
			  colorStop: '#C0C0DB',    // just experiment with them
			  strokeColor: '#EEEEEE',   // to see which ones work best for you
			  generateGradient: false
			};
			var target;
			var gauge;
				target = document.getElementById(jsonKey); // your canvas element	
				gauge = new Donut(target).setOptions(opts); // create sexy gauge!
				gauge.maxValue = 3000; // set max gauge value
				gauge.animationSpeed = 32; // set animation speed (32 is default value)
				gauge.set(jsonValue); // set actual value
			}


</script>

