/* jobwidget controller */
'use strict';
angular.module('datajob.ctrl', [])
    
.controller('DataJobController', ['$scope', '$rootScope','common', '$location','$timeout', function($scope, $rootScope, common, $location,$timeout) {

	$scope.dataValidation = false;
	$scope.dataQuality = false;
	$scope.dataProfiler = false;
	$scope.enableNextByDQ = false;
	$scope.enableNextByDP = false;
    $scope.dataQualities = [];
	$scope.dataProfilers = [];
    $scope.dataQualityVal = "";
    $scope.dataProfilerVal = "";
	
	$scope.disableNextButton = function() {
		var isDisable = true;
		angular.forEach($scope.jobConfigMethods, function(value,key) {
			if(value.visible) {
				isDisable = false;
				return;
			}
		});
		if(isDisable) {
			/*if($scope.dataValidation || ($scope.dataQuality && $scope.dataQualityVal!="") || ($scope.dataProfiler && $scope.dataProfilerVal!=""))
				isDisable = false;*/
            /*if($scope.dataValidation){
                isDisable = false;
            }*/
            if($scope.dataQuality && ($scope.dataQualityVal=="" || $scope.dataQualityVal==null )){
                isDisable = true;
            }else if($scope.dataQuality && ($scope.dataQualityVal!="" || $scope.dataQualityVal!=null)){
                isDisable = false;
            }
            if($scope.dataProfiler && ($scope.dataProfilerVal=="" || $scope.dataProfilerVal==null)){
                isDisable = true;
            }else if($scope.dataProfiler && ($scope.dataProfilerVal!="" || $scope.dataProfilerVal!=null)){
                isDisable = false;
            }
		}
    	return isDisable;
    };

	$scope.cancel = function () {
		$location.path('/define-job-info');
	};
	
	$scope.goToJobWidget = function () {
		/*common.setBackVal('datajob');
		//common.setDDV($scope.dataValidation);
		if(common.getDDV() || common.findSelectedTabs()){
			$location.path('/job-widget');	
		}else{
			$location.path('/job-preview');
		}*/
	};

	$scope.init = function () {
		$scope.jobConfigMethods  = common.getJobConfigMethod();
		$scope.dataQualities = common.dataQualityNameArr;
		//$scope.dataValidation = common.getDDV();
		$scope.dataProfilers = common.dataProfilingNameArr;
	};

	$scope.getJobConfigMethod = function (index, checked) {
		common.setJobConfigMethod(index, checked); 
	};

	$scope.enableControl = function (checked, param) {
		/*if (param === 'ddv') {
			$scope.dataValidation = false;
			if (checked) {
				$scope.dataValidation = checked;
			}
		}*/

		if (param === 'dq') {
			$scope.dataQuality = true;
			if (checked) {
				$scope.enableNextByDQ = true;
				$scope.dataQuality = false;
			}else{
				$scope.enableNextByDQ = false;
			}
		}

		if (param === 'dp') {
			$scope.dataProfiler = true;
			if (checked) {
				$scope.enableNextByDP = true;
				$scope.dataProfiler = false;
			}else{
				$scope.enableNextByDP = false;
			}
		}

	};

	$scope.runReports = function () {
		alert('Reports is not complete yet!');
	};
    
}]);