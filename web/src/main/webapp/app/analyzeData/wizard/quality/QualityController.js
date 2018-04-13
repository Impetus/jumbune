/* Data Quality controller */
'use strict';
angular.module('quality.ctrl', [])

.controller('QualityController', ['$scope', '$http', '$rootScope', '$routeParams', '$location', 'common', '$timeout',

	function($scope, $http, $rootScope, $routeParams, $location, common, $timeout) {
		$scope.DqtForm = {};
		$scope.select = true;
		$scope.noOfFields = false;
		var sharedData = [];
		$scope.fieldArray = [];
		$scope.dqtObj = {};
		$scope.common = common;
		$scope.errorMessageShow = false;
		$scope.dayFlag = false;
		$scope.enableDataQualityTimeline = false;
		$scope.submitForm = false;
		var clusList = common.getAnalyzeDataDetail();
		$scope.nullCheckList = [];
        $scope.fieldTypeList = [];
        $scope.fields = [];
        $scope.fieldCount = 1;
		$scope.enableRowData = [];
		//var that = this;

	    $scope.isOpen = false;

	    $scope.openCalendar = function(e) {
	        e.preventDefault();
	        e.stopPropagation();

	        $scope.isOpen = true;
	    };

		$scope.dataProfilingOperandArr = [
			{ label: 'must be null', value: 'mustBeNull' },
			{ label: 'must not be null', value: 'notNull' }
		];
		$scope.dataTypeArr = [
			{ label: 'int', value: 'int_type' },
			{ label: 'long', value: 'long_type' },
			{ label: 'float', value: 'float_type' },
			{ label: 'double', value: 'double_type' }
		];

		$scope.dqtObj.scheduleJob = 'specifyTime';
		$scope.dqtObj.interval = "";
		$scope.dqtObj.time = "00:00:00";
		$scope.repeatOptions = {
			"MINUTE": [5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55],
			"HOURLY": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23],
			"DAILY": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30]
		}
		$scope.resetInterval = function() {
			$scope.dqtObj.interval = "";
			if ($scope.dqtObj.schedulingEvent == 'DAILY') {
				$scope.dqtObj.time = "00:00";
			}
		}

		var idClock = document.getElementById("clockID");
		$(idClock).clockpicker();

		$scope.fieldValidationRules = [];

		//$scope.dqtObj.scheduleJob = '';

		$scope.hasError = function(fieldName) {
			var error = ($scope.DqtForm[fieldName].$invalid && !$scope.DqtForm[fieldName].$pristine) || ($scope.DqtForm[fieldName].$invalid && $scope.submitForm);
			return error;
		};
		 $scope.generateFields = function() {
            $scope.fields = [];
            for(var i=0; i<$scope.fieldCount; i++){
                $scope.fields.push(new getDefaultField(i));
            }
            return false;
        };

		$scope.init = function() {
			var savedDqtObj = common.getSavedDQ();
/*
			if (savedDqtObj != null && savedDqtObj != undefined) {
				$scope.enableDataQualityTimeline = savedDqtObj.isEnableQT;
				$scope.dqtObj.nameDQ = savedDqtObj.name;
				if ($scope.dqtObj.scheduleJob != undefined || $scope.dqtObj.scheduleJob != null ) {
					$scope.dqtObj.scheduleJob = savedDqtObj.scheduleJob;
				}
				
				$scope.dqtObj.cronExpression = savedDqtObj.cronExpression;
				$scope.dqtObj.schedulingEvent = savedDqtObj.schedulingEvent;
				$scope.dqtObj.interval = savedDqtObj.interval;
				$scope.dqtObj.time = savedDqtObj.time;

				$scope.dqtObj.tupleRS = savedDqtObj.tupleRS;
				$scope.dqtObj.tupleFS = savedDqtObj.tupleFS;
				$scope.dqtObj.fields = savedDqtObj.noOfFields;
				$scope.dqtObj.isEnableRow = savedDqtObj.isEnableRow;
				$scope.dqtObj.regEx = savedDqtObj.regEx;
				$scope.dqtObj.nullCheck = savedDqtObj.nullCheck;
				$scope.dqtObj.fieldType = savedDqtObj.fieldType;

				$scope.noOfFields = true;

				if ($scope.enableDataQualityTimeline) {
					$scope.enable = !$scope.enableDataQualityTimeline;
					$scope.select = false;
				}
				common.setSavedDQ(null);
			}*/
			$scope.recentJobResponse = common.getResonseData();
            
            var fullObj = angular.copy(common.widgetData);
            var validationDataObj = angular.copy(fullObj.dataValidation);
            //code to repopulated data validationdata
            if(common.jobMode === 'edit') {
                if(typeof validationDataObj !== undefined) {
                   // $scope.dataValidationTab = validationDataObj;
                    $scope.fields = validationDataObj.enableRowData;
                    $scope.fieldCount = validationDataObj.enableRowData.length;
                }
            } else {
                $scope.fields.push(new getDefaultField(0));
            }
            $scope.nullCheckList = [
		        {label:'must be null', value:'mustBeNull'},
		        {label:'must not be null', value:'notNull'}];
		            //$scope.dataValidationTab.fieldTypeList = ['int','char','float','double']
		            $scope.fieldTypeList = [
		        {label:'int', value:'int_type'},
		        {label:'long', value:'long_type'},
		        {label:'float', value:'float_type'},
		        {label:'double', value:'double_type'}];
			var searchModule = $location.search().module;

			if (searchModule) { 
				
			} else {
				$scope.autoFillValidation = { analyzeData: $scope.dataScheduleAutoFill() } 
			}
		};


		$scope.save = function() {
			var d = new Date();
			var browserGMT = (d.getTimezoneOffset() * (-1)) + "";
            $scope.enableRowData = [];
            $scope.fields.forEach(function(field) {
                if(field.enable == true){
                    $scope.enableRowData.push(field);
                }
		        
            });
			var test = common.getfieldNumberDetail();
            var dataValidationDetails = {                
				"recordSeparator": $scope.dqtObj.tupleRS,
				"fieldSeparator": $scope.dqtObj.tupleFS,
				"numOfFields": parseInt($scope.fieldCount),
                "fieldValidationList" : $scope.enableRowData
            };

			var obj = {
				"dataQualityTimeLineConfig": {
					"dataValidation": angular.copy(dataValidationDetails),
					"scheduleJob": $scope.dqtObj.scheduleJob,
					"cronExpression": $scope.dqtObj.cronExpression,
					"schedulingEvent": $scope.dqtObj.schedulingEvent,
					"interval": Number($scope.dqtObj.interval),
					"time": $scope.dqtObj.time,
					"browserGMT": browserGMT
				},
				"operatingCluster": clusList.clusterName,
				"operatingUser": clusList.jobSubUser,
				"enableDataQualityTimeline": "TRUE",
				"hdfsInputPath": clusList.hdfsInputPath,
				"tempDirectory" : angular.copy(clusList.tempDirectory),
                "parameters"  : angular.copy(clusList.parameters),
				"jumbuneJobName": clusList.jobName
			}

			common.setDQTFlag(obj.enableDataQualityTimeline);

			var jsonDataIs = JSON.stringify(obj);
			jsonDataIs = jsonDataIs.replace(/\\\\/g, '\\');
			$scope.content = new FormData();
			$scope.content.append("jsonData", jsonDataIs);

			var req = {
				method: 'POST',
				url: '/apis/validateservice/validatejobinput',
				headers: { 'Content-Type': undefined },
				transformRequest: angular.identity,
				data: $scope.content,
			};

			$http(req).then(function(data) {
				if (data.data.STATUS == "ERROR" && data.data.hdfsInputPath) {
					$scope.errorMessage = data.data.hdfsInputPath;
					$scope.errorMessageShow = true;
				} else {

					var req = {
						method: 'POST',
						url: 'apis/jobanalysis/save',
						headers: { 'Content-Type': undefined },
						transformRequest: angular.identity,
						data: $scope.content,
					};

					$http(req).then(function(data) {
						common.setJobName(data.data.JOB_NAME);
						$location.path('/analyze-data-quality');

					}, function(error) {
						console.log("in error", error)
					});
				}
			}, function(error) {
				console.log("in error", error)
			});

		};

		/*$scope.getObjectForJSON = function() {
			if ($scope.fieldValidationRules === undefined || $scope.fieldValidationRules.length == 0) {
				$scope.fieldValidationRulesArray = [];
			} else {
				$scope.fieldValidationRulesArray = $scope.fieldValidationRules
			}
			var getObject = {
				"enable": $scope.dqtObj.isEnableRow,
				"fieldValidationList": $scope.fieldValidationRulesArray,
				"recordSeparator": $scope.dqtObj.tupleRS,
				"fieldSeparator": $scope.dqtObj.tupleFS,
				"numOfFields": parseInt($scope.dqtObj.fields)
			};
			return getObject;

		};*/
		$scope.dataScheduleAutoFill = function() {
			$scope.recentJobResponse = common.getResonseData();
			var responseTRS = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.recordSeparator;
			var responseTuppleRS = JSON.stringify(responseTRS)
			$scope.dqtObj.schedulingEvent = $scope.recentJobResponse.dataQualityTimeLineConfig.schedulingEvent;
			$scope.dqtObj.interval = $scope.recentJobResponse.dataQualityTimeLineConfig.interval;
			$scope.dqtObj.tupleRS =  responseTuppleRS.split("\"")[1];
			$scope.dqtObj.cronExpression =  $scope.recentJobResponse.dataQualityTimeLineConfig.cronExpression;
			/*if ($scope.recentJobResponse.dataQualityTimeLineConfig.scheduleJob == 'specifyTime') {
                    $scope.dqtObj.scheduleJob = 'specifyTime';
            } else {
            	 	$scope.dqtObj.scheduleJob = 'cronExpression';
            }*/

			$scope.dqtObj.scheduleJob = $scope.recentJobResponse.dataQualityTimeLineConfig.scheduleJob;
			$scope.dqtObj.tupleFS = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.fieldSeparator;
			
			/*$scope.dqtObj.fields = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.numOfFields;
            $scope.fieldValidationRules = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.fieldValidationList;*/
             $scope.fieldCount = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.numOfFields;
                $scope.fields = $scope.recentJobResponse.dataQualityTimeLineConfig.dataValidation.fieldValidationList; 
		}
		$scope.dqtObj.scheduleJob = 'specifyTime';
		$scope.dqtObj.isSpecifyTime = $scope.dqtObj.scheduleJob;

		$scope.specifyTime = function() {
			$scope.dqtObj.scheduleJob = $scope.dqtObj.isSpecifyTime;
		};

		$scope.cancel = function() {
			$location.path('/dashboard');
		};
		$scope.back = function() {
			common.setJobDetailsFlagRes(true)
            common.setActiveTab('Data Quality');
			$location.path('/add-analyze-data-configuration');
		};
/*		$scope.generateFields = function(fields) {
			$scope.fieldArray = [];
			$scope.noOfFields = true;
			for (var i = 0; i < fields; i++) {
				$scope.fieldArray.push(i);

				$scope.fieldValidationRules.push(angular.copy($scope.fieldValidationObj));
				$scope.fieldValidationRules[i]["fieldNumber"] = i + 1;
			}
			return $scope.fieldValidationRules;
		};*/
		$scope.checkAll = function() {
            $scope.fields.forEach(function(field) {
               field.enable = $scope.selectAll.checkboxes;
            });
        };  
		function getDefaultField(i) {
            this.enable;
            this.nullCheck = '';
            this.dataType = '';
            this.regex = '';
            this.fieldNumber = i+1;
        }
		$('#formDisableOneClick').one('click', function() {  
			$(this).attr('disabled','disabled');
		});
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
		};
	}
]);
