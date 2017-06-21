/* Define Data Validation Controller */
'use strict';
angular.module('dataValidate.ctrl', []).controller('dataValidationController', ['$scope','$http', '$rootScope', 'common', '$location', 'DataValidationFactories','analyzeValidationFactory', function($scope, $http, $rootScope, common, $location, DataValidationFactories,analyzeValidationFactory){
    //alert("test")
		var onlyDdvPage = false;
		if(!$scope.dataValidationTab){
			onlyDdvPage = true;
			$scope.dataValidationTab = {};
		}
		
        $scope.dataValidationTab.enableDataValidation = "FALSE";
        $scope.dataValidationTab.nullCheckList = [];
        $scope.dataValidationTab.fieldTypeList = [];
        $scope.dataValidationTab.fields = [];
        $scope.dataValidationTab.fieldCount = 1;
		$scope.dataValidationTab.enableRowData = [];
        $scope.dataValidationTab.hdfsInputPathValidate = false;
        $scope.dataValidationTab.errorMessageShow = false;
        $scope.dataValidationTab.tupleRecordSeparatorValidate= false;
        $scope.dataValidationTab.tupleFieldSeparatorValidate= false;
        /*$scope.dataValidationTab.dataProfilingOperandArr = [
        {label:'must be null', value:'mustBeNull'},
        {label:'must not be null', value:'notNull'}];*/
        //$scope.dataValidationTab.fieldNumber = [];
         var clusList = common.getAnalyzeDataDetail();

        $scope.dataValidationTab.createFields = function() {
            $scope.dataValidationTab.fields = [];
            for(var i=0; i<$scope.dataValidationTab.fieldCount; i++){
                $scope.dataValidationTab.fields.push(new getDefaultField(i));
            }
            return false;
        };

        $scope.dataValidationTab.cancel = function () {
            //$location.path('/data-configuration');
            $location.path('/index');
        };
        
        $scope.dataValidationTab.back = function () {
            common.setJobDetailsFlagRes(true)
            common.setActiveTab('Data Validation');
            $location.path('/add-analyze-data-configuration');
        };
        $scope.dataValidationTab.isNextClicked = false;
        $scope.dataValidationTab.next = function () {
            var errorFound = false;
            $scope.dataValidationTab.isNextClicked = true;
            var msg = $rootScope.root.failMessageGeneric;
            $scope.dataValidationTab.enableRowData = [];
            $scope.dataValidationTab.fields.forEach(function(field) {
                /*if(field.enable && field.fieldNumber && (field.nullCheck=="" || field.dataType=="")){
                    errorFound = true;
                    msg = msg+"<br> -- Field row data is missing for enabled rows";
                }*/if(field.enable == true){
                    $scope.dataValidationTab.enableRowData.push(field);
                }
		        
            });
            var invalidForm = $scope.dataValidationTab.dataValidationForm.$invalid;
            if(errorFound || invalidForm){
                common.showMessage('failure', msg);
                return false;
            }

			var test = common.getfieldNumberDetail();
            var dataValidationDetails = {
		        //enableDataValidation : $scope.dataValidationTab.enableDataValidation,)
                //hdfsInputPath : $scope.dataValidationTab.hdfsInputPath,
                //fieldNumber : $scope.dataValidationTab.fieldNumber,
                recordSeparator : $scope.dataValidationTab.tupleRecordSeparator,
                fieldSeparator : $scope.dataValidationTab.tupleFieldSeparator,
                numOfFields : parseInt($scope.dataValidationTab.fieldCount),
                fieldValidationList : $scope.dataValidationTab.enableRowData,
                //operatingCluster : clusList.clusterName,
                //jumbuneJobName : clusList.jobName
            };

            var dataValidation1 = {
                //"dataValidation" : angular.copy(dataValidationDetails),
                "dataQualityTimeLineConfig" : {"dataValidation" : angular.copy(dataValidationDetails)},
                "operatingCluster" : angular.copy(clusList.operatingCluster),
                "operatingUser" : angular.copy(clusList.operatingUser),
                 //"enableDataValidation" : angular.copy($scope.dataValidationTab.enableDataValidation),
                 //"enableDataValidation" : "TRUE",
                 "enableDataValidation" : $scope.dataValidationTab.enableDataValidation,
                 "hdfsInputPath"  : angular.copy(clusList.hdfsInputPath),
                 "parameters" : angular.copy(clusList.parameters),
                "jumbuneJobName" : angular.copy(clusList.jumbuneJobName)
            }
			//common.setWidgetInfo('dataValidation', dataValidationDetails);
            common.setWidgetInfo('dataValidation', dataValidation1);
			if(onlyDdvPage) {
				//$location.path('/data-analysis-preview');
			}else {
				return true;
			}
            var mergedObject = angular.extend({} ,dataValidation1);
            common.setDataValFlag(mergedObject.enableDataValidation);
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
                                $scope.dataValidationTab.errorMessage = data.data.hdfsInputPath;
                                $scope.dataValidationTab.errorMessageShow = true;
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
                                    $location.path('/analyze-data');
                                }, function(error){
                                    console.log("in error",error)
                                }); 
                            }
                        }, function(error){
                            console.log("in error",error)
                        });

        };

        $scope.dataValidationTab.init = function() {
        	
            $scope.recentJobResponse = common.getResonseData();
            
            var fullObj = angular.copy(common.widgetData);
            var validationDataObj = angular.copy(fullObj.dataValidation);
            //code to repopulated data validationdata
            if(common.jobMode === 'edit') {
                if(typeof validationDataObj !== undefined) {
                   // $scope.dataValidationTab = validationDataObj;
                    $scope.dataValidationTab.fields = validationDataObj.enableRowData;
                    $scope.dataValidationTab.fieldCount = validationDataObj.enableRowData.length;
                }
            } else {
                $scope.dataValidationTab.fields.push(new getDefaultField(0));
            }
            $scope.dataValidationTab.nullCheckList = [
        {label:'must be null', value:'mustBeNull'},
        {label:'must not be null', value:'notNull'}];
            //$scope.dataValidationTab.fieldTypeList = ['int','char','float','double']
            $scope.dataValidationTab.fieldTypeList = [
        {label:'int', value:'int_type'},
        {label:'long', value:'long_type'},
        {label:'float', value:'float_type'},
        {label:'double', value:'double_type'}];
       // if ($scope.recentJobResponse.enableDataValidation == 'TRUE') {

            
        //}
            var searchModule = $location.search().module;
 
           if(searchModule) { 
                /* $scope.dataValidationTab.hdfsInputPath = null;
                $scope.dataValidationTab.tupleRecordSeparator = null;
                $scope.dataValidationTab.tupleFieldSeparator = null;
                $scope.dataValidationTab.fieldCount = null;
                $scope.dataValidationTab.fields = null;*/
                
            } else {
                 $scope.autoFillValidation = { analyzeData: $scope.dataValidationAutoFill()} 
            }

            
        };

        $scope.dataValidationAutoFill = function () {
                $scope.recentJobResponse = common.getResonseData();
                var responseTRS = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.recordSeparator;
		var responseTuppleRS = JSON.stringify(responseTRS)
                $scope.dataValidationTab.hdfsInputPath = $scope.recentJobResponse.hdfsInputPath;
                $scope.dataValidationTab.parameters = $scope.recentJobResponse.parameters;
                $scope.dataValidationTab.tupleRecordSeparator =  responseTuppleRS.split("\"")[1];
                $scope.dataValidationTab.tupleFieldSeparator = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.fieldSeparator;
                $scope.dataValidationTab.fieldCount = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.numOfFields;
                $scope.dataValidationTab.fields = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.fieldValidationList; 
        }

        $scope.dataValidationTab.invalidForm = function() {
            return false;
        };

        $scope.dataValidationTab.hasError = function(fieldName) {
            var error =  $scope.dataValidationTab.dataValidationForm[fieldName].$invalid && !$scope.dataValidationTab.dataValidationForm[fieldName].$pristine || ($scope.dataValidationTab.dataValidationForm[fieldName].$invalid && $scope.dataValidationTab.isNextClicked);
            return error;
        };
        $scope.dataValidationTab.handleErrorMessages = function(){
            $scope.dataValidationTab.CheckhdfsInputPath();
            $scope.dataValidationTab.ChecktupleRecordSeparator();
            $scope.dataValidationTab.ChecktupleFieldSeparator();

        }
        $scope.dataValidationTab.CheckhdfsInputPath = function(){
            if($scope.dataValidationTab.hdfsInputPath == "" || $scope.dataValidationTab.hdfsInputPath == undefined)
            {
                $scope.dataValidationTab.hdfsInputPathValidate = true;
            }else{
                $scope.dataValidationTab.hdfsInputPathValidate = false;
            }
        }
        $scope.dataValidationTab.ChecktupleRecordSeparator = function(){
            if($scope.dataValidationTab.tupleRecordSeparator == "" || $scope.dataValidationTab.tupleRecordSeparator == undefined)
            {
                $scope.dataValidationTab.tupleRecordSeparatorValidate = true;
            }else{
                $scope.dataValidationTab.tupleRecordSeparatorValidate = false;
            }
        }
        $scope.dataValidationTab.ChecktupleFieldSeparator = function(){
            if($scope.dataValidationTab.tupleFieldSeparator == "" || $scope.dataValidationTab.tupleFieldSeparator == undefined)
            {
                $scope.dataValidationTab.tupleFieldSeparatorValidate = true;
            }else{
                $scope.dataValidationTab.tupleFieldSeparatorValidate = false;
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
