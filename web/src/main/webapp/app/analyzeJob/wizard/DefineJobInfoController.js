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
						} else {
							this.errorFile = false;
						}
						if (containErrors) {
							return false;
						}
						var getJob = common.getJobDetails();
		var defineJobInfoData = {
	            systemType : self.systemType,
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
            } else {
                 $scope.autoFillJob = { analyzeJob: self.jobAutoFill()} 
            }
		

	};	
	this.jobAutoFill = function () {

				if ($scope.recentJobResponse.includeClassJar == 'TRUE') {
                    $scope.DefineJobInfoController.jarManifest = true;
                } else {
                	//nothing
                }
                $scope.DefineJobInfoController.filePathServer = $scope.recentJobResponse.inputFile;
                $scope.DefineJobInfoController.jobName = $scope.recentJobResponse.operatingUser;
                $scope.DefineJobInfoController.allJobInfo = $scope.recentJobResponse.jobs;

    }
	this.removeJobNode = function(jobIndex) {
		self.allJobInfo.splice(jobIndex,1);
		self.fieldArray.pop();
		self.noOfJobs = self.fieldArray.length;
	};
    
}]);
