/* Optimize Graph controller */
//'use strict';

angular.module('optimizeGraph.ctrl').controller('optimizeGraphController', ['$scope', '$http', '$rootScope', 'common', '$location', '$timeout','$compile',
	function($scope, $http, $rootScope, common, $location, $timeout,$compile) {

		$scope.optimizeObj = {};
		$scope.t3DJSON = [];
		$(".optimizeGraphClass").css("width", $(window).width() - 400);
		$scope.graphWidth = $(window).width() - 450;
		$scope.graphWidthlegends = 300;		
		$scope.tunningFlag = 'FALSE';
		$scope.showJobName = common.getOptimizeJobName();
		$scope.showLoader = false;
		$scope.webSocketErrorFlag = false;
		$scope.connectionLostFlag = false;
		$scope.licenseExpireTrue = false;
        $scope.licenseExpireDays = false;
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
			"tasktracker.http.threads"
		];
		/** Init function starts (websocket) */
		$scope.init = function() {
			$('[data-toggle="tooltip"]').tooltip();
            licenseExpireMessage();
			$scope.showTextBox = false;
			$scope.selectedGraphPoint = {};
			$scope.selectedObjValue = false;
			$scope.commandLineArguments = ''
			$scope.coreParameters = ''
			$scope.hdfsParamteres = ''
			$scope.mapredParameters = ''
			$scope.showEvent = false;
			$scope.graphPoint = {};
			var host = window.location.hostname;
			var port = window.location.port;
			var jobName = common.getOptimizeJobName();
			$scope.finalJobName = jobName;  
			if ($scope.finalJobName == null || $scope.finalJobName == undefined || $scope.finalJobName == "") {
				$location.path('/');
			} 
			else {
				var url = "ws://" + host + ":" + port + "/results/jobanalysis?jobName=" + jobName;

				var webSocket = new WebSocket(url);

				webSocket.onopen = function(event) {
					console.log("socket opened sucessfully")
				};

				webSocket.onmessage = function(event) {
					var serverData = angular.copy(event.data);
					serverData = JSON.parse(serverData);
					var selfTuningData = serverData.SELF_TUNING;
					$scope.showLoader = true;
					$scope.webSocketErrorFlag = true;
					if (selfTuningData != null && selfTuningData != undefined) {

						$scope.tunningFlag = 'FALSE';
						$('.ring-loader').remove();
						$('.message-loader').remove();
						var localData = JSON.parse(selfTuningData);
						var doErrorsExist = $scope.containErrors(localData);
						if (doErrorsExist) {
							return;
						}
						var newArray = [];
						newArray.push(localData);
						$scope.createGraph(newArray);
					} else {
						$scope.tunningFlag = 'TRUE';
						$('.ring-loader').remove();
						$('.message-loader').remove();
						var localData = JSON.parse(serverData.QUICK_TUNING);
						var doErrorsExist = $scope.containErrors(localData);
						if (doErrorsExist) {
							return;
						}
						
						if (Object.keys(localData).length > 0) {
							$scope.showEvent = true;
						}
						$scope.createTable(localData);

					}

				};
				webSocket.onerror = function(error) {
					$scope.displayErrorMessage("Connection lost to server");
				};
				webSocket.onclose = function(event) {
					//updateOutput("Connection Closed");
					//connectBtn.disabled = false;
					if ($scope.webSocketErrorFlag == false) {
						$scope.connectionLostFlag = true;
						$scope.displayErrorMessage("Connection lost to server");
					} else {
						console.log("socket closed sucessfully")
					}
				};

				function closeSocket() {
					webSocket.close();
				}
			}


		}
		function licenseExpireMessage ()  { 
            //licenseValidateFactory.submitLicense({},function(data) {
                    var data = common.getNodeSize();
                    var currentDate = data.currentTime;
                    if (data['Valid Until']) {
                        var expiryDate = data['Valid Until'];
                        var temp = new Date(data['Valid From']).toString();
                        data['Valid From'] = temp.substring(4, 16) + temp.substring(25);
                        temp = new Date(data['Valid Until']).toString();
                        data['Valid Until'] = temp.substring(4, 16) + temp.substring(25); 
                        var milliseconds = (expiryDate - currentDate);
                        var daysDiff = milliseconds/86400000;
                        if ( daysDiff <= 3) {
                            if ( daysDiff >= 1) {
                                $scope.daysDiffShow = Math.round(daysDiff);
                                $scope.licenseExpireDays = true;
                                $scope.licenseExpireTrue = false;
                            } else {
                                $scope.licenseExpireTrue = true;
                                $scope.licenseExpireDays = false;
                            }
                        } 
                    }
                //},
            //function(e) {
                //console.log(e);
            //}); 
        }
		/** Function checks data contains error or not */
		$scope.containErrors = function(localData) {
			var errorMessageToDisplay = "Something went wrong, Please contact 'help@jumbune.com'.";
            if (localData == undefined) {
                $scope.displayErrorMessage("Unable to fetch data from server");
            }
            var errorData = localData.ErrorAndException;
            if (errorData != null) {
                var errorMsgServer = "";
                if (!jQuery.isEmptyObject(errorData)) {
                    errorMsgServer = "<br>Error Message : <p class='errMesageDisplay'>[";
                    for (var key in errorData) {
                        if (key == undefined) {
                            continue;
                        }
                        if (key != errorData[key]) {
                        	errorMsgServer += key + '. ' + errorData[key];
                        } else {
                        	errorMsgServer += key + ".";
                        }
                        //errorMsgServer += key + ".";
                    }
                    errorMsgServer += "]</p>";
                }

                $scope.displayErrorMessage(errorMessageToDisplay + errorMsgServer);
                return true;
			}
			return false;
		}

		/** Display error messages */ 
		$scope.displayErrorMessage = function(errorMessageToDisplay) {
				/*var errorDiv = document.getElementById('errorMessage');
				                    errorDiv.style.display = '';
				                    errorDiv.innerHTML = '<strong>Error! </strong>' + errorMessageToDisplay;
				                    document.getElementById('resultWidgetContainer').style.display = 'none';
				                    document.querySelector('.widgetWrapper').style.marginBottom = errorDiv.style.marginTop;*/
				if ($scope.connectionLostFlag != false) {
					$("#tuningWidgetWrapper").remove();
					var errorDiv = document.getElementById('errorMessage');
					errorDiv.style.display = '';
					errorDiv.innerHTML = '<i class="fa fa-exclamation-triangle" style="font-size: 22px; margin:10px;"></i>' + errorMessageToDisplay;
					$('.widgetRow').css({'height': 600,'background-color': 'white'});
				} else {
					var myNode = document.getElementById("tuningWidgetWrapper");
					myNode.innerHTML = '<h2><span class="tracking"><i class="fa fa-home" style="font-size:30px;cursor:pointer;" ng-click="clickedHomeIcon()"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Job&nbsp;&nbsp;-&nbsp;&nbsp;' + $scope.showJobName + ' (Optimize Job)</span><span class="tracking1" style="float:right;"></span></h2>';
					$compile( myNode )($scope);
					$(myNode).css('padding', '0px !important');
					var errorDiv = document.getElementById('errorMessage');
					errorDiv.style.display = '';
					errorDiv.innerHTML = '<i class="fa fa-exclamation-triangle" style="font-size: 22px; margin:10px;"></i>' + errorMessageToDisplay;
					$('.widgetRow').css({'height': 600,'background-color': 'white'});
				}
			}
			
		/** copy option */
		$scope.addContent = function(cKey, cVal) {

		}

		/** download file */
		$scope.downloadFile = function() {
			var a = document.createElement('a');
			var fileName = "Hadoop Configuration Parameters.txt";
			contentType = 'data:application/octet-stream,';
			var fileContent = $scope.getExportFileContent();
			uriContent = contentType + encodeURIComponent(fileContent);
			a.setAttribute('href', uriContent);
			a.setAttribute('download', fileName);
			document.body.appendChild(a);
			a.click();
			document.body.removeChild(a);
		}

		/** Export file */
		$scope.getExportFileContent = function() {
			var fileContent = "Please configure the following properties in accordance with the file name\n\n" + "/***********************core-site.xml***********************/\n" + $scope.coreParameters + "\n\n" + "/***********************hdfs-site.xml************************/\n" + $scope.hdfsParamteres + "\n\n" + "/**********************mapred-site.xml**********************/\n" + $scope.mapredParameters;
			return fileContent;
		}

		/** Function shows text box event */
		$scope.showTextBoxEvent = function() {
			$scope.showTextBox = true;
			$scope.copyCmdParam = $scope.commandLineArguments;
		}

		/** Redirect to index page */
		$scope.clickedHomeIcon = function() {
			$location.path("/")
		}

		/** Function create table on click on job iteration circle/point */
		$scope.createTable = function(data) {
			var body = d3.select("#table_data").html(JSON.stringify(data));
			$scope.graphPoint = angular.copy(data)

			//copy option
			$scope.commandLineArguments = "";
			angular.forEach($scope.graphPoint, function(cVal, cKey) {
				$scope.commandLineArguments = $scope.commandLineArguments + ' -D ' + cKey + '=' + cVal;

				if (coreSite.indexOf(cKey) != -1) {
					$scope.coreParameters = $scope.coreParameters + '<property>\n\t<name>' + cKey + '</name>\n\t<value>' + cVal + '</value>\n</property>\n';
				} else if (hdfsSite.indexOf(cKey) != -1) {
					$scope.hdfsParamteres = $scope.hdfsParamteres + '<property>\n\t<name>' + cKey + '</name>\n\t<value>' + cVal + '</value>\n</property>\n';
				} else {
					$scope.mapredParameters = $scope.mapredParameters + '<property>\n\t<name>' + cKey + '</name>\n\t<value>' + cVal + '</value>\n</property>\n';
				}
			});


			if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
				$scope.$apply();
			}
		}

		/** Function create tunning graph */
		$scope.createGraph = function(data) {
			$scope.t3DJSON = angular.copy(data);

			delete $scope.t3DJSON[0][0]['coefficientNameValuePair']['dfs.blocksize'];

			if (Object.keys($scope.t3DJSON).length > 0) {
				var dataset = $scope.t3DJSON;
				/** Width and height */
				var w = $scope.graphWidth;
				var h = $(window).height() / 2;

				$('#optimizeGraphLegends').css("max-height", h);

				var legendsArr = [];

				for (var key in $scope.t3DJSON[0][0].coefficientNameValuePair) {
					legendsArr.push(key);
				}
				var padding = 70;

				/** Define axis ranges & scales   */

				var tickValue = [];

				var xExtents = d3.extent(d3.merge(dataset), function(d, i) {
					tickValue.push(i + 1);
					return i + 1;
				});

				var yExtents = d3.extent(d3.merge(dataset), function(d, i) {
					return d.executionTimeInMsecs / 1000;
				});

				var yExtents1 = d3.extent(d3.merge(dataset), function(d, i) {

					if (typeof d["coefficientNameValuePair"][legendsArr[0]] != "undefined") {
						return d["coefficientNameValuePair"][legendsArr[0]] + "s";
					} else {
						return 0;
					}
				});

				var xScale = d3.scale.linear()
					.domain([xExtents[0], xExtents[1]])
					.range([padding, w * .8]);

				var yScale = d3.scale.linear()
					.domain([yExtents[0] - .1, yExtents[1]])
					.range([h - 20, 20]);

				var yScale1 = d3.scale.linear()
					.domain([yExtents1[0] - .1, yExtents1[1]])
					.range([h - 20, 20]).nice();


				var tooltip = d3.select("body")
					.append("div")
					.attr("class", "sunburstTooltip")
					.style("position", "absolute")
					.style("z-index", "10")
					.style("opacity", 0);


				// Create SVG element
				var svg = d3.select("#optimizeGraph")
					.append("svg")
					.attr("id", "line_chart")
					.attr("width", w)
					.attr("height", h);


				/** Define lines */
				var line = d3.svg.line()
					.x(function(d) {
						return x(d.executionTimeInMsecs);
					})
					.y(function(d) {
						return y(d.y1, d.y2, d.y3);
					}).interpolate("monotone");

				var pathContainers = svg.selectAll('g.line')
					.data(dataset);

				pathContainers.enter().append('g')
					.attr('class', 'line')
					.style("stroke", "#000")
					.attr("style", function(d, i) {
						return "stroke: #7ac143";
					});

				pathContainers.selectAll('path')
					.data(function(d) {
						return [d];
					})
					.enter().append('path')
					.attr('d', d3.svg.line()
						.x(function(d, i) {
							return xScale(i + 1);
						})
						.y(function(d, i) {
							return yScale(d.executionTimeInMsecs / 1000);
						}).interpolate("monotone")
					);

				/** add circles */
				pathContainers.selectAll('circle')
					.data(function(d) {
						return d;
					})
					.enter().append('circle')
					.attr('cx', function(d, i) {
						return xScale(i + 1);
					})
					.attr('cy', function(d, i) {
						return yScale(d.executionTimeInMsecs / 1000);
					}).style("fill", function(d, i) {
						return "#7ac143";
					}).style("opacity", function(d, i) {
						return 1;
					}).style("stroke-width", function(d, i) {
						return "0px";
					}).style("cursor", function(d, i) {
						return "pointer";
					})
					.attr('r', 10)
					.on('click', function(d, i) {
						$scope.showTextBox = false;
						var body = d3.select("#table_data").html(JSON.stringify(d));
						$scope.selectedGraphPoint = angular.copy(d)



						if (Object.keys($scope.selectedGraphPoint).length > 0) {
							$scope.selectedObjValue = true;
							$scope.showEvent = true;
						}
						$("#tuningIterationNumber").html('Iteration : ' + (i + 1));
						/** copy option */
						$scope.commandLineArguments = "";
						angular.forEach($scope.selectedGraphPoint.coefficientNameValuePair, function(cVal, cKey) {
							$scope.commandLineArguments = $scope.commandLineArguments + ' -D ' + cKey + '=' + cVal;

							if (coreSite.indexOf(cKey) != -1) {
								$scope.coreParameters = $scope.coreParameters + '<property>\n\t<name>' + cKey + '</name>\n\t<value>' + cVal + '</value>\n</property>\n';
							} else if (hdfsSite.indexOf(cKey) != -1) {
								$scope.hdfsParamteres = $scope.hdfsParamteres + '<property>\n\t<name>' + cKey + '</name>\n\t<value>' + cVal + '</value>\n</property>\n';
							} else {
								$scope.mapredParameters = $scope.mapredParameters + '<property>\n\t<name>' + cKey + '</name>\n\t<value>' + cVal + '</value>\n</property>\n';
							}

						});


						if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
							$scope.$apply();
						}

					}).on("mouseover", function(d) {
						tooltip.style("display", "block");
						tooltip.html(function() {
							return d.executionTimeInMsecs / 1000 + 's';
						});
						return tooltip.transition()
							.duration(50)
							.style("opacity", 0.9);
					})
					.on("mousemove", function(d) {
						return tooltip
							.style("top", (d3.event.pageY - 10) + "px")
							.style("left", (d3.event.pageX + 10) + "px");
					})
					.on("mouseout", function() {
						return tooltip.style("display", "none");
					});;


				var pathContainers1 = svg.selectAll('g.line1')
					.data(dataset);

				pathContainers1.enter().append('g')
					.attr('class', 'line1')
					.style("stroke", "#000")
					.attr("style", function(d, i) {
						return "stroke: #FF8533";
					});

				pathContainers1.selectAll('path')
					.data(function(d) {
						return [d];
					})
					.enter().append('path')
					.attr('d', d3.svg.line()
						.x(function(d, i) {
							return xScale(i + 1);
						})
						.y(function(d, i) {
							return yScale1(d['coefficientNameValuePair']['dfs.blocksize']);
						}).interpolate("monotone")
					);

				/** Define X axis */
				var xAxis = d3.svg.axis()
					.scale(xScale)
					.tickValues(tickValue)
					.innerTickSize(-h)
					.outerTickSize(0)
					.orient("bottom")
					.ticks(5);

				/** Define Y axis */
				var yAxis = d3.svg.axis()
					.scale(yScale)
					.innerTickSize(-w * .8 + 60)
					.outerTickSize(0)
					.orient("left")
					.ticks(4);

				//Define Y axis
				var yAxis1 = d3.svg.axis()
					.scale(yScale1)
					.orient("right")
					.ticks(4);

				svg.append("g")
					.attr("class", "x_axis")
					.attr("transform", "translate(0," + (h - 30) + ")")
					.call(xAxis)
					.append("text")
					.attr("class", "label")
					.attr("x", w / 2)
					.attr("y", 25)
					.style("text-anchor", "end")
					.text("Iteration");

				/** Add Y axis */
				svg.append("g")
					.attr("class", "y_axis1")
					.attr("transform", "translate(" + (padding - 20) + ",0)")
					.call(yAxis);

				/** Add Y axis */
				svg.append("g")
					.attr("class", "y_axis2")
					.attr("transform", "translate(" + (w * .8 + 20) + "," + 0 + ")")
					.call(yAxis1);

				d3.select(".y_axis2")
					.append('text')
					.attr("x", (-h / 2))
					.attr("y", "150")
					.attr('class', 'label')
					.text(legendsArr[0])
					.attr("transform", "rotate(-90)")
					.style("text-anchor", "end")
					.style("fill", "#FF8533");

				d3.select(".y_axis1")
					.append('text')
					.attr("x", (-h / 4))
					.attr("y", "-35")
					.attr('class', 'label')
					.text('Execution Time(in seconds)')
					.attr("transform", "rotate(-90)")
					.style("text-anchor", "end")
					.style("fill", "#7ac143");

				var svg = d3.select("#optimizeGraphLegends")
					.append("svg")
					.style("height", ((legendsArr.length * 20) + 40) + "px")
					.attr("id", "line_chart")
					.attr("width", $scope.graphWidthlegends)
					.attr("height", h);

				var legend1 = svg.selectAll(".legend1")
					.data(["Execution Time"])
					.enter().append("g")
					.attr("class", "legend1")
					.attr("transform", function(d, i) {
						return "translate(" + (0) + "," + (20 + i * 30) + ")";
					});


				legend1.append("text")
					.attr("x", 20)
					.attr("y", 9)
					.attr("dy", ".35em")
					.style("text-anchor", "start")
					.style('font-weight', "bold")
					.text(function(d, i) {
						return d;
					});


				legend1.append("circle")
					.attr("cx", 10)
					.attr("cy", 8)
					.attr("r", 7)
					.attr("class", "dot_legend")
					.style("fill", function(d, i) {
						if (i == 0) {
							return "#7ac143";
						} else {
							return "#FF8533";
						}
					});

				var tempArr = legendsArr;
				var requiredArr = [];
				for (var i = 0; i < tempArr.length; i++) {
					if (tempArr[i].indexOf("compression") == -1) {          requiredArr.push(tempArr[i]);      } else {                           }
				}

				var legend = svg.selectAll(".legend")
					.data(requiredArr)
					.enter().append("g")
					.attr("class", "legend")
					.attr("id", function(d, i) {
						return "legends" + i;
					})
					.style("opacity", function(d, i) {
						if (i == 0) {
							return "1";
						} else {
							// return ".5";
							return "1";
						}
					})
					.attr("transform", function(d, i) {
						return "translate(" + (0) + "," + (40 + i * 20) + ")";
					});


				legend.append("text")
					.attr("x", 20)
					.attr("y", 9)
					.attr("dy", ".35em")
					.style("text-anchor", "start")
					.style("cursor", "pointer")
					.text(function(d, i) {
						return d;
					})
					.on("click", function(d, i) {
						$scope.updateGraph(d, i);
					});


				legend.append("circle")
					.attr("cx", 10)
					.attr("cy", 8)
					.attr("r", 7)
					.attr("class", "dot_legend")
					.style("cursor", "pointer")
					.style("fill", function(d, i) {
						return "#FF8533";
					})
					.on("click", function(d, i) {
						$scope.updateGraph(d, i);
					});
			}
		}


		$scope.updateGraph = function(index, i) {
			var dataset = angular.copy($scope.t3DJSON);
			var padding = 70;

			var legendsArr = [];

			for (var key in $scope.t3DJSON[0][0].coefficientNameValuePair) {
				legendsArr.push(key);
			}

			/** Width and height */
			var w = $scope.graphWidth;
			var h = $(window).height() / 2;

			var svg = d3.select("#line_chart");

			var xExtents = d3.extent(d3.merge(dataset), function(d, i) {
				return i + 1;
			});
			/*var yExtents1 = d3.extent(d3.merge(dataset), function (d, i) {
			    if (typeof d["coefficientNameValuePair"][index] != "undefined") {
			        return Number(d["coefficientNameValuePair"][index]);
			    } else {
			        return 0;
			    }
			});*/
			var yExtents1 = d3.extent(d3.merge(dataset), function(d, i) {
				if (typeof d["coefficientNameValuePair"][index] != "undefined") {
					if (index.indexOf("java.opt") != -1) {
						var s = d["coefficientNameValuePair"][index];
						var p = s.match(/\d/g);
						p = p.join("");
						console.log('s.slice(-1)==' + s.slice(-1));
						if (s.slice(-1) == 'g' || s.slice(-1) == 'G') {
							return Number(p) * 1024;
						} else if (s.slice(-1) == 'k' || s.slice(-1) == 'K') {
							return Number(p) / 1024;
						} else {
							return Number(p);
						}

					} else {
						return Number(d["coefficientNameValuePair"][index]);
					}
				} else {
					return 0;
				}
			});
			var xScale = d3.scale.linear()
				.domain([xExtents[0], xExtents[1]])
				.range([padding, w * .8]);


			var yScale1 = d3.scale.linear();
			if (isNaN(yExtents1[0]) == false && isNaN(yExtents1[1]) == false) {
				if (yExtents1[0] > parseFloat(yExtents1[1])) {
					yScale1.domain([yExtents1[1] - .1, yExtents1[0]]).range([h - 20, 20]).nice();
				} else if (yExtents1[0] == parseFloat(yExtents1[1])) {
					yScale1.domain([yExtents1[0] - .1, yExtents1[1]]).range([h - 20, 20]).nice();
				} else {
					yScale1.domain([yExtents1[0] - .1, yExtents1[1]]).range([h - 20, 20]).nice();
				}

			} else {
				yScale1.domain([0, 0]).range([h - 20, 20]).nice();
			}
			svg.selectAll("g.line1").remove();

			var pathContainers1 = svg.selectAll('g.line1')
				.data(dataset);

			pathContainers1.enter().append('g')
				.attr('class', 'line1')
				.style("stroke", "#000")
				.attr("style", function(d, i) {
					return "stroke: #FF8533";
				});

			pathContainers1.selectAll('path')
				.data(function(d) {
					return [d];
				})
				.enter().append('path')
				.attr('d', d3.svg.line()
					.x(function(d, i) {
						return xScale(i + 1);
					})
					.y(function(d, i) {
						if (index.indexOf("java.opts") != -1) {
							var s = d["coefficientNameValuePair"][index];
							var p = s.match(/\d/g);
							p = p.join("");
							return yScale1(p);
						} else {
							return yScale1(d['coefficientNameValuePair'][index]);
						}
					}).interpolate("monotone")
				);

			svg.selectAll("g.y_axis2").remove();

			svg.selectAll("g.legend").style("opacity", function(d, i) {
				return ".5";
			});


			svg.select("#legends" + i).style("opacity", function(d, i) {
				return "1";
			})
			$("g.legend text").css("font-weight", "normal");
			$("#legends" + i + " text").css("font-weight", "bold");

			var maxValue = {};
			for (var j = 0; j < legendsArr.length; j++) {
				var key = legendsArr[j];
				var max = Number(dataset[0][0]['coefficientNameValuePair'][key]);
				var curr;
				for (var i = 0; i < dataset[0].length; i++) {
					curr = Number(dataset[0][i]['coefficientNameValuePair'][key]);
					if (curr > max) {
						curr = max;
					}
				}
				maxValue[key] = max;
			}

			var keydatatype = {
				"io.file.buffer.size": "memory",
				"io.seqfile.compression.type": "string",
				"dfs.namenode.handler.count": "number",
				"dfs.datanode.handler.count": "number",
				"io.sort.factor": "number",
				"io.sort.mb": "number",
				"io.sort.record.percent": "percent",
				"io.sort.spill.percent": "percent",
				"mapred.child.java.opts": "string",
				"mapred.compress.map.output": "string",
				"mapred.job.reduce.input.buffer.percent": "percent",
				"mapred.job.reduce.input.buffer.percent": "percent",
				"mapred.job.reuse.jvm.num.tasks": "",
				"mapred.job.shuffle.input.buffer.percent": "percent",
				"mapred.job.shuffle.merge.percent": "percent",
				"mapred.map.child.java.opts": "string",
				"mapred.max.split.size": "memory",
				"mapred.min.split.size": "memory",
				"mapred.output.compress": "string",
				"mapred.output.compression.codec": "string",
				"mapred.output.compression.type": "string",
				"mapred.reduce.child.java.opts": "string",
				"mapred.reduce.parallel.copies": "number",
				"mapred.reduce.tasks": "number",
				"mapred.tasktracker.map.tasks.maximum": "number",
				"mapred.tasktracker.reduce.tasks.maximum": "number",
				"mapreduce.input.fileinputformat.split.maxsize": "memory",
				"mapreduce.input.fileinputformat.split.minsize": "memory",
				"mapreduce.job.jvm.numtasks": "number",
				"mapreduce.job.reduces": "number",
				"mapreduce.map.java.opts": "string",
				"mapreduce.map.memory.mb": "number",
				"mapreduce.map.output.compress": "string",
				"mapreduce.map.output.compress.codec": "string",
				"mapreduce.map.sort.spill.percent": "percent",
				"mapreduce.output.fileoutputformat.compress": "string",
				"mapreduce.output.fileoutputformat.compress.type": "string",
				"mapreduce.reduce.input.buffer.percent": "percent",
				"mapreduce.reduce.java.optsdfs.block.size": "memory",
				"mapreduce.reduce.memory.mb": "number",
				"mapreduce.reduce.shuffle.input.buffer.percent": "percent",
				"mapreduce.reduce.shuffle.merge.percent": "percent",
				"mapreduce.reduce.shuffle.parallelcopies": "number",
				"mapreduce.task.io.sort.factor": "number",
				"mapreduce.task.io.sort.mb": "number",
				"mapreduce.tasktracker.http.threads": "number",
				"mapreduce.tasktracker.map.tasks.maximum": "number",
				"mapreduce.tasktracker.reduce.tasks.maximum": "number",
				"tasktracker.http.threads": "number"
			};

			var appendUnit = function(value) {
				if (keydatatype[index] == "percent") {
					value = (value * 100).toFixed(2);
					if (value.slice(-1) == '0') {
						value = value.slice(0, -1);
						if (value.slice(-1) == '0') {
							value = value.slice(0, -2);
						}
					}
					return value + "%";
				} else if (keydatatype[index] == "number") {
					return value;
				} else if (keydatatype[index] == "memory") {
					var MAX = maxValue[index];
					var d = value;
					if ((d / 1073741824) >= 1) {
						d = d / 1073741824;
						// if max values less than 20 GB
						if (MAX > 21474836480) {
							d = Math.ceil(d);
						} else {
							d = d.toFixed(2);
						}
						suffix = " GB";
					} else if ((d / 1048576) >= 1) {
						d = d / 1048576;
						if (MAX > 20971520) {
							d = Math.ceil(d);
						} else {
							d = d.toFixed(1);
						}
						suffix = " MB";
					} else if ((d / 1024) >= 1) {
						d = d / 1024;
						if (MAX > 20480) {
							d = Math.ceil(d);
						} else {
							d = d.toFixed(1);
						}
						suffix = " KB";
					} else {
						d = Math.ceil(d);
						suffix = " Bytes";
					}
					return d + suffix;
				} else if (index.indexOf('java.opts') > -1) {
					return "-Xmx" + value + 'm';
				} else {
					return value;
				}
			}

			/*var yAxis1 = d3.svg.axis()
			.scale(yScale1)
			.orient("right")
			.tickFormat(myFunction)
			.ticks(4);*/


			var yAxis1 = d3.svg.axis()
				.scale(yScale1)
				.orient("right")
				.tickFormat(appendUnit)


			/*var bytesToString = function (bytes) {
			    var fmt = d3.format('.0f');
			    if (bytes < 1024) {
			        return bytes + 'B';
			    } else if (bytes < 1024 * 1024) {
			        return fmt(bytes / 1024) + 'KB';
			    } else if (bytes < 1024 * 1024 * 1024) {
			        return fmt(bytes / (1024 * 1024)) + 'MB';
			    } else {
			        return fmt(bytes / (1024 * 1024 * 1024)) + 'GB';
			    }
			}
			if(index == "mapreduce.input.fileinputformat.split.maxsize"){
			    //Define Y axis
			    var yAxis1 = d3.svg.axis()
			        .scale(yScale1)
			        .orient("right")
			        .tickFormat(bytesToString)
			        .ticks(4);
			}else{
			    //Define Y axis
			    var yAxis1 = d3.svg.axis()
			    .scale(yScale1)
			    .orient("right")
			    .ticks(4);
			}*/

			/** Add Y axis */
			d3.select("#line_chart").append("g")
				.attr("class", "y_axis2")
				.attr("transform", "translate(" + (w * .8 + 20) + "," + 0 + ")")
				.call(yAxis1);

			svg.select(".y_axis2")
				.append('text')
				.attr("x", (-h / 2))
				.attr("y", "150")
				.attr('class', 'label')
				.text(index)
				.attr("transform", "rotate(-90)")
				/*.style("text-anchor", "end")*/
				.style("text-anchor", "middle")
				.style("fill", "#FF8533");
		}

	}
]);
