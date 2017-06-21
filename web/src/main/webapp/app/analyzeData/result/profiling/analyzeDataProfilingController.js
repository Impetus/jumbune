/* Dashboard controller */
'use strict';
angular.module('analyzeDataProfiling.ctrl', ["ui.grid", 'ui.bootstrap', 'ui.grid.pagination'])
	.controller('AnalyzeDataProfiling', ['$scope', 'common', '$http', '$location', '$timeout', 'uiGridConstants', 'getTableDataFactory', 'getXmlTableDataFactory', 'getJsonTableDataFactory', function($scope, common, $http, $location, $timeout, uiGridConstants, getTableDataFactory, getXmlTableDataFactory, getJsonTableDataFactory) {
		//Voilation detail grid
		var jobName = common.getOptimizeJobName();
		$scope.showJobName = jobName
		$scope.violationTable = {};
		$scope.violationJSONTable = {};
		$scope.violationXmlTable = {};
		$scope.tableHideFlag = false;
		$scope.tableJSONHideFlag = false;
		$scope.dataRecievedFlag = false;
		$scope.dataFlag = false;
		$scope.jsonDataFlag = false;
		$scope.counterValidationFlagJson = false;
		$scope.hideTopFieldCounter = false;
		$scope.hideTopTypeViolationMessage = false;
		$scope.webSocketErrorFlag = false;
		$scope.licenseExpireTrue = false;
        $scope.licenseExpireDays = false;
		var graphJson = [

			{
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
			}

		];

		var graphJson2 = [];

		var noOfDates = [];
		var data = [];
		var data_option = [];
		$scope.init = function() {
			$('[data-toggle="tooltip"]').tooltip();
            licenseExpireMessage();
			$scope.selectedGraphPoint = {};
			$scope.gridOptionsTest = {};
			$scope.gridOptionsTestJson = {};
			$scope.gridOptionsXmlTest = {};
			$scope.gridFinalTest = {};
			$scope.finalTableData = {};
			$scope.fileNameRecieve = [];
			$scope.tableData = [{ "fieldName": "data", "violations": "12398" }]
			var host = window.location.hostname;
			var port = window.location.port;
			var jobName = common.getOptimizeJobName();
			var numFlag = common.getNumField();
			$scope.finalJobName = jobName;
			if ( $scope.finalJobName == null || $scope.finalJobName == undefined || $scope.finalJobName == "") {
				 $location.path('/');
			} else {
				var url = "ws://" + host + ":" + port + "/results/jobanalysis?jobName=" + $scope.finalJobName;
				$scope.showJobName = $scope.finalJobName;
			var webSocket = new WebSocket(url);

			webSocket.onopen = function(event) {
				console.log("socket opened sucessfully")

			};

			webSocket.onmessage = function(event) {
				$('.ring-loader').remove();
				$scope.webSocketErrorFlag = true;
				var serverData = angular.copy(event.data);
				console.log("webSocket data", serverData);
				var localData = JSON.parse(JSON.parse(serverData).DATA_PROFILING);
				var doErrorsExist = $scope.containErrors(localData);
				if (doErrorsExist) {
					return;
				}
				if (numFlag == "" || numFlag == null || isNaN(numFlag)) {
					$scope.dataProfiling(localData);
				} else {
					$scope.draw(localData)
				}

			};
			webSocket.onerror = function(error) {
				console.log(error);
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
			
			//$scope.dataProfiling();
		}
		$scope.draw = function(data) {
			angular.forEach(data, function(obj, key) {
				var sum = "";
				sum = sum + (parseInt(obj.matched) + parseInt(obj.unMatched));
				obj.total = sum;

			});
			$scope.gridOptions = {
				enableColumnResizing: true,
				enableSorting: true,
				onRegisterApi: function(gridApi) {
					$scope.gridApi = gridApi;
				},
				fastWatch: true,
				columnDefs: [
					{ field: 'fieldNo', displayName: 'Field Number', width: "25%" },
					{ field: 'rule', displayName: 'Rule', width: "25%" },
					{ field: 'matched', displayName: 'Passed Value', width: "25%" },
					{ field: 'unMatched', displayName: 'unMatched Value', visible: false, width: "25%" },
					{ field: 'total', displayName: 'Total Value', width: "25%" }
				]
			};
			//$scope.gridOptions.hideColumn("unMatched");
			$scope.gridOptions.data = data;
			$scope.dataRecievedFlag = true;
			$scope.$apply();
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
		$scope.containErrors = function(localData) {

			var dqtGraph = common.getDQTFlag();
			if (dqtGraph) {
				//localData = JSON.parse(localData);
				//localData = (localData);
				isJson(localData);
			}
			/*var errorMessageToDisplay;
			if (localData == undefined) {
				$scope.displayErrorMessage("Unable to fetch data from server");
			}
			var errorData = localData.ErrorAndException;
			if (errorData != null) {

				if (jQuery.isEmptyObject(errorData)) {
					errorMessageToDisplay = "Error occurs while running job."
				} else {
					for (var key in errorData) {
						if (key == undefined) {
							continue;
						}
						if (key != errorData[key]) {
							errorMessageToDisplay = key + '. ' + errorData[key];
						} else {
							errorMessageToDisplay = key + ".";
						}
						errorMessageToDisplay = key + ".";
					}
				}
				$scope.displayErrorMessage(errorMessageToDisplay);
				return true;*/
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
		        return false;
		        console.log("Invalid json")
		    }
		    return true;
		}
		$scope.displayErrorMessage = function(errorMessageToDisplay) {

			var dataValGraph = common.getProfilingFlag();
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
			$location.path("/");
		}
		$scope.tableJSON = function(legendData) {
			$scope.tableJSONHideFlag = true;
			$scope.violationJSONTable.label = legendData;
			//$scope.violationTable.value = legendData.value;
			$scope.$apply();

		}
		$scope.table = function(legendData) {
			$scope.tableHideFlag = true;
			$scope.violationTable.label = legendData;
			//$scope.violationTable.value = legendData.value;
			$scope.$apply();

		}
		$scope.tableChart = function(legendData) {
			$scope.tableHideFlag = true;
			$scope.violationTable.label = legendData.data.nodeData.age;
			//$scope.violationTable.value = legendData.value;
			$scope.$apply();

		}

		$scope.totalViolationsArray = new Array();
		$scope.fieldArray = new Array();
		$scope.createGraph = function(dataMaster) {
			for (var key in dataMaster) {

				var jsonObj = JSON.parse(dataMaster[key].jsonReport);
				$scope.countersDataValidation(jsonObj);
				//$scope.JsonDataValidation(jsonObj);
				//var data_type = (jsonObj["data_type"] == "undefined")? 0:jsonObj["Data Type"].totalViolations;

				//var null_check = (jsonObj["Null Check"] == "undefined")? 0:jsonObj["Null Check"].totalViolations;

				var regex = 0;
				if (jsonObj["Regex"]) {
					regex = jsonObj["Regex"].totalViolations;
				}
				var null_check = 0;
				if (jsonObj["Null Check"]) {
					null_check = jsonObj["Null Check"].totalViolations;
				}
				var data_type = 0;
				if (jsonObj["Data Type"]) {
					data_type = jsonObj["Data Type"].totalViolations;
				}
				if (angular.equals({}, jsonObj["DVSUMMARY"])) {
					$("#dataValidationWrap .widgetRow, #dataValidationWrap .widgetWrapper").hide();
					$("#dataValidationWrap").append('<div class="yellow" style="padding:5px;">No Data Violations Found</div>');
				}
				var num_type = 0;
				if (jsonObj["Number of Fields"]) {
					num_type = jsonObj["Number of Fields"].totalViolations;
				}
				$scope.totalViolationsArray["Regex"] = regex;
				$scope.totalViolationsArray["Data Type"] = data_type;
				$scope.totalViolationsArray["Null Check"] = null_check;
				$scope.totalViolationsArray["Number of Fields"] = num_type;

				var sunburstJson = {
					'name': 'Total Violations'
				};
				sunburstJson['children'] = [];
				for (var violationName in jsonObj) {
					var violationObj = jsonObj[violationName];
					var obj = {};
					obj['name'] = violationName + ' Violations';
					obj['children'] = [];

					for (var fieldNumber in violationObj.fieldMap) {
						var temp = {};
						temp['name'] = 'Field No. : ' + fieldNumber;
						temp['size'] = violationObj.fieldMap[fieldNumber];
						obj['children'].push(temp);
					}
					sunburstJson['children'].push(obj);
				}
				$scope.printGraph("#dataValidationGraph", sunburstJson);
				//("in createGraph",null_check)
				var sum_main_total = data_type + null_check + regex;
				var sum_num_total = num_type;
				var per_data_type = Math.round((data_type / sum_main_total) * 10000, 2) / 100;
				var per_regex = Math.round((regex / sum_main_total) * 10000, 2) / 100;
				var per_null_check = Math.round((null_check / sum_main_total) * 10000, 2) / 100;
				var per_num_field = Math.round((num_type / sum_num_total) * 10000, 2) / 100;
				$scope.pecentageDataArray = per_data_type;
				$scope.pecentageRegexArray = per_regex;
				$scope.pecentageNullArray = per_null_check;
				$scope.pecentageNumArray = per_num_field;

				if (jsonObj["Number of Fields"]) {
					var sub_num_type = jsonObj["Number of Fields"].fieldMap;
					var sum_sub_num_type = 0;
					for (var sub_key in sub_num_type) {
						sum_sub_num_type = sum_sub_num_type + sub_num_type[sub_key];
					}


					var sub_num_type_array = [];
					//var fieldMap =[];
					for (var sub_key in sub_num_type) {
						var per_data_type1 = Math.round((sub_num_type[sub_key] / sum_sub_num_type) * 10000, 2) / 100;
						var fieldMap = (sub_key)
						sub_num_type_array.push({ "nodeData": { "age": "Number of Fields", "population": sub_num_type[sub_key], "percent": per_data_type1, "fieldMap": fieldMap } })
					}

					data.push({ "nodeData": { "age": "Number of Fields", "population": num_type, "percent": per_num_field }, "subData": sub_num_type_array });
				}



				if (jsonObj["Data Type"]) {
					var sub_data_type = jsonObj["Data Type"].fieldMap;
					var sum_sub_data_type = 0;
					for (var sub_key in sub_data_type) {
						sum_sub_data_type = sum_sub_data_type + sub_data_type[sub_key];
					}


					var sub_data_type_array = [];
					//var fieldMap =[];
					for (var sub_key in sub_data_type) {
						var per_data_type1 = Math.round((sub_data_type[sub_key] / sum_sub_data_type) * 10000, 2) / 100;
						//fieldMap.push(sub_key)
						var fieldMap = (sub_key);
						sub_data_type_array.push({ "nodeData": { "age": "Data Type", "population": sub_data_type[sub_key], "percent": per_data_type1, "fieldMap": fieldMap } })
					}

					data.push({ "nodeData": { "age": "Data Type", "population": data_type, "percent": per_data_type }, "subData": sub_data_type_array });
				}
				if (jsonObj["Regex"]) {

					var sub_data_type = jsonObj["Regex"].fieldMap;

					var sum_sub_reg = 0;
					for (var sub_key in sub_data_type) {
						sum_sub_reg = sum_sub_reg + sub_data_type[sub_key];
					}

					var sub_data_type_array = [];
					for (var sub_key in sub_data_type) {
						var fieldMap = (sub_key);
						var per_data_type1 = Math.round((sub_data_type[sub_key] / sum_sub_reg) * 10000, 2) / 100;
						sub_data_type_array.push({ "nodeData": { "age": "Regex", "population": sub_data_type[sub_key], "percent": per_data_type1, "fieldMap": fieldMap } })
					}

					data.push({ "nodeData": { "age": "Regex", "population": regex, "percent": per_regex }, "subData": sub_data_type_array });

				}
				if (jsonObj["Null Check"]) {
					var sub_data_type = jsonObj["Null Check"].fieldMap;
					var sum_sub_null_check = 0;
					for (var sub_key in sub_data_type) {
						sum_sub_null_check = sum_sub_null_check + sub_data_type[sub_key];
					}
					var sub_data_type_array = [];
					for (var sub_key in sub_data_type) {
						var fieldMap = (sub_key);
						var per_data_type1 = Math.round((sub_data_type[sub_key] / sum_sub_null_check) * 10000, 2) / 100;
						sub_data_type_array.push({ "nodeData": { "age": "Null Check", "population": sub_data_type[sub_key], "percent": per_data_type1, "fieldMap": fieldMap } })
					}

					data.push({ "nodeData": { "age": "Null Check", "population": null_check, "percent": per_null_check }, "subData": sub_data_type_array });
				}
				var cleanTuple = 0;
				var dirtyTuple = 0;
				if (jsonObj["DVSUMMARY"]) {
					cleanTuple = jsonObj["DVSUMMARY"].cleanTuples;
					dirtyTuple = jsonObj["DVSUMMARY"].dirtyTuples;
				}
				//var cleanTuple = jsonObj["Data Type"].cleanTuple;
				//var dirtyTuple = jsonObj["Data Type"].dirtyTuple;
				sunburstJson = {
					'name': 'Total Tuples',
					'children': [{
						'name': 'Clean Tuples',
						'size': cleanTuple
					}, {
						'name': 'Dirty Tuples',
						'size': dirtyTuple
					}]
				};
				$scope.printGraph1("#tuplesGraph", sunburstJson);
				var per_clean_tuple = Math.round((cleanTuple / (cleanTuple + dirtyTuple)) * 10000, 2) / 100;
				var per_dirty_tuple = Math.round((dirtyTuple / (cleanTuple + dirtyTuple)) * 10000, 2) / 100;
				$scope.cleanTupleArr = per_clean_tuple;
				$scope.dirtyTupleArr = per_dirty_tuple;
				data_option.push({ "age": "Clean Tuple", "population": cleanTuple, "percent": per_clean_tuple });
				data_option.push({ "age": "Violated Tuple", "population": dirtyTuple, "percent": per_dirty_tuple });


			}

		}

		$scope.dataProfiling = function(data) {
			var w = 700;
			var h = 300;

			//var data = {"4948":58784,"392A":58414,"6214":57926,"null":25276,"3":25120};


			var dataset = [];
			var i = 0,
				sumTotal = 0;
			for (var key in data) {
				if (i === 5) {
					break;
				}
				i++;
				dataset.push({ "label": key, "value": data[key] });
				sumTotal = sumTotal + data[key];
			}

			var outerRadius = h / 2;
			var innerRadius = 0;
			var arc = d3.svg.arc()
				.innerRadius(innerRadius)
				.outerRadius(outerRadius);

			var pie = d3.layout.pie().value(function(d) {
				return d.value;
			});

			//Easy colors accessible via a 10-step ordinal scale
			var color = d3.scale.category10();

			//Create SVG element
			var svg = d3.select("#dataProfilingGrid")
				.append("svg")
				.attr("align", "center")
				.attr("width", w)
				.attr("height", h)
				.style("position", "relative")
				.style("left", "400")
				.style("top", "70");

			var tooltip = d3.select("body")
				.append("div")
				.attr("class", "tooltip")
				.style("position", "absolute")
				.style("z-index", "10")
				.style("opacity", 0);

			//Set up groups
			var arcs = svg.selectAll("g.arc")
				.data(pie(dataset))
				.enter()
				.append("g")
				.attr("class", "arc")
				.attr("transform", "translate(" + outerRadius + "," + outerRadius + ")");



			//Draw arc paths
			arcs.append("path")
				.attr('fill', function(d, i) {
					return color(i);
				})
				.attr("d", arc)
				.style('stroke', 'white')
				.style('stroke-width', 5)
				.on("mouseover", function(d) {
					tooltip.style("display", "block");
					tooltip.html(function() {
						return d.data.label + " : " + d.data.value;
					});
					return tooltip.transition()
						.duration(50)
						.style("opacity", 1);
				})
				.on("mousemove", function(d) {
					return tooltip
						.style("top", (d3.event.pageY - 10) + "px")
						.style("left", (d3.event.pageX + 10) + "px");
				})
				.on("mouseout", function() {
					return tooltip.style("display", "none");
				});



			//Labels
			arcs.append("text")
				.attr("transform", function(d) {
					return "translate(" + arc.centroid(d) + ")";
				})
				.attr("text-anchor", "middle")
				.text(function(d) {
					//return d.value;
					var per_data_type = Math.round((d.value / sumTotal) * 10000, 2) / 100;
					return per_data_type + "%";
				});

			var legend = svg.selectAll(".legend")
				.data(dataset)
				.enter().append("g")
				.attr("class", "legend")
				.attr("transform", function(d, i) {
					return "translate(" + w * .5 + "," + (i * 20) + ")";
				});

			legend.append("text")
				.attr("x", 35)
				.attr("y", 9)
				.attr("dy", ".35em")
				.style("text-anchor", "start")
				.text(function(d) {
					return d.label;
				});

			legend.append("rect")
				.attr("x", 10)
				.attr("width", 18)
				.attr("height", 18)
				.attr("class", "dot_legend")
				.attr('fill', function(d, i) {
					return color(i);
				})
				.on("mouseout", function() {

				});
		}
	}]);
