/* Dashboard controller */
'use strict';
angular.module('jobconfig.ctrl', [])
    
.controller('JobConfigController', ['$scope', '$rootScope','common', '$location', function($scope, $rootScope, common, $location) {

	$scope.cancel = function () {
		$location.path('/define-job-info');
	};

	$scope.goToJobWidget = function () {

		var error = common.getJobAnalysis();
		common.setBackVal('job');
		$location.path('/job-widget');
	};

	$scope.init = function () {
		$scope.jobConfigMethods  = common.getJobConfigMethod();
		//console.log($scope.jobConfigMethods);
	};

	$scope.getJobConfigMethod = function (index, checked) {
		common.setJobConfigMethod(index, checked); 
	};
    
}]);