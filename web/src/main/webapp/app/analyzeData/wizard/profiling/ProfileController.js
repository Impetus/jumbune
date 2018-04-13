/* Profile controller */
'use strict';
angular.module('profile.ctrl', [])

.controller('ProfileController', ['$scope', '$http', '$rootScope','$routeParams','$location', 'common', '$timeout','analyzeDataProfiling',

    function($scope, $http, $rootScope,$routeParams, $location,common, $timeout,analyzeDataProfiling) {

        $scope.selProfilingObj = {};
        $scope.enable = false;
        $scope.select = true;
        $scope.startCriteria = false;
        $scope.noOfFields = false;
        $scope.fieldArray = [];
        $scope.currDataProfile;
        $scope.toBeDeleted = false;
        $scope.common = common;
        $scope.submitForm = false;
        $scope.errorMessageShow = false;
        $scope.dataProfilingOperandArr = [];
        $scope.fieldCount = 1;
		$scope.enableRowData = [];
        $scope.fieldProfilingRulesObj = {
                        "comparisonValue":"",
                        "fieldNumber": "",
                        "dataProfilingOperand": ""

        };
        $scope.fieldProfilingRules = [];
        var clusList = common.getAnalyzeDataDetail();
				$scope.selProfilingObj.enableDataProfiling = 'noCriteria';
				$scope.hasError = function(fieldName) {
						var error = ($scope.DpForm[fieldName].$invalid && !$scope.DpForm[fieldName].$pristine) || ($scope.DpForm[fieldName].$invalid && submitForm)
						return error;
				};

        $scope.init = function() {
            $scope.recentJobResponse = common.getResonseData();
             $scope.dataProfilingOperandArr = [
        {label:'greater than equal to', value:'GREATER_THAN_EQUAL_TO'},
        {label:'less than equal to', value:'LESS_THAN_EQUAL_TO'}];

            var local = common.getLocalDP();
           
            if(common.jobMode === 'edit') {
                if(typeof validationDataObj !== undefined) {
                    $scope.fieldArray = validationDataObj.enableRowData;
                    $scope.fieldCount = validationDataObj.enableRowData.length;
                }
            } else {
                $scope.fieldArray.push(new getDefaultField(0));
            }
            common.setLocalDP(false);
            var searchModule = $location.search().module;

           if(searchModule) { 
            } else {
                 $scope.autoFillProfiling = { analyzeData: $scope.dataProfilingAutoFill()};
            }
        };

        $scope.dataProfilingAutoFill = function () {
                var responseTRS = $scope.recentJobResponse.dataProfilingBean.recordSeparator;
                var responseTuppleRS = JSON.stringify(responseTRS);
                $scope.selProfilingObj.inputPath = $scope.recentJobResponse.hdfsInputPath;
                $scope.selProfilingObj.tupleRS =    responseTuppleRS.split("\"")[1];
                $scope.selProfilingObj.tupleFS = $scope.recentJobResponse.dataProfilingBean.fieldSeparator;
                $scope.fieldCount = $scope.recentJobResponse.dataProfilingBean.numOfFields;
                $scope.fieldArray = $scope.recentJobResponse.dataProfilingBean.fieldProfilingRules;
        }
        $scope.addZkHostPort = function(){
            $scope.fieldProfilingRules.push(angular.copy($scope.fieldProfilingRulesObj));

        };
        $scope.save = function(){
             $scope.enableRowData = [];
            $scope.fieldArray.forEach(function(field) {
                if(field.fieldNumber){
                    $scope.enableRowData.push(field);
                }

            });

			var obj = {
				 "dataProfilingBean":{
                "recordSeparator": $scope.selProfilingObj.tupleRS,
                "fieldSeparator": $scope.selProfilingObj.tupleFS,
                "fieldProfilingRules" :$scope.enableRowData,
                "numOfFields": parseInt($scope.fieldCount)
                },
                 "enableDataProfiling" : "TRUE",
                 "operatingCluster" : clusList.clusterName,
                "jumbuneJobName" : clusList.jobName,
                "operatingUser" : angular.copy(clusList.jobSubUser),
                "hdfsInputPath": angular.copy(clusList.hdfsInputPath),
                "tempDirectory" : angular.copy(clusList.tempDirectory),
                "parameters"  : angular.copy(clusList.parameters)
            };
                     common.setProfilingFlag(obj.enableDataProfiling);
                    common.setNumField(obj.dataProfilingBean.numOfFields);
                    var jsonDataIs = JSON.stringify(obj);
                    jsonDataIs = jsonDataIs.replace(/\\\\/g, '\\')
                    $scope.content = new FormData();
                    $scope.content.append("jsonData", jsonDataIs);
                         var req = {
                            method: 'POST',
                            url:'/apis/validateservice/validatejobinput',
                            headers: {'Content-Type': undefined },
                            transformRequest: angular.identity,
                            data:$scope.content,
                        };

                        $http(req).then(function(data){
                            if(data.data.STATUS=="ERROR" && data.data.hdfsInputPath) {
                                $scope.errorMessage = data.data.hdfsInputPath;
                                $scope.errorMessageShow = true;
                            } else {

                                var req = {
                                    method: 'POST',
                                    url:'apis/jobanalysis/save',
                                    headers: {'Content-Type': undefined },
                                    transformRequest: angular.identity,
                                    data:$scope.content,
                                };

                                $http(req).then(function(data){
                                   common.setJobName(data.data.JOB_NAME);
                                    $location.path('/analyze-data-profiling');
                                }, function(error){
                                    console.log("Error while saving the job details.",error)
                                });
                            }
                        }, function(error){
							console.log("Error while validating the job details.", error);
                        });



        };

        $scope.enableQT = function(isQTimeline){
            $scope.enable = isQTimeline;
            $scope.select = false;
        };

        $scope.getObjectForJSON = function(){
            var getObject = {
                "dataProfilingBean":{
                "recordSeparator": $scope.selProfilingObj.tupleRS,
                "fieldSeparator": $scope.selProfilingObj.tupleFS,
                "fieldProfilingRules" :$scope.enableRowData,
                "numOfFields": parseInt($scope.selProfilingObj.fields)
                },
                 "enableDataProfiling" : "TRUE",
                 "operatingCluster" : clusList.clusterName,
                "jumbuneJobName" : clusList.jobName,
                "tempDirectory" : angular.copy(clusList.tempDirectory),
                "operatingUser" : angular.copy(clusList.jobSubUser),
                "hdfsInputPath": angular.copy(clusList.hdfsInputPath),
                "parameters"  : angular.copy(clusList.parameters)
            };
            return getObject;
        };

        $scope.enableDP = function(isDataProfiling){
            $scope.enable = isDataProfiling;
            $scope.select = false;
        };

        $scope.criteriaBased = function(param){
			$scope.startCriteria = false;
			if($scope.selProfilingObj.enableDataProfiling === 'withCriteria'){
				$scope.startCriteria = true;
			}
        };

        $scope.generateFields = function(){
            $scope.fieldArray = [];
            $scope.noOfFields = true;
            for(var i=0; i<$scope.fieldCount; i++){
                $scope.fieldArray.push(new getDefaultField(i));
            }
        };

        $scope.cancel = function(){
            $location.path('/index');
        };
        $scope.back = function(){
            common.setJobDetailsFlagRes(true)
            common.setActiveTab('Data Profiling');
            $location.path('/add-analyze-data-configuration');
        };

        $scope.gotoIndex = function(){
            $location.path('/dashboard')
        };

        $scope.submit = function(){
            common.dataAnalysisChart = 2;
            $location.path('/dpdq-preview');
        };

        $scope.displayMsgBox = function(type, messageString){
            if(type == 'Success'){
                $scope.saveSuccess = true;
            }
            $scope.displayBlock = true;
            $scope.blockMessage = messageString;
            $timeout(function(){
                $scope.displayBlock = false;
                $scope.blockMessage = "";
            },3000);
        };
        function getDefaultField(i) {
            this.comparisonValue = '';
            this.dataProfilingOperand = '';
            this.fieldNumber = '';
        }
        $('#formDisableOneClick').one('click', function() {
			$(this).attr('disabled','disabled');
		});
    }]);


