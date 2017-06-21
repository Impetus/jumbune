/* Dashboard controller */
'use strict';
angular.module('analyzeData.ctrl', ["ui.grid", 'ui.bootstrap', 'ui.grid.pagination'])
	.controller('AnalyzeData', ['$scope', 'common', '$http', '$location', '$timeout', 'uiGridConstants', 'getTableDataFactory', 'getXmlTableDataFactory', 'getJsonTableDataFactory', function($scope, common, $http, $location, $timeout, uiGridConstants, getTableDataFactory, getXmlTableDataFactory, getJsonTableDataFactory) {
		//Voilation detail grid
		var jobName                        = common.getOptimizeJobName();
		$scope.showJobName                 = jobName;
		$scope.violationTable              = {};
		$scope.violationJSONTable          = {};
		$scope.violationXmlTable           = {};
		$scope.tableHideFlag               = false;
		$scope.tableJSONHideFlag           = false;
		$scope.dataRecievedFlag            = false;
		$scope.dataFlag                    = false;
		$scope.jsonDataFlag                = false;
		$scope.counterValidationFlagJson   = false;
		$scope.hideTopFieldCounter         = false;
		$scope.hideTopTypeViolationMessage = false;
		$scope.webSocketErrorFlag = false;
		$scope.licenseExpireTrue = false;
        $scope.licenseExpireDays = false;
		var graphJson                      = [{
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

		var graphJson2  = [];
		
		var noOfDates   = [];
		var data        = [];
		var data_option = [];
		$scope.init     = function() {
			$scope.selectedGraphPoint = {};
			var host                  = window.location.hostname;
			var port                  = window.location.port;
			var jobName               = common.getOptimizeJobName();
			$('[data-toggle="tooltip"]').tooltip();
            licenseExpireMessage();
			if (jobName != null && jobName != undefined && jobName.length != 0) {               
				localStorage.setItem('jobName', jobName);
			} else {            
				jobName = localStorage.getItem('jobName');
			}   
			$scope.finalJobName = jobName;
			if ($scope.finalJobName == null || $scope.finalJobName == undefined || $scope.finalJobName == "") {
				$location.path('/');
			} else {
				     
			var url = "ws://" + host + ":" + port + "/results/jobanalysis?jobName=" + $scope.finalJobName;                  
			$scope.showJobName = $scope.finalJobName;
			var webSocket = new WebSocket(url);

			webSocket.onopen = function(event) {
				console.log("socket opened sucessfully")

			};

			webSocket.onmessage = function(event) {
				$('.ring-loader').remove();
				$('.message-loader').remove();
				$scope.webSocketErrorFlag = true;
				var serverData = angular.copy(event.data);
				console.log("webSocket data", serverData);
				var localData = JSON.parse(serverData).DATA_QUALITY_TIMELINE;
				var doErrorsExist = $scope.containErrors(localData);
				if (doErrorsExist) {
					return;
				}
				//$scope.createGraph(localData); 
				//$scope.dataQualityTimeline(localData);
				$scope.makeAndShowGraph(localData);
			};
			webSocket.onerror = function(error) {
				$scope.displayErrorMessage("Connection lost to server");
			};
			webSocket.onclose = function(event) {
				if ($scope.webSocketErrorFlag == false) {
						$scope.displayErrorMessage("Connection lost to server");
					} else {
						console.log("socket closed sucessfully")
					}
			};
			
			}

			//$scope.dataProfiling();
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
			localData = JSON.parse(localData);
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

		$scope.displayErrorMessage = function(errorMessageToDisplay) {

			var dataValGraph = common.getDQTFlag();
			if (dataValGraph) {
				$("#tuningWidgetWrapper").remove();
			}
			var errorDiv = document.getElementById('errorMessage');
			errorDiv.style.display = '';
			errorDiv.innerHTML = '<i class="fa fa-exclamation-triangle" style="font-size: 22px; margin:10px;"></i>' + errorMessageToDisplay;
			$('.widgetWrapper').css({'height': 600,'background-color': 'white'});
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
		var colors = d3.scale.category20();
		var keyColor = function(d, i) {
			return colors(d.key)
		};

		$scope.convertJumbuneJsonToGraphJson = function(jsonVal) {
			var dirtyValues = [];
			var nullValues = [];
			var dataTypeValues = [];
			var regexValues = [];
			var fieldValues = [];
			var obj = JSON.parse(jsonVal);
			var dates = Object.keys(obj)
			noOfDates = dates;
			for (var i = 0; i < dates.length; i++) {
				var nullTemp = [];
				var dataTypeTemp = [];
				var pureTemp = [];
				var regexTemp = [];
				var fieldTemp = [];

				var totalTuples = obj[dates[i]]["totalTupleProcessed"];

				var jsonReport = JSON.parse(obj[dates[i]].jsonReport);
				var keysPresent = Object.keys(jsonReport);
				var date = parseInt(dates[i]);


				//calculation of pure values
				pureTemp[0] = date;
				var pureTuples = obj[dates[i]]["cleanTuple"];
				if (pureTuples == null) {
					pureTuples = 0;
				}
				pureTemp[1] = pureTuples / 1;
				dirtyValues[i] = pureTemp;

				//calculation of null violations
				nullTemp[0] = date;
				var nullInfected = 0;
				if (keysPresent.indexOf("Null Check") >= 0) {
					nullInfected = jsonReport["Null Check"]["dirtyTuple"];
				}
				nullTemp[1] = nullInfected; //((nullInfected/totalTuples)*100)//
				nullValues[i] = nullTemp;

				//calculation of datatype violations 
				dataTypeTemp[0] = date;
				var dataTypeInfected = 0;
				if (keysPresent.indexOf("Data Type") >= 0) {
					dataTypeInfected = jsonReport["Data Type"]["dirtyTuple"];
				}
				dataTypeTemp[1] = dataTypeInfected; //((dataTypeInfected/totalTuples)*100)//
				dataTypeValues[i] = dataTypeTemp;

				//calculation of Regex violations 
				regexTemp[0] = date;
				var regexInfected = 0;
				if (keysPresent.indexOf("Regex") >= 0) {
					regexInfected = jsonReport["Regex"]["dirtyTuple"];
				}
				regexTemp[1] = regexInfected; //((dataTypeInfected/totalTuples)*100)//
				regexValues[i] = regexTemp;

				//calculation of Number of fields violations 
				fieldTemp[0] = date;
				var fieldInfected = 0;
				if (keysPresent.indexOf("Number of Fields") >= 0) {
					fieldInfected = jsonReport["Number of Fields"]["dirtyTuple"];
				}
				fieldTemp[1] = fieldInfected; //((dataTypeInfected/totalTuples)*100)//
				fieldValues[i] = fieldTemp;

			} // End of for

			var graphData = JSON.stringify(graphJson)
			graphData = JSON.parse(graphData)
			graphData[0]["values"] = dirtyValues;
			graphData[1]["values"] = nullValues;
			graphData[2]["values"] = dataTypeValues;
			graphData[3]["values"] = regexValues;
			graphData[4]["values"] = fieldValues;
			return graphData;
		}

		$scope.makeGraph = function(graphJsonData) {
			var chart = nv.models.stackedAreaChart()
				.useInteractiveGuideline(true)
				.x(function(d) {
					return d[0]
				})
				.y(function(d) {
					return d[1]
				})
				.controlLabels({ stacked: "Stacked" })
				.color(keyColor)
				.duration(300);
			chart.xAxis.tickFormat(function(d) {
				return d3.time.format('%H:%M-%d/%m')(new Date(d))
			});
			chart.yAxis.tickFormat(d3.format(',.2f'));


			document.getElementById('chart1').innerHTML = '';
			d3.select('#chart1')
				.append('svg')
				.attr('height', '600')
				.datum(graphJsonData)
				.transition().duration(1000)
				.call(chart)
				.each('start', function() {
					setTimeout(function() {
						d3.selectAll('#chart1 *').each(function() {
							if (this.__transition__)
								this.__transition__.duration = 1;
						})
					}, 0)
				});

			nv.utils.windowResize(chart.update);
			return chart;
		}

		$scope.makeGraphSchemaBasedOnKeysPresent = function(initialJson, finalJson) {
			var obj = JSON.stringify(initialJson)
			obj = JSON.parse(obj);
			var dates = Object.keys(obj)

			var arrIndex = parseInt(0);
			graphJson2[arrIndex++] = finalJson[0];

			var nullFlag = false;
			var dataTypeFlag = false;
			var regexFlag = false;
			var noOfFieldsFlag = false;

			for (var i = 0; i < dates.length; i++)

			{
				var jsonReport = JSON.parse(obj[dates[i]].jsonReport);
				var keysPresent = Object.keys(jsonReport);

				if (keysPresent.indexOf("Null Check") >= 0 && nullFlag == false) {
					graphJson2[arrIndex++] = finalJson[1];
					nullFlag = true;
				}
				if (keysPresent.indexOf("Data Type") >= 0 && dataTypeFlag == false) {
					graphJson2[arrIndex++] = finalJson[2]
					dataTypeFlag = true;
				}
				if (keysPresent.indexOf("Regex") >= 0 && regexFlag == false) {
					graphJson2[arrIndex++] = finalJson[3];
					regexFlag = true;
				}
				if (keysPresent.indexOf("Number of Fields") >= 0 && noOfFieldsFlag == false) {
					graphJson2[arrIndex++] = finalJson[4]
					noOfFieldsFlag = true;
				}

			}
			return graphJson2;
		}

		$scope.makeAndShowGraph = function(json) {
			graphJson = $scope.convertJumbuneJsonToGraphJson(json);
			graphJson2 = $scope.makeGraphSchemaBasedOnKeysPresent(JSON.parse(json), graphJson);
			var chartData = $scope.makeGraph(graphJson2);
			nv.addGraph(chartData);
		}
	}]);
