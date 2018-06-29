/* Define Data Validation Controller */
'use strict';
angular.module('dataValidateJSON.ctrl', []).controller('dataValidationControllerJSON', ['$scope','$http', '$rootScope', 'common', '$location', 'DataValidationFactories','analyzeValidationFactory', function($scope, $http, $rootScope, common, $location, DataValidationFactories,analyzeValidationFactory){
  

         var jsonData = common.getdataValidationJSONData();
         $scope.JSONdata = jsonData.data;
         var clusList = common.getAnalyzeDataDetailJSON();
        $scope.custom11 = [];
        $scope.errorMessageShow = false;

        for (var key in $scope.JSONdata) {
             $scope.custom11.push({
                'key' : key,
                'dataType' : $scope.JSONdata[key]
            })
        }

          $scope.nullCheckList = [
        {label:'can be null', value:'canBeNull'},
        {label:'can not be null', value:'cannotBeNull'}];

        $scope.cancel = function () {
            $location.path('/index');
        };

        $scope.back = function () {

            $location.path('/add-analyze-data-configuration');
        };
        $scope.next = function () {
            var temp = $scope.custom11;

            var finalJson = [];
            var secondColumn = {};
            for (var key in $scope.JSONdata) {
                secondColumn[key] = $scope.JSONdata[key];
            }
            finalJson.push(secondColumn);

            var thirdColumn = {};

            for (var i = 0; i < temp.length; i++) {
                if (temp[i].nullCheck != undefined && temp[i].nullCheck != "") {
                    thirdColumn[temp[i].key] = temp[i].nullCheck;
                }
            }

            finalJson.push(thirdColumn);

            var fourthColumn = {};

            for (var i = 0; i < temp.length; i++) {
                if (temp[i].regex != undefined && temp[i].regex != "") {
                    fourthColumn[temp[i].key] = temp[i].regex;
                }
            }

            finalJson.push(fourthColumn);

            var dataValidation1 = {
                fieldValidationList : finalJson,
                "operatingCluster" : angular.copy(clusList.selectedCluster),
                "operatingUser" : angular.copy(clusList.jobSubmissionUser),
                "enableJsonDataValidation" : "TRUE",
                "jumbuneJobName" : angular.copy(clusList.jumbuneJobName),
                "hdfsInputPath"  : angular.copy(clusList.hdfsInputPath),
                "tempDirectory" : angular.copy(clusList.tempDirectory),
                "parameters"  : angular.copy(clusList.parameters)
            }
            
            common.setDataValFlag(dataValidation1.enableJsonDataValidation);
            common.setWidgetInfo('dataValidation', dataValidation1);
            var mergedObject = angular.extend({} ,dataValidation1);
            common.setJsonDataValFlag(mergedObject.enableJsonDataValidation);
            common.setHdfsInputPath(mergedObject.hdfsInputPath);
            
           var jsonDataIs = JSON.stringify(mergedObject);
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
                                    url:'/apis/jobanalysis/save',
                                    headers: {'Content-Type': undefined },
                                    transformRequest: angular.identity,
                                    data:$scope.content,
                                };

                                $http(req).then(function(data){
                                    common.setJobName(data.data.JOB_NAME);
                                   $location.path('/analyze-data-json');
                                }, function(error){
                                    console.log("in error",error)
                                }); 

                            }
                        }, function(error){
                            console.log("in error",error)
                        });

        };

}]);
