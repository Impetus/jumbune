/* Analyze data JSON controller */
'use strict';
angular.module('analyzeDataJson.ctrl', ["ui.grid", 'ui.bootstrap', 'ui.grid.pagination'])
	.controller('AnalyzeDataJson', ['$scope', 'common', '$http', '$location', '$timeout', 'uiGridConstants', 'getTableDataFactory', 'getXmlTableDataFactory', 'getJsonTableDataFactory', function($scope, common, $http, $location, $timeout, uiGridConstants, getTableDataFactory, getXmlTableDataFactory, getJsonTableDataFactory) {
			//Voilation detail grid
			var jobName = common.getJobName();
			$scope.showJobName = jobName
			$scope.violationTable = {};
			$scope.violationJSONTable = {};
			$scope.violationXmlTable = {};
			$scope.tableHideFlag = false;
			$scope.tableJSONHideFlag = false;
			$scope.dataRecievedFlag = false;
			$scope.dataFlag = false;
			$scope.jsonDataFlag = false;
			$scope.noJsonDataViolatnFlag = false;
			$scope.counterValidationFlagJson = false;
			$scope.hideTopFieldCounter = false;
			$scope.hideTopTypeViolationMessage = false;
			$scope.webSocketErrorFlag = false;
			var finaljson;
			var graphJson = [{
				"key": "clean Tuples",
				"values": ""
			}, {
				"key": "null Checks",
				"values": ""
			}, {
				"key": "DataType Checks",
				"values": ""
			}, {
				"key": "Regex Check",
				"values": ""
			}, {
				"key": "Number of Fields",
				"values": ""
			}];

			var graphJson2 = [];
			var noOfDates = [];
			var data = [];
			var data_option = [];

			$scope.init = function() {
				$('[data-toggle="tooltip"]').tooltip();
				$scope.selectedGraphPoint = {};
				$scope.gridOptionsTest = {};
				$scope.gridOptionsTestJson = {};
				$scope.gridOptionsXmlTest = {};
				$scope.gridFinalTest = {};
				$scope.finalTableData = {};
				$scope.fileNameRecieve = [];
				$scope.tableData = [{ "fieldName": "data", "violations": "12398" }];

				var host = window.location.hostname;
				var port = window.location.port;

				var jobName = common.getJobName();

				$scope.finalJobName = jobName;
				if ( $scope.finalJobName == null || $scope.finalJobName == undefined || $scope.finalJobName == "") {
				 	$location.path('/index');
				} else {
					if (document.location.protocol === 'https:') {
                        var url = "wss://" + host + ":" + port + "/results/jobanalysis?jobName=" + $scope.finalJobName;;
                    } else {
                        var url = "ws://" + host + ":" + port + "/results/jobanalysis?jobName=" + $scope.finalJobName;;
                    }


				$scope.showJobName = $scope.finalJobName;
				var webSocket = new WebSocket(url);

				webSocket.onopen = function(event) {
					console.log("socket opened sucessfully")

				};

				webSocket.onmessage = function(event) {
					$('.ring-loader').remove();
					$scope.webSocketErrorFlag = true;
					var serverData = angular.copy(event.data);
					var localData = JSON.parse(JSON.parse(serverData).DATA_VALIDATION);
					var doErrorsExist = $scope.containErrors(localData);
						if (doErrorsExist) {
							return;
						}
					var KeyData = (Object.keys(localData)[0]);
                    var dataKey = localData[KeyData].jsonReport
                    var dvData = JSON.parse(dataKey);
                    var dvSummary = dvData['DVSUMMARY'].dirtyTuples;
                    if ( dvSummary == 0 || dvSummary == undefined) {
                    	$scope.safeApply($scope.noJsonDataViolatnFlag = true);
                    }
					for (var key in localData) {
						var jsonObj = JSON.parse(localData[key].jsonReport);
						finaljson = jsonObj;
						$scope.JsonDataValidation(jsonObj);
					}
					$scope.safeApply($scope.counterValidationFlag = true);
				};
				webSocket.onerror = function(error) {
					$scope.displayErrorMessage("Connection lost to server");
				};
				webSocket.onclose = function(event) {
					if ( $scope.webSocketErrorFlag == false) {
						$scope.displayErrorMessage("Connection lost to server");
					} else {
						console.log("socket closed sucessfully")
					}
				};

				}
				
			}
			$scope.JsonDataValidation = function(json) {
				var sunburstJson = $scope.getSunburstJsonForJsonValidation(json);
				printGraphJSON('#jsonGraphViolations', sunburstJson, json);
				setJsonValidationCounter(json);
			}


			function setJsonValidationCounter(json) {
				// Setting Counters
				var totalViolations = 0;
				for (var violationName in json) {
					totalViolations = json[violationName].totalViolation;
					break;
				}

				if (totalViolations != undefined && totalViolations != 0) {
					new CountUp('totalViolationsId', 0, totalViolations, 0, 2).start();
				}

				var NullKey = json['NullKey'];
				if (NullKey != null) {
					new CountUp('NullKeyId', 0, NullKey['totalKeyViolation'], 0, 2).start();
				}

				var DataKey = json['DataKey'];
				if (DataKey != null) {
					new CountUp('DataKeyId', 0, DataKey['totalKeyViolation'], 0, 2).start();
				}

				var MissingKey = json['MissingKey'];
				if (MissingKey != null) {
					new CountUp('MissingKeyId', 0, MissingKey['totalKeyViolation'], 0, 2).start();
				}

				var JsonSchemaKey = json['JsonSchemaKey'];
				if (JsonSchemaKey != null) {
					new CountUp('JsonSchemaKeyId', 0, JsonSchemaKey['totalKeyViolation'], 0, 2).start();
				}

				var RegexKey = json['RegexKey'];
				if (RegexKey != null) {
					new CountUp('RegexKeyId', 0, RegexKey['totalKeyViolation'], 0, 2).start();
				}

				// Setting counters for clean tuples and dirty tuples
				var cleanTuples = json['DVSUMMARY'].cleanTuples;
				new CountUp('cleanTuplesId', 0, cleanTuples, 0, 2).start();

				var dirtyTuples = json['DVSUMMARY'].dirtyTuples;
				new CountUp('dirtyTuplesId', 0, dirtyTuples, 0, 2).start();

				printCleanTuplesGraphForJson(cleanTuples, dirtyTuples,json);
			}

			function printCleanTuplesGraphForJson(cleanTuples, dirtyTuples,jsonC) {
				var sunburstJson = {
					name: 'Total Tuples',
					children: [{
						'name': 'Clean Tuples',
						'size': cleanTuples
					}, {
						'name': 'Dirty Tuples',
						'size': dirtyTuples
					}]
				};
				printSunburstForCleanDirtyJsonDV("#jsonTuplesGraph", sunburstJson,jsonC);
			}

			function printSunburstForCleanDirtyJsonDV(idWithHash, json,jsonC) {
				var width = 500,
					height = 250,
					radius = (Math.min(width, height) / 2) - 10;
				var formatNumber = d3.format(",d");
				var x = d3.scale.linear()
					.range([0, 2 * Math.PI]);
				var y = d3.scale.sqrt()
					.range([0, radius]);
				var color = d3.scale.category20c();
				var partition = d3.layout.partition()
					.value(function(d) {
						return d.size;
					});
				var arc = d3.svg.arc()
					.startAngle(function(d) {
						return Math.max(0, Math.min(2 * Math.PI, x(d.x)));
					})
					.endAngle(function(d) {
						return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx)));
					})
					.innerRadius(function(d) {
						return Math.max(0, y(d.y)) - 5;
					})
					.outerRadius(function(d) {
						return Math.max(0, y(d.y + d.dy));
					});
				var svg = d3.select(idWithHash).append("svg")
					.attr("width", width)
					.attr("height", height)
					.append("g")
					.attr("transform", "translate(" + width / 2 + "," + (height / 2) + ")");
				var colorMap = {
					'Clean Tuples': '#66BB6A',
					'Dirty Tuples': '#eF5350',
					'Total Violations': '#FFF',
					'Total Tuples': '#FFF',
					'Fatal Error': '#246184',
					'Data Type': '#2d78a4',
					'Null Type': '#3690c4',
					'Regex': '#53a2d0',
					'Other XML Errors': '#73b3d9',
					'NullKey': '#246184',
					'DataKey': '#2d78a4',
					'JsonSchemaKey': '#3690c4',
					'MissingKey': '#53a2d0',
					'RegexKey': '#73b3d9'
				}
				svg.selectAll("path")
					.data(partition.nodes(json))
					.enter().append("path")
					.attr("data-legend", function(d) {
						if (d.name == "Clean Tuples") {
                            if (jsonC["Clean Tuples"] == undefined) {
                                return d.name;
                            }
                        } else if (d.name == "Dirty Tuples") {
                            if (jsonC["Dirty Tuples"] == undefined) {
                                return d.name;
                            }
                        }
					})
					.attr("d", arc)
					.style("fill", function(d) {
						var c = colorMap[d.name];
						if (c != null) {
							return c;
						}
						return color((d.children ? d : d.parent).name);
					})
					.on("click", click)
					.style('stroke', '#fff')
					.append("title")
					.text(function(d) {
						return d.name + "\n" + formatNumber(d.value);
					});
					var legend = svg.append("g")
                    .attr("class", "legend")
                    .attr("transform", "translate(200,30)")
                    .style("font-size", "12px")
                    .style("cursor", "pointer")
                    .call(d3.legend)
				function click(d) {
					svg.transition()
						.duration(750)
						.tween("scale", function() {
							var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
								yd = d3.interpolate(y.domain(), [d.y, 1]),
								yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
							return function(t) {
								x.domain(xd(t));
								y.domain(yd(t)).range(yr(t));
							};
						})
						.selectAll("path")
						.attrTween("d", function(d) {
							return function() {
								return arc(d);
							};
						});
				}
				d3.select(self.frameElement).style("height", height + "px");
			}

			$scope.getSunburstJsonForJsonValidation = function(json) {
				var children = [];
				for (var violationType in json) {
					children.push({
						'name': violationType,
						'size': json[violationType].totalKeyViolation
					});
				}
				return {
					'name': 'Total Violations',
					'children': children
				};
			}

			$scope.generateTableForViolations = function(violationType) {
				$scope.jsonDataFlag = false;
				var name = violationType;
				$scope.selectedViolation = violationType;
				$scope.tableData = finaljson[violationType].fileViolationReport;
				$scope.tableJSON(violationType)

			}
			var newLegend = window;
            newLegend.xyz = function(d){
              var str = d.key;
                var res = str.substring(0, str.lastIndexOf(" "));
                $scope.generateTableForViolations(res)
            }
			function printGraphJSON(idWithHash, sunburstJson, json) {
				var width = 350,
					height = 250,
					radius = (Math.min(width, height) / 2);
				var formatNumber = d3.format(",d");
				var x = d3.scale.linear()
					.range([0, 2 * Math.PI]);
				var y = d3.scale.sqrt()
					.range([0, radius]);
				
				var partition = d3.layout.partition()
					.value(function(d) {
						return d.size;
					});
				var color = {
					'Total Violations': '#FFF',
					'NullKey': '#FFCA28',
					'DataKey': '#FFA726',
					'JsonSchemaKey': '#BDBDBD',
					'MissingKey': '#8D6E63',
					'RegexKey': '#FF7043'
				}
				var arc = d3.svg.arc()
					.startAngle(function(d) {
						return Math.max(0, Math.min(2 * Math.PI, x(d.x)));
					})
					.endAngle(function(d) {
						return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx)));
					})
					.innerRadius(function(d) {
						return Math.max(0, y(d.y)) - 10;
					})
					.outerRadius(function(d) {
						return Math.max(0, y(d.y + d.dy));
					});
				var svg = d3.select(idWithHash).append("svg")
					.attr("width", width)
					.attr("height", height)
					.append("g")
					.attr("transform", "translate(" + width / 2 + "," + (height / 2) + ")");


				svg.selectAll("path")
					.data(partition.nodes(sunburstJson))
					.enter().append("path")
					.attr("data-legend", function(d) {
                        if (d.name == "DataKey") {
                            if (json["DataKey"] == undefined) {
                                return d.name;
                            } else {
                                return d.name + " " + json["DataKey"].totalKeyViolation;

                            }
                        } else if (d.name == "JsonSchemaKey") {
                            if (json["JsonSchemaKey"] == undefined) {
                                return d.name;
                            } else {
                                return d.name + " " + json["JsonSchemaKey"].totalKeyViolation;

                            }

                        } else if (d.name == "RegexKey") {
                            if (json["RegexKey"] == undefined) {
                                return d.name;
                            } else {
                                return d.name + " " + json["RegexKey"].totalKeyViolation;

                            }

                        } else if (d.name == "NullKey") {
                            if (json["NullKey"] == undefined) {
                                return d.name;
                            } else {
                                return d.name + " " + json["NullKey"].totalKeyViolation;

                            }

                        } else if (d.name == "MissingKey") {
                            if (json["MissingKey"] == undefined) {
                                return d.name;
                            } else {
                                return d.name + " " + json["MissingKey"].totalKeyViolation;

                            }

                        }
                    })
					.attr("d", arc)
					.style("fill", function(d) {
						return color[d.name];
					})
					.on("click", click)
					.append("title")
					.text(function(d) {
						return d.name + "\n" + formatNumber(d.value);
					});

					var legend = svg.append("g")
                    .attr("class", "legend")
                    .attr("transform", "translate(200,30)")
                    .style("font-size", "12px")
                    .style("cursor", "pointer")
                    .call(d3.legend)
				function click(d) {
					svg.transition()
						.duration(750)
						.tween("scale", function() {
							var xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
								yd = d3.interpolate(y.domain(), [d.y, 1]),
								yr = d3.interpolate(y.range(), [d.y ? 20 : 0, radius]);
							return function(t) {
								x.domain(xd(t));
								y.domain(yd(t)).range(yr(t));
							};
						})
						.selectAll("path")
						.attrTween("d", function(d) {
							return function() {
								return arc(d);
							};
						});
				}
				d3.select(self.frameElement).style("height", height + "px");

			}

			$scope.containErrors = function(localData) {

				var dqtGraph = common.getDQTFlag();
				if (dqtGraph) {
					isJson(localData);
				}
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
			function isJson(str) {
                try {
                    JSON.parse(str);
                } catch (e) {
                    console.log("Invalid json received from server", e);
					return false;
                }
                return true;
            }
			$scope.displayErrorMessage = function(errorMessageToDisplay) {

				var dataValGraph = common.getDataValFlag();
				if (dataValGraph) {
					$("#resultWidgetContainer").remove();
				}
				var errorDiv = document.getElementById('errorMessage');
				errorDiv.style.display = '';
				errorDiv.innerHTML = '<i class="fa fa-exclamation-triangle" style="font-size: 22px; margin:10px;"></i>' + errorMessageToDisplay;
				$('.widgetRow').css({'height': 600,'background-color': 'white'});
			}
			$scope.safeApply = function(fn) {
				var phase = this.$root.$$phase;
				if (phase == '$apply' || phase == '$digest') {
					if (fn && (typeof(fn) === 'function')) {
						fn();
					}
				} else {
					this.$apply(fn);
				}
			};
			$scope.manualRefresh = function() {
				$scope.init();
			}
			$scope.clickedHomeIcon = function() {
				$location.path("/dashboard");
			}
			$scope.applyFun = function() {
				if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
				    $scope.$apply();
				}
			}
			$scope.tableJSON = function(legendData) {
				$scope.tableJSONHideFlag = true;
				$scope.violationJSONTable.label = legendData;
				$scope.applyFun()

			}
			$scope.table = function(legendData) {
				$scope.tableHideFlag = true;
				$scope.violationTable.label = legendData;
				$scope.applyFun()

			}
			$scope.tableChart = function(legendData) {
				$scope.tableHideFlag = true;
				$scope.violationTable.label = legendData.data.nodeData.age;
				$scope.applyFun()

			}

			$scope.getCall = function(fileName) {
				$scope.showFileName = fileName;
				var dvType = $scope.violationTable.label;
				var page = 1;
				var rows = 1000;
				getTableDataFactory.getTableData({
						"fileName": fileName,
						"jobName": jobName,
						"dvType": dvType,
						"page": page,
						"rows": rows
					}, {},
					function(data) {
						$scope.finalTableData = data;
						$scope.gridOptionsTest.data = data.rows;
						$scope.dataFlag = true;


					},
					function(e) {
						console.log(e)
					});

			}

			$scope.sampledetails = function(data) {
				$scope.gridOptionsTest = {
					paginationPageSizes: [10, 50, 100],
					paginationPageSize: 10,

					enableSorting: true,
					columnDefs: [

						{ field: 'lineNumber', index: 'lineNumber', displayName: 'LineNumber', width: "25%", align: "center", sorttype: "integer" },
						{ field: 'fieldNumber', index: 'fieldNumber', displayName: 'Key', width: "25%", align: "center", sorttype: "integer" },
						{ field: 'expectedValue', index: 'expectedValue', displayName: 'ExpectedValue', width: "25%", align: "center" },
						{ field: 'fileName', index: 'fileName', displayName: 'FileName', visible: false },
						{ field: 'actualValue', index: 'actualValue', displayName: 'ActualValue', width: "25%", align: "center" }
					]
				};


			};

			var cellToolTipTemplate = '<div class="ui-grid-cell-contents" title="{{COL_FIELD}}">Custom:{{ COL_FIELD }}</div>'

			$scope.getJSONCall = function(fileName) {
				$scope.showFileName = fileName;
				var dvType = $scope.selectedViolation;
				var page = 1;
				var rows = 1000;
				getJsonTableDataFactory.getJsonTableData({
						"fileName": fileName,
						"jobName": jobName,
						"dvType": dvType,
						"page": page,
						"rows": rows
					}, {},
					function(data) {
						$scope.finalTableData = data;
						$scope.gridOptionsTestJson.data = data.rows;
						$scope.jsonDataFlag = true;


					},
					function(e) {
						console.log(e)
					});

			}
			$scope.showJsonFileViolaions = function(data) {
				$scope.gridOptionsTestJson = {
					paginationPageSizes: [10, 50, 100],
					paginationPageSize: 10,
					enableSorting: true,
					columnDefs: [

						{ field: 'lineNumber', index: 'lineNumber', displayName: 'LineNumber', width: "25%", align: "center", sorttype: "integer" },
						{ field: 'fieldNumber', index: 'fieldNumber', displayName: 'Key', width: "25%", align: "center", sorttype: "integer" },
						{ field: 'expectedValue', index: 'expectedValue', displayName: 'ExpectedValue', width: "25%", align: "center" },
						{ field: 'fileName', index: 'fileName', displayName: 'FileName', visible: false },
						{ field: 'actualValue', index: 'actualValue', displayName: 'ActualValue', width: "25%", align: "center" }
					]
				};
			};

			$scope.totalViolationsArray = new Array();
			$scope.fieldArray = new Array();

			var colors = d3.scale.category20();
			var keyColor = function(d, i) {
				return colors(d.key)
			};

		}

	]);
