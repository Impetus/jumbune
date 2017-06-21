/* Define Job Info controller */
'use strict';
angular.module('defineJobInfo.ctrl', [])
    
.controller('DefineJobInfoController', ['$scope', '$rootScope','common', '$location','$http', function ($scope, $rootScope, common, $location,$http) {

	var self = this;
	this.noOfJobs = 1;
	this.cancel = function () {
		$location.path('/add-new-job-configuration');
	};
	this.allJobInfo = [];
	this.submitJobInfo = false;
	this.systemType = 'local';
	this.showError = false;
	this.jarManifest = false;
	this.errorFile = false;
	this.errorMessageShowFile = false;
	this.errorMessageFile = '';
	this.next = function () {
		
		var jobAnalysis = common.getJobAnalysis();
		var dataAnalysis = common.getDataAnalysis();
		self.addJar =  common.getAddJarVal();	
		
		if(self.systemType != 'local') {
			var filePathValue = self.filePathServer;
		}
		var containErrors = false;
						if (this.systemType == 'local') {
							var filePathLocalValue = document.getElementById("filePathLocal").value;
							if ( !filePathLocalValue || !(filePathLocalValue + '').endsWith('.jar') ) {
								this.errorFile = true;
								containErrors = true;
							}
						}
						/*if ( this.systemType == 'local' && !document.getElementById("filePathLocal").value ){
							this.errorFile = true;
							containErrors = true;
						}*/ else {
							this.errorFile = false;
						}
						if (containErrors) {
							return false;
						}
						var getJob = common.getJobDetails();
		var defineJobInfoData = {
	            systemType : self.systemType,
	            //filePathServer : self.filePathServer,
	            inputFile: filePathValue,
	            addJar: self.addJar,
	            noOfJobs: self.noOfJobs,
	            allJobInfo: self.allJobInfo,
	            jobName: self.jobName,
		    	isJarManifest: self.jarManifest,
		    	operatingCluster : getJob.clusterName
		};

		var jsonSmple = angular.copy(defineJobInfoData);
				 	var filenameis = $scope.filePath;
					var jsonDataIs = JSON.stringify(defineJobInfoData);
				 	$scope.content = new FormData();
		            //$scope.content.append("inputFile", filePathValue); 
		            $scope.content.append("jsonData", jsonDataIs);
		             
		            if ($scope.$root.$$phase != '$apply' && $scope.$root.$$phase != '$digest') {
		                $scope.$apply();
		            }
		            var req = {
                            method: 'POST', 
                            url:'/apis/validateservice/validatejobinput',
                            headers: {'Content-Type': undefined },
                            transformRequest: angular.identity,
                            data:$scope.content,
                        };
                        self.showLoader = true;

                        $http(req).then(function(data){
                        	self.showLoader = false;
                            if(data.data.STATUS=="ERROR" && data.data.filePath) {
                                self.errorMessageFile = data.data.filePath;
                                self.errorMessageShowFile = true;
                            } else {
                            	self.showLoader = true;
                            		var defineJobInfoData = {
							            systemType : self.systemType,
							            //filePathServer : self.filePathServer,
							            inputFile: filePathValue,
							            addJar: self.addJar,
							            noOfJobs: self.noOfJobs,
							            allJobInfo: self.allJobInfo,
							            jobName: self.jobName,
								    	isJarManifest: self.jarManifest
								};
                            	common.saveDefineJobInfo(defineJobInfoData);
								$location.path('/job-widget');
                            }
                        }, function(error){
                            console.log("in error",error)
                        });
		//common.setBackVal('datajob');
		/*if(common.getDDV() || common.findSelectedTabs()){
			$location.path('/job-widget');	
		}else{
			$location.path('/job-preview');
		}*/
/*
		self.submitJobInfo = true;
		self.fullFilePathLocal = angular.element('#filePathLocal').val();
		var msg = $rootScope.root.failMessageGeneric;
		if(self.jobInfoForm.$invalid){
			common.showMessage('failure', msg);
			return false;
		}
		if(self.systemType === 'local' && (angular.isUndefined(self.fullFilePathLocal) ||self.fullFilePathLocal == "")){
			self.showError = true;
			return false;		
		}
		var jobAnalysis = common.getJobAnalysis();
		var dataAnalysis = common.getDataAnalysis();
		self.addJar =  common.getAddJarVal();
		var defineJobInfoData = {
	            systemType : self.systemType,
	            filePathServer : self.filePathServer,
	            filePathLocal: self.fullFilePathLocal,
	            addJar: self.addJar,
	            noOfJobs: self.noOfJobs,
	            allJobInfo: self.allJobInfo,
		    isJarManifest: self.jarManifest
		};
		console.log('Final',defineJobInfoData);
		common.saveDefineJobInfo(defineJobInfoData);
		$location.path('/data-job-configuration');*/

	};
	
	this.classDisable = false;
	this.setClassJarMani = function (checked) {
		if (checked) {
			self.jarManifest = true;
			self.classDisable = true;
		} else {
			self.jarManifest = false;
			self.classDisable = false;
		}
	};

	this.setJobCount = function () {
		self.fieldArray = [];
        for(var i=1; i<=self.noOfJobs; i++){
            self.fieldArray.push(i);
        }
	};

	this.hasError = function(fieldName) {
		var error =  (self.jobInfoForm[fieldName].$invalid && !self.jobInfoForm[fieldName].$pristine) || (self.jobInfoForm[fieldName].$invalid && self.submitJobInfo);
		return error;
	};
	
	this.init = function(){	
		$("div.my-tool-tip").tooltip();
		$("td.my-tool-tip").tooltip();
		 $scope.recentJobResponse = common.getResonseData();
			var defineJobInfo = common.getDefineJobInfo();
			if(defineJobInfo !== null){
				if(Object.getOwnPropertyNames(defineJobInfo).length > 0) {
					self.systemType  = defineJobInfo.systemType;
					//self.filePathServer = defineJobInfo.filePathServer;
					self.filePathLocal= defineJobInfo.filePathLocal;
					if(self.systemType === 'local') {
						$("#filePathLocal").val(self.filePathLocal);
					}
					self.addJar= defineJobInfo.addJar;
					self.noOfJobs= defineJobInfo.noOfJobs;
					self.allJobInfo= defineJobInfo.allJobInfo;
					self.jarManifest = defineJobInfo.jarManifest;
				}		
			}
		self.setJobCount();
            var searchModule = $location.search().module;

           if(searchModule) { 
                /*$scope.DefineJobInfoController.systemType = null;
                $scope.DefineJobInfoController.filePathServer = null;
                $scope.DefineJobInfoController.jarManifest = null;
                $scope.DefineJobInfoController.jobName = null;
                $scope.DefineJobInfoController.allJobInfo = null;*/
                
            } else {
                 $scope.autoFillJob = { analyzeJob: self.jobAutoFill()} 
            }
		

	};	
	this.jobAutoFill = function () {

				if ($scope.recentJobResponse.includeClassJar == 'TRUE') {
                    $scope.DefineJobInfoController.jarManifest = true;
                    //$scope.DefineJobInfoController.jarManifest = false;
                } else {
                	//nothing
                	//$scope.tuningTab.quickRecommendationsRadio = "TRUE"
                }

                /*if ($scope.recentJobResponse.isLocalSystemJar == 'TRUE') {
                    $scope.DefineJobInfoController.systemType = "TRUE";
                    //$scope.tuningTab.manualRadio = $scope.recentJobResponse.clusterTuning.capabilityPerNode
                    
                } else if ($scope.recentJobResponse.isLocalSystemJar == 'FALSE') {
                    $scope.DefineJobInfoController.systemType = "FALSE";
                    //$scope.tuningTab.manualRadio = $scope.recentJobResponse.clusterTuning.capabilityPerNode
                   
                } else {

                }*/
                //$scope.DefineJobInfoController.systemType = $scope.recentJobResponse.runJobFromJumbune;
                $scope.DefineJobInfoController.filePathServer = $scope.recentJobResponse.inputFile;
                //$scope.DefineJobInfoController.jarManifest = $scope.recentJobResponse.includeClassJar;
                $scope.DefineJobInfoController.jobName = $scope.recentJobResponse.operatingUser;
                $scope.DefineJobInfoController.allJobInfo = $scope.recentJobResponse.jobs;
                //$scope.DefineJobInfoController.allJobInfo = $scope.recentJobResponse.jobs;

    }
	this.removeJobNode = function(jobIndex) {
		self.allJobInfo.splice(jobIndex,1);
		self.fieldArray.pop();
		self.noOfJobs = self.fieldArray.length;
	};
    
}]);
