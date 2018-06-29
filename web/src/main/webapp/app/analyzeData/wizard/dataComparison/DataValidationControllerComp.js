/* Define Data Validation Controller */
'use strict';
angular.module('dataValidateComp.ctrl', []).controller('dataValidationControllerComp', ['$scope', '$http', '$rootScope', 'common', '$location', 'DataValidationFactories', 'analyzeValidationFactory', 'getSchemaDataFactory', function($scope, $http, $rootScope, common, $location, DataValidationFactories, analyzeValidationFactory, getSchemaDataFactory) {
    var jsonData = common.getdataValidationXMLData();
    $scope.recordSeparator;
    $scope.JSONdata = jsonData.data;
    var clusList = common.getAnalyzeDataDetailXML();
    $scope.fieldValidationData = [];
    $scope.custom11 = [];
    $scope.errorMessageShow = false;
    $scope.isStructure = false;
    $("div.my-tool-tip").tooltip();

    for (var key in $scope.JSONdata) {
        $scope.custom11.push({
            'key': key,
            'dataType': $scope.JSONdata[key]
        })
    }
    $scope.workerHostArr = [];

    var fieldProfilingRulesObj = {
        "sourcefieldNumber": "",
        "transformationMethod": "",
        "destinationFieldNumber": "",
        "enable": false
    };

    $scope.workerNodeHostArrCount = [angular.copy(fieldProfilingRulesObj)];


    $scope.addWorkerNodeHost = function() {
        if ($scope.workerNodeHostArrCount.length <= 9) {
            //$scope.workerNodeHostArrCount.push($scope.workerNodeHostArrCount.length+1);
            $scope.workerNodeHostArrCount.push(angular.copy(fieldProfilingRulesObj));
        } else {

        }


    };

    $scope.removeWorkerNodeHost = function(index) {
        $scope.workerNodeHostArrCount.splice(index, 1);
    };


    $scope.cancel = function() {
        $location.path('/index');
    };

    $scope.back = function() {
        $location.path('/add-analyze-data-configuration');
    };
    function isNullOrEmpty(val) {
    	if (val == null || val == '') {
    		return true;
    	} else {
    		return false;
    	}
    }

    $scope.isDisabled = function() {
    	if (isNullOrEmpty($scope.sourcePrimaryKey) || 
    		isNullOrEmpty($scope.destinationPrimaryKey) || 
    		isNullOrEmpty($scope.sourcePath) || 
    		isNullOrEmpty($scope.destinationPath) || 
    		isNullOrEmpty($scope.fieldSeparator) ||
    		isNullOrEmpty($scope.recordSeparator) ||
    		$scope.workerNodeHostArrCount == null ||
    		$scope.workerNodeHostArrCount.length == 0 ||
    		$scope.workerNodeHostArrCount[0].sourcefieldNumber == '' ||
    		$scope.workerNodeHostArrCount[0].destinationFieldNumber == ''
    		) {
    		return true;
    	}
    	return false;
    }

    $scope.errorMessageShowsourcePath = false;
    $scope.errorMessageShowdestinationPath = false;

    $scope.next = function() {
        var filePathName = common.getJobJarFile();
        var dataValidation1 = {
            "dataSourceCompValidationInfo": {
                validationsList: angular.copy($scope.workerNodeHostArrCount),
                "sourcePrimaryKey": $scope.sourcePrimaryKey,
                "destinationPrimaryKey": $scope.destinationPrimaryKey,
                "sourcePath": $scope.sourcePath,
                "destinationPath": $scope.destinationPath,
                "fieldSeparator": $scope.fieldSeparator,
                "recordSeparator": $scope.recordSeparator
            },
            "operatingCluster": angular.copy(clusList.clusterName),
            "operatingUser": angular.copy(clusList.jobSubUser),
            "isDataSourceComparisonEnabled": "TRUE",
            "jumbuneJobName": angular.copy(clusList.jobName)
        }

        var array = dataValidation1["dataSourceCompValidationInfo"]["validationsList"];
        for (var i = 0; i < array.length; i++) {
            array[i]["sourcefieldNumber"] = Number(array[i]["sourcefieldNumber"]);
            array[i]["destinationFieldNumber"] = Number(array[i]["destinationFieldNumber"]);
        }
        common.setDataValFlag(dataValidation1.isDataSourceComparisonEnabled);
        common.setWidgetInfo('dataValidation', dataValidation1);
        var mergedObject = angular.extend({}, dataValidation1);
        common.setJsonDataValFlag(mergedObject.isDataSourceComparisonEnabled);
        /* var jsonDataIs = JSON.stringify(mergedObject);
         jsonDataIs = jsonDataIs.replace(/\\\\/g, '\\')
         $scope.content = new FormData();
         $scope.content.append("jsonData", jsonDataIs);*/
        var filenameis = $scope.filePath;
        var filePath = filePathName;
        var jsonDataIs = JSON.stringify(mergedObject);
        $scope.content = new FormData();
        $scope.content.append("inputFile", filePath);
        $scope.content.append("jsonData", jsonDataIs);

        if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
            $scope.$apply();
        }

        var req = {
            method: 'POST',
            url: '/apis/validateservice/validatejobinput',
            headers: { 'Content-Type': undefined },
            transformRequest: angular.identity,
            data: $scope.content,
        };

        $http(req).then(function(data) {
            if (data.data.STATUS=="ERROR" && (data.data.destinationPath || data.data.sourcePath)) {
                                $scope.errorMessagedestinationPath = data.data.destinationPath;
                                 $scope.errorMessagesourcePath = data.data.sourcePath;
                                $scope.errorMessageShowsourcePath = true;
                                $scope.errorMessageShowdestinationPath = true;
                            } else {

                var req = {
                    method: 'POST',
                    url: '/apis/jobanalysis/save',
                    headers: { 'Content-Type': undefined },
                    transformRequest: angular.identity,
                    data: $scope.content,
                };

                $http(req).then(function(data) {
                    common.setJobName(data.data.JOB_NAME);
                    $location.path('/analyze-data-comp');
                }, function(error) {
                    console.log("in error", error)
                });

            }
        }, function(error) {
            console.log("in error", error)
        });
    };
}]);
