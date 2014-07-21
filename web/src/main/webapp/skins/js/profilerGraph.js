(function(jvmGraph) {
	var bodyNode = d3.select('body').node();	 //No mapper information displayed on hover in JVM profiler.
	var jvmConfig = { renderer:'#jvmGraphContainer', jvmSvg:'', barG:'', lineG:'', xAxisSVG:'', yAxisSVG:'', legendsSVG:'', svgW:800, svgWrapH:0, svgH:450, barH:30, totalTime:0, barRows:0, marginR: 20, marginT:15, marginL: 60, marginB:75, vScrollMargin:5, hScrollMargin:20, legendsW:165, barColors:["#2f7ed8", "#c43333", "#afe240", "#9ecf34", "#8bbc21", "#2b4158"], barLegends:["Setup", "Mapper", "Shuffle", "Sorter", "Reducer", "Cleanup"], lineColors:["#17bfee", "#b954e3", "#0d233a"], lineLegends:["Avg. CPU Usage(%)", "Avg. Memory Usage(%)", "Avg. Data Flow Rate(kb/sec)"], xAxisTitle:'Execution Time', yAxisTitle:'Percentage Utilization'};
	
	jvmGraph.initJVMGraph = function(data) {
		$("<div>").attr("id", "jvmGraphWrap").css({"width":jvmConfig.svgW, "height":jvmConfig.svgH, "position":"relative"}).appendTo(jvmConfig.renderer);
		$('#jvmGraphWrap').append('<div class="hint"></div>');
		jvmConfig.svgW = jvmConfig.svgW - jvmConfig.marginL;
		jvmConfig.svgH = jvmConfig.svgH - jvmConfig.marginB - jvmConfig.marginT;
		jvmConfig.totalTime = data.graphData.totalTime;
		jvmConfig.svgW = jvmConfig.svgW - jvmConfig.legendsW;
		$("<div>").attr("id", "jvmGraph").attr("class", "jvm-graph").css({"width":(jvmConfig.svgW), "height":jvmConfig.svgH, "position":"absolute", "top":jvmConfig.marginT, "left":jvmConfig.marginL, "z-index": 1001}).appendTo("#jvmGraphWrap");
		jvmConfig.svgWrapH = jvmConfig.svgH;
		$.each(data.graphData.phaseOutput, function(key, obj) {
			if ( obj.taskOutputDetails.length > jvmConfig.barRows ) {
				jvmConfig.barRows = obj.taskOutputDetails.length;
			}
		});
		
		if( jvmConfig.barRows*jvmConfig.barH > jvmConfig.svgH ) {
			jvmConfig.svgH = (jvmConfig.barRows*jvmConfig.barH);
		}
		jvmConfig.svgW = jvmConfig.svgW - jvmConfig.marginR;
		jvmConfig.svgH = jvmConfig.svgH - jvmConfig.vScrollMargin + jvmConfig.marginT;
		jvmConfig.jvmSvg = d3.select("#jvmGraph").append("svg").attr("width", jvmConfig.svgW).attr("height", jvmConfig.svgH);
		jvmConfig.barG = jvmConfig.jvmSvg.append("g")
						.attr("class", "bar-g").attr("id", "jvmBarG")
						.attr("transform", "translate(0, 0)");
		jvmConfig.lineG = jvmConfig.jvmSvg.append("g")
						.attr("class", "line-g").attr("id", "jvmLineG")
						.attr("transform", "translate(0, 0)");
		jvmConfig.xAxisSVG = d3.select("#jvmGraphWrap").append("svg").attr("width", jvmConfig.svgW).attr("height", jvmConfig.marginB).style("position", "absolute").style("left", jvmConfig.marginL+"px").style("top", jvmConfig.svgWrapH+"px");
		jvmConfig.yAxisSVG = d3.select("#jvmGraphWrap").append("svg").attr("width", jvmConfig.marginL).attr("height", jvmConfig.svgWrapH+jvmConfig.marginT+jvmConfig.vScrollMargin).style("position", "absolute").style("left", "0").style("top", "0");
		jvmConfig.legendsSVG = d3.select("#jvmGraphWrap").append("svg").attr("width", jvmConfig.legendsW).attr("height", jvmConfig.svgH).style("position", "absolute").style("left", (jvmConfig.svgW+jvmConfig.marginL+jvmConfig.hScrollMargin)+"px").style("top", (jvmConfig.marginT)+"px");
		jvmConfig.svgW = jvmConfig.svgW - jvmConfig.marginR;
		createJVMBarGraph(data); // Create Bar Graph
		jvmConfig.svgH = jvmConfig.svgH - jvmConfig.marginT;
		createJVMLineGraph(data); // Create Line Graph
		createJVMGraphLegends(); // Create Legends
		
		$("#jvmGraph").scrollTop(jvmConfig.svgH + jvmConfig.marginB + jvmConfig.marginT);
		$("#jvmGraph").scroll(function() {
			var topPos = $(this).scrollTop();
			$("g#jvmLineG").attr("transform", "translate(0, "+ (topPos-10) +")");
		});
	};

	jvmGraph.createJVMBarGraph = function(data) {
		var colorCnt = 0;
		$.each(data.graphData.phaseOutput, function(phaseKey, phaseObj) {
			var i=0;
			for (; i<phaseObj.taskOutputDetails.length; i++) {
				var taskObj = phaseObj.taskOutputDetails[i];
				if (phaseKey != "reduceDetails") {
					var startPoint = taskObj.startPoint;
					var endPoint = taskObj.endPoint;
					var pointDiff = endPoint - startPoint;
					var pointPerc = (pointDiff*100)/jvmConfig.totalTime;
					var actBarW = (pointPerc*jvmConfig.svgW)/100;
					var startPointPerc = (startPoint*100)/jvmConfig.totalTime;
					var actStartPoint = (startPointPerc*jvmConfig.svgW)/100;
					var titleMsg = taskObj.taskID + ", Start Point: " + startPoint + ", End Point: " + endPoint; 
					jvmConfig.barG.append("rect")
						.attr("class", "bar-rect hint--bottom")
						.attr("height", jvmConfig.barH)
						.attr("fill", jvmConfig.barColors[colorCnt])
						.attr("x", actStartPoint)
						.attr("y", jvmConfig.svgH - (jvmConfig.barH*(i+1)))
						.attr("data-hint", titleMsg)
						.style("fill-opacity", "0.75")
						.on("mouseover", function() {
							var obj = d3.select(this);
							showTooltip(d3.select(this).attr("data-hint"),d3.select(this).attr("x"),d3.select(this).attr("y"));
							jvmConfig.barG.append("rect").attr("class", "hint--bottom").attr("id", "hoverObj").attr("x", obj.attr("x")).attr("y", obj.attr("y"))
								.attr("width", obj.attr("width")).attr("height", obj.attr("height")).attr("data-hint", obj.attr("data-hint")).style("fill", obj.style("fill"))
								.on("mouseout", function() { d3.select(this).remove();hideTooltip() });
						})
						.attr("width", 0).transition().duration(500)
						.delay(function(d, i) { return i * 50; })
						.attr("width", actBarW);
				} else {
					var reduceDetailsArr = [["shuffleStart", "shuffleEnd"], ["sortStart", "sortEnd"], ["reduceStart", "reduceEnd"]];
					var titleMsgArr = [["Shuffle Start", "Shuffle End"], ["Sort Start", "Sort End"], ["Reduce Start", "Reduce End"]];
					var actStartPoint=0;
					var j=0;
					colorCnt=1;
					for (; j<reduceDetailsArr.length; j++) {
						colorCnt++;
						var reduceStart = taskObj[reduceDetailsArr[j][0]];
						var reduceEnd = taskObj[reduceDetailsArr[j][1]];
						var reducePointDiff = reduceEnd - reduceStart;
						var reducePointPerc = (reducePointDiff*100)/jvmConfig.totalTime;
						var reduceBarW = (reducePointPerc*jvmConfig.svgW)/100;
						var titleMsg = taskObj.taskID + ", " + titleMsgArr[j][0] + ": " + reduceStart + ", " + titleMsgArr[j][1] + ": " + reduceEnd;
						var startPointPerc = (reduceStart*100)/jvmConfig.totalTime;
						actStartPoint = (startPointPerc*jvmConfig.svgW)/100;
						jvmConfig.barG.append("rect")
							.attr("class", "hint--bottom")
							.attr("height", jvmConfig.barH)
							.attr("fill", jvmConfig.barColors[colorCnt])
							.attr("x", actStartPoint)
							.attr("y", jvmConfig.svgH - (jvmConfig.barH*(i+1)))
							.attr("data-hint", titleMsg)
							.style("fill-opacity", "0.75")
							.on("mouseover", function() {
								var obj = d3.select(this);
								showTooltip(d3.select(this).attr("data-hint"),d3.select(this).attr("x"),d3.select(this).attr("y"));
								jvmConfig.barG.append("rect").attr("class", "hint--bottom").attr("id", "hoverObj").attr("x", obj.attr("x")).attr("y", obj.attr("y"))
									.attr("width", obj.attr("width")).attr("height", obj.attr("height")).attr("data-hint", obj.attr("data-hint")).style("fill", obj.style("fill"))
									.on("mouseout", function() { d3.select(this).remove();hideTooltip() });
								
							})
							.attr("width", 0).transition().duration(500)
							.delay(function(d, i) { return i * 50; })
							.attr("width", reduceBarW);
					}		
				}
			}
			colorCnt++;
		});
	};
	
	jvmGraph.createJVMLineGraph = function(data) {
		jvmConfig.svgWrapH = jvmConfig.svgWrapH - jvmConfig.marginT*2;
		createJVMLineAxis(data);
		var cpuG = jvmConfig.lineG.append("g").attr("class", "cpu-graph").attr("transform", "translate(0, "+ jvmConfig.marginT*2 +")");
		var memoryG = jvmConfig.lineG.append("g").attr("class", "memory-graph").attr("transform", "translate(0, "+ jvmConfig.marginT*2 +")");
		var flowRateG = jvmConfig.lineG.append("g").attr("class", "flowrate-graph").attr("transform", "translate(0, "+ jvmConfig.marginT*2 +")");
		createJVMCPULines(cpuG, data);
		createJVMMemoryLines(memoryG, data);
		createJVMFlowRateLines(flowRateG, data);
	};
	
	jvmGraph.createJVMLineAxis = function(data) {
		var xAxis = jvmConfig.xAxisSVG.append("g").attr("class", "axis").attr("transform", "translate(0, "+ (jvmConfig.marginT) +")");
		var xAxisLbl = jvmConfig.xAxisSVG.append("g").attr("class", "axis-lbl");
		var yAxis = jvmConfig.yAxisSVG.append("g").attr("class", "axis").attr("transform", "translate("+ (jvmConfig.marginL) +", "+ (jvmConfig.marginT*2+jvmConfig.vScrollMargin) +")");
		var yAxisLbl = jvmConfig.yAxisSVG.append("g").attr("class", "axis-lbl");
		yAxis.append("line")
				.attr("x1", 0).attr("y1", 0).attr("x2", 0).attr("y2", jvmConfig.svgWrapH)
				.style("stroke", "#888").style("stroke-width", "0.5");
		var xLbl = xAxisLbl.append("text")
				.attr("transform", "translate(0, 0)").attr("text-anchor", "left")
				.style("font-size", "14px")
				.text( jvmConfig.xAxisTitle );
		var yLbl = yAxisLbl.append("text")
				.attr("transform", "translate(0, 0) rotate(-90,30,20)").attr("text-anchor", "left")
				.style("font-size", "14px")
				.text( jvmConfig.yAxisTitle );
		xLbl.each(function() {
			var xPos = (jvmConfig.svgW - this.getBBox().width)/2;
			xAxisLbl.attr("transform", "translate("+ xPos +", "+ (jvmConfig.marginB-10) +")");
		});
		yLbl.each(function() {
			var yPos = jvmConfig.svgH - this.getBBox().width;
			yAxisLbl.attr("transform", "translate(10, "+ yPos +")");
		});
		
		var i=10;
		var yTipCnt=0;
		var tipGap = jvmConfig.svgWrapH/10;
		for (; i>=0; i--) {
			yAxis.append("line")
				.attr("x1", -10).attr("y1", tipGap*i).attr("x2", 0).attr("y2", tipGap*i)
				.style("stroke", "#888").style("stroke-width", "0.5");
			yAxis.append("text")
				.attr("transform", "translate("+ -20 +", "+ (((jvmConfig.svgWrapH/10)*i)+5) +")")
				.attr("fill", "#888")
				.attr("text-anchor", "middle")
				.style("font-size", "11px")
				.text(yTipCnt*10);
			yTipCnt++;
		}
	
		xAxis.append("line")
				.attr("x1", 0).attr("y1", 0).attr("x2", jvmConfig.svgW).attr("y2", 0)
				.style("stroke", "#888").style("stroke-width", "0.5");
		var tipCnt=0;
		$.each(data.graphData.memUsage, function(tipKey, tipObj) {
			tipCnt++;
		});
		if(jvmGraph.getObjectSize(data.graphData.cpuUsage)>0){
			var newTipCount = tipCnt;
		}else{
			var newTipCount = tipCnt*2;
		}
		var xLblDiff = jvmConfig.totalTime/newTipCount;
		var j=1;	
		for (; j<=newTipCount; j++) {
			var tipPosPerc = (xLblDiff*100)/jvmConfig.totalTime;
			var actTipPos = (tipPosPerc*jvmConfig.svgW)/100;
			xAxis.append("line")
				.attr("x1", actTipPos*j).attr("y1", 0).attr("x2", actTipPos*j).attr("y2", 10)
				.style("stroke", "#888").style("stroke-width", "0.5");
			xAxis.append("text")
				.attr("transform", "translate("+ (actTipPos*j) +", "+ 25 +")")
				.attr("fill", "#888")
				.attr("text-anchor", "middle")
				.style("font-size", "11px")
				.text( Math.floor(xLblDiff*j) );
		}
	};
	jvmGraph.getObjectSize = function(obj){
	    var size = 0, key;
	    for (key in obj) {
	        if (obj.hasOwnProperty(key)) size++;
	    }
	    return size;
	};
	jvmGraph.showTooltip = function(tContent,tXpos,tYpos) {
		//console.log(tContent+" X "+tXpos+" y "+tYpos);		
		var tooltipDiv = d3.select('.hint');
		$('#jvmGraphWrap').find('.hint').html(tContent);
		
		//$('#jvmGraphWrap').find('.hint').css('top',(parseInt(tYpos)+30));
		//$('#jvmGraphWrap').find('.hint').css('left',(parseInt(tXpos)+120));
		
			//No mapper information displayed on hover in JVM profiler.
		
		var absoluteMousePos = d3.mouse(bodyNode);
     /* tooltipDiv.style({
         left: (absoluteMousePos[0] + 10)+'px',
         top: (absoluteMousePos[1] - 15)+'px'
      });*/
                
		$('#jvmGraphWrap').find('.hint').css('top',(absoluteMousePos[1]-200));
		$('#jvmGraphWrap').find('.hint').css('left',(absoluteMousePos[0]-200));	      
      $('#jvmGraphWrap').find('.hint').fadeIn('fast');
	};
	/*jvmGraph.showTooltip2 = function(tObj) {
		var tOffset = $(tObj).offset();
		$('#jvmGraphWrap').find('.hint').html($(tObj).attr('data-hint')));
		$('#jvmGraphWrap').find('.hint').css('top',tOffset.top);
		$('#jvmGraphWrap').find('.hint').css('left',tOffset.left);
		$('#jvmGraphWrap').find('.hint').fadeIn('fast');
	};*/
	jvmGraph.hideTooltip = function() {
		$('#jvmGraphWrap').find('.hint').fadeOut('fast');
	};
	jvmGraph.createJVMCPULines = function(cpuObj, data) {
		var yPoints = [];
		var tipCnt=0;
		$.each(data.graphData.cpuUsage, function(tipKey, tipVal) {
			yPoints.push(tipVal);
			tipCnt++;
		});
		createJVMLines(cpuObj, yPoints, tipCnt, jvmConfig.lineColors[0]);
	};
	
	jvmGraph.createJVMMemoryLines = function(memoryObj, data) {
		var yPoints = [];
		var xPoints = [];
		var tipCnt=0;
		/*
		Updating object refrence
		*/		
		$.each(data.graphData.memUsage, function(tipKey, tipVal) {
			yPoints.push(tipVal);
			xPoints.push(tipKey);
			tipCnt++;
		});
		createJVMLines(memoryObj, yPoints, tipCnt, jvmConfig.lineColors[1], xPoints, true);
	};
	
	jvmGraph.createJVMLines = function(obj, yPoints, tipCnt, fillColor, xPoints, flag) {
		var xLblDiff = jvmConfig.totalTime/tipCnt;
		var pathD="";
		var j=1;
		for (; j<=tipCnt; j++) {
			if(typeof flag !=='undefined'){
				var x1Perc = (xLblDiff*100)/jvmConfig.totalTime;
//				x = (((x1Perc*jvmConfig.svgW)/100)*j)-(x1Perc);
				/*
				Changing the logic for plotting points on x-axis
				*/
				x = (jvmConfig.svgW/jvmConfig.totalTime)*(xPoints[j-1]);
				y = (jvmConfig.svgWrapH*(100-yPoints[j-1]))/100;
			}else{
				var x1Perc = (xLblDiff*100)/jvmConfig.totalTime;
				x = ((x1Perc*jvmConfig.svgW)/100)*j;
				y = (jvmConfig.svgWrapH*(100-yPoints[j-1]))/100;
			}

		
			if ( j == 1 ) {
				pathD += "M" + x + "," + y;
			} else {
				pathD += "L" + x + "," + y;
			}
	
			obj.append("circle")
				.on("mouseover", function() { d3.select(this).transition().duration(500).delay(function(d, i) { return i * 50; }).attr("r", 10).style("fill-opacity", "1");
				showTooltip(d3.select(this).attr("data-hint"),d3.select(this).attr("x"),d3.select(this).attr("y"));
				//showTooltip2(this);
				 })
				.on("mouseout", function() { d3.select(this).transition().duration(500).delay(function(d, i) { return i * 50; }).attr("r", 5).style("fill-opacity", "0.7");hideTooltip(); })
				.attr("class", "hint--bottom")
				.attr("transform", "translate("+ x +", "+ y +")")
				.style("fill-opacity", "0.7")
				.style("fill", fillColor)
				.attr("data-hint", yPoints[j-1]+"%")
				.attr("x", x)
				.attr("y", y)
				.attr("r", 0).transition().duration(500)
				.delay(function(d, i) { return i * 50; })
				.attr("r", 5);
		}
		obj.append("path")
			.style("stroke", fillColor).style("stroke-width", "1.5").attr("fill", "none")
			.style("stroke-opacity", "0.6")
			.attr("d", pathD)
			.transition().duration(500)
			.delay(function(d, i) { return i * 50; })
			.attr("d", pathD);
	};
	
	jvmGraph.createJVMFlowRateLines = function(flowRateG, data) {
		var frArr = [];
		var xPoints = [];
		$.each(data.graphData.phaseOutput, function(frKey, frObj) {
			var xStartPointPerc = (frObj.taskOutputDetails[0].startPoint*100)/jvmConfig.totalTime;
			var xEndPointPerc = (frObj.taskOutputDetails[0].endPoint*100)/jvmConfig.totalTime;
			var xStartPoint = (xStartPointPerc*jvmConfig.svgW)/100;
			var xEndPoint = (xEndPointPerc*jvmConfig.svgW)/100;
			var xcenterPoint = xStartPoint + (xEndPoint - xStartPoint)/2;
			xPoints.push(xcenterPoint);
			frArr.push(frObj.avgDataFlowRate);
		});
		var maxPoint = Math.max.apply( Math, frArr );
		var i=0;
		for (; i<frArr.length; i++) {
			var yPointPerc = (frArr[i]*100)/maxPoint;
			var x = ((yPointPerc*jvmConfig.svgW)/100)*i;
			var y = (jvmConfig.svgWrapH*[100-yPointPerc])/100;
			
			flowRateG.append("circle")
				.attr("transform", "translate("+ xPoints[i] +", "+ y +")")
				.style("fill-opacity", "0.4")
				.style("fill", jvmConfig.lineColors[2])
				.attr("class", "hint--bottom")
				.attr("data-hint", frArr[i])
				.attr("x", xPoints[i])
				.attr("y", y)
				.attr("r", 0).transition().duration(500)
				.delay(function(d, i) { return i * 50; })
				.attr("r", 10);
			flowRateG.append("circle")
				.on("mouseover", function() { d3.select(this).transition().duration(500).delay(function(d, i) { return i * 50; }).attr("r", 10).style("fill-opacity", "1");
				
				showTooltip(d3.select(this).attr("data-hint"),d3.select(this).attr("x"),d3.select(this).attr("y"));
				//showTooltip2(this);
				 })
				.on("mouseout", function() { d3.select(this).transition().duration(500).delay(function(d, i) { return i * 50; }).attr("r", 5).style("fill-opacity", "0.7");hideTooltip();})
				.attr("transform", "translate("+ xPoints[i] +", "+ y +")")
				.attr("class", "hint--bottom")
				.style("fill-opacity", "0.8")
				.style("fill", jvmConfig.lineColors[2])
				.attr("data-hint", frArr[i])
				.attr("r", 0).transition().duration(500)
				.delay(function(d, i) { return i * 50; })
				.attr("r", 5);
		}
	};
	
	jvmGraph.createJVMGraphLegends = function() {
		var legendH = 20;
		var barLegends = jvmConfig.legendsSVG.append("g").attr("class", "bar-legends").attr("transform", "translate(10, 0)");
		var lineLegends = jvmConfig.legendsSVG.append("g").attr("class", "line-legends").attr("transform", "translate(10, "+ ((jvmConfig.barLegends.length+2)*legendH) +")");
		var i=0;
		for (; i<jvmConfig.barLegends.length; i++) {
			barLegends.append("rect")
				.attr("x", 0).attr("y", legendH*i).attr("width", 10).attr("height", 10)
				.attr("fill", jvmConfig.barColors[i]).style("fill-opacity", "0.75");
			barLegends.append("text")
				.attr("transform", "translate(20, "+ ((legendH*i)+9) +")")
				.attr("fill", "#888")
				.attr("text-anchor", "left")
				.style("font-size", "12px")
				.text( jvmConfig.barLegends[i] );
		}
		var i=0;
		for (; i<jvmConfig.lineLegends.length; i++) {
			var target;
			if ( jvmConfig.lineLegends[i] == "Avg. Memory Usage(%)" ) {
				target = "memory-graph";
			} else if ( jvmConfig.lineLegends[i] == "Avg. CPU Usage(%)" ) {
				target = "cpu-graph";
			} else if ( jvmConfig.lineLegends[i] == "Avg. Data Flow Rate(kb/sec)" ) {
				lineLegends.append("circle")
				.attr("transform", "translate(5, "+ legendH*i +")")
				.style("fill-opacity", "0.4")
				.style("fill", jvmConfig.lineColors[i])
				.attr("r", 8);
				target = "flowrate-graph";
			}
			lineLegends.append("circle")
				.attr("transform", "translate(5, "+ legendH*i +")")
				.style("fill-opacity", "0.8")
				.style("fill", jvmConfig.lineColors[i])
				.attr("r", 5);
			lineLegends.append("text")
				.attr("transform", "translate(20, "+ ((legendH*i)+5) +")")
				.attr("fill", "#888")
				.attr("text-anchor", "left")
				.style("font-size", "12px")
				.style("cursor", "pointer")
				.attr("target", target)
				.text( jvmConfig.lineLegends[i] )
				.on("click", function() {
					var target = d3.select(this).attr("target");
					var isVisible = d3.select("g."+target).style("display");
					if (isVisible == "none" ) {
						d3.select("g."+target).style("display", "inline");
					} else {
						d3.select("g."+target).style("display", "none");
					}
				});
		}
	};
})(window);
