/* Dashboard controller */
//'use strict';
angular.module('analyzeJob.ctrl', ["ui.grid", 'ui.bootstrap'])
    .controller('AnalyzeJob', ['$scope', '$http', 'uiGridConstants', 'common', '$location', '$compile', function($scope, $http, uiGridConstants, common, $location, $compile) {


        $scope.createTreeTable = function(itemObj, insTotalUnmatchedKeys, insTotalUnmatchedValues) {
            var taskCounterData = new Array();
            $scope.hideLoader = true;
            if (mapCounterData[itemObj]) {
                $.each(mapCounterData[itemObj], function(counterKey, counterVal) {
                    var totalFilteredIn = counterVal["totalFilteredIn"];
                    var totalContextWrites = counterVal["totalContextWrites"];
                    var totalUnmatchedKeys = counterVal["totalUnmatchedKeys"];
                    if (totalUnmatchedKeys == '-1') {
                        totalUnmatchedKeys = '-';
                        if (insTotalUnmatchedKeys != '-1') {
                            totalUnmatchedKeys = '0';
                        }
                    }
                    var totalUnmatchedValues = counterVal["totalUnmatchedValues"];
                    if (totalUnmatchedValues == '-1') {
                        totalUnmatchedValues = '-';
                        if (insTotalUnmatchedValues != '-1') {
                            totalUnmatchedValues = '0';
                        }
                    }
                    var totalFilteredOut = counterVal["totalFilteredOut"];
                    var counterDetails = counterVal["counterDetails"];

                    //if(counterDetails != "method") {
                    var counterJsonObj = { "counterName": counterKey, "totalFilteredIn": totalFilteredIn, "totalContextWrites": totalContextWrites, "totalUnmatchedKeys": totalUnmatchedKeys, "totalUnmatchedValues": totalUnmatchedValues, "totalFilteredOut": totalFilteredOut };
                    taskCounterData.push(counterJsonObj);
                    //}
                });
            } else {
                taskCounterData = [];
            }

            var taskCounters = { "response": taskCounterData };
            if (jQuery.isEmptyObject(taskCounters["response"]) == true) {
                $("#taskCountersTableWrap").html('<div class="status info"><span>Information: </span>No data available.</div>');
                $("#taskCountersTableWrap .status").delay(300).fadeTo(150, 0.5).fadeTo(150, 1).fadeTo(150, 0.5).fadeTo(150, 1);
            } else {
                $("#taskCountersTableWrap").html('<table id="taskCountersTable"></table>');

                if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
                    $scope.$apply();
                }
            }

            $("#taskCountersTable").jqGrid({
                datastr: taskCounters,
                datatype: "jsonstring",
                height: "auto",
                hidegrid: false,
                colNames: ['Counter Name', 'Filtered In', 'Output Records', 'Unmatched Keys', 'Unmatched Values', 'Filtered From Inside'],
                colModel: [
                    { name: 'counterName', index: 'counterName', width: 70 },
                    { name: 'totalFilteredIn', index: 'totalFilteredIn', width: 70, align: 'center' },
                    { name: 'totalContextWrites', index: 'totalContextWrites', width: 70, align: 'center' },
                    { name: 'totalUnmatchedKeys', index: 'totalUnmatchedKeys', width: 70, align: 'center' },
                    { name: 'totalUnmatchedValues', index: 'totalUnmatchedValues', width: 70, align: 'center' },
                    { name: 'totalFilteredOut', index: 'totalFilteredOut', width: 70, align: 'center' }
                ],
                rowNum: 1000,
                jsonReader: {
                    repeatitems: false,
                    root: "response"
                }
            });


        }

        $scope.clickedHomeIcon = function() {
            $location.path("/")
        }

        $scope.init = function() {
            $scope.jobData = {};
            $scope.hideLoader = false;
            $scope.webSocketErrorFlag = false;
            $scope.showErrorMessage = false;
            $scope.licenseExpireTrue = false;
            $scope.licenseExpireDays = false;



            //start hardcode data
            //var jsonData = {"debugAnalysis":{"logMap":{"job_1444196705135_0080":{"jobMap":{"com.impetus.portout.mappers.oldapi.PortoutReducer":{"mapReduceMap":{"172.26.49.61":{"nodeMap":{"attempt_1444196705135_0080_r_000000_0":{"numOfSamples":0,"time":0,"instanceMap":{"reduce":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock#1":{"totalFilteredIn":9370,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"39","counterMap":{"If#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"40","counterMap":{"com.impetus.portout.mappers.oldapi.PortoutReducer.addHeader":{"totalFilteredIn":1,"totalFilteredOut":1,"totalExitKeys":0,"counterDetails":"method","counterMap":{"Switch#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"60","counterMap":{"Case#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"63","totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1},"Loop#1":{"totalFilteredIn":9370,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"46","totalInputKeys":0,"totalContextWrites":9370,"totalUnmatchedKeys":9370,"totalUnmatchedValues":-1}},"totalInputKeys":9370,"totalContextWrites":9371,"totalUnmatchedKeys":9371,"totalUnmatchedValues":-1}},"totalInputKeys":9370,"totalContextWrites":9371,"totalUnmatchedKeys":9371,"totalUnmatchedValues":-1},"attempt_1444196705135_0080_r_000001_0":{"numOfSamples":0,"time":0,"instanceMap":{"reduce":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock#1":{"totalFilteredIn":9232,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"39","counterMap":{"If#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"40","counterMap":{"com.impetus.portout.mappers.oldapi.PortoutReducer.addHeader":{"totalFilteredIn":1,"totalFilteredOut":1,"totalExitKeys":0,"counterDetails":"method","counterMap":{"Switch#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"60","counterMap":{"Case#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"63","totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1},"Loop#1":{"totalFilteredIn":9232,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"46","totalInputKeys":0,"totalContextWrites":9232,"totalUnmatchedKeys":9232,"totalUnmatchedValues":-1}},"totalInputKeys":9232,"totalContextWrites":9233,"totalUnmatchedKeys":9233,"totalUnmatchedValues":-1}},"totalInputKeys":9232,"totalContextWrites":9233,"totalUnmatchedKeys":9233,"totalUnmatchedValues":-1}},"totalInputKeys":18602,"totalContextWrites":18604,"totalUnmatchedKeys":18604,"totalUnmatchedValues":-1}},"totalInputKeys":18602,"totalContextWrites":18604,"totalUnmatchedKeys":18604,"totalUnmatchedValues":-1},"com.impetus.portout.mappers.oldapi.ServiceProviderMapper":{"mapReduceMap":{"172.26.49.61":{"nodeMap":{"attempt_1444196705135_0080_m_000001_0":{"numOfSamples":0,"time":0,"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock#1":{"totalFilteredIn":14123,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"27","counterMap":{"If#1":{"totalFilteredIn":9260,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"28","totalInputKeys":0,"totalContextWrites":9260,"totalUnmatchedKeys":1881,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":9260,"totalUnmatchedKeys":1881,"totalUnmatchedValues":-1}},"totalInputKeys":14123,"totalContextWrites":9260,"totalUnmatchedKeys":1881,"totalUnmatchedValues":-1}},"totalInputKeys":14123,"totalContextWrites":9260,"totalUnmatchedKeys":1881,"totalUnmatchedValues":-1},"attempt_1444196705135_0080_m_000000_0":{"numOfSamples":0,"time":0,"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock#1":{"totalFilteredIn":14020,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"27","counterMap":{"If#1":{"totalFilteredIn":9342,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"28","totalInputKeys":0,"totalContextWrites":9342,"totalUnmatchedKeys":1845,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":9342,"totalUnmatchedKeys":1845,"totalUnmatchedValues":-1}},"totalInputKeys":14020,"totalContextWrites":9342,"totalUnmatchedKeys":1845,"totalUnmatchedValues":-1}},"totalInputKeys":14020,"totalContextWrites":9342,"totalUnmatchedKeys":1845,"totalUnmatchedValues":-1}},"totalInputKeys":28143,"totalContextWrites":18602,"totalUnmatchedKeys":3726,"totalUnmatchedValues":-1}},"totalInputKeys":28143,"totalContextWrites":18602,"totalUnmatchedKeys":3726,"totalUnmatchedValues":-1}},"totalInputKeys":50100,"totalContextWrites":18604,"totalUnmatchedKeys":22330,"totalUnmatchedValues":-1}},"mrChain":{"job_1444196705135_0080":{"mapChainList":[{"name":"com.impetus.portout.mappers.oldapi.TupleValidateMapper","inputKeys":0,"contextWrites":0},{"name":"com.impetus.portout.mappers.oldapi.USRegionMapper","inputKeys":0,"contextWrites":0},{"name":"com.impetus.portout.mappers.oldapi.ServiceProviderMapper","inputKeys":28143,"contextWrites":18602}],"reduceChainList":[{"name":"com.impetus.portout.mappers.oldapi.PortoutReducer","inputKeys":18602,"contextWrites":18604},{"name":"com.impetus.portout.mappers.oldapi.PortoutRegionMapper","inputKeys":0,"contextWrites":0}]}},"partitionerMap":{}},"debuggerSummary":{"mapperReducerNames":[{"jobMapReduceName":"job_1444196705135_0080","totalUnmatchedKeys":22330,"totalUnmatchedValues":-1},{"jobMapReduceName":"com.impetus.portout.mappers.oldapi.PortoutReducer","totalUnmatchedKeys":18604,"totalUnmatchedValues":-1},{"jobMapReduceName":"com.impetus.portout.mappers.oldapi.ServiceProviderMapper","totalUnmatchedKeys":3726,"totalUnmatchedValues":-1}],"reducerInfo":[]}};
            //var localData = JSON.stringify(jsonData);

            //debugAnalysisCall(localData);
            //.debugAnalysis.logMap

            //return false;
            //end hardcode data


            var host = window.location.hostname;
            var port = window.location.port;
            var jobObj = common.getJobDetails();
            var jobName = jobObj.jobName;
            $scope.jumbuneJobname = jobName;
            $scope.finalJobName = jobName;
            $('[data-toggle="tooltip"]').tooltip();
            licenseExpireMessage();
            if ($scope.finalJobName == null || $scope.finalJobName == undefined || $scope.finalJobName == "") {
                $location.path('/');
            } 
            else {
                var url = "ws://" + host + ":" + port + "/results/jobanalysis?jobName=" + $scope.finalJobName;
                //var url = "ws://" + host + ":" + port + "/results/jobanalysis?jobName="+jobName;  
                var webSocket = new WebSocket(url);

                webSocket.onopen = function(event) {
                    console.log("socket open sucessfully")
                };

                webSocket.onmessage = function(event) {
                    $('.ring-loader').remove();
                    $scope.webSocketErrorFlag = true;
                    $('.loaderMessage').remove();
                    $scope.hideLoader = true;
                    //var mainData = JSON.parse(serverData.DEBUG_ANALYZER);

                    var serverData = angular.copy(event.data);
                    var localData = JSON.parse(serverData);
                    var doErrorsExist = $scope.containErrors(localData.DEBUG_ANALYZER);
                    if (doErrorsExist) {
                        return;
                    }
                    debugAnalysisCall(localData.DEBUG_ANALYZER);
                };
                webSocket.onerror = function(error) {
                    $scope.displayErrorMessage("Connection lost to server");
                };
                webSocket.onclose = function(event) {
                    //updateOutput("Connection Closed");
                    //connectBtn.disabled = false;
                    if ($scope.webSocketErrorFlag == false) {
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
        $scope.containErrors = function(localData) {
            localData = JSON.parse(localData);
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
            /*var errorDiv = document.getElementById('errorMessage');
                                errorDiv.style.display = '';
                                errorDiv.innerHTML = '<strong>Error! </strong>' + errorMessageToDisplay;
                                document.getElementById('resultWidgetContainer').style.display = 'none';
                                document.querySelector('.widgetWrapper').style.marginBottom = errorDiv.style.marginTop;*/
            $scope.showErrorMessage = true;
            var errorDiv = document.getElementById('errorMessage');
            errorDiv.style.display = '';
            errorDiv.innerHTML = '<i class="fa fa-exclamation-triangle" style="font-size: 22px; margin:10px;"></i>' + errorMessageToDisplay;
             $('.widgetWrapper').css({'height': 600,'background-color': 'white'});
        }

        /*      $scope.displayErrorMessage = function(errorMessageToDisplay) {

                  var dataValGraph = common.getDataValFlag();
                  if (dataValGraph) {
                      $("#resultWidgetContainer").remove();
                  }
                  var errorDiv = document.getElementById('errorMessage');
                  errorDiv.style.display = '';
                  errorDiv.innerHTML = '<i class="fa fa-exclamation-triangle" style="font-size: 22px; margin:10px;"></i>' + errorMessageToDisplay;
                  $('body').css('background-color', "white");
              }*/



        var mapCounterData = new Array();



        function debugAnalysisCall(jsonObj) {
            var jsonData = JSON.parse(jsonObj);
            $scope.hideLoader = true;

            $scope.jobData.jobName = Object.keys(jsonData.debugAnalysis.logMap)[0];



            // return false;
            //var jsonData = {"debugAnalysis":{"logMap":{"job_1444196705135_0080":{"jobMap":{"com.impetus.portout.mappers.oldapi.PortoutReducer":{"mapReduceMap":{"172.26.49.61":{"nodeMap":{"attempt_1444196705135_0080_r_000000_0":{"numOfSamples":0,"time":0,"instanceMap":{"reduce":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock#1":{"totalFilteredIn":9370,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"39","counterMap":{"If#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"40","counterMap":{"com.impetus.portout.mappers.oldapi.PortoutReducer.addHeader":{"totalFilteredIn":1,"totalFilteredOut":1,"totalExitKeys":0,"counterDetails":"method","counterMap":{"Switch#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"60","counterMap":{"Case#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"63","totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1},"Loop#1":{"totalFilteredIn":9370,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"46","totalInputKeys":0,"totalContextWrites":9370,"totalUnmatchedKeys":9370,"totalUnmatchedValues":-1}},"totalInputKeys":9370,"totalContextWrites":9371,"totalUnmatchedKeys":9371,"totalUnmatchedValues":-1}},"totalInputKeys":9370,"totalContextWrites":9371,"totalUnmatchedKeys":9371,"totalUnmatchedValues":-1},"attempt_1444196705135_0080_r_000001_0":{"numOfSamples":0,"time":0,"instanceMap":{"reduce":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock#1":{"totalFilteredIn":9232,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"39","counterMap":{"If#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"40","counterMap":{"com.impetus.portout.mappers.oldapi.PortoutReducer.addHeader":{"totalFilteredIn":1,"totalFilteredOut":1,"totalExitKeys":0,"counterDetails":"method","counterMap":{"Switch#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"60","counterMap":{"Case#1":{"totalFilteredIn":1,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"63","totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":1,"totalUnmatchedKeys":1,"totalUnmatchedValues":-1},"Loop#1":{"totalFilteredIn":9232,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"46","totalInputKeys":0,"totalContextWrites":9232,"totalUnmatchedKeys":9232,"totalUnmatchedValues":-1}},"totalInputKeys":9232,"totalContextWrites":9233,"totalUnmatchedKeys":9233,"totalUnmatchedValues":-1}},"totalInputKeys":9232,"totalContextWrites":9233,"totalUnmatchedKeys":9233,"totalUnmatchedValues":-1}},"totalInputKeys":18602,"totalContextWrites":18604,"totalUnmatchedKeys":18604,"totalUnmatchedValues":-1}},"totalInputKeys":18602,"totalContextWrites":18604,"totalUnmatchedKeys":18604,"totalUnmatchedValues":-1},"com.impetus.portout.mappers.oldapi.ServiceProviderMapper":{"mapReduceMap":{"172.26.49.61":{"nodeMap":{"attempt_1444196705135_0080_m_000001_0":{"numOfSamples":0,"time":0,"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock#1":{"totalFilteredIn":14123,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"27","counterMap":{"If#1":{"totalFilteredIn":9260,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"28","totalInputKeys":0,"totalContextWrites":9260,"totalUnmatchedKeys":1881,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":9260,"totalUnmatchedKeys":1881,"totalUnmatchedValues":-1}},"totalInputKeys":14123,"totalContextWrites":9260,"totalUnmatchedKeys":1881,"totalUnmatchedValues":-1}},"totalInputKeys":14123,"totalContextWrites":9260,"totalUnmatchedKeys":1881,"totalUnmatchedValues":-1},"attempt_1444196705135_0080_m_000000_0":{"numOfSamples":0,"time":0,"instanceMap":{"map":{"totalFilteredIn":0,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"method","counterMap":{"IfBlock#1":{"totalFilteredIn":14020,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"27","counterMap":{"If#1":{"totalFilteredIn":9342,"totalFilteredOut":0,"totalExitKeys":0,"counterDetails":"28","totalInputKeys":0,"totalContextWrites":9342,"totalUnmatchedKeys":1845,"totalUnmatchedValues":-1}},"totalInputKeys":0,"totalContextWrites":9342,"totalUnmatchedKeys":1845,"totalUnmatchedValues":-1}},"totalInputKeys":14020,"totalContextWrites":9342,"totalUnmatchedKeys":1845,"totalUnmatchedValues":-1}},"totalInputKeys":14020,"totalContextWrites":9342,"totalUnmatchedKeys":1845,"totalUnmatchedValues":-1}},"totalInputKeys":28143,"totalContextWrites":18602,"totalUnmatchedKeys":3726,"totalUnmatchedValues":-1}},"totalInputKeys":28143,"totalContextWrites":18602,"totalUnmatchedKeys":3726,"totalUnmatchedValues":-1}},"totalInputKeys":50100,"totalContextWrites":18604,"totalUnmatchedKeys":22330,"totalUnmatchedValues":-1}},"mrChain":{"job_1444196705135_0080":{"mapChainList":[{"name":"com.impetus.portout.mappers.oldapi.TupleValidateMapper","inputKeys":0,"contextWrites":0},{"name":"com.impetus.portout.mappers.oldapi.USRegionMapper","inputKeys":0,"contextWrites":0},{"name":"com.impetus.portout.mappers.oldapi.ServiceProviderMapper","inputKeys":28143,"contextWrites":18602}],"reduceChainList":[{"name":"com.impetus.portout.mappers.oldapi.PortoutReducer","inputKeys":18602,"contextWrites":18604},{"name":"com.impetus.portout.mappers.oldapi.PortoutRegionMapper","inputKeys":0,"contextWrites":0}]}},"partitionerMap":{}},"debuggerSummary":{"mapperReducerNames":[{"jobMapReduceName":"job_1444196705135_0080","totalUnmatchedKeys":22330,"totalUnmatchedValues":-1},{"jobMapReduceName":"com.impetus.portout.mappers.oldapi.PortoutReducer","totalUnmatchedKeys":18604,"totalUnmatchedValues":-1},{"jobMapReduceName":"com.impetus.portout.mappers.oldapi.ServiceProviderMapper","totalUnmatchedKeys":3726,"totalUnmatchedValues":-1}],"reducerInfo":[]}}

            var colorCodes = ["#0000ff", "#8a2be2", "#a52a2a", "#5f9ea0", "#7fff00", "#d2691e", "#ff7f50", "#6495ed", "#00ffff", "#00008b", "#008b8b", "#b8860b", "#006400", "#8b008b", "#556b2f", "#ff8c00", "#9932cc", "#8b0000", "#e9967a", "#8fbc8f", "#483d8b", "#2f4f4f", "#00ced1", "#9400d3", "#00bfff", "#1e90ff", "#228b22", "#ff00ff", "#ffd700", "#daa520", "#008000", "#adff2f", "#ff69b4", "#cd5c5c", "#4b0082", "#7cfc00", "#f08080", "#90ee90", "#ffb6c1", "#ffa07a", "#20b2aa", "#87cefa", "#778899", "#b0c4de", "#00ff00", "#32cd32", "#800000", "#66cdaa", "#0000cd", "#ba55d3", "#9370d8", "#3cb371", "#7b68ee", "#00fa9a", "#48d1cc", "#191970", "#808000", "#6b8e23", "#ffa500", "#da70d6", "#98fb98", "#afeeee", "#d87093", "#cd853f", "#ffc0cb", "#dda0dd", "#b0e0e6", "#800080", "#bc8f8f", "#4169e1", "#8b4513", "#fa8072", "#f4a460", "#2e8b57", "#a0522d", "#87ceeb", "#6a5acd", "#708090", "#00ff7f", "#4682b4", "#d2b48c", "#008080", "#d8bfd8", "#ff6347", "#40e0d0", "#ee82ee", "#ffff00", "#9acd32"];


            var logAnalysisJSONStringObj; //SHYAM: This variable is moved on Top
            enableDebugAnalysis(jsonData)



            var numOfJobs;
            var numOfMapReduce;
            var numOfNodes;
            var numOfInstance;
            var iPlot = [];
            var mrPlot = [];
            var instMapColorCode = new Array();
            var mrColorCode = new Array();



            function enableDebugAnalysis(a) //Shyam: argument name changed
            {
                // Create tree table
                $scope.hideLoader = true;
                $('<table id="jobtreegrid"></table>').appendTo('#jobptreegrid');
                $('<table id="maptreegrid"></table>').appendTo('#mapptreegrid');
                $('<table id="instreegrid"></table>').appendTo('#insptreegrid');
                var logJobChurningData = [];
                var logMapChurningData = [];
                var logInsChurningData = [];
                var debugAnalyzerErrorGridData = [];
                var allTaskCounterData = [];
                var errorCount = 0;

                logAnalysisJSONStringObj = a;
                numOfJobs = 0;

                $.each(logAnalysisJSONStringObj, function(logName, logBean) {

                    var logMap = logBean["logMap"];
                    //var jobChain=logBean["jobChain"];
                    var mrChain = logBean["mrChain"];
                    var partitionerMap = logBean["partitionerMap"];

                    if (typeof logBean["sampledHDFSPath"] != 'undefined') {
                        $('#sampledHDFSPathDataBox').html(logBean["sampledHDFSPath"]);
                        $('#sampledHDFSPathBox').show();
                    }
                    if (logName == 'ErrorAndException') {
                        $.each(logBean, function(key, val) {
                            errorCount++;
                            var eachDebugAnalyzerErrorJobsJsonObj = { "id": errorCount, "jobName": key, "errorMsg": val };
                            debugAnalyzerErrorGridData.push(eachDebugAnalyzerErrorJobsJsonObj);

                        });
                        $('#debugAnalyzerErrorsBox').show();
                        $('#debugErrorLinks').html('<button id="errorBtn"><span>Failed Jobs</span></button>');
                        $('#summary-debugger').find('.loaderMainBox').html('<div class="status error"><span>Information: </span>An error has been occurred while processing module, Please refer same module for more details.</div>').removeClass('loaderMainBox');
                        return;
                    } else if (logName == 'debuggerSummary') {
                        $.each(logBean, function(summaryMapperKey, summaryMapperVal) {

                            if (summaryMapperKey == 'mapperReducerNames') {
                                summaryJobHTml = '<div class="summary-debug-half-box"><table class="summary-debugger-table" width="100%" cellspacing="1" cellpadding="1"><tr class="summary-debug-heading"><td>Jobs/Mapper/Reducer</td><td>Unmatched Keys</td><td>Unmatched Values</td></tr>';
                                $.each(summaryMapperVal, function(summaryJobMapKey, summaryJobMapVal) {
                                    if (summaryJobMapVal['totalUnmatchedKeys'] == -1) {
                                        summaryJobMapVal['totalUnmatchedKeys'] = '-';
                                    }
                                    if (summaryJobMapVal['totalUnmatchedValues'] == -1) {
                                        summaryJobMapVal['totalUnmatchedValues'] = '-';
                                    }

                                    if (summaryJobMapVal['jobMapReduceName'].indexOf('.') > 0) {
                                        var summaryJobMapValArr = summaryJobMapVal['jobMapReduceName'].split('.');

                                        summaryJobMapSplitVal = summaryJobMapValArr[summaryJobMapValArr.length - 1];
                                        summaryJobHTml += "<tr><td title='" + summaryJobMapVal['jobMapReduceName'] + "'>" + summaryJobMapSplitVal + "</td><td>" + summaryJobMapVal['totalUnmatchedKeys'] + "</td><td>" + summaryJobMapVal['totalUnmatchedValues'] + "</td></tr>";
                                    } else {
                                        summaryJobHTml += "<tr><td title='" + summaryJobMapVal['jobMapReduceName'] + "'>" + summaryJobMapVal['jobMapReduceName'] + "</td><td>" + summaryJobMapVal['totalUnmatchedKeys'] + "</td><td>" + summaryJobMapVal['totalUnmatchedValues'] + "</td></tr>";
                                    }
                                });
                                summaryJobHTml += '</table></div>';
                                $('#summary-debugger').find('.summary-debug-main').html(summaryJobHTml);

                            }

                        });
                        $('#summary-debugger').find('.loaderMainBox').removeClass('loaderMainBox');
                        return;
                    }

                    $.each(logMap, function(jobId, jobBean) {
                        numOfJobs++;
                        var totalInputKeys = jobBean["totalInputKeys"];
                        var totalContextWrites = jobBean["totalContextWrites"];
                        var totalUnmatchedKeys = jobBean["totalUnmatchedKeys"];
                        var totalUnmatchedValues = jobBean["totalUnmatchedValues"];
                        var jobMap = jobBean["jobMap"];
                        var jobChainName = "";
                        var jobChainCounter = numOfJobs;
                        var jobChainCount = 10;

                        /*if(jobChain)
                        {
                        $.each(jobChain, function(jobChainId, jobChainBean){    
                            if(jobId == jobChainBean["name"])
                            {
                                jobChainName='<div class="jobChainIco"></div>';
                                jobChainCounter = jobChainCount;
                                //var jobChainInputKeys=jobChainBean["inputKeys"];
                                //var jobChainContextWrites=jobChainBean["contextWrites"];
                            }       
                            jobChainCount++;

                        });
                        if(jobChainCounter != 1)
                        {
                            jobChainCounter = 9999;
                            jobChainName = '<div class="jobChainFkIco"></div>';
                        }
                        }*/

                        // partitionerMap
                        if (partitionerMap) {
                            $.each(partitionerMap, function(partitionerMapId, partitionerMapBean) {
                                if (jobId == partitionerMapId) {
                                    partitionerMapName = '<span class=""><span class="partitionerMapName" rel="' + partitionerMapId + '" href="javascript:void(0);">P</span>&nbsp;</span>';
                                    jobChainName += partitionerMapName;

                                    //var partitionerMapInputKeys=jobChainBean["inputKeys"];
                                    //var partitionerMapContextWrites=jobChainBean["contextWrites"];
                                }
                            });
                        }
                        if (totalUnmatchedKeys == -1)
                            totalUnmatchedKeys = "-";
                        if (totalUnmatchedValues == -1)
                            totalUnmatchedValues = "-";

                        var logChurningJsonObj = { "chain": "-", "id": jobChainCounter, "elementName": jobChainName, "totalInputKeys": totalInputKeys, "totalContextWrites": totalContextWrites, "totalUnmatchedKeys": totalUnmatchedKeys, "totalUnmatchedValues": totalUnmatchedValues, level: "0", parent: "", isLeaf: false, expanded: false, loaded: true };
                        logJobChurningData.push(logChurningJsonObj);


                        numOfMapReduce = 0;
                        $.each(jobMap, function(mapReduceName, mapReduceBean) {
                            numOfMapReduce++;
                            var totalMapReduceInputKeys = mapReduceBean["totalInputKeys"];
                            var totalMapReduceContextWrites = mapReduceBean["totalContextWrites"];
                            var totalMapReduceUnmatchedKeys = mapReduceBean["totalUnmatchedKeys"];
                            var totalMapReduceUnmatchedValues = mapReduceBean["totalUnmatchedValues"];
                            var mapReduceMap = mapReduceBean["mapReduceMap"];
                            var mapChainName = "";
                            var mapChainCounter = "";
                            var mapChainCount = 1;

                            if (mrChain) {
                                $.each(mrChain, function(mrJobChainId, mrJobChainBean) {

                                    if (jobId == mrJobChainId) {
                                        //var marJobChain=mrJobChainBean[jobId];

                                        $.each(mrJobChainBean, function(mrMapChainId, mrMapChainBean) {

                                            if (mapReduceName == mrMapChainBean["name"]) {
                                                mapChainName = '<div class="mapChainIco"></div>';
                                                mapChainCounter = mapChainCount;
                                                //var mapChainInputKeys=mrMapChainBean["inputKeys"];
                                                //var mapChainContextWrites=mrMapChainBean["contextWrites"];
                                            }
                                            mapChainCount++;

                                        });
                                    }

                                });
                                if (!mapChainCounter) {
                                    mapChainCounter = 9999999;
                                    mapChainName = '<div class="mapChainFkIco"></div>';
                                }
                            }

                            var mapReduceJsonObj = { "chain": "-", "id": mapChainCounter, "elementName": mapChainName + "xzxx<div class='classDetails'>" + mapReduceName + "<form></form></div>", "totalInputKeys": totalMapReduceInputKeys, "totalContextWrites": totalMapReduceContextWrites, "totalUnmatchedKeys": totalMapReduceUnmatchedKeys, "totalUnmatchedValues": totalMapReduceUnmatchedValues, level: "1", parent: numOfJobs, isLeaf: false, expanded: false, loaded: true };

                            logMapChurningData.push(mapReduceJsonObj);


                            numOfNodes = 0;
                            $.each(mapReduceMap, function(nodeName, nodeBean) {
                                numOfNodes++;
                                var totalNodeInputKeys = nodeBean["totalInputKeys"];
                                var totalNodeContextWrites = nodeBean["totalContextWrites"];
                                var totalNodeUnmatchedKeys = nodeBean["totalUnmatchedKeys"];
                                var totalNodeUnmatchedValues = nodeBean["totalUnmatchedValues"];
                                var nodeMap = nodeBean["nodeMap"];


                                numOfInstance = 0;
                                var nodeJsonObj = { "id": numOfJobs + '_' + numOfMapReduce + '_' + numOfNodes, "elementName": "<div class='nodeDetails'>" + nodeName + "<form></form></div>", "totalInputKeys": totalNodeInputKeys, "totalContextWrites": totalNodeContextWrites, "totalUnmatchedKeys": totalNodeUnmatchedKeys, "totalUnmatchedValues": totalNodeUnmatchedValues, level: "2", parent: numOfJobs + '_' + numOfMapReduce, isLeaf: false, expanded: false, loaded: true };


                                var nodeColor = colorCodes[numOfNodes];

                                $.each(nodeMap, function(instanceName, instanceBean) {


                                    numOfInstance++;
                                    var totalInstanceInputKeys = instanceBean["totalInputKeys"];
                                    var totalInstanceContextWrites = instanceBean["totalContextWrites"];
                                    var totalInstanceUnmatchedKeys = instanceBean["totalUnmatchedKeys"];
                                    var totalInstanceUnmatchedValues = instanceBean["totalUnmatchedValues"];
                                    var taskName = instanceName;


                                    var instanceMap = instanceBean["instanceMap"];
                                    var instanceJsonObj = { "id": numOfJobs + '_' + numOfMapReduce + '_' + numOfNodes + '_' + numOfInstance, "elementName": "<span class='legendBullets' style='background-color:" + nodeColor + "'></span><div class='instancemodal' style='cursor:pointer'><i class='fa fa-caret-right'></i>" + instanceName + " <form><input type='hidden' name='elementName' value=" + taskName + " id='elementName' /></form></div>", "totalInputKeys": totalInstanceInputKeys, "totalContextWrites": totalInstanceContextWrites, "totalUnmatchedKeys": totalInstanceUnmatchedKeys, "totalUnmatchedValues": totalInstanceUnmatchedValues, level: "3", parent: numOfJobs + '_' + numOfMapReduce + '_' + numOfNodes, isLeaf: true, expanded: false, loaded: true };
                                    logInsChurningData.push(instanceJsonObj);
                                    if (typeof instanceMap == 'undefined') {
                                        return; }
                                    var taskCounterData = [];
                                    $.each(instanceMap, function(counterName, counterBean) {
                                        if (counterName.indexOf("contextWrite") != -1) {
                                            var totalFilteredIn = counterBean["totalFilteredIn"];
                                            var totalCounterContextWrites = counterBean["totalContextWrites"];
                                            var totalCounterUnmatchedKeys = counterBean["totalUnmatchedKeys"];
                                            var totalCounterUnmatchedValues = counterBean["totalUnmatchedValues"];
                                            var totalFilteredOut = counterBean["totalFilteredOut"];
                                            var counterMap2 = counterBean["counterMap"];
                                            var counterJsonObj = { "counterName": counterName, "filteredIn": '-', "contextWrites": totalCounterContextWrites, "unmatchedKeys": totalCounterUnmatchedKeys, "unmatchedValues": totalCounterUnmatchedValues, "filteredOut": '-', level: "0", parent: counterName, isLeaf: true, expanded: false, loaded: true };
                                            taskCounterData.push(counterJsonObj);
                                        } else {
                                            var totalFilteredIn = counterBean["totalFilteredIn"];
                                            var totalCounterContextWrites = counterBean["totalContextWrites"];
                                            var totalCounterUnmatchedKeys = counterBean["totalUnmatchedKeys"];
                                            var totalCounterUnmatchedValues = counterBean["totalUnmatchedValues"];
                                            var totalFilteredOut = counterBean["totalFilteredOut"];
                                            var counterMap2 = counterBean["counterMap"];
                                            var counterJsonObj = { "counterName": counterName, "filteredIn": totalFilteredIn, "contextWrites": totalCounterContextWrites, "unmatchedKeys": totalCounterUnmatchedKeys, "unmatchedValues": totalCounterUnmatchedValues, "filteredOut": totalFilteredOut, level: "0", parent: counterName, isLeaf: true, expanded: false, loaded: true };
                                            taskCounterData.push(counterJsonObj);
                                        }

                                        if (typeof counterMap2 != 'undefined') {
                                            $.each(counterMap2, function(counterName2, counterBean2) {

                                                var totalFilteredIn = counterBean2["totalFilteredIn"];
                                                var totalCounterContextWrites = counterBean2["totalContextWrites"];
                                                var totalCounterUnmatchedKeys = counterBean2["totalUnmatchedKeys"];
                                                var totalCounterUnmatchedValues = counterBean2["totalUnmatchedValues"];
                                                var totalFilteredOut = counterBean2["totalFilteredOut"];
                                                var counterMap3 = counterBean2["counterMap"];

                                                var counterJsonObj = { "counterName": counterName2, "filteredIn": '-', "contextWrites": totalCounterContextWrites, "unmatchedKeys": totalCounterUnmatchedKeys, "unmatchedValues": totalCounterUnmatchedValues, "filteredOut": '-', level: "1", parent: counterName, isLeaf: true, expanded: false, loaded: true };
                                                taskCounterData.push(counterJsonObj);

                                                if (typeof counterMap3 != 'undefined') {
                                                    $.each(counterMap3, function(counterName3, counterBean3) {

                                                        var totalFilteredIn = counterBean3["totalFilteredIn"];
                                                        var totalCounterContextWrites = counterBean3["totalContextWrites"];
                                                        var totalCounterUnmatchedKeys = counterBean3["totalUnmatchedKeys"];
                                                        var totalCounterUnmatchedValues = counterBean3["totalUnmatchedValues"];
                                                        var totalFilteredOut = counterBean3["totalFilteredOut"];
                                                        var counterMap4 = counterBean3["counterMap"];
                                                        var counterJsonObj = { "counterName": counterName3, "filteredIn": '-', "contextWrites": totalCounterContextWrites, "unmatchedKeys": totalCounterUnmatchedKeys, "unmatchedValues": totalCounterUnmatchedValues, "filteredOut": '-', level: "2", parent: counterName, isLeaf: true, expanded: false, loaded: true };
                                                        taskCounterData.push(counterJsonObj);

                                                        if (typeof counterMap4 != 'undefined') {
                                                            $.each(counterMap4, function(counterName4, counterBean4) {

                                                                var totalFilteredIn = counterBean4["totalFilteredIn"];
                                                                var totalCounterContextWrites = counterBean4["totalContextWrites"];
                                                                var totalCounterUnmatchedKeys = counterBean4["totalUnmatchedKeys"];
                                                                var totalCounterUnmatchedValues = counterBean4["totalUnmatchedValues"];
                                                                var totalFilteredOut = counterBean4["totalFilteredOut"];
                                                                var counterMap5 = counterBean4["counterMap"];
                                                                var counterJsonObj = { "counterName": counterName4, "filteredIn": '-', "contextWrites": totalCounterContextWrites, "unmatchedKeys": totalCounterUnmatchedKeys, "unmatchedValues": totalCounterUnmatchedValues, "filteredOut": '-', level: "3", parent: counterName, isLeaf: true, expanded: false, loaded: true };
                                                                taskCounterData.push(counterJsonObj);

                                                            });
                                                        }

                                                    });
                                                }

                                            });

                                        }

                                    });

                                    allTaskCounterData[instanceName] = taskCounterData;
                                });
                            });
                        });
                    });
                });


                $scope.jobData.totalInputKeys = logJobChurningData[0].totalInputKeys;
                $scope.jobData.totalContextWrites = logJobChurningData[0].totalContextWrites;
                $scope.jobData.totalUnmatchedKeys = logJobChurningData[0].totalUnmatchedKeys;
                $scope.jobData.totalUnmatchedValues = logJobChurningData[0].totalUnmatchedValues;
                if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
                    $scope.$apply();
                }

                //job Grid
                var logJobAnalysisJson = { "response": logJobChurningData };
                jobGrid = jQuery("#jobtreegrid");
                jobGrid.jqGrid({
                    datastr: logJobAnalysisJson,
                    datatype: "jsonstring",
                    height: 199,
                    sortable: true,
                    sortname: "id",
                    sortorder: "desc",
                    hidegrid: false,
                    loadui: "disable",
                    scrollOffset: 0,
                    colNames: ["Chain", "ID", "Name", "Input Keys", "Output Records", "Unmatched Keys", "Unmatched Values"],
                    colModel: [
                        { name: 'chain', index: 'chain', width: 20, align: "center", hidden: true },
                        { name: 'id', index: 'id', width: 10, align: "center", hidden: true },
                        { name: 'elementName', index: 'elementName', width: 210, align: "left", classes: "jobfirstcol" },
                        { name: 'totalInputKeys', index: 'totalInputKeys', width: 70, align: "center" },
                        { name: 'totalContextWrites', index: 'totalContextWrites', width: 70, align: "center" },
                        { name: 'totalUnmatchedKeys', index: 'totalUnmatchedKeys', width: 70, align: "center" },
                        { name: 'totalUnmatchedValues', index: 'totalUnmatchedValues', width: 70, align: "center" }
                    ],
                    rowNum: 10000,
                    jsonReader: {
                        repeatitems: false,
                        root: "response"
                    }
                });


                //job map/reduce grid
                var logMapAnalysisJson = { "response": logMapChurningData };
                mapGrid = jQuery("#maptreegrid");
                mapGrid.jqGrid({
                    datastr: logMapAnalysisJson,
                    datatype: "jsonstring",
                    height: 178,
                    hidegrid: false,
                    loadui: "disable",
                    scrollOffset: 0,
                    colNames: ["Chain", "ID", "", "Input Keys", "Output Records", "Unmatched Keys", "Unmatched Values"],
                    colModel: [
                        { name: 'chain', index: 'chain', width: 20, align: "center", hidden: true },
                        { name: 'id', index: 'id', width: 10, align: "center", hidden: true },
                        { name: 'elementName', index: 'elementName', width: 200, align: "left", formatter: classTitleShort, classes: "mrfirstcol" },
                        { name: 'totalInputKeys', index: 'totalInputKeys', width: 70, align: "center", hidden: true },
                        { name: 'totalContextWrites', index: 'totalContextWrites', width: 70, align: "center", hidden: true },
                        { name: 'totalUnmatchedKeys', index: 'totalUnmatchedKeys', width: 70, align: "center", hidden: true },
                        { name: 'totalUnmatchedValues', index: 'totalUnmatchedValues', width: 70, align: "center", hidden: true }
                    ],
                    rowNum: 10000,
                    footerrow: true,
                    userDataOnFooter: true,
                    altRows: true,
                    jsonReader: {
                        repeatitems: false,
                        root: "response"
                    }
                });


                //job intance
                var logInsAnalysisJson = { "response": logInsChurningData };
                insGrid = jQuery("#instreegrid");
                insGrid.jqGrid({
                    datastr: logInsAnalysisJson,
                    datatype: "jsonstring",
                    height: 157,
                    hidegrid: false,
                    loadui: "disable",
                    scrollOffset: 0,
                    colNames: ["chain", "ID", "Name", "Input Keys", "Output Records", "Unmatched Keys", "Unmatched Values"],
                    colModel: [
                        { name: 'chain', index: 'chain', width: 20, align: "center", hidden: true },
                        { name: 'id', index: 'id', width: 20, align: "center", hidden: true },
                        { name: 'elementName', index: 'elementName', width: 200, align: "left", formatter: classTitleShortFromInstanceTable, classes: "insfirstcol" },
                        { name: 'totalInputKeys', index: 'totalInputKeys', width: 70, align: "center", hidden: true },
                        { name: 'totalContextWrites', index: 'totalContextWrites', width: 70, align: "center", hidden: true },
                        { name: 'totalUnmatchedKeys', index: 'totalUnmatchedKeys', width: 70, align: "center", hidden: true },
                        { name: 'totalUnmatchedValues', index: 'totalUnmatchedValues', width: 70, align: "center", hidden: true }
                    ],
                    rowNum: 10000,
                    footerrow: true,
                    userDataOnFooter: true,
                    altRows: true,
                    jsonReader: {
                        repeatitems: false,
                        root: "response"
                    }
                });

                // error grid table start
                if (debugAnalyzerErrorGridData) {
                    $('<table id="debugAnalyzerErrortable"></table>').appendTo('#debugAnalyzerErrorDiv');
                    var debugAnalyzerErrorJobsGridDataJson = { "response": debugAnalyzerErrorGridData };
                    debugAnalyzerErrorJobsGrid = jQuery("#debugAnalyzerErrortable");
                    debugAnalyzerErrorJobsGrid.jqGrid({
                        datastr: debugAnalyzerErrorJobsGridDataJson,
                        datatype: "jsonstring",
                        height: 'auto',
                        hidegrid: false,
                        loadui: "disable",
                        colNames: ["ID", "Name", "Error"],
                        colModel: [
                            { name: 'id', index: 'id', width: 30, align: "center" },
                            { name: 'jobName', index: 'jobName', width: 150, align: "center" },
                            { name: 'errorMsg', index: 'errorMsg', width: 620, align: "left", formatter: addPaddInVal }

                        ],
                        rowNum: 10000,
                        jsonReader: {
                            repeatitems: false,
                            root: "response"
                        }
                    });
                }
                // error grid table end

                $('#debugAnalyzerErrortable tr:nth-child(even)').addClass("evenTableRow");


                $("#jobtreegrid tr:eq(1)").trigger('click');
                $('#mapptreegrid').find('div.ui-jqgrid-sdiv').insertBefore($('#mapptreegrid').find('div.ui-jqgrid-bdiv'));
                $('#insptreegrid').find('div.ui-jqgrid-sdiv').insertBefore($('#insptreegrid').find('div.ui-jqgrid-bdiv'));

                //createDebugReportPieCharts();
            }

            function addPaddInVal(cellvalue, options, rowObject) {
                $scope.hideLoader = true;

                var paddVal = '<div class="paddLeftWithTextWrap">' + cellvalue + '</div>';
                return paddVal;
            }

            $('#errorBtn').live('click', function() {
                $scope.hideLoader = true;


                $('#tabs').tabs("option", "disabled", []);

                var tabsLength = $('#tabs').tabs("length");
                $('#tabs').tabs('select', parseInt(tabsLength - 1));
                $("#tabs").tabs("refresh");


            });



            function classTitleShort(cellvalue, options, rowObject) {
                $scope.hideLoader = true;


                // do something here
                if (cellvalue.indexOf(".") != -1 && cellvalue.indexOf("mapChainIco") != -1) {
                    var valueArray = cellvalue.split('.');
                    valueArray = "<div class='mapChainIco'></div><span class='legendBullets mrlegendBullets mapper_ident'></span><div class='classDetails'><i class='fa fa-caret-right'></i>" + valueArray[valueArray.length - 1] + "</div>";
                    return valueArray;
                } else if (cellvalue.indexOf(".") != -1 && cellvalue.indexOf("redChainIco") != -1) {
                    var valueArray = cellvalue.split('.');
                    valueArray = "<div class='redChainIco'><i class='fa fa-caret-right'></i></div><span class='legendBullets mrlegendBullets mapper_ident'></span><div class='classDetails'><i class='fa fa-caret-right'></i>" + valueArray[valueArray.length - 1] + "</div>";
                    return valueArray;
                } else if (cellvalue.indexOf(".") != -1) {
                    var valueArray = cellvalue.split('.');
                    valueArray = "<div class='mapChainFkIco'></div><span class='legendBullets mrlegendBullets mapper_ident'></span><div class='classDetails'><i class='fa fa-caret-right'></i>" + valueArray[valueArray.length - 1] + "</div>";
                    return valueArray;
                } else {
                    return cellvalue;
                }
            }

            function classTitleShortFromInstanceTable(cellvalue, options, rowObject) {
                $scope.hideLoader = true;

                if (cellvalue.indexOf(".") != -1) {
                    var valueArray = cellvalue.split('.');
                    valueArray = "<div class='classDetails'>" + valueArray[valueArray.length - 1] + "</div>";
                    return valueArray;
                } else {
                    return cellvalue;
                }

            }


            $(".partitionerMapName").live('click', function() {
                $scope.hideLoader = true;

                var partitionerMapName = $(this).attr('rel');

                var numOfJobs = 0;
                var partitionerMapData = [];

                $.each(logAnalysisJSONStringObj, function(logName, logBean) {
                    var partitionerMap = logBean["partitionerMap"];
                    if (logName == 'ErrorAndException' || logName == 'debuggerSummary') {
                        return;
                    }
                    if (partitionerMapName) {
                        $.each(partitionerMap[partitionerMapName], function(partitionerMapId, partitionerMapBean) {

                            numOfJobs++;
                            var partitionName = partitionerMapBean["name"];
                            var partitionInputKeys = partitionerMapBean["inputKeys"];
                            var partitionIdealDistribution = partitionerMapBean["idealDistribution"];
                            var partitionVariance = partitionerMapBean["variance"] + "%";

                            var partitionerMapJsonObj = { "id": numOfJobs, "name": partitionName, "inputKeys": partitionInputKeys, "idealDistribution": partitionIdealDistribution, "variance": partitionVariance };
                            partitionerMapData.push(partitionerMapJsonObj);


                        });
                    }
                });


                var partitionerMapDataJson = { "response": partitionerMapData };
                jQuery("#partitionMapDivBox").html('<table id="partitionMapTable"></table>');

                jQuery("#partitionMapTable").jqGrid({
                    datastr: partitionerMapDataJson,
                    datatype: "jsonstring",
                    height: 'auto',
                    hidegrid: false,
                    loadui: "disable",
                    colNames: ["ID", "Name", "Input Keys", "Ideal Distribution", "Variance"],
                    colModel: [
                        { name: 'id', index: 'id', width: 30, align: "center" },
                        { name: 'name', index: 'name', width: 260, align: "center" },
                        { name: 'inputKeys', index: 'inputKeys', width: 60, align: "center" },
                        { name: 'idealDistribution', index: 'idealDistribution', width: 100, align: "center" },
                        { name: 'variance', index: 'variance', width: 100, align: "center" }

                    ],
                    rowNum: 100,
                    jsonReader: {
                        repeatitems: false,
                        root: "response"
                    }
                }).trigger("reloadGrid");


                $("#partitionMapDiv").dialog({
                    dialogClass: 'modalSelectLocation',
                    height: 'auto',
                    width: 600,
                    resizable: false,
                    modal: true
                });

            });

            $("#jobsViewLink").click(function() {
                $scope.hideLoader = true;
                $(".viewChangeLinks").find("a").each(function() {
                    $(this).removeClass("active");
                });
                $(this).addClass("active");

                $("#debugMainGridBox").find(".activegrid").each(function() {
                    $(this).fadeOut(300);
                });
                $("#jobActiveGrid").fadeIn(1500);

                $("#jobptreegrid").animate({ width: "100%" }, 500);
                $("#mapptreegrid, #insptreegrid").animate({ width: "100%" }, 500);

                $('#jobtreegrid').jqGrid("showCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
                $('#maptreegrid, #instreegrid').jqGrid("hideCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);

                //$('#insptreegrid').find('div.ui-jqgrid-sdiv td').hide();
                //$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0) td:eq(0)').show();
                //$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1) td:eq(0)').show();

                // Set all grid width
                /*$('#jobtreegrid').jqGrid().setGridWidth(522);
                $('#maptreegrid, #instreegrid').jqGrid().setGridWidth(221);*/
            });

            $("#mapsViewLink").click(function() {
                $scope.hideLoader = true;
                $(".viewChangeLinks").find("a").each(function() {
                    $(this).removeClass("active");
                });
                $(this).addClass("active");

                $("#debugMainGridBox").find(".activegrid").each(function() {
                    $(this).fadeOut(300);
                });
                $("#mapActiveGrid").fadeIn(1500);

                $("#jobptreegrid").animate({ width: "100%" }, 500);
                $("#mapptreegrid").animate({ width: "100%" }, 500);
                $("#insptreegrid").animate({ width: "100%" }, 500);

                $('#maptreegrid').jqGrid("showCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
                $('#jobtreegrid, #instreegrid').jqGrid("hideCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);

                //$('#insptreegrid').find('div.ui-jqgrid-sdiv td').hide();
                //$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0) td:eq(0)').show();
                //$('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1) td:eq(0)').show();

                //autoAdjustGridHeight();
                // Set all grid width
                /*$('#jobtreegrid').jqGrid().setGridWidth(222);
                $('#maptreegrid').jqGrid().setGridWidth(521);
                $('#instreegrid').jqGrid().setGridWidth(221);*/
            });

            $("#instViewLink").click(function() {
                $scope.hideLoader = true;
                $(".viewChangeLinks").find("a").each(function() {
                    $(this).removeClass("active");
                });
                $(this).addClass("active");

                $("#debugMainGridBox").find(".activegrid").each(function() {
                    $(this).fadeOut(300);
                });
                $("#insActiveGrid").fadeIn(1500);

                $("#jobptreegrid").animate({ width: "100%" }, 500);
                $("#mapptreegrid").animate({ width: "100%" }, 500);
                $("#insptreegrid").animate({ width: "100%" }, 500);

                $('#instreegrid').jqGrid("showCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);
                $('#jobtreegrid, #maptreegrid').jqGrid("hideCol", ["totalInputKeys", "totalContextWrites", "totalUnmatchedKeys", "totalUnmatchedValues"]);

                //$('#insptreegrid').find('div.ui-jqgrid-sdiv td').show();

                // Set all grid width
                /*$('#jobtreegrid').jqGrid().setGridWidth(222);
                $('#maptreegrid').jqGrid().setGridWidth(221);
                $('#instreegrid').jqGrid().setGridWidth(521);*/
            });

            //Click live jobs 
            $('#jobtreegrid tr').live('click', function() {
                $scope.hideLoader = true;
                var jobTitle = $scope.jobData.jobName; //$(this).find('.jobDetails').text();

                numOfMapReduce = 0;
                var logJobChurningData = [];
                var logMapChurningData = [];
                var logMapFooterChurningData = [];
                var logInsChurningData = [];

                $.each(logAnalysisJSONStringObj, function(logName, logBean) {
                    var logMap = logBean["logMap"];
                    if (logName == 'ErrorAndException' || logName == 'debuggerSummary') {
                        return;
                    }
                    $.each(logMap, function(jobId, jobBean) {
                        if (jobId == jobTitle) {
                            numOfJobs++;
                            var totalInputKeys = jobBean["totalInputKeys"];
                            var totalContextWrites = jobBean["totalContextWrites"];
                            var totalUnmatchedKeys = jobBean["totalUnmatchedKeys"];
                            if (totalUnmatchedKeys == '-1') {
                                totalUnmatchedKeys = '-';
                            }
                            var totalUnmatchedValues = jobBean["totalUnmatchedValues"];
                            if (totalUnmatchedValues == '-1') {
                                totalUnmatchedValues = '-';
                            }
                            var jobMap = jobBean["jobMap"];
                            var logChurningJsonObj = { "chain": " ", "id": numOfJobs, "elementName": jobId, "totalInputKeys": totalInputKeys, "totalContextWrites": totalContextWrites, "totalUnmatchedKeys": totalUnmatchedKeys, "totalUnmatchedValues": totalUnmatchedValues, level: "0", parent: "", isLeaf: false, expanded: false, loaded: true };
                            logMapFooterChurningData.push(logChurningJsonObj);
                        }
                    });
                });

                // push maps/reduces
                $.each(logAnalysisJSONStringObj, function(logName, logBean) {
                    var logMap = logBean["logMap"];
                    var mrChain = logBean["mrChain"];
                    if (logName == 'ErrorAndException' || logName == 'debuggerSummary') {
                        return;
                    }

                    //$('.classDetails').append('<i class="fa fa-caret-right"></i>');

                    $.each(logMap[jobTitle].jobMap, function(mapReduceName, mapReduceBean) {
                        numOfMapReduce++;
                        var totalMapReduceInputKeys = mapReduceBean["totalInputKeys"];
                        var totalMapReduceContextWrites = mapReduceBean["totalContextWrites"];
                        var totalMapReduceUnmatchedKeys = mapReduceBean["totalUnmatchedKeys"];
                        if (totalMapReduceUnmatchedKeys == '-1') {
                            totalMapReduceUnmatchedKeys = '-';
                        }
                        var totalMapReduceUnmatchedValues = mapReduceBean["totalUnmatchedValues"];
                        if (totalMapReduceUnmatchedValues == '-1') {
                            totalMapReduceUnmatchedValues = '-';
                        }
                        var mapReduceMap = mapReduceBean["mapReduceMap"];
                        var mapChainName = "";
                        var mapChainCounter = "";
                        var mapChainCount = 1;

                        $.each(mrChain, function(mrJobChainId, mrJobChainBean) {

                            if (jobTitle == mrJobChainId) {
                                //var marJobChain=mrJobChainBean[jobId];                    

                                if (mrJobChainBean["mapChainList"]) {
                                    $.each(mrJobChainBean["mapChainList"], function(mrMapChainId, mrMapChainBean) {

                                        if (mapReduceName == mrMapChainBean["name"]) {

                                            mapChainName = '<div class="mapChainIco"></div>';
                                            mapChainCounter = mapChainCount;
                                            //var mapChainInputKeys=mrMapChainBean["inputKeys"];
                                            //var mapChainContextWrites=mrMapChainBean["contextWrites"];
                                        }
                                        mapChainCount++;

                                    });
                                }

                                if (mrJobChainBean["reduceChainList"]) {
                                    $.each(mrJobChainBean["reduceChainList"], function(mrMapChainId, mrMapChainBean) {

                                        if (mapReduceName == mrMapChainBean["name"]) {

                                            mapChainName = '<div class="redChainIco"><i class="fa fa-caret-right"></i></div>';
                                            mapChainCounter = mapChainCount;
                                            //var mapChainInputKeys=mrMapChainBean["inputKeys"];
                                            //var mapChainContextWrites=mrMapChainBean["contextWrites"];
                                        }
                                        mapChainCount++;

                                    });
                                }


                            }

                        });
                        if (!mapChainCounter) {
                            mapChainCounter = 9999999;
                            mapChainName = '<div class="mapChainFkIco"></div>';
                        }

                        var mapReduceJsonObj = { "chain": "-", "id": mapChainCounter, "elementName": mapChainName + "asas<div class='classDetails'>" + mapReduceName + "<form></form></div>", "totalInputKeys": totalMapReduceInputKeys, "totalContextWrites": totalMapReduceContextWrites, "totalUnmatchedKeys": totalMapReduceUnmatchedKeys, "totalUnmatchedValues": totalMapReduceUnmatchedValues, level: "1", parent: numOfJobs, isLeaf: false, expanded: false, loaded: true };
                        logMapChurningData.push(mapReduceJsonObj);
                    });
                });
                mapGrid.clearGridData();
                mapGrid.jqGrid('footerData', 'set', logMapFooterChurningData[0]);
                mapGrid.jqGrid("setGridParam", { 'data': logMapChurningData }).trigger("reloadGrid");

                trIndex = $(this).index();

                $("#maptreegrid tr:nth-child(2)").trigger('click');

            });


            //Click live maps/reduces
            $("#maptreegrid tr").live('click', function() {
                $scope.hideLoader = true;

                var jobTitle = $scope.jobData.jobName; //$("#jobtreegrid tr:nth-child("+parseInt(trIndex+1)+")").find('.jobDetails').text();  
                var mapTitle = $(this).find('.classDetails').text();
                $('.classDetails').removeClass('highlightedClass');
                $(this).removeClass('ui-priority-secondary');

                $('.classDetails').addClass('ui-priority-secondary');

                $(this).find('.classDetails').addClass('highlightedClass');



                var insTitle = $(this).find('.instancemodal').text();



                // push instances
                var logJobChurningData = [];
                var logMapChurningData = [];
                var logNodChurningData = [];
                var logInsChurningData = [];
                var logInsFooterChurningData = [];
                numOfInstance = 0;
                numOfNodes = 0;
                numOfMapReduce = 0;

                $.each(logAnalysisJSONStringObj, function(logName, logBean) {
                    var logMap = logBean["logMap"];
                    if (logName == 'ErrorAndException' || logName == 'debuggerSummary') {
                        return;
                    }

                    $.each(logMap, function(jobId, jobBean) {
                        if (jobId == jobTitle) {
                            //Insert job name in breadcrum
                            $('#breadcrumBox').html('<span id="jobBc">' + jobId + '<span class="raquo">&raquo;</span></span>');

                            numOfJobs++;
                            var totalInputKeys = jobBean["totalInputKeys"];
                            var totalContextWrites = jobBean["totalContextWrites"];
                            var totalUnmatchedKeys = jobBean["totalUnmatchedKeys"];
                            if (totalUnmatchedKeys == '-1') {
                                totalUnmatchedKeys = '-';
                            }
                            var totalUnmatchedValues = jobBean["totalUnmatchedValues"];
                            if (totalUnmatchedValues == '-1') {
                                totalUnmatchedValues = '-';
                            }
                            var jobMap = jobBean["jobMap"];


                            var logChurningJsonObj = { "chain": " ", "id": 99999, "elementName": jobId, "totalInputKeys": totalInputKeys, "totalContextWrites": totalContextWrites, "totalUnmatchedKeys": totalUnmatchedKeys, "totalUnmatchedValues": totalUnmatchedValues, level: "0", parent: "", isLeaf: false, expanded: false, loaded: true };
                            logInsFooterChurningData.push(logChurningJsonObj);
                        }
                    });
                });


                $.each(logAnalysisJSONStringObj, function(logName, logBean) {
                    var logMap = logBean["logMap"];
                    if (logName == 'ErrorAndException' || logName == 'debuggerSummary') {
                        return;
                    }

                    $.each(logMap[jobTitle].jobMap, function(mapReduceName, mapReduceBean) {
                        numOfMapReduce++;
                        var valueArray = mapReduceName.split('.');


                        if (mapTitle == valueArray[valueArray.length - 1]) {
                            //Insert mapper/reducer name in breadcrum           
                            $('#breadcrumBox').append('<span id="mapBc">' + mapTitle + '<span class="raquo">&raquo;</span></span>');

                            var totalMapReduceInputKeys = mapReduceBean["totalInputKeys"];
                            var totalMapReduceContextWrites = mapReduceBean["totalContextWrites"];
                            var totalMapReduceUnmatchedKeys = mapReduceBean["totalUnmatchedKeys"];
                            if (totalMapReduceUnmatchedKeys == '-1') {
                                totalMapReduceUnmatchedKeys = '-';
                            }
                            var totalMapReduceUnmatchedValues = mapReduceBean["totalUnmatchedValues"];
                            if (totalMapReduceUnmatchedValues == '-1') {
                                totalMapReduceUnmatchedValues = '-';
                            }
                            var mapReduceMap = mapReduceBean["mapReduceMap"];
                            var mapReduceJsonObj = { "chain": " ", "id": 9999999, "id": numOfJobs + '_' + numOfMapReduce, "elementName": "<div class='classDetails'>" + mapReduceName + "<form></form></div>", "totalInputKeys": totalMapReduceInputKeys, "totalContextWrites": totalMapReduceContextWrites, "totalUnmatchedKeys": totalMapReduceUnmatchedKeys, "totalUnmatchedValues": totalMapReduceUnmatchedValues, level: "1", parent: numOfJobs, isLeaf: false, expanded: false, loaded: true };
                            logInsFooterChurningData.push(mapReduceJsonObj);


                            $.each(mapReduceMap, function(nodeName, nodeBean) {
                                numOfNodes++;
                                var nodeMap = nodeBean["nodeMap"];
                                logNodChurningData.push(nodeName);


                                var nodeColor = colorCodes[numOfNodes];

                                $.each(nodeMap, function(instanceName, instanceBean) {
                                    numOfInstance++;
                                    var totalInstanceInputKeys = instanceBean["totalInputKeys"];
                                    var totalInstanceContextWrites = instanceBean["totalContextWrites"];
                                    var totalInstanceUnmatchedKeys = instanceBean["totalUnmatchedKeys"];
                                    if (totalInstanceUnmatchedKeys == '-1') {
                                        totalInstanceUnmatchedKeys = '-';
                                    }
                                    var totalInstanceUnmatchedValues = instanceBean["totalUnmatchedValues"];
                                    if (totalInstanceUnmatchedValues == '-1') {
                                        totalInstanceUnmatchedValues = '-';
                                    }
                                    var mapValueArray = mapReduceName.split('.');

                                    var taskName = mapValueArray[mapValueArray.length - 1] + "__" + instanceName;

                                    var instanceMap = instanceBean["instanceMap"];

                                    var instanceJsonObj = { "id": numOfJobs + '_' + numOfMapReduce + '_' + numOfNodes + '_' + numOfInstance, "elementName": "<span class='legendBullets' style='background-color:" + nodeColor + "'></span><div class='instancemodal' style='cursor:pointer'><i class='fa fa-caret-right'></i>" + instanceName + " <form><input type='hidden' name='elementName' value=" + taskName + " id='elementName' /></form></div>", "totalInputKeys": totalInstanceInputKeys, "totalContextWrites": totalInstanceContextWrites, "totalUnmatchedKeys": totalInstanceUnmatchedKeys, "totalUnmatchedValues": totalInstanceUnmatchedValues, level: "3", parent: numOfJobs + '_' + numOfMapReduce + '_' + numOfNodes, isLeaf: true, expanded: false, loaded: true };

                                    logInsChurningData.push(instanceJsonObj);
                                });
                            });

                        } else
                            var mapReduceMap = mapReduceBean["mapReduceMap"];

                    });
                });

                insGrid.clearGridData();

                $('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1)').remove();

                insGrid.jqGrid("setGridParam", { 'data': logInsChurningData }).trigger("reloadGrid");
                insGrid.jqGrid('footerData', 'set', logInsFooterChurningData[0]);
                var copyMapHtml = $(this).clone().html().replace(/maptreegrid/g, "instreegrid");
                $(this).clone().insertAfter($('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0)'));
                $('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1)').html().replace(/maptreegrid/g, "instreegrid");
                $('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1)').removeClass().addClass('footrow footrow-ltr'); //ui-widget-content 



                $('#nodeLegendBox').html('');


                for (var i = 0; i < logNodChurningData.length; i++) {
                    $('#nodeLegendBox').append("<li><span class='legendBullets' style='background-color:" + colorCodes[i + 1] + "'></span>" + logNodChurningData[i] + "</li>");
                }

                $('#insptreegrid').find('div.ui-jqgrid-sdiv td').show();
                $('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(0) td:eq(0), div.ui-jqgrid-sdiv tr:eq(0) td:eq(1)').hide();
                $('#insptreegrid').find('div.ui-jqgrid-sdiv tr:eq(1) td:eq(0), div.ui-jqgrid-sdiv tr:eq(1) td:eq(1)').hide();

                // Set vertical scroller to Job grid
                var jobBDivHeight = $("#jobptreegrid .ui-jqgrid-bdiv").height();
                var jobBDivTableHeight = $("#jobptreegrid .ui-jqgrid-bdiv div:first").height();
                if (jobBDivTableHeight >= jobBDivHeight) {
                    jobGrid.jqGrid("setGridParam", { 'scrollOffset': 18 });
                }

                // Set vertical scroller to Mappers/Reducers grid
                var mapBDivHeight = $("#mapptreegrid .ui-jqgrid-bdiv").height();
                var mapBDivTableHeight = $("#mapptreegrid .ui-jqgrid-bdiv div:first").height();
                if (mapBDivTableHeight >= mapBDivHeight) {
                    mapGrid.jqGrid("setGridParam", { 'scrollOffset': 18 });
                }

                // Set vertical to Instance grid
                var insBDivHeight = $("#insptreegrid .ui-jqgrid-bdiv").height();
                var insBDivTableHeight = $("#insptreegrid .ui-jqgrid-bdiv div:first").height();
                if (insBDivTableHeight >= insBDivHeight) {
                    insGrid.jqGrid("setGridParam", { 'scrollOffset': 18 });
                }


            });

            // Instance counter sub table 

            $(document).ready(function() {
                $scope.hideLoader = true;

                $("#jobtreegrid tr:nth-child(2)").trigger('click'); // default trigger first row click of jobs table


                $(".instancemodal").live('click', function() {
                    var serializedData = $(this).find('form').serialize();
                    var tempName = serializedData.split('=');
                    var mapElementName = tempName[1].split('__')[0];
                    var elementName = tempName[1].split('__')[1];
                    var insKeyValArr = [];

                    //Insert instance name in breadcrum     


                    if ($('#breadcrumBox:contains(' + elementName + ')').length == 0 && $('#breadcrumBox').find('#insBc').length == 0)
                        $('#breadcrumBox').append('<span id="insBc">' + elementName + '</span>');
                    else
                        $('#breadcrumBox').find('#insBc').html(elementName);

                    $("#insSubCounter").show().find(".widget-header").html(elementName);


                    $.each(logAnalysisJSONStringObj, function(logName, logBean) {
                        var logMap = logBean["logMap"];
                        if (logName == 'ErrorAndException' || logName == 'debuggerSummary') {
                            return;
                        }
                        $.each(logMap, function(jobId, jobBean) {

                            var jobMap = jobBean["jobMap"];
                            $.each(jobMap, function(mapReduceName, mapReduceBean) {

                                var mapReduceMap = mapReduceBean["mapReduceMap"];
                                $.each(mapReduceMap, function(nodeName, nodeBean) {

                                    var nodeMap = nodeBean["nodeMap"];
                                    $.each(nodeMap, function(instanceName, instanceBean) {
                                        var mapValueArray = mapReduceName.split('.');

                                        insKeyValArr["totalUnmatchedKeys"] = instanceBean["totalUnmatchedKeys"];
                                        insKeyValArr["totalUnmatchedValues"] = instanceBean["totalUnmatchedValues"];

                                        if (mapElementName == mapValueArray[mapValueArray.length - 1] && elementName == instanceName) {
                                            var instanceMap = instanceBean["instanceMap"];
                                            if (typeof instanceMap == 'undefined') {
                                                $('#taskCountersDiv ul:first').html('');
                                                $("#taskCountersTableWrap").html('<div class="status info"><span>Information: </span>No data available.</div>');
                                                if (instanceName.indexOf('_m_') > 0) {
                                                    $("#treeRootName").html("Map");
                                                } else {
                                                    $("#treeRootName").html("Reduce");
                                                }
                                                return;
                                            }
                                            var taskCounterData = '';
                                            $.each(instanceMap, function(counterName, counterBean) {
                                                var counterMap2 = counterBean["counterMap"];

                                                mapCounterData[0] = counterMap2;

                                                if (counterName == "map") {
                                                    $("#treeRootName").html("Map");
                                                } else {
                                                    $("#treeRootName").html("Reduce");
                                                }
                                                taskCounterData += '<li id="phtml_1"><a class="rootmap" title="' + counterName + '" ng-click="createTreeTable(0, ' + instanceBean["totalUnmatchedKeys"] + ', ' + instanceBean["totalUnmatchedValues"] + ');"></a>';
                                                if (typeof counterMap2 != 'undefined') {
                                                    taskCounterData += '<ul>';
                                                    taskCounterData = createTreeNode(counterMap2, taskCounterData, instanceBean["totalUnmatchedKeys"], instanceBean["totalUnmatchedValues"]);
                                                    taskCounterData += '</ul></li>';



                                                }
                                            });
                                            if (taskCounterData.length > 0) {
                                                $('#taskCountersDiv ul:first').html(taskCounterData);
                                                var template = angular.element('#taskCountersDiv');
                                                var linkFn = $compile(template)($scope);
                                                if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
                                                    $scope.$apply();
                                                }


                                            }
                                        }
                                    });
                                });
                            });
                        });
                    });

                    $("#taskCountersDiv").jstree({
                        "plugins": ["themes", "html_data", "ui"],
                        "core": { "initially_open": ["phtml_1"] }
                    }).bind("before.jstree", function(e, data) {
                        if (data.func === "close_node") {
                            e.stopImmediatePropagation();
                            return false;
                        }
                    });
                    setTimeout(function() { $("#taskCountersDiv").jstree("open_all"); }, 500);
                    $("#taskCountersDiv").find("a.rootmap").trigger("click");
                });
            });

            // Create tree

            function createTreeNode(counterMap, taskCounterData, insTotalUnmatchedKeys, insTotalUnmatchedValues) {
                $scope.hideLoader = true;
                var dataType = '';
                $.each(counterMap, function(counterName, counterBean) {
                    var counterMapObj = counterBean["counterMap"];

                    dataType = counterBean["counterDetails"];
                    var lngth = mapCounterData.length;



                    mapCounterData[lngth] = counterMapObj;
                    if (dataType == "method") {
                        taskCounterData += '<li><a class="method" href="javascript:void(0);" title="' + counterName + '" ng-click="createTreeTable(' + lngth + ', ' + insTotalUnmatchedKeys + ', ' + insTotalUnmatchedValues + ');"></a>';

                    } else {
                        taskCounterData += '<li><a class="counters" href="javascript:void(0);" title="' + counterName + ' (Line no: ' + counterBean["counterDetails"] + ')" ng-click="createTreeTable(' + lngth + ', ' + insTotalUnmatchedKeys + ', ' + insTotalUnmatchedValues + ');"></a>';
                    }

                    if (typeof counterMapObj != 'undefined') {
                        taskCounterData += '<ul>';
                        taskCounterData = createTreeNode(counterMapObj, taskCounterData, insTotalUnmatchedKeys, insTotalUnmatchedValues);
                        taskCounterData += '</ul></li>';
                    }
                    if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
                        $scope.$apply();
                    }
                });
                return taskCounterData;
            }

            $("#debugRptViewOpt").find("a.viewOpt").click(function() {
                $scope.hideLoader = true;

                $("#debugRptViewOpt").find("a.viewOpt").each(function() {
                    $(this).find("span").removeClass("selView");
                });
                $(this).find("span").addClass("selView");
                var targetView = $(this).attr("ui:target");
                $("div.debugReportTblChartViewCnt").each(function() {
                    $(this).css({ "display": "none" });
                });
                $("div#" + targetView).css({ "display": "block" });
                if (targetView == "debugreportPieChartView") {
                    $("#debugReportAccordion").find("div.ui-accordion-content").each(function() {
                        $(this).css({ "height": "auto" });
                    });
                }

                for (var k = 0; k < mrPlot.length; k++) {
                    mrPlot[k].replot();
                    iPlot[k].replot();
                }
                //iPlot.replot();
            });

            $("#jobsChainSorting").live("click", function() {
                $scope.hideLoader = true;

                $("#jobtreegrid").jqGrid('setGridParam', { sortname: 'id', sortorder: 'asc' });
                //$('#jobtreegrid').jqGrid("showCol", ["chain"]);
                $('#jobtreegrid').trigger("reloadGrid");
                $("#jobptreegrid").find(".jobChainIco").each(function() {
                    $(this).css({ "visibility": "visible" });
                });
                $("#jobtreegrid tr:nth-child(2)").trigger('click'); // default trigger first row click of jobs table
            });

            $("#mapsChainSorting").live("click", function() {
                $scope.hideLoader = true;

                $("#maptreegrid").jqGrid('setGridParam', { sortname: 'id', sortorder: 'asc' });
                //$('#maptreegrid').jqGrid("showCol", ["chain"]);
                $('#maptreegrid').trigger("reloadGrid");
                $("#mapptreegrid").find(".mapChainIco, .redChainIco").each(function() {
                    $(this).css({ "visibility": "visible" });
                });
                $("#maptreegrid tr:nth-child(2)").trigger('click'); // default trigger first row click of map table
            });

        }
    }]);
