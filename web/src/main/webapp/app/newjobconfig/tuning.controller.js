/* jobwidget controller */
'use strict';
angular.module('jobwidget.ctrl')

.controller('TuningController', ['$scope', '$http', '$rootScope', 'common', '$location', '$timeout', 'analyzeOptimizedJob', 'getOtimizedYarn',

function($scope, $http, $rootScope, common, $location, $timeout, analyzeOptimizedJob, getOtimizedYarn) {
	/** Tuning Functionality Start */
	$scope.myFile                               = '';
	$scope.tuningTab                            = {};
	$scope.tuningTab.allJobInfo                 = [];
	$scope.tuningTab.allJobInfo[0]              = {};
	$scope.finalObjectData                      = {};
	$scope.filePath                             = {};
	$scope.saveSuccess                          = false;
	$scope.methodTuning                         = true;
	//$scope.tuningTab.cboRadio                 = true;
	$scope.tuningTab.hideTabs                   = {};
	$scope.tuningTab.quickRecommendationsRadio  = 'TRUE';
	$scope.tuningTab.standardHadoopExampleRadio = 'TRUE';
	$scope.tuningTab.manualRadio                = "CALCULATE_INTERNALLY";
	$scope.tuningTab.definedInFsRadio           = false;
	$scope.tuningTab.yarnRadioNonYarnRadio      = true;
	$scope.tuningTab.systemType                 = 'TRUE';
	$scope.tuningTab.jarManifest                = false;
	$scope.tuningTab.classDisable               = false;
	$scope.tuningTab.jobTypeOpn                 = "";
	$scope.tuningTab.standardHadoopSelect       = "";
	$scope.tuningTab.schedule                   = false;
	$scope.tuningTab.tuningScheduledTime        = "";
	$scope.tuningTab.enbTuningChk               = 'TRUE';
	$scope.tuningTab.errorMessageShowJob        = false;
	$scope.tuningTab.errorMessageShowFile       = false;
	$scope.tuningTab.errorFile                  = false;
	$scope.tuningTab.errorTuningScheduled       = false;
	$scope.tuningTab.checkYarn                  = "Yarn";
	$scope.getListJob                           = common.getNewJobDetail();
	$scope.tuningTab.standardHadoopList         = ["PI", "WORDCOUNT", "TERAGEN", "TERASORT", "RANDOMWRITER"]
	$scope.tuningTab.jobTypeOpnList = [
		{
			label: "Job is analyzing huge data set", 
			value: "READ_INTENSIVE"
		}, 
		{
			label: "Job is computation intensive", 
			value: "EXECUTION_INTENSIVE"
		}, 
		{
			label: "Job intended to produce huge data set", 
			value: "WRITE_INTENSIVE"
		}, 
		{
			label: "Job is read-write intensive", 
			value: "READ_WRITE_INTENSIVE"
		}, 
		{
			label: "Data set contains varying sized files", 
			value: "VARYING_SIZED_FILE_READ_INTENSIVE"
		}, 
		{
			label: "Data set contains small sized files", 
			value: "SMALL_FILES_READ_INTENSIVE"
		}, 
		{       
			label: "Can not be classified in above", 
			value: "CANT_BE_CLASSIFIED_IN_ABOVE"
		}];
	/** Selected Tab List Start */
	$scope.tuningTab.init = function() {
		/*getOtimizedYarn.getYarn(
				//{requestType : 'DELETE'clusterName : 'key'}, 
				{clustername : $scope.getListJob.selCluster},
				{},
				function(data) {
			}, function(e) {
				console.log(e);
		});*/
		$("td.my-tool-tip").tooltip();
		getOtimizedYarn.getYarn({}, function(data) {
			$scope.tuningTab.checkYarn = data.hadoopType;
		}, function(e) {
			console.log(e);
		});
		var flag                = true;
		var activeDivId         = '';
		$scope.jobConfigMethods = common.getJobConfigMethod();
		$scope.getJob           = common.getJobDetails();
		$scope.jobAnalysis      = common.getDefineJobInfo();
		$scope.methodTuning     = $scope.jobConfigMethods[2].visible;
		$scope.methodTuningDiv  = true;
		$scope.showMethod(true, 'Tuning');
		$scope.isActiveTab      = 'Tuning';
		$scope.showMethod($scope.methodTuning, activeDivId);
		var searchModule        = $location.search().module;
		if (searchModule) { 
			/*$scope.tuningTab.jobId = null;
				//$scope.tuningTab.jobTypeOpn = $scope.recentJobResponse.clusterTuning.hadoopPerformance;
				//end
				//CBO
				$scope.tuningTab.scheduleDateTime = null;
				$scope.tuningTab.dataGrowthRate = null;
				$scope.tuningTab.standardHadoopSelect = null;
				$scope.tuningTab.maximumTime = null;
				$scope.tuningTab.vCoreYarn = null;
				$scope.tuningTab.yarnMemory = null;
				$scope.tuningTab.filePathServer = null;*/
			//$scope.tuningTab.jobTypeOpn = $scope.recentJobResponse.clusterTuning.hadoopPerformance;
		} else {
			$scope.autoFillNextTunning = { optimizeJob: $scope.tuningTab.tunningAutoFill() } 
		}
	};
	$scope.tuningTab.tunningAutoFill = function() {
		$scope.recentJobResponse = common.getResonseData();
		if ($scope.recentJobResponse.clusterTuning.quickTuning == 'TRUE') {
			$scope.tuningTab.quickRecommendationsRadio = "FALSE"
		} else {
			//nothing
			//$scope.tuningTab.quickRecommendationsRadio = "TRUE"
		}
		if ($scope.recentJobResponse.clusterTuning.capabilityPerNode === 'MANUAL') {
			$scope.tuningTab.manualRadio = 'MANUAL';
		} else if ($scope.recentJobResponse.clusterTuning.capabilityPerNode === 'FAIRSCHEDULER') {
			$scope.tuningTab.manualRadio = 'FAIRSCHEDULER';
		} else if ($scope.recentJobResponse.clusterTuning.capabilityPerNode === 'CALCULATE_INTERNALLY') {
			$scope.tuningTab.manualRadio = 'CALCULATE_INTERNALLY';
		} else {
			//nothing
		}
		if ($scope.recentJobResponse.clusterTuning.useStandardHadoopExampleJar == 'TRUE') {
			$scope.tuningTab.standardHadoopExampleRadio = "TRUE";
		} else if ($scope.recentJobResponse.clusterTuning.useStandardHadoopExampleJar == 'FALSE') {
			$scope.tuningTab.standardHadoopExampleRadio = "FALSE";
			//$scope.tuningTab.manualRadio = $scope.recentJobResponse.clusterTuning.capabilityPerNode
		} else {}
		if ($scope.recentJobResponse.isLocalSystemJar == 'TRUE') {
			$scope.tuningTab.systemType = "TRUE";
			//$scope.tuningTab.manualRadio = $scope.recentJobResponse.clusterTuning.capabilityPerNode
		} else if ($scope.recentJobResponse.isLocalSystemJar == 'FALSE') {
			$scope.tuningTab.systemType = "FALSE";
			//$scope.tuningTab.manualRadio = $scope.recentJobResponse.clusterTuning.capabilityPerNode
		}

		if ($scope.recentJobResponse.includeClassJar == 'TRUE') {
			$scope.tuningTab.jarManifest = true;
			//$scope.DefineJobInfoController.jarManifest = false;
		} else {
			//nothing
			//$scope.tuningTab.quickRecommendationsRadio = "TRUE"
		}

		$scope.tuningTab.jobId      = $scope.recentJobResponse.clusterTuning.quickTuningJobID;
		$scope.tuningTab.jobTypeOpn = $scope.recentJobResponse.clusterTuning.hadoopPerformance;
		var tuningScheduledTime    = $scope.recentJobResponse.tuningScheduledTime;

		if (tuningScheduledTime != null && tuningScheduledTime.trim().length != 0) {
			$scope.tuningTab.tuningScheduledTime = tuningScheduledTime;
			$scope.tuningTab.schedule             = true;
		}

		$scope.tuningTab.standardHadoopSelect = $scope.recentJobResponse.clusterTuning.examples;
		$scope.tuningTab.maximumTime          = $scope.recentJobResponse.clusterTuning.maxTuningTime;
		if ($scope.recentJobResponse.clusterTuning.vCore > 0) {
			$scope.tuningTab.vCoreYarn        = $scope.recentJobResponse.clusterTuning.vCore;
		}
		if ($scope.recentJobResponse.clusterTuning.totalMemory > 0) {
			$scope.tuningTab.yarnMemory       = $scope.recentJobResponse.clusterTuning.totalMemory;
		}
		if ($scope.recentJobResponse.clusterTuning.dataGrowthRate > 0) {
			$scope.tuningTab.dataGrowthRate   = $scope.recentJobResponse.clusterTuning.dataGrowthRate;
		}
		
		$scope.tuningTab.allJobInfo           = $scope.recentJobResponse.jobs;
		$scope.tuningTab.filePathServer       = $scope.recentJobResponse.inputFile;
		$scope.myFile                         = $scope.recentJobResponse.inputFile;
		$scope.tuningTab.jobName              = $scope.recentJobResponse.operatingUser;
	}
	$scope.tuningTab.hideMethod = function() {
		//$scope.tuningTab.hideTabs = true;
	};
	$scope.tuningTab.setClassJarMani = function(checked) {
		if (checked) {
			$scope.tuningTab.jarManifest  = true;
			$scope.tuningTab.classDisable = true;
		} else {
			$scope.tuningTab.jarManifest  = false;
			$scope.tuningTab.classDisable = false;
		}
	};
	$scope.tuningTab.setJobCount = function() {
		$scope.tuningTab.fieldArray = [];
		/*for(var i=1; i<=self.noOfJobs; i++){
			self.fieldArray.push(i);
		}*/
	};
	//$scope.tuningTab.
	$scope.showMethod = function(param1, activeDivId) {
		$scope.methodTuningDiv = param1;
		common.activeForm(activeDivId);
	};
	$scope.cancel = function() {
		$location.path('/index');
		/*$scope.val = common.getBackVal();
		if ($scope.val === 'job') {
			$location.path('/job-configuration');
		}

		if ($scope.val === 'datajob') {
			$location.path('/data-job-configuration');
		}*/
	};
	$scope.back = function() {
		$location.path('/add-optimized-job-configuration');
	};

	$scope.preview = function() {
		var filePath = common.getJobJarFile();
		var isJarManifestVar = 'FALSE'
		if ($scope.tuningTab.jarManifest) {
			isJarManifestVar = 'TRUE'
		}
		$scope.finalObjectData.getListJob = $scope.getListJob;
		if ($scope.tuningTab.systemType == 'TRUE') {
			//if(Object.keys(filePath).length > 0)
			$scope.filePath = filePath;
		} else {
			$scope.filePath = $scope.tuningTab.filePathServer;
		}
		common.isFormsValid = false;
		var quickTuningVar = 'TRUE';
		if ($scope.tuningTab.quickRecommendationsRadio == 'TRUE' && $scope.tuningTab.standardHadoopExampleRadio == 'TRUE') {
			quickTuningVar = 'FALSE';
			var d = new Date();
			var browserGMT = (d.getTimezoneOffset() * (-1)) + "";
			/*----JobWidget Save----*/
			$scope.tuningTabJSON = {
				selfTuning           : $scope.tuningTab.enbTuningChk,
				operatingCluster     : $scope.finalObjectData.getListJob.selCluster,
				jumbuneJobName       : $scope.finalObjectData.getListJob.jobName,
				includeClassJar      : isJarManifestVar,
				jobs                 : $scope.tuningTab.allJobInfo,
				operatingUser        : $scope.tuningTab.jobName,
				slaveWorkingDirectory: "/home/impadmin/Desktop/uploaded/",
				schedule             : $scope.tuningTab.schedule,
				"clusterTuning"      : {
					quickTuning                : quickTuningVar,
					quickTuningJobID           : $scope.tuningTab.jobId,
					capabilityPerNode          : $scope.tuningTab.manualRadio,
					vCore                      : $scope.tuningTab.vCoreYarn,
					totalMemory                : $scope.tuningTab.yarnMemory,
					availableMapTasks          : $scope.tuningTab.mapSlotsNY,
					availableReduceTasks       : $scope.tuningTab.reduceSlotsNY,
					useStandardHadoopExampleJar: $scope.tuningTab.standardHadoopExampleRadio,
					examples                   : $scope.tuningTab.standardHadoopSelect,
					jobInputPath               : $scope.tuningTab.inputDataPath,
					outputFolder               : $scope.tuningTab.outputDataPath,
					hadoopPerformance          : $scope.tuningTab.jobTypeOpn,
					maxTuningTime              : $scope.tuningTab.maximumTime,
					"browserGMT"               : browserGMT
				}
			};
			if ($scope.tuningTab.dataGrowthRate) {
				$scope.tuningTabJSON.clusterTuning['dataGrowthRate'] = $scope.tuningTab.dataGrowthRate;
			}
			if ($scope.tuningTab.schedule == true) {
				$scope.tuningTabJSON['tuningScheduledTime'] = $scope.tuningTab.tuningScheduledTime;
			}
		} else if ($scope.tuningTab.quickRecommendationsRadio == 'TRUE' && $scope.tuningTab.standardHadoopExampleRadio == 'FALSE') {
			quickTuningVar       = 'FALSE';
			var d                = new Date();
			var browserGMT       = (d.getTimezoneOffset() * (-1)) + "";
			$scope.tuningTabJSON = {
				selfTuning           : $scope.tuningTab.enbTuningChk,
				operatingCluster     : $scope.finalObjectData.getListJob.selCluster,
				jumbuneJobName       : $scope.finalObjectData.getListJob.jobName,
				inputFile            : $scope.tuningTab.filePathServer,
				includeClassJar      : isJarManifestVar,
				isLocalSystemJar     : $scope.tuningTab.systemType,
				jobs                 : $scope.tuningTab.allJobInfo,
				operatingUser        : $scope.tuningTab.jobName,
				slaveWorkingDirectory: "/home/impadmin/Desktop/uploaded/",
				schedule             : $scope.tuningTab.schedule,
				"clusterTuning": {
					quickTuning                : quickTuningVar,
					quickTuningJobID           : $scope.tuningTab.jobId,
					capabilityPerNode          : $scope.tuningTab.manualRadio,
					vCore                      : $scope.tuningTab.vCoreYarn,
					totalMemory                : $scope.tuningTab.yarnMemory,
					availableMapTasks          : $scope.tuningTab.mapSlotsNY,
					availableReduceTasks       : $scope.tuningTab.reduceSlotsNY,
					useStandardHadoopExampleJar: $scope.tuningTab.standardHadoopExampleRadio,
					examples                   : $scope.tuningTab.standardHadoopSelect,
					jobInputPath               : $scope.tuningTab.inputDataPath,
					outputFolder               : $scope.tuningTab.outputDataPath,
					dataGrowthRate             : $scope.tuningTab.dataGrowthRate,
					hadoopPerformance          : $scope.tuningTab.jobTypeOpn,
					maxTuningTime              : $scope.tuningTab.maximumTime,
					"browserGMT"               : browserGMT
				}
			};
			if ($scope.tuningTab.schedule == true) {
				$scope.tuningTabJSON['tuningScheduledTime'] = $scope.tuningTab.tuningScheduledTime;
			}
		} else if ($scope.tuningTab.quickRecommendationsRadio == 'FALSE') {
			//quickTuningVar = 'TRUE'
			$scope.tuningTabJSON = {
				selfTuning      : $scope.tuningTab.enbTuningChk,
				operatingCluster: $scope.finalObjectData.getListJob.selCluster,
				jumbuneJobName  : $scope.finalObjectData.getListJob.jobName,
				"clusterTuning" : {
					quickTuning      : quickTuningVar,
					quickTuningJobID : $scope.tuningTab.jobId,
					hadoopPerformance: $scope.tuningTab.jobTypeOpn
				}
			};
		}
		/*----JobWidget Save----*/
		/*$scope.tuningTabJSON = {
			selfTuning : $scope.tuningTab.enbTuningChk,
			operatingCluster : $scope.finalObjectData.getListJob.selCluster,
			jumbuneJobName : $scope.finalObjectData.getListJob.jobName,
			//inputFile : $scope.filePath,
			includeClassJar : $scope.tuningTab.isJarManifest,
			isLocalSystemJar : $scope.tuningTab.systemType,
			jobs : $scope.tuningTab.allJobInfo,
			operatingUser :$scope.tuningTab.jobName,
			slaveWorkingDirectory: "/home/impadmin/Desktop/uploaded/",
			//selfTuning: true,
			schedule : $scope.tuningTab.schedule,
			tuningScheduledTime : $scope.tuningTab.scheduleDateTime,
			"clusterTuning": {
				quickTuning : quickTuningVar,
					quickTuningJobID : $scope.tuningTab.jobId,
					//cboRadio : $scope.tuningTab.cboRadio,
					//calculateInternallyRadio : $scope.tuningTab.calculateInternallyRadio,
					//manualRadio : $scope.tuningTab.manualRadio,
					capabilityPerNode : $scope.tuningTab.manualRadio,
					//isFairSchedulerEnabled : $scope.tuningTab.definedInFsRadio,
					//hadoopType : $scope.tuningTab.yarnRadioNonYarnRadio,
					//yarnRadio : $scope.tuningTab.yarnRadio,
					//nonYarnRadio : $scope.tuningTab.nonYarnRadio,
					vCore : $scope.tuningTab.vCoreYarn,
					memory : $scope.tuningTab.yarnMemory,
					availableMapTasks : $scope.tuningTab.mapSlotsNY,
					availableReduceTasks : $scope.tuningTab.reduceSlotsNY,
					useStandardHadoopExampleJar : $scope.tuningTab.standardHadoopExampleRadio,
					examples : $scope.tuningTab.standardHadoopSelect,
					//userDefinedRadio : $scope.tuningTab.userDefinedRadio,
					jobInputPath : $scope.tuningTab.inputDataPath,
					outputFolder : $scope.tuningTab.outputDataPath,
					dataGrowthRate : $scope.tuningTab.dataGrowthRate,
					hadoopPerformance : $scope.tuningTab.jobTypeOpn,
					//systemType : $scope.tuningTab.systemType,
					//jarManifest : $scope.tuningTab.jarManifest,
					//limitTuningTime : $scope.tuningTab.maximumTime
					maxTuningTime : $scope.tuningTab.maximumTime
			}
				
		};*/
		common.setWidgetInfo("tuningDtl", $scope.tuningTabJSON);
		common.setWidgetObject();
		common.setTunningFlag($scope.tuningTabJSON.clusterTuning.quickTuning)
			//common.createJobJson();
			//$location.path('/job-preview');
			//common.createJobJson();
			/*if($scope.tuningTab.enbTuningChk == true){
				common.setWidgetObject();
				//$location.path('/job-preview');
			}else{
				$scope.displayMsgBox('Failure',"Please fill alteast one form");
			}*/
			/*var object = angular.extend({}, $scope.tuningTabJSON)
					analyzeOptimizedJob.submitOptimizedJob({
					},object,
					function(data){
						console.log("Sucess data in post method for debghugger",data)
						$scope.displaySaveMsgBox('Success',"Cluster saved successfully!!!");
					},
					function(e){
						console.log(e);
					});*/
		var objectTest = { "name": "testName" };
		var jsonSmple  = angular.copy($scope.tuningTabJSON);
		var filenameis = $scope.filePath;
		var jsonDataIs = JSON.stringify($scope.tuningTabJSON);
		$scope.content = new FormData();
		$scope.content.append("inputFile", filePath);
		$scope.content.append("jsonData", jsonDataIs);
		if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
			$scope.$apply();
		}
		// Simple POST request example (passing data) :
		/*var req = {
							method: 'POST', 
							url:'apis/jobanalysis/save',
							headers: {'Content-Type': undefined },
							transformRequest: angular.identity,
							data:$scope.content,
						}

					if ($scope.tuningTabJSON.tuningScheduledTime==null || $scope.tuningTabJSON.tuningScheduledTime=="") {
						  $http(req).then(function(data){
						  common.setOptimizeJobName(data.data.JOB_NAME);
						  $location.path('/optimize-graph');
						}, function(error){
						  console.log("in error",error)
						});
					   } else {
						  $http(req).then(function(data){
						  common.setOptimizeJobName(data.data.JOB_NAME);
						  $location.path('/');
						}, function(error){
						  console.log("in error",error)
						});
						  
						}*/
		var containErrors = false;
		if ($scope.tuningTab.systemType == 'TRUE' && $scope.tuningTab.standardHadoopExampleRadio == 'FALSE') {
			var filePathLocalValue = document.getElementById("filePathLocal").value;
			if ( !filePathLocalValue || !(filePathLocalValue + '').endsWith('.jar') ) {
				$scope.tuningTab.errorFile = true;
				containErrors = true;
			}
		} else {
			$scope.tuningTab.errorFile = false;
		}
		if ($scope.tuningTab.schedule) {
			var tuningScheduledTime = $scope.tuningTab.tuningScheduledTime;
			if (tuningScheduledTime == null || tuningScheduledTime.trim().length == 0) {
				$scope.tuningTab.errorTuningScheduled = true;
				containErrors = true;
			} else {
				$scope.tuningTab.errorTuningScheduled = false;
			}
		} else {
			$scope.tuningTab.errorTuningScheduled = false;
		}
		/*if (containErrors) {
			return false;
		}*/
		if (containErrors) {
			return false;
		}
		var req = {
			method: 'POST',
			url: '/apis/validateservice/validatejobinput',
			headers: { 'Content-Type': undefined },
			transformRequest: angular.identity,
			data: $scope.content,
		};
		$scope.showLoader = true;
		$http(req).then(function(data) {
			$scope.showLoader = false;
			if (data.data.STATUS == "ERROR" && data.data.jobID) {
				$scope.tuningTab.errorMessageJob     = data.data.jobID;
				$scope.tuningTab.errorMessageShowJob = true;
				containErrors                        = true;
			} else if (data.data.STATUS == "ERROR" && data.data.filePath) {
				$scope.tuningTab.errorMessageFile     = data.data.filePath;
				$scope.tuningTab.errorMessageShowFile = true;
				containErrors                         = true;
			} else if (containErrors) {
				return false;
			} else {
				$scope.showLoader = true;
				var req = {
					method          : 'POST',
					url             : 'apis/jobanalysis/save',
					headers         : { 'Content-Type': undefined },
					transformRequest: angular.identity,
					data            : $scope.content,
				}
				if ($scope.tuningTabJSON.tuningScheduledTime == null || $scope.tuningTabJSON.tuningScheduledTime.trim().length == 0) {
					$http(req).then(function(data) {              
						common.setOptimizeJobName(data.data.JOB_NAME);              
						$location.path('/optimize-graph');
					}, function(error) {
						console.log("in error", error)
					});           
				} else {              
					$http(req).then(function(data) {              
						common.setOptimizeJobName(data.data.JOB_NAME);              
						$location.path('/');
					}, function(error) {
						console.log("in error", error)
					});                          
				}
			}
		}, function(error) {
			console.log("in error", error)
		});
	};
	$scope.displayMsgBox = function(type, messageString) {
			if (type == 'Success') {
				$scope.saveSuccess = true;
			}
			$scope.displayBlock = true;
			$scope.blockMessage = messageString;
			$timeout(function() {
				$scope.displayBlock = false;
				$scope.blockMessage = "";
			}, 3000);
		}
		/*$scope.tuningTab = {};
	$scope.tuningTab.enbTuningChk = false;
	$scope.tuningTab.resourceShareRadios = "fairScheduler";
	$scope.tuningTab.mapSlotsText = "";
	$scope.tuningTab.reduceSlotsText = "";
	$scope.tuningTab.mapReduceJarRadios="standardWordCount";
	$scope.tuningTab.jobTypeOpn = "";
	$scope.tuningTab.jobOutputChk = false;
	$scope.tuningTab.schedule = false;
	$scope.tuningTab.scheduleDateTime = "";
	$scope.tuningTab.jobTypeOpnList = [
			"Job is analyzing huge data set",
			"Job is computation intensive",
			"Job intended to produce huge data set",
			"Job is read-write intensive",
			"Data set contains varying sized files",
			"Data set contains small sized files",
			"Can't be classified in above"
		];*/
		// $scope.tuningTab.changeResourceShareRadios = function(){
		// 	if($scope.tuningTab.resourceShareRadios == 'fairScheduler'){
		// 		$scope.tuningTab.resourceShareRadios = 'manual';
		// 	}else{
		// 		$scope.tuningTab.resourceShareRadios = 'fairScheduler'
		// 	}
		// };
		/*$scope.tuningTab.showValidationErrors = function(){
		alert("Last Tunning");
	}
	
	$scope.tuningTab.isJobOutputChk = function(){
		$scope.tuningTab.jobOutputChk = !$scope.tuningTab.jobOutputChk;
	};
	
	$scope.tuningTab.isScheduleChecked = function(){
		$scope.tuningTab.schedule = !$scope.tuningTab.schedule;
	};*/
		/*	$scope.tuningTab.init1 = function () {
				showMethod(true, false, false, false, false, 'Tuning',false)
				if(common.jobMode === 'edit') {
					var jobwidget = common.getWidgetInfo();
					if(jobwidget && (jobwidget["tuningDtl"] !== undefined)) {
							$scope.tuningTab.enbTuningChk        = jobwidget["tuningDtl"].enableTuningChk;
							$scope.tuningTab.resourceShareRadios = jobwidget["tuningDtl"].resourceShareRadios?jobwidget["tuningDtl"].resourceShareRadios:'fairScheduler';
							$scope.tuningTab.mapSlotsText        =jobwidget["tuningDtl"].mapSlotsText ;
							$scope.tuningTab.reduceSlotsText     = jobwidget["tuningDtl"].reduceSlotsText;
							$scope.tuningTab.mapReduceJarRadios  = jobwidget["tuningDtl"].mapReduceJarRadios?jobwidget["tuningDtl"].mapReduceJarRadios:'standardWordCount';
							$scope.tuningTab.jobDataPathText     = jobwidget["tuningDtl"].jobDataPathText;
							$scope.tuningTab.outputFileText      = jobwidget["tuningDtl"].outputFileText;
							$scope.tuningTab.schedule            =jobwidget["tuningDtl"].schedule;
							$scope.tuningTab.scheduleDateTime    = jobwidget["tuningDtl"].scheduleDateTime;
							$scope.tuningTab.jobOutputChk        = jobwidget["tuningDtl"].jobOutputChk;
							$scope.tuningTab.jobTypeOpn          = jobwidget["tuningDtl"].jobTypeOpn;
					}
				}
			};*/
}]);

