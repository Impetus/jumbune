/* jobwidget controller */
//'use strict';
angular.module('jobwidget.ctrl', []).controller('JobWidgetController', ['$scope', '$http', '$rootScope','common', '$location','$timeout','analyzeJobFactory', 

	function($scope, $http, $rootScope, common, $location,$timeout,analyzeJobFactory) {
    
	/** Debugger Functionality Start */
	$scope.debuggerTab = {};
	
	/** WhatIF Tab Functionality Start */
	$scope.whatIfTab = {};
	
	/** Job Profiling Tab Functionality Start */
	$scope.jobProfilingTab = {};
	
	/** Data Validation Tab Functionality Start */
	$scope.dataValidationTab = {};
	$scope.filePath = {};
	$scope.showLoader = false;
	 $scope.unableUploadJarFileError = false;
    $scope.finalObjectData = {};
    $scope.filhPath = {};


	$scope.saveSuccess = false;

	$scope.previous = "";
	$scope.init = function () {
		var flag = true;
		var activeDivId = '';
		$scope.jobConfigMethods  = common.getJobConfigMethod();
		$scope.methodDebugger = true;
		$scope.methodJobProfiling = $scope.jobConfigMethods[1].visible;

		//code to show selected first job tab active
		angular.forEach($scope.jobConfigMethods, function(value,key) {
			if(value.visible && flag) {
				flag = false;
				activeDivId = value.name;
			}
		});

		if(activeDivId == 'Job Profiling')
			activeDivId = 'JobProfiling';

		$scope.isActiveTab = activeDivId;
		$scope.showMethod(true, false, 'Debugger');

	};

	$scope.showMethod = function (isDebugger, isJobProfilin, activeDivId) {
		if($scope.previous !== "" ){
			common.chkPrevFormIsValid($scope.previous, $scope);
			$scope.previous = activeDivId;
		}else{
			$scope.previous = activeDivId;
		}
		$scope.methodDebuggerDiv = isDebugger;
		$scope.methodJobProfilingDiv = isJobProfilin;
		
		
		common.activeForm(activeDivId);

	};
	
	$scope.cancel = function () {
		$location.path('/dashboard');
		
	};

	$scope.backDebugger = function () {
		$location.path('/define-job-info');
	};
	
	$scope.preview = function (tabName) {
		var jobAnalysis = common.getDefineJobInfo();
		var dataAnalysis = common.getWidgetInfo(); 
		var getJob = common.getJobDetails(); 
   		var getClusName = common.getSelectedClusterNameForRun();

   		var getjobPrev = common.getJobPreviewJson();
   		var getRegexValidations = common.getUdvRegexJson() ;
   		var getUserValidations = common.getRegexJson();
   		var test = common.getJobConfigMethod();
   		$scope.finalObjectData.jobAnalysis =jobAnalysis;
   		$scope.finalObjectData.getJob = getJob;
   		$scope.finalObjectData.getRegexValidations = getRegexValidations;
   		$scope.finalObjectData.getUserValidations = getUserValidations;
   		var filePath = common.getJobJarFile();
   		
		if ( typeof $scope.finalObjectData.jobAnalysis.inputFile != 'undefined' && $scope.finalObjectData.jobAnalysis.systemType != 'local' ){
			$scope.filePath = $scope.finalObjectData.jobAnalysis.inputFile;
        } else{
             $scope.filePath = filePath;
        }
		if($scope.previous != ''){
			common.chkPrevFormIsValid($scope.previous, $scope);
		}
			common.isFormsValid = false;
			/*----JobWidget Save----*/
				//To set TRUE or FALSE		
				var enableDebChkVar = profilingCheckVar = isJarManifestVar = enableLoggingOfUnmatchedVar = runFromJumbuneVar = 'FALSE';
				if($scope.debuggerTab.enableDebChk) enableDebChkVar = 'TRUE';
				if($scope.finalObjectData.jobAnalysis.isJarManifest) isJarManifestVar = 'TRUE';
				if($scope.debuggerTab.enableLoggingOfUnmatched) enableLoggingOfUnmatchedVar = 'TRUE';
				if($scope.jobProfilingTab.runFromJumbune) runFromJumbuneVar = 'TRUE';
				//End

				$scope.debuggerTabJSON = {
					debugAnalysis : 'TRUE',
					logKeyValues: enableLoggingOfUnmatchedVar,
					operatingCluster : $scope.finalObjectData.getJob.clusterName,
					jumbuneJobName : $scope.finalObjectData.getJob.jobName,
					tempDirectory: $scope.finalObjectData.getJob.tempDirectory,
					includeClassJar : isJarManifestVar,
					jobs : $scope.finalObjectData.jobAnalysis.allJobInfo,
					operatingUser : $scope.finalObjectData.jobAnalysis.jobName,
					userValidations : $scope.finalObjectData.getRegexValidations,
					regexValidations : 	$scope.finalObjectData.getUserValidations
				};
				if($scope.debuggerTabJSON.useRegexChk){
					if($scope.debuggerTab.useRegex.length >= 0){
						$scope.debuggerTabJSON.useRegex = $scope.debuggerTab.useRegex;
					}else{
						$scope.displayMsgBox('Failure',"Please add at least one regex");
						return false;
					}
				}
				if($scope.debuggerTabJSON.usrDefChk){
					if($scope.debuggerTab.udv.length >= 0){
						$scope.debuggerTabJSON.udv = $scope.debuggerTab.udv;
					}else{
						$scope.displayMsgBox('Failure',"Please add at least one defined validation");
						return false;
					}
				}
				common.setWidgetInfo("debuggerDtl", $scope.debuggerTabJSON);
				
				$scope.jobProfilingTabJSON ={
					runJobFromJumbune :runFromJumbuneVar,
					existingJobName : $scope.jobProfilingTab.existingJobName,
					operatingCluster : $scope.finalObjectData.getJob.clusterName,
					jumbuneJobName : $scope.finalObjectData.getJob.jobName,
					tempDirectory: $scope.finalObjectData.getJob.tempDirectory,
					inputFile : $scope.finalObjectData.jobAnalysis.inputFile,
					includeClassJar : isJarManifestVar,
					jobs : $scope.finalObjectData.jobAnalysis.allJobInfo,
					operatingUser : $scope.finalObjectData.jobAnalysis.jobName
				};
				common.setWidgetInfo("jobProfilingDtl", $scope.jobProfilingTabJSON);
				
				var object2 = angular.extend({}, $scope.jobProfilingTabJSON,$scope.debuggerTabJSON)

					//To submit json with file
					
				 	var jsonDataIs = JSON.stringify(object2);

				 	$scope.content = new FormData();
		            $scope.content.append("inputFile", filePath); 
		            $scope.content.append("jsonData", jsonDataIs);
		             
		            if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
		                $scope.$apply();
		            }
				 	 
				 	// Simple POST request example (passing data) :
					var req = {
							method: 'POST', 
				            url:'apis/jobanalysis/save',
				            headers: {'Content-Type': undefined },
				            transformRequest: angular.identity,
				            data:$scope.content,
						}
						$scope.showLoader = false;
						$http(req).then(function(data){
							if (data.data.STATUS == "ERROR") { 
                                 $scope.showLoader = false;
                                 $scope.unableUploadJarFileError = true;
                                return false;
                            } else {
								$scope.showLoader = true;
								common.setJobName(data.data.JOB_NAME);
								$location.path('/analyze-job');
							}
						}, function(error){
							console.log("in error",error)
						});
					//End
				common.createJobJson();
				if( $scope.debuggerTab.enableDebChk == true || $scope.whatIfTab.selectWhatIf == true || $scope.jobProfilingTab.profilingCheck == true || $scope.dataValidationTab.enableDataValidation == true){
					common.setWidgetObject();
				}else{
					$scope.displayMsgBox('Failure',"Please fill alteast one form");
				}
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
	}
}]);