/* Analyze data XML controller */
'use strict';
angular.module('analyzeDataXml.ctrl', ["ui.grid", 'ui.bootstrap', 'ui.grid.pagination'])
    .controller('AnalyzeDataXml', ['$scope', 'common', '$http', '$location', '$timeout', 'uiGridConstants', 'getTableDataFactory', 'getXmlTableDataFactory', 'getJsonTableDataFactory', function($scope, common, $http, $location, $timeout, uiGridConstants, getTableDataFactory, getXmlTableDataFactory, getJsonTableDataFactory) {
            //Voilation detail grid
            var jobName = common.getJobName();
            $scope.showJobName = jobName;
            $scope.violationTable = {};
            $scope.violationJSONTable = {};
            $scope.violationXmlTable = {};
            $scope.tableHideFlag = false;
            $scope.tableJSONHideFlag = false;
            $scope.dataRecievedFlag = false;
            $scope.dataFlag = false;
            $scope.jsonDataFlag = false;
            $scope.noXmlDataViolatnFlag = false;
            $scope.counterValidationFlagJson = false;
            $scope.hideTopFieldCounter = false;
            $scope.hideTopTypeViolationMessage = false;
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
            var xyz = function(d){
            }

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

                var url = "ws://" + host + ":" + port + "/results/jobanalysis?jobName=" + $scope.finalJobName;
                $scope.showJobName = $scope.finalJobName;
                var webSocket = new WebSocket(url);

                webSocket.onopen = function(event) {
                    console.log("socket opened sucessfully")

                };

                webSocket.onmessage = function(event) {
                    $('.ring-loader').remove();
                    var serverData = angular.copy(event.data);
                    console.log("webSocket data", serverData);
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
                        $scope.safeApply($scope.noXmlDataViolatnFlag = true);
                    }
                    for (var key in localData) {
                        var jsonObj = JSON.parse(localData[key].jsonReport);
                        finaljson = jsonObj;
                        $scope.XMLDataValidation(jsonObj);
                    }
                    $scope.safeApply($scope.counterValidationFlag = true);
                };
                webSocket.onerror = function(error) {
                    console.log(error);
                    $scope.displayErrorMessage("Connection lost to server");
                };
                webSocket.onclose = function(event) {
                    console.log("socket closed sucessfully")
                };

                function closeSocket() {
                    webSocket.close();
                }
            }
            $scope.XMLDataValidation = function(json) {
                var sunburstJson = $scope.getSunburstJsonForXMLValidation(json);
                printGraphXml('#xmlGraphViolations', sunburstJson, json);
                setXmlValidationCounter(json)
                    //$scope.safeApply($scope.counterValidationFlag = true);
            }

            function setKeyCounter(json, key) {
                var count = 0;
                if (json[key] != null) {
                    count = json[key].totalKeyViolation;

                }
                var ele = new CountUp(key + 'Id', 0, count, 0, 2);
                ele.start();
            }

            function setXmlValidationCounter(json) {
                // Setting Counters
                var totalViolations = 0;
                for (var violationName in json) {
                    totalViolations = json[violationName]['totalViolations'];
                    break;
                }
                new CountUp('TotalViolationsId', 0, totalViolations, 0, 2).start();

                var NullCheck = json['NullCheck'];
                if (NullCheck != null) {
                    new CountUp('NullCheckId', 0, NullCheck['individualViolations'], 0, 2).start();
                }

                var FatalError = json['FatalError'];
                if (FatalError != null) {
                    new CountUp('FatalErrorId', 0, FatalError['individualViolations'], 0, 2).start();
                }

                var OtherXMLError = json['OtherXMLError'];
                if (OtherXMLError != null) {
                    new CountUp('OtherXMLErrorId', 0, OtherXMLError['individualViolations'], 0, 2).start();
                }

                var Regex = json['Regex'];
                if (Regex != null) {
                    new CountUp('RegexId', 0, Regex['individualViolations'], 0, 2).start();
                }

                var DataType = json['DataType'];
                if (DataType != null) {
                    new CountUp('DataTypeId', 0, DataType['individualViolations'], 0, 2).start();
                }

                var cleanTuples = json['DVSUMMARY'].cleanTuples;
                new CountUp('cleanTuplesId', 0, cleanTuples, 0, 2).start();

                var dirtyTuples = json['DVSUMMARY'].dirtyTuples;
                new CountUp('dirtyTuplesId', 0, dirtyTuples, 0, 2).start();

                printCleanTuplesGraphForXML(cleanTuples, dirtyTuples,json);
            }

            function printCleanTuplesGraphForXML(cleanTuples, dirtyTuples,jsonC) {
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
                printSunburstForCleanDirtyXmlDV("#xmlTuplesGraph", sunburstJson,jsonC);
            }

            function printSunburstForCleanDirtyXmlDV(idWithHash, json,jsonC) {
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
                    'FatalError': '#246184',
                    'DataType': '#2d78a4',
                    'NullType': '#3690c4',
                    'Regex': '#53a2d0',
                    'OtherXMLErrors': '#73b3d9',
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
                //var legendArr = ["Clean Tuple", "Violated Tuple"]
               /* var legendArr = d3.scale.ordinal()
                    .domain(["Clean Tuple", "Violated Tuple"])
                    .range(["#6baed6", "#3182bd"]);
                var legend = svg.selectAll(".legend")
                    .data(legendArr.domain())
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
                var color1 = d3.scale.category20c();
                legend.append("rect")
                    .attr("x", 10)
                    .attr("width", 18)
                    .attr("height", 18)
                    .attr("class", "dot_legend")
                    .style("fill", legendArr)
                    .on("mouseout", function() {

                    });*/
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

            $scope.getSunburstJsonForXMLValidation = function(json) {
                var children = [];
                for (var violationType in json) {
                    children.push({
                        'name': violationType,
                        'size': json[violationType].individualViolations
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
                $scope.selectedViolationXML = violationType;
                $scope.tableDataXML = finaljson[violationType].violationList;
                $scope.tableJSON(violationType)


            }
            var newLegend = window;
            newLegend.xyz = function(d){
              var str = d.key;
                var res = str.substring(0, str.lastIndexOf(" "));
                $scope.generateTableForViolations(res)
            }

            function printGraphXml(idWithHash, sunburstJson, json) {
                var width = 350,
                    height = 250,
                    radius = (Math.min(width, height) / 2);
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
                var color = {
                    'Total Violations': '#FFF',
                    'DataType': '#FFA726',
                    'NullCheck': '#FFCA28',
                    'OtherXMLError': '#8D6E63',
                    'FatalError': '#BDBDBD',
                    'Regex': '#FF7043'
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
                        if (d.name == "FatalError") {
                            if (json["FatalError"] == undefined) {
                                return d.name;
                            } else {
                                return d.name + " " + json["FatalError"].individualViolations;

                            }
                        } else if (d.name == "OtherXMLError") {
                            if (json["OtherXMLError"] == undefined) {
                                return d.name;
                            } else {
                                return d.name + " " + json["OtherXMLError"].individualViolations;

                            }

                        } else if (d.name == "Regex") {
                            if (json["Regex"] == undefined) {
                                return d.name;
                            } else {
                                return d.name + " " + json["Regex"].individualViolations;

                            }

                        } else if (d.name == "NullCheck") {
                            //return d+" "+json["Number of Fields"];
                            if (json["NullCheck"] == undefined) {
                                return d.name;
                            } else {
                                return d.name + " " + json["NullCheck"].individualViolations;

                            }

                        } else if (d.name == "DataType") {
                            //return d+" "+json["Number of Fields"];
                            if (json["DataType"] == undefined) {
                                return d.name;
                            } else {
                                return d.name + " " + json["DataType"].individualViolations;

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

                /*var legendArr = d3.scale.ordinal()
                    .domain(["DataType", "NullCheck", "OtherXMLError", "FatalError", "Regex"])
                    .range(["#72B3D8", "#3184B3", "#3690c4", "#53a2d0", "#73b3d9"]);*/
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
                    //localData = JSON.parse(localData);
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
									console.log("Invalid json");
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
                $('body').css('background-color', "white");
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
                //$scope.violationTable.value = legendData.value;
                $scope.applyFun();

            }

            $scope.getXMLCall = function(fileName) {
                $scope.showFileName = fileName;
                var dvType = $scope.selectedViolationXML;
                var page = 1;
                var rows = 1000;
                getXmlTableDataFactory.getXmlTableData({
                        "fileName": fileName,
                        "jobName": $scope.finalJobName,
                        "dvType": dvType,
                        "page": page,
                        "rows": rows
                    }, {},
                    function(data) {
                        $scope.finalTableData = data;
                        $scope.gridOptionsXmlTest.data = data.rows;
                        $scope.jsonDataFlag = true;


                    },
                    function(e) {});

            }
            var cellToolTipTemplate = '<div class="ui-grid-cell-contents" title="{{COL_FIELD}}">Custom:{{ COL_FIELD }}</div>'
            $scope.sampledetailsXML = function(data) {
                /* var paginationOptions = {
                    pageNumber: 1,
                    pageSize: 5,
                    sort: null
                  };*/
                //$scope.getCall(fileName)
                $scope.gridOptionsXmlTest = {
                    paginationPageSizes: [10, 50, 100],
                    paginationPageSize: 10,

                    enableSorting: true,
                    columnDefs: [

                            { field: 'lineNumber', index: 'lineNumber', displayName: 'LineNumber', width: "15%", align: "center", sorttype: "integer" },
                            /*{field:'fieldNumber',index:'fieldNumber', displayName: 'FieldNumber', width: "25%", align:"center", sorttype: "integer"},
                            {field:'expectedValue',index:'expectedValue', displayName: 'ExpectedValue', width: "25%", align:"center"},
                            {field:'fileName',index:'fileName', displayName: 'FileName',  visible: false},*/
                            { field: 'message', index: 'message', displayName: 'Error Message', width: "85%", align: "center", style: "cursor:pointer", cellTemplate: cellToolTipTemplate }
                        ]
                        /*,
                                            onRegisterApi: function(gridApi){
                                                $scope.gridApi = gridApi;
                                                $scope.gridApi.core.on.sortChanged($scope, function(grid, sortColumns) {
                                                    if (sortColumns.length == 0) {
                                                      paginationOptions.sort = null;
                                                    } else {
                                                      paginationOptions.sort = sortColumns[0].sort.direction;
                                                    }
                                                });
                                                gridApi.pagination.on.paginationChanged($scope, function (newPage, pageSize) {
                                                    paginationOptions.pageNumber = newPage;
                                                    paginationOptions.pageSize = pageSize;

                                                });
                                            }*/
                };
                /*$scope.gridOptionsTest.data = data.rows;
                $scope.dataFlag = true;*/

            };
        }

    ]);
