/* Dashboard controller */
'use strict';
angular.module('dataComparisonCtrl.ctrl', ["ui.grid", 'ui.bootstrap', 'ui.grid.pagination'])
    .controller('AnalyzeDataComp', ['$scope', 'common', '$http', '$location', '$timeout', 'uiGridConstants', 'getTableDataFactory', 'getDataComparisonTableFactory', function($scope, common, $http, $location, $timeout, uiGridConstants, getTableDataFactory, getDataComparisonTableFactory) {
            //Voilation detail grid
            var jobName = common.getOptimizeJobName();
            $scope.showJobName = jobName
            $scope.violationTable = {};
            $scope.violationJSONTable = {};
            $scope.tableHideFlag = false;
            $scope.tableCompHideFlag = false;
            $scope.dataFlag = false;
            $scope.DataFlag = false;
            $scope.webSocketErrorFlag = false;
            $scope.licenseExpireTrue = false;
            $scope.licenseExpireDays = false;
            var finaljson;
            
            var data = [];
            /** init function */
            $scope.init = function() {
                $('[data-toggle="tooltip"]').tooltip();
                licenseExpireMessage();
                $scope.gridOptionsDataComp = {};
                $scope.finalTableData = {};
                var host = window.location.hostname;
                var port = window.location.port;

                var jobName = common.getOptimizeJobName();

                $scope.finalJobName = jobName;   
                if ($scope.finalJobName == null || $scope.finalJobName == undefined || $scope.finalJobName == "") {
                    $location.path('/');
                } 
                else {
                    var url = "ws://" + host + ":" + port + "/results/jobanalysis?jobName=" + $scope.finalJobName;

                }
                $scope.showJobName = $scope.finalJobName;
                
                /** websocket call start */
                var webSocket = new WebSocket(url);

                webSocket.onopen = function(event) {
                    console.log("socket opened sucessfully")

                };

                webSocket.onmessage = function(event) {
                   $('.ring-loader').remove();
                    $scope.webSocketErrorFlag = true;
                    var serverData = angular.copy(event.data);
                    console.log("webSocket data", serverData);
                    var localData = JSON.parse(JSON.parse(serverData).DATA_VALIDATION);
                    //$scope.counterValidationFlag = true
                    finaljson = localData;
                    $scope.DataSourceComparison(localData);
                    $scope.safeApply($scope.counterValidationFlag = true);
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

                function closeSocket() {
                    webSocket.close();
                }

                /** websocket call end */

            }
            /** License expire message */
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
            /* Creates sunbrust json */
            $scope.DataSourceComparison = function(json) {
                var sunburstJson = $scope.getSunburstJsonForDataComparison(json);
                printGraphDataComparison('#dataComparisonGraphViolations', sunburstJson, json);
                setDataComparisonCounter(json)

            }

            /** Function to set counters */
            function setDataComparisonCounter(json) {
                // Setting Counters
                var totalViolations = 0;
                for (var violationType in json['transformationViolationMap']) {
                    for (var sum in json['transformationViolationMap'][violationType]['violations']) {
                        totalViolations += json['transformationViolationMap'][violationType]['violations'][sum]
                    }
                    new CountUp('totalTransformationViolations', 0, totalViolations, 0, 2).start();
                }
                if (json['invalidRows']['noOfFieldsViolation'] != undefined && json['invalidRows']['noOfFieldsViolation'] != 0) {
                    new CountUp('numberOfFieldViolationsId', 0, json['invalidRows']['noOfFieldsViolation'], 0, 2).start();
                }

                if (json['invalidRows'] != undefined && (json['invalidRows']['noOfFieldsViolation'] != undefined && json['invalidRows']['noOfFieldsViolation'] != 0) || (json['invalidRows']['transformationViolation'] != undefined && json['invalidRows']['transformationViolation'] != 0)) {
                    var total;
                    if (json['invalidRows']['noOfFieldsViolation'] == undefined || json['invalidRows']['noOfFieldsViolation'] == 0) {
                        total = json['invalidRows']['transformationViolation'];
                    } else if (json['invalidRows']['transformationViolation'] == undefined || json['invalidRows']['transformationViolation'] == 0) {
                        total = json['invalidRows']['noOfFieldsViolation'];
                    } else {
                        total = json['invalidRows']['noOfFieldsViolation'] + json['invalidRows']['transformationViolation'];
                    }
                    new CountUp('totalRowAffectedId', 0, total, 0, 2).start();
                }

                if (json['invalidRows']['transformationViolation'] != undefined && json['invalidRows']['transformationViolation'] != 0) {
                    new CountUp('rowAffectedTransformationViolationsId', 0, json['invalidRows']['transformationViolation'], 0, 2).start();
                }

            }

            /** Sunburst json for data source comparison */
            $scope.getSunburstJsonForDataComparison = function(json) {
                var children = [];
                for (var violationType in json['transformationViolationMap']) {
                    var value = 0;
                    for (var sum in json['transformationViolationMap'][violationType]['violations']) {
                        value += json['transformationViolationMap'][violationType]['violations'][sum]

                    }
                    
                    children.push({
                        'name': violationType,
                        'size': value
                    });
                }
                return {
                    'name': 'Total Violations',
                    'children': children
                };
            }

            /** Function generate violation table */
            $scope.generateTableForViolations = function(violationType) {
                $scope.DataFlag = false;
                var name = violationType;
                $scope.selectedViolation = violationType;
                $scope.tableData = finaljson['transformationViolationMap'][violationType].violations;
                
                $scope.tableComp(violationType)

            }
            var newLegend = window;
            newLegend.xyz = function(d) {
                var str = d.key;
                var res = str.substring(0, str.lastIndexOf(" "));
                $scope.generateTableForViolations(res)
            }

            /** Function creates data comparison graph */
            function printGraphDataComparison(idWithHash, sunburstJson, json) {
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
                var color = d3.scale.ordinal()
                    .domain(["Total Violations"])
                    .range(["#FFF", "#FFCA28", "#FFA726", "#BDBDBD", "#8D6E63", "#8D6E63", "#FF7043"]);
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
                        if (d.name == "Total Violations") {

                        } else {
                            return d.name + " " + formatNumber(d.value);
                        }

                        /* if (d.name == "DataKey") {
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

                         }*/
                    })
                    .attr("d", arc)
                    .style("fill", function(d) {
                        return color(d.name);
                    })
                    //.on("click", click)
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

			/** Function to show contain error */
            $scope.containErrors = function(localData) {

                var dqtGraph = common.getDQTFlag();
                if (dqtGraph) {
                    //localData = JSON.parse(localData);
                    isJson(localData);
                }
                var errorMessageToDisplay;
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
                            errorMessageToDisplay = key + ".";
                        }
                    }
                    $scope.displayErrorMessage(errorMessageToDisplay);
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
            /** It display error messages */
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

            /** Used for apply function */
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

            /** Redirect to home page */
            $scope.clickedHomeIcon = function() {
                $location.path("/");
            }

            /** for digest cycle of apply function */
            $scope.applyFun = function() {
                if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
                    $scope.$apply();
                }
            }

            /** To show table on click on legends */
            $scope.tableComp = function(legendData) {
                $scope.tableCompHideFlag = true;
                $scope.violationJSONTable.label = legendData;
                $scope.applyFun()

            }

            var cellToolTipTemplate = '<div class="ui-grid-cell-contents" title="{{COL_FIELD}}">Custom:{{ COL_FIELD }}</div>'

            /** Function to get data for grid */
            $scope.getDataCompGrid = function(fileName) {
                $scope.showFileName = fileName;
                var transformationNumber = $scope.selectedViolation;
                var page = 1;
                var rows = 1000;
                getDataComparisonTableFactory.getTableDataComp({
                        "fileName": fileName,
                        "jobName": jobName,
                        "transformationNumber": transformationNumber,
                        "page": page,
                        "rows": rows
                    }, {},
                    function(data) {
                        $scope.finalTableData = data;
                        $scope.gridOptionsDataComp.data = data.rows;
                        $scope.DataFlag = true;


                    },
                    function(e) {
                        console.log(e)
                    });

            }

            /** It shows file violations in grid */
            $scope.showDataCompFileViolaions = function(data) {
                $scope.gridOptionsDataComp = {
                    paginationPageSizes: [10, 50, 100],
                    paginationPageSize: 10,
                    enableSorting: true,
                    columnDefs: [

                        { field: 'primaryKey', index: 'primaryKey', displayName: 'PrimaryKey', width: "25%", align: "center", sorttype: "integer" },
                        { field: 'transformationMethod', index: 'transformationMethod', displayName: 'TransformationMethod', width: "25%", align: "center" },
                        { field: 'expected', index: 'expected', displayName: 'ExpectedValue', width: "25%", align: "center" },
                       { field: 'actual', index: 'actual', displayName: 'ActualValue', width: "25%", align: "center" }
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
