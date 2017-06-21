<div class="pageTopPane" style="background:none;">
	<h2 class="pageTitle">Tuning</h2>
	<div id="tuningErrorLinks" class="errorLinkBox"></div>
</div>

<div class="tuningwrap">
	<div id="tuning2dchart" class="tuningpropchartwrap"></div>
	<div class="clear"></div>
	<div id="exportButtons"></div>
	<div class="clear"></div>
	<div id="tuningCoefficients" class="tuningcoefficients widget-container"><div class="widget-header" id="itrTitle"></div><div class="widget-body"><ul></ul></div></div>
	<div class="clear"></div>
	<div class="tuningextimechartwrap">
		<div id="tuningExTimeChart" style="text-align:center;"></div>
	</div>
	<div class="clear"></div>
</div>

<script type="text/javascript">

	function enableTuningCharts(tuningJSON, t3DJSON) {
		createTuning3DChart(t3DJSON);
		createTuningMultiAxisChart(tuningJSON);
	}

	function createTuning3DChart(t3DJSON) {
		var tuningErrorGridData = [];
		var errorCount=0;

		t3DJSON = jQuery.parseJSON(t3DJSON);
		
		
		//error json
		
		if(typeof t3DJSON["ErrorAndException"] != 'undefined') {		
										
			$.each(t3DJSON["ErrorAndException"], function(key, val){
			
				errorCount++;				
				var eachTuningErrorJobsJsonObj = { "id":errorCount,"jobName":key,"errorMsg":val};
				tuningErrorGridData.push(eachTuningErrorJobsJsonObj);
				
			
			
			});	
			$('#tuningErrorsBox').show();
			$('#tuningErrorLinks').html('<button id="errorBtn"><span>Failed Jobs</span></button>');
						

			// error grid table start
			if(tuningErrorGridData.length > 0)
			{						
				$('<table id="tuningErrortable"></table>').appendTo('#tuningErrorDiv');		
				var tuningErrorGridDataJson={"response": tuningErrorGridData};
				
		
											    
				tuningErrorJobsGrid = $("#tuningErrortable");						
				tuningErrorJobsGrid.jqGrid({
					datastr: tuningErrorGridDataJson,
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
				}).trigger("reloadGrid");
			
				$('#tuningErrortable tr:nth-child(even)').addClass("evenTableRow");
				return;
			}
			// error grid table end
		}
				
		var numRows = t3DJSON["x"];
		var numCols = t3DJSON["y"];
		
		var tooltipStrings = new Array();
		var values = new Array();
		var idx = 0;

		for (var i = 0; i < numRows; i++) {
			values[i] = new Array();
			for (var j = 0; j < numCols; j++) {
				values[i][j] = t3DJSON["z"][idx];
				var z = Math.round(t3DJSON["z"][idx]/100)/10;
				tooltipStrings[idx] = "Execution Time:" + z;
				idx++;
			}
		}
		
		var data = {nRows: numRows, nCols: numCols, formattedValues: values};
		surfacePlot = new SurfacePlot(document.getElementById("tuningExTimeChart"));
		
		// Don't fill polygons in IE. It's too slow.
		var fillPly = !isIEFillPly();
		
		// Define a colour gradient.
		var colour1 = {red:0, green:0, blue:255};
		var colour2 = {red:0, green:255, blue:255};
		var colour3 = {red:0, green:255, blue:0};
		var colour4 = {red:255, green:255, blue:0};
		var colour5 = {red:255, green:0, blue:0};
		var colours = [colour1, colour2, colour3, colour4, colour5];
		
		// Axis labels.
		var xAxisHeader	= "X-axis";
		var yAxisHeader	= "Y-axis";
		var zAxisHeader	= "Execution Time";
		
		var renderDataPoints = false;
		var background = '#ffffff';
		var axisForeColour = '#000000';
		var hideFloorPolygons = true;
		var chartOrigin = {x: 250, y:250};
		
		// Options for the basic canvas pliot.
		var basicPlotOptions = {fillPolygons: fillPly, tooltips: tooltipStrings, renderPoints: renderDataPoints }
		
		// Options for the webGL plot.
		var xLabels = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, ,13, 14, 15, 16, 17, 18, 19, 20];
		var yLabels = [0, 1, 2, 3, 4, 5];
		var zLabels = [0, 1, 2, 3, 4, 5, 6]; // These labels ar eused when autoCalcZScale is false;
		var glOptions = {xLabels: xLabels, yLabels: yLabels, zLabels: zLabels, chkControlId: false ,autoCalcZScale: false};
		
		// Options common to both types of plot.
		var options = {xPos: 0, yPos: 0, width: 500, height: 350, colourGradient: colours, 
			xTitle: xAxisHeader, yTitle: yAxisHeader, zTitle: zAxisHeader, 
			backColour: background, axisTextColour: axisForeColour, hideFlatMinPolygons: hideFloorPolygons, origin: chartOrigin};
		
		surfacePlot.draw(data, options, basicPlotOptions, glOptions);

		// Link the two charts for rotation.
		var plot1 = surfacePlot.getChart();
	}

	function isIEFillPly() {
		return /msie/i.test(navigator.userAgent) && !/opera/i.test(navigator.userAgent);
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

	function createTuningMultiAxisChart(tuningJSON) {
		tuningJSON = jQuery.parseJSON(tuningJSON);		
		var tuningItrCatArr = new Array();
		var tuningSeriesFinalArr = new Array();
		var tuningSeriesArr = new Array();	
		var tuningSeriesArr2 = new Array();
		var tuningSeriesArr3 = new Array();
		var tuningSeriesArr4 = new Array();
		var tuningExcTimeArr = new Array();
		var tuningPropKeyArr = new Array();
		var tuningPropDataArr = new Array();
		var tuningPropDataTypeArr = new Array();
		var tuningErrorGridData = [];
		var errorCount=0;
		tuningPropKeyArr.push('Execution Time');

		$.each(tuningJSON, function(itrKey, itrObj) {
			
			//error json
			if(itrKey == 'ErrorAndException')
			{							
				$.each(itrObj, function(key, val){
					
					errorCount++;				
					var eachTuningErrorJobsJsonObj = { "id":errorCount,"jobName":key,"errorMsg":val};
					tuningErrorGridData.push(eachTuningErrorJobsJsonObj);
					
					
					
				});	
				$('#tuningErrorsBox').show();
				$('#tuningErrorLinks').html('<button id="errorBtn"><span>Failed Jobs</span></button>');
				//return;
			}

			// error grid table start
			if(tuningErrorGridData.length > 0)
			{						
				$('<table id="tuningErrortable"></table>').appendTo('#tuningErrorDiv');		
				var tuningErrorGridDataJson={"response": tuningErrorGridData};
				
			
											    
				tuningErrorJobsGrid = $("#tuningErrortable");						
				tuningErrorJobsGrid.jqGrid({
					datastr: tuningErrorGridDataJson,
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
				}).trigger("reloadGrid");
				
				$('#tuningErrortable tr:nth-child(even)').addClass("evenTableRow");
				return;
			}
			// error grid table end
		
			tuningItrCatArr.push(itrKey+1);
			tuningExcTimeArr.push(itrObj["executionTimeInMsecs"]/1000);
			
			var tuningCnt = 0;
			
			$.each(itrObj["coefficientNameValuePair"], function(propKey, propVal) {
				if(/^[+-]?\d+(\.\d+)?([eE][+-]?\d+)?$/.test(propVal)) {
					if(!tuningPropDataArr[tuningCnt]) {
						tuningPropDataArr.push([]);
						tuningPropKeyArr.push(propKey);
					}
					if(propVal.indexOf(".") != -1) {
						tuningPropDataTypeArr.push("Float");
						tuningPropDataArr[tuningCnt].push(parseFloat(propVal));
					}else if(parseInt(propVal) < 500) {
						tuningPropDataTypeArr.push("Int");
						tuningPropDataArr[tuningCnt].push(parseInt(propVal));
					}else {
						tuningPropDataTypeArr.push("Double");
						tuningPropDataArr[tuningCnt].push(parseInt(propVal));
					}
					tuningCnt++;
				}
			});
		});
		//console.log(tuningPropDataArr);		
		tuningSeriesArr.push(
			
			tuningExcTimeArr
			
		);

		for(var i=0; i<tuningPropDataArr.length; i++) {
			var brightness = 0.4 - (i / tuningPropDataArr[i].length) / 8 ;
			if(tuningPropDataTypeArr[i] == "Float") {				
					tuningSeriesArr.push(											
						tuningPropDataArr[i]					
					);				
			}else if(tuningPropDataTypeArr[i] == "Int") {
				tuningSeriesArr.push(											
						tuningPropDataArr[i]				
					);
			}else if(tuningPropDataTypeArr[i] == "Double") {
				tuningSeriesArr.push(											
						tuningPropDataArr[i]					
					);
			}
		}		
		var tuningChartHt = (tuningPropKeyArr.length*20+30); /*for tuning graph bug */
		$('#tuning2dchart').height(tuningChartHt);				/*for tuning graph bug */
			
		  // Create the chart
		 $.jqplot.config.enablePlugins = true;
		 $.jqplot.config.defaultWidth = 975;
		 $.jqplot.config.defaultHeight = 400;
		  var plot3 = $.jqplot('tuning2dchart', tuningSeriesArr, 
			{ 
			 grid: {
					drawBorder: false,
					shadow: false,
					background: 'rgba(0,0,0,0)'
				},
			  title:'Line Style Options', 
					
			  // Set default options on all series, turn on smoothing.
			  seriesDefaults: {
				  rendererOptions: {
					  smooth: true
				  }
			  },
			  axes: {
				// options for each axis are specified in seperate option objects.
				xaxis: {
				  renderer:$.jqplot.LinearAxisRenderer,
				  label: "Iteration",
				  labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
				  // Turn off "padding".  This will allow data point to lie on the
				  // edges of the grid.  Default padding is 1.2 and will keep all
				  // points inside the bounds of the grid.
				  
				  ticks: tuningItrCatArr,
				  rendererOptions: {
					  smooth: true
				  },
				  labelOptions: {
							textColor: "#333",
							fontSize: 13
						},
				  tickOptions:{
							formatString: '%d', 
							textColor: "#333",
							fontSize: 10
						}
				},
				yaxis: {					 
						label: "Execution Time(sec)",
						labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
						autoscale:true,
						rendererOptions: {
						  smooth: true
						},							
						labelOptions: {
							textColor: "#333",
							marginLeft:50,
							fontSize: 13
						},
							tickOptions: {
							textColor: "#333",
							fontSize: 10
						}

					},
				y2axis: {					 
						label: "Float Values",
						labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
						autoscale:true,
						rendererOptions: {
						  smooth: true
						},						
						labelOptions: {
							textColor: "#89A54E",
							fontSize: 13
						},
							tickOptions: {
							textColor: "#89A54E",
							fontSize: 10
						}					  
					},
				y3axis: {					 
						label: "Int Values",
						labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
						autoscale:true,
					    rendererOptions: {
						  smooth: true
					    },						
						labelOptions: {
							textColor: "#ba55d3",
							fontSize: 13

						},
							tickOptions: {
							textColor: "#ba55d3",
							fontSize: 10
						}					  
					},
				y4axis: {					 
						label: "Double Values",	
						labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
						autoscale:true,
					    rendererOptions: {
						  smooth: true
					    },						
						labelOptions: {
							textColor: "#1e90ff",
							fontSize: 13
						},
							tickOptions: {
							textColor: "#1e90ff",
							fontSize: 10
						}					  
					}
			  },
			  // Series options are specified as an array of objects, one object
			  // for each series.
			  series:[ 
				  {
					// Change our line width and use a diamond shaped marker.
					lineWidth:2, 
					yaxis:"yaxis",
					markerOptions: { style:'dimaond' }
				  }, 
				  {
					// Don't show a line, just show markers.
					// Make the markers 7 pixels with an 'x' style
					lineWidth:2, 
					
					yaxis:"y2axis",
					markerOptions: { size: 7, style:"x" }
				  },
				  { 
					// Use (open) circlular markers.
					lineWidth:2, 
					
					yaxis:"y3axis",
					markerOptions: { style:"circle" }
				  }, 
				  {
					// Use a thicker, 5 pixel line and 10 pixel
					// filled square markers.
					lineWidth:2, 					
					yaxis:"y4axis",
					markerOptions: { style:"filledSquare", size:10 }
				  }
			  ],
			  legend:{
				show:true,				
				labels:tuningPropKeyArr,
				rowSpacing: '0.05em',
				marginTop :0,
				placement: "outsideGrid",				
				renderer: $.jqplot.EnhancedLegendRenderer,
					rendererOptions: {
						disableIEFading: false,						
						seriesToggle: 200,
						seriesToggleReplot: { resetAxes: true }
					}
			  }
			}
		  );

		  $("div.tuningwrap").resizable({delay:20});

                  $("div.tuningwrap").bind("resize", function(event, ui) {
                     plot3.replot();
                  });

		  $('#tuning2dchart .jqplot-yaxis-label').css({left:'5px'});

		  customJQPlotTooltip('line', 'tuning2dchart', plot3, '');		 
		 
		  $('#tuning2dchart').bind('jqplotDataClick', function (ev, seriesIndex, pointIndex, data) { 			  
			  createTuningCoefficientsList(pointIndex, tuningJSON);
		});
		  $('#tuning2dchart table.jqplot-table-legend').find('tr').each(function() {
				if($(this).text() != "Execution Time") {
					$(this).find("td:first").trigger('click')
				} else {
					$(this).find("td").trigger('click')
				}
			});	
	}
	
	var commandLineArguments;
	var coreParameters;
	var mapredParameters;
	var hdfsParamteres;
	
	function createTuningCoefficientsList(targetId, tuningJSON) {
		$("#tuningCoefficients").css({"display":"block"});
		$("#tuningCoefficients").find("#itrTitle").html("Iteration - "+(targetId+1));
		$("#tuningCoefficients ul:first").html("");
		
		commandLineArguments = "";
		coreParameters = "";
		mapredParameters = "";
		hdfsParamteres = "";
		
		$.each(tuningJSON[targetId]["coefficientNameValuePair"], function(cKey, cVal) {
			$("#tuningCoefficients ul:first").append('<li><label title="'+cKey+'" class="clabel">'+cKey+'</label><label title="'+cVal+'" class="cval">'+cVal+'</label></li>');
			addContent(cKey, cVal);	
		});
		$("#exportButtons").html('<a class="tuningButton leftSideFloat" id="commandLineArgumentsButton" onclick="createInputBox()">Copy command line parameters</a>');
		$("#exportButtons").append('<a class="tuningButton rightSideFloat" id="exportAsXMLButton" onclick="downloadFile()">Export Properties</a>');
		
	}

	function addContent(cKey, cVal) {
		commandLineArguments = commandLineArguments + ' -D ' + cKey + '=' + cVal;
		
		if (coreSite.indexOf(cKey) != -1) {
			coreParameters = coreParameters + '<property>\n\t<name>'+cKey+'</name>\n\t<value>'+cVal+'</value>\n</property>\n';
		} else if (hdfsSite.indexOf(cKey) != -1) {
			hdfsParamteres = hdfsParamteres + '<property>\n\t<name>'+cKey+'</name>\n\t<value>'+cVal+'</value>\n</property>\n';
		} else {
			mapredParameters = mapredParameters + '<property>\n\t<name>'+cKey+'</name>\n\t<value>'+cVal+'</value>\n</property>\n';
		}
	}
	
	function createInputBox() {
		$("#commandLineArgumentsButton").replaceWith('<div class="commandLineArgumentsInputBox leftSideFloat"><input type="text" value="'+commandLineArguments+'" onfocus="this.select();" style="width:700px;" onmouseup="return false;" spellcheck="false"/></div>');
	}
	
	function downloadFile() {
		var a = document.createElement('a');
		var fileName = "Hadoop Configuration Parameters.txt";
		contentType =  'data:application/octet-stream,';
		var fileContent = getExportFileContent();
		uriContent = contentType + encodeURIComponent(fileContent);
		a.setAttribute('href', uriContent);
		a.setAttribute('download', fileName);
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
	}
	
	function getExportFileContent() {
		var fileContent = "Please configure the following properties in accordance with the file name\n\n"
										+ "/***********************core-site.xml***********************/\n"
										+ coreParameters + "\n\n"
										+ "/***********************hdfs-site.xml************************/\n"
										+ hdfsParamteres + "\n\n"
										+ "/**********************mapred-site.xml**********************/\n"
										+ mapredParameters;
		return fileContent;
	}
	
	function createQuickTuningTable(quickTuningJSON) {
		quickTuningJSON = jQuery.parseJSON(quickTuningJSON);
		$("#tuning2dchart").remove();
		$("#tuningCoefficients").css({"display":"block"});
		$("#tuningCoefficients").find("#itrTitle").html("Tuning Parameters");
		$("#tuningCoefficients ul:first").html("");
		
		commandLineArguments = "";
		coreParameters = "";
		mapredParameters = "";
		hdfsParamteres = "";
		
		$.each(quickTuningJSON, function(cKey, cVal) {
			$("#tuningCoefficients ul:first").append('<li><label title="'+cKey+'" class="clabel">'+cKey+'</label><label title="'+cVal+'" class="cval">'+cVal+'</label></li>');
			addContent(cKey, cVal);	
		});
		$("#exportButtons").html('<a class="tuningButton leftSideFloat" id="commandLineArgumentsButton" onclick="createInputBox()">Copy command line arguments</a>');
		$("#exportButtons").append('<a class="tuningButton rightSideFloat" id="exportAsXMLButton" onclick="downloadFile()">Export Properties</a>');
		
	}
	
	$(document).ready(function () {			
		
		//$('body').find('table tr td.jqplot-table-legend:first').trigger('click');
	});
	
	var hdfsSite = ["dfs.namenode.handler.count", "dfs.datanode.handler.count", "dfs.blocksize"];
	
	var coreSite = ["io.file.buffer.size", "io.seqfile.compression.type"];
	
	var mapredSite = ["io.sort.factor",
	                   "io.sort.mb",
	                   "io.sort.record.percent",
	                   "io.sort.spill.percent",
	                   "mapred.child.java.opts",
	                   "mapred.compress.map.output",
	                   "mapred.job.reduce.input.buffer.percent",
	                   "mapred.job.reduce.input.buffer.percent",
	                   "mapred.job.reuse.jvm.num.tasks",
	                   "mapred.job.shuffle.input.buffer.percent",
	                   "mapred.job.shuffle.merge.percent",
	                   "mapred.map.child.java.opts",
	                   "mapred.max.split.size",
	                   "mapred.min.split.size",
	                   "mapred.output.compress",
	                   "mapred.output.compression.codec",
	                   "mapred.output.compression.type",
	                   "mapred.reduce.child.java.opts",
	                   "mapred.reduce.parallel.copies",
	                   "mapred.reduce.tasks",
	                   "mapred.tasktracker.map.tasks.maximum",
	                   "mapred.tasktracker.reduce.tasks.maximum",
	                   "mapreduce.input.fileinputformat.split.maxsize",
	                   "mapreduce.input.fileinputformat.split.minsize",
	                   "mapreduce.job.jvm.numtasks",
	                   "mapreduce.job.reduces",
	                   "mapreduce.map.java.opts",
	                   "mapreduce.map.memory.mb",
	                   "mapreduce.map.output.compress",
	                   "mapreduce.map.output.compress.codec",
	                   "mapreduce.map.sort.spill.percent",
	                   "mapreduce.output.fileoutputformat.compress",
	                   "mapreduce.output.fileoutputformat.compress.type",
	                   "mapreduce.reduce.input.buffer.percent",
	                   "mapreduce.reduce.java.optsdfs.block.size",
	                   "mapreduce.reduce.memory.mb",
	                   "mapreduce.reduce.shuffle.input.buffer.percent",
	                   "mapreduce.reduce.shuffle.merge.percent",
	                   "mapreduce.reduce.shuffle.parallelcopies",
	                   "mapreduce.task.io.sort.factor",
	                   "mapreduce.task.io.sort.mb",
	                   "mapreduce.tasktracker.http.threads",
	                   "mapreduce.tasktracker.map.tasks.maximum",
	                   "mapreduce.tasktracker.reduce.tasks.maximum",
	                   "tasktracker.http.threads"];
	
</script>