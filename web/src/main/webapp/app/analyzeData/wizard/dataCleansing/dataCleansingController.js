/* Define Data Validation Controller */
'use strict';
angular.module('dataCleansing.ctrl', []).controller('dataCleansingController', ['$scope','$http', '$rootScope', 'common', '$location', 'DataValidationFactories','analyzeValidationFactory', function($scope, $http, $rootScope, common, $location, DataValidationFactories,analyzeValidationFactory){

        //$scope.enableDataCleansing = "FALSE";
        $scope.enableDataCleansing = "TRUE";
        $scope.nullCheckList = [];
        $scope.fieldTypeList = [];
        $scope.fields = [];
        $scope.fieldCount = 1;
		$scope.enableRowData = [];
        $scope.hdfsInputPathValidate = false;
        $scope.errorMessageShow = false;
        $scope.tupleRecordSeparatorValidate= false;
        $scope.tupleFieldSeparatorValidate= false;
         var clusList = common.getAnalyzeDataDetail();

        $scope.createFields = function() {
            $scope.fields = [];
            for(var i=0; i<$scope.fieldCount; i++){
                $scope.fields.push(new getDefaultField(i));
            }
            return false;
        };

        $scope.cancel = function () {
            $location.path('/index');
        };

        $scope.back = function () {
            common.setJobDetailsFlagRes(true)
            common.setActiveTab('Data Cleansing');
            $location.path('/add-analyze-data-configuration');
        };
        $scope.isNextClicked = false;
        $scope.next = function () {
            var errorFound = false;
            $scope.isNextClicked = true;
            var msg = $rootScope.root.failMessageGeneric;
            $scope.fields.forEach(function(field) {
                if(field.enable == true){
		          $scope.enableRowData.push(field);
                }
            });
            var invalidForm = $scope.dataValidationForm.$invalid;
            if(errorFound || invalidForm){
                common.showMessage('failure', msg);
                return false;
            }
			var test = common.getfieldNumberDetail();
            var dataValidationDetails = {
                recordSeparator : $scope.tupleRecordSeparator,
                fieldSeparator : $scope.tupleFieldSeparator,
                numOfFields : parseInt($scope.fieldCount),
                fieldValidationList : $scope.enableRowData
            };
            var dataCleansingDetails = {
                dlcRootLocation: clusList.dlcRootLocation,
                cleanDataRootLocation: clusList.cleanDataRootLocation
            };
            var dataValidation1 = {
                "dataQualityTimeLineConfig" : {"dataValidation" : angular.copy(dataValidationDetails)},
                "operatingCluster" : angular.copy(clusList.operatingCluster),
                "operatingUser" : angular.copy(clusList.operatingUser),
                 "isDataCleansingEnabled" : $scope.enableDataCleansing,
                 "hdfsInputPath"  : angular.copy(clusList.hdfsInputPath),
                "jumbuneJobName" : angular.copy(clusList.jumbuneJobName),
                "parameters" : angular.copy(clusList.parameters),
                "dataCleansing" :  angular.copy(dataCleansingDetails)
            }
            common.setWidgetInfo('dataCleansing', dataValidation1);
            var mergedObject = angular.extend({} ,dataValidation1);
            common.setDataValFlag(mergedObject.enableDataCleansing);
            common.setHdfsInputPath(mergedObject.hdfsInputPath);

           var jsonDataIs = JSON.stringify(mergedObject);
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
                                    common.setOptimizeJobName(data.data.JOB_NAME);
                                    //$location.path('/analyze-data');
                                    $location.path('/define-analyzeData-cleansing')
                                }, function(error){
                                    console.log("in error",error)
                                }); 
                            }
                        }, function(error){
                            console.log("in error",error)
                        });

        };

        $scope.init = function() {        	
            $scope.recentJobResponse = common.getResonseData();
            var fullObj = angular.copy(common.widgetData);
            var validationDataObj = angular.copy(fullObj.dataValidation);
            //code to repopulated data validationdata
            if(common.jobMode === 'edit') {
                if(typeof validationDataObj !== undefined) {
                   // $scope = validationDataObj;
                    $scope.fields = validationDataObj.enableRowData;
                    $scope.fieldCount = validationDataObj.enableRowData.length;
                }
            } else {
                $scope.fields.push(new getDefaultField(0));
            }
            $scope.nullCheckList = [
        {label:'must be null', value:'mustBeNull'},
        {label:'must not be null', value:'notNull'}];
            $scope.fieldTypeList = [
        {label:'int', value:'int_type'},
        {label:'long', value:'long_type'},
        {label:'float', value:'float_type'},
        {label:'double', value:'double_type'}];
            var searchModule = $location.search().module;
 
           if(searchModule) { 
            } else {
                 $scope.autoFillValidation = { analyzeData: $scope.dataValidationAutoFill()} 
            }

            
        };

        $scope.dataValidationAutoFill = function () {
                $scope.recentJobResponse = common.getResonseData();
                var responseTRS = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.recordSeparator;
		var responseTuppleRS = JSON.stringify(responseTRS)
                $scope.hdfsInputPath = $scope.recentJobResponse.hdfsInputPath;
                $scope.tupleRecordSeparator =  responseTuppleRS.split("\"")[1];
                $scope.tupleFieldSeparator = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.fieldSeparator;
                $scope.fieldCount = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.numOfFields;
                $scope.fields = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.fieldValidationList; 
        }

        $scope.invalidForm = function() {
            return false;
        };

        $scope.hasError = function(fieldName) {
            var error =  $scope.dataValidationForm[fieldName].$invalid && !$scope.dataValidationForm[fieldName].$pristine || ($scope.dataValidationForm[fieldName].$invalid && $scope.isNextClicked);
            return error;
        };
        $scope.handleErrorMessages = function(){
            $scope.CheckhdfsInputPath();
            $scope.ChecktupleRecordSeparator();
            $scope.ChecktupleFieldSeparator();

        }
        $scope.CheckhdfsInputPath = function(){
            if($scope.hdfsInputPath == "" || $scope.hdfsInputPath == undefined)
            {
                $scope.hdfsInputPathValidate = true;
            }else{
                $scope.hdfsInputPathValidate = false;
            }
        }
        $scope.ChecktupleRecordSeparator = function(){
            if($scope.tupleRecordSeparator == "" || $scope.tupleRecordSeparator == undefined)
            {
                $scope.tupleRecordSeparatorValidate = true;
            }else{
                $scope.tupleRecordSeparatorValidate = false;
            }
        }
        $scope.ChecktupleFieldSeparator = function(){
            if($scope.tupleFieldSeparator == "" || $scope.tupleFieldSeparator == undefined)
            {
                $scope.tupleFieldSeparatorValidate = true;
            }else{
                $scope.tupleFieldSeparatorValidate = false;
            }
        }
        /* Private Methods-- */
        function getDefaultField(i) {
            this.enable = false;
            this.nullCheck = '';
            this.dataType = '';
            this.regex = '';
            this.fieldNumber = i+1;
        }
}]);
