/* Define Data Validation Controller */
'use strict';
angular.module('dataValidateXML.ctrl', []).controller('dataValidationControllerXML', ['$scope','$http', '$rootScope', 'common', '$location', 'DataValidationFactories','analyzeValidationFactory', 'getSchemaDataFactory',function($scope, $http, $rootScope, common, $location, DataValidationFactories,analyzeValidationFactory,getSchemaDataFactory){
  

         var jsonData = common.getdataValidationXMLData();
         $scope.JSONdata = jsonData.data;
         var clusList = common.getAnalyzeDataDetailXML();
        $scope.fieldValidationData = [];
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

        $scope.workerHostArr = [];
        
        var fieldProfilingRulesObj = {
                        "elementName":"",
                        "nullCheck": "",
                        "regex": ""

        };
        $scope.workerNodeHostArrCount = [angular.copy(fieldProfilingRulesObj)];
        
        
        $scope.addWorkerNodeHost = function(){
            if ($scope.workerNodeHostArrCount.length <= 9) {
                //$scope.workerNodeHostArrCount.push($scope.workerNodeHostArrCount.length+1);
                $scope.workerNodeHostArrCount.push(angular.copy(fieldProfilingRulesObj));
            } else {

            }
            
            
        };

        $scope.removeWorkerNodeHost = function(index){
            $scope.workerNodeHostArrCount.splice(index,1);

           /* if($scope.workerHostArr[index])
                $scope.workerHostArr.splice(index,1);*/
        };

        
        $scope.cancel = function () {
            $location.path('/index');
        };

        $scope.back = function () {

            $location.path('/add-analyze-data-configuration');
        };
        $scope.next = function () {
           /* var addThreadData = "schemaInput="+$scope.JSONdata+"&jumbuneJobName="+angular.copy(clusList.jobName);
            var req = {
                            method: 'POST', 
                            url:'/apis/xmldvreport/saveSchema',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded'  },
                            isArray: false,
                            data:addThreadData,
                        };

                        $http(req).then(function(data){
                            if(data.data){
                              $location.path('/analyze-data-hdfs'); 
                            }
                        console.log("data",data)
                            
                        }, function(error){
                        }); */
                        if ($scope.workerNodeHostArrCount.length==1){
                            var obj = $scope.workerNodeHostArrCount[0];
                            var elementName = obj['elementName'];
                            var nullCheck = obj['nullCheck'];
                            var regex = obj['regex'];
                            if ( (elementName == null || elementName == "")
                                && (nullCheck == null || nullCheck == "")
                                && (regex == null || regex == "")) {
                                $scope.workerNodeHostArrCount = angular.copy([]);
                            }

                        }
            var xmlDataValidation = {
                xmlElementBeanList : angular.copy($scope.workerNodeHostArrCount),
                "operatingCluster" : angular.copy(clusList.selectedCluster),
                "operatingUser" : angular.copy(clusList.jobSubmissionUser),
                "enableXmlDataValidation" : "TRUE",
                "jumbuneJobName" : angular.copy(clusList.jumbuneJobName),
                "hdfsInputPath"  : angular.copy(clusList.hdfsInputPath),
                "parameters"  : angular.copy(clusList.parameters)
            }
            common.setWidgetInfo('dataValidation', xmlDataValidation);
            var mergedObject = angular.extend({} ,xmlDataValidation);
            common.setXmlDataValFlag(mergedObject.enableXmlDataValidation);
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
                                            url:'apis/xmldvreport/updateSchema',
                                            headers: {'Content-Type': undefined },
                                            transformRequest: angular.identity,
                                            data:$scope.content,
                                        };

                                        $http(req).then(function(data){
                                            if(data.data == "true") {
                                                   var req = {
                                                    method: 'POST', 
                                                    url:'/apis/jobanalysis/save',
                                                    headers: {'Content-Type': undefined },
                                                    transformRequest: angular.identity,
                                                    data:$scope.content,
                                                };

                                                $http(req).then(function(data){
                                                    //if(data.data=="SUCCESS") {
                                                        common.setOptimizeJobName(data.data.JOB_NAME);
                                                       $location.path('/analyze-data-xml');
                                                   //}
                                                    
                                                }, function(error){
                                                    console.log("in error",error)
                                                }); 
                                            }
                                            
                                        }, function(error){
                                            console.log("in error",error)
                                        }); 

                             


                            }
                        }, function(error){
                            console.log("in error",error)
                        });

        };
/*        $scope.submit = function () {
               var xmlDataValidation = {

                "operatingCluster" : angular.copy(clusList.selectedCluster),
                "operatingUser" : angular.copy(clusList.jobSubmissionUser),
                "enableXmlDataValidation" : "TRUE",
                "jumbuneJobName" : angular.copy(clusList.jobName),
                "hdfsInputPath"  : $scope.hdfsInputPath
            }
            common.setWidgetInfo('dataValidation', xmlDataValidation);
            var mergedObject = angular.extend({} ,xmlDataValidation);
            common.setXmlDataValFlag(mergedObject.enableXmlDataValidation);
            common.setHdfsInputPath(mergedObject.hdfsInputPath);

           var jsonDataIs = JSON.stringify(mergedObject);
                    $scope.content = new FormData();
                    $scope.content.append("jsonData", jsonDataIs);
                    var req = {
                                    method: 'POST', 
                                    url:'/apis/jobanalysis/save',
                                    headers: {'Content-Type': undefined },
                                    transformRequest: angular.identity,
                                    data:$scope.content,
                                };

                                $http(req).then(function(data){
                                    console.log("in succes",data);
                                    common.setOptimizeJobName(data.data.JOB_NAME);
                                   $location.path('/analyze-data-xml');
                                }, function(error){
                                    console.log("in error",error)
                                }); 
        }*/

}]);
