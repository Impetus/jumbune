/* jobwidget controller */
//'use strict';
angular.module('jobwidget.ctrl', []).controller('JobWidgetController', ['$scope', '$http', '$rootScope','common', '$location','$timeout','analyzeJobFactory', 

	function($scope, $http, $rootScope, common, $location,$timeout,analyzeJobFactory) {
    
	/** Tuning Functionality Start */
	$scope.tuningTab = {};
	
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
	/** Selected Tab List Start */
	/* $scope.zkHostPortObj = {
                    "classname" : $scope.debuggerTab.reducerNameText,
                    "key" : $scope.debuggerTab.regexKeyText,
                    "value" : $scope.debuggerTab.regexValText
    };
    $scope.zkHostPortArr = [{
                    "classname" : $scope.debuggerTab.reducerNameText,
                    "key" : $scope.debuggerTab.regexKeyText,
                    "value" : $scope.debuggerTab.regexValText
    }];*/

    $scope.finalObjectData = {};
    $scope.filhPath = {};


	$scope.saveSuccess = false;

	$scope.previous = "";
	$scope.init = function () {
		var flag = true;
		var activeDivId = '';
		$scope.jobConfigMethods  = common.getJobConfigMethod();
		//$scope.methodDebugger = $scope.jobConfigMethods[0].visible;
		$scope.methodDebugger = true;
		$scope.methodJobProfiling = $scope.jobConfigMethods[1].visible;
		$scope.methodTuning = $scope.jobConfigMethods[2].visible;
		

		//code to repopulated data validationdata
		/*if(common.widgetData.datavalidation !== undefined) {
			$scope.dataValidationTab = angular.copy(common.widgetData.dataValidation);
			$scope.dataValidationTab.fields = angular.copy(common.widgetData.dataValidation.enableRowData)
		}*/

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
			
		//$scope.showMethod($scope.methodTuning, $scope.methodDebugger, $scope.methodWhatIf, $scope.methodJobProfiling, common.getDDV(), activeDivId);
		//$scope.showMethod($scope.methodDebugger, $scope.methodJobProfiling,$scope.methodTuning,activeDivId);
		$scope.showMethod(true, false, false, 'Debugger');

	};

	$scope.showMethod = function (isDebugger, isJobProfilin, isTuning, activeDivId) {
		if($scope.previous !== "" ){
			common.chkPrevFormIsValid($scope.previous, $scope);
			$scope.previous = activeDivId;
		}else{
			$scope.previous = activeDivId;
		}
		$scope.methodDebuggerDiv = isDebugger;
		$scope.methodJobProfilingDiv = isJobProfilin;
		$scope.methodTuningDiv = isTuning;
		
		
		common.activeForm(activeDivId);

	};
	
	$scope.cancel = function () {
		/*$scope.val = common.getBackVal();
		if ($scope.val === 'job') {
			$location.path('/job-configuration');
		}

		if ($scope.val === 'datajob') {
			$location.path('/data-job-configuration');
		}*/
		$location.path('/index');
		
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
   		//$scope.finalObjectData.serverJSON = serverJSON;
   		var filePath = common.getJobJarFile();
   		
		if ( typeof $scope.finalObjectData.jobAnalysis.inputFile != 'undefined' && $scope.finalObjectData.jobAnalysis.systemType != 'local' ){
			$scope.filePath = $scope.finalObjectData.jobAnalysis.inputFile;
        } else{
             $scope.filePath = filePath;
        }
		//return false;
		if($scope.previous != ''){
			common.chkPrevFormIsValid($scope.previous, $scope);
		}
	
		//if(common.getAllFormValid()){
			common.isFormsValid = false;
			/*----JobWidget Save----*/
				$scope.tuningTabJSON = {
					enableTuningChk : $scope.tuningTab.enbTuningChk,
					resourceShareRadios : $scope.tuningTab.resourceShareRadios,
					mapSlotsText : $scope.tuningTab.mapSlotsText,
					reduceSlotsText : $scope.tuningTab.reduceSlotsText,
					mapReduceJarRadios : $scope.tuningTab.mapReduceJarRadios,
					jobDataPathText : $scope.tuningTab.jobDataPathText,
					outputFileText : $scope.tuningTab.outputFileText,
					schedule : $scope.tuningTab.schedule,
					scheduleDateTime : $scope.tuningTab.scheduleDateTime,
					jobOutputChk : $scope.tuningTab.jobOutputChk,
					jobTypeOpn : $scope.tuningTab.jobTypeOpn,
					operatingCluster : $scope.finalObjectData.getJob.clusterName,
					jumbuneJobName : $scope.finalObjectData.getJob.jobName,
					quickRecommendationsRadio : $scope.tuningTab.quickRecommendationsRadio,
					cboRadio : $scope.tuningTab.cboRadio,
					calculateInternallyRadio : $scope.tuningTab.calculateInternallyRadio,
					manualRadio : $scope.tuningTab.manualRadio,
					definedInFsRadio : $scope.tuningTab.definedInFsRadio,
					yarnRadio : $scope.tuningTab.yarnRadio,
					nonYarnRadio : $scope.tuningTab.nonYarnRadio,
					standardHadoopExampleRadio : $scope.tuningTab.standardHadoopExampleRadio,
					userDefinedRadio : $scope.tuningTab.userDefinedRadio,
					inputDataPath : $scope.tuningTab.inputDataPath,
					outputDataPath : $scope.tuningTab.outputDataPath,
					dataGrowthRate : $scope.tuningTab.dataGrowthRate,
					systemType : $scope.tuningTab.systemType,
					jarManifest : $scope.tuningTab.jarManifest,
					maximumTime : $scope.tuningTab.maximumTime
				};
				common.setWidgetInfo("tuningDtl", $scope.tuningTabJSON);
				//To set TRUE or FALSE		
				var enableDebChkVar = profilingCheckVar = isJarManifestVar = enableLoggingOfUnmatchedVar = runFromJumbuneVar = 'FALSE';
				if($scope.debuggerTab.enableDebChk) enableDebChkVar = 'TRUE';					
				//if($scope.jobProfilingTab.profilingCheck) profilingCheckVar = 'TRUE';
				if($scope.finalObjectData.jobAnalysis.isJarManifest) isJarManifestVar = 'TRUE';
				if($scope.debuggerTab.enableLoggingOfUnmatched) enableLoggingOfUnmatchedVar = 'TRUE';
				if($scope.jobProfilingTab.runFromJumbune) runFromJumbuneVar = 'TRUE';
				//End

				$scope.debuggerTabJSON = {
					debugAnalysis : 'TRUE',
					//useRegexChk : $scope.debuggerTab.useRegexChk,
					//usrDefChk : $scope.debuggerTab.usrDefChk,
					logKeyValues: enableLoggingOfUnmatchedVar,
					operatingCluster : $scope.finalObjectData.getJob.clusterName,
					jumbuneJobName : $scope.finalObjectData.getJob.jobName,
					//inputFile : $scope.filhPath,
					includeClassJar : isJarManifestVar,
					//systemType : $scope.finalObjectData.jobAnalysis.systemType,
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
				//common.saveDefineJobInfo(defineJobInfoData);
				common.setWidgetInfo("debuggerDtl", $scope.debuggerTabJSON);
				
				$scope.jobProfilingTabJSON ={
					//enableStaticJobProfiling : profilingCheckVar,
					runJobFromJumbune :runFromJumbuneVar,
					existingJobName : $scope.jobProfilingTab.existingJobName,
					operatingCluster : $scope.finalObjectData.getJob.clusterName,
					jumbuneJobName : $scope.finalObjectData.getJob.jobName,
					inputFile : $scope.finalObjectData.jobAnalysis.inputFile,
					includeClassJar : isJarManifestVar,
					//systemType : $scope.finalObjectData.jobAnalysis.systemType,
					jobs : $scope.finalObjectData.jobAnalysis.allJobInfo,
					operatingUser : $scope.finalObjectData.jobAnalysis.jobName
				};
				common.setWidgetInfo("jobProfilingDtl", $scope.jobProfilingTabJSON);
				
				var object2 = angular.extend({}, $scope.jobProfilingTabJSON,$scope.debuggerTabJSON)
					/*analyzeJobFactory.submitAnalyzeJob({
                    },object2,
                    function(data){
                    	console.log("Sucess data in post method for debghugger",data)
                        $scope.displaySaveMsgBox('Success',"Cluster saved successfully!!!");
                    },
                    function(e){
                        console.log(e);
                    });*/
					

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
							$scope.showLoader = true;
							common.setOptimizeJobName(data.data.JOB_NAME);
							$location.path('/analyze-job');
						}, function(error){
							console.log("in error",error)
						});
					//End


				/*if(common.getDDV()){
					if(!$scope.dataValidationTab.next()) {
						return false;
					}
				}*/
				common.createJobJson();
				if($scope.tuningTab.enbTuningChk == true || $scope.debuggerTab.enableDebChk == true || $scope.whatIfTab.selectWhatIf == true || $scope.jobProfilingTab.profilingCheck == true || $scope.dataValidationTab.enableDataValidation == true){
					common.setWidgetObject();
					//$location.path('/job-preview');
				}else{
					$scope.displayMsgBox('Failure',"Please fill alteast one form");
				}
		//}else{
            //$scope.debuggerTab.handleErrorMessages();
            //$scope.whatIfTab.handleErrorMessages();
            //$scope.jobProfilingTab.handleErrorMessages();
            //$scope.dataValidationTab.handleErrorMessages();
            //$scope.displayMsgBox('Failure',"The job information is either invalid or incomplete. Please correct");
		//}
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