/* Dashboard controller */
'use strict';
angular.module('dataCleansingCtrl.ctrl', ["ui.grid", 'ui.bootstrap', 'ui.grid.pagination'])
    .controller('AnalyzeDataCleansing', ['$scope', 'common', '$http', '$location', '$timeout', 'uiGridConstants', 'getTableDataFactory', 'getDataComparisonTableFactory', function($scope, common, $http, $location, $timeout, uiGridConstants, getTableDataFactory, getDataComparisonTableFactory) {
            //Voilation detail grid
            var jobName = common.getOptimizeJobName();
            //var jobName = "qq"
            $scope.showJobName = jobName
            $scope.violationTable = {};
            $scope.violationJSONTable = {};
            $scope.tableHideFlag = false;
            $scope.tableCompHideFlag = false;
            $scope.dataFlag = false;
            $scope.DataFlag = false;
            $scope.webSocketErrorFlag = false;
            $scope.loaderFlag = false;
            var finaljson;
            $scope.licenseExpireTrue = false;
            $scope.licenseExpireDays = false;
            
            var data = [];
            /** init function */
            $scope.init = function() {
                $('[data-toggle="tooltip"]').tooltip();
                licenseExpireMessage();
                $("h2.my-tool-tip").tooltip();
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
                   $scope.loaderFlag = true;
                    $scope.webSocketErrorFlag = true;
                    var serverData = angular.copy(event.data);
                    console.log("webSocket data", serverData);
                    var localData = JSON.parse(JSON.parse(serverData).DATA_CLEANSING);
                    $scope.resultJson(localData)
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
            $scope.resultJson = function(localData) {
                for ( var key in localData) {
                    $scope.safeApply($scope.totalNumberOfDirtyFiles = localData[key].totalNumberOfDirtyFiles);
                    $scope.safeApply($scope.dlcFilesList = localData[key].cntOfDirtySubDirectories);
                    $scope.safeApply($scope.cleanFilesList = localData[key].cntOfCleanSubDirectories);
                    $scope.safeApply($scope.totalNumberOfCleanFiles = localData[key].totalNumberOfCleanFiles);
                    $scope.safeApply($scope.cleanDataRootLocation = localData[key].cleanDataRootLocation);
                    $scope.safeApply($scope.dlcRootLocation = localData[key].dlcRootLocation);
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
            $scope.totalViolationsArray = new Array();
            $scope.fieldArray = new Array();

            var colors = d3.scale.category20();
            var keyColor = function(d, i) {
                return colors(d.key)
            };
            function toggleChevron(e) {
                $(e.target)
                    .prev('.panel-heading')
                    .find("i.indicator")
                    .toggleClass('glyphicon-chevron-down glyphicon-chevron-up');
            }
            $('#accordion').on('hidden.bs.collapse', toggleChevron);
            $('#accordion').on('shown.bs.collapse', toggleChevron);

        }

    ]);
