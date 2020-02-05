/* Analyze data text controller */
'use strict';
angular.module('analyzeDataText.ctrl', ["ui.grid", 'ui.bootstrap', 'ui.grid.pagination'])
    .controller('AnalyzeDataText', ['$scope', 'common', '$http', '$location', '$timeout', 'uiGridConstants', 'getTableDataFactory', 'getXmlTableDataFactory', 'getJsonTableDataFactory', function($scope, common, $http, $location, $timeout, uiGridConstants, getTableDataFactory, getXmlTableDataFactory, getJsonTableDataFactory) {
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
        $scope.counterValidationFlagJson = false;
        $scope.noDataViolatnFlag = false;
        $scope.noDataViolatnTable = false;
        $scope.hideTopFieldCounter = false;
        $scope.hideTopTypeViolationMessage = false;
        $scope.webSocketErrorFlag = false;
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
            var jobName = common.getJobName();
            $scope.finalJobName = jobName;  
            if ($scope.finalJobName == null || $scope.finalJobName == undefined || $scope.finalJobName == "") {
                $location.path('/index');
            } 
            else {
                if (document.location.protocol === 'https:') {
					var url = "wss://" + host + ":" + port + "/results/jobanalysis?jobName=" + jobName;
				} else {
					var url = "ws://" + host + ":" + port + "/results/jobanalysis?jobName=" + jobName;
				}


                var webSocket = new WebSocket(url);                  
                $scope.showJobName = $scope.finalJobName;

                webSocket.onopen = function(event) {
                    console.log("socket opened sucessfully");
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
                    $scope.safeApply($scope.counterValidationFlag = true);
                    $scope.safeApply($scope.noDataViolatnTable = true);
                    var KeyData = (Object.keys(localData)[0]);
                    var dataKey = localData[KeyData].jsonReport
                    $scope.jsonData = JSON.parse(dataKey)
                    if ($scope.jsonData['Null Check'] == undefined  && $scope.jsonData['Data Type'] == undefined                               && $scope.jsonData['Regex'] == undefined                                 && $scope.jsonData['Number of Fields'] == undefined) {                           
                        if ($scope.jsonData['DVSUMMARY'] != undefined) {                               
                            angular.forEach($scope.jsonData, function(obj, key) {         
                                $scope.list = obj['violationList'];          
                                angular.forEach($scope.list, function(item, index, array) {
                                    $scope.fileNameRecieve.push(item.fileName);
                                });                                                                       
                            });
                            $scope.createGraph(localData);                    
                            $scope.dataViolationGraph();                    
                            $scope.dataValidationGraph();
                            $scope.noDataViolatnTable = false;
                            $scope.safeApply($scope.noDataViolatnFlag = true);
                        }                    
                    } else {
                        angular.forEach($scope.jsonData, function(obj, key) {                
                            $scope.list = obj['violationList'];                    
                            angular.forEach($scope.list, function(item, index, array) {
                                $scope.fileNameRecieve.push(item.fileName);
                            });                                           
                        });

                                            
                        $scope.createGraph(localData);                    
                        $scope.dataViolationGraph();                    
                        $scope.dataValidationGraph();                                            
                    }



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
        $scope.tableJSON = function(legendData) {
            $scope.tableJSONHideFlag = true;
            $scope.violationJSONTable.label = legendData;
            
        }
        $scope.table = function(legendData) {
            $scope.tableHideFlag = true;
            $scope.violationTable.label = legendData;
           
        }
        $scope.tableChart = function(legendData) {
            $scope.tableHideFlag = true;
            $scope.violationTable.label = legendData.data.nodeData.age;
           
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
                    console.log("Unable to fetch violations of file [" + fileName + "]", e);
                });
        }

        $scope.sampledetails = function(data) {
            $scope.gridOptionsTest = {
                paginationPageSizes: [10, 50, 100],
                paginationPageSize: 10,

                enableSorting: true,
                columnDefs: [

                        { field: 'lineNumber', index: 'lineNumber', displayName: 'LineNumber', width: "25%", align: "center", sorttype: "integer" },
                        { field: 'fieldNumber', index: 'fieldNumber', displayName: 'FieldNumber', width: "25%", align: "center", sorttype: "integer" },
                        { field: 'expectedValue', index: 'expectedValue', displayName: 'ExpectedValue', width: "25%", align: "center" },
                        { field: 'fileName', index: 'fileName', displayName: 'FileName', visible: false },
                        { field: 'actualValue', index: 'actualValue', displayName: 'ActualValue', width: "25%", align: "center" }
                    ]
                   
            };
        };



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
            $scope.gridOptions.data = data;
            $scope.dataRecievedFlag = true;
        }

        $scope.totalViolationsArray = new Array();
        $scope.fieldArray = new Array();
        $scope.createGraph = function(dataMaster) {
            for (var key in dataMaster) {
                var jsonObj = JSON.parse(dataMaster[key].jsonReport);
                $scope.countersDataValidation(jsonObj);
                
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
                    for (var sub_key in sub_data_type) {
                        var per_data_type1 = Math.round((sub_data_type[sub_key] / sum_sub_data_type) * 10000, 2) / 100;
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
                $scope.printGraph1("#tuplesGraph", sunburstJson, jsonObj);
                var per_clean_tuple = Math.round((cleanTuple / (cleanTuple + dirtyTuple)) * 10000, 2) / 100;
                var per_dirty_tuple = Math.round((dirtyTuple / (cleanTuple + dirtyTuple)) * 10000, 2) / 100;
                $scope.cleanTupleArr = per_clean_tuple;
                $scope.dirtyTupleArr = per_dirty_tuple;
                data_option.push({ "age": "Clean Tuple", "population": cleanTuple, "percent": per_clean_tuple });
                data_option.push({ "age": "Violated Tuple", "population": dirtyTuple, "percent": per_dirty_tuple });


            }

        }


        $scope.countersDataValidation = function(jsonObj) {
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
            var num_type = 0;
            if (jsonObj["Number of Fields"]) {
                num_type = jsonObj["Number of Fields"].totalViolations;
            }
            var cleanTuple = 0;
            var dirtyTuple = 0;
            if (jsonObj["DVSUMMARY"]) {
                cleanTuple = jsonObj["DVSUMMARY"].cleanTuples;
                dirtyTuple = jsonObj["DVSUMMARY"].dirtyTuples;
            }
            var nullViolationsEle = new CountUp("nullViolationsId", 0, null_check, 0, 2);
            nullViolationsEle.start();
            var regexViolationsEle = new CountUp("regexViolationsId", 0, regex, 0, 2);
            regexViolationsEle.start();
            var dataTypeViolationsEle = new CountUp("dataTypeViolationsId", 0, data_type, 0, 2);
            dataTypeViolationsEle.start();
            var numberOfFieldsViolationsEle = new CountUp("numberOfFieldsViolationsId", 0, num_type, 0, 2);
            numberOfFieldsViolationsEle.start();

            var cleanTupleEle = new CountUp("cleanTupleId", 0, cleanTuple, 0, 2);
            cleanTupleEle.start();

            var dirtyTupleEle = new CountUp("dirtyTupleId", 0, dirtyTuple, 0, 2);
            dirtyTupleEle.start();
            var map = {
                'Null Check': null_check,
                'Regex': regex,
                'Data Type': data_type,
                'No. of Fields': num_type
            };
            map = sortObject(map);

            if (null_check == 0 && regex == 0 && data_type == 0 && num_type == 0) {
                $scope.safeApply($scope.hideTopTypeViolationMessage = true);
            } else {
                var str = map[0].key;
                var maxNum = map[0].value;
                var containsMultipleType = false;
                for (var i = 1; i < map.length; i++) {
                    if (maxNum == map[i].value) {
                        str = str + ', ' + map[i].key;
                        containsMultipleType = true;
                    } else {
                        break;
                    }
                }
                document.getElementById('topTypeViolation').innerHTML = str;
                if (containsMultipleType) {
                    document.getElementById('topTypeViolationMessage').innerHTML = "contain maximum violations";
                } else {
                    document.getElementById('topTypeViolationMessage').innerHTML = "contains maximum violations";
                }
            }
            // Creating File Violations List
            var fileViolationsMap = {};
            for (var violationName in jsonObj) {
                if ('DVSUMMARY' == violationName) {
                    continue;
                }
                var violationObj = JSON.parse(JSON.stringify(jsonObj[violationName]));
                var fileViolationsList = violationObj['violationList'];
                for (var i = 0; i < fileViolationsList.length; i++) {
                    var fileObj = fileViolationsList[i];
                    var fileName = fileObj['fileName'];
                    var numOfViolations = fileObj['numOfViolations'];

                    if (!fileViolationsMap[fileName]) {
                        fileViolationsMap[fileName] = fileObj['numOfViolations'];
                    } else {
                        fileViolationsMap[fileName] = fileViolationsMap[fileName] + numOfViolations;
                    }
                }
            }
            var fileViolationsListDesc = sortObject(fileViolationsMap);
            // Keeping top 3 elements and removing rest
            fileViolationsListDesc.splice(5, fileViolationsListDesc.length);
            $scope.fileViolationsListNumber = fileViolationsListDesc;
            $scope.safeApply($scope.fileViolationsListNumber);

            // Finding Most polluted Field
            var fieldViolationsMap = {};
            for (var violationName in jsonObj) {
                if ('DVSUMMARY' == violationName) {
                    continue;
                }
                var violationObj = JSON.parse(JSON.stringify(jsonObj[violationName]));
                var fieldsMap = violationObj['fieldMap'];
                for (var fieldNumber in fieldsMap) {
                    var value = fieldsMap[fieldNumber];
                    if (!fieldViolationsMap[fieldNumber]) {
                        fieldViolationsMap[fieldNumber] = value;
                    } else {
                        fieldViolationsMap[fieldNumber] = fieldViolationsMap[fieldNumber] + value;
                    }
                }
            }

            var fieldViolationsListDesc = sortObject(fieldViolationsMap);

            if (fieldViolationsListDesc == null || fieldViolationsListDesc == undefined || fieldViolationsListDesc.length == 0) {
                $scope.safeApply($scope.hideTopFieldCounter = true);
            } else {
                var topField = fieldViolationsListDesc[0].key;
                if (Number(topField) == -1) {
                    $scope.safeApply($scope.hideTopFieldCounter = true);
                } else {
                    var topFieldValue = fieldViolationsListDesc[0].value;
                    document.getElementById('topFieldViolation').innerHTML = topField;
                }
            }

            //}

            function sortObject(obj) {
                var arr = [];
                var prop;
                for (prop in obj) {
                    if (obj.hasOwnProperty(prop)) {
                        arr.push({
                            'key': prop,
                            'value': obj[prop]
                        });
                    }
                }
                arr.sort(function(a, b) {
                    return a.value < b.value;
                });
                return arr; // returns array
            }

        }

        $scope.generateTableForViolations = function(violationType) {
            $scope.dataFlag = false;
            $scope.table(violationType);
        }

        $scope.dataValidationGraph = function() {


            var width = 500,
                height = 250,
                radius = Math.min(width, height) / 2;

            var color = d3.scale.ordinal()
                .range(["#367031", "#AB4644", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);

            var arc = d3.svg.arc()
                .padAngle(.02)
                .outerRadius(radius - 10)
                .innerRadius(0);

            var pie = d3.layout.pie()
                .sort(null)
                .value(function(d) {
                    return d.population;
                });

            var tooltip = d3.select("body")
                .append("div")
                .attr("class", "tooltip")
                .style("position", "absolute")
                .style("z-index", "10")
                .style("opacity", 0);

            var svg = d3.select("#cleanViolatedData").append("svg")
                .attr("width", width)
                .attr("height", height)
                .append("g")
                .attr("transform", "translate(" + width / 3 + "," + height / 2 + ")");

            data = data_option;

            data.forEach(function(d) {
                d.population = +d.population;
            });

            var g = svg.selectAll(".arc")
                .data(pie(data))
                .enter().append("g")
                .attr("class", "arc");

            g.append("path")
                .attr("d", arc)
                .style("fill", function(d) {
                    return color(d.data.age);
                })
                .on("mouseover", function(d) {
                    tooltip.style("display", "block");
                    tooltip.html(function() {
                        return d.data.age + " : " + d.data.population;

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
            var legend = svg.selectAll(".legend")
                .data(color.domain())
                .enter().append("g")
                .attr("class", "legend")
                .attr("transform", function(d, i) {
                    return "translate(" + width / 3 + "," + (i * 20) + ")";
                });

            legend.append("text")
                .attr("x", 35)
                .attr("y", 9)
                .attr("dy", ".35em")
                .style("text-anchor", "start")
                .text(function(d) {
                    return d;
                });

            legend.append("rect")
                .attr("x", 10)
                .attr("width", 18)
                .attr("height", 18)
                .attr("class", "dot_legend")
                .style("fill", color)
                .on("mouseout", function() {

                });
        }

        $scope.printGraph1 = function(idWithHash, json, jsonC) {
            var width = 470,
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
                .attr("transform", "translate(" + width / 4 + "," + (height / 2) + ")");
            var colorMap = {
                'Clean Tuples': '#66BB6A',
                'Dirty Tuples': '#eF5350',
                'Total Tuples': '#FFF',
                'Total Violations': '#FFF',
                'Fatal Error': '#246184',
                'Data Type': '#FFA726',
                'Null Type': '#FFCA28',
                'Regex': '#FF7043',
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
                .attr("transform", "translate(120,30)")
                .style("font-size", "12px")
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
        var newLegend = window;
        newLegend.xyz = function(d) {
            var str = d.key;
            //var res = str.substring(0, str.indexOf(" Violations"));
            var res = str.replace(/[0-9]/g, '');
            var res1 = res.replace(/(^[\s]+|[\s]+$)/g, '');
            $scope.dataFlag = false;
            $scope.table(res1);
        }
        $scope.printGraph = function(idWithHash, json) {
            var width = 530,
                height = 250,
                radius = (Math.min(width, height) / 2) - 10;
            var formatNumber = d3.format(",d");
            var x = d3.scale.linear()
                .range([0, 2 * Math.PI]);
            var y = d3.scale.sqrt()
                .range([0, radius]);
            var color = {
                'Total Violations': '#FFF',
                'Data Type': '#FFA726',
                'Null Check': '#FFCA28',
                'Number of Fields': '#8D6E63',
                'Regex': '#FF7043'
            }
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
                .attr("transform", "translate(" + width / 4 + "," + (height / 2) + ")");

            svg.selectAll("path")
                .data(partition.nodes(json))
                .enter().append("path")
                .attr("data-legend", function(d) {
                   
                    var stringNoViolation = (d.children ? d : d.parent).name.replace(' Violations','');
                    if ((d.children ? d : d.parent).name == "Data Type Violations") {
                        if ($scope.totalViolationsArray["Data Type"] == undefined) {
                            return (d.children ? d : d.parent).name;
                        } else {
                            return stringNoViolation + " " + $scope.totalViolationsArray["Data Type"];

                        }
                    } else if ((d.children ? d : d.parent).name == "Null Check Violations") {
                        if ($scope.totalViolationsArray["Null Check"] == undefined) {
                            return (d.children ? d : d.parent).name;
                        } else {
                           
                            return stringNoViolation + " " + $scope.totalViolationsArray["Null Check"];

                        }

                    } else if ((d.children ? d : d.parent).name == "Regex Violations") {
                        if ($scope.totalViolationsArray["Regex"] == undefined) {
                            return (d.children ? d : d.parent).name;
                        } else {
                           
                            return stringNoViolation + " " + $scope.totalViolationsArray["Regex"];

                        }

                    } else if ((d.children ? d : d.parent).name == "Number of Fields Violations") {
                        
                        if ($scope.totalViolationsArray["Number of Fields"] == undefined) {
                            return (d.children ? d : d.parent).name;
                        } else {
                            
                            return stringNoViolation + " " + $scope.totalViolationsArray["Number of Fields"];

                        }

                    }
                })
                .attr("d", arc)
                .style("fill", function(d) {
                    if ((d.children ? d : d.parent).name == 'Total Violations') {
                        return color[(d.children ? d : d.parent).name];
                    } else {
                        var str = (d.children ? d : d.parent).name;
                        var res = str.substring(0, str.lastIndexOf(" "));
                        return color[res];
                    }

                })
                .style('stroke', '#fff')
                .on("click", click)
                .append("title")
                .text(function(d) {
                    return d.name + "\n" + formatNumber(d.value);
                });
           
            var legend = svg.append("g")
                .attr("class", "legend")
                .attr("transform", "translate(105,30)")
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

        $scope.dataViolationGraph = function() {
            var width = 500,
                height = 250,
                maxRadius = Math.min(width, height) / 2;
            var svg = d3.select('#dataViolationGraphId').append('svg').attr('width', width).attr('height', height).append('g').attr('transform', 'translate(' + width / 3 + ',' + height / 2 + ')');
            var multiLevelData = [];

            var setMultiLevelData = function(data) {
                if (data == null)
                    return;
                var level = data.length,
                    counter = 0,
                    index = 0,
                    currentLevelData = [],
                    queue = [];
                for (var i = 0; i < data.length; i++) {
                    
                    queue.push(data[i]);
                }
                
                while (!queue.length == 0) {
                    var node = queue.shift();
                    currentLevelData.push(node);
                    level--;
                    if (node.subData) {
                        for (var i = 0; i < node.subData.length; i++) {
                            queue.push(node.subData[i]);
                            counter++;
                        };
                    }
                    if (level == 0) {
                        level = counter;
                        counter = 0;
                        multiLevelData.push(currentLevelData);
                        currentLevelData = [];
                    }
                }
            };
            var drawPieChart = function(_data, index) {
                var pie = d3.layout.pie().sort(null).padAngle(.01).value(function(d) {
                    return d.nodeData.population;
                });
                var tooltip = d3.select("body")
                    .append("div")
                    .attr("class", "tooltip")
                    .style("position", "absolute")
                    .style("z-index", "10")
                    .style("opacity", 0);
                var arc = d3.svg.arc().padAngle(.01).outerRadius((index + 1) * pieWidth - 1).innerRadius(index * pieWidth);
                var g = svg.selectAll('.arc' + index).data(pie(_data)).enter().append('g').attr('class', 'arc' + index);

                g.append('path')
                    .attr('d', arc)
                    .style('fill', function(d) {
                        return color(d.data.nodeData.age);
                    })
                    .on("click", function(legendData) {
                        $scope.tableChart(legendData);

                    })
                    .on("mouseover", function(d) {
                        tooltip.style("display", "block");
                        tooltip.html(function() {
                            if (d.data.nodeData.age == "Data Type") {
                                if (index == 0) {
                                    return d.data.nodeData.age + " : " + d.data.nodeData.percent + "%";
                                } else {
                                    return d.data.nodeData.age + " :: Field" + d.data.nodeData.fieldMap + " : " + d.data.nodeData.percent + "%";
                                }

                            } else if (d.data.nodeData.age == "Null Check") {
                                if (index == 0) {
                                    return d.data.nodeData.age + " : " + d.data.nodeData.percent + "%";
                                } else {
                                    return d.data.nodeData.age + " :: Field" + d.data.nodeData.fieldMap + " : " + d.data.nodeData.percent + "%";
                                }
                            } else if (d.data.nodeData.age == "Regex") {
                                if (index == 0) {
                                    return d.data.nodeData.age + " : " + d.data.nodeData.percent + "%";
                                } else {
                                    return d.data.nodeData.age + " :: Field" + d.data.nodeData.fieldMap + " : " + d.data.nodeData.percent + "%";
                                }
                            } else if (d.data.nodeData.age == "Number of Fields") {
                                
                                return d.data.nodeData.age + " : " + d.data.nodeData.percent + "%";
                                
                            }
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

                g.append('text').attr('transform', function(d) {
                        return 'translate(' + arc.centroid(d) + ')';
                    }).style("fill", "#FFF").attr('dy', '.35em').style('text-anchor', 'middle').text(function(d) {
                        return d.data.nodeData.percent + "%";
                    }).on("mouseover", function(d) {
                        tooltip.style("display", "block");
                        tooltip.html(function() {
                            if (d.data.nodeData.age == "Data Type") {
                                if (index == 0) {
                                    return d.data.nodeData.age + " : " + d.data.nodeData.percent + "%";
                                } else {
                                    return d.data.nodeData.age + " :: Field" + d.data.nodeData.fieldMap + " : " + d.data.nodeData.percent + "%";
                                }

                            } else if (d.data.nodeData.age == "Null Check") {
                                if (index == 0) {
                                    return d.data.nodeData.age + " : " + d.data.nodeData.percent + "%";
                                } else {
                                    return d.data.nodeData.age + " :: Field" + d.data.nodeData.fieldMap + " : " + d.data.nodeData.percent + "%";
                                }
                            } else if (d.data.nodeData.age == "Regex") {
                                if (index == 0) {
                                    return d.data.nodeData.age + " : " + d.data.nodeData.percent + "%";
                                } else {
                                    return d.data.nodeData.age + " :: Field" + d.data.nodeData.fieldMap + " : " + d.data.nodeData.percent + "%";
                                }
                            } else if (d.data.nodeData.age == "Number of Fields") {
                                
                                return d.data.nodeData.age + " : " + d.data.nodeData.percent + "%";
                                
                            }
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
            };
            setMultiLevelData(data);
            var pieWidth = parseInt(maxRadius / multiLevelData.length) - multiLevelData.length;
           
            var color = d3.scale.ordinal()
                .domain(["Null Check", "Data Type", "Regex", "Number of Fields"])
                .range(["#D78542", "#AC4547", "#A87B7E", "#1f77b4"]);

            for (var i = 0; i < multiLevelData.length; i++) {
               
                var _cData = multiLevelData[i];
                drawPieChart(_cData, i);
            }

            var legend = svg.selectAll(".legend")
                .data(color.domain())
                .enter().append("g")
                .attr("class", "legend")
                .style("cursor", "pointer")
                .on("click", function(legendData) {
                    $scope.table(legendData);
                    
                })
                .attr("transform", function(d, i) {
                    return "translate(" + width / 4 + "," + (i * 20) + ")";
                });

            legend.append("text")
                .attr("x", 35)
                .attr("y", 9)
                .attr("dy", ".35em")
                .style("text-anchor", "start")
                .text(function(d) {
                    if (d == "Data Type") {
                        if ($scope.totalViolationsArray["Data Type"] == undefined) {
                            return d;
                        } else {
                            return d + " " + $scope.totalViolationsArray["Data Type"];

                        }
                    } else if (d == "Null Check") {
                        if ($scope.totalViolationsArray["Null Check"] == undefined) {
                            return d;
                        } else {
                            return d + " " + $scope.totalViolationsArray["Null Check"];

                        }

                    } else if (d == "Regex") {
                        if ($scope.totalViolationsArray["Regex"] == undefined) {
                            return d;
                        } else {
                            return d + " " + $scope.totalViolationsArray["Regex"];

                        }

                    } else if (d == "Number of Fields") {
                        
                        if ($scope.totalViolationsArray["Number of Fields"] == undefined) {
                            return d;
                        } else {
                            return d + " " + $scope.totalViolationsArray["Number of Fields"];

                        }

                    }

                });

            legend.append("rect")
                .attr("x", 10)
                .attr("width", 18)
                .attr("height", 18)
                .attr("class", "dot_legend")
                .style("fill", color)
                .on("mouseout", function() {

                });
        }


    }]);
